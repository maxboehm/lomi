package com.maximilian_boehm.lod.tools.test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.maximilian_boehm.lod.tools.KeyValueDatabase;

public class TestDB {

	@Test
	public void test() {
		Map<String, String> kvdb = new KeyValueDatabase<String, String>().getDB("testname", true);
		Map<String, String> mapTest = new HashMap<String, String>();

		for (long i = 0; i < 100000000; i++) {
			String sKey = UUID.randomUUID().toString();
			String sValue = UUID.randomUUID().toString();

			for (int j = 0; j < 10; j++) {
				sValue += sValue;
			}

			kvdb.put(sKey, sValue);
			mapTest.put(sKey, sValue);
		}

		for(String sTestKey:mapTest.keySet()){
			Assert.assertTrue(kvdb.containsKey(sTestKey));
			String sDBValue = kvdb.get(sTestKey);
			Assert.assertTrue(sDBValue.equals(mapTest.get(sTestKey)));
		}
	}

}
