package com.maximilian_boehm.lod.main;

import java.io.File;

import org.slf4j.LoggerFactory;

import com.google.code.externalsorting.ExternalSort;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.reader.lazy.DBpediaInstanceType;
import com.maximilian_boehm.lod.tools.reader.lazy.DBpediaLazyInstances;
import com.maximilian_boehm.lod.tools.reader.lazy.DBpediaProperties;

public class DatabaseManager {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

	/**
	 * Fills the databases
	 * (Necessary for most of the steps we do here)
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Settings.initSettings();

		logger.info("1/4: Sort Files");
		File fTypes      = getSortedFile( "instance-types_en.nt");
		File fProperties = getSortedFile( "mappingbased-properties_en.nt");

		logger.info("2/4: Fill InstanceType-DB");
		DBpediaInstanceType.getSingleton().createDatabase(fTypes);

		logger.info("3/4: Fill Properties-DB");
		DBpediaProperties.getSingleton().createDatabase(fProperties);

		logger.info("4/4: Fill LazyInstances-DB");
		DBpediaLazyInstances.getSingleton().createDatabase(fProperties);
	}

	private static File getSortedFile(String sFile) throws Exception{
		File fInput = Settings.getMappingFile(sFile);

		if(!fInput.exists())
			throw new Exception("File "+sFile+" does not exist but is mandatory -> Download from dbpedia!");

		File fOutput = Settings.getMappingFile("sorted_"+sFile);
		if(!fOutput.exists())
			ExternalSort.sort(fInput, fOutput);

		return fOutput;
	}

	public static void checkPreCondition() throws Exception{
		if(DBpediaInstanceType.getSingleton().isEmpty())
			throw new Exception("Database DBpediaInstanceType is not filled! Use "+DatabaseManager.class.getPackage()+":"+DatabaseManager.class.getName());
		if(DBpediaProperties.getSingleton().isEmpty())
			throw new Exception("Database DBpediaProperties is not filled! Use "+DatabaseManager.class.getPackage()+":"+DatabaseManager.class.getName());
		if(DBpediaLazyInstances.getSingleton().isEmpty())
			throw new Exception("Database DBpediaLazyInstances is not filled! Use "+DatabaseManager.class.getPackage()+":"+DatabaseManager.class.getName());
	}

}
