/**
 * ServiziLavoratoreSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.servizilavoratore;

public class ServiziLavoratoreSoapBindingStub extends org.apache.axis.client.Stub implements ServiziLavoratore {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[9];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getLavoratoreIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "getLavoratoreIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("putLavoratoreIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in6"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in7"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in8"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in9"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in10"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in11"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in12"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in13"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "putLavoratoreIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("aggiornaCompetenzaIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in6"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in7"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in8"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in9"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in10"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in11"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in12"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in13"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "aggiornaCompetenzaIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("aggiornaCompExtraRegioneIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in6"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in7"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in8"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in9"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in10"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in11"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in12"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in13"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in14"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "aggiornaCompExtraRegioneIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getCpiMasterIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("urn:ServiziLavoratore", "CpiMasterLavoratore"));
		oper.setReturnClass(CpiMasterLavoratore.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "getCpiMasterIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("modificaCPICompIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in6"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in7"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in8"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in9"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in10"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in11"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in12"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in13"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "modificaCPICompIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[5] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("accorpaLavoratoriIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in6"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in7"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in8"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in9"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in10"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in11"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in12"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in13"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in14"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "accorpaLavoratoriIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[6] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("modificaCodiceFiscaleIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in6"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in7"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in8"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in9"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in10"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in11"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in12"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in13"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in14"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "modificaCodiceFiscaleIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[7] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("modificaAnagraficaLavoratoreIR");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in6"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in7"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in8"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in9"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in10"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in11"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in12"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in13"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "modificaAnagraficaLavoratoreIRReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[8] = oper;

	}

	public ServiziLavoratoreSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public ServiziLavoratoreSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public ServiziLavoratoreSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		if (service == null) {
			super.service = new org.apache.axis.client.Service();
		} else {
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
		java.lang.Class cls;
		javax.xml.namespace.QName qName;
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;

		qName = new javax.xml.namespace.QName("urn:ServiziLavoratore", "CpiMasterLavoratore");
		cachedSerQNames.add(qName);
		cls = CpiMasterLavoratore.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
		try {
			org.apache.axis.client.Call _call = super._createCall();
			if (super.maintainSessionSet) {
				_call.setMaintainSession(super.maintainSession);
			}
			if (super.cachedUsername != null) {
				_call.setUsername(super.cachedUsername);
			}
			if (super.cachedPassword != null) {
				_call.setPassword(super.cachedPassword);
			}
			if (super.cachedEndpoint != null) {
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if (super.cachedTimeout != null) {
				_call.setTimeout(super.cachedTimeout);
			}
			if (super.cachedPortName != null) {
				_call.setPortName(super.cachedPortName);
			}
			java.util.Enumeration keys = super.cachedProperties.keys();
			while (keys.hasMoreElements()) {
				java.lang.String key = (java.lang.String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			// All the type mapping information is registered
			// when the first call is made.
			// The type mapping information is actually registered in
			// the TypeMappingRegistry of the service, which
			// is the reason why registration is only needed for the first call.
			synchronized (this) {
				if (firstCall()) {
					// must set encoding style before registering serializers
					_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
					_call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
					for (int i = 0; i < cachedSerFactories.size(); ++i) {
						java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
						javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames.get(i);
						java.lang.Object x = cachedSerFactories.get(i);
						if (x instanceof Class) {
							java.lang.Class sf = (java.lang.Class) cachedSerFactories.get(i);
							java.lang.Class df = (java.lang.Class) cachedDeserFactories.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						} else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
							org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories
									.get(i);
							org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories
									.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						}
					}
				}
			}
			return _call;
		} catch (java.lang.Throwable _t) {
			throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
		}
	}

	public java.lang.String getLavoratoreIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"getLavoratoreIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0, in1, in2, in3, in4, in5 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String putLavoratoreIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"putLavoratoreIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0, in1, in2, in3, in4, in5, in6, in7, in8,
					in9, in10, in11, in12, in13 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String aggiornaCompetenzaIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"aggiornaCompetenzaIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0, in1, in2, in3, in4, in5, in6, in7, in8,
					in9, in10, in11, in12, in13 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String aggiornaCompExtraRegioneIR(java.lang.String in0, java.lang.String in1,
			java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5,
			java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9,
			java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13,
			java.lang.String in14) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"aggiornaCompExtraRegioneIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0, in1, in2, in3, in4, in5, in6, in7, in8,
					in9, in10, in11, in12, in13, in14 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public CpiMasterLavoratore getCpiMasterIR(java.lang.String in0) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"getCpiMasterIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (CpiMasterLavoratore) _resp;
				} catch (java.lang.Exception _exception) {
					return (CpiMasterLavoratore) org.apache.axis.utils.JavaUtils.convert(_resp,
							CpiMasterLavoratore.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String modificaCPICompIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"modificaCPICompIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0, in1, in2, in3, in4, in5, in6, in7, in8,
					in9, in10, in11, in12, in13 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String accorpaLavoratoriIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13, java.lang.String in14)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[6]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"accorpaLavoratoriIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0, in1, in2, in3, in4, in5, in6, in7, in8,
					in9, in10, in11, in12, in13, in14 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String modificaCodiceFiscaleIR(java.lang.String in0, java.lang.String in1, java.lang.String in2,
			java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6,
			java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10,
			java.lang.String in11, java.lang.String in12, java.lang.String in13, java.lang.String in14)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[7]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"modificaCodiceFiscaleIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0, in1, in2, in3, in4, in5, in6, in7, in8,
					in9, in10, in11, in12, in13, in14 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String modificaAnagraficaLavoratoreIR(java.lang.String in0, java.lang.String in1,
			java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5,
			java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9,
			java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[8]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://serviziLavoratore.webservices.coop.sil.eng.it",
				"modificaAnagraficaLavoratoreIR"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { in0, in1, in2, in3, in4, in5, in6, in7, in8,
					in9, in10, in11, in12, in13 });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
