package it.eng.sil.coop.webservices.doteCMS;

public interface RicezioneDomandaDoteInterface extends java.rmi.Remote {
	public String processDomandaDote(String inputXML) throws Throwable;
}
