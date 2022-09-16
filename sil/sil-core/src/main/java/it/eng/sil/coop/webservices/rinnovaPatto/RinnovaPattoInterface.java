package it.eng.sil.coop.webservices.rinnovaPatto;

public interface RinnovaPattoInterface extends java.rmi.Remote {
	public String putRinnovaPatto(String inputXML) throws java.rmi.RemoteException, Exception;
}
