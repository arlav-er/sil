<script type="text/javascript">
var opened;
var imgChiusa = "../../img/chiuso.gif";
var imgAperta = "../../img/aperto.gif";
    
function apriSelezionaSoggetto(soggetto, funzionediaggiornamento, prgAzienda, prgUnita, cdnLavoratore) {
	var f = "AdapterHTTP?PAGE=MovimentiSelezionaSoggettoPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento + "&CDNFUNZIONE=<%=_funzione%>";
    if (prgAzienda != '' && (typeof prgAzienda) != "undefined") {f = f + "&PRGAZ=" + prgAzienda;}
    if (prgUnita != '' && (typeof prgUnita) != "undefined") {f = f + "&PRGUAZ=" + prgUnita;}
    if (cdnLavoratore != '' && (typeof cdnLavoratore) != "undefined") {f = f + "&CDNLAV=" + cdnLavoratore;}
    var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
    opened = window.open(f, t, feat);
}

//funzione che funge da costruttore per gli oggetti sezione
function Sezione(sezione, img,aperta){    
    this.sezione=sezione;
    this.sezione.aperta=aperta;
    this.img=img;
}

//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa
function cambia(immagine, sezione) {
	if (sezione.style.display == '') {
		sezione.style.display = 'none';
		sezione.aperta = false;
		immagine.src = imgChiusa;
    	immagine.alt = 'Apri';
	}
	else if (sezione.style.display == "none") {
		sezione.style.display = "";
		sezione.aperta = true;
		immagine.src = imgAperta;
    	immagine.alt = "Chiudi";
	}
}

function cambiaLavMC(elem,stato){
  divVar = document.getElementById(elem);
  divVar.style.display = stato;
}

//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa per l'azienda, lav e mov
function cambiaTendina(immagine,sezione,campo) {
	if (immagine.alt == "Apri"){
    	if ((campo.value != "") || (campo.name == "datcomunicazioneda")){
      	//apri
      		cambiaLavMC(sezione,"");
    	}
    	immagine.src=imgAperta;
    	immagine.alt="Chiudi";
  	} else {
    	//chiudi
    	cambiaLavMC(sezione,"none");
    	immagine.src=imgChiusa;
    	immagine.alt="Apri";
  }
}
</script>
