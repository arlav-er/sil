package it.eng.sil.module.sap;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.LavoratoreBean;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.pojo.yg.sap.LavoratoreType;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.EventoAmministrativo;

public class ImportaSAP extends AbstractSimpleModule {

	private static final long serialVersionUID = -3983224412381905234L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ImportaSAP.class.getName());

	private String codErroreSezioneAnag = null;
	private String codErroreSezioneDid = null;
	private String codErroreSezioneEspLav = null;
	private String codErroreSezionePoliticheAttive = null;
	private boolean risultatoInResponse = true;
	private String valoreConfig = null;
	private String flgAnagraficaPresente = null;
	private String flgDidPresente = null;
	private String codErrore = null;
	private String msgErrCode = null;
	private String dataNormativa297 = null;

	private static final String POLITICHEOFF = "POLITICHE_OFF";

	public ImportaSAP() {
	}

	public void settaParametri(String valoreConfigurazione) {
		setCodErroreSezioneAnag("00");
		setCodErroreSezioneDid("00");
		setCodErroreSezioneEspLav("00");
		setCodErroreSezionePoliticheAttive("00");
		setCodErrore("00");
		setRisultatoInResponse(false);
		setValoreConfig(valoreConfigurazione);
		UtilsConfig utility = new UtilsConfig("AM_297");
		String strValoreConfig = utility.getValoreConfigurazione();
		if (strValoreConfig == null || strValoreConfig.equals("")) {
			setDataNormativa297(EventoAmministrativo.DATA_NORMATIVA_DEFAULT);
		} else {
			setDataNormativa297(strValoreConfig);
		}
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		LavoratoreType lavT = null;
		it.eng.sil.pojo.yg.sap.due.LavoratoreType lavT2 = null;
		try {
			setCodErroreSezioneAnag("00");
			setCodErroreSezioneDid("00");
			setCodErroreSezioneEspLav("00");
			setCodErroreSezionePoliticheAttive("00");
			setCodErrore("00");
			setRisultatoInResponse(true);
			// BigDecimal sap2 =
			// getResponseContainer().getServiceResponse().containsAttribute("M_CheckSap2.ROWS.ROW.datediff")?
			// (BigDecimal)
			// getResponseContainer().getServiceResponse().getAttribute("M_CheckSap2.ROWS.ROW.datediff"):null;
			String strValore = getResponseContainer().getServiceResponse()
					.containsAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.STRVALORE")
							? getResponseContainer().getServiceResponse()
									.getAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.STRVALORE").toString()
							: "";
			setValoreConfig(strValore);
			UtilsConfig utility = new UtilsConfig("AM_297");
			String strValoreConfig = utility.getValoreConfigurazione();
			if (strValoreConfig == null || strValoreConfig.equals("")) {
				setDataNormativa297(EventoAmministrativo.DATA_NORMATIVA_DEFAULT);
			} else {
				setDataNormativa297(strValoreConfig);
			}

			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			boolean isSap2 = true;
			// if(sap2!=null && sap2.intValue()>=0){
			// isSap2 = true;
			lavT2 = (it.eng.sil.pojo.yg.sap.due.LavoratoreType) getResponseContainer().getServiceResponse()
					.getAttribute("M_SAPCALLRICHIESTASAP.SAPWS2");
			// }
			// else{
			// isSap2 = false;
			// lavT = (LavoratoreType)
			// getResponseContainer().getServiceResponse().getAttribute("M_SAPCALLRICHIESTASAP.SAPWS");
			// }
			esegui(isSap2, lavT, lavT2, requestContainer, request, response, user);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in importazione SAP.", (Exception) e);
		}
	}

	public LavoratoreBean importa(boolean isSap2, SourceBean request, SourceBean response,
			RequestContainer requestContainer, User user, String xmlSAP) throws Exception {
		LavoratoreType lavT = null;
		it.eng.sil.pojo.yg.sap.due.LavoratoreType lavT2 = null;
		LavoratoreBean lav = null;
		try {
			if (isSap2) {
				lavT2 = convertToLavoratoreSAP2(xmlSAP);
			} else {
				lavT = convertToLavoratoreSAP(xmlSAP);
			}

			if (lavT != null || lavT2 != null) {
				lav = esegui(isSap2, lavT, lavT2, requestContainer, request, response, user);
			}
			return lav;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in importazione SAP.", (Exception) e);
			setCodErrore("99");
			setMsgErrCode(e.getMessage());
			return null;
		}
	}

	private LavoratoreBean esegui(boolean isSap2, LavoratoreType lavT, it.eng.sil.pojo.yg.sap.due.LavoratoreType lavT2,
			RequestContainer requestContainer, SourceBean request, SourceBean response, User user) throws Exception {
		MultipleTransactionQueryExecutor transExec = null;
		LavoratoreBean lav = null;
		String cfLav = null;
		boolean insertLav = false;
		boolean anagraficaOK = true;
		boolean successDid = true;
		boolean successEspLav = true;
		boolean successPolAttive = true;
		try {
			cfLav = isSap2 ? lavT2.getDatianagrafici().getDatipersonali().getCodicefiscale()
					: lavT.getDatianagrafici().getDatipersonali().getCodicefiscale();
			// istanzio oggetto lavoratore
			transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
			lav = new LavoratoreBean(cfLav, transExec);
			lav.setGestioneSap2(isSap2);
			if (isSap2) {
				lav.setLavoratoreSapDueType(lavT2);
			} else {
				lav.setLavoratoreSapType(lavT);
			}
			lav.setOperatore(user);
			// gestione anagrafica lavoratore
			if (lav.getCdnLavoratore() == null) {
				setFlgAnagraficaPresente("N");
				try {
					if (isSap2) {
						insertLav = lav.inserisciLavoratore2(transExec);
					} else {
						insertLav = lav.inserisciLavoratore(transExec);
					}
					if (!insertLav) {
						setCodErroreSezioneAnag("10");
						anagraficaOK = false;
						lav.setEsitoDatiAnagrafici("Errore importazione anagrafica");
						setCodErrore("01");
						if (lav.getMsgErrCode() != null) {
							setMsgErrCode(lav.getMsgErrCode());
						}
					}
				} catch (Exception e) {
					lav.setEsitoDatiAnagrafici("Errore importazione anagrafica");
					setCodErroreSezioneAnag("99");
					anagraficaOK = false;
					it.eng.sil.util.TraceWrapper.error(_logger, "Errore inserimento anagrafica lavoratore dalla SAP.",
							(Exception) e);

				}
			} else {
				setFlgAnagraficaPresente("S");
				lav.setEsitoDatiAnagrafici("Anagrafica lavoratore già presente");
			}

			if (anagraficaOK) {
				// gestione dati amministrativi/DID
				if (isSap2) {
					successDid = lav.gestisciDatiAmministrativiDID2(transExec, requestContainer, request, response,
							getDataNormativa297());
				} else {
					successDid = lav.gestisciDatiAmministrativiDID(transExec, requestContainer, request, response,
							getDataNormativa297());
				}

				if (!successDid) {
					setCodErroreSezioneDid("10");
					if (lav.geteEsitoDatiAmministrativi() == null || lav.geteEsitoDatiAmministrativi().equals("")) {
						lav.setEsitoDatiAmministrativi(
								"Non è possibile importare la dichiarazione immediata disponibilità");
					}
					setCodErrore("02");
					if (lav.getMsgErrCode() != null) {
						setMsgErrCode(lav.getMsgErrCode());
					}
				} else {
					if (lav.getFlgDidPresente() != null) {
						setFlgDidPresente(lav.getFlgDidPresente());
					}
				}

				// gestione esperienze professionali
				if (isSap2) {
					successEspLav = lav.gestisciEsperienzeLavoro2(transExec);
				} else {
					successEspLav = lav.gestisciEsperienzeLavoro(transExec);
				}

				if (!successEspLav) {
					setCodErroreSezioneEspLav("10");
				}

				// gestione politiche attive
				if (!getValoreConfig().equalsIgnoreCase(POLITICHEOFF)) {
					successPolAttive = lav.gestisciPoliticheAttive(transExec);
					if (!successPolAttive) {
						setCodErroreSezionePoliticheAttive("10");
					}
				}
			}

			// chiusura della connessione
			transExec.closeConnection();
			return lav;
		} catch (Exception e) {
			if (transExec != null) {
				transExec.closeConnection();
			}
			setCodErroreSezioneAnag("99");
			setCodErroreSezioneDid("99");
			setCodErroreSezioneEspLav("99");
			setCodErrore("99");
			setMsgErrCode(e.getMessage());
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in importazione SAP.", (Exception) e);
			return null;
		} finally {
			if (getRisultatoInResponse()) {
				response.setAttribute("CODICEERROREANAG", getCodErroreSezioneAnag());
				response.setAttribute("CODICEERROREDID", getCodErroreSezioneDid());
				response.setAttribute("CODICEERROREESPLAV", getCodErroreSezioneEspLav());
				response.setAttribute("CODICEERROREPOLITICHE", getCodErroreSezionePoliticheAttive());
				if (lav != null) {
					if (insertLav) {
						response.setAttribute("CDNLAVORATOREIMPORTATO", lav.getCdnLavoratore().toString());
					}
					response.setAttribute("ESPLAVORATIVESAP", new Integer(lav.getNumEsperienzeLavoro()).toString());
					response.setAttribute("ESPLAVORATIVEIMPORTATE",
							new Integer(lav.getNumEsperienzeLavoroImportate()).toString());
					response.setAttribute("ESITODATIANAGRAFICI", lav.getEsitoDatiAnagrafici());
					response.setAttribute("ESITODATIAMMINISTRATIVI", lav.geteEsitoDatiAmministrativi());
					response.setAttribute("POLITICHECOMPETENZA",
							new Integer(lav.getNumPoliticheCompetenza()).toString());
					response.setAttribute("COLLOQUIINSERITI", new Integer(lav.getNumColloquiInseriti()).toString());
					response.setAttribute("POLITICHEIMPORTATE", new Integer(lav.getNumPoliticheImportate()).toString());
					response.setAttribute("POLITICHENONIMPORTATE",
							new Integer(lav.getNumPoliticheNonImportate()).toString());
					response.setAttribute("POLITICHENONMAPPATE",
							new Integer(lav.getNumPoliticheNonMappate()).toString());
				}
			}
		}
	}

	public String getCodErroreSezioneAnag() {
		return this.codErroreSezioneAnag;
	}

	public void setCodErroreSezioneAnag(String codice) {
		this.codErroreSezioneAnag = codice;
	}

	public String getCodErroreSezioneDid() {
		return this.codErroreSezioneDid;
	}

	public void setCodErroreSezioneDid(String codice) {
		this.codErroreSezioneDid = codice;
	}

	public boolean getRisultatoInResponse() {
		return this.risultatoInResponse;
	}

	public void setRisultatoInResponse(boolean value) {
		this.risultatoInResponse = value;
	}

	public String getCodErroreSezioneEspLav() {
		return this.codErroreSezioneEspLav;
	}

	public void setCodErroreSezioneEspLav(String codice) {
		this.codErroreSezioneEspLav = codice;
	}

	public String getCodErroreSezionePoliticheAttive() {
		return this.codErroreSezionePoliticheAttive;
	}

	public void setCodErroreSezionePoliticheAttive(String codice) {
		this.codErroreSezionePoliticheAttive = codice;
	}

	public String getValoreConfig() {
		return this.valoreConfig;
	}

	public void setValoreConfig(String codice) {
		this.valoreConfig = codice;
	}

	public String getFlgAnagraficaPresente() {
		return this.flgAnagraficaPresente;
	}

	public void setFlgAnagraficaPresente(String flag) {
		this.flgAnagraficaPresente = flag;
	}

	public String getFlgDidPresente() {
		return this.flgDidPresente;
	}

	public void setFlgDidPresente(String flag) {
		this.flgDidPresente = flag;
	}

	public String getCodErrore() {
		return this.codErrore;
	}

	public void setCodErrore(String codice) {
		this.codErrore = codice;
	}

	public String getMsgErrCode() {
		return this.msgErrCode;
	}

	public void setMsgErrCode(String val) {
		this.msgErrCode = val;
	}

	public String getDataNormativa297() {
		return this.dataNormativa297;
	}

	public void setDataNormativa297(String val) {
		this.dataNormativa297 = val;
	}

	private LavoratoreType convertToLavoratoreSAP(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		LavoratoreType sap = null;
		try {

			jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<LavoratoreType> root = (JAXBElement<LavoratoreType>) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlSAP));
			sap = root.getValue();

		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return sap;
	}

	private it.eng.sil.pojo.yg.sap.due.LavoratoreType convertToLavoratoreSAP2(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		it.eng.sil.pojo.yg.sap.due.LavoratoreType sap = null;
		try {

			jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.due.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType> root = (JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType>) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlSAP));
			sap = root.getValue();

		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return sap;
	}
}