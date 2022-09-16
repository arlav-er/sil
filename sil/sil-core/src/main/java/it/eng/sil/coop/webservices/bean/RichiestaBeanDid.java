package it.eng.sil.coop.webservices.bean;

import org.w3c.dom.Document;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.coop.webservices.utils.Utils;

public class RichiestaBeanDid extends RichiestaBean {

	private String modelloStampa = null;
	private String protocollazione = null;
	private String dataDichiarazione = null;
	private String tipoDichiarazione = null;
	private String intestazioneStampa = null;

	public RichiestaBeanDid(Document doc) throws Exception {
		super(doc);
		String path = null;
		try {
			if (getOutputXML() == null) {
				path = "Stampa/ModelloStampa";
				setModelloStampa(getNodeValue(getRootXML(), path));
				if (getModelloStampa().equals("")) {
					setOutputXML(Utils.createXMLRisposta("99", "Errore generico"));
				} else {
					path = "Stampa/Protocollazione";
					setProtocollazione(getNodeValue(getRootXML(), path));

					path = "Stampa/Intestazione";
					setIntestazione(getNodeValue(getRootXML(), path));

					path = "DataDichiarazione";
					String dataStipulaDid = getNodeValue(getRootXML(), path);
					if (dataStipulaDid != null && !dataStipulaDid.equals("")) {
						dataStipulaDid = dataStipulaDid.substring(8, 10) + "/" + dataStipulaDid.substring(5, 7) + "/"
								+ dataStipulaDid.substring(0, 4);
						setDataDichiarazione(dataStipulaDid);
					}

					if (getDataDichiarazione() == null || getDataDichiarazione().equals("")
							|| !DateUtils.isValidDate(getDataDichiarazione(), 0)) {
						setOutputXML(Utils.createXMLRisposta("05", "Data non valida"));
					} else {
						if (DateUtils.compare(getDataDichiarazione(), DateUtils.getNow()) > 0) {
							setOutputXML(Utils.createXMLRisposta("06", "Data errata perché futura"));
						} else {
							path = "TipoDichiarazione";
							setTipoDichiarazione(getNodeValue(getRootXML(), path));
							if (getTipoDichiarazione().equals("")) {
								setOutputXML(Utils.createXMLRisposta("99", "Errore generico"));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			setOutputXML(Utils.createXMLRisposta("99", "Errore generico"));
		}
	}

	public String getModelloStampa() {
		return this.modelloStampa;
	}

	public void setModelloStampa(String val) {
		this.modelloStampa = val;
	}

	public String getProtocollazione() {
		return this.protocollazione;
	}

	public void setProtocollazione(String val) {
		this.protocollazione = val;
	}

	public String getIntestazione() {
		return this.intestazioneStampa;
	}

	public void setIntestazione(String val) {
		this.intestazioneStampa = val;
	}

	public String getDataDichiarazione() {
		return this.dataDichiarazione;
	}

	public void setDataDichiarazione(String val) {
		this.dataDichiarazione = val;
	}

	public String getTipoDichiarazione() {
		return this.tipoDichiarazione;
	}

	public void setTipoDichiarazione(String val) {
		this.tipoDichiarazione = val;
	}

}
