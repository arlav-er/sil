package it.eng.myportal.entity;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.myportal.entity.decodifiche.DeComune;


@Entity
@Table(name = "did_conferma_prenota", schema = "myportal")
@NamedQueries({
	@NamedQuery(name = "findConfermaDidByIfPfPrincipal", query = "select y from DidConfermaPrenota y where y.pfPrincipal.idPfPrincipal = :id_pf_principal")
	})
public class DidConfermaPrenota extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 90883L;

	private Integer idDidConfermaPrenota;
	private PfPrincipal pfPrincipal;
	
	private Date dtConferma;
	private String codiceFiscale;
	private DeComune deComune;
	
	public DidConfermaPrenota() {
	}
	
	@Id
	@SequenceGenerator(name = "did_conferma_prenota_id_did_conferma_prenota_seq", sequenceName = "did_conferma_prenota_id_did_conferma_prenota_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "did_conferma_prenota_id_did_conferma_prenota_seq")
	@Column(name = "id_did_conferma_prenota", unique = true, nullable = false)
	public Integer getIdDidConfermaPrenota() {
		return this.idDidConfermaPrenota;
	}

	public void setIdDidConfermaPrenota(Integer idDidConfermaPrenota) {
		this.idDidConfermaPrenota = idDidConfermaPrenota;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}
	

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_conferma", length = 13, nullable = false)
	public Date getDtConferma() {
		return dtConferma;
	}

	public void setDtConferma(Date dtConferma) {
		this.dtConferma = dtConferma;
	}


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_com_domicilio")
	public DeComune getDeComune() {
		return this.deComune;
	}
	public void setDeComune(DeComune deComune) {
		this.deComune = deComune;
	}

	

    @Column(name = "codice_fiscale")
    public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}


	@Override
	public String toString() {
		return "DidConfermaPrenota [idDidConfermaPrenota=" + idDidConfermaPrenota ;
	}

	public boolean equals(Object obj) {
		if (obj == null || this.getIdDidConfermaPrenota() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DidConfermaPrenota)) {
			return false;
		} else {
			return this.getIdDidConfermaPrenota().equals(((DidConfermaPrenota) obj).getIdDidConfermaPrenota());
		}
	}

	@Override
	public int hashCode() {
		return getIdDidConfermaPrenota().hashCode();
	}
}
