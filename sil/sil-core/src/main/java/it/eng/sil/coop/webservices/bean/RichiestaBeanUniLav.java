package it.eng.sil.coop.webservices.bean;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.coop.webservices.movimenti.constant.ContrattiConstant;
import it.eng.sil.coop.webservices.movimenti.constant.ErroriUniLavConstant;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ValidatorGeneral;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.movimenti.processors.ControllaMovimenti;
import it.eng.sil.module.movimenti.processors.ControlloDurataTD;
import it.eng.sil.module.movimenti.processors.ControlloMovimentoSimile;
import it.eng.sil.module.movimenti.processors.ControlloPermessi;
import it.eng.sil.module.movimenti.processors.ControlloTipoAssunzione;
import it.eng.sil.module.movimenti.processors.CrossController;
import it.eng.sil.module.movimenti.processors.EseguiImpatti;
import it.eng.sil.module.movimenti.processors.InsertApprendistato;
import it.eng.sil.module.movimenti.processors.InsertAzienda;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.InsertDatiMissione;
import it.eng.sil.module.movimenti.processors.InsertDocumento;
import it.eng.sil.module.movimenti.processors.InsertTirocinio;
import it.eng.sil.module.movimenti.processors.ProcControlloMbCmEtaLav;
import it.eng.sil.module.movimenti.processors.SelectMovimentoPrecManuale;
import it.eng.sil.module.movimenti.processors.SelectMovimentoSucc;
import it.eng.sil.module.movimenti.processors.UpdateMovimentoPrec;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

public class RichiestaBeanUniLav extends RichiestaBeanCO {

	private String lavoroStagionale = "";
	private String dataNullaOsta = "";
	private String numeroAtto = "";
	private String codAtecoUAz = "";

	public RichiestaBeanUniLav(Document doc) throws Exception {
		super(doc);
		caricaLavoratore();
		caricaRapporto();
		caricaAzienda();
	}

	public String getLavoroStagionale() {
		return this.lavoroStagionale;
	}

	public void setLavoroStagionale(String val) {
		this.lavoroStagionale = val;
	}

	public String getDataLegge68() {
		return this.dataNullaOsta;
	}

	public void setDataLegge68(String val) {
		this.dataNullaOsta = val;
	}

	public String getNumAtto68() {
		return this.numeroAtto;
	}

	public void setNumAtto68(String val) {
		this.numeroAtto = val;
	}

	public String getAtecoAz() {
		return this.codAtecoUAz;
	}

	public void setAtecoAz(String val) {
		this.codAtecoUAz = val;
	}

	private void caricaLavoratore() throws Exception {
		String path = null;
		path = "UniLav/Lavoratore/AnagraficaCompleta";

		XPath xpath = XPathFactory.newInstance().newXPath();

		Element nodeAnag = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
		if (nodeAnag != null) {
			path = "cittadinanza";
			setCittadinanza(getNodeValue(nodeAnag, path));
		}

		path = "UniLav/Lavoratore/extraCE";
		Element nodeExtraCE = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
		if (nodeExtraCE != null) {
			String dataScadenzaPS = nodeExtraCE.getAttribute("dataScadenzaPS");
			if (dataScadenzaPS != null && !dataScadenzaPS.equals("")) {
				setScadenzaPS(dataScadenzaPS.substring(8, 10) + "/" + dataScadenzaPS.substring(5, 7) + "/"
						+ dataScadenzaPS.substring(0, 4));
			}
			setMotivoPS(nodeExtraCE.getAttribute("motivoPermesso"));
			setTipoDocPS(nodeExtraCE.getAttribute("tipoDocumento"));
			if (nodeExtraCE.getAttribute("numeroDocumento") != null
					&& !nodeExtraCE.getAttribute("numeroDocumento").toString().equals("")) {
				setNumeroDoc(nodeExtraCE.getAttribute("numeroDocumento"));
			}
			if (nodeExtraCE.getAttribute("questura") != null
					&& !nodeExtraCE.getAttribute("questura").toString().equals("")) {
				setQuesturaPS(nodeExtraCE.getAttribute("questura"));
			}
		}
	}

	private void caricaRapporto() throws Exception {
		String path = null;
		String pathContratto = null;
		Element nodeContratto = null;

		XPath xpath = XPathFactory.newInstance().newXPath();

		if (getCodTipoMov().equalsIgnoreCase("AVV")) {
			pathContratto = "UniLav/InizioRapporto";
		} else {
			pathContratto = "UniLav/Proroga/Contratto";
			path = "UniLav/Proroga";

			Element nodeProroga = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
			if (nodeProroga != null) {
				if (nodeProroga.getAttribute("dataFineProroga") != null
						&& !nodeProroga.getAttribute("dataFineProroga").toString().equals("")) {
					String dtFinePro = nodeProroga.getAttribute("dataFineProroga");
					setDataFinePro(dtFinePro.substring(8, 10) + "/" + dtFinePro.substring(5, 7) + "/"
							+ dtFinePro.substring(0, 4));
				}
			}
		}

		nodeContratto = (Element) xpath.evaluate(pathContratto, getRootXML(), XPathConstants.NODE);
		if (nodeContratto != null) {

			if (nodeContratto.getAttribute("lavoroInAgricoltura") != null
					&& !nodeContratto.getAttribute("lavoroInAgricoltura").toString().equals("")) {
				setLavoroAgr(nodeContratto.getAttribute("lavoroInAgricoltura"));
			}

			if (nodeContratto.getAttribute("lavInMobilita") != null
					&& !nodeContratto.getAttribute("lavInMobilita").toString().equals("")) {
				setLavoroMobilita(nodeContratto.getAttribute("lavInMobilita"));
			}

			if (nodeContratto.getAttribute("lavoroStagionale") != null
					&& !nodeContratto.getAttribute("lavoroStagionale").toString().equals("")) {
				setLavoroStagionale(nodeContratto.getAttribute("lavoroStagionale"));
			}

			if (nodeContratto.getAttribute("dataFinePeriodoFormativo") != null
					&& !nodeContratto.getAttribute("dataFinePeriodoFormativo").toString().equals("")) {
				String dtFinePF = nodeContratto.getAttribute("dataFinePeriodoFormativo");
				setDataFinePF(
						dtFinePF.substring(8, 10) + "/" + dtFinePF.substring(5, 7) + "/" + dtFinePF.substring(0, 4));
			}

			if (nodeContratto.getAttribute("dataInizio") != null
					&& !nodeContratto.getAttribute("dataInizio").toString().equals("")) {
				String dtInizioMov = nodeContratto.getAttribute("dataInizio");
				if (getCodTipoMov().equalsIgnoreCase("AVV")) {
					setDataInizioMov(dtInizioMov.substring(8, 10) + "/" + dtInizioMov.substring(5, 7) + "/"
							+ dtInizioMov.substring(0, 4));
				} else {
					if (getCodTipoMov().equalsIgnoreCase("PRO")) {
						setDataInizioRappPro(dtInizioMov.substring(8, 10) + "/" + dtInizioMov.substring(5, 7) + "/"
								+ dtInizioMov.substring(0, 4));
					}
				}
			}

			if (nodeContratto.getAttribute("dataFine") != null
					&& !nodeContratto.getAttribute("dataFine").toString().equals("")) {
				String dtFineMov = nodeContratto.getAttribute("dataFine");
				setDataFineMov(
						dtFineMov.substring(8, 10) + "/" + dtFineMov.substring(5, 7) + "/" + dtFineMov.substring(0, 4));
			}

			if (nodeContratto.getAttribute("ggLavorativePreviste") != null
					&& !nodeContratto.getAttribute("ggLavorativePreviste").toString().equals("")) {
				setGiorniAgr(nodeContratto.getAttribute("ggLavorativePreviste"));
			}

			if (nodeContratto.getAttribute("tipoLavorazione") != null
					&& !nodeContratto.getAttribute("tipoLavorazione").toString().equals("")) {
				setTipoLavorazione(nodeContratto.getAttribute("tipoLavorazione"));
			}

			if (nodeContratto.getAttribute("entePrevidenziale") != null
					&& !nodeContratto.getAttribute("entePrevidenziale").toString().equals("")) {
				setEntePrev(nodeContratto.getAttribute("entePrevidenziale"));
			}

			if (nodeContratto.getAttribute("codiceEntePrevidenziale") != null
					&& !nodeContratto.getAttribute("codiceEntePrevidenziale").toString().equals("")) {
				setCodEntePrev(nodeContratto.getAttribute("codiceEntePrevidenziale"));
			}

			if (nodeContratto.getAttribute("TipologiaContrattuale") != null
					&& !nodeContratto.getAttribute("TipologiaContrattuale").toString().equals("")) {
				setContratto(nodeContratto.getAttribute("TipologiaContrattuale"));
			}

			if (nodeContratto.getAttribute("socioLavoratore") != null
					&& !nodeContratto.getAttribute("socioLavoratore").toString().equals("")) {
				setSocio(nodeContratto.getAttribute("socioLavoratore"));
			}

			if (nodeContratto.getAttribute("codMonoTempo") != null
					&& !nodeContratto.getAttribute("codMonoTempo").toString().equals("")) {
				setCodMonoTempo(nodeContratto.getAttribute("codMonoTempo"));
			} else {
				if (getDataFineMov().equals("")) {
					setCodMonoTempo(CodMonoTempoEnum.INDETERMINATO.getCodice());
				} else {
					setCodMonoTempo(CodMonoTempoEnum.DETERMINATO.getCodice());
				}
			}
		}

		path = "ccnl";
		String ccnlAzienda = getNodeValue(nodeContratto, path);
		setCCNL(ccnlAzienda);
		setCCNLAz(ccnlAzienda);
		path = "livelloInquadramento";
		setLivello(getNodeValue(nodeContratto, path));
		path = "legge68";

		Element nodeLegge68 = (Element) xpath.evaluate(path, nodeContratto, XPathConstants.NODE);
		if (nodeLegge68 != null) {
			if (nodeLegge68.getAttribute("dataNullaOsta") != null
					&& !nodeLegge68.getAttribute("dataNullaOsta").toString().equals("")) {
				String dataNullaOsta = nodeLegge68.getAttribute("dataNullaOsta").toString();
				setDataLegge68(dataNullaOsta.substring(8, 10) + "/" + dataNullaOsta.substring(5, 7) + "/"
						+ dataNullaOsta.substring(0, 4));
			}
			if (nodeLegge68.getAttribute("numeroAtto") != null
					&& !nodeLegge68.getAttribute("numeroAtto").toString().equals("")) {
				setNumAtto68(nodeLegge68.getAttribute("numeroAtto"));
			}

		}
		path = "tipoOrario";
		Element nodeOrarioContratto = (Element) xpath.evaluate(path, nodeContratto, XPathConstants.NODE);

		if (nodeOrarioContratto != null) {
			if (nodeOrarioContratto.getAttribute("codice") != null
					&& !nodeOrarioContratto.getAttribute("codice").toString().equals("")) {
				setOrario(nodeOrarioContratto.getAttribute("codice"));
			}
			if (nodeOrarioContratto.getAttribute("oreSettimaniliMedie") != null
					&& !nodeOrarioContratto.getAttribute("oreSettimaniliMedie").toString().equals("")) {
				setOreSettimanali(nodeOrarioContratto.getAttribute("oreSettimaniliMedie"));
			}

		}
		path = "RetribuzioneCompenso";
		setRetribuzione(getNodeValue(nodeContratto, path));
		path = "qualificaProfessionale";
		setQualifProf(getNodeValue(nodeContratto, path));
		path = "PatINAIL";
		setPatInail(getNodeValue(nodeContratto, path));
	}

	private void caricaAzienda() throws Exception {
		String path = null;
		Element nodeDatore = null;
		Element nodeSedeLegale = null;
		Element nodeSedeLavoro = null;
		path = "UniLav/DatoreLavoro";

		XPath xpath = XPathFactory.newInstance().newXPath();

		nodeDatore = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
		if (nodeDatore != null) {
			if (nodeDatore.getAttribute("codiceFiscale") != null
					&& !nodeDatore.getAttribute("codiceFiscale").toString().equals("")) {
				setCFAz(nodeDatore.getAttribute("codiceFiscale"));
			}
			if (nodeDatore.getAttribute("denominazione") != null
					&& !nodeDatore.getAttribute("denominazione").toString().equals("")) {
				setRagSocAz(nodeDatore.getAttribute("denominazione"));
			}
			path = "Settore";
			setAtecoAz(getNodeValue(nodeDatore, path));
			path = "PubblicaAmministrazione";
			setPubbAmm(getNodeValue(nodeDatore, path));
		}
		path = "UniLav/DatoreLavoro/SedeLegale";
		nodeSedeLegale = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);

		if (nodeSedeLegale != null) {
			path = "Comune";
			setComSedeLegale(getNodeValue(nodeSedeLegale, path));
			path = "cap";
			setCapSedeLegale(getNodeValue(nodeSedeLegale, path));
			path = "Indirizzo";
			setIndSedeLegale(getNodeValue(nodeSedeLegale, path));
			path = "e-mail";
			setMailAzLeg(getNodeValue(nodeSedeLegale, path));
			path = "telefono";
			setTelAzLeg(getNodeValue(nodeSedeLegale, path));
			path = "fax";
			setFaxAzLeg(getNodeValue(nodeSedeLegale, path));
		}
		path = "UniLav/DatoreLavoro/SedeLavoro";
		nodeSedeLavoro = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
		if (nodeSedeLavoro != null) {
			path = "Comune";
			setComAZ(getNodeValue(nodeSedeLavoro, path));
			path = "cap";
			setCapAz(getNodeValue(nodeSedeLavoro, path));
			path = "Indirizzo";
			setIndirizzoAz(getNodeValue(nodeSedeLavoro, path));
			path = "e-mail";
			setMailAz(getNodeValue(nodeSedeLavoro, path));
			path = "telefono";
			setTelAz(getNodeValue(nodeSedeLavoro, path));
			path = "fax";
			setFaxAz(getNodeValue(nodeSedeLavoro, path));
		}
	}

	public String checkDati() throws Exception {
		String erroreXML = null;

		if (!(getContratto().equals(""))) {
			if (!getContrattoValido(getContratto())) {
				erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_CONTRATTO_INESISTENTE,
						"Codice tipologia contrattuale inesistente");
				return erroreXML;
			}
		}

		if (!getCittadinanza().equals("")) {
			String flgCEE = getInfoCittadinanza(getCittadinanza());
			if (flgCEE.equalsIgnoreCase("N")) {
				if (getTipoDocPS().equals("") || getNumeroDoc().equals("") || getScadenzaPS().equals("")
						|| getMotivoPS().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_PERMESSO_SOGGIORNO,
							"Dati permesso di soggiorno mancanti o incompleti");
					return erroreXML;
				}
				if (getTipoDocPS().equalsIgnoreCase("3") || getTipoDocPS().equalsIgnoreCase("4")) {
					if (!getQuesturaPS().equals("")) {
						erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_PERMESSO_SOGGIORNO,
								"Dati permesso di soggiorno mancanti o incompleti");
						return erroreXML;
					}
				}
			}
		}

		if (getTelAzLeg().equals("") && getMailAzLeg().equals("") && getFaxAzLeg().equals("")) {
			erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_SEDE_LEGALE,
					"Dati della sede legale mancanti o incompleti");
			return erroreXML;
		}

		if (getTelAz().equals("") && getMailAz().equals("") && getFaxAz().equals("")) {
			erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_SEDE_OPERATIVA,
					"Dati della sede operativa mancanti o incompleti");
			return erroreXML;
		}

		String codMonoTempoMov = getCodMonoTempo();
		if (getCodTipoMov().equalsIgnoreCase("AVV")) {
			if (codMonoTempoMov.equalsIgnoreCase(CodMonoTempoEnum.DETERMINATO.getCodice())) {
				if (getDataFineMov().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_DATA_FINE_MANCANTE_ERRATA_TD,
							"Data fine mancante o precedente a data inizio");
					return erroreXML;
				}
			}
			if (!getDataInizioMov().equals("") && !getDataFineMov().equals("")) {
				if (DateUtils.compare(getDataFineMov(), getDataInizioMov()) < 0) {
					erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_DATA_FINE_MANCANTE_ERRATA_TD,
							"Data fine mancante o precedente a data inizio");
					return erroreXML;
				}
			}
		} else {
			if (getCodTipoMov().equalsIgnoreCase("PRO")) {
				if (codMonoTempoMov.equals(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
					erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_PROROGA_TI,
							"Errore proroga: contratto a tempo indeterminato non prorogabile");
					return erroreXML;
				}
				if (getDataFinePro().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_DATA_FINE_MANCANTE_ERRATA_TD,
							"Data fine mancante o precedente a data inizio");
					return erroreXML;
				}
				if (!getDataInizioRappPro().equals("") && !getDataFineMov().equals("")) {
					if (DateUtils.compare(getDataFineMov(), getDataInizioRappPro()) < 0) {
						erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_DATA_FINE_MANCANTE_ERRATA_TD,
								"Data fine mancante o precedente a data inizio");
						return erroreXML;
					}
				}
			}
		}

		if (!getOrario().equals("")) {
			String tipoOrario = getInfoOrario(getOrario());
			if (tipoOrario.equals("")) {
				erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_ORARIO_INESISTENTE,
						"Codice tipo orario inesistente");
				return erroreXML;
			}
			if (tipoOrario.equalsIgnoreCase("P")) {
				int nOreSett = 0;
				if (!getOreSettimanali().equals("")) {
					nOreSett = new Integer(getOreSettimanali()).intValue();
				}
				if (nOreSett <= 0) {
					erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_ORE_SETTIMANALI_MANCANTI,
							"Ore settimanali non indicate");
					return erroreXML;
				}
			}
		}

		if (!getQualifProf().equals("")) {
			String mansioneSil = getInfoQualifica(getQualifProf());
			if (mansioneSil.equals("")) {
				erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_MANSIONE_INESISTENTE,
						"Codice qualifica professionale inesistente");
				return erroreXML;
			} else {
				setQualifProf(mansioneSil);
			}
		}

		if (!getCCNL().equals("")) {
			if (!getInfoCCNL(getCCNL())) {
				erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_CCNL_INESISTENTE,
						"Codice CCNL inesistente");
				return erroreXML;
			}
		}

		if (!getAtecoAz().equals("")) {
			String atecoSil = getInfoSettore(getAtecoAz());
			if (atecoSil.equals("")) {
				erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_ATECO_INESISTENTE,
						"Codice ATECO inesistente");
				return erroreXML;
			} else {
				setAtecoAz(atecoSil);
			}
		}

		if (getLavoroAgr().equalsIgnoreCase("SI")) {
			int ggAgricolturta = 0;
			if (!getGiorniAgr().equals("")) {
				ggAgricolturta = new Integer(getGiorniAgr()).intValue();
			}

			if (ggAgricolturta <= 0) {
				erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_GIORNATE_AGRIC_MANCANTI,
						"Giornate previste in agricoltura non indicate");
				return erroreXML;
			}
		}

		if (getCodTipoMov().equalsIgnoreCase("PRO")) {
			if (getPrgRettifica().equals("")) {
				if (getPrgProroga().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_MOV_DA_PROROGARE_MANCANTE,
							"Errore proroga: movimento da prorogare non indicato");
					return erroreXML;
				}
			}
		}

		if (getCodMonoTempo().equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())
				&& getLavoroStagionale().equalsIgnoreCase("SI")) {
			if (!getContratto().equalsIgnoreCase(
					DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE)) {
				erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_CONTRATTO_LAVORO_STAGIONALE,
						"Errore su lavoro stagionale");
				return erroreXML;
			}
		}

		if (getCodMonoTipoContratto().equalsIgnoreCase(ContrattiConstant.COD_MONO_TIPO_APPRENDISTATO)) {
			if (getDataFinePF().equals("")) {
				if (getCodTipoMov().equalsIgnoreCase("AVV")) {
					if (!getDataFineMov().equals("")) {
						setDataFinePF(getDataFineMov());
					}
				} else {
					if (getCodTipoMov().equalsIgnoreCase("PRO")) {
						if (!getDataFinePro().equals("")) {
							setDataFinePF(getDataFinePro());
						}
					}
				}
			}

			if (getDataFinePF().equals("")) {
				erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_DATA_FINE_PERIODO_FORMATIVO,
						"Errore su data fine periodo formativo");
				return erroreXML;
			} else {
				if (getCodTipoMov().equalsIgnoreCase("AVV")) {
					if (!getDataInizioMov().equals("")) {
						if (DateUtils.compare(getDataFinePF(), getDataInizioMov()) <= 0) {
							erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_DATA_FINE_PERIODO_FORMATIVO,
									"Errore su data fine periodo formativo");
							return erroreXML;
						}
					}
				}
			}

			if (getLavoroStagionale().equalsIgnoreCase("SI")) {
				if (getCodTipoMov().equalsIgnoreCase("AVV")) {
					if (!getDataFineMov().equals("")) {
						if (DateUtils.compare(getDataFinePF(), getDataFineMov()) != 0) {
							erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_DATA_FINE_PERIODO_FORMATIVO,
									"Errore su data fine periodo formativo");
							return erroreXML;
						}
					} else {
						erroreXML = Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_DATA_FINE_MANCANTE_ERRATA_TD,
								"Data fine mancante per rapporti stagionali");
						return erroreXML;
					}
				} else {
					if (getCodTipoMov().equalsIgnoreCase("PRO")) {
						if (!getDataFinePro().equals("")) {
							if (DateUtils.compare(getDataFinePF(), getDataFinePro()) != 0) {
								erroreXML = Utils.createXMLRisposta(
										ErroriUniLavConstant.ERRORE_DATA_FINE_PERIODO_FORMATIVO,
										"Errore su data fine periodo formativo");
								return erroreXML;
							}
						}
					}
				}
			}
		} else {
			if (!getDataFinePF().equals("")) {
				setDataFinePF("");
			}
		}

		return null;
	}

	public int inserisciMovimento(LavoratoreBean lavService, BigDecimal user, MultipleTransactionQueryExecutor trans)
			throws Exception {

		int codiceErrore = 0;
		Map record = new HashMap();
		String dataInizioMov = "";
		String dataFineMov = "";
		String dataFinePro = "";
		String flgLavAgr = "";
		String ggPrevistiAgr = "";
		String tipoLavor = "";
		String strEntePrev = "";
		String strCodEntePrev = "";
		String codTipoContratto = "";
		String flgSocio = "";
		String codOrario = "";
		String numOreSett = "";
		String codMansione = "";
		String codMonoTempoMov = "";
		String ccnlMov = "";
		String patInail = "";
		String cfAz = "";
		String ragSocAz = "";
		String flgPubbAmm = "";
		String codComUaz = "";
		String strCapUaz = "";
		String strIndirizzoUaz = "";
		String codAtecoUAz = "";
		String mailAz = "";
		String telAz = "";
		String faxAz = "";
		String codComSedeLegale = "";
		String indirizzoSedeLegale = "";
		String dataScadPS = "";
		String motivoPS = "";
		String tipoDocPS = "";
		String numDocPS = "";
		String questuraPS = "";
		String retribuzioneMen = "";
		String flgLavMob = "";
		String flgLavStag = "";
		String dataFinePF = "";

		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;

		String processorbase = configbase + "processors" + File.separator;
		String codTipoMov = this.getCodTipoMov();
		BigDecimal cdnLav = lavService.getCdnLavoratore();
		String codCpiLav = lavService.getCodCpi();
		String collegato = null;

		if (codTipoMov.equalsIgnoreCase("AVV")) {
			collegato = "nessuno";
		} else {
			collegato = "precedente";
			dataFinePro = this.getDataFinePro();
		}

		dataInizioMov = this.getDataInizioMov();
		dataFineMov = this.getDataFineMov();
		flgLavAgr = this.getLavoroAgr();
		ggPrevistiAgr = this.getGiorniAgr();
		tipoLavor = this.getTipoLavorazione();
		strEntePrev = this.getEntePrev();
		strCodEntePrev = this.getCodEntePrev();
		codTipoContratto = this.getContratto();
		flgSocio = this.getSocio();
		if (flgSocio.equalsIgnoreCase("SI")) {
			flgSocio = "S";
		} else {
			flgSocio = "N";
		}
		codMonoTempoMov = this.getCodMonoTempo();
		codOrario = this.getOrario();
		numOreSett = this.getOreSettimanali();
		codMansione = this.getQualifProf();
		retribuzioneMen = this.getRetribuzione();
		// recupero ccnl associato al movimento e all'azienda
		ccnlMov = this.getCCNL();
		if (ccnlMov.equals("")) {
			ccnlMov = "NT";
		}
		patInail = this.getPatInail();
		cfAz = this.getCFAz();
		ragSocAz = this.getRagSocAz();
		codAtecoUAz = this.getAtecoAz();
		flgPubbAmm = this.getPubbAmm();
		if (flgPubbAmm.equalsIgnoreCase("SI")) {
			flgPubbAmm = "S";
		} else {
			flgPubbAmm = "N";
		}
		codComSedeLegale = this.getComSedeLegale();
		indirizzoSedeLegale = this.getIndSedeLegale();
		codComUaz = this.getComAZ();
		strCapUaz = this.getCapAz();
		strIndirizzoUaz = this.getIndirizzoAz();
		mailAz = this.getMailAz();
		telAz = this.getTelAz();
		faxAz = this.getFaxAz();
		dataScadPS = this.getScadenzaPS();
		motivoPS = this.getMotivoPS();
		tipoDocPS = this.getTipoDocPS();
		numDocPS = this.getNumeroDoc();
		questuraPS = this.getQuesturaPS();
		flgLavMob = this.getLavoroMobilita();
		flgLavStag = this.getLavoroStagionale();
		dataFinePF = this.getDataFinePF();

		if (!this.getPrgProroga().equals("")) {
			BigDecimal prgmovprec = new BigDecimal(this.getPrgProroga());
			record.put("PRGMOVIMENTOPREC", prgmovprec);
		} else {
			if (!this.getPrgRettifica().equals("")) {
				BigDecimal prgmovrett = new BigDecimal(this.getPrgRettifica());
				record.put("PRGMOVIMENTORETT", prgmovrett);
			}
		}

		record.put("CODSTATOATTO", "PA");
		record.put("CODTIPOMOV", codTipoMov);
		record.put("DATCOMUNICAZ", DateUtils.getNow());
		record.put("COLLEGATO", collegato);
		record.put("CONTEXT", "valida");
		record.put("CODMONOMOVDICH", "C");
		record.put("CODMONOPROV", "M");
		record.put("PERMETTIIMPATTI", "true");

		if (!cfAz.equals("")) {
			record.put("STRAZCODICEFISCALE", cfAz);
		}

		if (!ragSocAz.equals("")) {
			record.put("STRAZRAGIONESOCIALE", ragSocAz);
		}

		if (!flgPubbAmm.equals("")) {
			record.put("FLGPA", flgPubbAmm);
		}

		if (!codComUaz.equals("")) {
			record.put("CODUACOM", codComUaz);
		}

		if (!strCapUaz.equals("")) {
			record.put("STRUACAP", strCapUaz);
		}

		if (!strIndirizzoUaz.equals("")) {
			record.put("STRUAINDIRIZZO", strIndirizzoUaz);
		}

		if (!mailAz.equals("")) {
			record.put("STRUAEMAIL", mailAz);
		}

		if (!telAz.equals("")) {
			record.put("STRUATEL", telAz);
		}

		if (!faxAz.equals("")) {
			record.put("STRUAFAX", faxAz);
		}

		if (!codAtecoUAz.equals("")) {
			record.put("CODAZATECO", codAtecoUAz);
		}

		if (!codComSedeLegale.equals("")) {
			record.put("CODUALCOM", codComSedeLegale);
		}

		if (!indirizzoSedeLegale.equals("")) {
			record.put("STRUALINDIRIZZO", indirizzoSedeLegale);
		}

		if (!patInail.equals("")) {
			record.put("STRPATINAIL", patInail);
		}

		// LAVORATORE
		record.put("CDNLAVORATORE", cdnLav);
		record.put("CODCPILAV", codCpiLav);
		if (!dataScadPS.equals("")) {
			record.put("DATSCADENZA", dataScadPS);
		}
		if (!motivoPS.equals("")) {
			record.put("CODMOTIVOPERMSOGGEX", motivoPS);
		}
		if (!tipoDocPS.equals("")) {
			record.put("CODTIPODOCEX", tipoDocPS);
		}
		if (!numDocPS.equals("")) {
			record.put("STRNUMDOCEX", numDocPS);
		}
		if (!questuraPS.equals("")) {
			record.put("QUESTURALAV", questuraPS);
		}

		if (getCodTipoMov().equalsIgnoreCase("PRO")) {
			if (dataFineMov != null && !dataFineMov.equals("")) {
				dataInizioMov = DateUtils.giornoSuccessivo(dataFineMov);
			} else {
				dataInizioMov = "";
			}
			if (dataFinePro != null && !dataFinePro.equals("")) {
				dataFineMov = dataFinePro;
			}
		}
		if (!dataInizioMov.equals("")) {
			record.put("DATINIZIOMOV", dataInizioMov);
		}
		if (!dataFineMov.equals("")) {
			record.put("DATFINEMOV", dataFineMov);
		}
		if (!dataFinePro.equals("")) {
			record.put("DATFINEMOVPRO", dataFinePro);
		}

		if (!codTipoContratto.equals("")) {
			record.put("CODTIPOASS", codTipoContratto);
		}

		if (!codMonoTempoMov.equals("")) {
			record.put("CODMONOTEMPO", codMonoTempoMov);
		}

		if (!retribuzioneMen.equals("")) {
			record.put("DECRETRIBUZIONEMEN", new BigDecimal(retribuzioneMen));
		}

		if (!flgSocio.equals("")) {
			record.put("FLGSOCIO", flgSocio);
		}

		if (!strEntePrev.equals("")) {
			record.put("CODENTE", strEntePrev);
		}

		if (!strCodEntePrev.equals("")) {
			record.put("STRCODICEENTEPREV", strCodEntePrev);
		}

		if (!codOrario.equals("")) {
			record.put("CODORARIO", codOrario);
		}

		if (!numOreSett.equals("")) {
			record.put("NUMORESETT", numOreSett);
		}

		if (!codMansione.equals("")) {
			record.put("CODMANSIONE", codMansione);
		}

		if (!ccnlMov.equals("")) {
			record.put("CODAZCCNL", ccnlMov);
			record.put("CODCCNL", ccnlMov);
		}

		if (flgLavAgr.equalsIgnoreCase("SI")) {
			flgLavAgr = "S";
		} else {
			flgLavAgr = "N";
		}
		record.put("FLGLAVOROAGR", flgLavAgr);

		if (!ggPrevistiAgr.equals("")) {
			record.put("NUMGGPREVISTIAGR", ggPrevistiAgr);
		}

		if (!tipoLavor.equals("")) {
			record.put("CODLAVORAZIONE", tipoLavor);
		}

		if (flgPubbAmm.equalsIgnoreCase("S")) {
			record.put("CODAZTIPOAZIENDA", "PA");
		} else {
			if (flgLavAgr.equalsIgnoreCase("S")) {
				record.put("CODAZTIPOAZIENDA", "AGR");
			} else {
				record.put("CODAZTIPOAZIENDA", "AZI");
			}
		}

		if (flgLavMob.equalsIgnoreCase("SI")) {
			flgLavMob = "S";
		} else {
			flgLavMob = "N";
		}
		record.put("FLGLAVOROINMOBILITA", flgLavMob);

		if (flgLavStag.equalsIgnoreCase("SI")) {
			flgLavStag = "S";
		} else {
			flgLavStag = "N";
		}
		record.put("FLGLAVOROSTAGIONALE", flgLavStag);

		if (!dataFinePF.equals("")) {
			record.put("DATFINEPF", dataFinePF);
		}

		// PROTOCOLLAZIONE
		SourceBean rowProt = (SourceBean) trans.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");

		rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
		BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
		BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
		String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");
		String oraProt = "";
		String dataProt = "";
		if (datProtocollazione != null && !datProtocollazione.equals("")) {
			if (datProtocollazione.length() > 11) {
				oraProt = datProtocollazione.substring(11, 16);
			} else {
				datProtocollazione = "00:00";
			}
			dataProt = datProtocollazione.substring(0, 10);

			record.put("DATAPROT", dataProt);
			record.put("ORAPROT", oraProt);

		}
		record.put("NUMANNOPROT", numAnnoProt);
		record.put("NUMPROTOCOLLO", numProtocollo);
		record.put("TIPOPROT", "S");

		ValidatorGeneral validator = new ValidatorGeneral(record);
		// Inserimento Azienda
		RecordProcessor insAz = new InsertAzienda("Inserimento Azienda nel DB", trans, user,
				processorbase + "insertAzienda.xml", sbGenerale);
		validator.addProcessor(insAz);
		// Processore che seleziona il movimento precedente
		validator.addProcessor(new SelectMovimentoPrecManuale("Seleziona Precedente", trans, null));
		// Controlli sulle autorizzazioni
		validator.addProcessor(new ControlloPermessi("Autorizzazione per impatti", trans));
		// controllo dei dati sensibili del lavoratore
		validator.addProcessor(new ProcControlloMbCmEtaLav("Controllo dati lavoratore", trans));
		// Processore per il controllo dell'esistenza di movimenti simili a
		// quello in inserimento
		validator.addProcessor(new ControlloMovimentoSimile("Controllo movimenti simili", trans, sbGenerale));
		// controllo sul tipo di assunzione
		validator.addProcessor(new ControlloTipoAssunzione("Controllo tipo assunzione", trans));
		// Processore che controlla la durata dei movimenti a TD
		validator.addProcessor(new ControlloDurataTD("Controlla durata movimenti a TD"));
		// Processore che controlla i dati del movimento.
		validator.addProcessor(new ControllaMovimenti(sbGenerale, trans, user));
		// Processore per ulteriori controlli
		validator.addProcessor(new CrossController("Controllore Incrociato"));
		// Processore per l'esecuzione degli impatti
		RecordProcessor eseguiImpatti = new EseguiImpatti("Esecuzione impatti", sbGenerale, trans, user);
		validator.addProcessor(eseguiImpatti);
		// Inserimento Movimento
		validator.addProcessor(new InsertData("Inserimento Movimento", trans, processorbase + "insertMovimento.xml",
				"INSERT_MOVIMENTO", user));
		// Processore per l'inserimento in am_movimento_missione delle info relative alla missione
		validator.addProcessor(new InsertDatiMissione(user, trans, sbGenerale));
		// Processore che aggiorna il movimento precedente
		validator.addProcessor(new UpdateMovimentoPrec("Aggiorna Precedente", trans, user));
		// Processore per l'inserimento in am_movimento_apprendist
		validator.addProcessor(new InsertApprendistato(user, trans));
		// Processore per l'inserimento in am_movimento_apprendist delle info relative al tirocinio
		validator.addProcessor(new InsertTirocinio(user, trans, sbGenerale));
		validator.addProcessor(new SelectMovimentoSucc("CercaMovimentoSuccessivo", trans));
		// Processors per l'inserimento del documento
		validator.addProcessor(new InsertDocumento(user, trans));

		SourceBean result = validator.importRecords(trans);

		Vector processorResult = result.getAttributeAsVector("PROCESSOR");
		for (int i = 0; i < processorResult.size(); i++) {
			try {
				SourceBean sbProcessor = (SourceBean) processorResult.get(i);
				String tipoErrore = sbProcessor.containsAttribute("RESULT")
						? sbProcessor.getAttribute("RESULT").toString()
						: "";
				if (tipoErrore.equalsIgnoreCase("ERROR")) {
					codiceErrore = -1;
					SourceBean sbErrore = (SourceBean) sbProcessor.getAttribute("ERROR");
					if (sbErrore != null) {
						if (sbErrore.getAttribute("code") != null
								&& !sbErrore.getAttribute("code").toString().equals("")) {
							codiceErrore = new Integer(sbErrore.getAttribute("code").toString()).intValue();
						}
					}
				}
			} catch (Exception ex) {
				return -1;
			}
		}
		return codiceErrore;
	}

}
