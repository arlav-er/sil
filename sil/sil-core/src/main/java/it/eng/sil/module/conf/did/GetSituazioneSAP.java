package it.eng.sil.module.conf.did;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.rpc.JAXRPCException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.anag.VerificaEsistenzaSAP;
import it.eng.sil.pojo.yg.richiestaSAP.IDSAP;
import it.eng.sil.pojo.yg.sap.due.LavoratoreType;
import it.eng.sil.pojo.yg.sap.due.ObjectFactory;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.gov.lavoro.servizi.servizicoap.richiestasap.ListaDIDType;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;

public class GetSituazioneSAP extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7094878633560852719L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetSituazioneSAP.class.getName());

	public static final String END_POINT_NAME_VERIFICASAP = "VerificaEsistenzaSAP";
	public static final String END_POINT_NAME_RICHIESTASAP = "InvioRichiestaSAP";
	public static final String END_POINT_NAME_RICHIESTASAP_N00A02 = "richiestaSAPN00A02";

	private String descErroreMsgMinistero;

	@SuppressWarnings("rawtypes")
	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer requestContainer = getRequestContainer();
		disableMessageIdFail();
		disableMessageIdSuccess();

		boolean richiestaSap = false;
		boolean verificaEsistenzaSap = false;
		this.descErroreMsgMinistero = "";

		SourceBean datiLavBean = null;
		String codMinSap = "";
		String codstatoSAP = "";
		String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));

		this.setSectionQuerySelect("QUERY_SAP");
		SourceBean sbResult = doSelect(request, response);
		Vector vecRes = sbResult.getAttributeAsVector("ROW");
		SourceBean sapLav = null;
		if (vecRes != null && !vecRes.isEmpty() && vecRes.size() > 1) {
			// PIU' DI UN SP_LAVORATORE TROVATO
			reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP);
			String errMsg = "GetSituazioneSAP: fallito. Trovata piu' di una riga su SP_LAVORATORE con CDNLAVORATORE = "
					+ cdnLavoratore + " e DATFINEVAL == NULL.";
			_logger.error(errMsg);
		} else if (vecRes != null && !vecRes.isEmpty() && vecRes.size() == 1) {

			sapLav = (SourceBean) vecRes.elementAt(0);
			codMinSap = StringUtils.getAttributeStrNotNull(sapLav, "CODMINSAP");
			codstatoSAP = StringUtils.getAttributeStrNotNull(sapLav, "CODSTATO");

			// verifico se la sap e' bruciata o annullata
			if (codstatoSAP.equals("02") || codstatoSAP.equals("03")) {
				verificaEsistenzaSap = true;
			} else if (codstatoSAP.equals("01") || codstatoSAP.equals("04")) {
				// se sap attiva o perdita titolarita'
				richiestaSap = true;
			}
		} else {
			verificaEsistenzaSap = true;
		}

		if (verificaEsistenzaSap) {

			try {
				VerificaEsistenzaSAP verifica = new VerificaEsistenzaSAP();
				verifica.setCruscottoDid(true);
				User user = (User) requestContainer.getSessionContainer().getAttribute("@@USER@@");
				verifica.setUser(user);
				this.setSectionQuerySelect("QUERY_CF");
				datiLavBean = doSelect(request, response, false);
				verifica.setAnLavoratoreSB(datiLavBean);
				verifica.service(request, response);

				if (verifica.getCodMinSap() != null && !verifica.getCodMinSap().equals("")
						&& !verifica.getCodMinSap().equals("0") && !verifica.getCodMinSap().trim().startsWith("X001")) {
					codMinSap = verifica.getCodMinSap();
					richiestaSap = true;
				} else {
					richiestaSap = false;
					if (verifica.getCodMinSap() != null && verifica.getCodMinSap().trim().startsWith("X001")) {
						Vector<String> params = new Vector<String>();
						params.add(verifica.getCodMinSap());
						reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
								"Errore ministero SAP", params);
					} else {
						reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO);
					}
				}

			} catch (Exception ie) {
				richiestaSap = false;
				reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP, ie, "service()",
						"errore chiamata servizio verifica sap");
			}

		}

		if (richiestaSap) {
			String codFiscaleLav = "";
			if (datiLavBean == null) {
				this.setSectionQuerySelect("QUERY_CF");
				datiLavBean = doSelect(request, response, false);
			}
			codFiscaleLav = (String) datiLavBean.getAttribute("ROW.STRCODICEFISCALE");
			richiestaSapN00A02(response, reportOperation, codFiscaleLav);
		}
		response.setAttribute("CODMINSAPWS", codMinSap);
	}

	private void richiestaSap(SourceBean response, ReportOperationResult reportOperation, String codMinSap)
			throws JAXBException, SourceBeanException {
		LavoratoreType lavSap = null;
		try {
			lavSap = sendRichiestaSAP(codMinSap);
			if (lavSap != null) {
				response.setAttribute("SAPWS", lavSap);
				response.setAttribute("ESITO_SAP", "OK");
			} else {
				if (this.descErroreMsgMinistero == null || this.descErroreMsgMinistero.equals("")) {
					reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO);
				} else {
					Vector<String> params = new Vector<String>();
					params.add(this.descErroreMsgMinistero);
					reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
							"Errore ministero SAP", params);
				}
			}
		} catch (JAXRPCException ie) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP, ie, "service()",
					"errore chiamata servizio richiesta sap");
		} catch (RemoteException ie) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP, ie, "service()",
					"errore chiamata servizio richiesta sap");
		}
	}

	private void richiestaSapN00A02(SourceBean response, ReportOperationResult reportOperation, String codFiscaleLav)
			throws JAXBException, SourceBeanException {
		ListaDIDType lavSap = null;
		try {
			lavSap = sendRichiestaSAPN00A02(codFiscaleLav);
			if (lavSap != null) {
				response.setAttribute("SAPWS", lavSap);
				response.setAttribute("ESITO_SAP", "OK");
			} else {
				if (this.descErroreMsgMinistero == null || this.descErroreMsgMinistero.equals("")) {
					reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO);
				} else {
					Vector<String> params = new Vector<String>();
					params.add(this.descErroreMsgMinistero);
					reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
							"Errore ministero SAP", params);
				}
			}
		} catch (JAXRPCException ie) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP, ie, "service()",
					"errore chiamata servizio richiesta sap");
		} catch (RemoteException ie) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP, ie, "service()",
					"errore chiamata servizio richiesta sap");
		}
	}

	/**
	 * invoca il WS per recuperare l'xml della SAP2.0 dato un codice sap ministeriale
	 * 
	 * @param codMinSap
	 * @return
	 * @throws JAXRPCException
	 * @throws RemoteException
	 * @throws JAXBException
	 */
	private LavoratoreType sendRichiestaSAP(String codMinSap) throws JAXRPCException, RemoteException, JAXBException {
		_logger.info("INIZIO CHIAMATA RICHIESTA SAP, codMinSap =" + codMinSap);

		LavoratoreType sapLav = null;

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
		String invioSapEP = eps.getUrl(END_POINT_NAME_RICHIESTASAP);
		ServizicoapWSProxy proxy = new ServizicoapWSProxy();
		// proxy.setEndpoint(newinvioSap);
		proxy.setEndpoint(invioSapEP);

		IDSAP xmlCodiceSAP = new IDSAP();
		xmlCodiceSAP.setIdentificativoSap(codMinSap);

		String xmlSAP = proxy.richiestaSAP(convertRichiestaSapToString(xmlCodiceSAP));

		if (xmlSAP != null && !("").equalsIgnoreCase(xmlSAP) && !xmlSAP.trim().startsWith("X001")) {
			sapLav = convertToLavoratoreSAP(xmlSAP);
		} else {
			if (xmlSAP != null && xmlSAP.trim().startsWith("X001")) {
				this.descErroreMsgMinistero = xmlSAP.trim();
			}
		}

		_logger.info("FINE CHIAMATA RICHIESTA SAP, codMinSap =" + codMinSap);

		return sapLav;
	}

	private ListaDIDType sendRichiestaSAPN00A02(String codFiscaleLav)
			throws JAXRPCException, RemoteException, JAXBException {
		_logger.debug("INIZIO CHIAMATA RICHIESTA SAP, codice fiscale =" + codFiscaleLav);
		ListaDIDType sapLav = null;

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
		String invioSapEP = eps.getUrl(END_POINT_NAME_RICHIESTASAP_N00A02);

		it.gov.lavoro.servizi.servizicoap.ServizicoapWSProxy proxy = new it.gov.lavoro.servizi.servizicoap.ServizicoapWSProxy();
		proxy.setEndpoint(invioSapEP);

		String xmlSAP = proxy.richiestaSAP_N00_A02(codFiscaleLav);

		if (xmlSAP != null && !("").equalsIgnoreCase(xmlSAP)) {
			sapLav = convertToLavoratoreSAPN00A02(xmlSAP);
		}

		_logger.debug("FINE CHIAMATA RICHIESTA SAP, codice fiscale =" + codFiscaleLav);
		return sapLav;
	}

	@SuppressWarnings("unchecked")
	private LavoratoreType convertToLavoratoreSAP(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		LavoratoreType sap = null;
		jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<LavoratoreType> root = (JAXBElement<LavoratoreType>) jaxbUnmarshaller
				.unmarshal(new StringReader(xmlSAP));
		sap = root.getValue();
		return sap;
	}

	private String convertRichiestaSapToString(IDSAP richiestaSAP) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(IDSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(richiestaSAP, writer);
		String xmlRichiestaSAP = writer.getBuffer().toString();
		return xmlRichiestaSAP;
	}

	@SuppressWarnings("unchecked")
	private ListaDIDType convertToLavoratoreSAPN00A02(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		ListaDIDType sap = null;
		jaxbContext = JAXBContext.newInstance(it.gov.lavoro.servizi.servizicoap.richiestasap.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<ListaDIDType> root = (JAXBElement<ListaDIDType>) jaxbUnmarshaller
				.unmarshal(new StringReader(xmlSAP));
		sap = root.getValue();
		return sap;
	}

}
