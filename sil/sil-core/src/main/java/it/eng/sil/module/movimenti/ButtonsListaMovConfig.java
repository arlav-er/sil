package it.eng.sil.module.movimenti;

import java.sql.ResultSet;
import java.sql.Statement;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.Values;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

/**
 * Classe che crea il SourceBean di configurazione per la pagina della lista dei movimenti, visualizza o meno il
 * pulsante di aggiunta movimento collegato a seconda che l'utente abbia o meno i diritti per aggiungere i movimenti
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class ButtonsListaMovConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ButtonsListaMovConfig.class.getName());

	private String className = this.getClass().getName();

	public ButtonsListaMovConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		SessionContainer sessionContainer = this.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "MovimentiRisultRicercaPage");
		boolean canInsert_Mov_Coll = attributi.containsButton("INSERISCI");

		DataConnection dc = null;
		java.sql.Connection conn = null;
		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			dc = dcm.getConnection(Values.DB_SIL_DATI);
			conn = dc.getInternalConnection();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile ottenere una connessione dal pool", e);

		}

		String selectstat = "select conf.STRVALORE from ts_config_loc conf where upper(conf.CODTIPOCONFIG) = 'FORZAVAL' "
				+ " AND conf.STRCODRIF = (SELECT CODPROVINCIASIL FROM TS_GENERALE) ";

		ResultSet rs = null;
		Statement stmt = null;
		String forzaVal = "";

		try {
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(selectstat);

			if (rs != null) {
				if (rs.next()) {
					forzaVal = rs.getString(1);
				}
				rs.close();
			}
			stmt.close();
		} catch (Exception e) {
			_logger.debug("[" + this.getClass().getName() + "] Eccezione nella ricerca dei dati del Lavoratore: "
					+ e.toString());

		}
		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_0 = new SourceBean("COLUMN");
			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");
			SourceBean col_10 = new SourceBean("COLUMN");
			SourceBean col_11 = new SourceBean("COLUMN");
			SourceBean col_12 = new SourceBean("COLUMN");

			col_0.setAttribute("name", "CODSTATOATTO");
			col_0.setAttribute("label", "S t a t o");
			colonneSB.setAttribute(col_0);

			col_1.setAttribute("name", "DATAMOV");
			col_1.setAttribute("label", "Data evento");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "codTipoMovVisual");
			col_2.setAttribute("label", "T<br>i<br>p<br>o<br>");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "CODASSCESVISUAL");
			col_3.setAttribute("label", "T.<br>Contr./<br>Ces");
			colonneSB.setAttribute(col_3);

			col_11.setAttribute("name", "CODMONOTEMPO");
			col_11.setAttribute("label", "T e m p o");
			colonneSB.setAttribute(col_11);

			col_4.setAttribute("name", "COGNOMENOMELAV");
			col_4.setAttribute("label", "Cognome Nome Lav");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "CODFISCLAV");
			col_5.setAttribute("label", "Cod. Fis. Lav.");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "RAGSOCAZ");
			col_6.setAttribute("label", "Rag. Soc. Azienda");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "IndirAzienda");
			col_7.setAttribute("label", "Ind. Azienda");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "CODMONOTIPOFINE");
			col_8.setAttribute("label", "M o v  S e g");
			colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "DATFINEMOVEFFETTIVAVIS");
			col_9.setAttribute("label", "Data Fine");
			colonneSB.setAttribute(col_9);

			col_10.setAttribute("name", "MOVRETRIBUZIONE");
			col_10.setAttribute("label", "Redd. Mens &euro;");
			colonneSB.setAttribute(col_10);

			if (forzaVal.equals("true")) {
				col_12.setAttribute("name", "NOTEMODIF");
				col_12.setAttribute("label", "M o d i f.");
				colonneSB.setAttribute(col_12);
				request.setAttribute("forzaVal", forzaVal);
				SourceBean col_13 = new SourceBean("COLUMN");
				col_13.setAttribute("name", "RETRIBUZIONEPROSPETTICA");
				col_13.setAttribute("label", "Redd. Ann Prospettica &euro;");
				colonneSB.setAttribute(col_13);
			} else {
				col_12.setAttribute("name", "RETRIBUZIONEPROSPETTICA");
				col_12.setAttribute("label", "Redd. Ann Prospettica &euro;");
				colonneSB.setAttribute(col_12);
			}

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "PAGE");
			parameterSB1.setAttribute("scope", "");
			parameterSB1.setAttribute("type", "ABSOLUTE");
			parameterSB1.setAttribute("value", "MovDettaglioGeneraleConsultaPage");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "prgMovimento");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "PRGMOV");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "cdnFunzione");
			parameterSB3.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "CURRENTCONTEXT");
			parameterSB4.setAttribute("type", "ABSOLUTE");
			parameterSB4.setAttribute("value", "salva");
			parameterSB4.setAttribute("scope", "");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "ACTION");
			parameterSB5.setAttribute("scope", "");
			parameterSB5.setAttribute("type", "ABSOLUTE");
			parameterSB5.setAttribute("value", "naviga");

			SourceBean parameterSB6 = new SourceBean("PARAMETER");
			parameterSB6.setAttribute("name", "PROVENIENZA");
			parameterSB6.setAttribute("scope", "");
			parameterSB6.setAttribute("type", "ABSOLUTE");
			parameterSB6.setAttribute("value", "ListaMov");

			SourceBean parameterSB14 = new SourceBean("PARAMETER");
			if (forzaVal.equals("true")) {
				parameterSB14.setAttribute("name", "forzaVal");
				parameterSB14.setAttribute("scope", "LOCAL");
				parameterSB14.setAttribute("type", "");
				parameterSB14.setAttribute("value", "forzaVal");
			}

			// SourceBean parameterSB7 = new SourceBean("PARAMETER");
			// parameterSB7.setAttribute("name", "destinazione");
			// parameterSB7.setAttribute("scope", "");
			// parameterSB7.setAttribute("type", "ABSOLUTE");
			// parameterSB7.setAttribute("value",
			// "MovDettaglioGeneraleConsultaPage");

			SourceBean parameterSB8 = new SourceBean("PARAMETER");
			parameterSB8.setAttribute("name", "PageRitornoLista");
			parameterSB8.setAttribute("type", "ABSOLUTE");
			parameterSB8.setAttribute("value", "MovimentiRisultRicercaPage");
			parameterSB8.setAttribute("scope", "");

			SourceBean parameterSB15 = new SourceBean("PARAMETER");
			parameterSB15.setAttribute("name", "MostraTra");
			parameterSB15.setAttribute("scope", "LOCAL");
			parameterSB15.setAttribute("type", "");
			parameterSB15.setAttribute("value", "MostraTra");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			selectCaptionSB.setAttribute(parameterSB6);
			// selectCaptionSB.setAttribute(parameterSB7);
			selectCaptionSB.setAttribute(parameterSB8);
			selectCaptionSB.setAttribute(parameterSB14);
			selectCaptionSB.setAttribute(parameterSB15);
			captionsSB.setAttribute(selectCaptionSB);

			SourceBean deleteCaptionSB = new SourceBean("DELETE_CAPTION");
			deleteCaptionSB.setAttribute("image", "");
			deleteCaptionSB.setAttribute("label", "Cancella");
			deleteCaptionSB.setAttribute("confirm", "false");

			SourceBean deleteParameterSB1 = new SourceBean("PARAMETER");
			deleteParameterSB1.setAttribute("name", "PAGE");
			deleteParameterSB1.setAttribute("scope", "");
			deleteParameterSB1.setAttribute("type", "ABSOLUTE");
			deleteParameterSB1.setAttribute("value", "MovimentiRisultRicercaPage");

			SourceBean deleteParameterSB2 = new SourceBean("PARAMETER");
			deleteParameterSB2.setAttribute("name", "prgMovimento");
			deleteParameterSB2.setAttribute("scope", "LOCAL");
			deleteParameterSB2.setAttribute("type", "RELATIVE");
			deleteParameterSB2.setAttribute("value", "PRGMOV");

			SourceBean deleteParameterSB3 = new SourceBean("PARAMETER");
			deleteParameterSB3.setAttribute("name", "DELETEMOV");
			deleteParameterSB3.setAttribute("scope", "");
			deleteParameterSB3.setAttribute("type", "ABSOLUTE");
			deleteParameterSB3.setAttribute("value", "true");

			SourceBean deleteParameterSB4 = new SourceBean("PARAMETER");
			deleteParameterSB4.setAttribute("name", "cdnFunzione");
			deleteParameterSB4.setAttribute("type", "RELATIVE");
			deleteParameterSB4.setAttribute("value", "cdnFunzione");
			deleteParameterSB4.setAttribute("scope", "SERVICE_REQUEST");

			deleteCaptionSB.setAttribute(deleteParameterSB1);
			deleteCaptionSB.setAttribute(deleteParameterSB2);
			deleteCaptionSB.setAttribute(deleteParameterSB3);
			deleteCaptionSB.setAttribute(deleteParameterSB4);
			captionsSB.setAttribute(deleteCaptionSB);

			// Caption stampa
			SourceBean stampaCaptionSB = new SourceBean("CAPTION");
			stampaCaptionSB.setAttribute("image", "../../img/stampa.gif");
			stampaCaptionSB.setAttribute("label", "Stampa");
			stampaCaptionSB.setAttribute("confirm", "false");

			SourceBean stampaParameterSB1 = new SourceBean("PARAMETER");
			stampaParameterSB1.setAttribute("name", "prgMovimento");
			stampaParameterSB1.setAttribute("scope", "LOCAL");
			stampaParameterSB1.setAttribute("type", "RELATIVE");
			stampaParameterSB1.setAttribute("value", "PRGMOV");

			stampaCaptionSB.setAttribute(stampaParameterSB1);
			captionsSB.setAttribute(stampaCaptionSB);

			// Caption visualizzata solo se l'utente ne ha i diritti
			if (canInsert_Mov_Coll) {

				SourceBean addCaptionSB = new SourceBean("CAPTION");
				addCaptionSB.setAttribute("image", "../../img/add2.gif");
				addCaptionSB.setAttribute("label", "Aggiungi movimento collegato");
				addCaptionSB.setAttribute("confirm", "false");

				SourceBean addParameterSB1 = new SourceBean("PARAMETER");
				addParameterSB1.setAttribute("name", "PAGE");
				addParameterSB1.setAttribute("scope", "");
				addParameterSB1.setAttribute("type", "ABSOLUTE");
				addParameterSB1.setAttribute("value", "MovDettaglioGeneraleInserisciPage");

				SourceBean addParameterSB2 = new SourceBean("PARAMETER");
				addParameterSB2.setAttribute("name", "PRGMOVIMENTOPREC");
				addParameterSB2.setAttribute("scope", "LOCAL");
				addParameterSB2.setAttribute("type", "RELATIVE");
				addParameterSB2.setAttribute("value", "PRGMOV");

				SourceBean addParameterSB21 = new SourceBean("PARAMETER");
				addParameterSB21.setAttribute("name", "PRGMOVIMENTOSUCC");
				addParameterSB21.setAttribute("scope", "LOCAL");
				addParameterSB21.setAttribute("type", "RELATIVE");
				addParameterSB21.setAttribute("value", "PRGMOVIMENTOSUCC");

				SourceBean addParameterSB3 = new SourceBean("PARAMETER");
				addParameterSB3.setAttribute("name", "cdnFunzione");
				addParameterSB3.setAttribute("scope", "SERVICE_REQUEST");
				addParameterSB3.setAttribute("type", "RELATIVE");
				addParameterSB3.setAttribute("value", "cdnFunzione");

				SourceBean addParameterSB4 = new SourceBean("PARAMETER");
				addParameterSB4.setAttribute("name", "PROVENIENZA");
				addParameterSB4.setAttribute("scope", "");
				addParameterSB4.setAttribute("type", "ABSOLUTE");
				addParameterSB4.setAttribute("value", "ListaMov");

				SourceBean addParameterSB14 = new SourceBean("PARAMETER");
				if (forzaVal.equals("true")) {
					addParameterSB14.setAttribute("name", "forzaVal");
					addParameterSB14.setAttribute("type", "");
					addParameterSB14.setAttribute("value", "forzaVal");
					addParameterSB14.setAttribute("scope", "LOCAL");
				}

				SourceBean addParameterSB5 = new SourceBean("PARAMETER");
				addParameterSB5.setAttribute("name", "CURRENTCONTEXT");
				addParameterSB5.setAttribute("type", "ABSOLUTE");
				addParameterSB5.setAttribute("value", "inserisci");
				addParameterSB5.setAttribute("scope", "");

				SourceBean addParameterSB6 = new SourceBean("PARAMETER");
				addParameterSB6.setAttribute("name", "ACTION");
				addParameterSB6.setAttribute("scope", "");
				addParameterSB6.setAttribute("type", "ABSOLUTE");
				addParameterSB6.setAttribute("value", "naviga");

				SourceBean addParameterSB7 = new SourceBean("PARAMETER");
				addParameterSB7.setAttribute("name", "COLLEGATO");
				addParameterSB7.setAttribute("scope", "");
				addParameterSB7.setAttribute("type", "ABSOLUTE");
				addParameterSB7.setAttribute("value", "precedente");

				SourceBean addParameterSB8 = new SourceBean("PARAMETER");
				addParameterSB8.setAttribute("name", "TIPOMOV");
				addParameterSB8.setAttribute("scope", "LOCAL");
				addParameterSB8.setAttribute("type", "RELATIVE");
				addParameterSB8.setAttribute("value", "codTipoMov");

				SourceBean addParameterSB9 = new SourceBean("PARAMETER");
				addParameterSB9.setAttribute("name", "CODTIPOCESS");
				addParameterSB9.setAttribute("scope", "LOCAL");
				addParameterSB9.setAttribute("type", "RELATIVE");
				addParameterSB9.setAttribute("value", "CODMVCESSAZIONE");

				// SourceBean addParameterSB9 = new SourceBean("PARAMETER");
				// addParameterSB9.setAttribute("name", "destinazione");
				// addParameterSB9.setAttribute("type", "ABSOLUTE");
				// addParameterSB9.setAttribute("value",
				// "MovDettaglioGeneraleInserisciPage");
				// addParameterSB9.setAttribute("scope", "");

				SourceBean addParameterSB10 = new SourceBean("PARAMETER");
				addParameterSB10.setAttribute("name", "PageRitornoLista");
				addParameterSB10.setAttribute("scope", "");
				addParameterSB10.setAttribute("type", "ABSOLUTE");
				addParameterSB10.setAttribute("value", "MovimentiRisultRicercaPage");

				SourceBean addParameterSB11 = new SourceBean("PARAMETER");
				addParameterSB11.setAttribute("name", "CODSTATOATTO");
				addParameterSB11.setAttribute("scope", "LOCAL");
				addParameterSB11.setAttribute("type", "RELATIVE");
				addParameterSB11.setAttribute("value", "CODSTATOATTO");
				SourceBean addParameterSB12 = new SourceBean("PARAMETER");
				addParameterSB12.setAttribute("name", "CODMONOTIPOASS");
				addParameterSB12.setAttribute("scope", "LOCAL");
				addParameterSB12.setAttribute("type", "RELATIVE");
				addParameterSB12.setAttribute("value", "CODMONOTIPOASS");
				SourceBean addParameterSB13 = new SourceBean("PARAMETER");
				addParameterSB13.setAttribute("name", "CDNLAVORATORE");
				addParameterSB13.setAttribute("scope", "LOCAL");
				addParameterSB13.setAttribute("type", "RELATIVE");
				addParameterSB13.setAttribute("value", "CDNLAV");
				SourceBean addParameterSB15 = new SourceBean("PARAMETER");
				addParameterSB15.setAttribute("name", "CODTIPOASS");
				addParameterSB15.setAttribute("scope", "LOCAL");
				addParameterSB15.setAttribute("type", "RELATIVE");
				addParameterSB15.setAttribute("value", "CODTIPOASS");

				addCaptionSB.setAttribute(addParameterSB1);
				addCaptionSB.setAttribute(addParameterSB2);
				addCaptionSB.setAttribute(addParameterSB21);
				addCaptionSB.setAttribute(addParameterSB3);
				addCaptionSB.setAttribute(addParameterSB4);
				addCaptionSB.setAttribute(addParameterSB5);
				addCaptionSB.setAttribute(addParameterSB6);
				addCaptionSB.setAttribute(addParameterSB7);
				addCaptionSB.setAttribute(addParameterSB8);
				addCaptionSB.setAttribute(addParameterSB9);
				addCaptionSB.setAttribute(addParameterSB10);
				addCaptionSB.setAttribute(addParameterSB11);
				addCaptionSB.setAttribute(addParameterSB12);
				addCaptionSB.setAttribute(addParameterSB13);
				addCaptionSB.setAttribute(addParameterSB14);
				addCaptionSB.setAttribute(addParameterSB15);
				captionsSB.setAttribute(addCaptionSB);

			}

			// BUTTON
			SourceBean buttonsSB = new SourceBean("BUTTONS");
			// Button visualizzato solo se l'utente ne ha i diritti
			if (canInsert_Mov_Coll) {

				SourceBean button1SB = new SourceBean("INSERT_BUTTON");
				button1SB.setAttribute("image", "");
				button1SB.setAttribute("confirm", "FALSE");
				button1SB.setAttribute("label", "Nuovo movimento");

				SourceBean parameter1Button1 = new SourceBean("PARAMETER");
				parameter1Button1.setAttribute("name", "PAGE");
				parameter1Button1.setAttribute("type", "ABSOLUTE");
				parameter1Button1.setAttribute("value", "MovDettaglioGeneraleInserisciPage");
				parameter1Button1.setAttribute("scope", "");

				SourceBean parameter2Button1 = new SourceBean("PARAMETER");
				parameter2Button1.setAttribute("name", "cdnFunzione");
				parameter2Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter2Button1.setAttribute("type", "RELATIVE");
				parameter2Button1.setAttribute("value", "cdnFunzione");

				SourceBean parameter3Button1 = new SourceBean("PARAMETER");
				parameter3Button1.setAttribute("name", "cdnLavoratore");
				parameter3Button1.setAttribute("type", "RELATIVE");
				parameter3Button1.setAttribute("value", "cdnLavoratore");
				parameter3Button1.setAttribute("scope", "SERVICE_REQUEST");

				SourceBean parameter4Button1 = new SourceBean("PARAMETER");
				parameter4Button1.setAttribute("name", "prgAzienda");
				parameter4Button1.setAttribute("type", "RELATIVE");
				parameter4Button1.setAttribute("value", "prgAzienda");
				parameter4Button1.setAttribute("scope", "SERVICE_REQUEST");

				SourceBean parameter5Button1 = new SourceBean("PARAMETER");
				parameter5Button1.setAttribute("name", "prgUnita");
				parameter5Button1.setAttribute("type", "RELATIVE");
				parameter5Button1.setAttribute("value", "prgUnita");
				parameter5Button1.setAttribute("scope", "SERVICE_REQUEST");

				SourceBean parameter6Button1 = new SourceBean("PARAMETER");
				parameter6Button1.setAttribute("name", "PROVENIENZA");
				parameter6Button1.setAttribute("type", "RELATIVE");
				parameter6Button1.setAttribute("value", "provenienza");
				parameter6Button1.setAttribute("scope", "SERVICE_REQUEST");

				SourceBean parameter7Button1 = new SourceBean("PARAMETER");
				parameter7Button1.setAttribute("name", "CURRENTCONTEXT");
				parameter7Button1.setAttribute("type", "ABSOLUTE");
				parameter7Button1.setAttribute("value", "inserisci");
				parameter7Button1.setAttribute("scope", "");

				SourceBean parameter8Button1 = new SourceBean("PARAMETER");
				parameter8Button1.setAttribute("name", "ACTION");
				parameter8Button1.setAttribute("type", "ABSOLUTE");
				parameter8Button1.setAttribute("value", "naviga");
				parameter8Button1.setAttribute("scope", "");

				SourceBean parameter9Button1 = new SourceBean("PARAMETER");
				parameter9Button1.setAttribute("name", "COLLEGATO");
				parameter9Button1.setAttribute("type", "ABSOLUTE");
				parameter9Button1.setAttribute("value", "nessuno");
				parameter9Button1.setAttribute("scope", "");

				// SourceBean parameter10Button1 = new SourceBean("PARAMETER");
				// parameter10Button1.setAttribute("name", "destinazione");
				// parameter10Button1.setAttribute("type", "ABSOLUTE");
				// parameter10Button1.setAttribute("value",
				// "MovDettaglioGeneraleInserisciPage");
				// parameter10Button1.setAttribute("scope", "");

				SourceBean parameter11Button1 = new SourceBean("PARAMETER");
				parameter11Button1.setAttribute("name", "PageRitornoLista");
				parameter11Button1.setAttribute("type", "ABSOLUTE");
				parameter11Button1.setAttribute("value", "MovimentiRisultRicercaPage");
				parameter11Button1.setAttribute("scope", "");

				button1SB.setAttribute(parameter1Button1);
				button1SB.setAttribute(parameter2Button1);
				button1SB.setAttribute(parameter3Button1);
				button1SB.setAttribute(parameter4Button1);
				button1SB.setAttribute(parameter5Button1);
				button1SB.setAttribute(parameter6Button1);
				button1SB.setAttribute(parameter7Button1);
				button1SB.setAttribute(parameter8Button1);
				button1SB.setAttribute(parameter9Button1);
				// button1SB.setAttribute(parameter10Button1);
				button1SB.setAttribute(parameter11Button1);

				buttonsSB.setAttribute(button1SB);
			}

			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Lista Movimenti");
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);
			if (canInsert_Mov_Coll) {
				configSB.setAttribute(buttonsSB);
			}
			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}

		finally {
			Utils.releaseResources(dc, null, null);
		}

		return configSB;
	}
}