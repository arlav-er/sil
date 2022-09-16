  <!-- Visualizzazione sezione errori -->
  <%
	if(elemScriptAlert != null && !elemScriptAlert.equals("")){
		out.print(elemScriptAlert); 
  }
  if(elemScriptConfirm != null && !elemScriptConfirm.equals("")){
		out.print(elemScriptConfirm);
	}
  %> 