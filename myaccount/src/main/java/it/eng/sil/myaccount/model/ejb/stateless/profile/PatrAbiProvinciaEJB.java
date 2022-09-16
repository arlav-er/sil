package it.eng.sil.myaccount.model.ejb.stateless.profile;

import it.eng.sil.mycas.model.entity.profilo.PatrAbiProvincia;
import it.eng.sil.mycas.model.manager.AbstractTabellaGestioneEJB;

import java.util.List;

import javax.ejb.Stateless;

@Stateless
public class PatrAbiProvinciaEJB extends AbstractTabellaGestioneEJB<PatrAbiProvincia, Integer> {

	// TODO che roba è?
	@Override
	public String getFriendlyName() {
		return "Patr abi provincia";
	}

	@Override
	public Class<PatrAbiProvincia> getEntityClass() {
		return PatrAbiProvincia.class;
	}

	/**
	 * Recupera le PatrAbiProvincia relative ad un certo utente di tipo "patronato".
	 * 
	 * @param patronatoId
	 *            L'id dell'utente patronato.
	 */
	public List<PatrAbiProvincia> findByPatronatoId(Integer patronatoId) {
		return entityMan.createNamedQuery("findPatrAbiProvinciaByPatronatoId", PatrAbiProvincia.class)
				.setParameter("idPatronato", patronatoId).getResultList();
	}

}
