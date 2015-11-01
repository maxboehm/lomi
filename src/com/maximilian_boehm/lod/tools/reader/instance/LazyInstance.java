package com.maximilian_boehm.lod.tools.reader.instance;

import java.util.HashSet;
import java.util.Set;

import java.io.Serializable;

import org.openrdf.model.Statement;

import com.maximilian_boehm.lod.tools.reader.lazy.DBpediaInstanceType;
import com.maximilian_boehm.lod.tools.reader.lazy.DBpediaProperties;

/**
 * Only a subset of a "full" instance used to save memory
 */
public class LazyInstance extends BaseInstance implements Serializable{

	private static final long serialVersionUID = 1L;
	private String URI;
	private Set<String> names;

	@Override
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	@Override
	public Set<String> getNames() {
		return names;
	}
	public void setNames(Set<String> names) {
		this.names = names;
	}

	@Override
	public Set<String> getTypes(){
		try {
			return DBpediaInstanceType.getSingleton().get(getURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashSet<String>();
	}

	Set<Statement> setSt = null;

	@Override
	public Set<Statement> getStatements(){
		if(setSt==null)
			try {
				setSt = DBpediaProperties.getSingleton().get(getURI());
			} catch (Exception e) {
				e.printStackTrace();
			}
		if(setSt==null) setSt = new HashSet<>();
		return setSt;
	}

	@Override
	public boolean isOfType(String sURI){
		for(String sType:getTypes())
			if(sType.equals(sURI))
				return true;
		return false;
	}

}
