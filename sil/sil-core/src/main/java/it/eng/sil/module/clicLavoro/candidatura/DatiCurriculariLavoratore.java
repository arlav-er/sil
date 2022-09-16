/**
 * 
 */
package it.eng.sil.module.clicLavoro.candidatura;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.webservices.clicLavoro.CLUtility;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

/**
 * @author iescone
 *
 */
public class DatiCurriculariLavoratore extends CLUtility {
	static Logger _logger = Logger.getLogger(DatiCurriculariLavoratore.class.getName());
	Object[] inputParameters;
	private String dataInvio;
	private String tipoComunicazioneCL;

	public DatiCurriculariLavoratore(Document doc, BigDecimal cdnLavoratore, String dataInvio,
			String tipoComunicazione) {
		this.doc = doc;
		this.dataInvio = dataInvio;
		this.tipoComunicazioneCL = tipoComunicazione;
		inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;
	}

	/**
	 * Estrae tutti i campi relativi alle esperienze lavorative dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementEsperienzeLavorative(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Object[] inputParam = new Object[5];
		inputParam[0] = dataInvio;
		inputParam[1] = dataInvio;
		inputParam[2] = dataInvio;
		inputParam[3] = dataInvio;
		inputParam[4] = inputParameters[0];
		String namestatement = "";
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			// ridefinizione parametri input
			inputParam = new Object[5];
			inputParam[0] = dataInvio;
			inputParam[1] = dataInvio;
			inputParam[2] = dataInvio;
			inputParam[3] = dataInvio;
			inputParam[4] = inputParameters[0];
			namestatement = "CL_GET_ESPERIENZA_LAV";
		} else {
			namestatement = "CL_GET_ESPERIENZA_LAV_BLEN";
		}
		SourceBean ret = executeSelect(namestatement, inputParam, dc);

		// Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "tipoesperienza", false, tipoesperienzaCheck,
				"\"tipo esperienze lavorative\"");
		XMLValidator.checkFieldExists(ret, "qualificasvolta", false, qualificaCheck, "\"qualifica svolta \"");
		XMLValidator.checkFieldExists(ret, "descrqualificasvolta", false, lengthRangeCheck(0, 250),
				"\"descrizione qualifica svolta\"");
		XMLValidator.checkFieldExists(ret, "principalimansioni", false, lengthRangeCheck(0, 250),
				"\"principali mansioni\"");
		XMLValidator.checkFieldExists(ret, "nomedatore", false, lengthRangeCheck(0, 100), "\"nome datore di lavoro\"");
		XMLValidator.checkFieldExists(ret, "datainizio", false, dataCheck, "\"data inizio esperienze lavorative\"");
		XMLValidator.checkFieldExists(ret, "datafine", false, dataCheck, "\"data fine esperienze lavorative\"");
		XMLValidator.checkFieldExists(ret, "indirizzodatore", false, lengthRangeCheck(0, 100),
				"\"indirizzo datore di lavoro\"");
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			XMLValidator.checkFieldExists(ret, "tiporapporto", false, lengthRangeCheck(0, 8),
					"\"codice del contratto\"");
			XMLValidator.checkFieldExists(ret, "areaesperienza", false, lengthRangeCheck(0, 8), "\"codice dell'area\"");
			XMLValidator.checkFieldExists(ret, "codateco", false, lengthRangeCheck(0, 8), "\"codice ateco dell'area\"");
			XMLValidator.checkFieldExists(ret, "codmvcessazione", false, lengthRangeCheck(0, 2),
					"\"motivo cessazione del rapporto\"");
			XMLValidator.checkFieldExists(ret, "altrocessazione", false, lengthRangeCheck(0, 100),
					"\"altro motivo della cessazione del rapporto\"");
			XMLValidator.checkFieldExists(ret, "pivadatorelavoro", false, codiceFiscaleCheck,
					"\"partita iva datore di lavoro\"");
			XMLValidator.checkFieldExists(ret, "cfdatorelavoro", false, codiceFiscaleCheck,
					"\"codice fiscale datore di lavoro\"");
			XMLValidator.checkFieldExists(ret, "codcomunedatore", false, comuneCheck, "\"comune datore di lavoro\"");
			XMLValidator.checkFieldExists(ret, "codmansione", false, lengthRangeCheck(0, 8),
					"\"codice della mansione esperienza lavorativa\"");
		}

		generateTag(element, TAG_ESPERIENZELAVORATIVE, ret);

		return element;
	}

	/**
	 * Estrae tutti i campi relativi all'istruzioni dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementIstruzioni(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Object[] inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = inputParameters[0];
		String namestatement = "";
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			inputParam = new Object[3];
			inputParam[0] = dataInvio;
			inputParam[1] = dataInvio;
			inputParam[2] = inputParameters[0];
			namestatement = "CL_GET_ISTRUZIONI";
		} else {
			namestatement = "CL_GET_ISTRUZIONI_BLEN";
		}
		SourceBean ret = executeSelect(namestatement, inputParam, dc);

		// Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "titolostudio", true, titoloStudioCheck, "\"titolo di studio\"");
		XMLValidator.checkFieldExists(ret, "descrizioneistruzione", false, lengthRangeCheck(0, 100),
				"\"descrizione titolo di studio\"");
		XMLValidator.checkFieldExists(ret, "votazione", false, lengthRangeCheck(0, 30),
				"\"votazione titolo di studio\"");
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			XMLValidator.checkFieldExists(ret, "specifica", false, lengthRangeCheck(0, 200),
					"\"descrizione libera di specifica del titolo di studio\"");
			XMLValidator.checkFieldExists(ret, "stato", false, lengthRangeCheck(0, 1),
					"\"codice di stato del Titolo di studio\"");
			XMLValidator.checkFieldExists(ret, "annoistr", false, numerico1_4Check,
					"\"anno di conseguimento del titolo\"");
			XMLValidator.checkFieldExists(ret, "nomeistituto", false, lengthRangeCheck(0, 100),
					"\"nome dell'Istituto Scolastico\"");
			XMLValidator.checkFieldExists(ret, "codcomistituto", false, comuneCheck,
					"\"comune in cui ha sede l'istituto scolastico\"");
			XMLValidator.checkFieldExists(ret, "codtitolo", false, lengthRangeCheck(0, 8), "\"codice titolo studio\"");
		}

		generateTag(element, TAG_ISTRUZIONE, ret);

		return element;
	}

	/**
	 * Estrae tutti i campi relativi alle formazioni dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementFormazioni(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Object[] inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = inputParameters[0];
		String namestatement = "";
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			namestatement = "CL_GET_FORMAZIONE";
		} else {
			namestatement = "CL_GET_FORMAZIONE_BLEN";
		}
		SourceBean ret = executeSelect(namestatement, inputParam, dc);

		// Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "titolocorso", false, lengthRangeCheck(0, 100),
				"\"titolo corso di formazione\"");
		XMLValidator.checkFieldExists(ret, "idsede", false, comuneCheck, "\"sede corso di formazione\"");
		XMLValidator.checkFieldExists(ret, "durata", false, durataCheck, "\"durata corso di formazione\"");
		XMLValidator.checkFieldExists(ret, "idtipologiadurata", false, tipodurataCheck,
				"\"tipologia durata corso di formazione\"");
		XMLValidator.checkFieldExists(ret, "idattestazione", false, attestazionedurataCheck,
				"\"attestazione corso di formazione\"");
		XMLValidator.checkFieldExists(ret, "idqualifica", false, qualificaCheck, "\"qualifica corso di formazione\"");
		XMLValidator.checkFieldExists(ret, "descrqualifica", false, lengthRangeCheck(0, 250),
				"\"descrizione qualifica corso di formazione\"");
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			XMLValidator.checkFieldExists(ret, "codice", false, lengthRangeCheck(0, 13),
					"\"codice corso di formazione\"");
			XMLValidator.checkFieldExists(ret, "annoform", false, numerico1_4Check, "\"anno corso di formazione\"");
			XMLValidator.checkFieldExists(ret, "descrizione", false, lengthRangeCheck(0, 300),
					"\"descrizione corso di formazione\"");
			XMLValidator.checkFieldExists(ret, "contenuto", false, lengthRangeCheck(0, 100),
					"\"contenuto corso di formazione\"");
			XMLValidator.checkFieldExists(ret, "sede", false, lengthRangeCheck(0, 100), "\"ente corso di formazione\"");
			XMLValidator.checkFieldExists(ret, "completato", false, siNoCheck,
					"\"flag completato corso di formazione\"");
			XMLValidator.checkFieldExists(ret, "idattestazionesil", false, lengthRangeCheck(0, 2),
					"\"codice sil tipo certificato corso di formazione\"");
			XMLValidator.checkFieldExists(ret, "cdnambitodisciplinare", false, numerico1_4Check,
					"\"ambito disciplinare corso di formazione\"");
		}

		generateTag(element, TAG_FORMAZIONE, ret);

		return element;
	}

	/**
	 * Estrae tutti i campi relativi alle lingue dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementLingue(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		String namestatement = "";
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			namestatement = "CL_GET_LINGUE";
		} else {
			namestatement = "CL_GET_LINGUE_BLEN";
		}
		SourceBean ret = executeSelect(namestatement, inputParameters, dc);

		// Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "idlingua", true, linguaCheck, "\"lingua conosciuta\"");
		XMLValidator.checkFieldExists(ret, "idlivelloletto", false, livelloLinguaCheck, "\"livello lingua  (letto)\"");
		XMLValidator.checkFieldExists(ret, "idlivelloscritto", false, livelloLinguaCheck,
				"\"livello lingua  (scritto)\"");
		XMLValidator.checkFieldExists(ret, "idlivelloparlato", false, livelloLinguaCheck,
				"\"livello lingua  (parlato)\"");
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			XMLValidator.checkFieldExists(ret, "idlivellolettosil", false, numerico1_4Check,
					"\"livello lingua  (letto sil)\"");
			XMLValidator.checkFieldExists(ret, "idlivelloscrittosil", false, numerico1_4Check,
					"\"livello lingua  (scritto sil)\"");
			XMLValidator.checkFieldExists(ret, "idlivelloparlatosil", false, numerico1_4Check,
					"\"livello lingua  (parlato sil)\"");
			XMLValidator.checkFieldExists(ret, "certificata", false, siNoCheck, "\"certificazione lingua\"");
			XMLValidator.checkFieldExists(ret, "codmodlingua", false, lengthRangeCheck(0, 8),
					"\"Modalità di acquisizione lingua\"");
			XMLValidator.checkFieldExists(ret, "altramodlingua", false, lengthRangeCheck(0, 100),
					"\"Specifica modalità di apprendimento lingua non prevista\"");
		}

		generateTag(element, TAG_LINGUE, ret);

		return element;
	}

	/**
	 * Estrae tutti i campi relativi le conoscenze Informatiche dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementConoscInformatiche(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Object[] inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = inputParameters[0];
		SourceBean ret = executeSelect("CL_GET_CONOSC_INFORM", inputParam, dc);
		int numConoscenze = ret.getAttributeAsVector("ROW").size();
		if (numConoscenze > 0) {
			SourceBean ret2 = groupByFirstColumnWithoutEndingSeparator(ret, "tipoconoscenza", 1000);

			// Validazione per la parte applicativa
			XMLValidator.checkFieldExists(ret, "tipoconoscenza", true, lengthRangeCheck(0, 1000),
					"\"tipo conoscenza\"");

			Element child = generateTagNotClosed(element, null, TAG_CONOSCENZEINFORMATICHE, ret2);

			SourceBean ret3 = executeSelect("CL_GET_CONOSC_INFORM_SPEC", inputParameters, dc);
			SourceBean ret4 = groupByColumn(ret3, "specifiche", 1000);

			// Validazione per la parte applicativa
			XMLValidator.checkFieldExists(ret, "specifiche", false, lengthRangeCheck(0, 1000),
					"\"eventuali specifiche\"");

			child = generateTagNotClosed(element, child, TAG_CONOSCENZEINFORMATICHE, ret4);
			closedTag(element, child);
		}

		return element;
	}

	/**
	 * Estrae tutti i campi relativi le abilitazioni(patenti) dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementAbilitazPatenti(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		Object[] inputParam = null;
		SourceBean temp = null;

		inputParam = new Object[2];
		inputParam[0] = inputParameters[0];
		inputParam[1] = dataInvio;

		ArrayList<SourceBean> sourceBeans = new ArrayList<SourceBean>();
		temp = executeSelect("CL_GET_ALBI", inputParam, dc);
		XMLValidator.checkFieldExists(temp, "idalbo", false, albiCheck, "\"iscrizione albi\"");
		if (temp != null)
			sourceBeans.add(temp);

		inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = inputParameters[0];

		temp = executeSelect("CL_GET_PATENTI", inputParam, dc);
		XMLValidator.checkFieldExists(temp, "idpatenteguida", false, patentiCheck, "\"possesso patente\"");
		if (temp != null)
			sourceBeans.add(temp);

		temp = executeSelect("CL_GET_PATENTINO", inputParam, dc);
		XMLValidator.checkFieldExists(temp, "idpatententino", false, patentiCheck, "\"possesso patentino\"");
		if (temp != null)
			sourceBeans.add(temp);
		// Validazione per la parte applicativa

		SourceBean[] strArray = new SourceBean[sourceBeans.size()];
		generateTag(element, TAG_ABILITAZIONIPATENTI, sourceBeans.toArray(strArray));

		return element;
	}

	/**
	 * Estrae tutti i campi relativi alle lingue dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementProfessDesiderata(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Object[] inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = inputParameters[0];
		SourceBean retMans = executeSelect("CL_GET_DIS_CONTRATTO_MANSIONI", inputParam, dc);
		Vector<SourceBean> rows = retMans.getAttributeAsVector("ROW");
		if (rows.size() == 0) {
			throw new MandatoryFieldException("\"qualifica professionale \" ");
		}
		// per ogni record presente nel sourceBean
		for (SourceBean row : rows) {
			String prgMansione = row.getAttribute("prgmansione").toString();
			inputParam = new Object[3];
			inputParam[0] = dataInvio;
			inputParam[1] = inputParameters[0];
			inputParam[2] = prgMansione;
			SourceBean ret = executeSelect("CL_GET_PROF_DESID", inputParam, dc);

			Vector<SourceBean> rowsRet = ret.getAttributeAsVector("ROW");
			boolean checkProfessione = false;
			if (rowsRet.size() > 0) {
				SourceBean sourceBean = rowsRet.get(0);
				if (sourceBean.containsAttribute("idprofessione")) {
					String idProfessione = sourceBean.getAttribute("idprofessione").toString();
					if (!StringUtils.isEmptyNoBlank(idProfessione)) {
						checkProfessione = true;
					}
				}
			} else {
				throw new MandatoryFieldException("\"professione desiderata\" ");
			}
			if (checkProfessione) {
				// Validazione per la parte applicativa
				XMLValidator.checkFieldExists(ret, "idprofessione", true, qualificaCheck, "\"professione desiderata\"");
				XMLValidator.checkFieldExists(ret, "descrprofessione", true, lengthRangeCheck(0, 250),
						"\"descrittivo desiderata\"");
				XMLValidator.checkFieldExists(ret, "descrizioneprofessione", false, lengthRangeCheck(0, 300),
						"\"descrizione professione desiderata\"");
				XMLValidator.checkFieldExists(ret, "esperienzasettore", false, siNoCheck, "\"esperienza nel settore\"");
				XMLValidator.checkFieldExists(ret, "codmansione", false, lengthRangeCheck(0, 8),
						"\"codice mansione professione desiderata\"");

				Element child = generateTagNotClosed(element, null, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret);
				Element rootProfDesiderata = child;

				Object[] inputParam1 = new Object[1];
				inputParam1[0] = prgMansione;
				SourceBean ret1 = executeSelect("CL_GET_MANSIONE_DESCRIZIONEESPERIENZA", inputParam1, dc);
				int numEsperienze = ret1.getAttributeAsVector("ROW").size();
				if (numEsperienze > 0) {
					SourceBean ret2 = groupByColumnWithSeparator(ret1, "descrizioneesperienza", 300, "; ");
					XMLValidator.checkFieldExists(ret2, "descrizioneesperienza", true, lengthRangeCheck(0, 300),
							"\"descrizioneesperienza\"");
					child = generateTagNotClosed(element, child, "descrizioneesperienza", ret2);
				}

				Object[] inputParam12 = new Object[3];
				inputParam12[0] = dataInvio;
				inputParam12[1] = inputParameters[0];
				inputParam12[2] = prgMansione;
				SourceBean ret12 = executeSelect("CL_GET_TRASFERTE", inputParam12, dc);
				XMLValidator.checkFieldExists(ret12, "trasferte", false, modalitaCheck, "\"trasferte\"");
				child = generateTagNotClosed(element, child, "trasferte", ret12);

				Object[] inputParam2 = new Object[2];
				inputParam2[0] = inputParameters[0];
				inputParam2[1] = prgMansione;
				SourceBean ret2 = executeSelect("CL_GET_PROF_DESID_ORARIO", inputParam2, dc);
				// Validazione per la parte applicativa
				XMLValidator.checkFieldExists(ret2, "idmodalitalavorativa", false, modalitaCheck,
						"\"modalita di lavoro\"");
				child = generateTagNotClosed(element, child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret2);

				if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
						&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
					SourceBean ret2Sil = executeSelect("CL_GET_PROF_DESID_ORARIO_SIL", inputParam2, dc);
					// Validazione per la parte applicativa
					XMLValidator.checkFieldExists(ret2Sil, "idmodalitalavorativasil", false, lengthRangeCheck(0, 8),
							"\"modalita di lavoro sil\"");
					child = generateTagNotClosed(element, child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret2Sil);
				}

				SourceBean ret3 = executeSelect("CL_GET_PROF_DESID_TIPI_CONTRATTO", inputParam2, dc);
				// Validazione per la parte applicativa
				XMLValidator.checkFieldExists(ret3, "idtipologiacontratto", false, rapportiCheck,
						"\"tipologie contrattuali\"");
				child = generateTagNotClosed(element, child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret3);

				if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
						&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
					SourceBean ret3Sil = executeSelect("CL_GET_PROF_DESID_TIPI_CONTRATTO_SIL", inputParam2, dc);
					// Validazione per la parte applicativa
					XMLValidator.checkFieldExists(ret3Sil, "idtipologiacontrattosil", false, lengthRangeCheck(0, 8),
							"\"tipologia contratto sil\"");
					child = generateTagNotClosed(element, child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret3Sil);
				}

				SourceBean ret4 = executeSelect("CL_GET_PROF_DESID_MEZZI_TRASPORTO", inputParam2, dc);
				// Validazione per la parte applicativa
				XMLValidator.checkFieldExists(ret4, "mezzitrasporto", false, siNoCheck,
						"\"disponibilità di utilizzo mezzo di trasporto\"");
				child = generateTagNotClosed(element, child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret4);

				if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
						&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
					Element disponibilitaElement = doc.createElement(TAG_DISPONIBILITA);
					DatiDisponibilitaProfLavoratore datiDisponibilita = new DatiDisponibilitaProfLavoratore(doc,
							new BigDecimal(prgMansione), dataInvio);
					datiDisponibilita.createElementDatiDisponibilitaTurni(disponibilitaElement, dc);
					datiDisponibilita.createElementDatiDisponibilitaTerritorio(disponibilitaElement, dc);
					datiDisponibilita.createElementDatiDisponibilitaMobilita(disponibilitaElement, dc);
					if (datiDisponibilita.getInsertTagDisponibilita()) {
						rootProfDesiderata.appendChild(disponibilitaElement);
					}
				}

				closedTag(element, child);
			}
		}

		return element;
	}

	public Element createElementProfessDesiderataInvioMassivo(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Object[] inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = inputParameters[0];
		SourceBean retMans = executeSelect("CL_GET_DIS_CONTRATTO_MANSIONI", inputParam, dc);
		Vector<SourceBean> rows = retMans.getAttributeAsVector("ROW");
		/*
		 * if (rows.size() == 0) { throw new MandatoryFieldException("\"qualifica professionale \" "); }
		 */
		// per ogni record presente nel sourceBean
		if (rows.size() != 0) {
			for (SourceBean row : rows) {
				String prgMansione = row.getAttribute("prgmansione").toString();
				inputParam = new Object[3];
				inputParam[0] = dataInvio;
				inputParam[1] = inputParameters[0];
				inputParam[2] = prgMansione;
				SourceBean ret = executeSelect("CL_GET_PROF_DESID", inputParam, dc);

				Vector<SourceBean> rowsRet = ret.getAttributeAsVector("ROW");
				boolean checkProfessione = false;
				if (rowsRet.size() > 0) {
					SourceBean sourceBean = rowsRet.get(0);
					if (sourceBean.containsAttribute("idprofessione")) {
						String idProfessione = sourceBean.getAttribute("idprofessione").toString();
						if (!StringUtils.isEmptyNoBlank(idProfessione)) {
							checkProfessione = true;
						}
					}
				}
				/*
				 * else{ throw new MandatoryFieldException("\"professione desiderata\" "); }
				 */
				if (checkProfessione) {
					// Validazione per la parte applicativa
					XMLValidator.checkFieldExists(ret, "idprofessione", true, qualificaCheck,
							"\"professione desiderata\"");
					XMLValidator.checkFieldExists(ret, "descrprofessione", true, lengthRangeCheck(0, 250),
							"\"descrittivo desiderata\"");
					XMLValidator.checkFieldExists(ret, "descrizioneprofessione", false, lengthRangeCheck(0, 300),
							"\"descrizione professione desiderata\"");
					XMLValidator.checkFieldExists(ret, "esperienzasettore", false, siNoCheck,
							"\"esperienza nel settore\"");
					XMLValidator.checkFieldExists(ret, "codmansione", false, lengthRangeCheck(0, 8),
							"\"codice mansione professione desiderata\"");

					Element child = generateTagNotClosed(element, null, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret);
					Element rootProfDesiderata = child;

					Object[] inputParam1 = new Object[1];
					inputParam1[0] = prgMansione;
					SourceBean ret1 = executeSelect("CL_GET_MANSIONE_DESCRIZIONEESPERIENZA", inputParam1, dc);
					int numEsperienze = ret1.getAttributeAsVector("ROW").size();
					if (numEsperienze > 0) {
						SourceBean ret2 = groupByColumnWithSeparator(ret1, "descrizioneesperienza", 300, "; ");
						XMLValidator.checkFieldExists(ret2, "descrizioneesperienza", true, lengthRangeCheck(0, 300),
								"\"descrizioneesperienza\"");
						child = generateTagNotClosed(element, child, "descrizioneesperienza", ret2);
					}

					Object[] inputParam12 = new Object[3];
					inputParam12[0] = dataInvio;
					inputParam12[1] = inputParameters[0];
					inputParam12[2] = prgMansione;
					SourceBean ret12 = executeSelect("CL_GET_TRASFERTE", inputParam12, dc);
					XMLValidator.checkFieldExists(ret12, "trasferte", false, modalitaCheck, "\"trasferte\"");
					child = generateTagNotClosed(element, child, "trasferte", ret12);

					Object[] inputParam2 = new Object[2];
					inputParam2[0] = inputParameters[0];
					inputParam2[1] = prgMansione;
					/*
					 * 
					 * SourceBean ret2= executeSelect("CL_GET_PROF_DESID_ORARIO", inputParam2, dc); //Validazione per la
					 * parte applicativa XMLValidator.checkFieldExists(ret2, "idmodalitalavorativa", false,
					 * modalitaCheck, "\"modalita di lavoro\""); child = generateTagNotClosed(element, child,
					 * TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret2);
					 * 
					 * if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("") &&
					 * tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) { SourceBean ret2Sil=
					 * executeSelect("CL_GET_PROF_DESID_ORARIO_SIL", inputParam2, dc); //Validazione per la parte
					 * applicativa XMLValidator.checkFieldExists(ret2Sil, "idmodalitalavorativasil", false,
					 * lengthRangeCheck(0, 8), "\"modalita di lavoro sil\""); child = generateTagNotClosed(element,
					 * child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret2Sil); }
					 * 
					 * SourceBean ret3= executeSelect("CL_GET_PROF_DESID_TIPI_CONTRATTO", inputParam2, dc);
					 * //Validazione per la parte applicativa XMLValidator.checkFieldExists(ret3,
					 * "idtipologiacontratto", false, rapportiCheck, "\"tipologie contrattuali\""); child =
					 * generateTagNotClosed(element, child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret3);
					 * 
					 * if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("") &&
					 * tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) { SourceBean ret3Sil=
					 * executeSelect("CL_GET_PROF_DESID_TIPI_CONTRATTO_SIL", inputParam2, dc); //Validazione per la
					 * parte applicativa XMLValidator.checkFieldExists(ret3Sil, "idtipologiacontrattosil", false,
					 * lengthRangeCheck(0, 8), "\"tipologia contratto sil\""); child = generateTagNotClosed(element,
					 * child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret3Sil); }
					 */
					SourceBean ret4 = executeSelect("CL_GET_PROF_DESID_MEZZI_TRASPORTO", inputParam2, dc);
					// Validazione per la parte applicativa
					XMLValidator.checkFieldExists(ret4, "mezzitrasporto", false, siNoCheck,
							"\"disponibilità di utilizzo mezzo di trasporto\"");
					child = generateTagNotClosed(element, child, TAG_PROFESSIONEDESIDERATADISPONIBILITA, ret4);

					if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
							&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
						Element disponibilitaElement = doc.createElement(TAG_DISPONIBILITA);
						DatiDisponibilitaProfLavoratore datiDisponibilita = new DatiDisponibilitaProfLavoratore(doc,
								new BigDecimal(prgMansione), dataInvio);
						// datiDisponibilita.createElementDatiDisponibilitaTurni(disponibilitaElement, dc);
						// datiDisponibilita.createElementDatiDisponibilitaTerritorio(disponibilitaElement, dc);
						datiDisponibilita.createElementDatiDisponibilitaMobilita(disponibilitaElement, dc);
						if (datiDisponibilita.getInsertTagDisponibilita()) {
							rootProfDesiderata.appendChild(disponibilitaElement);
						}
					}
					closedTag(element, child);
				}
			}
		}
		return element;
	}

	public String getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(String dataInvio) {
		this.dataInvio = dataInvio;
	}

	public void setTipoComunicazioneCL(String tipoComunicazione) {
		this.tipoComunicazioneCL = tipoComunicazione;
	}

}
