package it.eng.myportal.dtos;

import org.jfree.util.Log;

/**
 * DTO di una portlet
 * 
 * @author Menzi
 *
 */
public class PtPortletDTO extends AbstractUpdatablePkDTO implements Comparable<PtPortletDTO> {
	private static final long serialVersionUID = -9141791133772957173L;

	private Integer id;
	private String nome;
	private String descrizione;
	private String codRuoloPortale;
	// indica se la portlet deve essere visualizzata o meno
	private Boolean visualizza;
	// indica la posizione della portlet nella homepage (FIXME ancora non supportato)
	private Integer posizione;
	// indica se la portlet deve essere visualizzata in modalita' ridotta (true) o intera (false)
	private Boolean ridotta;

	private String colonna;

	public PtPortletDTO() {
		super();
	}

	public PtPortletDTO(Integer id, String nome, String descrizione, Boolean visualizza, Integer posizione,
			String colonna, Boolean ridotta) {
		super();
		this.id = id;
		this.nome = nome;
		this.descrizione = descrizione;
		this.visualizza = visualizza;
		this.posizione = posizione;
		this.ridotta = ridotta;
		this.colonna = colonna;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getCodRuoloPortale() {
		return codRuoloPortale;
	}

	public void setCodRuoloPortale(String codRuoloPortale) {
		this.codRuoloPortale = codRuoloPortale;
	}

	@Override
	public int compareTo(PtPortletDTO o) {
		if (o == null)
			return -1;
		if (this == o)
			return 0;
		int diff = this.getPosizione().compareTo(o.getPosizione());
		if (diff == 0) {
			Log.warn("Nella scrivania sono rpesenti due oggetti nella stessa posizione.");
			diff++;
		}
		return diff;
	}

	@Override
	public String toString() {
		return this.getNome() + ": " + this.getColonna() + this.getPosizione();
	}

	public Boolean getVisualizza() {
		return visualizza;
	}

	public void setVisualizza(Boolean visualizza) {
		this.visualizza = visualizza;
	}

	public Integer getPosizione() {
		return posizione;
	}

	public void setPosizione(Integer posizione) {
		this.posizione = posizione;
	}

	public Boolean getRidotta() {
		return ridotta;
	}

	public void setRidotta(Boolean ridotta) {
		this.ridotta = ridotta;
	}

	public String getColonna() {
		return colonna;
	}

	public void setColonna(String colonna) {
		this.colonna = colonna;
	}

}
