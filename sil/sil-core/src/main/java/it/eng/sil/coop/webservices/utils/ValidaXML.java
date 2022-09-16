package it.eng.sil.coop.webservices.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class ValidaXML {

	private String xmlTracciato = null;
	private String xsdPathTracciato = null;

	public ValidaXML(String xml, String xsdPath) throws Exception {
		this.xmlTracciato = xml;
		this.xsdPathTracciato = xsdPath;
	}

	public void valida() throws SAXException, IOException {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		File schemaFile = new File(xsdPathTracciato);
		StreamSource streamSource = new StreamSource(schemaFile);
		Schema schema = factory.newSchema(streamSource);
		Validator validator = schema.newValidator();
		StreamSource datiXmlStreamSource = new StreamSource(new StringReader(xmlTracciato));
		validator.validate(datiXmlStreamSource);
	}
}