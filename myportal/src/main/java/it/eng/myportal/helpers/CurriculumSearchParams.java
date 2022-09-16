package it.eng.myportal.helpers;

import java.io.Serializable;
import java.util.List;

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeTitolo;

/**
 *
 * @author hatemalimam
 */

public class CurriculumSearchParams implements Serializable {

	private static final long serialVersionUID = 7886517961070144947L;

	private DeTitolo deTitolo;
	private DeComune comuneDomicilio;
	private List<DeLingua> deLingua;
	private String professione;
	private String titolo;
	private boolean automunito;
	private boolean motomunito;
	
	private String orderBy;
	private Integer startFrom;
	private Integer chunkSize; 
	private boolean ascending;

	public DeTitolo getDeTitolo() {
		return deTitolo;
	}

	public void setDeTitolo(DeTitolo deTitolo) {
		this.deTitolo = deTitolo;
	}

	public List<DeLingua> getDeLingua() {
		return deLingua;
	}

	public void setDeLingua(List<DeLingua> deLingua) {
		this.deLingua = deLingua;
	}

	public String getProfessione() {
		return professione;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public void setProfessione(String professione) {
		this.professione = professione;
	}

	public boolean getAutomunito() {
		return automunito;
	}

	public void setAutomunito(boolean automunito) {
		this.automunito = automunito;
	}

	public Boolean getMotomunito() {
		return motomunito;
	}

	public void setMotomunito(Boolean motomunito) {
		this.motomunito = motomunito;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(Integer startFrom) {
		this.startFrom = startFrom;
	}

	public Integer getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public DeComune getComuneDomicilio() {
		return comuneDomicilio;
	}

	public void setComuneDomicilio(DeComune comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}

	@Override
	public String toString() {
		return "CurriculumSearchParams [comuneDomicilio=" + comuneDomicilio + ", professione=" + professione
				+ ", titolo=" + titolo + ", orderBy=" + orderBy + ", startFrom=" + startFrom + ", chunkSize="
				+ chunkSize + ", ascending=" + ascending + "]";
	}

}
