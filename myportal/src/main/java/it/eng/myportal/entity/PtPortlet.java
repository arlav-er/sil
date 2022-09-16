package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.decodifiche.DeRuoloPortale;

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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "pt_portlet", schema = "myportal", uniqueConstraints = { @UniqueConstraint(columnNames = { "nome",
		"cod_ruolo_portale" }) })
@NamedQueries({ @NamedQuery(name = "findPtPortletByCodRuoloPortale", query = "SELECT pt FROM PtPortlet pt WHERE pt.deRuoloPortale.codRuoloPortale = :codRuoloPortale") })
public class PtPortlet extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 364088L;

	private Integer idPtPortlet;
	private String nome;
	private String descrizione;
	private DeRuoloPortale deRuoloPortale;

	public PtPortlet() {
	}

	@Id
	@SequenceGenerator(name = "pt_portlet_id_pt_portlet_seq", sequenceName = "pt_portlet_id_pt_portlet_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pt_portlet_id_pt_portlet_seq")
	@Column(name = "id_pt_portlet", unique = true, nullable = false)
	public Integer getIdPtPortlet() {
		return this.idPtPortlet;
	}

	public void setIdPtPortlet(Integer idPtPortlet) {
		this.idPtPortlet = idPtPortlet;
	}

	@Column(name = "nome", nullable = false, length = 100)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "descrizione", length = 1000)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_ruolo_portale", nullable = false)
	public DeRuoloPortale getDeRuoloPortale() {
		return deRuoloPortale;
	}

	public void setDeRuoloPortale(DeRuoloPortale deRuoloPortale) {
		this.deRuoloPortale = deRuoloPortale;
	}
}
