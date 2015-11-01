package com.maximilian_boehm.lod.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class PersistentMap {


	public static void write(File fFile, Map<String, String> map) throws Exception{

		try(FileWriter fstream = new FileWriter(fFile);
				BufferedWriter out= new BufferedWriter(fstream);){


			SortedSet<String> keys = new TreeSet<String>(map.keySet());

			for(String sKey:keys){
				String sVal = map.get(sKey);
				if(sVal==null)
					out.write(sKey+"\n");
				else
					out.write(sKey+" "+sVal+"\n");
			}

		}
	}

	public static Map<String, String> read(File fFile){
		return read(fFile, false);
	}
	public static Map<String, String> read(File f, boolean bLowerCase){
		Map<String, String> map = new HashMap<String, String>();
		try {
			if(!f.exists())
				return map;

			try(BufferedReader br = new BufferedReader(new FileReader(f))) {
				for(String line; (line = br.readLine()) != null; ) {
					String[] str = line.split(" ");
					String sKey = bLowerCase ? str[0].toLowerCase() : str[0];
					String sValue = null;
					if(str.length==2)
						sValue= str[1];

					if(sValue!=null)
						sValue.trim();

					map.put(sKey.trim(), sValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}
	public static Multimap<String, String> readMultiMap(File f, boolean bLowerCase){
		Multimap<String, String> map =ArrayListMultimap.create();

		try {
			if(!f.exists())
				return map;

			try(BufferedReader br = new BufferedReader(new FileReader(f))) {
				for(String line; (line = br.readLine()) != null; ) {
					String[] str = line.split(" ");
					String sKey = bLowerCase ? str[0].toLowerCase() : str[0];
					String sValue = null;
					if(str.length==2)
						sValue= str[1];

					if(sValue!=null)
						sValue.trim();

					map.put(sKey.trim(), sValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}


	public static void writeMultiMap(File fFile, Multimap<String, String> map) throws Exception{

		try(FileWriter fstream = new FileWriter(fFile);
				BufferedWriter out= new BufferedWriter(fstream);){

			SortedSet<String> keys = new TreeSet<String>(map.keySet());

			for(String sDbpURI:keys){

				for(String sScoURI: map.get(sDbpURI)){
					if(sScoURI==null)
						out.write(sDbpURI+"\n");
					else
						out.write(sDbpURI+" "+sScoURI+"\n");
				}
			}
		}
	}
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {

		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());

		return result;
	}


}
