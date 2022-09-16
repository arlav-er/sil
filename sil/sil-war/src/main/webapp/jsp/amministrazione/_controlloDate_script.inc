
var dataOdierna = "<%= DateUtils.getNow() %>";
var Messages = new Messages();
function Messages() {
	this.Date = new DateMessages();
    this.Amministrazione = new AmministrazioneMessages();
}
function DateMessages (){
	this.ERR_CANC_INS= "Attenzione: la data di cancellazione e' antecedente alla data di inserimento";
	this.ERR_INS= "Attenzione: la data di inserimento e' successiva alla data odierna";    
	this.ERR_USCITA= "Attenzione: la data di uscita e' posteriore alla data odierna";
	this.ERR_MOTIVO_USCITA = "Selezionare un motivo di uscita";
	this.ERR_DATA_USCITA = "Specificare la data di uscita";
	this.ERR_DATA_PATTO = "La data stipula patto e' antecedente alla data della dichiarazione di immediata disponibilità";
	this.ERR_DATA_STIP_SCAD = "La data stipula patto/accordo e' posteriore alla data di scadenza";
	this.ERR_DATA_STIP_USCITA = "La data di chiusura e' antecedente la data stipula";
	this.ERR_DATA_STIP= "Attenzione: la data stipula e' posteriore alla data odierna";
	this.ERR_DATA_MOTIVO= "Selezionare un motivo di chiusura patto/accordo";
	this.ERR_DATA_MOTIVO4= "Selezionare un motivo di uscita";
	this.ERR_DATA_MOTIVO2= "Impostare la data di chiusura patto/accordo";
	this.ERR_DATA_MOTIVO3= "Impostare la data di uscita";
	this.ERR_PERMESSO_SOGGIORNO_SCAD = "Inserire la data di scadenza";
	this.ERR_PERMESSO_SOGGIORNO_STIP_SCAD = "La data di scadenza del permesso e' antecedente la data di richiesta";
	this.ERR_PRIVACY= "Attenzione: la data di autorizzazione privacy e' successiva alla data odierna";
	this.WARNING_AUTH_PRIVACY= "Vuoi concedere l'autorizzazione al trattamento dei dati personali?";	
	this.WARNING_NO_AUTH_PRIVACY= "Vuoi negare l'autorizzazione al trattamento dei dati personali?";	
    this.WARNING_INS= "La data di inserimento e' diversa dalla data odierna";
    this.WARNING_STIPULA= "La data di stipula e' antecedente la data odierna";
    this.WARNING_INS_PREC= "La data di inserimento e' antecedente la data odierna";
    
}
function AmministrazioneMessages(){
    this.ERR_CM_TIPO_DICH="Tipo dichiarazione non permessa";
    this.ERR_CM_DICH_NON_POSSIBILE="Dichiarazione non possibile";
    this.ERR_CM_TIPO_DICH_OBB="Il tipo dichiarazione 'Collocamento mirato' e' obbligatorio";
}
function toDate(newDate) {
    var tokens = newDate.split('/');
    var usDate= tokens[2]+"/"+tokens[1]+"/"+tokens[0];
    return new Date(usDate);
}
// date in formato 'dd/mm/yyyy'
function isFuture(dateText) {
	return compDate(dateText, dataOdierna)==1;
}
// date in formato 'dd/mm/yyyy'
function isNow(dateText) {
	return compDate(dateText, dataOdierna)==0;
}
// date in formato 'dd/mm/yyyy'
function isOld(dateText) {
	return compDate(dateText, dataOdierna)==-1;
}

// date in formato 'dd/mm/yyyy'
function compDate(date1,date2) {
    if (date1==null || date1=="") return -1;    
    if (date2==null || date2=="") return 1;
    if ( toDate(date1).getTime()<toDate(date2).getTime()) return -1;
    if ( toDate(date1).getTime()>toDate(date2).getTime()) return 1;
    //if ( toDate(date1).getTime()==toDate(date2).getTime()) 
    return 0;
}

function maggioreDiUno(date1, date2, years) {
	var token1 = date1.split("/");
	var token2 = date2.split("/");
	var aa1 = token1[2];
	var gg1 = token1[0];
	var mm1 = token1[1];
	var aa2 = token2[2];
	var gg2 = token2[0];
	var mm2 = token2[1];	
	if ((parseInt(aa1,10)-parseInt(aa2,10))>=years){
		if ((parseInt(aa1,10)-parseInt(aa2,10))==years) {
			if ((parseInt(mm1,10)-parseInt(mm2,10))>0) return true;
			else if ((parseInt(mm1,10)-parseInt(mm2,10))==0) {
				if ((parseInt(gg1,10)-parseInt(gg2,10))>0) 
					return true;	
			}
		}
		else return true;		
	}
	return false;
}
function alertDateFuture(date0, dateName) {
	if (isFuture(date0)) {
        alert("La "+dateName +" e' posteriore alla data odierna");
        return false;
    }
    else return true;
}
