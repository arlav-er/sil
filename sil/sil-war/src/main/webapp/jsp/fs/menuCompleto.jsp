<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  com.engiweb.framework.tags.Util,
                  it.eng.sil.security.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.afExt.utils.StringUtils" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%!

  private void scriviVoceMenu(String voce, int livello, String pagina, boolean siChiude, String funzMenu, JspWriter out) throws IOException{

		String strFunzMenu = "\"\"";
		
        out.print(" objNodes[" + livello + "] = objNodes[" + (livello-1) + "]");

		String siChiudeStr= new Boolean(siChiude).toString();
		String url="\"\"";
		
		if ((pagina!=null) && (!pagina.equalsIgnoreCase("null"))){
			url = "\"AdapterHTTP?PAGE=" + pagina + "\"";
			strFunzMenu = "\"" +  funzMenu+ "\"";
		}

        out.println(".addChild(\"" +  livello + "\", \"" + voce +  "\"," + url +  
        	", \"main\", " + siChiudeStr 
          	+  "," + strFunzMenu + " );");
  }


  private String getQuotedAttrib(SourceBean menuBean, String attribute) {
	String input = StringUtils.notNull( (String) menuBean.getAttribute( attribute ) );
	if (StringUtils.isFilled(input)) {
		input = com.engiweb.framework.tags.Util.quote(input); 
		input = com.engiweb.framework.tags.Util.replace(input, "&apos;", "&acute;");                    
	}
	return input;
  }

%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
	<title>Menu-Home</title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <af:linkScript path="../../js/"/>
  <script type="text/javascript" src="../../js/document.js"></script>
  <script type="text/javascript" src="../../js/tree.js"></script>

  <%
    
   
      String reqCdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
	      
      SourceBean menuBean = (SourceBean) serviceResponse.getAttribute("MENUCOMPLETO");

      String cdnLav = (String) menuBean.getAttribute("CDNLAVORATORE");
      String prgAzienda = (String) menuBean.getAttribute("PRGAZIENDA");
      String prgUnita = (String) menuBean.getAttribute("PRGUNITA");
      

  %>


<script type="text/javascript">
  
  var cdnLav = <%=cdnLav==null? "-1" :  cdnLav%>;
  var prgAzienda = <%=prgAzienda==null? "-1" :  prgAzienda%>;
  var prgUnita = <%=prgUnita==null? "-1" :  prgUnita%>;
  var parCdnFunzione = <%=reqCdnFunzione==null? "-1" :  reqCdnFunzione%>;
  
  function caricaMenuLav(pCdnFunz, pCdnLav){
  
    if (pCdnLav == cdnLav) {
      if (parCdnFunzione!=pCdnFunz)	{
	      mostraPercorso(objTree.getText2(pCdnFunz+ "-L" ));
	      parCdnFunzione=pCdnFunz;
      }
      return;
    }
    else{
     
        var url = "AdapterHTTP?PAGE=MenuCompletoPage"
        url += "&CDNLAVORATORE=" + pCdnLav;
        url += "&cdnFunzione=" + pCdnFunz;
        window.top.menu.location = url;
    }
  }
  
  
   function caricaMenuAzienda(pCdnFunz, pPrgAzienda, pPrgUnita){
  
    if ((pPrgAzienda == prgAzienda) && (pPrgUnita == prgUnita)){
      if (parCdnFunzione!=pCdnFunz)	{
	      mostraPercorso(objTree.getText2(pCdnFunz+ "-L" ));
	      parCdnFunzione=pCdnFunz;
      }
    
      return;
    }
    else{
     
        var url = "AdapterHTTP?PAGE=MenuCompletoPage"
        url += "&PRGAZIENDA=" + pPrgAzienda;
        url += "&PRGUNITA=" + pPrgUnita;
        url += "&cdnFunzione=" + pCdnFunz;
        window.top.menu.location = url;
    }
  }
  


  function chiudiMenuContest(){
    if (confirm("Sei sicuro di voler chiudere il menu contestuale?")){
        
        objTree.collassaMenu("root_1");
        document.getElementById("tab_root_1" ).style.display = "none";
        objTree.espandiMenu("root_0");
        
        var url = "AdapterHTTP?PAGE=mainBlankPage"
        window.top.main.location = url;

        cdnLav = -1;
        prgAzienda = -1;
        prgUnita = -1;
        window.top.alto.document.getElementById('id_percorso_nav').innerHTML="";
       
    }
  }

  function chiudiMenuContest_noaction() {
        
        var nodo = document.getElementById("tab_root_1" );
        if (nodo !=null ) {
            objTree.collassaMenu("root_1");
        	nodo.style.display = "none";
        }
        objTree.espandiMenu("root_0");

        cdnLav = -1;
        prgAzienda = -1;
        prgUnita = -1;
  }



</script>



<script type="text/javascript">
    
        var objTree = new jsTree;
        var objNodes = new Array();
        
        objTree.createRoot("0", "", "", "");
        objNodes[0]=objTree.root;



	
       <%
        // LAto server 
        
        boolean reloadTopPage = menuBean.containsAttribute("RELOAD_TOP_PAGE");
        /// 1. Menu generale .....  
        List righe = menuBean.getAttributeAsVector("MENU_GEN.ROWS.ROW");
        for (int i=0; i< righe.size(); i++){
            SourceBean row = (SourceBean) righe.get(i);
            String voce = (String) row.getAttribute("STRVOCEMENU");
            String pagina = (String) row.getAttribute("STRPAGE");
            BigDecimal cdnFunzione = (BigDecimal) row.getAttribute("CDNFUNZIONE");
    		String funzMenu = null;        

      
            int livello = ((BigDecimal) row.getAttribute("level")).intValue();
            if (cdnFunzione!=null){
                pagina += "&CDNFUNZIONE=" + cdnFunzione.intValue();
	     		funzMenu=cdnFunzione.toString() + "-" + "G";     
   			}
            scriviVoceMenu( voce, livello,  pagina, false,  funzMenu,  out);
            
        } // for

        /// 2. Menu del lavoratore.... 
        List righeLav = menuBean.getAttributeAsVector("MENU_LAVORATORE.ROWS.ROW");
        if (righeLav!=null)
              for (int i=0; i< righeLav.size(); i++){
                  SourceBean row = (SourceBean) righeLav.get(i);
                  String voce = (String) row.getAttribute("STRVOCEMENU");
                  String pagina = (String) row.getAttribute("STRPAGE");
                  BigDecimal cdnFunzione = (BigDecimal) row.getAttribute("CDNFUNZIONE");
                  boolean closeAction = false;
        		  String funzMenu =  null;         

                  int livello = ((BigDecimal) row.getAttribute("level")).intValue();
                  if (livello == 1 ){
                    String nominativo = getQuotedAttrib(menuBean,"IDENTITA_LAV.ROWS.ROW.NOMINATIVO");
                    voce = nominativo;
                    closeAction = true;
                    
                  }
                  pagina += "&CDNLAVORATORE=" + cdnLav;

                  if (cdnFunzione!=null){
                      pagina += "&CDNFUNZIONE=" + cdnFunzione.intValue();
					  funzMenu=cdnFunzione.toString() + "-L"; 
				  }
   
                  scriviVoceMenu( voce, livello,  pagina, closeAction ,funzMenu, out);
            
              } // for








        /// 3. Menu dell'azienda.... 
        List righeAz = menuBean.getAttributeAsVector("MENU_AZIENDA.ROWS.ROW");
        if (righeAz!=null)
              for (int i=0; i< righeAz.size(); i++){
                  SourceBean row = (SourceBean) righeAz.get(i);
                  String voce = (String) row.getAttribute("STRVOCEMENU");
                  String pagina = (String) row.getAttribute("STRPAGE");
                  BigDecimal cdnFunzione = (BigDecimal) row.getAttribute("CDNFUNZIONE");
                  String funzMenu =  null;        
                  
                  boolean closeAction = false;
                  

                  int livello = ((BigDecimal) row.getAttribute("level")).intValue();
                  if (livello == 1 ){
                    String ragioneSociale = (String) menuBean.getAttribute("IDENTITA_AZ.ROWS.ROW.RAGIONESOCIALE");
                    if (ragioneSociale != null) {
						ragioneSociale = com.engiweb.framework.tags.Util.quote(ragioneSociale);                    
						ragioneSociale = com.engiweb.framework.tags.Util.replace(ragioneSociale, "&apos;", "&acute;");                    
					}
										
                    String indirizzo = getQuotedAttrib(menuBean,"IDENTITA_AZ.ROWS.ROW.INDIRIZZO");
                    
                   // String localita = getQuotedAttrib(menuBean,"IDENTITA_AZ.ROWS.ROW.COMUNE");

                    String comune = getQuotedAttrib(menuBean,"IDENTITA_AZ.ROWS.ROW.COMUNE");
					  
                    String targa = getQuotedAttrib(menuBean,"IDENTITA_AZ.ROWS.ROW.TARGA");
                    if (StringUtils.isFilled(targa)){
                    	targa = " (" + targa + ")";
                    }
					
					voce = ragioneSociale + " " + 
                           indirizzo  + " " +
                    		   comune +  targa ;
                //  	voce = com.engiweb.framework.tags.Util.quote(voce);

//                    voce = ragioneSociale + 
//                       		"<i><small><br>&nbsp;" + indirizzo  +
//                    		"<br>&nbsp;" + comune +  targa + "</small></i>" ;
                //  	voce = com.engiweb.framework.tags.Util.quote(voce);
                    closeAction = true;
                    
                  }
                  pagina += "&PRGAZIENDA=" + prgAzienda;
                  pagina += "&PRGUNITA=" + prgUnita;
				
				  if (cdnFunzione!=null){
                      pagina += "&CDNFUNZIONE=" + cdnFunzione.intValue();
					  funzMenu=cdnFunzione.toString() + "-A"; 
				  }
   
                  scriviVoceMenu( voce, livello,  pagina, closeAction ,funzMenu, out);
            
              } // for







       %>      

        function doLoad() {
            objTree.buildDOM();
            <%  if ((righeLav!=null) && (righeLav.size()>0)) {%>
              objTree.espandiMenu("root_1");
              //Ricerco il nodo in cui il cod funz = ....
			  
			  				<%  if (reqCdnFunzione!=null) {%> 		              
					              mostraPercorso(objTree.getText2("<%=reqCdnFunzione%>" + "-L" ))              
					         <%} //if %>              

			  				<%  if (reloadTopPage) {%> 		              
					              caricaTop();             
					         <%} //if %>              


              
            <%} else if ((righeAz!=null) && (righeAz.size()>0)) {%>
              objTree.espandiMenu("root_1");

			  				<%  if (reqCdnFunzione!=null) {%> 		              
					              mostraPercorso(objTree.getText2("<%=reqCdnFunzione%>" + "-A" ))              
					         <%} //if %>              

			  				<%  if (reloadTopPage) {%> 		              
					              caricaTop();             
					         <%} //if %> 

            <%} else {// if %>
                objTree.espandiMenu("root_0");       
				caricaTop();
            <%} //if %>
            
        }


      function caricaTop(){
       window.top.alto.location="AdapterHTTP?PAGE=topPage" ;
      }



	 function mostraPercorso (idMenu) {
		
			var pezzi = idMenu.split("_");
			var token="root";
			var percorso="<img src='../../img/utente.gif'> ";
			for (var i=1; i<pezzi.length; i++){
				token = token +"_" + pezzi[i]
				if (i!=1)
					percorso = percorso + " >> ";
				percorso = percorso + objTree.getText(token);
			}
			percorso = percorso.replace('<br>',' - ');
			percorso = percorso.replace('<br>&nbsp;',', ');
			window.top.alto.document.getElementById('id_percorso_nav').innerHTML=percorso;

		}



	 function creaLink (idMenu, url) {
		
			mostraPercorso(idMenu);
			window.top.main.location=url;

		}


</script>

</head>

<body class="menu" onLoad="doLoad();">


</body>
</html>
