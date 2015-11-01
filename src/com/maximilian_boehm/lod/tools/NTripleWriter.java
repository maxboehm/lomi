package com.maximilian_boehm.lod.tools;

import java.io.File;
import java.io.FileOutputStream;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

public class NTripleWriter  implements AutoCloseable{

	private RDFWriter writer;
	private FileOutputStream out;
	private File fOutputFile;


	public NTripleWriter(File fOutputFile, boolean bAppend) {
		try {
			out = new FileOutputStream(fOutputFile, bAppend);
			this.fOutputFile = fOutputFile;
			writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
			writer.startRDF();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public NTripleWriter(File fOutputFile) {
		this(fOutputFile, false);
	}

	public void writeStatement(Statement st) throws RDFHandlerException{
		writer.handleStatement(st);
	}

	public File getFile() throws Exception{
		return fOutputFile;
	}

	@Override
	public void close() throws Exception {
		writer.endRDF();
		if(out!=null) out.close();

	}

}
