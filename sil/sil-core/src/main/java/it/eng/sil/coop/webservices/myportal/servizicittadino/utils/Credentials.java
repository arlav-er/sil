package it.eng.sil.coop.webservices.myportal.servizicittadino.utils;

public class Credentials {

	private String username;
	private String password;

	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
