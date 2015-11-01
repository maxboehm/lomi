package com.maximilian_boehm.lod.instance_matcher.model;

import java.util.HashSet;
import java.util.Set;

import java.io.File;

import net.ricecode.similarity.DiceCoefficientStrategy;
import net.ricecode.similarity.JaroStrategy;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.LevenshteinDistanceStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

import org.apache.commons.io.FilenameUtils;
import org.openrdf.model.Statement;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.maximilian_boehm.lod.instance_matcher.model.writer.HTMLWriter;
import com.maximilian_boehm.lod.instance_matcher.model.writer.MappingWriter;
import com.maximilian_boehm.lod.instance_matcher.model.writer.NTWriter;
import com.maximilian_boehm.lod.tools.StringHashWrapper;
import com.maximilian_boehm.lod.tools.PerfMeasure;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.reader.instance.BaseInstance;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.Instance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;
import com.maximilian_boehm.lod.tools.reader.instance.LazyInstance;

public class InstanceMatcher {

	// members
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(InstanceMatcher.class);
	private StringHashWrapper m3 = new StringHashWrapper();
	private ListMultimap<String, Instance> mmInstances;
	private StringSimilarityService serviceDCS = new StringSimilarityServiceImpl(new DiceCoefficientStrategy());
	private StringSimilarityService serviceJS  = new StringSimilarityServiceImpl(new JaroStrategy());
	private StringSimilarityService serviceJWS = new StringSimilarityServiceImpl(new JaroWinklerStrategy());
	private StringSimilarityService serviceLDS = new StringSimilarityServiceImpl(new LevenshteinDistanceStrategy());

	public InstanceMatcher() throws Exception{
		// regular way
		readDBpedia(null);
	}

	public InstanceMatcher(File fDBpediaInstances) throws Exception {
		// way if on a fragment of dbpedia should be read
		readDBpedia(fDBpediaInstances);
	}

	Result result = null;

	/**
	 * @param fTransformedFile
	 * @return
	 * @throws Exception
	 */
	public Result mapInstances(File fTransformedFile) throws Exception {
		result = new Result();
		// Open file
		new InstanceReader(fTransformedFile.getAbsolutePath()).readInstances(true, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance scoInstance, long lLineCounter) {
				// Skip useless instances
				if(isUselessInstance(scoInstance)){
					result.getStatistics().incrementUseless();
					return;
				}

				PerfMeasure.increment("SCO-Instances");
				// Get names of schema.org-instance
				Set<String> setNames = scoInstance.getExpandedNamesByInverting();

				// Some websites create instances in a wrong way and thereby combine
				// a lot of different names within one instance which, in real world,
				// do not belong together
				if(setNames.size()>12) return;

				// Iterate over names
				for(String sSCOName:setNames){

					PerfMeasure.increment("SCO-Names");

					// Encode
					String sMetaphoneSchemaName = m3.encode(sSCOName);

					// Is there at least one hit
					if(mmInstances.containsKey(sMetaphoneSchemaName)){
						// Yes? Iterate over all hits?
						for(Instance dbpInstance:mmInstances.get(sMetaphoneSchemaName)){
							// Work on the comparison
							workInstance(dbpInstance, sSCOName, scoInstance);
						}
					}
				}
			}
		});

		// Log the statistics of this mapping process
		result.getStatistics().logStatistics();
		String sName = FilenameUtils.getBaseName(fTransformedFile.getAbsolutePath());
		// Write the outputs
		new HTMLWriter().write		(Settings.getOutputFile("Mapping_"+sName+".html"), result);
		new NTWriter().write		(Settings.getOutputFile("Mapping_"+sName+".nt"), result);
		new MappingWriter().write	(Settings.getOutputFile("Mapping_"+sName+".txt"), result);

		// Print the performance metrics
		PerfMeasure.print();

		return result;
	}


	/**
	 * Compare two instances
	 * @param dbpInstance
	 * @param sSCOName
	 * @param scoInstance
	 */
	private void workInstance(Instance dbpInstance, String sSCOName, ExtendedInstance scoInstance){
		PerfMeasure.increment("M3-Hits");

		// Iterate over the names of the dbpedia instance
		for(String sDBPName:dbpInstance.getNames()){

			PerfMeasure.increment("ALL ITERATIONS");

			// Drop if length difference is too big
			if(!bAssertLength(sDBPName, sSCOName, 3))
				continue;

			PerfMeasure.increment("AFTER Length-reduction");

			// Trim && LowerCase
			sSCOName = sSCOName.trim().toLowerCase();
			sDBPName = sDBPName.trim().toLowerCase();

			// The first letter does not equal? SKIP
			if(!bAssertFirstLetter(sDBPName, sSCOName))
				continue;

			PerfMeasure.increment("AFTER First-Letter-reduction");

			// Instantiate matchType and Score
			MatchType matchType = null;
			Double dScore = 0D;

			// CHeck for Equality?
			if(sDBPName.equals(sSCOName)){
				dScore = 4* getFactorByLength(sSCOName.length());
				matchType = MatchType.EQUALS;

				// Check for Similarity
			} else {
				PerfMeasure.start("similiarty-algo");
				// Compute similarity by different algorithms
				double score1 = serviceJWS.score(sSCOName, sDBPName); // Score is between 0 and 1
				double score2 = serviceJS.score(sSCOName, sDBPName); // Score is between 0 and 1
				double score3 = serviceDCS.score(sSCOName, sDBPName); // Score is between 0 and 1
				double score4 = serviceLDS.score(sSCOName, sDBPName); // Score is between 0 and 1
				PerfMeasure.end("similiarty-algo");
				// Calculate average
				double dSum = (score1+score2+score3+score4)/4;
				// Compare with threshold
				if(dSum > 0.85){
					matchType = MatchType.SIMILARITY;
					// As longer the string is, the higher gets the score
					dScore = 2* dSum * getFactorByLength(sSCOName.length());
				}
			}

			// Only proceed if it similarity or equality happened
			if(matchType!=null){

				// Persons generally have a lower scoring because only a small percentage
				// is relevant for dbpedia
				if(scoInstance.getTypes().contains("http://dbpedia.org/ontology/Person"))
					dScore = dScore * 0.4D;

				// If the score is lower 4... we don't go into it
				if(dScore<4)
					continue;

				PerfMeasure.increment("AFTER String comparison");

				// Compare types.
				Set<String> scoTypes = scoInstance.getExtendedTypeHierarchy();
				Set<String> dbpTypes = dbpInstance.getExtendedTypeHierarchy();

				if(dbpTypes.isEmpty()){
					PerfMeasure.start("EMPTY_DBPEDIA_TYPES");
					PerfMeasure.end("EMPTY_DBPEDIA_TYPES");
				}

				// If the both instances do not have parent classes
				if(!dbpTypes.isEmpty() && !BaseInstance.hasSameParentClass(scoTypes, dbpTypes))
					continue;

				PerfMeasure.increment("AFTER Type comparison");

				PerfMeasure.start("equalTypes");
				// Determine equal types
				Set<String> setEqualTypes = equalTypes(dbpInstance, scoInstance);
				PerfMeasure.end("equalTypes");

				int nSizeET = setEqualTypes.size();
				if(scoInstance.getTypes().contains("http://dbpedia.org/ontology/Person"))
					dScore = dScore + nSizeET;
				else
					dScore = dScore + 2*nSizeET;

				PerfMeasure.start("equalProperty");
				Set<String> setEqualProperties = equalProperty(dbpInstance, scoInstance);
				PerfMeasure.end("equalProperty");
				int nSizeEP = setEqualProperties.size();
				dScore = dScore + 2*nSizeEP;

				PerfMeasure.start("similarProperty");
				Set<String> setSimilarProperties = similarProperty(dbpInstance, scoInstance);
				PerfMeasure.end("similarProperty");
				int nSizeSP = setSimilarProperties.size();
				dScore = dScore + 2*nSizeSP;

				PerfMeasure.start("notMatchingProperty");
				Set<String> notMatchingProperty = notMatchingProperty(dbpInstance, scoInstance);
				PerfMeasure.end("notMatchingProperty");
				int nSizeNMP = notMatchingProperty.size();
				if(nSizeNMP>0){
					dScore = dScore / (nSizeNMP*1.5);
				}

				result.addMatch(matchType, dScore, sDBPName, dbpInstance, sSCOName, scoInstance, setEqualTypes, setEqualProperties);
			}
		}
	}


	/**
	 * Determine if the length difference does not exceed 'n'
	 * @param s1
	 * @param s2
	 * @param n
	 * @return
	 */
	private boolean bAssertLength(String s1, String s2, int n){
		int nDifference = s1.length() - s2.length();
		if(nDifference < (-1*n) || nDifference > n)
			return false;

		return true;
	}

	/**
	 * Determine if the first letter equals
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean bAssertFirstLetter(String s1, String s2){
		if(s1.length()>1 && s2.length()>1)
			if(!s1.substring(0,1).equals(s2.substring(0,1)))
				return false;


		return true;
	}

	/**
	 * f(x) = (x/10) * 2
	 * 3 -> 0.6		<br>
	 * 4 -> 0.8		<br>
	 * 5 -> 1		<br>
	 * 10 -> 2		<br>
	 * 15 -> 3		<br>
	 * 20 -> 4		<br>
	 * @param nLength
	 * @return
	 */
	private double getFactorByLength(int nLength){
		// 18 -> 1.8
		// 1 -> 0.1
		double dMulti = (nLength/10D)*2;

		return dMulti;

	}

	/**
	 * Determine the types which both instances have
	 * @param dbp
	 * @param sco
	 * @return
	 */
	private Set<String> equalTypes(Instance dbp, Instance sco){
		Set<String> setTypes = new HashSet<String>();
		for(String dbpType:dbp.getTypes())
			for(String scoType:sco.getTypes())
				if(dbpType.equals(scoType))
					setTypes.add(scoType);
		return setTypes;
	}

	/**
	 * Determine properties which are equal in both instances
	 * @param dbp
	 * @param sco
	 * @return
	 */
	private Set<String> equalProperty(Instance dbp, Instance sco){
		Set<String> setProperties = new HashSet<String>();
		if(dbp==null)
			return setProperties;

		if(dbp.getStatements()==null)
			return setProperties;

		for(Statement dbpST:dbp.getStatements()){
			if(!ExtendedInstance.isNameStatement(dbpST) && !ExtendedInstance.isTypeStatement(dbpST)){
				String sDBP = dbpST.getPredicate().stringValue().trim().toLowerCase();
				sDBP += ": "+dbpST.getObject().stringValue().trim().toLowerCase();

				for(Statement scoST:sco.getStatements()){
					if(!ExtendedInstance.isNameStatement(scoST) && !ExtendedInstance.isTypeStatement(scoST)){
						String sSCO = scoST.getPredicate().stringValue().trim().toLowerCase();
						sSCO += ": "+scoST.getObject().stringValue().trim().toLowerCase();

						if(sDBP.equals(sSCO))
							setProperties.add(sSCO);
					}
				}
			}
		}
		return setProperties;
	}
	/**
	 * Determine similar properties
	 * @param dbp
	 * @param sco
	 * @return
	 */
	private Set<String> similarProperty(Instance dbp, Instance sco){
		Set<String> setProperties = new HashSet<String>();
		if(dbp==null)
			return setProperties;

		if(dbp.getStatements()==null)
			return setProperties;

		for(Statement dbpST:dbp.getStatements()){
			if(!ExtendedInstance.isNameStatement(dbpST) && !ExtendedInstance.isTypeStatement(dbpST)){
				String sDBP = dbpST.getObject().stringValue().trim().toLowerCase();

				for(Statement scoST:sco.getStatements()){
					if(!ExtendedInstance.isNameStatement(scoST) && !ExtendedInstance.isTypeStatement(scoST)){
						String sSCO = scoST.getObject().stringValue().trim().toLowerCase();

						double score1 = serviceJWS.score(sSCO, sDBP); // Score is between 0 and 1
						double score2 = serviceJS.score(sSCO, sDBP); // Score is between 0 and 1
						double score3 = serviceDCS.score(sSCO, sDBP); // Score is between 0 and 1
						double score4 = serviceLDS.score(sSCO, sDBP); // Score is between 0 and 1
						double dSum = (score1+score2+score3+score4)/4;

						// Only if score exceeds score
						if(dSum > 0.68)
							setProperties.add(sSCO);
					}
				}
			}
		}
		return setProperties;
	}
	/**
	 * Determine the properties which do not match
	 * @param dbp
	 * @param sco
	 * @return
	 */
	private Set<String> notMatchingProperty(Instance dbp, Instance sco){
		Set<String> setProperties = new HashSet<String>();

		if(dbp==null)
			return setProperties;

		if(dbp.getStatements()==null)
			return setProperties;

		for(Statement dbpST:dbp.getStatements()){
			if(!ExtendedInstance.isNameStatement(dbpST) && !ExtendedInstance.isTypeStatement(dbpST)){
				String sDBPObject = dbpST.getObject().stringValue().trim().toLowerCase();
				String sDBPPredicate = dbpST.getPredicate().stringValue().trim().toLowerCase();

				for(Statement scoST:sco.getStatements()){
					if(!ExtendedInstance.isNameStatement(scoST) && !ExtendedInstance.isTypeStatement(scoST)){
						String sSCOObject = scoST.getObject().stringValue().trim().toLowerCase();
						String sSCOPredicate = scoST.getPredicate().stringValue().trim().toLowerCase();

						if(sSCOPredicate.equals(sDBPPredicate)){
							if(!sSCOObject.equals(sDBPObject))
								setProperties.add(sSCOPredicate+" vs "+sDBPPredicate);
						}

					}
				}
			}
		}
		return setProperties;
	}
	/**
	 * Load the dbpedia instances into memory
	 * @param fDBpediaInstances
	 * @throws Exception
	 */
	private void readDBpedia(File fDBpediaInstances) throws Exception{
		// Create new Multimap
		mmInstances = ArrayListMultimap.create();
		try {
			if(fDBpediaInstances==null){
				fDBpediaInstances = Settings.getMappingFile("sorted_mappingbased-properties_en.nt");
				if(!fDBpediaInstances.exists())
					throw new Exception("File "+fDBpediaInstances.getAbsolutePath()+" does not exist!");
			}

			// Read instances from dbpedia
			new InstanceReader(fDBpediaInstances.getAbsolutePath()).readInstances(false, new InstanceHandler() {

				@Override
				public void handleInstance(ExtendedInstance instance, long lLineCounter) {
					// Get the lazy instance (Only a subset of a whole instance
					LazyInstance lazyInstance = instance.getLazyInstance();

					// Iterate over all names of the instance
					for(String sName:lazyInstance.getNames()){
						// Encode name (For matching relevant!)
						String sEncoded = m3.encode(sName);
						// Save
						mmInstances.put(sEncoded, lazyInstance);
					}
				}

			});
		} catch (Exception e) {
			logger.trace("Error reading DBpedia", e);
		}
	}

	/**
	 * Determine if the instance is useless
	 * @param instance
	 * @return
	 */
	private boolean isUselessInstance(ExtendedInstance instance){
		Set<Statement> setStatements = instance.getStatements();

		// TRUE if size is too small
		if(setStatements.size()<=2)
			return true;

		if(setStatements.size()>2){
			int nCountProperties = 0;
			for(Statement st:setStatements)
				if(!ExtendedInstance.isNameStatement(st) && !ExtendedInstance.isTypeStatement(st))
					if(!st.getPredicate().toString().contains("http://dbpedia.org/property/url"))
						nCountProperties++;


			if(nCountProperties==0)
				return true;
		}


		// FALSE if one of the statements is NOT a statement describing the type or name
		for(Statement st:setStatements)
			if(!ExtendedInstance.isNameStatement(st) && !ExtendedInstance.isTypeStatement(st))
				return false;

		return true;
	}

}
