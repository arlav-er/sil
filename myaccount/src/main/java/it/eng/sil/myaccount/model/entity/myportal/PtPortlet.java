package it.eng.sil.myaccount.model.entity.myportal;

import it.eng.sil.mycas.model.entity.BaseTabellaGestioneEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "pt_portlet", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findPtPortletByCodRuoloPortale", query = "SELECT pt FROM PtPortlet pt WHERE codRuoloPortale = :codRuoloPortale") })
public class PtPortlet extends BaseTabellaGestioneEntity implements java.io.Serializable {
	private static final long serialVersionUID = 920750204972064291L;

	private Integer idPtPortlet;
	private String nome;
	private String descrizione;
	private String codRuoloPortale;

	@Id
	@SequenceGenerator(name = "pt_portlet_id_pt_portlet_seq", sequenceName = "pt_portlet_id_pt_portlet_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pt_portlet_id_pt_portlet_seq")
	@Column(name = "id_pt_portlet", unique = true, nullable = false)
	public Integer getIdPtPortlet() {
		return idPtPortlet;
	}

	public void setIdPtPortlet(Integer idPtPortlet) {
		this.idPtPortlet = idPtPortlet;
	}

	@Column(length = 100, nullable = false)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(length = 1000)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(length = 20)
	public String getCodRuoloPortale() {
		return codRuoloPortale;
	}

	public void setCodRuoloPortale(String codRuoloPortale) {
		this.codRuoloPortale = codRuoloPortale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idPtPortlet == null) ? 0 : idPtPortlet.hashCode());
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
		PtPortlet other = (PtPortlet) obj;
		if (idPtPortlet == null) {
			if (other.idPtPortlet != null)
				return false;
		} else if (!idPtPortlet.equals(other.idPtPortlet))
			return false;
		return true;
	}
}
