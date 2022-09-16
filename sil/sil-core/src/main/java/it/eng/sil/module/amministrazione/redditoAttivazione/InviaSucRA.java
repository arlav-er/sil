package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.datatype.DatatypeConfigurationException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.NuovoRedditoAttivazioneProxy;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioniSuccessiveNraBean;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioniSuccessiveNraType;
import it.eng.sil.module.AbstractSimpleModule;

public class InviaSucRA extends AbstractSimpleModule {

	private String END_POINT_NAME_CLIENT = "NuovoRedditoAttivazione";
	private static final long serialVersionUID = 5863792564864524603L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaSucRA.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws ParseException {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		DataConnectionManager dcm = null;
		DataConnection conn = null;
		Connection con = null;
		PreparedStatement ps = null;
		String sqlStr = null;

		Date dataCreazioneComunicazione = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(dataCreazioneComunicazione.getTime()));
		String identificativoComunicazione = "" + calendar.get(Calendar.YEAR);
		int lInt = calendar.get(Calendar.MONTH) + 1;
		identificativoComunicazione = identificativoComunicazione + (lInt > 9 ? lInt : "0" + lInt);
		lInt = calendar.get(Calendar.DAY_OF_MONTH);
		identificativoComunicazione = identificativoComunicazione + (lInt > 9 ? lInt : "0" + lInt);
		lInt = calendar.get(Calendar.HOUR_OF_DAY);
		identificativoComunicazione = identificativoComunicazione + (lInt > 9 ? lInt : "0" + lInt);
		lInt = calendar.get(Calendar.MINUTE);
		identificativoComunicazione = identificativoComunicazione + (lInt > 9 ? lInt : "0" + lInt);
		lInt = calendar.get(Calendar.SECOND);
		identificativoComunicazione = identificativoComunicazione + (lInt > 9 ? lInt : "0" + lInt);
		lInt = calendar.get(Calendar.MILLISECOND);
		identificativoComunicazione = identificativoComunicazione
				+ (lInt > 9 ? (lInt > 99 ? lInt : "0" + lInt) : "00" + lInt);

		String codiceOperatore = (String) request.getAttribute("codiceOperatore");
		BigInteger cdnutmod = new BigInteger((String) request.getAttribute("CDNUTMOD"));
		BigInteger NUMKLONUOVORA = new BigInteger((String) request.getAttribute("NUMKLONUOVORA"));
		BigInteger prgNuovoRA = new BigInteger((String) request.getAttribute("prgnuovora"));

		// Aggiornamento informazioni di invio sul DB
		try {
			dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);
			sqlStr = SQLStatements.getStatement("UPDATE_INFO_INVIO_NUOVO_RA");
			_logger.debug("Update informazioni di invio. Query: " + sqlStr);
			_logger.debug("Update informazioni di invio. Parametri: " + dataCreazioneComunicazione + ", " + ", "
					+ identificativoComunicazione + ", " + codiceOperatore + ", " + codiceOperatore + ", "
					+ cdnutmod.toString() + ", " + NUMKLONUOVORA.toString() + ", " + prgNuovoRA.toString());
			con = conn.getInternalConnection();
			ps = con.prepareStatement(sqlStr);
			ps.setDate(1, dataCreazioneComunicazione);
			ps.setString(2, identificativoComunicazione);
			ps.setString(3, codiceOperatore);
			ps.setBigDecimal(4, new BigDecimal(cdnutmod));
			ps.setBigDecimal(5, new BigDecimal(prgNuovoRA));
			ps.execute();
			_logger.debug(
					"Inserimento data di crezione, id ed operatore dell'inivio della comunicazione successiva relativa a nuovo reddito di attivazione");
		} catch (EMFInternalError e) {
			_logger.error("Error: " + className + " Message:" + e.getMessage());
		} catch (SQLException e) {
			_logger.error("Error: " + className + " Message:" + e.getMessage());
		} finally {
			Utils.releaseResources(conn, null, null);
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			request.setAttribute("DATACREAZIONECOMUNICAZVALIDATA", sdf.format(dataCreazioneComunicazione));
			request.setAttribute("IDCOMUNICAZIONEVALIDATA", identificativoComunicazione);
			request.setAttribute("CODICEOPERATOREVALIDATA", codiceOperatore);
		} catch (SourceBeanException e) {
			_logger.error("Error: " + className + " Message:" + e.getMessage());
		}

		////////////
		// METODO WS
		/////////////
		_logger.debug("Chiamata al WS");
		EsitoComunicazioneType esito = null;

		ComunicazioniSuccessiveNraBean beanValidazioneSucc = new ComunicazioniSuccessiveNraBean(request);

		try {
			ComunicazioniSuccessiveNraType domanda = beanValidazioneSucc.comunicazioneDomandaNra();

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String urlNRA = eps.getUrl(END_POINT_NAME_CLIENT);
			// pezza per la PDD Trento
			urlNRA += "/validazioneComunicazioniSuccessive";
			NuovoRedditoAttivazioneProxy nra = new NuovoRedditoAttivazioneProxy(urlNRA);

			esito = nra.validazioneComunicazioniSuccessive(domanda);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			_logger.error("Error: " + className + " Message:" + e.getMessage());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			_logger.error("Error: " + className + " Message:" + e.getMessage());
		}

		// Imposta i i parametri per l'aggiornamento
		try {
			if (esito != null) {
				if (esito.getCodice().equals("A100")) {
					response.setAttribute("invio_corretto", "ok");
					reportOperation.reportSuccess(MappaturaErroriClientNRA.getSilCode("A100"));
				} else {
					reportOperation.reportFailure(MappaturaErroriClientNRA.getSilCode(esito.getCodice()));
				}
			} else
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		} catch (SourceBeanException e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}
	}
}
