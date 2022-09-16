package it.eng.sil.coop.webservices.mystage.patto.custom;

public interface GetPattoLavoratoreInterface {
	public String getDati(String userName, String password, String codiceFiscale, String dataInizioTirocinio)
			throws java.rmi.RemoteException, Exception;
}