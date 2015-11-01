package com.maximilian_boehm.lod.deduper.test;

import java.util.HashSet;
import java.util.Set;

import java.io.File;

import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import com.maximilian_boehm.lod.tools.LomiConstants;
import com.maximilian_boehm.lod.tools.TestBase;
import com.maximilian_boehm.lod.tools.reader.statement.StatementReader;

public class TestReferences extends TestBase {

	private Set<String> setSubject = new HashSet<String>();

	@Test
	public void test() throws Exception{
		executeReferenceTest(getFile("Test_References.nq", this.getClass()));
	}

	public void executeReferenceTest(File fInput)throws Exception{

		new StatementReader().openFile(fInput.getAbsolutePath(), new RDFHandlerBase(){
			@Override
			public void handleStatement(Statement st) throws RDFHandlerException {
				// Write all subjects into set
				setSubject.add(st.getSubject().stringValue());
			}
		});

		new StatementReader().openFile(fInput.getAbsolutePath(), new RDFHandlerBase(){
			@Override
			public void handleStatement(Statement st) throws RDFHandlerException {
				if(st.getObject() instanceof URIImpl && !st.getPredicate().stringValue().equals(LomiConstants.RDF_TYPE)){
					URI uri = (URI)st.getObject();
					String sURI = uri.stringValue();
					if(sURI.startsWith("_:") && !setSubject.contains(sURI))
						// Check if URI exists
						System.out.println("Reference missing: "+st);
				}

			}
		});

	}



}
