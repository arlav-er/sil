package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.AgAppAnagraficaDTO;
import it.eng.myportal.entity.AgAppAnagrafica;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * 
 * @author Enrico
 */
@Stateless
public class AgAppAnagraficaHome extends AbstractUpdatableHome<AgAppAnagrafica, AgAppAnagraficaDTO> {

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@EJB
	private DeComuneHome deComuneHome;

	public AgAppAnagrafica findById(Integer id) {
		AgAppAnagrafica obj = findById(AgAppAnagrafica.class, id);
		return obj;
	}

	@Override
	public AgAppAnagraficaDTO toDTO(AgAppAnagrafica entity) {
		if (entity == null) {
			return null;
		}
		AgAppAnagraficaDTO dto = super.toDTO(entity);
		dto.setAgAppuntamentoDTO(agAppuntamentoHome.toDTO(entity.getAgAppuntamento()));
		dto.setCellulare(entity.getCellulare());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setCodMonoTipoSogg(entity.getCodMonoTipoSogg());
		dto.setCognome(entity.getCognome());
		dto.setDeComNascitaDTO(deComuneHome.toDTO(entity.getDeComune()));
		dto.setDeComuneDTO(deComuneHome.toDTO(entity.getDeComNascita()));
		dto.setDtNascita(entity.getDtNascita());
		dto.setId(entity.getIdAgAppAnagrafica());
		dto.setIndirizzo(entity.getIndirizzo());
		dto.setNome(entity.getNome());
		dto.setRagioneSociale(entity.getRagioneSociale());
		dto.setSesso(entity.getSesso());

		return dto;
	}

	@Override
	public AgAppAnagrafica fromDTO(AgAppAnagraficaDTO dto) {
		if (dto == null) {
			return null;
		}
		AgAppAnagrafica entity = super.fromDTO(dto);
		entity.setAgAppuntamento(agAppuntamentoHome.fromDTO(dto.getAgAppuntamentoDTO()));
		entity.setCellulare(dto.getCellulare());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setCodMonoTipoSogg(dto.getCodMonoTipoSogg());
		entity.setCognome(dto.getCognome());
		entity.setDeComune(deComuneHome.fromDTO(dto.getDeComuneDTO()));
		entity.setDeComNascita(deComuneHome.fromDTO(dto.getDeComNascitaDTO()));
		entity.setDtNascita(dto.getDtNascita());
		entity.setIdAgAppAnagrafica(dto.getId());
		entity.setIndirizzo(dto.getIndirizzo());
		entity.setNome(dto.getNome());
		entity.setRagioneSociale(dto.getRagioneSociale());
		entity.setSesso(dto.getSesso());

		return entity;
	}

	public Integer persistAnagrafica(Integer idPfPrincipal, String nome, String cognome, String sesso,
			String codiceFiscale, Date dataNascita, String codComuneNascita, String codComuneDomicilio,
			String indirizzoDomicilio, String cellulare, Integer idAgAppuntamento) {
		Date now = Calendar.getInstance().getTime();
		AgAppAnagrafica agAppAnagrafica = new AgAppAnagrafica();
		agAppAnagrafica.setAgAppuntamento(agAppuntamentoHome.findById(idAgAppuntamento));
		agAppAnagrafica.setCodiceFiscale(codiceFiscale);
		agAppAnagrafica.setCodMonoTipoSogg("L");
		agAppAnagrafica.setNome(nome);
		agAppAnagrafica.setCognome(cognome);
		agAppAnagrafica.setSesso(sesso);
		agAppAnagrafica.setDtNascita(dataNascita);
		agAppAnagrafica.setDeComNascita(deComuneHome.findById(codComuneNascita));
		agAppAnagrafica.setDeComune(deComuneHome.findById(codComuneDomicilio));
		agAppAnagrafica.setIndirizzo(indirizzoDomicilio);
		agAppAnagrafica.setCellulare(cellulare);
		agAppAnagrafica.setDtmIns(now);
		agAppAnagrafica.setDtmMod(now);
		agAppAnagrafica.setPfPrincipalIns(pfPrincipalHome.findById(idPfPrincipal));
		agAppAnagrafica.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));

		persist(agAppAnagrafica);

		return agAppAnagrafica.getIdAgAppAnagrafica();
	}
}
