package it.eng.sil.coop.webservices.firmagrafometrica.custom;

import java.io.File;
import java.io.Serializable;

public class BlobFG implements Serializable {

	private static final long serialVersionUID = -5601715405214575078L;

	private File file;
	private String xmlOutput;

	public File getFile() {
		return file;
	}

	public void setFilePDF(File file) {
		this.file = file;
	}

	public String getXmlOutput() {
		return xmlOutput;
	}

	public void setXmlOutput(String xmlOutput) {
		this.xmlOutput = xmlOutput;
	}

}
