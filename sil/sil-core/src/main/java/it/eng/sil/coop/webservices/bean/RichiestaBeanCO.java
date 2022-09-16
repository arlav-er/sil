package it.eng.sil.coop.webservices.bean;

import java.math.BigDecimal;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;

public class RichiestaBeanCO {

	private Element rootXML = null;

	private String cittadinanza = "";
	private String datScadenzaPS = "";
	private String motPS = "";
	private String tipoDocPS = "";
	private String numeroDocPS = "";
	private String questuraPS = "";

	private String dataInizioMov = "";
	private String dataFineMov = "";
	private String dataInizioRappPro = "";
	private String dataFinePF = "";
	private String entePrev = "";
	private String contratto = "";
	private String socioLav = "";
	private String lavoroInAgricoltura = "";
	private String lavoroInMobilita = "";
	private String ggLavorativePreviste = "";
	private String tipoLavorazione = "";
	private String codEntePrev = "";
	private String codMonoTempo = "";
	private String dataFinePro = "";

	private String ccnl = "";
	private String livelloInquadramento = "";
	private String codiceOrario = "";
	private String oreSettimaniliMedie = "";
	private String retribuzioneCompenso = "";
	private String qualificaProfessionale = "";
	private String patInail = "";

	private String cfAz = "";
	private String ragSocAz = "";
	private String flgPubbAmm = "";
	private String codComUaz = "";
	private String strCapUaz = "";
	private String strIndirizzoUaz = "";
	private String ccnlAz = "";
	private String codComSedeLegale = "";
	private String capSedeLegale = "";
	private String indirizzoSedeLegale = "";
	private String telAz = "";
	private String faxAz = "";
	private String mailAz = "";
	private String telAzLeg = "";
	private String faxAzLeg = "";
	private String mailAzLeg = "";

	private String codFiscaleLav = "";
	private String codTipoMov = "";
	private String prgMovDaRettificare = "";
	private String prgMovDaProrogare = "";
	private String codMonoTipoContratto = "";

	public RichiestaBeanCO(Document doc) throws Exception {
		this.rootXML = doc.getDocumentElement();
		String path = "codTipoMovimento";
		setCodTipoMov(getNodeValue(rootXML, path));
		path = "CodiceFiscale";
		setCFLav(getNodeValue(getRootXML(), path));
		path = "prgMovimentoDaRettificare";
		setPrgRettifica(getNodeValue(rootXML, path));
		path = "prgMovimentoDaProrogare";
		setPrgProroga(getNodeValue(rootXML, path));
	}

	public Element getRootXML() {
		return this.rootXML;
	}

	public void setRootXML(Element root) {
		this.rootXML = root;
	}

	public String getCittadinanza() {
		return this.cittadinanza;
	}

	public void setCittadinanza(String val) {
		this.cittadinanza = val;
	}

	public String getScadenzaPS() {
		return this.datScadenzaPS;
	}

	public void setScadenzaPS(String val) {
		this.datScadenzaPS = val;
	}

	public String getMotivoPS() {
		return this.motPS;
	}

	public void setMotivoPS(String val) {
		this.motPS = val;
	}

	public String getTipoDocPS() {
		return this.tipoDocPS;
	}

	public void setTipoDocPS(String val) {
		this.tipoDocPS = val;
	}

	public String getNumeroDoc() {
		return this.numeroDocPS;
	}

	public void setNumeroDoc(String val) {
		this.numeroDocPS = val;
	}

	public String getQuesturaPS() {
		return this.questuraPS;
	}

	public void setQuesturaPS(String val) {
		this.questuraPS = val;
	}

	public String getDataInizioMov() {
		return this.dataInizioMov;
	}

	public void setDataInizioMov(String val) {
		this.dataInizioMov = val;
	}

	public String getDataInizioRappPro() {
		return this.dataInizioRappPro;
	}

	public void setDataInizioRappPro(String val) {
		this.dataInizioRappPro = val;
	}

	public String getDataFineMov() {
		return this.dataFineMov;
	}

	public void setDataFineMov(String val) {
		this.dataFineMov = val;
	}

	public String getDataFinePF() {
		return this.dataFinePF;
	}

	public void setDataFinePF(String val) {
		this.dataFinePF = val;
	}

	public String getEntePrev() {
		return this.entePrev;
	}

	public void setEntePrev(String val) {
		this.entePrev = val;
	}

	public String getContratto() {
		return this.contratto;
	}

	public void setContratto(String val) {
		this.contratto = val;
	}

	public String getSocio() {
		return this.socioLav;
	}

	public void setSocio(String val) {
		this.socioLav = val;
	}

	public String getLavoroAgr() {
		return this.lavoroInAgricoltura;
	}

	public void setLavoroAgr(String val) {
		this.lavoroInAgricoltura = val;
	}

	public String getLavoroMobilita() {
		return this.lavoroInMobilita;
	}

	public void setLavoroMobilita(String val) {
		this.lavoroInMobilita = val;
	}

	public String getGiorniAgr() {
		return this.ggLavorativePreviste;
	}

	public void setGiorniAgr(String val) {
		this.ggLavorativePreviste = val;
	}

	public String getTipoLavorazione() {
		return this.tipoLavorazione;
	}

	public void setTipoLavorazione(String val) {
		this.tipoLavorazione = val;
	}

	public String getCodEntePrev() {
		return this.codEntePrev;
	}

	public void setCodEntePrev(String val) {
		this.codEntePrev = val;
	}

	public String getCodMonoTempo() {
		return this.codMonoTempo;
	}

	public void setCodMonoTempo(String val) {
		this.codMonoTempo = val;
	}

	public String getDataFinePro() {
		return this.dataFinePro;
	}

	public void setDataFinePro(String val) {
		this.dataFinePro = val;
	}

	public String getCCNL() {
		return this.ccnl;
	}

	public void setCCNL(String val) {
		this.ccnl = val;
	}

	public String getLivello() {
		return this.livelloInquadramento;
	}

	public void setLivello(String val) {
		this.livelloInquadramento = val;
	}

	public String getOrario() {
		return this.codiceOrario;
	}

	public void setOrario(String val) {
		this.codiceOrario = val;
	}

	public String getOreSettimanali() {
		return this.oreSettimaniliMedie;
	}

	public void setOreSettimanali(String val) {
		this.oreSettimaniliMedie = val;
	}

	public String getRetribuzione() {
		return this.retribuzioneCompenso;
	}

	public void setRetribuzione(String val) {
		this.retribuzioneCompenso = val;
	}

	public String getQualifProf() {
		return this.qualificaProfessionale;
	}

	public void setQualifProf(String val) {
		this.qualificaProfessionale = val;
	}

	public String getPatInail() {
		return this.patInail;
	}

	public void setPatInail(String val) {
		this.patInail = val;
	}

	public String getCFAz() {
		return this.cfAz;
	}

	public void setCFAz(String val) {
		this.cfAz = val;
	}

	public String getRagSocAz() {
		return this.ragSocAz;
	}

	public void setRagSocAz(String val) {
		this.ragSocAz = val;
	}

	public String getPubbAmm() {
		return this.flgPubbAmm;
	}

	public void setPubbAmm(String val) {
		this.flgPubbAmm = val;
	}

	public String getComAZ() {
		return this.codComUaz;
	}

	public void setComAZ(String val) {
		this.codComUaz = val;
	}

	public String getCapAz() {
		return this.strCapUaz;
	}

	public void setCapAz(String val) {
		this.strCapUaz = val;
	}

	public String getIndirizzoAz() {
		return this.strIndirizzoUaz;
	}

	public void setIndirizzoAz(String val) {
		this.strIndirizzoUaz = val;
	}

	public String getCCNLAz() {
		return this.ccnlAz;
	}

	public void setCCNLAz(String val) {
		this.ccnlAz = val;
	}

	public String getComSedeLegale() {
		return this.codComSedeLegale;
	}

	public void setComSedeLegale(String val) {
		this.codComSedeLegale = val;
	}

	public String getCapSedeLegale() {
		return this.capSedeLegale;
	}

	public void setCapSedeLegale(String val) {
		this.capSedeLegale = val;
	}

	public String getIndSedeLegale() {
		return this.indirizzoSedeLegale;
	}

	public void setIndSedeLegale(String val) {
		this.indirizzoSedeLegale = val;
	}

	public String getTelAz() {
		return this.telAz;
	}

	public void setTelAz(String val) {
		this.telAz = val;
	}

	public String getFaxAz() {
		return this.faxAz;
	}

	public void setFaxAz(String val) {
		this.faxAz = val;
	}

	public String getMailAz() {
		return this.mailAz;
	}

	public void setMailAz(String val) {
		this.mailAz = val;
	}

	public String getTelAzLeg() {
		return this.telAzLeg;
	}

	public void setTelAzLeg(String val) {
		this.telAzLeg = val;
	}

	public String getFaxAzLeg() {
		return this.faxAzLeg;
	}

	public void setFaxAzLeg(String val) {
		this.faxAzLeg = val;
	}

	public String getMailAzLeg() {
		return this.mailAzLeg;
	}

	public void setMailAzLeg(String val) {
		this.mailAzLeg = val;
	}

	public String getCodTipoMov() {
		return this.codTipoMov;
	}

	public void setCodTipoMov(String val) {
		this.codTipoMov = val;
	}

	public String getCFLav() {
		return this.codFiscaleLav;
	}

	public void setCFLav(String val) {
		this.codFiscaleLav = val;
	}

	public String getPrgRettifica() {
		return this.prgMovDaRettificare;
	}

	public void setPrgRettifica(String val) {
		this.prgMovDaRettificare = val;
	}

	public String getPrgProroga() {
		return this.prgMovDaProrogare;
	}

	public void setPrgProroga(String val) {
		this.prgMovDaProrogare = val;
	}

	public String getCodMonoTipoContratto() {
		return this.codMonoTipoContratto;
	}

	public void setCodMonoTipoContratto(String val) {
		this.codMonoTipoContratto = val;
	}

	public String getNodeValue(Element root, String path) throws Exception {
		String valore = "";
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node currNode = (Node) xpath.evaluate(path, root, XPathConstants.NODE);
		if (currNode != null) {
			Node n = currNode.getFirstChild();
			if (n != null) {
				valore = n.getNodeValue();
			}
		}
		return valore;
	}

	public SourceBean getInfoMov(BigDecimal prgMov, BigDecimal user, TransactionQueryExecutor txExec) throws Exception {
		SourceBean row = null;
		Object[] params = new Object[] { prgMov, user };
		row = (SourceBean) txExec.executeQuery("GET_INFO_MOV_WS_PORTALE", params, "SELECT");
		return row;
	}

	public boolean getContrattoValido(String codTipoContratto) throws Exception {
		Object[] params = new Object[] { codTipoContratto };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("RECUPERO_COD_MONOTIPOASS", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			String codTipoContrattoDB = row.getAttribute("CODTIPOCONTRATTO") != null
					? (String) row.getAttribute("CODTIPOCONTRATTO")
					: "";
			if (codTipoContrattoDB.equals("")) {
				return false;
			} else {
				String codMonoTipo = row.getAttribute("CODMONOTIPO") != null ? (String) row.getAttribute("CODMONOTIPO")
						: "";
				if (!codMonoTipo.equals("")) {
					setCodMonoTipoContratto(codMonoTipo);
				}
				return true;
			}
		} else {
			return false;
		}
	}

	public String getInfoCittadinanza(String codice) throws Exception {
		String flgCEE = "";
		Object[] params = new Object[] { codice };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_CITTADINANZA_WS_PORTALE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			flgCEE = row.getAttribute("flgcee") != null ? (String) row.getAttribute("flgcee") : "";
		}
		return flgCEE;
	}

	public String getInfoOrario(String codice) throws Exception {
		String ret = "";
		Object[] params = new Object[] { codice };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_ORARIO_WS_PORTALE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			ret = row.getAttribute("codmonoorario") != null ? (String) row.getAttribute("codmonoorario") : "";
		}
		return ret;
	}

	public String getInfoQualifica(String codice) throws Exception {
		String mansioneSil = "";
		Object[] params = new Object[] { codice };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_MANSIONE_WS_PORTALE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			mansioneSil = row.getAttribute("codmansione") != null ? (String) row.getAttribute("codmansione") : "";
		}
		return mansioneSil;
	}

	public boolean getInfoCCNL(String codice) throws Exception {
		Object[] params = new Object[] { codice };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_CCNL_WS_PORTALE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			String codccnl = row.getAttribute("codccnl") != null ? (String) row.getAttribute("codccnl") : "";
			if (!codccnl.equals("")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public String getInfoSettore(String codice) throws Exception {
		Object[] params = new Object[] { codice };
		String atecoSil = "";
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_ATECO_WS_PORTALE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			atecoSil = row.getAttribute("codateco") != null ? (String) row.getAttribute("codateco") : "";
		}
		return atecoSil;
	}

	public String getInfoComune(String codice) throws Exception {
		Object[] params = new Object[] { codice };
		String descComune = "";
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_COMUNE_WS_PORTALE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			descComune = row.getAttribute("strdenominazione") != null ? (String) row.getAttribute("strdenominazione")
					: "";
		}
		return descComune;
	}

}
