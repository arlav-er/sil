package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.CalcolaCpiLav;

public class InsertLavoratoreNew extends AbstractSimpleModule {
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
		String codCittadinanza2Hid = StringUtils.getAttributeStrNotNull(request, "codCittadinanza2Hid");
		String codstatoCivile = StringUtils.getAttributeStrNotNull(request, "codstatoCivile");
		String flgMilite = StringUtils.getAttributeStrNotNull(request, "flgMilite");
		BigDecimal numFigli = (BigDecimal) request.getAttribute("numFigli");
		String strNote = StringUtils.getAttributeStrNotNull(request, "strNote");
		String FLGCFOK = StringUtils.getAttributeStrNotNull(request, "FLGCFOK");
		String codComRes = StringUtils.getAttributeStrNotNull(request, "codComRes");
		String strIndirizzores = StringUtils.getAttributeStrNotNull(request, "strIndirizzores");
		String strLocalitares = StringUtils.getAttributeStrNotNull(request, "strLocalitares");
		String strCapRes = StringUtils.getAttributeStrNotNull(request, "strCapRes");
		String codComdom = StringUtils.getAttributeStrNotNull(request, "codComdom");
		String strIndirizzodom = StringUtils.getAttributeStrNotNull(request, "strIndirizzodom");
		String strLocalitadom = StringUtils.getAttributeStrNotNull(request, "strLocalitadom");
		String strCapDom = StringUtils.getAttributeStrNotNull(request, "strCapDom");
		String strTelRes = StringUtils.getAttributeStrNotNull(request, "strTelRes");
		String strTelDom = StringUtils.getAttributeStrNotNull(request, "strTelDom");
		String strTelAltro = StringUtils.getAttributeStrNotNull(request, "strTelAltro");
		String strCell = StringUtils.getAttributeStrNotNull(request, "strCell");
		String strEmail = StringUtils.getAttributeStrNotNull(request, "strEmail");
		String strFax = StringUtils.getAttributeStrNotNull(request, "strFax");

		Object[] obj = null;

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		int updateError = 0;
		boolean updateOK = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			this.enableTransactions(transExec);

			//
			transExec.initTransaction();

			if (!request.getAttribute("FLGCFOK").toString().equalsIgnoreCase("S")) {
				CF_utils.verificaCF(strCodiceFiscale, strNome, strCognome, strSesso, datNasc, codComNas);
				// se c'è errore lancia un'eccezione di tipo CfException
			}

			// this.setSectionQuerySelect("QUERY_SELECT_A");
			obj = new Object[1];
			obj[0] = strCodiceFiscale;

			SourceBean lav = ((SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE", obj, "SELECT"));

			if (lav != null && lav.containsAttribute("ROW.CDNLAVORATORE")) {
				updateError = 3;
				throw new Exception("Codice Fiscale già presente in archivio.");
			} else {
				// this.setSectionQueryInsert("QUERY_INSERT");
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

				updateOK = ((Boolean) transExec.executeQuery("INSERT_AN_LAVORATOREANAGIND", obj, "INSERT"))
						.booleanValue();

				if (!updateOK) {
					updateError = 1;
					throw new Exception("impossibile aggiornare AN_LAVORATORE in transazione");
				}

				// 17/04/2006 DAVIDE: AGGANCIO ALL'INDICE REGIONALE
				/*
				 * String getCPIComp = "SELECT C.CODCPI FROM DE_COMUNE C WHERE upper(C.CODCOM) =
				 * '" + codComdom.toUpperCase()+"'"; SourceBean result = null; try { result = (SourceBean)
				 * transExec.executeQueryByStringStatement(getCPIComp, null, TransactionQueryExecutor.SELECT); } catch
				 * (Exception e) { throw new Exception("Non è stato trovato il cpi competente del lavoratore\n" +
				 * e.toString()); }
				 * 
				 * String cpiComp = StringUtils.getAttributeStrNotNull(result,"ROW.CODCPI");
				 * 
				 * SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer(); User utente
				 * = (User) sessione.getAttribute(User.USERID);
				 * 
				 * String poloMittente = InfoProvinciaSingleton.getInstance().getCodice(); String strCdnUtente =
				 * InfoProvinciaSingleton.getInstance().getCodice(); String strCdnGruppo =
				 * String.valueOf(utente.getCdnGruppo()); String strCdnProfilo = String.valueOf(utente.getCdnProfilo());
				 * String strMittente = utente.getNome() + " " + utente.getCognome();
				 * 
				 * 
				 * TestataMessageTO testataMessaggio=new TestataMessageTO();
				 * testataMessaggio.setPoloMittente(poloMittente); testataMessaggio.setCdnGruppo(strCdnGruppo);
				 * testataMessaggio.setCdnUtente(strCdnUtente); testataMessaggio.setCdnProfilo(strCdnProfilo);
				 * testataMessaggio.setStrMittente(strMittente);
				 * 
				 * PutLavoratoreIRMessage putLavoratoreIRMessage= new PutLavoratoreIRMessage();
				 * putLavoratoreIRMessage.setTestata(testataMessaggio);
				 * 
				 * String codMonoTipoCpi = "";
				 * 
				 * if (codCpi.equals(cpiComp)) { codMonoTipoCpi="C"; //il cpi di domicilio e il cpi di inserimento
				 * coincidono: il cpi ha la competenza amministrativa } else { codMonoTipoCpi="T";// settaggio di test -
				 * bisogna ancora controllare se la residenza sia in regione (credo). Da verificare ulteriormente }
				 * 
				 * //imposto i parametri applicativi putLavoratoreIRMessage.setCodiceFiscale(strCodiceFiscale);
				 * putLavoratoreIRMessage.setNome(strNome); putLavoratoreIRMessage.setCognome(strCognome);
				 * putLavoratoreIRMessage.setDataNascita(datNasc); putLavoratoreIRMessage.setComune(codComNas);
				 * 
				 * putLavoratoreIRMessage.setCodCpi(codCpi); putLavoratoreIRMessage.setCodMonoTipoCpi(codMonoTipoCpi);
				 * putLavoratoreIRMessage.setCpiComp(cpiComp);
				 * 
				 * 
				 * //prelevo i nomi della coda di output dai file di configurazione DataSourceJNDI dataSourceJndi = new
				 * DataSourceJNDI(); String dataSourceJndiName = dataSourceJndi.getJndi();
				 * 
				 * OutQ outQ=new OutQ();
				 * 
				 * //mando il messaggio putLavoratoreIRMessage.setDataSourceJndi(dataSourceJndiName);
				 * 
				 * //mando il messaggio try { putLavoratoreIRMessage.send(outQ); } catch (Exception e1) { //return
				 * ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
				 * "Fallito inserimento nella coda di messaggi da inviare all'Indice
				 * Regionale", warnings, nested); throw new Exception("Errore nella chiamata all'indice
				 * regionale:richesta di inserimento del nuovo lavoratore. \n" + e1); }
				 */
			}

			// Aggiornamento in AN_LAV_STORIA_INF

			// devo rileggere il numkloLavStoriaInf
			// this.setSectionQuerySelect("QUERY_SELECT");
			// SourceBean numklo = doSelect(request, response);
			obj = new Object[1];
			obj[0] = cdnLavoratore;

			SourceBean numklo = ((SourceBean) transExec.executeQuery("SELECT_AN_LAV_STORIA_INF", obj, "SELECT"));

			BigDecimal numkloLavStoriaInf = (BigDecimal) numklo.getAttribute("ROW.NUMKLOLAVSTORIAINF");

			if (numkloLavStoriaInf != null) {
				numkloLavStoriaInf = numkloLavStoriaInf.add(new BigDecimal("1"));
				request.delAttribute("numKloLavStoriaInf");
				request.setAttribute("numKloLavStoriaInf", numkloLavStoriaInf.toString());
			} else {
				updateError = 4;
				throw new Exception("Non è stato trovato il lavoratore " + cdnLavoratore + " in AN_LAV_STORIA_INF ");

			}

			// ///////////////////////////
			setCodCpi(request, response);
			// ///////////////////////////
			// this.setSectionQueryUpdate("QUERY_UPDATE_AN_LAV_S");

			obj = new Object[6];
			// obj[0] = StringUtils.getAttributeStrNotNull(request,"codCpi");
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

				// Calcolo dei dati relativi al cpi del lavoratore appena
				// inserito
				// E' stato spostato in una classe apposita perché va utilizzato
				// anche nella validazione dei movimenti
				// ed è soggetto a modifiche in futuro.
				CalcolaCpiLav calcolatoreCpi = new CalcolaCpiLav(strRegioneRes, strRegioneRif,
						StringUtils.getAttributeStrNotNull(regioneRes, "ROW.CODPROVINCIA"),
						StringUtils.getAttributeStrNotNull(provinciaUser, "ROW.CODPROVINCIASIL").toString(),
						StringUtils.getAttributeStrNotNull(request, "codCpi"), codCpi);

				obj[0] = calcolatoreCpi.getCodCpiTit();
				obj[2] = calcolatoreCpi.getCodMonoTipoCpi();
				obj[3] = calcolatoreCpi.getCodCpiOrig();
			}

			updateOK = ((Boolean) transExec.executeQuery("SAVE_AN_LAV_STORIA_INF_INSERT", obj, "UPDATE"))
					.booleanValue();

			if (!updateOK) {
				updateError = 2;
				throw new Exception("impossibile aggiornare AN_LAV_STORIA_INF in transazione");
			}

			transExec.commitTransaction();

			// this.setMessageIdSuccess(MessageCodes.General.INSERT_SUCCESS);
			reportOperation.reportSuccess(idSuccess);
			response.setAttribute("operationResult", "SUCCESS");

		} catch (CfException cfEx) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(cfEx.getMessageIdFail());
			try {
				response.setAttribute("operationResult", "ERROR");
			} catch (SourceBeanException sbEx) {
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, sbEx, className + "::service()", "");
				response.setAttribute("errorMesg", "Codice fiscale errato");
			} // catch (SourceBeanException ex)
		} catch (Exception e) {
			transExec.rollBackTransaction();
			int msgcod = MessageCodes.General.INSERT_FAIL;

			try {
				SourceBean opRes = new SourceBean("OPERATIONRESULT");
				opRes.setAttribute("MESSAGE", "Messaggio_di_errore");
				response.setAttribute("operationResult", "ERROR");
				switch (updateError) {
				case 1:
					response.setAttribute("errorMesg", "Impossibile aggiornare AN_LAVORATORE");
					break;

				case 2:
					response.setAttribute("errorMesg", "Impossibile aggiornare AN_LAVORATORE_STORIA_INF");
					break;

				case 3:
					response.setAttribute("errorMesg", "Codice fiscale già presente in archivio");
					msgcod = MessageCodes.Anag.INSERT_FAIL_CF_ESISTENTE; // fornisce
																			// ulteriori
																			// dettagli
					break;

				case 4:
					response.setAttribute("errorMesg", "Non è stato trovato il nuovo lavoratore in AN_LAV_STORIA_INF");
					break;

				}
			} catch (SourceBeanException sbEx) {
				// reportOperation.reportFailure(
				// MessageCodes.General.INSERT_FAIL, sbEx, className +
				// "::service()", "");
				return;
			} // catch (SourceBeanException ex)

			reportOperation.reportFailure(msgcod, e, className + "::service() ", "update in transazione");

		}
	}

	/**
	 * Nel caso in cui la residenza del lavoratore sia uguale al domicilio debbo valorizzare il parametro codcpi per la
	 * successiva update della tabella am_lav_storia_inf (.codCpiTit) Il parametro codCPIifDOMeqRESHid viene valorizzato
	 * dalla funzione AggiornaForm(..) della pagina FindComune.jsp
	 */
	private void setCodCpi(SourceBean request, SourceBean response) {
		try {
			if (((String) request.getAttribute("CODCOMDOM")).equals("")
					&& ((String) request.getAttribute("CODCPI")).equals("")) {
				String codCpiResidenza = (String) request.getAttribute("codCPIifDOMeqRESHid");

				if (codCpiResidenza.length() > 0) {
					request.delAttribute("CODCPI");
					request.setAttribute("CODCPI", codCpiResidenza);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}// end class
