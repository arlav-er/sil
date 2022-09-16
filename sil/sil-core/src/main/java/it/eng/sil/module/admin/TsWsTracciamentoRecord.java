package it.eng.sil.module.admin;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.module.agenda.QueryGenerica;
import oracle.sql.CLOB;

public class TsWsTracciamentoRecord extends QueryGenerica {

	public void service(SourceBean request, SourceBean response) {

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");

			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
					getResponseContainer(), pool, statement, "SELECT");

			boolean viewSoap = ("true".equalsIgnoreCase((String) request.getAttribute("viewSoap")));

			oracle.sql.CLOB soap = (CLOB) rowsSourceBean.getAttribute("ROW.STRMESSAGGIOSOAP");

			StringBuffer sb1 = new StringBuffer();

			Reader reader = soap.getCharacterStream();
			BufferedReader in = new BufferedReader(reader);
			String line = null;
			while ((line = in.readLine()) != null) {
				sb1.append(line);
			}
			if (in != null) {
				in.close();
			}

			String clobdata = sb1.toString(); // this is the clob data converted into string

			if (!viewSoap) {

				int start = clobdata.indexOf("<dati>");

				if (start == -1) {
					return;
				}
				start += 6;
				int end = clobdata.lastIndexOf("</dati>");

				clobdata = clobdata.substring(start, end);

				clobdata = clobdata.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"");
			}
			clobdata = format(clobdata);

			String indent = "";
			for (int i = 0; i < 20; i++) {
				indent += " ";
			}

			clobdata = clobdata.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;")
					.replaceAll("\n", "<br/>").replaceAll(indent, "&nbsp;");
			rowsSourceBean.updAttribute("ROW.STRMESSAGGIOSOAP", clobdata);
			// System.out.println(data);
			response.setAttribute((SourceBean) rowsSourceBean);

		} catch (Exception ex) {

		}
	}

//	public static String format(String unformattedXml) throws Exception {
//		final Document document = parseXmlFile(unformattedXml);
//
//		OutputFormat format = new OutputFormat(document);
// TODO?
//		format.setLineWidth(65);
//		format.setIndenting(true);
// TODO?
//		format.setIndent(20);
//		// System.out.println(format.getEncoding());
//		format.setMediaType("text/html");
//		format.setMethod("html");
//		// format.setLineSeparator("<br/>");
//		// format.set
//		Writer out = new StringWriter();
//		XMLSerializer serializer = new XMLSerializer(out, format);
//		serializer.serialize(document);
//
//		return out.toString();
//	}

	public static String format(String unformattedXml) throws Exception {
		final Document document = parseXmlFile(unformattedXml);
		
		StringWriter stringOut = new StringWriter();
	    try {
	       DOMSource domSource = new DOMSource(document);
	       StreamResult result = new StreamResult(stringOut);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	       transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/html");
	       transformer.setOutputProperty(OutputKeys.METHOD, "html");
	       
	       transformer.transform(domSource, result);
	    }
	    catch(TransformerException ex) {
	       throw ex;
	    }
	    return stringOut.toString().trim();		

	}
	
	private static Document parseXmlFile(String in) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(in));
		return db.parse(is);
	}

}
