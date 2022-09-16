package it.eng.myportal.servlet;

import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 * 
 * 
 * @author coticone
 *
 */
@WebServlet(urlPatterns = { "/faces/secure/getCleanSolr" })
public class GetCleanSolr extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String xml = "";
		String baseDominio = ConstantsSingleton.getSolrUrl();
		Document xmlSolr = Utils.documentSOLR(baseDominio + "/core0/import?command=full-import&clean=true");
		if (xmlSolr == null) {
			xml = "";
		} else {
			try {
				xml = format(xmlSolr);
			} catch (TransformerFactoryConfigurationError e) {
				log.error(e.getMessage());
			} catch (TransformerException e) {
				log.error(e.getMessage());
			}

		}

		OutputStreamWriter w = new OutputStreamWriter(response.getOutputStream());

		w.write(xml);
		w.flush();
		w.close();
	}

	public String format(Document document) throws TransformerFactoryConfigurationError, TransformerException {

		StringWriter stw = new StringWriter();
		Transformer serializer = TransformerFactory.newInstance().newTransformer();
		serializer.transform(new DOMSource(document), new StreamResult(stw));
		return stw.toString();

	}

}
