<script language="JavaScript">

function infoStoriche(pagina,parametri)
{ var urlo = "AdapterHTTP?PAGE="+pagina + parametri;
  var titolo = "InfoStoriche";
  var w=(screen.availWidth)*0.85;  var l=(screen.availHeight)*0.1;
  var h=(screen.availHeight)*0.8; var t=(screen.availHeight)*0.1;
  //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  window.open(urlo, titolo, feat);
}

function aggiornaNote(form)	{
	form.soloNote.value="true";
	doFormSubmit(form);	
}

</script>