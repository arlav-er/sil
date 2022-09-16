/*
 * Creato il 13-ottobre-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ControlloReddito {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ControlloReddito.class.getName());
	public static final boolean REDDITO_SU_DUE_ANNI = true;

	/**
	 * controlla se c'è stato superamento del limite (reddito) nell'anno della did o nell'anno successivo.
	 * 
	 * @param statoOccupazionale
	 * @param mov
	 * @param limiteAnnoSuccessivo
	 * @return
	 * @throws Exception
	 * @throws ControlliException
	 */
	public static boolean minoreDelLimite(StatoOccupazionaleBean statoOccupazionale, SourceBean mov,
			double limiteAnnoSuccessivo) throws Exception, ControlliException {
		boolean superato = false;
		boolean superatoAnnoDid = false;
		// boolean superatoAnnoSuccessivo = false;
		superatoAnnoDid = statoOccupazionale.getReddito() > statoOccupazionale.getLimiteReddito();
		// superatoAnnoSuccessivo = statoOccupazionale.getRedditoAnnoSuccessivo() > limiteAnnoSuccessivo;
		superato = superatoAnnoDid;
		return minoreDelLimite(superato, mov);
	}

	/**
	 * controlla se c'è stato superamento del limite (reddito) quando si sta inserendo la DID o quando in ricostruzione
	 * storia si incontra la DID.
	 * 
	 * @param reddito
	 * @param mov
	 * @param cdnlavoratore
	 * @return
	 * @throws Exception
	 * @throws ControlliException
	 */
	public static boolean minoreDelLimite(Reddito reddito, SourceBean mov, String cdnlavoratore, SourceBean cm,
			TransactionQueryExecutor transExec) throws Exception, ControlliException {
		return minoreDelLimite(!reddito.minoreDelLimite(mov, cdnlavoratore, cm, transExec), mov);
	}

	private static boolean minoreDelLimite(boolean limiteSuperato, SourceBean mov)
			throws Exception, ControlliException {
		boolean minore = false;
		SourceBean request = RequestContainer.getRequestContainer().getServiceRequest();
		Object op = request.getAttribute(MovimentoBean.FLAG_OP_SANARE);
		boolean forzatura = request.containsAttribute(MovimentoBean.FLAG_OP_SANARE_FORZATURA)
				&& request.getAttribute(MovimentoBean.FLAG_OP_SANARE_FORZATURA).equals("S");
		if (op == null || (mov != null && mov.containsAttribute(MovimentoBean.FLAG_OP_SANARE_NON_GESTIRE))) {
			// non e' in atto nessuna oprazione per sanare la sit. amm.
			minore = !limiteSuperato;
		} else {
			// e' in corso l' operazione per sanare la sit. amm.
			String flagDettaglio = (String) request.getAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO);
			String flagLimiteSuperato = (String) request.getAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO);
			boolean flagLimiteSuperatoMov = false;
			if (mov != null) {
				Object o = mov.getAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO);
				flagLimiteSuperatoMov = o != null && o.equals("S");
			}
			if (flagDettaglio.equals("S")) {
				if (flagLimiteSuperato.equals("S")) {
					// dettaglio superamento limite
					_logger.debug(
							"ControlloReddito: DS: limiteSuperato=" + limiteSuperato + ", forzatura=" + forzatura);

					if (!limiteSuperato) {
						if (mov != null) {
							if (mov.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA) != null) {
								request.updAttribute(SanareSituazioneAmministrativa.FLAG_LIMITE_INF, "S");
							} else {
								request.updAttribute(SanareSituazioneAmministrativa.FLAG_LIMITE_SUP, "S");
							}
						}
					} else {
						request.updAttribute(SanareSituazioneAmministrativa.FLAG_LIMITE_SUP, "S");
					}
					minore = !limiteSuperato;
				} else {
					// dettaglio mancato superamento limite
					_logger.debug(
							"ControlloReddito: DN: limiteSuperato=" + limiteSuperato + ", forzatura=" + forzatura);

					minore = !limiteSuperato;
					if (limiteSuperato) {
						if (!forzatura) {
							throw new ControlliException(MessageCodes.StatoOccupazionale.REDDITO_SUPERIORE_OP_SANARE);
						}
					} else {
						if (mov != null) {
							if (mov.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA) == null) {
								if (!forzatura) {
									throw new ControlliException(
											MessageCodes.StatoOccupazionale.REDDITO_SUPERIORE_OP_SANARE);
								}
							}
						}
					}
				}
			} else {// dichiarazione generica
				if (flagLimiteSuperato.equals("S")) {
					// superamento limite
					_logger.debug(
							"ControlloReddito: GS: limiteSuperato=" + limiteSuperato + ", forzatura=" + forzatura);

					/**
					 * se la richiesta del reddito viene dalla did allora di default ritorno un reddito maggiore come da
					 * tipo dichiarazione
					 */
					if (mov == null) {
						if (limiteSuperato) {
							minore = false;
						} else {
							minore = true;
						}
					} else {
						/**
						 * se il movimento precede quello da cui si e' verificato il superamento ritorno un reddito
						 * basso altrimenti il percorso sara' quello di limite superato
						 */
						if (flagLimiteSuperatoMov) {
							minore = false;
						}

						else {
							minore = true;
						}
					}
				} else {
					// mancato superamento limite
					_logger.debug(
							"ControlloReddito: GN: limiteSuperato=" + limiteSuperato + ", forzatura=" + forzatura);

					minore = true;
					if (limiteSuperato) {
						if (!forzatura)
							throw new ControlliException(MessageCodes.StatoOccupazionale.REDDITO_SUPERIORE_OP_SANARE);
					}
				}
			}
		}
		return minore;
	}
}