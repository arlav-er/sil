<% String [] aliasLav = new String [] {	"RagSociale", 
										"PartitaIva",
										"CodiceFiscaleAzienda",
										"NaturaGiuridica",										
										"TipoAzienda",
										"SitoInternet",	
										"AttivAteco",
										"NumSoci",
										"NumDipendenti",
										"NumCollaboratori",
										"NumAltraPosizione",
										"DataInizioAttivita",
										"DataFineAttivita",
										"PatInail",
										"FlgObbl68",
										"NumAlboInterinali",
										"NumAgSomministrazione",
										"DataCambioRagsociale",
										"DenominazioneSedeLegale",
										"IndirizzoSedeLegale",
										"ComuneSedeLegale",
										"CapSedeLegale",
										"TelSedeLegale",
										"FaxSedeLegale",
										"EmailSedeLegale"
										};

%>
<p class="titolo"><b>Dati Azienda</b></p>
<select class="dropzone" id="tagAlias" size=10 ondblclick="onDblClick(this.value)">
	<%for (int i=0;i<aliasLav.length;i++) { %>
	<option draggable="true"
		ondragstart="event.dataTransfer.setData('text/plain','@<%= aliasLav[i]%>')"
		value="@<%= aliasLav[i]%>"><%= aliasLav[i]%></option>
	<%} %>
</select>
