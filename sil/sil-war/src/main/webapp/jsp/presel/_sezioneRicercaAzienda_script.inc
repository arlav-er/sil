<script language="javascript">
	function aggiornaAzienda(){
	    document.Frm1.codAteco.value = opened.dati.codAteco;
   	    document.Frm1.codAtecoHid.value = opened.dati.codAteco;
	    document.Frm1.strTipoAteco.value = opened.dati.tipoAteco;
	    document.Frm1.strAteco.value = opened.dati.strDesAtecoUAz;    
	    document.Frm1.strCodFiscaleAzienda.value=opened.dati.codiceFiscaleAzienda;
	    document.Frm1.strPartitaIvaAzienda.value=opened.dati.partitaIva;
	    document.Frm1.strRagSocialeAzienda.value=opened.dati.ragioneSociale;
	    document.Frm1.strIndirizzoAzienda.value=opened.dati.strIndirizzoAzienda;
	    document.Frm1.codComAzienda.value=opened.dati.codCom;
	    document.Frm1.codComAziendaHid.value=opened.dati.codCom;
	    
	    document.Frm1.strComAzienda.value=opened.dati.comuneAzienda;
	    document.Frm1.strComAziendaHid.value=opened.dati.comuneAzienda;
	    document.Frm1.codNatGiuridica.value=opened.dati.CODNATGIURIDICA;  
	    opened.close();
	}
</script>	