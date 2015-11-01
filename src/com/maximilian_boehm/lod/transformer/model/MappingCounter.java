package com.maximilian_boehm.lod.transformer.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.maximilian_boehm.lod.tools.PersistentMap;

public class MappingCounter {


	private static int nNullValue = 0;

	private static int nDroppedProperty = 0;
	private static int nDroppedClass = 0;
	private static int nMappedProperty = 0;
	private static int nMappedClass = 0;
	private static int nNotMappedProperty = 0;
	private static int nNotMappedClass = 0;
	private static int nResolved = 0;
	private static Map<String, Integer> mapCount = new HashMap<String, Integer>();
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(MappingCounter.class);

	public static void nullValue(){
		nNullValue++;
	}
	public static void registerMissingMapping(String sVal){
		Integer nCount = mapCount.get(sVal)==null ? 0 : mapCount.get(sVal);
		nCount = nCount + 1;
		mapCount.put(sVal, nCount);
	}
	public static void increment_Dropped_Property()	 {nDroppedProperty++;}
	public static void increment_Mapped_Property()	 {nMappedProperty++;}
	public static void increment_NotMapped_Property(){nNotMappedProperty++;}
	public static void increment_Dropped_Class()	 {nDroppedClass++;}
	public static void increment_Mapped_Class()		 {nMappedClass++;}
	public static void increment_NotMapped_Class()	 {nNotMappedClass++;}
	public static void increment_Resolved_Class()	 {nResolved++;}

	public static void outputMappingStatistics(){
		logger.info("Null Values: "+nNullValue);

		logger.info("Property DROPPED   : "+nDroppedProperty);
		logger.info("Property MAPPED    : "+nMappedProperty);
		logger.info("Property NOT MAPPED: "+nNotMappedProperty);

		logger.info("Class DROPPED   : " +nDroppedClass);
		logger.info("Class MAPPED    : " +nMappedClass);
		logger.info("Class NOT MAPPED: " +nNotMappedClass);
		logger.info("Class RESOLVED  : " +nResolved);

		for(String s:PersistentMap.sortByValue(mapCount).keySet())
			if(mapCount.get(s)>200)
				logger.info("Missing mapping: "+mapCount.get(s)+ ": "+s);
	}
}
