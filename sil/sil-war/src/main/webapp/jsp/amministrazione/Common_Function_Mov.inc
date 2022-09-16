<script language="javascript">
  function azzeraAzienda(){
      document.Frm1.ragioneSociale.value = "";
      document.Frm1.pIva.value = "";
      document.Frm1.codFiscaleAzienda.value = "";
      document.Frm1.IndirizzoAzienda.value = "";
      document.Frm1.PRGAZIENDA.value = "";
      document.Frm1.PRGUNITA.value = "";
      if (document.Frm1.codTipoAz != null) {
      	document.Frm1.codTipoAz.value = "";
      }
      if (document.Frm1.codTipoAzienda != null) {
      	document.Frm1.codTipoAzienda.value = "";
      }
      if (document.Frm1.descrTipoAz != null) {
      	document.Frm1.descrTipoAz.value = "";
      }
      document.Frm1.FLGDATIOK.value = "";
      if (document.Frm1.CODNATGIURIDICA != null) {
      	document.Frm1.CODNATGIURIDICA.value = "";
      }
      var imgV = document.getElementById("tendinaAzienda");
      cambiaLavMC("aziendaSez","none");
      imgV.src=imgChiusa;
      imgV.alt = "Apri";
  }
    
  function azzeraAziendaUt(){
      document.Frm1.ragioneSocialeUt.value = "";
      document.Frm1.pIvaUt.value = "";
      document.Frm1.codFiscaleAziendaUt.value = "";
      document.Frm1.IndirizzoAziendaUt.value = "";
      document.Frm1.PRGAZIENDAUt.value = "";
      document.Frm1.PRGUNITAUt.value = "";
      if (document.Frm1.codTipoAzUt != null) {
      	document.Frm1.codTipoAzUt.value = "";
      }
      if (document.Frm1.codTipoAziendaUt != null) {
      	document.Frm1.codTipoAziendaUt.value = "";
      }
      if (document.Frm1.descrTipoAzUt != null) {
      	document.Frm1.descrTipoAzUt.value = "";
      }
      document.Frm1.FLGDATIOKUt.value = "";
      if (document.Frm1.CODNATGIURIDICAUt != null) {
      	document.Frm1.CODNATGIURIDICAUt.value = "";
      }
      var imgV = document.getElementById("tendinaAziendaUt");
      cambiaLavMC("aziendaSezUt","none");
      imgV.src=imgChiusa;
      imgV.alt = "Apri";
  }

  function azzeraLavoratore(){
      document.Frm1.codiceFiscaleLavoratore.value = "";
        document.Frm1.cognome.value = "";
        document.Frm1.nome.value = "";
        document.Frm1.CDNLAVORATORE.value = "";
        document.Frm1.FLGCFOK.value = "";
        var imgV = document.getElementById("tendinaLav");
        cambiaLavMC("lavoratoreSez","none");
        imgV.src=imgChiusa;
        imgV.alt = "Apri";
  }
</script>