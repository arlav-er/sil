/*
 * Chiude la finestra corrente eseguendo il REFRESH della finestra genitore.
 * Mi serve perché se si è (cosa molto probabile) su di una lista, questa
 * non è effettivamente l'ULTIMA lista visitata perché prevale l'ultima visitata
 * tramite finestra di pop-up!
 */
function closePopupAndRefresh() {

	// NB: in caso di POST dalla pagina di ricerca, l'URL termina subito con "AdapterHTTP"
	// senza alcun parametro, il che manda in errore il FW. Enjoy.
	// ORA, se l'URL della pagina chiamante la pop-up è uno di quelli delle LISTE DOCUMENTI,
	// allora prima di chiudere faccio il REFRESH della pagina chiamante.
	var url = window.opener.location.href;
	var urlUp = url.toUpperCase();
	if ( (urlUp.indexOf("PAGE=DOCUMENTIASSOCIATIPAGE") >= 0) ||
	     (urlUp.indexOf("PAGE=LISTADOCUMENTIPAGE") >= 0)     ) {

		window.opener.prepareSubmit();
		window.opener.location.replace(url);	// Refresh finestra genitore
	}

	// Chiudo la finestra corrente.
	window.close();
}
