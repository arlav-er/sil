package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.dtos.IDTO;
import it.eng.myportal.dtos.IHasUniqueValue;

/**
 *
 * Classe per il confronto requisiti; associo il dato richiesto nella vacanzy col dato corrispondente nel cv
 *
 * @author girotti
 *
 * @param <Va>
 *            dto della vacancy
 * @param <Cv>
 *            dto del cv
 */
public class VaCvContainerDTO<Va extends IHasUniqueValue, Cv extends IHasUniqueValue> implements IDTO {
	
	private static final long serialVersionUID = -4797463277669366904L;

	private Cv cvDto;

	private Va vaDto;

	public Cv getCvDto() {
		return cvDto;
	}

	public Va getVaDto() {
		return vaDto;
	}

	public void setCvDto(Cv cvDto) {
		this.cvDto = cvDto;

	}

	public void setVaDto(Va vaDto) {
		this.vaDto = vaDto;
	}

}