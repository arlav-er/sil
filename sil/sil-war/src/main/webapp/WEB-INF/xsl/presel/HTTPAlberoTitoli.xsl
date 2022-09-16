<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes"/>
<xsl:template match="DE_TITOLO">
<html><head><title>Titoli di studio</title><link rel="stylesheet" type="text/css" href="../../css/stili.css"/><link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<script type="text/javascript" src="../../js/document.js"></script><script type="text/javascript" src="../../js/tree_codifiche.js"></script><script type="text/javascript">
function F (ID, Desc, Tipo, Laurea) {
  window.opener.document.Frm1.codTitolo.value = ID;
  window.opener.document.Frm1.strTitolo.value = Desc.replace(/\^/g, '\'');
  window.opener.document.Frm1.strTipoTitolo.value= Tipo.replace(/\^/g, '\'');
  window.opener.document.Frm1.flgLaurea.value=Laurea;
  window.opener.toggleVisStato();
  window.close();
}
var ot = new jsTree;
ot.createRoot("","","","");
<xsl:apply-templates select="TITOLO" />
function doLoad() {ot.buildDOM();} 
function ricercaAvanzataTitoliStudio() {
  var w=800; var l=((screen.availWidth)-w)/2;
  var h=500; var t=((screen.availHeight)-h)/2;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  window.location="AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage";
}
</script></head><br/><body onload="doLoad();objLocalTree.toggleExpand('root_0');" class="gestione"><br/><center><table><tr><td><input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:ricercaAvanzataTitoliStudio();"/></td><td><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="window.close();"/></td></tr></table></center></body></html>
</xsl:template><xsl:template match="TITOLO">
c<xsl:value-of select="@codTitolo"/>="<xsl:value-of select="@codTitolo"/>";
d<xsl:value-of select="@codTitolo"/>="<xsl:value-of select="@strDescrizione"/>";
p<xsl:value-of select="@codTitolo"/>="<xsl:value-of select="@desTipologia"/>";
l<xsl:value-of select="@codTitolo"/>="<xsl:value-of select="@flgLaurea" />";
<xsl:if test="name(..)='DE_TITOLO'">o<xsl:value-of select="@codTitolo"/>=ot.root.addChild("","<xsl:value-of select="@strDescrizione"/>","javascript:F(c<xsl:value-of select="@codTitolo"/>,d<xsl:value-of select="@codTitolo"/>.replace('\\\'','^'),p<xsl:value-of select="@codTitolo"/>.replace('\\\'','^'),l<xsl:value-of select="@codTitolo"/>);","");</xsl:if>
<xsl:if test="name(..)='TITOLO'">o<xsl:value-of select="@codTitolo"/>=o<xsl:value-of select="@codPadre"/>.addChild("","<xsl:value-of select="@strDescrizione"/>","javascript:F(c<xsl:value-of select="@codTitolo"/>,d<xsl:value-of select="@codTitolo"/>.replace('\\\'','^'),p<xsl:value-of select="@codTitolo"/>.replace('\\\'','^'),l<xsl:value-of select="@codTitolo"/>);","");
</xsl:if><xsl:apply-templates select="*" /></xsl:template></xsl:stylesheet>