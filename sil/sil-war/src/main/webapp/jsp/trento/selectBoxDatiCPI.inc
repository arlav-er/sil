<% String [] aliasCpi = new String [] { "Cap", 
										"CodComune",
										"CodiceCPI",
										"CodProvincia",
										"Comune",
										"CPIEmail",
										"emailPEC",
										"emailADL",
										"Fax",
										"Indirizzo",
										"Localita",					
										"Nome",
										"Orario",
										"Provincia",
										"Responsabile",
										"Telefono"};
%>
<p class="titolo"><b>Dati Cpi</b></p>
<select class="dropzone" id="tagAliasDatiCPI" size=10 ondblclick="onDblClick(this.value)">
	<%for (int i=0;i<aliasCpi.length;i++) { %>
	<option draggable="true"
		ondragstart="event.dataTransfer.setData('text/plain','@<%= aliasCpi[i]%>')"
		value="@<%= aliasCpi[i]%>"><%= aliasCpi[i]%></option>
	<%} %>
</select>
