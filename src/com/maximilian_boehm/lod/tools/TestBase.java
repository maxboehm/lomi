package com.maximilian_boehm.lod.tools;

import java.util.ArrayList;
import java.util.List;

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

public class TestBase extends TestCase{
	private List<File> list2Delete = new ArrayList<File>();
	public static boolean bDeleteFiles = false;

	@Override
	@Before
	public void setUp() {
		try {
			Settings.initSettings();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	@After
	public void tearDown() {
		for(File f:list2Delete) f.delete();
	}

	protected void addFile2Delete(File f) {
		if(bDeleteFiles)
			list2Delete.add(f);
	}

	protected File getFile(String sFileName, Class<?> clz){
		String sFile = clz.getResource(sFileName).getFile();
		sFile = sFile.replace("lod_microdata/bin/com/maximilian_boehm", "lod_microdata/src/com/maximilian_boehm");
		return new File(sFile);
	}

}
