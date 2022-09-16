package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.ITreeable;

import java.util.List;

import javax.ejb.Local;

/**
 * Interfaccia per le Home che gestiscono le tabelle di decodifica
 * @author Rodi A.
 *
 * @param <DTO>
 */
@Local
public interface ITreeableHome<DTO extends ITreeable> extends ISuggestibleHome<DTO> {
	
	/**
	 * 
	 * @param par
	 * @return
	 */
	List<DTO> findByCodPadre(String par);
	
	
	
}
