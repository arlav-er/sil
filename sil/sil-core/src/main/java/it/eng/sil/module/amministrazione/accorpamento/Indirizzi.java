/*
 * Created on Jun 23, 2005
 *
 */
package it.eng.sil.module.amministrazione.accorpamento;

import com.engiweb.framework.base.SourceBean;

/**
 * Classe che contiene i dati da visualizzare nella pagina di VISUALIZZA E CONFRONTA dell'accorpamento lavoratore,
 * sezioni domicilio, residenza, altri riferimenti. Queste tre sezioni sono presenti nella pagina di
 * anagrafica-indirizzi e sono valorizzate con le informazioni di un unico modulo. Non dovrebbe mai essere vuoto o null,
 * pero' le informazioni contenute si riferiscono a campi di an_lavoratore che possono essere null.
 * 
 * @author savino
 */
public class Indirizzi {

	public String strNome = "";
	public String strCognome = "";
	public String codComRes = "";
	public String strComRes = "";
	public String provRes = "";
	public String strIndirizzores = "";
	public String strLocalitares = "";
	public String strCapRes = "";
	public String codComdom = "";
	public String strComdom = "";
	public String provDom = "";
	public String strIndirizzodom = "";
	public String strLocalitadom = "";
	public String strCapDom = "";
	public String numKloLavoratore = "";
	public String codCpiTit = "";
	public String strCpiTit = "";
	public String codMonoTipoCpi = "";
	public String codCpiOrig = "";
	public String strCpiOrig = "";

	public String strTelRes = "";
	public String strTelDom = "";
	public String strTelAltro = "";
	public String strCell = "";
	public String strEmail = "";
	public String strFax = "";
	public String codCpiComp = codCpiTit;
	public String strCpiComp = strCpiTit;

	public Indirizzi(SourceBean row) {
		if (row == null)
			return;
		if (row.containsAttribute("STRTELRES")) {
			strTelRes = row.getAttribute("STRTELRES").toString();
		}
		if (row.containsAttribute("STRTELDOM")) {
			strTelDom = row.getAttribute("STRTELDOM").toString();
		}
		if (row.containsAttribute("STRTELALTRO")) {
			strTelAltro = row.getAttribute("STRTELALTRO").toString();
		}
		if (row.containsAttribute("STRCELL")) {
			strCell = row.getAttribute("STRCELL").toString();
		}
		if (row.containsAttribute("STREMAIL")) {
			strEmail = row.getAttribute("STREMAIL").toString();
		}
		if (row.containsAttribute("STRFAX")) {
			strFax = row.getAttribute("STRFAX").toString();
		}

		if (row.containsAttribute("codComRes")) {
			codComRes = row.getAttribute("codComRes").toString();
		}
		if (row.containsAttribute("provRes")) {
			provRes = row.getAttribute("provRes").toString();
		}
		if (row.containsAttribute("strComRes")) {
			strComRes = row.getAttribute("strComRes").toString() + (!provRes.equals("") ? " (" + provRes + ")" : "");
		}
		if (row.containsAttribute("strIndirizzores")) {
			strIndirizzores = row.getAttribute("strIndirizzores").toString();
		}
		if (row.containsAttribute("strLocalitares")) {
			strLocalitares = row.getAttribute("strLocalitares").toString();
		}
		if (row.containsAttribute("strCapRes")) {
			strCapRes = row.getAttribute("strCapRes").toString();
		}
		if (row.containsAttribute("codComdom")) {
			codComdom = row.getAttribute("codComdom").toString();
		}
		if (row.containsAttribute("provDom")) {
			provDom = row.getAttribute("provDom").toString();
		}
		if (row.containsAttribute("strComdom")) {
			strComdom = row.getAttribute("strComdom").toString() + (!provDom.equals("") ? " (" + provDom + ")" : "");
		}
		if (row.containsAttribute("strIndirizzodom")) {
			strIndirizzodom = row.getAttribute("strIndirizzodom").toString();
		}
		if (row.containsAttribute("strLocalitadom")) {
			strLocalitadom = row.getAttribute("strLocalitadom").toString();
		}
		if (row.containsAttribute("strCapDom")) {
			strCapDom = row.getAttribute("strCapDom").toString();
		}
		/*
		 * if (row.containsAttribute("cdnUtins")) {cdnUtins=row.getAttribute("cdnUtins").toString();} if
		 * (row.containsAttribute("dtmins")) {dtmins=row.getAttribute("dtmins").toString();} if
		 * (row.containsAttribute("cdnUtmod")) {cdnUtmod=row.getAttribute("cdnUtmod").toString();} if
		 * (row.containsAttribute("dtmmod")) {dtmmod=row.getAttribute("dtmmod").toString();}
		 */
		if (row.containsAttribute("numKloLavoratore")) {
			numKloLavoratore = row.getAttribute("numKloLavoratore").toString();
		}
		if (row.containsAttribute("CODCPITIT")) {
			codCpiTit = row.getAttribute("CODCPITIT").toString();
		}
		if (row.containsAttribute("STRDESCRIZIONE")) {
			strCpiTit = row.getAttribute("STRDESCRIZIONE").toString();
		}
		if (row.containsAttribute("CODMONOTIPOCPI")) {
			codMonoTipoCpi = (String) row.getAttribute("CODMONOTIPOCPI");
		}
		if (row.containsAttribute("CODCPIORIG")) {
			codCpiOrig = row.getAttribute("CODCPIORIG").toString();
		}
		if (row.containsAttribute("STRDESCRIZIONEORIG")) {
			strCpiOrig = row.getAttribute("STRDESCRIZIONEORIG").toString();
		}

		// if (row.containsAttribute("NumKloLavStoriaInf"))
		// {numKloLavStoriaInf=row.getAttribute("NumKloLavStoriaInf").toString();}

		if (codMonoTipoCpi.equalsIgnoreCase("T")) {
			codCpiComp = codCpiOrig;
			strCpiComp = strCpiOrig;
		}

	}
}
