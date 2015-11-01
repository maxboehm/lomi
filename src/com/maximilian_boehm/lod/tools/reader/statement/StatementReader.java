package com.maximilian_boehm.lod.tools.reader.statement;

import java.io.InputStream;
import java.net.URL;

import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.NTriplesParserSettings;

public class StatementReader {

	public void openFile(String sFile) throws Exception{
		openFile(sFile, null);
	}

	public void openFile(String sFile, RDFHandler handler) throws Exception{
		if(!sFile.startsWith("file:"))
			sFile = "file:///"+sFile;

		URL documentUrl = new URL(sFile);
		RDFParser rdfParser = Rio.createParser(Rio.getParserFormatForFileName("test.nq"));
		rdfParser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		rdfParser.getParserConfig().set(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES, false);
		rdfParser.getParserConfig().addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);

		rdfParser.setRDFHandler(handler);

		try(InputStream inputStream = documentUrl.openStream();) {
			rdfParser.parse(inputStream, documentUrl.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
