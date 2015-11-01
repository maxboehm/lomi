package com.maximilian_boehm.lod.tools.reader.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.maximilian_boehm.lod.tools.reader.lazy.DBpediaInstanceType;
public class TestInstanceType {

	Set<String> expected = new HashSet<String>(Arrays.asList(
			"http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#InformationEntity",
			"http://schema.org/Movie",
			"http://dbpedia.org/ontology/Work",
			"http://schema.org/CreativeWork",
			"http://dbpedia.org/ontology/Wikidata:Q11424",
			"http://dbpedia.org/ontology/Film"
			));

	@Test
	public void test() throws Exception{
		DBpediaInstanceType type = DBpediaInstanceType.getSingleton();
		assertEquals(5, type.get("http://dbpedia.org/resource/The_Lord_of_the_Rings:_War_of_the_Ring").size());
		assertThat(type.get("http://dbpedia.org/resource/The_Lord_of_the_Rings:_The_Fellowship_of_the_Ring"), is(expected));
	}

}