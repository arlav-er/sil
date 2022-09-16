package it.eng.sil.coop.webservices.mobilita;

public interface AdapterWS extends java.rmi.Remote {
	public java.lang.String execute(java.lang.String mittente, java.lang.String destinatario,
			java.lang.String nomeServizio, java.lang.String nomeMetodo, java.lang.String dati, java.lang.String token)
			throws java.rmi.RemoteException;
}