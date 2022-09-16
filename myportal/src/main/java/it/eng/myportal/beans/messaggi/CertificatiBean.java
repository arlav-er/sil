package it.eng.myportal.beans.messaggi;

import static it.eng.myportal.utils.ConstantsSingleton.DEFAULT_BUFFER_SIZE;
import static it.eng.myportal.utils.ConstantsSingleton.TMP_DIR;
import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.dtos.MsgAllegatoDTO;
import it.eng.myportal.dtos.MsgCertificatoDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * BackingBean della tab CERTIFICATI nella sezione Messaggi dell'Azienda
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class CertificatiBean extends AbstractMasterDetailTabBean<MsgCertificatoDTO> {

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	private MsgMessaggioDTO risposta;

	private MsgAllegatoDTO allegato;
	private String attachmentName;

	private Boolean openTicket = true; // il ticket è ancora aperto? TODO andrà
										// nel ticket

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		risposta = new MsgMessaggioDTO();
		allegato = new MsgAllegatoDTO();
		try {
			list = retrieveData();

			data = buildNewDataIstance();
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
		}
	}

	@Override
	public MsgCertificatoDTO buildNewDataIstance() {
		return new MsgCertificatoDTO();
	}

	@Override
	protected List<MsgCertificatoDTO> retrieveData() {
		return msgMessaggioHome.findMessaggiCertificati(getSession().getPrincipalId());
	}

	/**
	 * Visualizza un ticket
	 */
	@Override
	public void view() {
		super.view();
		try {
			if (!data.getLetto()) { // se il messaggio visualizzato non era mai
									// stato letto allora segna come 'letto'
									// anche tutte le risposte.
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), data.getId(), true);
				for (MsgMessaggioDTO msg : data.getRisposte()) {
					if (!msg.getLetto())
						msgMessaggioHome.signAsRead(getSession().getPrincipalId(), msg.getId(), true);
				}
				data.setLetto(true);
			}
		} catch (EJBException e) {
			log.error("Errore durante la visualizzazione di un messaggio di supporto: " + e.getMessage());
		}
	}

	/**
	 * Salva un nuovo ticket
	 */
	@Override
	protected void saveData() {
		data.setCodTipoMessaggio(MsgMessaggio.CERTIFICATO);
		data.setDtmMod(data.getDtmIns());
		data.setIdFrom(getSession().getPrincipalId());
		data.setLetto(true); // l'ho scritto io...
		data = msgMessaggioHome.persistMsgCertificatoDTO(data, getSession().getPrincipalId());
		return;
	}

	@Override
	protected AbstractHome<?, MsgCertificatoDTO, Integer> getHome() {
		throw new UnsupportedOperationException("Impossibile richiamare la Home");
	}

	public MsgMessaggioDTO getRisposta() {
		return risposta;
	}

	public void setRisposta(MsgMessaggioDTO risposta) {
		this.risposta = risposta;
	}

	public Boolean getOpenTicket() {
		return openTicket;
	}

	public void setOpenTicket(Boolean openTicket) {
		this.openTicket = openTicket;
	}

	public void send() {
		try {
			risposta.setCodTipoMessaggio(MsgMessaggio.CERTIFICATO);

			if (allegato.getTempFilename() != null && !allegato.getTempFilename().isEmpty()) {
				String baseDir = TMP_DIR;
				File attach = new File(baseDir + File.separator + allegato.getTempFilename());
				FileInputStream input = new FileInputStream(attach);
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int length;
				while ((length = input.read(buffer)) > 0) {
					output.write(buffer, 0, length);
				}
				byte[] fileAllegato = output.toByteArray();
				output.close();
				input.close();
				boolean delete = attach.delete();
				if (!delete) {
					log.debug("File non cancellato:" + allegato.getTempFilename());
				}
				allegato.setContenuto(fileAllegato);

				risposta.getAllegati().add(allegato);
			}

			List<MsgMessaggioDTO> risps = data.getRisposte();
			// se chi invia è un utente
			if (getSession().isUtente()) {
				// allora il destinatario è la provincia legato al messaggio.
				risposta.setProvinciaTo(data.getProvinciaTo());
			}
			// se invece chi invia è un'azienda
			else {
				// allora il destinatario è l'utente da cui è partito il messaggio
				risposta.setIdTo(data.getIdFrom());
			}
			risposta.setIdFrom(getSession().getPrincipalId());
			risposta.setLetto(true); // l'ho scritto io...
			risposta.setMittente("Io"); // non importa...
			risposta.setOggetto(data.getOggetto()); // sempre quello
			risposta.setTicket(data.getTicket());
			risposta.setIdMsgMessaggioPrecedente(data.getLast().getId());
			risposta = homePersist(msgMessaggioHome, risposta);
			data.getRisposte().add(risposta);
			data.getAllegati().addAll(risposta.getAllegati());
			risposta = new MsgMessaggioDTO();
			allegato = new MsgAllegatoDTO();
		} catch (RuntimeException e) {
			addErrorMessage("message.error");
		} catch (IOException e) {
			addErrorMessage("message.error");
		}
	}

	public void showInsertPanel() {
		super.showInsertPanel();
		data.setDtmIns(new Date());
	}

	public MsgAllegatoDTO getAllegato() {
		return allegato;
	}

	public void setAllegato(MsgAllegatoDTO allegato) {
		this.allegato = allegato;
	}

}
