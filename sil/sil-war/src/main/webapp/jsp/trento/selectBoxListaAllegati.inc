<% String [] aliasAllegati = new String [] {	"Allegati"};
%>
<p class="titolo"><b>Lista Allegati</b></p>
<select class="dropzone" id="tagListaAllegati" size=2>
	<%for (int i=0;i<aliasAllegati.length;i++) { %>
	<option draggable="true"
		ondragstart="event.dataTransfer.setData('text/plain','@<%= aliasAllegati[i]%>')"
		value="@<%= aliasAllegati[i]%>"><%= aliasAllegati[i]%></option>
	<%} %>
</select>