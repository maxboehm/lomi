package com.maximilian_boehm.lod.tools.test;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.maximilian_boehm.lod.transformer.model.SchemaMappingProvider;

public class TestCountMapping {

	@Test
	public void test() throws Exception{
		count(SchemaMappingProvider.getClassMapping());
		count(SchemaMappingProvider.getPropertyMapping());
	}

	public void count(Map<String, String> map){
		int nNull = 0;
		int nGiven = 0;
		int nDrop = 0;

		for(String s:map.keySet()){
			String sVal = map.get(s);

			if(sVal==null || sVal.isEmpty())
				nNull++;
			else if(sVal.equals("http://drop"))
				nDrop++;
			else
				nGiven++;
		}
		Assert.assertTrue(nNull>100);
		Assert.assertTrue(nGiven>200);
		Assert.assertTrue(nDrop>150);
	}

}
