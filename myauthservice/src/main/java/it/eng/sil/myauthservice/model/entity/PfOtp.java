package it.eng.sil.myauthservice.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import it.eng.sil.base.enums.OTPRequestEnum;
import it.eng.sil.mycas.model.entity.BaseTabellaGestioneEntity;

/**
 * The persistent class for the pf_otp database table.
 * 
 */
@Entity
@Table(name = "pf_otp", schema = "mycas")
@NamedQueries({ @NamedQuery(name = "PfOtp.findAll", query = "SELECT p FROM PfOtp p"),
		@NamedQuery(name = "PfOtp.findByRequestCode", query = "SELECT p FROM PfOtp p WHERE p.otpReqCode = :reqCode") })

public class PfOtp extends BaseTabellaGestioneEntity {

	private static final long serialVersionUID = -7474597884102956573L;

	@Id
	@SequenceGenerator(name = "pf_otp_id_pf_otp_seq", sequenceName = "mycas.pf_otp_id_pf_otp_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pf_otp_id_pf_otp_seq")
	@Generated(GenerationTime.INSERT)
	@Column(name = "id_pf_otp", unique = true, nullable = false)
	private Integer idPfOtp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_scadenza", nullable = false)
	private Date dataScadenza;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validazione")
	private Date dataValidazione;

	private String descrizione;

	@Column(name = "otp_req_code", nullable = false)
	private String otpReqCode;

	@Column(name = "otp_secret")
	private String otpSecret;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_otp", nullable = false)
	private OTPRequestEnum tipoOtp;

	public PfOtp() {
	}

	public Integer getIdPfOtp() {
		return this.idPfOtp;
	}

	public void setIdPfOtp(Integer idPfOtp) {
		this.idPfOtp = idPfOtp;
	}

	public Date getDataScadenza() {
		return this.dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getOtpReqCode() {
		return this.otpReqCode;
	}

	public void setOtpReqCode(String otpReqCode) {
		this.otpReqCode = otpReqCode;
	}

	public String getOtpSecret() {
		return this.otpSecret;
	}

	public void setOtpSecret(String otpSecret) {
		this.otpSecret = otpSecret;
	}

	public OTPRequestEnum getTipoOtp() {
		return this.tipoOtp;
	}

	public void setTipoOtp(OTPRequestEnum tipoOtp) {
		this.tipoOtp = tipoOtp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idPfOtp == null) ? 0 : idPfOtp.hashCode());
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
		PfOtp other = (PfOtp) obj;
		if (idPfOtp == null) {
			if (other.idPfOtp != null)
				return false;
		} else if (!idPfOtp.equals(other.idPfOtp))
			return false;
		return true;
	}

	public Date getDataValidazione() {
		return dataValidazione;
	}

	public void setDataValidazione(Date dataValidazione) {
		this.dataValidazione = dataValidazione;
	}

}