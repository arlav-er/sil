<?xml version="1.0" encoding="utf-8"?>
<!-- Edited with XML Spy v4.2 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">

		<xsl:for-each select="ROOT/TIPOFUNZIONE">

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
								<xsl:sort select="@DESCRIZIONE" />
								
								<!--<xsl:variable name="cod_funzione"><xsl:value-of select="@CODICE" /></xsl:variable>-->
								
								
								<tr>
									<td align="left">

										<b>
											<div>
												<xsl:attribute name="title"><xsl:value-of select="@NOTA" /></xsl:attribute>
											 	<xsl:value-of select="@DESCRIZIONE" />
											 	<xsl:if test="@CONF='1'">
											 	<xsl:text> *</xsl:text>
											 	</xsl:if>
											 </div>
										</b>
									</td>
								</tr>
								<tr>
									<td>

										<table style="display:" width="100%" >
											<tr>
												<td></td>
											</tr>


											<xsl:for-each select="COMPONENTE">
												<xsl:sort select="@DENOMINAZIONE" />
												
												<xsl:variable name="FUNZ_COMP">CK_<xsl:value-of select="@CODICE"/></xsl:variable>
												<tr>
													<td align="left">
																	
																		<input type="checkbox">
																			<xsl:attribute name="name"><xsl:copy-of select="$FUNZ_COMP" /></xsl:attribute>
																			
																			<xsl:choose>
																			<xsl:when test="@ABILITATO='SI'">
																				<xsl:attribute name="checked">true</xsl:attribute>
																			</xsl:when>
																		</xsl:choose>
																		</input>
    											<span>
												
												<xsl:attribute name="title"><xsl:value-of select="@DESCRIZIONE" /></xsl:attribute>
											 	<xsl:value-of select="@DENOMINAZIONE" />
											 												 	
											 	<xsl:if test="@CONF='1'">
													<xsl:text> *</xsl:text>
												</xsl:if>
											 </span>

													</td>
												</tr>

												<tr>
												<!--	<td></td>-->
													<td>
													

														<table border="0"  style="display:" bgcolor="white">
															<tr>
																<td></td>
															</tr>
															

															<xsl:for-each select="ATTRIBUTO">
															    <!-- <xsl:sort select="@POSIZIONE" /> -->
 																<xsl:sort select="@TIPO" />
 																<xsl:sort select="@SEZIONE" />
																<xsl:sort select="@DENOMINAZIONE" />

																<xsl:variable name="FUNZ_COMP_ATTR"><xsl:copy-of select="$FUNZ_COMP" />_<xsl:value-of select="@CODICE"/></xsl:variable>
																<tr>
																	<td>
																		<img src="../../img/lines/line2.gif" border="0" /><img src="../../img/lines/line4.gif" border="0" />
																	</td>
																	<td nowrap="true" valign="top">
																		
																		
																		
																			<input type="checkbox">
																				<xsl:attribute name="name"><xsl:copy-of select="$FUNZ_COMP_ATTR" /></xsl:attribute>
																				
																				<xsl:choose>
																				<xsl:when test="@ABILITATO='SI'">
																					<xsl:attribute name="checked">true</xsl:attribute>
																				</xsl:when>
																			</xsl:choose>
																			</input>
																			
																			
																				<xsl:choose>
																					<xsl:when test="@TIPO='B'">
																						<img src="../../img/btn.gif" title="Pulsante" border="0" />
																					</xsl:when>
																					<xsl:when test="@TIPO='L'">
																						<img src="../../img/link.gif" title="Collegamento" border="0" />
																					</xsl:when>
																					<xsl:when test="@TIPO='G'">
																						<img src="../../img/salto.gif" title="Salto funzionale" border="0" />
																					</xsl:when>
																					<xsl:when test="@TIPO='S'">
																						<img src="../../img/sezione.gif" title="Sezione" border="0" />
																					</xsl:when>
																					<xsl:when test="@TIPO='C'">
																						<img src="../../img/campo.gif" title="Campo" border="0" />
																					</xsl:when>
																				</xsl:choose>
																		
																		
																		
																		
																				<span nowrap="true" style=" width:300px;">
																					<xsl:attribute name="title"><xsl:value-of select="@NOTA" /></xsl:attribute>
																				 	<xsl:value-of select="@DENOMINAZIONE" />
																				 	
																				 	 <xsl:if test="@SEZIONE != ''">
																					     (<xsl:value-of select="@SEZIONE" />)
																					  </xsl:if>
																					  																					 
																					  <xsl:if test="@CONF='1'">
																					 	<xsl:text> *</xsl:text>
																					 </xsl:if>
																				 </span>

																		 

																		 
																		 <xsl:choose>
																			<xsl:when test="@TIPO='C'">
																			
																				
																			
																				Posizione n.
																				<input type="text">
																					<xsl:attribute name="name">POS_<xsl:copy-of select="$FUNZ_COMP_ATTR" /></xsl:attribute>
																					<xsl:attribute name="value"><xsl:value-of select="@POSIZIONE" /></xsl:attribute>
																					<xsl:attribute name="size">2</xsl:attribute>
																					<xsl:attribute name="maxlength">2</xsl:attribute>
																				</input>
																				
																				A capo 	
																				<input type="checkbox">
																				<xsl:attribute name="name">RET_<xsl:copy-of select="$FUNZ_COMP_ATTR" /></xsl:attribute>
																				
																					<xsl:choose>
																						<xsl:when test="@CARRIAGE_RET='SI'">
																							<xsl:attribute name="checked">true</xsl:attribute>
																						</xsl:when>
																					</xsl:choose>
	  																			</input>
																				
																				
																				
																				
																			</xsl:when>
																		</xsl:choose>
																		 																		 
																	</td>
																</tr>
															</xsl:for-each>
														</table>


													</td>

												</tr>
											</xsl:for-each>
										</table>


									</td>
								</tr>

								<tr>
									<td colspan="2">
										<hr />
									</td>
								</tr>


							</xsl:for-each>
						</table>
					</td>



				</tr>


			</table>

			<p></p>

		</xsl:for-each>



	</xsl:template>
</xsl:stylesheet>
