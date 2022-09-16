<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 java.math.*,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.util.StyleUtils,
                 it.eng.sil.security.*"%>
            

<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

//dati della richiesta
BigDecimal numAnno		= null; 
BigDecimal numRichiesta	= null;
BigDecimal numRichiestaOrig	= null;
String ente				= ""; 
String luogoDiLavoro	= "";
BigDecimal posti		= null;  
BigDecimal numProfili 	= null;  
BigDecimal exLsu		= null;
BigDecimal exMilitari	= null;
BigDecimal mobilita	= null;
String mansione			= "";
String requisiti		= "";
String contratto		= "";  
String orario	 		= ""; 
String notaFissa 		= ""; 
String notaSpec 		= "";
String datChiam 		= "";
String riusoGrad 		= "";
//specifiche per la provincia ed il cpi
String prov				= "";
String codProv			= "";
String cpi				= "";
String codCpi			= "";
String indirizzo		= "";
String tel				= "";
String fax				= "";
String email			= "";
String dataPubb			= "";
String notaFissaCpi		= "";
String notaChiusura		= "";
//variabili della riga precedente
String datChiamPrec		= "";
String dataPubbPrec		= "";
String codCpiPrec		= "";
String notaCpiPrec		= "";
String codCpiSucc		= "";
String datChiamSucc     = "";

//Vector listaPubb = serviceResponse.getAttributeAsVector("M_PUB_SPAMPA_DETTAGLIO_ASTA_CM.ROWS.ROW");
%>

<html>
<head>
	<title>Pubblicazioni delle chiamate per le aste</title>
	<STYLE type="text/css">
	td.titolo {	font-family: Verdana, Arial, Helvetica, Sans-serif;
				font-size: 20px; 
				font-weight: bold;
				text-color: #000000;
				text-align: center;
	}	
	td.head   {	font-family: Verdana, Arial, Helvetica, Sans-serif;
				font-size: 14px; 
				font-weight: bold;
				text-color: #000000;
				text-align: center;
				vertical-align: middle;
				border-right: 2px solid black;
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 3px;
				padding-bottom: 3px;
				
	}
	td.head2  {	font-family: Verdana, Arial, Helvetica, Sans-serif;
				font-size: 14px; 
				font-weight: bold;
				text-color: #000000;
				text-align: center;
				vertical-align: middle;
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 3px;
				padding-bottom: 3px;
	}
	td.cross  {	font-family: Verdana, Arial, Helvetica, Sans-serif;
				font-size: 14px; 
				font-weight: normal;
				text-color: #000000;
				text-align: center;
				vertical-align: middle;
				border-right: 2px solid black;
				border-bottom: 2px solid black;
				border-top: 2px solid black;
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 3px;
				padding-bottom: 3px;
	}
	td.cross2 {	font-family: Verdana, Arial, Helvetica, Sans-serif;
				font-size: 14px; 
				font-weight: normal;
				text-color: #000000;
				text-align: center;
				vertical-align: middle;
				border-bottom: 2px solid black;
				border-top: 2px solid black;
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 3px;
				padding-bottom: 3px;
	}	
	td.nota	  {	font-family: Verdana, Arial, Helvetica, Sans-serif;
				font-size: 15px; 
				text-color: #000000;
				text-align: justify;
				vertical-align: middle;
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 3px;
				padding-bottom: 3px;				
	}
	
	table.griglia { font-family: Verdana, Arial, Helvetica, Sans-serif;
					border-top: 2px solid black;
					border-left: 2px solid black;					
					border-right: 2px solid black;
					border-bottom: 2px solid black;								
	}
	table.fontpage  { font-family: Verdana, Arial, Helvetica, Sans-serif;
				font-size: 14px;
	}
	
	</STYLE>
</head>
<body>
<% 
	SourceBean pr	= (SourceBean)serviceResponse.getAttribute("M_PUB_NOTA_FISSA.ROWS.ROW");
    codProv			= StringUtils.getAttributeStrNotNull(pr,"CODPROVINCIASIL");
%>
	
	<table width="100%" class="fontpage">
		<tr>
			<td align="left" colspan="1" width="50%">
				<img src="../../img/loghi/prov_<%=codProv%>.gif" height="70" >
			</td>
			<td align="right" colspan="1" width="50%">
				<img src="../../img/loghi/prov_<%=codProv%>_cpi.gif" height="70" >
			</td>
		<tr>
	</table>
<% 	
	String tabCpi		= "";
	String tabChiam		= "";
	String tabPubb		= "";
	String tr			= "";
	String riga			= "";
	String tabTot		= "";
	String tabNota		= "";
	//variabili per il protocollo
	String eticProt			= " PROTOCOLLO N. ";
	String numProt 			= StringUtils.getAttributeStrNotNull(serviceRequest,"NUMPROT");
	BigDecimal stampProt	= null;
	BigDecimal numRichProt	= null;	
	String strStampProt		= "";
	String strNumRichProt	= "";
	String protocollo		= "";
	Vector listNumProt 		= null;
	String CODMONOTIPOGRAD  = "";
	String tipoGrad         = "";	
	
	Vector listPubbPrec	= serviceResponse.getAttributeAsVector("M_PUB_SPAMPA_DETTAGLIO_ASTA_CM.ROWS.ROW");
	if(!numProt.equals("")){
		listNumProt	= serviceResponse.getAttributeAsVector("M_PUB_NUM_PROT_CM.PROTOCOLLO.ROWS.ROW");
	} else {
		listNumProt	= serviceResponse.getAttributeAsVector("M_PUB_NUM_PROT_CM.ROWS.ROW");
	}

	for(int j=0; j<listPubbPrec.size(); j++){
		SourceBean resPrec 	    = (SourceBean) listPubbPrec.get(j);
		SourceBean resPrecSucc = null;
//		if(listPubbPrec.size() != j+1){
//			resPrecSucc 		= (SourceBean) listPubbPrec.get(j+1);
//			codCpiSucc			= StringUtils.getAttributeStrNotNull(resPrecSucc,"CODCPI");
//			datChiamSucc   		= StringUtils.getAttributeStrNotNull(resPrecSucc,"DATCHIAMATACM");
//		}
		codCpi					= StringUtils.getAttributeStrNotNull(resPrec,"CODCPI");
		datChiam 				= StringUtils.getAttributeStrNotNull(resPrec,"DATCHIAMATACM");
		dataPubb				= StringUtils.getAttributeStrNotNull(resPrec,"DATPUBBLICAZIONE");
		codProv 				= StringUtils.getAttributeStrNotNull(resPrec,"CODPROVINCIA");
		prov 					= StringUtils.getAttributeStrNotNull(resPrec,"STRDENOMINAZIONE");
		cpi						= StringUtils.getAttributeStrNotNull(resPrec,"STRCPI");
		indirizzo				= StringUtils.getAttributeStrNotNull(resPrec,"STRINDIRIZZO");
		tel						= StringUtils.getAttributeStrNotNull(resPrec,"STRTEL");
		fax						= StringUtils.getAttributeStrNotNull(resPrec,"STRFAX");
		email					= StringUtils.getAttributeStrNotNull(resPrec,"STREMAIL");
		CODMONOTIPOGRAD     	= StringUtils.getAttributeStrNotNull(resPrec,"CODMONOTIPOGRAD");
		//Nota Pubblica
		SourceBean nota	= (SourceBean)serviceResponse.getAttribute("M_PUB_NOTA_FISSA.ROWS.ROW");
		notaFissa		= StringUtils.getAttributeStrNotNull(nota, "STRNOTAAVVPUBBLICO");
		//corpo della griglia
		numAnno = (BigDecimal)resPrec.getAttribute("NUMANNO");
		//variabile stringa introdotta per evitare errori nel caso in cui la variabile BigDecimal sia nulla
		//anno di riferimento
		String strNumAnno = "";
		if (numAnno == null) {
			strNumAnno = "";
		} else {
			strNumAnno = numAnno.toString();
		}
		//numero della richiesta
		numRichiesta	= (BigDecimal)resPrec.getAttribute("NUMRICHIESTA");
		String strNumRichiesta = "";
		if (numRichiesta == null) {
			strNumRichiesta = "";
		} else {
			strNumRichiesta = numRichiesta.toString();
		}
		//numero della richiesta originale
		String strNumRichiestaOrig = "";
		numRichiestaOrig = (BigDecimal)resPrec.getAttribute("NUMRICHIESTAORIG");
		if (numRichiestaOrig != null) {
			strNumRichiestaOrig = numRichiestaOrig.toString();	
		}
		else {
			strNumRichiestaOrig = strNumRichiesta;
		}
		
		//numero profili
		numProfili = (BigDecimal)resPrec.getAttribute("NUMPROFRICHIESTI"); 
		//numProfili		= (BigDecimal)resPrec.getAttribute("NUMPOSTICM"); 
		String strNumProfili = "";
		if (numProfili == null) {
			strNumProfili = "";
		} else {
			strNumProfili = numProfili.toString();
		}
		//numero exLsu
		exLsu			= (BigDecimal)resPrec.getAttribute("NUMPOSTOLSU");
		String strExLSU = "";
		if (numProfili == null || exLsu == null) {
			strExLSU = "";
		} else if(numProfili != null && numProfili.intValue()>0){
			strExLSU = "(" + exLsu.toString() + " ex LSU)";
		}
		//numero exMilitari
		exMilitari		= (BigDecimal)resPrec.getAttribute("NUMPOSTOMILITARE");
		String strExMilitari = "";		
		if (numProfili == null || exMilitari == null) {
			strExMilitari = "";
		} else if(numProfili != null && numProfili.intValue()>0){
			strExMilitari = "(" + exMilitari.toString() + " ex militari)";
		}
		
		//numero Mobilità
		mobilita = (BigDecimal)resPrec.getAttribute("NUMPOSTOMB");
		String strMobilita = "";		
		if (numProfili == null || mobilita == null) {
			strMobilita = "";
		} else if(numProfili != null && numProfili.intValue()>0){
			strMobilita = "(" + mobilita.toString() + " mobilità)";
		}
	
		
		ente			= StringUtils.getAttributeStrNotNull(resPrec, "STRDATIAZIENDAPUBB");		
		luogoDiLavoro	= StringUtils.getAttributeStrNotNull(resPrec, "STRLUOGOLAVORO");			
		mansione		= StringUtils.getAttributeStrNotNull(resPrec, "STRMANSIONEPUBB");
		requisiti		= StringUtils.getAttributeStrNotNull(resPrec, "TXTCARATTERISTFIGPROF");
		contratto		= StringUtils.getAttributeStrNotNull(resPrec, "TXTCONDCONTRATTUALE");
		orario			= StringUtils.getAttributeStrNotNull(resPrec, "STRNOTEORARIOPUBB");
		riusoGrad		= StringUtils.getAttributeStrNotNull(resPrec, "FLGRIUSOGRADUATORIA");
		notaSpec		= StringUtils.getAttributeStrNotNull(resPrec, "STRNOTAAVVISOPUBB");
		notaFissaCpi	= StringUtils.getAttributeStrNotNull(resPrec, "STRNOTE");
							  
			
		//il codice che segue è organizzato in modo tale da ottenere una formattazione della stampa delle richieste
		//ordinata e raggrupate per tre parametri: il CPI, la data chiamata e la data pubblicazione.
		//richieste con stesso Cpi saranno raggruppate con un unica intestazione, stesso discorso vale per gli altri paremetri
		//le variabili stringa definite rappresentano lo stream HTML per la rappresentazione delle tabelle.

        if(CODMONOTIPOGRAD.equals("D"))
		 	tipoGrad = "Avviamento numerico art.8";
		else if(CODMONOTIPOGRAD.equals("A"))
		    tipoGrad = "Avviamento numerico art.18";
		else if(CODMONOTIPOGRAD.equals("G"))
		    tipoGrad = "Graduatoria art.1";
		
		//variabile che rappresenta il contenuto della richiesta
		riga="	<tr><td class=\"cross\">&nbsp;" + strNumRichiestaOrig + "/" +strNumAnno + "&nbsp;</td> " +
			 " 	<td class=\"cross\">&nbsp;" +ente+ "&nbsp;</td> " +
			 "	<td class=\"cross\">&nbsp;" +luogoDiLavoro+ "&nbsp;</td> " +
			 "	<td class=\"cross\">&nbsp;" +strNumProfili+ "&nbsp;<br>" +tipoGrad+ "</td> " +
			 "	<td class=\"cross\">&nbsp;" +mansione+ "&nbsp;</td> " +
			 "	<td class=\"cross\">&nbsp;" +requisiti+ "&nbsp;</td> " +
			 "	<td class=\"cross\">&nbsp;" +contratto+ "<br>&nbsp;" +orario+ "&nbsp;</td> " +	
			 //"	<td class=\"cross2\">&nbsp;" +tipoGrad+ "&nbsp;</td> " +				 	
			 // modifica 23/01/2007 savino: tolto il commento al controllo di riusoGrad. Se non presente la nota non va stampata
			 "	</tr> ";
			 if (riusoGrad.equals("S")){
			 	riga+=
				 "	<tr> " +
				 "	<td colspan=\"8\" align=\"justify\" class=\"nota\"> " + notaFissa +						
				 "	</td> " +		
				 "	</tr> ";
			 }
			 if (!notaSpec.equals("")){
				 riga+=  
				 "	<tr> " +
				 "	<td colspan=\"8\" align=\"justify\" class=\"nota\"> " + notaSpec +					
				 "	</td> "	+	
				 "	</tr>" ;
			 }
		
			 	
		//controllo uguaglianza fra il numRichiesta del protocollo e il numeroRich della chiamata
		//se il risultato e true stampo il protocollo
		// modifica savino 23/01/2007: 	bisogna controllare l'esistenza di un solo numero di richiesta
		boolean protocolloBeccato = false;		
		for(int i=0; i<listNumProt.size() && !protocolloBeccato; i++){
			SourceBean sb 	= (SourceBean) listNumProt.get(i);
			//numero della richiesta
			numRichProt = (BigDecimal)sb.getAttribute("NUMRICHIESTA");
			strNumRichProt = "";
			if (numRichProt == null) {
				strNumRichProt = "";
			} else {
				strNumRichProt = numRichProt.toString();
			}
			//numero del protocollo
			stampProt = (BigDecimal)sb.getAttribute("NUMPROTOCOLLO");			
			strStampProt = "";
			if (stampProt == null) {
				strStampProt = "";
			} else {
				strStampProt = stampProt.toString();
			}			
			//se true stampo il protocollo
			if(strNumRichProt.equals(strNumRichiesta)){
				protocollo = eticProt + strStampProt;
				protocolloBeccato = true;
			} else {
				protocollo = "";
			}
		}				
		
	
		// controllo sul Cpi
//		if (!codCpi.equals("") && codCpiPrec.equals(codCpi)){
//			datChiam = StringUtils.getAttributeStrNotNull(resPrec,"DATCHIAMATACM");
			
			// controllo sulla data chiamata
			if (!datChiam.equals("") && datChiamPrec.equals(datChiam)){
				dataPubb = StringUtils.getAttributeStrNotNull(resPrec,"DATPUBBLICAZIONE");
				//controllo sulla data di pubblicazione
				
				if (!dataPubb.equals("") && dataPubbPrec.equals(dataPubb) && !codCpi.equals("") && codCpiPrec.equals(codCpi)){

					tr += riga;

				} else {
					
						dataPubbPrec = dataPubb;
						
						if(tabPubb.equals("")){					
						
						} else {
							// chiudo il primo record e aggiungo l'intestazione e il record successivo alla tabella pubblicazioni
							tabPubb += tr + " </table></tr><tr> ";
							
							// 1
							if (!codCpiPrec.equals(codCpi)){
								tabPubb += tabNota;
							}
							
							tabPubb +=" <table width=\"100%\" class=\"fontpage\"> " +
									  "	<tr> " +
									  "	<td colspan=\"2\" align=\"left\" class=\"nota\">PUBBLICAZIONE DEL " + dataPubb + protocollo +
									  "	</td> " +
									  "	</tr> " +
									  "	</table> " +
									  "	<table class=\"griglia\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr> " +
									  "	<td class=\"head\" >Cod.</td> " +
									  "	<td class=\"head\" >Ente</td> " +
									  "	<td class=\"head\">Luogo di lavoro</td> " +
									  "	<td class=\"head\" width=\"12%\">Num. posti</td> " +
									  "	<td class=\"head\">Profilo professionale<br>e qualifica</td> " +
									  "	<td class=\"head\">Requisiti richiesti</td> " +
								      " <td class=\"head\">Tipologia rapporto<br>di lavoro</td> ";
							
							tr = riga;

							tabNota	=  " <table width=\"100%\" class=\"fontpage\"> " +
					  				   " <tr><td colspan=\"2\" align=\"left\"> " + notaFissaCpi +
					  				   " </td></tr></table>";
														
						}
				}	// chiusura blocco data pubblicazione

			} else { //else sulla data chiamata
			    datChiamPrec = datChiam;
			    dataPubbPrec = dataPubb;
				
				if (!codCpi.equals("") && codCpiPrec.equals(codCpi)){
					
					if(tabChiam.equals("")){
						    
					} else {
						
						// chiudo la tebella con i record, chiudo la tabella pubblicazione e aggiungo la tabella data chiamata
						tabChiam += tabPubb + tr + " </table></tr></table></tr>"; //new
						
						// 2
						tabChiam += tabNota;
						
						tabChiam +=" <tr><table width=\"100%\" class=\"fontpage\"> " +
								   " <tr> " +					
								   " <td class=\"titolo\" align=\"center\" colspan=\"2\"><br> " +
								   " CHIAMATA SUI PRESENTI (ASTA) DEL " + datChiam +				
								   " </td> " +	 				
								   " <tr> " +
								   " </table>";
								   
						tabPubb =  " <table width=\"100%\" class=\"fontpage\"> " +
								   " <tr> " +								
								   " <td colspan=\"2\" align=\"left\" class=\"nota\">PUBBLICAZIONE DEL " + dataPubb  + protocollo +
								   " </td> " +											
								   " </tr> " +
								   " </table> " + 
								   " <tr><table class=\"griglia\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr> " +
								   " <td class=\"head\" >Cod.</td> " +
								   " <td class=\"head\" >Ente</td> " +
								   " <td class=\"head\">Luogo di lavoro</td> " +
								   " <td class=\"head\" width=\"12%\">Num. posti</td> " +
								   " <td class=\"head\">Profilo professionale<br>e qualifica</td> " +
								   " <td class=\"head\">Requisiti richiesti</td> " +
							       " <td class=\"head\">Tipologia rapporto<br>di lavoro</td>";
						tr = riga;

						tabNota	=  " <table width=\"100%\" class=\"fontpage\"> " +
					  			   " <tr><td colspan=\"2\" align=\"left\"> " + notaFissaCpi +
					  			   " </td></tr></table>";
					}
				
				}else{

					notaCpiPrec 	= notaFissaCpi;
					codCpiPrec 		= codCpi;
					datChiamPrec	= datChiam;
					dataPubbPrec	= dataPubb;
		
					if(tabCpi.equals("")){
						
						// se non ho ancora un valore per tabCpi creo nuova tabella per cpi, data chiamata e pubblicazione				
						tabCpi	= "&nbsp;";

						tabChiam = " <table width=\"100%\" class=\"fontpage\"> " +
								   " <tr> " +					
								   " <td class=\"titolo\" align=\"center\" colspan=\"2\"><br> " +
								   " CHIAMATA SUI PRESENTI (ASTA) DEL " + datChiam +				
								   " </td> " +	 				
								   " <tr> " +
								   " </table>";
					 	tabPubb	   = " <table width=\"100%\" class=\"fontpage\"> " +
									 " <tr> " +								
									 " <td colspan=\"2\" align=\"left\" class=\"nota\">PUBBLICAZIONE DEL " + dataPubb  + protocollo +
									 " </td> " +											
									 " </tr> " +
									 " </table> " + 
									 " <tr><table class=\"griglia\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr> " +
									 " <td class=\"head\" >Cod.</td> " +
									 " <td class=\"head\" >Ente</td> " +
									 " <td class=\"head\">Luogo di lavoro</td> " +
									 " <td class=\"head\" width=\"12%\">Num. posti</td> " +
									 " <td class=\"head\">Profilo professionale<br>e qualifica</td> " +
									 " <td class=\"head\">Requisiti richiesti</td> " +
								     " <td class=\"head\">Tipologia rapporto<br>di lavoro</td>";
						
						tr = riga;

						tabNota	=  " <table width=\"100%\" class=\"fontpage\"> " +
					  			   " <tr><td colspan=\"2\" align=\"left\"> " + notaFissaCpi +
					  			   " </td></tr></table>";
						
					} else {
						// aggiungo tutte le tabelle a tabCpi
						tabCpi	+=  tabChiam + tabPubb + tr + " </table></tr></table> ";
						
						// 4
						tabCpi += tabNota;

						tabChiam =	  "  </table></tr><tr><table width=\"100%\" class=\"fontpage\"> " +
									  " <tr> " +					
									  " <td class=\"titolo\" align=\"center\" colspan=\"2\"><br> " +
									  " CHIAMATA SUI PRESENTI (ASTA) DEL " + datChiam +				
									  " </td> " +	 				
									  " <tr> " +
									  " </table>";
									   
				         tabPubb =	  " <table width=\"100%\" class=\"fontpage\"> " +
									  "	<tr> " +								
									  "	<td colspan=\"2\" align=\"left\" class=\"nota\">PUBBLICAZIONE DEL " + dataPubb + protocollo +
									  "	</td> " +											
									  "	</tr> " +
									  "	</table> " + 
									  "	<tr><table class=\"griglia\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr> " +
									  "	<td class=\"head\" >Cod.</td> " +
									  "	<td class=\"head\" >Ente</td> " +
									  "	<td class=\"head\">Luogo di lavoro</td> " +
									  "	<td class=\"head\" width=\"12%\">Num. posti</td> " +
									  "	<td class=\"head\">Profilo professionale<br>e qualifica</td> " +
									  "	<td class=\"head\">Requisiti richiesti</td> " +
								      " <td class=\"head\">Tipologia rapporto<br>di lavoro</td> ";
						 tr = riga;

						 tabNota = 	 " <table width=\"100%\" class=\"fontpage\"> " +
					  				 " <tr><td colspan=\"2\" align=\"left\"> " + notaFissaCpi + 
					  				 " </td></tr></table>";					   	
						 
					}		
				}
			}	// chisura blocco data chiamata
	}	// chiusura del ciclo for	
		
	// chiudo tutte le tabelle e raccolgo la stringa finale in tabTot che è una stringa che rappresenta 
	// tutto il codice html necessario per l'organizzazione  e la stampa delle tabelle 
	
	if (!tabCpi.equals("")){
		tabTot = tabCpi + tabChiam + tabPubb + tr +  "</table></tr></table></tr></table>" + tabNota ;
	}
%>
<%=tabTot%>
<%@ include file="/jsp/MIT.inc" %>
<br/>
<br/>
<br/>

</body>
</html>
