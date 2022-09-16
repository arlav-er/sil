/**
 * 
 */
package it.eng.sil.coop.webservices.clicLavoro;

import java.math.BigDecimal;
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
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.util.DBAccess;
import it.eng.sil.util.xml.XMLValidator;

/**
 * @author iescone <- vuoi un router?
 *
 */
public abstract class CLUtility {

	public final static int CODE_XML_VALIDATION = 1;
	public final static String ERR_XML_VALIDATION = "Errore nella generazione dell'xml di risposta";

	/**
	 * L'utilizzo di questo costruttore non permette la validazione xsd
	 */
	public CLUtility() {
	}

	/**
	 * Se il parametro schemaXSD è null, non esegue la validazione xsd
	 * 
	 * @param schemaXSD
	 */
	public CLUtility(String schemaXSD) {
		this.schemaXSD = schemaXSD;
	}

	static Logger _logger = Logger.getLogger(CLUtility.class.getName());
	/**
	 * Documento di appoggio all'interno del quale viene costruito l'xml
	 */
	protected Document doc;
	protected DocumentBuilderFactory factory;
	protected DocumentBuilder parser;
	protected String schemaXSD;

	public static final int TIPO_CLICLAVORO_CANDIDATURA = 6;
	public static final int TIPO_CLICLAVORO_VACANCY = 7;

	public final static String TAG_PREFIX = "cliclavoro:";
	public final static String TAG_CANDIDATURA = TAG_PREFIX + "Candidatura";
	public static final String TAG_LAVORATORE = TAG_PREFIX + "Lavoratore";
	public static final String TAG_DATIANAGRAFICI = TAG_PREFIX + "DatiAnagrafici";
	public static final String TAG_DOMICILIO = TAG_PREFIX + "Domicilio";
	public static final String TAG_RECAPITI = TAG_PREFIX + "Recapiti";
	public static final String TAG_DATICURRICULARI = TAG_PREFIX + "DatiCurriculari";
	public static final String TAG_DATIAGGIUNTIVI = TAG_PREFIX + "DatiAggiuntivi";
	public static final String TAG_DATIAGGIUNTIVIGENERALI = TAG_PREFIX + "DatiGenerali";
	public static final String TAG_DATIAGGIUNTIVIABILITAZIONI = TAG_PREFIX + "Abilitazioni";
	public static final String TAG_DATIAGGIUNTIVIANNOTAZIONI = TAG_PREFIX + "Annotazioni";
	public static final String TAG_ESPERIENZELAVORATIVE = TAG_PREFIX + "EsperienzeLavorative";
	public static final String TAG_ISTRUZIONE = TAG_PREFIX + "Istruzione";
	public static final String TAG_FORMAZIONE = TAG_PREFIX + "Formazione";
	public static final String TAG_LINGUE = TAG_PREFIX + "Lingue";
	public static final String TAG_CONOSCENZEINFORMATICHE = TAG_PREFIX + "ConoscenzeInformatiche";
	public static final String TAG_ABILITAZIONIPATENTI = TAG_PREFIX + "AbilitazioniPatenti";
	public static final String TAG_PROFESSIONEDESIDERATADISPONIBILITA = TAG_PREFIX
			+ "ProfessioneDesiderataDisponibilita";
	public static final String TAG_DATISISTEMA = TAG_PREFIX + "DatiSistema";
	public static final String TAG_ALTREINFORMAZIONI = TAG_PREFIX + "AltreInformazioni";
	public static final String TAG_ABILITAZIONIAL = TAG_PREFIX + "Albi";
	public static final String TAG_ABILITAZIONIPG = TAG_PREFIX + "Patenti";
	public static final String TAG_ABILITAZIONIPT = TAG_PREFIX + "Patentini";
	public static final String TAG_DISPONIBILITA = TAG_PREFIX + "Disponibilita";
	public static final String TAG_DISPONIBILITATERRITORIO = TAG_PREFIX + "Territorio";
	public static final String TAG_DISPONIBILITATERRITORIOCOMUNE = TAG_PREFIX + "DispComune";
	public static final String TAG_DISPONIBILITAMOBILITA = TAG_PREFIX + "Mobilita";
	public static final String TIPOCOMUNICAZIONECL = "01";
	public static final String TIPOCOMUNICAZIONEBLEN = "01_BLEN";
	public static final String CONFIGURAZIONE_CURR_CLIC_LAVORO = "CUR_CLIC";
	public static final String CONFIGURAZIONE_DEFAULT = "0";
	public static final String CONFIGURAZIONE_CUSTOM = "1";
	public static final String AMBITO_DIFFUSIONE_REGIONALE = "01";

	public final static String TAG_VACANCY = TAG_PREFIX + "Vacancy";
	public static final String TAG_DATORELAVORO = TAG_PREFIX + "DatoreLavoro";
	public static final String TAG_DATICONTATTO = TAG_PREFIX + "DatiContatto";
	public static final String TAG_RICHIESTA = TAG_PREFIX + "Richiesta";
	public static final String TAG_PROFILORICHIESTO = TAG_PREFIX + "ProfiloRichiesto";
	public static final String TAG_ISTRUZIONEFORMAZIONE = TAG_PREFIX + "IstruzioneFormazione";
	public static final String TAG_TITOLOSTUDIO = TAG_PREFIX + "titolostudio";
	public static final String TAG_LINGUA = TAG_PREFIX + "Lingua";
	public static final String TAG_CONDIZIONIOFFERTE = TAG_PREFIX + "CondizioniOfferte";
	public static final String TAG_DURATARICHIESTA = TAG_PREFIX + "DurataRichiesta";

	public static final Pattern codiceFiscaleCheck = Pattern
			.compile("([A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])|([0-9]{11})");
	public static final Pattern siNoCheck = Pattern.compile("(SI|NO)");
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
	public static final Pattern numerico1Check = Pattern.compile("\\d{1}");
	public static final Pattern titoloStudioCheck = Pattern.compile("[0-9]{2}");
	public static final Pattern linguaCheck = Pattern.compile("[0-9]{3}|[A-Z]{3}|WU");
	public static final Pattern livelloLinguaCheck = Pattern.compile("[A-C]{1}[1-2]{1}");
	public static final Pattern albiCheck = Pattern.compile("[0]{2}[0-9]{2}");
	public static final Pattern patentiCheck = Pattern.compile("[0-9]{2}");
	public static final Pattern listeSpecialiCheck = Pattern.compile("\\d{1}");
	// public static final Pattern qualificaCheck = Pattern.compile("([0-9]\\.){4}[0-9]{1,2}");
	public static final Pattern qualificaCheck = Pattern.compile("([0-9]\\.){5}[0-9]{1,2}");// NEW...è quella nell'xsd
																							// versione 33..se non
																							// facessim questo
																							// controllo, comunque
																							// fallirebbe la validazione
																							// sullo schema
	public static final Pattern ccnlCheck = Pattern.compile("[0-9,A-Z]{3}|[A-Z]{2}");
	public static final Pattern modalitaCheck = Pattern.compile("[A-Z]{2}");
	public static final Pattern tipoComunicazioneCheck = Pattern.compile("(01|02)");
	public static final Pattern rapportiCheck = Pattern.compile("[A-Z]{1}[0-9]{2}");
	public static final Pattern cittadinanzaCheck = Pattern.compile("\\d{3}");
	public static final Pattern durataCheck = Pattern.compile("\\d*{4}");
	public static final Pattern tipodurataCheck = Pattern.compile("[OGMA]{1}");
	public static final Pattern tipoesperienzaCheck = Pattern.compile("[A-Z]{1}[0-9]{2}");
	public static final Pattern attestazionedurataCheck = Pattern.compile("[NFQSCT]{1}");
	public static final Pattern dispTerritCheck = Pattern.compile("\\d{2}");
	public static final Pattern dataCheck = Pattern
			.compile("(19|20)\\d\\d([- /.])(0[1-9]|1[012])([- /.])(0[1-9]|[12][0-9]|3[01])");
	public static final Pattern dataTimeCheck = Pattern
			.compile("(19|20)\\d\\d([- /.])(0[1-9]|1[012])([- /.])(0[1-9]|[12][0-9]|3[01])T(\\d{2}):(\\d{2}):(\\d{2})");

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

		try {
			xmlOut = XMLValidator.domToString(doc);
			if (this.schemaXSD != null) {
				String validityErrors = XMLValidator.getValidityErrors(xmlOut, this.schemaXSD);
				if (validityErrors != null) {
					_logger.error("Errore nella validazione dell'xml da inviare" + validityErrors);
					_logger.error("xml generato: " + xmlOut);
					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_XML_VALIDATION,
							validityErrors);
				}
			} else {
				_logger.error("XSD nullo nella validazione della candidatura");
			}
			return xmlOut;
		} catch (TransformerException e) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_XML_VALIDATION, e.getMessage());
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

	public String groupByColumnWithSeparator(SourceBean bean, int maxLength, String separator) {
		String value = "";
		Vector<String> vctRecords = new Vector<String>();
		Vector<SourceBean> rows = bean.getAttributeAsVector("ROW");

		// per ogni record presente nel sourceBean
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = rows.get(i);
			Vector<SourceBeanAttribute> attributi = row.getContainedAttributes();

			if (!attributi.isEmpty()) {
				String tipo = (attributi.get(0)).getValue().toString();
				if (!vctRecords.contains(tipo)) {
					value += tipo;
					if (i < (rows.size() - 1)) {
						value += separator;
					}
					vctRecords.add(tipo);
				}
			}
		}
		if (value.length() > 0) {
			if (value.length() > maxLength) {
				int maxL = maxLength - 3;
				if (maxL < 0) {
					maxL = 0;
				}
				value = value.substring(0, maxL) + "...";
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

	public SourceBean groupByColumnWithSeparator(SourceBean bean, String attribute, int maxLength, String separator) {
		SourceBean sourceBean = null, sourceBeanRow = null;
		try {
			sourceBean = new SourceBean("ROWS");
			sourceBeanRow = new SourceBean("ROW");
			sourceBeanRow.setAttribute(attribute, groupByColumnWithSeparator(bean, maxLength, separator));
			sourceBean.setAttribute(sourceBeanRow);
		} catch (SourceBeanException e) {
			_logger.error("Errore durante la chiamata al metodo groupByColumnWithSeparator: " + e.getMessage());
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

	public String groupByFirstColumnWithoutEndingSeparator(SourceBean bean, int maxLength) {
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
			s = s.substring(0, s.lastIndexOf(","));
			if (iterator.hasNext()) {
				s += "; ";
			}
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

	public SourceBean groupByFirstColumnWithoutEndingSeparator(SourceBean bean, String attribute, int maxLength) {
		SourceBean sourceBean = null, sourceBeanRow = null;
		try {
			sourceBean = new SourceBean("ROWS");
			sourceBeanRow = new SourceBean("ROW");
			sourceBeanRow.setAttribute(attribute, groupByFirstColumnWithoutEndingSeparator(bean, maxLength));
			sourceBean.setAttribute(sourceBeanRow);
		} catch (SourceBeanException e) {
			_logger.error(
					"Errore durante la chiamata al metodo groupByFirstColumnWithoutEndingSeparator: " + e.getMessage());
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

	/**
	 * 
	 * @deprecated 11/10/2012 vedi metodo getCodComunicazione(int tipoComunicazione, DataConnection dc)
	 * 
	 * @return codice comunicazione valorizzato con venticinque zeri
	 */
	public String getCodComunicazione() {
		return "0000000000000000000000000";
	}

	/**
	 * 
	 * @param tipoComunicazione
	 * @param dc
	 * @return
	 * @throws EMFUserError
	 */
	public String getCodComunicazione(int tipoComunicazione, DataConnection dc) throws EMFUserError {

		String codComunicazione = "";
		Object[] params = null;
		String codModuloCom = "0";

		// recupera la targa della provincia
		SourceBean sbTargaProvincia = executeSelect("GET_CL_TARGA_PROVINCIA");
		String strTargaProvincia = (String) sbTargaProvincia.getAttribute("ROW.STRTARGA");

		// recupera progressivo comunicazione e anno
		params = new Object[1];
		if (TIPO_CLICLAVORO_CANDIDATURA == tipoComunicazione) {
			params[0] = TIPO_CLICLAVORO_CANDIDATURA;
			codModuloCom = "2";
		} else if (TIPO_CLICLAVORO_VACANCY == tipoComunicazione) {
			params[0] = TIPO_CLICLAVORO_VACANCY;
			codModuloCom = "1";
		}
		SourceBean sbCodComunicazione = executeSelect("GET_CL_CODCOMUNICAZIONE", params, dc);
		String annoInvio2dgt = (String) sbCodComunicazione.getAttribute("ROW.ANNO");
		BigDecimal numProgressivo = (BigDecimal) sbCodComunicazione.getAttribute("ROW.CODCOMUNICAZIONE");
		String prgCom = StringUtils.getAttributeStrNotNull(sbCodComunicazione, "ROW.CODCOM");

		// infine si uniscono le stringhe
		codComunicazione = "SIL" + strTargaProvincia + annoInvio2dgt + codModuloCom + prgCom;

		if (codComunicazione.length() < 25) {
			_logger.error("Errore nella costruzione del codComunicazione ClicLavoro" + codComunicazione);
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
		} else {
			Object[] paramsUpdate = new Object[2];
			paramsUpdate[0] = numProgressivo.add(new BigDecimal("1"));
			if (TIPO_CLICLAVORO_CANDIDATURA == tipoComunicazione) {
				paramsUpdate[1] = TIPO_CLICLAVORO_CANDIDATURA;
			} else if (TIPO_CLICLAVORO_VACANCY == tipoComunicazione) {
				paramsUpdate[1] = TIPO_CLICLAVORO_VACANCY;
			}
			boolean checkUpdate = (Boolean) QueryExecutor.executeQuery("UPDATE_CL_CODCOMUNICAZIONE", paramsUpdate,
					"UPDATE", Values.DB_SIL_DATI);
			if (!checkUpdate) {
				_logger.error("Errore nell'aggiornamento del progressivo nella tabella TS_PROGRESSIVO");
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
			}
		}

		return codComunicazione;
	}
}
