package com.maximilian_boehm.lod.tools;

import java.util.HashMap;
import java.util.Map;

import java.io.File;

import org.mapdb.DB;
import org.mapdb.DB.BTreeMapMaker;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class KeyValueDatabase<K, V>{

	private static Map<String, DB> mapDB = new HashMap<String, DB>();

	public Map<K,V> getDB(String sDBName, boolean bNewDBInstance) {
		return getDB(sDBName, bNewDBInstance, true);
	}
	@SuppressWarnings("unchecked")
	public Map<K,V> getDB(String sDBName, boolean bNewDBInstance, boolean bValueSerializer) {
		File fDir =  Settings.getDatabaseFile(sDBName+".db");
		if(bNewDBInstance){
			fDir.delete();
			fDir.deleteOnExit();
		}

		DB database = mapDB.get(sDBName);

		if(bNewDBInstance && database!=null){
			database.delete(sDBName);
		}

		if(database==null){
			database =  DBMaker
					.fileDB(fDir)
					.transactionDisable()
					.closeOnJvmShutdown()
					.fileMmapEnable()
					.asyncWriteEnable()
					.executorEnable()
					.lockSingleEnable()
					.allocateIncrement(500  * 1024*1024)       // 512MB
					.cacheExecutorEnable()
					.storeExecutorEnable()
					.make()
					;
			mapDB.put(sDBName, database);
		}



		BTreeMapMaker maker = database.treeMapCreate(sDBName).nodeSize(150).keySerializer(Serializer.STRING_ASCII).counterEnable();
		if(bValueSerializer) maker.valueSerializer(Serializer.STRING_ASCII);
		return  (Map<K, V>) maker.makeOrGet();

	}

}
