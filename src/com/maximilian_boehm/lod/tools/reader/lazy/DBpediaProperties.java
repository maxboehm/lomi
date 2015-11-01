package com.maximilian_boehm.lod.tools.reader.lazy;

import java.util.Map;
import java.util.Set;

import java.io.File;

import org.openrdf.model.Statement;

import com.maximilian_boehm.lod.tools.KeyValueDatabase;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;

public class DBpediaProperties {

	private static DBpediaProperties dbpProperties = null;
	Map<String, Set<Statement>> kvProperties =  new KeyValueDatabase<String, Set<Statement>>().getDB("dbp-properties", false, false);


	public static DBpediaProperties getSingleton() throws Exception{
		if(dbpProperties==null) dbpProperties = new DBpediaProperties();
		return dbpProperties;
	}
	protected DBpediaProperties() {}

	public Set<Statement> get(String sURI){
		return kvProperties.get(sURI);
	}

	public boolean isEmpty(){
		return kvProperties.isEmpty();
	}

	/**
	 * Expects properties
	 * @param f
	 * @throws Exception
	 */
	public void createDatabase(File f) throws Exception {



		new InstanceReader(f.getAbsolutePath()).readInstances(false, new ExtendedInstanceHandler() {

			@Override
			public boolean skipStatement(Statement st) {
				if(ExtendedInstance.isNameStatement(st) || ExtendedInstance.isTypeStatement(st))
					return true;
				return false;
			}

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				kvProperties.put(instance.getURI(), instance.getStatements());
			}
		});
	}

}
