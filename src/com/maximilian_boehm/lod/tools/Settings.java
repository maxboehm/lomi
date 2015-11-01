package com.maximilian_boehm.lod.tools;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.maximilian_boehm.lod.main.DatabaseManager;

public class Settings {

	public static final Double SCORE_THRESHOLD = 7.0;
	private static String sBaseDir     = "/Users/Max/Documents/Programmierung/dbpedia/lomi/";
	private static String sInputDir    = sBaseDir+"input/";
	private static String sMappingDir  = sBaseDir+"mapping/MappingSchemaDotOrg2DBpedia/";
	private static String sOutputDir   = sBaseDir+"output/";
	private static String sTmpDir	   = sBaseDir+"tmp/";
	private static String sDatabaseDir = sBaseDir+"db/";

	public static File getInputFile	 (String sFileName){return new File(sInputDir	+sFileName);}
	private static Logger getLogger(String s){
		return (Logger)LoggerFactory.getLogger(s);
	}
	public static File getMappingFile (String sFileName){return new File(sMappingDir +sFileName);}
	public static File getOutputFile  (String sFileName){return new File(sOutputDir	 +sFileName);}
	public static File getDatabaseFile(String sFileName){return new File(sDatabaseDir+sFileName);}
	public static File getTmpFile	  (String sFileName){return new File(sTmpDir	 +sFileName);}
	public static File getDir    (String s){return new File(sBaseDir+s);}
	public static File getBaseDir    	(){return new File(sBaseDir);}
	public static File getInputDir    	(){return new File(sInputDir);}
	public static File getOutputDir   	(){return new File(sOutputDir);}
	public static File getMappingDir   	(){return new File(sMappingDir);}
	public static File getTMPDir		(){return new File(sTmpDir);}
	public static File getDatabaseDir   (){return new File(sDatabaseDir);}

	/** If file is really big, this might be necessary (Depends on available space on hard drive) **/
	public static void setJavaTmpDir(String sDir){
		System.setProperty("java.io.tmpdir", sDir);
	}
	public static void initSettings() throws Exception{
		checkDir(getBaseDir());
		checkDir(getInputDir());
		checkDir(getOutputDir());
		checkDir(getMappingDir());
		checkDir(getTMPDir());
		checkDir(getDatabaseDir());

		DatabaseManager.checkPreCondition();

		setJavaTmpDir(getTMPDir().getAbsolutePath());
		getLogger("com.maximilian_boehm.lod").setLevel(Level.DEBUG);
		getLogger("org.openrdf.rio"			).setLevel(Level.INFO);

	}
	public static void setMappingDir (String sDir){sMappingDir	= sDir;}
	public static void setInputDir   (String sDir){sInputDir  	= sDir;}
	public static void setDatabaseDir(String sDir){sDatabaseDir = sDir;}
	public static void setOutputDir	 (String sDir){sOutputDir 	= sDir;}
	public static void setTmpDir	 (String sDir){sTmpDir 	 	= sDir;}

	private static void checkDir(File f) throws Exception{
		if(!f.exists()) throw new Exception("Directory "+f.getAbsolutePath()+" does not exist");

	}
}
