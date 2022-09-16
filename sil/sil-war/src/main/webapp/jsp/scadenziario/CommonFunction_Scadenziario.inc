<%!
// strTitoloLista           :titolo da visualizzare per la descrizione della lista
// vettIntestazioni         :vettore delle intestazioni della lista
// vettColonne              :vettore delle colonne presenti nella serviceResponse
// vettColonneLink          :vettore che permette di definire dei link per le colonne
// vettAzioniColonneLink    :vettore delle azioni dei link per le colonne
// vettIconeColonneLink     :vettore delle icone dei link per le colonne
// vettColonneVisibili      :vettore che indica se una colonna è visibile
// vettIntestazioniBottoni  :vettore intestazioni dei bottoni da aggiungere alla fine
// vettIconeBottoni         :vettore delle icone dei bottoni
// vettAzioniBottoni        :vettore delle azioni dei bottoni
// rows_VectorScadenze      :vettore di SourceBean (rappresenta il contenuto della lista)
//isCpi						:boolean (true se l'operatore è associato ad un cpi, altrimenti false)
private String StampaLista (String codCpiLocal,
                            String strTitoloLista,
                            String strListaFiltrata,
                            String vettIntestazioni[],
                            String vettColonne[],
                            boolean vettColonneLink[], 
                            String vettAzioniColonneLink[],
                            String vettIconeColonneLink[],
                            boolean vettColonneVisibili[],
                            String vettIntestazioniBottoni[],
                            String vettIconeBottoni[],
                            String vettAzioniBottoni[],
                            String vettIntestazioniBottoniComuni[],
                            String vettIconeBottoniComuni[], 
                            String vettAzioniBottoniComuni[],
                            Vector rows_VectorScadenze,
                            int nCodScadenzaLocal,
                            boolean isCpi) {

  SourceBean riga = null;
  Object objValueColonna;
  String strValueColonna = "";
  String strCdnKey = "";
  String strCdnKeyStampa = "";
  String strCdnAzienda = "";
  String strCdnUnita = "";
  Object dataScadenza = "";
  String strDataScadenza = "";
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  String strNomeClasse = "";
  
  StringBuffer output = new StringBuffer();
  //output.append ("<CENTER><H2>");
  //output.append (strTitoloLista);
  //output.append ("</H2></CENTER>");
  
  int nFiltraLista = Integer.parseInt(strListaFiltrata);

  if ((nCodScadenzaLocal != 1001) && (nCodScadenzaLocal != 1002) && (nCodScadenzaLocal != 5) && (nCodScadenzaLocal != 1003)) {
    if (rows_VectorScadenze.size() > 0 || nFiltraLista > 0) {
      output.append("<table width=");
      output.append("100% ");
      output.append("><tr>");

      output.append("<td align=left>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
      output.append("<td align=left>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");

/** 
 **   Commentato il checkbox in data 27/09/2004 
 **
      if (rows_VectorScadenze.size() > 0) {
        output.append("<td align=left class=etichetta nowrap>Seleziona tutti</td>");
        output.append("<td align=left class=campo nowrap><input type=checkbox name=selAllRighe ");
        output.append("onclick=javascript:SelezionaRighe();></td>");
      }
 **
 **
**/
  
      switch(nCodScadenzaLocal) {
        case 1:
          output.append("<td align=left class=etichetta nowrap>Filtro su soggetti in scadenza</td>");
          output.append("<td align=left class=campo nowrap><select name=FILTRO>");
          output.append("<option value=0");
          if (strListaFiltrata.equals("0")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("tutti");
          output.append("</option>");
          output.append("<option value=1");
          if (strListaFiltrata.equals("1")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("solo non trattati");
          output.append("</option>");
          output.append("</select>");
          output.append("</td>");
      
          output.append("<td align=left class=campo nowrap>");
          output.append("<input class=pulsanti type=button name=btnFiltra value=Applica ");
          output.append("onclick=javascript:FiltraNew(FILTRO);></td>");
          output.append("</td>");
          break;
      
        case 2:
          output.append("<td align=left class=etichetta nowrap>Filtro su soggetti in scadenza</td>");
          output.append("<td align=left class=campo nowrap><select name=FILTRO>");
          output.append("<option value=0");
          if (strListaFiltrata.equals("0")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("tutti");
          output.append("</option>");
          output.append("<option value=1");
          if (strListaFiltrata.equals("1")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("solo non trattati");
          output.append("</option>");
          output.append("</select>");
          output.append("</td>");
      
          output.append("<td align=left class=campo nowrap>");
          output.append("<input class=pulsanti type=button name=btnFiltra value=Applica ");
          output.append("onclick=javascript:FiltraNew(FILTRO);></td>");
          output.append("</td>");
          break;

        case 3:
          output.append("<td align=left class=etichetta nowrap>Filtro su soggetti in scadenza</td>");
          output.append("<td align=left class=campo nowrap><select name=FILTRO>");
          output.append("<option value=0");
          if (strListaFiltrata.equals("0")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("tutti");
          output.append("</option>");
          output.append("<option value=1");
          if (strListaFiltrata.equals("1")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("solo non trattati");
          output.append("</option>");
          output.append("</select>");
          output.append("</td>");
      
          output.append("<td align=left class=campo nowrap>");
          output.append("<input class=pulsanti type=button name=btnFiltra value=Applica ");
          output.append("onclick=javascript:FiltraNew(FILTRO);></td>");
          output.append("</td>");
          break;

        case 4:
          output.append("<td align=left class=etichetta nowrap>Filtro su soggetti in scadenza</td>");
          output.append("<td align=left class=campo nowrap><select name=FILTRO>");
          output.append("<option value=0");
          if (strListaFiltrata.equals("0")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("tutti");
          output.append("</option>");
          output.append("<option value=1");
          if (strListaFiltrata.equals("1")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("filtro data contatti");
          output.append("</option>");
          output.append("<option value=2");
          if (strListaFiltrata.equals("2")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("filtro data appuntamenti");
          output.append("</option>");
          output.append("</select>");
          output.append("</td>");
      
          output.append("<td align=left class=campo nowrap>");
          output.append("<input class=pulsanti type=button name=btnFiltra value=Applica ");
          output.append("onclick=javascript:FiltraNew(FILTRO);></td>");
          output.append("</td>");
          break;

        case 101:
          output.append("<td align=left class=etichetta nowrap>Filtro su soggetti in scadenza</td>");
          output.append("<td align=left class=campo nowrap><select name=FILTRO>");
          output.append("<option value=0");
          if (strListaFiltrata.equals("0")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("tutti");
          output.append("</option>");
          output.append("<option value=1");
          if (strListaFiltrata.equals("1")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("solo non trattati");
          output.append("</option>");
          output.append("</select>");
          output.append("</td>");
      
          output.append("<td align=left class=campo nowrap>");
          output.append("<input class=pulsanti type=button name=btnFiltra value=Applica ");
          output.append("onclick=javascript:FiltraNew(FILTRO);></td>");
          output.append("</td>");
          break;

        case 102:
          output.append("<td align=left class=etichetta nowrap>Filtro su soggetti in scadenza</td>");
          output.append("<td align=left class=campo nowrap><select name=FILTRO>");
          output.append("<option value=0");
          if (strListaFiltrata.equals("0")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("tutti");
          output.append("</option>");
          output.append("<option value=1");
          if (strListaFiltrata.equals("1")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("solo non trattati");
          output.append("</option>");
          output.append("</select>");
          output.append("</td>");
      
          output.append("<td align=left class=campo nowrap>");
          output.append("<input class=pulsanti type=button name=btnFiltra value=Applica ");
          output.append("onclick=javascript:FiltraNew(FILTRO);></td>");
          output.append("</td>");  
          break;

        case 103:
          output.append("<td align=left class=etichetta nowrap>Filtro su soggetti in scadenza</td>");
          output.append("<td align=left class=campo nowrap><select name=FILTRO>");
          output.append("<option value=0");
          if (strListaFiltrata.equals("0")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("tutti");
          output.append("</option>");
          output.append("<option value=1");
          if (strListaFiltrata.equals("1")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("solo non trattati");
          output.append("</option>");
          output.append("</select>");
          output.append("</td>");
      
          output.append("<td align=left class=campo nowrap>");
          output.append("<input class=pulsanti type=button name=btnFiltra value=Applica ");
          output.append("onclick=javascript:FiltraNew(FILTRO);></td>");
          output.append("</td>");
          break;

        case 104:
          output.append("<td align=left class=etichetta nowrap>Filtro su soggetti in scadenza</td>");
          output.append("<td align=left class=campo nowrap><select name=FILTRO>");
          output.append("<option value=0");
          if (strListaFiltrata.equals("0")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("tutti");
          output.append("</option>");
          output.append("<option value=1");
          if (strListaFiltrata.equals("1")) {
            output.append(" selected");
          }
          output.append(">");
          output.append("solo non trattati");
          output.append("</option>");
          output.append("</select>");
          output.append("</td>");
      
          output.append("<td align=left class=campo nowrap>");
          output.append("<input class=pulsanti type=button name=btnFiltra value=Applica ");
          output.append("onclick=javascript:FiltraNew(FILTRO);></td>");
          output.append("</td>");
          break;
      } 
      output.append("<td align=left>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
      output.append("<td align=left>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
      output.append("</tr></table>");
    }
  }
/** 
 **   Commentato il checkbox in data 27/09/2004 
 **
  else {
    if (rows_VectorScadenze.size() > 0) {
      output.append("<table>");
      output.append("<tr>");
      output.append("<td align=left class=etichetta nowrap>Seleziona tutti</td>");
      output.append("<td align=left class=campo nowrap><input type=checkbox name=selAllRighe ");
      output.append("onclick=javascript:SelezionaRighe();></td>");
      output.append("</tr></table>");
    }
  }
 **
 **
**/

  if (rows_VectorScadenze.size() > 0) {
    output.append(htmlStreamTop);
    output.append("<table class=lista align=center width=");
    output.append("100% ");
    output.append("><TR>");
    output.append("<TH class=lista>");
    output.append("&nbsp;");
    output.append("</TH>");
//
// 27/09/2004
// Per riallineare le intestazioni con l'assenza del check scarto la prima colonna
//
//    for (int i=0;i < vettIntestazioni.length;i++) {

    for (int i=1;i < vettIntestazioni.length;i++) {
      if (vettColonneVisibili[i]) {
        output.append("<TH class=lista nowrap>");
        output.append(vettIntestazioni[i]);
        output.append("</TH>");
      }
    }
    for (int i=0;i < vettIntestazioniBottoni.length;i++) {
      if (vettIntestazioniBottoni[i].compareTo("") != 0) {
        output.append("<TH class=lista nowrap>");
        output.append(vettIntestazioniBottoni[i]);
        output.append("</TH>");
      }
    }

    for (int i = 0; i < vettIntestazioniBottoniComuni.length; i++) {
      if (vettIntestazioniBottoniComuni[i].compareTo("") != 0) {
        output.append("<TH class=lista nowrap>");
        output.append(vettIntestazioniBottoniComuni[i]);
        output.append("</TH>");
      }
    }
  
    output.append("</TR>");
    String strCodiceUnitaAzienda = "";
    String colonnaAzienda="";
    String colonnaUnita="";
    for (int i = 0; i < rows_VectorScadenze.size(); i++) {
        output.append("<TR class=lista>");
        if ((i+1) % 2  == 0) {
          strNomeClasse = "lista_pari";  
        }
        else {
          strNomeClasse = "lista_dispari";
        }
        riga=(SourceBean) rows_VectorScadenze.elementAt(i);
        switch(nCodScadenzaLocal) {
        /*Lavoratori da Ricontattare*/ 
       	 case 1:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[7]);
            break;
          
          case 2:
            strCodiceUnitaAzienda = vettColonne[0].toString();
            strCdnAzienda = riga.getAttribute("codazienda").toString();
            strCdnUnita = riga.getAttribute("codunitaazienda").toString();
            strCdnKeyStampa = strCdnAzienda + "#" + strCdnUnita;
            dataScadenza = riga.getAttribute(vettColonne[8]);
            break;
            
          /*Scadenze validità della scheda lavoratori*/
          case 3:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[5]);
            break;
		  /*Scadenze dei permessi di soggiorno*/	
          case 4:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[5]);
            break;

          case 101:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[5]);
            break;

          case 102:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[5]);
            break;

          case 103:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[5]);
            break;

          case 104:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[5]);
            break;

          case 1001:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[5]);
            break;

          case 1002:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[4]);
            break;

          case 1003:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[4]);
            break;

		 case 1004:
            strCdnKey = riga.getAttribute(vettColonne[0]).toString();
            strCdnKeyStampa = strCdnKey;
            dataScadenza = riga.getAttribute(vettColonne[4]);
            break;
        
	    case 5:
	        strCdnKey = riga.getAttribute(vettColonne[0]).toString();
	        strCdnKeyStampa = strCdnKey;
	        dataScadenza = riga.getAttribute(vettColonne[4]);
	        break;
        }
        
      
        if (dataScadenza != null) {
          strDataScadenza = dataScadenza.toString();
        }

/** 
 **   Commentato il checkbox in data 27/09/2004 
 **
        output.append("<TD class=");
        output.append(strNomeClasse);
        output.append(">");
      
        output.append("<input type=checkbox name=rigaScadenza value=");
        output.append(strCdnKeyStampa);
        output.append(">");
 **
 **
**/
        output.append("</TD>");
        for (int j = 0; j < vettColonne.length; j++) {
          if (vettColonneVisibili[j]) {
            output.append("<TD class=");
            output.append(strNomeClasse);
            output.append(" NOWRAP >");
            objValueColonna = riga.getAttribute(vettColonne[j]);
            if (objValueColonna != null) {
              strValueColonna = objValueColonna.toString();
            }
            if (!vettColonneLink[j]) {
              output.append(strValueColonna);
            }
            else {
              output.append("<A HREF=\"");
              output.append(vettAzioniColonneLink[j]);
              output.append("(");
              switch(nCodScadenzaLocal) {
                case 1:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 2:
                  output.append(strCdnAzienda);
                  output.append(",");
                  output.append(strCdnUnita);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 3:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 4:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 101:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 102:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 103:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 104:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 1001:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 1002:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;

                case 1003:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;
                  
                case 1004:
                  output.append(strValueColonna);
                  output.append(");");
                  output.append("\"");
                  output.append(">");
                  break;                  
                
				case 5:
                    output.append(strValueColonna);
                    output.append(");");
                    output.append("\"");
                    output.append(">");
                  break;
              }
              if (vettIconeColonneLink[j] != "") {
                output.append("<IMG border=0 src=");
                output.append(vettIconeColonneLink[j]);
                output.append(" alt=consulta>");
              }
              else {
                output.append(strValueColonna);
              }
              output.append("</A>");
            }
            output.append("</TD>");
          }
        }
        for (int j = 0; j < vettIntestazioniBottoni.length; j++) {
          if (vettIntestazioniBottoni[j].compareTo("") != 0) {
            output.append("<TD class=");
            output.append(strNomeClasse);
            output.append(">");
            output.append("<A HREF=\"");
            output.append(vettAzioniBottoni[j]);
            output.append("(");
            switch(nCodScadenzaLocal) {
              case 1:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;
            
              case 2:
                output.append(strCdnAzienda);
                output.append(",");
                output.append(strCdnUnita);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 3:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 4:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 101:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 102:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 103:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 104:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 1001:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 1002:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;

              case 1003:
                output.append(strCdnKey);
                output.append(",'");
                output.append(strDataScadenza);
                output.append("');");
                output.append("\"");
                output.append(">");
                break;
                
              case 1004:
                output.append(strCdnKey);
                output.append(",'");
                // Savino 13/03/2006: in verifiche-soggetti pronti all'incrocio il parametro data scadenza
                //     viene utilizzato per la descrizione cpi, col rischio di avere caratteri non accettati da javascript (Es.: reggio nell'emilia) 
                output.append(StringUtils.formatValue4Javascript(strDataScadenza));
                output.append("');");
                output.append("\"");
                output.append(">");
                break;
              
				case 5:
                  output.append(strCdnKey);
                  output.append(",'");
                  output.append(strDataScadenza);
                  output.append("');");
                  output.append("\"");
                  output.append(">");
                  break;
            }
        
            if (vettIconeBottoni[j] != "") {
              output.append("<IMG border=0 src=");
              output.append(vettIconeBottoni[j]);
              output.append(" alt=");
              output.append(vettIntestazioniBottoni[j]);
              output.append(">");
            }
            output.append("</A>");
            output.append("</TD>");
          }
        }

        for (int j = 0; j < vettIntestazioniBottoniComuni.length; j++) {
          if (vettIntestazioniBottoniComuni[j].compareTo("") != 0) {
            output.append("<TD class=");
            output.append(strNomeClasse);
            output.append(">");
            output.append("<A HREF=\"");
            output.append(vettAzioniBottoniComuni[j]);
            output.append("(");
            output.append(strCdnKey);
            output.append(",'");
            output.append(codCpiLocal);
            output.append("');");
            output.append("\"");
            output.append(">");
            if (vettIconeBottoniComuni[j] != "") {
              output.append("<IMG border=0 src=");
              output.append(vettIconeBottoniComuni[j]);
              output.append(" alt=");
              output.append(vettIntestazioniBottoniComuni[j]);
              output.append(">");
            }
            output.append("</A>");
            output.append("</TD>");
          }
        } 
        output.append("</TR>");
    }
    output.append("</TABLE>");
    output.append(htmlStreamBottom);
  }
  else {
  		//se non ho risultati e l'utente è un cpi allora il messaggio sarà il seguente
  		if(isCpi){
  			output.append("<table class=lista align=center>");
    		output.append("<tr>");
    		output.append("<td align=center><b>Nessun Risultato Trovato</b></td>");
    		output.append("</tr>");
    		output.append("</table></center><br>");
    	}else{
    		//se non ho risultati e l'utente non è un cpi allora il messaggio sarà il seguente
    		output.append("<table class=lista align=center>");
    		output.append("<tr>");
    		output.append("<td align=center><b>Impossibile trovare le eventuali verifiche poichè l'utente non possiede un codice CPI associato</b></td>");
    		output.append("</tr>");
    		output.append("</table></center><br>");
    	}
    	
  }
  
  return output.toString();
}
%>
