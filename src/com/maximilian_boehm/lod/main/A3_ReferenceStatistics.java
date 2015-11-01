package com.maximilian_boehm.lod.main;

import java.io.File;
import java.io.FilenameFilter;

import org.openrdf.model.Statement;
import org.slf4j.LoggerFactory;

import com.maximilian_boehm.lod.tools.FileWorker;
import com.maximilian_boehm.lod.tools.FileWorker.Worker;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;

public class A3_ReferenceStatistics {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(A3_ReferenceStatistics.class);


	/**
	 * ReferenceStatistics computes how much of the matched instances have references to dbpedia instances
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Settings.initSettings();
		new A3_ReferenceStatistics().countDir("output-matching");
		System.exit(0);
	}

	long lReferences = 0;
	public void countDir(String sDir) throws Exception{

		new FileWorker().work(Settings.getDir(sDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".nt");
			}
		}, new Worker() {

			@Override
			public void work(File f) throws Exception {
				oneFile(f);
			}
		});

		logger.info(sDir);
		logger.info("Ref2DBP: "+lReferences);
		logger.info("--------");
	}
	private void oneFile(File f) throws Exception{
		new InstanceReader(f.getAbsolutePath()).readInstances(false, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				for(Statement st:instance.getStatements())
					if(st.getObject().stringValue().contains("dbpedia.org")){
						if(!st.getPredicate().stringValue().contains("/team"))
							logger.info(st.toString());
						lReferences++;
					}
			}
		});
	}
}
