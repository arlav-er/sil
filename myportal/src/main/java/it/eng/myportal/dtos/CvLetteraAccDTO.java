package it.eng.myportal.dtos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CvLetteraAccDTO extends AbstractUpdatablePkDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1646544546034946064L;

	protected Integer idPfPrincipal;
	protected String nome;
	protected String brevePresentazione;
	protected String motivazObiettivi;
	protected String benefici;
	protected String puntiForzaQualita;
	protected String saluti;
	protected Boolean flagInviato;
	// true se la lettera e' referenziato in almento un messaggio
	private Boolean referenziatoInMsg;

	protected String lettera;

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getBrevePresentazione() {
		return brevePresentazione;
	}

	public void setBrevePresentazione(String brevePresentazione) {
		this.brevePresentazione = brevePresentazione;
		lettera = null;
	}

	public String getMotivazObiettivi() {
		return motivazObiettivi;
	}

	public void setMotivazObiettivi(String motivazObiettivi) {
		this.motivazObiettivi = motivazObiettivi;
		lettera = null;
	}

	public String getBenefici() {
		return benefici;
	}

	public void setBenefici(String benefici) {
		this.benefici = benefici;
		lettera = null;
	}

	public String getPuntiForzaQualita() {
		return puntiForzaQualita;
	}

	public void setPuntiForzaQualita(String puntiForzaQualita) {
		this.puntiForzaQualita = puntiForzaQualita;
		lettera = null;
	}

	public String getSaluti() {
		return saluti;
	}

	public void setSaluti(String saluti) {
		this.saluti = saluti;
		lettera = null;
	}

	public Boolean getFlagInviato() {
		return flagInviato;
	}

	public void setFlagInviato(Boolean flagInviato) {
		this.flagInviato = flagInviato;
	}

	public Boolean getReferenziatoInMsg() {
		return referenziatoInMsg;
	}

	public void setReferenziatoInMsg(Boolean referenziatoInMsg) {
		this.referenziatoInMsg = referenziatoInMsg;
	}

	public String getLettera() {
		if (lettera == null) {
			StringBuilder letteraHTML = new StringBuilder();

			String brevePresentazioneClean = (brevePresentazione != null) ? cleanString(brevePresentazione) : "";
			if (!brevePresentazioneClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(brevePresentazioneClean);
				letteraHTML.append("</p>");
			}
			String motivazObiettiviClean = (motivazObiettivi != null) ? cleanString(motivazObiettivi) : "";
			if (!motivazObiettiviClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(motivazObiettiviClean);
				letteraHTML.append("</p>");
			}
			String beneficiClean = (benefici != null) ? cleanString(benefici) : "";
			if (!beneficiClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(beneficiClean);
				letteraHTML.append("</p>");
			}
			String puntiForzaQualitaClean = (puntiForzaQualita != null) ? cleanString(puntiForzaQualita) : "";
			if (!puntiForzaQualitaClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(puntiForzaQualitaClean);
				letteraHTML.append("</p>");
			}
			String salutiClean = (saluti != null) ? cleanString(saluti) : "";
			if (!salutiClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(salutiClean);
				letteraHTML.append("</p>");
			}

			lettera = letteraHTML.toString();
		}
		return lettera;
	}

	private String cleanString(String string) {
		String returnString = "";
		if (string != null) {
			StringBuilder sb = new StringBuilder(string);

			// pattern da eliminare
			String patternString = "((&nbsp;)|(\\s)|(<br>)|(</br>)|(<br />))";

			// cerca il patterno solo all'inizio della stringa
			Pattern leadingSpaces = Pattern.compile("^" + patternString + "*", Pattern.CASE_INSENSITIVE);
			// cerca il pattern solo alla fine della stringa
			Pattern trailingSpaces = Pattern.compile(patternString + "*$", Pattern.CASE_INSENSITIVE);

			// match dei pattern nella stringa
			Matcher matcher;

			matcher = leadingSpaces.matcher(sb.toString());
			sb = new StringBuilder(matcher.replaceAll(""));

			matcher = trailingSpaces.matcher(sb.toString());
			sb = new StringBuilder(matcher.replaceAll(""));

			returnString = sb.toString();
		}

		return returnString;
	}

	public boolean isProprietary(Integer idPfPrincipal) {
		boolean result = false;

		if (this.idPfPrincipal.equals(idPfPrincipal)) {
			result = true;
		}

		return result;
	}
}
