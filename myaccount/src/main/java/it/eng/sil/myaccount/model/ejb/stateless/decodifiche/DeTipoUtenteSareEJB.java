package it.eng.sil.myaccount.model.ejb.stateless.decodifiche;

import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeTipoUtenteSare;
import it.eng.sil.mycas.model.manager.AbstractTabellaDecodificaEJB;

import javax.ejb.Stateless;

@Stateless
public class DeTipoUtenteSareEJB extends AbstractTabellaDecodificaEJB<DeTipoUtenteSare, String> {

	@Override
	public String getFriendlyName() {
		return "Tipo Utente SARE";
	}

	@Override
	public Class<DeTipoUtenteSare> getEntityClass() {
		return DeTipoUtenteSare.class;
	}

}
