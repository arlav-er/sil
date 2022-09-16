package it.eng.sil.myaccount.model.entity.myportal;

import it.eng.sil.mycas.model.entity.BaseTabellaGestioneEntity;

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

import org.hibernate.annotations.Type;

@Entity
@Table(name = "pt_scrivania", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findPtScrivaniaByIdPfPrincipal", query = "SELECT pt FROM PtScrivania pt WHERE pt.idPfPrincipal = :idPfPrincipal") })
public class PtScrivania extends BaseTabellaGestioneEntity implements java.io.Serializable {
	private static final long serialVersionUID = -6119605707804026473L;

	private Integer idPtScrivania;
	private PtPortlet ptPortlet;
	private Integer idPfPrincipal;
	private Integer posizione;
	private String optColonna;
	private Boolean flagVisualizza;
	private Boolean flagRidotta;

	@Id
	@SequenceGenerator(name = "pt_scrivania_id_pt_scrivania_seq", sequenceName = "pt_scrivania_id_pt_scrivania_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pt_scrivania_id_pt_scrivania_seq")
	@Column(name = "id_pt_scrivania", unique = true, nullable = false)
	public Integer getIdPtScrivania() {
		return idPtScrivania;
	}

	public void setIdPtScrivania(Integer idPtScrivania) {
		this.idPtScrivania = idPtScrivania;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pt_portlet", nullable = false)
	public PtPortlet getPtPortlet() {
		return ptPortlet;
	}

	public void setPtPortlet(PtPortlet ptPortlet) {
		this.ptPortlet = ptPortlet;
	}

	@Column(nullable = false)
	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	@Column(nullable = false)
	public Integer getPosizione() {
		return posizione;
	}

	public void setPosizione(Integer posizione) {
		this.posizione = posizione;
	}

	@Column(length = 1)
	public String getOptColonna() {
		return optColonna;
	}

	public void setOptColonna(String optColonna) {
		this.optColonna = optColonna;
	}

	@Type(type = "yes_no")
	@Column(nullable = false, length = 1)
	public Boolean getFlagVisualizza() {
		return flagVisualizza;
	}

	public void setFlagVisualizza(Boolean flagVisualizza) {
		this.flagVisualizza = flagVisualizza;
	}

	@Type(type = "yes_no")
	@Column(nullable = false, length = 1)
	public Boolean getFlagRidotta() {
		return flagRidotta;
	}

	public void setFlagRidotta(Boolean flagRidotta) {
		this.flagRidotta = flagRidotta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idPtScrivania == null) ? 0 : idPtScrivania.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PtScrivania other = (PtScrivania) obj;
		if (idPtScrivania == null) {
			if (other.idPtScrivania != null)
				return false;
		} else if (!idPtScrivania.equals(other.idPtScrivania))
			return false;
		return true;
	}
}
