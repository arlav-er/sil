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
import it.eng.sil.coop.webservices.movimenti.constant.ErroriUniSommConstant;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ValidatorGeneral;
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

public class RichiestaBeanUniSomm extends RichiestaBeanCO {

	private String flgAzEstera = "";
	private String codAtecoUtiliz = "";
	private String numAgenziaSomm = "";
	private String numMatricola = "";
	private String indennitaDisponibilita = "";

	private String cfAzUtiliz = "";
	private String ragSocAzUtiliz = "";

	private String codComSedeLegaleUtiliz = "";
	private String capSedeLegaleUtiliz = "";
	private String indirizzoSedeLegaleUtiliz = "";
	private String telAzLegUtiliz = "";
	private String faxAzLegUtiliz = "";
	private String mailAzLegUtiliz = "";

	private String codComSedeLavoroUtiliz = "";
	private String capSedeLavoroUtiliz = "";
	private String indirizzoSedeLavoroUtiliz = "";
	private String telAzLavoroUtiliz = "";
	private String faxAzLavoroUtiliz = "";
	private String mailAzLavoroUtiliz = "";
	private String numIscrizioneAlbo = "";
	private String flgEsteraOperanteInItalia = "";
	private String dataInizioMissione = "";
	private String dataFineMissione = "";
	private String descAttivita = "";
	private String descAttivitaAgric = "";
	private String numContrattoSomm = "";
	private String dataInizioContrattoSomm = "";
	private String dataFineContrattoSomm = "";

	public RichiestaBeanUniSomm(Document doc) throws Exception {
		super(doc);
		caricaLavoratore();
		caricaRapporto();
		caricaAzienda();
		caricaAziendaUtilzzatrice();
	}

	public String getCFAzUtiliz() {
		return this.cfAzUtiliz;
	}

	public void setCFAzUtiliz(String val) {
		this.cfAzUtiliz = val;
	}

	public String getRagSocAzUtiliz() {
		return this.ragSocAzUtiliz;
	}

	public void setRagSocAzUtiliz(String val) {
		this.ragSocAzUtiliz = val;
	}

	public String getAzEstera() {
		return this.flgAzEstera;
	}

	public void setAzEstera(String val) {
		this.flgAzEstera = val;
	}

	public String getAtecoAzUtiliz() {
		return this.codAtecoUtiliz;
	}

	public void setAtecoAzUtiliz(String val) {
		this.codAtecoUtiliz = val;
	}

	public String getNumAgenziaSomm() {
		return this.numAgenziaSomm;
	}

	public void setNumAgenziaSomm(String val) {
		this.numAgenziaSomm = val;
	}

	public String getMatricolaSomm() {
		return this.numMatricola;
	}

	public void setMatricolaSomm(String val) {
		this.numMatricola = val;
	}

	public String getIndennitaDispo() {
		return this.indennitaDisponibilita;
	}

	public void setIndennitaDispo(String val) {
		this.indennitaDisponibilita = val;
	}

	public String getComSedeLegaleUtiliz() {
		return this.codComSedeLegaleUtiliz;
	}

	public void setComSedeLegaleUtiliz(String val) {
		this.codComSedeLegaleUtiliz = val;
	}

	public String getCapSedeLegaleUtiliz() {
		return this.capSedeLegaleUtiliz;
	}

	public void setCapSedeLegaleUtiliz(String val) {
		this.capSedeLegaleUtiliz = val;
	}

	public String getIndSedeLegaleUtiliz() {
		return this.indirizzoSedeLegaleUtiliz;
	}

	public void setIndSedeLegaleUtiliz(String val) {
		this.indirizzoSedeLegaleUtiliz = val;
	}

	public String getTelAzLegUtiliz() {
		return this.telAzLegUtiliz;
	}

	public void setTelAzLegUtiliz(String val) {
		this.telAzLegUtiliz = val;
	}

	public String getFaxAzLegUtiliz() {
		return this.faxAzLegUtiliz;
	}

	public void setFaxAzLegUtiliz(String val) {
		this.faxAzLegUtiliz = val;
	}

	public String getMailAzLegUtiliz() {
		return this.mailAzLegUtiliz;
	}

	public void setMailAzLegUtiliz(String val) {
		this.mailAzLegUtiliz = val;
	}

	public String getComSedeLavoroUtiliz() {
		return this.codComSedeLavoroUtiliz;
	}

	public void setComSedeLavoroUtiliz(String val) {
		this.codComSedeLavoroUtiliz = val;
	}

	public String getCapSedeLavoroUtiliz() {
		return this.capSedeLavoroUtiliz;
	}

	public void setCapSedeLavoroUtiliz(String val) {
		this.capSedeLavoroUtiliz = val;
	}

	public String getIndSedeLavoroUtiliz() {
		return this.indirizzoSedeLavoroUtiliz;
	}

	public void setIndSedeLavoroUtiliz(String val) {
		this.indirizzoSedeLavoroUtiliz = val;
	}

	public String getTelAzLavoroUtiliz() {
		return this.telAzLavoroUtiliz;
	}

	public void setTelAzLavoroUtiliz(String val) {
		this.telAzLavoroUtiliz = val;
	}

	public String getFaxAzLavoroUtiliz() {
		return this.faxAzLavoroUtiliz;
	}

	public void setFaxAzLavoroUtiliz(String val) {
		this.faxAzLavoroUtiliz = val;
	}

	public String getMailAzLavoroUtiliz() {
		return this.mailAzLavoroUtiliz;
	}

	public void setMailAzLavoroUtiliz(String val) {
		this.mailAzLavoroUtiliz = val;
	}

	public String getIscrizioneAlbo() {
		return this.numIscrizioneAlbo;
	}

	public void setIscrizioneAlbo(String val) {
		this.numIscrizioneAlbo = val;
	}

	public String getFlgEsteraInItalia() {
		return this.flgEsteraOperanteInItalia;
	}

	public void setFlgEsteraInItalia(String val) {
		this.flgEsteraOperanteInItalia = val;
	}

	public String getDataInizioMissione() {
		return this.dataInizioMissione;
	}

	public void setDataInizioMissione(String val) {
		this.dataInizioMissione = val;
	}

	public String getDataFineMissione() {
		return this.dataFineMissione;
	}

	public void setDataFineMissione(String val) {
		this.dataFineMissione = val;
	}

	public String getDescAttivita() {
		return this.descAttivita;
	}

	public void setDescAttivita(String val) {
		this.descAttivita = val;
	}

	public String getDescAttivitaAgric() {
		return this.descAttivitaAgric;
	}

	public void setDescAttivitaAgric(String val) {
		this.descAttivitaAgric = val;
	}

	public String getNumContrattoSomm() {
		return this.numContrattoSomm;
	}

	public void setNumContrattoSomm(String val) {
		this.numContrattoSomm = val;
	}

	public String getDataInizioContrattoSomm() {
		return this.dataInizioContrattoSomm;
	}

	public void setDataInizioContrattoSomm(String val) {
		this.dataInizioContrattoSomm = val;
	}

	public String getDataFineContrattoSomm() {
		return this.dataFineContrattoSomm;
	}

	public void setDataFineContrattoSomm(String val) {
		this.dataFineContrattoSomm = val;
	}

	private void caricaLavoratore() throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();

		String path = null;
		path = "UniSomm/Lavoratore/AnagraficaCompleta";
		Element nodeAnag = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
		if (nodeAnag != null) {
			path = "cittadinanza";
			setCittadinanza(getNodeValue(nodeAnag, path));
		}
		path = "UniSomm/Lavoratore/extraCE";
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
		String pathRapporto = null;
		Element nodeRapporto = null;

		pathRapporto = "UniSomm/AgenziaSomministrazione/RapportoAgenziaSomm";

		XPath xpath = XPathFactory.newInstance().newXPath();
		nodeRapporto = (Element) xpath.evaluate(pathRapporto, getRootXML(), XPathConstants.NODE);

		if (nodeRapporto != null) {
			if (nodeRapporto.getAttribute("dataInizioSomm") != null
					&& !nodeRapporto.getAttribute("dataInizioSomm").toString().equals("")) {
				String dtInizioMov = nodeRapporto.getAttribute("dataInizioSomm");
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

			if (nodeRapporto.getAttribute("dataFineSomm") != null
					&& !nodeRapporto.getAttribute("dataFineSomm").toString().equals("")) {
				String dtFineMov = nodeRapporto.getAttribute("dataFineSomm");
				setDataFineMov(
						dtFineMov.substring(8, 10) + "/" + dtFineMov.substring(5, 7) + "/" + dtFineMov.substring(0, 4));
			}

			if (nodeRapporto.getAttribute("entePrevidenziale") != null
					&& !nodeRapporto.getAttribute("entePrevidenziale").toString().equals("")) {
				setEntePrev(nodeRapporto.getAttribute("entePrevidenziale"));
			}

			if (nodeRapporto.getAttribute("codiceEntePrevidenziale") != null
					&& !nodeRapporto.getAttribute("codiceEntePrevidenziale").toString().equals("")) {
				setCodEntePrev(nodeRapporto.getAttribute("codiceEntePrevidenziale"));
			}

			if (nodeRapporto.getAttribute("TipologiaContrattuale") != null
					&& !nodeRapporto.getAttribute("TipologiaContrattuale").toString().equals("")) {
				setContratto(nodeRapporto.getAttribute("TipologiaContrattuale"));
			}

			if (nodeRapporto.getAttribute("codMonoTempo") != null
					&& !nodeRapporto.getAttribute("codMonoTempo").toString().equals("")) {
				setCodMonoTempo(nodeRapporto.getAttribute("codMonoTempo"));
			} else {
				if (getDataFineMov().equals("")) {
					setCodMonoTempo(CodMonoTempoEnum.INDETERMINATO.getCodice());
				} else {
					setCodMonoTempo(CodMonoTempoEnum.DETERMINATO.getCodice());
				}
			}

			if (nodeRapporto.getAttribute("numeroAgenziaSomm") != null
					&& !nodeRapporto.getAttribute("numeroAgenziaSomm").toString().equals("")) {
				setNumAgenziaSomm(nodeRapporto.getAttribute("numeroAgenziaSomm"));
			}

			if (nodeRapporto.getAttribute("numeroMatricola") != null
					&& !nodeRapporto.getAttribute("numeroMatricola").toString().equals("")) {
				setMatricolaSomm(nodeRapporto.getAttribute("numeroMatricola"));
			}

			if (nodeRapporto.getAttribute("indennitaDisponibilita") != null
					&& !nodeRapporto.getAttribute("indennitaDisponibilita").toString().equals("")) {
				setIndennitaDispo(nodeRapporto.getAttribute("indennitaDisponibilita"));
			}

			if (nodeRapporto.getAttribute("lavInMobilita") != null
					&& !nodeRapporto.getAttribute("lavInMobilita").toString().equals("")) {
				setLavoroMobilita(nodeRapporto.getAttribute("lavInMobilita"));
			}

			if (nodeRapporto.getAttribute("dataFinePeriodoFormativo") != null
					&& !nodeRapporto.getAttribute("dataFinePeriodoFormativo").toString().equals("")) {
				String dtFinePF = nodeRapporto.getAttribute("dataFinePeriodoFormativo");
				if (!dtFinePF.equals("")) {
					setDataFinePF(dtFinePF.substring(8, 10) + "/" + dtFinePF.substring(5, 7) + "/"
							+ dtFinePF.substring(0, 4));
				}
			}
		}
	}

	private void caricaAzienda() throws Exception {

		XPath xpath = XPathFactory.newInstance().newXPath();

		String path = null;
		Element nodeDatore = null;
		Element nodeSedeLegale = null;
		Element nodeSedeLavoro = null;
		path = "UniSomm/AgenziaSomministrazione";

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
			if (nodeDatore.getAttribute("agenziaestera") != null
					&& !nodeDatore.getAttribute("agenziaestera").toString().equals("")) {
				setAzEstera(nodeDatore.getAttribute("agenziaestera"));
			}
			if (nodeDatore.getAttribute("numeroIscrizioneAlbo") != null
					&& !nodeDatore.getAttribute("numeroIscrizioneAlbo").toString().equals("")) {
				setIscrizioneAlbo(nodeDatore.getAttribute("numeroIscrizioneAlbo"));
			}
		}
		path = "UniSomm/AgenziaSomministrazione/SedeLegale";
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
		path = "UniSomm/AgenziaSomministrazione/SedeLavoro";
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

	private void caricaAziendaUtilzzatrice() throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();

		String path = null;
		Element nodeDittaUtiliz = null;
		Element nodeSedeLegaleUtiliz = null;
		Element nodeSedeLavoroUtiliz = null;
		Element nodeRapporto = null;
		path = "UniSomm/DittaUtilizzatrice";
		nodeDittaUtiliz = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
		if (nodeDittaUtiliz != null) {
			if (nodeDittaUtiliz.getAttribute("codiceFiscale") != null
					&& !nodeDittaUtiliz.getAttribute("codiceFiscale").toString().equals("")) {
				setCFAzUtiliz(nodeDittaUtiliz.getAttribute("codiceFiscale"));
			}
			if (nodeDittaUtiliz.getAttribute("denominazione") != null
					&& !nodeDittaUtiliz.getAttribute("denominazione").toString().equals("")) {
				setRagSocAzUtiliz(nodeDittaUtiliz.getAttribute("denominazione"));
			}
		}

		path = "UniSomm/DittaUtilizzatrice/SedeLegale";
		nodeSedeLegaleUtiliz = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
		if (nodeSedeLegaleUtiliz != null) {
			path = "Comune";
			setComSedeLegaleUtiliz(getNodeValue(nodeSedeLegaleUtiliz, path));
			path = "cap";
			setCapSedeLegaleUtiliz(getNodeValue(nodeSedeLegaleUtiliz, path));
			path = "Indirizzo";
			setIndSedeLegaleUtiliz(getNodeValue(nodeSedeLegaleUtiliz, path));
			path = "e-mail";
			setMailAzLegUtiliz(getNodeValue(nodeSedeLegaleUtiliz, path));
			path = "telefono";
			setTelAzLegUtiliz(getNodeValue(nodeSedeLegaleUtiliz, path));
			path = "fax";
			setFaxAzLegUtiliz(getNodeValue(nodeSedeLegaleUtiliz, path));
		}

		path = "UniSomm/DittaUtilizzatrice/SedeLavoro";
		nodeSedeLavoroUtiliz = (Element) xpath.evaluate(path, getRootXML(), XPathConstants.NODE);
		if (nodeSedeLavoroUtiliz != null) {
			path = "Comune";
			setComSedeLavoroUtiliz(getNodeValue(nodeSedeLavoroUtiliz, path));
			path = "cap";
			setCapSedeLavoroUtiliz(getNodeValue(nodeSedeLavoroUtiliz, path));
			path = "Indirizzo";
			setIndSedeLavoroUtiliz(getNodeValue(nodeSedeLavoroUtiliz, path));
			path = "e-mail";
			setMailAzLavoroUtiliz(getNodeValue(nodeSedeLavoroUtiliz, path));
			path = "telefono";
			setTelAzLavoroUtiliz(getNodeValue(nodeSedeLavoroUtiliz, path));
			path = "fax";
			setFaxAzLavoroUtiliz(getNodeValue(nodeSedeLavoroUtiliz, path));
		}

		path = "Settore";
		setAtecoAzUtiliz(getNodeValue(nodeDittaUtiliz, path));

		path = "PubblicaAmministrazione";
		setPubbAmm(getNodeValue(nodeDittaUtiliz, path));
		path = "DittaUtilizzatriceEsteraOperanteInItalia";
		setFlgEsteraInItalia(getNodeValue(nodeDittaUtiliz, path));

		String numContratto = nodeDittaUtiliz.getAttribute("numeroContrattoSomm");
		if (numContratto != null && !numContratto.equals("")) {
			setNumContrattoSomm(numContratto);
		}
		String dataInizioContrattoSomm = nodeDittaUtiliz.getAttribute("dataInizioContrattoSomm");
		if (dataInizioContrattoSomm != null && !dataInizioContrattoSomm.equals("")) {
			setDataInizioContrattoSomm(dataInizioContrattoSomm.substring(8, 10) + "/"
					+ dataInizioContrattoSomm.substring(5, 7) + "/" + dataInizioContrattoSomm.substring(0, 4));
		}

		String dataFineContrattoSomm = nodeDittaUtiliz.getAttribute("dataFineContrattoSomm");
		if (dataFineContrattoSomm != null && !dataFineContrattoSomm.equals("")) {
			setDataFineContrattoSomm(dataFineContrattoSomm.substring(8, 10) + "/"
					+ dataFineContrattoSomm.substring(5, 7) + "/" + dataFineContrattoSomm.substring(0, 4));
		}

		if (getCodTipoMov().equalsIgnoreCase("AVV")) {
			path = "InizioContratto/Inizio";
			nodeRapporto = (Element) xpath.evaluate(path, nodeDittaUtiliz, XPathConstants.NODE);
			if (nodeRapporto != null) {
				path = "PatINAIL";
				setPatInail(getNodeValue(nodeRapporto, path));
				path = "tipoOrario";
				Element nodeOrarioContratto = (Element) xpath.evaluate(path, nodeRapporto, XPathConstants.NODE);
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
				path = "ccnl";
				setCCNL(getNodeValue(nodeRapporto, path));
				path = "qualificaProfessionale";
				setQualifProf(getNodeValue(nodeRapporto, path));
				path = "livelloInquadramento";
				setLivello(getNodeValue(nodeRapporto, path));
				String datInizioMis = nodeRapporto.getAttribute("dataInizioMissione");
				if (datInizioMis != null && !datInizioMis.equals("")) {
					setDataInizioMissione(datInizioMis.substring(8, 10) + "/" + datInizioMis.substring(5, 7) + "/"
							+ datInizioMis.substring(0, 4));
				}
				String datFineMis = nodeRapporto.getAttribute("dataFineMissione");
				if (datFineMis != null && !datFineMis.equals("")) {
					setDataFineMissione(datFineMis.substring(8, 10) + "/" + datFineMis.substring(5, 7) + "/"
							+ datFineMis.substring(0, 4));
				}
				if (nodeRapporto.getAttribute("ggLavorativePreviste") != null
						&& !nodeRapporto.getAttribute("ggLavorativePreviste").toString().equals("")) {
					setGiorniAgr(nodeRapporto.getAttribute("ggLavorativePreviste"));
				}
				if (nodeRapporto.getAttribute("descrizioneAttivita") != null
						&& !nodeRapporto.getAttribute("descrizioneAttivita").toString().equals("")) {
					setDescAttivita(nodeRapporto.getAttribute("descrizioneAttivita"));
				}
				if (nodeRapporto.getAttribute("descrizioneAttivitaAgricol") != null
						&& !nodeRapporto.getAttribute("descrizioneAttivitaAgricol").toString().equals("")) {
					setDescAttivitaAgric(nodeRapporto.getAttribute("descrizioneAttivitaAgricol"));
				}
				if (nodeRapporto.getAttribute("lavoroInAgricoltura") != null
						&& !nodeRapporto.getAttribute("lavoroInAgricoltura").toString().equals("")) {
					setLavoroAgr(nodeRapporto.getAttribute("lavoroInAgricoltura"));
				}
			}
		} else {
			path = "InizioContratto/Proroga";
			nodeRapporto = (Element) xpath.evaluate(path, nodeDittaUtiliz, XPathConstants.NODE);
			if (nodeRapporto != null) {
				path = "dataFineProroga";
				String dtFinePro = nodeRapporto.getAttribute(path) != null ? nodeRapporto.getAttribute(path).toString()
						: "";
				if (!dtFinePro.equals("")) {
					setDataFinePro(dtFinePro.substring(8, 10) + "/" + dtFinePro.substring(5, 7) + "/"
							+ dtFinePro.substring(0, 4));
				}
				path = "dataInizioMissione";
				String datInizioMis = nodeRapporto.getAttribute(path) != null
						? nodeRapporto.getAttribute(path).toString()
						: "";
				if (!datInizioMis.equals("")) {
					setDataInizioMissione(datInizioMis.substring(8, 10) + "/" + datInizioMis.substring(5, 7) + "/"
							+ datInizioMis.substring(0, 4));
				}
				path = "dataFineMissione";
				String datFineMis = nodeRapporto.getAttribute(path) != null ? nodeRapporto.getAttribute(path).toString()
						: "";
				if (datFineMis != null && !datFineMis.equals("")) {
					setDataFineMissione(datFineMis.substring(8, 10) + "/" + datFineMis.substring(5, 7) + "/"
							+ datFineMis.substring(0, 4));
				}
			}
		}
	}

	public String checkDati() throws Exception {
		String erroreXML = null;

		if (!(getContratto().equals(""))) {
			if (!getContrattoValido(getContratto())) {
				erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_CONTRATTO_INESISTENTE,
						"Codice tipologia contrattuale inesistente");
				return erroreXML;
			}
		}

		if (!getCittadinanza().equals("")) {
			String flgCEE = getInfoCittadinanza(getCittadinanza());
			if (flgCEE.equalsIgnoreCase("N")) {
				if (getTipoDocPS().equals("") || getNumeroDoc().equals("") || getScadenzaPS().equals("")
						|| getMotivoPS().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_PERMESSO_SOGGIORNO,
							"Dati permesso di soggiorno mancanti o incompleti");
					return erroreXML;
				}
				if (getTipoDocPS().equalsIgnoreCase("3") || getTipoDocPS().equalsIgnoreCase("4")) {
					if (!getQuesturaPS().equals("")) {
						erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_PERMESSO_SOGGIORNO,
								"Dati permesso di soggiorno mancanti o incompleti");
						return erroreXML;
					}
				}
			}
		}

		if (getTelAzLeg().equals("") && getMailAzLeg().equals("") && getFaxAzLeg().equals("")) {
			erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_SEDE_LEGALE,
					"Dati della sede legale dell'azienda di somministrazione mancanti o incompleti");
			return erroreXML;
		}

		if (getTelAz().equals("") && getMailAz().equals("") && getFaxAz().equals("")) {
			erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_SEDE_OPERATIVA,
					"Dati della sede operativa dell'azienda di somministrazione mancanti o incompleti");
			return erroreXML;
		}

		if (!getOrario().equals("")) {
			String tipoOrario = getInfoOrario(getOrario());
			if (tipoOrario.equals("")) {
				erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_ORARIO_INESISTENTE,
						"Codice tipo orario inesistente");
				return erroreXML;
			}
			if (tipoOrario.equalsIgnoreCase("P")) {
				int nOreSett = 0;
				if (!getOreSettimanali().equals("")) {
					nOreSett = new Integer(getOreSettimanali()).intValue();
				}
				if (nOreSett <= 0) {
					erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_ORE_SETTIMANALI_MANCANTI,
							"Ore settimanali non indicate");
					return erroreXML;
				}
			}
		}

		if (!getQualifProf().equals("")) {
			String mansioneSil = getInfoQualifica(getQualifProf());
			if (mansioneSil.equals("")) {
				erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_MANSIONE_INESISTENTE,
						"Codice qualifica professionale inesistente");
				return erroreXML;
			} else {
				setQualifProf(mansioneSil);
			}
		}

		if (!getCCNL().equals("")) {
			if (!getInfoCCNL(getCCNL())) {
				erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_CCNL_INESISTENTE,
						"Codice CCNL inesistente");
				return erroreXML;
			}
		}

		if (!getAtecoAzUtiliz().equals("")) {
			String atecoSil = getInfoSettore(getAtecoAzUtiliz());
			if (atecoSil.equals("")) {
				erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_ATECO_INESISTENTE,
						"Codice ATECO utilizzatrice inesistente");
				return erroreXML;
			} else {
				setAtecoAzUtiliz(atecoSil);
			}
		}

		if (getLavoroAgr().equalsIgnoreCase("SI")) {
			int ggAgricolturta = 0;
			if (!getGiorniAgr().equals("")) {
				ggAgricolturta = new Integer(getGiorniAgr()).intValue();
			}

			if (ggAgricolturta <= 0) {
				erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_GIORNATE_AGRIC_MANCANTI,
						"Giornate previste in agricoltura non indicate");
				return erroreXML;
			}
		}

		if (getCodTipoMov().equalsIgnoreCase("PRO")) {
			if (getPrgRettifica().equals("")) {
				if (getPrgProroga().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_MOV_DA_PROROGARE_MANCANTE,
							"Errore proroga: movimento da prorogare non indicato");
					return erroreXML;
				}
			}
		}

		if (getTelAzLegUtiliz().equals("") && getFaxAzLegUtiliz().equals("") && getMailAzLegUtiliz().equals("")) {
			erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_SEDE_LEGALE_UTILIZ,
					"Dati della sede legale dell'azienda utilizzatrice mancanti o incompleti");
			return erroreXML;
		}

		if (getTelAzLavoroUtiliz().equals("") && getFaxAzLavoroUtiliz().equals("")
				&& getMailAzLavoroUtiliz().equals("")) {
			erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_SEDE_OPERATIVA_UTILIZ,
					"Dati della sede operativa dell'azienda utilizzatrice mancanti o incompleti");
			return erroreXML;
		}

		String codMonoTempoMov = getCodMonoTempo();
		if (getCodTipoMov().equalsIgnoreCase("AVV")) {
			if (codMonoTempoMov.equalsIgnoreCase(CodMonoTempoEnum.DETERMINATO.getCodice())) {
				if (getDataFineMov().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_DATA_FINE_MANCANTE_ERRATA_TD,
							"Data fine mancante o precedente a data inizio");
					return erroreXML;
				}

				if (DateUtils.compare(getDataFineMov(), getDataInizioMov()) < 0) {
					erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_DATA_FINE_MANCANTE_ERRATA_TD,
							"Data fine mancante o precedente a data inizio");
					return erroreXML;
				}

				if (getDataFineMissione().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_DATA_FINE_MISSIONE,
							"Data fine missione mancante o precedente a data inizio");
					return erroreXML;
				}

				if (!getDataInizioMissione().equals("") && !getDataFineMissione().equals("")) {
					if (DateUtils.compare(getDataFineMissione(), getDataInizioMissione()) < 0) {
						erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_DATA_FINE_MISSIONE,
								"Data fine missione mancante o precedente a data inizio");
						return erroreXML;
					}
				}
			}
		} else {
			if (getCodTipoMov().equalsIgnoreCase("PRO")) {
				if (codMonoTempoMov.equals(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
					erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_PROROGA_TI,
							"Errore proroga: contratto a tempo indeterminato non prorogabile");
					return erroreXML;
				}

				if (getDataFinePro().equals("")) {
					erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_DATA_FINE_MANCANTE_ERRATA_TD,
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

				if (!getDataInizioMissione().equals("") && !getDataFineMissione().equals("")) {
					if (DateUtils.compare(getDataFineMissione(), getDataInizioMissione()) < 0) {
						erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_DATA_FINE_MISSIONE,
								"Data fine missione mancante o precedente a data inizio");
						return erroreXML;
					}
				}
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
				erroreXML = Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_DATA_FINE_PERIODO_FORMATIVO,
						"Errore su data fine periodo formativo");
				return erroreXML;
			} else {
				if (getCodTipoMov().equalsIgnoreCase("AVV")) {
					if (!getDataInizioMov().equals("")) {
						if (DateUtils.compare(getDataFinePF(), getDataInizioMov()) <= 0) {
							erroreXML = Utils.createXMLRisposta(
									ErroriUniSommConstant.ERRORE_DATA_FINE_PERIODO_FORMATIVO,
									"Errore su data fine periodo formativo");
							return erroreXML;
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
		String ccnlMovUtiliz = "";
		String patInail = "";
		String cfAz = "";
		String ragSocAz = "";
		String flgPubbAmm = "";
		String flgAzEstera = "";
		String codComUaz = "";
		String strCapUaz = "";
		String strIndirizzoUaz = "";
		String settoreUtiliz = "";
		String luogoLavoro = "";
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
		String cfAzUtiliz = "";
		String strNumAlboInterinali = "";
		String retribuzioneMen = "";
		String flgLavMob = "";
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
		ccnlMovUtiliz = this.getCCNL();
		if (ccnlMovUtiliz.equals("")) {
			ccnlMovUtiliz = "NT";
		}
		patInail = this.getPatInail();
		cfAz = this.getCFAz();
		ragSocAz = this.getRagSocAz();
		settoreUtiliz = this.getAtecoAzUtiliz();
		flgPubbAmm = this.getPubbAmm();
		flgAzEstera = this.getAzEstera();
		strNumAlboInterinali = this.getIscrizioneAlbo();
		if (flgPubbAmm.equalsIgnoreCase("SI")) {
			flgPubbAmm = "S";
		} else {
			flgPubbAmm = "N";
		}
		if (flgAzEstera.equalsIgnoreCase("SI")) {
			flgAzEstera = "S";
		} else {
			flgAzEstera = "N";
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

		record.put("CODAZTIPOAZIENDA", "INT");
		if (!strNumAlboInterinali.equals("")) {
			record.put("STRAZNUMALBOINTERINALI", strNumAlboInterinali);
		}

		if (!cfAz.equals("")) {
			record.put("STRAZCODICEFISCALE", cfAz);
		}

		if (!ragSocAz.equals("")) {
			record.put("STRAZRAGIONESOCIALE", ragSocAz);
		}

		if (!flgPubbAmm.equals("")) {
			record.put("FLGPA", flgPubbAmm);
		}

		if (!flgAzEstera.equals("")) {
			record.put("FLGAZESTERA", flgAzEstera);
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

		if (!codComSedeLegale.equals("")) {
			record.put("CODUALCOM", codComSedeLegale);
		}

		if (!indirizzoSedeLegale.equals("")) {
			record.put("STRUALINDIRIZZO", indirizzoSedeLegale);
		}

		if (!patInail.equals("")) {
			record.put("STRPATINAIL", patInail);
		}

		record.put("CODAZCCNL", "267");
		record.put("CODAZATECO", "781000");

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

		if (!ccnlMovUtiliz.equals("")) {
			record.put("CODCCNL", ccnlMovUtiliz);
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

		if (!this.getNumAgenziaSomm().equals("")) {
			record.put("STRNUMAGSOMMINISTRAZIONE", this.getNumAgenziaSomm());
		}

		if (!this.getMatricolaSomm().equals("")) {
			record.put("STRMATRICOLA", this.getMatricolaSomm());
		}

		cfAzUtiliz = this.getCFAzUtiliz();
		if (cfAzUtiliz.equals("") || cfAzUtiliz.equalsIgnoreCase(cfAz)) {
			record.put("FLGASSPROPRIA", "S");
		} else {
			record.put("FLGASSPROPRIA", "N");
		}

		if (!cfAzUtiliz.equals("")) {
			record.put("STRAZINTCODICEFISCALE", cfAzUtiliz);
		}
		if (!settoreUtiliz.equals("")) {
			record.put("CODAZINTATECO", settoreUtiliz);
		}
		if (!this.getRagSocAzUtiliz().equals("")) {
			record.put("STRAZINTRAGIONESOCIALE", this.getRagSocAzUtiliz());
			luogoLavoro = luogoLavoro + this.getRagSocAzUtiliz();
		}

		if (!this.getIndSedeLavoroUtiliz().equals("")) {
			record.put("STRUAINTINDIRIZZO", this.getIndSedeLavoroUtiliz());
			luogoLavoro = luogoLavoro + " - " + this.getIndSedeLavoroUtiliz();
		}

		if (!this.getComSedeLavoroUtiliz().equals("")) {
			record.put("CODUAINTCOM", this.getComSedeLavoroUtiliz());
			String denominazioneUtiliz = getInfoComune(this.getComSedeLavoroUtiliz());
			luogoLavoro = luogoLavoro + " (" + denominazioneUtiliz + ")";
		}

		if (!this.getCapSedeLavoroUtiliz().equals("")) {
			record.put("STRUAINTCAP", this.getCapSedeLavoroUtiliz());
		}

		if (!luogoLavoro.equals("")) {
			record.put("STRLUOGODILAVORO", luogoLavoro);
		}

		if (!this.getCodTipoMov().equals("")) {
			record.put("CODTIPOMIS", this.getCodTipoMov());
		}

		if (!this.getDataInizioMissione().equals("")) {
			record.put("DATINIZIORAPLAV", this.getDataInizioMissione());
		}

		if (!this.getDataFineMissione().equals("")) {
			record.put("DATFINERAPLAV", this.getDataFineMissione());
		}

		if (!ccnlMovUtiliz.equals("")) {
			record.put("CODAZINTCCNL", ccnlMovUtiliz);
		}

		if (flgPubbAmm.equals("S")) {
			record.put("CODAZINTTIPOAZIENDA", "PA");
		} else {
			if (flgLavAgr.equalsIgnoreCase("S")) {
				record.put("CODAZINTTIPOAZIENDA", "AGR");
			} else {
				record.put("CODAZINTTIPOAZIENDA", "AZI");
			}
		}

		if (!this.getDataInizioContrattoSomm().equals("")) {
			record.put("DATAZINTINIZIOCONTRATTO", this.getDataInizioContrattoSomm());
		}
		if (!this.getDataFineContrattoSomm().equals("")) {
			record.put("DATAZINTFINECONTRATTO", this.getDataFineContrattoSomm());
		}

		if (!this.getNumContrattoSomm().equals("")) {
			record.put("STRAZINTNUMCONTRATTO", this.getNumContrattoSomm());
		}

		if (flgLavMob.equalsIgnoreCase("SI")) {
			flgLavMob = "S";
		} else {
			flgLavMob = "N";
		}
		record.put("FLGLAVOROINMOBILITA", flgLavMob);

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
