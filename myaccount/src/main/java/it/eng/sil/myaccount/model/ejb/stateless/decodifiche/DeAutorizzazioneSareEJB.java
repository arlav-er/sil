package it.eng.sil.myaccount.model.ejb.stateless.decodifiche;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeAutorizzazioneSare;
import it.eng.sil.mycas.model.manager.AbstractTabellaDecodificaEJB;

import java.util.List;

import javax.ejb.Stateless;

@Stateless
public class DeAutorizzazioneSareEJB extends AbstractTabellaDecodificaEJB<DeAutorizzazioneSare, String> {

	public DeAutorizzazioneSare findAutorizzazioneConcessa() throws MyCasNoResultException {
		return findById("4");
	}

	@Override
	public String getFriendlyName() {
		return "Tipo Utente SARE";
	}

	protected List<DeAutorizzazioneSare> findByCodPadre(String codicePadre) throws Exception {
		throw new UnsupportedOperationException("findByCodPadre: is not implemented");
	}

	@Override
	public Class<DeAutorizzazioneSare> getEntityClass() {
		return DeAutorizzazioneSare.class;
	}

}
