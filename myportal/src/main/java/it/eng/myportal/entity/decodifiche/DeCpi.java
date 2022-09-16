package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

/**
 * @author girotti
 * 
 */
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_cpi", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findByCodIntermediarioCl", query = "select d from DeCpi d where d.codIntermediarioCl = :codIntermediarioCl"),
		@NamedQuery(name = "findDeCpiValideByProvincia", query = "SELECT cpi FROM DeComune com JOIN com.deCpi cpi WHERE com.deProvincia.codProvincia = :codProvincia AND cpi.codCpi != 'NT' AND cpi.dtInizioVal < :now AND :now < cpi.dtFineVal GROUP BY cpi.codCpi"),
		@NamedQuery(name = "findBySuggestionAndProvincia", query = "select d from DeCpi d where upper(d.descrizione) like :suggestion and d.deProvincia.codProvincia = :codProvincia"),
		@NamedQuery(name = "findDeCpiByCodMin", query = "SELECT cpi FROM DeCpi cpi WHERE UPPER(cpi.codCpiMin) = UPPER(:codMin) AND cpi.codCpi LIKE '%0'") })
public class DeCpi extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 4943854390406222900L;

	private String codCpi;
	private String descrizione;
	private String indirizzo;
	private String localita;
	private String cap;
	private DeComune deComune;
	private DeProvincia deProvincia;
	private String emailServiziOnline;// character varying(80),
	private String emailAbilitazioneNoPec; // character varying(80)

	private String tel;
	private String fax;// character varying(20), -- fax
	private String email;// character varying(80),
	private String orario;// character varying(100), -- orario del cpi
	private String note;// character varying(100), -- note relativo allo
						// specifico cpi
	private String responsabile;// character varying(100),
	private String codMonoTipoFile;// character varying(1), -- Codice
									// alfanumerico non decodificato di
									// lunghezza 1:...

	private String emailMigrazione;// character varying(80),
	// flg_mov_orig_migr;//character varying(1), -- Indica se nelle migrazioni,
	// per indicare il movimento precedente, si prende l'originale (S) o il
	// precedente (N)

	private String indirizzoStampa;// character varying(600), -- indirizzo da
									// visualizzare nelle stampe.
	private String rifSms;// character varying(100), -- riferimento per l'sms
	private String emailRifCl;// character varying(80), -- Indirizzo e-mail
								// del referente per ClicLavoro
	private String telRifCl;// character varying(20), -- Riferimento
							// telefonico del referente per Cliclavoro
	private String emailPortale;// character varying(80), -- E-mail CPI
								// associata alle vacancy da inviare al
								// portale regionale

	private String codIntermediarioCl;

	// youth garantee
	private String codCpiMin;
	private String descrizioneMin;
	private Boolean flgPatronato;
	private String telPatronato;

	public DeCpi() {
	}

	@Column(name = "cap", length = 5)
	public String getCap() {
		return cap;
	}

	@Override
	public int hashCode() {
		return getCodCpi().hashCode();
	}

	@Column(name = "cod_intermediario_cl", length = 12)
	public String getCodIntermediarioCl() {
		return codIntermediarioCl;
	}

	@Column(name = "cod_mono_tipo_file", length = 1)
	public String getCodMonoTipoFile() {
		return codMonoTipoFile;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_com", nullable = false)
	public DeComune getDeComune() {
		return this.deComune;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia", nullable = false)
	public DeProvincia getDeProvincia() {
		return this.deProvincia;
	}

	@Column(name = "descrizione", nullable = false, length = 300)
	public String getDescrizione() {
		return this.descrizione;
	}

	@Column(name = "email", length = 80)
	public String getEmail() {
		return email;
	}

	@Column(name = "email_migrazione", length = 80)
	public String getEmailMigrazione() {
		return emailMigrazione;
	}

	@Column(name = "email_portale", length = 80)
	public String getEmailPortale() {
		return emailPortale;
	}

	@Column(name = "email_rif_cl", length = 80)
	public String getEmailRifCl() {
		return emailRifCl;
	}

	@Column(name = "email_servizi_online", length = 80)
	public String getEmailServiziOnline() {
		return emailServiziOnline;
	}

	@Column(name = "fax", length = 20)
	public String getFax() {
		return fax;
	}

	@Column(name = "indirizzo", length = 60)
	public String getIndirizzo() {
		return indirizzo;
	}

	@Column(name = "indirizzo_stampa", length = 600)
	public String getIndirizzoStampa() {
		return indirizzoStampa;
	}

	@Column(name = "localita", length = 50)
	public String getLocalita() {
		return localita;
	}

	@Column(name = "note", length = 100)
	public String getNote() {
		return note;
	}

	@Column(name = "orario", length = 100)
	public String getOrario() {
		return orario;
	}

	@Column(name = "responsabile", length = 100)
	public String getResponsabile() {
		return responsabile;
	}

	@Column(name = "rif_sms", length = 100)
	public String getRifSms() {
		return rifSms;
	}

	@Column(name = "tel", length = 20)
	public String getTel() {
		return tel;
	}

	@Column(name = "tel_rif_cl", length = 20)
	public String getTelRifCl() {
		return telRifCl;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public void setCodCpi(String codCittadinanza) {
		this.codCpi = codCittadinanza;
	}

	public void setCodIntermediarioCl(String codIntermediarioCl) {
		this.codIntermediarioCl = codIntermediarioCl;
	}

	public void setCodMonoTipoFile(String cod_mono_tipo_file) {
		this.codMonoTipoFile = cod_mono_tipo_file;
	}

	public void setDeComune(DeComune deComune) {
		this.deComune = deComune;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEmailMigrazione(String email_migrazione) {
		this.emailMigrazione = email_migrazione;
	}

	public void setEmailPortale(String email_portale) {
		this.emailPortale = email_portale;
	}

	public void setEmailRifCl(String email_rif_cl) {
		this.emailRifCl = email_rif_cl;
	}

	public void setEmailServiziOnline(String email_servizi_online) {
		this.emailServiziOnline = email_servizi_online;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public void setIndirizzoStampa(String indirizzo_stampa) {
		this.indirizzoStampa = indirizzo_stampa;
	}

	public void setLocalita(String nazione) {
		this.localita = nazione;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setOrario(String orario) {
		this.orario = orario;
	}

	public void setResponsabile(String responsabile) {
		this.responsabile = responsabile;
	}

	public void setRifSms(String rif_sms) {
		this.rifSms = rif_sms;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setTelRifCl(String tel_rif_cl) {
		this.telRifCl = tel_rif_cl;
	}

	@Column(name = "cod_cpi_min", length = 11)
	public String getCodCpiMin() {
		return codCpiMin;
	}

	public void setCodCpiMin(String codCpiMin) {
		this.codCpiMin = codCpiMin;
	}

	@Column(name = "descrizione_min", length = 100)
	public String getDescrizioneMin() {
		return descrizioneMin;
	}

	public void setDescrizioneMin(String descrizioneMin) {
		this.descrizioneMin = descrizioneMin;
	}

	@Id
	@Column(name = "cod_cpi", unique = true, nullable = false, length = 9)
	public String getCodCpi() {
		return codCpi;
	}

	@Column(name = "flg_patronato")
	public Boolean getFlgPatronato() {
		return flgPatronato;
	}

	public void setFlgPatronato(Boolean flgPatronato) {
		this.flgPatronato = flgPatronato;
	}

	@Column(name = "tel_patronato", length = 30)
	public String getTelPatronato() {
		return telPatronato;
	}

	public void setTelPatronato(String telPatronato) {
		this.telPatronato = telPatronato;
	}

	@Column(name = "email_abilitazione_no_pec", length = 80)
	public String getEmailAbilitazioneNoPec() {
		return emailAbilitazioneNoPec;
	}

	public void setEmailAbilitazioneNoPec(String emailAbilitazioneNoPec) {
		this.emailAbilitazioneNoPec = emailAbilitazioneNoPec;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodCpi() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeCpi)) {
			return false;
		} else {
			return this.getCodCpi().equalsIgnoreCase(((DeCpi) obj).getCodCpi());
		}
	}

}
