package it.eng.sil.coop.webservices.myportal.servizicittadino;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getaccountcittadino.out.RispostaAccountCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getdettagliocittadino.out.RispostaDettaglioCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.putaccountcittadino.in.PutAccountCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.putaccountcittadino.out.RispostaputAccountCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.reinviomailaccreditamento.out.RispostaputReinvioMail;
import it.eng.sil.coop.webservices.myportal.servizicittadino.client.GetAccountCittadinoClient;
import it.eng.sil.coop.webservices.myportal.servizicittadino.client.GetDettaglioCittadinoClient;
import it.eng.sil.coop.webservices.myportal.servizicittadino.client.PutAccountCittadinoClient;
import it.eng.sil.coop.webservices.myportal.servizicittadino.client.ReinvioMailClient;
import it.eng.sil.coop.webservices.myportal.servizicittadino.client.ServiziCittadinoClientException;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.QueryUtils;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.RispostaUtils;

public class ServiziCittadino {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ServiziCittadino.class.getName());

	public static final boolean reinvioMail(String idPfPrincipal, String destinatarioCC)
			throws ServiziCittadinoException {

		String codiceEsito = null;
		RispostaputReinvioMail.Esito esito = null;
		RispostaputReinvioMail rispostaputReinvioMail;
		ReinvioMailClient reinvioMailClient = new ReinvioMailClient();

		try {

			rispostaputReinvioMail = reinvioMailClient.reinvioMail(idPfPrincipal, destinatarioCC);
			esito = rispostaputReinvioMail.getEsito();
			codiceEsito = esito.getCodice();

		} catch (ServiziCittadinoClientException e) {
			rispostaputReinvioMail = null;
			throw new ServiziCittadinoException(e.getMessage());
		}

		////////////////////
		// VERIFICA ESITO //
		////////////////////

		if ("00".equals(codiceEsito)) {

			// 00 Operazione Completata
			return true;

		} else if ("01".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("01 Errore in fase di autenticazione Si verifica quando i dati di
			// accesso al servizio comunicati non sono corretti.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("02".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("02 Errore invio mail Si verifica quando il sistema non è riuscito a
			// completare l'operazione di reinvio mail con le credenziali di accreditamento.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("03".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("03 Account già abilitato Si verifica quando si tenta di reinviare la
			// mail con le credenziali di accreditamento per un utente che risulta essere già in stato 'Attivato'
			// (flag_abilitato = 'S')");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("04".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("04 Account già abilitato");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("99".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("99 Errore generico Errore generico che verrà fornito qualora
			// l'errore che si verifica non rientri tra le casistiche precedentemente esposte.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		}

		_logger.error("Esito non gestito: " + esito.getCodice() + " " + esito.getDescrizione());

		return false;

	}

	public static final boolean putAccountCittadino(String idPfPrincipal, String username, String cognome, String nome,
			String email, String comuneNascita, String comuneDomicilio, String indirizzoDomicilio, String codiceFiscale,
			String dataNascita, String cittadinanza, String codProvinciaSil, String documentoIdentita,
			String numeroDocumento, String dtScadenzaDocumento) throws ServiziCittadinoException {

		String codiceEsito = null;
		RispostaputAccountCittadino.Esito esito = null;
		RispostaputAccountCittadino rispostaputAccountCittadino;
		PutAccountCittadinoClient putAccountCittadinoClient = new PutAccountCittadinoClient();

		try {

			rispostaputAccountCittadino = putAccountCittadinoClient.putAccountCittadino(idPfPrincipal, username,
					cognome, nome, email, comuneNascita, comuneDomicilio, indirizzoDomicilio, codiceFiscale,
					dataNascita, cittadinanza, codProvinciaSil, documentoIdentita, numeroDocumento,
					dtScadenzaDocumento);
			esito = rispostaputAccountCittadino.getEsito();
			codiceEsito = esito.getCodice();

		} catch (ServiziCittadinoClientException e) {
			rispostaputAccountCittadino = null;
			throw new ServiziCittadinoException(e.getMessage());
		}

		////////////////////
		// VERIFICA ESITO //
		////////////////////

		if ("00".equals(codiceEsito)) {

			// 00 Dati aggiornati correttamente.
			return true;

		} else if ("01".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("01 Errore in fase di autenticazione Si verifica quando i dati di
			// accesso al servizio comunicati non sono corretti.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("02".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("02 InputXML non valido Si verifica quando il parametro inputXML non
			// rispetta l'XSD di validazione formale.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("03".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("03 Cittadino non trovato Si verifica quando non è stato trovato
			// alcun utente di tipo cittadino corrispondente alla richiesta.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("04".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("04 Inserimento non riuscito: username già presente Si verifica
			// quando lo username indicato è già presente sul Portale");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("05".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("05 Inserimento non riuscito: e-mail già registrata Si verifica
			// quando la mail indicata è già stata registrata per un altro account.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("06".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("06 Inserimento non riuscito Si verifica quando vi è stato un errore
			// sull'inserimento di dati non congrui tra i due sistemi che non sono relativi a username o e-mail.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("10".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("10 Aggiornamento non riuscito Si verifica quando vi è stato un
			// errore sull'aggiornamento di dati non congrui tra i due sistemi.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("99".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("99 Errore generico Errore generico che verrà fornito qualora
			// l'errore che si verifica non rientri tra le casistiche precedentemente esposte.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		}

		_logger.error("Esito non gestito: " + esito.getCodice() + " " + esito.getDescrizione());

		return false;

	}

	public static final boolean putAccountCittadino(PutAccountCittadino accountCittadino)
			throws ServiziCittadinoException {

		String codiceEsito = null;
		RispostaputAccountCittadino.Esito esito = null;
		RispostaputAccountCittadino rispostaputAccountCittadino;
		PutAccountCittadinoClient putAccountCittadinoClient = new PutAccountCittadinoClient();

		try {

			rispostaputAccountCittadino = putAccountCittadinoClient.putAccountCittadino(accountCittadino);
			esito = rispostaputAccountCittadino.getEsito();
			codiceEsito = esito.getCodice();

		} catch (ServiziCittadinoClientException e) {
			rispostaputAccountCittadino = null;
			throw new ServiziCittadinoException(e.getMessage());
		}

		////////////////////
		// VERIFICA ESITO //
		////////////////////

		if ("00".equals(codiceEsito)) {

			// 00 Dati aggiornati correttamente.
			return true;

		} else if ("01".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("01 Errore in fase di autenticazione Si verifica quando i dati di
			// accesso al servizio comunicati non sono corretti.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("02".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("02 InputXML non valido Si verifica quando il parametro inputXML non
			// rispetta l'XSD di validazione formale.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("03".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("03 Cittadino non trovato Si verifica quando non è stato trovato
			// alcun utente di tipo cittadino corrispondente alla richiesta.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("04".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("04 Inserimento non riuscito: username già presente Si verifica
			// quando lo username indicato è già presente sul Portale");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("05".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("05 Inserimento non riuscito: e-mail già registrata Si verifica
			// quando la mail indicata è già stata registrata per un altro account.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("06".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("06 Inserimento non riuscito Si verifica quando vi è stato un errore
			// sull'inserimento di dati non congrui tra i due sistemi che non sono relativi a username o e-mail.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("10".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("10 Aggiornamento non riuscito Si verifica quando vi è stato un
			// errore sull'aggiornamento di dati non congrui tra i due sistemi.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("99".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("99 Errore generico Errore generico che verrà fornito qualora
			// l'errore che si verifica non rientri tra le casistiche precedentemente esposte.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		}

		_logger.error("Esito non gestito: " + esito.getCodice() + " " + esito.getDescrizione());

		return false;

	}

	public static final SourceBean getDettaglioCittadino(String inputIdPfPrincipal) throws ServiziCittadinoException {

		String codiceEsito = null;
		SourceBean rispostaSB = null;
		RispostaDettaglioCittadino.Esito esito = null;
		RispostaDettaglioCittadino.DettaglioCittadino dettaglioCittadino = null;
		RispostaDettaglioCittadino rispostaDettaglioCittadino = null;
		GetDettaglioCittadinoClient getDettaglioCittadinoClient = new GetDettaglioCittadinoClient();

		///////////////
		// CHIAMA WS //
		///////////////

		try {

			rispostaDettaglioCittadino = getDettaglioCittadinoClient.getDettaglioCittadino(inputIdPfPrincipal);
			dettaglioCittadino = rispostaDettaglioCittadino.getDettaglioCittadino();
			esito = rispostaDettaglioCittadino.getEsito();
			codiceEsito = esito.getCodice();

		} catch (ServiziCittadinoClientException e) {
			rispostaDettaglioCittadino = null;
			dettaglioCittadino = null;
			throw new ServiziCittadinoException(e.getMessage());
		}

		////////////////////
		// VERIFICA ESITO //
		////////////////////

		if ("00".equals(codiceEsito)) {
			// 00 Nessun Errore
		} else if ("01".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("01 Errore in fase di autenticazione Si verifica quando i dati di
			// accesso al servizio comunicati non sono corretti.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("02".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("02 InputXML non valido Si verifica quando il parametro inputXML non
			// rispetta l'XSD di validazione formale.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("03".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("03 Cittadino non trovato Si verifica quando non è stato trovato
			// alcun utente di tipo cittadino corrispondente alla richiesta.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("99".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("99 Errore generico Errore generico che verrà fornito qualora
			// l'errore che si verifica non rientri tra le casistiche precedentemente esposte.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else {
			_logger.error("Esito non gestito: " + esito.getCodice() + " " + esito.getDescrizione());
		}

		//////////////////////////
		// CREAZIONE SOURCEBEAN //
		//////////////////////////

		try {

			rispostaSB = RispostaUtils.toSourceBean(dettaglioCittadino);

		} catch (SourceBeanException e) {
			// throw new ServiziCittadinoException("Errore durante la generazione del SourceBean di risposta");
			throw new ServiziCittadinoException("Errore generico)");
		}

		return rispostaSB;

	}

	public static final SourceBean getAccountCittadino(String cdnLavoratore) throws ServiziCittadinoException {

		String[] datiCittadino;

		String cognome;
		String nome;
		String codiceFiscale;
		String email;

		try {

			datiCittadino = QueryUtils.getDatiCittadino(cdnLavoratore);
			cognome = datiCittadino[0];
			nome = datiCittadino[1];
			codiceFiscale = datiCittadino[2];
			email = datiCittadino[3];

		} catch (Exception e) {
			// throw new ServiziCittadinoException("Errore nel recuopero dei dati del cittadino (cognome, nome, codice
			// fiscale, email)");
			throw new ServiziCittadinoException(ServiziCittadinoException.ERRORE_GENERICO);
		}

		return getAccountCittadino(cognome, nome, codiceFiscale, email);

	}

	public static final SourceBean getAccountCittadino(String cognome, String nome, String codiceFiscale, String email)
			throws ServiziCittadinoException {

		String codiceEsito = null;
		SourceBean rispostaSB = null;
		RispostaAccountCittadino.DatiAccount datiAccount = null;
		RispostaAccountCittadino.Esito esito = null;
		RispostaAccountCittadino rispostaAccountCittadino;
		GetAccountCittadinoClient getAccountCittadinoClient = new GetAccountCittadinoClient();

		try {

			rispostaAccountCittadino = getAccountCittadinoClient.getAccountCittadino(cognome, nome, codiceFiscale,
					email);
			datiAccount = rispostaAccountCittadino.getDatiAccount();
			esito = rispostaAccountCittadino.getEsito();
			codiceEsito = esito.getCodice();

		} catch (ServiziCittadinoClientException e) {
			rispostaAccountCittadino = null;
			throw new ServiziCittadinoException(e.getMessage());
		}

		////////////////////
		// VERIFICA ESITO //
		////////////////////

		if ("00".equals(codiceEsito)) {
			// 00 Nessun Errore
		} else if ("01".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("01 Errore in fase di autenticazione Si verifica quando i dati di
			// accesso al servizio comunicati non sono corretti.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("02".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("02 InputXML non valido Si verifica quando il parametro inputXML non
			// rispetta l'XSD di validazione formale.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("03".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("03 Cittadino non trovato Si verifica quando non è stato trovato
			// alcun utente di tipo cittadino corrispondente alla richiesta.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else if ("99".equals(codiceEsito)) {
			// throw new ServiziCittadinoException("99 Errore generico Errore generico che verrà fornito qualora
			// l'errore che si verifica non rientri tra le casistiche precedentemente esposte.");
			throw new ServiziCittadinoException(esito.getDescrizione());
		} else {
			_logger.error("Esito non gestito: " + esito.getCodice() + " " + esito.getDescrizione());
		}

		//////////////////////////
		// CREAZIONE SOURCEBEAN //
		//////////////////////////

		try {

			rispostaSB = RispostaUtils.toSourceBean(datiAccount);

		} catch (SourceBeanException e) {
			throw new ServiziCittadinoException(ServiziCittadinoException.ERRORE_GENERICO);
		}

		return rispostaSB;

	}

}
