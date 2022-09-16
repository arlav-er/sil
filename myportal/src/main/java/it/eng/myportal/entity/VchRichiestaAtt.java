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

@Entity
@Table(name="vch_richiesta_att", schema="myportal")
@NamedQueries({
//		@NamedQuery(name="findVchRichiestaAttByUserId", query="select vra from VchRichiestaAtt vra WHERE vra.pfPrincipalIns.idPfPrincipal = :userId")
		@NamedQuery(name="findVchRichiestaAttByUserIdAndCodRisultato", query="select vra from VchRichiestaAtt vra WHERE vra.pfPrincipalIns.idPfPrincipal = :userId and vra.deWsVchEsito.codRisultato = '0'")
})
public class VchRichiestaAtt extends AbstractEntity{

	
	private static final long serialVersionUID = 1L;
	private Integer idVchRichiestaAtt;
	private Date dtRichiesta;
	private String codiceFiscaleCitt;
	private VchEntiAccreditati vchEntiAccreditati;
	private Integer codAttivazione;
	private DeWsVchEsito deWsVchEsito;
	private String erroreNonCodificato;
	
	//Contructor
	public VchRichiestaAtt(){
		super();
	}

	//Getter and setter
	@Id
	@Column(columnDefinition = "serial", name = "id_vch_richiesta_att", unique = true, nullable = false)
	@SequenceGenerator(name = "VchRichiestaAttSequence", sequenceName = "vch_richiesta_att_id_vch_richiesta_att_seq", schema = "myportal", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VchRichiestaAttSequence")
	public Integer getIdVchRichiestaAtt() {
		return idVchRichiestaAtt;
	}

	public void setIdVchRichiestaAtt(Integer idVchRichiestaAtt) {
		this.idVchRichiestaAtt = idVchRichiestaAtt;
	}

	@Column(name="dt_richiesta", nullable=false)
	@Temporal(TemporalType.DATE)
	public Date getDtRichiesta() {
		return dtRichiesta;
	}

	public void setDtRichiesta(Date dtRichiesta) {
		this.dtRichiesta = dtRichiesta;
	}

	@Column(name="codice_fiscale_citt", length=16, nullable=false)
	public String getCodiceFiscaleCitt() {
		return codiceFiscaleCitt;
	}

	public void setCodiceFiscaleCitt(String codiceFiscaleCitt) {
		this.codiceFiscaleCitt = codiceFiscaleCitt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vch_enti_accreditati", nullable = false, columnDefinition = "integer")
	public VchEntiAccreditati getVchEntiAccreditati() {
		return vchEntiAccreditati;
	}
	
	public void setVchEntiAccreditati(VchEntiAccreditati vchEntiAccreditati) {
		this.vchEntiAccreditati = vchEntiAccreditati;
	}

	@Column(name="cod_attivazione", nullable=false)
	public Integer getCodAttivazione() {
		return codAttivazione;
	}

	public void setCodAttivazione(Integer codAttivazione) {
		this.codAttivazione = codAttivazione;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="cod_risultato", nullable=false)
	public DeWsVchEsito getDeWsVchEsito() {
		return deWsVchEsito;
	}

	public void setDeWsVchEsito(DeWsVchEsito deWsVchEsito) {
		this.deWsVchEsito = deWsVchEsito;
	}

	@Column(name="errore_non_codificato", length=3)
	public String getErroreNonCodificato() {
		return erroreNonCodificato;
	}

	public void setErroreNonCodificato(String erroreNonCodificato) {
		this.erroreNonCodificato = erroreNonCodificato;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idVchRichiestaAtt == null) ? 0 : idVchRichiestaAtt.hashCode());
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
		VchRichiestaAtt other = (VchRichiestaAtt) obj;
		if (idVchRichiestaAtt == null) {
			if (other.idVchRichiestaAtt != null)
				return false;
		} else if (!idVchRichiestaAtt.equals(other.idVchRichiestaAtt))
			return false;
		return true;
	}

}
