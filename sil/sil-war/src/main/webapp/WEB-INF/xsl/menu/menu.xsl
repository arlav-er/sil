<?xml version="1.0" encoding="utf-8"?>


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output omit-xml-declaration="yes"/> 

<xsl:template match="menu">
    <xsl:apply-templates/>
</xsl:template>
  


<!-- vocemenu -->
<xsl:template match="vocemenu">
	<table cellpadding="2" cellspacing="2" border="0">
		<tr>
			<td> </td>
			<td>
						<xsl:choose>
							  <xsl:when test="funzione/@cdnfunzione!=''">
									<img  src="../../img/i_p.gif" border="0" />

									<span class="unsel">
									
										<xsl:attribute name="id"><xsl:value-of select="@idvocemenu"/></xsl:attribute>
										<xsl:attribute name="onClick">seleziona(<xsl:value-of select="@idvocemenu"/>)</xsl:attribute>
						
										<xsl:value-of select="@descrizionevocemenu"/>
									</span>


							  </xsl:when>
							   <xsl:otherwise>
  							   	<img  src="../../img/btnMinus.gif" border="0" />

									<span class="unsel"  style="font-weight: bold;">
									
										<xsl:attribute name="id"><xsl:value-of select="@idvocemenu"/></xsl:attribute>
										<xsl:attribute name="onClick">seleziona(<xsl:value-of select="@idvocemenu"/>)</xsl:attribute>
						
										<xsl:value-of select="@descrizionevocemenu"/>
									</span>

							   </xsl:otherwise>	
						</xsl:choose>
						
		  		<xsl:apply-templates/>
			</td>
		</tr>
  </table>
 </xsl:template>

</xsl:stylesheet>
