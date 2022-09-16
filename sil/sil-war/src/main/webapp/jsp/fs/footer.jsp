<%@ page contentType="text/html;charset=utf-8"%>
<%@ page session="false"%>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <script>
    
    function indietro(){
     window.top.main.indietro();
    }

    function avanti(){
     window.top.main.avanti();
    }
    
   function mostraHyperLink(){
     window.top.main.mostraHyperLink();
    }

   function mostraBackLink() {
    window.top.main.mostraBackLink();
   }
  


    
    function rinfresca(){
     var imgAvanti =  document.getElementById("immAvanti");
     var imgIndietro =  document.getElementById("immIndietro");
     var hMainIndietro = window.top.main.indietro;
     var hMainAvanti  = window.top.main.avanti;
     var hMostraHyperLink  = window.top.main.mostraHyperLink;
     var hMostraBackLink = window.top.main.mostraBackLink;
     
     var sezPuls = document.getElementById("sezPulsanti");
   
       if ((hMainIndietro == null) && (hMainAvanti == null) ){
         sezPuls.style.display="none";
       } else{
                     sezPuls.style.display="";
  
                     if (hMainIndietro == null){
                        document.form.btnIndietro.disabled=true;       
                        imgIndietro.src="../../img/indietro_disabled.gif" 
          
                     }else{
                        document.form.btnIndietro.disabled=false;       
                        imgIndietro.src="../../img/indietro.gif" 
                     }


                     if (hMainAvanti == null){
                        document.form.btnAvanti.disabled=true;       
                        imgAvanti.src="../../img/avanti_disabled.gif" 
                     }else{
                        document.form.btnAvanti.disabled=false;       
                        imgAvanti.src="../../img/avanti.gif" 
                     }
      }
      
      <%// Cancella i link precedenti e li aggiorna %>

      document.getElementById('sezLinks').innerHTML='';
      document.getElementById('sezBack').innerHTML='';
      if (hMostraHyperLink != null){
        mostraHyperLink();
      }
      if (hMostraBackLink != null){
        mostraBackLink();
      }
    }
    
  </script>
</head>

<body class="nav">
<form name="form">
  <table width="100%" height="100%" align="right">
  <tr>
    <td align="left"><div id="sezBack">
      </div>
    </td>
    <td align="left"><div id="sezLinks">
      </div>
    </td>
    <td align="right" style="padding: 0; margin:0;" width="190px"><div id="sezPulsanti" style="display:none;">
      <button type="button" class="ListButtonChangePage" name="btnIndietro" 
          onClick="indietro()"><span align="center"><img id="immIndietro" src="../../img/indietro.gif" alt="Indietro" align="absmiddle" /> Indietro</span></button>
      &nbsp;
      <button type="button" class="ListButtonChangePage" name="btnAvanti" 
          onClick="avanti()"><span align="center">Avanti <img  id="immAvanti" src="../../img/avanti.gif" alt="Avanti" align="absmiddle" /></span></button>
      </div>
    </td>
  </tr>
  </table>
</form>
</body>
</html>
