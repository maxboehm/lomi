package com.maximilian_boehm.lod.deduper.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.LoggerFactory;

import com.google.code.externalsorting.ExternalSort;
import com.maximilian_boehm.lod.tools.KeyValueDatabase;
import com.maximilian_boehm.lod.tools.PerfMeasure;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.NTripleWriter;
import com.maximilian_boehm.lod.tools.URIHelper;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;


/**
 * This class finds duplicated and removes them in the output
 * It also corrects links between duplicate instances
 */
public class Deduper {

	// Members
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(Deduper.class);
	// Factory for creating URIs
	private ValueFactory factory = ValueFactoryImpl.getInstance();
	// This object stores which URI has which hash
	private MapHashToURI uri2hashMapper;
	// This map stores the hashes which are already in the output
	private Map<String, String> kvHashDone;
	// Count the instances which were written to file
	private int nCounter = 0;


	public File dedupeFile(File fInput, boolean bSort, boolean bDeleteInput) throws Exception {
		// Instantiate
		uri2hashMapper = new MapHashToURI(fInput.getName());
		kvHashDone = new KeyValueDatabase<String, String>().getDB("hash2state-"+FilenameUtils.getBaseName(fInput.getName()), true);
		/**
		 * ######################################################################
		 * Step 1: ORDER THE INPUT FILES ALPHABETICALLY
		 * This is necessary for iterating over the instances (and not statements)
		 * The iteration over instances is necessary to be able to calculate the hash of a instance
		 * immediately. Using this approach, data can be read from disk and directly handled.
		 * This avoids loading any bigger chunks into memory (files can be bigger than 100gb)
		 * ######################################################################
		 */

		File fSortedInput = null;

		if(bSort){
			// Create file handle for sorted input
			fSortedInput = Settings.getTmpFile(FilenameUtils.getBaseName(fInput.getName())+"_Sorted.nq");
			// Sort file
			logger.debug("START SORTING");
			sortFile(fInput, fSortedInput, false);
			logger.debug("START READING");

			// Only delete original input file if switch is given
			if(bDeleteInput)
				fInput.delete();
		} else
			fSortedInput = fInput;


		/**
		 * ######################################################################
		 * Step 2: ITERATE OVER INSTANCES AND CALCULATE HASH
		 * In this step, the instance reader will read all instances one and generate a hash out of each
		 * With this approach, equal instances will be found (without comparing against each other, which would be too expensive)
		 * e.g.:
			<_:1002547f9d126ed264eaf068e44e490f> <http://schema.org/Person/email> <mailto:tfrye@bethelks.edu> .
			<_:1002547f9d126ed264eaf068e44e490f> <http://schema.org/Person/jobTitle> "Assistant Professor of Mathematics" .
			<_:1002547f9d126ed264eaf068e44e490f> <http://schema.org/Person/name> "Timothy Frye" .
			AND
			<_:2002547f9d126ed264eaf068e44e49DD> <http://schema.org/Person/email> <mailto:tfrye@bethelks.edu> .
			<_:2002547f9d126ed264eaf068e44e49DD> <http://schema.org/Person/jobTitle> "Assistant Professor of Mathematics" .
			<_:2002547f9d126ed264eaf068e44e49DD> <http://schema.org/Person/name> "Timothy Frye" .

		 * Will result in the same hash (only the Predicates and Objects which are not URIs will be taken into account
		 *
		 * The 'uri2hashMapper' will save each URI assigend to the equivalent hash
		 * ######################################################################
		 */
		long l1 = System.currentTimeMillis();
		InstanceReader instanceReader = new InstanceReader(fSortedInput.getAbsolutePath());
		instanceReader.readInstances(true, new InstanceHandler() {
			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				uri2hashMapper.addInstance(instance);
			}
		});
		/**
		 * ######################################################################
		 * Step 3: ITERATE OVER INSTANCES, REWRITE STATEMENTS AND WRITE TO DISK
		 * By using the previously mapped uri to hashes, it will decided if the hash
		 * has already be handled (and skipped) or if it needs to be handled.
		 * If it needs to be handled, the statements will be adapted and afterwards written to disk.
		 *
		 * Adapting the statements does several things:
		 * a) Replace the subject with the hash of the instance
		 * b) If the object is a referenced instance, rewrite the reference to the hash of the instance
		 *
		 * To clarify: Two iteration are required, because no bigger data should be loaded into memory
		 * But if one would use only one iteration, how to solve the rewriting of the statements?
		 * If, for example, a references b, but b got declared afterwards, this can only be resolved
		 * either by loading all into memory or by iterating two times (The reference a to b has
		 * to be rewritten so that a references the hash of b).
		 * ######################################################################
		 */
		logger.debug("BEGIN WRITING");
		l1 = System.currentTimeMillis();

		// Remember which hashes has be done already
		File fFinalOutput = Settings.getOutputFile(FilenameUtils.getBaseName(fInput.getName())+"Deduped.nq");
		// Open writer
		try (NTripleWriter writer = new NTripleWriter(fFinalOutput);){

			// Read instances
			instanceReader.readInstances(true, new InstanceHandler() {

				@Override
				public void handleInstance(ExtendedInstance instance, long lLineCounter) {
					PerfMeasure.start("INSTANCE");

					// Determine the hashed value of the current URI
					String sHashedValueByURI = instance.getHash();

					// There has to be a hashed value (Because it was
					// computed in the previous step)
					if(sHashedValueByURI==null || sHashedValueByURI.isEmpty())
						new Throwable("Hashed value is missing: "+instance.getURI()).printStackTrace();

					// ALREADY WRITTEN? SKIP
					PerfMeasure.start("DONE.GET");
					String bDone = kvHashDone.get(sHashedValueByURI);
					PerfMeasure.end("DONE.GET");

					// If the hash has been already written to disk
					if(bDone!=null && bDone.equals("1"))
						// Abort here
						return;

					PerfMeasure.start("ADAPT");
					// Rewrite instance (replace subject & rewrite references)
					adaptStatement(instance, sHashedValueByURI);
					PerfMeasure.end("ADAPT");

					PerfMeasure.start("WRITE");
					// -> Write instance
					writeInstance(instance, writer);
					PerfMeasure.end("WRITE");

					// Remember that this instance was already handled
					PerfMeasure.start("DONE.PUT");
					kvHashDone.put(sHashedValueByURI, "1");
					PerfMeasure.end("DONE.PUT");

					PerfMeasure.end("INSTANCE");

					nCounter++;
					if(nCounter % 500000 == 0)
						PerfMeasure.print();
				}
			});

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		logger.debug("WRITE FILE: "+(System.currentTimeMillis()-l1)+" ms");

		/**
		 * ######################################################################
		 * Step 4: SORT THE OUTPUT
		 * This is necessary for better readability and also for later processing
		 * ######################################################################
		 */
		sortFile(fFinalOutput, fFinalOutput, true);

		return fFinalOutput;
	}

	/**
	 * Adapting the statements does several things:
	 * a) Replace the subject with the hash of the instance
	 * b) If the object is a referenced instance, rewrite the reference to the hash of the instance
	 * @param instance
	 * @param sHashedValueByURI
	 */
	private void adaptStatement(ExtendedInstance instance, String sHashedValueByURI){
		// init new set of statements
		Set<Statement> setNewStatements = new HashSet<Statement>();

		// Iterate over all statements
		for(Statement st:instance.getStatements()){
			Statement newSt = null;
			// Set the subject to the hash of the instance
			URI subject = factory.createURI("_:"+sHashedValueByURI);
			// Determine predicate and object
			URI   predicate = st.getPredicate();
			Value object = st.getObject();

			// If it is a BNodeImpl, the object potentially should be rewritten to the hash
			if(object instanceof BNodeImpl){
				PerfMeasure.start("BNODE");
				// Determine the string-value
				String sObj = object.stringValue();

				// Only if it starts with "genid" it has to be rewritten
				if(sObj.startsWith("genid")){
					PerfMeasure.start("GENID");

					// Determine the URI by Object and Context
					String sObjURI = URIHelper.getURI(sObj, st.getContext());

					// Get the hash by the URI
					PerfMeasure.start("U2H.GETOBJ");
					String sHashURI2Ref = uri2hashMapper.getHashByURI(sObjURI);
					PerfMeasure.end("U2H.GETOBJ");
					// Is the referenced data not found?
					if(sHashURI2Ref==null || sHashURI2Ref.isEmpty()){
						// Happens if the referenced instance has no type! (and then it will be ignored)
						logger.info(sObjURI+" Reference was not found");

					} else {
						// Set the object to the hash of the referenced instance
						sObj = "_:"+sHashURI2Ref;
						// Create new statement (Subject and Object have be changed)
						newSt = factory.createStatement(subject, predicate, factory.createURI(sObj));
					}
					PerfMeasure.end("GENID");
				}
				PerfMeasure.end("BNODE");
			} else if(object instanceof LiteralImpl){
				// Create new statement (only subject has be changed)
				newSt = normalizeLiteralObject(subject, predicate, (Literal)object);

			} else if(object instanceof URIImpl) {
				newSt = cleanURIObject(subject, predicate, (URIImpl)object);
			} else {
				new Throwable("Darf nicht passieren: "+object.getClass()).printStackTrace();
			}
			// In the case, the hash is missing, the statement can be null
			if(newSt!=null)
				setNewStatements.add(newSt);
		}
		// Replace the existing statements with the adapted
		instance.replaceStatements(setNewStatements);
	}

	/**
	 * Normalize sometimes very weird input (Spaces, Linesbreaks, etc.)
	 * @param subject
	 * @param predicate
	 * @param lit
	 * @return
	 */
	private Statement normalizeLiteralObject(URI subject, URI predicate, Literal lit){
		String sVal = lit.getLabel();

		// If the object literally equals 'null', drop the content
		if(sVal.toLowerCase().trim().equals("null"))
			return null;

		// replace linebreaks and rewrite too much spacing
		if((sVal.contains("\n") || sVal.contains("  "))){
			sVal = sVal.replaceAll("\\s+", " ").trim();
			if(sVal.endsWith(","))
				sVal = sVal.substring(0, sVal.length()-1);
			lit = factory.createLiteral(sVal);
		}
		// return new result
		return factory.createStatement(subject, predicate, lit);
	}

	/**
	 * @param subject
	 * @param predicate
	 * @param uriObject
	 * @return
	 */
	private Statement cleanURIObject(URI subject, URI predicate,URIImpl uriObject){
		String sObject = uriObject.stringValue();

		// we can not handle embedded images at the moment
		if(sObject.contains("data:image"))
			return null;

		return factory.createStatement(subject, predicate, uriObject);
	}

	/**
	 * Sort a file
	 * @param fInput
	 * @param fOutput
	 * @param bOverwrite
	 * @throws Exception
	 */
	private void sortFile(File fInput, File fOutput, boolean bOverwrite) throws Exception{
		logger.debug("START SORTING");
		long l1 = System.currentTimeMillis();
		if(!fOutput.exists() || bOverwrite)
			ExternalSort.sort(fInput, fOutput);
		logger.debug("END SORTING: "+(System.currentTimeMillis()-l1)+" ms");
	}

	/**
	 * Write an instance to disk
	 * @param instance
	 * @param writer
	 */
	private void writeInstance(ExtendedInstance instance, NTripleWriter writer){
		try {
			// Iterate over all statements of an instance
			for(Statement st:instance.getStatements())
				// And write each statement to a file!
				writer.writeStatement(st);
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
	}
}
