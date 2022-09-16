function confermaArchiviazione() {
	var msg = "Il movimento sarà archiviato in una tabella di appoggio.\n" +
	 "L'archiviazione non recepisce le eventuali modifiche effettuate in maschera.\n" +
	 "Si desidera procedere con l'operazione?";
	if (confirm(msg)) {
		return true;
	}
	else {
		return false;
	}
}
