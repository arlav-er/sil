package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.IDecode;

import java.util.List;

import javax.ejb.Local;
import javax.faces.model.SelectItem;

/**
 * Interfaccia per le Home che gestiscono le tabelle di decodifica
 * @author Rodi A.
 *
 * @param <DTO>
 */
@Local
public interface IDecodeHome<DTO extends IDecode> extends IDTOHome<DTO>, IHasPrimaryKeyHome<DTO, String> {
	
	
	/**
	 * Restituisce tutti gli elementi della tabella in oggetti di tipo SelectItem,
	 * per essere utilizzati in un frontend JSF.
	 * @param addBlank booleano per aggiungere una riga bianca all'inizio delle opzioni
	 * @return lista delle opzioni all'interno di input di tipo select
	 */
	List<SelectItem> getListItems(boolean addBlank);
}
