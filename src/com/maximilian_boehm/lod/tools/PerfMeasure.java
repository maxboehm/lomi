package com.maximilian_boehm.lod.tools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

public class PerfMeasure {

	private static Map<String, Integer> mapCounter = new LinkedHashMap<String, Integer>();
	private static Map<String, PMObject> mapTime = new LinkedHashMap<String, PMObject>();
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(PerfMeasure.class);

	public static void increment(String sName){
		Integer i = mapCounter.get(sName);
		if(i==null) i = 0;

		i++;

		mapCounter.put(sName, i);
	}

	public static void print(){
		for(String s:mapCounter.keySet()){
			logger.info(s+": "+mapCounter.get(s));
		}
		System.out.println("Average Time");
		for(String s:mapTime.keySet()){
			logger.info(fillString(+mapTime.get(s).avgTime())+": "+s);
		}
		System.out.println("Cumulated Time");
		for(String s:mapTime.keySet()){
			logger.info(fillString(+mapTime.get(s).cumTime())+": "+s);

		}
		System.out.println("Numbers");
		for(String s:mapTime.keySet()){
			logger.info(fillString(+mapTime.get(s).measurements())+": "+s);
		}

	}

	private static String fillString(Long l){
		String s = l.toString();
		while(s.length()<9)
			s = " "+s;
		return s;

	}

	private static PMObject get(String sName){
		PMObject obj = mapTime.get(sName);
		if(obj==null) obj = new PMObject();
		mapTime.put(sName, obj);
		return obj;

	}

	public static void start(String sName){
		get(sName).start();

	}
	public static void end(String sName){
		if(mapTime.containsKey(sName))
			get(sName).end();
	}

	public static class PMObject {

		long lStart = -1;
		long lMeasurements = 0;
		long lMTime = 0;

		public PMObject() {

		}

		public void start(){
			lStart = System.nanoTime();
		}
		public void end(){
			lMeasurements++;
			lMTime += System.nanoTime() - lStart;
		}

		public Long avgTime(){
			if(lMeasurements==0) lMeasurements = 1;
			return lMTime/lMeasurements;
		}

		public Long cumTime(){
			return lMTime;
		}
		public Long measurements(){
			return lMeasurements;
		}


	}

}
