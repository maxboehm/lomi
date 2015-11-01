package com.maximilian_boehm.lod.transformer.model;

import java.util.HashMap;
import java.util.Map;

import net.ricecode.similarity.LevenshteinDistanceStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

public class Mapper {

	private StringSimilarityService simService 			= new StringSimilarityServiceImpl(new LevenshteinDistanceStrategy());
	private Map<String, String> 	mapCache			= new HashMap<String, String>();
	private Map<String, String>		mapClassMappings 	= null;
	private Map<String, String> 	mapPropertyMappings = null;

	public Mapper() throws Exception{
		mapClassMappings 	= SchemaMappingProvider.getClassMapping();
		mapPropertyMappings = SchemaMappingProvider.getPropertyMapping();
	}

	/**
	 * sURI can be in the following form:
	 * a) Class/Property
	 * b) Class
	 * c) Property
	 * At first, it will be looked for a matching property / class
	 * If there is no hit, Levensthein Distance will be applied
	 *
	 * It doesn't matter whether the URI will be in upper or in lower case
	 * because it will be internally transformed to lower case
	 * @param sURI
	 * @return
	 */
	public String getMapping(String sURI){
		sURI = sURI.toLowerCase();

		// Transform 'InStock"@en' to 'InStock'
		if(sURI.contains("\"@"))
			sURI = sURI.substring(0, sURI.indexOf("\"@"));

		sURI = sURI.replaceAll("\"", "");
		sURI = sURI.replaceAll("'", "");

		if(mapCache.containsKey(sURI))
			return mapCache.get(sURI);

		// ########################################
		// Step 1: Look for equal Strings
		// ########################################

		// If it is a class, there won't be a slash inside the string
		if(mapClassMappings.containsKey(sURI))
			return cache(sURI, mapClassMappings.get(sURI));

		// Take special treatment into account
		// e.g. Map a property of a special class to another property
		// sportsteam/employee -> http://dbpedia.org/ontology/team
		// but map 'regular' employee -> http://dbpedia.org/ontology/xxxx
		if(mapPropertyMappings.containsKey(sURI))
			return cache(sURI, mapPropertyMappings.get(sURI));

		// No class? Then get the name of the Property
		String sProperty = sURI.contains("/") ? sURI.substring(sURI.lastIndexOf("/")+1) : sURI;

		if(mapPropertyMappings.containsKey(sProperty))
			return cache(sURI, mapPropertyMappings.get(sProperty));

		// ########################################
		// Step 2: Look for similar Strings
		// ########################################

		// If it is a class
		// e.g. "AggregateRating"
		if(!sURI.contains("/"))
			for(String sKey:mapClassMappings.keySet())
				if(simService.score(sKey, sURI)>0.8D)
					//logger.debug(sKey+" "+sURI);
					return cache(sURI, mapClassMappings.get(sKey));


		// Maybe, there is just a typo?
		for(String sKey:mapPropertyMappings.keySet())
			if(simService.score(sKey, sURI)>=0.8D || simService.score(sKey, sProperty)>=0.8D)
				//logger.debug(sKey+" "+sURI);
				return cache(sURI, mapPropertyMappings.get(sKey));


		// ########################################
		// Step 3: No Mapping found? -> Return null
		// ########################################
		return cache(sURI, null);
	}

	private String cache(String key, String value){
		mapCache.put(key, value);

		return value;

	}


}
