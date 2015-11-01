package com.maximilian_boehm.lod.tools.reader.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.Serializable;

import org.openrdf.model.Statement;

import com.maximilian_boehm.lod.tools.MD5Hash;
import com.maximilian_boehm.lod.tools.URIHelper;

public class ExtendedInstance extends BaseInstance implements Serializable{

	// members
	private static final long serialVersionUID = 1L;
	private Set<Statement> setStatements = new HashSet<Statement>();
	private String sHash = null;
	private String sURI = null;

	public void addStatement(Statement st){
		if(!setStatements.contains(st))
			setStatements.add(st);
	}

	public String getHash(){
		if(sHash==null) sHash = MD5Hash.calculateHash(getHashKey());
		return sHash;
	}

	public ExtendedInstance(){}

	public LazyInstance getLazyInstance(){
		LazyInstance si = new LazyInstance();
		si.setURI(getURI());
		Set<String> setOldNames = getNames();
		setOldNames.addAll(getValueByStatement("http://dbpedia.org/property/birthName"));
		setOldNames.addAll(getValueByStatement("http://dbpedia.org/ontology/alias"));
		setOldNames.addAll(getValueByStatement("http://dbpedia.org/ontology/alternativeName"));
		setOldNames.addAll(getValueByStatement("http://dbpedia.org/property/alternativeNames"));
		setOldNames.addAll(getValueByStatement("http://dbpedia.org/property/otherName"));
		Set<String> setNewNames = new HashSet<String>();


		// For example, if there are two names
		// Shelly &
		// Shelly Kishore
		// Only take the most specific one!

		boolean bIsOfTypePerson = isOfType("http://dbpedia.org/ontology/Person");

		for(String sName:setOldNames){
			boolean bExclude = false;
			if(sName.toLowerCase().equals("unknown") || sName.toLowerCase().equals("null"))
				bExclude = true;

			if(bIsOfTypePerson && !sName.contains(" "))
				bExclude = true;

			if(!bExclude)
				for(String sName2:setOldNames)
					if(!sName.equals(sName2) && sName2.contains(sName) )
						bExclude = true;

			if(!bExclude)
				setNewNames.add(sName);
		}



		si.setNames(setNewNames);
		return si;
	}

	/**
	 * Create the hash of the statements
	 * Predicates and Objects which are not URIs will be taken into account
	 * @return
	 */
	public String getHashKey(){

		// Create a string of the size of the statements
		StringBuilder sb = new StringBuilder(setStatements.size()*15);

		// Order statements alphabetically
		List<Statement> list = new ArrayList<Statement>(setStatements);
		Collections.sort(list, new Comparator<Statement>() {
			@Override
			public int compare(Statement e1, Statement e2) {
				return e1.getPredicate().toString().compareTo(e2.getPredicate().toString());
			}
		});

		for(Statement st:list){
			String sPredicate = st.getPredicate().toString();
			String sObject = st.getObject().stringValue();

			sb.append(sPredicate);

			if(sObject.startsWith("genid-") || sObject.startsWith("_:"))
				continue;
			// Exclusion of description is used to stop abusing as a breadcrumb-navigation (Changing text in description)
			else if(sObject.startsWith("http://") || (list.size()>=4 && sPredicate.endsWith("/description")))
				continue;
			else
				sb.append(sObject);
		}
		return sb.toString();
	}

	public Set<String> getValueByStatement(String sPredicate){
		Set<String> setValue = new HashSet<String>();
		for(Statement st:getStatements()){
			if(st.getPredicate().stringValue().equals(sPredicate))
				setValue.add(st.getObject().toString());
		}
		return setValue;
	}

	@Override
	public Set<String> getNames(){
		Set<String> setNames = new HashSet<String>();

		for(Statement st:getStatements())
			if(isNameStatement(st))
				setNames.add(st.getObject().stringValue());

		return setNames;
	}

	@Override
	public Set<Statement> getStatements(){
		return setStatements;
	}

	@Override
	public Set<String> getTypes(){
		Set<String> setTypes = new HashSet<String>();
		for(Statement st:getStatements())
			if(isTypeStatement(st))
				setTypes.add(st.getObject().toString());
		return setTypes;
	}

	@Override
	public String getURI() {
		return sURI;
	}

	public void replaceStatements(Set<Statement> newStatements){
		setStatements = newStatements;
	}

	public void setHash(String sHash) {
		this.sHash = sHash;
	}


	public void setURI(Statement st) {
		sURI = URIHelper.getURI(st);
	}

	@Override
	public String toString() {
		String s = hashCode()+"\r\n";
		for(Statement st:setStatements)
			s += st.getSubject()+" "+st.getPredicate()+" "+st.getObject()+" \r\n";
		return s;
	}


}
