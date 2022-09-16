/**
 * SILWSSOAPImplSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.infosilperservsoc;

public class SILWSSOAPImplSoapBindingSkeleton
		implements it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImpl, org.apache.axis.wsdl.Skeleton {
	private it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImpl impl;
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
				new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "req"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "Request"),
				it.eng.sil.coop.webservices.infosilperservsoc.Request.class, false, false), };
		_oper = new org.apache.axis.description.OperationDesc("getInfoLavoratore", _params,
				new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "getInfoLavoratoreReturn"));
		_oper.setReturnType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "Response"));
		_oper.setElementQName(new javax.xml.namespace.QName("", "getInfoLavoratore"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("getInfoLavoratore") == null) {
			_myOperations.put("getInfoLavoratore", new java.util.ArrayList());
		}
		((java.util.List) _myOperations.get("getInfoLavoratore")).add(_oper);
	}

	public SILWSSOAPImplSoapBindingSkeleton() {
		this.impl = new it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImplSoapBindingImpl();
	}

	public SILWSSOAPImplSoapBindingSkeleton(it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImpl impl) {
		this.impl = impl;
	}

	public it.eng.sil.coop.webservices.infosilperservsoc.Response getInfoLavoratore(
			it.eng.sil.coop.webservices.infosilperservsoc.Request req) throws java.rmi.RemoteException {
		it.eng.sil.coop.webservices.infosilperservsoc.Response ret = impl.getInfoLavoratore(req);
		return ret;
	}

}
