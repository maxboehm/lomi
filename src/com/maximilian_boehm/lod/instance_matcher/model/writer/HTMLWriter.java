package com.maximilian_boehm.lod.instance_matcher.model.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

import org.openrdf.model.Statement;

import com.maximilian_boehm.lod.instance_matcher.model.Match;
import com.maximilian_boehm.lod.instance_matcher.model.Result;
import com.maximilian_boehm.lod.tools.reader.instance.Instance;

/**
 * Create human readable visual output for mapping
 */
public class HTMLWriter implements ResultWriter {
	// Member
	DecimalFormat numberFormatter = new DecimalFormat("#,###,###,##0.00");

	@Override
	public void write(File f, Result result) throws Exception {
		StringBuffer sb = new StringBuffer();

		// Create header
		sb.append(
				"<html><head></head><body><link rel='stylesheet' href='stylesheet.css'></link><script src='http://www.kryogenix.org/code/browser/sorttable/sorttable.js'></script><table class='sortable'>");
		sb.append(
				"<tr><td>Type</td><td>Score</td><td>Matching Type</td><td>DBO-Link</td><td>SCO Properties</td><td>DBP Properties</td><td>Classes</td><td></td><td></td></tr>");

		// Iterate over matches
		for (Match m : result.getMatches()) {
			// Each match is a new row
			String sResult = "<tr>";
			// Each attribute is a now column
			sResult += append(m.getTyp().toString());
			sResult += append(numberFormatter.format(m.calculateScore()));
			sResult += append(m.getDbpName());
			sResult += append(getMatchingTypes(m));
			sResult += append(getMatchingProperties(m));
			sResult += append(m.getDBPInstance().getURI());
			sResult += append(getStatements(m.getSCOInstance()));
			sResult += append(getStatements(m.getDBPInstance()));
			sResult += append(getTypes(m.getDBPInstance()));
			sResult += "</tr>";
			sResult += "\r\n";
			// Do some replacements for better readability (Otherwise, some columns would be too long)
			sResult = sResult.replace("http://dbpedia.org", "dbo");
			sResult = sResult.replace("dbo/resource/", "dbo/r/");
			sResult = sResult.replace("dbo/resource/", "dbo/r/");
			sResult = sResult.replace("dbo/ontology/", "dbo/o/");
			sResult = sResult.replace("http://www.opengis.net/gml/", "opengis/");
			sb.append(sResult);
		}

		// Close
		sb.append("</table></body></html>");

		// Write it to a file
		try (BufferedWriter bwr = new BufferedWriter(new FileWriter(f));) {
			bwr.write(sb.toString());
			bwr.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Replace character which would make problems and embed it into a column
	 * @param sAppend
	 * @return
	 */
	private String append(String sAppend) {
		sAppend = sAppend.replaceAll("\\r\\n|\\r|\\n", " ");
		sAppend = sAppend.replace(";", ",");
		sAppend = sAppend.replace("<", "&#60;");
		sAppend = sAppend.replace(">", "&#62;");
		sAppend = sAppend.replace("&#60;br&#62;", "<br>");

		return "<td>" + sAppend + "</td>";

	}

	/**
	 * Return all types which both instances have
	 * @param m
	 * @return
	 */
	public String getMatchingTypes(Match m) {
		String s = "";
		for (String sType : m.getTypes())
			s += sType + "\n";
		return s;
	}

	/**
	 * Return all properties both instances have
	 * @param m
	 * @return
	 */
	public String getMatchingProperties(Match m) {
		String s = "";
		for (String sType : m.getProperties())
			s += sType + "\n";
		return s;
	}

	/**
	 * Return types encoded in HTML
	 * @param instance
	 * @return
	 */
	public String getTypes(Instance instance) {
		String sType = "";
		for (String s : instance.getTypes())
			sType += s + "<br>";

		return sType.length() > 2 ? sType.substring(0, sType.length() - 1) : "";
	}

	/**
	 * Return statements encoded in HTML
	 * @param instance
	 * @return
	 */
	public String getStatements(Instance instance) {
		String sType = "";
		if (instance.getStatements() != null)
			for (Statement st : instance.getStatements())
				sType += st.toString() + "<br>";

		return sType.length() > 2 ? sType.substring(0, sType.length() - 1) : "";
	}

}
