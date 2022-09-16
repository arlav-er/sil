<% 
  String strTestataMovimento = "In corso";
  try {
	  //La data di oggi (dd/MM/yyyy)
	  GregorianCalendar o = new GregorianCalendar();
	  o.setTime(new Date());
	  GregorianCalendar oggi = new GregorianCalendar(o.get(GregorianCalendar.YEAR), 
	  												 o.get(GregorianCalendar.MONTH), 
	  												 o.get(GregorianCalendar.DAY_OF_MONTH));
	  //Se ho la data di inizio guardo se è futura 
	  if (datInizioMov != null && !datInizioMov.equals("")) {
	  	GregorianCalendar i = new GregorianCalendar();
	  	i.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(datInizioMov));
	  	GregorianCalendar inizio = new GregorianCalendar(i.get(GregorianCalendar.YEAR), 
	  												 i.get(GregorianCalendar.MONTH), 
	  												 i.get(GregorianCalendar.DAY_OF_MONTH));   	
		//Controllo se la data di inizio è futura
		if (inizio.after(oggi)){strTestataMovimento = "Futuro";}
		else {
			//Altrimenti controllo se ho un movimento successivo
			if (codMonoTipoFine != null && !codMonoTipoFine.equals("")) {
				
				//Decodifico il tipo di movimento e creo la dicitura
				if (codMonoTipoFine.equalsIgnoreCase("P")) {
					strTestataMovimento = "Prorogato";
				} else   if (codMonoTipoFine.equalsIgnoreCase("T")) {
					strTestataMovimento = "Trasformato";  
				} else   if (codMonoTipoFine.equalsIgnoreCase("C")) {
					strTestataMovimento = "Cessato";   
				} else throw new Exception();
				
				//Inserisco la data di fine se c'è
				if (datFineMovEff != null &&  !datFineMovEff.equals("")) {
					//Scelgo la data a seconda del tipo di movimento successivo
					if (codMonoTipoFine.equalsIgnoreCase("P") || codMonoTipoFine.equalsIgnoreCase("T")) {
						//Aggiungo un giorno alla data di fine effettiva
					  	GregorianCalendar fineEffettiva = new GregorianCalendar();
					  	fineEffettiva.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(datFineMovEff));
					  	fineEffettiva.set(GregorianCalendar.DAY_OF_MONTH, 
					  					fineEffettiva.get(GregorianCalendar.DAY_OF_MONTH) + 1);
					  	strTestataMovimento = strTestataMovimento + "</strong> in data <strong>"
					  					+ fineEffettiva.get(GregorianCalendar.DAY_OF_MONTH) + "/"
					  					+ (fineEffettiva.get(GregorianCalendar.MONTH) + 1) + "/"
					  					+ fineEffettiva.get(GregorianCalendar.YEAR);
					} else if (codMonoTipoFine.equalsIgnoreCase("C")) {
						strTestataMovimento = strTestataMovimento + "</strong> in data <strong>" + datFineMovEff;		
					}
				}	
						
			} else {
			//Non ho un tipo di movimento successivo
				//controllo il tipo di movimento corrente
				if (codTipoMov != null && 
					(codTipoMov.equalsIgnoreCase("AVV") || 
					 codTipoMov.equalsIgnoreCase("TRA") || 
					 codTipoMov.equalsIgnoreCase("PRO"))) {
					//Controllo il tipo di tempo
					if (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("D")) {
						//Controllo se ho la data fine
						if (datFineMov != null && !datFineMov.equals("")) {
							//Controllo se sono dopo il periodo di validità
						  	GregorianCalendar f = new GregorianCalendar();
						  	f.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(datFineMov));
						  	GregorianCalendar fine = new GregorianCalendar(f.get(GregorianCalendar.YEAR), 
						  												 f.get(GregorianCalendar.MONTH), 
						  												 f.get(GregorianCalendar.DAY_OF_MONTH));
						  	if (oggi.after(fine)) {
						  		strTestataMovimento = "Scaduto</strong> in data <strong>" + datFineMov;
						  	}							
						}
					} else if (codMonoTempo != null && !(codMonoTempo.equalsIgnoreCase("I") || codMonoTempo.equals(""))) throw new Exception();
				} else if (codTipoMov != null && codTipoMov.equalsIgnoreCase("CES")) {
					//Controllo se la cessazione è precedente
					if (inizio.before(oggi)) {
						strTestataMovimento = "Cessazione</strong> in data <strong>" + datInizioMov;
					} else strTestataMovimento = "Futuro";
				} else if (codTipoMov != null && !codTipoMov.equals("")) throw new Exception();
			}
	    } 
	  }
  } catch (Exception e) {
  	strTestataMovimento = "";
  }
%>
  <tr>
    <td colspan="8" class="etichetta">Stato del movimento: <strong><%=strTestataMovimento%></strong></td>
  </tr>          
  <tr>
    <td colspan="8" class="etichetta" id="infoAvv">Inf. Valide in fase di avviamento: 
        <% String tempoAvv = "";
           if ( codTipoMov.equalsIgnoreCase("AVV") ) {
              tempoAvv = codMonoTempo;
           } else {
              tempoAvv = codMonoTempoAvv;
           }
           if (!codTipoAss.equalsIgnoreCase("")) {
             if (!tempoAvv.equalsIgnoreCase("")) {
              if (tempoAvv.equalsIgnoreCase("D")) {%>
                 Rapporto a <strong>TD</strong> di tipo <strong><%=codTipoAss%></strong>
            <%} else if (tempoAvv.equalsIgnoreCase("I")){%>
                 Rapporto a <strong>TI</strong> di tipo <strong><%=codTipoAss%></strong>
            <%} else {//Questa condizione nn si dovrebbe mai verificare... ma la vita è piena di sorprese %>
                 Rapporto a <strong><%=codMonoTempoAvv%></strong> di tipo <strong><%=codTipoAss%></strong>
            <%}
             } else {%>
                 Rapporto di tipo <strong><%=codTipoAss%></strong>
           <%}%>
        <%} else if (!dataInizioAvv.equalsIgnoreCase("")) {%>Rapporto<%}%>
        <%if (!dataInizioAvv.equalsIgnoreCase("")) {%>
          iniziato il <strong><%=dataInizioAvv%></strong>
          
        <%}%>
    </td>
  </tr>