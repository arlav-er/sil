package it.eng.myportal.beans;

import javax.faces.event.ComponentSystemEvent;

public interface ICheckCliclavoro {
	
	public void checkDatiCliclavoroListener(ComponentSystemEvent event);

	/**
	 * Metodo che implementa il controllo bloccante sui dati da inviare a
	 * cliclavoro. Se il controllo ha successo l'operazione puo' procedere, se
	 * invece fallisce l'operazione non viene eseguita e viene visualizzato un
	 * messaggio di warning.
	 * 
	 * @return true se il controllo ha successo, false altrimenti.
	 */
	public boolean checkDatiCliclavoroSpecifico(ComponentSystemEvent event);

	/**
	 * Restituisce il messaggio di warning da visualizzare nel caso il controllo
	 * fallisca.
	 * 
	 * @return il messaggio di warning
	 */
	public String getCheckMessageCliclavoro();
}
