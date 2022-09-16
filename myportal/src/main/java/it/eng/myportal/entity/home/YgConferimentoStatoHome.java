package it.eng.myportal.entity.home;

import it.eng.myportal.entity.YgConferimentoStato;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeAdesioneEjb;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneMinHome;
import it.eng.myportal.utils.Utils;
import it.gov.lavoro.servizi.servizicoapAdesioneSet.types.holders.Risposta_setStatoAdesioneYG_TypeEsitoHolder;
import it.eng.myportal.dtos.YgConferimentoStatoDTO;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.rpc.holders.StringHolder;

@Stateless
public class YgConferimentoStatoHome extends AbstractUpdatableHome<YgConferimentoStato, YgConferimentoStatoDTO> {

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private DeStatoAdesioneMinHome deStatoAdesioneMinHome;

	@EJB
	private YouthGuaranteeAdesioneEjb youthGuaranteeAdesioneEjb;

	@Override
	public YgConferimentoStato findById(Integer id) {
		return findById(YgConferimentoStato.class, id);
	}

	@Override
	public YgConferimentoStato fromDTO(YgConferimentoStatoDTO dto) {
		YgConferimentoStato entity = super.fromDTO(dto);

		entity.setIdYgConferimentoStato(dto.getId());
		entity.setCodEsito(dto.getCodEsito());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		if (dto.getDeProvinciaDTO() != null) {
			entity.setDeProvincia(deProvinciaHome.findById(dto.getDeProvinciaDTO().getId()));
		}
		if (dto.getDeStatoAdesioneMinDTO() != null) {
			entity.setDeStatoAdesioneMin(deStatoAdesioneMinHome.findById(dto.getDeStatoAdesioneMinDTO().getId()));
		}
		entity.setDtmInvio(dto.getDtmInvio());
		entity.setFlgProcessato(dto.getFlgProcessato());
		entity.setStrMessErrore(dto.getStrMessErrore());
		if (dto.getYgAdesioneDTO() != null) {
			entity.setYgAdesione(ygAdesioneHome.findById(dto.getYgAdesioneDTO().getId()));
		}

		return entity;
	}

	@Override
	public YgConferimentoStatoDTO toDTO(YgConferimentoStato entity) {
		YgConferimentoStatoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdYgConferimentoStato());
		dto.setCodEsito(entity.getCodEsito());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setDeProvinciaDTO(deProvinciaHome.toDTO(entity.getDeProvincia()));
		dto.setDeStatoAdesioneMinDTO(deStatoAdesioneMinHome.toDTO(entity.getDeStatoAdesioneMin()));
		dto.setDtmInvio(entity.getDtmInvio());
		dto.setFlgProcessato(entity.getFlgProcessato());
		dto.setStrMessErrore(entity.getStrMessErrore());
		dto.setYgAdesioneDTO(ygAdesioneHome.toDTO(entity.getYgAdesione()));

		return dto;
	}

	public List<YgConferimentoStato> getAdesioniYgConferimentoIniziale() {
		Query q = entityManager.createQuery(" select yg " + " from YgConferimentoStato yg "
				+ " where yg.flgProcessato is null" + " and yg.ygAdesione is not null ");

		q.setMaxResults(500);
		List<YgConferimentoStato> results = q.getResultList();

		return results;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processBatchConferimentoIniziale(YgConferimentoStato ygConferimentoStato) {
		Risposta_setStatoAdesioneYG_TypeEsitoHolder esitoHolder = new Risposta_setStatoAdesioneYG_TypeEsitoHolder();
		StringHolder messaggioErroreHolder = new StringHolder();
		Integer idPfPrincipalAdmin = 0;

		String codiceFiscale = null;
		if (ygConferimentoStato.getCodiceFiscale() != null) {
			codiceFiscale = ygConferimentoStato.getCodiceFiscale();
		}
		XMLGregorianCalendar dataAdesione = null;
		if (ygConferimentoStato.getYgAdesione() != null && ygConferimentoStato.getYgAdesione().getDtAdesione() != null) {
			try {
				dataAdesione = Utils.dateToGregorianDate(ygConferimentoStato.getYgAdesione().getDtAdesione());
			} catch (Exception e) {
				log.error("processBatchConferimentoIniziale: Impossibile parsare data adesione. idYgConferimentoStato = "
						+ ygConferimentoStato.getIdYgConferimentoStato()
						+ ", idYgAdesione = "
						+ ygConferimentoStato.getYgAdesione().getIdYgAdesione());
			}
		}
		String codRegioneMin = null;
		if (ygConferimentoStato.getYgAdesione() != null && ygConferimentoStato.getYgAdesione().getDeRegione() != null) {
			codRegioneMin = ygConferimentoStato.getYgAdesione().getDeRegione().getCodMin();
		}
		String codStatoAdesioneMin = null;
		if (ygConferimentoStato.getDeStatoAdesioneMin() != null) {
			codStatoAdesioneMin = ygConferimentoStato.getDeStatoAdesioneMin().getCodStatoAdesioneMin();
		}

		if (codiceFiscale == null || dataAdesione == null || codRegioneMin == null || codStatoAdesioneMin == null) {
			log.error("processBatchConferimentoIniziale: Dati obbligatori mancanti: idYgConferimentoStato = "
					+ ygConferimentoStato.getIdYgConferimentoStato() + ", idYgAdesione = "
					+ ygConferimentoStato.getYgAdesione().getIdYgAdesione() + ", codiceFiscale = " + codiceFiscale
					+ ", dataAdesione = " + dataAdesione + ", codRegioneMin = " + codRegioneMin
					+ ", codStatoAdesioneMin = " + codStatoAdesioneMin);
			return;
		}
		youthGuaranteeAdesioneEjb.setStatoAdesioneYG(codiceFiscale, dataAdesione, codRegioneMin, codStatoAdesioneMin,
				esitoHolder, messaggioErroreHolder, idPfPrincipalAdmin);

		// menzi recion
		ygConferimentoStato.setDtmIns(new Date());
		ygConferimentoStato.setCodEsito(esitoHolder.value.getValue());
		ygConferimentoStato.setStrMessErrore(messaggioErroreHolder.value);
		ygConferimentoStato.setFlgProcessato(true);
		merge(ygConferimentoStato);
	}
}
