package it.eng.sil.util;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.jsp.JspWriter;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.LogUtils;
import it.eng.sil.Values;

/*
 * @author: Stefania Orioli
 * 
 * Questa classe genera il codice javascript da inserire nella Jsp per richiamare
 * la popup con i messaggi in evidenza (non scaduti) per un certo lavoratore.
 * 1) Nella JSP inserire
 * 	 EvidenzePopUp jsEvid = new EvidenzePopUp(String cdnLavoratore, int cdnFunzione);
 * 2) Nell'header va messa la chiamata:
 * 	 <% jsEvid.show(out); %>
 * 3) Infine per l'apertura automatica della popup la funzione javascript "apriEvidenze()" va 
 * 	 chiamata sull'evento onload:
 * 	 <body onLoad="apriEvidenze()">
 *    oppure
 * 	 <body onLoad="apriEvidenze();rinfresca()"> nel caso in cui l'evento onLoad fosse
 * 	 gia' stato utilizzato per chiamare la funzione rinfresca()
 *  
 */

public class EvidenzePopUp {

	private final String className = this.getClass().getName();
	private String cdnLavoratore = "";
	private int cdnFunzione = 0;
	private int cdnGruppo = 0;
	private int cdnProfilo = 0;

	private EvidenzePopUp() {
	}

	public EvidenzePopUp(String cdnLavoratore, int cdnFunzione, int cdnGruppo, int cdnProfilo) {
		this.cdnLavoratore = cdnLavoratore;
		this.cdnFunzione = cdnFunzione;
		this.cdnGruppo = cdnGruppo;
		this.cdnProfilo = cdnProfilo;
	}

	public void show(JspWriter out) throws IOException {
		boolean exEv = false;
		String js = "";

		try {
			exEv = exEvid();
			js = addScript(exEv);
		} catch (Exception e) {
			exEv = false;
			js = "<script language=\"JavaScript\">\n" + "function apriEvidenze() { } \n" + "</script>";
		}
		out.println(js);
	} // public void show(JspWriter out, boolean popUp)

	/*
	 * PRIVATE
	 */

	// private boolean exEvid(String cdnLav) throws SourceBeanException {
	private boolean exEvid() throws SourceBeanException {
		DataConnectionManager dcm = null;
		DataConnection conn = null;
		SQLCommand stmt = null;
		DataResult res = null;
		ScrollableDataResult sdr = null;
		SourceBean rowsSourceBean = null;
		SourceBean row = null;
		StringBuffer buf = new StringBuffer();
		boolean eEv = false;
		String errMsg = "Errore durante il controllo sull'esistenza delle evidenze non scadute";

		try {
			dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);

			buf.append("select count(1) as nroEvNonScad from an_evidenza e ");
			buf.append(" inner join ts_vis_evidenza evis on (e.PRGTIPOEVIDENZA=evis.PRGtipoevidenza and cdngruppo=");
			buf.append(this.cdnGruppo);
			buf.append(") where e.cdnLavoratore=");
			buf.append(this.cdnLavoratore);
			buf.append(" and cdnprofilo=");
			buf.append(this.cdnProfilo);
			buf.append(" and trunc(e.datDataScad)>=trunc(sysdate) ");
			stmt = conn.createSelectCommand(buf.toString());
			LogUtils.logDebug("MSalvaFestivo", buf.toString(), this);
			res = stmt.execute();
			sdr = (ScrollableDataResult) res.getDataObject();
			rowsSourceBean = sdr.getSourceBean();
			BigDecimal nro = (BigDecimal) rowsSourceBean.getAttribute("ROW.NROEVNONSCAD");
			if (nro.intValue() > 0) {
				eEv = true;
			} else {
				eEv = false;
			}
		} // try
		catch (Exception ex) {
			eEv = false;
			LogUtils.logError(this.className, errMsg, ex, this);
		} finally {
			Utils.releaseResources(conn, stmt, res);
		}
		return eEv;
	} // private boolean exEvid(Object codLavoratore)

	private String addScript(boolean exEv) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script language=\"JavaScript\">\n ");
		buf.append("function apriEvidenze() {\n ");
		if (exEv) {
			buf.append("window.open(\"AdapterHTTP?PAGE=ListaEvidenzePage&SCAD=N&CDNLAVORATORE=" + this.cdnLavoratore);
			buf.append("&CDNFUNZIONE=" + Integer.toString(cdnFunzione));
			buf.append("\",\"Evidenze\", ");
			buf.append(
					"\"toolbar=NO,statusbar=YES,height=500,width=800,scrollbars=YES,resizable=YES,left=150,top=100\");\n");
		}
		buf.append("}\n");
		buf.append("</script>\n");
		return buf.toString();
	} // private String addScript(Object codLavoratore)

}