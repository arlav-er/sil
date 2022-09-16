<script language="JavaScript">

function apriGestioneProtocollazioneParam(parametri, tipoDoc, pageDaChiamare) {
  var urlo = "AdapterHTTP?PAGE=GestioneProtocollazioneParamPage";
  urlo += parametri;
  urlo +="&tipoDoc="+tipoDoc;
  urlo +="&PAGE_DA_CHIAMARE="+pageDaChiamare;
  
  var titolo = "gestioneProtocollazioneStampeParametriche";
  var w=800; var l=((screen.availWidth)-w)/2;
  var h=350; var t=((screen.availHeight)-h)/2;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  
  var opened = window.open(urlo, titolo, feat);
  opened.focus();
}
</script>

