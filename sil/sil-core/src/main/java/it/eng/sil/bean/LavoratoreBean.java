package it.eng.sil.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.webservices.agenda.appuntamento.LavoratoreUtils;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.pojo.yg.sap.Datipersonali;
import it.eng.sil.pojo.yg.sap.Datistranieri;
import it.eng.sil.pojo.yg.sap.Domicilio;
import it.eng.sil.pojo.yg.sap.Esperienzalavoro;
import it.eng.sil.pojo.yg.sap.EsperienzelavoroLst;
import it.eng.sil.pojo.yg.sap.LavoratoreType;
import it.eng.sil.pojo.yg.sap.PoliticheAttive;
import it.eng.sil.pojo.yg.sap.Recapiti;
import it.eng.sil.pojo.yg.sap.Residenza;
import it.eng.sil.pojo.yg.sap.Statoinanagrafe;
import it.eng.sil.pojo.yg.sap.due.PoliticheAttiveLst;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;

public class LavoratoreBean {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(LavoratoreBean.class.getName());

	private static final String CODIFICA_INESISTENTE = "NT";
	private boolean gestioneSap2 = false;
	private BigDecimal cdnlavoratore = null;
	private String codiceFiscale = null;
	private String codCpi = null;
	private String codMonoTipoCpi = null;
	private ElencoAnagraficoBean elenco = null;
	private DidBean did = null;
	private LavoratoreType lavoratoreSapType = null;
	private it.eng.sil.pojo.yg.sap.due.LavoratoreType lavoratoreSapDueType = null;
	private User operatore = null;
	private int numEsperienzeLavoro;
	private int numEsperienzeLavoroImportate;
	private String esitoDatiAnagrafici = null;
	private String esitoDatiAmministrativi = null;
	private boolean operazioneInserimento = false;
	private int numPoliticheCompetenza;
	private int numColloquiInseriti;
	private int numPoliticheImportate;
	private int numPoliticheNonImportate;
	private int numPoliticheNonMappate;
	private String flgDidPresente = null;
	private String msgErrCode = null;

	public LavoratoreBean() {
		setNumEsperienzeLavoro(0);
		setNumEsperienzeLavoroImportate(0);
		setEsitoDatiAnagrafici("");
		setEsitoDatiAmministrativi("");
		setNumPoliticheCompetenza(0);
		setNumColloquiInseriti(0);
		setNumPoliticheImportate(0);
		setNumPoliticheNonImportate(0);
		setNumPoliticheNonMappate(0);
		setOperazioneInserimento(false);
	}

	public LavoratoreBean(String codFisc, MultipleTransactionQueryExecutor transExec) throws Exception {
		this.codiceFiscale = codFisc.toUpperCase();
		setNumEsperienzeLavoro(0);
		setNumEsperienzeLavoroImportate(0);
		setEsitoDatiAnagrafici("");
		setEsitoDatiAmministrativi("");
		setNumPoliticheCompetenza(0);
		setNumColloquiInseriti(0);
		setNumPoliticheImportate(0);
		setNumPoliticheNonImportate(0);
		setNumPoliticheNonMappate(0);
		setOperazioneInserimento(false);
		caricaLavoratore(transExec);
	}

	protected void caricaLavoratore(MultipleTransactionQueryExecutor transExec) throws Exception {
		Object[] params = new Object[] { getCodiceFiscale() };
		SourceBean row = (SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			BigDecimal cdnlav = (BigDecimal) row.getAttribute("cdnLavoratore");
			if (cdnlav != null) {
				setCdnLavoratore(cdnlav);
				Object[] inputParameters = new Object[1];
				inputParameters[0] = cdnlav;
				SourceBean anLavStoriaInfSourceBean = ((SourceBean) transExec
						.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));
				anLavStoriaInfSourceBean = anLavStoriaInfSourceBean.containsAttribute("ROW")
						? (SourceBean) anLavStoriaInfSourceBean.getAttribute("ROW")
						: anLavStoriaInfSourceBean;
				String codCpiTit = (String) anLavStoriaInfSourceBean.getAttribute("CODCPITIT");
				String codMonoTipoCpi = (String) anLavStoriaInfSourceBean.getAttribute("CODMONOTIPOCPI");
				if (codCpiTit != null) {
					setCodCpi(codCpiTit);
				}
				if (codMonoTipoCpi != null) {
					setCodMonoTipoCpi(codMonoTipoCpi);
				}
				setElencoAnagrafico(new ElencoAnagraficoBean());
				getElencoAnagrafico().caricaElenco(cdnlav, transExec);
				setDid(new DidBean());
				getDid().loadDid(getCdnLavoratore(), transExec);
			}
		}
	}

	protected void caricaLavoratoreInserito(MultipleTransactionQueryExecutor transExec) throws Exception {
		setElencoAnagrafico(new ElencoAnagraficoBean());
		getElencoAnagrafico().caricaElenco(getCdnLavoratore(), transExec);
		setDid(new DidBean());
	}

	public boolean inserisciLavoratore(MultipleTransactionQueryExecutor transExec) {
		BigDecimal cdnLavoratore = null;
		try {
			transExec.initTransaction();
			BigDecimal userID = new BigDecimal(getOperatore().getCodut());
			// dati necessari

			String flgCfOk = "N";
			String cfLav = null;
			Object[] inputParameters = null;

			boolean success = false;

			// nuovo cdnLavoratore

			cdnLavoratore = LavoratoreUtils.getNextCdnLavoratore(transExec);
			setCdnLavoratore(cdnLavoratore);

			Datipersonali datiPersonali = getLavoratoreSapType().getDatianagrafici().getDatipersonali();

			Datistranieri dsImporta = getLavoratoreSapType().getDatianagrafici().getDatistranieri();
			if (dsImporta == null) {
				dsImporta = new Datistranieri();
			}
			Residenza residenzImporta = getLavoratoreSapType().getDatianagrafici().getResidenza();
			if (residenzImporta == null) {
				residenzImporta = new Residenza();
			}
			Domicilio domicilioImporta = getLavoratoreSapType().getDatianagrafici().getDomicilio();
			if (domicilioImporta == null) {
				domicilioImporta = new Domicilio();
			}
			Recapiti recap = getLavoratoreSapType().getDatianagrafici().getRecapiti();
			if (recap == null) {
				recap = new Recapiti();
			}

			cfLav = datiPersonali.getCodicefiscale();

			// verifica cf

			if (CF_utils.verificaParzialeCF(cfLav, datiPersonali.getNome(), datiPersonali.getCognome()) == 0) {
				flgCfOk = "S";
			} else {
				_logger.warn("Errore verifica codice fiscale: " + getCodiceFiscale());
				transExec.rollBackTransaction();
				return false;
			}

			// inserimento lavoratore
			String indirizzoDomicilio = domicilioImporta.getIndirizzo();
			if (indirizzoDomicilio != null && indirizzoDomicilio.length() > 60) {
				indirizzoDomicilio = indirizzoDomicilio.substring(0, 60);
			}
			String indirizzoResidenza = residenzImporta.getIndirizzo();
			if (indirizzoResidenza != null && indirizzoResidenza.length() > 60) {
				indirizzoResidenza = indirizzoResidenza.substring(0, 60);
			}
			inputParameters = new Object[31];
			inputParameters[0] = cdnLavoratore;
			inputParameters[1] = datiPersonali.getCodicefiscale();
			inputParameters[2] = datiPersonali.getCognome();
			inputParameters[3] = datiPersonali.getNome();
			inputParameters[4] = datiPersonali.getSesso();
			inputParameters[5] = DateUtils.formatXMLGregorian(datiPersonali.getDatanascita());
			inputParameters[6] = datiPersonali.getCodcomune();
			inputParameters[7] = datiPersonali.getCodcittadinanza();
			inputParameters[8] = datiPersonali.getCodcittadinanza();
			inputParameters[9] = null;
			inputParameters[10] = null;
			inputParameters[11] = null;
			inputParameters[12] = null;
			inputParameters[13] = flgCfOk;
			inputParameters[14] = userID;
			inputParameters[15] = userID;
			inputParameters[16] = residenzImporta.getCodcomune();
			inputParameters[17] = indirizzoResidenza;
			inputParameters[18] = residenzImporta.getLocalita();
			inputParameters[19] = residenzImporta.getCap();
			inputParameters[20] = domicilioImporta.getCodcomune();
			inputParameters[21] = indirizzoDomicilio;
			inputParameters[22] = domicilioImporta.getLocalita();
			inputParameters[23] = domicilioImporta.getCap();
			inputParameters[24] = null;
			inputParameters[25] = recap.getTelefono();
			inputParameters[26] = null;
			inputParameters[27] = recap.getCellulare();
			inputParameters[28] = recap.getEmail();
			inputParameters[29] = recap.getFax();
			inputParameters[30] = null;

			success = ((Boolean) transExec.executeQuery("INSERT_AN_LAVORATOREANAGIND_APPUNTAMENTO_APPUNTAMENTO_ONLINE",
					inputParameters, "INSERT")).booleanValue();

			if (!success) {
				_logger.warn(
						"Errore durante inserimento lavoratore nella tabella an_lavoratore: " + getCodiceFiscale());
				transExec.rollBackTransaction();
				return false;
			}

			// regola competenza (codmonotipocpi)
			String codCpiTit = null;
			String codCpiOrig = null;
			String codTipoCpi = null;
			SourceBean rowCpi = LavoratoreUtils.getInfoCpiProvinciaComune(transExec, domicilioImporta.getCodcomune());

			if (rowCpi != null) {
				rowCpi = rowCpi.containsAttribute("ROW") ? (SourceBean) rowCpi.getAttribute("ROW") : rowCpi;
				String codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPI");
				codCpiDomicilio = codCpiDomicilio.trim();
				String codProvinciaDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "PROVINCIACPI");
				String codProvinciaSil = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIASIL");
				if (codCpiDomicilio.equalsIgnoreCase(CODIFICA_INESISTENTE)) {
					codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGO");
				}
				if (codProvinciaDomicilio.equalsIgnoreCase(codProvinciaSil)) {
					codTipoCpi = Properties.CODMONOTIPOCPI_COMP;
					codCpiTit = codCpiDomicilio;
				} else {
					codTipoCpi = Properties.CODMONOTIPOCPI_TIT;
					codCpiTit = getOperatore().getCodRif();
					if (codCpiTit == null || codCpiTit.length() < 9) {
						codCpiTit = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGOGEN");
					}
					codCpiOrig = codCpiDomicilio;
				}

				setCodCpi(codCpiTit);
				setCodMonoTipoCpi(codTipoCpi);
				// aggiornamento an_lav_storia_inf

				inputParameters = new Object[1];
				inputParameters[0] = cdnLavoratore;
				SourceBean anLavStoriaInfSourceBean = ((SourceBean) transExec
						.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));

				anLavStoriaInfSourceBean = anLavStoriaInfSourceBean.containsAttribute("ROW")
						? (SourceBean) anLavStoriaInfSourceBean.getAttribute("ROW")
						: anLavStoriaInfSourceBean;

				BigDecimal prgLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean.getAttribute("PRGLAVSTORIAINF");
				BigDecimal numkloLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean
						.getAttribute("NUMKLOLAVSTORIAINF");

				inputParameters = new Object[9];
				inputParameters[0] = codCpiTit;
				inputParameters[1] = userID;
				inputParameters[2] = userID;
				inputParameters[3] = codTipoCpi;
				inputParameters[4] = codCpiOrig;
				inputParameters[5] = numkloLavStoriaInf.add(new BigDecimal("1"));
				inputParameters[6] = domicilioImporta.getCodcomune();
				inputParameters[7] = null;
				inputParameters[8] = prgLavStoriaInf;

				success = ((Boolean) transExec.executeQuery("UPDATE_AN_LAV_STORIA_INF_INS_LAV_IMPORTA_SAP",
						inputParameters, "UPDATE")).booleanValue();

				if (!success) {
					_logger.warn("Errore durante aggiornamento tabella an_lav_storia_inf: " + getCodiceFiscale());
					transExec.rollBackTransaction();
					return false;
				}
			}

			// inserimento nella tabella sp_lavoratore se presente identificativo SAP
			String identificativoSAP = getLavoratoreSapType().getDatiinvio().getIdentificativosap();
			if (identificativoSAP != null && !identificativoSAP.equals("")) {
				inputParameters = new Object[9];
				inputParameters[0] = DateUtils.getNow();
				inputParameters[1] = identificativoSAP;
				inputParameters[2] = getLavoratoreSapType().getDatiinvio().getCodiceentetit();
				inputParameters[3] = DateUtils
						.formatXMLGregorian(getLavoratoreSapType().getDatiinvio().getDatadinascita());
				inputParameters[4] = cdnLavoratore;
				inputParameters[5] = userID;
				inputParameters[6] = userID;
				inputParameters[7] = datiPersonali.getCodicefiscale();
				inputParameters[8] = "01";

				success = ((Boolean) transExec.executeQuery("INSERT_SP_LAVORATORE_FROM_SAP", inputParameters, "INSERT"))
						.booleanValue();

				if (!success) {
					_logger.warn(
							"Errore durante inserimento lavoratore nella tabella sp_lavoratore: " + getCodiceFiscale());
					transExec.rollBackTransaction();
					return false;
				}
			}

			caricaLavoratoreInserito(transExec);

			transExec.commitTransaction();
			setEsitoDatiAnagrafici("Anagrafica importata correttamente");
			setOperazioneInserimento(true);
			return true;
		} catch (Exception e) {
			try {
				transExec.rollBackTransaction();
			} catch (EMFInternalError e1) {
				it.eng.sil.util.TraceWrapper.error(_logger, "Errore inserimento lavoratore.", (Exception) e1);
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore inserimento lavoratore.", (Exception) e);
			setEsitoDatiAnagrafici("Errore importazione anagrafica");
			setMsgErrCode(e.getMessage());
			return false;
		}

	}

	public boolean inserisciLavoratore2(MultipleTransactionQueryExecutor transExec) {
		BigDecimal cdnLavoratore = null;
		try {
			transExec.initTransaction();
			BigDecimal userID = new BigDecimal(getOperatore().getCodut());
			// dati necessari

			String flgCfOk = "N";
			String cfLav = null;
			Object[] inputParameters = null;

			boolean success = false;

			// nuovo cdnLavoratore

			cdnLavoratore = LavoratoreUtils.getNextCdnLavoratore(transExec);
			setCdnLavoratore(cdnLavoratore);

			it.eng.sil.pojo.yg.sap.due.Datipersonali datiPersonali = getLavoratoreSapDueType().getDatianagrafici()
					.getDatipersonali();

			it.eng.sil.pojo.yg.sap.due.Datistranieri dsImporta = getLavoratoreSapDueType().getDatianagrafici()
					.getDatistranieri();
			if (dsImporta == null) {
				dsImporta = new it.eng.sil.pojo.yg.sap.due.Datistranieri();
			}
			it.eng.sil.pojo.yg.sap.due.Residenza residenzImporta = getLavoratoreSapDueType().getDatianagrafici()
					.getResidenza();
			if (residenzImporta == null) {
				residenzImporta = new it.eng.sil.pojo.yg.sap.due.Residenza();
			}
			it.eng.sil.pojo.yg.sap.due.Domicilio domicilioImporta = getLavoratoreSapDueType().getDatianagrafici()
					.getDomicilio();
			if (domicilioImporta == null) {
				domicilioImporta = new it.eng.sil.pojo.yg.sap.due.Domicilio();
			}
			it.eng.sil.pojo.yg.sap.due.Recapiti recap = getLavoratoreSapDueType().getDatianagrafici().getRecapiti();
			if (recap == null) {
				recap = new it.eng.sil.pojo.yg.sap.due.Recapiti();
			}

			cfLav = datiPersonali.getCodicefiscale();

			// verifica cf

			if (CF_utils.verificaParzialeCF(cfLav, datiPersonali.getNome(), datiPersonali.getCognome()) == 0) {
				flgCfOk = "S";
			} else {
				_logger.warn("Errore verifica codice fiscale: " + getCodiceFiscale());
				transExec.rollBackTransaction();
				return false;
			}

			// inserimento lavoratore
			String indirizzoDomicilio = domicilioImporta.getIndirizzo();
			if (indirizzoDomicilio != null && indirizzoDomicilio.length() > 60) {
				indirizzoDomicilio = indirizzoDomicilio.substring(0, 60);
			}
			String indirizzoResidenza = residenzImporta.getIndirizzo();
			if (indirizzoResidenza != null && indirizzoResidenza.length() > 60) {
				indirizzoResidenza = indirizzoResidenza.substring(0, 60);
			}
			inputParameters = new Object[31];
			inputParameters[0] = cdnLavoratore;
			inputParameters[1] = datiPersonali.getCodicefiscale();
			inputParameters[2] = datiPersonali.getCognome();
			inputParameters[3] = datiPersonali.getNome();
			inputParameters[4] = datiPersonali.getSesso();
			inputParameters[5] = DateUtils.formatXMLGregorian(datiPersonali.getDatanascita());
			inputParameters[6] = datiPersonali.getCodcomune();
			inputParameters[7] = datiPersonali.getCodcittadinanza();
			inputParameters[8] = datiPersonali.getCodcittadinanza();
			inputParameters[9] = null;
			inputParameters[10] = null;
			inputParameters[11] = null;
			inputParameters[12] = null;
			inputParameters[13] = flgCfOk;
			inputParameters[14] = userID;
			inputParameters[15] = userID;
			inputParameters[16] = residenzImporta.getCodcomune();
			inputParameters[17] = indirizzoResidenza;
			inputParameters[18] = residenzImporta.getLocalita();
			inputParameters[19] = residenzImporta.getCap();
			inputParameters[20] = domicilioImporta.getCodcomune();
			inputParameters[21] = indirizzoDomicilio;
			inputParameters[22] = domicilioImporta.getLocalita();
			inputParameters[23] = domicilioImporta.getCap();
			inputParameters[24] = null;
			inputParameters[25] = recap.getTelefono();
			inputParameters[26] = null;
			inputParameters[27] = recap.getCellulare();
			inputParameters[28] = recap.getEmail();
			inputParameters[29] = recap.getFax();
			inputParameters[30] = null;

			success = ((Boolean) transExec.executeQuery("INSERT_AN_LAVORATOREANAGIND_APPUNTAMENTO_APPUNTAMENTO_ONLINE",
					inputParameters, "INSERT")).booleanValue();

			if (!success) {
				_logger.warn(
						"Errore durante inserimento lavoratore nella tabella an_lavoratore: " + getCodiceFiscale());
				transExec.rollBackTransaction();
				return false;
			}

			// regola competenza (codmonotipocpi)
			String codCpiTit = null;
			String codCpiOrig = null;
			String codTipoCpi = null;
			SourceBean rowCpi = LavoratoreUtils.getInfoCpiProvinciaComune(transExec, domicilioImporta.getCodcomune());

			if (rowCpi != null) {
				rowCpi = rowCpi.containsAttribute("ROW") ? (SourceBean) rowCpi.getAttribute("ROW") : rowCpi;
				String codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPI");
				codCpiDomicilio = codCpiDomicilio.trim();
				String codProvinciaDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "PROVINCIACPI");
				String codProvinciaSil = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIASIL");
				if (codCpiDomicilio.equalsIgnoreCase(CODIFICA_INESISTENTE)) {
					codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGO");
				}
				if (codProvinciaDomicilio.equalsIgnoreCase(codProvinciaSil)) {
					codTipoCpi = Properties.CODMONOTIPOCPI_COMP;
					codCpiTit = codCpiDomicilio;
				} else {
					codTipoCpi = Properties.CODMONOTIPOCPI_TIT;
					codCpiTit = getOperatore().getCodRif();
					if (codCpiTit == null || codCpiTit.length() < 9) {
						codCpiTit = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGOGEN");
					}
					codCpiOrig = codCpiDomicilio;
				}

				setCodCpi(codCpiTit);
				setCodMonoTipoCpi(codTipoCpi);
				// aggiornamento an_lav_storia_inf

				inputParameters = new Object[1];
				inputParameters[0] = cdnLavoratore;
				SourceBean anLavStoriaInfSourceBean = ((SourceBean) transExec
						.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));

				anLavStoriaInfSourceBean = anLavStoriaInfSourceBean.containsAttribute("ROW")
						? (SourceBean) anLavStoriaInfSourceBean.getAttribute("ROW")
						: anLavStoriaInfSourceBean;

				BigDecimal prgLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean.getAttribute("PRGLAVSTORIAINF");
				BigDecimal numkloLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean
						.getAttribute("NUMKLOLAVSTORIAINF");

				inputParameters = new Object[9];
				inputParameters[0] = codCpiTit;
				inputParameters[1] = userID;
				inputParameters[2] = userID;
				inputParameters[3] = codTipoCpi;
				inputParameters[4] = codCpiOrig;
				inputParameters[5] = numkloLavStoriaInf.add(new BigDecimal("1"));
				inputParameters[6] = domicilioImporta.getCodcomune();
				inputParameters[7] = null;
				inputParameters[8] = prgLavStoriaInf;

				success = ((Boolean) transExec.executeQuery("UPDATE_AN_LAV_STORIA_INF_INS_LAV_IMPORTA_SAP",
						inputParameters, "UPDATE")).booleanValue();

				if (!success) {
					_logger.warn("Errore durante aggiornamento tabella an_lav_storia_inf: " + getCodiceFiscale());
					transExec.rollBackTransaction();
					return false;
				}
			}

			// inserimento nella tabella sp_lavoratore se presente identificativo SAP
			String identificativoSAP = getLavoratoreSapDueType().getDatiinvio().getIdentificativosap();
			if (identificativoSAP != null && !identificativoSAP.equals("")) {
				inputParameters = new Object[9];
				inputParameters[0] = DateUtils.getNow();
				inputParameters[1] = identificativoSAP;
				inputParameters[2] = getLavoratoreSapDueType().getDatiinvio().getCodiceentetit();
				inputParameters[3] = DateUtils
						.formatXMLGregorian(getLavoratoreSapDueType().getDatiinvio().getDatadinascita());
				inputParameters[4] = cdnLavoratore;
				inputParameters[5] = userID;
				inputParameters[6] = userID;
				inputParameters[7] = datiPersonali.getCodicefiscale();
				inputParameters[8] = "01";

				success = ((Boolean) transExec.executeQuery("INSERT_SP_LAVORATORE_FROM_SAP", inputParameters, "INSERT"))
						.booleanValue();

				if (!success) {
					_logger.warn(
							"Errore durante inserimento lavoratore nella tabella sp_lavoratore: " + getCodiceFiscale());
					transExec.rollBackTransaction();
					return false;
				}
			}

			caricaLavoratoreInserito(transExec);

			transExec.commitTransaction();
			setEsitoDatiAnagrafici("Anagrafica importata correttamente");
			setOperazioneInserimento(true);
			return true;
		} catch (Exception e) {
			try {
				transExec.rollBackTransaction();
			} catch (EMFInternalError e1) {
				it.eng.sil.util.TraceWrapper.error(_logger, "Errore inserimento lavoratore.", (Exception) e1);
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore inserimento lavoratore.", (Exception) e);
			setEsitoDatiAnagrafici("Errore importazione anagrafica");
			setMsgErrCode(e.getMessage());
			return false;
		}

	}

	public void calcolaStatoOccupazionale(MultipleTransactionQueryExecutor txExec, String dataCalcolo)
			throws Exception {
		SituazioneAmministrativaFactory.newInstance(getCdnLavoratore().toString(), dataCalcolo, txExec)
				.calcolaImpatti();
		getDid().aggiornaInfoDid(txExec, getCdnLavoratore());
	}

	public boolean aggiornaCompetenza(MultipleTransactionQueryExecutor transExec, String codCom) {
		String codCpiTit = null;
		String codCpiOrig = null;
		String codTipoCpi = null;
		try {
			BigDecimal userID = new BigDecimal(getOperatore().getCodut());
			SourceBean rowCpi = LavoratoreUtils.getInfoCpiProvinciaComune(transExec, codCom);
			if (rowCpi != null) {
				rowCpi = rowCpi.containsAttribute("ROW") ? (SourceBean) rowCpi.getAttribute("ROW") : rowCpi;
				String codCpiComune = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPI");
				codCpiComune = codCpiComune.trim();
				String codProvinciaCpi = SourceBeanUtils.getAttrStrNotNull(rowCpi, "PROVINCIACPI");
				String codProvinciaSil = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIASIL");
				String codProvinciaDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIA");
				if (codCpiComune.equalsIgnoreCase(CODIFICA_INESISTENTE)) {
					codCpiComune = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGO");
					codProvinciaCpi = codProvinciaDomicilio;
				}
				if (codProvinciaCpi.equalsIgnoreCase(codProvinciaSil)) {
					codTipoCpi = Properties.CODMONOTIPOCPI_COMP;
					codCpiTit = codCpiComune;
				} else {
					codTipoCpi = Properties.CODMONOTIPOCPI_TIT;
					codCpiTit = getOperatore().getCodRif();
					if (codCpiTit == null || codCpiTit.length() < 9) {
						codCpiTit = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGOGEN");
					}
					codCpiOrig = codCpiComune;
				}

				setCodCpi(codCpiTit);
				setCodMonoTipoCpi(codTipoCpi);
				// aggiornamento an_lav_storia_inf
				Object[] inputParameters = new Object[1];
				inputParameters[0] = getCdnLavoratore();
				SourceBean anLavStoriaInfSourceBean = ((SourceBean) transExec
						.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));

				anLavStoriaInfSourceBean = anLavStoriaInfSourceBean.containsAttribute("ROW")
						? (SourceBean) anLavStoriaInfSourceBean.getAttribute("ROW")
						: anLavStoriaInfSourceBean;

				BigDecimal prgLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean.getAttribute("PRGLAVSTORIAINF");
				BigDecimal numkloLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean
						.getAttribute("NUMKLOLAVSTORIAINF");
				String strNote = anLavStoriaInfSourceBean.containsAttribute("STRNOTE")
						? anLavStoriaInfSourceBean.getAttribute("STRNOTE").toString()
						: "";
				if (strNote.equals("")) {
					strNote = "Aggiornamento da SAP Ministeriale";
				} else {
					strNote = strNote + " Aggiornamento da SAP Ministeriale";
					if (strNote.length() > 1000) {
						strNote = strNote.substring(0, 1000);
					}
				}

				inputParameters = new Object[8];
				inputParameters[0] = codCpiTit;
				inputParameters[1] = userID;
				inputParameters[2] = userID;
				inputParameters[3] = codTipoCpi;
				inputParameters[4] = codCpiOrig;
				inputParameters[5] = numkloLavStoriaInf.add(new BigDecimal("1"));
				inputParameters[6] = strNote;
				inputParameters[7] = prgLavStoriaInf;

				boolean success = ((Boolean) transExec.executeQuery("UPDATE_AN_LAV_STORIA_INF_FROM_IMPORTA_SAP",
						inputParameters, "UPDATE")).booleanValue();

				if (!success) {
					_logger.warn("Errore durante aggiornamento tabella an_lav_storia_inf: " + getCodiceFiscale());
					return false;
				}
			} else {
				throw new Exception("Errore recupero cpi competente");
			}
			return true;
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Aggiornamento competenza amministrativa.", (Exception) ex);
			return false;
		}
	}

	public boolean gestisciDatiAmministrativiDID(MultipleTransactionQueryExecutor transExec,
			RequestContainer requestContainer, SourceBean request, SourceBean response, String dataNormativa297) {
		String dataDid = null;
		BigDecimal prgDid = null;
		boolean successDid = true;
		try {
			BigDecimal userid = new BigDecimal(getOperatore().getCodut());
			// gestione dati amministrativi/DID
			Statoinanagrafe sta = getLavoratoreSapType().getDatiamministrativi().getStatoinanagrafe();
			if (sta != null && sta.getDisponibilita() != null) {
				dataDid = DateUtils.formatXMLGregorian(sta.getDisponibilita());
				DidBean did = getDid();
				if (did != null) {
					prgDid = did.getPrgDichDisponibilita();
					if (prgDid == null) {
						if (DateUtils.compare(dataDid, dataNormativa297) < 0) {
							setFlgDidPresente("N");
							setEsitoDatiAmministrativi(
									"Impossibile inserire la dichiarazione di immediata disponibilità: la data di dichiarazione risulta essere precedente al "
											+ dataNormativa297);
							return false;
						}
						setFlgDidPresente("N");
						try {
							transExec.initTransaction();

							int resultAutDid = did.gestisciAutomatismiDID(getCdnLavoratore(), dataDid, userid,
									transExec);

							if (resultAutDid <= 0) {

								successDid = did.inserisciDid(request, response, getCdnLavoratore().toString(), dataDid,
										getElencoAnagrafico().getPrgElencoAnagrafico(), getCodCpi(), userid,
										requestContainer, transExec);

								if (successDid) {
									if (getOperazioneInserimento()
											&& getCodMonoTipoCpi().equalsIgnoreCase(Properties.CODMONOTIPOCPI_TIT)) {
										successDid = aggiornaCompetenza(transExec, getLavoratoreSapType()
												.getDatianagrafici().getDomicilio().getCodcomune());
										if (!successDid) {
											transExec.rollBackTransaction();
											return false;
										}
									}
								} else {
									transExec.rollBackTransaction();
									return false;
								}

								if (successDid) {
									calcolaStatoOccupazionale(transExec, did.getDatDichiarazione());
									transExec.commitTransaction();
									setEsitoDatiAmministrativi(
											"Dichiarazione immediata disponibilità importata correttamente");
									return true;
								} else {
									transExec.rollBackTransaction();
									return false;
								}
							} else {
								// Effettuata in automatico la riapertura did annullata e la conseguente riapertura del
								// patto
								if (request.containsAttribute("cdnLavoratore")) {
									request.updAttribute("cdnLavoratore", getCdnLavoratore().toString());
								} else {
									request.setAttribute("cdnLavoratore", getCdnLavoratore().toString());
								}
								if (request.containsAttribute("FORZA_INSERIMENTO")) {
									request.updAttribute("FORZA_INSERIMENTO", "true");
								} else {
									request.setAttribute("FORZA_INSERIMENTO", "true");
								}
								if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
									request.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
								} else {
									request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
								}
								if (request.containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
									request.updAttribute("FORZA_CHIUSURA_MOBILITA", "true");
								} else {
									request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
								}
								requestContainer.setServiceRequest(request);
								calcolaStatoOccupazionale(transExec, dataDid);
								transExec.commitTransaction();
								return true;
							}
						} catch (Exception e) {
							transExec.rollBackTransaction();
							it.eng.sil.util.TraceWrapper.error(_logger,
									"Errore in inserimento dichiarazione immediata disponibiltà dalla SAP.",
									(Exception) e);
							return false;
						}
					} else {
						setFlgDidPresente("S");
						setEsitoDatiAmministrativi("Per il lavoratore è già presente una DID valida");
					}
				}
			} else {
				setEsitoDatiAmministrativi("Data dichiarazione disponibilità non presente");
			}

			return true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore gestione esperienze lavorative dalla SAP.",
					(Exception) e);
			setMsgErrCode(e.getMessage());
			return false;
		}

	}

	public boolean gestisciDatiAmministrativiDID2(MultipleTransactionQueryExecutor transExec,
			RequestContainer requestContainer, SourceBean request, SourceBean response, String dataNormativa297) {
		String dataDid = null;
		BigDecimal prgDid = null;
		boolean successDid = true;
		try {
			BigDecimal userid = new BigDecimal(getOperatore().getCodut());
			// gestione dati amministrativi/DID
			it.eng.sil.pojo.yg.sap.due.Statoinanagrafe sta = getLavoratoreSapDueType().getDatiamministrativi()
					.getStatoinanagrafe();
			if (sta != null && sta.getDisponibilita() != null) {
				dataDid = DateUtils.formatXMLGregorian(sta.getDisponibilita());
				DidBean did = getDid();
				if (did != null) {
					prgDid = did.getPrgDichDisponibilita();
					if (prgDid == null) {
						if (DateUtils.compare(dataDid, dataNormativa297) < 0) {
							setFlgDidPresente("N");
							setEsitoDatiAmministrativi(
									"Impossibile inserire la dichiarazione di immediata disponibilità: la data di dichiarazione risulta essere precedente al "
											+ dataNormativa297);
							return false;
						}
						setFlgDidPresente("N");
						try {
							transExec.initTransaction();

							int resultAutDid = did.gestisciAutomatismiDID(getCdnLavoratore(), dataDid, userid,
									transExec);

							if (resultAutDid <= 0) {

								successDid = did.inserisciDid(request, response, getCdnLavoratore().toString(), dataDid,
										getElencoAnagrafico().getPrgElencoAnagrafico(), getCodCpi(), userid,
										requestContainer, transExec);

								if (successDid) {
									if (getOperazioneInserimento()
											&& getCodMonoTipoCpi().equalsIgnoreCase(Properties.CODMONOTIPOCPI_TIT)) {
										successDid = aggiornaCompetenza(transExec, getLavoratoreSapDueType()
												.getDatianagrafici().getDomicilio().getCodcomune());
										if (!successDid) {
											transExec.rollBackTransaction();
											return false;
										}
									}
								} else {
									transExec.rollBackTransaction();
									return false;
								}

								if (successDid) {
									calcolaStatoOccupazionale(transExec, did.getDatDichiarazione());
									transExec.commitTransaction();
									setEsitoDatiAmministrativi(
											"Dichiarazione immediata disponibilità importata correttamente");
									return true;
								} else {
									transExec.rollBackTransaction();
									return false;
								}
							} else {
								// Effettuata in automatico la riapertura did annullata e la conseguente riapertura del
								// patto
								if (request.containsAttribute("cdnLavoratore")) {
									request.updAttribute("cdnLavoratore", getCdnLavoratore().toString());
								} else {
									request.setAttribute("cdnLavoratore", getCdnLavoratore().toString());
								}
								if (request.containsAttribute("FORZA_INSERIMENTO")) {
									request.updAttribute("FORZA_INSERIMENTO", "true");
								} else {
									request.setAttribute("FORZA_INSERIMENTO", "true");
								}
								if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
									request.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
								} else {
									request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
								}
								if (request.containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
									request.updAttribute("FORZA_CHIUSURA_MOBILITA", "true");
								} else {
									request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
								}
								requestContainer.setServiceRequest(request);
								calcolaStatoOccupazionale(transExec, dataDid);
								transExec.commitTransaction();
								return true;
							}
						} catch (Exception e) {
							transExec.rollBackTransaction();
							it.eng.sil.util.TraceWrapper.error(_logger,
									"Errore in inserimento dichiarazione immediata disponibiltà dalla SAP.",
									(Exception) e);
							return false;
						}
					} else {
						setFlgDidPresente("S");
						setEsitoDatiAmministrativi("Per il lavoratore è già presente una DID valida");
					}
				}
			} else {
				setEsitoDatiAmministrativi("Data dichiarazione disponibilità non presente");
			}

			return true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore gestione esperienze lavorative dalla SAP.",
					(Exception) e);
			setMsgErrCode(e.getMessage());
			return false;
		}

	}

	public boolean gestisciEsperienzeLavoro(MultipleTransactionQueryExecutor transExec) {
		int numEspImportate = 0;
		try {
			BigDecimal userid = new BigDecimal(getOperatore().getCodut());
			EsperienzelavoroLst espLavorative = getLavoratoreSapType().getEsperienzelavoroLst();
			if (espLavorative != null) {
				List listeEspLav = espLavorative.getEsperienzalavoro();
				if (!listeEspLav.isEmpty()) {
					setNumEsperienzeLavoro(listeEspLav.size());
					for (int j = 0; j < listeEspLav.size(); j++) {
						Esperienzalavoro item = (Esperienzalavoro) listeEspLav.get(j);
						try {
							BigDecimal prgMansione = null;
							String codMansioneSil = null;
							String flgEspFormazione = null;
							Object[] params = new Object[1];
							params[0] = item.getCodprofessione();

							SourceBean rowMans = (SourceBean) transExec.executeQuery("GET_CODMANSIONE", params,
									"SELECT");
							if (rowMans != null) {
								rowMans = (rowMans.containsAttribute("ROW") ? (SourceBean) rowMans.getAttribute("ROW")
										: rowMans);
								codMansioneSil = (String) rowMans.getAttribute("RESULT");
								if (codMansioneSil != null) {
									params = new Object[2];
									params[0] = getCdnLavoratore();
									params[1] = codMansioneSil;

									SourceBean mansioneSourceBean = (SourceBean) transExec.executeQuery(
											"SELECT_PRGMANSIONE_DA_CODMANSIONE_E_LAVORATORE", params, "SELECT");
									if (mansioneSourceBean != null) {
										mansioneSourceBean = (mansioneSourceBean.containsAttribute("ROW")
												? (SourceBean) mansioneSourceBean.getAttribute("ROW")
												: mansioneSourceBean);
										prgMansione = (BigDecimal) mansioneSourceBean.getAttribute("PRGMANSIONE");
										if (prgMansione == null) {
											if (item.getDataFinePeriodoFormativo() != null) {
												flgEspFormazione = "S";
											} else {
												flgEspFormazione = "N";
											}
											try {
												transExec.initTransaction();
												SourceBean rowSequence = (SourceBean) transExec
														.executeQuery("SELECT_MANSIONE_SEQUENCE", null, "SELECT");
												rowSequence = (rowSequence.containsAttribute("ROW")
														? (SourceBean) rowSequence.getAttribute("ROW")
														: rowSequence);
												BigDecimal prgMansioneNew = (BigDecimal) rowSequence
														.getAttribute("PRGMANSIONE");

												params = new Object[] { prgMansioneNew, getCdnLavoratore(),
														codMansioneSil, "S", flgEspFormazione, "P", null, null, userid,
														userid };
												Boolean res = (Boolean) transExec.executeQuery(
														"INSERT_SAP_MANSIONE_FROM_IMPORTA", params, "INSERT");

												if (res != null && res.booleanValue()) {
													Boolean resEsp = inserisciEspLavorative(prgMansioneNew, item,
															transExec);

													if (resEsp != null && resEsp.booleanValue()) {
														transExec.commitTransaction();
														numEspImportate = numEspImportate + 1;
														setNumEsperienzeLavoroImportate(numEspImportate);
													} else {
														transExec.rollBackTransaction();
													}

												} else {
													transExec.rollBackTransaction();
												}
											} catch (Exception ex) {
												transExec.rollBackTransaction();
												it.eng.sil.util.TraceWrapper.error(_logger,
														"Errore in inserimento esperienze lavorative dalla SAP.",
														(Exception) ex);
											}
										} else {
											String dataInizio = null;
											Object[] paramsEsp = new Object[4];
											paramsEsp[0] = getCdnLavoratore();
											paramsEsp[1] = prgMansione;
											if (item.getDatainizio() != null) {
												dataInizio = DateUtils.formatXMLGregorian(item.getDatainizio());
												paramsEsp[2] = new BigDecimal(
														Integer.parseInt(dataInizio.substring(6, 10), 10)).toString();
												paramsEsp[3] = new BigDecimal(
														Integer.parseInt(dataInizio.substring(3, 5), 10)).toString();

											}
											SourceBean rowEspLav = (SourceBean) transExec.executeQuery(
													"GET_ESP_LAVORO_FROM_IMPORTA_SAP", paramsEsp, "SELECT");
											if (rowEspLav != null) {
												rowEspLav = (rowEspLav.containsAttribute("ROW")
														? (SourceBean) rowEspLav.getAttribute("ROW")
														: rowEspLav);
												int numEsperienze = new Integer(
														rowEspLav.getAttribute("NUMESPERIENZE").toString()).intValue();
												if (numEsperienze == 0) {
													try {
														transExec.initTransaction();
														Boolean resEsp = inserisciEspLavorative(prgMansione, item,
																transExec);
														if (resEsp != null && resEsp.booleanValue()) {
															transExec.commitTransaction();
															numEspImportate = numEspImportate + 1;
															setNumEsperienzeLavoroImportate(numEspImportate);
														} else {
															transExec.rollBackTransaction();
														}
													} catch (Exception ex) {
														transExec.rollBackTransaction();
														it.eng.sil.util.TraceWrapper.error(_logger,
																"Errore in inserimento esperienze lavorative dalla SAP.",
																(Exception) ex);
													}
												}
											}
										}
									}
								}
							}
						} catch (Exception eM) {
							it.eng.sil.util.TraceWrapper.error(_logger,
									"Errore recupero mansione dalla SAP:" + item.getCodprofessione(), (Exception) eM);
						}
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore gestione esperienze lavorative dalla SAP.",
					(Exception) e);
			return false;
		}
		return true;
	}

	public boolean gestisciEsperienzeLavoro2(MultipleTransactionQueryExecutor transExec) {
		int numEspImportate = 0;
		try {
			BigDecimal userid = new BigDecimal(getOperatore().getCodut());
			it.eng.sil.pojo.yg.sap.due.EsperienzelavoroLst espLavorative = getLavoratoreSapDueType()
					.getEsperienzelavoroLst();
			if (espLavorative != null) {
				List listeEspLav = espLavorative.getEsperienzalavoro();
				if (!listeEspLav.isEmpty()) {
					setNumEsperienzeLavoro(listeEspLav.size());
					for (int j = 0; j < listeEspLav.size(); j++) {
						it.eng.sil.pojo.yg.sap.due.Esperienzalavoro item = (it.eng.sil.pojo.yg.sap.due.Esperienzalavoro) listeEspLav
								.get(j);
						try {
							BigDecimal prgMansione = null;
							String codMansioneSil = null;
							String flgEspFormazione = null;
							Object[] params = new Object[1];
							params[0] = item.getCodprofessione();

							SourceBean rowMans = (SourceBean) transExec.executeQuery("GET_CODMANSIONE", params,
									"SELECT");
							if (rowMans != null) {
								rowMans = (rowMans.containsAttribute("ROW") ? (SourceBean) rowMans.getAttribute("ROW")
										: rowMans);
								codMansioneSil = (String) rowMans.getAttribute("RESULT");
								if (codMansioneSil != null) {
									params = new Object[2];
									params[0] = getCdnLavoratore();
									params[1] = codMansioneSil;

									SourceBean mansioneSourceBean = (SourceBean) transExec.executeQuery(
											"SELECT_PRGMANSIONE_DA_CODMANSIONE_E_LAVORATORE", params, "SELECT");
									if (mansioneSourceBean != null) {
										mansioneSourceBean = (mansioneSourceBean.containsAttribute("ROW")
												? (SourceBean) mansioneSourceBean.getAttribute("ROW")
												: mansioneSourceBean);
										prgMansione = (BigDecimal) mansioneSourceBean.getAttribute("PRGMANSIONE");
										if (prgMansione == null) {
											if (item.getDataFinePeriodoFormativo() != null) {
												flgEspFormazione = "S";
											} else {
												flgEspFormazione = "N";
											}
											try {
												transExec.initTransaction();
												SourceBean rowSequence = (SourceBean) transExec
														.executeQuery("SELECT_MANSIONE_SEQUENCE", null, "SELECT");
												rowSequence = (rowSequence.containsAttribute("ROW")
														? (SourceBean) rowSequence.getAttribute("ROW")
														: rowSequence);
												BigDecimal prgMansioneNew = (BigDecimal) rowSequence
														.getAttribute("PRGMANSIONE");

												params = new Object[] { prgMansioneNew, getCdnLavoratore(),
														codMansioneSil, "S", flgEspFormazione, "P", null, null, userid,
														userid };
												Boolean res = (Boolean) transExec.executeQuery(
														"INSERT_SAP_MANSIONE_FROM_IMPORTA", params, "INSERT");

												if (res != null && res.booleanValue()) {
													Boolean resEsp = inserisciEspLavorative2(prgMansioneNew, item,
															transExec);

													if (resEsp != null && resEsp.booleanValue()) {
														transExec.commitTransaction();
														numEspImportate = numEspImportate + 1;
														setNumEsperienzeLavoroImportate(numEspImportate);
													} else {
														transExec.rollBackTransaction();
													}

												} else {
													transExec.rollBackTransaction();
												}
											} catch (Exception ex) {
												transExec.rollBackTransaction();
												it.eng.sil.util.TraceWrapper.error(_logger,
														"Errore in inserimento esperienze lavorative dalla SAP.",
														(Exception) ex);
											}
										} else {
											String dataInizio = null;
											Object[] paramsEsp = new Object[4];
											paramsEsp[0] = getCdnLavoratore();
											paramsEsp[1] = prgMansione;
											if (item.getDatainizio() != null) {
												dataInizio = DateUtils.formatXMLGregorian(item.getDatainizio());
												paramsEsp[2] = new BigDecimal(
														Integer.parseInt(dataInizio.substring(6, 10), 10)).toString();
												paramsEsp[3] = new BigDecimal(
														Integer.parseInt(dataInizio.substring(3, 5), 10)).toString();

											}
											SourceBean rowEspLav = (SourceBean) transExec.executeQuery(
													"GET_ESP_LAVORO_FROM_IMPORTA_SAP", paramsEsp, "SELECT");
											if (rowEspLav != null) {
												rowEspLav = (rowEspLav.containsAttribute("ROW")
														? (SourceBean) rowEspLav.getAttribute("ROW")
														: rowEspLav);
												int numEsperienze = new Integer(
														rowEspLav.getAttribute("NUMESPERIENZE").toString()).intValue();
												if (numEsperienze == 0) {
													try {
														transExec.initTransaction();
														Boolean resEsp = inserisciEspLavorative2(prgMansione, item,
																transExec);
														if (resEsp != null && resEsp.booleanValue()) {
															transExec.commitTransaction();
															numEspImportate = numEspImportate + 1;
															setNumEsperienzeLavoroImportate(numEspImportate);
														} else {
															transExec.rollBackTransaction();
														}
													} catch (Exception ex) {
														transExec.rollBackTransaction();
														it.eng.sil.util.TraceWrapper.error(_logger,
																"Errore in inserimento esperienze lavorative dalla SAP.",
																(Exception) ex);
													}
												}
											}
										}
									}
								}
							}
						} catch (Exception eM) {
							it.eng.sil.util.TraceWrapper.error(_logger,
									"Errore recupero mansione dalla SAP:" + item.getCodprofessione(), (Exception) eM);
						}
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore gestione esperienze lavorative dalla SAP.",
					(Exception) e);
			return false;
		}
		return true;
	}

	protected Boolean inserisciEspLavorative(BigDecimal prgMansioneNew, Esperienzalavoro item,
			MultipleTransactionQueryExecutor transExec) throws Exception {
		String codContratto = null;
		String codAteco = null;
		if (item.getTiporapporto() != null) {
			String codTipoContratto = item.getTiporapporto().getTipocontratto();
			if (codTipoContratto != null) {
				Object[] paramsC = new Object[1];
				paramsC[0] = codTipoContratto;
				SourceBean rowC = (SourceBean) transExec.executeQuery("RECUPERO_COD_MONOTIPOASS", paramsC, "SELECT");
				if (rowC != null) {
					rowC = (rowC.containsAttribute("ROW") ? (SourceBean) rowC.getAttribute("ROW") : rowC);
					codContratto = (String) rowC.getAttribute("CODCONTRATTO");
				}
			}
		}
		if (item.getAzienda() != null) {
			String codAtecoDot = item.getAzienda().getCodateco();
			if (codAtecoDot != null) {
				Object[] paramsAt = new Object[1];
				paramsAt[0] = codAtecoDot;
				SourceBean rowAt = (SourceBean) transExec.executeQuery("SELECT_CODATECO_DA_CODATECODOT", paramsAt,
						"SELECT");
				if (rowAt != null) {
					rowAt = (rowAt.containsAttribute("ROW") ? (SourceBean) rowAt.getAttribute("ROW") : rowAt);
					codAteco = (String) rowAt.getAttribute("CODATECO");
				}
			}
		}
		String descAttivita = item.getCompiti();
		String meseInizio = null;
		String annoInizio = null;
		String meseFine = null;
		String annoFine = null;
		String numMesi = null;
		String dataInizio = null;
		String dataFine = null;
		String codFiscaleAzienda = null;
		String ragioneSociale = null;
		String codComAzienda = null;
		String indirizzoAzienda = null;
		if (item.getDatainizio() != null) {
			dataInizio = DateUtils.formatXMLGregorian(item.getDatainizio());
			meseInizio = new BigDecimal(Integer.parseInt(dataInizio.substring(3, 5), 10)).toString();
			annoInizio = new BigDecimal(Integer.parseInt(dataInizio.substring(6, 10), 10)).toString();
		}
		if (item.getDatafine() != null) {
			dataFine = DateUtils.formatXMLGregorian(item.getDatafine());
			meseFine = new BigDecimal(Integer.parseInt(dataFine.substring(3, 5), 10)).toString();
			annoFine = new BigDecimal(Integer.parseInt(dataFine.substring(6, 10), 10)).toString();
		}
		if (dataInizio != null && dataFine != null) {
			numMesi = new Integer(DateUtils.monthsBetween(dataInizio, dataFine)).toString();
			;
		}
		if (item.getUtilizzatrice() != null) {
			codFiscaleAzienda = item.getUtilizzatrice().getCodicefiscale();
			ragioneSociale = item.getUtilizzatrice().getDatorelavoro();
		} else {
			if (item.getAzienda() != null) {
				codFiscaleAzienda = item.getAzienda().getCodicefiscale();
				ragioneSociale = item.getAzienda().getDatorelavoro();
			}
		}
		if (item.getLuogolavoro() != null) {
			codComAzienda = item.getLuogolavoro().getCodcomune();
			indirizzoAzienda = item.getLuogolavoro().getIndirizzo();
			if (indirizzoAzienda.length() > 60) {
				indirizzoAzienda = indirizzoAzienda.substring(0, 60);
			}
		}

		BigDecimal userid = new BigDecimal(getOperatore().getCodut());

		Object[] params = new Object[] { prgMansioneNew, descAttivita, codContratto, ragioneSociale, codFiscaleAzienda,
				codComAzienda, indirizzoAzienda, codAteco, meseInizio, annoInizio, meseFine, annoFine, numMesi, null,
				userid, userid };

		Boolean resEsp = (Boolean) transExec.executeQuery("INSERT_SAP_ESP_LAVORO_FROM_IMPORTA", params, "INSERT");

		return resEsp;
	}

	protected Boolean inserisciEspLavorative2(BigDecimal prgMansioneNew,
			it.eng.sil.pojo.yg.sap.due.Esperienzalavoro item, MultipleTransactionQueryExecutor transExec)
			throws Exception {
		String codContratto = null;
		String codAteco = null;
		if (item.getTiporapporto() != null) {
			String codTipoContratto = item.getTiporapporto().getTipocontratto();
			if (codTipoContratto != null) {
				Object[] paramsC = new Object[1];
				paramsC[0] = codTipoContratto;
				SourceBean rowC = (SourceBean) transExec.executeQuery("RECUPERO_COD_MONOTIPOASS", paramsC, "SELECT");
				if (rowC != null) {
					rowC = (rowC.containsAttribute("ROW") ? (SourceBean) rowC.getAttribute("ROW") : rowC);
					codContratto = (String) rowC.getAttribute("CODCONTRATTO");
				}
			}
		}
		if (item.getAzienda() != null) {
			String codAtecoDot = item.getAzienda().getCodateco();
			if (codAtecoDot != null) {
				Object[] paramsAt = new Object[1];
				paramsAt[0] = codAtecoDot;
				SourceBean rowAt = (SourceBean) transExec.executeQuery("SELECT_CODATECO_DA_CODATECODOT", paramsAt,
						"SELECT");
				if (rowAt != null) {
					rowAt = (rowAt.containsAttribute("ROW") ? (SourceBean) rowAt.getAttribute("ROW") : rowAt);
					codAteco = (String) rowAt.getAttribute("CODATECO");
				}
			}
		}
		String descAttivita = item.getCompiti();
		String meseInizio = null;
		String annoInizio = null;
		String meseFine = null;
		String annoFine = null;
		String numMesi = null;
		String dataInizio = null;
		String dataFine = null;
		String codFiscaleAzienda = null;
		String ragioneSociale = null;
		String codComAzienda = null;
		String indirizzoAzienda = null;
		if (item.getDatainizio() != null) {
			dataInizio = DateUtils.formatXMLGregorian(item.getDatainizio());
			meseInizio = new BigDecimal(Integer.parseInt(dataInizio.substring(3, 5), 10)).toString();
			annoInizio = new BigDecimal(Integer.parseInt(dataInizio.substring(6, 10), 10)).toString();
		}
		if (item.getDatafine() != null) {
			dataFine = DateUtils.formatXMLGregorian(item.getDatafine());
			meseFine = new BigDecimal(Integer.parseInt(dataFine.substring(3, 5), 10)).toString();
			annoFine = new BigDecimal(Integer.parseInt(dataFine.substring(6, 10), 10)).toString();
		}
		if (dataInizio != null && dataFine != null) {
			numMesi = new Integer(DateUtils.monthsBetween(dataInizio, dataFine)).toString();
			;
		}
		if (item.getUtilizzatrice() != null) {
			codFiscaleAzienda = item.getUtilizzatrice().getCodicefiscale();
			ragioneSociale = item.getUtilizzatrice().getDatorelavoro();
		} else {
			if (item.getAzienda() != null) {
				codFiscaleAzienda = item.getAzienda().getCodicefiscale();
				ragioneSociale = item.getAzienda().getDatorelavoro();
			}
		}
		if (item.getLuogolavoro() != null) {
			codComAzienda = item.getLuogolavoro().getCodcomune();
			indirizzoAzienda = item.getLuogolavoro().getIndirizzo();
			if (indirizzoAzienda.length() > 60) {
				indirizzoAzienda = indirizzoAzienda.substring(0, 60);
			}
		}

		BigDecimal userid = new BigDecimal(getOperatore().getCodut());

		Object[] params = new Object[] { prgMansioneNew, descAttivita, codContratto, ragioneSociale, codFiscaleAzienda,
				codComAzienda, indirizzoAzienda, codAteco, meseInizio, annoInizio, meseFine, annoFine, numMesi, null,
				userid, userid };

		Boolean resEsp = (Boolean) transExec.executeQuery("INSERT_SAP_ESP_LAVORO_FROM_IMPORTA", params, "INSERT");

		return resEsp;
	}

	public boolean gestisciPoliticheAttive(MultipleTransactionQueryExecutor transExec) {
		try {
			boolean isTransazioneAttiva = false;
			boolean operatoreCollegato = false;
			BigDecimal prgSpiCollegato = null;
			BigDecimal userid = new BigDecimal(getOperatore().getCodut());
			List<PoliticheAttive> listaP = null;
			List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaP2 = null;
			if (isGestioneSap2()) {
				PoliticheAttiveLst allListaP = getLavoratoreSapDueType().getPoliticheAttiveLst();
				if (allListaP != null) {
					listaP2 = allListaP.getPoliticheAttive();
				} else {
					listaP2 = new ArrayList<it.eng.sil.pojo.yg.sap.due.PoliticheAttive>();
				}
			} else {
				listaP = getLavoratoreSapType().getPoliticheAttive();
			}

			int politicheAttiveCompetenza = 0;
			int colloquiInseriti = 0;
			int politicheInserite = 0;
			int politicheNonImportate = 0;
			int politicheNonMappate = 0;
			if (!isGestioneSap2() && listaP.size() > 0) {
				for (int j = 0; j < listaP.size(); j++) {
					PoliticheAttive item = null;
					isTransazioneAttiva = false;
					try {
						item = (PoliticheAttive) listaP.get(j);
						String cpiSil = cpiPoliticaAttivaDiCompetenza(item, null, transExec);
						if (cpiSil != null && !cpiSil.equals("")) {
							politicheAttiveCompetenza = politicheAttiveCompetenza + 1;
							String dataColloquio = DateUtils.formatXMLGregorian(item.getDataProposta());
							String dataFine = DateUtils.formatXMLGregorian(item.getDataFine());
							String data = DateUtils.formatXMLGregorian(item.getData());
							String tipoAttivita = item.getTipoAttivita();
							BigDecimal prgColloquio = cercaColloquio(dataColloquio, tipoAttivita, transExec);
							if (prgColloquio == null) {
								// inserimento colloquio con servizio definito nella configurazione SAP_IMP
								UtilsConfig utility = new UtilsConfig("SAP_IMP");
								String codServizioConfigurazione = utility.getValoreConfigurazione();
								transExec.initTransaction();
								isTransazioneAttiva = true;
								if (!operatoreCollegato) {
									prgSpiCollegato = getOperatoreCollegato(userid, transExec);
									operatoreCollegato = true;
								}
								String noteColloquio = item.getDescrizione();
								if (noteColloquio != null && noteColloquio.length() > 2000) {
									noteColloquio = noteColloquio.substring(0, 2000);
								}
								prgColloquio = getProgressivoColloquio(transExec);
								boolean inserimentoColloquio = inserisciColloquio(prgColloquio, getCdnLavoratore(),
										codServizioConfigurazione, dataColloquio, cpiSil, userid, prgSpiCollegato,
										noteColloquio, transExec);
								if (!inserimentoColloquio) {
									throw new Exception(
											"impossibile inserire il colloquio per importare la politica attiva"
													+ item.getTipoAttivita());
								}
								inserimentoColloquio = inserisciSchedaColloquio(prgColloquio, transExec);
								if (!inserimentoColloquio) {
									throw new Exception(
											"impossibile inserire il colloquio per importare la politica attiva"
													+ item.getTipoAttivita());
								}
								colloquiInseriti = colloquiInseriti + 1;
							}

							String tipoProgetto = item.getTitoloProgetto();
							BigDecimal prgAzioneColloquio = null;
							try {
								prgAzioneColloquio = cercaAzione(dataColloquio, tipoAttivita, tipoProgetto, transExec);
							} catch (Exception eAzione) {
								politicheNonImportate = politicheNonImportate + 1;
								it.eng.sil.util.TraceWrapper.error(_logger,
										"Errore mappatura politica attiva: " + item.getTipoAttivita()
												+ " titolo progetto: " + item.getTitoloProgetto()
												+ " con data proposta:" + dataColloquio,
										(Exception) eAzione);
								throw new Exception("Errore mappatura politica attiva");
							}

							if (prgAzioneColloquio != null) {
								BigDecimal prgPercorso = cercaPercorso(prgColloquio, prgAzioneColloquio, transExec);
								if (prgPercorso == null) {
									if (!isTransazioneAttiva) {
										transExec.initTransaction();
										isTransazioneAttiva = true;
									}
									if (!operatoreCollegato) {
										prgSpiCollegato = getOperatoreCollegato(userid, transExec);
										operatoreCollegato = true;
									}
									prgPercorso = getProgressivoPercorso(transExec);
									String[] esito = esitoPercorso(dataColloquio, data, dataFine);

									boolean inserimentoPercorso = inserisciPercorso(prgPercorso, prgColloquio, esito[0],
											esito[1], prgAzioneColloquio, item, null, userid, prgSpiCollegato,
											transExec);
									if (!inserimentoPercorso) {
										throw new Exception(
												"impossibile inserire il percorso per importare la politica attiva"
														+ item.getTipoAttivita());
									}
									politicheInserite = politicheInserite + 1;
								} else {
									politicheNonImportate = politicheNonImportate + 1;
								}
							} else {
								politicheNonMappate = politicheNonMappate + 1;
							}

							if (isTransazioneAttiva) {
								transExec.commitTransaction();
							}
						}
					} catch (Exception ex) {
						if (isTransazioneAttiva) {
							transExec.rollBackTransaction();
						}
						it.eng.sil.util.TraceWrapper.error(_logger,
								"Errore gestione politica attiva " + item.getDescrizione(), (Exception) ex);
					}
				}
				setNumPoliticheCompetenza(politicheAttiveCompetenza);
				setNumColloquiInseriti(colloquiInseriti);
				setNumPoliticheImportate(politicheInserite);
				setNumPoliticheNonImportate(politicheNonImportate);
				setNumPoliticheNonMappate(politicheNonMappate);
			} else if (isGestioneSap2() && listaP2.size() > 0) {
				for (int j = 0; j < listaP2.size(); j++) {
					it.eng.sil.pojo.yg.sap.due.PoliticheAttive item = null;
					isTransazioneAttiva = false;
					try {
						item = (it.eng.sil.pojo.yg.sap.due.PoliticheAttive) listaP2.get(j);
						String cpiSil = cpiPoliticaAttivaDiCompetenza(null, item, transExec);
						if (cpiSil != null && !cpiSil.equals("")) {
							politicheAttiveCompetenza = politicheAttiveCompetenza + 1;
							String dataColloquio = DateUtils.formatXMLGregorian(item.getDataProposta());
							String dataFine = DateUtils.formatXMLGregorian(item.getDataFine());
							String data = DateUtils.formatXMLGregorian(item.getData());
							String tipoAttivita = item.getTipoAttivita();
							BigDecimal prgColloquio = cercaColloquio(dataColloquio, tipoAttivita, transExec);
							if (prgColloquio == null) {
								// inserimento colloquio con servizio definito nella configurazione SAP_IMP
								UtilsConfig utility = new UtilsConfig("SAP_IMP");
								String codServizioConfigurazione = utility.getValoreConfigurazione();
								transExec.initTransaction();
								isTransazioneAttiva = true;
								if (!operatoreCollegato) {
									prgSpiCollegato = getOperatoreCollegato(userid, transExec);
									operatoreCollegato = true;
								}
								String noteColloquio = item.getDescrizione();
								if (noteColloquio != null && noteColloquio.length() > 2000) {
									noteColloquio = noteColloquio.substring(0, 2000);
								}
								prgColloquio = getProgressivoColloquio(transExec);
								boolean inserimentoColloquio = inserisciColloquio(prgColloquio, getCdnLavoratore(),
										codServizioConfigurazione, dataColloquio, cpiSil, userid, prgSpiCollegato,
										noteColloquio, transExec);
								if (!inserimentoColloquio) {
									throw new Exception(
											"impossibile inserire il colloquio per importare la politica attiva"
													+ item.getTipoAttivita());
								}
								inserimentoColloquio = inserisciSchedaColloquio(prgColloquio, transExec);
								if (!inserimentoColloquio) {
									throw new Exception(
											"impossibile inserire il colloquio per importare la politica attiva"
													+ item.getTipoAttivita());
								}
								colloquiInseriti = colloquiInseriti + 1;
							}

							String tipoProgetto = item.getTitoloProgetto();
							BigDecimal prgAzioneColloquio = null;
							try {
								prgAzioneColloquio = cercaAzione(dataColloquio, tipoAttivita, tipoProgetto, transExec);
							} catch (Exception eAzione) {
								politicheNonImportate = politicheNonImportate + 1;
								it.eng.sil.util.TraceWrapper.error(_logger,
										"Errore mappatura politica attiva: " + item.getTipoAttivita()
												+ " titolo progetto: " + item.getTitoloProgetto()
												+ " con data proposta:" + dataColloquio,
										(Exception) eAzione);
								throw new Exception("Errore mappatura politica attiva");
							}

							if (prgAzioneColloquio != null) {
								BigDecimal prgPercorso = cercaPercorso(prgColloquio, prgAzioneColloquio, transExec);
								if (prgPercorso == null) {
									if (!isTransazioneAttiva) {
										transExec.initTransaction();
										isTransazioneAttiva = true;
									}
									if (!operatoreCollegato) {
										prgSpiCollegato = getOperatoreCollegato(userid, transExec);
										operatoreCollegato = true;
									}
									prgPercorso = getProgressivoPercorso(transExec);
									String[] esito = esitoPercorso(dataColloquio, data, dataFine);

									boolean inserimentoPercorso = inserisciPercorso(prgPercorso, prgColloquio, esito[0],
											esito[1], prgAzioneColloquio, null, item, userid, prgSpiCollegato,
											transExec);
									if (!inserimentoPercorso) {
										throw new Exception(
												"impossibile inserire il percorso per importare la politica attiva"
														+ item.getTipoAttivita());
									}
									politicheInserite = politicheInserite + 1;
								} else {
									politicheNonImportate = politicheNonImportate + 1;
								}
							} else {
								politicheNonMappate = politicheNonMappate + 1;
							}

							if (isTransazioneAttiva) {
								transExec.commitTransaction();
							}
						}
					} catch (Exception ex) {
						if (isTransazioneAttiva) {
							transExec.rollBackTransaction();
						}
						it.eng.sil.util.TraceWrapper.error(_logger,
								"Errore gestione politica attiva " + item.getDescrizione(), (Exception) ex);
					}
				}
				setNumPoliticheCompetenza(politicheAttiveCompetenza);
				setNumColloquiInseriti(colloquiInseriti);
				setNumPoliticheImportate(politicheInserite);
				setNumPoliticheNonImportate(politicheNonImportate);
				setNumPoliticheNonMappate(politicheNonMappate);
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore gestione politiche attive dalla SAP.", (Exception) e);
			return false;
		}
		return true;
	}

	protected String cpiPoliticaAttivaDiCompetenza(PoliticheAttive item,
			it.eng.sil.pojo.yg.sap.due.PoliticheAttive item2, MultipleTransactionQueryExecutor transExec)
			throws Exception {
		String cpiSil = null;
		String codEntePromotore = isGestioneSap2() ? item2.getCodiceEntePromotore() : item.getCodiceEntePromotore();
		if (codEntePromotore != null) {
			Object[] params = new Object[1];
			params[0] = codEntePromotore;
			SourceBean row = (SourceBean) transExec.executeQuery("SELECT_CODCPI_COMPETENZA_POLO_PROVINCIALE", params,
					"SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				cpiSil = (String) row.getAttribute("codcpisil");
			}
		}
		return cpiSil;
	}

	private BigDecimal cercaColloquio(String dataColloquio, String tipoAttivita,
			MultipleTransactionQueryExecutor transExec) throws Exception {
		SourceBean colloquio = null;
		BigDecimal prgColloquio = null;
		Object[] params = new Object[3];
		params[0] = getCdnLavoratore();
		params[1] = dataColloquio;
		params[2] = tipoAttivita;
		colloquio = (SourceBean) transExec.executeQuery("SELECT_COLLOQUIO_IMPORTA_POLITICAATTIVA", params, "SELECT");
		if (colloquio != null) {
			Vector colloqui = colloquio.getAttributeAsVector("ROW");
			if (colloqui.size() > 0) {
				SourceBean rowColloquio = (SourceBean) colloqui.get(0);
				if (rowColloquio != null && rowColloquio.containsAttribute("PRGCOLLOQUIO")) {
					prgColloquio = (BigDecimal) rowColloquio.getAttribute("PRGCOLLOQUIO");
				}
			}
		}
		return prgColloquio;
	}

	private BigDecimal cercaPercorso(BigDecimal prgColloquio, BigDecimal prgAzioni,
			MultipleTransactionQueryExecutor transExec) throws Exception {
		SourceBean percorso = null;
		BigDecimal prgPercorso = null;
		Object[] params = new Object[2];
		params[0] = prgColloquio;
		params[1] = prgAzioni;
		percorso = (SourceBean) transExec.executeQuery("CONTROLLA_ESISTENZA_AZIONE_IMPORTA_POLITICAATTIVA", params,
				"SELECT");
		if (percorso != null) {
			Vector percorsi = percorso.getAttributeAsVector("ROW");
			if (percorsi.size() > 0) {
				SourceBean rowPercorso = (SourceBean) percorsi.get(0);
				if (rowPercorso != null && rowPercorso.containsAttribute("PRGPERCORSO")) {
					prgPercorso = (BigDecimal) rowPercorso.getAttribute("PRGPERCORSO");
				}
			}
		}
		return prgPercorso;
	}

	private BigDecimal cercaAzione(String dataRif, String tipoAttivita, String tipoProgetto,
			MultipleTransactionQueryExecutor transExec) throws Exception {
		SourceBean azione = null;
		BigDecimal prgAzione = null;
		Object[] params = new Object[4];
		params[0] = tipoAttivita;
		params[1] = tipoProgetto;
		params[2] = dataRif;
		params[3] = dataRif;

		azione = (SourceBean) transExec.executeQuery("SELECT_AZIONE_IMPORTA_POLITICAATTIVA", params, "SELECT");
		if (azione != null) {
			azione = (azione.containsAttribute("ROW") ? (SourceBean) azione.getAttribute("ROW") : azione);
			prgAzione = (BigDecimal) azione.getAttribute("PRGAZIONI");
		}
		return prgAzione;
	}

	private BigDecimal getProgressivoColloquio(MultipleTransactionQueryExecutor transExec) throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) transExec.executeQuery("OR_COLLOQUIO_NEXTVAL", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("do_nextval");

		}
		return progressivo;
	}

	private BigDecimal getProgressivoPercorso(MultipleTransactionQueryExecutor txExec) throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) txExec.executeQuery("OR_PERCORSO_CONCORDATO_NEXTVAL", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("do_nextval");

		}
		return progressivo;
	}

	private BigDecimal getOperatoreCollegato(BigDecimal userId, MultipleTransactionQueryExecutor transExec)
			throws Exception {
		BigDecimal prgSpi = null;
		Object[] params = new Object[] { userId };
		SourceBean row = (SourceBean) transExec.executeQuery("SEL_SPI_UTENTE", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgSpi = (BigDecimal) row.getAttribute("prgspi");

		}
		return prgSpi;
	}

	private boolean inserisciColloquio(BigDecimal prgColloquio, BigDecimal cdnLav, String codServizio,
			String dataColloquio, String codCpi, BigDecimal userid, BigDecimal prgSpi, String noteColloquio,
			MultipleTransactionQueryExecutor transExec) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgColloquio, cdnLav, dataColloquio, codServizio, prgSpi, codCpi,
				noteColloquio, userid, userid };
		Boolean res = (Boolean) transExec.executeQuery("WS_RINNOVO_INSERISCI_COLLOQUIO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean inserisciSchedaColloquio(BigDecimal prgColloquio, MultipleTransactionQueryExecutor txExec)
			throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgColloquio };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_INSERISCI_SCHEDA_COLLOQUIO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean inserisciPercorso(BigDecimal prgPercorso, BigDecimal prgcolloquio, String codEsito,
			String codEsitoRendicont, BigDecimal prgazioni, PoliticheAttive item,
			it.eng.sil.pojo.yg.sap.due.PoliticheAttive item2, BigDecimal userId, BigDecimal prgSpi,
			MultipleTransactionQueryExecutor txExec) throws Exception {
		boolean isOK = false;
		String dataStimata = isGestioneSap2() ? DateUtils.formatXMLGregorian(item2.getDataProposta())
				: DateUtils.formatXMLGregorian(item.getDataProposta());
		String dataFine = isGestioneSap2() ? DateUtils.formatXMLGregorian(item2.getDataFine())
				: DateUtils.formatXMLGregorian(item.getDataFine());
		String codTipologiaDurata = isGestioneSap2() ? item2.getTipologiaDurata() : item.getTipologiaDurata();
		String tipoAttivita = isGestioneSap2() ? item2.getTipoAttivita() : item.getTipoAttivita();
		BigDecimal prgSpiAvvio = null;
		BigDecimal prgSpiPro = null;
		BigDecimal prgSpiConcluso = null;
		String cfDatoreLavoro = null;
		if (tipoAttivita.equalsIgnoreCase("C06") || tipoAttivita.equalsIgnoreCase("D01")
				|| tipoAttivita.equalsIgnoreCase("E01") || tipoAttivita.equalsIgnoreCase("E02")
				|| tipoAttivita.equalsIgnoreCase("E03") || tipoAttivita.equalsIgnoreCase("H01")) {
			cfDatoreLavoro = isGestioneSap2() ? item2.getDescrizione() : item.getDescrizione();
		}
		if (codEsito != null) {
			if (codEsito.equalsIgnoreCase("AVV")) {
				prgSpiAvvio = prgSpi;
			} else {
				if (codEsito.equalsIgnoreCase("FC")) {
					prgSpiConcluso = prgSpi;
				} else {
					if (codEsito.equalsIgnoreCase("PRO")) {
						prgSpiPro = prgSpi;
					}
				}
			}
		}

		Object[] params = new Object[] { prgPercorso, prgcolloquio, dataStimata, prgazioni, codEsito, codEsitoRendicont,
				userId, userId, dataFine, codTipologiaDurata, cfDatoreLavoro, prgSpiAvvio, prgSpiPro, prgSpiConcluso };
		Boolean res = (Boolean) txExec.executeQuery("INSERISCI_AZIONE_IMPORTAZIONE", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private String[] esitoPercorso(String dataProposta, String data, String dataFine) throws Exception {
		String[] esito = new String[2];
		String dataOdierna = DateUtils.getNow();
		if (dataProposta != null && data != null && dataFine != null && DateUtils.compare(dataProposta, data) == 0
				&& DateUtils.compare(data, dataFine) == 0) {
			esito[0] = "RIF";
			esito[1] = "A";
		} else {
			if ((dataProposta != null && data != null && dataFine != null)
					&& (DateUtils.compare(dataProposta, data) <= 0 && DateUtils.compare(data, dataFine) <= 0)
					&& (!dataProposta.equals(data) || !data.equals(dataFine))) {
				if (DateUtils.compare(dataFine, dataOdierna) <= 0) {
					esito[0] = "FC";
					esito[1] = "E";
				} else {
					if (DateUtils.compare(data, dataOdierna) <= 0 && DateUtils.compare(dataFine, dataOdierna) > 0) {
						esito[0] = "AVV";
						esito[1] = "P";
					} else {
						if (DateUtils.compare(data, dataOdierna) > 0 && DateUtils.compare(dataFine, dataOdierna) > 0) {
							esito[0] = "PRO";
							esito[1] = "P";
						}
					}
				}
			}
		}
		return esito;
		// data_proposta = data = data_fine
	}

	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	public BigDecimal getCdnLavoratore() {
		return this.cdnlavoratore;
	}

	public String getCodCpi() {
		return this.codCpi;
	}

	public String getCodMonoTipoCpi() {
		return this.codMonoTipoCpi;
	}

	public ElencoAnagraficoBean getElencoAnagrafico() {
		return this.elenco;
	}

	public DidBean getDid() {
		return this.did;
	}

	public User getOperatore() {
		return operatore;
	}

	public LavoratoreType getLavoratoreSapType() {
		return lavoratoreSapType;
	}

	public int getNumEsperienzeLavoro() {
		return this.numEsperienzeLavoro;
	}

	public int getNumEsperienzeLavoroImportate() {
		return this.numEsperienzeLavoroImportate;
	}

	public String getEsitoDatiAnagrafici() {
		return this.esitoDatiAnagrafici;
	}

	public String geteEsitoDatiAmministrativi() {
		return this.esitoDatiAmministrativi;
	}

	public boolean getOperazioneInserimento() {
		return this.operazioneInserimento;
	}

	public int getNumPoliticheCompetenza() {
		return this.numPoliticheCompetenza;
	}

	public int getNumColloquiInseriti() {
		return this.numColloquiInseriti;
	}

	public int getNumPoliticheImportate() {
		return this.numPoliticheImportate;
	}

	public int getNumPoliticheNonImportate() {
		return this.numPoliticheNonImportate;
	}

	public int getNumPoliticheNonMappate() {
		return this.numPoliticheNonMappate;
	}

	public void setCodiceFiscale(String val) {
		this.codiceFiscale = val;
	}

	public void setCdnLavoratore(BigDecimal val) {
		this.cdnlavoratore = val;
	}

	public void setCodCpi(String val) {
		this.codCpi = val;
	}

	public void setCodMonoTipoCpi(String val) {
		this.codMonoTipoCpi = val;
	}

	public void setElencoAnagrafico(ElencoAnagraficoBean val) {
		this.elenco = val;
	}

	public void setDid(DidBean val) {
		this.did = val;
	}

	public void setLavoratoreSapType(LavoratoreType lavoratoreSapType) {
		this.lavoratoreSapType = lavoratoreSapType;
	}

	public void setOperatore(User operatore) {
		this.operatore = operatore;
	}

	public void setNumEsperienzeLavoro(int val) {
		this.numEsperienzeLavoro = val;
	}

	public void setNumEsperienzeLavoroImportate(int val) {
		this.numEsperienzeLavoroImportate = val;
	}

	public void setEsitoDatiAnagrafici(String val) {
		this.esitoDatiAnagrafici = val;
	}

	public void setEsitoDatiAmministrativi(String val) {
		this.esitoDatiAmministrativi = val;
	}

	public void setOperazioneInserimento(boolean val) {
		this.operazioneInserimento = val;
	}

	public void setNumPoliticheCompetenza(int val) {
		this.numPoliticheCompetenza = val;
	}

	public void setNumColloquiInseriti(int val) {
		this.numColloquiInseriti = val;
	}

	public void setNumPoliticheImportate(int val) {
		this.numPoliticheImportate = val;
	}

	public void setNumPoliticheNonImportate(int val) {
		this.numPoliticheNonImportate = val;
	}

	public void setNumPoliticheNonMappate(int val) {
		this.numPoliticheNonMappate = val;
	}

	public String getFlgDidPresente() {
		return this.flgDidPresente;
	}

	public void setFlgDidPresente(String flag) {
		this.flgDidPresente = flag;
	}

	public String getMsgErrCode() {
		return this.msgErrCode;
	}

	public void setMsgErrCode(String val) {
		this.msgErrCode = val;
	}

	public it.eng.sil.pojo.yg.sap.due.LavoratoreType getLavoratoreSapDueType() {
		return lavoratoreSapDueType;
	}

	public void setLavoratoreSapDueType(it.eng.sil.pojo.yg.sap.due.LavoratoreType lavoratoreSapDueType) {
		this.lavoratoreSapDueType = lavoratoreSapDueType;
	}

	public boolean isGestioneSap2() {
		return gestioneSap2;
	}

	public void setGestioneSap2(boolean gestioneSap2) {
		this.gestioneSap2 = gestioneSap2;
	}

}
