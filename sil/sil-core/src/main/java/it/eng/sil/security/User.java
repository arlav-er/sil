package it.eng.sil.security;

import com.engiweb.framework.error.EMFInternalError;

/**
 * @author Franco Vuoto
 */

public class User {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(User.class.getName());
	private String codRif = "";
	private String codRif2 = "";
	private String cognome = "";
	private String descGruppo = "";
	private String descProfilo = "";
	private String descTipoGruppo = "";

	private String nome = "";
	private String username = null;
	private int cdnGruppo = 0;
	private int cdnProfilo = 0;
	private int cdnTipoGruppo;
	private int codut = 0;
	private int prgProfilo = 0;
	private String codTipo = "";
	private String cfUtenteCollegato = "";

	public static final String USERID = "@@USER@@";

	public User(int codiceutente, String username, String nome, String cognome) throws EMFInternalError {
		this.codut = codiceutente;
		this.username = username;
		this.nome = nome;
		this.cognome = cognome;

		_logger.debug("User :: costruttore " + " username=" + username + " nome=" + nome + " cognome=" + cognome
				+ " codiceutente=" + codiceutente);

	}

	public void setCdnGruppo(int newCdnGruppo) {
		cdnGruppo = newCdnGruppo;
	}

	public int getCdnGruppo() {
		return cdnGruppo;
	}

	public void setCdnProfilo(int newCdnProfilo) {
		cdnProfilo = newCdnProfilo;
	}

	public int getCdnProfilo() {
		return cdnProfilo;
	}

	public void setCdnTipoGruppo(int newCdnTipoGruppo) {
		cdnTipoGruppo = newCdnTipoGruppo;
	}

	public int getCdnTipoGruppo() {
		return cdnTipoGruppo;
	}

	public void setCodRif(String newCodRif) {
		codRif = newCodRif;
	}

	public String getCodRif() {
		return codRif;
	}

	public int getCodut() {
		return codut;
	}

	public void setCognome(String newCognome) {
		cognome = newCognome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setDescGruppo(String newDescGruppo) {
		descGruppo = newDescGruppo;
	}

	public String getDescGruppo() {
		return descGruppo;
	}

	public void setDescProfilo(String newDescProfilo) {
		descProfilo = newDescProfilo;
	}

	public String getDescProfilo() {
		return descProfilo;
	}

	public void setDescTipoGruppo(String newDescTipoGruppo) {
		descTipoGruppo = newDescTipoGruppo;
	}

	public String getDescTipoGruppo() {
		return descTipoGruppo;
	}

	public void setNome(String newNome) {
		nome = newNome;
	}

	/*
	 * public Object getUserAttribute(String attributeName) throws EMFInternalError { return
	 * userAttr.getProperty(attributeName); } public boolean hasRole(String roleName) throws EMFInternalError { return
	 * false; }
	 * 
	 * public Collection getRoles() throws EMFInternalError { return null; }
	 * 
	 * public Collection getFunctionalities() throws EMFInternalError { return null; }
	 * 
	 * public boolean isAbleToExecuteAction(String actionName) throws EMFInternalError { return
	 * this.functionalities.contains(actionName); }
	 * 
	 * public boolean isAbleToExecuteModuleInPage(String pageName, String moduleName) throws EMFInternalError {
	 * _logger.debug( "DBUserProfile :: isAbleToExecuteModuleInPage :: pageName [" +pageName + "] moduleName [" +
	 * moduleName + "]");
	 * 
	 * 
	 * return this.functionalities.contains(moduleName); }
	 * 
	 * public void setApplication(String applicationName) throws EMFInternalError { }
	 */
	/*
	 * public Object getUserUniqueIdentifier() { return username; }
	 */
	public String getNome() {
		return nome;
	}

	public void setPrgProfilo(int newPrgProfilo) {
		prgProfilo = newPrgProfilo;
	}

	public int getPrgProfilo() {
		return prgProfilo;
	}

	public String getUsername() {
		return username;
	}

	/**
	 * @return
	 */
	public String getCodRif2() {
		return codRif2;
	}

	/**
	 * @param string
	 */
	public void setCodRif2(String string) {
		codRif2 = string;
	}

	public void setCodTipo(String newCodTipo) {
		codTipo = newCodTipo;
	}

	public String getCodTipo() {
		return codTipo;
	}

	public void setCfUtenteCollegato(String cf) {
		cfUtenteCollegato = cf;
	}

	public String getCfUtenteCollegato() {
		return cfUtenteCollegato;
	}
}
