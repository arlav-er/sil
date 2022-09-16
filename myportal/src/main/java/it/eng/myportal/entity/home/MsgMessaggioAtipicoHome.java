package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.AtpConsulenzaDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeRuoloPortaleDTO;
import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.MsgAllegatoDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoMiniDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.AtpConsulenza;
import it.eng.myportal.entity.MsgAllegato;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.MsgMessaggioAtipico;
import it.eng.myportal.entity.MsgMessaggioAtipico_;
import it.eng.myportal.entity.MsgMessaggioLetto;
import it.eng.myportal.entity.MsgMessaggioLetto_;
import it.eng.myportal.entity.MsgMessaggio_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRuoloPortale;
import it.eng.myportal.entity.decodifiche.DeStatoPratica;
import it.eng.myportal.entity.decodifiche.DeStatoPratica_;
import it.eng.myportal.entity.decodifiche.DeTipoPratica;
import it.eng.myportal.entity.home.decodifiche.DeAtpAttivitaSvoltaHome;
import it.eng.myportal.entity.home.decodifiche.DeAtpContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoPraticaHome;
import it.eng.myportal.entity.home.decodifiche.DeTemaConsulenzaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoConsulenzaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoPraticaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoQuesitoHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Turro
 */
@Stateless
public class MsgMessaggioAtipicoHome extends AbstractUpdatableHome<MsgMessaggioAtipico, MsgMessaggioAtipicoDTO> {

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	MsgMessaggioLettoHome msgMessaggioLettoHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeTipoConsulenzaHome deTipoConsulenzaHome;

	@EJB
	DeAtpAttivitaSvoltaHome deAtpAttivitaSvoltaHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeAtpContrattoHome deAtpContrattoHome;

	@EJB
	MsgAllegatoHome msgAllegatoHome;

	@EJB
	DeStatoPraticaHome deStatoPraticaHome;

	@EJB
	DeTipoPraticaHome deTipoPraticaHome;

	@EJB
	DeTipoQuesitoHome deTipoQuesitoHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	AtpConsulenzaHome atpConsulenzaHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeRegioneHome deRegioneHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DeTemaConsulenzaHome deTemaConsulenzaHome;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	public MsgMessaggioAtipico findById(Integer id) {
		return findById(MsgMessaggioAtipico.class, id);
	}

	@Override
	public MsgMessaggioAtipicoDTO findDTOById(Integer id) {
		MsgMessaggioAtipico pers = findById(id);

		return toDTO(pers);
	}

	@Override
	public MsgMessaggioAtipicoDTO toDTO(MsgMessaggioAtipico entity) {
		if (entity == null)
			return null;
		MsgMessaggioAtipicoDTO dto = super.toDTO(entity);
		dto.setTicket(entity.getMsgMessaggio().getTicket());
		dto.setId(entity.getIdMsgMessaggio());
		dto.setOggetto(entity.getMsgMessaggio().getOggetto());
		dto.setCorpo(entity.getMsgMessaggio().getCorpo());
		dto.setDeStatoPratica(deStatoPraticaHome.toDTO(entity.getDeStatoPratica()));
		dto.setMittente(msgMessaggioHome.getNomeMessaggio(entity.getMsgMessaggio().getPfPrincipalFrom()));
		dto.setNome(entity.getNome());
		dto.setCognome(entity.getCognome());
		dto.setCodSesso(entity.getCodSesso());
		dto.setAltraAttivita(entity.getAltraAttivita());
		dto.setAssociazioneProfessionale(entity.getAssociazioneProfessionale());
		dto.setCap(entity.getCap());
		dto.setCommittenza(entity.getCommittenza());
		dto.setDtInoltroCoord(entity.getDtInoltroCoord());
		dto.setDtInoltroCPI(entity.getDtInoltroCPI());
		dto.setDtRisposta(entity.getDtRisposta());
		dto.setTipoConsulenza(deTipoConsulenzaHome.toDTO(entity.getDeTipoConsulenza()));
		if (entity.getDeAttivita() != null) {
			dto.setDeAttivita(deAttivitaHome.findDTOById(entity.getDeAttivita().getCodAteco()));
		}
		if (entity.getDeAtpAttivitaSvolta() != null) {
			dto.setDeAtpAttivitaSvolta(deAtpAttivitaSvoltaHome.findDTOById(entity.getDeAtpAttivitaSvolta()
					.getCodAtpAttivitaSvolta()));
		}
		if (entity.getDeAtpContratto() != null) {
			dto.setDeAtpContrattoDTO(deAtpContrattoHome.findDTOById(entity.getDeAtpContratto().getCodAtpContratto()));
			dto.setDeAtpContrattoPadreDTO(deAtpContrattoHome.findDTOById(dto.getDeAtpContrattoDTO().getCodPadre()));
		}
		if (entity.getDeComuneByCodComuneLavoro() != null) {
			dto.setDeComuneByCodComuneLavoro(deComuneHome.toDTO(entity.getDeComuneByCodComuneLavoro()));
		}
		if (entity.getDeComuneByCodComuneResidenza() != null) {
			dto.setDeComuneByCodComuneResidenza(deComuneHome.toDTO(entity.getDeComuneByCodComuneResidenza()));
		}
		if (entity.getDeProvincias() != null) {
			for (DeProvincia p : entity.getDeProvincias()) {
				dto.getDeProvincias().add(deProvinciaHome.toDTO(p));
			}
		}
		dto.setDeTipoQuesito(deTipoQuesitoHome.toDTO(entity.getDeTipoQuesito()));
		dto.setDeTitolo(deTitoloHome.toDTO(entity.getDeTitolo()));
		dto.setEmail(entity.getEmail());
		dto.setIdFrom(entity.getMsgMessaggio().getPfPrincipalFrom().getIdPfPrincipal());
		dto.setFlagAssociazioneProfessionale(entity.getFlagAssociazioneProfessionale());
		dto.setFasciaEta(entity.getFasciaEta());
		dto.setOpzStatoOccupazionale(entity.getOpzStatoOccupazionale());
		dto.setTelefonoFax(entity.getTelefonoFax());
		dto.setTemaConsulenza(deTemaConsulenzaHome.toDTO(entity.getMsgMessaggio().getDeTemaConsulenza()));
		dto.setInoltri(new ArrayList<MsgMessaggioDTO>());
		if (entity.getMsgMessaggio() != null && entity.getMsgMessaggio().getInoltrati() != null) {
			for (MsgMessaggio m : entity.getMsgMessaggio().getInoltrati()) {
				dto.getInoltri().add(msgMessaggioHome.toDTO(m));
			}
		}
		if (entity.getMsgMessaggio().getInoltrante() != null) {
			dto.setIdMsgMessaggioInoltrante(entity.getMsgMessaggio().getInoltrante().getIdMsgMessaggio());
		}
		List<MsgMessaggioDTO> risposte = new ArrayList<MsgMessaggioDTO>();
		if (entity.getMsgMessaggio().getSuccessivo() != null) {
			risposte.add(msgMessaggioHome.toDTO(entity.getMsgMessaggio().getSuccessivo()));
			dto.setRisposte(risposte);
		}
		dto.setAttivitaSvoltaTesto(entity.getAttivitaSvoltaTesto());
		dto.setMotivoRifiuto(entity.getMotivoRifiuto());
		dto.setDeProvincia(deProvinciaHome.toDTO(entity.getDeProvincia()));

		List<MsgMessaggioDTO> richiesteCPI = dto.getInoltriDaCPIversoConsulente();
		if (richiesteCPI != null && richiesteCPI.size() > 0) {
			List<MsgMessaggioDTO> consulenze = richiesteCPI.get(richiesteCPI.size() - 1).getRisposte();
			if (consulenze != null && consulenze.size() > 0) {
				dto.setConsulenza((AtpConsulenzaDTO) consulenze.get(consulenze.size() - 1));
			}
		}
		dto.setFlagIscrittoCpi(entity.getFlagIscrittoCpi());
		if (entity.getDeComuneIscrizione() != null && entity.getDeComuneIscrizione().getCodCom() != null) {
			dto.setComuneIscrizione(deComuneHome.toDTO(entity.getDeComuneIscrizione()));
		}
		if (entity.getDeAtpContrattoUltimo() != null && entity.getDeAtpContrattoUltimo().getCodAtpContratto() != null) {
			dto.setDeAtpContrattoUltimoDTO(deAtpContrattoHome.toDTO(entity.getDeAtpContrattoUltimo()));
			dto.setDeAtpContrattoPadreDTO(deAtpContrattoHome
					.findDTOById(dto.getDeAtpContrattoUltimoDTO().getCodPadre()));
		}

		if (entity.getDeSettoreUltimoContratto() != null && entity.getDeSettoreUltimoContratto().getCodAteco() != null) {
			dto.setSettoreUltimoContratto(deAttivitaHome.toDTO(entity.getDeSettoreUltimoContratto()));
		}
		dto.setDtFineUltimoContratto(entity.getDtFineUltimoContratto());
		if (entity.getDeTipoPratica() != null && entity.getDeTipoPratica().getCodTipoPratica() != null) {
			dto.setDeTipoPratica(deTipoPraticaHome.toDTO(entity.getDeTipoPratica()));
		}

		if (entity.getDeRegione() != null) {
			dto.setDeRegione(deRegioneHome.toDTO(entity.getDeRegione()));
		}

		if (entity.getMsgMessaggio().getMsgAllegatos() != null) {
			dto.setAllegati(new ArrayList<MsgAllegatoDTO>());
			for (MsgAllegato a : entity.getMsgMessaggio().getMsgAllegatos()) {
				dto.getAllegati().add(msgAllegatoHome.toDTO(a));
			}
		}
		dto.setFlgSeparataInps(entity.getFlgSeparataInps());
		return dto;
	}

	@Override
	public MsgMessaggioAtipico fromDTO(MsgMessaggioAtipicoDTO dto) {
		if (dto == null)
			return null;
		MsgMessaggioAtipico entity = super.fromDTO(dto);
		entity.setNome(dto.getNome());
		entity.setCognome(dto.getCognome());
		entity.setCodSesso(dto.getCodSesso());
		entity.setAltraAttivita(dto.getAltraAttivita());
		entity.setAssociazioneProfessionale(dto.getAssociazioneProfessionale());
		entity.setCap(dto.getCap());
		entity.setCommittenza(dto.getCommittenza());
		entity.setDeTipoConsulenza(deTipoConsulenzaHome.findById(dto.getTipoConsulenza().getId()));
		if (dto.getDeAttivita() != null && dto.getDeAttivita().getId() != null) {
			entity.setDeAttivita(deAttivitaHome.findById(dto.getDeAttivita().getId()));
		} else if (dto.getSettoreUltimoContratto().getId() != null) {
			entity.setDeAttivita(deAttivitaHome.findById(dto.getSettoreUltimoContratto().getId()));
		}
		if (dto.getDeAtpAttivitaSvolta() != null && dto.getDeAtpAttivitaSvolta().getId() != null) {
			entity.setDeAtpAttivitaSvolta(deAtpAttivitaSvoltaHome.findById(dto.getDeAtpAttivitaSvolta().getId()));
		}
		if (dto.getDeAtpContrattoDTO() != null && dto.getDeAtpContrattoDTO().getId() != null) {
			entity.setDeAtpContratto(deAtpContrattoHome.findById(dto.getDeAtpContrattoDTO().getId()));
		}
		if (dto.getDeComuneByCodComuneLavoro() != null && dto.getDeComuneByCodComuneLavoro().getId() != null) {
			entity.setDeComuneByCodComuneLavoro(deComuneHome.findById(dto.getDeComuneByCodComuneLavoro().getId()));
		}
		if (dto.getDeComuneByCodComuneResidenza() != null && dto.getDeComuneByCodComuneResidenza().getId() != null) {
			entity.setDeComuneByCodComuneResidenza(deComuneHome.findById(dto.getDeComuneByCodComuneResidenza().getId()));
		}
		if (dto.getDeProvincias() != null) {
			for (DeProvinciaDTO p : dto.getDeProvincias()) {
				entity.getDeProvincias().add(deProvinciaHome.findById(p.getId()));
			}
		}
		entity.setAttivitaSvoltaTesto(dto.getAttivitaSvoltaTesto());
		entity.setDeTipoQuesito(deTipoQuesitoHome.findById(dto.getDeTipoQuesito().getId()));
		if (dto.getDeTitolo() != null && dto.getDeTitolo().getId() != null) {
			entity.setDeTitolo(deTitoloHome.findById(dto.getDeTitolo().getId()));
		}
		entity.setEmail(dto.getEmail());
		entity.setFlagAssociazioneProfessionale(dto.getFlagAssociazioneProfessionale());
		entity.setFasciaEta(dto.getFasciaEta());
		entity.setOpzStatoOccupazionale(dto.getOpzStatoOccupazionale());
		entity.setTelefonoFax(dto.getTelefonoFax());
		entity.setDeStatoPratica(deStatoPraticaHome.findById(dto.getDeStatoPratica().getId()));
		if (dto.getId() != null) {
			entity.setMsgMessaggio(msgMessaggioHome.findById(dto.getId()));
		}
		entity.setDtInoltroCoord(dto.getDtInoltroCoord());
		entity.setDtInoltroCPI(dto.getDtInoltroCPI());
		entity.setDtRisposta(dto.getDtRisposta());
		if (dto.getDeTipoPratica() != null && dto.getDeTipoPratica().getId() != null) {
			entity.setDeTipoPratica(deTipoPraticaHome.findById(dto.getDeTipoPratica().getId()));
		}

		entity.setFlagIscrittoCpi(dto.getFlagIscrittoCpi());
		if (dto.getComuneIscrizione() != null && dto.getComuneIscrizione().getId() != null) {
			entity.setDeComuneIscrizione(deComuneHome.findById(dto.getComuneIscrizione().getId()));
		}
		if (dto.getDeAtpContrattoUltimoDTO() != null && dto.getDeAtpContrattoUltimoDTO().getId() != null) {
			entity.setDeAtpContrattoUltimo(deAtpContrattoHome.findById(dto.getDeAtpContrattoUltimoDTO().getId()));
		}

		if (dto.getSettoreUltimoContratto() != null && dto.getSettoreUltimoContratto().getId() != null) {
			entity.setDeSettoreUltimoContratto(deAttivitaHome.findById(dto.getSettoreUltimoContratto().getId()));
		}
		entity.setDtFineUltimoContratto(dto.getDtFineUltimoContratto());

		if (dto.getDeProvincia() != null && dto.getDeProvincia().getDescrizione() != null) {
			entity.setDeProvincia(deProvinciaHome.findByDenominazione(dto.getDeProvincia().getDescrizione()));
		}

		if (dto.getDeRegione() != null && dto.getDeRegione().getId() != null) {
			entity.setDeRegione(deRegioneHome.findById(dto.getDeRegione().getId()));
		}
		entity.setFlgSeparataInps(dto.getFlgSeparataInps());

		return entity;
	}

	public List<MsgMessaggioAtipicoMiniDTO> findPraticheAtipici(Integer idPfPrincipal) {
		return findPraticheAtipici(idPfPrincipal, null);
	}

	public Long findPraticheAtipiciCount(Integer idPfPrincipal) {
		return findPraticheAtipiciCount(idPfPrincipal, null);
	}

	public List<MsgMessaggioAtipicoMiniDTO> findPraticheAtipici(Integer idPfPrincipal, String codTipoQuesito) {
		return findPraticheAtipici(idPfPrincipal, codTipoQuesito, 0, 0);
	}

	public List<MsgMessaggioAtipicoMiniDTO> findPraticheAtipici(Integer idPfPrincipal, int startResultsFrom,
			int maxResults) {
		return findPraticheAtipici(idPfPrincipal, null, startResultsFrom, maxResults);
	}

	/**
	 * Questo metodo costruisce la stringa da usare per le query findPraticheAtipici e findPraticheAtipiciCount.
	 */
	private String findPraticheNativeString(Integer idPfPrincipal, String codTipoQuesito, boolean count) {
		// Creo la clausola "select" (variabile, a seconda se devo fare una
		// count o no)
		String nativeQueryStr = "";
		if (count)
			nativeQueryStr += "select count(*) ";
		else
			nativeQueryStr += "select p.id_msg_messaggio,p2.oggetto,p2.corpo,p.cod_stato_pratica, sp.descrizione as desc_sp,"
					+ "p2.dtm_ins, p.dt_inoltro_coord,p.dt_inoltro_cpi,p.dt_risposta,"
					+ "tc.cod_tipo_consulenza,tc.descrizione as desc_tc, tp.cod_tipo_pratica, tp.descrizione as desc_tp, "
					+ "p.nome,p.cognome, p.targa_cpi_incaricato,(select count(msgmessagg0_.id_msg_messaggio) as col_0_0_ "
					+ " from msg_messaggio msgmessagg0_ "
					+ "where (msgmessagg0_.id_msg_messaggio not in "
					+ "(select msgmessagg4_.id_msg_messaggio "
					+ "from msg_messaggio_letto msgmessagg3_ "
					+ "inner join msg_messaggio msgmessagg4_ on msgmessagg3_.id_msg_messaggio=msgmessagg4_.id_msg_messaggio "
					+ "inner join mycas.pf_principal pfprincipa5_ on msgmessagg3_.id_pf_principal=pfprincipa5_.id_pf_principal "
					+ "where pfprincipa5_.id_pf_principal=?)) "
					+ "and cast (msgmessagg0_.ticket as integer)=p.id_msg_messaggio and "
					+ "(msgmessagg0_.id_pf_principal_to=? or msgmessagg0_.cod_provincia_to=? or msgmessagg0_.cod_ruolo_portale_to=?)) as letti  ";

		// Aggiungo le clausole "from" e "where"
		nativeQueryStr += " from atp_messaggio_atipico p "
				+ "join msg_messaggio p2 on (p.id_msg_messaggio = p2.id_msg_messaggio) "
				+ "left join de_stato_pratica sp on (p.cod_stato_pratica = sp.cod_stato_pratica) "
				+ "left join de_tipo_consulenza tc on (p.cod_tipo_consulenza = tc.cod_tipo_consulenza) "
				+ "left join mycas.de_tipo_pratica tp on (p.cod_tipo_pratica = tp.cod_tipo_pratica) "
				+ "where exists ( " + "select distinct cast (ticket as integer) from msg_messaggio m "
				+ "where cast (m.ticket as integer) = p.id_msg_messaggio " + "and m.cod_tipo_messaggio = 'ATIP' ";

		// Aggiungo un pezzo a seconda di chi sta effettuando la query.
		PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);

		if (principal.isConsulente()) {
			/*
			 * Se sono consulente posso vedere le pratiche con messaggi miei o per me (controllando tipo pratica e tipo
			 * consulenza che siano uguali al mio consulente_info) e tutte le pratiche chiuse del mio stesso tipo
			 * consulenza
			 */
			nativeQueryStr += " and ((m.id_pf_principal_from = ? or m.id_pf_principal_to = ? or m.cod_provincia_to = ? or m.cod_ruolo_portale_to = ?) "
					+ " and p.cod_tipo_consulenza = '"
					+ ("A".equals(principal.getConsulenteInfo().getOpzAtipicoPIva()) ? ConstantsSingleton.DeTipoConsulenza.ATIPICO
							: ConstantsSingleton.DeTipoConsulenza.PARTITA_IVA)
					+ "' "
					+ " and p.cod_tipo_pratica = '"
					+ principal.getConsulenteInfo().getDeTipoPratica().getCodTipoPratica()
					+ "') "
					+ "or (p.cod_stato_pratica = 'CHIUSA' and p.cod_tipo_consulenza = '"
					+ ("A".equals(principal.getConsulenteInfo().getOpzAtipicoPIva()) ? ConstantsSingleton.DeTipoConsulenza.ATIPICO
							: ConstantsSingleton.DeTipoConsulenza.PARTITA_IVA) + "')) ";
		} else if (principal.isProvincia()) {
			// Se sono CPI vedo tutte le pratiche mie o indirizzate a me piu'
			// tutte le pratiche chiuse indirizzate anche agli altri CPI
			nativeQueryStr += "and ((m.id_pf_principal_from = ? " + "or m.id_pf_principal_to = ? "
					+ "or m.cod_provincia_to = ? or m.cod_ruolo_portale_to = ?)) "
					+ "or (p.cod_stato_pratica = 'CHIUSA')) ";
		} else {
			// Per gli altri utenti: possono vedere tutte le pratiche con
			// messaggi loro o per loro
			nativeQueryStr += "and (m.id_pf_principal_from = ? " + "or m.id_pf_principal_to = ? "
					+ "or m.cod_provincia_to = ? or m.cod_ruolo_portale_to = ?)) ";
		}

		// Aggiungo un pezzo in base al tipo di questito.
		if (codTipoQuesito != null) {
			nativeQueryStr += " and p.cod_tipo_quesito = ?";
		}

		// Se non sto facendo una count, aggiungo una clausola order by.
		if (!count)
			nativeQueryStr += " order by p.id_msg_messaggio desc";

		return nativeQueryStr;
	}

	/**
	 * Questo metodo effettua una query per trovare una lista di pratiche atipici in base a dei parametri di ricerca.
	 * Risultato paginato.
	 */
	public List<MsgMessaggioAtipicoMiniDTO> findPraticheAtipici(Integer idPfPrincipal, String codTipoQuesito,
			int startResultsFrom, int maxResults) {

		// Creo la query.
		String nativeQueryStr = findPraticheNativeString(idPfPrincipal, codTipoQuesito, false);
		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);

		// Setto i parametri della query.
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		String codProvinciaUtente = "--"; // Valore tampone che non sarà mai uguale a nessun cod_provincia
		if (pfPrincipal.isProvincia()) {
			codProvinciaUtente = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia()
					.getCodProvincia();
		}

		nativeQuery.setParameter(1, idPfPrincipal);
		nativeQuery.setParameter(2, idPfPrincipal);
		nativeQuery.setParameter(3, codProvinciaUtente);
		nativeQuery.setParameter(4, pfPrincipal.getDeRuoloPortale().getCodRuoloPortale());
		nativeQuery.setParameter(5, idPfPrincipal);
		nativeQuery.setParameter(6, idPfPrincipal);
		nativeQuery.setParameter(7, codProvinciaUtente);
		nativeQuery.setParameter(8, pfPrincipal.getDeRuoloPortale().getCodRuoloPortale());
		if (codTipoQuesito != null) {
			nativeQuery.setParameter(9, codTipoQuesito);
		}

		// Setto la paginazione.
		if (startResultsFrom > 0) {
			nativeQuery.setFirstResult(startResultsFrom);
		}

		if (maxResults > 0) {
			nativeQuery.setMaxResults(maxResults);
		}

		// Eseguo la query e trasformo il risultato in un DTO.
		List resultList = nativeQuery.getResultList();
		List<MsgMessaggioAtipicoMiniDTO> lista = new ArrayList<MsgMessaggioAtipicoMiniDTO>();

		for (int i = 0; i < resultList.size(); i++) {
			Object obj = resultList.get(i);
			Object[] objectArray = (Object[]) obj;
			MsgMessaggioAtipicoMiniDTO meggaggioDto = new MsgMessaggioAtipicoMiniDTO((Integer) objectArray[0],
					(String) objectArray[1], (String) objectArray[2], (String) objectArray[3], (String) objectArray[4],
					(Date) objectArray[5], (Date) objectArray[6], (Date) objectArray[7], (Date) objectArray[8],
					(String) objectArray[9], (String) objectArray[10], (String) objectArray[11],
					(String) objectArray[12], (String) objectArray[13], (String) objectArray[14],
					(String) objectArray[15]);

			meggaggioDto.setDaLeggere(((BigInteger) objectArray[16]).longValue() > 0 ? Boolean.TRUE : Boolean.FALSE);

			lista.add(meggaggioDto);
		}

		return lista;
	}

	/**
	 * Questo metodo fa una query per contare il numero totale di pratiche atipici che corrispondono a dei parametri di
	 * ricerca. Serve per la paginazione.
	 */
	public Long findPraticheAtipiciCount(Integer idPfPrincipal, String codTipoQuesito) {
		// Creo la query.
		String nativeQueryStr = findPraticheNativeString(idPfPrincipal, codTipoQuesito, true);
		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);

		// Setto i parametri della query.
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		String codProvinciaUtente = "--"; // Valore tampone che non sarà mai uguale a nessun cod_provincia
		if (pfPrincipal.isProvincia()) {
			codProvinciaUtente = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia()
					.getCodProvincia();
		}

		nativeQuery.setParameter(1, idPfPrincipal);
		nativeQuery.setParameter(2, idPfPrincipal);
		nativeQuery.setParameter(3, codProvinciaUtente);
		nativeQuery.setParameter(4, pfPrincipal.getDeRuoloPortale().getCodRuoloPortale());
		if (codTipoQuesito != null) {
			nativeQuery.setParameter(5, codTipoQuesito);
		}

		// Eseguo la query e restituisco il risultato.
		BigInteger result = (BigInteger) nativeQuery.getSingleResult();
		return result.longValue();
	}

	public List<MsgMessaggioAtipicoMiniDTO> findPraticheCittadino(Integer idPfPrincipal) {

		// MARCO
		List<MsgMessaggioAtipicoMiniDTO> lista = new ArrayList<MsgMessaggioAtipicoMiniDTO>();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MsgMessaggioAtipicoMiniDTO> query = cb.createQuery(MsgMessaggioAtipicoMiniDTO.class);

		Root<MsgMessaggioAtipico> root = query.from(MsgMessaggioAtipico.class);
		Join<MsgMessaggioAtipico, MsgMessaggio> pratica = root.join(MsgMessaggioAtipico_.msgMessaggio);
		Join<MsgMessaggio, PfPrincipal> principalFrom = pratica.join(MsgMessaggio_.pfPrincipalFrom, JoinType.LEFT);
		Join<MsgMessaggio, PfPrincipal> principalTo = pratica.join(MsgMessaggio_.pfPrincipalTo, JoinType.LEFT);
		Join<MsgMessaggioAtipico, DeStatoPratica> statoPratica = root.join(MsgMessaggioAtipico_.deStatoPratica);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// Cerco eventuale provincia utente
		Predicate provinciaToCondition = cb.or(); // Sempre falsa di default.
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		if (pfPrincipal.isProvincia()) {
			DeProvincia deProvinciaUtente = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next()
					.getDeProvincia();
			provinciaToCondition = cb.equal(pratica.get(MsgMessaggio_.deProvinciaTo), deProvinciaUtente);
		}

		// Condizione "il messaggio è diretto a me" (idPfPrincipalTo, deProvinciaTo)
		Predicate p = cb.or(cb.equal(principalFrom.get(PfPrincipal_.idPfPrincipal), idPfPrincipal),
				cb.equal(principalTo.get(PfPrincipal_.idPfPrincipal), idPfPrincipal), provinciaToCondition,
				cb.equal(pratica.get(MsgMessaggio_.deRuoloPortaleTo), pfPrincipal.getDeRuoloPortale()));
		whereConditions.add(p);

		// lista messaggi letti
		Subquery<Integer> listaLetti = listaLetti(query);

		Subquery<Long> messDaLeggere = query.subquery(Long.class);
		Root<MsgMessaggio> messaggi = messDaLeggere.from(MsgMessaggio.class);
		messDaLeggere.select(cb.count(messaggi.get(MsgMessaggio_.idMsgMessaggio)));
		messDaLeggere.where(cb.not(messaggi.in(listaLetti)),
				cb.equal(messaggi.get(MsgMessaggio_.ticket), pratica.get(MsgMessaggio_.ticket)));

		Predicate p2 = cb.greaterThan(messDaLeggere, new Long(0));

		whereConditions.add(p2);

		query.select(cb.construct(MsgMessaggioAtipicoMiniDTO.class, root.get(MsgMessaggioAtipico_.idMsgMessaggio),
				pratica.get(MsgMessaggio_.ticket), pratica.get(MsgMessaggio_.oggetto),
				statoPratica.get(DeStatoPratica_.descrizione), pratica.get(MsgMessaggio_.dtmIns), cb.literal("TRUE")));

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		lista = entityManager.createQuery(query).getResultList();

		// Query da leggere

		query.select(cb.construct(MsgMessaggioAtipicoMiniDTO.class, root.get(MsgMessaggioAtipico_.idMsgMessaggio),
				pratica.get(MsgMessaggio_.ticket), pratica.get(MsgMessaggio_.oggetto),
				statoPratica.get(DeStatoPratica_.descrizione), pratica.get(MsgMessaggio_.dtmIns), cb.literal("FALSE")));

		whereConditions.clear();
		whereConditions.add(p);
		p2 = cb.equal(messDaLeggere, new Long(0));

		whereConditions.add(p2);
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		lista.addAll(entityManager.createQuery(query).getResultList());

		return lista;

	}

	/**
	 * @param query
	 * @return
	 */
	private Subquery<Integer> listaLetti(CriteriaQuery<MsgMessaggioAtipicoMiniDTO> query) {
		Subquery<Integer> listaLetti = query.subquery(Integer.class);
		Root<MsgMessaggioLetto> msgLetti = listaLetti.from(MsgMessaggioLetto.class);
		Join<MsgMessaggioLetto, MsgMessaggio> letti = msgLetti.join(MsgMessaggioLetto_.msgMessaggio);
		listaLetti.select(letti.get(MsgMessaggio_.idMsgMessaggio));
		return listaLetti;
	}

	/**
	 * Metodo utilizzado dal cittadino per inviare la richiesta di una nuova pratica ai coordinatori
	 * 
	 * @param messaggioAtipicoDTO
	 * @param idPrincipalIns
	 * @return
	 */
	public MsgMessaggioAtipicoDTO invioDomanda(MsgMessaggioAtipicoDTO messaggioAtipicoDTO, Integer idPrincipalIns) {

		setDtmPrincipal(messaggioAtipicoDTO, idPrincipalIns, idPrincipalIns);

		messaggioAtipicoDTO.setCodTipoMessaggio(ConstantsSingleton.TipoMessaggio.ATIPICI);
		messaggioAtipicoDTO.setIdFrom(idPrincipalIns);
		messaggioAtipicoDTO.setDeStatoPratica(deStatoPraticaHome.findDTOById(ConstantsSingleton.DeStatoPratica.NUOVA));
		messaggioAtipicoDTO.setDeTipoQuesito(messaggioAtipicoDTO.getDeTipoQuesito());

		MsgMessaggio messaggio = msgMessaggioHome.fromDTO(messaggioAtipicoDTO);

		msgMessaggioHome.persist(messaggio);

		messaggio.setTicket(messaggio.getIdMsgMessaggio().toString());

		DeRuoloPortale ruoloCoordinatore = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.COORDINA);
		messaggio.setDeRuoloPortaleTo(ruoloCoordinatore);
		messaggio.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.TipoMessaggio.ATIPICI));
		messaggio.setOggetto(messaggioAtipicoDTO.getOggetto());
		messaggio.setCorpo(messaggioAtipicoDTO.getCorpo());

		msgMessaggioHome.merge(messaggio);

		MsgMessaggioAtipico pratica = fromDTO(messaggioAtipicoDTO);
		pratica.setMsgMessaggio(messaggio);

		String allegatoCvNameTmp = messaggioAtipicoDTO.getAllegatoCvNameTmp();

		if (StringUtils.isNotBlank(messaggioAtipicoDTO.getAllegatoCvName())
				&& StringUtils.isNotBlank(allegatoCvNameTmp)) {
			try {
				byte[] contenuto;
				contenuto = Utils.fileToByte(ConstantsSingleton.TMP_DIR + File.separator + allegatoCvNameTmp);
				MsgAllegatoDTO msgAllegato = new MsgAllegatoDTO();
				msgAllegato.setFilename(messaggioAtipicoDTO.getAllegatoCvName());
				msgAllegato.setContenuto(contenuto);
				msgAllegato.setIdMsgMessaggio(messaggio.getIdMsgMessaggio());
				msgAllegatoHome.persistDTO(msgAllegato, idPrincipalIns);

			} catch (Exception e) {
				throw new MyPortalException("messaggio.allegato.error", e);
			}
		}

		String allegatoFileNameTmp = messaggioAtipicoDTO.getAllegatoFileNameTmp();

		if (StringUtils.isNotBlank(messaggioAtipicoDTO.getAllegatoFileName())
				&& StringUtils.isNotBlank(allegatoFileNameTmp)) {
			try {
				byte[] contenuto;
				contenuto = Utils.fileToByte(ConstantsSingleton.TMP_DIR + File.separator + allegatoFileNameTmp);
				MsgAllegatoDTO msgAllegato = new MsgAllegatoDTO();
				msgAllegato.setFilename(messaggioAtipicoDTO.getAllegatoFileName());
				msgAllegato.setContenuto(contenuto);
				msgAllegato.setIdMsgMessaggio(messaggio.getIdMsgMessaggio());
				msgAllegatoHome.persistDTO(msgAllegato, idPrincipalIns);

			} catch (Exception e) {
				throw new MyPortalException("messaggio.allegato.error", e);
			}
		}

		persist(pratica);

		List<PfPrincipal> listaCoordinatori = pfPrincipalHome.findCoordinatori();

		EmailDTO registrationEmail = EmailDTO.buildEmailCoordinatore(pratica, listaCoordinatori);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);

		return toDTO(pratica);

	}

	/**
	 * Metodo utilizzato dal coordinatore per inoltrare la pratica al CPI.
	 * 
	 * @param praticaDaInoltrare
	 *            Un DTO che rappresenta la pratica da inoltrare.
	 * @param idTicket
	 *            L'ID del ticket a cui appartiene la pratica.
	 * @param idPfPrincipal
	 *            L'ID del coordinatore che sta eseguendo l'operazione.
	 * @return
	 */
	public MsgMessaggioAtipicoDTO inoltraCPI(MsgMessaggioAtipicoDTO praticaDaInoltrare, Integer idTicket,
			Integer idPfPrincipal) {

		// Setto data di inserimento e modifica del nuovo messaggio, e creo
		// l'entità.
		setDtmPrincipal(praticaDaInoltrare, idPfPrincipal, idPfPrincipal);
		MsgMessaggio inoltrante = msgMessaggioHome.findById(idTicket);
		MsgMessaggio daInoltrare = msgMessaggioHome.fromDTO(praticaDaInoltrare);

		daInoltrare.setTicket(inoltrante.getTicket());
		daInoltrare.setOggetto(inoltrante.getOggetto());
		daInoltrare.setCorpo(inoltrante.getCorpo());
		daInoltrare.setPfPrincipalFrom(pfPrincipalHome.findById(idPfPrincipal));
		daInoltrare.setPfPrincipalTo(null);
		daInoltrare.setDtScadenza(null);

		DeProvincia provincia = deProvinciaHome.findById(praticaDaInoltrare.getDeProvincia().getId());
		daInoltrare.setDeProvinciaTo(provincia);
		daInoltrare.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.TipoMessaggio.ATIPICI));
		daInoltrare.setDeTemaConsulenza(null);
		daInoltrare.setPrecedente(null);
		daInoltrare.setInoltrante(inoltrante);

		msgMessaggioHome.persist(daInoltrare);

		// Trovo la pratica a cui appartiene il messaggio che sto inoltrando, e
		// la aggiorno.
		MsgMessaggioAtipico pratica = findById(idTicket);
		Date data = new Date();
		pratica.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.INOL_CPI));
		pratica.setDeProvincia(provincia);
		pratica.setDtmMod(data);
		if (pratica.getDtInoltroCoord() == null)
			pratica.setDtInoltroCoord(data);
		pratica.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));
		pratica.setTargaCPIincaricato(provincia.getTarga());

		merge(pratica);
		entityManager.flush();
		entityManager.refresh(pratica);

		// MARCO: Mando una mail agli utenti coinvolti nella pratica.
		MsgMessaggio inoltrato = pratica.getMsgMessaggio().getInoltrati().get(0);
		List<PfPrincipal> listaUtenti = null;

		DeProvincia provinciaInoltrati = inoltrato.getDeProvinciaTo();
		DeRuoloPortale ruoloPortaleInoltrati = inoltrato.getDeRuoloPortaleTo();
		if (provinciaInoltrati != null) {
			listaUtenti = pfPrincipalHome.findPrincipalsProvinceByCodProvincia(provinciaInoltrati.getCodProvincia());
		} else if (ruoloPortaleInoltrati != null) {
			listaUtenti = pfPrincipalHome.findPrincipalsByCodRuoloPortale(ruoloPortaleInoltrati.getCodRuoloPortale());
		}

		if (listaUtenti != null) {
			EmailDTO cpiEmail = EmailDTO.buildEmailCPI(pratica, listaUtenti);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, cpiEmail);

			if (pratica.getMsgMessaggio().getInoltrati().size() <= 1) {
				EmailDTO cittEmail = EmailDTO.buildEmailConfermaCittadino(pratica);
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, cittEmail);
			}
		}

		return toDTO(pratica);
	}

	/**
	 * Metodo utilizzado dal coordinatore per archiviare la pratica (negare il consenso all'inoltro) e opzionalmente
	 * rispondere al cittadino.
	 * 
	 * @param messaggioAtipicoDTO
	 * @param rispondiAlCittadino
	 * @param idPfPrincipal
	 * @return
	 */
	public void archivia(MsgMessaggioAtipicoDTO messaggioAtipicoDTO, Integer ticket, Boolean rispondiAlCittadino,
			Integer idPfPrincipal) {

		setDtmPrincipal(messaggioAtipicoDTO, null, idPfPrincipal);

		MsgMessaggioAtipico daArchiviare = findById(ticket);
		daArchiviare.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.RIFIUTO));
		daArchiviare.setMotivoRifiuto(messaggioAtipicoDTO.getMotivoRifiuto());

		merge(daArchiviare);

		if (rispondiAlCittadino) {

			MsgMessaggio domanda = daArchiviare.getMsgMessaggio();
			MsgMessaggio risposta = new MsgMessaggio();
			PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);
			risposta.setTicket(domanda.getTicket());
			risposta.setOggetto(domanda.getOggetto());
			risposta.setCorpo(messaggioAtipicoDTO.getCorpo());
			risposta.setPfPrincipalFrom(principal);
			risposta.setPfPrincipalTo(domanda.getPfPrincipalFrom());
			risposta.setDeProvinciaTo(null);
			risposta.setDeRuoloPortaleTo(null);
			risposta.setDtScadenza(null);
			risposta.setDeTipoMessaggio(domanda.getDeTipoMessaggio());
			risposta.setPrecedente(domanda);
			risposta.setDeTemaConsulenza(null);
			Date data = new Date();
			risposta.setDtmIns(data);
			risposta.setDtmMod(data);
			risposta.setPfPrincipalIns(principal);
			risposta.setPfPrincipalMod(principal);

			msgMessaggioHome.persist(risposta);

			daArchiviare.setDtRisposta(data);
			merge(daArchiviare);

			EmailDTO rispostaEmail = EmailDTO.buildEmailRifiutoCittadino(daArchiviare);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, rispostaEmail);
		}

	}

	/**
	 * Metodo utilizzato dal CPI per inoltrare la pratica al consulente
	 * 
	 * @param messaggioAtipicoDTO
	 * @param idPfPrincipal
	 * @return
	 */
	public MsgMessaggioAtipicoDTO inoltraConsulente(MsgMessaggioAtipicoDTO praticaDaInoltrare,
			Integer idMsgMessaggioRicevuto, Integer idPfPrincipal, String idTipoPratica, boolean isRifiuto) {
		setDtmPrincipal(praticaDaInoltrare, idPfPrincipal, idPfPrincipal);

		MsgMessaggio ricevuto = msgMessaggioHome.findById(idMsgMessaggioRicevuto);
		MsgMessaggioDTO daInoltrare = praticaDaInoltrare;
		DeTipoPratica tipoPratica = deTipoPraticaHome.findById(idTipoPratica);

		daInoltrare.setTicket(ricevuto.getTicket());
		daInoltrare.setOggetto(ricevuto.getOggetto());
		daInoltrare.setCorpo(praticaDaInoltrare.getCorpo());
		daInoltrare.setIdFrom(idPfPrincipal);
		DeRuoloPortaleDTO ruoloConsulente = deRuoloPortaleHome.findDTOById(ConstantsSingleton.DeRuoloPortale.CONSULEN);
		daInoltrare.setRuoloPortaleTo(ruoloConsulente);
		daInoltrare.setCodTipoMessaggio(ConstantsSingleton.TipoMessaggio.ATIPICI);
		daInoltrare.setIdMsgMessaggioInoltrante(ricevuto.getIdMsgMessaggio());

		msgMessaggioHome.persistDTO(daInoltrare, idPfPrincipal);

		MsgMessaggioAtipico pratica = findById(Integer.parseInt(ricevuto.getTicket()));

		pratica.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.INOL_CON));
		Date data = new Date();
		pratica.setDtmMod(data);
		if (pratica.getDtInoltroCPI() == null)
			pratica.setDtInoltroCPI(data);
		pratica.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));
		pratica.setDeTipoPratica(tipoPratica);
		merge(pratica);

		List<PfPrincipal> listaConsulenti = pfPrincipalHome.findConsulenti();

		String provinciaCPI = "";
		DeProvinciaDTO provCpi = deProvinciaHome.findDTOBytarga(pratica.getTargaCPIincaricato());
		if (provCpi != null) {
			provinciaCPI = provCpi.getDescrizione();
		}

		EmailDTO consulentiEmail = null;
		if (isRifiuto) {
			String denominazioneProvincia = deProvinciaHome.findDTOBytarga(pratica.getTargaCPIincaricato())
					.getDescrizione();
			consulentiEmail = EmailDTO.buildEmailRifiutaRispostaCPItoConsulente(pratica, listaConsulenti,
					denominazioneProvincia);
		} else {
			consulentiEmail = EmailDTO.buildEmailCPItoConsulente(pratica, listaConsulenti, provinciaCPI);
		}
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, consulentiEmail);

		return toDTO(pratica);
	}

	/**
	 * Metodo utilizzato dal CPI per rispondere al cittadino
	 * 
	 * @param messaggioAtipicoDTO
	 * @param idPfPrincipal
	 * @return
	 */
	public MsgMessaggioAtipicoDTO rispondiAlCittadino(MsgMessaggioAtipicoDTO messaggioAtipicoDTO, Integer idPfPrincipal) {

		MsgMessaggioAtipico pratica = findById(messaggioAtipicoDTO.getId());
		Date data = new Date();
		pratica.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.CHIUSA));
		pratica.setDtmMod(data);
		pratica.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));

		if (messaggioAtipicoDTO.getDeTipoPratica() != null && messaggioAtipicoDTO.getDeTipoPratica().getId() != null) {
			DeTipoPratica tipoPratica = deTipoPraticaHome.findById(messaggioAtipicoDTO.getDeTipoPratica().getId());
			pratica.setDeTipoPratica(tipoPratica);
		}

		if (pratica.getDtRisposta() == null)
			pratica.setDtRisposta(data);
		merge(pratica);

		PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);
		MsgMessaggioDTO risposta = new MsgMessaggioDTO();
		risposta.setCodTipoMessaggio(ConstantsSingleton.TipoMessaggio.ATIPICI);
		risposta.setTicket(pratica.getIdMsgMessaggio().toString());
		risposta.setOggetto(messaggioAtipicoDTO.getOggetto());
		risposta.setCorpo(messaggioAtipicoDTO.getCorpo());
		risposta.setIdFrom(idPfPrincipal);
		risposta.setIdTo(pratica.getMsgMessaggio().getPfPrincipalFrom().getIdPfPrincipal());
		risposta.setIdMsgMessaggioPrecedente(pratica.getMsgMessaggio().getIdMsgMessaggio());

		risposta.setAllegatoFileName(messaggioAtipicoDTO.getAllegatoFileName());
		risposta.setAllegatoFileNameTmp(messaggioAtipicoDTO.getAllegatoFileNameTmp());
		risposta.setDtmIns(data);
		risposta.setDtmMod(data);
		risposta.setIdPrincipalIns(idPfPrincipal);
		risposta.setIdPrincipalMod(idPfPrincipal);
		msgMessaggioHome.persistDTO(risposta, idPfPrincipal);

		EmailDTO rispostaEmail = EmailDTO.buildEmailRispostaCittadino(pratica, principal
				.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia().getDenominazione());
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, rispostaEmail);

		return toDTO(pratica);
	}

	/**
	 * Metodo utilizzato dal CPI per notificare al coordinatore che ha inoltrato il messaggio che è stata inviata la
	 * risposta al cittadino
	 * 
	 * @param idCoordinatore
	 * @param messaggioAtipicoDTO
	 * @param idPfPrincipal
	 * @return
	 */
	public MsgMessaggioAtipicoDTO inviaCopiaAlCoordinatore(Integer idCoordinatore,
			MsgMessaggioAtipicoDTO messaggioAtipicoDTO, Integer idPfPrincipal) {

		MsgMessaggioAtipico pratica = findById(messaggioAtipicoDTO.getId());
		Date data = new Date();
		pratica.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.CHIUSA));
		pratica.setDtmMod(data);
		pratica.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));

		if (messaggioAtipicoDTO.getDeTipoPratica() != null && messaggioAtipicoDTO.getDeTipoPratica().getId() != null) {
			DeTipoPratica tipoPratica = deTipoPraticaHome.findById(messaggioAtipicoDTO.getDeTipoPratica().getId());
			pratica.setDeTipoPratica(tipoPratica);
		}

		if (pratica.getDtRisposta() == null)
			pratica.setDtRisposta(data);
		merge(pratica);

		PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);
		MsgMessaggioDTO risposta = new MsgMessaggioDTO();
		risposta.setCodTipoMessaggio(ConstantsSingleton.TipoMessaggio.ATIPICI);
		risposta.setTicket(messaggioAtipicoDTO.getTicket());
		risposta.setOggetto(messaggioAtipicoDTO.getOggetto());
		risposta.setCorpo(messaggioAtipicoDTO.getCorpo());
		risposta.setIdFrom(idPfPrincipal);
		risposta.setIdTo(idCoordinatore);
		risposta.setIdMsgMessaggioPrecedente(messaggioAtipicoDTO.getIdMsgMessaggioPrecedente());

		risposta.setAllegatoFileName(messaggioAtipicoDTO.getAllegatoFileName());
		risposta.setAllegatoFileNameTmp(messaggioAtipicoDTO.getAllegatoFileNameTmp());
		risposta.setDtmIns(data);
		risposta.setDtmMod(data);
		risposta.setIdPrincipalIns(idPfPrincipal);
		risposta.setIdPrincipalMod(idPfPrincipal);
		msgMessaggioHome.persistDTO(risposta, idPfPrincipal);

		EmailDTO rispostaEmail = EmailDTO.buildEmailRispostaCoordinatore(pratica, principal
				.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia().getDenominazione());
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, rispostaEmail);

		return toDTO(pratica);
	}

	/**
	 * Metodo utilizzato dal consulente per rispondere al CPI
	 * 
	 * @param praticaDTO
	 * @param accettato
	 * @param idPfPrincipal
	 * @return
	 */
	public MsgMessaggioAtipicoDTO rispondiAlCPI(MsgMessaggioAtipicoDTO praticaDTO, Boolean accettato,
			Integer idPfPrincipal) {
		MsgMessaggioAtipico pratica = findById(praticaDTO.getId());
		if (accettato) {
			pratica.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.CON_RISP));
		} else {
			pratica.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.CON_RIF));
		}
		pratica.setDtmMod(new Date());
		pratica.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));

		merge(pratica);

		praticaDTO.setId(null);

		PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);
		MsgMessaggioDTO risposta = new MsgMessaggioDTO();
		risposta.setTicket(praticaDTO.getTicket());
		risposta.setOggetto(praticaDTO.getOggetto());
		risposta.setCorpo(praticaDTO.getCorpo());
		risposta.setIdFrom(idPfPrincipal);
		MsgMessaggio fromCoordToCpi = pratica.getMsgMessaggio().getInoltrati().get(0);
		if (fromCoordToCpi.getInoltrati() != null && fromCoordToCpi.getInoltrati().size() > 0) {
			MsgMessaggio fromCpiToCons = fromCoordToCpi.getInoltrati().get(fromCoordToCpi.getInoltrati().size() - 1);
			risposta.setIdMsgMessaggioPrecedente(fromCpiToCons.getIdMsgMessaggio());
		}
		risposta.setProvinciaTo(deProvinciaHome.toDTO(fromCoordToCpi.getDeProvinciaTo()));
		risposta.setRuoloPortaleTo(deRuoloPortaleHome.toDTO(fromCoordToCpi.getDeRuoloPortaleTo()));
		risposta.setCodTipoMessaggio(praticaDTO.getCodTipoMessaggio());
		risposta.setAllegatoFileName(praticaDTO.getAllegatoFileName());
		risposta.setAllegatoFileNameTmp(praticaDTO.getAllegatoFileNameTmp());
		Date data = new Date();
		risposta.setDtmIns(data);
		risposta.setDtmMod(data);
		risposta.setTicket(pratica.getIdMsgMessaggio().toString());
		risposta.setIdPrincipalIns(idPfPrincipal);
		risposta.setIdPrincipalMod(idPfPrincipal);

		risposta = msgMessaggioHome.persistDTO(risposta, idPfPrincipal);

		AtpConsulenza consulenza = new AtpConsulenza();
		MsgMessaggio temp = msgMessaggioHome.findById(risposta.getId());
		consulenza.setMsgMessaggio(temp);
		consulenza.setMsgMessaggioAtipico(pratica);
		consulenza.setMinuti(praticaDTO.getMinuti());
		consulenza.setNote(praticaDTO.getNote());
		consulenza.setDtmIns(data);
		consulenza.setDtmMod(data);
		consulenza.setPfPrincipalIns(principal);
		consulenza.setPfPrincipalMod(principal);

		atpConsulenzaHome.persist(consulenza);

		// Parte in cui mando la mail a tutte le provincie di quel gruppo
		List<PfPrincipal> listaUtenti = null;

		if (temp.getDeProvinciaTo() != null) {
			listaUtenti = pfPrincipalHome.findPrincipalsProvinceByCodProvincia(temp.getDeProvinciaTo()
					.getCodProvincia());
		} else if (temp.getDeRuoloPortaleTo() != null) {
			listaUtenti = pfPrincipalHome.findPrincipalsByCodRuoloPortale(temp.getDeRuoloPortaleTo()
					.getCodRuoloPortale());
		}

		if (listaUtenti != null) {
			if (accettato) {
				EmailDTO rispostaEmail = EmailDTO.buildEmailConsulenteToCPI(pratica, temp, listaUtenti);
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, rispostaEmail);
			} else {
				EmailDTO rispostaEmail = EmailDTO.buildEmailRifiutoConsulenteToCPI(pratica, temp, listaUtenti);
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, rispostaEmail);
			}
		}

		return toDTO(pratica);

	}

	/**
	 * Questo metodo setta la data di inserimento/modfica di un DTO che rappresenta un messaggio atipico.
	 * 
	 * @param pratica
	 *            Il DTO in cui inserire la data.
	 * @param idPrincipalIns
	 *            L'ID di chi sta effettuando l'inserimento (null se è una modifica).
	 * @param idPrincipalMod
	 *            L'ID di chi sta effettuando la modifica (uguale a idPrincipalIns se è un inserimento).
	 */
	private void setDtmPrincipal(MsgMessaggioAtipicoDTO pratica, Integer idPrincipalIns, Integer idPrincipalMod) {
		Date data = new Date();
		// Se è un inserimento, setto la data di inserimento del DTO.
		if (idPrincipalIns != null) {
			pratica.setIdPrincipalIns(idPrincipalIns);
			pratica.setDtmIns(data);
		}
		// In ogni caso, setto la data di modifica del DTO.
		pratica.setIdPrincipalMod(idPrincipalMod);
		pratica.setDtmMod(data);
	}

	/**
	 * Questo metodo setta la data di inserimento/modfica di un DTO che rappresenta un messaggio atipico.
	 * 
	 * @param pratica
	 *            Il DTO in cui inserire la data.
	 * @param principalIns
	 *            Il PfPrincipal che sta effettuando l'inserimento (null se è una modifica).
	 * @param principalMod
	 *            Il PfPrincipal che sta effettuando la modifica (uguale a principalIns se è un inserimento).
	 */
	private void setDtmPrincipal(MsgMessaggio messaggio, PfPrincipal principalIns, PfPrincipal principalMod) {
		Date data = new Date();
		// Se è un inserimento, setto la data di inserimento nel DTO.
		if (principalIns != null) {
			messaggio.setPfPrincipalIns(principalIns);
			messaggio.setDtmIns(data);
		}
		// In ogni caso, setto la data di modifica nel DTO.
		messaggio.setPfPrincipalMod(principalMod);
		messaggio.setDtmMod(data);
	}

	public MsgMessaggioAtipicoDTO inoltraRispostaAlCittadino(Integer ticket, Integer idPfPrincipal,
			MsgMessaggioAtipicoDTO integrazione) {

		MsgMessaggioAtipico pratica = findById(ticket);
		List<MsgMessaggio> listaInoltrati = pratica.getMsgMessaggio().getInoltrati().iterator().next().getInoltrati();
		MsgMessaggio ultimoInoltrato = listaInoltrati.get(listaInoltrati.size() - 1);
		MsgMessaggio ricevuto = ultimoInoltrato.getSuccessivo();

		pratica.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.CHIUSA));
		if (pratica.getDtRisposta() == null)
			pratica.setDtRisposta(new Date());

		merge(pratica);

		PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);
		MsgMessaggioDTO rispostaCittadino = new MsgMessaggioDTO();
		rispostaCittadino.setAllegatoFileName(integrazione.getAllegatoFileName());
		rispostaCittadino.setAllegatoFileNameTmp(integrazione.getAllegatoFileNameTmp());
		rispostaCittadino.setTicket(ticket.toString());
		rispostaCittadino.setIdFrom(idPfPrincipal);
		rispostaCittadino.setIdTo(pratica.getMsgMessaggio().getPfPrincipalFrom().getIdPfPrincipal());
		rispostaCittadino.setCodTipoMessaggio(ConstantsSingleton.TipoMessaggio.ATIPICI);
		rispostaCittadino.setCorpo(integrazione.getCorpo());
		rispostaCittadino.setOggetto(ricevuto.getOggetto());
		rispostaCittadino.setIdMsgMessaggioPrecedente(pratica.getMsgMessaggio().getIdMsgMessaggio());
		rispostaCittadino.setIdMsgMessaggioInoltrante(ricevuto.getIdMsgMessaggio());

		msgMessaggioHome.persistDTO(rispostaCittadino, idPfPrincipal);

		EmailDTO rispostaEmail = EmailDTO.buildEmailRispostaCittadino(pratica, principal
				.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia().getDenominazione());
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, rispostaEmail);

		return toDTO(pratica);

	}

	/**
	 * Metodo utilizzato dal CPI per rifiutare una pratica al Coordinatore
	 * 
	 * @param ticket
	 * @param idPfPrincipal
	 * @return
	 */
	public MsgMessaggioAtipicoDTO rifiutaPratica(MsgMessaggioAtipicoDTO rispostaForm, Integer ticket,
			Integer idPfPrincipal) {

		// MARCO
		MsgMessaggioAtipico pratica = findById(ticket);
		PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);
		DeProvincia provincia = principal.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia();

		List<MsgMessaggio> listaInoltrati = pratica.getMsgMessaggio().getInoltrati(provincia);
		MsgMessaggio ultimoInoltrato = listaInoltrati.get(listaInoltrati.size() - 1);

		pratica.setDeStatoPratica(deStatoPraticaHome.findById(ConstantsSingleton.DeStatoPratica.CPI_RIF));
		merge(pratica);

		MsgMessaggio risposta = new MsgMessaggio();
		risposta.setTicket(ticket.toString());
		risposta.setPfPrincipalFrom(principal);
		risposta.setPfPrincipalTo(ultimoInoltrato.getPfPrincipalFrom());
		risposta.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.TipoMessaggio.ATIPICI));

		risposta.setOggetto(ultimoInoltrato.getOggetto());
		risposta.setCorpo(rispostaForm.getCorpo());
		risposta.setPrecedente(ultimoInoltrato);
		setDtmPrincipal(risposta, principal, principal);

		msgMessaggioHome.persist(risposta);

		EmailDTO rispostaEmail = EmailDTO.buildEmailRifiutoCoordinatore(pratica);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, rispostaEmail);
		return toDTO(pratica);

	}

	public List<MsgMessaggioAtipicoDTO> findDTObyIdPfPrincipal(Integer idPfPrincipal) {
		List<MsgMessaggioAtipicoDTO> listaDto = new ArrayList<MsgMessaggioAtipicoDTO>();
		List<MsgMessaggioAtipico> listaPratiche = entityManager
				.createNamedQuery("findPraticaByIdPfPrincipal", MsgMessaggioAtipico.class)
				.setParameter("idPfPrincipal", idPfPrincipal).getResultList();

		if (listaPratiche == null || listaPratiche.isEmpty())
			return null;

		for (MsgMessaggioAtipico m : listaPratiche) {
			listaDto.add(toDTO(m));
		}
		return listaDto;

	}

	public List<MsgMessaggioAtipico> findbyIdPfPrincipal(Integer idPfPrincipal) {
		List<MsgMessaggioAtipico> listaPratiche = entityManager
				.createNamedQuery("findPraticaByIdPfPrincipal", MsgMessaggioAtipico.class)
				.setParameter("idPfPrincipal", idPfPrincipal).getResultList();

		if (listaPratiche == null || listaPratiche.isEmpty())
			return null;

		return listaPratiche;
	}

	public DeTitoloDTO findLastTitolo(Integer idPfPrincipal) {
		List<MsgMessaggioAtipico> listaPratiche = findbyIdPfPrincipal(idPfPrincipal);
		if (listaPratiche != null && !listaPratiche.isEmpty()) {
			MsgMessaggioAtipico ultima = listaPratiche.get(listaPratiche.size() - 1);
			if (ultima.getDeTitolo() != null && ultima.getDeTitolo().getCodTitolo() != null) {
				return deTitoloHome.toDTO(ultima.getDeTitolo());
			}
		}
		return null;
	}

	public Map<String, Integer> findNumMessaggiNonLetti(Integer pfPrincipalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(pfPrincipalId);
		if (pfPrincipal.isUtente()) {
			return findNumMessaggiNonLettiProvincia(pfPrincipalId);
		} else if (pfPrincipal.isAzienda()) {
			return findNumMessaggiNonLettiProvincia(pfPrincipalId);
		} else if (pfPrincipal.isProvincia()) {
			return findNumMessaggiNonLettiProvincia(pfPrincipalId);
		} else if (pfPrincipal.isCoordinatore()) {
			return findNumMessaggiNonLettiProvincia(pfPrincipalId);
		} else {
			throw new EJBException("Impossibile recuperare il numero di messaggi non letti.");
		}
	}

	/*
	 * private Map<String, Integer> findNumMessaggiNonLettiUtente(Integer pfPrincipalId) { return null; }
	 */

	private Map<String, Integer> findNumMessaggiNonLettiProvincia(Integer pfPrincipalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(pfPrincipalId);
		String codProvinciaUser = "--"; // valore tampone se non è una provincia
		if (pfPrincipal.isProvincia()) {
			codProvinciaUser = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia()
					.getCodProvincia();
		}

		// CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		Query query = entityManager
				.createNativeQuery("select ama.cod_tipo_quesito, count(*) from atp_messaggio_atipico ama where ama.id_msg_messaggio in (select distinct(cast(mm.ticket as integer)) from msg_messaggio mm where (mm.cod_gruppo_to = ? or mm.id_pf_principal_to=?) and mm.id_msg_messaggio not in (select mml.id_msg_messaggio from msg_messaggio_letto mml where mml.id_pf_principal = ?) ) group by ama.cod_tipo_quesito");

		query.setParameter(1, codProvinciaUser);
		query.setParameter(2, pfPrincipalId);
		query.setParameter(3, pfPrincipalId);
		// Root<MsgMessaggioAtipico> messaggio =
		// query.from(MsgMessaggioAtipico.class);
		//
		// List<Predicate> whereConditions = new ArrayList<Predicate>();
		//
		// Join<MsgMessaggioAtipico, DeTipoQuesito> deTipoQuesito =
		// messaggio.join(MsgMessaggioAtipico_.deTipoQuesito);
		//
		// //whereConditions.add(qb.or(conditionGroup, conditionBroadcast,
		// conditionDiretto));
		//
		// query.select(qb.array(deTipoQuesito.get(DeTipoQuesito_.codTipoQuesito),
		// qb.count(messaggio)));
		//
		//
		//
		// // subquery - messaggio
		// Subquery<Integer> subquery = query.subquery(Integer.class);
		// Root<MsgMessaggio> subMessaggio = subquery.from(MsgMessaggio.class);
		// subquery.select(subMessaggio.get(MsgMessaggio_.ticket).as(Integer.class));
		// // fine subquery
		//
		// // subsubquery - messaggio letto
		// Subquery<Integer> subSubquery = subquery.subquery(Integer.class);
		// Root<MsgMessaggioLetto> subSubMessaggioLetto =
		// subquery.from(MsgMessaggioLetto.class);
		// subSubquery.select(subSubMessaggioLetto.get(MsgMessaggioLetto_.msgMessaggio).get(MsgMessaggio_.idMsgMessaggio));
		// subquery.where(qb.equal(subSubMessaggioLetto.get(MsgMessaggioLetto_.pfPrincipal),
		// pfPrincipal));
		// // fine subquery
		//
		// subquery.where(qb.or(qb.equal(subMessaggio.get(MsgMessaggio_.deGruppo),
		// principalGruppo),qb.equal(subMessaggio.get(MsgMessaggio_.pfPrincipalTo),
		// pfPrincipal)),qb.not(subMessaggio.get(MsgMessaggio_.idMsgMessaggio).in(subSubquery)));
		//
		// query.where(messaggio.get(MsgMessaggioAtipico_.idMsgMessaggio).in(subquery));
		//
		// query.groupBy(deTipoQuesito.get(DeTipoQuesito_.codTipoQuesito));
		//
		// TypedQuery<Object[]> q = entityManager.createQuery(query);

		List<Object[]> list = query.getResultList();

		// colpo di genio. ruby insegna, w le classi anonime!
		// costruisco una hashMap che invece di ritornare null, ritorna 0.
		Map<String, Integer> ret = new HashMap<String, Integer>() {
			private static final long serialVersionUID = 147666768119277156L;

			@Override
			public Integer get(Object arg0) {
				Integer res = super.get(arg0);
				return res == null ? 0 : res;
			}
		};

		for (Object[] objects : list) {
			String codice = (String) objects[0];
			BigInteger numero = (BigInteger) objects[1];
			ret.put(codice, numero.intValue());
		}
		return ret;
	}

}
