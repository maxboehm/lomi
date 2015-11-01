package com.maximilian_boehm.lod.tools;


public class StringHashWrapper {

	private NameMatcher matcher = null;

	public StringHashWrapper(){
		matcher = new NameMatcher();
	}

	public String encode(String s){
		return matcher.encode(s);
	}

}
