/*
 * Created on Mar 1, 2007
 */
package it.eng.sil.bean.protocollo;

import org.apache.axis.utils.StringUtils;

/**
 * @author savino
 */
public class DOCAREAProfiloER implements DOCAREAProfiloIFace {
	/**
	 * Descrizione significativa del documento
	 */
	private String descrizioneOggetto;
	/**
	 * Busta identificatore:
	 */
	private Identificatore identificatore;
	private Destinatario destinatario;
	private Destinatario mittente;
	private Documento documento;

	public DOCAREAProfiloER() {
	}

	public String toXML() {
		return this.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(
				"<Segnatura xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\".\\DOCAREAProto.xsd\">");
		buf.append("<Intestazione>");
		buf.append("<Oggetto>").append(this.descrizioneOggetto).append("</Oggetto>");
		// Identificatore
		buf.append("<Identificatore>");
		buf.append("<CodiceAmministrazione>").append(this.identificatore.codiceAmministrazione)
				.append("</CodiceAmministrazione>");
		buf.append("<CodiceAOO>").append(this.identificatore.codiceAOO).append("</CodiceAOO>");
		buf.append("<NumeroRegistrazione>").append(this.identificatore.numeroRegistrazione)
				.append("</NumeroRegistrazione>");
		buf.append("<DataRegistrazione>").append(this.identificatore.dataRegistrazione).append("</DataRegistrazione>");
		buf.append("<Flusso>").append(this.identificatore.flusso).append("</Flusso>");
		buf.append("</Identificatore>");
		// Mittente
		buf.append("<Mittente>");
		// Amministrazione
		buf.append("<Amministrazione>");
		buf.append("<Denominazione>").append(this.mittente.amministrazione.denominazione).append("</Denominazione>");
		buf.append("<CodiceAmministrazione>").append(this.mittente.amministrazione.codiceAmministrazione)
				.append("</CodiceAmministrazione>");
		buf.append("<IndirizzoTelematico tipo=\"").append(this.mittente.amministrazione.indirizzoTelematico.tipo)
				.append("\">").append(this.mittente.amministrazione.indirizzoTelematico.indirizzo)
				.append("</IndirizzoTelematico>");
		buf.append("<UnitaOrganizzativa id=\"").append(this.mittente.amministrazione.unitaOrganizzativa).append("\"/>");
		buf.append("</Amministrazione>");
		buf.append("<AOO>");
		buf.append("<CodiceAOO>").append(this.mittente.aoo.codiceAOO).append("</CodiceAOO>");
		buf.append("</AOO>");
		buf.append("</Mittente>");
		// Destinatario
		buf.append("<Destinatario>");
		buf.append("<Amministrazione>");
		buf.append("<Denominazione>").append(this.destinatario.amministrazione.denominazione)
				.append("</Denominazione>");
		buf.append("<CodiceAmministrazione>").append(this.destinatario.amministrazione.codiceAmministrazione)
				.append("</CodiceAmministrazione>");
		buf.append("<IndirizzoTelematico tipo=\"").append(this.destinatario.amministrazione.indirizzoTelematico.tipo)
				.append("\">").append(this.destinatario.amministrazione.indirizzoTelematico.indirizzo)
				.append("</IndirizzoTelematico>");
		buf.append("<UnitaOrganizzativa id=\"").append(this.destinatario.amministrazione.unitaOrganizzativa)
				.append("\"/>");
		buf.append("</Amministrazione>");
		buf.append("</Destinatario>");
		//
		buf.append("</Intestazione>");
		// Descrizione
		buf.append("<Descrizione>");
		// Documenti
		buf.append("<Documento id=\"").append(this.documento.id).append("\" nome=\"").append(this.documento.nome)
				.append("\">");
		buf.append("<DescrizioneDocumento>").append(this.documento.descrizione).append("</DescrizioneDocumento>");
		buf.append("</Documento>");
		//
		buf.append("</Descrizione>");
		//
		buf.append("</Segnatura>");
		return buf.toString();
	}

	public void setOggetto(String s) {
		this.descrizioneOggetto = StringUtils.escapeNumericChar(s);
	}

	/**
	 * 
	 * @param id
	 *            e' lo IngDocID restituito dal servizio inserimento pag. 23
	 * @param nome
	 *            il nome del file
	 */
	public void setDocumento(String id, String nome, String descrizione) {
		this.documento = new Documento();
		this.documento.id = id;
		this.documento.nome = StringUtils.escapeNumericChar(nome);
		this.documento.descrizione = StringUtils.escapeNumericChar(descrizione);
	}

	/**
	 * 
	 * @param codiceAmministrazione
	 *            codice dell'ente che gestisce l'applicazione chiamante il WS
	 * @param codiceAOO
	 *            P_X Dove X è il nome dell'applicazione chiamante
	 * @param dataRegistrazione
	 *            per il caso d) ed e) vale sempre 0
	 * @param flusso
	 *            per il caso d) ed e) vale sempre "U"
	 * @param numeroRegistrazione
	 *            per il caso d) ed e) vale sempre 0
	 */
	public void setIdentificatore(String codiceAmministrazione, String codiceAOO, String dataRegistrazione,
			String flusso, String numeroRegistrazione) {
		this.identificatore = new Identificatore();
		this.identificatore.codiceAmministrazione = codiceAmministrazione;
		this.identificatore.codiceAOO = codiceAOO;
		this.identificatore.dataRegistrazione = dataRegistrazione;
		this.identificatore.flusso = flusso;
		this.identificatore.numeroRegistrazione = numeroRegistrazione;
	}

	/**
	 * 
	 * @param codiceAmministrazione
	 *            codice dell'ente che gestisce l'applicazione chiamaNTE IL WS
	 * @param denominazione
	 *            denominazione dell'ente che gestisce l'applicazione chiamante il WS
	 * @param email
	 *            indirizzo e-mail dell'ente che gestisce l'applicazione chiamante il WS
	 * @param unitaOrgID
	 *            Un elemento UnitaOrganizzativa rappresenta un elemento nel percorso che costituisce della descrizione
	 *            di un indirizzo.!!!!
	 * @param codiceAOO
	 *            P_X Dove X è il nome dell'applicazione chiamante (pag 16 e pag 19 elemento AOO)
	 */
	public void setMittente(String codiceAmministrazione, String denominazione, String email, String unitaOrgID,
			String codiceAOO) {
		this.mittente = new Destinatario();
		this.mittente.amministrazione = new Amministrazione();
		this.mittente.amministrazione.indirizzoTelematico = new IndirizzoTelematico();
		this.mittente.amministrazione.codiceAmministrazione = codiceAmministrazione;
		this.mittente.amministrazione.denominazione = denominazione;
		this.mittente.amministrazione.indirizzoTelematico.indirizzo = email;
		this.mittente.amministrazione.indirizzoTelematico.tipo = "smtp";
		this.mittente.amministrazione.unitaOrganizzativa = unitaOrgID;
		this.mittente.aoo = new AOO();
		this.mittente.aoo.codiceAOO = codiceAOO;
	}

	/**
	 * 
	 * @param codiceAmministrazione
	 *            codice dell'ente che fornisce il servizio web di protocollazione
	 * @param denominazione
	 *            denominazione dell'ente che fornisce il servizio web di protocollazione
	 * @param email
	 *            e-mail dell'ente che fornisce il servizio web di protocollazione
	 * @param unitaOrgID
	 *            Un elemento UnitaOrganizzativa rappresenta un elemento nel percorso che costituisce della descrizione
	 *            di un indirizzo.!!!
	 */
	public void setDestinatario(String codiceAmministrazione, String denominazione, String email, String unitaOrgID) {
		this.destinatario = new Destinatario();
		this.destinatario.amministrazione = new Amministrazione();
		this.destinatario.amministrazione.indirizzoTelematico = new IndirizzoTelematico();
		this.destinatario.amministrazione.codiceAmministrazione = codiceAmministrazione;
		this.destinatario.amministrazione.denominazione = denominazione;
		this.destinatario.amministrazione.indirizzoTelematico.indirizzo = email;
		this.destinatario.amministrazione.indirizzoTelematico.tipo = "smtp";
		this.destinatario.amministrazione.unitaOrganizzativa = unitaOrgID;
	}

	private class Identificatore {
		private String codiceAmministrazione;
		private String codiceAOO;
		private String numeroRegistrazione;
		private String dataRegistrazione;
		private String flusso;
	}

	private class Amministrazione {
		private String denominazione;
		private String codiceAmministrazione;
		private IndirizzoTelematico indirizzoTelematico;
		private String unitaOrganizzativa;
		private String ruolo;
		private Persona persona;
		private String indirizzoPostale;
		private String telefono;
		private String fax;
	}

	private class IndirizzoTelematico {
		private String tipo;
		private String note;
		private String indirizzo;
	}

	private class Persona {
		private String nome;
		private String cognome;
		private String titolo;
		private String codiceFiscale;
		private String identificativo;
		private String denominazione;
		private IndirizzoTelematico indirizzoTelematico;
	}

	private class Destinatario {
		Amministrazione amministrazione;
		AOO aoo;
		Persona persona;
		IndirizzoTelematico indirizzoTelematico;
		String telefono;
		String fax;
		String indirizzoPostale;
	}

	private class AOO {
		String codiceAOO;
		String denominazione;
	}

	private class Classifica {
		String codiceAmministrazione;
		String codiceAOO;
		String codiceTitolario;
	}

	private class Fascicolo {
		String numero;
		String anno;
		String fascicolo;
	}

	private class Documento {
		String id;
		String nome;
		String descrizione;
	}

	public static void main(String a[]) {
		DOCAREAProfiloER p = new DOCAREAProfiloER();
		p.setDestinatario("AAA", "Regione emila romagna", "probo@regione.emilia-romagna.it", "51");
		p.setMittente("P_SILER", "Provincia di Bologna", "probo@provincia.bologna.it", "15", "AOO_??");
		p.setIdentificatore("booo", "P_SILER", "02/03/2007", "U", "0");
		p.setOggetto(
				"Richiesta numero di protocollo per documenti registrati nell'archivio della provincia di bologna");
		p.setDocumento("ppd09834hrj", "documento_vuoto.rtf", "Documento vuoto obbligatorio");
		System.out.println(p);
	}

}
