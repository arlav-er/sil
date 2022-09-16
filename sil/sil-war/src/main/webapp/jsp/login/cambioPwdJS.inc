<script language="JavaScript">

	function valida() {

	var oldPwd = document.Frm1.oldPwd.value;
	var newPwd = document.Frm1.newPwd.value;
	var newPwd2 = document.Frm1.newPwd2.value;
 	
	if (newPwd != newPwd2) {
		alert ("La password di conferma è diversa da quella inserita come nuova password");
		document.Frm1.newPwd2.focus();
		return false;
	}

	if (newPwd == oldPwd) {
		alert ("La nuova password non può essere uguale a quella vecchia");
		document.Frm1.newPwd.focus();
		return false;
	}


	return true;
	}
	
</script>