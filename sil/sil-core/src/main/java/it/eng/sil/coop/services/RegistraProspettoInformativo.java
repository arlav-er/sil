/*
 * Created on Jan 24, 2008
 */
package it.eng.sil.coop.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.AzReferenteBean;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.cig.bean.AziendaBean;
import it.eng.sil.cig.bean.UnitaAziendaBean;
import it.eng.sil.coop.CoopApplicationException_Lavoratore;
import it.eng.sil.coop.bean.ProspettoInformativo;
import it.eng.sil.coop.services.logger.DBErrorLogger;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.StatementUtils;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
@SuppressWarnings("unchecked")
public class RegistraProspettoInformativo implements IFaceService {

	private static final Logger _logger = Logger.getLogger(RegistraProspettoInformativo.class.getName());
	private static final DBErrorLogger _dbLogger = new DBErrorLogger();
	private static final int maxLengthRagSoc = 100;
	private static final int numMaxTentativiProtocollazione = 3;
	private RequestContainer rc;

	public static final int ACQUISIZIONE_PROSPETTO = 1;
	public static final int RETTIFICA_PROSPETTO = 2;
	public static final int ANNULLAMENTO_PROSPETTO = 3;

	private BigDecimal prgAziendaIn = null;
	private BigDecimal prgUnitaIn = null;
	private String cfAzIn = null;
	private String prgKeyProspettoInf = null;

	String prospettoXML = null;

	String cdnUt = "190";
	// DONA 25/02/2010 X DOCAREA
	// RequestContainer rc = null;

	String noteIndirizzoUnita = null;
	RequestContextIFace rcIFace = null;
	SourceBean res = null;
	ResponseContainer rs = null;
	SourceBean prospettoBean = null;
	String codComunicazione = "";
	Integer tipoComunicazione = null; /*
										 * tipo comunicazione, può essere Prospetto Informativo=1, Rettifica=2,
										 * Annullamento=3
										 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.services.IFaceService#send(javax.jms.Message)
	 */
	public void send(Message msg) throws CoopApplicationException_Lavoratore, JMSException {
		_logger.debug("Prospetto Informativo... ");

		TransactionQueryExecutor tex = null;
		boolean protocolla = true;

		try {
			ObjectMessage message = (ObjectMessage) msg;
			Serializable arrObj = message.getObject();
			List l = (List) arrObj;
			prospettoXML = (String) l.get(0);
			prospettoBean = SourceBean.fromXMLString(prospettoXML);
			codComunicazione = (String) prospettoBean.getAttribute("DATIGENERALI.codiceComunicazione");
			// String validityErrors = UtilityXml.getValidityErrors(prospettoXML, "comPI_SILP.xsd");
			// if (validityErrors != null) {
			// _logger.info("Errori di validazione sull'xml:" + validityErrors);
			// }
			String tipoComunicazioneStr = (String) prospettoBean.getAttribute("DATIGENERALI.tipoComunicazione");
			tipoComunicazione = Integer.valueOf(tipoComunicazioneStr);

			synchronized (this) {
				switch (tipoComunicazione) {
				case ACQUISIZIONE_PROSPETTO:
					_logger.info("=== " + codComunicazione + " - Acquisizione nuovo prospetto ===");
					protocolla = false;
					TransactionQueryExecutor texAcquisizione = null;
					try {
						// Prima transazione
						texAcquisizione = new TransactionQueryExecutor(Values.DB_SIL_DATI);
						texAcquisizione.initTransaction();
						acquisisciProspetto(prospettoBean, texAcquisizione, protocolla);
						texAcquisizione.commitTransaction();
						boolean isOKPR = false;
						int iTentativo = 1;
						_logger.debug("Before protocollaDocumento");
						while (!isOKPR && iTentativo <= numMaxTentativiProtocollazione) {
							try {
								// Seconda transazione
								texAcquisizione = new TransactionQueryExecutor(Values.DB_SIL_DATI);
								texAcquisizione.initTransaction();
								Documento doc = protocollaDocumento(this.prgAziendaIn, this.prgUnitaIn, this.cfAzIn,
										this.prgKeyProspettoInf, texAcquisizione);
								_logger.debug("Post protocollaDocumento");
								ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
								isOKPR = true;
								texAcquisizione.commitTransaction();
								iTentativo = numMaxTentativiProtocollazione + 1;
							} catch (Exception e) {
								texAcquisizione.rollBackTransaction();
								_logger.error("RegistraProspettoInformativo: fallita protocollazione. \n", e);
								iTentativo = iTentativo + 1;
							}
						}
						if (isOKPR) {
							_logger.info("Inserito un prospetto informativo con prgProspetto " + this.prgKeyProspettoInf
									+ " e codComunicazione " + codComunicazione + ".");
						} else {
							_logger.error(
									"RegistraProspettoInformativo: problema con la protocollazione in fase di acquisizione prgProspetto "
											+ this.prgKeyProspettoInf + " e codComunicazione " + codComunicazione + ".");
							;
							_logger.info("Aggiorno le note del prospetto " + this.prgKeyProspettoInf);
							// aggiornane le note
							String note = "Errore in Protocollazione per concorrenza";
							Object[] param = new Object[3];
							param[0] = cdnUt;
							param[1] = note;
							param[2] = this.prgKeyProspettoInf;
							QueryExecutor.executeQuery("CM_RETTIFICA_PROSPETTO_NOTE", param, "UPDATE", Values.DB_SIL_DATI);
						}
					} catch (Exception ex) {
						try {
							if (texAcquisizione != null) {
								texAcquisizione.rollBackTransaction();
							}
						} catch (EMFInternalError ex1) {
							_logger.error("RegistraProspettoInformativo: problema con la rollback", ex1);
						}
	
						_logger.error("RegistraProspettoInformativo: fallito. \n" + prospettoXML, ex);
	
						try {
							_dbLogger.log(_logger, "CM", codComunicazione, prospettoXML, "-1", ex.getMessage());
						} catch (Exception sExLog) {
							_logger.error("RegistraProspettoInformativo: log su db fallito. \n", sExLog);
						}
					}
					break;
				case RETTIFICA_PROSPETTO:
					TransactionQueryExecutor texRettifica = null;
					try {
						// -- Inizializzo il TransactionQueryExecutor
						texRettifica = new TransactionQueryExecutor(Values.DB_SIL_DATI);
						// Prima transazione
						texRettifica.initTransaction();
						_logger.info("=== " + codComunicazione + " - Rettifica prospetto precedentemente acquisito ===");
						protocolla = false;
						ProspettoInformativo prospettoInserito = acquisisciProspetto(prospettoBean, texRettifica,
								protocolla);
						rettificaProspetto(prospettoInserito, texRettifica);
						// -- COMMIT TRANSAZIONE
						texRettifica.commitTransaction();
						boolean isOKPR = false;
						int iTentativo = 1;
						_logger.debug("Before protocollaDocumento");
						while (!isOKPR && iTentativo <= numMaxTentativiProtocollazione) {
							try {
								// Seconda transazione
								texRettifica = new TransactionQueryExecutor(Values.DB_SIL_DATI);
								texRettifica.initTransaction();
								Documento doc = protocollaDocumento(this.prgAziendaIn, this.prgUnitaIn, this.cfAzIn,
										this.prgKeyProspettoInf, texRettifica);
								_logger.debug("Post protocollaDocumento");
								ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
								isOKPR = true;
								texRettifica.commitTransaction();
								iTentativo = numMaxTentativiProtocollazione + 1;
							} catch (Exception e) {
								texRettifica.rollBackTransaction();
								_logger.error("RegistraProspettoInformativo: fallita protocollazione. \n", e);
								iTentativo = iTentativo + 1;
							}
						}
						if (isOKPR) {
							_logger.info("Inserito un prospetto informativo con prgProspetto " + this.prgKeyProspettoInf
									+ " e codComunicazione " + codComunicazione + ".");
						} else {
							_logger.error(
									"RegistraProspettoInformativo: problema con la protocollazione in fase di rettifica prgProspetto "
											+ this.prgKeyProspettoInf + " e codComunicazione " + codComunicazione + ".");
							;
							_logger.info("Aggiorno le note del prospetto " + this.prgKeyProspettoInf);
							// aggiornane le note
							String note = "Errore in Protocollazione per concorrenza";
							Object[] param = new Object[3];
							param[0] = cdnUt;
							param[1] = note;
							param[2] = this.prgKeyProspettoInf;
							QueryExecutor.executeQuery("CM_RETTIFICA_PROSPETTO_NOTE", param, "UPDATE", Values.DB_SIL_DATI);
						}
					} catch (Exception ex) {
						try {
							if (texRettifica != null) {
								texRettifica.rollBackTransaction();
							}
						} catch (EMFInternalError ex1) {
							_logger.error("RegistraProspettoInformativo: problema con la rollback", ex1);
						}
	
						_logger.error("RegistraProspettoInformativo: fallito. \n" + prospettoXML, ex);
	
						try {
							_dbLogger.log(_logger, "CM", codComunicazione, prospettoXML, "-1", ex.getMessage());
						} catch (Exception sExLog) {
							_logger.error("RegistraProspettoInformativo: log su db fallito. \n", sExLog);
						}
					}
					break;
				case ANNULLAMENTO_PROSPETTO:
					try {
						// -- Inizializzo il TransactionQueryExecutor
						tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
						tex.initTransaction();
						_logger.info("=== " + codComunicazione + " - Annullamento prospetto precedentemente acquisito ===");
						annullaProspetto(prospettoBean, tex);
						// -- COMMIT TRANSAZIONE
						tex.commitTransaction();
					} catch (Exception e) {
						try {
							if (tex != null) {
								tex.rollBackTransaction();
							}
						} catch (EMFInternalError e1) {
							_logger.error("RegistraProspettoInformativo: problema con la rollback", e);
						}
					}
					break;
				default:
					_logger.error("Tipo comunicazione '" + tipoComunicazione + "' sconosciuto.");
					break;
				}
			}

			_logger.info("=== " + codComunicazione + " - Acquisizione terminata ===\n");
		}

		catch (Exception e) {
			_logger.error("RegistraProspettoInformativo: fallito. \n" + prospettoXML, e);
			try {
				_dbLogger.log(_logger, "CM", codComunicazione, prospettoXML, "-1", e.getMessage());
			} catch (Exception se) {
				_logger.error("RegistraProspettoInformativo: log su db fallito. \n", se);
			}
		}
	}

	/**
	 * Acquisisce un prospetto
	 * 
	 * @param prospettoInformativo
	 * @param tex
	 * @return restituisce il prgProspettoInf della comunicazione appena inserita.
	 * @throws Exception
	 */
	private ProspettoInformativo acquisisciProspetto(SourceBean prospettoInformativo, TransactionQueryExecutor tex,
			boolean protocolla) throws Exception {
		String cfRiferimento = (String) prospettoBean.getAttribute("Azienda.strCodiceFiscale");
		String dataConsegnaProspetto = (String) prospettoBean.getAttribute("DATIGENERALI.datConsegnaProspetto");
		String dataProspetto = (String) prospettoBean.getAttribute("DATIGENERALI.datProspetto");

		StringBuffer strlog = new StringBuffer();
		strlog.append("ProspettoInformativo.codiceFiscaleAz=");
		strlog.append(cfRiferimento);
		strlog.append(", dataProspetto=");
		strlog.append(dataProspetto);
		strlog.append(", dataConsegnaProspetto=");
		strlog.append(dataConsegnaProspetto);

		_logger.debug(strlog.toString());

		//
		SourceBean cmProspettoInf = (SourceBean) prospettoBean.getAttribute("DATIGENERALI");

		if (!cmProspettoInf.containsAttribute("NUMORECCNL")) {
			cmProspettoInf.setAttribute("NUMORECCNL", "40");
		}

		String strNote = (String) cmProspettoInf.getAttribute("strNote");
		String datConsegnaProspetto = (String) cmProspettoInf.getAttribute("datConsegnaProspetto");
		String parteData = datConsegnaProspetto.substring(0, 10);
		String parteOra = datConsegnaProspetto.substring(11, 19);
		cmProspettoInf.updAttribute("datConsegnaProspetto", parteData + " " + parteOra);

		SourceBean riepilogo = (SourceBean) prospettoBean.getAttribute("RIEPILOGO");
		Vector attributi = riepilogo.getContainedAttributes();
		cmProspettoInf.updContainedAttributes(attributi);

		SourceBean autorizzazioni = null;

		autorizzazioni = (SourceBean) prospettoBean.getAttribute("AUTORIZZAZIONI");

		if (autorizzazioni != null) {
			SourceBean esonero = (SourceBean) autorizzazioni.getAttribute("ESONERO");
			SourceBean gradualita = (SourceBean) autorizzazioni.getAttribute("GRADUALITA");
			SourceBean sospensione = (SourceBean) autorizzazioni.getAttribute("SOSPENSIONE");
			SourceBean compensazioneTerritoriale = (SourceBean) autorizzazioni
					.getAttribute("COMPENSAZIONETERRITORIALE");
			SourceBean convenzione = (SourceBean) autorizzazioni.getAttribute("CONVENZIONE");
			SourceBean passaggioA15Dipendenti = (SourceBean) autorizzazioni.getAttribute("PassaggioA15Dipendenti");
			SourceBean cmEsoneri60xMille = (SourceBean) autorizzazioni.getAttribute("esoneri60permille");

			if (esonero != null) {
				attributi = esonero.getContainedAttributes();
				cmProspettoInf.updContainedAttributes(attributi);
			}

			if (gradualita != null) {
				attributi = gradualita.getContainedAttributes();
				cmProspettoInf.updContainedAttributes(attributi);
			}

			if (sospensione != null) {
				// flgSospensione, statoSosp, causaleSosp, numLavoratoriSosp
				attributi = sospensione.getContainedAttributes();
				cmProspettoInf.updContainedAttributes(attributi);
			}

			if (compensazioneTerritoriale != null) {
				attributi = compensazioneTerritoriale.getContainedAttributes();
				cmProspettoInf.updContainedAttributes(attributi);
			}

			if (convenzione != null) {
				attributi = convenzione.getContainedAttributes();
				cmProspettoInf.updContainedAttributes(attributi);
			}

			// il flag è presente solo per l'Umbria
			if (passaggioA15Dipendenti != null) {
				attributi = passaggioA15Dipendenti.getContainedAttributes();
				cmProspettoInf.updContainedAttributes(attributi);
			}

			if (cmEsoneri60xMille != null) {
				if (cmProspettoInf.containsAttribute("FLGESONEROAUTOCERT")) {
					cmProspettoInf.updAttribute("FLGESONEROAUTOCERT", "S");
				} else {
					cmProspettoInf.setAttribute("FLGESONEROAUTOCERT", "S");
				}
				// dataautocertificazione, numerolavoratori60permille, percentuale, numerolavoratoriesonero
				attributi = cmEsoneri60xMille.getContainedAttributes();
				cmProspettoInf.updContainedAttributes(attributi);
			} else {
				if (cmProspettoInf.containsAttribute("FLGESONEROAUTOCERT")) {
					cmProspettoInf.updAttribute("FLGESONEROAUTOCERT", "N");
				} else {
					cmProspettoInf.setAttribute("FLGESONEROAUTOCERT", "N");
				}
			}
		}

		SourceBean cmGradualita = (SourceBean) prospettoBean.getAttribute("Gradualita");
		if (cmGradualita != null) {
			// datatrasformazione che va a finire nella colonna DATCONCGRADUALITA, numPercGradualita
			attributi = cmGradualita.getContainedAttributes();
			cmProspettoInf.updContainedAttributes(attributi);
		}

		SourceBean cmSospensioneMobilita = (SourceBean) prospettoBean.getAttribute("SospensioneMobilita");
		if (cmSospensioneMobilita != null) {
			// flgSospensioneMobilita
			attributi = cmSospensioneMobilita.getContainedAttributes();
			cmProspettoInf.updContainedAttributes(attributi);

			String datFineSospMBNazionale = (String) cmProspettoInf.getAttribute("dataFineSospensione");
			if (datFineSospMBNazionale != null) {
				cmProspettoInf.delAttribute("dataFineSospensione");
				cmProspettoInf.setAttribute("datFineSospMBNazionale", datFineSospMBNazionale);
			}

		}

		SourceBean anAzienda = (SourceBean) prospettoBean.getAttribute("Azienda");
		//
		SourceBean anUnitaAzienda = (SourceBean) prospettoBean.getAttribute("Azienda.UnitaAzProvRif");
		//
		SourceBean cmEsclusioneProspetto = (SourceBean) prospettoBean.getAttribute("Esclusioni");
		//
		SourceBean cmPersonaleDipendente = (SourceBean) prospettoBean.getAttribute("personaledipendente");
		SourceBean cmPersonaleNonDipendente = (SourceBean) prospettoBean.getAttribute("personalenondipendente");

		if (cmPersonaleDipendente != null) {

			SourceBean categorieDisabiliTPSB = (SourceBean) cmPersonaleDipendente.getAttribute("disabili");
			if (categorieDisabiliTPSB != null) {
				String sDisabili = (String) categorieDisabiliTPSB.getAttribute("nlavoratoritempopieno");
				if (sDisabili != null) {
					BigDecimal disabili = new BigDecimal(sDisabili);
					if (cmEsclusioneProspetto.containsAttribute("NUMDISABILI")) {
						cmEsclusioneProspetto.updAttribute("NUMDISABILI", disabili);
					} else {
						cmEsclusioneProspetto.setAttribute("NUMDISABILI", disabili);
					}
				}
			}

			SourceBean categorieCentralinistiTPSB = (SourceBean) cmPersonaleDipendente.getAttribute("centralinisti");
			if (categorieCentralinistiTPSB != null) {
				String sNumCentNonVedentiInForza = (String) categorieCentralinistiTPSB
						.getAttribute("nlavoratoritempopieno");
				if (sNumCentNonVedentiInForza != null) {
					BigDecimal numCentNonVedentiInForza = new BigDecimal(sNumCentNonVedentiInForza);
					if (cmProspettoInf.containsAttribute("NUMCENTNONVEDENTIFORZA")) {
						cmProspettoInf.updAttribute("NUMCENTNONVEDENTIFORZA", numCentNonVedentiInForza);
					} else {
						cmProspettoInf.setAttribute("NUMCENTNONVEDENTIFORZA", numCentNonVedentiInForza);
					}
				}
			}

			SourceBean categorieMassoTPSB = (SourceBean) cmPersonaleDipendente.getAttribute("massofisioterapisti");
			if (categorieMassoTPSB != null) {
				String sNumMassoInForza = (String) categorieMassoTPSB.getAttribute("nlavoratoritempopieno");
				if (sNumMassoInForza != null) {
					BigDecimal numMassoInForza = new BigDecimal(sNumMassoInForza);
					if (cmProspettoInf.containsAttribute("NUMMASSOFISIOTERAPISTIFORZA")) {
						cmProspettoInf.updAttribute("NUMMASSOFISIOTERAPISTIFORZA", numMassoInForza);
					} else {
						cmProspettoInf.setAttribute("NUMMASSOFISIOTERAPISTIFORZA", numMassoInForza);
					}
				}
			}

			SourceBean categorieTelelavoroTPSB = (SourceBean) cmPersonaleDipendente.getAttribute("telelavoro");
			if (categorieTelelavoroTPSB != null) {
				String sNumTelelavoroInForza = (String) categorieTelelavoroTPSB.getAttribute("nlavoratoritempopieno");
				if (sNumTelelavoroInForza != null) {
					BigDecimal numTelelavoroInForza = new BigDecimal(sNumTelelavoroInForza);
					if (cmProspettoInf.containsAttribute("NUMTELELAVFT")) {
						cmProspettoInf.updAttribute("NUMTELELAVFT", numTelelavoroInForza);
					} else {
						cmProspettoInf.setAttribute("NUMTELELAVFT", numTelelavoroInForza);
					}
				}
			}
		}

		if (cmPersonaleNonDipendente != null) {

			SourceBean categorieDisSommTPSB = (SourceBean) cmPersonaleNonDipendente
					.getAttribute("lavoratoridisabilisomministrati");
			if (categorieDisSommTPSB != null) {
				String sDisabiliSomm = (String) categorieDisSommTPSB.getAttribute("nlavoratoritempopieno");
				if (sDisabiliSomm != null) {
					BigDecimal disabiliSomm = new BigDecimal(sDisabiliSomm);
					if (cmProspettoInf.containsAttribute("NUMSOMMFT")) {
						cmProspettoInf.updAttribute("NUMSOMMFT", disabiliSomm);
					} else {
						cmProspettoInf.setAttribute("NUMSOMMFT", disabiliSomm);
					}
				}
			}

			SourceBean categorieDisConvTPSB = (SourceBean) cmPersonaleNonDipendente
					.getAttribute("lavoratoridisabiliconv12bis14");
			if (categorieDisConvTPSB != null) {
				String sDisabiliConv = (String) categorieDisConvTPSB.getAttribute("nlavoratoritempopieno");
				if (sDisabiliConv != null) {
					BigDecimal disabiliConv = new BigDecimal(sDisabiliConv);
					if (cmProspettoInf.containsAttribute("NUMCONV12BIS14FT")) {
						cmProspettoInf.updAttribute("NUMCONV12BIS14FT", disabiliConv);
					} else {
						cmProspettoInf.setAttribute("NUMCONV12BIS14FT", disabiliConv);
					}
				}
			}
		}

		res = new SourceBean("SERVICE_RESPONSE");
		// DONA 25/02/2010 X DOCAREA
		// rc = new RequestContainer();
		// RequestContainer.setRequestContainer(rc);
		preparaContesto();
		rs = new ResponseContainer();
		rs.setErrorHandler(new EMFErrorHandler());
		SessionContainer session = new SessionContainer(false);

		session.setAttribute("_CDUT_", new BigDecimal(cdnUt));
		rc.setSessionContainer(session);
		rcIFace = new DefaultRequestContext(rc, rs);

		BigDecimal prgUnita = null;
		BigDecimal prgAzienda = null;
		String strIndirizzo = (String) anUnitaAzienda.getAttribute("strIndirizzo");
		String codCom = (String) anUnitaAzienda.getAttribute("codCom");
		String strPECemail = (String) anUnitaAzienda.getAttribute("strPECemail");

		// controlliamo l'esistenza di azienda e unita'
		String strCodiceFiscale = (String) anAzienda.getAttribute("strCodiceFiscale");
		SourceBean row = (SourceBean) tex.executeQuery("GET_PRG_AZIENDA", new Object[] { strCodiceFiscale }, "SELECT");
		prgAzienda = (BigDecimal) row.getAttribute("row.prgAzienda");
		if (prgAzienda == null) {
			String strRagioneSociale = (String) anAzienda.getAttribute("strRagioneSociale");
			if (strRagioneSociale != null && strRagioneSociale.length() > maxLengthRagSoc) {
				strRagioneSociale = strRagioneSociale.substring(0, maxLengthRagSoc);
			}
			String codTipoAzienda = (String) anAzienda.getAttribute("codTipoAzienda");
			//
			AziendaBean azienda = new AziendaBean(strCodiceFiscale, strRagioneSociale, codTipoAzienda, cdnUt, cdnUt,
					tex);
			azienda.setStrPartitaIva((String) anAzienda.getAttribute("strPartitaIva"));
			azienda.setStrNumAlboInterinali((String) anAzienda.getAttribute("strNumAlboInterinali"));
			azienda.setFlgObbligoL68((String) anAzienda.getAttribute("flgObbligoL68"));
			azienda.setStrNumAgSomministrazione((String) anAzienda.getAttribute("strNumAgSomministrazione"));
			try {
				azienda.insert();
				prgAzienda = new BigDecimal(azienda.getPrgAzienda());
			} catch (EMFInternalError e) {
				try {
					SourceBean rowApp = (SourceBean) tex.executeQuery("GET_PRG_AZIENDA",
							new Object[] { strCodiceFiscale }, "SELECT");
					prgAzienda = (BigDecimal) rowApp.getAttribute("row.prgAzienda");
					if (prgAzienda == null) {
						_logger.debug("RegistraProspettoInformativo: ERRORE=L'azienda deve essere presente");
						throw new Exception("L'azienda deve essere presente");
					}
				} catch (Exception e1) {
					_logger.debug("RegistraProspettoInformativo: ERRORE=L'azienda deve essere presente. " + e1);
					throw e1;
				}
			}

			// Devo inserire la sede aziendale (con flag sede = S)
			prgUnita = inserisciSedeAzienda(anUnitaAzienda, prgAzienda, codCom, strIndirizzo, tex);
			if (prgUnita == null) {
				_logger.debug("RegistraProspettoInformativo: ERRORE in inserimento sede legale azienda");
				throw new Exception("ERRORE INSERIMENTO SEDE LEGALE AZIENDA");
			}
		} else {
			SourceBean primaUnita = null;
			SourceBean primaUnitaComune = null;
			BigDecimal numkloUAZ = null;
			BigDecimal numkloUAZComune = null;
			BigDecimal prgUnitaComune = null;
			Vector rows = null;
			String flgSedeUAZ = "";
			int codiceRitAgg = 0;
			row = (SourceBean) tex.executeQuery("GET_SEDE_AZIENDA_MOBILITA_DA_VALIDARE",
					new Object[] { prgAzienda, codCom, strIndirizzo }, "SELECT");
			rows = row.getAttributeAsVector("ROW");
			int nSizeUA = rows.size();
			// Ricerca sedi aziendali che hanno stesso comune e stesso indirizzo
			if (nSizeUA > 0) {
				primaUnita = (SourceBean) rows.get(0);
				prgUnita = (BigDecimal) primaUnita.getAttribute("PRGUNITA");
				numkloUAZ = (BigDecimal) primaUnita.getAttribute("NUMKLOUNITAAZIENDA");
				codiceRitAgg = aggiornaSedeLegale(prgAzienda, prgUnita, numkloUAZ, tex);
				if (codiceRitAgg < 0) {
					_logger.debug("RegistraProspettoInformativo: ERRORE in aggiornamento sede legale azienda");
					throw new Exception("ERRORE AGGIORNAMENTO SEDE LEGALE AZIENDA");
				}
				try {
					numkloUAZ = numkloUAZ.add(new BigDecimal(1));
					aggiornaPECemailSedeLegale(prgAzienda, prgUnita, strPECemail, tex, numkloUAZ);
				} catch (Exception e) {
					throw new Exception("ERRORE AGGIORNAMENTO PEC EMAIL SEDE LEGALE AZIENDA");
				}
			} else {
				// Verifica esistenza sede legale nella stesso comune
				row = (SourceBean) tex.executeQuery("COOP_GET_UNITA_AZIENDA_STESSO_COMUNE",
						new Object[] { prgAzienda, codCom }, "SELECT");
				rows = row.getAttributeAsVector("ROW");
				nSizeUA = rows.size();
				if (nSizeUA > 0) {
					primaUnitaComune = (SourceBean) rows.get(0);
					prgUnitaComune = (BigDecimal) primaUnitaComune.getAttribute("PRGUNITA");
					numkloUAZComune = (BigDecimal) primaUnitaComune.getAttribute("numklounitaazienda");
					for (int i = 0; i < nSizeUA; i++) {
						primaUnita = (SourceBean) rows.get(i);
						flgSedeUAZ = primaUnita.getAttribute("flgSede") != null
								? primaUnita.getAttribute("flgSede").toString()
								: "";
						if (flgSedeUAZ.equalsIgnoreCase("S")) {
							prgUnita = (BigDecimal) primaUnita.getAttribute("prgUnita");
							numkloUAZ = (BigDecimal) primaUnita.getAttribute("numklounitaazienda");
							// bugfix del 29/01/2014
							aggiornaPECemailSedeLegale(prgAzienda, prgUnita, strPECemail, tex, numkloUAZ);
							break;
						}
					}
					// Se esiste sedeLegale nello stesso comune allora aggancio il prospetto a quella sede
					// Nel caso contrario verifico esistenza sedeLegale nella stessa provincia
					if (prgUnita == null) {
						row = (SourceBean) tex.executeQuery("COOP_GET_UNITA_AZIENDA_STESSA_PROVINCIA",
								new Object[] { prgAzienda, codCom }, "SELECT");
						rows = row.getAttributeAsVector("ROW");
						nSizeUA = rows.size();
						for (int i = 0; i < nSizeUA; i++) {
							primaUnita = (SourceBean) rows.get(i);
							flgSedeUAZ = primaUnita.getAttribute("flgSede") != null
									? primaUnita.getAttribute("flgSede").toString()
									: "";
							if (flgSedeUAZ.equalsIgnoreCase("S")) {
								prgUnita = (BigDecimal) primaUnita.getAttribute("prgUnita");
								numkloUAZ = (BigDecimal) primaUnita.getAttribute("NUMKLOUNITAAZIENDA");
								// bugfix del 31/01/2014
								aggiornaPECemailSedeLegale(prgAzienda, prgUnita, strPECemail, tex, numkloUAZ);
								break;
							}
						}
						if (prgUnita == null) {
							// Aggiorno la sede legale relativa alla prima unità del comune trovata
							prgUnita = prgUnitaComune;
							codiceRitAgg = aggiornaSedeLegale(prgAzienda, prgUnitaComune, numkloUAZComune, tex);
							if (codiceRitAgg < 0) {
								_logger.debug(
										"RegistraProspettoInformativo: ERRORE in aggiornamento sede legale azienda");
								throw new Exception("ERRORE AGGIORNAMENTO SEDE LEGALE AZIENDA");
							}
							try {
								numkloUAZComune = numkloUAZComune.add(new BigDecimal(1));
								aggiornaPECemailSedeLegale(prgAzienda, prgUnita, strPECemail, tex, numkloUAZComune);
							} catch (Exception e) {
								throw new Exception("ERRORE AGGIORNAMENTO PEC EMAIL SEDE LEGALE AZIENDA");
							}
						}
					}
				} else {
					// Devo inserire la sede aziendale (con flag sede = S) e aggiornare flgSede = N per le altre
					prgUnita = inserisciSedeAzienda(anUnitaAzienda, prgAzienda, codCom, strIndirizzo, tex);
					if (prgUnita == null) {
						_logger.debug("RegistraProspettoInformativo: ERRORE in inserimento sede legale azienda");
						throw new Exception("ERRORE INSERIMENTO SEDE LEGALE AZIENDA");
					}
					row = (SourceBean) tex.executeQuery("COOP_GET_INFO_UNITA_AZIENDA_INSERITA",
							new Object[] { prgAzienda, prgUnita }, "SELECT");
					if (row != null) {
						numkloUAZ = (BigDecimal) row.getAttribute("ROW.numklounitaazienda");
						codiceRitAgg = aggiornaSedeLegale(prgAzienda, prgUnita, numkloUAZ, tex);
						if (codiceRitAgg < 0) {
							_logger.debug("RegistraProspettoInformativo: ERRORE in aggiornamento sede legale azienda");
							throw new Exception("ERRORE AGGIORNAMENTO SEDE LEGALE AZIENDA");
						}
					} else {
						_logger.debug("RegistraProspettoInformativo: ERRORE in recupero dati sede legale azienda");
						throw new Exception("ERRORE RECUPERO DATI SEDE LEGALE AZIENDA");
					}
				}
			}
		}

		//
		SourceBean referenteProspetto = (SourceBean) prospettoBean.getAttribute("REFERENTEPROSPETTO");
		// ora abbiamo sia il prgunita che il prgazienda
		String strRefCognome = (String) referenteProspetto.getAttribute("strRefCognome");
		String strRefNome = (String) referenteProspetto.getAttribute("strRefNome");
		String strRefTel = (String) referenteProspetto.getAttribute("strRefTel");
		if (strRefTel == null || strRefTel.equals("")) {
			strRefTel = "0";
		}
		String strRefFax = (String) referenteProspetto.getAttribute("strRefFax");
		String strRefEMail = (String) referenteProspetto.getAttribute("strRefEMail");
		Object param[];
		param = new Object[5];
		param[0] = prgAzienda;
		param[1] = prgUnita;
		param[2] = strRefCognome;
		param[3] = strRefNome;
		param[4] = "RCM";

		// ricerca in an_az_referente con prgazienda e prgunita strreferente e ruolo
		SourceBean referentiBean = (SourceBean) tex.executeQuery("COOP_GET_AN_AZ_REFERENTE", param, "SELECT");
		Vector listaReferenti = referentiBean.getAttributeAsVector("ROW");
		// se esiste aggiornare
		Object prgAzReferente = null;
		if (listaReferenti.size() > 0) {
			for (int i = 0; i < listaReferenti.size(); i++) {
				// per ogni riga fai l'update
				prgAzReferente = ((SourceBean) listaReferenti.get(i)).getAttribute("prgAzReferente");
				param = new Object[4];
				// se mi arriva "0", lascio il numero che è già presente su DB.
				param[0] = ("0".equals(strRefTel)) ? null : strRefTel;
				param[1] = strRefFax;
				param[2] = strRefEMail;
				param[3] = prgAzReferente.toString();
				try {
					tex.executeQuery("COOP_UPD_AN_AZ_REFERENTE", param, "UPDATE");
				} catch (EMFInternalError e) {
					Throwable errOrigine = ((EMFInternalError) e).getNativeException();
					if (errOrigine instanceof SQLException && ((SQLException) errOrigine).getErrorCode() == 20667) {
						_logger.debug("RegistraProspettoInformativo: ERRORE= " + e);
					} else {
						_logger.debug("RegistraProspettoInformativo: ERRORE= " + e);
						throw e;
					}
				}
			}
		} else {
			// altrimenti inserire un nuovo record
			AzReferenteBean azReferenteBean = new AzReferenteBean(prgAzienda, prgUnita, strRefCognome, strRefTel, "RCM",
					cdnUt, tex);
			azReferenteBean.setStrEmail(strRefEMail);
			azReferenteBean.setStrFax(strRefFax);
			azReferenteBean.setStrNome(strRefNome);
			//
			try {
				azReferenteBean.insert();
				prgAzReferente = azReferenteBean.getPrgAzReferente();
			} catch (EMFInternalError e) {
				Throwable errOrigine = ((EMFInternalError) e).getNativeException();
				if (errOrigine instanceof SQLException && ((SQLException) errOrigine).getErrorCode() == 20667) {
					try {
						SourceBean referentiBeanApp = (SourceBean) tex.executeQuery("COOP_GET_AN_AZ_REFERENTE",
								new Object[] { prgAzienda, prgUnita, strRefCognome, strRefNome, "RCM" }, "SELECT");
						Vector listaReferentiApp = referentiBeanApp.getAttributeAsVector("ROW");
						if (listaReferentiApp.size() > 0) {
							SourceBean primoReferente = (SourceBean) listaReferentiApp.get(0);
							prgAzReferente = primoReferente.getAttribute("PRGAZREFERENTE");
						}
					} catch (EMFInternalError e1) {
						_logger.debug("RegistraProspettoInformativo: ERRORE= " + e1);
						throw e1;
					}
				} else {
					_logger.debug("RegistraProspettoInformativo: ERRORE= " + e);
					throw e;
				}
			}
		}

		//
		SourceBean prgProspettoBean = (SourceBean) tex.executeQuery("SELECT_PRGPROSPETTOINF_SEQUENCE", null, "SELECT");
		BigDecimal prgProspettoInf = (BigDecimal) prgProspettoBean.getAttribute("ROW.do_nextval");
		cmProspettoInf.setAttribute("prgProspettoInf", prgProspettoInf);
		cmProspettoInf.setAttribute("prgAzienda", prgAzienda);
		cmProspettoInf.setAttribute("prgUnita", prgUnita);
		cmProspettoInf.setAttribute("codMonoProv", "S");
		cmProspettoInf.setAttribute("PRGAZREFERENTE", prgAzReferente);

		// Impostazione FLGCOMPETENZA
		String flgCompetenza = "N";
		String codProvinciaRifQ2 = cmProspettoInf.getAttribute("codProvinciaRif") != null
				? cmProspettoInf.getAttribute("codProvinciaRif").toString()
				: "";
		SourceBean rowProv = (SourceBean) tex.executeQuery("GET_COMPETENZA_PROSPETTO",
				new Object[] { codProvinciaRifQ2, codProvinciaRifQ2 }, "SELECT");
		if (rowProv != null) {
			String codProvinciaSil = rowProv.getAttribute("row.codProvinciaSil") != null
					? (String) rowProv.getAttribute("row.codProvinciaSil")
					: "";
			if (!codProvinciaSil.equals("")) {
				flgCompetenza = "S";
			}
		}
		cmProspettoInf.setAttribute("flgCompetenza", flgCompetenza);
		if (flgCompetenza.equals("S")) {
			cmProspettoInf.updAttribute("CODMONOSTATOPROSPETTO", "V");
		} else {
			cmProspettoInf.updAttribute("CODMONOSTATOPROSPETTO", "S");
		}

		SourceBean riepilogoNaz = (SourceBean) prospettoBean.getAttribute("riepilogonazionale");
		BigDecimal numlavoratoriBC3 = null;
		BigDecimal numlavoratoriBC18 = null;
		if (riepilogoNaz != null) {
			SourceBean numBaseComputoArt3 = (SourceBean) riepilogoNaz.getAttribute("NUMLAVBASECOMPUTOART3");
			SourceBean numBaseComputoArt18 = (SourceBean) riepilogoNaz.getAttribute("NUMLAVBASECOMPUTOART18");
			numlavoratoriBC3 = new BigDecimal(numBaseComputoArt3.getCharacters());
			numlavoratoriBC18 = new BigDecimal(numBaseComputoArt18.getCharacters());
		} else {
			numlavoratoriBC3 = new BigDecimal("0");
			numlavoratoriBC18 = new BigDecimal("0");
		}
		cmProspettoInf.setAttribute("NUMBASECOMPUTOART3", numlavoratoriBC3);
		cmProspettoInf.setAttribute("NUMBASECOMPUTOART18", numlavoratoriBC18);

		Vector<SourceBean> elencoRiepProv = prospettoBean.getAttributeAsVector("elencoriepilogativoprovinciale");
		String provinciaQ2 = Integer.valueOf(codProvinciaRifQ2).toString();
		boolean exitCiclo = false;
		for (int k = 0; (k < elencoRiepProv.size() && !exitCiclo); k++) {
			SourceBean elencoRiep = elencoRiepProv.get(k);
			SourceBean provinciaSB = (SourceBean) elencoRiep.getAttribute("PROVINCIA");
			// Elimina gli zero inutili
			String provinciaRiep = Integer.valueOf(provinciaSB.getCharacters()).toString();
			if (provinciaRiep.equals(provinciaQ2)) {
				SourceBean numBaseComputoArt3Prov = (SourceBean) elencoRiep.getAttribute("NUMLAVBASECOMPUTOART3");
				SourceBean numBaseComputoArt18Prov = (SourceBean) elencoRiep.getAttribute("NUMLAVBASECOMPUTOART18");
				BigDecimal numlavoratoriBC3Prov = new BigDecimal(numBaseComputoArt3Prov.getCharacters());
				BigDecimal numlavoratoriBC18Prov = new BigDecimal(numBaseComputoArt18Prov.getCharacters());
				cmProspettoInf.setAttribute("NUMBASECOMPUTOART3PROV", numlavoratoriBC3Prov);
				cmProspettoInf.setAttribute("NUMBASECOMPUTOART18PROV", numlavoratoriBC18Prov);
				exitCiclo = true;
			}
		}

		//
		if ("true".equals(cmProspettoInf.getAttribute("flgConvenzione")))
			cmProspettoInf.updAttribute("NUMCONVENZIONI", "1");
		//
		if (noteIndirizzoUnita != null) {
			if (strNote == null) {
				strNote = noteIndirizzoUnita;
			} else {
				strNote += ";" + noteIndirizzoUnita;
			}
		}

		// 20/12/2010 - Rodi - Aggiungo i fisioterapisti ciechi

		String numPostiCentralinisti = (String) cmProspettoInf.getAttribute("NUMPOSTICENTRALINISTI");
		String numMassofisioterapisti = (String) cmProspettoInf.getAttribute("NUMMASSOFISIOTERAPISTI");

		// String numCentralinistiInForza = (String) riepilogo.getAttribute("numCentralinistiInForza");
		// String numMassofisioterapistiInForza = (String) riepilogo.getAttribute("numMassofisioterapistiInForza");

		// inserisco i valori nei loro campi

		if (numPostiCentralinisti != null) {
			BigDecimal numCentNonVedentiObbligo = new BigDecimal(numPostiCentralinisti);
			cmProspettoInf.delAttribute("NUMPOSTICENTRALINISTI");
			cmProspettoInf.setAttribute("NUMCENTNONVEDENTIOBBLIGO", numCentNonVedentiObbligo);
		}
		if (numMassofisioterapisti != null) {
			BigDecimal numMassofisioterapistiObbligo = new BigDecimal(numMassofisioterapisti);
			cmProspettoInf.delAttribute("NUMMASSOFISIOTERAPISTI");
			cmProspettoInf.setAttribute("NUMMASSOFISIOTERAPISTIOBBLIGO", numMassofisioterapistiObbligo);
		}
		/*
		 * if (numCentralinistiInForza != null) { BigDecimal numCentNonVedentiInForza = new
		 * BigDecimal(numCentralinistiInForza); cmProspettoInf.setAttribute("NUMCENTNONVEDENTIFORZA",
		 * numCentNonVedentiInForza); } if (numMassofisioterapistiInForza != null) { BigDecimal numMassoInForza = new
		 * BigDecimal(numMassofisioterapistiInForza); cmProspettoInf.setAttribute("NUMMASSOFISIOTERAPISTIFORZA",
		 * numMassoInForza); }
		 */

		if (strNote != null) {
			cmProspettoInf.updAttribute("strnote", strNote);
		}

		//
		ModuleIFace modulo = ModuleFactory.getModule("COOP_INS_PROSPETTO_INFORMATIVO");
		((RequestContextIFace) modulo).setRequestContext(rcIFace);
		((AbstractSimpleModule) modulo).enableTransactions(tex);
		rc.setServiceRequest(cmProspettoInf);

		modulo.service(cmProspettoInf, res);
		if (!rs.getErrorHandler().isOK())
			throw new Exception("Impossibile inserire il prospetto informativo.");
		//
		if (cmEsclusioneProspetto != null) {

			cmEsclusioneProspetto.setAttribute("prgProspettoInf", prgProspettoInf);
			modulo = ModuleFactory.getModule("COOP_INS_CM_ESCLUSIONE_PROSPETTO");
			((RequestContextIFace) modulo).setRequestContext(rcIFace);
			((AbstractSimpleModule) modulo).enableTransactions(tex);
			rc.setServiceRequest(cmEsclusioneProspetto);
			modulo.service(cmEsclusioneProspetto, res);
			if (!rs.getErrorHandler().isOK())
				throw new Exception("Impossibile inserire la esclusione prospetto informativo.");
		}
		// utilizzare strCognome e strNome per cercare il lavoratore
		Vector listaCmPiLavRiserva = prospettoBean.getAttributeAsVector("LavoratoriL68.Lavoratore");
		for (int i = 0; i < listaCmPiLavRiserva.size(); i++) {
			SourceBean cmPiLavRiserva = (SourceBean) listaCmPiLavRiserva.get(i);
			SourceBean rapporto = (SourceBean) cmPiLavRiserva.getAttribute("Rapporto");
			cmPiLavRiserva.updContainedAttributes(rapporto.getContainedAttributes());
			String strCodiceFiscaleLav = (String) cmPiLavRiserva.getAttribute("strCodiceFiscaleLav");
			SourceBean lavoratoreBean = (SourceBean) tex.executeQuery("COOP_PROSPETTO_RICERCA_LAVORATORE",
					new Object[] { strCodiceFiscaleLav }, "SELECT");
			Object cdnLavoratore = lavoratoreBean.getAttribute("row.cdnlavoratore");
			if (cdnLavoratore != null) {
				cmPiLavRiserva.setAttribute("cdnLavoratore", cdnLavoratore);
			}
			cmPiLavRiserva.setAttribute("prgProspettoInf", prgProspettoInf);
			String nomeLav = cmPiLavRiserva.getAttribute("strNomeLav") != null
					? cmPiLavRiserva.getAttribute("strNomeLav").toString()
					: "";
			if (!nomeLav.equals("")) {
				nomeLav = nomeLav.trim();
				cmPiLavRiserva.updAttribute("strNomeLav", nomeLav);
			}
			String cognomeLav = cmPiLavRiserva.getAttribute("strCognomeLav") != null
					? cmPiLavRiserva.getAttribute("strCognomeLav").toString()
					: "";
			if (!cognomeLav.equals("")) {
				cognomeLav = cognomeLav.trim();
				cmPiLavRiserva.updAttribute("strCognomeLav", cognomeLav);
			}
			modulo = ModuleFactory.getModule("COOP_INS_CM_PI_LAV_RISERVA");
			((RequestContextIFace) modulo).setRequestContext(rcIFace);
			((AbstractSimpleModule) modulo).enableTransactions(tex);
			rc.setServiceRequest(cmPiLavRiserva);
			modulo.service(cmPiLavRiserva, res);
			if (!rs.getErrorHandler().isOK())
				throw new Exception("Impossibile inserire la riserva lavoratore prospetto informativo.");
		}
		//
		Vector listaCmPiMansioneDisp = prospettoBean.getAttributeAsVector("PostiDisponibili.Dettaglio");
		for (int i = 0; i < listaCmPiMansioneDisp.size(); i++) {
			SourceBean cmPiMansioneDisp = (SourceBean) listaCmPiMansioneDisp.get(i);
			cmPiMansioneDisp.setAttribute("prgProspettoInf", prgProspettoInf);
			modulo = ModuleFactory.getModule("COOP_INS_CM_PI_MANSIONE_DISP");
			((RequestContextIFace) modulo).setRequestContext(rcIFace);
			((AbstractSimpleModule) modulo).enableTransactions(tex);
			rc.setServiceRequest(cmPiMansioneDisp);
			modulo.service(cmPiMansioneDisp, res);
			if (!rs.getErrorHandler().isOK())
				throw new Exception("Impossibile inserire la disponibilita' mansione prospetto informativo.");
		}
		//

		SourceBean cmPiCompensazione = (SourceBean) prospettoBean
				.getAttribute("Autorizzazioni.CompensazioneTerritoriale");
		if (cmPiCompensazione != null) {
			Vector listaCmPiCompensazione = cmPiCompensazione.getAttributeAsVector("Dettaglio");
			for (int i = 0; i < listaCmPiCompensazione.size(); i++) {
				SourceBean cmPiCompensazioneDettaglio = (SourceBean) listaCmPiCompensazione.get(i);
				cmPiCompensazioneDettaglio.setAttribute("prgProspettoInf", prgProspettoInf);
				modulo = ModuleFactory.getModule("COOP_INS_CM_PI_COMPENSAZIONE");
				((RequestContextIFace) modulo).setRequestContext(rcIFace);
				((AbstractSimpleModule) modulo).enableTransactions(tex);
				rc.setServiceRequest(cmPiCompensazioneDettaglio);
				modulo.service(cmPiCompensazioneDettaglio, res);
				if (!rs.getErrorHandler().isOK())
					throw new Exception("Impossibile inserire la disponibilita' mansione prospetto informativo.");
			}
		}

		Vector listaAssPubSelezione = prospettoBean.getAttributeAsVector("AssunzioniPubblicaSelezione.Dettaglio");
		for (int i = 0; i < listaAssPubSelezione.size(); i++) {
			SourceBean assPubSelezione = (SourceBean) listaAssPubSelezione.get(i);
			assPubSelezione.setAttribute("prgProspettoInf", prgProspettoInf);
			modulo = ModuleFactory.getModule("COOP_INS_CM_ASSUNZ_PB_SELEZIONE");
			((RequestContextIFace) modulo).setRequestContext(rcIFace);
			((AbstractSimpleModule) modulo).enableTransactions(tex);
			rc.setServiceRequest(assPubSelezione);
			modulo.service(assPubSelezione, res);
			if (!rs.getErrorHandler().isOK())
				throw new Exception("Impossibile inserire assunzione mediante pubblica selezione.");
		}

		Vector listaIntermittenti = prospettoBean.getAttributeAsVector("DettaglioIntermittenti.Dettaglio");
		for (int i = 0; i < listaIntermittenti.size(); i++) {
			SourceBean dettaglioIntermittente = (SourceBean) listaIntermittenti.get(i);
			dettaglioIntermittente.setAttribute("prgProspettoInf", prgProspettoInf);
			modulo = ModuleFactory.getModule("COOP_INS_CM_INTERMITTENTI_PROS");
			((RequestContextIFace) modulo).setRequestContext(rcIFace);
			((AbstractSimpleModule) modulo).enableTransactions(tex);
			rc.setServiceRequest(dettaglioIntermittente);
			modulo.service(dettaglioIntermittente, res);
			if (!rs.getErrorHandler().isOK())
				throw new Exception("Impossibile inserire dettaglio intermittenti.");
		}

		Vector listaPartTime = prospettoBean.getAttributeAsVector("DettaglioPartTime.Dettaglio");
		for (int i = 0; i < listaPartTime.size(); i++) {
			SourceBean dettaglioPartTime = (SourceBean) listaPartTime.get(i);
			dettaglioPartTime.setAttribute("prgProspettoInf", prgProspettoInf);
			modulo = ModuleFactory.getModule("COOP_INS_CM_PARTTIME_PROS");
			((RequestContextIFace) modulo).setRequestContext(rcIFace);
			((AbstractSimpleModule) modulo).enableTransactions(tex);
			rc.setServiceRequest(dettaglioPartTime);
			modulo.service(dettaglioPartTime, res);
			if (!rs.getErrorHandler().isOK())
				throw new Exception("Impossibile inserire dettaglio parttime.");
		}

		// Inizio sezione nuova Decreto 2016
		BigDecimal orarioSettPT = null;
		BigDecimal orarioSvoltoPT = null;
		BigDecimal numLavPT = null;
		BigDecimal zero = new BigDecimal("0");
		// disabili parttime
		Vector listaDisabiliPartTime = cmPersonaleDipendente.getAttributeAsVector("disabili.parttime");
		for (int i = 0; i < listaDisabiliPartTime.size(); i++) {
			SourceBean dettaglioDisabiliPartTime = (SourceBean) listaDisabiliPartTime.get(i);
			if (dettaglioDisabiliPartTime != null) {
				String numLavoratoriPT = (String) dettaglioDisabiliPartTime.getAttribute("nlavoratoriparttime");
				if (numLavoratoriPT != null) {
					numLavPT = new BigDecimal(numLavoratoriPT);
					dettaglioDisabiliPartTime.setAttribute("NUMLAVORATORI", numLavPT);
				}
				String numOrarioSettPT = (String) dettaglioDisabiliPartTime
						.getAttribute("orariosettimanalecontrattuale");
				if (numOrarioSettPT != null) {
					orarioSettPT = new BigDecimal(numOrarioSettPT);
					dettaglioDisabiliPartTime.setAttribute("DECORARIOSETTCONTRATTUALE", orarioSettPT);
				}
				String numOrarioSvoltoPT = (String) dettaglioDisabiliPartTime.getAttribute("orariosettimanaleparttime");
				if (numOrarioSvoltoPT != null) {
					orarioSvoltoPT = new BigDecimal(numOrarioSvoltoPT);
					dettaglioDisabiliPartTime.setAttribute("DECORARIOSETTSVOLTO", orarioSvoltoPT);
				}
				dettaglioDisabiliPartTime.setAttribute("PRGPROSPETTOINF", prgProspettoInf);
				dettaglioDisabiliPartTime.setAttribute("CODTIPOPTDISABILE", "D");

				// FLGOLTRE50, DECCOPERTURA
				double decCoperturaDiv = 0;
				try {
					if ((numLavPT != null && numLavPT.equals(zero)) || (orarioSvoltoPT != null && orarioSettPT != null
							&& orarioSvoltoPT.equals(zero) && orarioSettPT.equals(zero))) {
						decCoperturaDiv = 0;
					} else {
						decCoperturaDiv = (numLavPT.multiply(orarioSvoltoPT).doubleValue())
								/ (orarioSettPT.doubleValue());
					}
				} catch (Exception eDiv) {
					decCoperturaDiv = 0;
				}
				double decCoperturaDivRound = new BigDecimal(decCoperturaDiv).setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				dettaglioDisabiliPartTime.setAttribute("DECCOPERTURA", decCoperturaDivRound);

				if (orarioSvoltoPT.doubleValue() / orarioSettPT.doubleValue() > 0.50) {
					dettaglioDisabiliPartTime.setAttribute("FLGOLTRE50", "S");
				}

				modulo = ModuleFactory.getModule("COOP_INS_PI_DETT_PT_DISABILI");
				((RequestContextIFace) modulo).setRequestContext(rcIFace);
				((AbstractSimpleModule) modulo).enableTransactions(tex);
				rc.setServiceRequest(dettaglioDisabiliPartTime);
				modulo.service(dettaglioDisabiliPartTime, res);
				if (!rs.getErrorHandler().isOK())
					throw new Exception("Impossibile inserire dettaglio disabili parttime.");

				dettaglioDisabiliPartTime.delAttribute("PRGPROSPETTOINF");
				dettaglioDisabiliPartTime.delAttribute("CODTIPOPTDISABILE");
				dettaglioDisabiliPartTime.delAttribute("NUMLAVORATORI");
				dettaglioDisabiliPartTime.delAttribute("DECORARIOSETTCONTRATTUALE");
				dettaglioDisabiliPartTime.delAttribute("DECORARIOSETTSVOLTO");
				dettaglioDisabiliPartTime.delAttribute("DECCOPERTURA");
				dettaglioDisabiliPartTime.delAttribute("FLGOLTRE50");
			}
		}
		// centralinisti parttime
		Vector listaCentralinistiPartTime = cmPersonaleDipendente.getAttributeAsVector("centralinisti.parttime");
		for (int i = 0; i < listaCentralinistiPartTime.size(); i++) {
			SourceBean dettaglioCentralinistiPartTime = (SourceBean) listaCentralinistiPartTime.get(i);
			if (dettaglioCentralinistiPartTime != null) {
				String numLavoratoriPT = (String) dettaglioCentralinistiPartTime.getAttribute("nlavoratoriparttime");
				if (numLavoratoriPT != null) {
					numLavPT = new BigDecimal(numLavoratoriPT);
					dettaglioCentralinistiPartTime.setAttribute("NUMLAVORATORI", numLavPT);
				}
				String numOrarioSettPT = (String) dettaglioCentralinistiPartTime
						.getAttribute("orariosettimanalecontrattuale");
				if (numOrarioSettPT != null) {
					orarioSettPT = new BigDecimal(numOrarioSettPT);
					dettaglioCentralinistiPartTime.setAttribute("DECORARIOSETTCONTRATTUALE", orarioSettPT);
				}
				String numOrarioSvoltoPT = (String) dettaglioCentralinistiPartTime
						.getAttribute("orariosettimanaleparttime");
				if (numOrarioSvoltoPT != null) {
					orarioSvoltoPT = new BigDecimal(numOrarioSvoltoPT);
					dettaglioCentralinistiPartTime.setAttribute("DECORARIOSETTSVOLTO", orarioSvoltoPT);
				}
				dettaglioCentralinistiPartTime.setAttribute("PRGPROSPETTOINF", prgProspettoInf);
				dettaglioCentralinistiPartTime.setAttribute("CODTIPOPTDISABILE", "C");

				// FLGOLTRE50, DECCOPERTURA
				double decCoperturaDiv = 0;
				try {
					if ((numLavPT != null && numLavPT.equals(zero)) || (orarioSvoltoPT != null && orarioSettPT != null
							&& orarioSvoltoPT.equals(zero) && orarioSettPT.equals(zero))) {
						decCoperturaDiv = 0;
					} else {
						decCoperturaDiv = (numLavPT.multiply(orarioSvoltoPT).doubleValue())
								/ (orarioSettPT.doubleValue());
					}
				} catch (Exception eDiv) {
					decCoperturaDiv = 0;
				}
				double decCoperturaDivRound = new BigDecimal(decCoperturaDiv).setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				dettaglioCentralinistiPartTime.setAttribute("DECCOPERTURA", decCoperturaDivRound);

				if (orarioSvoltoPT.doubleValue() / orarioSettPT.doubleValue() > 0.50) {
					dettaglioCentralinistiPartTime.setAttribute("FLGOLTRE50", "S");
				}

				modulo = ModuleFactory.getModule("COOP_INS_PI_DETT_PT_DISABILI");
				((RequestContextIFace) modulo).setRequestContext(rcIFace);
				((AbstractSimpleModule) modulo).enableTransactions(tex);
				rc.setServiceRequest(dettaglioCentralinistiPartTime);
				modulo.service(dettaglioCentralinistiPartTime, res);
				if (!rs.getErrorHandler().isOK())
					throw new Exception("Impossibile inserire dettaglio centralinisti parttime.");

				dettaglioCentralinistiPartTime.delAttribute("PRGPROSPETTOINF");
				dettaglioCentralinistiPartTime.delAttribute("CODTIPOPTDISABILE");
				dettaglioCentralinistiPartTime.delAttribute("NUMLAVORATORI");
				dettaglioCentralinistiPartTime.delAttribute("DECORARIOSETTCONTRATTUALE");
				dettaglioCentralinistiPartTime.delAttribute("DECORARIOSETTSVOLTO");
				dettaglioCentralinistiPartTime.delAttribute("DECCOPERTURA");
				dettaglioCentralinistiPartTime.delAttribute("FLGOLTRE50");
			}
		}
		// massofisioterapisti parttime
		Vector listaMassoPartTime = cmPersonaleDipendente.getAttributeAsVector("massofisioterapisti.parttime");
		for (int i = 0; i < listaMassoPartTime.size(); i++) {
			SourceBean dettaglioMassoPartTime = (SourceBean) listaMassoPartTime.get(i);
			if (dettaglioMassoPartTime != null) {
				String numLavoratoriPT = (String) dettaglioMassoPartTime.getAttribute("nlavoratoriparttime");
				if (numLavoratoriPT != null) {
					numLavPT = new BigDecimal(numLavoratoriPT);
					dettaglioMassoPartTime.setAttribute("NUMLAVORATORI", numLavPT);
				}
				String numOrarioSettPT = (String) dettaglioMassoPartTime.getAttribute("orariosettimanalecontrattuale");
				if (numOrarioSettPT != null) {
					orarioSettPT = new BigDecimal(numOrarioSettPT);
					dettaglioMassoPartTime.setAttribute("DECORARIOSETTCONTRATTUALE", orarioSettPT);
				}
				String numOrarioSvoltoPT = (String) dettaglioMassoPartTime.getAttribute("orariosettimanaleparttime");
				if (numOrarioSvoltoPT != null) {
					orarioSvoltoPT = new BigDecimal(numOrarioSvoltoPT);
					dettaglioMassoPartTime.setAttribute("DECORARIOSETTSVOLTO", orarioSvoltoPT);
				}
				dettaglioMassoPartTime.setAttribute("PRGPROSPETTOINF", prgProspettoInf);
				dettaglioMassoPartTime.setAttribute("CODTIPOPTDISABILE", "M");

				// FLGOLTRE50, DECCOPERTURA
				double decCoperturaDiv = 0;
				try {
					if ((numLavPT != null && numLavPT.equals(zero)) || (orarioSvoltoPT != null && orarioSettPT != null
							&& orarioSvoltoPT.equals(zero) && orarioSettPT.equals(zero))) {
						decCoperturaDiv = 0;
					} else {
						decCoperturaDiv = (numLavPT.multiply(orarioSvoltoPT).doubleValue())
								/ (orarioSettPT.doubleValue());
					}
				} catch (Exception eDiv) {
					decCoperturaDiv = 0;
				}
				double decCoperturaDivRound = new BigDecimal(decCoperturaDiv).setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				dettaglioMassoPartTime.setAttribute("DECCOPERTURA", decCoperturaDivRound);

				if (orarioSvoltoPT.doubleValue() / orarioSettPT.doubleValue() > 0.50) {
					dettaglioMassoPartTime.setAttribute("FLGOLTRE50", "S");
				}

				modulo = ModuleFactory.getModule("COOP_INS_PI_DETT_PT_DISABILI");
				((RequestContextIFace) modulo).setRequestContext(rcIFace);
				((AbstractSimpleModule) modulo).enableTransactions(tex);
				rc.setServiceRequest(dettaglioMassoPartTime);
				modulo.service(dettaglioMassoPartTime, res);
				if (!rs.getErrorHandler().isOK())
					throw new Exception("Impossibile inserire dettaglio centralinisti parttime.");

				dettaglioMassoPartTime.delAttribute("PRGPROSPETTOINF");
				dettaglioMassoPartTime.delAttribute("CODTIPOPTDISABILE");
				dettaglioMassoPartTime.delAttribute("NUMLAVORATORI");
				dettaglioMassoPartTime.delAttribute("DECORARIOSETTCONTRATTUALE");
				dettaglioMassoPartTime.delAttribute("DECORARIOSETTSVOLTO");
				dettaglioMassoPartTime.delAttribute("DECCOPERTURA");
				dettaglioMassoPartTime.delAttribute("FLGOLTRE50");
			}
		}
		// telelavoro parttime
		Vector listaTelelavoroPartTime = cmPersonaleDipendente.getAttributeAsVector("telelavoro.parttime");
		for (int i = 0; i < listaTelelavoroPartTime.size(); i++) {
			SourceBean dettaglioTelelavoroPartTime = (SourceBean) listaTelelavoroPartTime.get(i);
			if (dettaglioTelelavoroPartTime != null) {
				String numLavoratoriPT = (String) dettaglioTelelavoroPartTime.getAttribute("nlavoratoriparttime");
				if (numLavoratoriPT != null) {
					numLavPT = new BigDecimal(numLavoratoriPT);
					dettaglioTelelavoroPartTime.setAttribute("NUMLAVORATORI", numLavPT);
				}
				String numOrarioSettPT = (String) dettaglioTelelavoroPartTime
						.getAttribute("orariosettimanalecontrattuale");
				if (numOrarioSettPT != null) {
					orarioSettPT = new BigDecimal(numOrarioSettPT);
					dettaglioTelelavoroPartTime.setAttribute("DECORARIOSETTCONTRATTUALE", orarioSettPT);
				}
				String numOrarioSvoltoPT = (String) dettaglioTelelavoroPartTime
						.getAttribute("orariosettimanaleparttime");
				if (numOrarioSvoltoPT != null) {
					orarioSvoltoPT = new BigDecimal(numOrarioSvoltoPT);
					dettaglioTelelavoroPartTime.setAttribute("DECORARIOSETTSVOLTO", orarioSvoltoPT);
				}
				dettaglioTelelavoroPartTime.setAttribute("PRGPROSPETTOINF", prgProspettoInf);
				dettaglioTelelavoroPartTime.setAttribute("CODTIPOPTDISABILE", "T");

				// FLGOLTRE50, DECCOPERTURA
				double decCoperturaDiv = 0;
				try {
					if ((numLavPT != null && numLavPT.equals(zero)) || (orarioSvoltoPT != null && orarioSettPT != null
							&& orarioSvoltoPT.equals(zero) && orarioSettPT.equals(zero))) {
						decCoperturaDiv = 0;
					} else {
						decCoperturaDiv = (numLavPT.multiply(orarioSvoltoPT).doubleValue())
								/ (orarioSettPT.doubleValue());
					}
				} catch (Exception eDiv) {
					decCoperturaDiv = 0;
				}
				double decCoperturaDivRound = new BigDecimal(decCoperturaDiv).setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				dettaglioTelelavoroPartTime.setAttribute("DECCOPERTURA", decCoperturaDivRound);

				if (orarioSvoltoPT.doubleValue() / orarioSettPT.doubleValue() > 0.50) {
					dettaglioTelelavoroPartTime.setAttribute("FLGOLTRE50", "S");
				}

				modulo = ModuleFactory.getModule("COOP_INS_PI_DETT_PT_DISABILI");
				((RequestContextIFace) modulo).setRequestContext(rcIFace);
				((AbstractSimpleModule) modulo).enableTransactions(tex);
				rc.setServiceRequest(dettaglioTelelavoroPartTime);
				modulo.service(dettaglioTelelavoroPartTime, res);
				if (!rs.getErrorHandler().isOK())
					throw new Exception("Impossibile inserire dettaglio centralinisti parttime.");

				dettaglioTelelavoroPartTime.delAttribute("PRGPROSPETTOINF");
				dettaglioTelelavoroPartTime.delAttribute("CODTIPOPTDISABILE");
				dettaglioTelelavoroPartTime.delAttribute("NUMLAVORATORI");
				dettaglioTelelavoroPartTime.delAttribute("DECORARIOSETTCONTRATTUALE");
				dettaglioTelelavoroPartTime.delAttribute("DECORARIOSETTSVOLTO");
				dettaglioTelelavoroPartTime.delAttribute("DECCOPERTURA");
				dettaglioTelelavoroPartTime.delAttribute("FLGOLTRE50");
			}
		}

		// disabili somministrati (art. 34, co. 3, D.Lgs. 81/2015) parttime
		Vector listaDisSommPartTime = cmPersonaleNonDipendente
				.getAttributeAsVector("lavoratoridisabilisomministrati.parttime");
		for (int i = 0; i < listaDisSommPartTime.size(); i++) {
			SourceBean dettaglioDisSommPartTime = (SourceBean) listaDisSommPartTime.get(i);
			if (dettaglioDisSommPartTime != null) {
				String numLavoratoriPT = (String) dettaglioDisSommPartTime.getAttribute("nlavoratoriparttime");
				if (numLavoratoriPT != null) {
					numLavPT = new BigDecimal(numLavoratoriPT);
					dettaglioDisSommPartTime.setAttribute("NUMLAVORATORI", numLavPT);
				}
				String numOrarioSettPT = (String) dettaglioDisSommPartTime
						.getAttribute("orariosettimanalecontrattuale");
				if (numOrarioSettPT != null) {
					orarioSettPT = new BigDecimal(numOrarioSettPT);
					dettaglioDisSommPartTime.setAttribute("DECORARIOSETTCONTRATTUALE", orarioSettPT);
				}
				String numOrarioSvoltoPT = (String) dettaglioDisSommPartTime.getAttribute("orariosettimanaleparttime");
				if (numOrarioSvoltoPT != null) {
					orarioSvoltoPT = new BigDecimal(numOrarioSvoltoPT);
					dettaglioDisSommPartTime.setAttribute("DECORARIOSETTSVOLTO", orarioSvoltoPT);
				}
				dettaglioDisSommPartTime.setAttribute("PRGPROSPETTOINF", prgProspettoInf);
				dettaglioDisSommPartTime.setAttribute("CODTIPOPTDISABILE", "S");

				// FLGOLTRE50, DECCOPERTURA
				double decCoperturaDiv = 0;
				try {
					if ((numLavPT != null && numLavPT.equals(zero)) || (orarioSvoltoPT != null && orarioSettPT != null
							&& orarioSvoltoPT.equals(zero) && orarioSettPT.equals(zero))) {
						decCoperturaDiv = 0;
					} else {
						decCoperturaDiv = (numLavPT.multiply(orarioSvoltoPT).doubleValue())
								/ (orarioSettPT.doubleValue());
					}
				} catch (Exception eDiv) {
					decCoperturaDiv = 0;
				}
				double decCoperturaDivRound = new BigDecimal(decCoperturaDiv).setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				dettaglioDisSommPartTime.setAttribute("DECCOPERTURA", decCoperturaDivRound);

				if (orarioSvoltoPT.doubleValue() / orarioSettPT.doubleValue() > 0.50) {
					dettaglioDisSommPartTime.setAttribute("FLGOLTRE50", "S");
				}

				modulo = ModuleFactory.getModule("COOP_INS_PI_DETT_PT_DISABILI");
				((RequestContextIFace) modulo).setRequestContext(rcIFace);
				((AbstractSimpleModule) modulo).enableTransactions(tex);
				rc.setServiceRequest(dettaglioDisSommPartTime);
				modulo.service(dettaglioDisSommPartTime, res);
				if (!rs.getErrorHandler().isOK())
					throw new Exception("Impossibile inserire dettaglio centralinisti parttime.");

				dettaglioDisSommPartTime.delAttribute("PRGPROSPETTOINF");
				dettaglioDisSommPartTime.delAttribute("CODTIPOPTDISABILE");
				dettaglioDisSommPartTime.delAttribute("NUMLAVORATORI");
				dettaglioDisSommPartTime.delAttribute("DECORARIOSETTCONTRATTUALE");
				dettaglioDisSommPartTime.delAttribute("DECORARIOSETTSVOLTO");
				dettaglioDisSommPartTime.delAttribute("DECCOPERTURA");
				dettaglioDisSommPartTime.delAttribute("FLGOLTRE50");
			}
		}

		// disabili in convenzione artt. 12-bis e 14 parttime
		Vector listaDisConvPartTime = cmPersonaleNonDipendente
				.getAttributeAsVector("lavoratoridisabiliconv12bis14.parttime");
		for (int i = 0; i < listaDisConvPartTime.size(); i++) {
			SourceBean dettaglioDisConvPartTime = (SourceBean) listaDisConvPartTime.get(i);
			if (dettaglioDisConvPartTime != null) {
				String numLavoratoriPT = (String) dettaglioDisConvPartTime.getAttribute("nlavoratoriparttime");
				if (numLavoratoriPT != null) {
					numLavPT = new BigDecimal(numLavoratoriPT);
					dettaglioDisConvPartTime.setAttribute("NUMLAVORATORI", numLavPT);
				}
				String numOrarioSettPT = (String) dettaglioDisConvPartTime
						.getAttribute("orariosettimanalecontrattuale");
				if (numOrarioSettPT != null) {
					orarioSettPT = new BigDecimal(numOrarioSettPT);
					dettaglioDisConvPartTime.setAttribute("DECORARIOSETTCONTRATTUALE", orarioSettPT);
				}
				String numOrarioSvoltoPT = (String) dettaglioDisConvPartTime.getAttribute("orariosettimanaleparttime");
				if (numOrarioSvoltoPT != null) {
					orarioSvoltoPT = new BigDecimal(numOrarioSvoltoPT);
					dettaglioDisConvPartTime.setAttribute("DECORARIOSETTSVOLTO", orarioSvoltoPT);
				}
				dettaglioDisConvPartTime.setAttribute("PRGPROSPETTOINF", prgProspettoInf);
				dettaglioDisConvPartTime.setAttribute("CODTIPOPTDISABILE", "V");

				// FLGOLTRE50, DECCOPERTURA
				double decCoperturaDiv = 0;
				try {
					if ((numLavPT != null && numLavPT.equals(zero)) || (orarioSvoltoPT != null && orarioSettPT != null
							&& orarioSvoltoPT.equals(zero) && orarioSettPT.equals(zero))) {
						decCoperturaDiv = 0;
					} else {
						decCoperturaDiv = (numLavPT.multiply(orarioSvoltoPT).doubleValue())
								/ (orarioSettPT.doubleValue());
					}
				} catch (Exception eDiv) {
					decCoperturaDiv = 0;
				}
				double decCoperturaDivRound = new BigDecimal(decCoperturaDiv).setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				dettaglioDisConvPartTime.setAttribute("DECCOPERTURA", decCoperturaDivRound);

				if (orarioSvoltoPT.doubleValue() / orarioSettPT.doubleValue() > 0.50) {
					dettaglioDisConvPartTime.setAttribute("FLGOLTRE50", "S");
				}

				modulo = ModuleFactory.getModule("COOP_INS_PI_DETT_PT_DISABILI");
				((RequestContextIFace) modulo).setRequestContext(rcIFace);
				((AbstractSimpleModule) modulo).enableTransactions(tex);
				rc.setServiceRequest(dettaglioDisConvPartTime);
				modulo.service(dettaglioDisConvPartTime, res);
				if (!rs.getErrorHandler().isOK())
					throw new Exception("Impossibile inserire dettaglio centralinisti parttime.");

				dettaglioDisConvPartTime.delAttribute("PRGPROSPETTOINF");
				dettaglioDisConvPartTime.delAttribute("CODTIPOPTDISABILE");
				dettaglioDisConvPartTime.delAttribute("NUMLAVORATORI");
				dettaglioDisConvPartTime.delAttribute("DECORARIOSETTCONTRATTUALE");
				dettaglioDisConvPartTime.delAttribute("DECORARIOSETTSVOLTO");
				dettaglioDisConvPartTime.delAttribute("DECCOPERTURA");
				dettaglioDisConvPartTime.delAttribute("FLGOLTRE50");
			}
		}
		// Fine sezione nuova Decreto 2016

		// 08/03/2013 - LANDI - Decreto UNIPI Gennaio 2013
		// I campi elencoriepilogativoprovinciale e riepilogonazionale vengono riportati nella copia in corso d'anno
		if (elencoRiepProv != null) {
			for (SourceBean elencoRiep : elencoRiepProv) {
				insertElencoRiepilogativoProvinciale(elencoRiep, prgProspettoInf.toString(), tex);
			}
		}

		// Riepilogo nazionale
		if (riepilogoNaz != null) {
			insertRiepilogoNazionale(riepilogoNaz, prgProspettoInf.toString(), tex);
		}

		if (flgCompetenza.equals("S")) {
			// storicizzazione
			String stmProcedure = "{ call ? := PG_COLL_MIRATO_2.STORICIZZA_PROSPETTO_SARE(?,?) }";
			CallableStatement command = null;
			ResultSet dr = null;
			command = tex.getDataConnection().getInternalConnection().prepareCall(stmProcedure);
			command.registerOutParameter(1, Types.VARCHAR);
			command.setString(2, prgProspettoInf.toString());
			command.setString(3, cdnUt);
			dr = command.executeQuery();
			String codiceRit = command.getString(1);
			dr.close();
			command.close();
			if (codiceRit.equals("-1"))
				throw new Exception("Impossibile storicizzare il prospetto aperto esistente.");

			// genera copia

			// ale 28/02/2011
			String encryptKey = System.getProperty("_ENCRYPTER_KEY_");

			String stmProcedureCopia = "{ call ? := PG_COLL_MIRATO_2.GENERACOPIA_PROSPETTO_SARE(?,?,?) }";
			CallableStatement commandCopia = null;
			ResultSet drCopia = null;
			commandCopia = tex.getDataConnection().getInternalConnection().prepareCall(stmProcedureCopia);
			commandCopia.registerOutParameter(1, Types.VARCHAR);
			commandCopia.setString(2, prgProspettoInf.toString());
			commandCopia.setString(3, cdnUt);
			// ale 28/02/2011
			commandCopia.setString(4, encryptKey);

			drCopia = commandCopia.executeQuery();
			String codiceRitCopia = commandCopia.getString(1);
			drCopia.close();
			commandCopia.close();
			if (codiceRitCopia.equals("-1"))
				throw new Exception("Impossibile copiare il prospetto aperto esistente.");
		}

		this.prgAziendaIn = prgAzienda;
		this.prgUnitaIn = prgUnita;
		this.cfAzIn = strCodiceFiscale;
		this.prgKeyProspettoInf = prgProspettoInf.toString();
		// DONA 25/02/2010 X DOCAREA
		// ORA PROTOCOLLIAMO IL DOCUMENTO
		if (protocolla) {
			boolean isOKProtocollazione = false;
			int iTentativo = 1;
			_logger.debug("Before protocollaDocumento");
			while (!isOKProtocollazione && iTentativo <= numMaxTentativiProtocollazione) {
				try {
					Documento doc = protocollaDocumento(prgAzienda, prgUnita, strCodiceFiscale,
							prgProspettoInf.toString(), tex);
					_logger.debug("Post protocollaDocumento");
					ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
					isOKProtocollazione = true;
					iTentativo = numMaxTentativiProtocollazione + 1;
				} catch (Exception e) {
					_logger.error("RegistraProspettoInformativo: fallita protocollazione. \n", e);
					iTentativo = iTentativo + 1;
				}
			}
			if (isOKProtocollazione) {
				_logger.info("Inserito un prospetto informativo con prgProspetto " + prgProspettoInf
						+ " e codComunicazione " + codComunicazione + ".");
			} else {
				throw new Exception("Impossibile protocollare il prospetto " + prgProspettoInf + " e codComunicazione "
						+ codComunicazione + ".");
			}
		}
		// recupero i dati sul prospetto appena inserito.
		// E' preferibile farlo con una select su DB poiché non tutti i dati come datRiferimento o codProvincia
		// sono formattati correttamente sul prospetto che arriva dal ministero.
		ProspettoInformativo ret = new ProspettoInformativo(prgProspettoInf, tex);
		return ret;
	}

	/**
	 * Rettifica un prospetto
	 * 
	 * @param prospettoInformativo
	 *            prospetto in entrata
	 * @param prgProspettoInserito
	 *            progressivo del prospetto arrivato, inserito e che rettifica il precedente
	 * @param tex
	 * @throws EMFInternalError
	 */
	private void rettificaProspetto(ProspettoInformativo prospettoInserito, TransactionQueryExecutor tex)
			throws Exception {

		// codice comunicazione del prospetto rettificato
		String codComunicazioneOrig = (String) prospettoInserito.getCodComunicazioneOrig();

		// == 1 ==
		// rettifica il prospetto precedente, impostando il CODSTATOATTO = 'AN' (annullato)
		{
			// costruisco un bean contenente i dati del prospetto informativo rettificato
			List<ProspettoInformativo> listaProspettiTrovati = ProspettoInformativo.getProspetti(codComunicazioneOrig,
					tex);
			if (listaProspettiTrovati.isEmpty()) {
				_logger.info("Non è stato trovato nessun prospetto da rettificare con codice " + codComunicazioneOrig);
			} else {
				// recupero il prospetto più recente che è il primo della lista e lo rettifico
				ProspettoInformativo prospettoRettificato = listaProspettiTrovati.get(0);
				_logger.info("Rettifico il prospetto " + prospettoRettificato.getCodComunicazione());
				BigDecimal prgDocumento = getPrgDocumentoDaRettificare(prospettoRettificato.getPrgProspettoInf(), tex);
				if (prgDocumento != null)
					rettificaDocumento(prgDocumento, tex);
				else
					_logger.info("Non è stato trovato alcun documento legato al prospetto da rettificare.");
			}
		}

		// == 2 ==
		// inserisco nel campo note del prospetto in corso d'anno la notifica dell'avvenuta rettifica.
		{
			// recupera prgprospettoinf da aggiornare
			Object[] param = new Object[3];
			param[0] = prospettoInserito.getPrgAzienda();
			param[1] = prospettoInserito.getPrgUnita();
			param[2] = prospettoInserito.getCodProvincia();

			SourceBean res = (SourceBean) tex.executeQuery("CM_GET_PRGPROSPETTO_DA_AGGIORNARE", param, "SELECT");
			BigDecimal prgProspettoDaAggiornare = (BigDecimal) res.getAttribute("ROW.PRGPROSPETTOINF");

			if (prgProspettoDaAggiornare != null) {
				_logger.info("Aggiorno le note del prospetto " + prgProspettoDaAggiornare);

				// aggiornane le note
				String note = "Esiste una rettifica con codcomunicazione " + codComunicazione;
				param = new Object[3];
				param[0] = cdnUt;
				param[1] = note;
				param[2] = prgProspettoDaAggiornare;
				tex.executeQuery("CM_RETTIFICA_PROSPETTO_NOTE", param, "UPDATE");
			} else {
				_logger.info("Non è stato trovato un prospetto in corso d'anno ion cui inserire le note.");
			}
		}

		// == 3 ==
		// imposta il CODSTATOATTO = 'PR' (protocollato) al prospetto appena acquisito
		{
			Object[] param = new Object[2];

			param[0] = cdnUt;
			param[1] = prospettoInserito.getPrgProspettoInf();

			tex.executeQuery("CM_IMPOSTA_STATO_PROTOCOLLATO", param, "UPDATE");
		}

	}

	/**
	 * Annulla un prospetto
	 * 
	 * @param prospettoInformativo
	 * @param tex
	 * @throws Exception
	 */
	private void annullaProspetto(SourceBean prospettoInformativo, TransactionQueryExecutor tex) throws Exception {

		// codice comunicazione del prospetto annullato
		String codComunicazioneOrig = (String) prospettoInformativo
				.getAttribute("DATIGENERALI.CODICECOMUNICAZIONEPRECEDENTE");

		// costruisco un bean contenente i dati del prospetto informativo annullato
		List<ProspettoInformativo> listaProspettiTrovati = ProspettoInformativo.getProspetti(codComunicazioneOrig, tex);
		if (listaProspettiTrovati.isEmpty()) {
			_logger.info("Non è stato trovato nessun prospetto da annullare con codice " + codComunicazioneOrig + ". "
					+ "Procedo con l'inserimento di quello arrivato con stato ANNULLATO.");
			boolean protocolla = true;

			ProspettoInformativo acquisito = acquisisciProspetto(prospettoInformativo, tex, protocolla);

			// == 1 ==
			// annulla il prospetto impostando il CODSTATOATTO = 'AN' (annullato)
			BigDecimal prgDocumento = getPrgDocumentoDaRettificare(acquisito.getPrgProspettoInf(), tex);
			if (prgDocumento != null) {
				rettificaDocumento(prgDocumento, tex);
			} else {
				_logger.info("Non è stato trovato alcun documento legato al prospetto da annullare.");
			}
		} else {

			// == 1 ==
			// annulla il prospetto precedente, impostando il CODSTATOATTO = 'AN' (annullato)
			// recupero il prospetto più recente che è il primo della lista e lo annullo
			ProspettoInformativo prospettoAnnullato = listaProspettiTrovati.get(0);
			_logger.info("Annullato il prospetto " + prospettoAnnullato.getCodComunicazione());
			BigDecimal prgDocumento = getPrgDocumentoDaRettificare(prospettoAnnullato.getPrgProspettoInf(), tex);
			if (prgDocumento != null) {
				rettificaDocumento(prgDocumento, tex);
			} else {
				_logger.info("Non è stato trovato alcun documento legato al prospetto da annullare.");
			}

			// == 2 ==
			// imposto nel documento precedente il codice comunicazione del prospetto che l'ha annullato
			{
				Object[] param = new Object[3];
				param[0] = cdnUt;
				param[1] = codComunicazione;
				param[2] = prospettoAnnullato.getPrgProspettoInf();

				tex.executeQuery("CM_ANNULLA_PROSPETTO", param, "UPDATE");

			}

			// == 3 ==
			// inserisco nel campo note del prospetto in corso d'anno la notifica dell'avvenuto annullamento.
			{
				// recupera prgprospettoinf da aggiornare
				Object[] param = new Object[3];
				param[0] = prospettoAnnullato.getPrgAzienda();
				param[1] = prospettoAnnullato.getPrgUnita();
				param[2] = prospettoAnnullato.getCodProvincia();

				SourceBean res = (SourceBean) tex.executeQuery("CM_GET_PRGPROSPETTO_DA_AGGIORNARE", param, "SELECT");
				BigDecimal prgProspettoDaAggiornare = (BigDecimal) res.getAttribute("ROW.PRGPROSPETTOINF");

				_logger.info("Aggiorno le note del prospetto " + prgProspettoDaAggiornare);

				// aggiornane le note
				String note = "Esiste un annullamento con codcomunicazione " + codComunicazione;
				param = new Object[3];
				param[0] = cdnUt;
				param[1] = note;
				param[2] = prgProspettoDaAggiornare;
				tex.executeQuery("CM_RETTIFICA_PROSPETTO_NOTE", param, "UPDATE");
			}
		}
	}

	private BigDecimal getPrgDocumentoDaRettificare(BigDecimal prgProspettoInf, TransactionQueryExecutor tex)
			throws EMFInternalError {
		Object[] param = new Object[1];
		param[0] = prgProspettoInf;

		_logger.info("Imposto stato ANNULLATO al prospetto " + prgProspettoInf);

		SourceBean res = (SourceBean) tex.executeQuery("CM_GET_PRGDOCUMENTO_DA_RETTIFICARE_BY_PRGPROSPETTOINF", param,
				"SELECT");

		BigDecimal prgDocumento = (BigDecimal) res.getAttribute("ROW.PRGDOCUMENTO");
		return prgDocumento;
	}

	private void rettificaDocumento(BigDecimal prgDocumento, TransactionQueryExecutor tex) throws EMFInternalError {
		Object[] param = new Object[2];
		param[0] = cdnUt;
		param[1] = prgDocumento;

		tex.executeQuery("CM_RETTIFICA_PROSPETTO", param, "UPDATE");

	}

	private void insertRiepilogoNazionale(SourceBean riepilogoNaz, String prgProspettoInf, TransactionQueryExecutor tex)
			throws EMFInternalError {

		SourceBean quotariservadisabiliSB = (SourceBean) riepilogoNaz.getAttribute("QUOTARISERVADISABILI");
		SourceBean quotariservacatprotetteSB = (SourceBean) riepilogoNaz.getAttribute("QUOTARISERVACATPROTETTE");
		SourceBean esoneriSB = (SourceBean) riepilogoNaz.getAttribute("ESONERI");
		SourceBean disabiliSB = (SourceBean) riepilogoNaz.getAttribute("DISABILI");
		SourceBean catprotetteSB = (SourceBean) riepilogoNaz.getAttribute("CATPROTETTE");
		SourceBean scoperturedisabiliSB = (SourceBean) riepilogoNaz.getAttribute("SCOPERTUREDISABILI");
		SourceBean scoperturecatprotetteSB = (SourceBean) riepilogoNaz.getAttribute("SCOPERTURECATPROTETTE");
		SourceBean noteSB = (SourceBean) riepilogoNaz.getAttribute("STRNOTE");
		SourceBean numBaseComputoArt3 = (SourceBean) riepilogoNaz.getAttribute("NUMLAVBASECOMPUTOART3");
		SourceBean numBaseComputoArt18 = (SourceBean) riepilogoNaz.getAttribute("NUMLAVBASECOMPUTOART18");
		SourceBean numEsuberiArt18 = (SourceBean) riepilogoNaz.getAttribute("QUOTAESUBERIART18");
		SourceBean sospIncorso = (SourceBean) riepilogoNaz.getAttribute("SOSPENSIONIINCORSO");

		BigDecimal numlavoratori = null;
		BigDecimal quotariservadisabili = new BigDecimal(quotariservadisabiliSB.getCharacters());
		BigDecimal quotariservacatprotette = new BigDecimal(quotariservacatprotetteSB.getCharacters());
		BigDecimal sospensioni = null;
		BigDecimal esoneri = new BigDecimal(esoneriSB.getCharacters());
		BigDecimal disabili = new BigDecimal(disabiliSB.getCharacters());
		BigDecimal catprotette = new BigDecimal(catprotetteSB.getCharacters());
		BigDecimal catprotettecontatecomedisabili = null;
		BigDecimal scoperturedisabili = new BigDecimal(scoperturedisabiliSB.getCharacters());
		BigDecimal scoperturecatprotette = new BigDecimal(scoperturecatprotetteSB.getCharacters());
		String strNote = (noteSB != null) ? noteSB.getCharacters() : null;
		BigDecimal numlavoratoriBC3 = new BigDecimal(numBaseComputoArt3.getCharacters());
		BigDecimal numlavoratoriBC18 = new BigDecimal(numBaseComputoArt18.getCharacters());
		String flgSospInCorso = (sospIncorso != null) ? sospIncorso.getCharacters() : null;
		BigDecimal numEsuberi = null;
		if (numEsuberiArt18 != null) {
			numEsuberi = new BigDecimal(numEsuberiArt18.getCharacters());
		}

		Object[] param = new Object[16];
		{
			int j = 0;
			param[j++] = new BigDecimal(prgProspettoInf);// prgprospettoinf
			param[j++] = numlavoratori; // numlavoratoribc
			param[j++] = quotariservadisabili; // numquotarisdisabili
			param[j++] = quotariservacatprotette; // numquotariscatprot
			param[j++] = sospensioni; // numsospensioni
			param[j++] = esoneri; // numesoneri
			param[j++] = disabili; // numdisabiliforza
			param[j++] = catprotette; // numcatprotforza
			param[j++] = catprotettecontatecomedisabili;// numcatprotdisabiliforza
			param[j++] = scoperturedisabili; // numscoperturadis
			param[j++] = scoperturecatprotette; // numscoperturacatprot
			param[j++] = strNote; // strnote
			param[j++] = numlavoratoriBC3; // numlavoratoriBC3
			param[j++] = numlavoratoriBC18; // numlavoratoriBC18
			param[j++] = flgSospInCorso; // flgSospInCorso
			param[j++] = numEsuberi; // quotaEsuberiArt18
		}

		tex.executeQuery("INSERT_CM_RIEPILOGONAZIONALE", param, "INSERT");

	}

	private void insertElencoRiepilogativoProvinciale(SourceBean elencoRiep, String prgProspettoInf,
			TransactionQueryExecutor tex) throws Exception {

		SourceBean provinciaSB = (SourceBean) elencoRiep.getAttribute("PROVINCIA");
		SourceBean categoriecompdisabiliSB = (SourceBean) elencoRiep.getAttribute("CATEGORIACOMPDISABILI");
		SourceBean numerocompdisabiliSB = (SourceBean) elencoRiep.getAttribute("NUMEROCOMPDISABILI");
		SourceBean categoriacompcatprotetteSB = (SourceBean) elencoRiep.getAttribute("CATEGORIACOMPCATPROTETTE");
		SourceBean numerocompcatprotetteSB = (SourceBean) elencoRiep.getAttribute("NUMEROCOMPCATPROTETTE");
		SourceBean disabiliSB = (SourceBean) elencoRiep.getAttribute("DISABILI");
		SourceBean catprotetteSB = (SourceBean) elencoRiep.getAttribute("CATPROTETTE");
		SourceBean quotariservadisabiliSB = (SourceBean) elencoRiep.getAttribute("QUOTARISERVADISABILI");
		SourceBean quotariservacatprotetteSB = (SourceBean) elencoRiep.getAttribute("QUOTARISERVACATPROTETTE");
		SourceBean esoneratiSB = (SourceBean) elencoRiep.getAttribute("ESONERATI");
		SourceBean scoperturedisabiliSB = (SourceBean) elencoRiep.getAttribute("SCOPERTUREDISABILI");
		SourceBean scoperturecatprotetteSB = (SourceBean) elencoRiep.getAttribute("SCOPERTURECATPROTETTE");
		SourceBean numBaseComputoArt3 = (SourceBean) elencoRiep.getAttribute("NUMLAVBASECOMPUTOART3");
		SourceBean numBaseComputoArt18 = (SourceBean) elencoRiep.getAttribute("NUMLAVBASECOMPUTOART18");
		SourceBean sospIncorso = (SourceBean) elencoRiep.getAttribute("SOSPENSIONIINCORSO");

		// Elimina gli zero inutili
		String provincia = Integer.valueOf(provinciaSB.getCharacters()).toString();
		BigDecimal numlavoratori = null;
		BigDecimal sospensioni = null;
		BigDecimal disabili = new BigDecimal(disabiliSB.getCharacters());
		BigDecimal catprotette = new BigDecimal(catprotetteSB.getCharacters());
		BigDecimal catprotettecontatecomedisabili = null;
		BigDecimal quotariservadisabili = new BigDecimal(quotariservadisabiliSB.getCharacters());
		BigDecimal quotariservacatprotette = new BigDecimal(quotariservacatprotetteSB.getCharacters());
		BigDecimal esonerati = new BigDecimal(esoneratiSB.getCharacters());
		BigDecimal scoperturedisabili = new BigDecimal(scoperturedisabiliSB.getCharacters());
		BigDecimal scoperturecatprotette = new BigDecimal(scoperturecatprotetteSB.getCharacters());
		BigDecimal numlavoratoriBC3 = new BigDecimal(numBaseComputoArt3.getCharacters());
		BigDecimal numlavoratoriBC18 = new BigDecimal(numBaseComputoArt18.getCharacters());
		String flgSospInCorso = (sospIncorso != null) ? sospIncorso.getCharacters() : null;

		String categoriecompdisabili = null;
		if (categoriecompdisabiliSB != null)
			categoriecompdisabili = categoriecompdisabiliSB.getCharacters();

		BigDecimal numerocompdisabili = null;
		if (numerocompdisabiliSB != null)
			numerocompdisabili = new BigDecimal(numerocompdisabiliSB.getCharacters());

		String categoriacompcatprotette = null;
		if (categoriacompcatprotetteSB != null)
			categoriacompcatprotette = categoriacompcatprotetteSB.getCharacters();

		BigDecimal numerocompcatprotette = null;
		if (numerocompcatprotetteSB != null)
			numerocompcatprotette = new BigDecimal(numerocompcatprotetteSB.getCharacters());

		Object[] param = new Object[19];
		{
			int j = 0;
			param[j++] = numlavoratori; // numlavoratoribc
			param[j++] = sospensioni; // numsospensioni
			param[j++] = disabili; // numdisabiliforza
			param[j++] = catprotette; // numcatprotforza
			param[j++] = catprotettecontatecomedisabili;// numcatprotdisabiliforza
			param[j++] = quotariservadisabili; // numquotarisdisabili
			param[j++] = quotariservacatprotette; // numquotariscatprot
			param[j++] = esonerati; // numesoneri
			param[j++] = scoperturedisabili; // numscoperturadis
			param[j++] = scoperturecatprotette; // numscoperturacatprot
			param[j++] = numerocompcatprotette; // numcompcatprot
			param[j++] = numerocompdisabili; // numcompdisabili
			param[j++] = new BigDecimal(prgProspettoInf);// prgprospettoinf
			param[j++] = provincia; // codprovincia
			param[j++] = categoriecompdisabili; // codmonoeccdiffdisabili
			param[j++] = categoriacompcatprotette; // codmonoeccdiffcatprot
			param[j++] = numlavoratoriBC3; // numlavoratoriBC3
			param[j++] = numlavoratoriBC18; // numlavoratoriBC18
			param[j++] = flgSospInCorso; // flgSospInCorso
		}

		tex.executeQuery("INSERT_CM_RIEPILOGOPROVINCIALE", param, "INSERT");

	}

	/**
	 * Eseguo la funzione PG_COLL_MIRATO_2.GET_PERC_INVALIDITA_L68: se l'azienda è di fascia 'C' (Da 16 a 35 dipendenti)
	 * e il lavoratore ha una percentuale d'invalidità superiore al 50%
	 * 
	 * ALLORA
	 * 
	 * imposta la copertura del lavoratore a 1
	 * 
	 * @param tex
	 * @param prgProspettoInf
	 * @param cdnLavoratore
	 * @param encryptKey
	 */

	public String getPercInvalidita(TransactionQueryExecutor txExecutor, String p_prgprospettoInf,
			String p_cdnLavoratore, String encryptKey) {

		String checkPercInvalidita = "";
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		try {
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList<DataField> parameters = null;
			conn = txExecutor.getDataConnection();

			sqlStr = SQLStatements.getStatement("GET_PERC_INVALIDITA_L68");
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			parameters = new ArrayList<DataField>(5);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 2. p_prgRichiestaAz
			parameters.add(conn.createDataField("p_prgprospettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgprospettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgTipoIncrocio
			parameters.add(
					conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, new BigInteger(p_cdnLavoratore)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_key
			parameters.add(conn.createDataField("p_encrypterKey", java.sql.Types.VARCHAR, encryptKey));
			command.setAsInputParameters(paramIndex++);
			// 5. checkPercInvalidita
			parameters.add(conn.createDataField("p_checkPercInvalidita", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			// 1. checkPercInvalidita
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			checkPercInvalidita = df.getStringValue();

			// SourceBean row = new SourceBean("ROW");
			// row.setAttribute("CodiceRit", codiceRit);
			if (!codiceRit.equals("0")) {
				// int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					// msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Check verifica perc invalidità lavoratori L68. sqlCode=" + errCode;
					break;
				default:
					// msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Check verifica perc invalidità lavoratori L68. SqlCode=" + errCode;
				}
				_logger.debug(msg);
			}
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure perc invalidità lavoratori L68";
			_logger.debug(msg);
		}
		return checkPercInvalidita;
	}

	/**
	 */
	private Documento protocollaDocumento(BigDecimal prgAzienda, BigDecimal prgUnita, String dataInvio,
			String prgprospettoinf_storicizzato, TransactionQueryExecutor tex) throws Exception {
		Documento doc = null;
		try {
			preparaContesto();
			doc = new Documento();
			String dataOdierna = DateUtils.getNow();
			SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String dataEOraProtocollo = fd.format(new Date());
			String annoProtocollo = dataOdierna.substring(6, 10);

			String utenteCoordinamento = "190";

			doc.setPrgDocumento(null); // nuovo documento
			doc.setCdnLavoratore(null);
			doc.setPrgAzienda(prgAzienda);
			doc.setPrgUnita(prgUnita);
			doc.setCodTipoDocumento("PINF");
			doc.setStrDescrizione("Prospetto informativo " + prgprospettoinf_storicizzato);
			doc.setDatInizio(dataOdierna);
			doc.setCodMonoIO("I");
			doc.setTipoProt("S");
			doc.setDatAcqril(dataOdierna);
			// valori generici necessari per la protocollazione
			doc.setDatProtocollazione(dataEOraProtocollo);
			doc.setNumAnnoProt(new BigDecimal(annoProtocollo));
			doc.setNumProtocollo(new BigDecimal(0));
			doc.setCdnUtIns(new BigDecimal(utenteCoordinamento));
			doc.setCdnUtMod(new BigDecimal(utenteCoordinamento));
			doc.setCodStatoAtto("PR");
			doc.setPagina("CMProspRiepilogoPage");
			doc.setChiaveTabella(prgprospettoinf_storicizzato);

			// SourceBean proto = (SourceBean) ConfigSingleton.getInstance().getAttribute("PROTOCOLLO");
			// _logger.fatal("configurazione protocollo: "+proto);
			doc.insert(tex);
			rimuoviContesto();
			return doc;

		} catch (Exception e) {
			StringBuffer msg = new StringBuffer(100);
			msg.append("protocollaDocumento() fallito. Servizio registra prospetto informativo da NCR. ");
			msg.append("prgAzienda=");
			msg.append(prgAzienda);
			msg.append(", prgUnita=");
			msg.append(prgUnita);
			msg.append(", dataInvio=");
			msg.append(dataInvio);

			if (_logger.isEnabledFor(Level.DEBUG))
				_logger.debug(msg.toString(), e);
			rimuoviContesto();
			throw e;
		}
	}

	private void aggiornaPECemailSedeLegale(BigDecimal prgAzienda, BigDecimal prgUnita, String strPECemail,
			TransactionQueryExecutor tex, BigDecimal numklo) throws EMFInternalError {

		Object[] param = new Object[5];

		param[0] = strPECemail;
		param[1] = cdnUt;
		param[2] = numklo;
		param[3] = prgAzienda;
		param[4] = prgUnita;

		try {
			tex.executeQuery("COOP_UPD_PEC_EMAIL", param, "UPDATE");
		} catch (EMFInternalError e) {
			_logger.debug("RegistraProspettoInformativo: ERRORE AGGIORNAMENTO PEC EMAIL SEDE LEGALE", e);
			throw e;
		}

	}

	private int aggiornaSedeLegale(BigDecimal prgAzienda, BigDecimal prgUnita, BigDecimal numkloUAZ,
			TransactionQueryExecutor tex) throws Exception {

		String stmProcedure = "{ call ? := PG_GESTAMM.AGGIORNASEDELEGALE(?,?,?,?) }";
		CallableStatement command = null;
		ResultSet dr = null;
		command = tex.getDataConnection().getInternalConnection().prepareCall(stmProcedure);
		command.registerOutParameter(1, Types.NUMERIC);
		command.setBigDecimal(2, prgAzienda);
		command.setBigDecimal(3, prgUnita);
		command.setBigDecimal(4, numkloUAZ);
		command.setBigDecimal(5, new BigDecimal(cdnUt));

		dr = command.executeQuery();

		int codiceRit = command.getInt(1);
		dr.close();
		command.close();

		return codiceRit;
	}

	private BigDecimal inserisciSedeAzienda(SourceBean anUnitaAzienda, BigDecimal prgAzienda, String codCom,
			String strIndirizzo, TransactionQueryExecutor tex) {
		BigDecimal prgUnita = null;
		try {
			String codAzStato = "1";// in attivita'
			String codAteco = (String) anUnitaAzienda.getAttribute("codAteco");
			String codCCNL = (String) anUnitaAzienda.getAttribute("codCcnl");
			String flgSede = (String) anUnitaAzienda.getAttribute("flgSede");
			String strCap = (String) anUnitaAzienda.getAttribute("strCap");
			String strCognome = (String) anUnitaAzienda.getAttribute("strCognome");
			String strNome = (String) anUnitaAzienda.getAttribute("strNome");
			String strDenominazione = (String) anUnitaAzienda.getAttribute("strDenominazione");
			String stremail = (String) anUnitaAzienda.getAttribute("strEMail");
			String strfax = (String) anUnitaAzienda.getAttribute("strFax");
			String strTel = (String) anUnitaAzienda.getAttribute("strTel");
			String strReferente = strNome + " " + strCognome;
			String strPECemail = (String) anUnitaAzienda.getAttribute("strPECemail");
			UnitaAziendaBean unita = new UnitaAziendaBean(prgAzienda, codCom, codAzStato, codAteco, strIndirizzo, cdnUt,
					cdnUt, tex);
			unita.setCodCCNL(codCCNL);
			unita.setFlgSede(flgSede);
			unita.setStrCap(strCap);
			unita.setStrReferente(strReferente);
			unita.setStrDenominazione(strDenominazione);
			unita.setStrEmail(stremail);
			unita.setStrFax(strfax);
			unita.setStrTel(strTel);
			unita.setStrPECemail(strPECemail);
			//
			unita.insert();
			prgUnita = new BigDecimal(unita.getPrgUnita());
			return prgUnita;
		} catch (Exception e) {
			_logger.debug("RegistraProspettoInformativo: ERRORE=. " + e);
			return null;
		}
	}

	private void preparaContesto() {
		try {
			rc = new RequestContainer();
			ResponseContainer respCont = new ResponseContainer();

			RequestContainer.setRequestContainer(rc);

			SourceBean serviceRequest = new SourceBean("SERVICE_REQUEST");
			SourceBean serviceResponse = new SourceBean("SERVICE_RESPONSE");

			SessionContainer sessionCont = new SessionContainer(false);
			rc.setServiceRequest(serviceRequest);
			rc.setSessionContainer(sessionCont);
			respCont.setErrorHandler(new EMFErrorHandler());
			respCont.setServiceResponse(serviceResponse);

			BigDecimal codUtente = new BigDecimal(190);
			sessionCont.setAttribute(Values.CODUTENTE, codUtente);

			SourceBean sb = StatementUtils.getSourceBeanByStatement("GET_UTENTE_INFO", codUtente.toString());
			String username = Utils.notNull(sb.getAttribute("ROW.STRLOGIN"));
			String nome = Utils.notNull(sb.getAttribute("ROW.STRNOME"));
			String cognome = Utils.notNull(sb.getAttribute("ROW.STRCOGNOME"));

			User user = new User(codUtente.intValue(), username, nome, cognome);
			sessionCont.setAttribute(User.USERID, user);

			user.setCdnProfilo(4); // Per i permessi sul lavoratore o sull'azienda // TODO
			SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_CODCPICAPOLUOGO", null, "SELECT",
					Values.DB_SIL_DATI);
			String codCpiCapoluogo = (String) row.getAttribute("ROW.RESULT");
			user.setCodRif(codCpiCapoluogo); // Per il calcolo del CPI di competenza
		} catch (Exception e) {
			_logger.error("impossibile creare il contesto framework del servizio per la protocollazione del documento",
					e);

		}
	}

	private void rimuoviContesto() {
		if (this.rc != null)
			RequestContainer.delRequestContainer();
	}

}