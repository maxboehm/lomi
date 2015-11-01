package com.maximilian_boehm.lod.instance_matcher.model;

import java.util.Collection;

import org.slf4j.LoggerFactory;


public class ResultStatistics {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ResultStatistics.class);

	private int nCountUselessInstances = 0;

	Result result;

	public ResultStatistics(Result result) {
		this.result = result;
	}

	public int countMatchesBySameTypes() {
		int nCounter = 0;
		for (Match m : getMatches())
			nCounter+= m.getTypes().size();

		return nCounter;
	}

	public int countNumberOfMatches(){
		return getMatches().size();
	}

	public int countNumberOfMatches(Integer nMinLength, MatchType type){
		int nCounter = 0;
		for(Match m:getMatches())
			if(type == null || m.getTyp() == type)
				if(nMinLength==null || m.getDbpName().length() > nMinLength)
					nCounter++;
		return nCounter;
	}
	private Collection<Match> getMatches(){
		return result.getMatches();
	}

	public void incrementUseless(){
		nCountUselessInstances++;
	}

	public void logStatistics(){
		logger.info("Matches: "+countNumberOfMatches());
		logger.info("Matches, Equality		 : "+countNumberOfMatches(null, MatchType.EQUALS));
		logger.info("Matches, Equality, L=6  : "+countNumberOfMatches(6, MatchType.EQUALS));
		logger.info("Matches, Similarity	 : "+countNumberOfMatches(null, MatchType.SIMILARITY));
		logger.info("Matches, Similarity, L=6: "+countNumberOfMatches(6, MatchType.SIMILARITY));
		logger.info("Matches, Same Type		 : "+countMatchesBySameTypes());
		logger.info("Useless Instances       : "+nCountUselessInstances);
	}


}
