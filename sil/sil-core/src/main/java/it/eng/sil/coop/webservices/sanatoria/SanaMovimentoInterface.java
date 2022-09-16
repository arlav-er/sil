package it.eng.sil.coop.webservices.sanatoria;

public interface SanaMovimentoInterface extends java.rmi.Remote {
	public String putSanatoriaReddito(String inputXML) throws java.rmi.RemoteException, Exception;
}
