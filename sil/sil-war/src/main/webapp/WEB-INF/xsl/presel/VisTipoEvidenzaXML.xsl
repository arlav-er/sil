<!-- Edited A MANAZZA by RolfEdit 1.0 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<table bgcolor="white" style="display:" width="100%" >
			<tr>
				<td></td>
			</tr>

			<xsl:for-each select="ROOT/PROFILO">
				<xsl:sort select="@denominazione" />												
				<tr>
					<td align="left">									
					<!--
						<input type="checkbox">
							<xsl:attribute name="name">PROF<xsl:value-of select="@codice" /></xsl:attribute>
							<xsl:attribute name="onClick">javascript:clickProfilo('PROF<xsl:value-of select="@codice" />')</xsl:attribute>
							<xsl:choose>
								<xsl:when test="@selezionato='SI'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:when>
							</xsl:choose>
						</input>
					-->
					<b>
    					<span>
							<xsl:attribute name="title"><xsl:value-of select="@descrizione" /></xsl:attribute>
						 	<xsl:value-of select="@denominazione" />
						</span>
					</b>
					</td>
				</tr>
				<tr>
					<td>
						<table border="0"  style="display:" bgcolor="white">
							<tr>
								<td></td>
							</tr>
								<xsl:for-each select="GRUPPO">
									<xsl:sort select="@denominazione" />																									
									<tr>
										<td>
											<img src="../../img/lines/line2.gif" border="0" /><img src="../../img/lines/line4.gif" border="0" />
										</td>
										<td nowrap="true" valign="top">
											<input type="checkbox">
											<!--	<xsl:attribute name="name">PG<xsl:value-of select="@profilo" />_<xsl:value-of select="@codice" /></xsl:attribute> -->
												<xsl:attribute name="name">PG</xsl:attribute>
												<xsl:attribute name="value"><xsl:value-of select="@profilo" />_<xsl:value-of select="@codice" /></xsl:attribute>
												<xsl:choose>
													<xsl:when test="@selezionato='SI'">
														<xsl:attribute name="checked">true</xsl:attribute>
													</xsl:when>
												</xsl:choose>
											</input>
											<span>	
											 	<xsl:value-of select="@denominazione" />
											</span>											 	
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</td>
					</tr>
					<tr><td></td></tr>
					<tr><td></td></tr>
				</xsl:for-each>
			</table>
	</xsl:template>
</xsl:stylesheet>
