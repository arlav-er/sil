/**
 * 
 */
package it.eng.sil.module.voucher;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.blen.StringUtils;

/**
 * @author Fatale
 *
 */
public class VisualizzPaginaTotaliVoucher extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7326731161956283235L;

	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(VisualizzPaginaTotaliVoucher.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro la classe VisualizzPaginaTotaliVoucher");

		DataConnection dc = null;
		DataConnectionManager dcm = null;
		SQLCommand cmdSelect = null;
		DataResult dr = null;

		try {
			RequestContainer reqCont = getRequestContainer();
			ResponseContainer resCont = getResponseContainer();

			String pool = (String) getConfig().getAttribute("POOL");
			String statement = SQLStatements.getStatement("GET_LISTA_SUB_TOTALI_VOUCHER");
			StringBuffer buf = new StringBuffer(statement);

			SourceBean req = reqCont.getServiceRequest();

			SessionContainer session = reqCont.getSessionContainer();
			User user = (User) session.getAttribute(User.USERID);
			String tipoGruppoCollegato = user.getCodTipo();

			String nome = SourceBeanUtils.getAttrStrNotNull(req, "nomeSel");
			String cognome = SourceBeanUtils.getAttrStrNotNull(req, "cognomeSel");
			String cf = SourceBeanUtils.getAttrStrNotNull(req, "cf");
			String tipoRic = SourceBeanUtils.getAttrStrNotNull(req, "tipoRicerca");
			String statoVch = SourceBeanUtils.getAttrStrNotNull(req, "PRGSTATO");
			String azione = SourceBeanUtils.getAttrStrNotNull(req, "PRGAZIONE");

			// String statoText = SourceBeanUtils.getAttrStrNotNull(req, "statoTDAtext");
			// String azioneText = SourceBeanUtils.getAttrStrNotNull(req, "azioneTDAtext");

			String codCpi = SourceBeanUtils.getAttrStrNotNull(req, "codCPI");
			String cfEnte = SourceBeanUtils.getAttrStrNotNull(req, "cfEnte");
			String sedeEnte = SourceBeanUtils.getAttrStrNotNull(req, "cfSedeEnte");

			// Modifica 03/10/2016 aggiunti nuovi parametri di nella ricerca
			String codiceAttivazione = SourceBeanUtils.getAttrStrNotNull(req, "strCodAttivazione");
			String dataStatoDal = SourceBeanUtils.getAttrStrNotNull(req, "dataStatoDa");
			String dataStatoAl = SourceBeanUtils.getAttrStrNotNull(req, "dataStatoA");
			String motivoAnull = SourceBeanUtils.getAttrStrNotNull(req, "codAnnullTDA");
			String statoPagamento = SourceBeanUtils.getAttrStrNotNull(req, "statoPagamentoTDA");
			String assegnatiScaduti = SourceBeanUtils.getAttrStrNotNull(req, "assegnatiScaduti");
			String attivatiScaduti = SourceBeanUtils.getAttrStrNotNull(req, "attivatiScaduti");

			if (tipoRic.equalsIgnoreCase("esatta")) {
				if ((nome != null) && (!nome.equals(""))) {
					nome = StringUtils.replace(nome, "'", "''");
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(lav.strnome) = '" + nome.toUpperCase() + "'");
				}

				if ((cognome != null) && (!cognome.equals(""))) {
					cognome = StringUtils.replace(cognome, "'", "''");
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(lav.strcognome) = '" + cognome.toUpperCase() + "'");
				}

				if ((cf != null) && (!cf.equals(""))) {
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(lav.strCodiceFiscale) = '" + cf.toUpperCase() + "'");
				}
			} else {
				if ((nome != null) && (!nome.equals(""))) {
					nome = StringUtils.replace(nome, "'", "''");
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(lav.strnome) like '" + nome.toUpperCase() + "%'");
				}

				if ((cognome != null) && (!cognome.equals(""))) {
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					cognome = StringUtils.replace(cognome, "'", "''");
					buf.append(" upper(lav.strcognome) like '" + cognome.toUpperCase() + "%'");
				}

				if ((cf != null) && (!cf.equals(""))) {
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(lav.strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
				}
			}

			// //Modifica 03/10/2016 aggiunti nuovi parametri di nella ricerca
			// 1. aggiunto il codice di attivazione
			if ((codiceAttivazione != null) && (!codiceAttivazione.equals(""))) {
				codiceAttivazione = StringUtils.replace(codiceAttivazione, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(vou.codattivazione) = '" + codiceAttivazione.toUpperCase() + "'");
			}

			// 2. modifica del campo stato con aggiunta delle date

			if ((statoVch != null) && (!statoVch.equals(""))) {

				String nomeCampoData = "";

				if (statoVch.equalsIgnoreCase("ANN")) {
					nomeCampoData = "DTMUTANN";
				} else if (statoVch.equalsIgnoreCase("ASS")) {
					nomeCampoData = "DTMUTASS";
				} else if (statoVch.equalsIgnoreCase("ATT")) {
					nomeCampoData = "DTMUTATT";
				} else if (statoVch.equalsIgnoreCase("CHI")) {
					nomeCampoData = "DTMUTCONC";
				}
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(VOU.CODSTATOVOUCHER) = '" + statoVch.toUpperCase() + "'");

				if ((dataStatoDal != null) && (!dataStatoDal.equals(""))) {
					buf.append(" AND ");
					buf.append(" TRUNC(VOU." + nomeCampoData + ") >= to_date('" + dataStatoDal + "', 'dd/mm/yyyy') ");
				}
				if ((dataStatoAl != null) && (!dataStatoAl.equals(""))) {
					buf.append(" AND ");
					buf.append(" TRUNC(VOU." + nomeCampoData + ") <= to_date('" + dataStatoAl + "', 'dd/mm/yyyy') ");
				}
			}

			// 3. modifica del campo stato pagamento
			if ((statoPagamento != null) && (!statoPagamento.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" VOU.CODVCHSTATOPAGAMENTO = '" + statoPagamento + "'");
			}

			// 4. modifica del campo Motivo annullamento
			if ((motivoAnull != null) && (!motivoAnull.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" VOU.CODVCHMOTIVOANNULLAMENTO = '" + motivoAnull + "'");
			}

			// 5. modifica del campo Assegnati scaduti AND (vou.codstatovoucher ='ASS' AND trunc(vou.datMaxattivazione)
			// < trunc(sysdate))
			if ((assegnatiScaduti != null) && (!assegnatiScaduti.equals(""))
					&& (assegnatiScaduti.equalsIgnoreCase("on"))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}

				buf.append(" upper(VOU.CODSTATOVOUCHER) = 'ASS'");
				buf.append(" AND TRUNC(VOU.DATMAXATTIVAZIONE) < TRUNC(SYSDATE)");
			}

			// 6. modifica del campo Attivati Scaduti AND (vou.codstatovoucher ='ATT' AND trunc(vou.datMAxErogazione) <
			// trunc(sysdate))

			if ((attivatiScaduti != null) && (!attivatiScaduti.equals(""))
					&& (attivatiScaduti.equalsIgnoreCase("on"))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}

				buf.append(" upper(VOU.CODSTATOVOUCHER) = 'ATT'");
				buf.append(" AND TRUNC(VOU.DATMAXEROGAZIONE) < TRUNC(SYSDATE)");
			}

			if ((azione != null) && (!azione.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" per.prgazioni = " + azione);
			}

			// La parte se ente o sede
			if (!sedeEnte.equals("")) {

				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(Vou.codsede) = '" + sedeEnte.toUpperCase() + "'");
			} else {
				if (!cfEnte.equals("")) {
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(Vou.strcfenteaccreditato) = '" + cfEnte.toUpperCase() + "'");
				}
			}

			// Codice cpi
			if ((codCpi != null) && (!codCpi.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" cl.CODCPI = '" + codCpi + "'");
			}

			String statementFinale = buf.toString();

			dcm = DataConnectionManager.getInstance();
			dc = dcm.getConnection(pool);
			cmdSelect = dc.createSelectCommand(statementFinale);

			// Messaggio di Debug
			_logger.debug(className + "::DynamicQueryGenerica: eseguo query " + statementFinale);

			// esegue la query
			dr = cmdSelect.execute();
			// Recupera il risultato della query
			ScrollableDataResult sdr = null;
			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}
			// Recupero il bean di risposta
			SourceBean rowsSourceBean = sdr.getSourceBean();
			// Messaggio di Debug
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

			BigDecimal conteggio = new BigDecimal(0);
			BigDecimal TOT_EURO_ATTESA = new BigDecimal(0);
			BigDecimal TOT_EURO_PAGATI = new BigDecimal(0);

			if (rowsSourceBean != null) {

				conteggio = (BigDecimal) rowsSourceBean.getAttribute("ROW.CONTEGGIO");
				TOT_EURO_ATTESA = (BigDecimal) rowsSourceBean.getAttribute("ROW.TOT_EURO_ATTESA");
				TOT_EURO_PAGATI = (BigDecimal) rowsSourceBean.getAttribute("ROW.TOT_EURO_PAGATI");
				sdr = null;
			}
			if (conteggio != null) {
				serviceResponse.setAttribute("conteggioTot", conteggio);
			}
			if (TOT_EURO_ATTESA != null) {
				serviceResponse.setAttribute("inAttesaTot", TOT_EURO_ATTESA);
			}
			if (TOT_EURO_PAGATI != null) {
				serviceResponse.setAttribute("pagatoEuroTot", TOT_EURO_PAGATI);
			}

			serviceResponse.setAttribute("codiceFiscaleSel", cf);
			serviceResponse.setAttribute("cognomeSel", cognome);
			serviceResponse.setAttribute("nomeSel", nome);

			// Modifica aggiunta 03/10/2016
			serviceResponse.setAttribute("codiceAttivazioneSel", codiceAttivazione);
			serviceResponse.setAttribute("statoVchSel", statoVch);
			// serviceResponse.setAttribute("statoPagamentoSel",statoPagamento);
			// serviceResponse.setAttribute("motivoAnullSel",motivoAnull);
			serviceResponse.setAttribute("assegnatiScadutiSel", assegnatiScaduti);
			serviceResponse.setAttribute("attivatiScadutiSel", attivatiScaduti);
			serviceResponse.setAttribute("codCpiSel", codCpi);

			serviceResponse.setAttribute("descrCodCpiSel", SourceBeanUtils.getAttrStrNotNull(req, "descrCodCpiSel"));
			serviceResponse.setAttribute("descrStato", SourceBeanUtils.getAttrStrNotNull(req, "descrStato"));
			serviceResponse.setAttribute("descrAzione", SourceBeanUtils.getAttrStrNotNull(req, "descrAzione"));
			serviceResponse.setAttribute("descrPagamento", SourceBeanUtils.getAttrStrNotNull(req, "descrPagamento"));
			serviceResponse.setAttribute("descrAnnull", SourceBeanUtils.getAttrStrNotNull(req, "descrAnnull"));
			serviceResponse.setAttribute("descrSedeSA", SourceBeanUtils.getAttrStrNotNull(req, "descrSedeSA"));

			if ((statoVch != null) && (!statoVch.equals(""))) {
				String statoText = "";
				String sqlStato = " select de_vch_stato.strdescrizione from de_vch_stato where de_vch_stato.codstatovoucher ='"
						+ statoVch + "'";
				dcm = DataConnectionManager.getInstance();
				dc = dcm.getConnection(pool);
				cmdSelect = dc.createSelectCommand(sqlStato);

				dr = cmdSelect.execute();
				// Recupera il risultato della query
				ScrollableDataResult sdr2 = null;
				if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
					sdr2 = (ScrollableDataResult) dr.getDataObject();
				}
				// Recupero il bean di risposta
				SourceBean rowsSourceBeanStato = sdr2.getSourceBean();
				// Messaggio di Debug
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean",
						rowsSourceBeanStato);

				if (rowsSourceBeanStato != null) {
					statoText = rowsSourceBeanStato.getAttribute("ROW.STRDESCRIZIONE").toString();
					serviceResponse.setAttribute("statoSel", statoText);
					sdr2 = null;
				}

			}
			if ((azione != null) && (!azione.equals(""))) {
				String azioneText = "";
				String sqlAzione = "select de_azione.strDescrizione from de_azione  inner join vch_modello_voucher on (de_azione.prgazioni = vch_modello_voucher.prgazioni) ";
				sqlAzione += "  where vch_modello_voucher.flgattivo = 'S' and de_azione.prgAzioni=" + azione;

				dcm = DataConnectionManager.getInstance();
				dc = dcm.getConnection(pool);
				cmdSelect = dc.createSelectCommand(sqlAzione);

				dr = cmdSelect.execute();
				// Recupera il risultato della query
				ScrollableDataResult sdr3 = null;
				if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
					sdr3 = (ScrollableDataResult) dr.getDataObject();
				}
				// Recupero il bean di risposta
				SourceBean rowsSourceBeanAzione = sdr3.getSourceBean();
				// Messaggio di Debug
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean",
						rowsSourceBeanAzione);

				if (rowsSourceBeanAzione != null) {
					azioneText = rowsSourceBeanAzione.getAttribute("ROW.STRDESCRIZIONE").toString();
					serviceResponse.setAttribute("azioneSel", azioneText);
					sdr3 = null;
				}

			}

			// Recupero la descrizione per la visualizzazione del criterio
			if ((statoPagamento != null) && (!statoPagamento.equals(""))) {
				String statoPagQuery = " select strdescrizione from de_vch_stato_pagamento where codvchstatopagamento='"
						+ statoPagamento + "'";
				String descrPagato = "";
				dcm = DataConnectionManager.getInstance();
				dc = dcm.getConnection(pool);
				cmdSelect = dc.createSelectCommand(statoPagQuery);

				dr = cmdSelect.execute();
				// Recupera il risultato della query
				ScrollableDataResult sdr4 = null;
				if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
					sdr4 = (ScrollableDataResult) dr.getDataObject();
				}
				// Recupero il bean di risposta
				SourceBean rowsSourceBeanPagato = sdr4.getSourceBean();
				// Messaggio di Debug
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBeanPagato",
						rowsSourceBeanPagato);

				if (rowsSourceBeanPagato != null) {
					descrPagato = rowsSourceBeanPagato.getAttribute("ROW.STRDESCRIZIONE").toString();
					serviceResponse.setAttribute("statoPagamentoSel", descrPagato);
					sdr4 = null;
				}

			}
			// Modifica aggiunta
			if ((motivoAnull != null) && (!motivoAnull.equals(""))) {
				String motivAnnSql = " select strdescrizione from de_vch_motivo_annullamento where CODVCHMOTIVOANNULLAMENTO ='"
						+ motivoAnull + "'";
				String descrMotivoAnn = "";
				dcm = DataConnectionManager.getInstance();
				dc = dcm.getConnection(pool);
				cmdSelect = dc.createSelectCommand(motivAnnSql);

				dr = cmdSelect.execute();
				// Recupera il risultato della query
				ScrollableDataResult sdr5 = null;
				if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
					sdr5 = (ScrollableDataResult) dr.getDataObject();
				}
				// Recupero il bean di risposta
				SourceBean rowsSourceBeanmotivoAnn = sdr5.getSourceBean();
				// Messaggio di Debug
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBeanPagato",
						rowsSourceBeanmotivoAnn);

				if (rowsSourceBeanmotivoAnn != null) {
					descrMotivoAnn = rowsSourceBeanmotivoAnn.getAttribute("ROW.STRDESCRIZIONE").toString();
					serviceResponse.setAttribute("motivoAnullSel", descrMotivoAnn);
					sdr5 = null;
				}
			}
			// Modifica paramtri
			if (tipoGruppoCollegato != null && tipoGruppoCollegato.equalsIgnoreCase(Properties.SOGGETTO_ACCREDITATO)) {
				if (!sedeEnte.equals("")) {
					if (cfEnte != null && !cfEnte.equals("")) {
						String descrEnteSql = "select strdenominazione from an_vch_ente  where  strcodicefiscale='"
								+ cfEnte + "'" + " and codsede='" + sedeEnte + "'";
						String descrEnte = "";
						dcm = DataConnectionManager.getInstance();
						dc = dcm.getConnection(pool);
						cmdSelect = dc.createSelectCommand(descrEnteSql);

						dr = cmdSelect.execute();
						// Recupera il risultato della query
						ScrollableDataResult sdr6 = null;
						if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
							sdr6 = (ScrollableDataResult) dr.getDataObject();
						}
						// Recupero il bean di risposta
						SourceBean rowsSourceBeanSede = sdr6.getSourceBean();
						// Messaggio di Debug
						it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBeanPagato",
								rowsSourceBeanSede);

						if (rowsSourceBeanSede != null) {
							descrEnte = rowsSourceBeanSede.getAttribute("ROW.strdenominazione").toString();
							serviceResponse.setAttribute("sedeEnteSel", descrEnte);
							sdr6 = null;
						}

					}

				}
			}

			serviceResponse.setAttribute((SourceBean) rowsSourceBean);

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowsSourceBean)", ex);

		} finally {
			Utils.releaseResources(dc, cmdSelect, dr);
		}
	}

}
