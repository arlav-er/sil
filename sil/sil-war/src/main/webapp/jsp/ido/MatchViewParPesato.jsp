<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  java.util.*, 
                  java.math.*,
                  java.io.*,
                  it.eng.afExt.utils.StringUtils,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*, it.eng.sil.security.PageAttribs" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>      

<!-- --- NOTE: Gestione Patto
-->

<%
            String numfasciaetaesatta= "";
            String numfasciaetaprima= ""; 
            String numfasciaetasec= ""; 
            String numpstudio= ""; 
            String numpstudiogruppo= ""; 
            String numpstudioalias=   ""; 
            String numpmansione = ""; 
            String numpmansionegruppo= ""; 
            String numpmansionealias= ""; 
            String numpesperienza= ""; 
            String numpnoesperienza = ""; 
            String numpesperienzaalias =""; 
            String numpinfo =""; 
            String numpinfomin= ""; 
            String numpinfogruppo = ""; 
            String numinfogruppomin= ""; 
            String numplingua= "";
            String numplinguainf= "";
            String numdecsogliarichiesta= "";
			String dataValiditaCV = "";
			String prgAlternativa = "";
			String flgIncrocioMirato = "";
			String codMonoCMCategoria = "";			
			String strCategoriaCM = "";
			String rosaFiltro = "";
    Vector rows = serviceResponse.getAttributeAsVector("M_GET_PAR_INCROCIO.ROWS.ROW");
    SourceBean row = null;
       	for (int i = 0; i < rows.size(); i++) {
          row=(SourceBean) rows.elementAt(i); 
					prgAlternativa = it.eng.sil.util.Utils.notNull(row.getAttribute("PRGALTERNATIVA"));
          		  dataValiditaCV = it.eng.sil.util.Utils.notNull(row.getAttribute("DATSTATOLAV"));
                  numfasciaetaesatta = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPFASCIAETAESATTA"));
                    if (numfasciaetaesatta!= null && !numfasciaetaesatta.equals("")) {
                    numfasciaetaesatta = numfasciaetaesatta + " %";
                    } else {
                  numfasciaetaesatta = "Non Usato";
                  }

                  numfasciaetaprima = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPFASCIAETAPRIMA"));
                    if (numfasciaetaprima!= null && !numfasciaetaprima.equals("")) {
                    numfasciaetaprima = numfasciaetaprima + " %";                  
                    } else {
                  numfasciaetaprima = "Non Usato";
                  }

                  
                  numfasciaetasec = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPFASCIAETASEC"));
                    if (numfasciaetasec!= null && !numfasciaetasec.equals("")) {
                    numfasciaetasec = numfasciaetasec + " %";                  
                    } else {
                  numfasciaetasec = "Non Usato";
                  }

                            
                  numpstudio = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPSTUDIO"));
                    if (numpstudio!= null && !numpstudio.equals("")) {
                    numpstudio = numpstudio + " %";                  
                    } else {
                  numpstudio = "Non Usato";
                  }

                  numpstudiogruppo = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPSTUDIOGRUPPO"));
                  if (numpstudiogruppo!= null && !numpstudiogruppo.equals("")) {
                    numpstudiogruppo = numpstudiogruppo + " %";                  
                    } else {
                    numpstudiogruppo = "Non Usato";
                  }
                  
                  numpstudioalias = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPSTUDIOALIAS"));
                    if (numpstudioalias!= null && !numpstudioalias.equals("")) {
                    numpstudioalias = numpstudioalias + " %";                  
                    } else {
                  numpstudioalias = "Non Usato";
                  }


                  numpmansione = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPMANSIONE"));
                  if (numpmansione!= null && !numpmansione.equals("")) {
                    numpmansione = numpmansione + " %";                  
                    } else {
                    numpmansione = "Non Usato";
                  }


                  numpmansionegruppo = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPMANSIONEGRUPPO"));
                  if (numpmansionegruppo!= null && !numpmansionegruppo.equals("")) {
                    numpmansionegruppo = numpmansionegruppo + " %";                  
                    } else {
                    numpmansionegruppo = "Non Usato";
                  }


                  numpmansionealias = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPMANSIONEALIAS"));
                  if (numpmansionealias!= null && !numpmansionealias.equals("")) {
                    numpmansionealias = numpmansionealias + " %";                  
                    } else {
                    numpmansionealias = "Non Usato";
                  }




                  numpesperienza = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPESPERIENZA"));
                  if (numpesperienza!= null && !numpesperienza.equals("")) {
                    numpesperienza = numpesperienza + " %";                  
                    } else {
                    numpesperienza = "Non Usato";
                  }


                  
                  numpnoesperienza = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPNOESPERIENZA"));
                  if (numpnoesperienza!= null && !numpnoesperienza.equals("")) {
                    numpnoesperienza = numpnoesperienza + " %";                  
                    } else {
                    numpnoesperienza = "Non Usato";
                  }


                  
                  numpesperienzaalias = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPESPERIENZAALIAS")); 
                  if (numpesperienzaalias!= null && !numpesperienzaalias.equals("")) {
                    numpesperienzaalias = numpesperienzaalias + " %";                  
                    } else {
                    numpesperienzaalias = "Non Usato";
                  }

                  
                  numpinfo = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPINFO"));
                  if (numpinfo!= null && !numpinfo.equals("")) {
                    numpinfo = numpinfo + " %";                  
                    } else {
                    numpinfo = "Non Usato";
                  }


                  
                  numpinfomin = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPINFOMIN"));
                  if (numpinfomin!= null && !numpinfomin.equals("")) {
                    numpinfomin = numpinfomin + " %";                  
                    } else {
                    numpinfomin = "Non Usato";
                  }


                  
                  numpinfogruppo = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPINFOGRUPPO"));
                  if (numpinfogruppo!= null && !numpinfogruppo.equals("")) {
                    numpinfogruppo = numpinfogruppo + " %";                  
                    } else {
                    numpinfogruppo = "Non Usato";
                  }


                  
                  numinfogruppomin = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPINFOGRUPPOMIN")); 
                  if (numinfogruppomin!= null && !numinfogruppomin.equals("")) {
                    numinfogruppomin = numinfogruppomin + " %";                  
                    } else {
                    numinfogruppomin = "Non Usato";
                  }

                  
                  numplingua = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPLINGUA"));
                  if (numplingua!= null && !numplingua.equals("")) {
                    numplingua = numplingua + " %";                  
                    } else {
                    numplingua = "Non Usato";
                  }
                  
                  numplinguainf = it.eng.sil.util.Utils.notNull(row.getAttribute("NUMPLINGUAINF")); 
                  if (numplinguainf!= null && !numplinguainf.equals("")) {
                    numplinguainf = numplinguainf + " %";                  
                    } else {
                    numplinguainf = "Non Usato";
                  }

                  numdecsogliarichiesta = it.eng.sil.util.Utils.notNull(row.getAttribute("DECSOGLIARICHIESTA")); 
                  if (numdecsogliarichiesta!= null && !numdecsogliarichiesta.equals("")) {
                    numdecsogliarichiesta = numdecsogliarichiesta + " %";                  
                    } else {
                    numdecsogliarichiesta = "Non Usato";
                  }
                  
                  flgIncrocioMirato = StringUtils.getAttributeStrNotNull(row, "flgIncrocioMirato").equals("S")?"Si":"No";
                  
                  codMonoCMCategoria = StringUtils.getAttributeStrNotNull(row, "codMonoCMCategoria");
                  if (("D").equalsIgnoreCase(codMonoCMCategoria)) {
				     strCategoriaCM = "Disabili";
                  } 
                  else if (("A").equalsIgnoreCase(codMonoCMCategoria)) {
                  	 strCategoriaCM = "Categoria protetta ex. Art. 18";
                  }
                  else if (("E").equalsIgnoreCase(codMonoCMCategoria)) {
	  	          	 strCategoriaCM = "Entrambi";
	  			  }
                  else {
				 	 strCategoriaCM = "Non Usato";
                  }
                  
                  rosaFiltro = StringUtils.getAttributeStrNotNull(row, "FLGFILTRICMAATTIVATI").equals("S")?"Si":"No";
                  
      }

    

%>



<html>
<head>
<title>Parametri utilizzati per l'incrocio</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<script>
    function chiudi() {
    window.close();
  }
</script> 
</head>

<body class="gestione">
<br><br>
<p class="titolo"><b>Parametri utilizzati per l'incrocio</b></p>
    <table width="96%" border="0px" cellspacing="0" cellpadding="0" align="center">
      <!-- top -->
      <tr>
        <td align="left" valign="top" width="6" height="19" class="cal_header"><img src="../../img/angoli/bia1.gif" height="10" width="6"></td>
        <td class="cal_header" align="center" width="25%" valign="middle" >Parametro</td>
        <td class="cal_header" align="center" width="50%" valign="middle" >Pesi</td>
        <td class="cal_header" align="center" width="25%" valign="middle" >Valore</td>        
        <td class="cal_header" align="right" valign="top" height="19" width="6"><img src="../../img/angoli/bia2.gif" width="6" height="10"></td>
      </tr>
      <!-- end top -->
   

  <!--tr>
            <td class="cal"  valign="middle" width="6"></td>
            <td rowspan ="3" class="ido_bordato">Età</td>
            <td class="ido_bordato">Fascia d'età esatta </td>
            <td class="ido_bordato"> <%= numfasciaetaesatta %> </td>
            <td class="cal"  valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Fascia d'età ± 5 anni <%= numfasciaetaprima %></td>
            <td class="ido_bordato" align="center"><%= numfasciaetaprima %></td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Fascia d'età ± 10 anni <%= numfasciaetasec %> </td>
            <td class="ido_bordato" align="center"><%= numfasciaetasec %> </td>           
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr-->

  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td rowspan ="2" class="ido_bordato">Conoscenza Linguistiche</td>
            <td class="ido_bordato">Corrispondenza Esatta e livello maggiore o uguale</td>
            <td class="ido_bordato"><%= numplingua %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Corrispondenza esatta ma livello inferiore</td>
            <td class="ido_bordato" align="center"> <%= numplinguainf %>  </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>


    <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td rowspan ="3" class="ido_bordato">Mansioni</td>
            <td class="ido_bordato">Corrispondenza Esatta</td>
            <td class="ido_bordato"><%= numpmansione %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Nello stesso gruppo</td>
            <td class="ido_bordato" align="center"><%= numpmansionegruppo %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Appartenenti a gruppi simili</td>
            <td class="ido_bordato" align="center"><%= numpmansionealias %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>

  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td rowspan ="3" class="ido_bordato">Esperienza nella mansione</td>
            <td class="ido_bordato">Esperienza nella mansione cercata</td>
            <td class="ido_bordato"> <%= numpesperienza %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Nessuna esperienza nella mansione cercata </td>
            <td class="ido_bordato" align="center"> <%= numpnoesperienza %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Esperienza in una mansione simile  </td>
            <td class="ido_bordato" align="center"><%= numpesperienzaalias %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>

 <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td rowspan ="3" class="ido_bordato">Titoli di Studio</td>
            <td class="ido_bordato">Corrispondenza Esatta  </td>
            <td class="ido_bordato"><%= numpstudio %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Nello stesso gruppo </td>
            <td class="ido_bordato" align="center"> <%= numpstudiogruppo %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Appartenenti a gruppi simili </td>
           <td class="ido_bordato" align="center"> <%= numpstudioalias %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  

  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td rowspan ="4" class="ido_bordato">Conoscenze Informatiche</td>
            <td class="ido_bordato">Corrispondenza Esatta e livello maggiore o uguale</td>
            <td class="ido_bordato"> <%= numpinfo %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Corrispondenza esatta ma livello inferiore</td>
            <td class="ido_bordato" align="center"> <%= numpinfomin %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Nello stesso gruppo e livello maggiore o uguale </td>
            <td class="ido_bordato" align="center"> <%= numpinfogruppo %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato" align="center">Nello stesso gruppo ma con livello inferiore </td>
            <td class="ido_bordato" align="center"> <%= numinfogruppomin %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>

    <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td rowspan ="1" class="ido_bordato">Soglia di Vicinanza</td>
            <td class="ido_bordato">&nbsp;</td>
            <td class="ido_bordato"><%= numdecsogliarichiesta %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td rowspan ="1" class="ido_bordato">&nbsp;</td>
            <td class="ido_bordato">&nbsp;</td>
            <td class="ido_bordato">&nbsp;</td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>
  <tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td rowspan ="1" class="ido_bordato">Profilo</td>
            <td class="ido_bordato">&nbsp;</td>
            <td class="ido_bordato"><%= prgAlternativa %> </td>
            <td class="cal" align="center" valign="middle" width="6"></td>
  </tr>  
  
  	<%
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {
	// END-PARTE-TEMP
	%>
	<tr>
		<td class="cal" align="center" valign="middle" width="6"></td>
    	<td rowspan ="1" class="ido_bordato">Data validità curriculum</td>
        <td class="ido_bordato">&nbsp;</td>
        <td class="ido_bordato"><%= dataValiditaCV %> </td>
        <td class="cal" align="center" valign="middle" width="6"></td>
    
	<%
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
	%>
	<%
		if ("No".equalsIgnoreCase(flgIncrocioMirato)) {
	%>
			<tr>
				<td class="cal" align="center" valign="middle" width="6"></td>
		    	<td rowspan ="1" class="ido_bordato">Data validità curriculum</td>
		        <td class="ido_bordato">&nbsp;</td>
		        <td class="ido_bordato"><%= dataValiditaCV %> </td>
		        <td class="cal" align="center" valign="middle" width="6"></td>		
	        </tr>	
	<%
		}
	%>
		<tr>
			<td class="cal" align="center" valign="middle" width="6"></td>
	    	<td rowspan ="1" class="ido_bordato">Uso incrocio mirato</td>
	        <td class="ido_bordato">&nbsp;</td>
	        <td class="ido_bordato"><%=flgIncrocioMirato%> </td>
	        <td class="cal" align="center" valign="middle" width="6"></td>		
        </tr>	
        <tr>
			<td class="cal" align="center" valign="middle" width="6"></td>
	    	<td rowspan ="1" class="ido_bordato">Categoria CM</td>
	        <td class="ido_bordato">&nbsp;</td>
	        <td class="ido_bordato"><%=strCategoriaCM%> </td>
	        <td class="cal" align="center" valign="middle" width="6"></td>		
        </tr>	
        <tr>
			<td class="cal" align="center" valign="middle" width="6"></td>
	    	<td rowspan ="1" class="ido_bordato">Utilizzo filtri CM</td>
	        <td class="ido_bordato">&nbsp;</td>
	        <td class="ido_bordato"><%=rosaFiltro%> </td>
	        <td class="cal" align="center" valign="middle" width="6"></td>		
        </tr>	
	<%
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
	%>          
      <!-- bottom -->
      <tr class="cal">
        <td class="cal_header" valign="bottom" align="left" height="6" width="6"><img src="../../img/angoli/bia4.gif" height="10" width="6"></td>
        <td class="cal_header" align="center" valign="middle">&nbsp;</td>
        <td class="cal_header" align="center" valign="middle">&nbsp;</td>
        <td class="cal_header" align="center" valign="middle">&nbsp;</td>

        <td class="cal_header" align="right" valign="bottom"><img src="../../img/angoli/bia3.gif" height="10" width="6" ></td>
      </tr>
      <!-- end bottom -->
      <tr>
        <td>&nbsp;&nbsp;</td>
      </tr>
      
      <tr>
          <td colspan="4" align="center">
                  <input class="pulsante" type="button" name="Chiudi"  value="Chiudi" onclick="javascript:chiudi();">
            </td>
        </tr>
    </table>
<br/>
<table width="100%" class="main">
  <tr>
  </tr>
</table>
</body>
</html>

