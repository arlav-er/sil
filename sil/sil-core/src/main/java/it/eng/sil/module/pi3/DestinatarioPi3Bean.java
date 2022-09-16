package it.eng.sil.module.pi3;

import java.io.Serializable;
import java.math.BigDecimal;

public class DestinatarioPi3Bean implements Serializable {

	private static final long serialVersionUID = -1625298935228106653L;

	private BigDecimal prgProtDestinatario; // PRGPROTDEST
	private BigDecimal prgProtPi3; // PRGPROTPITRE
	private String codiceMotivoTrasmissioneInterna; // CODMOTTRASMINT
	private String strDestinatarioPi3; // STRDESPROT
	private String strDestinatarioSil; // STRDESTINATARIO
	private String flgDestinatario; // FLGDESTP
	private BigDecimal numKloProtDestinatario; // NUMKLOCPROTDEST

	public BigDecimal getPrgProtDestinatario() {
		return prgProtDestinatario;
	}

	public void setPrgProtDestinatario(BigDecimal prgProtDestinatario) {
		this.prgProtDestinatario = prgProtDestinatario;
	}

	public BigDecimal getPrgProtPi3() {
		return prgProtPi3;
	}

	public void setPrgProtPi3(BigDecimal prgProtPi3) {
		this.prgProtPi3 = prgProtPi3;
	}

	public String getCodiceMotivoTrasmissioneInterna() {
		return codiceMotivoTrasmissioneInterna;
	}

	public void setCodiceMotivoTrasmissioneInterna(String codiceMotivoTrasmissioneInterna) {
		this.codiceMotivoTrasmissioneInterna = codiceMotivoTrasmissioneInterna;
	}

	public String getStrDestinatarioPi3() {
		return strDestinatarioPi3;
	}

	public void setStrDestinatarioPi3(String strDestinatarioPi3) {
		this.strDestinatarioPi3 = strDestinatarioPi3;
	}

	public String getStrDestinatarioSil() {
		return strDestinatarioSil;
	}

	public void setStrDestinatarioSil(String strDestinatarioSil) {
		this.strDestinatarioSil = strDestinatarioSil;
	}

	public String getFlgDestinatario() {
		return flgDestinatario;
	}

	public void setFlgDestinatario(String flgDestinatario) {
		this.flgDestinatario = flgDestinatario;
	}

	public BigDecimal getNumKloProtDestinatario() {
		return numKloProtDestinatario;
	}

	public void setNumKloProtDestinatario(BigDecimal numKloProtDestinatario) {
		this.numKloProtDestinatario = numKloProtDestinatario;
	}

}
