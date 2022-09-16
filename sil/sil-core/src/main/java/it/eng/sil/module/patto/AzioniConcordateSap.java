package it.eng.sil.module.patto;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.SmartScrollableDataResult;
import it.eng.afExt.dispatching.module.impl.ListModule;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.patto.politicheattive.LavoratorePoliticheType;
import it.eng.sil.module.patto.politicheattive.LavoratorePoliticheType.PoliticheAttiveLst;
import it.eng.sil.module.patto.politicheattive.PoliticheAttive;
import oracle.jdbc.OracleTypes;

public class AzioniConcordateSap extends ListModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AzioniConcordateSap.class.getName());

	private String className = this.getClass().getName();

	public AzioniConcordateSap() {
	}

	public void service(SourceBean request, SourceBean response) {
		int rowsNumber = 0;
		int totalPages = 0;
		DataConnection dc = null;
		DataConnectionManager dcm = null;
		SmartScrollableDataResult smartDataResult = null;
		Map<String, String> listaCPIMin = new HashMap<String, String>();

		int pagedRows = Integer.parseInt((String) getConfig().getAttribute("ROWS"));
		if (pagedRows < 0) {
			pagedRows = Integer.MAX_VALUE;
		}

		String pool = (String) getConfig().getAttribute("POOL");
		String strPageNumber = "";
		String cdnLavoratore = request.getAttribute("CDNLAVORATORE").toString();
		try {

			if (!getServiceRequest().containsAttribute("LIST_PAGE")) {
				strPageNumber = "1";
			} else {
				strPageNumber = (String) getServiceRequest().getAttribute("LIST_PAGE");
			}
			String strMessage = (String) getServiceRequest().getAttribute("MESSAGE");

			int pageNumber = 1;

			if (strPageNumber != null) {
				pageNumber = new Integer(strPageNumber).intValue();
			} else {
				if ((strMessage != null) && (!strMessage.equalsIgnoreCase("LIST_FIRST"))) {
					pageNumber = -1;
				}
			}

			dcm = DataConnectionManager.getInstance();
			if (dcm == null) {
				_logger.error(className + "::execute: dcm null");
				throw new Exception("Errore nel recupero della connessione");
			}
			dc = dcm.getConnection(pool);

			if (dc == null) {
				_logger.error(className + "::service: dc null");
				throw new Exception("Errore nel recupero della connessione");
			}

			Connection connection = dc.getInternalConnection();

			CallableStatement stmtCreaSap = connection.prepareCall("{? = call pg_sap.getXMLPoliticheAttiveSAP(?, ?) }");

			stmtCreaSap.registerOutParameter(1, OracleTypes.CLOB);
			stmtCreaSap.setString(2, cdnLavoratore);
			stmtCreaSap.registerOutParameter(3, OracleTypes.VARCHAR);
			stmtCreaSap.execute();

			String xml = (String) stmtCreaSap.getString(1);
			String codErrore = (String) stmtCreaSap.getString(3);

			if (xml != null && !xml.equals("")) {
				if (("00").equalsIgnoreCase(codErrore)) {
					// BigDecimal sap2 =
					// getResponseContainer().getServiceResponse().containsAttribute("M_CheckSap2.ROWS.ROW.datediff")?
					// (BigDecimal)
					// getResponseContainer().getServiceResponse().getAttribute("M_CheckSap2.ROWS.ROW.datediff"):null;
					//

					SourceBean risposta = new SourceBean("ROWS");

					LavoratorePoliticheType xmlSapPolitiche = convertToLavoratoreSAP(xml);

					List<PoliticheAttive> listaPolAttive = null;

					// if(sap2!=null && sap2.intValue()>=0){
					PoliticheAttiveLst listPol = xmlSapPolitiche.getPoliticheAttiveLst();
					if (listPol != null) {
						listaPolAttive = listPol.getPoliticheAttive();
					}

					// }else{
					// listaPolAttive = xmlSapPolitiche.getPoliticheAttive();
					// }

					if (listaPolAttive != null && listaPolAttive.size() > 0) {
						// ordinamento per data proposta decrescente, tipo attivita crescente
						try {
							ordinamentoPolitche(listaPolAttive);
						} catch (Exception ex) {
							_logger.error(className + "ordinamento politioche attive ::service: " + ex.getMessage());
						}
						for (PoliticheAttive politicheAttiveMin : listaPolAttive) {
							String datColloquio = "";
							String datInizio = "";
							String datFine = "";
							String datStimata = "";
							if (politicheAttiveMin.getDataProposta() != null) {
								datColloquio = DateUtils.formatXMLGregorian(politicheAttiveMin.getDataProposta());
							}
							if (politicheAttiveMin.getData() != null) {
								datInizio = DateUtils.formatXMLGregorian(politicheAttiveMin.getData());
							}
							if (politicheAttiveMin.getDataFine() != null) {
								datFine = DateUtils.formatXMLGregorian(politicheAttiveMin.getDataFine());
							}
							if (politicheAttiveMin.getDataStimata() != null) {
								datStimata = DateUtils.formatXMLGregorian(politicheAttiveMin.getDataStimata());
							}
							String descrizioneAz = politicheAttiveMin.getDescrizioneAzioneSil();
							String esitoAz = politicheAttiveMin.getEsitoAzioneSil();
							String attivita = politicheAttiveMin.getTipoAttivita() + " - "
									+ politicheAttiveMin.getTitoloDenominazione();
							String progetto = politicheAttiveMin.getTitoloProgetto() + " - "
									+ politicheAttiveMin.getDescrizione();
							String entePromotore = politicheAttiveMin.getCodiceEntePromotore();
							if (entePromotore != null && !entePromotore.equals("")) {
								String descrizioneEnte = "";
								if (listaCPIMin.isEmpty() || !listaCPIMin.containsKey(entePromotore)) {
									Object[] params = new Object[] { entePromotore };
									SourceBean rowEnte = (SourceBean) QueryExecutor.executeQuery(
											"SELECT_ENTETIT_BY_KEY", params, "SELECT", Values.DB_SIL_DATI);
									if (rowEnte != null) {
										rowEnte = rowEnte.containsAttribute("ROW")
												? (SourceBean) rowEnte.getAttribute("ROW")
												: rowEnte;
										descrizioneEnte = StringUtils.getAttributeStrNotNull(rowEnte, "DESCRIZIONE");
										listaCPIMin.put(entePromotore, descrizioneEnte);
									}
								} else {
									descrizioneEnte = listaCPIMin.get(entePromotore);
								}
								entePromotore = entePromotore + " - " + descrizioneEnte;
							}
							SourceBean elemento = new SourceBean("ROW");
							elemento.setAttribute("CODDESCATTIVITA", attivita);
							elemento.setAttribute("CODDESCPROGETTO", progetto);
							elemento.setAttribute("CODCPIMIN", entePromotore);

							if (!datColloquio.equals("")) {
								elemento.setAttribute("DATCOLLOQUIO", datColloquio);
								elemento.setAttribute("DATPROPOSTA", datColloquio);
							}
							if (descrizioneAz != null) {
								elemento.setAttribute("DESCAZIONE", descrizioneAz);
							}
							if (!datStimata.equals("")) {
								elemento.setAttribute("DATSTIMATA", datStimata);
							}
							if (esitoAz != null) {
								elemento.setAttribute("DESCESITO", esitoAz);
							}
							if (!datInizio.equals("")) {
								elemento.setAttribute("DATINIZIO", datInizio);
							}
							if (!datFine.equals("")) {
								elemento.setAttribute("DATFINE", datFine);
							}
							risposta.setAttribute(elemento);
						}
					}
					response.setAttribute(risposta);
				}
			}
		} catch (Exception e) {
			_logger.error(className + "::service: " + e.getMessage());
		} finally {
			Utils.releaseResources(dc, null, null);
		}
	}

	private LavoratorePoliticheType convertToLavoratoreSAP(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		LavoratorePoliticheType sap = null;

		jaxbContext = JAXBContext.newInstance(it.eng.sil.module.patto.politicheattive.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<LavoratorePoliticheType> root = (JAXBElement<LavoratorePoliticheType>) jaxbUnmarshaller
				.unmarshal(new StringReader(xmlSAP));
		sap = root.getValue();

		return sap;
	}

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

}
