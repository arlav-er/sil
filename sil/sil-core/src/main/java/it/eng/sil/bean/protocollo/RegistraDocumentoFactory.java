/*	
 * Created on Feb 28, 2007
 */
package it.eng.sil.bean.protocollo;

import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;

/**
 * @author Savino
 */
public class RegistraDocumentoFactory {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RegistraDocumentoFactory.class.getName());

	public static RegistraDocumentoStrategy getStrategy(Documento doc, TransactionQueryExecutor tex)
			throws EMFUserError {
		RegistraDocumentoStrategy proto = null;
		if (doc == null)
			throw new EMFUserError(EMFErrorSeverity.ERROR, -1);
		if (tex == null)
			throw new EMFUserError(EMFErrorSeverity.ERROR, -1);
		// NON E' POSSIBILE CHE SI ARRIVI ANCORA QUI CON IL CODSTATOATTO NON
		// SETTATO........
		if (doc.getCodStatoAtto() == null || doc.getCodStatoAtto().equals(""))
			throw new EMFUserError(EMFErrorSeverity.ERROR, -1);
		try {
			int tipo = ProtocolloDocumentoUtil.tipoProtocollo();
			int x = -1;
			// tipo = 3;
			// tipo = "PR".equals(doc.getCodStatoAtto())? tipo : 0;
			/*
			 * E' importante sapere se si deve o meno protocollare il documento. Anche nel caso in cui si debba
			 * annullarlo potrebbe essere necessario chiamare docarea, ma per ora non si fa.
			 * 
			 * 0. da protocollare in docarea versione 1.6.1 1. da protocollare in locale in automatico, oppure da non
			 * protocollare anche se in docarea, oppure gia' protocollato esternamente 2. da protocollare
			 * protocollazione manuale 3. da protocollare in docarea ma nella versione particolare usata dal sare.
			 */
			if (doc.protocollatoEsternamente())
				x = 2;
			else if ("PR".equals(doc.getCodStatoAtto()) && tipo == ProtocolloDocumentoUtil.TIPO_DOCAREA_VER_161)
				x = 0;
			else if ((tipo == ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_AUTOMATICA)
					|| (!"PR".equals(doc.getCodStatoAtto()) && !ProtocolloDocumentoUtil.protocollazioneLocale()))
				x = 1;
			else if ("PR".equals(doc.getCodStatoAtto()) && tipo == ProtocolloDocumentoUtil.TIPO_DOCAREA_SARE)
				x = 3;
			else if (tipo == ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_MANUALE)
				x = 2;

			switch (x) {
			case 0:
				proto = (RegistraDocumentoStrategy) Class
						.forName("it.eng.sil.bean.protocollo.RDS_ProtocolloDocAreaPantarei161").newInstance();
				break;
			case 1:
				proto = (RegistraDocumentoStrategy) Class.forName("it.eng.sil.bean.protocollo.RDS_DBLocale")
						.newInstance();
				break;
			case 2:
				proto = (RegistraDocumentoStrategy) Class.forName("it.eng.sil.bean.protocollo.RDS_DBLocale")
						.newInstance();
				break;
			case 3:
				proto = (RegistraDocumentoStrategy) Class
						.forName("it.eng.sil.bean.protocollo.RDS_ProtocolloDocAreaSare").newInstance();
				// proto = (RegistraDocumentoStrategy)
				// Class.forName("it.eng.sil.bean.protocollo.DOCAREAProtocolloDocumentoAMano").newInstance();

				break;
			}

			//
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile creare la classe di gestione del numero di protocollo", e);

			// TODO Savino DOCAREA: creare il numero di errore per il protocollo
			throw new EMFUserError(EMFErrorSeverity.ERROR, -1);
		}
		return proto;
	}
}