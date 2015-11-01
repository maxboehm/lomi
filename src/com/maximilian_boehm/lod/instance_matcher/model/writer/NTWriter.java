package com.maximilian_boehm.lod.instance_matcher.model.writer;

import java.util.HashSet;
import java.util.Set;

import java.io.File;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.maximilian_boehm.lod.instance_matcher.model.Match;
import com.maximilian_boehm.lod.instance_matcher.model.Result;
import com.maximilian_boehm.lod.tools.NTripleWriter;
import com.maximilian_boehm.lod.tools.reader.instance.BaseInstance;
import com.maximilian_boehm.lod.tools.reader.instance.Instance;

/**
 * Write the TRIPLES
 */
public class NTWriter implements ResultWriter {

	// Factory for creating URIs
	private ValueFactory factory = ValueFactoryImpl.getInstance();


	@Override
	public void write(File f, Result result) throws Exception {
		// Open the Triple Writer
		try (NTripleWriter writer = new NTripleWriter(f);){

			// Iterate over matches
			for(Match m:result.getMatches()){
				Instance instanceSCO = m.getSCOInstance();

				Set<Statement> setSTSCO = instanceSCO.getStatements();
				Set<Statement> setSTDBP = m.getDBPInstance().getStatements();

				// If a instance would have no statements, just skip it
				if(setSTSCO==null){
					System.out.println("setstsco==null "+instanceSCO);
					continue;
				}
				if(setSTDBP==null){
					System.out.println("setSTDBP==null "+m.getDBPInstance().getURI());
					continue;
				}

				// Iterate over the difference-set of Schema.org to dbpedia
				for(Statement st:getDifference(setSTSCO, setSTDBP)){
					// If it is no name or type statement
					if(!BaseInstance.isTypeStatement(st) && !BaseInstance.isNameStatement(st)){
						// Create URI
						org.openrdf.model.URI subject = factory.createURI(m.getDBPInstance().getURI());

						Value val = getReferencedObject(st.getObject(), result);
						if(val!=null){
							st = factory.createStatement(subject, st.getPredicate(), val);

							writer.writeStatement(st);
						}
					}
				}

			}
		}
	}

	/**
	 * @param value
	 * @param result
	 * @return
	 */
	private Value getReferencedObject(Value value, Result result){
		if(!(value instanceof URI))
			return value;

		String sValue = value.stringValue();

		if(sValue.startsWith("http"))
			return value;

		for(Match m:result.getMatches())
			if(m.getSCOInstance().getURI().equals(sValue))
				return factory.createURI(m.getDBPInstance().getURI());

		return null;
	}

	/**
	 * @param set1
	 * @param set2
	 * @return
	 */
	public Set<Statement> getDifference(Set<Statement> set1, Set<Statement> set2){
		Set<Statement> outputStatements = new HashSet<>();

		for(Statement st:set1)
			if(!contains(set2, st))
				outputStatements.add(st);

		return outputStatements;
	}

	/**
	 * @param set1
	 * @param st2
	 * @return
	 */
	public boolean contains(Set<Statement> set1, Statement st2){
		for(Statement st1:set1)
			if(st1.getSubject().toString().equals(st2.getSubject().toString()) && st1.getObject().toString().equals(st2.getObject().toString()))
				return true;
		return false;
	}

}
