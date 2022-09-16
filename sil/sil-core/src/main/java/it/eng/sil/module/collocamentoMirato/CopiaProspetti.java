package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;
import it.eng.sil.module.movimenti.HtmlResultLogger;
import it.eng.sil.module.movimenti.MultipleResultLogger;
import it.eng.sil.module.movimenti.ResultLogFormatter;
import it.eng.sil.module.movimenti.ResultLogger;

public class CopiaProspetti extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CopiaProspetti.class.getName());
	String pool = null;

	public CopiaProspetti() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessione = getRequestContainer().getSessionContainer();
		// Azione specificata dall'utente
		String azione = StringUtils.getAttributeStrNotNull(request, "azioneScelta");
		String numAnnoXCopia = StringUtils.getAttributeStrNotNull(request, "annoNew");
		String numAnnoVerifica = StringUtils.getAttributeStrNotNull(request, "annoOld");
		String codMonoCategoria = StringUtils.getAttributeStrNotNull(request, "codMonoCategoria");

		int numMaxProspCopia = 0;
		ArrayList<BigDecimal> prgProspettiArray = null;
		if (sessione.getAttribute("COPIAPROSPETTOCORRENTE") == null) {
			if (azione.equalsIgnoreCase("validaSelezionati") || azione.equalsIgnoreCase("validaTutti")) {
				numMaxProspCopia = ProspettiConstant.NUM_MAX_COPIA_IN_CORSO;

				sessione.delAttribute("PROGRESSIVOULTIMACOPIAPROSP");
				sessione.delAttribute("RISULTATI_ULTIMA_COPIA_PROSP");

				prgProspettiArray = new ArrayList<BigDecimal>();
				String strListaProspDaCopiare = request.containsAttribute("prgListaProspDaCopiare")
						? request.getAttribute("prgListaProspDaCopiare").toString()
						: "";
				if (!strListaProspDaCopiare.equals("") && azione.equalsIgnoreCase("validaSelezionati")) {
					Vector vettPrg = StringUtils.split(strListaProspDaCopiare, "#");
					for (int i = 0; i < vettPrg.size(); i++)
						prgProspettiArray.add(new BigDecimal(vettPrg.get(i).toString()));
				} else {
					if (azione.equalsIgnoreCase("validaTutti")) {
						int i = 0;
						SourceBean sbProspFiltrato = null;
						Vector prospettiFiltrati = getServiceResponse()
								.getAttributeAsVector("M_ProspDaCopiareFiltrati.ROWS.ROW");
						int nProspFiltrati = prospettiFiltrati.size();
						if (nProspFiltrati > numMaxProspCopia) {
							nProspFiltrati = numMaxProspCopia;
						}
						for (; i < nProspFiltrati; i++) {
							sbProspFiltrato = (SourceBean) prospettiFiltrati.get(i);
							prgProspettiArray.add((BigDecimal) sbProspFiltrato.getAttribute("PRGPROSPETTOINF"));
						}
					}
				}

				// Oggetto per la copia prospetti
				ValidatorCopia validator = new ValidatorCopia();

				// creo l'oggetto che esegue il log dei risultati
				ResultLogger resultLogger = null;
				BigDecimal prgValidazioneMassiva = null;
				try {
					resultLogger = new HtmlResultLogger();
					MultipleResultLogger dbLogger = new MultipleResultLogger(prgProspettiArray.size(), sessione,
							numAnnoXCopia, numAnnoVerifica, codMonoCategoria);

					prgValidazioneMassiva = dbLogger.getPrgValidazioneMassiva();
					validator.setPrgValidazione(prgValidazioneMassiva);
					resultLogger.addChildResultLogger(dbLogger); // (x)->Html->DB
				} catch (Exception e) {
					// errore scrittura log
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Impossibile inizalizzare il logger per registrare i risultati", e);
					return;
				}
				// Creo l'oggetto per il recupero dei risultati
				ResultLogFormatter risultatiCorrenti = null;
				try {
					risultatiCorrenti = new ResultLogFormatter(prgValidazioneMassiva);
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Impossibile inizalizzare il logger per il recupero dei risultati", e);
					return;
				}
				Thread t = new Thread(new BackGroundCopiaProsp(RequestContainer.getRequestContainer(), validator,
						resultLogger, prgProspettiArray, numAnnoXCopia), "VALIDATOREMASSIVOCOPIAPROSPETTO");

				sessione.setAttribute("PROCESSOCOPIAPROSPETTOCORRENTE", t);

				sessione.setAttribute("PROGRESSIVOULTIMACOPIAPROSP", prgValidazioneMassiva);
				sessione.setAttribute("RISULTATI_ULTIMA_COPIA_PROSP", risultatiCorrenti);

				sessione.setAttribute("COPIAPROSPETTOCORRENTE", validator);

				// Avvio la copia prospetti
				t.start();
			}
		}
	}

}