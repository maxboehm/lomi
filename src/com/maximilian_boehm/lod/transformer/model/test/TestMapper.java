package com.maximilian_boehm.lod.transformer.model.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.maximilian_boehm.lod.tools.LomiConstants;
import com.maximilian_boehm.lod.transformer.model.Mapper;

public class TestMapper {

	@Test
	public void test() throws Exception{
		Mapper mapper = new Mapper();
		assertEquals(LomiConstants.HTTP_DROP				, mapper.getMapping("AggregateRating"));
		assertEquals("http://dbpedia.org/ontology/Company"	, mapper.getMapping("AnimalShelter"));
		assertEquals(null									, mapper.getMapping("adfasdfasdf"));
		assertEquals("http://dbpedia.org/ontology/Company"	, mapper.getMapping("shoestore"));
		assertEquals("http://dbpedia.org/ontology/Company"	, mapper.getMapping("ShoeStore"));
		// Does not directly match but through levensthein
		assertEquals("http://dbpedia.org/ontology/Company"	, mapper.getMapping("ShoeStora"));

		// Properties
		assertEquals(null									, mapper.getMapping("mainCont234234234234entOfPage2"));
		assertEquals(LomiConstants.HTTP_DROP				, mapper.getMapping("mainContentOfPage2"));
		assertEquals(LomiConstants.HTTP_DROP				, mapper.getMapping("mainContentOfPage"));
		assertEquals("http://dbpedia.org/ontology/almaMater", mapper.getMapping("members"));
		assertEquals("http://dbpedia.org/ontology/name"		, mapper.getMapping("name"));

		// Does not directly match but through levensthein
		assertEquals("http://dbpedia.org/ontology/name"		, mapper.getMapping("namea"));
	}

}
