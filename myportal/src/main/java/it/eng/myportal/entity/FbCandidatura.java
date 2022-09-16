package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "fb_candidatura", schema = "myportal")
public class FbCandidatura extends AbstractEntity {
	private static final long serialVersionUID = -4545092857879947019L;

	private Integer idFbCandidatura;
	private FbSchedaFabbisogno fbSchedaFabbisogno;
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String email;
	private boolean flagIdoneo;

	@Id
	@SequenceGenerator(name = "fb_candidatura_id_fb_candidatura_seq", sequenceName = "fb_candidatura_id_fb_candidatura_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fb_candidatura_id_fb_candidatura_seq")
	@Column(name = "id_fb_candidatura", unique = true, nullable = false)
	public Integer getIdFbCandidatura() {
		return idFbCandidatura;
	}

	public void setIdFbCandidatura(Integer idFbCandidatura) {
		this.idFbCandidatura = idFbCandidatura;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fb_scheda_fabbisogno", nullable = false)
	public FbSchedaFabbisogno getFbSchedaFabbisogno() {
		return fbSchedaFabbisogno;
	}

	public void setFbSchedaFabbisogno(FbSchedaFabbisogno fbSchedaFabbisogno) {
		this.fbSchedaFabbisogno = fbSchedaFabbisogno;
	}

	@Column(name = "nome", nullable = false, length = 100)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "cognome", nullable = false, length = 100)
	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@Column(name = "codice_fiscale", nullable = false, length = 16)
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Column(name = "email", length = 100)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "flg_idoneo")
	public boolean isFlagIdoneo() {
		return flagIdoneo;
	}

	public void setFlagIdoneo(boolean flagIdoneo) {
		this.flagIdoneo = flagIdoneo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idFbCandidatura == null) ? 0 : idFbCandidatura.hashCode());
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
		FbCandidatura other = (FbCandidatura) obj;
		if (idFbCandidatura == null) {
			if (other.idFbCandidatura != null)
				return false;
		} else if (!idFbCandidatura.equals(other.idFbCandidatura))
			return false;
		return true;
	}

}
