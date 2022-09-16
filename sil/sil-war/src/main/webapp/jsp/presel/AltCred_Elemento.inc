<tr>
  <td class="etichetta">Specifica del credito</td>
  <td class="campo">
    <af:textBox name="STRSPECIFICA"
                title="Specifica del credito"
                value="<%= strSpecifica %>"
                maxlength="200"
                size="50"
                required="true"
                classNameBase="input"
                readonly="<%= fieldReadOnly %>"
                onKeyUp="fieldChanged()"/>
  </td>
</tr>
