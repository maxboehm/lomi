package com.maximilian_boehm.lod.tools.reader.lazy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;

import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;

public class DBpediaHierarchy {

	private static Map<String, Set<String>> mapHierarchy = null;
	private static Map<String, Set<String>> mapWorkingHierarchy = new HashMap<String, Set<String>>();


	public static void printClasses(String sClass){
		System.out.println(sClass);
		for(String s:getParentClasses(sClass))
			System.out.println("   "+s);
	}


	public static Set<String> getParentClasses(String sType){
		if(mapHierarchy==null) init();
		return mapHierarchy.get(sType);
	}

	private static void init(){
		mapHierarchy = new HashMap<String, Set<String>>();
		try {
			new InstanceReader(Settings.getMappingFile("db_sorted.txt").getAbsolutePath()).readInstances(true, new InstanceHandler() {
				@Override
				public void handleInstance(ExtendedInstance instance, long lLineCounter) {
					for(String sType:instance.getTypes())
						if(sType.equals("http://www.w3.org/2002/07/owl#Class")){
							Set<String> setSubClassOf = new HashSet<>();
							for(Statement st:instance.getStatements()){
								if(st.getPredicate().toString().equals("http://www.w3.org/2000/01/rdf-schema#subClassOf")){
									String sSubClass = st.getObject().toString();
									if(sSubClass.startsWith("http://dbpedia.org/ontology/") && !sSubClass.equals("http://dbpedia.org/ontology/Agent"))
										setSubClassOf.add(sSubClass);
								}
							}

							mapWorkingHierarchy.put(instance.getURI(), setSubClassOf);
							return;
						}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String sURI:mapWorkingHierarchy.keySet()){

			Set<String> setOldParentClasses = mapWorkingHierarchy.get(sURI);
			Set<String> setParentClasses = new HashSet<>();
			for(String sParentClass:setOldParentClasses){
				resolveNode(sParentClass, setParentClasses);
			}
			setOldParentClasses.addAll(setParentClasses);
			setParentClasses.remove("http://www.w3.org/2002/07/owl#Thing");
			mapHierarchy.put(sURI, setParentClasses);
		}
	}

	@SuppressWarnings("unused")
	private void printHierarchy(){
		for(String sURI:mapHierarchy.keySet()){
			System.out.print(sURI+": ");
			for(String sParentClass:mapHierarchy.get(sURI)){
				System.out.print(sParentClass+", ");
			}
			System.out.println();
		}

	}

	private static void resolveNode(String sParentClass, Set<String> setParentClasses){
		Set<String> setCurrentParentClasses = mapWorkingHierarchy.get(sParentClass);

		if(setCurrentParentClasses==null)
			return;

		setParentClasses.add(sParentClass);
		setParentClasses.addAll(setCurrentParentClasses);


		for(String s:setCurrentParentClasses)
			resolveNode(s, setParentClasses);
	}

}
