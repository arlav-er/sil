package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.DeQualificaSrq;
import it.eng.myportal.entity.decodifiche.min.DeAttivitaMin;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "yg_adesione_azienda", schema = "myportal")
@NamedQuery(name = "findYgAdesioneAziendaByPfPrincipal", query = "select y from YgAdesioneAzienda y where y.pfPrincipal.idPfPrincipal = :id_pf_principal")
public class YgAdesioneAzienda extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 3305506115081893343L;

	private Integer idYgAdesioneAzienda;
	private PfPrincipal pfPrincipal;
	private Boolean flgAdesione;
	private Date dtAdesione;
	private DeQualificaSrq deQualificaSrq;
	private DeAttivitaMin deAttivitaMin;
	private List<YgAdesioneAziendaSede> ygAdesioneAziendaSedeList;

	private String email1;
	private String email2;
	private String email3;

	@Id
	@SequenceGenerator(name = "yg_adesione_azienda_id_yg_adesione_azienda_seq", sequenceName = "yg_adesione_azienda_id_yg_adesione_azienda_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_adesione_azienda_id_yg_adesione_azienda_seq")
	@Column(name = "id_yg_adesione_azienda", unique = true, nullable = false)
	public Integer getIdYgAdesioneAzienda() {
		return idYgAdesioneAzienda;
	}

	public void setIdYgAdesioneAzienda(Integer idYgAdesioneAzienda) {
		this.idYgAdesioneAzienda = idYgAdesioneAzienda;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_adesione", length = 1, nullable = false)
	public Boolean getFlgAdesione() {
		return flgAdesione;
	}

	public void setFlgAdesione(Boolean flgAdesione) {
		this.flgAdesione = flgAdesione;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_adesione", nullable = true, length = 13)
	public Date getDtAdesione() {
		return dtAdesione;
	}

	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_qualifica_srq")
	public DeQualificaSrq getDeQualificaSrq() {
		return deQualificaSrq;
	}

	public void setDeQualificaSrq(DeQualificaSrq deQualificaSrq) {
		this.deQualificaSrq = deQualificaSrq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_attivita_min", nullable = false)
	public DeAttivitaMin getDeAttivitaMin() {
		return deAttivitaMin;
	}

	public void setDeAttivitaMin(DeAttivitaMin deAttivitaMin) {
		this.deAttivitaMin = deAttivitaMin;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ygAdesioneAzienda", cascade = CascadeType.ALL)
	public List<YgAdesioneAziendaSede> getYgAdesioneAziendaSedeList() {
		return ygAdesioneAziendaSedeList;
	}

	public void setYgAdesioneAziendaSedeList(List<YgAdesioneAziendaSede> ygAdesioneAziendaSedeList) {
		this.ygAdesioneAziendaSedeList = ygAdesioneAziendaSedeList;
	}

	@Column(name = "email_1", nullable = false, length = 255)
	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	@Column(name = "email_2", nullable = true, length = 255)
	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	@Column(name = "email_3", nullable = true, length = 255)
	public String getEmail3() {
		return email3;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idYgAdesioneAzienda == null) ? 0 : idYgAdesioneAzienda.hashCode());
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
		YgAdesioneAzienda other = (YgAdesioneAzienda) obj;
		if (idYgAdesioneAzienda == null) {
			if (other.idYgAdesioneAzienda != null)
				return false;
		} else if (!idYgAdesioneAzienda.equals(other.idYgAdesioneAzienda))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "YgAdesioneAzienda [idYgAdesioneAzienda=" + idYgAdesioneAzienda + ", pfPrincipal=" + pfPrincipal
				+ ", flgAdesione=" + flgAdesione + ", dtAdesione=" + dtAdesione + ", deQualificaSrq=" + deQualificaSrq
				+ ", deAttivitaMin=" + deAttivitaMin + "]";
	}

}
