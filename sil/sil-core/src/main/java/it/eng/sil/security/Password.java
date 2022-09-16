/*
 * Creato il 5-ott-04
 * Author: vuoto
 * 
 */
package it.eng.sil.security;

/**
 * @author vuoto
 * 
 */
public class Password {

	private String value = "";
	private String encValue = "";

	private int contaAlfaUpperCase = 0;
	private int contaAlfaLowerCase = 0;

	private int contaNum = 0;
	private int contaNonAlfa = 0;

	public Password() {
		value = "";
		encValue = "";
	}

	private void validate() {

		contaAlfaUpperCase = 0;
		contaAlfaLowerCase = 0;

		contaNum = 0;
		contaNonAlfa = 0;

		for (int i = 0; i < value.length(); i++) {
			int c = value.charAt(i);
			if ((c >= 'A') && (c <= 'Z')) {
				contaAlfaUpperCase++;
			}
			if ((c >= 'a') && (c <= 'z')) {
				contaAlfaLowerCase++;
			} else if ((c >= '0') && (c <= '9')) {
				contaNum++;
			} else {
				contaNonAlfa++;
			}

		}
	}

	public Password(String value) {
		this.value = value;
		encValue = "";
		validate();
	}

	public boolean hasAltenateCase() {

		return ((contaAlfaUpperCase >= 1) && (contaAlfaLowerCase >= 1));
	}

	public boolean hasDigits() {

		return (contaNum > 0);
	}

	public boolean isEnoughLong() {

		return (value.length() >= 8);
	}

	/**
	 * @return
	 */
	public String getEncValue() {

		if (encValue != null) {
			AsymmetricProviderSingleton bs = AsymmetricProviderSingleton.getInstance();
			encValue = "#SHA#" + bs.enCrypt(value);
		}

		return encValue;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param string
	 */
	public void setValue(String string) {
		value = string;
		validate();
	}

	public static void main(String[] args) {
		Password p = new Password("Antenucci");

		System.out.println(p.getEncValue());
		System.out.println(p.getValue());

	}

}
