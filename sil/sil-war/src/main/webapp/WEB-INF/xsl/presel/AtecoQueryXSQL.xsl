<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>
	<xsl:template match="ROOT">
		<DE_ATTIVITA>
			<xsl:apply-templates select="attivita">
			</xsl:apply-templates>
		</DE_ATTIVITA>	
	</xsl:template>
	<xsl:template match="attivita">
	<xsl:param name="codAtecoPadre">0</xsl:param>
		<xsl:if test="@codPadre=$codAtecoPadre">
			<xsl:element name="attivita">
				<xsl:attribute name="codAteco">
					<xsl:value-of select="@codAteco"/>
				</xsl:attribute>
				<xsl:attribute name="strDescrizione">
					<xsl:value-of select="@strDescrizione"/>
				</xsl:attribute>
				<xsl:attribute name="cdnLivello">
					<xsl:value-of select="@cdnLivello"/>
				</xsl:attribute>
				<xsl:attribute name="codAtecoDot">
					<xsl:value-of select="@codAtecoDot"/>
				</xsl:attribute>
				<xsl:attribute name="codPadre">
					<xsl:value-of select="@codPadre"/>
				</xsl:attribute>
				<xsl:attribute name="desTipoAteco">
					<xsl:value-of select="@desTipoAteco"/>
				</xsl:attribute>
				<xsl:apply-templates select="/ROOT/attivita">
					<xsl:with-param name="codAtecoPadre" select="@codAteco"/>
				</xsl:apply-templates>
			</xsl:element>		
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>