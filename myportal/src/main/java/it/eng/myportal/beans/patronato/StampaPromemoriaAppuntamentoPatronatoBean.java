package it.eng.myportal.beans.patronato;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AppuntamentoFilterDTO;
import it.eng.myportal.dtos.AppuntamentoDTO;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.exception.MyPortalException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class StampaPromemoriaAppuntamentoPatronatoBean extends AbstractBaseBean {

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	private AppuntamentoFilterDTO appuntamentoFilterDTO;
	private List<AppuntamentoDTO> appuntamenti;
	private boolean noResults = false;
	private StreamedContent promemoriaPdfFile;

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		appuntamentoFilterDTO = new AppuntamentoFilterDTO();
		appuntamenti = new ArrayList<AppuntamentoDTO>();
	}

	public void preparePromemoriaPdfFile(Integer idAgAppuntamento) {
		try {
			promemoriaPdfFile = agAppuntamentoHome.createPromemoriaPdfFile(idAgAppuntamento);
		} catch (MyPortalException e) {
			addErrorMessage("pdf.error");
		}
	}

	public void redirectHome() {
		super.redirectHome();
	}

	public void cercaAppuntamenti() {
		noResults = false;
		appuntamenti = agAppuntamentoHome.findAppuntamentoPatronatoDTObyFilter(session.getPrincipalId(),
				appuntamentoFilterDTO);

		/* ordino il risultato per data e orario */
		StampaPromemoriaAppuntamentoPatronatoBean.SlotComparator slotComparator = new SlotComparator();
		Collections.sort(appuntamenti, slotComparator);

		if (appuntamenti.size() == 0) {
			noResults = true;
		}
	}

	public AppuntamentoFilterDTO getAppuntamentoFilterDTO() {
		return appuntamentoFilterDTO;
	}

	public void setAppuntamentoFilterDTO(AppuntamentoFilterDTO appuntamentoFilterDTO) {
		this.appuntamentoFilterDTO = appuntamentoFilterDTO;
	}

	public List<AppuntamentoDTO> getAppuntamenti() {
		return appuntamenti;
	}

	public void setAppuntamenti(List<AppuntamentoDTO> appuntamenti) {
		this.appuntamenti = appuntamenti;
	}

	public boolean isNoResults() {
		return noResults;
	}

	public void setNoResults(boolean noResults) {
		this.noResults = noResults;
	}

	public StreamedContent getPromemoriaPdfFile() {
		return promemoriaPdfFile;
	}

	public void setPromemoriaPdfFile(StreamedContent promemoriaPdfFile) {
		this.promemoriaPdfFile = promemoriaPdfFile;
	}

	/**
	 * Classe Comparator per ordinare i risultati della ricerca secondo la data
	 * e l'orario
	 * 
	 * @author enrico
	 *
	 */
	private class SlotComparator implements Comparator<AppuntamentoDTO> {

		/**
		 * Ordina gli appuntamenti per data e orario. L'orario si presuppone nel
		 * formato HH:MM, se c'e' un qualsiasi errore il primo appuntamento
		 * viene ordinato prima del secondo.
		 */
		@Override
		public int compare(AppuntamentoDTO first, AppuntamentoDTO second) {
			try {
				String firstOreStr, firstMinutiStr, secondOreStr, secondMinutiStr;
				Integer firstOre, firstMinuti, secondOre, secondMinuti;
				String[] firstSplit = first.getOrario().split(":");
				String[] secondSplit = second.getOrario().split(":");
				firstOreStr = firstSplit[0];
				firstMinutiStr = firstSplit[1];
				secondOreStr = secondSplit[0];
				secondMinutiStr = secondSplit[1];
				firstOre = Integer.parseInt(firstOreStr);
				firstMinuti = Integer.parseInt(firstMinutiStr);
				secondOre = Integer.parseInt(secondOreStr);
				secondMinuti = Integer.parseInt(secondMinutiStr);

				int perData = first.getData().compareTo(second.getData());
				if (perData != 0) {
					// sono in date diverse
					return perData;
				} else {
					// stesa data, ordino per orario
					if (firstOre.compareTo(secondOre) == 0) {
						return firstMinuti.compareTo(secondMinuti);
					} else {
						return firstOre.compareTo(secondOre);
					}
				}
			} catch (Exception e) {
				return -1;
			}
		}
	}

}
