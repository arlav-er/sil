package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.movimenti.RecordProcessor;

public class Protocollazione implements RecordProcessor {
	private String className;
	String numProt;
	String annoPR;
	String dataPR;
	String oraPR;
	String tipoPR;

	public Protocollazione(String numProt, String annoPR, String dataPR, String oraPR, String tipoPR) {
		className = this.getClass().getName();
		this.numProt = numProt;
		this.annoPR = annoPR;
		this.dataPR = dataPR;
		this.oraPR = oraPR;
		this.tipoPR = tipoPR;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		ArrayList warnings = new ArrayList();
		record.put("numProtIniziale", numProt);
		record.put("NUMANNOPROT", annoPR);
		record.put("DATAPROT", dataPR);
		record.put("ORAPROT", oraPR);
		record.put("TIPOPROT", tipoPR);
		return null; // ProcessorsUtils.createResponse("Inserimento parametri
						// per protocollazione", className, null, null,
						// warnings, null);
	}

}