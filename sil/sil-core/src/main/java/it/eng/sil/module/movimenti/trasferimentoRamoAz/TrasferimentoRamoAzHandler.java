/*
 * Created on 5-ott-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.module.movimenti.trasferimentoRamoAz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author mancinid
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class TrasferimentoRamoAzHandler extends DefaultHandler {

	// Attributi dell'elemento VarDatori:
	private String dataComunicazione = ""; // dataInvio
	private String tipoDelegato = "";
	private String delegato = "";
	private String codiceComunicazione = "";
	private String codiceComunicazionePrec = "";

	private String codFiscaleAzAttuale = "";
	private String ragioneSocialeAzAttuale = "";
	private String settoreAzAttuale = "";

	private String codFiscaleAzPrecedente = "";
	private String ragioneSocialeAzPrecedente = "";
	private String settoreAzPrecedente = "";

	private String tipoComunicazione = "";

	// Attributi dell'elemento TrasferimentoAzienda:
	private String dataInizioTrasferimento = "";
	private String codiceTrasferimento = "";

	boolean isSedeLavoro = false;
	boolean isElementoSede = false;
	boolean isElementoRecapiti = false;
	boolean isLavoratori = false;
	boolean isElementoDatiLavoratore = false;
	boolean isElementoContratto = false;
	boolean isDatoreAttuale = false;
	boolean isDatorePrecedente = false;
	boolean isSettore = false;
	boolean isTipoComunicazione = false;
	boolean isCodFiscLav = false;
	boolean isMittenteSare = false;

	private int numeroSedeLavoro = 0;

	private TreeMap sedeAttuale;
	private TreeMap sedePrecedente;
	private TreeMap nuovaSede;
	private ArrayList codFiscLavList;
	private HashMap datiLav;
	private HashMap contrattoLav;
	private String qName = "";

	private String elementValue = "";

	// Costruttore per recuperare la nuova SedeLavoro:
	private int numeroSedeLavoroParam;
	TreeMap nuovaSedeNSL = new TreeMap();
	ArrayList codFiscLavListNSL = new ArrayList();

	public TrasferimentoRamoAzHandler(int numeroSedeLavoroParam) {
		this.numeroSedeLavoroParam = numeroSedeLavoroParam;
	}

	// Costruttore per recuperare datiLav e contrattoLav:
	private String codFiscParam;
	private HashMap datiLavCF = new HashMap();
	private HashMap contrattoLavCF = new HashMap();

	public TrasferimentoRamoAzHandler(String codFiscParam) {
		this.codFiscParam = codFiscParam;
	}

	// Costruttore di default:
	public TrasferimentoRamoAzHandler() {
	}

	public void startDocument() {
		// System.out.println("Inizio documento ... ");
	}

	public void endDocument() {
		// System.out.println("Fine documento ... ");
	}

	public void characters(char[] ch, int start, int len) {
		elementValue += new String(ch, start, len);
		/*
		 * String prefix = ""; if (qName.indexOf(":")>0) prefix = qName.substring(0, qName.indexOf(":")+1); String str =
		 * new String(ch,start,len); //System.out.println("characters: " + str); if (isDatoreAttuale && isElementoSede)
		 * { for (int j=0; j<TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length; j++) { if
		 * (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[j])) { String eName =
		 * prefix.equals("")? qName:qName.substring(qName.indexOf(':')+1); sedeAttuale.put(eName, str); } } } if
		 * (isDatoreAttuale && isElementoRecapiti) { for (int j=0;
		 * j<TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length; j++) { if
		 * (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[j])) { String eName =
		 * prefix.equals("")? qName:qName.substring(qName.indexOf(':')+1); sedeAttuale.put(eName, str); } } } if
		 * (isDatorePrecedente && isElementoSede) { for (int j=0; j<TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length;
		 * j++) { if (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[j])) { String eName =
		 * prefix.equals("")? qName:qName.substring(qName.indexOf(':')+1); sedePrecedente.put(eName, str); } } } if
		 * (isDatorePrecedente && isElementoRecapiti) { for (int j=0;
		 * j<TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length; j++) { if
		 * (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[j])) { String eName =
		 * prefix.equals("")? qName:qName.substring(qName.indexOf(':')+1); sedePrecedente.put(eName, str); } } } if
		 * (isSedeLavoro && isElementoSede) { for (int j=0; j<TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length; j++) {
		 * if (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[j])) { String eName =
		 * prefix.equals("")? qName:qName.substring(qName.indexOf(':')+1); nuovaSede.put(eName, str); } } } if
		 * (isSedeLavoro && isElementoRecapiti) { for (int j=0; j<TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length;
		 * j++) { if (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[j])) { String eName =
		 * prefix.equals("")? qName:qName.substring(qName.indexOf(':')+1); nuovaSede.put(eName, str); } } } if
		 * (isSedeLavoro && isLavoratori && isElementoDatiLavoratore) { for (int j=0;
		 * j<TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE.length; j++) { if
		 * (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[j])) { String eName =
		 * prefix.equals("")? qName:qName.substring(qName.indexOf(':')+1); datiLav.put(eName, str); } } } if
		 * (isSedeLavoro && isLavoratori && isElementoDatiLavoratore && isCodFiscLav) { if
		 * (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[2])) {
		 * codFiscLavList.add(str); } } if (isSedeLavoro && isLavoratori && isElementoContratto) { for (int j=0;
		 * j<TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO.length; j++) { if
		 * (qName.equalsIgnoreCase(prefix+TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[j])) { String eName =
		 * prefix.equals("")? qName:qName.substring(qName.indexOf(':')+1); contrattoLav.put(eName, str); } } } if
		 * (isDatoreAttuale && isSettore) { settoreAzAttuale = str; } if (isDatorePrecedente && isSettore) {
		 * settoreAzPrecedente = str; } if (isTipoComunicazione) { tipoComunicazione = str; } if (isMittenteSare) {
		 * datiLav.put(TrasferimentoRamoAzXmlConst.MITTENTE_SARE, str);
		 * 
		 * }
		 */
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		this.qName = qName;
		String prefix = "";
		elementValue = "";
		if (qName.indexOf(":") > 0)
			prefix = qName.substring(0, qName.indexOf(":") + 1);

		/*
		 * System.out.println("Inizio elemento ___ " + qName); for (int i=0; i<atts.getLength(); i++) {
		 * //System.out.println("Attributo ___ " + atts.getQName(i) + " valore ___ " + atts.getValue(i)); }
		 */
		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.VARDATORI)) {
			dataComunicazione = getAttribute(TrasferimentoRamoAzXmlConst.DATA_COMUNICAZIONE, atts);
			dataComunicazione = getStringDateFormatted(dataComunicazione.substring(0, 10));
			tipoDelegato = getAttribute(TrasferimentoRamoAzXmlConst.TIPO_DELEGATO, atts);
			delegato = getAttribute(TrasferimentoRamoAzXmlConst.DELEGATO, atts);
			codiceComunicazione = getAttribute(TrasferimentoRamoAzXmlConst.CODICE_COMUNICAZIONE, atts);
			codiceComunicazionePrec = getAttribute(TrasferimentoRamoAzXmlConst.CODICE_COMUNICAZIONE_PREC, atts);
		}

		// Il Cod. Fiscale Azienda Precedente deve essere diverso da Cod. Fiscale Azienda Attuale
		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.DATORE_ATTUALE)) {
			codFiscaleAzAttuale = getAttribute(TrasferimentoRamoAzXmlConst.CODICE_FISCALE_DATORE, atts);
			ragioneSocialeAzAttuale = getAttribute(TrasferimentoRamoAzXmlConst.RAGIONE_SOCIALE_DATORE, atts);
			sedeAttuale = new TreeMap();
			isDatoreAttuale = true;
		} else if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.DATORE_PRECEDENTE)) {
			codFiscaleAzPrecedente = getAttribute(TrasferimentoRamoAzXmlConst.CODICE_FISCALE_DATORE, atts);
			ragioneSocialeAzPrecedente = getAttribute(TrasferimentoRamoAzXmlConst.RAGIONE_SOCIALE_DATORE, atts);
			sedePrecedente = new TreeMap();
			isDatorePrecedente = true;
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.SETTORE)) {
			isSettore = true;
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.TIPO_COMUNICAZIONE)) {
			isTipoComunicazione = true;
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.TRASFERIMENTO_AZIENDA)) {
			dataInizioTrasferimento = getAttribute(TrasferimentoRamoAzXmlConst.DATA_INIZIO_TRASFERIMENTO, atts);
			dataInizioTrasferimento = getStringDateFormatted(dataInizioTrasferimento);
			codiceTrasferimento = getAttribute(TrasferimentoRamoAzXmlConst.CODICE_TRASFERIMENTO, atts);
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.SEDE_LAVORO)) {
			numeroSedeLavoro++;
			nuovaSede = new TreeMap();
			codFiscLavList = new ArrayList();
			isSedeLavoro = true;
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.LAVORATORI)) {
			datiLav = new HashMap();
			contrattoLav = new HashMap();
			isLavoratori = true;
		}

		for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length; j++) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[j])) {
				isElementoSede = true;
			}
		}

		for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length; j++) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[j])) {
				isElementoRecapiti = true;
			}
		}

		for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE.length; j++) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[j])) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[2])) {
					isCodFiscLav = true;
				}
				isElementoDatiLavoratore = true;
			}
		}

		for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO.length; j++) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[j])) {
				isElementoContratto = true;
			}
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.CONTRATTO)
				|| qName.equals(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[3])
				|| qName.equals(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[4])) {
			for (int i = 0; i < atts.getLength(); i++) {
				String attributeQName = atts.getQName(i);
				String attributeValue = atts.getValue(i);
				if (attributeQName.startsWith("data")) {
					attributeValue = getStringDateFormatted(attributeValue);
				}
				contrattoLav.put(attributeQName, attributeValue);
			}
		}
		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.MITTENTE_SARE)) {
			isMittenteSare = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		// System.out.println("Fine elemento " + qName);
		String prefix = "";
		if (qName.indexOf(":") > 0)
			prefix = qName.substring(0, qName.indexOf(":") + 1);

		if (isDatoreAttuale && isElementoSede) {
			for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length; j++) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[j])) {
					String eName = prefix.equals("") ? qName : qName.substring(qName.indexOf(':') + 1);
					sedeAttuale.put(eName, elementValue);
				}
			}
		}
		if (isDatoreAttuale && isElementoRecapiti) {
			for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length; j++) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[j])) {
					String eName = prefix.equals("") ? qName : qName.substring(qName.indexOf(':') + 1);
					sedeAttuale.put(eName, elementValue);
				}
			}
		}
		if (isDatorePrecedente && isElementoSede) {
			for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length; j++) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[j])) {
					String eName = prefix.equals("") ? qName : qName.substring(qName.indexOf(':') + 1);
					sedePrecedente.put(eName, elementValue);
				}
			}
		}
		if (isDatorePrecedente && isElementoRecapiti) {
			for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length; j++) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[j])) {
					String eName = prefix.equals("") ? qName : qName.substring(qName.indexOf(':') + 1);
					sedePrecedente.put(eName, elementValue);
				}
			}
		}
		if (isSedeLavoro && isElementoSede) {
			for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length; j++) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[j])) {
					String eName = prefix.equals("") ? qName : qName.substring(qName.indexOf(':') + 1);
					nuovaSede.put(eName, elementValue);
				}
			}
		}
		if (isSedeLavoro && isElementoRecapiti) {
			for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length; j++) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[j])) {
					String eName = prefix.equals("") ? qName : qName.substring(qName.indexOf(':') + 1);
					nuovaSede.put(eName, elementValue);
				}
			}
		}
		if (isSedeLavoro && isLavoratori && isElementoDatiLavoratore) {
			for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE.length; j++) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[j])) {
					String eName = prefix.equals("") ? qName : qName.substring(qName.indexOf(':') + 1);
					datiLav.put(eName, elementValue);
				}
			}
		}
		if (isSedeLavoro && isLavoratori && isElementoDatiLavoratore && isCodFiscLav) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[2])) {
				codFiscLavList.add(elementValue);
			}
		}
		if (isSedeLavoro && isLavoratori && isElementoContratto) {
			for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO.length; j++) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[j])) {
					String eName = prefix.equals("") ? qName : qName.substring(qName.indexOf(':') + 1);
					contrattoLav.put(eName, elementValue);
				}
			}
		}
		if (isDatoreAttuale && isSettore) {
			settoreAzAttuale = elementValue;
		}
		if (isDatorePrecedente && isSettore) {
			settoreAzPrecedente = elementValue;
		}
		if (isTipoComunicazione) {
			tipoComunicazione = elementValue;
		}
		if (isMittenteSare) {
			datiLav.put(TrasferimentoRamoAzXmlConst.MITTENTE_SARE, elementValue);

		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.DATORE_ATTUALE)) {
			isDatoreAttuale = false;
		} else if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.DATORE_PRECEDENTE)) {
			isDatorePrecedente = false;
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.SETTORE)) {
			isSettore = false;
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.TIPO_COMUNICAZIONE)) {
			isTipoComunicazione = false;
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.SEDE_LAVORO)) {
			/*
			 * L'oggetto più pesante caricato in memoria è codFiscLavList: si tratta di un ArrayList dei codici fiscali
			 * di tutti i lavoratori appartenenti ad una determinata SedeLavoro
			 */
			if (numeroSedeLavoro == numeroSedeLavoroParam) {
				nuovaSedeNSL = nuovaSede;
				codFiscLavListNSL = codFiscLavList;
			}
			isSedeLavoro = false;
		}

		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.LAVORATORI)) {
			String codFiscLavoratore = (String) datiLav.get(TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[2]);
			if (codFiscLavoratore.equals(codFiscParam)) {
				datiLavCF = datiLav;
				contrattoLavCF = contrattoLav;
			}
			isLavoratori = false;
		}

		for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length; j++) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[j])) {
				isElementoSede = false;
			}
		}

		for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length; j++) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[j])) {
				isElementoRecapiti = false;
			}
		}

		for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE.length; j++) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[j])) {
				if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[2])) {
					isCodFiscLav = false;
				}
				isElementoDatiLavoratore = false;
			}
		}

		for (int j = 0; j < TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO.length; j++) {
			if (qName.equalsIgnoreCase(prefix + TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[j])) {
				isElementoContratto = false;
			}
		}
		if (qName.equals(prefix + TrasferimentoRamoAzXmlConst.MITTENTE_SARE)) {
			isMittenteSare = false;
		}
	}

	private String getAttribute(String attributeName, Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			if (attributes.getQName(i).equals(attributeName)) {
				return attributes.getValue(i);
			}
		}
		return "";
	}

	// La data nel formato yyyy-mm-dd bisogna trasformarla nel nostro formato dd/mm/yyyy
	private String getStringDateFormatted(String date) {
		if (date.length() == 10 && date.substring(4, 5).equals("-") && date.substring(7, 8).equals("-")) {
			return date.substring(8, 10) + "/" + date.substring(5, 7) + "/" + date.substring(0, 4);
		}
		return date;
	}

	/**
	 * @return
	 */
	public String getCodFiscaleAzAttuale() {
		return codFiscaleAzAttuale;
	}

	/**
	 * @return
	 */
	public String getCodFiscaleAzPrecedente() {
		return codFiscaleAzPrecedente;
	}

	/**
	 * @param string
	 */
	public void setCodFiscaleAzAttuale(String string) {
		codFiscaleAzAttuale = string;
	}

	/**
	 * @param string
	 */
	public void setCodFiscaleAzPrecedente(String string) {
		codFiscaleAzPrecedente = string;
	}

	/**
	 * @return
	 */
	public String getDataInizioTrasferimento() {
		return dataInizioTrasferimento;
	}

	/**
	 * @param string
	 */
	public void setDataInizioTrasferimento(String string) {
		dataInizioTrasferimento = string;
	}

	/**
	 * @return
	 */
	public TreeMap getNuovaSede() {
		return nuovaSede;
	}

	/**
	 * @return
	 */
	public String getQName() {
		return qName;
	}

	/**
	 * @param map
	 */
	public void setNuovaSede(TreeMap map) {
		nuovaSede = map;
	}

	/**
	 * @param string
	 */
	public void setQName(String string) {
		qName = string;
	}

	/**
	 * @return
	 */
	public String getRagioneSocialeAzAttuale() {
		return ragioneSocialeAzAttuale;
	}

	/**
	 * @return
	 */
	public String getRagioneSocialeAzPrecedente() {
		return ragioneSocialeAzPrecedente;
	}

	/**
	 * @param string
	 */
	public void setRagioneSocialeAzAttuale(String string) {
		ragioneSocialeAzAttuale = string;
	}

	/**
	 * @param string
	 */
	public void setRagioneSocialeAzPrecedente(String string) {
		ragioneSocialeAzPrecedente = string;
	}

	/**
	 * @return
	 */
	public String getDataComunicazione() {
		return dataComunicazione;
	}

	/**
	 * @param string
	 */
	public void setDataComunicazione(String string) {
		dataComunicazione = string;
	}

	/**
	 * @return
	 */
	public HashMap getContrattoLav() {
		return contrattoLav;
	}

	/**
	 * @param map
	 */
	public void setContrattoLav(HashMap map) {
		contrattoLav = map;
	}

	/**
	 * @return
	 */
	public String getCodiceComunicazione() {
		return codiceComunicazione;
	}

	/**
	 * @return
	 */
	public String getDelegato() {
		return delegato;
	}

	/**
	 * @return
	 */
	public String getTipoDelegato() {
		return tipoDelegato;
	}

	/**
	 * @param string
	 */
	public void setCodiceComunicazione(String string) {
		codiceComunicazione = string;
	}

	/**
	 * @param string
	 */
	public void setDelegato(String string) {
		delegato = string;
	}

	/**
	 * @param string
	 */
	public void setTipoDelegato(String string) {
		tipoDelegato = string;
	}

	/**
	 * @return
	 */
	public String getCodiceTrasferimento() {
		return codiceTrasferimento;
	}

	/**
	 * @param string
	 */
	public void setCodiceTrasferimento(String string) {
		codiceTrasferimento = string;
	}

	/**
	 * @return
	 */
	public TreeMap getSedeAttuale() {
		return sedeAttuale;
	}

	/**
	 * @return
	 */
	public TreeMap getSedePrecedente() {
		return sedePrecedente;
	}

	/**
	 * @param map
	 */
	public void setSedeAttuale(TreeMap map) {
		sedeAttuale = map;
	}

	/**
	 * @param map
	 */
	public void setSedePrecedente(TreeMap map) {
		sedePrecedente = map;
	}

	/**
	 * @return
	 */
	public String getSettoreAzAttuale() {
		return settoreAzAttuale;
	}

	/**
	 * @return
	 */
	public String getSettoreAzPrecedente() {
		return settoreAzPrecedente;
	}

	/**
	 * @return
	 */
	public String getTipoComunicazione() {
		return tipoComunicazione;
	}

	/**
	 * @param string
	 */
	public void setSettoreAzAttuale(String string) {
		settoreAzAttuale = string;
	}

	/**
	 * @param string
	 */
	public void setSettoreAzPrecedente(String string) {
		settoreAzPrecedente = string;
	}

	/**
	 * @param string
	 */
	public void setTipoComunicazione(String string) {
		tipoComunicazione = string;
	}

	/**
	 * @return
	 */
	public ArrayList getCodFiscLavList() {
		return codFiscLavList;
	}

	/**
	 * @param list
	 */
	public void setCodFiscLavList(ArrayList list) {
		codFiscLavList = list;
	}

	/**
	 * @return
	 */
	public int getNumeroSedeLavoro() {
		return numeroSedeLavoro;
	}

	/**
	 * @param i
	 */
	public void setNumeroSedeLavoro(int i) {
		numeroSedeLavoro = i;
	}

	/**
	 * @return
	 */
	public HashMap getDatiLavCF() {
		return datiLavCF;
	}

	/**
	 * @param map
	 */
	public void setDatiLavCF(HashMap map) {
		datiLavCF = map;
	}

	/**
	 * @return
	 */
	public HashMap getContrattoLavCF() {
		return contrattoLavCF;
	}

	/**
	 * @param map
	 */
	public void setContrattoLavCF(HashMap map) {
		contrattoLavCF = map;
	}

	/**
	 * @return
	 */
	public TreeMap getNuovaSedeNSL() {
		return nuovaSedeNSL;
	}

	/**
	 * @param map
	 */
	public void setNuovaSedeNSL(TreeMap map) {
		nuovaSedeNSL = map;
	}

	/**
	 * @return
	 */
	public ArrayList getCodFiscLavListNSL() {
		return codFiscLavListNSL;
	}

	/**
	 * @param list
	 */
	public void setCodFiscLavListNSL(ArrayList list) {
		codFiscLavListNSL = list;
	}

	/**
	 * MAIN PER TEST STATICO
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		String datiXML = "";
		String filePath = "C:\\Progetti\\SIL\\lavori in corso\\2008\\20080604_trasferimento_ramo_azienda\\com_di_test_var_trasferimentoramoazienda_04.xml";
		String s = "";
		try {
			char[] buff = new char[1024];
			java.io.FileReader fr = new java.io.FileReader(filePath);
			int n = 0;
			while ((n = fr.read(buff)) > 0) {
				s += new String(buff, 0, n);
			}
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		datiXML = s;
		//
		javax.xml.parsers.SAXParserFactory parserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
		javax.xml.parsers.SAXParser parser = parserFactory.newSAXParser();
		TrasferimentoRamoAzHandler trasfRamoAzHandler = new TrasferimentoRamoAzHandler();
		java.io.InputStream xmlInputStream = new java.io.ByteArrayInputStream(datiXML.getBytes());
		parser.parse(xmlInputStream, trasfRamoAzHandler);
		////// TEST

		TreeMap sedePrecedente = trasfRamoAzHandler.getSedePrecedente();
		TreeMap sedeAttuale = trasfRamoAzHandler.getSedeAttuale();
		String dataInizioTrasferimento = trasfRamoAzHandler.getDataInizioTrasferimento();
		String codTipoTrasf = trasfRamoAzHandler.getCodiceTrasferimento();
		String codFiscaleAzAttuale = trasfRamoAzHandler.getCodFiscaleAzAttuale();
		String codFiscaleAzPrecedente = trasfRamoAzHandler.getCodFiscaleAzPrecedente();
		String ragSocialeAzAttuale = trasfRamoAzHandler.getRagioneSocialeAzAttuale();
		//
		trasfRamoAzHandler = new TrasferimentoRamoAzHandler(1);
		xmlInputStream = new java.io.ByteArrayInputStream(datiXML.getBytes());
		parser.parse(xmlInputStream, trasfRamoAzHandler);
		TreeMap nuovaSede = trasfRamoAzHandler.getNuovaSedeNSL();
		ArrayList codFiscLavList = trasfRamoAzHandler.getCodFiscLavListNSL();
		//
		String cfLavoratore = "CMNGNN58H64H225T";
		trasfRamoAzHandler = new TrasferimentoRamoAzHandler(cfLavoratore);
		xmlInputStream = new java.io.ByteArrayInputStream(datiXML.getBytes());
		parser.parse(xmlInputStream, trasfRamoAzHandler);
		HashMap datiLavoratore = trasfRamoAzHandler.getDatiLavCF();
		HashMap contrattoLavoratore = trasfRamoAzHandler.getContrattoLavCF();
		//
		HashMap datiTestata = TrasferimentoRamoAzUtils.getDatiTestataVardatori(trasfRamoAzHandler);
		//
		com.engiweb.framework.base.SourceBean mov = TrasferimentoRamoAzUtils.getUniSilMovimentoAvv(datiTestata,
				sedePrecedente, datiLavoratore, contrattoLavoratore);
		System.out.println(mov.toString());
	}

	public String getCodiceComunicazionePrec() {
		return codiceComunicazionePrec;
	}

}
