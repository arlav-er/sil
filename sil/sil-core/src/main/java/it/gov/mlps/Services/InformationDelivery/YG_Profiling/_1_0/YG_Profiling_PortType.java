/**
 * YG_Profiling_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_0;

public interface YG_Profiling_PortType extends java.rmi.Remote {
	public it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0.YG_Profiling[] insert(
			it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0.Insert_Input insert_Input_msg)
			throws java.rmi.RemoteException;

	public it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0.YG_Profiling[] select(
			it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0.Select_Input select_Input_msg)
			throws java.rmi.RemoteException;
}
