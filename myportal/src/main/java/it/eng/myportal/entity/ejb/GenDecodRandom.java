package it.eng.myportal.entity.ejb;

import it.eng.myportal.entity.decodifiche.DeAbilitazioneGen;
import it.eng.myportal.entity.decodifiche.DeAgevolazione;
import it.eng.myportal.entity.decodifiche.DeAlbo;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.DeAttivitaPf;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.DeCorso;
import it.eng.myportal.entity.decodifiche.DeFiltro;
import it.eng.myportal.entity.decodifiche.DeGradoLin;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeOrario;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.DePatentino;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeRetribuzione;
import it.eng.myportal.entity.decodifiche.DeSvSezione;
import it.eng.myportal.entity.decodifiche.DeSvTemplate;
import it.eng.myportal.entity.decodifiche.DeTipoAbilitazioneGen;
import it.eng.myportal.entity.decodifiche.DeTipoPoi;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.DeTrasferta;
import it.eng.myportal.entity.decodifiche.DeTurno;

import java.util.List;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

//@SecurityDomain(SecurityDomains.MYPORTAL)
//@RolesAllowed({ Roles.AMMINISTRATORE })
@Singleton
public class GenDecodRandom {

	@PersistenceContext
	private EntityManager entityManager;

	// private Log log = LogFactory.getLog(this.getClass());

	int indexListaDeAbilitazioneGen = 0;

	int indexListaDeAgevolazione = 0;
	int indexListaDeAlbo = 0;

	int indexListaDeAttivita = 0;

	int indexListaDeAttivitaPf = 0;
	int indexListaDeComune = 0;

	int indexListaDeContratto = 0;

	int indexListaDeCorso = 0;
	int indexListaDeFiltro = 0;

	int indexListaDeGradoLin = 0;

	int indexListaDeLingua = 0;
	int indexListaDeMansione = 0;

	int indexListaDeOrario = 0;

	int indexListaDePatente = 0;
	int indexListaDePatentino = 0;

	int indexListaDeProvenienzaVacancy = 0;

	int indexListaDeProvincia = 0;
	int indexListaDeRegione = 0;

	int indexListaDeRetribuzione = 0;

	int indexListaDeSvSezione = 0;
	int indexListaDeSvTemplate = 0;

	int indexListaDeTipoAbilitazioneGen = 0;

	int indexListaDeTipoContratto = 0;
	int indexListaDeTipoGruppo = 0;

	int indexListaDeTipoPoi = 0;

	int indexListaDeTitolo = 0;
	int indexListaDeTrasferta = 0;

	int indexListaDeTurno = 0;

	List<DeAbilitazioneGen> listaDeAbilitazioneGen = null;
	List<DeAgevolazione> listaDeAgevolazione = null;

	List<DeAlbo> listaDeAlbo = null;

	List<DeAttivita> listaDeAttivita = null;
	List<DeAttivitaPf> listaDeAttivitaPf = null;

	List<DeComune> listaDeComune = null;

	List<DeContratto> listaDeContratto = null;
	List<DeCorso> listaDeCorso = null;

	List<DeFiltro> listaDeFiltro = null;

	List<DeGradoLin> listaDeGradoLin = null;
	List<DeLingua> listaDeLingua = null;

	List<DeMansione> listaDeMansione = null;

	List<DeOrario> listaDeOrario = null;
	List<DePatente> listaDePatente = null;

	List<DePatentino> listaDePatentino = null;

	List<DeProvenienza> listaDeProvenienzaVacancy = null;
	List<DeProvincia> listaDeProvincia = null;

	List<DeRegione> listaDeRegione = null;

	List<DeRetribuzione> listaDeRetribuzione = null;
	List<DeSvSezione> listaDeSvSezione = null;

	List<DeSvTemplate> listaDeSvTemplate = null;

	List<DeTipoAbilitazioneGen> listaDeTipoAbilitazioneGen = null;

	List<DeTipoPoi> listaDeTipoPoi = null;
	List<DeTitolo> listaDeTitolo = null;

	List<DeTrasferta> listaDeTrasferta = null;

	List<DeTurno> listaDeTurno = null;

	public GenDecodRandom() {
	}

	public DeAbilitazioneGen getDeAbilitazioneGen() {

		if (listaDeAbilitazioneGen == null) {
			TypedQuery<DeAbilitazioneGen> query = entityManager.createQuery(
					"SELECT r from DeAbilitazioneGen r ORDER BY RANDOM()", DeAbilitazioneGen.class);
			query.setMaxResults(100);
			listaDeAbilitazioneGen = query.getResultList();

		}
		indexListaDeAbilitazioneGen++;
		return listaDeAbilitazioneGen.get(indexListaDeAbilitazioneGen % listaDeAbilitazioneGen.size());
	}

	public DeAgevolazione getDeAgevolazione() {

		if (listaDeAgevolazione == null) {
			TypedQuery<DeAgevolazione> query = entityManager.createQuery(
					"SELECT r from DeAgevolazione r ORDER BY RANDOM()", DeAgevolazione.class);
			query.setMaxResults(100);
			listaDeAgevolazione = query.getResultList();

		}
		indexListaDeAgevolazione++;
		return listaDeAgevolazione.get(indexListaDeAgevolazione % listaDeAgevolazione.size());
	}

	public DeAlbo getDeAlbo() {

		if (listaDeAlbo == null) {
			TypedQuery<DeAlbo> query = entityManager.createQuery("SELECT r from DeAlbo r ORDER BY RANDOM()",
					DeAlbo.class);
			query.setMaxResults(100);
			listaDeAlbo = query.getResultList();

		}
		indexListaDeAlbo++;
		return listaDeAlbo.get(indexListaDeAlbo % listaDeAlbo.size());
	}

	public DeAttivita getDeAttivita() {

		if (listaDeAttivita == null) {
			TypedQuery<DeAttivita> query = entityManager.createQuery("SELECT r from DeAttivita r ORDER BY RANDOM()",
					DeAttivita.class);
			query.setMaxResults(100);
			listaDeAttivita = query.getResultList();

		}
		indexListaDeAttivita++;
		return listaDeAttivita.get(indexListaDeAttivita % listaDeAttivita.size());
	}

	public DeAttivitaPf getDeAttivitaPf() {

		if (listaDeAttivitaPf == null) {
			TypedQuery<DeAttivitaPf> query = entityManager.createQuery(
					"SELECT r from DeAttivitaPf r ORDER BY RANDOM()", DeAttivitaPf.class);
			query.setMaxResults(100);
			listaDeAttivitaPf = query.getResultList();

		}
		indexListaDeAttivitaPf++;
		return listaDeAttivitaPf.get(indexListaDeAttivitaPf % listaDeAttivitaPf.size());
	}

	public DeComune getDeComune() {

		if (listaDeComune == null) {
			TypedQuery<DeComune> query = entityManager.createQuery("SELECT r from DeComune r ORDER BY RANDOM()",
					DeComune.class);
			query.setMaxResults(100);
			listaDeComune = query.getResultList();

		}
		indexListaDeComune++;
		return listaDeComune.get(indexListaDeComune % listaDeComune.size());
	}

	public DeContratto getDeContratto() {

		if (listaDeContratto == null) {
			TypedQuery<DeContratto> query = entityManager.createQuery("SELECT r from DeContratto r ORDER BY RANDOM()",
					DeContratto.class);
			query.setMaxResults(100);
			listaDeContratto = query.getResultList();

		}
		indexListaDeContratto++;
		return listaDeContratto.get(indexListaDeContratto % listaDeContratto.size());
	}

	public DeCorso getDeCorso() {

		if (listaDeCorso == null) {
			TypedQuery<DeCorso> query = entityManager.createQuery("SELECT r from DeCorso r ORDER BY RANDOM()",
					DeCorso.class);
			query.setMaxResults(100);
			listaDeCorso = query.getResultList();

		}
		indexListaDeCorso++;
		return listaDeCorso.get(indexListaDeCorso % listaDeCorso.size());
	}

	public DeFiltro getDeFiltro() {

		if (listaDeFiltro == null) {
			TypedQuery<DeFiltro> query = entityManager.createQuery("SELECT r from DeFiltro r ORDER BY RANDOM()",
					DeFiltro.class);
			query.setMaxResults(100);
			listaDeFiltro = query.getResultList();

		}
		indexListaDeFiltro++;
		return listaDeFiltro.get(indexListaDeFiltro % listaDeFiltro.size());
	}

	public DeGradoLin getDeGradoLin() {

		if (listaDeGradoLin == null) {
			TypedQuery<DeGradoLin> query = entityManager.createQuery("SELECT r from DeGradoLin r ORDER BY RANDOM()",
					DeGradoLin.class);
			query.setMaxResults(100);
			listaDeGradoLin = query.getResultList();

		}
		indexListaDeGradoLin++;
		return listaDeGradoLin.get(indexListaDeGradoLin % listaDeGradoLin.size());
	}

	public DeLingua getDeLingua() {

		if (listaDeLingua == null) {
			TypedQuery<DeLingua> query = entityManager.createQuery("SELECT r from DeLingua r ORDER BY RANDOM()",
					DeLingua.class);
			query.setMaxResults(100);
			listaDeLingua = query.getResultList();

		}
		indexListaDeLingua++;
		return listaDeLingua.get(indexListaDeLingua % listaDeLingua.size());
	}

	public DeMansione getDeMansione() {

		if (listaDeMansione == null) {
			TypedQuery<DeMansione> query = entityManager.createQuery("SELECT r from DeMansione r ORDER BY RANDOM()",
					DeMansione.class);
			query.setMaxResults(100);
			listaDeMansione = query.getResultList();

		}
		indexListaDeMansione++;
		return listaDeMansione.get(indexListaDeMansione % listaDeMansione.size());
	}

	public DeOrario getDeOrario() {

		if (listaDeOrario == null) {
			TypedQuery<DeOrario> query = entityManager.createQuery("SELECT r from DeOrario r ORDER BY RANDOM()",
					DeOrario.class);
			query.setMaxResults(100);
			listaDeOrario = query.getResultList();

		}
		indexListaDeOrario++;
		return listaDeOrario.get(indexListaDeOrario % listaDeOrario.size());
	}

	public DePatente getDePatente() {

		if (listaDePatente == null) {
			TypedQuery<DePatente> query = entityManager.createQuery("SELECT r from DePatente r ORDER BY RANDOM()",
					DePatente.class);
			query.setMaxResults(100);
			listaDePatente = query.getResultList();

		}
		indexListaDePatente++;
		return listaDePatente.get(indexListaDePatente % listaDePatente.size());
	}

	public DePatentino getDePatentino() {

		if (listaDePatentino == null) {
			TypedQuery<DePatentino> query = entityManager.createQuery("SELECT r from DePatentino r ORDER BY RANDOM()",
					DePatentino.class);
			query.setMaxResults(100);
			listaDePatentino = query.getResultList();

		}
		indexListaDePatentino++;
		return listaDePatentino.get(indexListaDePatentino % listaDePatentino.size());
	}

	public DeProvenienza getDeProvenienzaVacancy() {

		if (listaDeProvenienzaVacancy == null) {
			TypedQuery<DeProvenienza> query = entityManager.createQuery(
					"SELECT r from DeProvenienzaVacancy r ORDER BY RANDOM()", DeProvenienza.class);
			query.setMaxResults(100);
			listaDeProvenienzaVacancy = query.getResultList();

		}
		indexListaDeProvenienzaVacancy++;
		return listaDeProvenienzaVacancy.get(indexListaDeProvenienzaVacancy % listaDeProvenienzaVacancy.size());
	}

	public DeProvincia getDeProvincia() {

		if (listaDeProvincia == null) {
			TypedQuery<DeProvincia> query = entityManager.createQuery("SELECT r from DeProvincia r ORDER BY RANDOM()",
					DeProvincia.class);
			query.setMaxResults(100);
			listaDeProvincia = query.getResultList();

		}
		indexListaDeProvincia++;
		return listaDeProvincia.get(indexListaDeProvincia % listaDeProvincia.size());
	}

	public DeRegione getDeRegione() {

		if (listaDeRegione == null) {
			TypedQuery<DeRegione> query = entityManager.createQuery("SELECT r from DeRegione r ORDER BY RANDOM()",
					DeRegione.class);
			query.setMaxResults(100);
			listaDeRegione = query.getResultList();

		}
		indexListaDeRegione++;
		return listaDeRegione.get(indexListaDeRegione % listaDeRegione.size());
	}

	public DeRetribuzione getDeRetribuzione() {

		if (listaDeRetribuzione == null) {
			TypedQuery<DeRetribuzione> query = entityManager.createQuery(
					"SELECT r from DeRetribuzione r ORDER BY RANDOM()", DeRetribuzione.class);
			query.setMaxResults(100);
			listaDeRetribuzione = query.getResultList();

		}
		indexListaDeRetribuzione++;
		return listaDeRetribuzione.get(indexListaDeRetribuzione % listaDeRetribuzione.size());
	}

	public DeSvSezione getDeSvSezione() {

		if (listaDeSvSezione == null) {
			TypedQuery<DeSvSezione> query = entityManager.createQuery("SELECT r from DeSvSezione r ORDER BY RANDOM()",
					DeSvSezione.class);
			query.setMaxResults(100);
			listaDeSvSezione = query.getResultList();

		}
		indexListaDeSvSezione++;
		return listaDeSvSezione.get(indexListaDeSvSezione % listaDeSvSezione.size());
	}

	public DeSvTemplate getDeSvTemplate() {

		if (listaDeSvTemplate == null) {
			TypedQuery<DeSvTemplate> query = entityManager.createQuery(
					"SELECT r from DeSvTemplate r ORDER BY RANDOM()", DeSvTemplate.class);
			query.setMaxResults(100);
			listaDeSvTemplate = query.getResultList();

		}
		indexListaDeSvTemplate++;
		return listaDeSvTemplate.get(indexListaDeSvTemplate % listaDeSvTemplate.size());
	}

	public DeTipoAbilitazioneGen getDeTipoAbilitazioneGen() {

		if (listaDeTipoAbilitazioneGen == null) {
			TypedQuery<DeTipoAbilitazioneGen> query = entityManager.createQuery(
					"SELECT r from DeTipoAbilitazioneGen r ORDER BY RANDOM()", DeTipoAbilitazioneGen.class);
			query.setMaxResults(100);
			listaDeTipoAbilitazioneGen = query.getResultList();

		}
		indexListaDeTipoAbilitazioneGen++;
		return listaDeTipoAbilitazioneGen.get(indexListaDeTipoAbilitazioneGen % listaDeTipoAbilitazioneGen.size());
	}

	public DeTipoPoi getDeTipoPoi() {

		if (listaDeTipoPoi == null) {
			TypedQuery<DeTipoPoi> query = entityManager.createQuery("SELECT r from DeTipoPoi r ORDER BY RANDOM()",
					DeTipoPoi.class);
			query.setMaxResults(100);
			listaDeTipoPoi = query.getResultList();

		}
		indexListaDeTipoPoi++;
		return listaDeTipoPoi.get(indexListaDeTipoPoi % listaDeTipoPoi.size());
	}

	public DeTitolo getDeTitolo() {

		if (listaDeTitolo == null) {
			TypedQuery<DeTitolo> query = entityManager.createQuery("SELECT r from DeTitolo r ORDER BY RANDOM()",
					DeTitolo.class);
			query.setMaxResults(100);
			listaDeTitolo = query.getResultList();

		}
		indexListaDeTitolo++;
		return listaDeTitolo.get(indexListaDeTitolo % listaDeTitolo.size());
	}

	public DeTrasferta getDeTrasferta() {

		if (listaDeTrasferta == null) {
			TypedQuery<DeTrasferta> query = entityManager.createQuery("SELECT r from DeTrasferta r ORDER BY RANDOM()",
					DeTrasferta.class);
			query.setMaxResults(100);
			listaDeTrasferta = query.getResultList();

		}
		indexListaDeTrasferta++;
		return listaDeTrasferta.get(indexListaDeTrasferta % listaDeTrasferta.size());
	}

	public DeTurno getDeTurno() {

		if (listaDeTurno == null) {
			TypedQuery<DeTurno> query = entityManager.createQuery("SELECT r from DeTurno r ORDER BY RANDOM()",
					DeTurno.class);
			query.setMaxResults(100);
			listaDeTurno = query.getResultList();

		}
		indexListaDeTurno++;
		return listaDeTurno.get(indexListaDeTurno % listaDeTurno.size());
	}

	/**
	 * Svuota le collection interene del singleton. Tipicamente da invocare al termine della sessione.
	 */

	public void empty() {
		listaDeAbilitazioneGen = null;
		listaDeAgevolazione = null;
		listaDeAlbo = null;
		listaDeAttivita = null;
		listaDeAttivitaPf = null;
		listaDeComune = null;
		listaDeContratto = null;
		listaDeCorso = null;
		listaDeFiltro = null;
		listaDeGradoLin = null;
		listaDeLingua = null;
		listaDeMansione = null;
		listaDeOrario = null;
		listaDePatente = null;
		listaDePatentino = null;
		listaDeProvenienzaVacancy = null;
		listaDeProvincia = null;
		listaDeRegione = null;
		listaDeRetribuzione = null;
		listaDeSvSezione = null;
		listaDeSvTemplate = null;
		listaDeTipoAbilitazioneGen = null;
		listaDeTipoPoi = null;
		listaDeTitolo = null;
		listaDeTrasferta = null;
		listaDeTurno = null;

	}

}
