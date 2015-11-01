package com.maximilian_boehm.lod.instance_matcher.test.gold_standard;

import java.util.Map;

import java.io.File;

import org.junit.Test;

import com.maximilian_boehm.lod.instance_matcher.model.InstanceMatcher;
import com.maximilian_boehm.lod.instance_matcher.model.Match;
import com.maximilian_boehm.lod.instance_matcher.model.Result;
import com.maximilian_boehm.lod.tools.GZipFile;
import com.maximilian_boehm.lod.tools.PersistentMap;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.TestBase;

public class TestGoldStandard extends TestBase {

	@Test
	public void test() throws Exception {
		Settings.initSettings();

		File fZippedDbpedia = getFile("dbpedia.nq.gz", this.getClass());
		File fDBP = new GZipFile().gunzipIt(fZippedDbpedia);

		File fZippedInput = getFile("input.nq.gz", this.getClass());
		File fInput = new GZipFile().gunzipIt(fZippedInput);

		File fGoldStandard = getFile("gold_standard.txt", this.getClass());

		Map<String, String> mapGold = PersistentMap.read(fGoldStandard);


		InstanceMatcher mapper = new InstanceMatcher(fDBP);

		Result result = mapper.mapInstances(fInput);

		int nPositive = 0;
		int nNegative = 0;
		for(Match m:result.getMatches()){

			if(m.getNameScore()>=7)
				if(mapGold.containsKey(m.getSCOInstance().getURI()))
					nPositive++;
				else {
					nNegative++;
					System.err.println(m.getDBPInstance().getURI());
				}
		}

		System.out.println("Positive: "+nPositive);
		System.out.println("Negative: "+nNegative);

		fInput.delete();
		fDBP.delete();

	}

}
