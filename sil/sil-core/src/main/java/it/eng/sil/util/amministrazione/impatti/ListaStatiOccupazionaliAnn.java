/*
 * Creato il 13-ottobre-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util.amministrazione.impatti;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

public class ListaStatiOccupazionaliAnn extends ListaStatiOccupazionali {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ListaStatiOccupazionaliAnn.class.getName());

	public ListaStatiOccupazionaliAnn(String dataRif, Object cdnLavoratore, java.util.List statiOccupazionali,
			java.util.List dids, java.util.List listaMobilita, java.util.List movimenti,
			it.eng.afExt.utils.TransactionQueryExecutor txExecutor) throws Exception {
		super(dataRif, cdnLavoratore, statiOccupazionali, dids, listaMobilita, movimenti, txExecutor);
	}

	protected void init(String dataRif, Object cdnLavoratore, java.util.List dids, java.util.List listaMobilita,
			java.util.List movimenti) throws Exception {

		StatoOccupazionaleBean statoOccCorrente = null;
		for (int i = 0; i < statiOccupazionali.size(); i++) {
			StatoOccupazionaleBean so = (StatoOccupazionaleBean) statiOccupazionali.get(i);
			String dataInizio = (String) so.getDataInizio();
			if (it.eng.afExt.utils.DateUtils.compare(dataRif, dataInizio) > 0) {
				posIniziale = i;
				_logger.debug("ListaStatiOccupazionali.init():trovato indice=" + i);

			} else
				break;
		}

		if (posIniziale < 0) {

			StatoOccupazionaleBean stAperto = (StatoOccupazionaleBean) (statiOccupazionali.size() == 0 ? null
					: statiOccupazionali.get(statiOccupazionali.size() - 1));
			statoOccCorrente = StatoOccupazionaleBean.creaStatoOccupazionaleBeanIniziale(dataRif, cdnLavoratore,
					stAperto, dids, listaMobilita, movimenti, txExecutor);
			statiOccupazionali.add(0, statoOccCorrente);
			posIniziale = 0;
			_logger.debug("ListaStatiOccupazionali.init():indice non trocato");

		}
	}
}