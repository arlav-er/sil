package it.eng.myportal.dtos;

import it.eng.myportal.utils.Utils;

public class CvVisualizzaLetteraAccDTO extends CvLetteraAccDTO {
	private String nomeCandidato;
	private String cognomeCandidato;
	
	public String getNomeCandidato() {
		return nomeCandidato;
	}

	public void setNomeCandidato(String nomeCandidato) {
		this.nomeCandidato = nomeCandidato;
	}

	public String getCognomeCandidato() {
		return cognomeCandidato;
	}

	public void setCognomeCandidato(String cognomeCandidato) {
		this.cognomeCandidato = cognomeCandidato;
	}

	public String getLettera() {
		if (lettera == null) {
			StringBuilder letteraHTML = new StringBuilder();

			String brevePresentazioneClean = (brevePresentazione != null) ? Utils.trimHTML(brevePresentazione)
					: "";
			if (!brevePresentazioneClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(brevePresentazioneClean);
				letteraHTML.append("</p>");
			}
			String motivazObiettiviClean = (motivazObiettivi != null) ? Utils.trimHTML(motivazObiettivi)
					: "";
			if (!motivazObiettiviClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(motivazObiettiviClean);
				letteraHTML.append("</p>");
			}
			String beneficiClean = (benefici != null) ? Utils.trimHTML(benefici)
					: "";
			if (!beneficiClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(beneficiClean);
				letteraHTML.append("</p>");
			}
			String puntiForzaQualitaClean = (puntiForzaQualita != null) ? Utils.trimHTML(puntiForzaQualita)
					: "";
			if (!puntiForzaQualitaClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(puntiForzaQualitaClean);
				letteraHTML.append("</p>");
			}
			String salutiClean = (saluti != null) ? Utils.trimHTML(saluti) : "";
			if (!salutiClean.isEmpty()) {
				letteraHTML.append("<p>");
				letteraHTML.append(salutiClean);
				letteraHTML.append("</p>");
			}

			lettera = letteraHTML.toString();
		}
		return lettera;
	}
}
