package it.eng.myportal.dtos;

import org.jfree.util.Log;

/**
 * DTO di una portlet posizionata sulla scrivania. Attenzione. L'id è quello del
 * record sulla pt_scrivania L'id della portlet è idPortlet
 * 
 * @author Menzi
 * 
 */
public class PtScrivaniaDTO extends AbstractUpdatablePkDTO implements IHasPrimaryKey<Integer>,
		Comparable<PtScrivaniaDTO> {

	
	private static final long serialVersionUID = 795018382740304189L;
	/**
	 * Id dell'utente a cui viene mostrata la portlet
	 */
	private Integer idPrincipal;
	private Integer idPortlet;
	private String nome;
	private String descrizione;
	private Integer posizione;
	private String colonna;
	private Boolean visualizza;
	private Boolean ridotta;

	public PtScrivaniaDTO() {
		super();
	}

	public PtScrivaniaDTO(Integer id, Integer idPrincipal, Integer idPortlet, Integer posizione,
			Boolean flagVisualizza, Boolean flagRidotta, String colonna) {
		super();
		this.id = id;
		this.idPrincipal = idPrincipal;
		this.idPortlet = idPortlet;
		this.posizione = posizione;
		this.visualizza = flagVisualizza;
		this.ridotta = flagRidotta;
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

	/**
	 * @return the idPrincipal
	 */
	public Integer getIdPrincipal() {
		return idPrincipal;
	}

	/**
	 * @return the idPortlet
	 */
	public Integer getIdPortlet() {
		return idPortlet;
	}

	/**
	 * @return the posizione
	 */
	public Integer getPosizione() {
		return posizione;
	}

	/**
	 * @return the flagVisualizza
	 */
	public Boolean getVisualizza() {
		return visualizza;
	}

	/**
	 * @return the flagRidotta
	 */
	public Boolean getRidotta() {
		return ridotta;
	}

	/**
	 * @param idPrincipal
	 *            the idPrincipal to set
	 */
	public void setIdPrincipal(Integer idPrincipal) {
		this.idPrincipal = idPrincipal;
	}

	/**
	 * @param idPortlet
	 *            the idPortlet to set
	 */
	public void setIdPortlet(Integer idPortlet) {
		this.idPortlet = idPortlet;
	}

	/**
	 * @param posizione
	 *            the posizione to set
	 */
	public void setPosizione(Integer posizione) {
		this.posizione = posizione;
	}

	/**
	 * @param flagVisualizza
	 *            the flagVisualizza to set
	 */
	public void setVisualizza(Boolean flagVisualizza) {
		this.visualizza = flagVisualizza;
	}

	/**
	 * @param flagRidotta
	 *            the flagRidotta to set
	 */
	public void setRidotta(Boolean flagRidotta) {
		this.ridotta = flagRidotta;
	}

	public String getColonna() {
		return colonna;
	}

	public void setColonna(String colonna) {
		this.colonna = colonna;
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

	/**
	 * All'interno del compare c'è un piccolo workaround per evitare problemi
	 * nel caso in cui due portlet si trovino nella stessa posizione. Se per
	 * qualche motivo arcano ciò dovesse succedere allora sposta la portlet
	 * corrente di una posizione rispetto a quella già presente enl set.
	 */
	@Override
	public int compareTo(PtScrivaniaDTO o) {
		if (o == null)
			return -1;
		if (this == o)
			return 0;
		int diff = this.getPosizione().compareTo(o.getPosizione());
		if (diff == 0) {
			Log.warn("Nella scrivania sono presenti due oggetti nella stessa posizione.");
			diff++;
		}
		return diff;
	}

	@Override
	public String toString() {
		return this.getNome() + ": " + this.getColonna() + this.getPosizione();
	}

}
