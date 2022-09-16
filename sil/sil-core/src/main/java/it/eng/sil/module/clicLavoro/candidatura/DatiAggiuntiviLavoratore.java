/**
 * 
 */
package it.eng.sil.module.clicLavoro.candidatura;

import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.webservices.clicLavoro.CLUtility;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

import java.math.BigDecimal;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFUserError;

/**
 * @author iescone
 *
 */
public class DatiAggiuntiviLavoratore extends CLUtility {
	static Logger _logger = Logger.getLogger(DatiAggiuntiviLavoratore.class.getName());
  	Object[] inputParameters;
  	private String dataInvio;
  	private String titoloCandidatura;

	public DatiAggiuntiviLavoratore(Document doc, BigDecimal cdnLavoratore, String dataInvio, String titolo) {
		this.doc = doc;
		this.dataInvio = dataInvio;
		this.titoloCandidatura = titolo;
	  	inputParameters = new Object[1];
    	inputParameters[0] = cdnLavoratore;
	}
	
	public Element createElementGenerali(Element element, DataConnection dc) throws MandatoryFieldException, FieldFormatException, EMFUserError {
		// si deve creare elemento titolo e elemento opz_tipo_decodifiche
		SourceBean ret = null;
		
		UtilsConfig utility = new UtilsConfig(CONFIGURAZIONE_CURR_CLIC_LAVORO);
		String config = utility.getConfigurazioneDefault_Custom();
		if (config.equals(CONFIGURAZIONE_DEFAULT)) {
			try {
				ret = new SourceBean("ROWS");
				SourceBean row = new SourceBean("ROW");
				row.setAttribute("opz_tipo_decodifiche", "M");
				ret.setAttribute(row);
			} catch (SourceBeanException e) {
				_logger.error(e.getMessage());
			}
			
			XMLValidator.checkFieldExists(ret, "opz_tipo_decodifiche", false, lengthRangeCheck(0, 1), "\"tipo codifiche tracciato\"");
			
		}
		else {
			if (this.titoloCandidatura != null && !this.titoloCandidatura.equals("")) {
				try {
					ret = new SourceBean("ROWS");
					SourceBean row = new SourceBean("ROW");
					row.setAttribute("titolo", titoloCandidatura);
					row.setAttribute("opz_tipo_decodifiche", "S");
					ret.setAttribute(row);
				} catch (SourceBeanException e) {
					_logger.error(e.getMessage());
				}
			}
			else {
				Object[] inputParam = new Object[1];
				inputParam[0] = inputParameters[0];
				ret = executeSelect("CL_GET_ABILITAZIONI_AGGIUNTIVE_DATI_GENERALI", inputParam, dc);		
				//Validazione per la parte applicativa
				Vector<SourceBean> rows = ret.getAttributeAsVector("ROW"); 
				for (SourceBean row : rows) {
					String titoloCand = row.getAttribute("titolo")!=null?row.getAttribute("titolo").toString():null;
					if (titoloCand == null || titoloCand.equals("")) {
						try {
							row.delAttribute("opz_tipo_decodifiche");
							row.updAttribute("titolo", titoloCandidatura);
							row.updAttribute("opz_tipo_decodifiche", "S");
						} catch (SourceBeanException e) {
							_logger.error(e.getMessage());
						}
					}
				}
			}
			
			XMLValidator.checkFieldExists(ret, "titolo", false, lengthRangeCheck(0, 100), "\"titolo candidatura\"");
			XMLValidator.checkFieldExists(ret, "opz_tipo_decodifiche", false, lengthRangeCheck(0, 1), "\"tipo codifiche tracciato\"");
		}
		
		generateTag(element, TAG_DATIAGGIUNTIVIGENERALI, ret);
		
		return element;
	}
	
	public Element createElementAbilitazioni(Element element, DataConnection dc) throws MandatoryFieldException, FieldFormatException, EMFUserError {
		
		Element abilitazioniElement = doc.createElement(TAG_DATIAGGIUNTIVIABILITAZIONI);
		
		Object[] inputParam = null;
		
		inputParam = new Object[2];
		inputParam[0] = inputParameters[0];
		inputParam[1] = dataInvio;
		
		SourceBean ret = executeSelect("CL_GET_ABILITAZIONI_AGGIUNTIVE_ALBI", inputParam, dc);			

		//Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "idalbosil", false, lengthRangeCheck(0, 8), "\"iscrizione albi sil\"");
		XMLValidator.checkFieldExists(ret, "idalbo", false, lengthRangeCheck(0, 8), "\"iscrizione albi\"");
		XMLValidator.checkFieldExists(ret, "notealbo", false, lengthRangeCheck(0, 100), "\"note iscrizione albi\"");
		
		generateTag(abilitazioniElement, TAG_ABILITAZIONIAL, ret);
		
		ret = executeSelect("CL_GET_ABILITAZIONI_AGGIUNTIVE_PATENTI", inputParam, dc);			

		//Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "idpatenteguidasil", false, lengthRangeCheck(0, 8), "\"possesso patente sil\"");
		XMLValidator.checkFieldExists(ret, "idpatenteguida", false, lengthRangeCheck(0, 8), "\"possesso patente\"");
		XMLValidator.checkFieldExists(ret, "notepatenti", false, lengthRangeCheck(0, 100), "\"note patente\"");
		
		generateTag(abilitazioniElement, TAG_ABILITAZIONIPG, ret);
		
		ret = executeSelect("CL_GET_ABILITAZIONI_AGGIUNTIVE_PATENTINO", inputParam, dc);			

		//Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "idpatentinosil", false, lengthRangeCheck(0, 8), "\"possesso patentino sil\"");
		XMLValidator.checkFieldExists(ret, "idpatentino", false, lengthRangeCheck(0, 8), "\"possesso patentino\"");
		XMLValidator.checkFieldExists(ret, "notepatentini", false, lengthRangeCheck(0, 100), "\"note patentino\"");
		
		generateTag(abilitazioniElement, TAG_ABILITAZIONIPT, ret);
		
		element.appendChild(abilitazioniElement);
		
		return element;
		
	}
	
	public Element createElementAnnotazioni(Element element, DataConnection dc) throws MandatoryFieldException, FieldFormatException, EMFUserError {
		// si deve creare elemento limitazionicv e elemento notecv		
		Object[] inputParam = null;
		
		inputParam = new Object[1];
		inputParam[0] = inputParameters[0];
		
		SourceBean ret = executeSelect("CL_GET_ABILITAZIONI_AGGIUNTIVE_ANNOTAZIONI", inputParam, dc);			

		//Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "limitazionicv", false, lengthRangeCheck(0, 3000), "\"nota che riguarda le limitazioni di disponibilità del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "notecv", false, lengthRangeCheck(0, 3000), "\"nota relativa ad altre informazioni nel CV del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "competenze", false, lengthRangeCheck(0, 2000), "\"nota curriculum  relativa ad altre informazioni nel CV del lavoratore\"");
		
		generateTag(element, TAG_DATIAGGIUNTIVIANNOTAZIONI, ret);
		
		return element;
	}

	public String getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(String dataInvio) {
		this.dataInvio = dataInvio;
	}
  	
}
