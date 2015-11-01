package com.maximilian_boehm.lod.tools.reader.counter;

public abstract class CounterBase {

	private String sFile = "";

	public CounterBase(String sFile){
		setFile(sFile);
	}

	public String getFile() {
		return sFile;
	}

	public void setFile(String file) {
		sFile = file;
	}
}
