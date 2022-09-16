package it.eng.myportal.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * AppValutazione entity
 */
@Entity
@Table(name = "app_valutazione", schema = "myportal")

@NamedQueries({
		@NamedQuery(name = "appValutazione.getValutazioni", query = "select n from AppValutazione n order by n.idAppValutazione desc"),
		@NamedQuery(name = "appValutazione.getCountValutazioni", query = "select count(n.id) from AppValutazione n") })
public class AppValutazione extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = 8603199121508564259L;

	private Integer idAppValutazione;
	private Short numStelle;
	private String messaggio;
	private PfPrincipal pfPrincipalMitt;

	@Id
	@SequenceGenerator(name = "app_valutazione_id_app_valutazione_seq", sequenceName = "app_valutazione_id_app_valutazione_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_valutazione_id_app_valutazione_seq")
	@Column(name = "id_app_valutazione", unique = true, nullable = false)
	public Integer getIdAppValutazione() {
		return idAppValutazione;
	}

	public void setIdAppValutazione(Integer idAppValutazione) {
		this.idAppValutazione = idAppValutazione;
	}

	@Column(name = "num_stelle", nullable = true)
	public Short getNumStelle() {
		return numStelle;
	}

	public void setNumStelle(Short numStelle) {
		this.numStelle = numStelle;
	}

	@Column(name = "messaggio", nullable = true, length = 1000)
	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_principal_mitt", nullable = true)
	public PfPrincipal getPfPrincipalMitt() {
		return pfPrincipalMitt;
	}

	public void setPfPrincipalMitt(PfPrincipal pfPrincipalMitt) {
		this.pfPrincipalMitt = pfPrincipalMitt;
	}

	public AppValutazione() {
	}
}
