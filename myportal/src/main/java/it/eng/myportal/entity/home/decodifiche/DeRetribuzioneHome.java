package it.eng.myportal.entity.home.decodifiche;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;

import it.eng.myportal.dtos.DeRetribuzioneDTO;
import it.eng.myportal.entity.decodifiche.DeRetribuzione;

/**
 * Home object for domain model class VaDeRetribuzione.
 * 
 * @see it.eng.myportal.entity.VaDeRetribuzione
 * @author iescone
 */
@Stateless(name = "DeRetribuzioneHome")
public class DeRetribuzioneHome extends AbstractDecodeHome<DeRetribuzione, DeRetribuzioneDTO> {

	public DeRetribuzione findById(String id) {
		return findById(DeRetribuzione.class, id);
	}

	//@SuppressWarnings("unchecked")
	@Override
	public List<SelectItem> getListItems(boolean addBlank) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			List<DeRetribuzione> all = findAll();
			for (DeRetribuzione deRetribuzione : all) {
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setGroupingSeparator('.');
				DecimalFormat fo = new DecimalFormat();
				fo.setDecimalFormatSymbols(symbols);
				String label = fo.format(deRetribuzione.getLimInfDecimale()) + " - " + fo.format(deRetribuzione.getLimSupDecimale());
				selectItems.add(new SelectItem(deRetribuzione.getCodRetribuzione(),label));
			}			
			if (addBlank)
				selectItems.add(0, new SelectItem("", ""));

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare risultati per retribuzioni: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca di risultati di tipo DeRetribuzione: " + re.getMessage());
		}
		return selectItems;
	}

	@Override
	public DeRetribuzioneDTO toDTO(DeRetribuzione retribuzione) {
		if (retribuzione == null)
			return null;
		DeRetribuzioneDTO ret = super.toDTO(retribuzione);
		ret.setId(retribuzione.getCodRetribuzione());
		ret.setLimiteInferiore(retribuzione.getLimInfDecimale());
		ret.setLimiteSuperiore(retribuzione.getLimSupDecimale());
		return ret;
	}

	@Override
	public DeRetribuzione fromDTO(DeRetribuzioneDTO retribuzione) {
		if (retribuzione == null)
			return null;
		DeRetribuzione ret = super.fromDTO(retribuzione);
		ret.setCodRetribuzione(retribuzione.getId());
		ret.setLimInfDecimale(retribuzione.getLimiteInferiore());
		ret.setLimSupDecimale(retribuzione.getLimiteSuperiore());
		return ret;
	}

}
