package it.eng.sil.module.pi3;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import com.nttdata._2012.Pi3.ClassificationScheme;
import com.nttdata._2012.Pi3.Document;
import com.nttdata._2012.Pi3.Project;

import it.eng.sil.bean.Documento;

public class CreaDocumentPi3Bean implements Serializable {

	private static final long serialVersionUID = 7919497290688172885L;

	private String nrPraticaSPIL;
	private UtentePi3Bean utenteMittente;
	private ArrayList<UtentePi3Bean> lstUtentiDestinatari;
	private Documento documentSil;
	private Document documentPi3;
	private ArrayList<Documento> lstDocumentiAllegati;

	private String codeRegister;
	private Project project;
	private ClassificationScheme classificationScheme;
	private String codeProject; // codice/valore di Classificazione PAT del Titolario
	private String prgTitolario; // id/prg del Titolario

	private boolean isTemplateDoc;

	private BigDecimal cdnUtins;
	private BigDecimal cdnUtMod;
	private Date dtMins;
	private Date dtmMod;

	private boolean isDocumentoFirmabile;

	private boolean isConsensoAttivo;
	private boolean isDocumentoFirmato;
	private String documentType;
	private String tipoDelTrattamento;
	private boolean isDocInEntrata;
	private boolean isDocInUscita;
	private boolean isDocRepertoriato;

	public String getNrPraticaSPIL() {
		return nrPraticaSPIL;
	}

	public void setNrPraticaSPIL(String nrPraticaSPIL) {
		this.nrPraticaSPIL = nrPraticaSPIL;
	}

	public UtentePi3Bean getUtenteMittente() {
		return utenteMittente;
	}

	public void setUtenteMittente(UtentePi3Bean utenteMittente) {
		this.utenteMittente = utenteMittente;
	}

	public Documento getDocumentSil() {
		return documentSil;
	}

	public void setDocumentSil(Documento documentSil) {
		this.documentSil = documentSil;
	}

	public Document getDocumentPi3() {
		return documentPi3;
	}

	public void setDocumentPi3(Document documentPi3) {
		this.documentPi3 = documentPi3;
	}

	public ArrayList<Documento> getLstDocumentiAllegati() {
		return lstDocumentiAllegati;
	}

	public void setLstDocumentiAllegati(ArrayList<Documento> lstDocumentiAllegati) {
		this.lstDocumentiAllegati = lstDocumentiAllegati;
	}

	public String getCodeRegister() {
		return codeRegister;
	}

	public void setCodeRegister(String codeRegister) {
		this.codeRegister = codeRegister;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public ClassificationScheme getClassificationScheme() {
		return classificationScheme;
	}

	public void setClassificationScheme(ClassificationScheme classificationScheme) {
		this.classificationScheme = classificationScheme;
	}

	public String getCodeProject() {
		return codeProject;
	}

	public void setCodeProject(String codeProject) {
		this.codeProject = codeProject;
	}

	public String getPrgTitolario() {
		return prgTitolario;
	}

	public void setPrgTitolario(String prgTitolario) {
		this.prgTitolario = prgTitolario;
	}

	public BigDecimal getCdnUtins() {
		return cdnUtins;
	}

	public void setCdnUtins(BigDecimal cdnUtins) {
		this.cdnUtins = cdnUtins;
	}

	public BigDecimal getCdnUtMod() {
		return cdnUtMod;
	}

	public void setCdnUtMod(BigDecimal cdnUtMod) {
		this.cdnUtMod = cdnUtMod;
	}

	public Date getDtMins() {
		return dtMins;
	}

	public void setDtMins(Date dtMins) {
		this.dtMins = dtMins;
	}

	public Date getDtmMod() {
		return dtmMod;
	}

	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}

	public ArrayList<UtentePi3Bean> getLstUtentiDestinatari() {
		return lstUtentiDestinatari;
	}

	public void setLstUtentiDestinatari(ArrayList<UtentePi3Bean> lstUtentiDestinatari) {
		this.lstUtentiDestinatari = lstUtentiDestinatari;
	}

	public boolean isDocumentoFirmabile() {
		return isDocumentoFirmabile;
	}

	public void setDocumentoFirmabile(boolean isDocumentoFirmabile) {
		this.isDocumentoFirmabile = isDocumentoFirmabile;
	}

	public boolean isTemplateDoc() {
		return isTemplateDoc;
	}

	public void setTemplateDoc(boolean templateDoc) {
		this.isTemplateDoc = templateDoc;
	}

	public boolean isConsensoAttivo() {
		return isConsensoAttivo;
	}

	public void setConsensoAttivo(boolean isConsensoAttivo) {
		this.isConsensoAttivo = isConsensoAttivo;
	}

	public boolean isDocumentoFirmato() {
		return isDocumentoFirmato;
	}

	public void setDocumentoFirmato(boolean isDocumentoFirmato) {
		this.isDocumentoFirmato = isDocumentoFirmato;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getTipoDelTrattamento() {
		return tipoDelTrattamento;
	}

	public void setTipoDelTrattamento(String tipoDelTrattamento) {
		this.tipoDelTrattamento = tipoDelTrattamento;
	}

	public boolean isDocInEntrata() {
		return isDocInEntrata;
	}

	public void setDocInEntrata(boolean isDocInEntrata) {
		this.isDocInEntrata = isDocInEntrata;
	}

	public boolean isDocInUscita() {
		return isDocInUscita;
	}

	public void setDocInUscita(boolean isDocInUscita) {
		this.isDocInUscita = isDocInUscita;
	}

	public boolean isDocRepertoriato() {
		return isDocRepertoriato;
	}

	public void setDocRepertoriato(boolean isDocRepertoriato) {
		this.isDocRepertoriato = isDocRepertoriato;
	}

}
