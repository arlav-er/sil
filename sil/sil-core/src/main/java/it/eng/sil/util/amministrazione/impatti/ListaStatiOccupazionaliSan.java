/*
 * Creato il 13-ottobre-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util.amministrazione.impatti;

import java.util.List;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ListaStatiOccupazionaliSan extends ListaStatiOccupazionali {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ListaStatiOccupazionaliSan.class.getName());

	public ListaStatiOccupazionaliSan(SourceBean movimento, List movimenti, List statiOccupazionali, List dids,
			List listaMobilita, TransactionQueryExecutor txExecutor) throws Exception {
		super(movimento, movimenti, statiOccupazionali, dids, listaMobilita, txExecutor);
	}

	protected void init(String dataRif, Object cdnLavoratore, List dids, List listaMobilita, List movimenti)
			throws Exception {
		StatoOccupazionaleBean statoOccCorrente = null;
		for (int i = 0; i < statiOccupazionali.size(); i++) {
			StatoOccupazionaleBean so = (StatoOccupazionaleBean) statiOccupazionali.get(i);
			String dataInizio = (String) so.getDataInizio();
			if (DateUtils.compare(dataRif, dataInizio) > 0) {
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