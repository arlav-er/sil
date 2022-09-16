package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.AgenziaDTO;
import it.eng.myportal.dtos.AziendaInfoRettificaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeTipoAbilitatoDTO;
import it.eng.myportal.dtos.DeTipoDelegatoDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.SedeDTO;
import it.eng.myportal.dtos.SoggettoDTO;
import it.eng.myportal.entity.AziendaInfoRettifica;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeAutorizzazioneSare;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeTipoAbilitato;
import it.eng.myportal.entity.decodifiche.DeTipoDelegato;
import it.eng.myportal.entity.decodifiche.DeTipoUtenteSare;
import it.eng.myportal.entity.ejb.NotificationBuilder;
import it.eng.myportal.entity.ejb.UtenteAziendaSARE;
import it.eng.myportal.entity.home.decodifiche.DeAutorizzazioneSareHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoAbilitatoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoDelegatoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoUtenteSareHome;
import it.eng.myportal.enums.SoftwareSAREUtilizzato;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

/**
 * Home object for domain model class Azienda.
 * 
 * @see it.eng.myportal.entity.Azienda
 * @author Rodi A.
 */
@Stateless
public class AziendaInfoRettificaHome extends AbstractUpdatableHome<AziendaInfoRettifica, AziendaInfoRettificaDTO> {

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeTipoAbilitatoHome deTipoAbilitatoHome;

	@EJB
	DeTipoDelegatoHome deTipoDelegatoHome;

	@EJB
	DeTipoUtenteSareHome deTipoUtenteSareHome;

	@EJB
	DeAutorizzazioneSareHome deAutorizzazioneSareHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	SvAziendaInfoHome svAziendaInfoHome;

	@EJB
	UtenteAziendaSARE utenteAziendaSARE;

	@EJB
	NotificationBuilder notificationBuilder;

	public AziendaInfoRettifica findById(Integer id) {
		AziendaInfoRettifica obj = findById(AziendaInfoRettifica.class, id);
		return obj;
	}

	@Override
	public AziendaInfoRettificaDTO toDTO(AziendaInfoRettifica entity) {
		if (entity == null)
			return null;
		AziendaInfoRettificaDTO dto = super.toDTO(entity);

		dto.setId(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setPfPrincipalDTO(pfPrincipalHome.toDTO(entity.getPfPrincipal()));
		dto.setLogo(entity.getLogo());
		if (entity.getDeComuneRichiedente() != null) {
			dto.setComune(deComuneHome.toDTO(entity.getDeComuneRichiedente()));
		}
		dto.setFlagAgenziaEstera(Boolean.TRUE.equals(entity.getFlagAgenziaEstera()));

		dto.setSoftwareSAREUtilizzato(SoftwareSAREUtilizzato.getInstance(entity.getSwCreazioneCo()));
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		if (entity.getDeProvincia() != null) {
			dto.setProvinciaRiferimento(deProvinciaHome.toDTO(entity.getDeProvincia()));
		}
		// richiedente
		dto.setNomeRic(entity.getNomeRic());
		dto.setCognomeRic(entity.getCognomeRic());
		if (entity.getDeComuneNascitaRic() != null) {
			dto.setComuneNascitaRic(deComuneHome.toDTO(entity.getDeComuneNascitaRic()));
		} else {
			dto.setComuneNascitaRic(new DeComuneDTO());
		}

		dto.setDataNascitaRic(entity.getDtDataNascitaRic());
		dto.setIndirizzoRic(entity.getIndirizzoRic());
		dto.setCapRic(entity.getCapRic());

		dto.setMittenteSare(entity.getMittenteSare());

		// Referente
		dto.setReferenteSare(entity.getReferenteSare());
		dto.setEmailReferente(entity.getEmailReferente());
		dto.setNomeReferente(entity.getNomeRic());
		dto.setCognomeReferente(entity.getCognomeRic());
		dto.setTelefonoReferente(entity.getTelefonoReferente());

		// Sede (operativa)
		SedeDTO sedeOperativa = new SedeDTO();
		if (entity.getDeComuneSede() != null) {
			sedeOperativa.setComune(deComuneHome.toDTO(entity.getDeComuneSede()));
		}
		sedeOperativa.setCap(entity.getCapSede());
		sedeOperativa.setFax(entity.getFaxSede());
		sedeOperativa.setIndirizzo(entity.getIndirizzoSede());
		sedeOperativa.setTelefono(entity.getTelefonoSede());
		dto.setSedeOperativa(sedeOperativa);

		dto.setRagioneSociale(entity.getRagioneSociale());

		// SedeLegale

		SedeDTO sedeLegale = new SedeDTO();
		DeComune deComuneByCodComSedeLegale = entity.getDeComuneSedeLegale();
		if (deComuneByCodComSedeLegale != null) {
			sedeLegale.setComune(deComuneHome.toDTO(deComuneByCodComSedeLegale));
		}
		sedeLegale.setCap(entity.getCapSedeLegale());
		sedeLegale.setFax(entity.getFaxSedeLegale());
		sedeLegale.setIndirizzo(entity.getIndirizzoSedeLegale());
		sedeLegale.setTelefono(entity.getTelefonoSedeLegale());
		dto.setSedeLegale(sedeLegale);
		dto.setPartitaIva(entity.getPartitaIva());

		DeTipoAbilitato deTipoAbilitato = entity.getDeTipoAbilitato();
		if (deTipoAbilitato != null) {
			dto.setTipoAbilitato(deTipoAbilitatoHome.toDTO(deTipoAbilitato));
		}
		DeTipoDelegato deTipoDelegato = entity.getDeTipoDelegato();
		if (deTipoDelegato != null) {
			dto.setTipoDelegato(deTipoDelegatoHome.toDTO(deTipoDelegato));
		}
		DeComune deComuneByCodComIscrizione = entity.getDeComuneIscrizione();

		/**
		 * AGENZIA SOMMINISTRAZIONE
		 */
		AgenziaDTO agenzia = new AgenziaDTO();
		agenzia.setEstera(entity.getFlagAgenziaEstera());
		agenzia.setNumeroProvvedimento(entity.getIscrProvvedNumero());
		agenzia.setDataProvvedimento(entity.getDtIscrProvvedData());

		if (deComuneByCodComIscrizione != null) {
			agenzia.setComune(deComuneHome.toDTO(deComuneByCodComIscrizione));
		}
		agenzia.setNumeroIscrizione(entity.getIscrNumero());
		agenzia.setDataIscrizione(entity.getDtIscrData());

		dto.setAgenzia(agenzia);

		/**
		 * Soggetto Abilitato
		 */

		SoggettoDTO soggettoAbilitato = new SoggettoDTO();
		soggettoAbilitato.setOrdineIscrizione(entity.getIscrOrdine());
		if (deComuneByCodComIscrizione != null) {
			soggettoAbilitato.setLuogoIscrizione(deComuneHome.toDTO(entity.getDeComuneIscrizione()));
		}
		soggettoAbilitato.setDataIscrizione(entity.getDtIscrData());
		soggettoAbilitato.setNumeroIscrizione(entity.getIscrNumero());
		dto.setSoggettoAbilitato(soggettoAbilitato);

		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setPartitaIva(entity.getPartitaIva());

		DeTipoUtenteSare deTipoUtenteSare = entity.getDeTipoUtenteSare();
		if (deTipoUtenteSare != null) {
			dto.setCodTipoUtenteSare(deTipoUtenteSare.getCodTipoUtenteSare());
		}

		DeAutorizzazioneSare deAutorizzazioneSare = entity.getDeAutorizzazioneSare();
		if (deAutorizzazioneSare != null) {
			dto.setCodAutorizzazioneSare(deAutorizzazioneSare.getCodAutorizzazioneSare());
			dto.setDescAutorizzazioneSare(deAutorizzazioneSare.getDescrizione());
		}

		dto.setEmail(entity.getEmailRic());
		dto.setDomanda(entity.getDomanda());
		dto.setRisposta(entity.getRisposta());

		return dto;
	}

	@Override
	public AziendaInfoRettifica fromDTO(AziendaInfoRettificaDTO dto) {
		if (dto == null)
			return null;
		AziendaInfoRettifica entity = super.fromDTO(dto);
		if (dto.getProvinciaRiferimento() != null && dto.getProvinciaRiferimento().getId() != null) {
			entity.setDeProvincia(deProvinciaHome.findById(dto.getProvinciaRiferimento().getId()));
		}

		PfPrincipal pfPrincipal = pfPrincipalHome.findById(dto.getId());
		entity.setIdPfPrincipal(pfPrincipal.getIdPfPrincipal());
		entity.setPfPrincipal(pfPrincipal);

		if (dto.getComune() != null && dto.getComune().getId() != null) {
			entity.setDeComuneRichiedente(deComuneHome.findById(dto.getComune().getId()));
		}
		entity.setLogo(dto.getLogo());
		entity.setPartitaIva(dto.getPartitaIva());

		entity.setFlagAgenziaEstera(dto.getFlagAgenziaEstera());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		if (dto.getSoftwareSAREUtilizzato() != null) {
			entity.setSwCreazioneCo(dto.getSoftwareSAREUtilizzato().toString());
		}

		// richiedente
		addRichiedente(entity, dto);
		entity.setIndirizzoRic(dto.getIndirizzoRic());
		entity.setCapRic(dto.getCapRic());

		String codTipoUtenteSare = dto.getCodTipoUtenteSare();
		if (StringUtils.isNotBlank(codTipoUtenteSare)) {
			entity.setDeTipoUtenteSare(deTipoUtenteSareHome.findById(codTipoUtenteSare));
		}

		String codAutorizzazioneSare = dto.getCodAutorizzazioneSare();
		if (StringUtils.isNotBlank(codAutorizzazioneSare)) {
			entity.setDeAutorizzazioneSare(deAutorizzazioneSareHome.findById(codAutorizzazioneSare));
		}
		entity.setRagioneSociale(dto.getRagioneSociale());

		// Referente
		entity.setMittenteSare(dto.getMittenteSare());
		entity.setReferenteSare(dto.getReferenteSare());
		entity.setEmailReferente(dto.getEmailReferente());
		entity.setTelefonoReferente(dto.getTelefonoReferente());

		// Sede (operativa)
		addSedeOperativa(entity, dto.getSedeOperativa());

		// SedeLegale
		addSedeLegale(entity, dto.getSedeLegale());

		entity.setEmailReferente(dto.getEmailReferente());

		DeTipoAbilitatoDTO deTipoAbilitatoDTO = dto.getTipoAbilitato();
		if (deTipoAbilitatoDTO != null && deTipoAbilitatoDTO.getId() != null) {
			entity.setDeTipoAbilitato(deTipoAbilitatoHome.findById(deTipoAbilitatoDTO.getId()));

			/**
			 * AGENZIA SOMMINISTRAZIONE
			 */
			if (ConstantsSingleton.DeTipoAbilitato.AGENZIA_SOMMINISTRAZIONE.equals(deTipoAbilitatoDTO.getId())) {
				AgenziaDTO agenzia = dto.getAgenzia();
				addAgenziaInfo(entity, agenzia);
			}

			/**
			 * Soggetto Abilitato
			 */
			else if (ConstantsSingleton.DeTipoAbilitato.SOGGETTO_ABILITATO.equals(deTipoAbilitatoDTO.getId())) {
				SoggettoDTO soggettoAbilitato = dto.getSoggettoAbilitato();
				addSoggettoAbilitatoInfo(entity, soggettoAbilitato);
			}
		}
		DeTipoDelegatoDTO deTipoDelegatoDTO = dto.getTipoDelegato();
		if (deTipoDelegatoDTO != null && deTipoDelegatoDTO.getId() != null) {
			entity.setDeTipoDelegato(deTipoDelegatoHome.findById(deTipoDelegatoDTO.getId()));
		}

		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setPartitaIva(dto.getPartitaIva());

		return entity;
	}

	/**
	 * Aggiunge all'entity le informazioni sull'agenzia di somministrazione
	 * 
	 * @param entity
	 * @param agenzia
	 * @return
	 */
	public void addAgenziaInfo(AziendaInfoRettifica entity, AgenziaDTO agenzia) {
		entity.setFlagAgenziaEstera(agenzia.getEstera());
		entity.setIscrProvvedNumero(agenzia.getNumeroProvvedimento());
		entity.setDtIscrProvvedData(agenzia.getDataProvvedimento());

		if (agenzia.getComune() != null && agenzia.getComune().getId() != null) {
			entity.setDeComuneIscrizione(deComuneHome.findById(agenzia.getComune().getId()));
		}
		entity.setIscrNumero(agenzia.getNumeroIscrizione());
		entity.setDtIscrData(agenzia.getDataIscrizione());

	}

	/**
	 * Aggiunge all'entity le informazioni sul so
	 * 
	 * @param entity
	 * @param agenzia
	 * @return
	 */
	public void addSoggettoAbilitatoInfo(AziendaInfoRettifica entity, SoggettoDTO soggettoAbilitato) {
		entity.setIscrOrdine(soggettoAbilitato.getOrdineIscrizione());
		DeComuneDTO luogoIscrizione = soggettoAbilitato.getLuogoIscrizione();
		if (luogoIscrizione != null && luogoIscrizione.getId() != null) {
			entity.setDeComuneIscrizione(deComuneHome.findById(luogoIscrizione.getId()));
		}
		entity.setIscrNumero(soggettoAbilitato.getNumeroIscrizione());
		entity.setDtIscrData(soggettoAbilitato.getDataIscrizione());
	}

	/**
	 * Aggiunge all'entity le informazioni sulla sede operativa
	 * 
	 * @param entity
	 * @param sede
	 * @return
	 */
	public void addSedeOperativa(AziendaInfoRettifica entity, SedeDTO sede) {
		DeComuneDTO comuneSede = sede.getComune();
		if (comuneSede != null && comuneSede.getId() != null) {
			entity.setDeComuneSede(deComuneHome.findById(comuneSede.getId()));
		}
		entity.setCapSede(sede.getCap());
		entity.setFaxSede(sede.getFax());
		entity.setIndirizzoSede(sede.getIndirizzo());
		entity.setTelefonoSede(sede.getTelefono());
	}

	/**
	 * Aggiunge all'entity le informazioni sulla sede legale
	 * 
	 * @param entity
	 * @param sede
	 * @return
	 */
	public void addSedeLegale(AziendaInfoRettifica entity, SedeDTO sede) {
		DeComuneDTO comuneSedeLegale = sede.getComune();
		if (comuneSedeLegale != null && comuneSedeLegale.getId() != null) {
			entity.setDeComuneSedeLegale(deComuneHome.findById(comuneSedeLegale.getId()));
		}
		entity.setCapSedeLegale(sede.getCap());
		entity.setIndirizzoSedeLegale(sede.getIndirizzo());
		entity.setTelefonoSedeLegale(sede.getTelefono());
		entity.setFaxSedeLegale(sede.getFax());

	}

	/**
	 * Aggiunge all'entity le informazioni sul richiedente
	 * 
	 * @param entity
	 * @param sede
	 * @return
	 */
	public void addRichiedente(AziendaInfoRettifica entity, AziendaInfoRettificaDTO dto) {
		entity.setNomeRic(dto.getNomeRic());
		entity.setCognomeRic(dto.getCognomeRic());
		if (dto.getComuneNascitaRic() != null && dto.getComuneNascitaRic().getId() != null) {
			entity.setDeComuneNascitaRic(deComuneHome.findById(dto.getComuneNascitaRic().getId()));
		}
		entity.setDtDataNascitaRic(dto.getDataNascitaRic());
		entity.setEmailRic(dto.getEmail());
		entity.setDomanda(dto.getDomanda());
		entity.setRisposta(dto.getRisposta());
	}

	@Override
	public AziendaInfoRettificaDTO persistDTO(AziendaInfoRettificaDTO data, Integer idPrincipalIns) {
		AziendaInfoRettificaDTO ret = super.persistDTO(data, idPrincipalIns);
		Set<MsgMessaggioDTO> n = notificationBuilder.buildNotifications(ret);
		notificationBuilder.sendNotification(n);
		return ret;  
	}

	@Override
	public AziendaInfoRettificaDTO mergeDTO(AziendaInfoRettificaDTO data, Integer idPrincipalMod) {
		AziendaInfoRettificaDTO temp = super.mergeDTO(data, idPrincipalMod);
		Set<MsgMessaggioDTO> n = notificationBuilder.buildNotifications(data);
		notificationBuilder.sendNotification(n);
		return temp;
	}

	@Override
	public void removeById(Integer id, Integer idPrincipalMod) {
		super.removeById(id, idPrincipalMod);
		Set<MsgMessaggioDTO> n = notificationBuilder.buildNotificationsRettificaDelete(pfPrincipalHome
		        .findById(idPrincipalMod));
		notificationBuilder.sendNotification(n);
	}

	/**
	 * Respinge una richiesta di rettifica
	 * 
	 * @param idPfPrincipal
	 * @param idPrincipalMod
	 */
	public void rejectById(Integer idPfPrincipal, Integer idPrincipalMod) {
		Set<MsgMessaggioDTO> n = notificationBuilder.buildNotificationsRettificaRejected(
		        pfPrincipalHome.findById(idPrincipalMod).getProvinciasForIdPfPrincipal().iterator().next(),
		        findById(idPfPrincipal).getPfPrincipal().getAziendaInfo());
		notificationBuilder.sendNotification(n);
		
		super.removeById(idPfPrincipal, idPrincipalMod);
	}

	/**
	 * Restituisce la rettifica effettuata da un'azienda se ne ha fatta una,
	 * null altrimenti
	 * 
	 * @param idPfPrincipalAzienda
	 * @return
	 */
	public AziendaInfoRettificaDTO findDTOById(Integer id) {
		return toDTO(findById(id));
	}
}