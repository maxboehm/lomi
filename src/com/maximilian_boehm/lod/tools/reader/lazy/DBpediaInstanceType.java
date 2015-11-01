package com.maximilian_boehm.lod.tools.reader.lazy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.io.File;

import com.maximilian_boehm.lod.tools.KeyValueDatabase;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;

public class DBpediaInstanceType {

	private static DBpediaInstanceType dbpiType = null;
	private Map<String, Set<String>> kvInstances = new KeyValueDatabase<String, Set<String>>().getDB("dbp-instances", false, false);;


	public static DBpediaInstanceType getSingleton() throws Exception{
		if(dbpiType==null) dbpiType = new DBpediaInstanceType();
		return dbpiType;
	}

	public boolean isEmpty(){
		return kvInstances.isEmpty();
	}

	protected DBpediaInstanceType() {}

	public Set<String> get(String sURI){
		Set<String> set = kvInstances.get(sURI);
		if(set!=null)
			return set;
		else
			return new HashSet<>();
	}

	/**
	 * Expects a file containing types!
	 * @param f
	 * @throws Exception
	 */
	public void createDatabase(File f) throws Exception {

		new InstanceReader(f.getAbsolutePath()).readInstances(false, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				Set<String> setTypes = get(instance.getURI());
				setTypes.addAll(instance.getTypes());
				kvInstances.put(instance.getURI(), setTypes);
			}
		});
	}

}
