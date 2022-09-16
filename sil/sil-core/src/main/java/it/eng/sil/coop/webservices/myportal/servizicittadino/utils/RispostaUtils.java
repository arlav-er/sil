package it.eng.sil.coop.webservices.myportal.servizicittadino.utils;

import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getaccountcittadino.out.RispostaAccountCittadino.DatiAccount;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getaccountcittadino.out.RispostaAccountCittadino.DatiAccount.AccountCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getdettagliocittadino.out.RispostaDettaglioCittadino.DettaglioCittadino;

public class RispostaUtils {

	public static SourceBean toSourceBean(DatiAccount datiAccount) throws SourceBeanException {

		SourceBean sourceBean = null;
		sourceBean = new SourceBean("ROWS");

		SourceBean currentSourceBean = null;

		List<AccountCittadino> accountCittadini = datiAccount.getAccountCittadino();

		AccountCittadino accountCittadino = null;
		String username = null;
		String cognome = null;
		String nome = null;
		String email = null;
		Object idPfPrincipal = null;
		String abilitatoServiziAmministrativi = null;
		String abilitato = null;

		for (int i = 0; i < accountCittadini.size(); i++) {

			accountCittadino = accountCittadini.get(i);

			currentSourceBean = new SourceBean("ROW");

			username = accountCittadino.getUsername();
			cognome = accountCittadino.getCognome();
			nome = accountCittadino.getNome();
			email = accountCittadino.getEmail();
			idPfPrincipal = XmlUtils.getStringFromSimpleXmlElement(accountCittadino.getIdPfPrincipal());
			abilitatoServiziAmministrativi = accountCittadino.getAbilitatoServiziAmministrativi();
			abilitato = accountCittadino.getAbilitato();

			if (username != null)
				currentSourceBean.setAttribute("username", username);
			if (cognome != null)
				currentSourceBean.setAttribute("cognome", cognome);
			if (nome != null)
				currentSourceBean.setAttribute("nome", nome);
			if (email != null)
				currentSourceBean.setAttribute("email", email);
			if (idPfPrincipal != null)
				currentSourceBean.setAttribute("idPfPrincipal", idPfPrincipal);
			if (abilitatoServiziAmministrativi != null) {
				if ("S".equals(abilitatoServiziAmministrativi)) {
					currentSourceBean.setAttribute("abilitatoServiziAmministrativiDesc", "SI");
				} else if ("N".equals(abilitatoServiziAmministrativi)) {
					currentSourceBean.setAttribute("abilitatoServiziAmministrativiDesc", "NO");
				} else {
					currentSourceBean.setAttribute("abilitatoServiziAmministrativiDesc", "");
				}
				currentSourceBean.setAttribute("abilitatoServiziAmministrativi", abilitatoServiziAmministrativi);
			}
			if (abilitato != null) {
				if ("S".equals(abilitato)) {
					currentSourceBean.setAttribute("abilitatoDesc", "Attivato");
				} else if ("N".equals(abilitato)) {
					currentSourceBean.setAttribute("abilitatoDesc", "Da attivare");
				} else {
					currentSourceBean.setAttribute("abilitatoDesc", "");
				}
				currentSourceBean.setAttribute("abilitato", abilitato);
			}

			sourceBean.setAttribute(currentSourceBean);

		}

		return sourceBean;

	}

	public static SourceBean toSourceBean(DettaglioCittadino dettaglioCittadino) throws SourceBeanException {

		String username = dettaglioCittadino.getUsername();
		String cognome = dettaglioCittadino.getCognome();
		String nome = dettaglioCittadino.getNome();
		String email = dettaglioCittadino.getEmail();
		String idPfPrincipal = XmlUtils.getStringFromSimpleXmlElement(dettaglioCittadino.getIdPfPrincipal());
		String comuneNascita = dettaglioCittadino.getComuneNascita();
		String comuneDomicilio = dettaglioCittadino.getComuneDomicilio();
		String indirizzoDomicilio = dettaglioCittadino.getIndirizzoDomicilio();
		String codiceFiscale = dettaglioCittadino.getCodiceFiscale();
		String dataNascita = (String) DateUtils.gregorianDateToString(dettaglioCittadino.getDataNascita());
		String cittadinanza = dettaglioCittadino.getCittadinanza();
		String documentoIdentita = dettaglioCittadino.getDocumentoIdentita();
		String codStatus = dettaglioCittadino.getCodStatus();
		String numeroDocumento = dettaglioCittadino.getNumeroDocumento();
		String dtScadenzaDocumento = (String) DateUtils
				.gregorianDateToString(dettaglioCittadino.getDtScadenzaDocumento());
		String abilitatoServiziAmministrativi = dettaglioCittadino.getAbilitatoServiziAmministrativi();
		String abilitato = dettaglioCittadino.getAbilitato();

		SourceBean sourceBean = null;

		sourceBean = new SourceBean("ROW");
		if (username != null)
			sourceBean.setAttribute("username", username);
		if (cognome != null)
			sourceBean.setAttribute("cognome", cognome);
		if (nome != null)
			sourceBean.setAttribute("nome", nome);
		if (email != null)
			sourceBean.setAttribute("email", email);
		if (idPfPrincipal != null)
			sourceBean.setAttribute("idPfPrincipal", idPfPrincipal);
		if (comuneNascita != null)
			sourceBean.setAttribute("comuneNascita", comuneNascita);
		if (comuneDomicilio != null)
			sourceBean.setAttribute("comuneDomicilio", comuneDomicilio);
		if (indirizzoDomicilio != null)
			sourceBean.setAttribute("indirizzoDomicilio", indirizzoDomicilio);
		if (codiceFiscale != null)
			sourceBean.setAttribute("codiceFiscale", codiceFiscale);
		if (dataNascita != null)
			sourceBean.setAttribute("dataNascita", dataNascita);
		if (cittadinanza != null)
			sourceBean.setAttribute("cittadinanza", cittadinanza);
		if (documentoIdentita != null)
			sourceBean.setAttribute("documentoIdentita", documentoIdentita);
		if (codStatus != null)
			sourceBean.setAttribute("codStatus", codStatus);
		if (numeroDocumento != null)
			sourceBean.setAttribute("numeroDocumento", numeroDocumento);
		if (dtScadenzaDocumento != null)
			sourceBean.setAttribute("dtScadenzaDocumento", dtScadenzaDocumento);
		if (abilitatoServiziAmministrativi != null) {
			if ("S".equals(abilitatoServiziAmministrativi)) {
				sourceBean.setAttribute("abilitatoServiziAmministrativiDesc", "SI");
			} else if ("N".equals(abilitatoServiziAmministrativi)) {
				sourceBean.setAttribute("abilitatoServiziAmministrativiDesc", "NO");
			} else {
				sourceBean.setAttribute("abilitatoServiziAmministrativiDesc", "");
			}
			sourceBean.setAttribute("abilitatoServiziAmministrativi", abilitatoServiziAmministrativi);
		}
		if (abilitato != null) {
			if ("S".equals(abilitato)) {
				sourceBean.setAttribute("abilitatoDesc", "Attivato");
			} else if ("N".equals(abilitato)) {
				sourceBean.setAttribute("abilitatoDesc", "Da attivare");
			} else {
				sourceBean.setAttribute("abilitatoDesc", "");
			}
			sourceBean.setAttribute("abilitato", abilitato);
		}

		return sourceBean;

	}

}
