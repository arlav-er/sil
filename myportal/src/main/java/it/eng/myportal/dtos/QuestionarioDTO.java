/**
 * 
 */
package it.eng.myportal.dtos;

/**
 * Classe DTO contenente i dati di una domanda del questionario
 * @author iescone
 *
 */
public class QuestionarioDTO implements IHasPrimaryKey<Integer> {

	private String domanda;
	private Integer punteggio;
	private Integer id;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDomanda() {
		return domanda;
	}

	public void setDomanda(String domanda) {
		this.domanda = domanda;
	}

	public Integer getPunteggio() {
		return punteggio;
	}

	public void setPunteggio(Integer punteggio) {
		this.punteggio = punteggio;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof QuestionarioDTO)) return false;
		 Integer otherId = ((QuestionarioDTO) o ).getId();
		if (otherId != null && this.getId() != null)
			return otherId.equals(this.getId());
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		if (this.getId() != null) return this.getId().hashCode();
		else return 0;
	}
	
}
