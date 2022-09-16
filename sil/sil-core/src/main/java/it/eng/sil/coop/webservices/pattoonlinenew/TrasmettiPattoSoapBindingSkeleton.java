/**
 * TrasmettiPattoSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.pattoonlinenew;

public class TrasmettiPattoSoapBindingSkeleton
		implements it.eng.sil.coop.webservices.pattoonlinenew.TrasmettiPatto, org.apache.axis.wsdl.Skeleton {
	private it.eng.sil.coop.webservices.pattoonlinenew.TrasmettiPatto impl;
	private static java.util.Map _myOperations = new java.util.Hashtable();
	private static java.util.Collection _myOperationsList = new java.util.ArrayList();

	/**
	 * Returns List of OperationDesc objects with this name
	 */
	public static java.util.List getOperationDescByName(java.lang.String methodName) {
		return (java.util.List) _myOperations.get(methodName);
	}

	/**
	 * Returns Collection of OperationDescs
	 */
	public static java.util.Collection getOperationDescs() {
		return _myOperationsList;
	}

	static {
		org.apache.axis.description.OperationDesc _oper;
		org.apache.axis.description.FaultDesc _fault;
		org.apache.axis.description.ParameterDesc[] _params;
		_params = new org.apache.axis.description.ParameterDesc[] { new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://pattoonlinenew.webservices.coop.sil.eng.it", "pattoAccettatoType"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://pattoonlinenew.webservices.coop.sil.eng.it",
						">pattoAccettatoType"),
				it.eng.sil.coop.webservices.pattoonlinenew.PattoAccettatoType.class, false, false), };
		_oper = new org.apache.axis.description.OperationDesc("aggiornaPatto", _params,
				new javax.xml.namespace.QName("http://pattoonlinenew.webservices.coop.sil.eng.it", "esitoType"));
		_oper.setReturnType(
				new javax.xml.namespace.QName("http://pattoonlinenew.webservices.coop.sil.eng.it", ">esitoType"));
		_oper.setElementQName(new javax.xml.namespace.QName("", "aggiornaPatto"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("aggiornaPatto") == null) {
			_myOperations.put("aggiornaPatto", new java.util.ArrayList());
		}
		((java.util.List) _myOperations.get("aggiornaPatto")).add(_oper);
	}

	public TrasmettiPattoSoapBindingSkeleton() {
		this.impl = new it.eng.sil.coop.webservices.pattoonlinenew.TrasmettiPattoSoapBindingImpl();
	}

	public TrasmettiPattoSoapBindingSkeleton(it.eng.sil.coop.webservices.pattoonlinenew.TrasmettiPatto impl) {
		this.impl = impl;
	}

	public it.eng.sil.coop.webservices.pattoonlinenew.EsitoType aggiornaPatto(
			it.eng.sil.coop.webservices.pattoonlinenew.PattoAccettatoType parameters) throws java.rmi.RemoteException {
		it.eng.sil.coop.webservices.pattoonlinenew.EsitoType ret = impl.aggiornaPatto(parameters);
		return ret;
	}

}
