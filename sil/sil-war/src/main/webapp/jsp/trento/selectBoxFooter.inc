<% String [] aliasFooter = null;
	if (regione.equals(Properties.UMB)) {	
		aliasFooter = new String[]{"UMBFooter"};
   } else if (regione.equals(Properties.TN)) { 
	    aliasFooter = new String[]{"Footer", "PATFooter", "ModCertificatoFooter"};
   }
%>
<p class="titolo"><b>Footer</b></p>
<select class="dropzone" id="tagFooter" size=4>
	<%for (int i=0;i<aliasFooter.length;i++) { %>
	<option draggable="true"
		ondragstart="event.dataTransfer.setData('text/plain','@<%= aliasFooter[i]%>')"
		value="@<%= aliasFooter[i]%>"><%= aliasFooter[i]%></option>
	<%} %>
</select>