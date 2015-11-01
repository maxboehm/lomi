package com.maximilian_boehm.lod.instance_matcher.model.measure;

import java.util.Map;

import java.io.File;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;
import com.maximilian_boehm.lod.tools.PersistentMap;

public class MatchMeasure {

	/**
	 * Get metrics by edited file and gold standard
	 * @param fInput
	 * @param fGold
	 * @return
	 */
	public Pair<Double, Double> getMeasure(File fInput, File fGold){
		// Read the mapping between schema.org instances and dbpedia instances
		Multimap<String, String> mapInput = PersistentMap.readMultiMap(fInput, false);
		// Read the manually created mapping
		Map<String, String> mapGold = PersistentMap.read(fGold);

		double dTruePositive = 0;
		double dFalsePositive = 0;

		double dFalseNegatives = mapGold.size();

		// Iterate over Mapping
		for(String sDBPURI:mapGold.keySet()){
			String sSCOURI = mapGold.get(sDBPURI);

			boolean bHit = false;;

			// Look if this mapping has been marked as GOLD Standard
			for(String sResultSCOURI : mapInput.get(sDBPURI)){
				if(sSCOURI.equals(sResultSCOURI))
					bHit = true;

			}
			// Has this mapping been marked as GOLD?
			if(bHit)
				dTruePositive++;
			else
				dFalsePositive++;

		}
		// Calculate metrics
		double dPrecision = dFalseNegatives / (dTruePositive+dFalsePositive);
		double dRecall = dTruePositive / dFalseNegatives;
		// return result
		return Pair.of(dPrecision, dRecall);

	}

}
