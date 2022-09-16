<script language="Javascript">
var imgChiusa = "../../img/chiuso.gif";
var imgAperta = "../../img/aperto.gif";
//funzione che funge da costruttore per gli oggetti sezione
function Sezione(sezione, img,aperta){    
    this.sezione=sezione;
    this.sezione.aperta=aperta;
    this.img=img;
}

//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa
function cambia(immagine, sezione) {
	if (sezione.style.display == 'inline') {
		sezione.style.display = 'none';
		sezione.aperta = false;
		immagine.src = imgChiusa;
    	immagine.alt = 'Apri';
	}
	else if (sezione.style.display == "none") {
		sezione.style.display = "inline";
		sezione.aperta = true;
		immagine.src = imgAperta;
    	immagine.alt = "Chiudi";
	}
}
</script>
