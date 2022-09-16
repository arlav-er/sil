<?xml version="1.0" encoding="utf-8"?>
<!-- Edited with XML Spy v4.2 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<br/>
		<xsl:for-each select="ROOT/TIPOFUNZIONE">
			<xsl:if test=".//FILTRO">
				<br/>
				<!-- tabella del tipo di funzione 
					Non serve a niente!!!
				-->
				<table bgcolor="silver" width="90%">
					<tr>
						<td>
							<img alt='Apri' src='../../img/chiuso.gif'> 
								<xsl:attribute name="onclick">onOffTF('<xsl:value-of select="@CODICE" />');</xsl:attribute>
								<xsl:attribute name="id">IMG_<xsl:value-of select="@CODICE" /></xsl:attribute>
							</img>
							<xsl:value-of select="@ETICHETTA" />  
						</td>
					</tr>
					<tr>
						<td>
							<!-- tabella della funzione
								serve per collassare il tipo di funzione
							-->
							<table bgcolor="white" style="display:none" width="100%">
								<xsl:attribute name="id">TF_<xsl:value-of select="@CODICE" /></xsl:attribute>
								<tr>
									<td/>
								</tr>
								
								<xsl:for-each select="FUNZIONE">
									<xsl:sort select="@DESCRIZIONE"/>
									<xsl:if test=".//FILTRO">								
		                				<xsl:variable name="FUNZ"><xsl:value-of select="@CODICE"/></xsl:variable>
										<tr>
											<td align="left">
												<b>
													<div>
														<xsl:attribute name="title"><xsl:value-of select="@NOTA" /></xsl:attribute>
														<xsl:value-of select="@DESCRIZIONE" />
													</div>
												</b>
											</td>
										</tr>
										<tr>
											<td>
												<table style="display:" width="100%" >
													<tr>
														<td/>
													</tr>
													
													<xsl:for-each select="COMPONENTE">												
														<xsl:sort select="@DENOMINAZIONE" />
														<xsl:if test=".//FILTRO">
															<xsl:variable name="COMP">SEL_<xsl:copy-of select="$FUNZ"/>_<xsl:value-of select="@CODICE"/></xsl:variable>
																											
															<tr>
																<td align="left">
				    												<span>
																		<xsl:attribute name="title"><xsl:value-of select="@DESCRIZIONE" /></xsl:attribute>
															 			<xsl:value-of select="@DENOMINAZIONE" />
															 		</span>
																</td>
															</tr>
															<tr>
																<td>
																	<table border="0"  style="display:" bgcolor="white">
																		<tr>
																			<td/>
																		</tr>
																		
																		<xsl:if test="./LETTURA/LAVORATORE/FILTRO">
																			<tr>
																				<td>
																					<img src="../../img/lines/line2.gif" border="0" /><img src="../../img/lines/line4.gif" border="0" />
																				</td>
																				<td>Filtro sola lettura per lavoratore:
																					<select>
																						<xsl:attribute name="name"><xsl:copy-of select="$COMP" />_L</xsl:attribute>
																						<option></option>
																						<xsl:for-each select="./LETTURA/LAVORATORE/FILTRO">
																							<xsl:sort select="@DESCRIZIONE"/>
																							<option>
																								<xsl:attribute name="value"><xsl:value-of select="@CODICE" /></xsl:attribute>
																								<xsl:choose>
																									<xsl:when test="@SELEZIONATO='SI'">
																										<xsl:attribute name="selected">selected</xsl:attribute>
																									</xsl:when>
																								</xsl:choose>
																								<xsl:value-of select="@DESCRIZIONE" />
																							</option>
																						</xsl:for-each>
																					</select>
																				</td>
																			</tr>																
																		</xsl:if>
																		<xsl:if test="./LETTURA/SEDE/FILTRO">
																			<tr>
																				<td>
																					<img src="../../img/lines/line2.gif" border="0" /><img src="../../img/lines/line4.gif" border="0" />
																				</td>
																				<td>Filtro sola lettura per azienda:
																					<select>
																						<xsl:attribute name="name"><xsl:copy-of select="$COMP" />_U</xsl:attribute>
																						<option></option>
																						<xsl:for-each select="./LETTURA/SEDE/FILTRO">
																							<xsl:sort select="@DESCRIZIONE" />
																							<option>
																								<xsl:attribute name="value"><xsl:value-of select="@CODICE" /></xsl:attribute>
																								<xsl:choose>
																									<xsl:when test="@SELEZIONATO='SI'">
																										<xsl:attribute name="selected">selected</xsl:attribute>
																									</xsl:when>
																								</xsl:choose>
																								<xsl:value-of select="@DESCRIZIONE" />
																							</option>
																						</xsl:for-each>
																					</select>
																				</td>
																			</tr>																
																		</xsl:if>
																		<xsl:if test="./SCRITTURA/LAVORATORE/FILTRO">
																			<tr>
																				<td>
																					<img src="../../img/lines/line2.gif" border="0" /><img src="../../img/lines/line4.gif" border="0" />
																				</td>
																				<td>Filtro scrittura per lavoratore:
																					<select>
																						<xsl:attribute name="name"><xsl:copy-of select="$COMP" />_L</xsl:attribute>
																						<option></option>
																						<xsl:for-each select="./SCRITTURA/LAVORATORE/FILTRO">
																							<xsl:sort select="@DESCRIZIONE" />
																							<option>
																								<xsl:attribute name="value"><xsl:value-of select="@CODICE" /></xsl:attribute>
																								<xsl:choose>
																									<xsl:when test="@SELEZIONATO='SI'">
																										<xsl:attribute name="selected">selected</xsl:attribute>
																									</xsl:when>
																								</xsl:choose>
																								<xsl:value-of select="@DESCRIZIONE" />
																							</option>
																						</xsl:for-each>
																					</select>
																				</td>
																			</tr>																
																		</xsl:if>															
																		<xsl:if test="./SCRITTURA/SEDE/FILTRO">
																			<tr>
																				<td>
																					<img src="../../img/lines/line2.gif" border="0" /><img src="../../img/lines/line4.gif" border="0" />
																				</td>
																				<td>Filtro scrittura per azienda:
																					<select>
																						<xsl:attribute name="name"><xsl:copy-of select="$COMP" />_U</xsl:attribute>
																						<option></option>
																						<xsl:for-each select="./SCRITTURA/SEDE/FILTRO">
																							<xsl:sort select="@DESCRIZIONE" />
																							<option>
																								<xsl:attribute name="value"><xsl:value-of select="@CODICE" /></xsl:attribute>
																								<xsl:choose>
																									<xsl:when test="@SELEZIONATO='SI'">
																										<xsl:attribute name="selected">selected</xsl:attribute>
																									</xsl:when>
																								</xsl:choose>
																								<xsl:value-of select="@DESCRIZIONE" />
																							</option>
																						</xsl:for-each>
																					</select>
																				</td>
																			</tr>																
																		</xsl:if>
																		<xsl:if test="./LISTA/FILTRO">
																			<tr>
																				<td>
																					<img src="../../img/lines/line2.gif" border="0" /><img src="../../img/lines/line4.gif" border="0" />
																				</td>
																				<td>Filtro di lista:
																					<select>
																						<xsl:attribute name="name"><xsl:copy-of select="$COMP" /></xsl:attribute>
																						<option></option>
																						<xsl:for-each select="./LISTA/FILTRO">
																							<xsl:sort select="@DESCRIZIONE" />
																							<option>
																								<xsl:attribute name="value"><xsl:value-of select="@CODICE" /></xsl:attribute>
																								<xsl:choose>
																									<xsl:when test="@SELEZIONATO='SI'">
																										<xsl:attribute name="selected">selected</xsl:attribute>
																									</xsl:when>
																								</xsl:choose>
																								<xsl:value-of select="@DESCRIZIONE" />
																							</option>
																						</xsl:for-each>
																					</select>
																				</td>
																			</tr>																
																		</xsl:if>		
																	</table>
																</td>
															</tr>
														</xsl:if>
													</xsl:for-each>
													
												</table>
											</td>
										</tr>
										<tr>
											<td colspan="2">
												<hr/>
											</td>
										</tr>
									</xsl:if>
								</xsl:for-each>
							
							</table>
						</td>
					</tr>
				</table>
				<p></p>
			</xsl:if>
		</xsl:for-each>

	</xsl:template>
</xsl:stylesheet>

