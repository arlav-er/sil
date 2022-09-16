<% String [] aliasDatiProt = new String [] {	"AnnoProt", 
										"NumProt", 
										"DataProt",
										"DataSistema"};
%>
<p class="titolo"><b>Dati Protocollazione</b></p>
<select class="dropzone" id="tagAliasDatiProt" size=4 ondblclick="onDblClick(this.value)">
	<%for (int i=0;i<aliasDatiProt.length;i++) { %>
	<option draggable="true"
		ondragstart="event.dataTransfer.setData('text/plain','@<%= aliasDatiProt[i]%>')"
		value="@<%= aliasDatiProt[i]%>"><%= aliasDatiProt[i]%></option>
	<%} %>
</select>