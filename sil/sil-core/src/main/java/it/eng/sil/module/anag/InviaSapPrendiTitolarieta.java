package it.eng.sil.module.anag;

import java.io.StringWriter;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.conf.did.ConferimentoUtility;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.pojo.yg.sap.due.PoliticheAttive;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class InviaSapPrendiTitolarieta extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InviaSapPrendiTitolarieta.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		int resultDid = 0;
		int resultPresaCarico = 0;
		String dataDid = "";
		ReportOperationResult reportOperation = null;
		boolean esitoInvioSAP = false;
		try {
			SourceBean serviceResponse = getResponseContainer().getServiceResponse();
			reportOperation = new ReportOperationResult(this, response);
			disableMessageIdSuccess();
			disableMessageIdFail();
			String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
			dataDid = Utils.notNull(request.getAttribute("DATADID"));
			if (!dataDid.equals("")) {
				User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
				String codCpiRif = user.getCodRif();
				MultipleTransactionQueryExecutor transExec = null;
				String dataTrasferimento = Utils.notNull(request.getAttribute("DATATRASFCOMP"));
				try {
					transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
					ConferimentoUtility conferimento = new ConferimentoUtility(transExec, new BigDecimal(cdnLavoratore),
							dataDid, codCpiRif);
					try {
						transExec.initTransaction();
						resultDid = conferimento.gestisciDID(user, getRequestContainer(), request, response);
						if (resultDid == 0) {
							transExec.commitTransaction();
						} else {
							transExec.rollBackTransaction();
						}

						// gestione presa in carico
						try {
							transExec.initTransaction();
							resultPresaCarico = conferimento.gestisciPresaInCarico150Trasferimento(user, "AVV", "E",
									dataTrasferimento);
							if (resultPresaCarico == 0) {
								transExec.commitTransaction();
							} else {
								transExec.rollBackTransaction();
							}
						} catch (Exception e2) {
							transExec.rollBackTransaction();
							it.eng.sil.util.TraceWrapper.error(_logger,
									"Errore in verifica azione presa in carico 150.", (Exception) e2);
						}
					} catch (Exception e1) {
						transExec.rollBackTransaction();
						it.eng.sil.util.TraceWrapper.error(_logger, "Errore in inserimento did.", (Exception) e1);
					}
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);
				} finally {
					if (transExec != null) {
						transExec.closeConnection();
					}
				}
			}

			// INVIO SAP
			ModuleIFace moduleGestioneInvioSAP = null;
			try {
				SourceBean serviceRequest = getRequestContainer().getServiceRequest();
				serviceRequest.setAttribute("INVIASAPFROMTRASFERIMENTO", "S");
				if (serviceResponse.containsAttribute("M_InfoTsGenerale.ROWS.ROW")) {
					String flgPoloReg = serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.FLGPOLOREG").toString();
					String codProvinciaSil = serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.CODPROVINCIASIL")
							.toString();
					String codRegioneSil = serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.CODREGIONESIL")
							.toString();
					String dataSap2 = serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.DATSAP2") != null
							? serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.DATSAP2").toString()
							: "";
					serviceRequest.setAttribute("FLGPOLOREGTRASF", flgPoloReg);
					serviceRequest.setAttribute("PROVINCIASILTRASF", codProvinciaSil);
					serviceRequest.setAttribute("REGIONESILTRASF", codRegioneSil);
					serviceRequest.setAttribute("DATSAP2", dataSap2);
				}
				DefaultRequestContext drc = new DefaultRequestContext(getRequestContainer(), getResponseContainer());
				moduleGestioneInvioSAP = ModuleFactory.getModule("M_GestioneInvioSap");
				((AbstractModule) moduleGestioneInvioSAP).setRequestContext(drc);
				moduleGestioneInvioSAP.service(getRequestContainer().getServiceRequest(), response);
				response.setAttribute("SAPTITOLARIETAINVIATA", "S");
				esitoInvioSAP = true;
				if (!dataDid.equals("")) {
					reportOperation.reportSuccess(MessageCodes.Trasferimento.SAP_INVIATA);
				} else {
					reportOperation.reportSuccess(MessageCodes.Trasferimento.SAP_INVIATA_SOLO_TITOLARIETA);
				}
			} catch (Exception me) {
				if (!response.containsAttribute("ERRORENOLOG")) {
					it.eng.sil.util.TraceWrapper.error(_logger, "InviaSapPrendiTitolarieta:service():errore inviaSAP",
							me);
				}
			} finally {
				if (!dataDid.equals("")) {
					if (resultDid == 0) {
						if (esitoInvioSAP) {
							reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_DID_INSERITA);
						} else {
							reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.SUCCESS_DID_INSERITA);
						}
					} else {
						reportOperation.reportFailure(resultDid);
					}
					if (resultPresaCarico == 0) {
						if (esitoInvioSAP) {
							reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_PRESA_CARICO150);
						} else {
							reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.SUCCESS_PRESA_CARICO150);
						}
					} else {
						reportOperation.reportFailure(resultPresaCarico);
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);
		}
	}

	public PoliticheAttive loadPresaInCarico(ConferimentoUtility conferimento) {
		PoliticheAttive presaInCarico = null;
		try {
			Object params[] = new Object[2];
			params[0] = conferimento.getPrgPercorsoA02();
			params[1] = conferimento.getPrgColloquioA02();
			SourceBean row = null;
			row = (SourceBean) conferimento.getTransExec().executeQuery("SELECT_PRESA_IN_CARICO_A02_05", params,
					"SELECT");
			if (row != null) {
				row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
				presaInCarico = new PoliticheAttive();
				String tipoAttivita = row.containsAttribute("CODTIPOATTIVITA")
						? row.getAttribute("CODTIPOATTIVITA").toString()
						: "";
				String denominazione = row.containsAttribute("STRDESCRIZIONE")
						? row.getAttribute("STRDESCRIZIONE").toString()
						: "";
				String dataColl = row.containsAttribute("DATCOLLOQUIO") ? row.getAttribute("DATCOLLOQUIO").toString()
						: "";
				String dataAvvio = row.containsAttribute("DATAVVIOAZIONE")
						? row.getAttribute("DATAVVIOAZIONE").toString()
						: "";
				String dataEffettiva = row.containsAttribute("DATEFFETTIVA")
						? row.getAttribute("DATEFFETTIVA").toString()
						: "";
				String codProgetto = row.containsAttribute("CODPROGETTO") ? row.getAttribute("CODPROGETTO").toString()
						: "";
				String codCpiMin = row.containsAttribute("CODCPIMIN") ? row.getAttribute("CODCPIMIN").toString() : "";

				presaInCarico.setTipoAttivita(tipoAttivita);
				presaInCarico.setTitoloDenominazione(denominazione);
				presaInCarico.setDataProposta(DateUtils.toXMLGregorianCalendarDate(dataColl));
				presaInCarico.setData(DateUtils.toXMLGregorianCalendarDate(dataAvvio));
				presaInCarico.setDataFine(DateUtils.toXMLGregorianCalendarDate(dataEffettiva));
				presaInCarico.setDescrizione("Presa in carico per trasferimento");
				presaInCarico.setTitoloProgetto(codProgetto);
				presaInCarico.setCodiceEntePromotore(codCpiMin);
			}
			return presaInCarico;

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);
			return null;
		}

	}

	public String convertLavoratoreTypeToString2(it.eng.sil.pojo.yg.sap.due.LavoratoreType lavoratoreSAP)
			throws JAXBException {
		StringWriter stringWriter = new StringWriter();

		JAXBContext jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.due.LavoratoreType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		QName qName = new QName("", "lavoratore");
		JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType> root = new JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType>(
				qName, it.eng.sil.pojo.yg.sap.due.LavoratoreType.class, lavoratoreSAP);

		jaxbMarshaller.marshal(root, stringWriter);

		String result = stringWriter.toString();

		return result;
	}

}