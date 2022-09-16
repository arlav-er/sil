<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes"/>
<xsl:template match="DE_MANSIONE"><link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/><html><head><title>Mansioni</title><link rel="stylesheet" type="text/css" href="../../css/stili.css"/><link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<script type="text/javascript" src="../../js/document.js"></script><script type="text/javascript" src="../../js/tree_codifiche.js"></script>
<script type="text/javascript">
function F(ID, Desc, Tipo) {
	if (isScaduto(Desc)) {
		alert("Non è possibile inserire una Mansione scaduta");
	} else {
		var indiceMans = myIndiceMansione();
		if (indiceMans == '') {
		    window.opener.document.Frm1.CODMANSIONE.value = ID;
		    window.opener.document.Frm1.DESCMANSIONE.value = Desc.replace(/\^/g, '\'');
		    window.opener.document.Frm1.strTipoMansione.value = Tipo.replace(/\^/g, '\'');
	    }
	   	else {
	   		var fieldMansione = eval('window.opener.document.Frm1.CODMANSIONE' + indiceMans);
			var fieldMansioneDesc = eval('window.opener.document.Frm1.DESCMANSIONE' + indiceMans);
			var fieldMansioneTipo = eval('window.opener.document.Frm1.strTipoMansione' + indiceMans);
			fieldMansione.value = ID;
			fieldMansioneDesc.value = Desc.replace(/\^/g, '\'');
			fieldMansioneTipo.value = Tipo.replace(/\^/g, '\'');
	   	}
	    window.close();
	}
}
function isScaduto(str) {
	if (opener.flagRicercaPage != "S") { // Se NON provengo da una page di ricerca ammetto solo quelli non scaduti.
		if (str.substring(str.length-9) == "(scaduto)") {
			return true;
		}
	}
	return false;
}
ot = new jsTree;
ot.createRoot("","","","");
<xsl:apply-templates select="MANSIONE" />
function doLoad(){
	ot.buildDOM();
}

function myFlgid() {
	var flgIdo = 'N';
	var strQueryStringMans = window.location.href;
	var strUrl = strQueryStringMans.split("?");
	if (strUrl.length > 1) {
		var parametriQS = strUrl[1];
		var attributi = parametriQS.split("&amp;");
		if (attributi.length > 0) {
			for(var i = 0; i &lt; attributi.length; i++) {
				var attributo = attributi[i].split("=");
				if (attributo.length > 1) {
					var nome = attributo[0];
					var valore = attributo[1];
					if (nome.toUpperCase() == 'FLGIDO') {
						flgIdo = valore;
						break;
					}
				}
			}
		}
	}
	return flgIdo;
}


function myIndiceMansione() {
	var indiceMansione = '';
	var strQueryStringMans = window.location.href;
	var strUrl = strQueryStringMans.split("?");
	if (strUrl.length > 1) {
		var parametriQS = strUrl[1];
		var attributi = parametriQS.split("&amp;");
		if (attributi.length > 0) {
			for(var i = 0; i &lt; attributi.length; i++) {
				var attributo = attributi[i].split("=");
				if (attributo.length > 1) {
					var nome = attributo[0];
					var valore = attributo[1];
					if (nome.toUpperCase() == 'INDICEMANSIONE') {
						indiceMansione = valore;
						break;
					}
				}
			}
		}
	}
	return indiceMansione;
}


function ricercaAvanzataMansioni() {
	var url = "AdapterHTTP?PAGE=RicercaMansioneAvanzataPage&amp;FLGIDO=" + myFlgid();
	var indiceMans = myIndiceMansione();
	if (indiceMans != '') {
		url +="&amp;indiceMansione="+indiceMans;
	}
	window.location = url;
}

</script>

</head><br/><body onload="doLoad();objLocalTree.toggleExpand('root_0');" class="gestione"><br/><center><table><tr><td><input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:ricercaAvanzataMansioni();"/></td><td><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="javascript:window.close();"/></td></tr></table></center></body></html>
</xsl:template>
<xsl:template match="MANSIONE">
d<xsl:value-of select="@codMansione"/>="<xsl:value-of select="@strDescrizione"/>";
p<xsl:value-of select="@codMansione"/>="<xsl:value-of select="@desTipologia" />";
<xsl:if test="name(..)='DE_MANSIONE'">o<xsl:value-of select="@codMansione"/>=ot.root.addChild("","<xsl:value-of select="@strDescrizione"/>","javascript:F('<xsl:value-of select="@codMansione"/>',d<xsl:value-of select="@codMansione"/>.replace('\\\'','^'),p<xsl:value-of select="@codMansione"/>.replace('\\\'','^'));","");
</xsl:if>
<xsl:if test="name(..)='MANSIONE'">o<xsl:value-of select="@codMansione"/>=o<xsl:value-of select="@codPadre"/>.addChild("","<xsl:value-of select="@strDescrizione"/>","javascript:F('<xsl:value-of select="@codMansione"/>',d<xsl:value-of select="@codMansione"/>.replace('\\\'','^'),p<xsl:value-of select="@codMansione"/>.replace('\\\'','^'));","");
</xsl:if>
<xsl:apply-templates select="*" />
</xsl:template>
</xsl:stylesheet>