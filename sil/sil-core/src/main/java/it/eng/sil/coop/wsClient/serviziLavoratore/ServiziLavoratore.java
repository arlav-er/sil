/**
 * ServiziLavoratore.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.serviziLavoratore;

public interface ServiziLavoratore extends java.rmi.Remote {
	public java.lang.String getLavoratoreIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5) throws java.rmi.RemoteException;

	public java.lang.String putLavoratoreIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException;

	public java.lang.String aggiornaCompetenzaIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException;

	public java.lang.String aggiornaCompExtraRegioneIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13, java.lang.String in14)
			throws java.rmi.RemoteException;

	public it.eng.sil.coop.wsClient.serviziLavoratore.CpiMasterLavoratoreBean getCpiMasterIR(java.lang.String in0)
			throws java.rmi.RemoteException;

	public java.lang.String modificaCPICompIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException;

	public java.lang.String accorpaLavoratoriIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13, java.lang.String in14)
			throws java.rmi.RemoteException;

	public java.lang.String modificaCodiceFiscaleIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13, java.lang.String in14)
			throws java.rmi.RemoteException;

	public java.lang.String modificaAnagraficaLavoratoreIR(java.lang.String in0, java.lang.String in1,
			java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5,
			java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9,
			java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13)
			throws java.rmi.RemoteException;
}
