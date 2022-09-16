/**
 * 
 */
package it.eng.sil.module.cigs.bean;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.StringUtils;

/**
 * @author User1
 * 
 */
public class InvioSiferXmlBuilder {

	private static final String RIF_PA_OPERAZIONE = "rif_pa_operazione";

	private static final String ENTE_SEDE_ID = "ente_sede__id";

	private static final String ID_PROPOSTA_CATALOGO = "id_proposta_catalogo";

	private static final String ANNULLAMENTO_PROPOSTA_CATALOGO = "annullamento_proposta_catalogo";

	private static final String ITALIANO_PER_STRANIERI = "italiano_per_stranieri";

	private static final String ANNULLAMENTO_OPERAZIONE = "annullamento_operazione";

	private static final String CODICE_FISCALE = "codice_fiscale";

	private static final String COGNOME = "cognome";
	private static final String NOME = "nome";
	private static final String SESSO = "sesso";
	private static final String NASCITA_DATA = "nascita__data";
	private static final String NASCITA_CODICE_CATASTALE = "nascita__codice_catastale";
	private static final String CITTADINANZA = "cittadinanza";
	private static final String CITTADINANZA_SECONDA = "cittadinanza_seconda";

	private static final String TELEFONO_CELLULARE = "telefono_cellulare";

	private static final String EMAIL = "email";

	private static final String CODICE_FISCALE_ACCORPATO = "codice_fiscale_accorpato";

	private static final String DATA_ACCORPAMENTO = "data_accorpamento";

	private static final String NON_DISPONIBILE = "Non disponibile";

	public static final String ROW = "ROW";

	private Map<BigDecimal, SourceBean> prgAltraIscr2Azienda;
	private Map<BigDecimal, List<SourceBean>> prgAccordo2Operazioni;
	private Map<BigDecimal, List<SourceBean>> prgAltraIsrc2Periodi;
	private Map<BigDecimal, List<SourceBean>> prgAltraIsrc2Servizi;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(InvioSiferXmlBuilder.class);

	public static final String CDNLAVORATORE = "CDNLAVORATORE";

	private InvioSiferXmlBuilder() {
		super();
	}

	private static final String PRG_ALTRA_ISCR = "prgaltraiscr";
	private static final String[] CITTADINANZA_BLACK_LIST = { "JUG", "CUR", "COR", "SDR" };

	/* pattern per la verifica di un indirizzo email */
	private static final Pattern emailRegExCheck = Pattern.compile(
			"(([A-Za-z0-9]+_+)|([A-Za-z0-9]+\\-+)|([A-Za-z0-9]+\\.+)|([A-Za-z0-9]+\\++))*[A-Za-z0-9]+@((\\w+\\-+)|(\\w+\\.))*\\w{1,63}\\.[a-zA-Z]{2,6}");

	/* pattern per la verifica del CAP */
	private static final Pattern capRegExCheck = Pattern.compile("\\d{5}");

	/* pattern per la verifica del codice fiscale */
	private static final Pattern codFiscRegExCheck = Pattern
			.compile("([A-Z]{6}[\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{3}[A-Z])|(\\d{11})");

	private SourceBean partecipante = null;

	private SourceBean cfAccorpatoCambio = null;

	private SourceBean cfAccorpatiAcc = null;

	public InvioSiferXmlBuilder setPartecipante(SourceBean partecipante) {
		if (logger.isDebugEnabled()) {
			logger.debug("setPartecipante(SourceBean) - start");
		}

		this.partecipante = partecipante;

		if (logger.isDebugEnabled()) {
			logger.debug("setPartecipante(SourceBean) - end");
		}
		return this;
	}

	public InvioSiferXmlBuilder setCfAccorpatoCambio(SourceBean _cfAccorpatoCambio) {
		if (logger.isDebugEnabled()) {
			logger.debug("setCfAccorpatoCambio(SourceBean) - start");
		}

		this.cfAccorpatoCambio = _cfAccorpatoCambio;

		if (logger.isDebugEnabled()) {
			logger.debug("setCfAccorpatoCambio(SourceBean) - end");
		}
		return this;
	}

	public InvioSiferXmlBuilder setCfAccorpatiAcc(SourceBean _cfAccorpatiAcc) {
		if (logger.isDebugEnabled()) {
			logger.debug("setCfAccorpatiAcc(SourceBean) - start");
		}

		this.cfAccorpatiAcc = _cfAccorpatiAcc;

		if (logger.isDebugEnabled()) {
			logger.debug("setCfAccorpatiAcc(SourceBean) - end");
		}
		return this;
	}

	private Vector<SourceBean> accordi = null, aziende = null, operazioni = null, periodi = null;
	private Vector<SourceBean> servizi = null;

	private Vector<SourceBean> propostaCatalogo;

	private HashMap<BigDecimal, List<SourceBean>> prgAccordo2PropostaCatalogo;

	public InvioSiferXmlBuilder setPeriodi(Vector<SourceBean> periodi) {
		if (logger.isDebugEnabled()) {
			logger.debug("setPeriodi(Vector<SourceBean>) - start");
		}

		this.periodi = periodi;
		prgAltraIsrc2Periodi = new HashMap<BigDecimal, List<SourceBean>>();
		for (int i = 0; i < periodi.size(); i++) {
			SourceBean rowAttribute = periodi.get(i);
			BigDecimal key = BeanUtils.getBigDecimal((SourceBean) rowAttribute, PRG_ALTRA_ISCR, BigDecimal.ZERO);
			List<SourceBean> listPeriodi = (List<SourceBean>) prgAltraIsrc2Periodi.get(key);
			if (listPeriodi == null) {
				listPeriodi = new ArrayList<SourceBean>();
			}
			listPeriodi.add(rowAttribute);
			prgAltraIsrc2Periodi.put(key, listPeriodi);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("setPeriodi(Vector<SourceBean>) - end");
		}
		return this;
	}

	public InvioSiferXmlBuilder setServizi(Vector<SourceBean> _servizi) {
		if (logger.isDebugEnabled()) {
			logger.debug("setServizi(Vector<SourceBean>) - start");
		}

		this.servizi = _servizi;
		prgAltraIsrc2Servizi = new HashMap<BigDecimal, List<SourceBean>>();
		for (int i = 0; i < _servizi.size(); i++) {
			SourceBean rowAttribute = _servizi.get(i);
			BigDecimal key = BeanUtils.getBigDecimal((SourceBean) rowAttribute, PRG_ALTRA_ISCR, BigDecimal.ZERO);
			List<SourceBean> listServizi = (List<SourceBean>) prgAltraIsrc2Servizi.get(key);
			if (listServizi == null) {
				listServizi = new ArrayList<SourceBean>();
			}
			listServizi.add(rowAttribute);
			prgAltraIsrc2Servizi.put(key, listServizi);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("setServizi(Vector<SourceBean>) - end");
		}
		return this;
	}

	public InvioSiferXmlBuilder setAccordi(Vector<SourceBean> accordi) {
		if (logger.isDebugEnabled()) {
			logger.debug("setAccordi(Vector<SourceBean>) - start");
		}

		this.accordi = accordi;

		if (logger.isDebugEnabled()) {
			logger.debug("setAccordi(Vector<SourceBean>) - end");
		}
		return this;
	}

	public InvioSiferXmlBuilder setAziende(Vector<SourceBean> aziende) {
		if (logger.isDebugEnabled()) {
			logger.debug("setAziende(Vector<SourceBean>) - start");
		}

		this.aziende = aziende;
		prgAltraIscr2Azienda = new HashMap<BigDecimal, SourceBean>();
		for (int i = 0; i < aziende.size(); i++) {
			SourceBean rowAttribute = aziende.get(i);
			BigDecimal key = BeanUtils.getBigDecimal((SourceBean) rowAttribute, PRG_ALTRA_ISCR, BigDecimal.ZERO);
			prgAltraIscr2Azienda.put(key, rowAttribute);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("setAziende(Vector<SourceBean>) - end");
		}
		return this;
	}

	public InvioSiferXmlBuilder setOperazioni(Vector<SourceBean> _operazioni) {
		if (logger.isDebugEnabled()) {
			logger.debug("setOperazioni(Vector<SourceBean>) - start");
		}

		// filtro i corsi by id - rif_pa_operazione
		Map<String, List<SourceBean>> rifPa2Operazioni = new HashMap<String, List<SourceBean>>();
		for (int i = 0; i < _operazioni.size(); i++) {
			SourceBean rowAttribute = _operazioni.get(i);
			String rif_pa = BeanUtils.getObjectToString((SourceBean) rowAttribute, RIF_PA_OPERAZIONE, "");
			String ente_sede__id = BeanUtils.getObjectToString((SourceBean) rowAttribute, ENTE_SEDE_ID, "");
			BigDecimal prg_altra_iscr = BeanUtils.getBigDecimal((SourceBean) rowAttribute, PRG_ALTRA_ISCR,
					BigDecimal.ZERO);
			// String key = prg_altra_iscr.toString() + "-" + rif_pa;
			String key = prg_altra_iscr.toString() + "-" + rif_pa + "/" + ente_sede__id;
			List<SourceBean> listOperazioni = (List<SourceBean>) rifPa2Operazioni.get(key);
			if (listOperazioni == null) {
				listOperazioni = new ArrayList<SourceBean>();
			}
			listOperazioni.add(rowAttribute);
			rifPa2Operazioni.put(key, listOperazioni);
		}
		// TODO - per ogni id con più record devo stabilire quale inviare
		// Conto quanti son inseriti - quanti son cancellati > 0
		this.operazioni = new Vector<SourceBean>();
		for (List<SourceBean> listSB : rifPa2Operazioni.values()) {
			// x ogni codrifPa ho una lista
			// almeno 1 ci deve essere!
			if (listSB.size() == 1) {
				// ho un solo record x il corso, lo metto in lista
				operazioni.add(listSB.get(0));
				continue;
			}
			// ho più recordo x lo stesso corso
			if (listSB.size() > 1) {
				int count_ins = 0, count_del = 0;
				SourceBean sbIns = null;
				SourceBean sbDel = null;
				for (SourceBean sourceBean : listSB) {
					final Object annOp = sourceBean.getAttribute(ANNULLAMENTO_OPERAZIONE);
					// conto quanti record son inseriti e quanti annullati
					if ("N".equals(annOp)) {
						count_ins++;
						sbIns = sourceBean;
					} else {
						count_del++;
						sbDel = sourceBean;
					}
				}
				// Se gli inserimenti son positivi - è un inserimento!
				if (count_ins > 0) {
					// && count_del >0
					// if (count_ins > count_del) {
					operazioni.add(sbIns);
				} else {
					operazioni.add(sbDel);
				}
			}
		}

		prgAccordo2Operazioni = new HashMap<BigDecimal, List<SourceBean>>();
		for (int i = 0; i < operazioni.size(); i++) {
			SourceBean rowAttribute = operazioni.get(i);
			BigDecimal key = BeanUtils.getBigDecimal((SourceBean) rowAttribute, PRG_ALTRA_ISCR, BigDecimal.ZERO);
			List<SourceBean> listOperazioni = (List<SourceBean>) prgAccordo2Operazioni.get(key);
			if (listOperazioni == null) {
				listOperazioni = new ArrayList<SourceBean>();
			}
			listOperazioni.add(rowAttribute);
			prgAccordo2Operazioni.put(key, listOperazioni);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("setOperazioni(Vector<SourceBean>) - end");
		}
		return this;

	}

	public InvioSiferXmlBuilder setPropostaCatalogo(Vector<SourceBean> _propostaCatalogo) {
		if (logger.isDebugEnabled()) {
			logger.debug("setPropostaCatalogo(Vector<SourceBean>) - start");
		}

		// filtro i corsi by id - id_proposta_catalogo
		Map<String, List<SourceBean>> id2propostaCatalogo = new HashMap<String, List<SourceBean>>();
		for (int i = 0; i < _propostaCatalogo.size(); i++) {
			SourceBean rowAttribute = _propostaCatalogo.get(i);
			String idCat = BeanUtils.getObjectToString((SourceBean) rowAttribute, ID_PROPOSTA_CATALOGO, "");
			String idSed = BeanUtils.getObjectToString((SourceBean) rowAttribute, ENTE_SEDE_ID, "");
			BigDecimal prg_altra_iscr = BeanUtils.getBigDecimal((SourceBean) rowAttribute, PRG_ALTRA_ISCR,
					BigDecimal.ZERO);
			String key = prg_altra_iscr.toString() + "-" + idCat + "-" + idSed;
			List<SourceBean> listOperazioni = (List<SourceBean>) id2propostaCatalogo.get(key);
			if (listOperazioni == null) {
				listOperazioni = new ArrayList<SourceBean>();
			}
			listOperazioni.add(rowAttribute);
			id2propostaCatalogo.put(key, listOperazioni);
		}
		// TODO - per ogni id con più record devo stabilire quale inviare
		// Conto quanti son inseriti - quanti son cancellati > 0
		this.propostaCatalogo = new Vector<SourceBean>();
		for (List<SourceBean> listSB : id2propostaCatalogo.values()) {
			// x ogni codrifPa ho una lista
			// almeno 1 ci deve essere!
			if (listSB.size() == 1) {
				// ho un solo record x il corso, lo metto in lista
				propostaCatalogo.add(listSB.get(0));
				continue;
			}
			// ho più recordo x lo stesso corso
			Object annOp;
			Object italianoPerStranieri;
			boolean isItaPerStranieriIns = false;
			boolean isItaPerStranieriDel = false;
			if (listSB.size() > 1) {
				int count_ins = 0, count_del = 0;
				SourceBean sbIns = null;
				SourceBean sbDel = null;
				for (SourceBean sourceBean : listSB) {
					annOp = sourceBean.getAttribute(ANNULLAMENTO_PROPOSTA_CATALOGO);
					italianoPerStranieri = sourceBean.getAttribute(ITALIANO_PER_STRANIERI);
					// conto quanti record son inseriti e quanti annullati
					if ("N".equals(annOp)) {
						count_ins++;
						isItaPerStranieriIns = isItaPerStranieriIns || "S".equals(italianoPerStranieri);
						sbIns = sourceBean;
					} else if ("S".equals(annOp)) {
						count_del++;
						isItaPerStranieriDel = isItaPerStranieriDel || "S".equals(italianoPerStranieri);
						sbDel = sourceBean;
					} else {
						throw new RuntimeException(
								ANNULLAMENTO_PROPOSTA_CATALOGO + " Atteso (S|N) invece vale:" + annOp);
					}
				}
				// Se gli inserimenti son positivi - è un inserimento!
				if (count_ins > 0) {
					if (isItaPerStranieriIns) {
						try {
							sbIns.delAttribute(ITALIANO_PER_STRANIERI);
							sbIns.setAttribute(ITALIANO_PER_STRANIERI, "S");
						} catch (SourceBeanException e) {
						}
					}
					propostaCatalogo.add(sbIns);
				} else {
					if (isItaPerStranieriDel) {
						try {
							sbDel.delAttribute(ITALIANO_PER_STRANIERI);
							sbDel.setAttribute(ITALIANO_PER_STRANIERI, "S");
						} catch (SourceBeanException e) {
						}
					}
					propostaCatalogo.add(sbDel);
				}
			}
		}

		prgAccordo2PropostaCatalogo = new HashMap<BigDecimal, List<SourceBean>>();
		for (int i = 0; i < propostaCatalogo.size(); i++) {
			SourceBean rowAttribute = propostaCatalogo.get(i);
			BigDecimal key = BeanUtils.getBigDecimal((SourceBean) rowAttribute, PRG_ALTRA_ISCR, BigDecimal.ZERO);
			List<SourceBean> listOperazioni = (List<SourceBean>) prgAccordo2PropostaCatalogo.get(key);
			if (listOperazioni == null) {
				listOperazioni = new ArrayList<SourceBean>();
			}
			listOperazioni.add(rowAttribute);
			prgAccordo2PropostaCatalogo.put(key, listOperazioni);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("setPropostaCatalogo(Vector<SourceBean>) - end");
		}
		return this;

	}

	/**
	 * Costruisce l'xml dei lavoratori da inviare al sifer.
	 * 
	 * @return l'xml da inviare
	 * @throws ParseException
	 * @see it.eng.sil.coop.webservices.corsoCIG.InvioWsSifer
	 */
	public String buildXml() throws ParseException {
		if (logger.isDebugEnabled()) {
			logger.debug("buildXml() - start");
		}

		final SourceBean rowPartecipante = (SourceBean) partecipante.getAttribute(ROW);
		BeanUtils buPartecipante = new BeanUtils(rowPartecipante);
		String bustaPartecipante = getXmlPartecipante(buPartecipante);
		BeanUtils buAccordo;
		StringBuilder sbAllAccordi = new StringBuilder();
		// BeanUtils buAccordo = new BeanUtils(accordo);
		for (SourceBean accordoCurrent : accordi) {
			buAccordo = new BeanUtils(accordoCurrent);
			sbAllAccordi.append(getXmlAccordo(buAccordo));

		} // for (SourceBean accordoCurrent : accordi)

		StringBuilder domCigSb = new StringBuilder();
		domCigSb.append(
				"<tns:PartecipanteCrisi xsi:schemaLocation=\"https://sifer.regione.emilia-romagna.it/WebService/Crisi/PartecipanteCrisi PartecipanteCrisi.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tns=\"https://sifer.regione.emilia-romagna.it/WebService/Crisi/PartecipanteCrisi\">");
		domCigSb.append(bustaPartecipante);
		domCigSb.append(sbAllAccordi.toString());
		domCigSb.append("</tns:PartecipanteCrisi>");
		String returnString = domCigSb.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("buildXml() - end");
		}
		return returnString;
	}

	/**
	 * Costruisce l'xml relativo ad un partecipante.
	 * 
	 * @param bu
	 * @return
	 * @throws ParseException
	 */
	private String getXmlPartecipante(BeanUtils bu) throws ParseException {
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlPartecipante(BeanUtils) - start");
		}

		StringBuilder s = new StringBuilder();
		s.append("<tns:partecipante>");
		bu.append(s, CODICE_FISCALE);
		final SourceBean rowAccorpatoCambio = (SourceBean) cfAccorpatoCambio.getAttribute(ROW);

		// 20-10-2010 Rodi - eliminato campo codice_fiscale_vecchio
		// BeanUtils buAccorpamento = new BeanUtils(rowAccorpatoCambio);
		// String codFiscVecchio = buAccorpamento.getObjectToString4Html("codice_fiscale_vecchio");
		// Matcher m1 = codFiscRegExCheck.matcher(codFiscVecchio);
		// if (!m1.matches()) {
		// codFiscVecchio = null;
		// }
		// buAccorpamento.appendOrNil(s, "codice_fiscale_vecchio", codFiscVecchio);

		/*
		 * scorri tutti i record estratti dalla tabella AN_LAVORATORE_ACCORPA per generare gli elementi
		 * <tns:accorpamento>
		 */
		Vector<SourceBean> rows = cfAccorpatiAcc.getAttributeAsVector("ROW");
		if (rows != null && !rows.isEmpty()) {
			for (SourceBean row : rows) {
				BeanUtils buAcc = new BeanUtils(row);
				String codFiscAcc = buAcc.getObjectToString4Html(CODICE_FISCALE_ACCORPATO);
				String dataAccorpamento = buAcc.getObjectToString4Html(DATA_ACCORPAMENTO);
				/* verifico che il codice fiscale accorpato sia valido. Non posso inserire codici fiscali non validi. */
				Matcher m2 = codFiscRegExCheck.matcher(codFiscAcc);
				if (m2.matches()) {
					s.append("<tns:accorpamento>");
					buAcc.appendValue(s, CODICE_FISCALE_ACCORPATO, codFiscAcc);
					buAcc.appendValue(s, DATA_ACCORPAMENTO, dataAccorpamento);
					s.append("</tns:accorpamento>");
				}
			}
		}
		bu.appendValueTrim(s, COGNOME, bu.getObjectToString4Html(COGNOME));
		bu.appendValueTrim(s, NOME, bu.getObjectToString4Html(NOME));
		bu.append(s, SESSO);
		bu.appendDate(s, NASCITA_DATA);
		bu.append(s, NASCITA_CODICE_CATASTALE);
		String cittadinanza = bu.getObjectToString4Html(CITTADINANZA);
		for (String citt : CITTADINANZA_BLACK_LIST) {
			if (citt.equals(cittadinanza)) {
				cittadinanza = null;
				break;
			}
		}
		bu.appendValueTrimOrNil(s, CITTADINANZA, cittadinanza);
		String cittadinanza_seconda = bu.getObjectToString4Html(CITTADINANZA_SECONDA);
		for (String citt : CITTADINANZA_BLACK_LIST) {
			if (citt.equals(cittadinanza_seconda)) {
				cittadinanza_seconda = null;
				break;
			}
		}
		bu.appendValueTrimOrNil(s, CITTADINANZA_SECONDA, cittadinanza_seconda);
		bu.appendValueTrimOrNil(s, TELEFONO_CELLULARE, bu.getObjectToString4Html(TELEFONO_CELLULARE));
		// bu.appendOrNil(s, "telefono_cellulare");
		String email = bu.getStringNotNull(EMAIL);
		// (([A-Za-z0-9]+_+)|([A-Za-z0-9]+\-+)|([A-Za-z0-9]+\.+)|([A-Za-z0-9]+\++))*[A-Za-z0-9]+@((\w+\-+)|(\w+\.))*\w{1,63}\.[a-zA-Z]{2,6}
		// /schema/simpleType[@name='email_type']/restriction/pattern
		// /schema/*[@name='email_type']/restriction/pattern
		// /schema/simpleType[@name='email_type']/restriction/pattern/@value
		String emailChecked;
		if (StringUtils.isEmptyNoBlank(email)) {
			// email = "nessuna@email.it";
			emailChecked = null;
		} else {
			Matcher m = emailRegExCheck.matcher(email);
			boolean matches = m.matches();
			if (matches) {
				// ok - la posso inviare
				emailChecked = email;
			} else {
				// KO - NON la Invio!
				logger.error("Lavoratore cf:" + bu.getObjectToString4Html(CODICE_FISCALE) + ", ha una email non valida:"
						+ email);
				emailChecked = null;
			}
		}
		bu.appendValueOrNil(s, EMAIL, emailChecked);
		bu.append(s, "residenza__codice_catastale");
		// default se non c'è-> sconosciuto
		String residenza__indirizzo = bu.getObjectToString4Html("residenza__indirizzo");
		// non può essere nullo - controllo con lenght
		residenza__indirizzo = (residenza__indirizzo.length() < 3) ? NON_DISPONIBILE : residenza__indirizzo;
		bu.appendValueTrimOrNil(s, "residenza__indirizzo", residenza__indirizzo);
		// bu.appendOrNil(s, "residenza__indirizzo");
		String residenza__cap = bu.getStringNotNull("residenza__cap");

		Matcher m = capRegExCheck.matcher(residenza__cap);
		if (!m.matches()) {
			residenza__cap = "00000";
		}
		bu.appendValueOrNil(s, "residenza__cap", residenza__cap);
		// String residenza__telefono =
		// bu.getStringNotNull("residenza__telefono");
		// if (StringUtils.isEmptyNoBlank(residenza__telefono)) {
		// residenza__telefono = "051505050";
		// }
		bu.appendOrNil(s, "residenza__telefono");
		bu.appendOrNil(s, "domicilio__codice_catastale");
		String domicilio__indirizzo = bu.getObjectToString4Html("domicilio__indirizzo");
		// non può essere nullo - controllo con lenght
		domicilio__indirizzo = (domicilio__indirizzo.length() < 3) ? NON_DISPONIBILE : domicilio__indirizzo;
		bu.appendValueTrimOrNil(s, "domicilio__indirizzo", domicilio__indirizzo);
		// bu.appendOrNil(s, "domicilio__indirizzo");
		String domicilio__cap = bu.getObjectToString4Html("domicilio__cap");
		m = capRegExCheck.matcher(domicilio__cap);
		if (!m.matches()) {
			domicilio__cap = "00000";
		}
		bu.appendValueOrNil(s, "domicilio__cap", domicilio__cap);
		bu.appendValueTrimOrNil(s, "domicilio__telefono", bu.getObjectToString4Html("domicilio__telefono"));
		// bu.appendOrNil(s, "domicilio__telefono");

		String titolo_studio = bu.getStringNotNull("titolo_studio");
		if (StringUtils.isEmptyNoBlank(titolo_studio)) {
			titolo_studio = "00000000";
		}
		bu.appendValueOrNil(s, "titolo_studio", titolo_studio);
		s.append("</tns:partecipante>");
		String returnString = s.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlPartecipante(BeanUtils) - end");
		}
		return returnString;
	}

	public static InvioSiferXmlBuilder getInstance(String codiceFiscale, QueryExecutorObject qExec) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("getInstance(String, QueryExecutorObject) - start");
		}

		List<DataField> inputParameters = new ArrayList<DataField>();
		DataConnection dc = qExec.getDataConnection();

		// ------------------- _partecipante
		inputParameters.add(dc.createDataField("codiceFiscale", Types.VARCHAR, codiceFiscale));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_CDN_LAVORATORE);

		Object returned = qExec.exec();
		BigDecimal cndLavavoratore = null;
		// SourceBean _partecipante = null;
		if (returned instanceof SourceBean) {
			SourceBean cdnLav = (SourceBean) returned;
			final SourceBean attribute = (SourceBean) cdnLav.getAttribute(ROW);
			cndLavavoratore = BeanUtils.getBigDecimal(attribute, CDNLAVORATORE, BigDecimal.ZERO);
		}
		if (BigDecimal.ZERO.equals(cndLavavoratore)) {
			throw new Exception("Non trovato lavoratore con codiceFiscale:" + codiceFiscale);
		}
		InvioSiferXmlBuilder returnInvioSiferXmlBuilder = getInstance(cndLavavoratore, qExec);
		if (logger.isDebugEnabled()) {
			logger.debug("getInstance(String, QueryExecutorObject) - end");
		}
		return returnInvioSiferXmlBuilder;

	}

	public static InvioSiferXmlBuilder getInstance(BigDecimal cndLavoratore, QueryExecutorObject qExec)
			throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("getInstance(BigDecimal, QueryExecutorObject) - start");
		}

		List<DataField> inputParameters = new ArrayList<DataField>();
		DataConnection dc = qExec.getDataConnection();
		Object returned;
		// ------------------- _partecipante
		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavoratore", Types.NUMERIC, cndLavoratore));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_LAV_PARTECIPANTE_BY_CDNLAVORATORE);

		returned = qExec.exec();
		SourceBean _partecipante = null;
		if (returned instanceof SourceBean) {
			_partecipante = (SourceBean) returned;
		} else {
			throw new Exception("Non trovato lavoratore by cndLavoratore:" + cndLavoratore);
		}
		// ------------------- _accorpamento
		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavoratore", Types.NUMERIC, cndLavoratore));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_CF_ACCORPATO_X_CAMBIO);
		returned = qExec.exec();
		SourceBean _cfAccorpatoCambio = null;
		if (returned instanceof SourceBean) {
			_cfAccorpatoCambio = (SourceBean) returned;
		} else {
			throw new Exception(
					"Nessun lavoratore accorpato per cambio di codice fiscale by cndLavoratore:" + cndLavoratore);
		}

		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavoratore", Types.NUMERIC, cndLavoratore));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_CF_ACCORPATI_X_ACCORPAMENTO);
		returned = qExec.exec();
		SourceBean _cfAccorpatiAcc = null;
		if (returned instanceof SourceBean) {
			_cfAccorpatiAcc = (SourceBean) returned;
		} else {
			throw new Exception("Nessun lavoratore accorpato per accorpamento by cndLavoratore:" + cndLavoratore);
		}

		// ------------------- _info_azienda
		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavavoratore", Types.NUMERIC, cndLavoratore));
		// inputParameters.add(dc.createDataField("prgAltraIscr", Types.NUMERIC,
		// prgAltraIscr));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_INFO_AZIENDA_BY_CDNLAVORATORE);
		returned = qExec.exec();
		SourceBean _info_azienda = null;
		if (returned instanceof SourceBean) {
			_info_azienda = (SourceBean) returned;
		} else {
			throw new Exception("Non trovata azienda del lavoratore by cndLavoratore:" + cndLavoratore);
		}

		// ------------------- _info_accordo
		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavavoratore", Types.NUMERIC, cndLavoratore));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_INFO_ACCORDO_BY_CDNLAVORATORE);
		returned = qExec.exec();
		SourceBean _info_accordo = null;
		if (returned instanceof SourceBean) {
			_info_accordo = (SourceBean) returned;
		} else {
			throw new Exception("Non trovati accordi del lavoratore by cndLavoratore:" + cndLavoratore);
		}

		// ------------------- _info_operazione
		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavavoratore", Types.NUMERIC, cndLavoratore));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_INFO_OPERAZIONE_BY_CDNLAVORATORE);
		returned = qExec.exec();
		SourceBean _info_operazione = null;
		if (returned instanceof SourceBean) {
			_info_operazione = (SourceBean) returned;
		}

		// ------------------- _proposta_catalogo
		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavavoratore", Types.NUMERIC, cndLavoratore));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_INFO_PROPOSTA_CATALOGO_BY_CDNLAVORATORE);
		returned = qExec.exec();
		SourceBean _proposta_catalogo = null;
		if (returned instanceof SourceBean) {
			_proposta_catalogo = (SourceBean) returned;
		}

		// ------------------- _list_periodi
		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavavoratore", Types.NUMERIC, cndLavoratore));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_LIST_PERIODI_BY_CDNLAVORATORE);
		returned = qExec.exec();
		SourceBean _list_periodi = null;
		if (returned instanceof SourceBean) {
			_list_periodi = (SourceBean) returned;
		}

		// ------------------- _list_servizi
		inputParameters.clear();
		inputParameters.add(dc.createDataField("cndLavavoratore", Types.NUMERIC, cndLavoratore));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(InvioSiferDbQuery.GET_LIST_SERVIZI_BY_CDNLAVORATORE);
		returned = qExec.exec();
		SourceBean _list_servizi = null;
		if (returned instanceof SourceBean) {
			_list_servizi = (SourceBean) returned;
		}

		final String errorMsg = "Impossibile Recuperare i dati per Invio a Sifer";
		// if ((_partecipante == null) || (_info_accordo == null) ||
		// (_info_azienda == null) || (_info_operazione == null)) {
		// throw new Exception(errorMsg);
		// }

		InvioSiferXmlBuilder invioSiferXmlB = new InvioSiferXmlBuilder();

		Vector<SourceBean> vAziende = _info_azienda.getAttributeAsVector(ROW);
		if (vAziende == null || vAziende.isEmpty()) {
			throw new Exception(errorMsg);
		}
		Vector<SourceBean> vOperazioni = _info_operazione.getAttributeAsVector(ROW);
		Vector<SourceBean> vPropostaCatalogo = _proposta_catalogo.getAttributeAsVector(ROW);

		Vector<SourceBean> vAccordi = _info_accordo.getAttributeAsVector(ROW);
		if (vAccordi == null || vAccordi.isEmpty()) {
			// throw new Exception(errorMsg);
			if (vAccordi == null) {
				vAccordi = new Vector<SourceBean>();
			}
			// Se manca la presa in carico devo cmq spedire i corsi
			// recupero tutte le PRG_ALTRA_ISCR dai corsi
			Set<BigDecimal> prgAltraIscrSet = new HashSet<BigDecimal>();
			SourceBean sourceBeanDummy = null;
			BigDecimal prgAltraIscr;
			if (vOperazioni != null)
				for (SourceBean sb : vOperazioni) {
					prgAltraIscr = BeanUtils.getBigDecimal(sb, PRG_ALTRA_ISCR, BigDecimal.ZERO);
					prgAltraIscrSet.add(prgAltraIscr);
				}
			if (vPropostaCatalogo != null)
				for (SourceBean sb : vPropostaCatalogo) {
					prgAltraIscr = BeanUtils.getBigDecimal(sb, PRG_ALTRA_ISCR, BigDecimal.ZERO);
					prgAltraIscrSet.add(prgAltraIscr);

				}
			for (BigDecimal prgAltraIscrBD : prgAltraIscrSet) {
				sourceBeanDummy = new SourceBean("Dummy");
				sourceBeanDummy.setAttribute(PRG_ALTRA_ISCR, prgAltraIscrBD);
				vAccordi.add(sourceBeanDummy);
			}
		}
		Vector<SourceBean> vPeriodi = _list_periodi.getAttributeAsVector(ROW);
		Vector<SourceBean> vServizi = _list_servizi.getAttributeAsVector(ROW);
		invioSiferXmlB.setPartecipante(_partecipante).setAccordi(vAccordi).setAziende(vAziende)
				.setOperazioni(vOperazioni).setPeriodi(vPeriodi).setServizi(vServizi)
				.setPropostaCatalogo(vPropostaCatalogo).setCfAccorpatoCambio(_cfAccorpatoCambio)
				.setCfAccorpatiAcc(_cfAccorpatiAcc);

		if (logger.isDebugEnabled()) {
			logger.debug("getInstance(BigDecimal, QueryExecutorObject) - end");
		}
		return invioSiferXmlB;
	}

	private static String getXmlAzienda(BeanUtils bu) throws ParseException {
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlAzienda(BeanUtils) - start");
		}

		StringBuilder s = new StringBuilder();

		bu.appendOrNil(s, "azienda__codice_fiscale");
		bu.appendValueTrimOrNil(s, "azienda__ragione_sociale", bu.getObjectToString4Html("azienda__ragione_sociale"));
		bu.appendOrNil(s, "azienda__codice_catastale");
		String azienda__indirizzo = bu.getObjectToString4Html("azienda__indirizzo");
		// non può essere nullo - controllo con lenght
		azienda__indirizzo = (azienda__indirizzo.length() < 3) ? NON_DISPONIBILE : azienda__indirizzo;
		bu.appendValueTrimOrNil(s, "azienda__indirizzo", azienda__indirizzo);
		String azienda__cap = bu.getObjectToString4Html("azienda__cap");
		Matcher m = capRegExCheck.matcher(azienda__cap);
		if (!m.matches()) {
			azienda__cap = "00000";
		}
		bu.appendValueOrNil(s, "azienda__cap", azienda__cap);
		String tipo_contratto = bu.getObjectToString4Html("tipo_contratto");
		if ("AUT".equalsIgnoreCase(tipo_contratto)) {
			tipo_contratto = null;
		}
		bu.appendValueOrNil(s, "tipo_contratto", tipo_contratto);
		String cassa_integrazione__tipoRaw = bu.getStringNotNull("cassa_integrazione__tipo");
		// String cassa_integrazione__tipo;
		// if ("O".equalsIgnoreCase(cassa_integrazione__tipoRaw)) {
		// cassa_integrazione__tipo = "CIG0";
		// } else {
		// cassa_integrazione__tipo = "CIGS";
		// }
		bu.appendValue(s, "cassa_integrazione__tipo", cassa_integrazione__tipoRaw);
		bu.appendDateOrNil(s, "cassa_integrazione__data_inizio", "cassa_integr__data_inizio");
		bu.appendDateOrNil(s, "cassa_integrazione__data_fine");
		String returnString = s.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlAzienda(BeanUtils) - end");
		}
		return returnString;
	}

	/**
	 * Costruisce la parte di xml da inviare al Sifer contenente l'accordo relativo ad un lavoratore
	 * 
	 * @param bu
	 * @return
	 * @throws ParseException
	 */
	private String getXmlAccordo(BeanUtils bu) throws ParseException {
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlAccordo(BeanUtils) - start");
		}

		StringBuilder s = new StringBuilder();
		BigDecimal prgAltraIscr = bu.getBigDecimal0(PRG_ALTRA_ISCR);
		if (BigDecimal.ZERO.equals(prgAltraIscr)) {
			// TODO devo recuperare in qlc modo il prg
		}
		SourceBean sbAz = prgAltraIscr2Azienda.get(prgAltraIscr);
		BeanUtils buAz = new BeanUtils(sbAz);
		s.append("<tns:accordo>");
		s.append(getXmlAzienda(buAz));
		// bu.append(s, "cassa_integrazione__periodi");
		s.append(getXmlPeriodi(prgAltraIscr));
		bu.appendDateOrNil(s, "presa_in_carico__data");
		bu.appendOrNil(s, "presa_in_carico__cpi_sportello");
		bu.appendDateOrNil(s, "presa_in_carico__fine");
		s.append(getXmlServizi(prgAltraIscr));
		/*
		 * bu.appendOrNil(s, "politica_attiva__ore_standard"); bu.appendOrNil(s, "politica_attiva__ore_mediatore");
		 * bu.appendOrNil(s, "politica_attiva__ore_disabile");
		 */
		bu.appendDateOrNil(s, "politica_attiva__data_ritiro");
		bu.appendDateOrNil(s, "data_decadenza");
		bu.appendDateOrNil(s, "data_rientro_lavoro");
		String accordo_crisi__codice = bu.getStringNotNull("accordo_crisi__codice");
		if (StringUtils.isEmptyNoBlank(accordo_crisi__codice)) {
			BigDecimal accordo_crisi__codiceBD = bu.getBigDecimal0(PRG_ALTRA_ISCR);
			bu.appendValue(s, "accordo_crisi__codice", accordo_crisi__codiceBD.toString());
			bu.appendOrNil(s, "accordo_crisi__codice_precedente", "accordo_crisi__codice_prec");
		} else {
			bu.append(s, "accordo_crisi__codice");

			// siccome abbiamo l'accordo crisi codice,
			// l'accordo crisi precedente deve essere sempre presente
			// se non c'è, mettiamo PRGALTRAISCR
			String accordo_crisi__codice_prec = bu.getStringNotNull("accordo_crisi__codice_prec");
			if (StringUtils.isEmptyNoBlank(accordo_crisi__codice_prec)) {
				accordo_crisi__codice_prec = bu.getBigDecimal0(PRG_ALTRA_ISCR).toString();
			}

			bu.appendValue(s, "accordo_crisi__codice_precedente", accordo_crisi__codice_prec);
		}
		bu.appendOrNil(s, "accordo_crisi__codice_temporaneo", "accordo_crisi__codice_temp");
		bu.appendDateOrNil(s, "accordo_crisi__data_cessazione");
		bu.appendOrNil(s, "accordo_crisi__motivo_cessazione", "accordo_crisi__motivo_cess");
		bu.appendDateOrNil(s, "accordo_crisi__data_autorizzazione");
		bu.appendOrNil(s, "accordo_crisi__autorizzato");
		bu.appendOrNil(s, "linea_azione");
		List<SourceBean> listOper = prgAccordo2Operazioni.get(prgAltraIscr);
		if (listOper != null)
			for (SourceBean operazione : listOper) {
				BeanUtils buOp = new BeanUtils(operazione);
				s.append(getXmlOperazione(buOp));
			}
		List<SourceBean> listPropCatalogo = prgAccordo2PropostaCatalogo.get(prgAltraIscr);
		/* scorri le proposte catalogo ed inseriscile all'interno dell'xml */
		if (listPropCatalogo != null)
			for (SourceBean propostaCatalogo : listPropCatalogo) {
				BeanUtils buPc = new BeanUtils(propostaCatalogo);
				s.append(getXmlPropostaCatalogo(buPc));
			}
		bu.appendOrNil(s, "annullamento_accordo");
		s.append("</tns:accordo>");
		String returnString = s.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlAccordo(BeanUtils) - end");
		}
		return returnString;
	}

	/**
	 * Costruisce la parte di xml da inviare al Sifer contenente la proposta catalogo da inserire nell'accordo
	 * 
	 * @param bu
	 * @return
	 * @throws ParseException
	 */
	private String getXmlPropostaCatalogo(BeanUtils bu) {
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlPropostaCatalogo(BeanUtils) - start");
		}

		StringBuilder s = new StringBuilder();
		s.append("<tns:proposta_catalogo>");
		bu.append(s, "id_proposta_catalogo");
		bu.appendOrNil(s, ENTE_SEDE_ID);
		bu.appendOrNil(s, ITALIANO_PER_STRANIERI);
		bu.appendOrNil(s, ANNULLAMENTO_PROPOSTA_CATALOGO);
		s.append("</tns:proposta_catalogo>");
		String returnString = s.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlPropostaCatalogo(BeanUtils) - end");
		}
		return returnString;
	}

	/**
	 * Costruisce la parte di xml relativa al servizio.
	 * 
	 * @param prgAltraIscr
	 * @return
	 * @throws ParseException
	 */
	private String getXmlServizi(BigDecimal prgAltraIscr) throws ParseException {
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlServizi(BigDecimal) - start");
		}

		List<SourceBean> listServizi = prgAltraIsrc2Servizi.get(prgAltraIscr);
		if (listServizi == null || listServizi.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("getXmlServizi(BigDecimal) - end");
			}
			return "";
		}
		StringBuilder s = new StringBuilder();
		Iterator<SourceBean> i = listServizi.iterator();
		while (i.hasNext()) {
			s.append("<tns:servizio>");
			SourceBean periodo = (SourceBean) i.next();
			BeanUtils bu = new BeanUtils(periodo);
			bu.append(s, "tipo");
			bu.append(s, "durata");
			bu.appendOrNil(s, "mediatore");
			bu.appendOrNil(s, "disabile");
			bu.appendOrNil(s, "esito");
			bu.appendDate(s, "data");
			s.append("</tns:servizio>");
		}
		String returnString = s.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlServizi(BigDecimal) - end");
		}
		return returnString;
	}

	private String getXmlPeriodi(BigDecimal prgAltraIscr) {
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlPeriodi(BigDecimal) - start");
		}

		List<SourceBean> listPeriodi = prgAltraIsrc2Periodi.get(prgAltraIscr);
		if (listPeriodi == null || listPeriodi.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("getXmlPeriodi(BigDecimal) - end");
			}
			return "<tns:cassa_integrazione__periodi xsi:nil=\"true\"/>";
		}
		StringBuilder s = new StringBuilder();
		s.append("<tns:cassa_integrazione__periodi>");
		Iterator<SourceBean> i = listPeriodi.iterator();
		while (i.hasNext()) {
			SourceBean periodo = (SourceBean) i.next();
			BeanUtils bu = new BeanUtils(periodo);
			s.append("DAL:");
			s.append(bu.getStringNotNull4Html("datinizio"));
			s.append(" AL:");
			s.append(bu.getStringNotNull4Html("datfine"));
			BigDecimal numoreptsett = bu.getBigDecimal0("numoreptsett");
			BigDecimal numoreftsett = bu.getBigDecimal0("numoreftsett");
			BigDecimal numtotggcigs = bu.getBigDecimal0("numtotggcigs");
			BigDecimal numtotorecigs = bu.getBigDecimal0("numtotorecigs");
			s.append(" ore Sett:");
			if (numoreptsett.intValue() > 0) {
				s.append(numoreptsett);
				s.append(" - Part Time");
			} else {
				s.append(numoreftsett);
				s.append(" - Full Time");
			}
			s.append(" Giorni tot:");
			s.append(numtotggcigs);
			s.append(" Ore tot:");
			s.append(numtotorecigs);
			if (i.hasNext()) {
				s.append(" *** ");
			}
		}
		s.append("</tns:cassa_integrazione__periodi>");
		String returnString = s.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlPeriodi(BigDecimal) - end");
		}
		return returnString;
	}

	private String getXmlOperazione(BeanUtils bu) {
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlOperazione(BeanUtils) - start");
		}

		StringBuilder s = new StringBuilder();
		s.append("<tns:operazione>");
		bu.appendTrim(s, RIF_PA_OPERAZIONE);
		bu.appendOrNil(s, ENTE_SEDE_ID);
		bu.appendOrNil(s, ANNULLAMENTO_OPERAZIONE);
		s.append("</tns:operazione>");
		String returnString = s.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("getXmlOperazione(BeanUtils) - end");
		}
		return returnString;
	}

}
