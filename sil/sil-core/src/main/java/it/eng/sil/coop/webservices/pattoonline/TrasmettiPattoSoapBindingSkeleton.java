/**
 * TrasmettiPattoSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.pattoonline;

public class TrasmettiPattoSoapBindingSkeleton
		implements it.eng.sil.coop.webservices.pattoonline.TrasmettiPatto, org.apache.axis.wsdl.Skeleton {
	private it.eng.sil.coop.webservices.pattoonline.TrasmettiPatto impl;
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
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/",
						"RequestAggiornaPatto"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "PattoAccettatoType"),
				it.eng.sil.coop.webservices.pattoonline.PattoAccettatoType.class, false, false), };
		_oper = new org.apache.axis.description.OperationDesc("aggiornaPatto", _params, new javax.xml.namespace.QName(
				"http://pattoonline.webservices.coop.sil.eng.it/", "ResponseAggiornaPatto"));
		_oper.setReturnType(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "EsitoType"));
		_oper.setElementQName(new javax.xml.namespace.QName("", "AggiornaPatto"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("aggiornaPatto") == null) {
			_myOperations.put("aggiornaPatto", new java.util.ArrayList());
		}
		((java.util.List) _myOperations.get("aggiornaPatto")).add(_oper);
	}

	public TrasmettiPattoSoapBindingSkeleton() {
		this.impl = new it.eng.sil.coop.webservices.pattoonline.TrasmettiPattoSoapBindingImpl();
	}

	public TrasmettiPattoSoapBindingSkeleton(it.eng.sil.coop.webservices.pattoonline.TrasmettiPatto impl) {
		this.impl = impl;
	}

	public it.eng.sil.coop.webservices.pattoonline.EsitoType aggiornaPatto(
			it.eng.sil.coop.webservices.pattoonline.PattoAccettatoType richiesta) throws java.rmi.RemoteException {
		it.eng.sil.coop.webservices.pattoonline.EsitoType ret = impl.aggiornaPatto(richiesta);
		return ret;
	}

}
