package com.maximilian_boehm.lod.transformer.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.google.code.externalsorting.ExternalSort;
import com.maximilian_boehm.lod.tools.KeyValueDatabase;
import com.maximilian_boehm.lod.tools.LomiConstants;
import com.maximilian_boehm.lod.tools.PerfMeasure;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.NTripleWriter;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;

public class Transformer {

	// members
	private ValueFactory factory = ValueFactoryImpl.getInstance();
	private Map<String, List<String>> refStore = null;
	private Mapper mapping = null;

	public File transformFile(File fInput) throws Exception{
		mapping = new Mapper();

		refStore = new KeyValueDatabase<String, List<String>>().getDB(fInput.getName()+"_uri2ref", true, false);

		// open the reader
		InstanceReader instanceReader = new InstanceReader(fInput.getAbsolutePath());

		// open output file
		File fOutputFile = Settings.getOutputFile(FilenameUtils.getBaseName(fInput.getName())+"_Transformed.nq");

		// open the writer
		try (NTripleWriter writer = new NTripleWriter(fOutputFile);){

			// read file for the first time
			// transform statements according to the mapping file
			// -> schema.org -> dbpedia.org
			// also determine the referenced resources
			instanceReader.readInstances(true, new InstanceHandler() {

				@Override
				public void handleInstance(ExtendedInstance instance, long lLineCounter) {
					try {
						PerfMeasure.start("TRANSFORM");
						// Transform from schema.org to dbpedia.org
						transformStatement(instance, null);
						PerfMeasure.end("TRANSFORM");

						PerfMeasure.start("RESOLVE");
						// determine which resources are referenced and store them
						resolveStatement(instance);
						PerfMeasure.end("RESOLVE");

						PerfMeasure.start("WRITE");
						writeInstance(instance, writer, new InstanceValidator() {

							@Override
							public boolean isValidInstance(ExtendedInstance instance) {
								for(String sType:instance.getTypes()){

									if(sType.contains("schema.org")){
										MappingCounter.increment_NotMapped_Class();
										return false;

									} else if(sType.equals(LomiConstants.HTTP_DROP)){
										MappingCounter.increment_Dropped_Class();
										return false;

									} else if(sType.equals(LomiConstants.HTTP_RESOLVE)){
										MappingCounter.increment_Resolved_Class();
										return false;

									} else {
										MappingCounter.increment_Mapped_Class();
										return true;
									}
								}
								return false;
							}
						});
						PerfMeasure.end("WRITE");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});


			// At this point in time, we only know which URIs want to embed some other URIs
			instanceReader.readInstances(true, new InstanceHandler() {

				@Override
				public void handleInstance(ExtendedInstance instance, long lLineCounter) {
					try {
						// Is this instance a instance we want to embed in another one?
						if(refStore.containsKey(instance.getURI())){
							// Create new holder
							Set<Statement> setStatements = new HashSet<Statement>();

							// Create the embeddings for EACH URI
							for(String sURI:refStore.get(instance.getURI())){
								// run over each statement
								for(Statement resolvedST:instance.getStatements()){
									if(	!resolvedST.getPredicate().stringValue().equals(LomiConstants.RDF_TYPE) &&
											!resolvedST.getPredicate().toString().endsWith("/name")){
										// and set a new Subject!
										Statement newSt = transformStatement(resolvedST, factory.createURI(sURI));
										setStatements.add(newSt);
									}
								}
							}
							// Job is done? Remove it...
							refStore.remove(instance.getURI());
							instance.replaceStatements(setStatements);

							transformStatement(instance, null);

							// determine which resources are referenced and store them into a hashmap
							resolveStatement(instance);

							// write the manipulated instance
							writeInstance(instance, writer, new InstanceValidator() {

								@Override
								public boolean isValidInstance(ExtendedInstance instance) {
									return true;
								}
							});
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		//MappingCounter.outputMappingStatistics();
		ExternalSort.sort(fOutputFile, fOutputFile);

		return fOutputFile;
	}

	private void transformStatement(ExtendedInstance instance, Resource subject) throws Exception{
		// New set of Statements
		Set<Statement> newSetSt = new HashSet<Statement>();

		// Iterate over current set of statements
		for(Statement st:instance.getStatements())
			newSetSt.add(transformStatement(st, subject));

		instance.replaceStatements(newSetSt);
	}

	private Statement transformStatement(Statement st, Resource subject) throws Exception{
		return factory.createStatement(
				// SUBJECT
				subject==null ? st.getSubject() : subject,
						// PREDICATE
						(URI)  replaceURI(st.getPredicate()),
						// OBJECT
						(Value)replaceURI(st.getObject()));
	}

	private void writeInstance(ExtendedInstance instance, NTripleWriter writer, InstanceValidator validator) throws Exception{
		if(validator.isValidInstance(instance))
			for(Statement st:instance.getStatements())
				if(isValidStatement(st))
					writer.writeStatement(st);
	}

	interface InstanceValidator{
		public boolean isValidInstance(ExtendedInstance instance);
	}

	/**
	 * http://schema.org/Place -> Place
	 * https://schema.org/Place -> Place
	 * https://schema.org/abc/Place -> abc/Place
	 * ....
	 * @param sURL
	 * @return
	 */
	public static String getPathByURL(String sURL){
		String sPath = "";
		String sDomain = ".org";
		// Get the first occurence of .org
		int nFirstOrgDomain = sURL.toLowerCase().indexOf(sDomain)+sDomain.length();
		// Delete all before the .org
		sPath = sURL.substring(nFirstOrgDomain);

		if(sPath.startsWith("/"))
			sPath = sPath.substring(1);

		return sPath;
	}

	private List<Statement> getStatement2Resolve(Set<Statement> setStatement){
		List<Statement> listResolveStatements = new ArrayList<Statement>();
		for(Statement st:setStatement)
			if(st.getPredicate().toString().equals("http://resolve"))
				listResolveStatements.add(st);

		return listResolveStatements;
	}

	private boolean isValidStatement(Statement st){
		if(st.getPredicate().toString().equals(LomiConstants.HTTP_DROP)){
			MappingCounter.increment_Dropped_Property();
			return false;

		} else if(st.getPredicate().toString().contains("schema.org")){
			MappingCounter.increment_NotMapped_Property();
			return false;

		} else {
			MappingCounter.increment_Mapped_Property();
			return true;
		}
	}

	private Object replaceURI(Object obj){
		String sVal = obj.toString();


		if(sVal.contains("schema.org")){
			String sPath = getPathByURL(sVal);
			sVal = "http://schema.org/"+sPath;

			// Get the mapping by the path (e.g. 'Person' -> 'http://dbpedia.org/ontology/Person')
			String sNewMapping = mapping.getMapping(sPath);

			// Is there a mapping?
			if(sNewMapping!=null){
				return factory.createURI(sNewMapping);
			} else {
				String sObj = obj.toString();
				sObj = sObj.substring(sObj.toLowerCase().indexOf(".org/")+5);

				if(sObj.contains("/")){
					String sClzz = sObj.substring(0, sObj.indexOf("/"));
					String sMapping = mapping.getMapping(sClzz);

					// only ignore it, if the class should be not dropped
					if(sMapping != null && !sMapping.equals(LomiConstants.HTTP_DROP))
						// Does the mapping miss? Count it
						MappingCounter.registerMissingMapping(sVal);


				} else {
					// Does the mapping miss? Count it
					MappingCounter.registerMissingMapping(sVal);
				}
			}
		}

		return obj;
	}

	private void resolveStatement(ExtendedInstance instance) throws Exception{
		Set<Statement> setStatement = instance.getStatements();

		// iterate over all statements containing "resolve"
		for(Statement st2Resolve:getStatement2Resolve(setStatement)){
			// remove the statement (We don't want to see it later anymore..)
			setStatement.remove(st2Resolve);
			// Determine the instance which we refer to
			Value value2Resolve = st2Resolve.getObject();

			if(value2Resolve instanceof URIImpl){
				String sURI2Resolve = value2Resolve.stringValue();

				// Is there already a reference to this instance from another instance?
				List<String> listURI = refStore.get(sURI2Resolve);
				// No? Create an empty list
				if(listURI==null) listURI = new ArrayList<String>();
				listURI.add(instance.getURI());
				// Remember: listURI contains the URIs which want to embed sURI2Resolve
				refStore.put(sURI2Resolve, listURI);
			}
		}

		// Returns the statement which was cleaned from all 'resolve' instructions
		instance.replaceStatements(setStatement);
	}
}
