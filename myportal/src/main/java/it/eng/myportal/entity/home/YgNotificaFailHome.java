package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.YgNotificaFailDTO;
import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.YgNotificaFail;
import it.eng.myportal.enums.ErroreNotificaCambioStatoAdesioneYGEnum;
import it.eng.myportal.utils.Utils;

import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

@Stateless
public class YgNotificaFailHome extends AbstractUpdatableHome<YgNotificaFail, YgNotificaFailDTO> {

	@EJB
	YgAdesioneHome ygAdesioneManager;

	@Override
	public YgNotificaFail findById(Integer id) {
		return findById(YgNotificaFail.class, id);
	}

	@Override
	public YgNotificaFail fromDTO(YgNotificaFailDTO dto) {
		YgNotificaFail entity = super.fromDTO(dto);

		entity.setIdYgNotificaFail(dto.getId());
		entity.setCodErrore(dto.getCodErrore());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setCodMonoTipoNotifica(dto.getCodMonoTipoNotifica());
		entity.setDtAdesione(dto.getDtAdesione());
		entity.setCodStatoAdesioneMin(dto.getCodStatoAdesioneMin());
		entity.setStrMessErrore(dto.getStrMessErrore());
		entity.setCodRegioneAdesioneMin(dto.getCodRegioneAdesione());
		entity.setDtmStatoAdesioneMin(dto.getDtStatoAdesioneMin());
		if (dto.getYgAdesioneDTO() != null) {
			entity.setYgAdesione(ygAdesioneManager.findById(dto.getYgAdesioneDTO().getId()));
		}

		return entity;
	}

	@Override
	public YgNotificaFailDTO toDTO(YgNotificaFail entity) {
		YgNotificaFailDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdYgNotificaFail());
		dto.setCodErrore(entity.getCodErrore());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setCodMonoTipoNotifica(entity.getCodMonoTipoNotifica());
		dto.setCodRegioneAdesione(entity.getCodRegioneAdesioneMin());
		dto.setCodStatoAdesioneMin(entity.getCodStatoAdesioneMin());
		dto.setDtAdesione(entity.getDtAdesione());
		dto.setDtStatoAdesioneMin(entity.getDtmStatoAdesioneMin());
		dto.setStrMessErrore(entity.getStrMessErrore());
		dto.setYgAdesioneDTO(ygAdesioneManager.toDTO(entity.getYgAdesione()));

		return dto;
	}

	/**
	 * Scrive un log sulla tabella yg_notifica_fail. Questa funzione va chiamata per loggare gli errori non dovuti ad
	 * eccezioni in genere, cioe' non per l'errore generico. Per loggare errori dovuti ad un'eccezione di MyPortal
	 * (errore generico, tipo E) occorre chiamare la funzione logException.
	 * 
	 * @param datiStatoAdesione
	 * @param errore
	 * @param codMonoTipoNotifica
	 */
	public void logErrore(YgAdesione ygAdesione,
			it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione datiStatoAdesione,
			Date dtStatoAdesione, String codErrore, String messaggioErrore, String codMonoTipoNotifica,
			Integer idPfPrincipal) {
		try {
			logErrore(ygAdesione, datiStatoAdesione.getCodiceFiscale(),
					Utils.fromXMLGregorianCalendar(datiStatoAdesione.getDataAdesione()),
					StringUtils.leftPad(datiStatoAdesione.getRegioneAdesione(), 2, "0"),
					datiStatoAdesione.getStatoAdesione(), dtStatoAdesione, codErrore, messaggioErrore,
					codMonoTipoNotifica, idPfPrincipal);
		} catch (ParseException e) {
			// se va in eccezione la conversione della data (la data arriva dal ministero quindi in realta' e' sempre
			// corretta) loggo senza data
			logErrore(ygAdesione, datiStatoAdesione.getCodiceFiscale(), null,
					StringUtils.leftPad(datiStatoAdesione.getRegioneAdesione(), 2, "0"),
					datiStatoAdesione.getStatoAdesione(), dtStatoAdesione, codErrore, messaggioErrore,
					codMonoTipoNotifica, idPfPrincipal);
		}
	}

	/**
	 * Scrive un log sulla tabella yg_notifica_fail. Questa funzione va chiamata per loggare gli errori non dovuti ad
	 * eccezioni in genere, cioe' non per l'errore generico. Per loggare errori dovuti ad un'eccezione di MyPortal
	 * (errore generico, tipo E) occorre chiamare la funzione logException.
	 * 
	 * @param datiStatoAdesione
	 * @param errore
	 * @param codMonoTipoNotifica
	 */
	public void logErrore(YgAdesione ygAdesione,
			it.eng.myportal.youthGuarantee.getStatoAdesione.input.DatiStatoAdesione datiStatoAdesione,
			Date dtStatoAdesioneMin, String codErrore, String messaggioErrore, String codMonoTipoNotifica,
			Integer idPfPrincipal) {
		logErrore(ygAdesione, datiStatoAdesione.getCodiceFiscale(), null,
				StringUtils.leftPad(datiStatoAdesione.getRegioneAdesione(), 2, "0"), null,
				dtStatoAdesioneMin, codErrore, messaggioErrore, codMonoTipoNotifica, idPfPrincipal);
	}

	/**
	 * Scrive un log sulla tabella yg_notifica_fail. Questa funzione va chiamata per loggare gli errori non dovuti ad
	 * eccezioni in genere, cioe' non per l'errore generico. Per loggare errori dovuti ad un'eccezione di MyPortal
	 * (errore generico, tipo E) occorre chiamare la funzione logException.
	 * 
	 * @param datiStatoAdesione
	 * @param errore
	 * @param codMonoTipoNotifica
	 */
	public void logErrore(YgAdesione ygAdesione,
			it.eng.myportal.youthGuarantee.notificaStatoAdesione.input.DatiStatoAdesione datiStatoAdesione,
			Date dtStatoAdesioneMin, String codErrore, String messaggioErrore, String codMonoTipoNotifica,
			Integer idPfPrincipal) {
		try {
			logErrore(ygAdesione, datiStatoAdesione.getCodiceFiscale(),
					Utils.fromXMLGregorianCalendar(datiStatoAdesione.getDataAdesione()),
					StringUtils.leftPad(datiStatoAdesione.getRegioneAdesione(), 2, "0"),
					datiStatoAdesione.getStatoAdesione(), dtStatoAdesioneMin, codErrore, messaggioErrore,
					codMonoTipoNotifica, idPfPrincipal);
		} catch (ParseException e) {
			// se va in eccezione la conversione della data (la data arriva dal ministero quindi in realta' e' sempre
			// corretta) loggo senza data
			logErrore(ygAdesione, datiStatoAdesione.getCodiceFiscale(), null,
					StringUtils.leftPad(datiStatoAdesione.getRegioneAdesione(), 2, "0"),
					datiStatoAdesione.getStatoAdesione(), dtStatoAdesioneMin, codErrore, messaggioErrore,
					codMonoTipoNotifica, idPfPrincipal);
		}
	}

	/**
	 * Scrive un log sulla tabella yg_notifica_fail. Questa funzione va chiamata per loggare gli errori di parsing xml
	 * dei messaggi ricevuti.
	 * 
	 * @param codiceFiscale
	 * @param dataAdesione
	 * @param codRegioneMin
	 * @param codStatoAdesioneMin
	 * @param messaggioErrore
	 * @param codMonoTipoNotifica
	 */
	public void logErroreParsing(String xml, String codMonoTipoNotifica, Integer idPfPrincipal) {
		logErrore(null, null, null, null, null, null, ErroreNotificaCambioStatoAdesioneYGEnum.XML.getCodice(), xml,
				codMonoTipoNotifica, idPfPrincipal);
	}

	/**
	 * Scrive un log sulla tabella yg_notifica_fail.
	 * 
	 * @param codiceFiscale
	 * @param dataAdesione
	 * @param codRegioneMin
	 * @param codStatoAdesioneMin
	 * @param codErrore
	 * @param messaggioErrore
	 * @param codMonoTipoNotifica
	 */
	private void logErrore(YgAdesione ygAdesione, String codiceFiscale, Date dataAdesione, String codRegioneMin,
			String codStatoAdesioneMin, Date dtStatoAdesioneMin, String codErrore, String messaggioErrore,
			String codMonoTipoNotifica, Integer idPfPrincipal) {
		YgNotificaFail ygNotificaFail = new YgNotificaFail();
		ygNotificaFail.setYgAdesione(ygAdesione);
		ygNotificaFail.setCodiceFiscale(codiceFiscale);
		ygNotificaFail.setDtAdesione(dataAdesione);
		ygNotificaFail.setCodRegioneAdesioneMin(codRegioneMin);
		ygNotificaFail.setCodStatoAdesioneMin(codStatoAdesioneMin);
		ygNotificaFail.setDtmStatoAdesioneMin(dtStatoAdesioneMin);
		ygNotificaFail.setCodErrore(codErrore);
		ygNotificaFail.setStrMessErrore(messaggioErrore);
		ygNotificaFail.setCodMonoTipoNotifica(codMonoTipoNotifica);
		Date now = new Date();
		ygNotificaFail.setDtmIns(now);
		ygNotificaFail.setDtmMod(now);
		ygNotificaFail.setPfPrincipalIns(pfPrincipalHome.findById(idPfPrincipal));
		ygNotificaFail.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));

		persist(ygNotificaFail);
	}
}
