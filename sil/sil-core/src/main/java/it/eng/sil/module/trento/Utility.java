package it.eng.sil.module.trento;

public class Utility {
	public static String replaceDatiCPI(String editor, String cod_cpi, String desc_cpi, String indirizzo, String localita, String cap, String codcom, 
			String codProvincia, String comune, String provincia, String telefono, String fax, String email, String orario, String responsabile, String EMAIL_PEC, String EMAIL_ADL) {
		if (cod_cpi != null)
			editor = editor.replace("@CodiceCPI", cod_cpi);

		if (desc_cpi != null)
			editor = editor.replace("@Nome", desc_cpi);
		else
			editor = editor.replace("@Nome", "_");
		
		if (indirizzo != null)
			editor = editor.replace("@Indirizzo", indirizzo);
		else
			editor = editor.replace("@Indirizzo", "_");
		
		if (localita != null)
			editor = editor.replace("@Localita", localita);
		else
			editor = editor.replace("@Localita", "_");

		if (cap != null)
			editor = editor.replace("@Cap", cap);
		else
			editor = editor.replace("@Cap", "_");

		if (codcom != null)
			editor = editor.replace("@CodComune", codcom);
		else
			editor = editor.replace("@CodComune", "_");
		
		if (codProvincia != null)
			editor = editor.replace("@CodProvincia", codProvincia);
		else
			editor = editor.replace("@CodProvincia", "_");

		if (comune != null)
			editor = editor.replace("@Comune", comune);
		else
			editor = editor.replace("@Comune", "_");

		if (provincia != null)
			editor = editor.replace("@Provincia", provincia);
		else
			editor = editor.replace("@Provincia", "_");
		
		if (telefono != null)
			editor = editor.replace("@Telefono", telefono);
		else
			editor = editor.replace("@Telefono", "_");

		if (fax != null)
			editor = editor.replace("@Fax", fax);
		else
			editor = editor.replace("@Fax", "_");
		
		if (email != null)
			editor = editor.replace("@E-mail", email);
		else
			editor = editor.replace("@E-mail", "_");

		if (orario != null)
			editor = editor.replace("@Orario", orario);
		else
			editor = editor.replace("@Orario", "_");
		
		if (responsabile != null)
			editor = editor.replace("@Responsabile", responsabile);
		else
			editor = editor.replace("@Responsabile", "_");
		if (email != null)
			editor = editor.replace("@emailPEC", EMAIL_PEC);
		else
			editor = editor.replace("@emailPEC", "_");

		if (email != null)
			editor = editor.replace("@emailADL", EMAIL_ADL);
		else
			editor = editor.replace("@emailADL", "_");

		return editor;
	}

	public static String replaceDatiLavoratoreFake(String par) {

		par = par.replace("@NomeLavoratore", "Fabiana");
		par = par.replace("@CognomeLavoratore", "Piccinino");
		par = par.replace("@Sesso", "F");
		par = par.replace("@CodiceFiscale", "PCNFFB66C27L049Z");
		par = par.replace("@ComuneNascita", "RAGUSA");
		par = par.replace("@IndirizzoDomicilio", "VIA LAGA");
		par = par.replace("@IndirizzoResidenza", "VIA LAGA");
		par = par.replace("@ComuneDomicilio", "BOLOGNA");
		par = par.replace("@ComuneResidenza", "BOLOGNA");
		par = par.replace("@DataNascita", "27/04/1979");
		par = par.replace("@Nazione", "ITALIA");
		par = par.replace("@Cittadinanza", "italiana");
		par = par.replace("@SecondaCittadinanza", "italiana");
		par = par.replace("@SecondaNazionalita", "italiana");
		par = par.replace("@ProvinciaResidenza", "BOLOGNA");
		par = par.replace("@LocalitaResidenza", "BOLOGNA");
		par = par.replace("@CapResidenza", "40100");
		par = par.replace("@ProvinciaDomicilio", "BOLOGNA");
		par = par.replace("@LocalitaDomicilio", "BOLOGNA");
		par = par.replace("@CapDomicilio", "40100");
		par = par.replace("@TelefonoResidenza", "051889988");
		par = par.replace("@TelefonoDomicilio", "051889988");

		return par;
	}

	public static String replaceDatiLavoratore(String editor,
			String strcodicefiscale, String strcognome, String strnome,
			String strsesso, String datnasc, String strcomnas,
			String strcittadinanza, String strnazione, String strcittadinanza2,
			String strnazione2, String strcomres, String provres,
			String strindirizzores, String strlocalitares, String strcapres,
			String strcomdom, String provdom, String strindirizzodom,
			String strlocalitadom, String strcapdom, String strtelres,
			String strteldom) {
		
		if (strnome != null)
			editor = editor.replace("@NomeLavoratore", strnome);
		else
			editor = editor.replace("@NomeLavoratore", "_");

		if (strcognome != null)
			editor = editor.replace("@CognomeLavoratore", strcognome);
		else
			editor = editor.replace("@CognomeLavoratore", "_");

		if (strcodicefiscale != null)
			editor = editor.replace("@CodiceFiscale", strcodicefiscale);
		else
			editor = editor.replace("@CodiceFiscale", "_");

		if (strsesso != null)
			editor = editor.replace("@Sesso", strsesso);
		else
			editor = editor.replace("@Sesso", "_");

		if (datnasc != null)
			editor = editor.replace("@DataNascita", datnasc);
		else
			editor = editor.replace("@DataNascita", "_");

		if (strnazione != null)
			editor = editor.replace("@Nazione", strnazione);
		else
			editor = editor.replace("@Nazione", "_");

		if (strcittadinanza != null)
			editor = editor.replace("@Cittadinanza", strcittadinanza);
		else
			editor = editor.replace("@Cittadinanza", "_");

		if (strcittadinanza2 != null)
			editor = editor.replace("@SecondaCittadinanza", strcittadinanza2);
		else
			editor = editor.replace("@SecondaCittadinanza", "_");

		if (strcomnas != null)
			editor = editor.replace("@ComuneNascita", strcomnas);
		else
			editor = editor.replace("@ComuneNascita", "_");

		if (strindirizzodom != null)
			editor = editor.replace("@IndirizzoDomicilio", strindirizzodom);
		else
			editor = editor.replace("@IndirizzoDomicilio", "_");
	
		if (strindirizzores != null)
			editor = editor.replace("@IndirizzoResidenza", strindirizzores);
		else
			editor = editor.replace("@IndirizzoResidenza", "_");

		if (strcomdom != null)
			editor = editor.replace("@ComuneDomicilio", strcomdom);
		else
			editor = editor.replace("@ComuneDomicilio", "_");

		if (strcomres != null)
			editor = editor.replace("@ComuneResidenza", strcomres);
		else
			editor = editor.replace("@ComuneResidenza", "_");

		if (strnazione2 != null)
			editor = editor.replace("@SecondaNazionalita", strnazione2);
		else
			editor = editor.replace("@SecondaNazionalita", "_");

		if (provres != null)
			editor = editor.replace("@ProvinciaResidenza", provres);
		else
			editor = editor.replace("@ProvinciaResidenza", "_");

		if (strlocalitares != null)
			editor = editor.replace("@LocalitaResidenza", strlocalitares);
		else
			editor = editor.replace("@LocalitaResidenza", "_");
		
		if (strcapres != null)
			editor = editor.replace("@CapResidenza", strcapres);
		else
			editor = editor.replace("@CapResidenza", "_");

		if (provdom != null)
			editor = editor.replace("@ProvinciaDomicilio", provdom);
		else
			editor = editor.replace("@ProvinciaDomicilio", "_");

		if (strlocalitadom != null)
			editor = editor.replace("@LocalitaDomicilio", strlocalitadom);
		else
			editor = editor.replace("@LocalitaDomicilio", "_");

		if (strcapdom != null)
			editor = editor.replace("@CapDomicilio", strcapdom);
		else
			editor = editor.replace("@CapDomicilio", "_");

		if (strtelres != null)
			editor = editor.replace("@TelefonoResidenza", strtelres);
		else
			editor = editor.replace("@TelefonoResidenza", "_");

		if (strteldom != null)
			editor = editor.replace("@TelefonoDomicilio", strteldom);
		else
			editor = editor.replace("@TelefonoDomicilio", "_");
		return editor;
	}

	
}
