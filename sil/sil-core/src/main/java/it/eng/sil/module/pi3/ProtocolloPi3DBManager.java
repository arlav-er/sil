package it.eng.sil.module.pi3;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.documenti.documentiList;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.util.blen.StringUtils;
import oracle.sql.BLOB;

public class ProtocolloPi3DBManager {

	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProtocolloPi3DBManager.class.getName());

	String DATE_FORMAT_NOW = "dd/MM/yyyy";
	String DATE_PARSE = "yyyy-MM-dd'T'HH:mm:sss";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	SimpleDateFormat sdfp = new SimpleDateFormat(DATE_PARSE);

	public ProtocolloPi3DBManager() {
		super();
	}

	public ProtocolloPi3Bean getProtocolloPi3(String prgProtPiTre) throws Exception {

		ProtocolloPi3Bean bean = new ProtocolloPi3Bean();

		_logger.debug("GET_AM_PROTOCOLLO_PITRE");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		// sqlparams[0] = (String) params.get("PRGPROTPITRE");
		sqlparams[0] = prgProtPiTre;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROTOCOLLO_PITRE", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setPrgProtPitre((BigDecimal) row.getAttribute("PRGPROTPITRE"));
			bean.setStrSegnatura((String) row.getAttribute("STRSEGNATURA"));
			bean.setStridDoc((String) row.getAttribute("STRIDDOC"));
			bean.setDataProt((Date) row.getAttribute("DATPROT"));
			bean.setStrMittente((String) row.getAttribute("STRMITTENTE"));
			bean.setStrOggetto((String) row.getAttribute("STROGGETTO"));
			// bean.setStrDestinatario((String)row.getAttribute("STRDESTINATARIO"));
			bean.setPrgTitolario((BigDecimal) row.getAttribute("PRGTITOLARIO"));
			bean.setCdnUtMod((BigDecimal) row.getAttribute("CDNUTMOD"));
			bean.setCdnUtins((BigDecimal) row.getAttribute("CDNUTINS"));
			bean.setDtMins((Date) row.getAttribute("DTMINS"));
			bean.setDtmMod((Date) row.getAttribute("DTMOD"));
			bean.setStrNumPratica((String) row.getAttribute("STRNUMPRATICA"));
			bean.setStrMittentePi3((String) row.getAttribute("STRMITPROT"));
		}

		return bean;
	}

	public ProtocolloPi3Bean getProtocolloPi3ByNrPratica(String nrPratica) throws Exception {

		ProtocolloPi3Bean bean = new ProtocolloPi3Bean();

		_logger.debug("GET_AM_PROTOCOLLO_PITRE_BY_NUM_PRATICA");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = nrPratica;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROTOCOLLO_PITRE_BY_NUM_PRATICA", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setPrgProtPitre((BigDecimal) row.getAttribute("PRGPROTPITRE"));
			bean.setStrSegnatura((String) row.getAttribute("STRSEGNATURA"));
			bean.setStridDoc((String) row.getAttribute("STRIDDOC"));
			bean.setDataProt((Date) row.getAttribute("DATPROT"));
			bean.setStrMittente((String) row.getAttribute("STRMITTENTE"));
			bean.setStrOggetto((String) row.getAttribute("STROGGETTO"));
			// bean.setStrDestinatario((String)row.getAttribute("STRDESTINATARIO"));
			bean.setPrgTitolario((BigDecimal) row.getAttribute("PRGTITOLARIO"));
			bean.setCdnUtMod((BigDecimal) row.getAttribute("CDNUTMOD"));
			bean.setCdnUtins((BigDecimal) row.getAttribute("CDNUTINS"));
			bean.setDtMins((Date) row.getAttribute("DTMINS"));
			bean.setDtmMod((Date) row.getAttribute("DTMOD"));
			bean.setStrNumPratica((String) row.getAttribute("STRNUMPRATICA"));
			bean.setStrMittentePi3((String) row.getAttribute("STRMITPROT"));
		}

		return bean;
	}

	public ProtocolloPi3Bean getUtenteFromProtocolloPi3(String idUtente) throws Exception {

		ProtocolloPi3Bean beanUtente = new ProtocolloPi3Bean();

		_logger.debug("GET_UTENTE_MITTENTE + GET_UTENTE_DESTINATARIO");

		ProtocolloPi3Bean beanMittente = this.getUtenteMittenteFromProtocolloPi3(idUtente);
		ProtocolloPi3Bean beanDestinatario = this.getUtenteDestinatarioFromProtocolloPi3(idUtente);

		if (!StringUtils.isEmpty(beanMittente.getStrMittentePi3())) {
			beanUtente.setStrMittente(beanMittente.getStrMittente());
			beanUtente.setStrMittentePi3(beanMittente.getStrMittentePi3());
		}

		if (!StringUtils.isEmpty(beanDestinatario.getStrDestinatarioPi3())) {
			beanUtente.setStrDestinatario(beanDestinatario.getStrDestinatario());
			beanUtente.setStrDestinatarioPi3(beanDestinatario.getStrDestinatarioPi3());
		}

		return beanUtente;
	}

	public ProtocolloPi3Bean getUtenteMittenteFromProtocolloPi3(String idUtente) throws Exception {

		ProtocolloPi3Bean bean = new ProtocolloPi3Bean();

		_logger.debug("GET_UTENTE_MITTENTE");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = idUtente;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery("GET_UTENTE_MITTENTE", sqlparams, "SELECT",
				Values.DB_SIL_DATI);

		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBeanRow = (SourceBean) iRows.next();

				String mittentePi3 = (String) sourceBeanRow.getAttribute("STRMITPROT");

				if (!StringUtils.isEmpty(mittentePi3)) {
					bean.setStrMittente((String) sourceBeanRow.getAttribute("STRMITTENTE"));
					bean.setStrMittentePi3(mittentePi3);

					break;
				}
			}
		}

		return bean;
	}

	public ProtocolloPi3Bean getUtenteDestinatarioFromProtocolloPi3(String idUtente) throws Exception {

		ProtocolloPi3Bean bean = new ProtocolloPi3Bean();

		_logger.debug("GET_UTENTE_DESTINATARIO");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = idUtente;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery("GET_UTENTE_DESTINATARIO", sqlparams, "SELECT",
				Values.DB_SIL_DATI);

		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBeanRow = (SourceBean) iRows.next();

				String destinazionePi3 = (String) sourceBeanRow.getAttribute("STRDESPROT");

				if (!StringUtils.isEmpty(destinazionePi3)) {
					bean.setStrDestinatario((String) sourceBeanRow.getAttribute("STRDESTINATARIO"));
					bean.setStrDestinatarioPi3(destinazionePi3);

					break;
				}
			}

		}

		return bean;
	}

	public boolean isDocumentSentPi3(String prgDocumento) throws Exception {

		boolean isDocumentSent = false;

		_logger.debug("CHECK_AM_PROTOCOLLO_DOCUMENTO_PITRE_STATE");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgDocumento;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("CHECK_AM_PROTOCOLLO_DOCUMENTO_PITRE_STATE", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			String codStatoDoc = (String) row.getAttribute("CODSTATODOC");

			if (!StringUtils.isEmpty(codStatoDoc)) {
				if (codStatoDoc.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_PROTOCOLLATO)) {
					isDocumentSent = true;
				}
			}

		}

		return isDocumentSent;

	}

	public boolean isDocumentAlreadyProcessedIntoPi3(String prgDocumento) throws Exception {

		boolean isDocumentProcessed = false;

		_logger.debug("CHECK_AM_PROTOCOLLO_DOCUMENTO_PITRE_STATE - isDocumentAlreadyProcessedIntoPi3");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgDocumento;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("CHECK_AM_PROTOCOLLO_DOCUMENTO_PITRE_STATE", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			BigDecimal prgProtDocPi3 = (BigDecimal) row.getAttribute("PRGPROTDOCPITRE");

			if (prgProtDocPi3 != null) {
				if (prgProtDocPi3.compareTo(BigDecimal.ZERO) != 0) {
					isDocumentProcessed = true;
				}
			}

		}

		return isDocumentProcessed;

	}

	public boolean isPraticaAlreadyProcessedIntoPi3(String nrPratica) throws Exception {

		boolean isPraticaProcessed = false;

		_logger.debug("GET_AM_PROTOCOLLO_PITRE_BY_NUM_PRATICA - isPraticaAlreadyProcessedIntoPi3");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = nrPratica;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROTOCOLLO_PITRE_BY_NUM_PRATICA", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			BigDecimal prgProtPi3 = (BigDecimal) row.getAttribute("PRGPROTPITRE");

			if (prgProtPi3 != null) {
				if (prgProtPi3.compareTo(BigDecimal.ZERO) != 0) {
					isPraticaProcessed = true;
				}
			}

		}

		return isPraticaProcessed;

	}

	public BigDecimal insertProtocolloPi3(ProtocolloPi3Bean bean) throws Exception {
		// boolean sqlEsito = false;
		BigDecimal prgProtPi3 = null;
		TransactionQueryExecutor transExec = null;

		_logger.debug("INS_AM_PROTOCOLLO_PITRE");

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			// GET PRG_PROT_PITRE (nextval)
			SourceBean row = (SourceBean) transExec.executeQuery("GET_PRG_PROT_FROM_AM_PROTOCOLLO_PITRE", null,
					"SELECT");

			prgProtPi3 = (BigDecimal) row.getAttribute("ROW.PRGPROTPI3");

			Object[] sqlparams = new Object[13];
			sqlparams[0] = (BigDecimal) prgProtPi3;
			sqlparams[1] = (String) bean.getStrSegnatura();
			sqlparams[2] = (String) bean.getStridDoc();

			// sqlparams[3] = (Date) bean.getDataProt();
			if (bean.getDataProt() != null) {
				sqlparams[3] = (String) sdf.format(bean.getDataProt());
			} else {
				sqlparams[3] = null;
			}

			sqlparams[4] = (String) bean.getStrMittente();
			sqlparams[5] = (String) bean.getStrOggetto();
			// sqlparams[5] = (String) bean.getStrDestinatario();
			sqlparams[6] = (Integer) bean.getPrgTitolario().intValue();
			sqlparams[7] = (Integer) bean.getCdnUtMod().intValue();
			sqlparams[8] = (Integer) bean.getCdnUtins().intValue();

			if (bean.getDtMins() != null) {
				sqlparams[9] = (String) sdf.format(bean.getDtMins());
			} else {
				sqlparams[9] = null;
			}

			if (bean.getDtmMod() != null) {
				sqlparams[10] = (String) sdf.format(bean.getDtmMod());
			} else {
				sqlparams[10] = null;
			}

			// sqlparams[8] = (Date) bean.getDtMins();
			// sqlparams[9] = (Date) bean.getDtmMod();
			sqlparams[11] = (String) bean.getStrNumPratica();
			sqlparams[12] = (String) bean.getStrMittentePi3();

			boolean esito = ((Boolean) transExec.executeQuery("INS_AM_PROTOCOLLO_PITRE", sqlparams, "INSERT"))
					.booleanValue();
			// sqlEsito = esito;

			transExec.commitTransaction();
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		// return sqlEsito;
		return prgProtPi3;
	}

	public boolean updateProtocolloPi3(ProtocolloPi3Bean bean) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("UPD_AM_PROTOCOLLO_PITRE");

		// GET NUMLOK
		BigDecimal numKLokProtocolloPiTre = null;
		BigDecimal prgConsensoFirma = null;
		Object[] sqlparamsNumLok = new Object[1];
		sqlparamsNumLok[0] = (String) bean.getPrgProtPitre().toString();

		try {

			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			Object[] sqlparams = new Object[12];
			sqlparams[0] = (String) bean.getStrSegnatura();
			sqlparams[1] = (String) bean.getStridDoc();

			sqlparams[2] = (Date) bean.getDataProt();
			// if (bean.getDataProt() != null){
			// sqlparams[2] = (String) sdf.format(bean.getDataProt());
			// }

			sqlparams[3] = (String) bean.getStrMittente();
			sqlparams[4] = (String) bean.getStrOggetto();
			// sqlparams[5] = (String) bean.getStrDestinatario();
			sqlparams[5] = (Integer) bean.getPrgTitolario().intValue();
			sqlparams[6] = (Integer) bean.getCdnUtMod().intValue();
			sqlparams[7] = (Date) bean.getDtmMod();
			sqlparams[8] = (String) bean.getStrNumPratica();
			sqlparams[9] = (BigDecimal) bean.getNumKloProtocollo();
			sqlparams[10] = (String) bean.getStrMittentePi3();

			sqlparams[11] = bean.getPrgProtPitre().toString();

			boolean esito = ((Boolean) transExec.executeQuery("UPD_AM_PROTOCOLLO_PITRE", sqlparams, "UPDATE"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;
	}

	public boolean deleteProtocolloPi3(String prgProtPiTre) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("DELETE_AM_PROTOCOLLO_PITRE");

		try {

			Object params[] = new Object[1];
			params[0] = prgProtPiTre;
			Boolean esito = (Boolean) transExec.executeQuery("DELETE_AM_PROTOCOLLO_PITRE", params, "DELETE");
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;

	}

	public ArrayList<ProtocolloDocumentoPi3Bean> getListProtocolloDocumentoPi3(String prgProtPiTre) throws Exception {

		ArrayList<ProtocolloDocumentoPi3Bean> listBean = new ArrayList<ProtocolloDocumentoPi3Bean>();

		_logger.debug("GET_LIST_AM_PROTOCOLLO_DOCUMENTO_PITRE");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgProtPiTre;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery("GET_LIST_AM_PROTOCOLLO_DOCUMENTO_PITRE", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBean = (SourceBean) iRows.next();

				ProtocolloDocumentoPi3Bean bean = new ProtocolloDocumentoPi3Bean();

				bean.setPrgProtDocPitre((BigDecimal) sourceBean.getAttribute("PRGPROTDOCPITRE"));
				bean.setPrgProtPitre((BigDecimal) sourceBean.getAttribute("PRGPROTPITRE"));
				bean.setFlgPrincipale((String) sourceBean.getAttribute("FLGPRINCIPALE"));
				bean.setFlgNotificaAnnullamento((String) sourceBean.getAttribute("FLGNOTIFICANNUL"));
				bean.setPrgDocumento((BigDecimal) sourceBean.getAttribute("PRGDOCUMENTO"));
				bean.setCdnUtMod((BigDecimal) sourceBean.getAttribute("CDNUTMOD"));
				bean.setCdnUtins((BigDecimal) sourceBean.getAttribute("CDNUTINS"));
				bean.setDtMins((Date) sourceBean.getAttribute("DTMINS"));
				bean.setDtmMod((Date) sourceBean.getAttribute("DTMOD"));
				bean.setDatInvio((Date) sourceBean.getAttribute("DATINVIO"));
				bean.setCodStatoInvio((String) sourceBean.getAttribute("CODSTATODOC"));

				listBean.add(bean);
			}

		}

		return listBean;
	}

	public ArrayList<ProtocolloDocumentoPi3Bean> getListProtocolloDocumentoPi3ByNrPratica(String nrPratica)
			throws Exception {

		ArrayList<ProtocolloDocumentoPi3Bean> listBean = new ArrayList<ProtocolloDocumentoPi3Bean>();

		_logger.debug("GET_LIST_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_NR_PRATICA");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = nrPratica;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery(
				"GET_LIST_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_NR_PRATICA", sqlparams, "SELECT", Values.DB_SIL_DATI);
		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBean = (SourceBean) iRows.next();

				ProtocolloDocumentoPi3Bean bean = new ProtocolloDocumentoPi3Bean();

				bean.setPrgProtDocPitre((BigDecimal) sourceBean.getAttribute("PRGPROTDOCPITRE"));
				bean.setPrgProtPitre((BigDecimal) sourceBean.getAttribute("PRGPROTPITRE"));
				bean.setFlgPrincipale((String) sourceBean.getAttribute("FLGPRINCIPALE"));
				bean.setFlgNotificaAnnullamento((String) sourceBean.getAttribute("FLGNOTIFICANNUL"));
				bean.setPrgDocumento((BigDecimal) sourceBean.getAttribute("PRGDOCUMENTO"));
				bean.setCdnUtMod((BigDecimal) sourceBean.getAttribute("CDNUTMOD"));
				bean.setCdnUtins((BigDecimal) sourceBean.getAttribute("CDNUTINS"));
				bean.setDtMins((Date) sourceBean.getAttribute("DTMINS"));
				bean.setDtmMod((Date) sourceBean.getAttribute("DTMOD"));
				bean.setDatInvio((Date) sourceBean.getAttribute("DATINVIO"));
				bean.setCodStatoInvio((String) sourceBean.getAttribute("CODSTATODOC"));

				listBean.add(bean);
			}

		}

		return listBean;
	}

	public ArrayList<ProtocolloDocumentoPi3Bean> getListAllegatiFromProtocolloDocumentoPi3ByNrPratica(String nrPratica)
			throws Exception {

		ArrayList<ProtocolloDocumentoPi3Bean> listBean = new ArrayList<ProtocolloDocumentoPi3Bean>();

		_logger.debug("GET_LIST_ALLEGATI_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_NR_PRATICA");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = nrPratica;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery(
				"GET_LIST_ALLEGATI_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_NR_PRATICA", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBean = (SourceBean) iRows.next();

				ProtocolloDocumentoPi3Bean bean = new ProtocolloDocumentoPi3Bean();

				bean.setPrgProtDocPitre((BigDecimal) sourceBean.getAttribute("PRGPROTDOCPITRE"));
				bean.setPrgProtPitre((BigDecimal) sourceBean.getAttribute("PRGPROTPITRE"));
				bean.setFlgPrincipale((String) sourceBean.getAttribute("FLGPRINCIPALE"));
				bean.setFlgNotificaAnnullamento((String) sourceBean.getAttribute("FLGNOTIFICANNUL"));
				bean.setPrgDocumento((BigDecimal) sourceBean.getAttribute("PRGDOCUMENTO"));
				bean.setCdnUtMod((BigDecimal) sourceBean.getAttribute("CDNUTMOD"));
				bean.setCdnUtins((BigDecimal) sourceBean.getAttribute("CDNUTINS"));
				bean.setDtMins((Date) sourceBean.getAttribute("DTMINS"));
				bean.setDtmMod((Date) sourceBean.getAttribute("DTMOD"));
				bean.setDatInvio((Date) sourceBean.getAttribute("DATINVIO"));
				bean.setCodStatoInvio((String) sourceBean.getAttribute("CODSTATODOC"));

				listBean.add(bean);
			}

		}

		return listBean;
	}

	public ProtocolloDocumentoPi3Bean getMainDocumentFromProtocolloDocumentoPi3ByPrgDocumento(String prgDocumento)
			throws Exception {

		ProtocolloDocumentoPi3Bean bean = new ProtocolloDocumentoPi3Bean();

		_logger.debug("GET_MAIN_DOCUMENT_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_NR_PRATICA");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgDocumento;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery(
				"GET_MAIN_DOCUMENT_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_NR_PRATICA", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setPrgProtDocPitre((BigDecimal) row.getAttribute("PRGPROTDOCPITRE"));
			bean.setPrgProtPitre((BigDecimal) row.getAttribute("PRGPROTPITRE"));
			bean.setFlgPrincipale((String) row.getAttribute("FLGPRINCIPALE"));
			bean.setFlgNotificaAnnullamento((String) row.getAttribute("FLGNOTIFICANNUL"));
			bean.setPrgDocumento((BigDecimal) row.getAttribute("PRGDOCUMENTO"));
			bean.setCdnUtMod((BigDecimal) row.getAttribute("CDNUTMOD"));
			bean.setCdnUtins((BigDecimal) row.getAttribute("CDNUTINS"));
			bean.setDtMins((Date) row.getAttribute("DTMINS"));
			bean.setDtmMod((Date) row.getAttribute("DTMOD"));
			bean.setDatInvio((Date) row.getAttribute("DATINVIO"));
			bean.setCodStatoInvio((String) row.getAttribute("CODSTATODOC"));
		}

		return bean;
	}

	public ProtocolloDocumentoPi3Bean getProtocolloDocumentoPi3(String prgProtDocPiTre) throws Exception {

		ProtocolloDocumentoPi3Bean bean = new ProtocolloDocumentoPi3Bean();

		_logger.debug("GET_AM_PROTOCOLLO_DOCUMENTO_PITRE");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgProtDocPiTre;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROTOCOLLO_DOCUMENTO_PITRE", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setPrgProtDocPitre((BigDecimal) row.getAttribute("PRGPROTDOCPITRE"));
			bean.setPrgProtPitre((BigDecimal) row.getAttribute("PRGPROTPITRE"));
			bean.setFlgPrincipale((String) row.getAttribute("FLGPRINCIPALE"));
			bean.setFlgNotificaAnnullamento((String) row.getAttribute("FLGNOTIFICANNUL"));
			bean.setPrgDocumento((BigDecimal) row.getAttribute("PRGDOCUMENTO"));
			bean.setCdnUtMod((BigDecimal) row.getAttribute("CDNUTMOD"));
			bean.setCdnUtins((BigDecimal) row.getAttribute("CDNUTINS"));
			bean.setDtMins((Date) row.getAttribute("DTMINS"));
			bean.setDtmMod((Date) row.getAttribute("DTMOD"));
			bean.setDatInvio((Date) row.getAttribute("DATINVIO"));
			bean.setCodStatoInvio((String) row.getAttribute("CODSTATODOC"));
		}

		return bean;
	}

	public ProtocolloDocumentoPi3Bean getProtocolloMainDocumentoPi3ByPrgDocumento(String prgDocumento)
			throws Exception {

		ProtocolloDocumentoPi3Bean bean = new ProtocolloDocumentoPi3Bean();

		_logger.debug("GET_AM_PROTOCOLLO_MAIN_DOCUMENTO_PITRE_BY_PRG_DOCUMENTO");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgDocumento;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_PRG_DOCUMENTO",
				sqlparams, "SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setPrgProtDocPitre((BigDecimal) row.getAttribute("PRGPROTDOCPITRE"));
			bean.setPrgProtPitre((BigDecimal) row.getAttribute("PRGPROTPITRE"));
			bean.setFlgPrincipale((String) row.getAttribute("FLGPRINCIPALE"));
			bean.setFlgNotificaAnnullamento((String) row.getAttribute("FLGNOTIFICANNUL"));
			bean.setPrgDocumento((BigDecimal) row.getAttribute("PRGDOCUMENTO"));
			bean.setCdnUtMod((BigDecimal) row.getAttribute("CDNUTMOD"));
			bean.setCdnUtins((BigDecimal) row.getAttribute("CDNUTINS"));
			bean.setDtMins((Date) row.getAttribute("DTMINS"));
			bean.setDtmMod((Date) row.getAttribute("DTMOD"));
			bean.setDatInvio((Date) row.getAttribute("DATINVIO"));
			bean.setCodStatoInvio((String) row.getAttribute("CODSTATODOC"));
		}

		return bean;
	}

	public boolean isMainDocument(String prgDocumento) throws Exception {

		boolean isMainDocument = false;

		_logger.debug("GET_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_PRG_DOCUMENTO - IS MAIN DOCUMENT");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgDocumento;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_PRG_DOCUMENTO",
				sqlparams, "SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			String flgPrincipale = (String) row.getAttribute("FLGPRINCIPALE");

			if (!StringUtils.isEmpty(flgPrincipale)) {
				if (flgPrincipale.equalsIgnoreCase("S")) {
					isMainDocument = true;
				}
			}
		}

		return isMainDocument;
	}

	public boolean isDocumentoAnnullato(String prgDocumento) throws Exception {

		boolean isDocumentoAnnullato = false;

		_logger.debug("GET_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_PRG_DOCUMENTO - IS DOCUMENTO ANNULLATO");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgDocumento;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROTOCOLLO_DOCUMENTO_PITRE_BY_PRG_DOCUMENTO",
				sqlparams, "SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			String flgNotificaAnnullamento = (String) row.getAttribute("FLGNOTIFICANNUL");

			if (!StringUtils.isEmpty(flgNotificaAnnullamento)) {
				if (flgNotificaAnnullamento.equalsIgnoreCase("S")) {
					isDocumentoAnnullato = true;
				}
			}
		}

		return isDocumentoAnnullato;
	}

	public boolean insertProtocolloDocumentoPi3(ProtocolloDocumentoPi3Bean bean) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("INS_AM_PROTOCOLLO_DOCUMENTO_PITRE");

		Object[] sqlparams = new Object[10];
		sqlparams[0] = (BigDecimal) bean.getPrgProtPitre();
		sqlparams[1] = (String) bean.getFlgPrincipale();
		sqlparams[2] = (BigDecimal) bean.getPrgDocumento();
		sqlparams[3] = (Integer) bean.getCdnUtMod().intValue();
		sqlparams[4] = (Integer) bean.getCdnUtins().intValue();

		if (bean.getDtMins() != null) {
			sqlparams[5] = (String) sdf.format(bean.getDtMins());
		} else {
			sqlparams[5] = null;
		}

		if (bean.getDtMins() != null) {
			sqlparams[6] = (String) sdf.format(bean.getDtmMod());
		} else {
			sqlparams[6] = null;
		}

		if (bean.getDatInvio() != null) {
			sqlparams[7] = (String) sdf.format(bean.getDatInvio());
		} else {
			sqlparams[7] = null;
		}

		sqlparams[8] = (String) bean.getCodStatoInvio();
		sqlparams[9] = (String) bean.getFlgNotificaAnnullamento();

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();
			boolean esito = ((Boolean) transExec.executeQuery("INS_AM_PROTOCOLLO_DOCUMENTO_PITRE", sqlparams, "INSERT"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;
	}

	public boolean updateProtocolloDocumentoPi3(ProtocolloDocumentoPi3Bean bean) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("UPD_AM_PROTOCOLLO_DOCUMENTO_PITRE");

		// GET NUMLOK
		BigDecimal numKLokProtocolloDocumentoPiTre = null;
		BigDecimal prgConsensoFirma = null;
		Object[] sqlparamsNumLok = new Object[1];
		sqlparamsNumLok[0] = (String) bean.getPrgProtDocPitre().toString();

		try {

			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			Object[] sqlparams = new Object[8];
			sqlparams[0] = (String) bean.getFlgPrincipale();
			sqlparams[1] = (BigDecimal) bean.getPrgDocumento();
			sqlparams[2] = (Integer) bean.getCdnUtMod().intValue();
			sqlparams[3] = (Date) bean.getDtmMod();
			sqlparams[4] = (Date) bean.getDatInvio();
			sqlparams[5] = (String) bean.getCodStatoInvio();

			sqlparams[6] = (BigDecimal) bean.getNumKloProtDocumento();

			sqlparams[7] = bean.getPrgProtDocPitre().toString();

			boolean esito = ((Boolean) transExec.executeQuery("UPD_AM_PROTOCOLLO_DOCUMENTO_PITRE", sqlparams, "UPDATE"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;
	}

	public boolean deleteProtocolloDocumentoPi3(String prgProtDocPiTre) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("DELETE_AM_PROTOCOLLO_DOCUMENTO_PITRE");

		try {

			Object params[] = new Object[1];
			params[0] = prgProtDocPiTre;
			Boolean esito = (Boolean) transExec.executeQuery("DELETE_AM_PROTOCOLLO_DOCUMENTO_PITRE", params, "DELETE");
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;

	}

	// documentType = 'IM' per la DID
	// documentType = 'IMDICANN' per la DID annuale
	// documentType = 'PT297' per il PATTO
	// documentType = 'TRCPI' per la Richiesta di Trasferimento
	public BigDecimal getPrgTemplateStampa(String documentType) throws Exception {

		BigDecimal prgTemplateStampa = new BigDecimal(0);

		_logger.info("GET_PRG_ST_TEMPLATE_STAMPA");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = documentType;
		_logger.info("documentType :" + documentType);
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_PRG_ST_TEMPLATE_STAMPA", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			_logger.info("row :" + row.toString());
			_logger.info("row contiene attributi? :" + row.containsAttribute("PRGTEMPLATESTAMPA"));
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			try {
				prgTemplateStampa = (BigDecimal) row.getAttribute("PRGTEMPLATESTAMPA");

				if (prgTemplateStampa.compareTo(BigDecimal.ZERO) == 0) {
					return null;
				}
			} catch (Exception e) {
				_logger.warn("prgTemplateStampa vuoto");
				return null;
			}
		}
		_logger.info("prgTemplateStampa :" + prgTemplateStampa);
		return prgTemplateStampa;

	}

	public BigDecimal getPrgTemplateStampaByNomeDoc(String nomeDoc) throws Exception {

		BigDecimal prgTemplateStampa = new BigDecimal(0);

		_logger.info("GET_PRG_ST_TEMPLATE_STAMPA");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = nomeDoc;
		_logger.info("documentType :" + nomeDoc);
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_PRG_ST_TEMPLATE_STAMPA_BY_NOME_DOC", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			_logger.info("row :" + row.toString());
			_logger.info("row contiene attributi? :" + row.containsAttribute("PRGTEMPLATESTAMPA"));
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			try {
				prgTemplateStampa = (BigDecimal) row.getAttribute("PRGTEMPLATESTAMPA");

				if (prgTemplateStampa.compareTo(BigDecimal.ZERO) == 0) {
					return null;
				}
			} catch (Exception e) {
				_logger.warn("prgTemplateStampa vuoto");
				return null;
			}
		}
		_logger.info("prgTemplateStampa :" + prgTemplateStampa);
		return prgTemplateStampa;

	}

	// documentType = 'IM' per la DID
	// documentType = 'IMDICANN' per la DID annuale
	// documentType = 'PT297' per il PATTO
	// documentType = 'TRCPI' per la Richiesta di Trasferimento
	public BigDecimal getPrgConfigProtocolloFromTemplateStampa(String documentType) throws Exception {

		BigDecimal prgConfigProtocollo = new BigDecimal(0);

		_logger.debug("GET_PRG_CONFIG_PROT_FROM_ST_TEMPLATE_STAMPA");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = documentType;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_PRG_CONFIG_PROT_FROM_ST_TEMPLATE_STAMPA",
				sqlparams, "SELECT", Values.DB_SIL_DATI);
		if (row != null) {

			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			prgConfigProtocollo = (BigDecimal) row.getAttribute("PRGCONFIGPROT");

			if (prgConfigProtocollo.compareTo(BigDecimal.ZERO) == 0) {
				return null;
			}
		}

		return prgConfigProtocollo;

	}

	public String getCodiceFromTitolario(String prgConfigProtocollo) throws Exception {

		String codiceProtocollo = new String();

		_logger.debug("GET_CODICE_FROM_TITOLARIO");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgConfigProtocollo;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_CODICE_FROM_TITOLARIO", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			codiceProtocollo = (String) row.getAttribute("STRCODICECLASSIFICAZIONE");
		}

		return codiceProtocollo;

	}

	public String getTitolarioFromCodice(String prgTitolario) throws Exception {

		String descTitolario = new String();

		_logger.debug("GET_TITOLARIO_FROM_CODICE");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgTitolario;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_TITOLARIO_FROM_CODICE", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			descTitolario = (String) row.getAttribute("STRCODICECLASSIFICAZIONE");
		}

		return descTitolario;

	}

	public String getCodiceFromPi3(String annoProt) throws Exception {

		String codiceTitolario = null;

		String descrizioneTitolario = null;

		_logger.debug("EXIST_DOCUMENT_PI3");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = annoProt;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("EXIST_DOCUMENT_PI3", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			if (!StringUtils.isEmpty(row.toString())) {
				try {
					codiceTitolario = ((BigDecimal) row.getAttribute("PRGTITOLARIO")).toString();
					descrizioneTitolario = getTitolarioFromCodice(codiceTitolario);
				} catch (NullPointerException e) {
					_logger.debug("riga vuota ");
				}
			}
		}

		return descrizioneTitolario;

	}

	public BigDecimal getPrgFromTitolario(String strCodiceClassificazione) throws Exception {

		BigDecimal prgTitolario = new BigDecimal(0);

		_logger.debug("GET_PRG_FROM_TITOLARIO");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = strCodiceClassificazione;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_PRG_FROM_TITOLARIO", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			prgTitolario = (BigDecimal) row.getAttribute("PRGTITOLARIO");
		}

		return prgTitolario;

	}

	/**
	 * 
	 * es.: 24.3, 24.4, 24.7, 6.4
	 * 
	 */
	public ArrayList<String> getListCodiciFromTitolario() throws Exception {

		ArrayList<String> listaCodici = new ArrayList<String>();

		_logger.debug("GET_CODICI_FROM_TITOLARIO");

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery("GET_CODICI_FROM_TITOLARIO", null, "SELECT",
				Values.DB_SIL_DATI);
		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBean = (SourceBean) iRows.next();

				listaCodici.add((String) sourceBean.getAttribute("STRCODICECLASSIFICAZIONE"));
			}

		}

		return listaCodici;
	}

	public boolean isDocumentTypeGraphSignature(String prgTemplateStampa) throws Exception {

		boolean isFirmaGrafo = false;

		_logger.debug("DettaglioTemplate - Stampa");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgTemplateStampa;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("DettaglioTemplate", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			String flgFirmaGrafo = (String) row.getAttribute("FLGFIRMAGRAFO");

			if (!StringUtils.isEmpty(flgFirmaGrafo)) {
				if (flgFirmaGrafo.equalsIgnoreCase("1")) {
					isFirmaGrafo = true;
				}
			}
		}

		return isFirmaGrafo;
	}

	public boolean isAllegatoDocumentoFirmato(String prgDocumento) throws Exception {

		boolean isFirmato = false;

		_logger.debug("IS_ALLEGATO_DOCUMENTO_FIRMATO");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgDocumento;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("IS_ALLEGATO_DOCUMENTO_FIRMATO", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			String flgFirmato = (String) row.getAttribute("FLGFIRMATO");

			if (!StringUtils.isEmpty(flgFirmato)) {
				if (flgFirmato.equalsIgnoreCase("S")) {
					isFirmato = true;
				}
			}
		}

		return isFirmato;

	}

	// in ENTRATA = 'A'
	// in USCITA = 'P'
	// in INTERNO NON DEFINITO = 'I'
	public String getDocumentType(String prgTemplateStampa) throws Exception {

		String tipoProtocollazione = null;

		_logger.debug("DettaglioTemplate - getDocumentType");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgTemplateStampa;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("DettaglioTemplate", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			tipoProtocollazione = (String) row.getAttribute("CODTIPOPROTOCOLLAZIONE");
		}

		return tipoProtocollazione;
	}

	// CODTIPODOMINIO='DL' (DOMINIO LAVORATORE)
	// CODTIPODOMINIO='DA' (DOMINIO AZIENDA)
	public String getDominio(String prgTemplateStampa) throws Exception {

		String codeTipoDominio = null;

		_logger.debug("DettaglioTemplate - getDominio");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgTemplateStampa;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("DettaglioTemplate", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			codeTipoDominio = (String) row.getAttribute("CODTIPODOMINIO");
		}

		return codeTipoDominio;
	}

	public String getDescTypeProt(String prgTemplateStampa) throws Exception {

		String descTipoProtocollazione = null;

		_logger.debug("DettaglioTemplate - getDescTypeProt");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgTemplateStampa;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("DettaglioTemplate", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			descTipoProtocollazione = (String) row.getAttribute("DESCRIZIONEPROTOCOLLAZIONE");
		}
		_logger.debug("descTipoProtocollazione ::::" + descTipoProtocollazione);
		return descTipoProtocollazione;
	}

	// PROTOCOLLO = 'P'
	// REPERTORIO = 'R'
	// NON APPLICABILE = 'NA'
	public String getCodeTipoTrattamento(String prgTemplateStampa) throws Exception {

		String tipoDelTrattamento = null;

		_logger.debug("DettaglioTemplate - getCodeTipoTrattamento");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgTemplateStampa;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("DettaglioTemplate", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			tipoDelTrattamento = (String) row.getAttribute("CODTIPOTRATTAMENTO");
		}

		return tipoDelTrattamento;
	}

	public String getDescTipoTrattamento(String prgTemplateStampa) throws Exception {

		String descTipoTrattamento = null;

		_logger.debug("DettaglioTemplate - getDescTipoTrattamento");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgTemplateStampa;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("DettaglioTemplate", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			descTipoTrattamento = (String) row.getAttribute("DESCRIZIONETRATTAMENTO");
		}
		_logger.debug("getDescTipoTrattamento ::::" + descTipoTrattamento);
		return descTipoTrattamento;
	}

	public String getDescTipoDocumento(String prgTemplateStampa) throws Exception {

		String descTipoDocumento = null;

		_logger.debug("DettaglioTemplate - getDescTipoDocumento");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgTemplateStampa;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("DettaglioTemplate", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			descTipoDocumento = (String) row.getAttribute("STRDESCRIZIONE");
		}
		_logger.debug("getDescTipoDocumento ::::" + descTipoDocumento);
		return descTipoDocumento;
	}

	public ArrayList<Documento> getListaDocumentiDiD(RequestContainer reqQ, String cdnLavoratore) throws Exception {

		ArrayList<Documento> listBean = new ArrayList<Documento>();

		_logger.debug("GET_LISTA_DOCUMENTI_ED_ALLEGATI_DID");

		TransactionQueryExecutor transExec = null;

		reqQ.setAttribute("cdnLavoratore", cdnLavoratore);

		documentiList documentiList = new documentiList();
		String query = documentiList.getStatement(reqQ, null);

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			// SourceBean rows = (SourceBean) QueryExecutor.executeQuery(reqQ, resQ, pool, new SourceBean(query),
			// "SELECT");
			SourceBean rows = (SourceBean) ProcessorsUtils.executeSelectQuery(query, transExec);
			// transExec.commitTransaction();

			if (rows != null) {
				Vector vRows = rows.getAttributeAsVector("ROW");
				Iterator<SourceBean> iRows = vRows.iterator();

				while (iRows.hasNext()) {
					SourceBean sourceBean = (SourceBean) iRows.next();

					Documento bean = new Documento();

					bean.setPrgDocumento((BigDecimal) sourceBean.getAttribute("PRGDOCUMENTO"));

					listBean.add(bean);
				}

			}
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		} finally {
			Utils.releaseResources(transExec.getDataConnection(), null, null);
		}

		return listBean;

	}

	public boolean insertInvioProtocollazioneDifferita(InvioProtocollazioneDifferitaBean bean) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("INS_TS_INVIO_PROT_DIFFERITA");

		Object[] sqlparams = new Object[3];
		sqlparams[0] = (String) bean.getStrNumPratica();

		if (bean.getDatIns() != null) {
			sqlparams[1] = (String) sdf.format(bean.getDatIns());
		} else {
			sqlparams[1] = null;
		}

		/*
		 * if (bean.getBlbFile() != null){
		 * 
		 * if(bean.getBlbFile().length() > 0){
		 * 
		 * byte fileContent[] = new byte[(int)bean.getBlbFile().length()]; FileInputStream fin = new
		 * FileInputStream(bean.getBlbFile()); int read = 0; ByteArrayOutputStream ous = new ByteArrayOutputStream();
		 * while ( (read = fin.read(fileContent)) != -1 ) { ous.write(fileContent, 0, read); }
		 * 
		 * sqlparams[2] = fileContent;
		 * 
		 * } else{ sqlparams[2] = null; }
		 * 
		 * } else{ sqlparams[2] = null; }
		 */

		sqlparams[2] = (String) bean.getCodStato();

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();
			boolean esito = ((Boolean) transExec.executeQuery("INS_TS_INVIO_PROT_DIFFERITA", sqlparams, "INSERT"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;
	}

	public boolean writeBLOBean(BigDecimal prgProtDifferita, File fileBean) throws Exception {

		boolean sqlEsito = false;
		SQLCommand selectCommand = null;
		DataResult dr = null;
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			// Creazione connessione
			DataConnection conn = transExec.getDataConnection();

			// Creazione Statement
			String stmt = SQLStatements.getStatement("WRITE_BLOB_BEAN");
			selectCommand = conn.createSelectCommand(stmt);
			ArrayList inputParameters = new ArrayList(1);
			inputParameters.add(conn.createDataField("PRGPROTDIFFERITA", Types.BIGINT, prgProtDifferita));

			// Esecuzione query
			dr = selectCommand.execute(inputParameters);

			// Recupero puntatore OutputStream per scrittura BLOB
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			DataField df = sdr.getDataRow().getColumn("BLBFILE");
			BLOB resultBlob = (BLOB) df.getObjectValue();
			OutputStream outStream = resultBlob.getBinaryOutputStream();

			// InputStream is = new FileInputStream(fileBean);
			// byte[] byteRes = IOUtils.toByteArray(is);
			byte[] byteRes = ProtocolloPi3Utility.fromFileToByteArray(fileBean);
			// Scrittura del SourceBean nel BLOB
			// byte[] byteRes = result.toXML(false).getBytes();
			int chunk = resultBlob.getChunkSize();
			for (int i = 0; i * chunk < byteRes.length; i++) {
				int chunkLength = ((i + 1) * chunk < byteRes.length ? chunk : byteRes.length - (i * chunk));
				byte[] b = new byte[chunkLength];
				for (int j = 0; j < chunkLength; j++) {
					b[j] = byteRes[(i * chunk) + j];
				}
				outStream.write(b, 0, chunkLength);
			}
			outStream.flush();
			outStream.close();

			transExec.commitTransaction();
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return true;
	}

	public File readBLOBean(BigDecimal prgProtDifferita) throws Exception {
		// Creazione connessione
		DataConnection conn = null;
		SQLCommand selectCommand = null;
		DataResult dr = null;
		File blob = null;

		try {
			conn = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);

			// Creazione Statement
			String stmt = SQLStatements.getStatement("READ_BLOB_BEAN");
			selectCommand = conn.createSelectCommand(stmt);
			ArrayList inputParameters = new ArrayList(1);
			inputParameters.add(conn.createDataField("PRGPROTDIFFERITA", Types.BIGINT, prgProtDifferita));

			// File blob = File.createTempFile("temp", "tmp");

			// Esecuzione query
			dr = selectCommand.execute(inputParameters);

			// Recupero BLOB come InputStream
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			DataField df = sdr.getDataRow().getColumn("BLBFILE");
			BLOB resultBlob = (BLOB) df.getObjectValue();

			// FileOutputStream fout = new FileOutputStream(blob);
			// IOUtils.copy(resultBlob.getBinaryStream(), fout);
			blob = ProtocolloPi3Utility.putBlobDbFieldIntoGenericFile(resultBlob.getBinaryStream());

			// FileUtils.writeByteArrayToFile(blob, resultBlob.getBytes());

			// Creazione del SourceBean
			/*
			 * InputStream instream = resultBlob.getBinaryStream(); StringBuffer strBuffer = new StringBuffer(); int
			 * chunk = resultBlob.getChunkSize(); byte[] buffer = new byte[chunk]; int length; while ((length =
			 * instream.read(buffer)) != -1) { strBuffer.append(new String(buffer, 0, length)); } blob =
			 * SourceBean.fromXMLString(strBuffer.toString());
			 */
		} finally {
			Utils.releaseResources(conn, selectCommand, dr);
		}

		// Ritorno l'eccezione o il blob
		if (blob == null) {
			throw new Exception("Impossibile recuperare il file bean memorizzato");
		} else
			return blob;
	}

	public boolean updateInvioProtocollazioneDifferita(InvioProtocollazioneDifferitaBean bean) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("UPD_TS_INVIO_PROT_DIFFERITA");

		try {

			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			Object[] sqlparams = new Object[3];

			if (bean.getDatInvio() != null) {
				sqlparams[0] = (String) sdf.format(bean.getDatInvio());
			} else {
				sqlparams[0] = null;
			}
			// sqlparams[0] = (Date) bean.getDatInvio();

			sqlparams[1] = (String) bean.getCodStato();
			sqlparams[2] = (BigDecimal) bean.getPrgProtDifferita();

			boolean esito = ((Boolean) transExec.executeQuery("UPD_TS_INVIO_PROT_DIFFERITA", sqlparams, "UPDATE"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;
	}

	public boolean setNullBlobInvioProtocollazioneDifferita(InvioProtocollazioneDifferitaBean bean) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("SET_NULL_BLOB_TS_INVIO_PROT_DIFFERITA");

		try {

			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			Object[] sqlparams = new Object[1];
			sqlparams[0] = (BigDecimal) bean.getPrgProtDifferita();

			boolean esito = ((Boolean) transExec.executeQuery("SET_NULL_BLOB_TS_INVIO_PROT_DIFFERITA", sqlparams,
					"UPDATE")).booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;
	}

	public boolean deleteInvioProtocollazioneDifferita(BigDecimal prgProtDifferita) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("DEL_TS_INVIO_PROT_DIFFERITA");

		try {

			Object params[] = new Object[1];
			params[0] = prgProtDifferita;
			Boolean esito = (Boolean) transExec.executeQuery("DEL_TS_INVIO_PROT_DIFFERITA", params, "DELETE");
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;

	}

	public InvioProtocollazioneDifferitaBean getInvioProtocollazioneDifferitaByNrPratica(String nrPratica)
			throws Exception {

		InvioProtocollazioneDifferitaBean bean = new InvioProtocollazioneDifferitaBean();

		_logger.debug("GET_TS_INVIO_PROT_DIFFERITA_BY_NUM_PRATICA");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = nrPratica;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_TS_INVIO_PROT_DIFFERITA_BY_NUM_PRATICA",
				sqlparams, "SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setPrgProtDifferita((BigDecimal) row.getAttribute("PRGPROTDIFFERITA"));
			bean.setStrNumPratica((String) row.getAttribute("STRNUMPRATICA"));
			bean.setDatIns((Date) row.getAttribute("DATINS"));
			bean.setDatInvio((Date) row.getAttribute("DATINVIO"));

			// bean.setBlbFile((File)row.getAttribute("BLBFILE")); // FROM BYTES ARRAY TO FILE?

			bean.setCodStato((String) row.getAttribute("CODSTATO"));

		}

		return bean;
	}

	public ArrayList<InvioProtocollazioneDifferitaBean> getListaDaElabInvioProtocollazioneDifferitaByCodStato(
			String codStato) throws Exception {

		ArrayList<InvioProtocollazioneDifferitaBean> listBean = new ArrayList<InvioProtocollazioneDifferitaBean>();

		_logger.debug("LIST_DAELAB_TS_INVIO_PROT_DIFFERITA_BY_CODSTATO");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = codStato;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery("LIST_DAELAB_TS_INVIO_PROT_DIFFERITA_BY_CODSTATO",
				sqlparams, "SELECT", Values.DB_SIL_DATI);
		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBean = (SourceBean) iRows.next();

				InvioProtocollazioneDifferitaBean bean = new InvioProtocollazioneDifferitaBean();

				bean.setPrgProtDifferita((BigDecimal) sourceBean.getAttribute("PRGPROTDIFFERITA"));
				bean.setStrNumPratica((String) sourceBean.getAttribute("STRNUMPRATICA"));
				bean.setDatIns((Date) sourceBean.getAttribute("DATINS"));
				bean.setDatInvio((Date) sourceBean.getAttribute("DATINVIO"));

				// bean.setBlbFile((File)sourceBean.getAttribute("BLBFILE")); // FROM BYTES ARRAY TO FILE?

				bean.setCodStato((String) sourceBean.getAttribute("CODSTATO"));

				listBean.add(bean);
			}

		}

		return listBean;
	}

	public BatchProtocollazioneDifferitaBean getBatchProtocollazioneDifferitaByNomeBatch(String nomeBatch)
			throws Exception {

		BatchProtocollazioneDifferitaBean bean = new BatchProtocollazioneDifferitaBean();

		_logger.debug("GET_TS_BATCH_PROT_DIFFERITA_BY_NOME_BATCH");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = nomeBatch;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_TS_BATCH_PROT_DIFFERITA_BY_NOME_BATCH", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setPrgBatchProtDifferita((BigDecimal) row.getAttribute("PRGBATCHPROTDIF"));
			bean.setStrDescrizione((String) row.getAttribute("STRDESCRIZIONE"));
			bean.setNumOraInizio((String) row.getAttribute("NUMORAINIZIO"));
			bean.setNumOraFine((String) row.getAttribute("NUMORAFINE"));
			bean.setFlgProtDiff((String) row.getAttribute("FLGPROTDIFF"));
		}

		return bean;
	}

	public boolean insertProtocollazionePi3Destinatario(DestinatarioPi3Bean bean) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("INS_AM_PROT_PITRE_DEST");

		Object[] sqlparams = new Object[5];
		sqlparams[0] = (BigDecimal) bean.getPrgProtPi3();
		sqlparams[1] = (String) bean.getCodiceMotivoTrasmissioneInterna();
		sqlparams[2] = (String) bean.getStrDestinatarioPi3();
		sqlparams[3] = (String) bean.getStrDestinatarioSil();
		sqlparams[4] = (String) bean.getFlgDestinatario();

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();
			boolean esito = ((Boolean) transExec.executeQuery("INS_AM_PROT_PITRE_DEST", sqlparams, "INSERT"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();
		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;
	}

	public boolean updateProtocollazionePi3Destinatario(DestinatarioPi3Bean bean) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("UPD_AM_PROT_PITRE_DEST");

		try {

			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			Object[] sqlparams = new Object[6];
			sqlparams[0] = (String) bean.getCodiceMotivoTrasmissioneInterna();
			sqlparams[1] = (String) bean.getStrDestinatarioPi3();
			sqlparams[2] = (String) bean.getStrDestinatarioSil();
			sqlparams[3] = (String) bean.getFlgDestinatario();
			sqlparams[4] = (BigDecimal) bean.getNumKloProtDestinatario();
			sqlparams[5] = (BigDecimal) bean.getPrgProtDestinatario();

			boolean esito = ((Boolean) transExec.executeQuery("UPD_AM_PROT_PITRE_DEST", sqlparams, "UPDATE"))
					.booleanValue();
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;
	}

	public boolean deleteProtocollazionePi3Destinatario(BigDecimal prgProtDestinatario) throws Exception {
		boolean sqlEsito = false;
		TransactionQueryExecutor transExec = null;

		_logger.debug("DELETE_AM_PROT_PITRE_DEST");

		try {

			Object params[] = new Object[1];
			params[0] = prgProtDestinatario;
			Boolean esito = (Boolean) transExec.executeQuery("DELETE_AM_PROT_PITRE_DEST", params, "DELETE");
			sqlEsito = esito;
			transExec.commitTransaction();

		} catch (Exception e) {
			_logger.error("Error: " + e.getMessage(), e);
			transExec.rollBackTransaction();
			throw new Exception(e);
		}

		return sqlEsito;

	}

	public DestinatarioPi3Bean getProtocollazionePi3Destinatario(String prgProtDestinatario) throws Exception {

		DestinatarioPi3Bean bean = new DestinatarioPi3Bean();

		_logger.debug("GET_AM_PROT_PITRE_DEST");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgProtDestinatario;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROT_PITRE_DEST", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			bean.setPrgProtDestinatario((BigDecimal) row.getAttribute("PRGPROTDEST"));
			bean.setPrgProtPi3((BigDecimal) row.getAttribute("PRGPROTPITRE"));
			bean.setCodiceMotivoTrasmissioneInterna((String) row.getAttribute("CODMOTTRASMINT"));
			bean.setStrDestinatarioPi3((String) row.getAttribute("STRDESPROT"));
			bean.setStrDestinatarioSil((String) row.getAttribute("STRDESTINATARIO"));
			bean.setFlgDestinatario((String) row.getAttribute("FLGDESTP"));
			bean.setNumKloProtDestinatario((BigDecimal) row.getAttribute("NUMKLOPROTDEST"));

		}

		return bean;
	}

	public ArrayList<DestinatarioPi3Bean> getListaProtocollazionePi3DestinatarioByDestSil(String strDestinatarioSil)
			throws Exception {

		ArrayList<DestinatarioPi3Bean> listBean = new ArrayList<DestinatarioPi3Bean>();

		_logger.debug("GET_AM_PROT_PITRE_DEST_BY_DEST_SIL");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = strDestinatarioSil;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROT_PITRE_DEST_BY_DEST_SIL", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBean = (SourceBean) iRows.next();

				DestinatarioPi3Bean bean = new DestinatarioPi3Bean();

				bean.setPrgProtDestinatario((BigDecimal) sourceBean.getAttribute("PRGPROTDEST"));
				bean.setPrgProtPi3((BigDecimal) sourceBean.getAttribute("PRGPROTPITRE"));
				bean.setCodiceMotivoTrasmissioneInterna((String) sourceBean.getAttribute("CODMOTTRASMINT"));
				bean.setStrDestinatarioPi3((String) sourceBean.getAttribute("STRDESPROT"));
				bean.setStrDestinatarioSil((String) sourceBean.getAttribute("STRDESTINATARIO"));
				bean.setFlgDestinatario((String) sourceBean.getAttribute("FLGDESTP"));
				bean.setNumKloProtDestinatario((BigDecimal) sourceBean.getAttribute("NUMKLOPROTDEST"));

				listBean.add(bean);
			}

		}

		return listBean;
	}

	public ArrayList<DestinatarioPi3Bean> getListaProtocollazionePi3DestinatarioByDestPi3(String strDestinatarioPi3)
			throws Exception {

		ArrayList<DestinatarioPi3Bean> listBean = new ArrayList<DestinatarioPi3Bean>();

		_logger.debug("GET_AM_PROT_PITRE_DEST_BY_DEST_PI3");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = strDestinatarioPi3;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery("GET_AM_PROT_PITRE_DEST_BY_DEST_PI3", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBean = (SourceBean) iRows.next();

				DestinatarioPi3Bean bean = new DestinatarioPi3Bean();

				bean.setPrgProtDestinatario((BigDecimal) sourceBean.getAttribute("PRGPROTDEST"));
				bean.setPrgProtPi3((BigDecimal) sourceBean.getAttribute("PRGPROTPITRE"));
				bean.setCodiceMotivoTrasmissioneInterna((String) sourceBean.getAttribute("CODMOTTRASMINT"));
				bean.setStrDestinatarioPi3((String) sourceBean.getAttribute("STRDESPROT"));
				bean.setStrDestinatarioSil((String) sourceBean.getAttribute("STRDESTINATARIO"));
				bean.setFlgDestinatario((String) sourceBean.getAttribute("FLGDESTP"));
				bean.setNumKloProtDestinatario((BigDecimal) sourceBean.getAttribute("NUMKLOPROTDEST"));

				listBean.add(bean);
			}

		}

		return listBean;
	}

	public ArrayList<DestinatarioPi3Bean> getListaProtocollazionePi3DestinatarioByPrgProtPi3(String prgProtPi3)
			throws Exception {

		ArrayList<DestinatarioPi3Bean> listBean = new ArrayList<DestinatarioPi3Bean>();

		_logger.debug("GET_LIST_AM_PROT_PITRE_DEST_BY_PRG_PROT");

		BigDecimal cdnLav = null;
		Object[] sqlparams = new Object[1];
		sqlparams[0] = prgProtPi3;

		SourceBean rows = (SourceBean) QueryExecutor.executeQuery("GET_LIST_AM_PROT_PITRE_DEST_BY_PRG_PROT", sqlparams,
				"SELECT", Values.DB_SIL_DATI);
		if (rows != null) {
			Vector vRows = rows.getAttributeAsVector("ROW");
			Iterator<SourceBean> iRows = vRows.iterator();

			while (iRows.hasNext()) {
				SourceBean sourceBean = (SourceBean) iRows.next();

				DestinatarioPi3Bean bean = new DestinatarioPi3Bean();

				bean.setPrgProtDestinatario((BigDecimal) sourceBean.getAttribute("PRGPROTDEST"));
				bean.setPrgProtPi3((BigDecimal) sourceBean.getAttribute("PRGPROTPITRE"));
				bean.setCodiceMotivoTrasmissioneInterna((String) sourceBean.getAttribute("CODMOTTRASMINT"));
				bean.setStrDestinatarioPi3((String) sourceBean.getAttribute("STRDESPROT"));
				bean.setStrDestinatarioSil((String) sourceBean.getAttribute("STRDESTINATARIO"));
				bean.setFlgDestinatario((String) sourceBean.getAttribute("FLGDESTP"));
				bean.setNumKloProtDestinatario((BigDecimal) sourceBean.getAttribute("NUMKLOPROTDEST"));

				listBean.add(bean);
			}

		}

		return listBean;
	}

}
