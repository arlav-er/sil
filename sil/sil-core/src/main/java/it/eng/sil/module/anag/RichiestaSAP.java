package it.eng.sil.module.anag;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.pojo.yg.richiestaSAP.IDSAP;
import it.eng.sil.pojo.yg.sap.LavoratoreType;
import it.eng.sil.pojo.yg.sap.PoliticheAttive;
import it.eng.sil.util.Utils;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;

public class RichiestaSAP extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RichiestaSAP.class.getName());
	private String className = this.getClass().getName();
	private static final String SERVIZIO_SAP = "InvioRichiestaSAP";

	@SuppressWarnings("unused")
	private static final String INPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "sap" + File.separator + "Rev008_SAP.xsd";
	@SuppressWarnings("unused")
	private static final String INPUT_XSD_SAP2 = ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "sap" + File.separator + "Rev009_SAP.xsd";

	private static final String INPUT_XSD_SAP_VIEW = ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "sap" + File.separator + "Rev.009_SAP_view.xsd";

	/* modificato settembre 2019 */
	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult rep = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		String codminsap = Utils.notNull(request.getAttribute("CODMINSAP"));

		it.eng.sil.pojo.yg.sap.view.LavoratoreType lavTSapView = new it.eng.sil.pojo.yg.sap.view.LavoratoreType();

		@SuppressWarnings("unused")
		String pool = (String) getConfig().getAttribute("POOL");

		try {
			_logger.info("CHIAMATA SAP, CODMINSAP= " + codminsap);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String invioSapEP = eps.getUrl(SERVIZIO_SAP);
			ServizicoapWSProxy proxy = new ServizicoapWSProxy();
			proxy.setEndpoint(invioSapEP);

			IDSAP xmlCodiceSAP = new IDSAP();
			xmlCodiceSAP.setIdentificativoSap(codminsap);

			List<it.eng.sil.pojo.yg.sap.view.PoliticheAttive> listaPView = null;

			List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaPolitiche = null;

			String xmlSAP = proxy.richiestaSAP(convertRichiestaSapToString(xmlCodiceSAP));

			if (request.containsAttribute("importaSAP")) {
				it.eng.sil.pojo.yg.sap.due.LavoratoreType lavoratoreSap = null;
				if (xmlSAP != null && !("").equalsIgnoreCase(xmlSAP) && !xmlSAP.trim().startsWith("X001")) {
					try {
						_logger.debug("VALIDAZIONE XML RISPETTO A SAP2.0");
						if (validazioneXml(xmlSAP, INPUT_XSD_SAP_VIEW)) {
							_logger.debug("VALIDAZIONE SAP2.0 OK");
							lavoratoreSap = convertToLavoratoreSAP2(xmlSAP);
							if (lavoratoreSap != null) {
								it.eng.sil.pojo.yg.sap.due.PoliticheAttiveLst allListaLav = lavoratoreSap
										.getPoliticheAttiveLst();
								if (allListaLav != null) {
									listaPolitiche = allListaLav.getPoliticheAttive();
								} else {
									listaPolitiche = new ArrayList<it.eng.sil.pojo.yg.sap.due.PoliticheAttive>();
								}
							}
						} else {
							lavoratoreSap = null;
						}
					} catch (Exception exValidazione) {
						_logger.error(exValidazione);
					}
					if (listaPolitiche != null && listaPolitiche.size() > 0) {
						try {
							ordinamentoPolitche2(listaPolitiche);
						} catch (Exception ex) {
							_logger.error(className + "ordinamento politiche attive ::service: " + ex.getMessage());
						}
					}
					if (lavTSapView != null) {
						response.setAttribute("SAPWS2", lavoratoreSap);
					}
				} else {

					if (xmlSAP != null && xmlSAP.trim().startsWith("X001")) {
						Vector<String> params = new Vector<String>();
						params.add(xmlSAP);
						rep.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
								"Errore ministero SAP", params);
					} else {
						rep.reportFailure(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO);
					}
				}
			} else {

				if (xmlSAP != null && !("").equalsIgnoreCase(xmlSAP) && !xmlSAP.trim().startsWith("X001")) {
					try {
						_logger.debug("VALIDAZIONE XML RISPETTO A SAP2.0");
						if (validazioneXml(xmlSAP, INPUT_XSD_SAP_VIEW)) {
							_logger.debug("VALIDAZIONE SAP2.0 OK");
							lavTSapView = convertToLavoratoreSAPView(xmlSAP);
							if (lavTSapView != null) {
								it.eng.sil.pojo.yg.sap.view.PoliticheAttiveLst allListaP = lavTSapView
										.getPoliticheAttiveLst();
								if (allListaP != null) {
									listaPView = allListaP.getPoliticheAttive();
								} else {
									listaPView = new ArrayList<it.eng.sil.pojo.yg.sap.view.PoliticheAttive>();
								}
							}
						} else {
							lavTSapView = null;
						}
					} catch (Exception exValidazione) {
						_logger.error(exValidazione);
					}
					if (listaPView != null && listaPView.size() > 0) {
						try {
							ordinamentoPolitcheView(listaPView);
						} catch (Exception ex) {
							_logger.error(className + "ordinamento politioche attive ::service: " + ex.getMessage());
						}
					}
					if (lavTSapView != null) {
						response.setAttribute("SAPWS2_VIEW", lavTSapView);
					}
				} else {

					if (xmlSAP != null && xmlSAP.trim().startsWith("X001")) {
						Vector<String> params = new Vector<String>();
						params.add(xmlSAP);
						rep.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
								"Errore ministero SAP", params);
					} else {
						rep.reportFailure(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO);
					}
				}
			}

			_logger.info("CHIAMATA RICHIESTA SAP, CODMINSAP=" + codminsap + " - RISPOSTA=" + xmlSAP);
		} // try
		catch (Exception ex) {
			rep.reportFailure(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP, ex, "service()",
					"errore chiamata servizio richiesta sap");
		} // catch (Exception ex)
	}

	// Borriello: codice prima di settembre 2019
	/*
	 * public void service(SourceBean request, SourceBean response) { ReportOperationResult rep = new
	 * ReportOperationResult(this, response); disableMessageIdFail(); disableMessageIdSuccess();
	 * 
	 * String codminsap = Utils.notNull(request.getAttribute("CODMINSAP"));
	 * 
	 * LavoratoreType lavTSap = new LavoratoreType(); it.eng.sil.pojo.yg.sap.due.LavoratoreType lavTSap2 = new
	 * it.eng.sil.pojo.yg.sap.due.LavoratoreType ();
	 * 
	 * boolean isSap2=true; String pool = (String) getConfig().getAttribute("POOL");
	 * 
	 * // BigDecimal sap2 =
	 * getResponseContainer().getServiceResponse().containsAttribute("M_CheckSap2.ROWS.ROW.datediff")? // (BigDecimal)
	 * getResponseContainer().getServiceResponse().getAttribute("M_CheckSap2.ROWS.ROW.datediff"):null; // if (sap2!=null
	 * && sap2.intValue()>=0) { // isSap2 = true; // } // else { // isSap2 = false; // }
	 * 
	 * try { _logger.info("CHIAMATA SAP, CODMINSAP="+codminsap);
	 * 
	 * DataSourceJNDI dataSourceJndi = new DataSourceJNDI(); String dataSourceJndiName = dataSourceJndi.getJndi();
	 * 
	 * EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName); String invioSapEP =
	 * eps.getUrl(SERVIZIO_SAP); ServizicoapWSProxy proxy = new ServizicoapWSProxy(); proxy.setEndpoint(invioSapEP);
	 * 
	 * IDSAP xmlCodiceSAP = new IDSAP(); xmlCodiceSAP.setIdentificativoSap(codminsap);
	 * 
	 * List<PoliticheAttive> listaP = null; List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaP2 = null;
	 * 
	 * String xmlSAP = proxy.richiestaSAP(convertRichiestaSapToString(xmlCodiceSAP));
	 * 
	 * if (xmlSAP != null && !("").equalsIgnoreCase(xmlSAP) && !xmlSAP.trim().startsWith("X001")) { try{ if (!isSap2) {
	 * _logger.debug("VALIDAZIONE XML RISPETTO A SAP1"); if(validazioneXml(xmlSAP, INPUT_XSD)){
	 * _logger.debug("VALIDAZIONE SAP1 OK"); lavTSap = convertToLavoratoreSAP(xmlSAP); if (lavTSap != null) { listaP =
	 * lavTSap.getPoliticheAttive(); } } else{ lavTSap = null; } } else {
	 * _logger.debug("VALIDAZIONE XML RISPETTO A SAP2.0"); if(validazioneXml(xmlSAP, INPUT_XSD_SAP2)){
	 * _logger.debug("VALIDAZIONE SAP2.0 OK"); lavTSap2 = convertToLavoratoreSAP2(xmlSAP); if (lavTSap2 != null) {
	 * it.eng.sil.pojo.yg.sap.due.PoliticheAttiveLst allListaP = lavTSap2.getPoliticheAttiveLst(); if(allListaP !=
	 * null){ listaP2 = allListaP.getPoliticheAttive(); } else{ listaP2= new
	 * ArrayList<it.eng.sil.pojo.yg.sap.due.PoliticheAttive>(); } } } else{ lavTSap2 = null; } } } catch(Exception
	 * exValidazione){ _logger.error(exValidazione); }
	 * 
	 * if (!isSap2 && listaP != null && listaP.size() > 0) { try { ordinamentoPolitche(listaP); } catch (Exception ex) {
	 * _logger.error( className + "ordinamento politioche attive ::service: " + ex.getMessage()); } }else if (isSap2 &&
	 * listaP2 != null && listaP2.size() > 0) { try { ordinamentoPolitche2(listaP2); } catch (Exception ex) {
	 * _logger.error( className + "ordinamento politioche attive ::service: " + ex.getMessage()); } } if(!isSap2 &&
	 * lavTSap!=null){ response.setAttribute("SAPWS", lavTSap); }else if(isSap2 && lavTSap2!=null){
	 * response.setAttribute("SAPWS2", lavTSap2); } }else{
	 * 
	 * if (xmlSAP != null && xmlSAP.trim().startsWith("X001")) { Vector<String> params = new Vector<String>();
	 * params.add(xmlSAP); rep.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
	 * "Errore ministero SAP", params); } else { rep.reportFailure(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO); } }
	 * 
	 * _logger.info("CHIAMATA RICHIESTA SAP, CODMINSAP="+codminsap+" - RISPOSTA="+xmlSAP); } // try catch (Exception ex)
	 * { rep.reportFailure(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP, ex, "service()",
	 * "errore chiamata servizio richiesta sap"); } // catch (Exception ex) }
	 */

	@SuppressWarnings("unchecked")
	private it.eng.sil.pojo.yg.sap.view.LavoratoreType convertToLavoratoreSAPView(String xmlSAP) {
		JAXBContext jaxbContext;
		it.eng.sil.pojo.yg.sap.view.LavoratoreType sap = null;
		try {

			jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.view.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.sil.pojo.yg.sap.view.LavoratoreType> root = (JAXBElement<it.eng.sil.pojo.yg.sap.view.LavoratoreType>) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlSAP));
			sap = root.getValue();

		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return sap;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private LavoratoreType convertToLavoratoreSAP(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		LavoratoreType sap = null;
		try {

			jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<LavoratoreType> root = (JAXBElement<LavoratoreType>) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlSAP));
			sap = root.getValue();

		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return sap;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private it.eng.sil.pojo.yg.sap.due.LavoratoreType convertToLavoratoreSAP2(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		it.eng.sil.pojo.yg.sap.due.LavoratoreType sap = null;
		try {

			jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.due.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType> root = (JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType>) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlSAP));
			sap = root.getValue();

		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return sap;
	}

	public String convertRichiestaSapToString(IDSAP richiestaSAP) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(IDSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(richiestaSAP, writer);
		String xmlRichiestaSAP = writer.getBuffer().toString();
		return xmlRichiestaSAP;
	}

	@SuppressWarnings("unused")
	private void ordinamentoPolitche(List<PoliticheAttive> listaPolAttive) throws Exception {
		for (int i = 0; i < listaPolAttive.size(); i++) {
			int posMax = i;
			PoliticheAttive itemMax = (PoliticheAttive) listaPolAttive.get(i);
			if (itemMax.getDataProposta() != null) {
				String dataPropostaMax = DateUtils.formatXMLGregorian(itemMax.getDataProposta());
				String tipoAttivitaMin = itemMax.getTipoAttivita();
				for (int j = i + 1; j < listaPolAttive.size(); j++) {
					PoliticheAttive itemCurr = (PoliticheAttive) listaPolAttive.get(j);
					if (itemCurr.getDataProposta() != null) {
						String dataPropostaCurr = DateUtils.formatXMLGregorian(itemCurr.getDataProposta());
						String tipoAttivitaCurr = itemCurr.getTipoAttivita();
						if (DateUtils.compare(dataPropostaCurr, dataPropostaMax) > 0) {
							posMax = j;
							dataPropostaMax = dataPropostaCurr;
							if (tipoAttivitaCurr != null) {
								tipoAttivitaMin = tipoAttivitaCurr;
							}
						} else {
							if (DateUtils.compare(dataPropostaCurr, dataPropostaMax) == 0) {
								if (tipoAttivitaMin != null && tipoAttivitaCurr != null
										&& tipoAttivitaCurr.compareToIgnoreCase(tipoAttivitaMin) < 0) {
									posMax = j;
									dataPropostaMax = dataPropostaCurr;
									tipoAttivitaMin = tipoAttivitaCurr;
								}
							}
						}
					}
				}
				if (posMax > i) {
					// scambio di elementi
					PoliticheAttive itemApp = (PoliticheAttive) listaPolAttive.get(posMax);
					listaPolAttive.set(posMax, listaPolAttive.get(i));
					listaPolAttive.set(i, itemApp);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void ordinamentoPolitche2(List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaPolAttive)
			throws Exception {
		for (int i = 0; i < listaPolAttive.size(); i++) {
			int posMax = i;
			it.eng.sil.pojo.yg.sap.due.PoliticheAttive itemMax = (it.eng.sil.pojo.yg.sap.due.PoliticheAttive) listaPolAttive
					.get(i);
			if (itemMax.getDataProposta() != null) {
				String dataPropostaMax = DateUtils.formatXMLGregorian(itemMax.getDataProposta());
				String tipoAttivitaMin = itemMax.getTipoAttivita();
				for (int j = i + 1; j < listaPolAttive.size(); j++) {
					it.eng.sil.pojo.yg.sap.due.PoliticheAttive itemCurr = (it.eng.sil.pojo.yg.sap.due.PoliticheAttive) listaPolAttive
							.get(j);
					if (itemCurr.getDataProposta() != null) {
						String dataPropostaCurr = DateUtils.formatXMLGregorian(itemCurr.getDataProposta());
						String tipoAttivitaCurr = itemCurr.getTipoAttivita();
						if (DateUtils.compare(dataPropostaCurr, dataPropostaMax) > 0) {
							posMax = j;
							dataPropostaMax = dataPropostaCurr;
							if (tipoAttivitaCurr != null) {
								tipoAttivitaMin = tipoAttivitaCurr;
							}
						} else {
							if (DateUtils.compare(dataPropostaCurr, dataPropostaMax) == 0) {
								if (tipoAttivitaMin != null && tipoAttivitaCurr != null
										&& tipoAttivitaCurr.compareToIgnoreCase(tipoAttivitaMin) < 0) {
									posMax = j;
									dataPropostaMax = dataPropostaCurr;
									tipoAttivitaMin = tipoAttivitaCurr;
								}
							}
						}
					}
				}
				if (posMax > i) {
					// scambio di elementi
					it.eng.sil.pojo.yg.sap.due.PoliticheAttive itemApp = (it.eng.sil.pojo.yg.sap.due.PoliticheAttive) listaPolAttive
							.get(posMax);
					listaPolAttive.set(posMax, listaPolAttive.get(i));
					listaPolAttive.set(i, itemApp);
				}
			}
		}
	}

	private void ordinamentoPolitcheView(List<it.eng.sil.pojo.yg.sap.view.PoliticheAttive> listaPolAttive)
			throws Exception {
		for (int i = 0; i < listaPolAttive.size(); i++) {
			int posMax = i;
			it.eng.sil.pojo.yg.sap.view.PoliticheAttive itemMax = (it.eng.sil.pojo.yg.sap.view.PoliticheAttive) listaPolAttive
					.get(i);
			if (itemMax.getDataProposta() != null) {
				String dataPropostaMax = DateUtils.formatXMLGregorian(itemMax.getDataProposta());
				String tipoAttivitaMin = itemMax.getTipoAttivita();
				for (int j = i + 1; j < listaPolAttive.size(); j++) {
					it.eng.sil.pojo.yg.sap.view.PoliticheAttive itemCurr = (it.eng.sil.pojo.yg.sap.view.PoliticheAttive) listaPolAttive
							.get(j);
					if (itemCurr.getDataProposta() != null) {
						String dataPropostaCurr = DateUtils.formatXMLGregorian(itemCurr.getDataProposta());
						String tipoAttivitaCurr = itemCurr.getTipoAttivita();
						if (DateUtils.compare(dataPropostaCurr, dataPropostaMax) > 0) {
							posMax = j;
							dataPropostaMax = dataPropostaCurr;
							if (tipoAttivitaCurr != null) {
								tipoAttivitaMin = tipoAttivitaCurr;
							}
						} else {
							if (DateUtils.compare(dataPropostaCurr, dataPropostaMax) == 0) {
								if (tipoAttivitaMin != null && tipoAttivitaCurr != null
										&& tipoAttivitaCurr.compareToIgnoreCase(tipoAttivitaMin) < 0) {
									posMax = j;
									dataPropostaMax = dataPropostaCurr;
									tipoAttivitaMin = tipoAttivitaCurr;
								}
							}
						}
					}
				}
				if (posMax > i) {
					// scambio di elementi
					it.eng.sil.pojo.yg.sap.view.PoliticheAttive itemApp = (it.eng.sil.pojo.yg.sap.view.PoliticheAttive) listaPolAttive
							.get(posMax);
					listaPolAttive.set(posMax, listaPolAttive.get(i));
					listaPolAttive.set(i, itemApp);
				}
			}
		}
	}

	/* Valida l'xml passato in input con l'xsd passato come secondo parametro */
	private static boolean validazioneXml(String xml, String xsdPath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			File schemaFile = new File(xsdPath);
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			StreamSource datiXmlStreamSource = new StreamSource(new StringReader(xml));
			validator.validate(datiXmlStreamSource);
			return true;
		} catch (Exception e) {
			_logger.error(e);
			return false;
		}
	}

}