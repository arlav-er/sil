/**
 * AcquisizioneFMSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public class AcquisizioneFMSoapBindingSkeleton implements
		it.eng.sil.coop.webservices.firmagrafometrica.AcquisizioneFirmaGrafometrica, org.apache.axis.wsdl.Skeleton {
	private it.eng.sil.coop.webservices.firmagrafometrica.AcquisizioneFirmaGrafometrica impl;
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
		_params = new org.apache.axis.description.ParameterDesc[] {
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
								"in_document_pdf"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "BLOB"),
						it.eng.sil.coop.webservices.firmagrafometrica.BLOB.class, false, false),
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
								"in_string_pdfname"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false),
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
								"in_string_xmlparams"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false), };
		_oper = new org.apache.axis.description.OperationDesc("uploadDocFirmato", _params,
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
						"out_xml_output"));
		_oper.setReturnType(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "XML"));
		_oper.setElementQName(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"uploadDocFirmato"));
		_oper.setSoapAction("uploadDocFirmato..9.0.0");
		_myOperationsList.add(_oper);
		if (_myOperations.get("uploadDocFirmato") == null) {
			_myOperations.put("uploadDocFirmato", new java.util.ArrayList());
		}
		((java.util.List) _myOperations.get("uploadDocFirmato")).add(_oper);
	}

	public AcquisizioneFMSoapBindingSkeleton() {
		this.impl = new it.eng.sil.coop.webservices.firmagrafometrica.AcquisizioneFMSoapBindingImpl();
	}

	public AcquisizioneFMSoapBindingSkeleton(
			it.eng.sil.coop.webservices.firmagrafometrica.AcquisizioneFirmaGrafometrica impl) {
		this.impl = impl;
	}

	public it.eng.sil.coop.webservices.firmagrafometrica.XML uploadDocFirmato(
			it.eng.sil.coop.webservices.firmagrafometrica.BLOB in_document_pdf, java.lang.String in_string_pdfname,
			java.lang.String in_string_xmlparams) throws java.rmi.RemoteException {
		it.eng.sil.coop.webservices.firmagrafometrica.XML ret = impl.uploadDocFirmato(in_document_pdf,
				in_string_pdfname, in_string_xmlparams);
		return ret;
	}

}
