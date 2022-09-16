package it.eng.myportal.rest.app.helper;

import java.util.Date;

import javax.ejb.EJBException;

import org.apache.commons.lang3.time.DateUtils;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.utils.ConstantsSingleton;

public class AppUtils {

	public static final DeProvincia getDeProvinciaRiferimento(PfPrincipal pfPrincipal) {
		/*
		 * La provincia di riferimento per le notifiche puntuale e per feedback viene determinata a partire dall'utente
		 * cittadino destinatario o mittente (per feedback) della stessa. Viene verificata la provincia riferimento, comune di
		 * domicilio e comune di nascita.
		 */
		DeProvincia ret = null;

		if (pfPrincipal.isUtente()) {
			UtenteInfo utenteInfo = pfPrincipal.getUtenteInfo();

			if (utenteInfo.getDeProvincia() != null) {
				ret = utenteInfo.getDeProvincia();
			} else if (utenteInfo.getDeComuneDomicilio() != null) {
				ret = utenteInfo.getDeComuneDomicilio().getDeProvincia();
			} else if (utenteInfo.getDeComuneNascita() != null) {
				ret = utenteInfo.getDeComuneNascita().getDeProvincia();
			} else {
				throw new EJBException(
						"Impossibile determinare la provincia di riferimento per l'utente in quanto non presente'"
								+ utenteInfo.getPfPrincipal().getUsername());
			}
		} else {
			throw new EJBException(
					"Impossibile determinare la provincia di riferimento, tipologia utente non corretta");
		}
		return ret;
	}
		
	/**
	 * Inizializzazione del CV da persistere rispetto alle logiche nuovo IDO o meno
	 * 
	 * @return
	 */
	public CvDatiPersonali initCV() {
		CvDatiPersonali cvDatiPersonali = new CvDatiPersonali();
				
		if (ConstantsSingleton.App.NUOVO_IDO) {
			// Inizializzazioni nuovo IDO
			
			// Flag IDO: true se nuovo IDO, false altrimenti
			cvDatiPersonali.setFlagIdo(true);
			// OpzTipoDecodifiche: S se nuovo IDO, null per tutte le altre
			cvDatiPersonali.setOpzTipoDecodifiche(CvDatiPersonali.OpzTipoDecodifiche.SIL);
			// Flag check trattamento dati personali true
			cvDatiPersonali.setFlagTrattamentoDati(true);
			
		} else {
			cvDatiPersonali.setFlagIdo(false);
			cvDatiPersonali.setOpzTipoDecodifiche(null);;
		}
		
		return cvDatiPersonali;
	}
	
	/** 
	 * Metodo per il calcolo di una data di scadenza del CV.
	 * Per Trento 60 gg, per RER 180 per le altre regioni 30.
	 * 
	 * @return
	 */
	public Date calcolaDtaScadenzaCV(Date dtaRiferimento) {		
		Date dtScadenza = null;
		Integer codRegione = ConstantsSingleton.COD_REGIONE;
		
		if (codRegione.equals(ConstantsSingleton.COD_REGIONE_TRENTO)) {
			dtScadenza = DateUtils.addDays(dtaRiferimento, 60);
		} else if (codRegione.equals(ConstantsSingleton.COD_REGIONE_RER)){
			dtScadenza = DateUtils.addDays(dtaRiferimento, 180);
		} else {
			dtScadenza = DateUtils.addDays(dtaRiferimento, 30);
		}
		
		return dtScadenza;
	}

}
