package it.eng.myportal.entity.home;

import it.eng.myportal.entity.IcarSessione;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.exception.MyPortalException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Turro
 */
@Stateless
public class IcarSessioneHome {

	protected final Log log = LogFactory.getLog(this.getClass());

	@PersistenceContext
	protected EntityManager entityManager;

	public Map<String, String> getSessioneByToken(String token) {

		String query = "select i from IcarSessione i where i.token = :token";
		List<IcarSessione> lista = entityManager.createQuery(query, IcarSessione.class)
				.setParameter("token", token)
				.getResultList();
		if (lista == null || lista.isEmpty()) {
			throw new MyPortalException("Token non trovato", true);
		}
		Map<String, String> map = new HashMap<String, String>();
		for (IcarSessione i : lista) {
			map.put(i.getChiave(), i.getValore());
			log.info(String.format("%s=%s", i.getChiave(), i.getValore()));
		}

		 query = "delete from IcarSessione i where i.token = :token";
		 entityManager.createQuery(query).setParameter("token", token).executeUpdate();
		return map;

	}

	public UtenteInfo getUtenteInfoForteByCf(String cf) {
		String query = "select u from UtenteInfo u, PfPrincipal p where p.idPfPrincipal=u.idPfPrincipal and u.codiceFiscale =:cf "
				+ " and p.flagAbilitatoServizi='Y' "
				+ "and p.email not like 'cl\\_%' and upper(p.username) not like 'CANC\\_%'";

		List<UtenteInfo> listaUtenti = entityManager.createQuery(query, UtenteInfo.class)
				.setParameter("cf", cf)
				.getResultList();

		if (listaUtenti == null || listaUtenti.isEmpty()) {
			return null;
		} else {
			if (listaUtenti.size() == 1) {
				return listaUtenti.get(0);
			} else {
				throw new MyPortalException("Troppi utenti trovati per il codice fiscale '" + cf + "'", true);
			}

		}

	}

}
