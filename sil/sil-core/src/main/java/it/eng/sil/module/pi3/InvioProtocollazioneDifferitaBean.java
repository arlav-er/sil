package it.eng.sil.module.pi3;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class InvioProtocollazioneDifferitaBean implements Serializable {

	private static final long serialVersionUID = -5293552732807110416L;

	private BigDecimal prgProtDifferita;
	private String strNumPratica;
	private Date datIns;
	private Date datInvio;
	private File blbFile;
	private String codStato;

	public BigDecimal getPrgProtDifferita() {
		return prgProtDifferita;
	}

	public void setPrgProtDifferita(BigDecimal prgProtDifferita) {
		this.prgProtDifferita = prgProtDifferita;
	}

	public String getStrNumPratica() {
		return strNumPratica;
	}

	public void setStrNumPratica(String strNumPratica) {
		this.strNumPratica = strNumPratica;
	}

	public Date getDatIns() {
		return datIns;
	}

	public void setDatIns(Date datIns) {
		this.datIns = datIns;
	}

	public Date getDatInvio() {
		return datInvio;
	}

	public void setDatInvio(Date datInvio) {
		this.datInvio = datInvio;
	}

	public File getBlbFile() {
		return blbFile;
	}

	public void setBlbFile(File blbFile) {
		this.blbFile = blbFile;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

}
