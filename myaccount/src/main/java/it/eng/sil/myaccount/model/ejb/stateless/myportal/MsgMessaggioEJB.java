package it.eng.sil.myaccount.model.ejb.stateless.myportal;

import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.entity.myportal.MsgMessaggio;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.AbstractTabellaGestioneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

@Stateless
public class MsgMessaggioEJB extends AbstractTabellaGestioneEJB<MsgMessaggio, Integer> {

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	public void setDestinatario(MsgMessaggio messaggio, PfPrincipal pfPrincipal) throws EJBException,
			MyCasNoResultException {
		DeProvincia provincia = null;
		// se chi invia è un cittadino
		if (pfPrincipal.isUtente()) {
			UtenteInfo utenteInfo = pfPrincipal.getUtenteInfo();
			if (utenteInfo.getDeProvincia() != null) {
				provincia = utenteInfo.getDeProvincia();
			} else if (utenteInfo.getDeComuneDomicilio() != null) {
				provincia = utenteInfo.getDeComuneDomicilio().getDeProvincia();
			} else if (utenteInfo.getDeComuneNascita() != null) {
				provincia = utenteInfo.getDeComuneNascita().getDeProvincia();
			} else {
				throw new EJBException("Impossibile spedire il messaggio per l'utente '" + pfPrincipal.getUsername()
						+ "' in quanto non ha una provincia di riferimento.");
			}
			// se chi invia è un'azienda
		} else if (pfPrincipal.isAzienda()) {
			provincia = aziendaInfoEJB.getProvinciaRiferimento(pfPrincipal.getAziendaInfo());
		} else {
			throw new EJBException("Impossibile identificare il tipo di utente.");
		}
		String codProvinciaTo = provincia.getCodProvincia();

		if (codProvinciaTo == null) {
			throw new EJBException("Non hai una provincia associata a cui inviare il messaggio");
		}

		DeProvincia prov = deProvinciaEJB.findById(codProvinciaTo);
		messaggio.setCodProvinciaTo(codProvinciaTo);
		log.info("Il messaggio dell'utente '" + pfPrincipal.getUsername() + "' verrà  inviato alla provincia "
				+ prov.getDescrizione());
	}

	@Override
	public String getFriendlyName() {
		return "Tabella dei messaggi";
	}

	@Override
	public Class<MsgMessaggio> getEntityClass() {
		return MsgMessaggio.class;
	}
}