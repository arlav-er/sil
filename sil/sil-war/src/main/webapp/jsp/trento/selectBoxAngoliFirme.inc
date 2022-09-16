<% Map aliasAngoliFirme = new HashMap();
	aliasAngoliFirme.put("Firma lav. ang alto a dx", "DD_2");
	aliasAngoliFirme.put("Firma op. ang alto a dx", "DI_2");
	aliasAngoliFirme.put("Firma lav. ang basso a sx", "DD_1");
	aliasAngoliFirme.put("Firma op. ang basso sx", "DI_1");	

	Iterator iterAF = aliasAngoliFirme.entrySet().iterator();

%>
<p class="titolo"><b>Tags Firma Grafometrica</b></p>
<select class="dropzone" id="tagAliasAngoliFirme" size=4>
	<%while (iterAF.hasNext()) { 
		Map.Entry mEntry = (Map.Entry) iterAF.next();%>
	<option draggable="true"
		ondragstart="event.dataTransfer.setData('text/plain','@<%= mEntry.getValue()%>')"
		value="@<%= mEntry.getValue()%>"><%= mEntry.getKey()%></option>
	<%} %>
</select>