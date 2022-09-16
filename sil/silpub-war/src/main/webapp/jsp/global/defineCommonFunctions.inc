<%!  // *** defineCommonFunctions.inc ***
     // Definizione delle funzioni comuni a molte pagine
    
	private String TD_CampoObbligatorio (String nomeCampo, String nomeClass) {

    return  TD_CampoObbligatorio (nomeCampo, nomeClass, null);
  }


	private String TD_CampoObbligatorio (String nomeCampo, String nomeClass, String colSpan) {

    StringBuffer output = new StringBuffer();
    String nomeClassUpCase = nomeClass.toUpperCase();

    output.append("<td class=\"");

    if (nomeClassUpCase.startsWith("ETICH") ||
        nomeClassUpCase.startsWith("DETT")  ) {

          output.append(nomeClass);
          output.append("Obblig\"");    // es: "etichSx" --> "etichSxObblig"
    }
    else {
          output.append(nomeClass);     // mantiene class e aggiunge stile BOLD
          output.append("\" style=\"FONT-WEIGHT:BOLD\"");
    }

    if (colSpan != null) {
      output.append(" colspan=\"");
      output.append(colSpan);
      output.append("\"");
    }
    output.append(">");
    output.append(nomeCampo);
    output.append("*</td>");    // Aggiunge anche un ASTERISCO a fine etichetta!
    return output.toString();
  }

%>