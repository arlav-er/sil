<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes"/>
<xsl:template match="DE_ATTIVITA">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/><html><head><title>Codici di attività</title><link rel="stylesheet" type="text/css" href="../../css/stili.css"/><link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<script type="text/javascript" src="../../js/document.js"></script><script type="text/javascript" src="../../js/tree_codifiche.js"></script><script type="text/javascript">
function F(ID, Desc, Tipo, Codatecodot){
	if (isScaduto(Desc)) {
		alert("Non è possibile inserire una Attività scaduta");
	} else {
		if( Codatecodot == '' ){
			alert("E' possibile selezionare solo i codici ministeriali");
		}else{
			window.opener.document.Frm1.codAteco.value = ID;
			window.opener.document.Frm1.strAteco.value = Desc.replace(/\^/g, '\'');
			window.opener.document.Frm1.strTipoAteco.value= Tipo.replace(/\^/g, '\'');
			window.close();
		}
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
var ot = new jsTree;
ot.createRoot("","","","");
<xsl:apply-templates select="ATTIVITA" />
function doLoad() {ot.buildDOM();} 
function ricercaAvanzataAteco() {
  window.location="AdapterHTTP?PAGE=RicercaAtecoAvanzataPage";
}
</script></head><br/><body onload="doLoad();objLocalTree.toggleExpand('root_0');" class="gestione"><br/><center><table><tr><td><input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:ricercaAvanzataAteco();"/></td><td><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="javascript:window.close();"/></td></tr></table></center></body></html>
</xsl:template><xsl:template match="ATTIVITA">var d<xsl:value-of select="@codAteco"/>="<xsl:value-of select="@strDescrizione"/>";var p<xsl:value-of select="@codAteco"/>="<xsl:value-of select="@desTipoAteco"/>";<xsl:if test="name(..)='DE_ATTIVITA'">var o<xsl:value-of select="@codAteco"/>=ot.root.addChild("","<xsl:value-of select="@strDescrizione"/>","javascript:F('<xsl:value-of select="@codAteco"/>',d<xsl:value-of select="@codAteco"/>.replace('\\\'','^'),p<xsl:value-of select="@codAteco"/>.replace('\\\'','^'),'<xsl:value-of select="@codAtecoDot"/>');","");</xsl:if><xsl:if test="name(..)='ATTIVITA'">var o<xsl:value-of select="@codAteco"/>=o<xsl:value-of select="@codPadre"/>.addChild("","<xsl:value-of select="@strDescrizione"/>","javascript:F('<xsl:value-of select="@codAteco"/>',d<xsl:value-of select="@codAteco"/>.replace('\\\'','^'),p<xsl:value-of select="@codAteco"/>.replace('\\\'','^'),'<xsl:value-of select="@codAtecoDot"/>');","");</xsl:if><xsl:apply-templates select="*" /></xsl:template></xsl:stylesheet>