package it.eng.sil.module.consenso;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.adobe.idp.services.GestioneConsensi_InserimentoConsensoProxy;
import com.adobe.idp.services.GestioneConsensi_RevocaConsensoProxy;
import com.adobe.idp.services.GestioneConsensi_RicercaConsensoProxy;
import com.adobe.idp.services.XML;
import com.adobe.idp.services.holders.RicercaConsensoXmlOutputBean;
import com.adobe.idp.services.holders.XMLHolder;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSProxy;

public class Consenso {

	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Consenso.class.getName());

	private Map<String, Object> params;

	String DATE_FORMAT_NOW = "dd/MM/yyyy";
	String DATE_PARSE = "yyyy-MM-dd'T'HH:mm:sss";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	SimpleDateFormat sdfp = new SimpleDateFormat(DATE_PARSE);

	public Consenso(Map<String, Object> params) {
		super();

		this.params = params;
	}

	public SourceBean verificaConsenso() throws ServiceException, RemoteException {
		SourceBean resConsenso = null;

		_logger.debug("VERIFICA CONSENSO");

		// DATEHolder dataRaccolta = new DATEHolder();
		// DATEHolder dataRevoca = new DATEHolder();
		// BooleanHolder esitoConsenso = new BooleanHolder(false);
		XMLHolder outputXmlConsenso = new XMLHolder();

		String codiceFiscaleLavoratoreString = (String) params.get("codiceFiscaleLavoratore");

		try {
			GestioneConsensi_RicercaConsensoProxy proxy = new GestioneConsensi_RicercaConsensoProxy();
			proxy.setEndpoint(getConsensoWSUrl(GConstants.WS_NAME_URL_RICERCA_CONSENSO_LAVORATORE));
			XML xmlH = proxy.invoke(codiceFiscaleLavoratoreString);

			Date dateRaccolta = null;
			String stringDateRegistrazione = "";
			Date dateRevoca = null;
			String stringDateRevoca = "";

			RicercaConsensoXmlOutputBean bean = getRicercaConsensoXmlOutput(xmlH);

			if (bean.getDataRaccolta() != null) {
				stringDateRegistrazione = sdf.format(bean.getDataRaccolta());
			}

			/*
			 * else{ stringDateRegistrazione = sdf.format(new Date()); }
			 */

			if (bean.getDataRevoca() != null) {
				stringDateRevoca = sdf.format(bean.getDataRevoca());
			}

			String code = getCodeFromVerificaConsenso(bean);
			// String code = bean.getStato();

			params.put("strOrigine", GConstants.CONSENSO_SISTEMA_DI_ORIGINE);
			params.put("codStatoConsenso", code);
			params.put("datRegistrazione", stringDateRegistrazione);
			params.put("datRevoca", stringDateRevoca);

			if (_logger.isDebugEnabled()) {
				_logger.debug("xmlOutputConsenso: " + outputXmlConsenso.value);
				_logger.debug("xmlCodiceConsenso: " + bean.getCodiceOutput());
				_logger.debug("xmlDescrizioneConsenso: " + bean.getDescrizioneOutput());
				_logger.debug("xmlDataRaccolta: " + stringDateRegistrazione);
				_logger.debug("xmlDataRevoca: " + stringDateRevoca);
			}

			// Inserimento/Aggiornamento DB su Tabella AM_CONSENSO_FIRMA
			gestisciAmConsensoFirmaDB(params);

			resConsenso = new SourceBean("resConsenso");
			resConsenso.setAttribute("code", code);

			if (bean.getDataRaccolta() != null) {
				resConsenso.setAttribute("dateRec", bean.getDataRaccolta());
			}

			if (bean.getDataRevoca() != null) {
				resConsenso.setAttribute("dateRev", bean.getDataRevoca());
			}

		} catch (ServiceException e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e);
		} catch (IOException e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e);
		}

		return resConsenso;
	}

	public SourceBean inserisciConsenso() throws ServiceException, RemoteException, SourceBeanException {
		SourceBean resConsenso = null;
		XML outputXmlConsenso = new XML();

		_logger.debug("INSERISCI CONSENSO");

		String codiceFiscaleLavoratoreString = (String) params.get("codiceFiscaleLavoratore");
		String codiceOperatore = params.get("cdnUtins") + "";
		String dataAttualeOggi = (String) params.get("dataRaccoltaOggi");

		try {
			GestioneConsensi_InserimentoConsensoProxy proxy = new GestioneConsensi_InserimentoConsensoProxy();
			proxy.setEndpoint(getConsensoWSUrl(GConstants.WS_NAME_URL_INSERIMENTO_CONSENSO_LAVORATORE));
			outputXmlConsenso = proxy.invoke(codiceFiscaleLavoratoreString, codiceOperatore,
					GConstants.CONSENSO_SISTEMA_DI_ORIGINE);

			RicercaConsensoXmlOutputBean bean = getRicercaConsensoXmlOutput(outputXmlConsenso);

			// String dataAttuale = DateUtils.getNow();
			String dataAttuale = dataAttualeOggi;
			if (bean.getDataRaccolta() != null) {
				dataAttuale = sdf.format(bean.getDataRaccolta());
			} else {
				dataAttuale = sdf.format(new Date());
			}

			params.put("strOrigine", GConstants.CONSENSO_SISTEMA_DI_ORIGINE);
			params.put("codStatoConsenso", GConstants.CONSENSO_ATTIVO_CODICE);
			params.put("datRegistrazione", dataAttuale);
			params.put("datRevoca", null);

			// Inserimento/Aggiornamento DB su Tabella AM_CONSENSO_FIRMA
			gestisciAmConsensoFirmaDB(params);

			if (_logger.isDebugEnabled()) {
				_logger.debug("xmlOutputConsenso: " + outputXmlConsenso.getDocument());
				_logger.debug("xmlCodiceConsenso: " + bean.getCodiceOutput());
				_logger.debug("xmlDescrizioneConsenso: " + bean.getDescrizioneOutput());
				_logger.debug("dataRaccolta: " + dataAttuale);
			}

			resConsenso = new SourceBean("resConsenso");
			resConsenso.setAttribute("code", GConstants.CONSENSO_ATTIVO_CODICE);
			Date dateAttualeToday = sdf.parse(dataAttuale);
			resConsenso.setAttribute("dateRec", dateAttualeToday);
			resConsenso.setAttribute("dateRev", "");
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e);
		}

		return resConsenso;
	}

	public SourceBean revocaConsenso() throws ServiceException, RemoteException, SourceBeanException {
		SourceBean resConsenso = null;
		XML outputXmlConsenso = new XML();

		_logger.debug("REVOCA CONSENSO");

		String codiceFiscaleLavoratoreString = (String) params.get("codiceFiscaleLavoratore");

		try {
			GestioneConsensi_RevocaConsensoProxy proxy = new GestioneConsensi_RevocaConsensoProxy();
			proxy.setEndpoint(getConsensoWSUrl(GConstants.WS_NAME_URL_REVOCA_CONSENSO_LAVORATORE));
			outputXmlConsenso = proxy.invoke(codiceFiscaleLavoratoreString);

			RicercaConsensoXmlOutputBean bean = getRicercaConsensoXmlOutput(outputXmlConsenso);

			String dataAttuale = DateUtils.getNow();
			Date dateAttualeToday = sdf.parse(dataAttuale);

			params.put("strOrigine", GConstants.CONSENSO_SISTEMA_DI_ORIGINE);
			params.put("codStatoConsenso", GConstants.CONSENSO_REVOCATO_CODICE);
			params.put("datRegistrazione", "");
			params.put("datRevoca", dataAttuale);

			// Inserimento/Aggiornamento DB su Tabella AM_CONSENSO_FIRMA
			gestisciAmConsensoFirmaDB(params);

			if (_logger.isDebugEnabled()) {
				_logger.debug("xmlOutputConsenso: " + outputXmlConsenso.getDocument());
				_logger.debug("xmlCodiceConsenso: " + bean.getCodiceOutput());
				_logger.debug("xmlDescrizioneConsenso: " + bean.getDescrizioneOutput());
				_logger.debug("dataRevoca: " + dataAttuale);
			}

			resConsenso = new SourceBean("resConsenso");
			resConsenso.setAttribute("code", GConstants.CONSENSO_REVOCATO_CODICE);
			resConsenso.setAttribute("dateRec", null);
			resConsenso.setAttribute("dateRev", dateAttualeToday);
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e);
		}

		return resConsenso;
	}

	private RicercaConsensoXmlOutputBean getRicercaConsensoXmlOutput(XML outputXmlConsenso) throws ServiceException {

		RicercaConsensoXmlOutputBean bean = new RicercaConsensoXmlOutputBean();

		try {

			if (outputXmlConsenso != null) {

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new StringReader(outputXmlConsenso.getDocument())));

				XPath xPath = XPathFactory.newInstance().newXPath();

				Node nodeCodiceOutput = (Node) xPath.evaluate("//CodiceOutput/text()", doc, XPathConstants.NODE);
				if (nodeCodiceOutput != null) {
					bean.setCodiceOutput(Integer.parseInt(nodeCodiceOutput.getNodeValue()));
				}

				Node nodeDescrizioneOutput = (Node) xPath.evaluate("//DescrizioneOutput/text()", doc,
						XPathConstants.NODE);
				if (nodeDescrizioneOutput != null) {
					bean.setDescrizioneOutput(nodeDescrizioneOutput.getNodeValue());
				}

				Node nodeStatoOutput = (Node) xPath.evaluate("//Stato/text()", doc, XPathConstants.NODE);
				if (nodeStatoOutput != null) {
					bean.setStato(nodeStatoOutput.getNodeValue());
				}

				Node nodeDataRaccoltaOutput = (Node) xPath.evaluate("//DataRaccolta/text()", doc, XPathConstants.NODE);
				if (nodeDataRaccoltaOutput != null) {
					Date dataRaccoltaDate = sdfp.parse(nodeDataRaccoltaOutput.getNodeValue());
					bean.setDataRaccolta(dataRaccoltaDate);
				}

				Node nodeDataRevocaOutput = (Node) xPath.evaluate("//DataRevoca/text()", doc, XPathConstants.NODE);
				if (nodeDataRevocaOutput != null) {
					Date dataRevocaDate = sdfp.parse(nodeDataRevocaOutput.getNodeValue());
					bean.setDataRevoca(dataRevocaDate);
				}
			}

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return bean;

	}

	private void gestisciAmConsensoFirmaDB(Map<String, Object> params) throws ServiceException {

		boolean sqlEsito = false;

		try {
			// Verifica la prima volta se esiste un record gia inserito per il lavoratore
			BigDecimal cdnLav = getConsensoFirma();
			if (cdnLav == null) {
				// Inserimento
				sqlEsito = insertConsensoFirma(params);
			} else {
				// Aggiornamento
				sqlEsito = updateConsensoFirma(params);
			}

			_logger.debug("sqlEsisto: " + sqlEsito);

		} catch (ServiceException e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e);
		} catch (EMFInternalError e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			throw new ServiceException(e);
		}

	}

	private String getCodeFromVerificaConsenso(RicercaConsensoXmlOutputBean outputXmlConsenso) {
		String code = GConstants.CONSENSO_NON_DISPONIBILE_CODICE;

		/*
		 * if (esitoConsenso.value == false && outputXmlConsenso.getCodiceOutput() == 2){ code =
		 * GConstants.CONSENSO_ASSENTE; } else if (esitoConsenso.value == false && outputXmlConsenso.getCodiceOutput()
		 * == 0){ code = GConstants.CONSENSO_REVOCATO; } else if (esitoConsenso.value == true &&
		 * outputXmlConsenso.getCodiceOutput() == 0){ code = GConstants.CONSENSO_ATTIVO; }
		 */

		if (outputXmlConsenso.getStato().equalsIgnoreCase(GConstants.CONSENSO_ASSENTE)) {
			code = GConstants.CONSENSO_ASSENTE_CODICE;
		} else if (outputXmlConsenso.getStato().equalsIgnoreCase(GConstants.CONSENSO_REVOCATO)) {
			code = GConstants.CONSENSO_REVOCATO_CODICE;
		} else if (outputXmlConsenso.getStato().equalsIgnoreCase(GConstants.CONSENSO_ATTIVO)) {
			code = GConstants.CONSENSO_ATTIVO_CODICE;
		}

		return code;
	}

	public ConsensoFirmaBean getConsensoFirma(String cdnlavoratore) throws Exception {

		_logger.debug("GET_AM_CONSENSO_FIRMA");

		ConsensoFirmaBean bean = new ConsensoFirmaBean();

		Object[] sqlparams = new Object[1];
		sqlparams[0] = cdnlavoratore;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_CONSENSO_FIRMA", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setCdnLav((BigDecimal) row.getAttribute("cdnlavoratore"));
			bean.setPrgAzienda((BigDecimal) row.getAttribute("prgazienda"));
			bean.setSistemaOrigine((String) row.getAttribute("strorigine"));
			bean.setCodiceStatoConsenso((String) row.getAttribute("codstatoconsenso"));
			bean.setDataRegistrazione((Date) row.getAttribute("datregistrazione"));
			bean.setDataRevoca((Date) row.getAttribute("datrevoca"));
			bean.setCdnUtins((BigDecimal) row.getAttribute("cdnutins"));
			bean.setDtMins((Date) row.getAttribute("dtmins"));
			bean.setCdnUtMod((BigDecimal) row.getAttribute("cdnutmod"));
			bean.setDtmMod((Date) row.getAttribute("dtmmod"));
		}

		return bean;
	}

	private BigDecimal getConsensoFirma() throws Exception, ServiceException {

		_logger.debug("GET_AM_CONSENSO_FIRMA");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = (String) params.get("cdnLavoratore");

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_CONSENSO_FIRMA", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			cdnLav = (BigDecimal) row.getAttribute("cdnlavoratore");

		}

		return cdnLav;
	}

	private boolean insertConsensoFirma(Map<String, Object> params) throws EMFInternalError, ServiceException {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("INSERT CONSENSO FIRMA");

		Object[] sqlparams = new Object[8];
		sqlparams[0] = (String) params.get("cdnLavoratore");
		sqlparams[1] = (String) params.get("prgAzienda");
		sqlparams[2] = (String) params.get("strOrigine");
		sqlparams[3] = (String) params.get("codStatoConsenso");
		sqlparams[4] = (String) params.get("datRegistrazione");
		sqlparams[5] = (String) params.get("datRevoca");
		sqlparams[6] = (Integer) params.get("cdnUtins");
		sqlparams[7] = (Integer) params.get("cdnUtmod");

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();
			boolean esito = ((Boolean) transExec.executeQuery("INS_AM_CONSENSO_FIRMA", sqlparams, "INSERT"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();
		} catch (EMFInternalError e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new ServiceException(e);
		}

		return sqlEsito;
	}

	private boolean updateConsensoFirma(Map<String, Object> params) throws EMFInternalError, ServiceException {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("UDPATE CONSENSO FIRMA");

		// GET NUMLOK
		BigDecimal numkloconsensofirma = null;
		BigDecimal prgConsensoFirma = null;
		Object[] sqlparamsNumLok = new Object[1];
		sqlparamsNumLok[0] = (String) params.get("cdnLavoratore");

		try {

			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			// SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_CONSENSO_FIRMA", sqlparamsNumLok,
			// "SELECT", Values.DB_SIL_DATI);
			SourceBean row = (SourceBean) transExec.executeQuery("GET_AM_CONSENSO_FIRMA", sqlparamsNumLok, "SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				prgConsensoFirma = (BigDecimal) row.getAttribute("prgconsensofirma");
				numkloconsensofirma = ((BigDecimal) row.getAttribute("numkloconsensofirma")).add(new BigDecimal(1));
				// numkloconsensofirma = (BigDecimal) row.getAttribute("numkloconsensofirma");
			}

			Object[] sqlparams = new Object[10];
			sqlparams[0] = (String) params.get("cdnLavoratore");
			sqlparams[1] = (String) params.get("prgAzienda");
			sqlparams[2] = (String) params.get("strOrigine");
			sqlparams[3] = (String) params.get("codStatoConsenso");
			sqlparams[4] = (String) params.get("datRegistrazione");
			sqlparams[5] = (String) params.get("datRevoca");
			sqlparams[6] = (Integer) params.get("cdnUtins");
			sqlparams[7] = (Integer) params.get("cdnUtmod");
			sqlparams[8] = numkloconsensofirma;
			sqlparams[9] = prgConsensoFirma;

			boolean esito = ((Boolean) transExec.executeQuery("UPD_AM_CONSENSO_FIRMA", sqlparams, "UPDATE"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (EMFInternalError e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new ServiceException(e);
		}

		return sqlEsito;
	}

	private String getConsensoWSUrlOld(String urlName) throws Exception {

		String url = "";

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();

		ServizicoapWSProxy proxy = new ServizicoapWSProxy();
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndiName, urlName);
		url = endPoint.getUrl();

		_logger.debug("Web Service Gestione Consenso URL: " + url);

		return url;

	}

	private String getConsensoWSUrl(String urlName) throws Exception {

		String url = "";

		Object[] inputParameters = new Object[1]; // END_POINT_NAME
		inputParameters[0] = urlName;
		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("SELECT_TS_ENDPOINT", inputParameters, "SELECT",
				Values.DB_SIL_DATI);

		url = (String) ret.getAttribute("ROW.STRURL");

		_logger.debug("Web Service Gestione Consenso URL: " + url);

		return url;

	}

	public String getStatoConsensoFirma(String codiceConsenso) throws Exception, ServiceException {

		_logger.debug("getStatoConsensoFirma");

		String statoConsenso = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = codiceConsenso;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_STATO_CONSENSO_FIRMA", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			statoConsenso = (String) row.getAttribute("STATO_CONSENSO");

		}

		return statoConsenso;
	}

}
