package com.maximilian_boehm.lod.tools.reader.lazy;

import java.util.Map;

import java.io.File;

import com.maximilian_boehm.lod.tools.KeyValueDatabase;
import com.maximilian_boehm.lod.tools.StringHashWrapper;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;
import com.maximilian_boehm.lod.tools.reader.instance.LazyInstance;

public class DBpediaLazyInstances {

	private static DBpediaLazyInstances dbpediaLazyInstances = null;
	Map<String,LazyInstance> kvInstances =  new KeyValueDatabase<String, LazyInstance>().getDB("dbp-lazyinstances", false, false);
	StringHashWrapper m3 = new StringHashWrapper();


	public static DBpediaLazyInstances getSingleton() throws Exception{
		if(dbpediaLazyInstances==null) dbpediaLazyInstances = new DBpediaLazyInstances();
		return dbpediaLazyInstances;
	}
	protected DBpediaLazyInstances() {}

	public LazyInstance get(String sM3){
		return kvInstances.get(sM3);
	}

	public boolean isEmpty(){
		return kvInstances.isEmpty();
	}

	/**
	 * Expects a file containing properties
	 * @param f
	 * @throws Exception
	 */
	public void createDatabase(File f) throws Exception {
		new InstanceReader(f.getAbsolutePath()).readInstances(false, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				LazyInstance lazyInstance = instance.getLazyInstance();
				for(String sName:lazyInstance.getNames()){
					String sM3 = m3.encode(sName);
					kvInstances.put(sM3, lazyInstance);
				}
			}

		});
	}
}
