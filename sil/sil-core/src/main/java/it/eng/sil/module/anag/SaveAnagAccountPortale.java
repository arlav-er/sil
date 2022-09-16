package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class SaveAnagAccountPortale extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SaveAnagAccountPortale.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();
			response.setAttribute("TITOLO", "Risultato aggiornamento dati sul SIL");
			boolean ret = doUpdate(request, response);
			if (!ret) {
				transExec.rollBackTransaction();
				response.setAttribute("operationResult", "ERROR");
				response.setAttribute("RISULTATO", "Errore aggiornamento dati.");
			} else {
				String codCittadinanza = StringUtils.getAttributeStrNotNull(request, "CITTADINANZA_PORT");
				String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
				BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
						.getAttribute("_CDUT_");
				Boolean updatePerm = new Boolean(false);

				if (!codCittadinanza.equals("")) {
					Object params[] = new Object[1];
					boolean cittadinanzaCEE = false;
					params[0] = codCittadinanza;
					SourceBean sb = (SourceBean) transExec.executeQuery("GET_FLAG_CITTADINANZA_CEE", params, "SELECT");
					cittadinanzaCEE = (sb != null && sb.containsAttribute("ROW.FLGCEE")
							&& sb.getAttribute("ROW.FLGCEE").equals("S"));

					if (cittadinanzaCEE) {
						params[0] = cdnLavoratore;
						SourceBean sb2 = (SourceBean) transExec.executeQuery("GET_PERM_SOGG_LAV", params, "SELECT");
						String prgPermSog = sb2.containsAttribute("ROW.prgPermSogg")
								? sb2.getAttribute("ROW.prgPermSogg").toString()
								: "";
						BigDecimal numkloPermSogg = sb2.containsAttribute("ROW.numKloPermSogg")
								? (BigDecimal) sb2.getAttribute("ROW.numKloPermSogg")
								: null;

						if ((prgPermSog != null) && !prgPermSog.equals("")) {
							numkloPermSogg = numkloPermSogg.add(new BigDecimal(1));
							params = new Object[4];
							params[0] = DateUtils.getNow();
							params[1] = numkloPermSogg;
							params[2] = user;
							params[3] = new BigDecimal(prgPermSog);
							updatePerm = (Boolean) transExec.executeQuery("UPDATE_AM_EX_PERM_CONCORRENZA", params,
									"UPDATE");

							if (!updatePerm.booleanValue()) {
								throw new Exception("Errore nell'aggiornamento");
							}
						}
					} else {
						response.setAttribute("CITTADINANZAEXTRACEE", "S");
					}
				}
				transExec.commitTransaction();
				response.setAttribute("operationResult", "SUCCESS");
				response.setAttribute("RISULTATO", "Dati aggiornati correttamente.");
			}
		} catch (Exception ex) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			response.setAttribute("operationResult", "ERROR");
			response.setAttribute("RISULTATO", "Errore generico.");
			_logger.error("Errore aggiornamento dati anagrafici", ex);
		}
	}
}