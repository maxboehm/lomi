package com.maximilian_boehm.lod.instance_matcher.model;

import java.util.Set;

import com.maximilian_boehm.lod.tools.reader.instance.Instance;

public class Match {

	private MatchType 	typ;
	private double 		dNameScore;
	private Instance	DBPInstance = null;
	private String 		dbpName 	= null;
	private Instance 	SCOInstance = null;
	private String 		scoName 	= null;
	private Set<String> setTypes 	= null;
	private Set<String> setProperties = null;

	public Match(MatchType typ, double s, String dbpName, Instance dbp, String scoName, Instance sco, Set<String> setTypes, Set<String> setProperties) {
		setTyp(typ);
		setNameScore(s);
		setDBPInstance(dbp);
		setDbpName(dbpName);
		setSCOInstance(sco);
		setScoName(scoName);
		setTypes(setTypes);
		setProperties(setProperties);
	}

	public double calculateScore(){
		return getNameScore()+getTypes().size()+getProperties().size();
	}


	public Set<String> getProperties() {
		return setProperties;
	}


	public void setProperties(Set<String> setProperties) {
		this.setProperties = setProperties;
	}


	public Set<String> getTypes() {
		return setTypes;
	}


	public void setTypes(Set<String> setTypes) {
		this.setTypes = setTypes;
	}


	public String getDbpName() {
		return dbpName;
	}


	public void setDbpName(String dbpName) {
		this.dbpName = dbpName;
	}


	public String getScoName() {
		return scoName;
	}


	public void setScoName(String scoName) {
		this.scoName = scoName;
	}


	public MatchType getTyp() {
		return typ;
	}

	public void setTyp(MatchType typ) {
		this.typ = typ;
	}

	public double getNameScore() {
		return dNameScore;
	}
	public void setNameScore(double score) {
		dNameScore = score;
	}
	public Instance getDBPInstance() {
		return DBPInstance;
	}
	public void setDBPInstance(Instance dBPInstance) {
		DBPInstance = dBPInstance;
	}
	public Instance getSCOInstance() {
		return SCOInstance;
	}
	public void setSCOInstance(Instance sCOInstance) {
		SCOInstance = sCOInstance;
	}

	@Override
	public String toString() {
		String s = getNameScore()+" ["+getScoName()+"] ["+getDbpName()+"]";
		return s;
	}

}
