//-------------------------------------------------------------------------------------- 
// SCRIPT COMUNI PER IL CARICAMENTO DELLE COMBOBOX	 
//--------------------------------------------------------------------------------------
//Codici di insert Flag Rapp
//(se eok=true viene inserita anche l'opzione vuota)
function insertFlagRapp(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) objCodSoc.options[i++] = new Option("","",false,false);    
   
   objCodSoc.options[i++] = new Option("S","S",false,false);
   objCodSoc.options[i++] = new Option("N","N",false,false);
}

//Codici di Ente Esecutore Intervento
//(se eok=true viene inserita anche l'opzione vuota)
function insertCodSocieta(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) objCodSoc.options[i++] = new Option("","",false,false);    
   
   objCodSoc.options[i++] = new Option("C.S.","C.S.",false,false);
   objCodSoc.options[i++] = new Option("SDM","SDM",false,false);
}

//Codici di Ente che richiede una pratica
//(se eok=true viene inserita anche l'opzione vuota)
function insertEnteRichiedente(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objCodSoc.options[i++] = new Option("","",false,false);       
   }       
   objCodSoc.options[i++] = new Option("BANCA","BANCA",false,false); 
   objCodSoc.options[i++] = new Option("C.S.","C.S.",false,false);      
}

/**********************************
***** SOSPENSIONE INTERVENTO ******
***********************************/

//Codici di Ente che rifiuta una richiesta di sospensione intervento 
//(se eok=true viene inserita anche l'opzione vuota)
function insertEnteRifRichiestaSosp(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objCodSoc.options[i++] = new Option("","",false,false);       
   }       
   objCodSoc.options[i++] = new Option("BANCA","BANCA",false,false); 
   objCodSoc.options[i++] = new Option("C.S.","C.S.",false,false);      
}

//Codici di Ente che riattiva intervento sospeso
//(se eok=true viene inserita anche l'opzione vuota)
function insertEnteRiattiva(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objCodSoc.options[i++] = new Option("","",false,false);       
   }       
   objCodSoc.options[i++] = new Option("BANCA","BANCA",false,false); 
   objCodSoc.options[i++] = new Option("C.S.","C.S.",false,false);      	
}

//Codici di Ente che Richiede Sospensione
//(se eok=true viene inserita anche l'opzione vuota)
function insertEnteRichiedenteSosp(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) objCodSoc.options[i++] = new Option("","",false,false); 
   
   objCodSoc.options[i++] = new Option("BANCA","BANCA",false,false);
   objCodSoc.options[i++] = new Option("C.S.","C.S.",false,false);   
   objCodSoc.options[i++] = new Option("SDM","SDM",false,false);
}

//Codici di Ente che sospende 
//(se eok=true viene inserita anche l'opzione vuota)
function insertEnteSosp(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objCodSoc.options[i++] = new Option("","",false,false); 
      
   }    
   objCodSoc.options[i++] = new Option("AUTOM","AUTOM",false,false); 
   objCodSoc.options[i++] = new Option("BANCA","BANCA",false,false); 
   objCodSoc.options[i++] = new Option("C.S.","C.S.",false,false);      
}

/************************************
***** USCITA A VUOTO INTERVENTO******
*************************************/

//Codici di Ente che richiede una Uscita a Vuoto
//(se eok=true viene inserita anche l'opzione vuota)
function insertEnteRichUscita(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objCodSoc.options[i++] = new Option("","",false,false);       
   }  
           	
   objCodSoc.options[i++] = new Option("BANCA","BANCA",false,false);    	
   objCodSoc.options[i++] = new Option("SDM","SDM",false,false); 
}

//Codici di Ente che accetta una Richiesta di Uscita a Vuoto
//(se eok=true viene inserita anche l'opzione vuota)
function insertEnteAcceUscita(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objCodSoc.options[i++] = new Option("","",false,false);       
   }       
   objCodSoc.options[i++] = new Option("BANCA","BANCA",false,false); 
   objCodSoc.options[i++] = new Option("C.S.","C.S.",false,false);      	
}

//Codici di Ente che Rifiuta una Uscita a Vuoto
//(se eok=true viene inserita anche l'opzione vuota)
function insertEnteRifUscita(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objCodSoc.options[i++] = new Option("","",false,false);       
   }       
   objCodSoc.options[i++] = new Option("BANCA","BANCA",false,false); 
   objCodSoc.options[i++] = new Option("C.S.","C.S.",false,false);      	
}

//Fasi pratica 
//(se eok=true viene inserita anche l'opzione vuota)
function insertFasiPratica(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) objCodSoc.options[i++] = new Option("","",false,false);          
          
   objCodSoc.options[i++] = new Option("ININSER","ININSER",false,false);
   objCodSoc.options[i++] = new Option("PRESECUZ","PRESECUZ",false,false);
   objCodSoc.options[i++] = new Option("DACOMPLET","DACOMPLET",false,false);
   objCodSoc.options[i++] = new Option("DACOMPINGE","DACOMPINGE",false,false);
   objCodSoc.options[i++] = new Option("COMPLINGE","COMPLINGE",false,false);
   objCodSoc.options[i++] = new Option("COMPLETATA","COMPLETATA",false,false);
   objCodSoc.options[i++] = new Option("CHIUSA","CHIUSA",false,false);   
}

//Modalità Operativa
//(se eok=true viene inserita anche l'opzione vuota)
function insertModalitaOperativa(objCodSoc, eok)
{
   var i = 0;
   if (eok == true) objCodSoc.options[i++] = new Option("","",false,false);          
   
   objCodSoc.options[i++] = new Option("Manuale","M",false,false);
   objCodSoc.options[i++] = new Option("Automatico","A",false,false);   
}
//check di un elemento di una combo 
function checkedSelect(objCombo,value)
{
    for (var ind=0;ind < objCombo.length;ind++)
        {
           if (objCombo.options[ind].value== value )
               objCombo.options[ind].selected=true;
        }
}

/*********************************************
 *** ASSEGNA/VISUALIZZA ATTIVITA' E GRUPPI ***
 *********************************************/
//Codici dei gruppi a cui può appartenere 
//l'oggetto relativo alla pratica in esame
//(se eok=true viene inserita anche l'opzione vuota)
function insertGruppo(objAtt, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objAtt.options[i++] = new Option("",null,true,false);       
   }  

   objAtt.options[i++] = new Option("1","1",false,false); 
   objAtt.options[i++] = new Option("2","2",false,false); 
   objAtt.options[i++] = new Option("3","3",false,false); 
   objAtt.options[i++] = new Option("4","4",false,false);          
}

//Opzioni di ordinamento della pagina Assegna/Visualizza
//attività e gruppi relative alla pratica in esame
//(se eok=true viene inserita anche l'opzione vuota)
function insertOrdinamentoPer(objAtt, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objAtt.options[i++] = new Option("",null,true,false);       
   }  

   objAtt.options[i++] = new Option("GRUPPO","GRUPPO",false,false); 
   objAtt.options[i++] = new Option("OGGETTO","OGGETTO",false,false); 
}
