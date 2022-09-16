package it.eng.sil.util.grabber;

import java.net.URLEncoder;

import it.eng.afExt.utils.StringUtils;

public class PersonaFiscale extends htmlbase {

	// con GET e SET
	String cognome;
	String nome;
	String dataNascita;
	String lnascita;
	String pnascita;
	String sesso;
	String codiceFiscale = "";

	// solo GET
	String comuneResidenza = "";
	String provinciaResidenza = "";

	// locali
	String ggnascita;
	String mmnascita;
	String aaaanascita;

	public PersonaFiscale() {
	}

	// Server di riferimento
	private String cServer = "http://www.agenziaentrate.it/servizi/duplicatocf/richiesta.htm";

	public void eseguiCtrlDatiAnag() throws PersonaFiscaleException {
		StringBuffer buf;

		try {

			lnascita = StringUtils.removeDoubleWhiteSpace(lnascita);

			// Preparo I parametri per il post
			String cPost = "richiesta=1" + "&cognome=" + URLEncoder.encode(cognome) + "&nome=" + URLEncoder.encode(nome)
					+ "&ggnascita=" + ggnascita + "&mmnascita=" + mmnascita + "&aaaanascita="
					+ URLEncoder.encode(aaaanascita) + "&lnascita=" + URLEncoder.encode(lnascita) + "&pnascita="
					+ URLEncoder.encode(pnascita) + "&sesso=" + URLEncoder.encode(sesso);

			String cCookie = "";
			buf = postPage(cServer, cCookie, cPost);

			// System.out.println(buf);
		} catch (Throwable ex) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_NELLA_COMUNICAZIONE, ex.getMessage());
		}

		String page = buf.toString();

		if (page.indexOf("Soggetto non presente in archivio.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_PERSONA_NON_PRESENTE_IN_ARCHIVIO,
					"Soggetto non presente in archivio");
			// System.out.println("Soggetto non presente in archivio ");
		} else if (page.indexOf("Operazione già effettuata nel corso dell'anno.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"Operazione già effettuata nel corso dell'anno.");
			// System.out.println("Operazione già effettuata nel corso
			// dell'anno.");
		} else if (page.indexOf("Il cognome non è stato inserito correttamente.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"Il cognome non è stato inserito correttamente.");
			// System.out.println("Il cognome non è stato inserito
			// correttamente.");
		} else if (page.indexOf("Il comune di nascita non è stato inserito correttamente.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"Il comune di nascita non è stato inserito correttamente.");
			// System.out.println("Il comune di nascita non è stato inserito
			// correttamente.");
		} else if (page.indexOf("Il nome non è stato inserito correttamente.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"Il nome non è stato inserito correttamente.");
			// System.out.println("Il nome non è stato inserito
			// correttamente.");
		} else if (page
				.indexOf("Dati non sufficientemente selettivi. Effettuare la ricerca per codice fiscale.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"Dati non sufficientemente selettivi. Effettuare la ricerca per codice fiscale.");
			// System.out.println("Il nome non è stato inserito
			// correttamente.");
		} else if (page.indexOf("La data di nascita non è stata inserita correttamente.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"La data di nascita non è stata inserita correttamente.");
			// System.out.println("La data di nascita non è stata inserita
			// correttamente.");
		} else if (page.indexOf("Operazione non effettuabile, rivolgersi all'ufficio.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"Operazione non effettuabile.");
		} else {

			// Residenza
			String pattern = "<td width=\"40%\"><b>Comune residenza</b></td>\r\n            					<td width=\"60%\"><b>";
			int initIdx = page.indexOf(pattern);
			int lenPattern = pattern.length();
			int fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			comuneResidenza = page.substring(initIdx + lenPattern, fineIdx).trim();

			// Residenza
			pattern = "<td width=\"40%\"><b>Provincia residenza</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			provinciaResidenza = page.substring(initIdx + lenPattern, fineIdx).trim();

			// Codice fiscale

			pattern = "<td width=\"40%\"><b>Codice fiscale</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			codiceFiscale = page.substring(initIdx + lenPattern, fineIdx).trim();

		}

	}

	public void eseguiCtrlCodFiscale() throws PersonaFiscaleException {
		StringBuffer buf;

		try {

			// Preparo I parametri per il post
			String cPost = "richiesta=2" + "&cf=" + URLEncoder.encode(codiceFiscale);

			String cCookie = "";
			buf = postPage(cServer, cCookie, cPost);

			// System.out.println(buf);
		} catch (Throwable ex) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_NELLA_COMUNICAZIONE, ex.getMessage());
		}

		String page = buf.toString();

		if (page.indexOf("<b>Il codice fiscale non è stato inserito correttamente.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_CF_NON_VALIDO,
					"Codice fiscale [" + codiceFiscale.toUpperCase().trim() + "] non valido");
			// System.out.println("Soggetto non presente in archivio ");
		} else if (page.indexOf("<b>Codice fiscale non attribuito.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_CF_NON_ATTRIBUITO,
					"Codice fiscale [" + codiceFiscale.toUpperCase().trim() + "] non attribuito");
			// System.out.println("Soggetto non presente in archivio ");
		} else if (page.indexOf("Operazione già effettuata nel corso dell'anno.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"Operazione già effettuata nel corso dell'anno.");
			// System.out.println("Operazione già effettuata nel corso
			// dell'anno.");
		} else if (page.indexOf("Operazione non effettuabile, rivolgersi all'ufficio.") != -1) {
			throw new PersonaFiscaleException(PersonaFiscaleException.ERR_OPERAZIONE_GIA_EFFETTUATA,
					"Operazione non effettuabile.");
		} else {

			// cognome
			String pattern = "<td width=\"40%\"><b>Cognome</b></td>\r\n            					<td width=\"60%\"><b>";
			int initIdx = page.indexOf(pattern);
			int lenPattern = pattern.length();
			int fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			cognome = page.substring(initIdx + lenPattern, fineIdx).trim();

			// nome
			pattern = "<td width=\"40%\"><b>Nome</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			nome = page.substring(initIdx + lenPattern, fineIdx).trim();

			// Data di nascita

			pattern = "<td width=\"40%\"><b>Data di nascita</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			dataNascita = page.substring(initIdx + lenPattern, fineIdx).trim();

			// Comune di nascita

			pattern = "<td width=\"40%\"><b>Comune di nascita</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			lnascita = page.substring(initIdx + lenPattern, fineIdx).trim();

			// Provincia di nascita

			pattern = "<td width=\"40%\"><b>Provincia nascita</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			pnascita = page.substring(initIdx + lenPattern, fineIdx).trim();

			// Comune di residenza

			pattern = "<td width=\"40%\"><b>Comune residenza</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			comuneResidenza = page.substring(initIdx + lenPattern, fineIdx).trim();

			// Provincia di residenza

			pattern = "<td width=\"40%\"><b>Provincia residenza</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			provinciaResidenza = page.substring(initIdx + lenPattern, fineIdx).trim();

			// Provincia di residenza

			pattern = "<td width=\"40%\"><b>Sesso</b></td>\r\n            					<td width=\"60%\"><b>";
			initIdx = page.indexOf(pattern);
			lenPattern = pattern.length();
			fineIdx = page.indexOf("</b></td>", initIdx + lenPattern);

			sesso = page.substring(initIdx + lenPattern, fineIdx).trim();

		}

	}

	/**
	 * @return
	 */
	public String getCognome() {
		if (cognome != null)
			return cognome.toUpperCase();
		return null;

	}

	/**
	 * @return
	 */
	public String getComuneNascita() {
		if (lnascita != null)
			return lnascita.toUpperCase();
		return null;
	}

	/**
	 * @return
	 */
	public String getNome() {
		if (nome != null)
			return nome.toUpperCase();
		return null;

	}

	/**
	 * @return
	 */
	public String getProvinciaNascita() {

		if (pnascita != null)
			return pnascita.toUpperCase();
		return null;

	}

	/**
	 * @return
	 */
	public String getSesso() {
		if (sesso != null)
			return sesso.toUpperCase();
		return null;

	}

	/**
	 * @param string
	 */
	public void setCognome(String string) {
		cognome = string;
	}

	/**
	 * @param string
	 */
	public void setComuneNascita(String string) {
		lnascita = string;
	}

	/**
	 * @param string
	 */
	public void setNome(String string) {
		nome = string;
	}

	/**
	 * @param string
	 */
	public void setProvinciaNascita(String pNascita) {
		if ((pNascita != null) && (pNascita.equalsIgnoreCase("EX"))) {
			pNascita = "EE";
		}
		pnascita = pNascita;
	}

	/**
	 * @param string
	 */
	public void setSesso(String string) {

		sesso = string;
	}

	/**
	 * @return
	 */
	public String getDataNascita() {
		return dataNascita;
	}

	/**
	 * @param string
	 */
	public void setDataNascita(String string) {
		dataNascita = string;

		ggnascita = dataNascita.substring(0, 2);
		mmnascita = dataNascita.substring(3, 5);
		aaaanascita = dataNascita.substring(6);

	}

	/**
	 * @return
	 */
	public String getCodiceFiscale() {
		if (codiceFiscale != null)
			return codiceFiscale.toUpperCase();
		return null;
	}

	/**
	 * @return
	 */
	public String getComuneResidenza() {

		if (comuneResidenza != null)
			return comuneResidenza.toUpperCase();
		return null;

	}

	/**
	 * @return
	 */
	public String getProvinciaResidenza() {

		if (provinciaResidenza != null)
			return provinciaResidenza.toUpperCase();
		return null;

	}

	/**
	 * @param string
	 */
	public void setProvinciaResidenza(String string) {
		provinciaResidenza = string;
	}

	/**
	 * @param string
	 */
	public void setCodiceFiscale(String string) {
		codiceFiscale = string;
	}

	public static void main(String[] args) {

		for (int i = 0; i < 100; i++) {

			System.out.println("**************************");
			System.out.println("** Tentativo n. " + (i + 1));
			System.out.println("**************************");

			PersonaFiscale f = new PersonaFiscale();

			/*
			 * f.setCognome("rossi"); f.setNome("aldo"); f.setDataNascita("25/10/1972");
			 * 
			 * f.setComuneNascita("MODENA"); f.setProvinciaNascita("MO"); f.setSesso("M");
			 * 
			 * try {
			 * 
			 * f.eseguiCtrlDatiAnag(); System.out.println("Comune di residenza : " + f.getComuneResidenza());
			 * System.out.println("Provincia di residenza : " + f.getProvinciaResidenza());
			 * System.out.println("Codice Fiscale : " + f.getCodiceFiscale()); } catch (PersonaFiscaleException ex) {
			 * System.out.println("*** ERRORE ***");
			 * 
			 * System.out.println("Codice di errore: " + ex.getErrorCode()); System.out.println("Descrizione : " +
			 * ex.getMessage()); }
			 */
			f.setCodiceFiscale("VLLFBA67L18C236X");

			try {

				f.eseguiCtrlCodFiscale();
				System.out.println("Nome     : " + f.getNome());
				System.out.println("Cognome  : " + f.getCognome());
				System.out.println("Comune di nascita  : " + f.getComuneNascita());
				System.out.println("Provincia di nascita  : " + f.getProvinciaNascita());

				System.out.println("Comune di residenza     : " + f.getComuneResidenza());
				System.out.println("Provincia di residenza  : " + f.getProvinciaResidenza());
				System.out.println("Sesso                   : " + f.getSesso());
				System.out.println("Codice Fiscale          : " + f.getCodiceFiscale());

			} catch (PersonaFiscaleException ex) {
				System.out.println("*** ERRORE ***");

				System.out.println("Codice di errore: " + ex.getErrorCode());
				System.out.println("Descrizione     : " + ex.getMessage());

			}
		}

	}
}
