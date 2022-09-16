package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.DeComune;

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

import org.hibernate.annotations.Type;

@Entity
@Table(name = "fb_convenzione_sede", schema = "myportal")
@NamedQueries(value = {
		@NamedQuery(name = "findConvenzioneSedeByIdConvenzione", query = "select sedi from FbConvenzioneSede sedi WHERE sedi.convenzione.idConvenzione = :convenzioneId"),
		@NamedQuery(name = "countAltreSediAccreditate", query = "SELECT COUNT(s) FROM FbConvenzioneSede s WHERE s.flgAccreditata = true AND s.idSedeAccreditataMycas = :idSedeAccreditata AND NOT s.idConvenzioneSedi = :idSedePortale") })
public class FbConvenzioneSede extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 8895117893825384109L;

	private Integer idConvenzioneSedi;
	private String nome;
	private String indirizzo;
	private DeComune deComune;
	private String cap;
	private FbConvenzione convenzione;
	private boolean flgAccreditata = false; // Default FALSE
	private Double latitudine;
	private Double longitudine;
	private String motivoEliminazione;
	private Integer idSedeAccreditataMycas;

	public FbConvenzioneSede() {

	}

	@Id
	@SequenceGenerator(name = "fb_convenzione_sede_id_convenzione_sede_seq", sequenceName = "fb_convenzione_sede_id_convenzione_sede_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fb_convenzione_sede_id_convenzione_sede_seq")
	@Column(name = "id_convenzione_sede", unique = true, nullable = false)
	public Integer getIdConvenzioneSedi() {
		return idConvenzioneSedi;
	}

	@Column(length = 255, nullable = false)
	public String getNome() {
		return nome;
	}

	@Column(name = "indirizzo", length = 255, nullable = false)
	public String getIndirizzo() {
		return indirizzo;
	}

	@ManyToOne
	@JoinColumn(name = "cod_comune", nullable = false)
	public DeComune getDeComune() {
		return deComune;
	}

	@Column(length = 5)
	public String getCap() {
		return cap;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_convenzione")
	public FbConvenzione getConvenzione() {
		return convenzione;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_accreditata", nullable = false, length = 1)
	public boolean getFlgAccreditata() {
		return flgAccreditata;
	}

	@Column(name = "latitudine")
	public Double getLatitudine() {
		return latitudine;
	}

	@Column(name = "longitudine")
	public Double getLongitudine() {
		return longitudine;
	}

	@Column(name = "motivo_eliminazione", length = 200)
	public String getMotivoEliminazione() {
		return motivoEliminazione;
	}

	@Column(name = "id_sede_accreditata_mycas")
	public Integer getIdSedeAccreditataMycas() {
		return idSedeAccreditataMycas;
	}

	public void setIdConvenzioneSedi(Integer idConvenzioneSede) {
		this.idConvenzioneSedi = idConvenzioneSede;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public void setDeComune(DeComune deComune) {
		this.deComune = deComune;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public void setConvenzione(FbConvenzione idConvenzione) {
		this.convenzione = idConvenzione;
	}

	public void setFlgAccreditata(boolean flgAccreditata) {
		this.flgAccreditata = flgAccreditata;
	}

	public void setLatitudine(Double latitudine) {
		this.latitudine = latitudine;
	}

	public void setLongitudine(Double longitudine) {
		this.longitudine = longitudine;
	}

	public void setMotivoEliminazione(String motivoEliminazione) {
		this.motivoEliminazione = motivoEliminazione;
	}

	public void setIdSedeAccreditataMycas(Integer idSedeAccreditataMycas) {
		this.idSedeAccreditataMycas = idSedeAccreditataMycas;
	}

}
