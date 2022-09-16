package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.ISuggestible;

import java.util.List;

import javax.ejb.Local;

/**
 * Interfaccia per le Home che gestiscono le tabelle di decodifica
 *
 * @author Rodi A.
 *
 * @param <DTO>
 */
@Local
public interface ISuggestibleHome<DTO extends ISuggestible> extends IDecodeHome<DTO> {

	/**
	 * Ogni Home implementa questo metodo per la restituzione dei
	 *
	 * @param par
	 *            termine da usare per la ricerca dei suggerimenti
	 * @return suggerimenti all'interno di campi di input autocomplete
	 */
	List<DTO> findBySuggestion(String par);

	/**
	 * Ogni Home implementa questo metodo per la restituzione dei DTO che corrispondono alla descrizione
	 * @param  desc - descrizione da ricercare
	 * @return lista elementi corrispondenti
	 */
	List<DTO> findByDescription(String desc);
}
