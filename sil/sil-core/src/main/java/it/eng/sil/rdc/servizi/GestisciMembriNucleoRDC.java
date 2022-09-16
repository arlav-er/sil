package it.eng.sil.rdc.servizi;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;

public class GestisciMembriNucleoRDC extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8762785069662233899L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GestisciMembriNucleoRDC.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		MultipleTransactionQueryExecutor tex = null;
		BigDecimal prgAmRdc = null;
		BigDecimal prgAmRdcStorico = null;
		RDCBean supportRdc = new RDCBean();
		boolean checkLav = true;
		boolean risposta = false;
		String codiceCPI = null;
		String codiceCPIMin = null;
		int index = SourceBeanUtils.getAttrInt(serviceRequest, "indiceSelezionato");
		try {
			tex = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);

			codiceCPI = supportRdc.getCpiMembroNucleo(serviceRequest, index, tex);

			codiceCPIMin = supportRdc.getCpiMinisterialeMembroNucleo(codiceCPI, tex);

			SourceBean notificaDB = supportRdc.getBeanNotificaRdcPerStorico(
					supportRdc.getAttribute(serviceRequest, "codiceFiscale", index),
					supportRdc.getAttribute(serviceRequest, "protInps", index),
					supportRdc.getAttribute(serviceRequest, "codRuolo", index));

			boolean insertAmRdc = true;

			if ((notificaDB != null) && notificaDB.containsAttribute("ROW")) {
				// datainvio
				Date dataInvioNotifica = new SimpleDateFormat("dd/MM/yyyy")
						.parse(supportRdc.getAttribute(serviceRequest, "dataDomanda", index));
				prgAmRdc = (BigDecimal) notificaDB.getAttribute("ROW.PRGRDC");
				BigDecimal numKlo = (BigDecimal) notificaDB.getAttribute("ROW.NUMKLORDC");
				BigDecimal cdnLav = (BigDecimal) notificaDB.getAttribute("ROW.CDNLAVORATORE");
				Date dataInvioRecordDB = (Date) notificaDB.getAttribute("ROW.DATINVIO");
				if (dataInvioNotifica.compareTo(dataInvioRecordDB) > 0) {
					insertAmRdc = false;
					prgAmRdcStorico = DBKeyGenerator.getNextSequence(tex.getDataConnection(), "S_AM_RDC_STORICO");
					risposta = supportRdc.insericiAmRdcStoricoAggiornaAmdRdc(prgAmRdcStorico, prgAmRdc, cdnLav, numKlo,
							codiceCPIMin, index, serviceRequest, tex);
					if (risposta) {
						reportOperation.reportSuccess(MessageCodes.RDC.NUCLEOFAM_RDC_NOTIFICA_UPD);
					}
				} else {
					insertAmRdc = false;
					checkLav = false;
					reportOperation.reportSuccess(MessageCodes.RDC.NUCLEOFAM_RDC_NOTIFICA_PRES);
				}
			}

			if (insertAmRdc) {
				prgAmRdc = DBKeyGenerator.getNextSequence(tex.getDataConnection(), "S_AM_RDC");
				risposta = supportRdc.insericiAmRdc(prgAmRdc, codiceCPIMin, index, serviceRequest, tex);
				if (risposta) {
					reportOperation.reportSuccess(MessageCodes.RDC.NUCLEOFAM_RDC_NOTIFICA_AGG);
				} else {
					reportOperation.reportFailure(00000);
				}

			}

		} catch (Exception e) {
			if (tex != null) {
				_logger.error(e.getMessage(), e);
			}

		}

		try {
			if (checkLav) {
				// vedo se il lavoratore e' nella tabella am_rdc
				SourceBean sbNumKlo = (SourceBean) QueryExecutor.executeQuery("NotificaRDC_NumKlo",
						new Object[] { prgAmRdc }, "SELECT", Values.DB_SIL_DATI);
				SourceBean sbNumKloRdc = sbNumKlo.containsAttribute("ROW") ? (SourceBean) sbNumKlo.getAttribute("ROW")
						: sbNumKlo;
				BigDecimal numKlo = (BigDecimal) sbNumKloRdc.getAttribute("NUMKLORDC");
				if (sbNumKloRdc.getAttribute("CDNLAVORATORE") == null) {
					// cerco il lavoratore
					BigDecimal cdnLav = null;
					boolean canUpdateCdnLav = false;
					SourceBean spLavoratoreSB = supportRdc
							.getBeanLavoratore(supportRdc.getAttribute(serviceRequest, "codiceFiscale", index));
					if (spLavoratoreSB != null && spLavoratoreSB.containsAttribute("ROW")) {
						// lavoratore trovato
						SourceBean sbCdnLav = spLavoratoreSB.containsAttribute("ROW")
								? (SourceBean) spLavoratoreSB.getAttribute("ROW")
								: spLavoratoreSB;
						cdnLav = (BigDecimal) sbCdnLav.getAttribute("CDNLAVORATORE");
					}
					if (cdnLav == null) {
						// lavoratore non trovato, lo censisco
						cdnLav = DBKeyGenerator.getNextSequence(tex.getDataConnection(), "S_AN_LAVORATORE");
						boolean rispostaInsLav = supportRdc.inserisciLavoratore(serviceRequest, codiceCPI, index,
								cdnLav, tex);

						if (rispostaInsLav) {
							reportOperation.reportSuccess(MessageCodes.RDC.NUCLEOFAM_RDC_AN_AGG);
							canUpdateCdnLav = true;
						} else {
							reportOperation.reportFailure(MessageCodes.RDC.NUCLEOFAM_RDC_AN_ERR);
						}
					} else {
						reportOperation.reportSuccess(MessageCodes.RDC.NUCLEOFAM_RDC_AN_PRES);
						canUpdateCdnLav = true;
					}
					if (canUpdateCdnLav) {
						// aggiorno la riga con cdnlavoratore
						supportRdc.updateAmRdc(cdnLav, numKlo, prgAmRdc, tex);
						reportOperation.reportSuccess(MessageCodes.RDC.NUCLEOFAM_RDC_NOTIFICA_UPD);
					}
				}

			}

		} catch (Exception e) {
			_logger.error("errore ", e);

		} finally {
			if (tex != null) {
				tex.closeConnection();
			}
		}

	}

}
