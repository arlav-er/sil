package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.QuestionarioDTO;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object per l'entity Questionario
 * Estende una classe astratta per avere buona parte dei metodi già implementati.
 * 
 * @see it.eng.myportal.entity.Questionario
 * @author aiescone.
 */
@Stateless
public class QuestionarioHome {

	protected final Log log = LogFactory.getLog(this.getClass());

	/**
	 * Elenco domande del questionario
	 */
	List<QuestionarioDTO> domande;
	
	/**
	 * Restituisce tutte le righe della tabella corrispondente all'entity concreta che invoca il metodo.
	 * 
	 * @return tutte le righe in tabella
	 */
	public List<QuestionarioDTO> findAll() {

		domande = new ArrayList<QuestionarioDTO>();
		QuestionarioDTO dto = new QuestionarioDTO();
		dto.setId(1);
		dto.setPunteggio(1);
		dto.setDomanda("Ti piacerebbe lavorare in autonomia o in un gruppo di lavoro ?");
		domande.add(dto);
		
		dto = new QuestionarioDTO();
		dto.setId(2);
		dto.setPunteggio(3);
		dto.setDomanda("Ti piacerebbe lavorare nell'industria o nel commercio ?");
		domande.add(dto);
		
		dto = new QuestionarioDTO();
		dto.setId(3);
		dto.setPunteggio(0);
		dto.setDomanda("Ti piacerebbe lavorare con il legno ?");
		domande.add(dto);
		
		dto = new QuestionarioDTO();
		dto.setId(4);
		dto.setPunteggio(2);
		dto.setDomanda("Ti piacerebbe lavorare con tessuti, pellicce o cuoio ?");
		domande.add(dto);
		
		dto = new QuestionarioDTO();
		dto.setId(5);
		dto.setPunteggio(3);
		dto.setDomanda("Ti piacerebbe lavorare con sostanze chimiche ?");
		domande.add(dto);
		
		dto = new QuestionarioDTO();
		dto.setId(6);
		dto.setPunteggio(5);
		dto.setDomanda("Ti piacerebbe lavorare col vetro o la ceramica ?");
		domande.add(dto);
		
		return domande;
	}

	public List<QuestionarioDTO> getDomande() {
		return domande;
	}

	public void setDomande(List<QuestionarioDTO> domande) {
		this.domande = domande;
	}
	
}
