package it.eng.sil.module.ido.art16OnLine;

import java.math.BigDecimal;

import javax.xml.bind.JAXBException;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.agenda.appuntamento.Constants;
import it.eng.sil.coop.webservices.agenda.appuntamento.EsitoInserimentoLavoratore;
import it.eng.sil.coop.webservices.art16online.istanze.CandidaturaType;
import it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;

public class EseguiAzioniCandidatura extends Thread {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EseguiAzioniCandidatura.class.getName());
	private BigDecimal prgIstanza = null;
	private ResponseIstanzaArt16 responseXsd = null;
	private CandidaturaType[] elencoCandidature = null;
	private BigDecimal cdnUtente = null;
	private BigDecimal cdnGruppo = null;
	private String prgRosa = null;
	private String prgRichiestaAz = null;
	private String codCpi = null;
	private MultipleTransactionQueryExecutor transExec = null;
	private SessionContainer session = null;

	public EseguiAzioniCandidatura(BigDecimal prgIstanza, ResponseIstanzaArt16 responseXsd,
			CandidaturaType[] elencoCandidature, BigDecimal cdnUtente, BigDecimal cndGruppo, String prgRosa,
			String prgRichiestaAz, String codCpi, SessionContainer session) {
		super();
		this.prgIstanza = prgIstanza;
		this.responseXsd = responseXsd;
		this.elencoCandidature = elencoCandidature;
		this.cdnUtente = cdnUtente;
		this.cdnGruppo = cndGruppo;
		this.prgRosa = prgRosa;
		this.prgRichiestaAz = prgRichiestaAz;
		this.codCpi = codCpi;
		this.session = session;
		this.transExec = null;
	}

	@Override
	public void run() {
		IstanzeBeanUtils istanzeUtils = new IstanzeBeanUtils();

		try {
			this.transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
			for (int i = 0; i < elencoCandidature.length; i++) {
				boolean skipStep = false;
				// gestione candidatura
				it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidatoXsd = responseXsd
						.getIstanzaArt16().getListaCandidature().getCandidatura().get(i);
				String esitoFinale = null;
				// Primo step
				transExec.initTransaction();
				EsitoInserimentoLavoratore esitoLavAnag = istanzeUtils.gestioneLavoratore(candidatoXsd, cdnUtente,
						cdnGruppo, codCpi, prgRosa, prgRichiestaAz, transExec);
				if (esitoLavAnag.getCodErrore().equalsIgnoreCase(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_OK)) {
					transExec.commitTransaction();
				} else {
					esitoFinale = esitoLavAnag.getCodErrore();
					transExec.rollBackTransaction();
					// Anagrafica non inseribile - errore bloccante
					if (esitoLavAnag.getCodErrore()
							.equalsIgnoreCase(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_INS_ANAG)) {
						skipStep = true;
					}
				}
				if (!skipStep) {
					// Secondo step - opzionale
					if (candidatoXsd.getISEE() != null) {
						transExec.initTransaction();
						EsitoInserimentoLavoratore esitoLavIsee = istanzeUtils.gestioneISEELavoratore(candidatoXsd,
								cdnUtente, prgRosa, prgRichiestaAz, transExec);
						if (esitoLavIsee.getCodErrore()
								.equalsIgnoreCase(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_OK)) {
							transExec.commitTransaction();
						} else {
							esitoFinale = esitoLavIsee.getCodErrore();
							transExec.rollBackTransaction();
						}
					}
					// Terzo step
					transExec.initTransaction();
					EsitoInserimentoLavoratore esitoAdesione = istanzeUtils.gestioneAdesioneLavoratore(candidatoXsd,
							cdnUtente, cdnGruppo, prgRosa, transExec);
					if (esitoAdesione.getCodErrore()
							.equalsIgnoreCase(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_OK)) {
						transExec.commitTransaction();
					} else {
						esitoFinale = esitoAdesione.getCodErrore();
						transExec.rollBackTransaction();
					}
				}

				if (StringUtils.isEmptyNoBlank(esitoFinale)) {
					esitoFinale = Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_OK;
				}

				// memorizzazione tabella detail
				transExec.initTransaction();
				String xmlCandidatura = istanzeUtils.getXmlCandidatura(candidatoXsd);
				BigDecimal prgRisultato = istanzeUtils.getPrgRisultatoIstanza(transExec);
				Object[] detailParams = new Object[5];
				detailParams[0] = prgIstanza;
				detailParams[1] = esitoFinale;
				detailParams[2] = prgRisultato;
				detailParams[3] = xmlCandidatura;
				detailParams[4] = candidatoXsd.getAnagrafica().getCodicefiscale().trim().toUpperCase();
				boolean success = ((Boolean) transExec.executeQuery("INSERT_DO_RISULTATO_ISTANZA", detailParams,
						"INSERT")).booleanValue();
				if (success) {
					transExec.commitTransaction();
				} else {
					transExec.rollBackTransaction();
				}

				// this.sleep(10000);
			}
			transExec.initTransaction();
			Object[] updateParams = new Object[3];
			updateParams[0] = IstanzeBeanUtils.ELABORAZIONE_TERMINATA;
			updateParams[1] = cdnUtente;
			updateParams[2] = prgIstanza;

			boolean success = ((Boolean) transExec.executeQuery("UPDATE_DO_CARICA_ISTANZA", updateParams, "UPDATE"))
					.booleanValue();
			if (success) {
				transExec.commitTransaction();
			} else {
				transExec.rollBackTransaction();
			}
			_logger.info("=========== Scarico istanze online terminato ===========");
		} catch (EMFInternalError e) {
			_logger.fatal("EMFInternalError", e);
		} catch (JAXBException e) {
			_logger.fatal("JAXBException", e);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			if (transExec != null) {
				transExec.closeConnection();
			}
		}
	}

	public BigDecimal getPrgIstanza() {
		return prgIstanza;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		transExec.closeConnection();
		_logger.debug("EseguiAzioniCandidatura::run(): Connessione chiusa dal Garbage Collector!!!");
	}

}
