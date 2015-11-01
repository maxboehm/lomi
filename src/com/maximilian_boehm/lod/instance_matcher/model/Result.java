package com.maximilian_boehm.lod.instance_matcher.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.reader.instance.Instance;


public class Result {

	// SCO 2 DBP
	// DBP URI TO MATCH
	Map<String, Match> mapMatches = new HashMap<>();
	ResultStatistics rs = null;

	public void addMatch(MatchType typ,double dScore, String dbpName, Instance dbpInstance, String scoName, Instance scoInstance, Set<String> setTypes, Set<String> setProperties) {
		Match m = new Match(typ, dScore, dbpName, dbpInstance, scoName, scoInstance, setTypes, setProperties);

		// If the matching score is below the threshold
		// SKIP THE MATCH
		if(m.calculateScore()<Settings.SCORE_THRESHOLD)
			return;

		String sKey = scoInstance.getURI()+dbpInstance.getURI();
		mapMatches.put(sKey, m);

	}

	public Collection<Match> getMatches(){
		return mapMatches.values();
	}

	public ResultStatistics getStatistics(){
		if(rs==null) rs = new ResultStatistics(this);
		return rs;
	}

	@Override
	public String toString() {
		String s = "";
		for(Match m:getMatches())
			s+= m.toString()+"\n";

		return s;
	}

}
