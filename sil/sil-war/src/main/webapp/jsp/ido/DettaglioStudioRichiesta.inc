                          <tr>
                            <td class="etichetta" nowrap>Profilo n. &nbsp;</td>
                            <td class="campo"><af:textBox classNameBase="input" type="number" name="prgAlternativa" readonly="true" value="<%=prgAlternativa.toString()%>" size="2" />
                            </td>
                          </tr>
                          <tr valign="top">
                            <td class="etichetta">Codice &nbsp;</td>
                            <td class="campo" colspan="3">
                                <af:textBox classNameBase="input" onKeyUp="fieldChanged()" readonly="<%= String.valueOf(!canManage) %>" title="Codice del titolo" name="codTitolo" value="<%= codTitolo %>" size="10" maxlength="8" onBlur="clearTitolo();" required="true" />&nbsp;                    

                                <af:textBox type="hidden" name="codTitoloHid" value="<%= codTitolo %>"/>
                                <% if (canManage) {
                                %>
                                  <A href="javascript:selectTitolo_onClick(Frm1.codTitolo, Frm1.codTitoloHid, Frm1.strTitolo,  Frm1.strTipoTitolo);">
                                    <img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                                  <A href="javascript:ricercaAvanzataTitoliStudio();">
                                    Ricerca avanzata
                                  </A>
                               <%
                               }
                               %>
                            </td>
                          </tr>
                          <tr valign="top">
                            <td class="etichetta">Tipo &nbsp;</td>
                            <td class="campo" colspan="3">
                              <af:textBox type="hidden" name="flgLaurea" />
                              <af:textBox size="50" classNameBase="input" title="Tipo del titolo" name="strTipoTitolo" value="<%= tipoTitolo%>" readonly="true"  /> 
                            </td>
                          </tr>
                          <tr>
                            <td class="etichetta">Titolo &nbsp;</td>
                            <td class="campo" colspan="3">
                              <af:textArea cols="30" rows="4"  classNameBase="textarea" name="strTitolo" value="<%= titolo %>" readonly="true" required="true" />
                            </td>
                          </tr>
                          <tr>
                            <td class="etichetta">Specifica &nbsp;</td>
                            <td class="campo" colspan="3">
                              <af:textBox size="50" classNameBase="input" name="strSpecifica" onKeyUp="fieldChanged()" value="<%=strSpecifica%>" readonly="<%= String.valueOf(!canManage) %>"/>&nbsp;</td>
                          </tr>
                          <tr>
                            <td class="etichetta">Conseguito &nbsp;</td>
                            <td colspan="3" class="campo">
                              <af:comboBox name="flgConseguito"
                                         title="conseguito" required="false"
                                         classNameBase="input"
                                         disabled="<%= String.valueOf(!canManage) %>"
                                         onChange="fieldChanged()"
                                         selectedValue="<%= flgConseguito %>">
                              <option value=""  <% if ( "".equals(flgConseguito) )  { out.print("SELECTED=\"true\""); } %> ></option>
                              <option value="S" <% if ( "S".equals(flgConseguito) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
                              <option value="N" <% if ( "N".equals(flgConseguito) ) { out.print("SELECTED=\"true\""); } %> >No</option>
                            </af:comboBox> 
                            </td>
                          </tr>
                          <tr>
                          <td class="etichetta">Indispensabile &nbsp;</td>
                            <td class="campo">
                            <af:comboBox name="FLGINDISPENSABILE"
                                         title="Indispensabile" required="false"
                                         classNameBase="input"
                                         disabled="<%= String.valueOf(!canManage) %>"
                                         onChange="fieldChanged()"
                                         selectedValue="<%= flgIndispensabile %>">
                              <option value=""  <% if ( "".equals(flgIndispensabile) )  { out.print("SELECTED=\"true\""); } %> ></option>
                              <option value="S" <% if ( "S".equals(flgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
                              <option value="N" <% if ( "N".equals(flgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >No</option>
                            </af:comboBox>  
                            </td>
                          </tr>