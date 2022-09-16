package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.MsgAllegatoDTO;
import it.eng.myportal.entity.MsgAllegato;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeProvincia;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

@Stateless
public class MsgAllegatoHome extends AbstractUpdatableHome<MsgAllegato, MsgAllegatoDTO> {

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@Override
	public MsgAllegato findById(Integer id) {
		return findById(MsgAllegato.class, id);
	}

	/**
	 * Recupera la lista di allegati per un messaggio
	 * 
	 * @param idMsgMessaggio
	 * @return
	 */
	public List<MsgAllegato> findByMsgMessaggioId(Integer idMsgMessaggio) {
		MsgAllegato msgAllegato = new MsgAllegato();
		msgAllegato.setMsgMessaggio(msgMessaggioHome.findById(idMsgMessaggio));
		return findByExample(msgAllegato);
	}

	@Override
	public MsgAllegatoDTO toDTO(MsgAllegato entity) {
		if (entity == null)
			return null;
		MsgAllegatoDTO dto = super.toDTO(entity);

		dto.setContenuto(entity.getContenuto());
		dto.setFilename(entity.getFilename() != null ? entity.getFilename() : "allegato.bin");
		dto.setId(entity.getIdMsgAllegato());
		dto.setIdMsgMessaggio(entity.getMsgMessaggio().getIdMsgMessaggio());

		return dto;
	}

	@Override
	public MsgAllegato fromDTO(MsgAllegatoDTO dto) {
		if (dto == null)
			return null;
		MsgAllegato entity = super.fromDTO(dto);

		entity.setContenuto(dto.getContenuto());
		entity.setFilename(dto.getFilename());
		entity.setMsgMessaggio(msgMessaggioHome.findById(dto.getIdMsgMessaggio()));

		return entity;
	}

	/**
	 * Recupera un'allegato controllando che l'utente, del quale viene passato lo username, abbia i permessi sufficienti
	 * alla sua visualizzazione. L'utente può scaricare l'allegato in uno dei seguenti casi:
	 * <ul>
	 * <li>Il messaggio è stato inviato dall'utente stesso</li>
	 * <li>Il messaggio è stato inviato all'utente</li>
	 * <li>Il messaggio è stato inviato ad una provincia al quale appartiene l'utente</li>
	 * <li>Il messaggio è stato inviato al ruolo dell'utente (es coordinatore)</li>
	 * </ul>
	 * 
	 * @param parseInt
	 *            id dell'allegato
	 * @param username
	 *            username dell'utente che lo vuole scaricare.
	 * @return l'allegato se può visualizzarlo
	 * @throws EJBException
	 *             se non ha i permessi sufficienti.
	 */
	public MsgAllegatoDTO findDTOByIdAndUsername(Integer parseInt, String username) {
		MsgAllegato allegato = findById(parseInt);
		MsgMessaggio messaggio = allegato.getMsgMessaggio();
		PfPrincipal user = pfPrincipalHome.findByUsername(username);

		if (user == null) {
			throw new EJBException("Eseguire l'autenticazione per recuperare gli allegati.");
		}

		DeProvincia userProvincia = null;
		if (user.isProvincia()) {
			userProvincia = user.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia();
		}
		PfPrincipal from = messaggio.getPfPrincipalFrom();
		PfPrincipal to = messaggio.getPfPrincipalTo();
		DeProvincia provinciaTo = messaggio.getDeProvinciaTo();

		// se l'utente è il mittente allora ok
		if (from != null && user.getIdPfPrincipal().equals(from.getIdPfPrincipal())) {
			return toDTO(allegato);
		}

		// se gli è stato inviato ok
		if (to != null && user.getIdPfPrincipal().equals(to.getIdPfPrincipal())) {
			return toDTO(allegato);
		}

		// se è stato inviato alla sua provincia ok
		if (provinciaTo != null && userProvincia != null
				&& userProvincia.getCodProvincia().equals(provinciaTo.getCodProvincia())) {
			return toDTO(allegato);
		}

		// se è stato inviato al suo ruolo allora ok
		if (messaggio.getDeRuoloPortaleTo() != null && messaggio.getDeRuoloPortaleTo().equals(user.getDeRuoloPortale())) {
			return toDTO(allegato);
		}

		throw new EJBException("Non hai i permessi sufficienti per visualizzare l'allegato.");
	}

}
