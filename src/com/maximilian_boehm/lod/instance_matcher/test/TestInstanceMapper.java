package com.maximilian_boehm.lod.instance_matcher.test;

import org.junit.Test;

import com.maximilian_boehm.lod.instance_matcher.model.InstanceMatcher;
import com.maximilian_boehm.lod.instance_matcher.model.Result;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.TestBase;

public class TestInstanceMapper extends TestBase {

	@Test
	public void test() throws Exception {
		Settings.initSettings();
		// Use only a subset of dbpedia to speedup
		InstanceMatcher mapper = new InstanceMatcher(getFile("test_dbpedia_org.nq", this.getClass()));
		// Do the mapping
		Result result = mapper.mapInstances(getFile("test_schema_org.nq", this.getClass()));
		System.out.println(result);
	}

}
