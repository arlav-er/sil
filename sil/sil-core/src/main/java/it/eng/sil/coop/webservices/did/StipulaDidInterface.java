package it.eng.sil.coop.webservices.did;

public interface StipulaDidInterface extends java.rmi.Remote {
	public String putCreaDID(String inputXML) throws java.rmi.RemoteException, Exception;
}
