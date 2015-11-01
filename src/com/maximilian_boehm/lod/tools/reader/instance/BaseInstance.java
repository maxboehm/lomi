package com.maximilian_boehm.lod.tools.reader.instance;

import java.util.Set;

import org.openrdf.model.Statement;

import com.maximilian_boehm.lod.tools.LomiConstants;
import com.maximilian_boehm.lod.tools.reader.lazy.DBpediaHierarchy;

public abstract class BaseInstance implements Instance{

	/**
	 * "Arjen, Robben" -> "Robben, Arjen"
	 * @return
	 */
	public Set<String> getExpandedNamesByInverting(){
		Set<String> setNames = getNames();

		for(String s:getNames()){
			String[] arr = s.split(",");
			if(arr.length==2 && arr[1].length()>3 && arr[0].length()>2){
				String sNew = arr[1]+" "+arr[0];
				setNames.add(sNew.trim());
			}

			s = s.replaceAll(",", "");
			s = s.replaceAll(".", "");
			s = s.replaceAll("_", "");


			arr = s.split(" ");
			if(arr.length==2 && arr[1].length()>3 && arr[0].length()>2){
				String sNew = arr[1]+" "+arr[0];
				setNames.add(sNew.trim());
			}
		}

		return setNames;
	}

	@Override
	public Set<String> getExtendedTypeHierarchy() {
		Set<String> setTypes = getTypes();

		for(String s:getTypes()){
			Set<String> setParent = DBpediaHierarchy.getParentClasses(s);
			if(setParent!=null)
				setTypes.addAll(setParent);
		}

		return setTypes;
	}

	public static boolean hasSameParentClass(Set<String> set1, Set<String> set2){
		if(set1.isEmpty() || set2.isEmpty()) return false;

		for(String s:set1)
			if(set2.contains(s))
				return true;

		return false;
	}

	public boolean hasMoreThanOneType(){
		boolean bType = false;
		for(Statement st:getStatements())
			if(isTypeStatement(st)){
				if(bType) return true;
				if(!bType) bType=true;
			}
		return false;
	}

	@Override
	public boolean isOfType(String sURI){
		for(Statement st:getStatements())
			if(isTypeStatement(st))
				if(st.getObject().stringValue().contains(sURI))
					return true;
		return false;
	}

	public boolean hasStatement(){
		return !getStatements().isEmpty();
	}

	public boolean hasTypeDeclaration(){
		for(Statement st:getStatements())
			if(isTypeStatement(st))
				return true;
		return false;
	}

	public static boolean isNameStatement(Statement st){
		return  st.getPredicate().toString().equals("http://dbpedia.org/property/name") ||
				st.getPredicate().toString().equals("http://dbpedia.org/ontology/name") ||
				st.getPredicate().toString().equals("http://www.w3.org/2000/01/rdf-schema#label") ||
				st.getPredicate().toString().equals("http://xmlns.com/foaf/0.1/name");
	}

	public static boolean isTypeStatement(Statement st){
		return  st.getPredicate().toString().equals(LomiConstants.RDF_TYPE) ||
				st.getPredicate().toString().equals(LomiConstants.DBP_TYPE);
	}

}
