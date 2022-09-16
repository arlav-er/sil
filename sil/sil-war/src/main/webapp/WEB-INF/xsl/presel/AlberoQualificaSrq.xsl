<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes"/>
<xsl:template match="DE_QUALIFICA_SRQ"><link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/><html><head><title>Mansioni</title><link rel="stylesheet" type="text/css" href="../../css/stili.css"/><link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<script type="text/javascript" src="../../js/document.js"></script><script type="text/javascript" src="../../js/tree_codifiche.js"></script>
<script type="text/javascript">
function F(ID, Desc, Tipo) {
  window.opener.document.Frm1.CODQUALIFICASRQ.value = ID;
  window.opener.document.Frm1.DESCQUALIFICASRQ.value = Desc.replace(/\^/g, '\'');
  window.close();
}
ot = new jsTree;
ot.createRoot("","","","");
<xsl:apply-templates select="MANSIONE" />function doLoad(){ot.buildDOM();}function ricercaAvanzataMansioni() {window.location="AdapterHTTP?PAGE=RicercaQualificaSRQAvanzataPage";}</script></head><br/><body onload="doLoad();objLocalTree.toggleExpand('root_0');" class="gestione"><br/><center><table><tr><td><input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:ricercaAvanzataMansioni();"/></td><td><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="javascript:window.close();"/></td></tr></table></center></body></html>
</xsl:template><xsl:template match="MANSIONE">d<xsl:value-of select="@codMansione"/>="<xsl:value-of select="@strDescrizione"/>";p<xsl:value-of select="@codMansione"/>="<xsl:value-of select="@desTipologia" />";<xsl:if test="name(..)='DE_QUALIFICA_SRQ'">o<xsl:value-of select="@codMansione"/>=ot.root.addChild("","<xsl:value-of select="@strDescrizione"/>","javascript:F('<xsl:value-of select="@codMansione"/>',d<xsl:value-of select="@codMansione"/>.replace('\\\'','^'),p<xsl:value-of select="@codMansione"/>.replace('\\\'','^'));","");</xsl:if><xsl:if test="name(..)='MANSIONE'">o<xsl:value-of select="@codMansione"/>=o<xsl:value-of select="@codPadre"/>.addChild("","<xsl:value-of select="@strDescrizione"/>","javascript:F('<xsl:value-of select="@codMansione"/>',d<xsl:value-of select="@codMansione"/>.replace('\\\'','^'),p<xsl:value-of select="@codMansione"/>.replace('\\\'','^'));","");</xsl:if><xsl:apply-templates select="*" /></xsl:template></xsl:stylesheet>