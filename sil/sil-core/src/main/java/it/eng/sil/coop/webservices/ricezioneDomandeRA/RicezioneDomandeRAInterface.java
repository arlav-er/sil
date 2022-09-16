package it.eng.sil.coop.webservices.ricezioneDomandeRA;

public interface RicezioneDomandeRAInterface extends java.rmi.Remote {
	public String processDomanda(String inputXML) throws Throwable;
}
