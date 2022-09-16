package it.eng.sil.module.sap;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.CalcolaCpiLav;

public class InsertSAPLavoratore extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		String codCpi = user.getCodRif();
		BigDecimal userid = new BigDecimal(user.getCodut());

		BigDecimal cdnLavoratore = (BigDecimal) request.getAttribute("cdnLavoratore");
		String strCodiceFiscale = StringUtils.getAttributeStrNotNull(request, "strCodiceFiscale");
		String strCognome = StringUtils.getAttributeStrNotNull(request, "strCognome");
		String strNome = StringUtils.getAttributeStrNotNull(request, "strNome");
		String strSesso = StringUtils.getAttributeStrNotNull(request, "strSesso");
		String datNasc = StringUtils.getAttributeStrNotNull(request, "datNasc");
		String codComNas = StringUtils.getAttributeStrNotNull(request, "codComNas");
		String codCittadinanzaHid = StringUtils.getAttributeStrNotNull(request, "codCittadinanzaHid");
		String codCittadinanza2Hid = null;
		String codstatoCivile = null;
		String flgMilite = null;
		BigDecimal numFigli = null;
		String strNote = null;
		String FLGCFOK = null;
		String strIndirizzodom = StringUtils.getAttributeStrNotNull(request, "strIndirizzodom");
		String strLocalitadom = StringUtils.getAttributeStrNotNull(request, "strLocalitadom");
		String codComdom = StringUtils.getAttributeStrNotNull(request, "codComdom");
		String strCapDom = StringUtils.getAttributeStrNotNull(request, "strCapDom");
		String strIndirizzores = StringUtils.getAttributeStrNotNull(request, "strIndirizzoRes");
		String strLocalitares = StringUtils.getAttributeStrNotNull(request, "strLocalitares");
		String codComRes = StringUtils.getAttributeStrNotNull(request, "codComRes");
		String strCapRes = StringUtils.getAttributeStrNotNull(request, "strCapRes");
		String strTelRes = null;
		String strTelDom = null;
		String strTelAltro = null;
		String strCell = StringUtils.getAttributeStrNotNull(request, "strCell");
		String strEmail = StringUtils.getAttributeStrNotNull(request, "strEmail");
		String strFax = StringUtils.getAttributeStrNotNull(request, "strFax");

		Object[] obj = null;

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		int insertError = 0;
		boolean insertOK = false;
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			this.enableTransactions(transExec);

			transExec.initTransaction();

			obj = new Object[1];
			obj[0] = strCodiceFiscale;

			SourceBean lav = ((SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE", obj, "SELECT"));

			if (lav != null && lav.containsAttribute("ROW.CDNLAVORATORE")) {
				insertError = 1;
				throw new Exception("Codice Fiscale già presente in archivio.");
			} else {
				obj = new Object[30];
				obj[0] = cdnLavoratore;
				obj[1] = strCodiceFiscale;
				obj[2] = strCognome;
				obj[3] = strNome;
				obj[4] = strSesso;
				obj[5] = datNasc;
				obj[6] = codComNas;
				obj[7] = codCittadinanzaHid;
				obj[8] = codCittadinanza2Hid;
				obj[9] = codstatoCivile;
				obj[10] = flgMilite;
				obj[11] = numFigli;
				obj[12] = strNote;
				obj[13] = FLGCFOK;
				obj[14] = userid;
				obj[15] = userid;
				obj[16] = codComRes;
				obj[17] = strIndirizzores;
				obj[18] = strLocalitares;
				obj[19] = strCapRes;
				obj[20] = codComdom;
				obj[21] = strIndirizzodom;
				obj[22] = strLocalitadom;
				obj[23] = strCapDom;
				obj[24] = strTelRes;
				obj[25] = strTelDom;
				obj[26] = strTelAltro;
				obj[27] = strCell;
				obj[28] = strEmail;
				obj[29] = strFax;

				insertOK = ((Boolean) transExec.executeQuery("INSERT_AN_LAVORATOREANAGIND", obj, "INSERT"))
						.booleanValue();

				if (!insertOK) {
					insertError = 2;
					throw new Exception("impossibile aggiornare AN_LAVORATORE in transazione");
				}
			}

			// Aggiornamento in AN_LAV_STORIA_INF
			// devo rileggere il numkloLavStoriaInf
			obj = new Object[1];
			obj[0] = cdnLavoratore;

			SourceBean numklo = ((SourceBean) transExec.executeQuery("SELECT_AN_LAV_STORIA_INF", obj, "SELECT"));

			BigDecimal numkloLavStoriaInf = (BigDecimal) numklo.getAttribute("ROW.NUMKLOLAVSTORIAINF");

			if (numkloLavStoriaInf != null) {
				numkloLavStoriaInf = numkloLavStoriaInf.add(new BigDecimal("1"));
				request.delAttribute("numKloLavStoriaInf");
				request.setAttribute("numKloLavStoriaInf", numkloLavStoriaInf.toString());
			} else {
				insertError = 3;
				throw new Exception("Non è stato trovato il lavoratore " + cdnLavoratore + " in AN_LAV_STORIA_INF ");

			}

			setCodCpi(request, response);

			obj = new Object[6];
			obj[0] = "";
			obj[1] = userid;
			obj[2] = "";
			obj[3] = "";
			obj[4] = numkloLavStoriaInf;
			obj[5] = cdnLavoratore;

			/*
			 * Controllo di quale CPI si tratta
			 */
			String strRegioneRif = "";
			String strRegioneRes = "";
			Object[] objReg = new Object[1];
			objReg[0] = codCpi;
			SourceBean regioneRif = ((SourceBean) transExec.executeQuery("SELECT_REGIONE", objReg, "SELECT"));
			// CPI del luogo in cui risiede il lavoratore
			objReg[0] = StringUtils.getAttributeStrNotNull(request, "codCpi");
			SourceBean regioneRes = ((SourceBean) transExec.executeQuery("SELECT_REGIONE", objReg, "SELECT"));
			SourceBean provinciaUser = ((SourceBean) transExec.executeQuery("GET_DATI_TS_GENERALE", null, "SELECT"));

			if ((regioneRif != null) && (regioneRes != null)) {
				if ((regioneRif.getAttribute("ROW.CODREGIONE") != null)
						&& !regioneRif.getAttribute("ROW.CODREGIONE").equals("")) {
					strRegioneRif = (String) regioneRif.getAttribute("ROW.CODREGIONE");
				}

				if ((regioneRes.getAttribute("ROW.CODREGIONE") != null)
						&& !regioneRes.getAttribute("ROW.CODREGIONE").equals("")) {
					strRegioneRes = (String) regioneRes.getAttribute("ROW.CODREGIONE");
				}

				CalcolaCpiLav calcolatoreCpi = new CalcolaCpiLav(strRegioneRes, strRegioneRif,
						StringUtils.getAttributeStrNotNull(regioneRes, "ROW.CODPROVINCIA"),
						StringUtils.getAttributeStrNotNull(provinciaUser, "ROW.CODPROVINCIASIL").toString(),
						StringUtils.getAttributeStrNotNull(request, "codCpi"), codCpi);

				obj[0] = calcolatoreCpi.getCodCpiTit();
				obj[2] = calcolatoreCpi.getCodMonoTipoCpi();
				obj[3] = calcolatoreCpi.getCodCpiOrig();
			}

			insertOK = ((Boolean) transExec.executeQuery("SAVE_AN_LAV_STORIA_INF_INSERT", obj, "UPDATE"))
					.booleanValue();

			if (!insertOK) {
				insertError = 4;
				throw new Exception("impossibile aggiornare AN_LAV_STORIA_INF in transazione");
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(MessageCodes.ImportaSAP.INSERT_LAV_OK);
			response.setAttribute("operationResult", "SUCCESS");

		} catch (Exception e) {
			transExec.rollBackTransaction();
			int msgcod = MessageCodes.ImportaSAP.INSERT_LAV_FAIL;
			response.setAttribute("operationResult", "ERROR");
			String errorMsg = "";
			switch (insertError) {
			case 1:
				errorMsg = "Codice fiscale già presente in archivio";
				break;

			case 2:
				errorMsg = "Impossibile aggiornare AN_LAVORATORE";
				break;

			case 3:
				errorMsg = "Non è stato trovato il nuovo lavoratore in AN_LAV_STORIA_INF";
				break;

			case 4:
				errorMsg = "Impossibile aggiornare AN_LAVORATORE_STORIA_INF";
				break;
			}
			reportOperation.reportFailure(msgcod, e, className + "::service() ", errorMsg);

		}
	}

	private void setCodCpi(SourceBean request, SourceBean response) {
		try {
			if (((String) request.getAttribute("codComdom")).equals("")
					&& ((String) request.getAttribute("codCPIDom")).equals("")) {
				String codCpiResidenza = (String) request.getAttribute("codCPIRes");

				if (codCpiResidenza.length() > 0) {
					request.setAttribute("CODCPI", codCpiResidenza);
					response.setAttribute("CODCPI", codCpiResidenza);
				}
			} else {
				String codCpiDomicilio = (String) request.getAttribute("codCPIDom");

				if (codCpiDomicilio.length() > 0) {
					request.setAttribute("CODCPI", codCpiDomicilio);
					response.setAttribute("CODCPI", codCpiDomicilio);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
