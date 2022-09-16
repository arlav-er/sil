/**
 *
 */
package it.eng.sil.coop.webservices.myportal;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.util.DBAccess;
import it.eng.sil.util.xml.XMLValidator;

/**
 * @author iescone
 *
 */
public abstract class MyPortalUtility {
	/**
	 * L'utilizzo di questo costruttore non permette la validazione xsd
	 */
	public MyPortalUtility() {
	}

	/**
	 * Se il parametro schemaXSD è null, non esegue la validazione xsd
	 * 
	 * @param schemaXSD
	 */
	public MyPortalUtility(String schemaXSD) {
		this.schemaXSD = schemaXSD;
	}

	static Logger _logger = Logger.getLogger(MyPortalUtility.class.getName());
	/**
	 * Documento di appoggio all'interno del quale viene costruito l'xml
	 */
	protected Document doc;
	protected DocumentBuilderFactory factory;
	protected DocumentBuilder parser;
	protected String schemaXSD;

	public final static String TAG_PREFIX = "";
	// public final static String TAG_CANDIDATURA = TAG_PREFIX + "Candidatura";
	// public static final String TAG_LAVORATORE = TAG_PREFIX + "Lavoratore";
	// public static final String TAG_DOMICILIO = TAG_PREFIX + "Domicilio";
	// public static final String TAG_RECAPITI = TAG_PREFIX + "Recapiti";
	// public static final String TAG_DATICURRICULARI = TAG_PREFIX + "DatiCurriculari";
	// public static final String TAG_ESPERIENZELAVORATIVE = TAG_PREFIX + "EsperienzeLavorative";
	// public static final String TAG_ISTRUZIONE = TAG_PREFIX + "Istruzione";
	// public static final String TAG_FORMAZIONE = TAG_PREFIX + "Formazione";
	// public static final String TAG_LINGUE = TAG_PREFIX + "Lingue";
	// public static final String TAG_CONOSCENZEINFORMATICHE = TAG_PREFIX + "ConoscenzeInformatiche";
	// public static final String TAG_ABILITAZIONIPATENTI = TAG_PREFIX + "AbilitazioniPatenti";
	// public static final String TAG_PROFESSIONEDESIDERATADISPONIBILITA = TAG_PREFIX +
	// "ProfessioneDesiderataDisponibilita";
	// public static final String TAG_ALTREINFO = TAG_PREFIX + "AltreInfo";

	public final static String TAG_VACANCY = TAG_PREFIX + "Vacancy";
	public static final String TAG_DATORELAVORO = TAG_PREFIX + "DatoreLavoro";
	public static final String TAG_DATIANAGRAFICI = TAG_PREFIX + "DatiAnagrafici";
	public static final String TAG_DATICONTATTO = TAG_PREFIX + "DatiContatto";
	public static final String TAG_DATICONTATTO_ALTERNATIVO = TAG_PREFIX + "DatiContattoAlternativo";
	public static final String TAG_RICHIESTA = TAG_PREFIX + "Richiesta";
	public static final String TAG_PROFILORICHIESTO = TAG_PREFIX + "ProfiloRichiesto";
	public static final String TAG_ISTRUZIONEFORMAZIONE = TAG_PREFIX + "IstruzioneFormazione";
	public static final String TAG_TITOLOSTUDIO = TAG_PREFIX + "titolostudio";
	public static final String TAG_LINGUA = TAG_PREFIX + "Lingua";
	public static final String TAG_CONDIZIONIOFFERTE = TAG_PREFIX + "CondizioniOfferte";
	public static final String TAG_DURATARICHIESTA = TAG_PREFIX + "DurataRichiesta";
	public static final String TAG_REGISTRAZIONEAZIENDA = TAG_PREFIX + "RegistrazioneAzienda";
	public static final String TAG_DATIRICHIEDENTE = TAG_PREFIX + "DatiRichiedente";
	public static final String TAG_DATIAZIENDA = TAG_PREFIX + "DatiAzienda";
	public static final String TAG_DATISISTEMA = TAG_PREFIX + "DatiSistema";

	public static final Pattern codiceFiscaleCheck = Pattern
			.compile("([A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])|([0-9]{11})");
	public static final Pattern siNoCheck = Pattern.compile("(S|N)");
	public static final Pattern emailCheck = Pattern.compile(
			"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
	public static final Pattern atecoCheck = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{2}");
	public static final Pattern capCheck = Pattern.compile("[0-9]{5}");
	public static final Pattern sessoCheck = Pattern.compile("[MF]{1}");
	public static final Pattern comuneCheck = Pattern.compile("([A-Z]{1}\\d{3})|([0]{4})");
	public static final Pattern ampiezzaCheck = Pattern.compile("[0]{1}[0-9]{1}");
	public static final Pattern numerico0_2Check = Pattern.compile("\\d{0,2}");
	public static final Pattern numerico1_4Check = Pattern.compile("\\d{1,4}");
	public static final Pattern numerico0_9Check = Pattern.compile("\\d{0,9}");
	public static final Pattern numerico7Check = Pattern.compile("\\d{7}");
	public static final Pattern numerico1Check = Pattern.compile("\\d{1}");
	public static final Pattern titoloStudioCheck = Pattern.compile("[0-9]{2}");
	public static final Pattern linguaCheck = Pattern.compile("[0-9]{3}|[A-Z]{3}|WU");
	// public static final Pattern livelloLinguaCheck = Pattern.compile("[A-C]{1}[1-2]{1}");
	public static final Pattern albiCheck = Pattern.compile("[0]{2}[0-9]{2}");
	public static final Pattern patentiCheck = Pattern.compile("[0-9]{2}");
	public static final Pattern listeSpecialiCheck = Pattern.compile("\\d{1}");
	public static final Pattern qualificaCheck = Pattern.compile("([0-9]\\.){4}[0-9]{1,2}");
	public static final Pattern ccnlCheck = Pattern.compile("[0-9,A-Z]{3}|[A-Z]{2}");
	public static final Pattern modalitaCheck = Pattern.compile("[A-Z]{2}");
	public static final Pattern tipoComunicazioneCheck = Pattern.compile("(01|02)");
	public static final Pattern rapportiCheck = Pattern.compile("[0-9,A-Z]{2}");
	public static final Pattern cittadinanzaCheck = Pattern.compile("\\d{3}");
	public static final Pattern durataCheck = Pattern.compile("\\d*{4}");
	public static final Pattern tipodurataCheck = Pattern.compile("[OGM]{1}");
	public static final Pattern tipoesperienzaCheck = Pattern.compile("[A-Z]{1}(\\.[0-9]{2}){2}");
	public static final Pattern attestazionedurataCheck = Pattern.compile("[NFQSCT]{1}");
	public static final Pattern dispTerritCheck = Pattern.compile("\\d{2}");
	public static final Pattern dataCheck = Pattern
			.compile("(19|20|21)\\d\\d([- /.])(0[1-9]|1[012])([- /.])(0[1-9]|[12][0-9]|3[01])");

	public static final Pattern lengthRangeCheck(int min, int max) {
		return Pattern.compile(".{" + min + "," + max + "}");
	};

	public static final Pattern lengthMinCheck(int min) {
		return Pattern.compile(".{" + min + ",}");
	};

	public static final Pattern lengthMaxCheck(int max) {
		return Pattern.compile(".{," + max + "}");
	};

	/**
	 * Restituisce l'xml costruito a partire dal Document generato. Se è stato impostato lo scxhema xsd, esegue anche la
	 * validazione
	 * 
	 * @return l'xml costruito
	 * @throws EMFUserError
	 */
	public String generaXML() throws EMFUserError {
		String xmlOut;

		File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
				+ File.separator + "myportal" + File.separator + this.schemaXSD);

		try {
			xmlOut = XMLValidator.domToString(doc);
			if (this.schemaXSD != null) {
				String validityErrors = XMLValidator.getValidityErrors(xmlOut, schemaFile);
				if (validityErrors != null) {
					_logger.error("Errore nella validazione dell'xml da inviare" + validityErrors);
					_logger.error("xml generato: " + xmlOut);
					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MyPortal.CODE_XML_VALIDATION,
							validityErrors);
				}
			}
			return xmlOut;
		} catch (TransformerException e) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MyPortal.CODE_XML_VALIDATION, e.getMessage());
		}
	}

	/**
	 * Esegue una generica query che può accettare o meno dei parametri
	 * 
	 * @param query_name
	 *            nome della query da eseguire
	 * @param inputParameters
	 *            array di parametri da passare(opzionale)
	 * @return SourceBean contenente i risultati della query
	 * @throws EMFUserError
	 */
	public SourceBean executeSelect(String query_name, Object[] inputParameters, DataConnection dc)
			throws EMFUserError {

		SourceBean ret = null;

		if (dc != null) {
			DBAccess dbAcc = new DBAccess();

			try {
				ret = dbAcc.selectToSourceBean(query_name, inputParameters, dc);
			} catch (EMFInternalError e) {
				_logger.error("Errore nell'esecuzione della query: " + query_name);
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
			}
		} else {
			ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT", Values.DB_SIL_DATI);
		}

		return ret;
	}

	/**
	 * Passa più sourceBean allo stesso tagName
	 * 
	 * @see generateTag
	 * @param parent
	 * @param tagName
	 * @param sourceBean
	 *            array
	 */
	public void generateTag(Element parent, String tagName, SourceBean... sourceBeans) {
		Element element = doc.createElement(tagName);
		for (SourceBean sourceBean : sourceBeans) {
			Vector<SourceBean> rows = sourceBean.getAttributeAsVector("ROW");
			// crea un nuovo tag
			// per cogni record presente nel sourceBean
			for (SourceBean row : rows) {
				addSourceBeanToElement(row, element);
			} // prossimo record
		}
		// aggiungi l'elemento al padre
		parent.appendChild(element);
	}

	/**
	 * Genera un tag di nome tagName, figlio di parent e contenente un tag per ogni attributo delle row del sourceBean
	 * 
	 * @param parent
	 * @param tagName
	 * @param sourceBean
	 */
	public void generateTag(Element parent, String tagName, SourceBean sourceBean) {
		Vector<SourceBean> rows = sourceBean.getAttributeAsVector("ROW");

		// per ogni record presente nel sourceBean
		for (SourceBean row : rows) {
			// crea un nuovo tag
			Element element = doc.createElement(tagName);

			addSourceBeanToElement(row, element);

			// aggiungi l'elemento al padre
			parent.appendChild(element);

		} // prossimo record

		return;
	}

	/**
	 * aggiunge ad un tag, figlio di parent per ogni attributo delle row del sourceBean
	 * 
	 * @param parent
	 * @param sourceBean
	 */
	public void addTag(Element parent, SourceBean sourceBean) {
		Vector<SourceBean> rows = sourceBean.getAttributeAsVector("ROW");

		// per ogni record presente nel sourceBean
		for (SourceBean row : rows) {
			addSourceBeanToElement(row, parent);

		} // prossimo record

		return;
	}

	/**
	 * Genera un tag di nome tagName(se non già esistente quindi passato come parametro), figlio di parent e contenente
	 * un tag per ogni attributo delle row del sourceBean
	 * 
	 * @param parent
	 * @param child
	 * @param tagName
	 * @param sourceBean
	 * @return Element child
	 */
	public Element generateTagNotClosed(Element parent, Element child, String tagName, SourceBean sourceBean) {
		if (child == null) {
			// crea un nuovo tag
			child = doc.createElement(tagName);
		}
		Vector<SourceBean> rows = sourceBean.getAttributeAsVector("ROW");

		// per ogni record presente nel sourceBean
		for (SourceBean row : rows) {
			addSourceBeanToElement(row, child);

		} // prossimo record

		return child;
	}

	/**
	 * Chiude un tag parent, aggiungendone il figlio
	 * 
	 * @param parent
	 * @param child
	 */
	public void closedTag(Element parent, Element child) {
		parent.appendChild(child);
		return;
	}

	/**
	 * Aggiunge i campi di un sourceBean come figli di un tag. Il sourceBean contiene un solo 'record'
	 * 
	 * @param bean
	 * @param parent
	 */
	public void addSourceBeanToElement(SourceBean bean, Element parent) {
		Vector<SourceBeanAttribute> attributi = bean.getContainedAttributes();

		if (!attributi.isEmpty()) {
			/* per ogni attributo all'interno del sourceBean */
			for (SourceBeanAttribute attributo : attributi) {
				String nome = attributo.getKey().toLowerCase();
				Object valore = attributo.getValue();

				Element tag = doc.createElement(TAG_PREFIX + nome);
				Text node = doc.createTextNode(String.valueOf(valore));
				tag.appendChild(node);

				parent.appendChild(tag);

			}
		}
	}

	/**
	 * Restituisce una stringa che contiene i valori di tutti i records raggruppati per la sola colonna presente es.:
	 * "contenuto colonna", "contenuto colonna", "contenuto colonna", etc.
	 * 
	 * @param bean
	 *            contenente un unica colonna
	 * @param maxLenght
	 *            indica la lunghezza massima della stringa
	 * @return String stringa formattata
	 */
	public String groupByColumn(SourceBean bean, int maxLength) {
		String value = "";
		Vector<String> vctRecords = new Vector<String>();
		Vector<SourceBean> rows = bean.getAttributeAsVector("ROW");

		// per ogni record presente nel sourceBean
		for (SourceBean row : rows) {
			Vector<SourceBeanAttribute> attributi = row.getContainedAttributes();

			if (!attributi.isEmpty()) {
				String tipo = (attributi.get(0)).getValue().toString();
				if (!vctRecords.contains(tipo)) {
					value += tipo + ", ";
					vctRecords.add(tipo);
				}
			}
		}
		if (value.length() > 0) {
			value = value.substring(0, value.lastIndexOf(","));
			if (value.length() > maxLength) {
				value = value.substring(0, maxLength);
			}
		}
		return value;
	}

	/**
	 * Restituisce un SourceBean contenente un unica riga con un unico attributo il cui valore è dato dall'insieme dei
	 * valori di tutti i records raggruppati per la sola colonna presente es.: "contenuto colonna", "contenuto colonna",
	 * "contenuto colonna", etc.
	 * 
	 * @param bean
	 *            contenente solo 2 colonne(tipo, descrizione)
	 * @param attribute
	 *            il nome dell'unico attributo del SourceBean
	 * @param maxLenght
	 *            indica la lunghezza massima della stringa
	 * @return SourceBean
	 */
	public SourceBean groupByColumn(SourceBean bean, String attribute, int maxLength) {
		SourceBean sourceBean = null, sourceBeanRow = null;
		try {
			sourceBean = new SourceBean("ROWS");
			sourceBeanRow = new SourceBean("ROW");
			sourceBeanRow.setAttribute(attribute, groupByColumn(bean, maxLength));
			sourceBean.setAttribute(sourceBeanRow);
		} catch (SourceBeanException e) {
			_logger.error("Errore durante la chiamata al metodo groupByColumn: " + e.getMessage());
		}

		return sourceBean;
	}

	/**
	 * Restituisce una stringa che contiene i valori di tutti i records raggruppati per la prima colonna es.: "contenuto
	 * 1 colonna": "contenuto 2 colonna", "contenuto 2 colonna", "contenuto 2 colonna", etc.;
	 * 
	 * @param bean
	 *            contenente solo 2 colonne(tipo, descrizione)
	 * @param maxLenght
	 *            indica la lunghezza massima della stringa
	 * @return String stringa formattata
	 */
	public String groupByFirstColumn(SourceBean bean, int maxLength) {
		String value = "";
		Map<String, String> mapRecords = new TreeMap<String, String>();
		Vector<SourceBean> rows = bean.getAttributeAsVector("ROW");

		// per ogni record presente nel sourceBean
		for (SourceBean row : rows) {
			Vector<SourceBeanAttribute> attributi = row.getContainedAttributes();

			if (!attributi.isEmpty()) {
				String tipo = (attributi.get(0)).getValue().toString();
				String desc = (attributi.get(1)).getValue().toString();
				if (mapRecords.containsKey(tipo)) {
					String valoreMappa = mapRecords.get(tipo);
					valoreMappa += (desc + ", ");
					mapRecords.put(tipo, valoreMappa);
				} else {
					mapRecords.put(tipo, tipo + ":" + desc + ", ");
				}
			}
		}
		for (Iterator iterator = mapRecords.values().iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			s = s.substring(0, s.lastIndexOf(",")) + "; ";
			value += s;
		}
		if (value.length() > maxLength) {
			value = value.substring(0, maxLength);
		}
		return value;
	}

	/**
	 * Restituisce un SourceBean contenente un unica riga con un unico attributo il cui valore è dato dall'insieme dei
	 * valori di tutti i records raggruppati per la prima colonna es.: "contenuto 1 colonna": "contenuto 2 colonna",
	 * "contenuto 2 colonna", "contenuto 2 colonna", etc.;
	 * 
	 * @param bean
	 *            contenente solo 2 colonne(tipo, descrizione)
	 * @param attribute
	 *            il nome dell'unico attributo del SourceBean
	 * @param maxLenght
	 *            indica la lunghezza massima della stringa
	 * @return SourceBean
	 */
	public SourceBean groupByFirstColumn(SourceBean bean, String attribute, int maxLength) {
		SourceBean sourceBean = null, sourceBeanRow = null;
		try {
			sourceBean = new SourceBean("ROWS");
			sourceBeanRow = new SourceBean("ROW");
			sourceBeanRow.setAttribute(attribute, groupByFirstColumn(bean, maxLength));
			sourceBean.setAttribute(sourceBeanRow);
		} catch (SourceBeanException e) {
			_logger.error("Errore durante la chiamata al metodo groupByFirstColumn: " + e.getMessage());
		}

		return sourceBean;
	}

	/**
	 * Esegue una generica query che necessita di prgRichiestaAz come parametro
	 * 
	 * @param query_name
	 *            nome della query da eseguire
	 * @return SourceBean contenente i risultati della query
	 */
	protected SourceBean executeSelect(String query_name) {
		SourceBean ret = (SourceBean) QueryExecutor.executeQuery(query_name, null, "SELECT", Values.DB_SIL_DATI);
		return ret;
	}

}
