                          <tr>
                            <td class="etichetta" nowrap>Profilo n. &nbsp;</td>
                            <td class="campo"><af:textBox classNameBase="input" type="number" name="prgAlternativa" readonly="true" value="<%=prgAlternativa.toString()%>" size="2" />
                            </td>
                          </tr>
                          <tr valign="top">
                            <td class="etichetta">Codice &nbsp;</td>
                            <td class="campo" colspan="3">
                                <af:textBox classNameBase="input" onKeyUp="fieldChanged()" readonly="<%= String.valueOf(!canManage) %>" title="Codice del corso" name="codCorso" value="<%= codCorso %>" size="10" maxlength="8" onBlur="clearTitolo();" required="true" />&nbsp;                    

                                <af:textBox type="hidden" name="codCorsoHid" value="<%= codCorso %>"/>
                                <% if (canManage) {
                                %>
                                  <A href="javascript:selectCorsi_onClick(Frm1.codCorso, Frm1.codCorsoHid, Frm1.strTitolo);">
                                    <img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                                  <A href="javascript:ricercaAvanzataCorsi();">
                                    Ricerca avanzata
                                  </A>
                               <%
                               }
                               %>
                            </td>
                          </tr>
                          <tr>
                            <td class="etichetta">Titolo &nbsp;</td>
                            <td class="campo" colspan="3">
                              <af:textArea cols="30" rows="4"  classNameBase="textarea" name="strTitolo" title="Titolo del corso" value="<%= titolo %>" readonly="true" required="true" />
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
                              <option value="S" <% if ( "S".equals(flgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
                              <option value="N" <% if ( "N".equals(flgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >No</option>
                            </af:comboBox>  
                            </td>
                          </tr>