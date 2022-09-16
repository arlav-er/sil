package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.List;



/**
 * Classe contenitore delle informazioni su un Utente in sessione.<br/>
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in sessione</b>
 *
 *
 * @author Rodi A.
 *
 */
public class UtenteDTO extends AbstractUpdatablePkDTO implements IUpdatable {

	private static final long serialVersionUID = -1285469945446147040L;
	private PfPrincipalDTO pfPrincipalDTO;
	private Boolean abilitato;
	private Boolean abilitatoServizi;
	private List<CvDatiPersonaliDTO> curricula;
	private List<CvDatiPersonaliDTO> curriculaPalesi;
	private List<CvLetteraAccDTO> lettere;
	private String descProvincia;

	public UtenteDTO() {
		pfPrincipalDTO = new PfPrincipalDTO();
		curricula = new ArrayList<CvDatiPersonaliDTO>();
		curriculaPalesi = new ArrayList<CvDatiPersonaliDTO>();
		lettere = new ArrayList<CvLetteraAccDTO>();
	}
	
	public String getDescProvincia() {
		return descProvincia;
	}

	public void setDescProvincia(String descProvincia) {
		this.descProvincia = descProvincia;
	}

	public PfPrincipalDTO getPfPrincipalDTO() {
		return pfPrincipalDTO;
	}

	public void setPfPrincipalDTO(PfPrincipalDTO pfPrincipalDTO) {
		this.pfPrincipalDTO = pfPrincipalDTO;
	}

	public String getNome() {
		return pfPrincipalDTO.getNome();
	}

	public void setNome(String nome) {
		pfPrincipalDTO.setNome(nome);
	}

	public String getCognome() {
		return pfPrincipalDTO.getCognome();
	}

	public void setCognome(String cognome) {
		pfPrincipalDTO.setCognome(cognome);
	}

	public String getEmail() {
		return pfPrincipalDTO.getEmail();
	}

	public void setEmail(String email) {
		pfPrincipalDTO.setEmail(email);
	}

	public String getStileSelezionato() {
		return pfPrincipalDTO.getStileSelezionato();
	}

	public void setStileSelezionato(String stileSelezionato) {
		pfPrincipalDTO.setStileSelezionato(stileSelezionato);
	}

	public List<CvDatiPersonaliDTO> getCurricula() {
		return curricula;
	}

	public void setCurricula(List<CvDatiPersonaliDTO> curricula) {
		this.curricula = curricula;
	}

	public List<CvDatiPersonaliDTO> getCurriculaPalesi() {
		return curriculaPalesi;
	}

	public void setCurriculaPalesi(List<CvDatiPersonaliDTO> curricula) {
		this.curriculaPalesi = curricula;
	}

	public List<CvLetteraAccDTO> getLettere() {
		return lettere;
	}

	public void setLettere(List<CvLetteraAccDTO> lettere) {
		this.lettere = lettere;
	}

	public Boolean getAbilitato() {
		return abilitato;
	}

	public void setAbilitato(Boolean abilitato) {
		this.abilitato = abilitato;
	}

	public Boolean getAbilitatoServizi() {
		return abilitatoServizi;
	}

	public void setAbilitatoServizi(Boolean abilitatoServizi) {
		this.abilitatoServizi = abilitatoServizi;
	}

	public String getTokenAbilitazioneServizi() {
		return pfPrincipalDTO.getRegistrazioneForteToken();
	}

	public void setTokenAbilitazioneServizi(String tokenAbilitazioneServizi) {
		pfPrincipalDTO.setRegistrazioneForteToken(tokenAbilitazioneServizi);
	}
}
