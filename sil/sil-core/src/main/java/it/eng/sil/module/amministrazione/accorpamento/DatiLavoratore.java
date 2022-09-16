/*
 * Created on Jun 23, 2005
 *
 */
package it.eng.sil.module.amministrazione.accorpamento;

import com.engiweb.framework.base.SourceBean;

/**
 * Classe che contiene i dati da visualizzare nella pagina di VISUALIZZA E CONFRONTA dell'accorpamento lavoratore,
 * sezione Dati personali. Il SourceBean deve essere presente, per cui se e' null viene lanciata una eccezione.
 * 
 * @author savino
 * 
 */
public class DatiLavoratore {
	public String cdnLavoratore = "";
	public String strCodiceFiscale = "";
	public String strCognome = "";
	public String strNome = "";
	public String strSesso = "";
	public String datNasc = "";
	public String codComNas = "";
	public String strComNas = "";
	public String codCittadinanza = "";
	public String strCittadinanza = "";
	public String strNazione = "";
	public String codCittadinanza2 = "";
	public String strCittadinanza2 = "";
	public String strNazione2 = "";
	public String codstatoCivile = "";
	public String numFigli = "";
	public String strNote = "";
	public String flgMilite = "";
	public String strFlgcfOk = "";

	/**
	 * @param row
	 *            SourceBean contenente i dati del lavoratore (vedi pagina anagrafica)
	 * 
	 * @throws Exception
	 *             se row null
	 */
	public DatiLavoratore(SourceBean row) throws Exception {
		if (row == null)
			throw new Exception("Impossibile istanziare DatiLavoratore: il SourceBean e' null");
		if (row.containsAttribute("cdnLavoratore")) {
			cdnLavoratore = row.getAttribute("cdnLavoratore").toString();
		}
		if (row.containsAttribute("strCodiceFiscale")) {
			strCodiceFiscale = row.getAttribute("strCodiceFiscale").toString();
		}
		if (row.containsAttribute("strCognome")) {
			strCognome = row.getAttribute("strCognome").toString();
		}
		if (row.containsAttribute("strNome")) {
			strNome = row.getAttribute("strNome").toString();
		}
		if (row.containsAttribute("strSesso")) {
			strSesso = row.getAttribute("strSesso").toString();
		}
		if (row.containsAttribute("datNasc")) {
			datNasc = row.getAttribute("datNasc").toString();
		}
		if (row.containsAttribute("codComNas")) {
			codComNas = row.getAttribute("codComNas").toString();
		}
		if (row.containsAttribute("strComNas")) {
			strComNas = row.getAttribute("strComNas").toString();
		}
		if (row.containsAttribute("codCittadinanza")) {
			codCittadinanza = row.getAttribute("codCittadinanza").toString();
		}
		if (row.containsAttribute("strCittadinanza")) {
			strCittadinanza = row.getAttribute("strCittadinanza").toString();
		}
		if (row.containsAttribute("strNazione")) {
			strNazione = row.getAttribute("strNazione").toString();
		}
		if (row.containsAttribute("codCittadinanza2")) {
			codCittadinanza2 = row.getAttribute("codCittadinanza2").toString();
		}
		if (row.containsAttribute("strCittadinanza2")) {
			strCittadinanza2 = row.getAttribute("strCittadinanza2").toString();
		}
		if (row.containsAttribute("strNazione2")) {
			strNazione2 = row.getAttribute("strNazione2").toString();
		}
		if (row.containsAttribute("codstatoCivile")) {
			codstatoCivile = row.getAttribute("codstatoCivile").toString();
		}
		if (row.containsAttribute("numFigli")) {
			numFigli = row.getAttribute("numFigli").toString();
		}
		if (row.containsAttribute("strNote")) {
			strNote = row.getAttribute("strNote").toString();
		}
		if (row.containsAttribute("FLGCFOK")) {
			strFlgcfOk = row.getAttribute("FLGCFOK").toString();
		}
		// if (row.containsAttribute("cdnUtins"))
		// {cdnUtins=row.getAttribute("cdnUtins").toString();}
		// if (row.containsAttribute("dtmins"))
		// {dtmins=row.getAttribute("dtmins").toString();}
		// if (row.containsAttribute("cdnUtmod"))
		// {cdnUtmod=row.getAttribute("cdnUtmod").toString();}
		// if (row.containsAttribute("dtmmod"))
		// {dtmmod=row.getAttribute("dtmmod").toString();}
		// if (row.containsAttribute("numKloLavoratore"))
		// {numKloLavoratore=row.getAttribute("numKloLavoratore").toString();}
		flgMilite = row.containsAttribute("flgMilite") ? row.getAttribute("flgMilite").toString() : "";
	}
}
