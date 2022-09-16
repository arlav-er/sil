<script language="javascript">
<!--


	function setFlagIO(sel){
		document.getElementsByName("FlgCodMonoIO")[0].value=comboTemplateStampeDominio[sel]['STRIO'];
		//alert(comboTemplateStampeDominio[sel]['FLGFIRMAGRAFO']);
	  	if(comboTemplateStampeDominio[sel]['FLGFIRMAGRAFO']=='1'){
	  		document.getElementById('messaggioConsenso').style.display='inline';
	  		document.getElementById('FIRMAGRAFOMETRICA').value="true";
	  	}else if(comboTemplateStampeDominio[sel]['FLGFIRMAGRAFO']=='0'){
	  		document.getElementById('messaggioConsenso').style.display='none';
	  		document.getElementById('FIRMAGRAFOMETRICA').value="false";
	  	}
	}
	  
	  
-->
</script>