package it.eng.sil.module.cig;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * Inserimento di un contatto in maniera automatica. Il modulo e' chiamato dopo l'inserimento in CI_CORSO e in
 * CI_CORSO_CATALOGO.
 * 
 * @author uberti
 */
public class InsertContatto extends AbstractSimpleModule {

	private static final long serialVersionUID = -2808032455459344913L;

	private TransactionQueryExecutor transExec;

	public void service(SourceBean request, SourceBean response) throws Exception {

		// se i contatti non sono abilitati salto tutto
		if (!isContattiAbilitato(request, response))
			return;

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		// ottengo il codcpicontatto (cod rif sull'utente) e lo metto in request
		String codCpiContatto = user.getCodRif();
		request.updAttribute("CODCPICONTATTO", codCpiContatto);

		// ottengo il prgcorsoci dalla sessione
		BigDecimal prgCorsoCi = (BigDecimal) sessionContainer.getAttribute("PRGCORSOCI");
		sessionContainer.delAttribute("PRGCORSOCI");
		request.updAttribute("PRGCORSOCI", prgCorsoCi);

		// ottengo datcontatto e stroracontatto
		Calendar now = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String datContatto = df.format(now.getTime());
		df = new SimpleDateFormat("HH:mm");
		String strOraContatto = df.format(now.getTime());
		request.updAttribute("DATCONTATTO", datContatto);
		request.updAttribute("STRORACONTATTO", strOraContatto);

		String numidproposta = StringUtils.getAttributeStrNotNull(request, "numidproposta");
		String numrecid = StringUtils.getAttributeStrNotNull(request, "numrecid");

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();
			boolean success = false;

			// ottengo PRGSPICONTATTO, nome e sigla operatore
			this.setSectionQuerySelect("QUERY_PRGSPIUT");
			SourceBean prgSpiUt = this.doSelect(request, response);
			// String nomeOperatore = "";
			String siglaOperatore = "";
			if (prgSpiUt != null) {
				BigDecimal prgSpi = (BigDecimal) prgSpiUt.getAttribute("ROW.PRGSPI");
				request.updAttribute("PRGSPICONTATTO", prgSpi);
				// nomeOperatore = (String) prgSpiUt.getAttribute("ROW.NOME");
				siglaOperatore = (String) prgSpiUt.getAttribute("ROW.STRSIGLAOPERATORE");
				if (siglaOperatore == null) {
					siglaOperatore = "";
				}
			}

			// ottengo la STRDESCRIZIONE del CPI
			this.setSectionQuerySelect("QUERY_SELECT_CPIDESC");
			SourceBean strDescCpi = this.doSelect(request, response);
			String strDesc = "";
			if (strDescCpi != null) {
				strDesc = (String) strDescCpi.getAttribute("ROW.STRDESCRIZIONE");
			}

			// ottengo TXTCONTATTO
			String referenteSede = (String) request.getAttribute("REFERENTESEDE");
			String strSede = (String) request.getAttribute("STRSEDE");
			String lavoratore = (String) request.getAttribute("LAVORATORE");
			String denominazione = (String) request.getAttribute("DENOMINAZIONE");
			String codTipoIscr = (String) request.getAttribute("CODTIPOISCR");
			StringBuffer emailBody = new StringBuffer();
			emailBody.append("Spett.le ").append(referenteSede).append(", referente per la sede ").append(strSede)
					.append("\ncon la presente siamo ad informarla che il Sig./ra ").append(lavoratore);

			if (codTipoIscr != null
					&& (codTipoIscr.equals("O") || codTipoIscr.equals("S") || codTipoIscr.equals("M"))) {
				emailBody.append(", titolare di un ammortizzatore sociale in deroga,");
			}

			emailBody.append(" ha opzionato, presso il Centro per l'Impiego di  ").append(strDesc).append(", il corso ")
					.append(denominazione).append(".\n\n").append("Si ricorda che, nel caso in cui il Sig/ra ")
					.append(lavoratore).append(" non prendesse contatti con la vostra sede")
					.append(" entro 4 giorni lavorativi, occorre darne tempestiva comunicazione all'operatore ")
					.append(siglaOperatore).append(" del Centro per l'Impiego di ").append(strDesc).append(".\n\n")
					.append("Cordiali saluti\n").append(siglaOperatore).append("\nCentro per l'Impiego di ")
					.append(strDesc);
			request.updAttribute("TXTCONTATTO", emailBody);
			request.updAttribute("PRGMOTCONTRATTO", new BigDecimal(101));
			// ottengo prgazienda e prgunita da de_catalogo_proposta
			request.updAttribute("numidproposta", numidproposta);
			request.updAttribute("numrecid", numrecid);
			this.setSectionQuerySelect("QUERY_SELECT_AZUNITA");
			SourceBean beanAzUnita = this.doSelect(request, response);
			BigDecimal prgAzienda = (BigDecimal) beanAzUnita.getAttribute("ROW.PRGAZIENDA");
			BigDecimal prgUnita = (BigDecimal) beanAzUnita.getAttribute("ROW.PRGUNITA");
			// se non presenti, metto 0 in request perché NULL non è accettato, e poi uso decode sulla query
			if (prgAzienda == null) {
				prgAzienda = new BigDecimal(0);
			}
			if (prgUnita == null) {
				prgUnita = new BigDecimal(0);
			}
			request.updAttribute("PRGAZIENDA", prgAzienda);
			request.updAttribute("PRGUNITA", prgUnita);

			// ottengo stremail da de_catalogo_sede
			this.setSectionQuerySelect("QUERY_CATSEDE_EMAIL");
			SourceBean beanStrEmail = this.doSelect(request, response);
			String strEmail = (String) beanStrEmail.getAttribute("ROW.STREMAIL");
			request.updAttribute("STREMAIL", strEmail);

			// ottengo la chiave primaria prgcontatto ed eseguo l'inserimento
			BigDecimal prgContatto = getPrgContattoPK(request, response);
			if (prgContatto != null) {
				setKeyInRequest(prgContatto, request);
				this.setSectionQueryInsert("QUERY_INSERT_CONTATTO");
				success = this.doInsert(request, response);

				// aggiorno PRGCONTATTOISC nella CI_CORSO_CATALOGO
				setKeyInRequest(prgContatto, request);
				this.setSectionQueryUpdate("QUERY_UPDATE_CORSOCAT");
				success = this.doUpdate(request, response);

				if (success) {
					transExec.commitTransaction();
				} else {
					transExec.rollBackTransaction();
				}
			}
		} catch (EMFInternalError e) {
			transExec.rollBackTransaction();
		}

	}

	/**
	 * Ottiene la primary key dalla sequence S_AG_CONTATTO.
	 * 
	 * @param request
	 *            SourceBean request.
	 * @param response
	 *            SourceBean response.
	 * @return BigDecimal primary key.
	 * @throws Exception.
	 */
	private BigDecimal getPrgContattoPK(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");
		SourceBean beanprgCorsoCi = (SourceBean) doSelect(request, response);
		return (BigDecimal) beanprgCorsoCi.getAttribute("ROW.PRGCONTATTO");
	}

	/**
	 * Imposta la primary key PRGCONTATTO nella request.
	 * 
	 * @param prgContatto
	 *            primary key.
	 * @param request
	 *            SourceBean Request.
	 * @throws Exception.
	 */
	private void setKeyInRequest(BigDecimal prgContatto, SourceBean request) throws Exception {
		if (request.getAttribute("PRGCONTATTO") != null) {
			request.delAttribute("PRGCONTATTO");
		}
		request.updAttribute("PRGCONTATTO", prgContatto);
	}

	private boolean isContattiAbilitato(SourceBean request, SourceBean response) {
		this.setSectionQuerySelect("QUERY_ISCONTATTOAB");
		SourceBean beanContatto = (SourceBean) doSelect(request, response);
		String abilitato = (String) beanContatto.getAttribute("ROW.ABILITATO");
		return "S".equalsIgnoreCase(abilitato);
	}
}
