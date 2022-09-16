package it.eng.myportal.dtos;

public class RecuperoPasswordDTO implements IDTO{

	private static final long serialVersionUID = 174570859410746541L;
	
	private String userOrEmail;
	
	private String email;
	
	private String userName;
	
	private String nome;
	
	private String cognome;
	
	private String domanda;
	
	private String risposta;
	
	private String passwordToken;
	
		
	
	public RecuperoPasswordDTO() {
		super();
	}


	public String getUserOrEmail() {
		return userOrEmail;
	}


	public void setUserOrEmail(String userOrEmail) {
		this.userOrEmail = userOrEmail;
	}


	public String getDomanda() {
		return domanda;
	}


	public void setDomanda(String domanda) {
		this.domanda = domanda;
	}


	public String getRisposta() {
		return risposta;
	}


	public void setRisposta(String risposta) {
		this.risposta = risposta;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPasswordToken() {
		return passwordToken;
	}


	public void setPasswordToken(String passwordToken) {
		this.passwordToken = passwordToken;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getCognome() {
		return cognome;
	}


	public void setCognome(String cognome) {
		this.cognome = cognome;
	}



	
	
}