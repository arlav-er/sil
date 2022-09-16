/*
 * Created on 16-nov-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.webservices.madreperla.servizi;

import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * @author loc_esposito
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public abstract class MadreperlaServiceImpl {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MadreperlaServiceImpl.class.getName());
	protected String response;
	protected Document docInput;

	static final private String namespace = "http://www.satanet.it/Madreperla-SIL";

	private String pathSchemaInput;
	private String pathSchemaOutput;
	private String childClass;

	public MadreperlaServiceImpl(String _pathSchemaInput, String _pathSchemaOutput, String _childClass) {
		response = "";
		pathSchemaInput = ConfigSingleton.getRootPath() + "\\WEB-INF\\xsd\\MadreperlaXsd\\" + _pathSchemaInput;
		pathSchemaOutput = ConfigSingleton.getRootPath() + "\\WEB-INF\\xsd\\MadreperlaXsd\\" + _pathSchemaOutput;
		childClass = _childClass;
	}

	public void caricaInputXml(String inputXml) throws ValidazioneException {
		try {

			docInput = UtilityXml.validaXml(inputXml, namespace, pathSchemaInput);

		} catch (SAXNotRecognizedException ne) {
			it.eng.sil.util.TraceWrapper.error(_logger, childClass + ":caricaInputXml", ne);

			throw new ValidazioneException("Errori nella validazione");
		} catch (SAXNotSupportedException se) {
			it.eng.sil.util.TraceWrapper.error(_logger, childClass + ":caricaInputXml", se);

			throw new ValidazioneException("Errori nella validazione");
		} catch (IOException ie) {
			it.eng.sil.util.TraceWrapper.error(_logger, childClass + ":caricaInputXml", ie);

			throw new ValidazioneException("Errori nella validazione");
		} catch (SAXException se) {
			it.eng.sil.util.TraceWrapper.error(_logger, childClass + ":caricaInputXml", se);

			throw new ValidazioneException("Errori nella validazione");
		}
	}

	public String getResponse() throws ValidazioneException {
		/*
		 * try{
		 * 
		 * Document doc = UtilityXml.validaXml(response, namespace, pathSchemaOutput);
		 * 
		 * }catch (SAXNotRecognizedException ne) { it.eng.sil.util.TraceWrapper.debug( _logger, childClass
		 * +":getResponse", ne);
		 * 
		 * throw new ValidazioneException("Errori nella validazione"); } catch (SAXNotSupportedException se) {
		 * it.eng.sil.util.TraceWrapper.debug( _logger, childClass +":getResponse", se);
		 * 
		 * throw new ValidazioneException("Errori nella validazione"); } catch (IOException ie){
		 * it.eng.sil.util.TraceWrapper.debug( _logger, childClass +":getResponse", ie);
		 * 
		 * throw new ValidazioneException("Errori nella validazione"); } catch (SAXException se){
		 * it.eng.sil.util.TraceWrapper.debug( _logger, childClass +":getResponse", se);
		 * 
		 * throw new ValidazioneException("Errori nella validazione"); }
		 */

		// return UtilityXml.adeguamentoCaratteriNonValidi(response);
		return response;
	}

	abstract public void esegui() throws Exception;

}