package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeStatoInvioClDTO;
import it.eng.myportal.entity.decodifiche.DeStatoInvioCl;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeStatoInvioCl.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeStatoInvioCl
 * @author Turrini
 */
@Stateless
public class DeStatoInvioClHome extends AbstractSuggestibleHome<DeStatoInvioCl, DeStatoInvioClDTO> {

	public DeStatoInvioCl findById(final String id) {
		return findById(DeStatoInvioCl.class, id);
	}

	@Override
	public DeStatoInvioClDTO toDTO(final DeStatoInvioCl entity) {
		if (entity == null)
			return null;
		DeStatoInvioClDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodStatoInvioCl());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeStatoInvioCl fromDTO(DeStatoInvioClDTO dto) {
		if (dto == null)
			return null;
		final DeStatoInvioCl entity = super.fromDTO(dto);
		entity.setCodStatoInvioCl(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	/**
	 * Restituisce true se il codice passato come input indica che l'oggetto (cv
	 * e vacancy) e' gia' stato comunicato a cliclavoro, false altrimenti. Cioe'
	 * se lo stato di invio non e' ne PA ne PE
	 * 
	 * @param codDeStatoInvioCl
	 * @return
	 */
	public boolean giaComunicatoCliclavoro(String codDeStatoInvioCl) {
		boolean result = false;

		if (codDeStatoInvioCl != null && !codDeStatoInvioCl.isEmpty()) {
			if (!(codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO) || codDeStatoInvioCl
			        .equals(ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE))) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Restituisce true se il codice passato come input indica che e' stata
	 * comunicata la chiusura su cliclavoro per l'oggetto cui si riferisce (cv e
	 * vacancy) ma cliclavoro ha risposto con un erroe, false altrimenti. Cioe'
	 * se lo stato di invio e' CE
	 * 
	 * @param codDeStatoInvioCl
	 * @return
	 */
	public boolean isErroreChiusura(String codDeStatoInvioCl) {
		boolean result = false;

		if (codDeStatoInvioCl != null && !codDeStatoInvioCl.isEmpty()) {
			if (codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE)) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Restituisce true se il codice passato come input indica che e' la
	 * chiusura candidatura e' in attesa d'invio a cliclavoro. <br>
	 * Cioe' se lo stato di invio e' CA
	 * 
	 * @param codDeStatoInvioCl
	 * @return
	 */
	public boolean isInAttesaChiusura(String codDeStatoInvioCl) {
		boolean result = false;

		if (codDeStatoInvioCl != null && !codDeStatoInvioCl.isEmpty()) {
			if (codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA)) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Metodo per il calcolo dello statoInvio per le comunicazioni con
	 * cliclavoro In base allo stato attuale a all'operazione che si compie
	 * calcola il nuovo stato. Lo schema delle transizioni e' riportato
	 * nell'analisi.
	 * 
	 * @param codStatoInvioClCorrente
	 * @param azione
	 * @return
	 * @throws StatoInvioException
	 */
	public String calcolaStatoInvioCL(String codStatoInvioClCorrente, ConstantsSingleton.AzioneCliclavoro azione) {
		String codStatoInvioCl = null;

		if (codStatoInvioClCorrente == null || codStatoInvioClCorrente.isEmpty()) {
			// NON SONO IN ALCUNO STATO
			switch (azione) {
			case SINCRONIZZO: // vado in PA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO)) {
			// PA
			switch (azione) {
			case CHIUSURA: // in questo caso va eliminato il riferimento su DB
				throw new MyPortalException("cliclavoro.statoinconsistente");
			case MODIFICA_CV: // vado in PA
			case MODIFICA_VA: // vado in PA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO;
				break;
			case CLICLAVORO_ERR: // vado in PE
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE;
				break;
			case CLICLAVORO_OK: // vado in PI
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE)) {
			// PE
			switch (azione) {
			case CHIUSURA: // in questo caso va eliminato il riferimento su DB
				throw new MyPortalException("cliclavoro.statoinconsistente");
			case MODIFICA_CV: // vado in PA
			case MODIFICA_VA: // vado in PA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO)) {
			// PI
			switch (azione) {
			case MODIFICA_CV: // vado in VA
			case MODIFICA_VA: // vado in VA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE;
				break;
			case CHIUSURA: // vado in CA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE)) {
			// VA
			switch (azione) {
			case MODIFICA_CV: // vado in VA
			case MODIFICA_VA: // vado in VA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE;
				break;
			case CHIUSURA: // vado in CA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA;
				break;
			case CLICLAVORO_ERR: // vado in VE
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.VARIAZIONE_INVIATA_ERRORE;
				break;
			case CLICLAVORO_OK: // vado in VI
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.VARIAZIONE_INVIATA;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.VARIAZIONE_INVIATA_ERRORE)) {
			// VE
			switch (azione) {
			case MODIFICA_CV: // vado in VA
			case MODIFICA_VA: // vado in VA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE;
				break;
			case CHIUSURA: // vado in CA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.VARIAZIONE_INVIATA)) {
			// VI
			switch (azione) {
			case MODIFICA_CV: // vado in VA
			case MODIFICA_VA: // vado in VA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE;
				break;
			case CHIUSURA: // vado in CA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA)) {
			// CA
			switch (azione) {
			case MODIFICA_CV: // resto in CA
			case MODIFICA_VA: // resto in CA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA;
				break;
			case CLICLAVORO_ERR: // vado in CE
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE;
				break;
			case CLICLAVORO_OK: // vado in CI
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA;
				break;
			case SINCRONIZZO: // vado in VA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE)) {
			// CE
			switch (azione) {
			case MODIFICA_CV: // vado in CA
			case MODIFICA_VA: // vado in CA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA;
				break;
			case SINCRONIZZO: // vado in VA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA)) {
			// CI
			switch (azione) {
			case SINCRONIZZO: // vado in PA
				codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO;
				break;
			default:
				throw new MyPortalException("cliclavoro.statoinconsistente");
			}
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO)) {
			// MA
			codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO;
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.INVIATA_ERRORE)) {
			// ME
			codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.INVIATA_ERRORE;
		} else if (codStatoInvioClCorrente.equals(ConstantsSingleton.DeStatoInvioCl.INVIATA)) {
			// MI
			codStatoInvioCl = ConstantsSingleton.DeStatoInvioCl.INVIATA;
		} else {
			throw new MyPortalException("cliclavoro.statoinconsistente");
		}

		return codStatoInvioCl;
	}
}
