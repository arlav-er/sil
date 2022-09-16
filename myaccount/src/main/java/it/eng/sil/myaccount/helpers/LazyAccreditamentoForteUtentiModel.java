package it.eng.sil.myaccount.helpers;

import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyAccreditamentoForteUtentiModel extends LazyDataModel<UtenteInfo> {

	private static final long serialVersionUID = -1737664927685707496L;

	private UtenteInfoEJB utenteInfoEJB;
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String codProvinciaRichiedente;
	private Boolean abilitazionePec;

	private Integer findAllCount;

	public LazyAccreditamentoForteUtentiModel(UtenteInfoEJB utenteInfoEJB, String nome, String cognome,
			String codiceFiscale, String codProvinciaRichiedente, Boolean abilitazionePec) {
		this.utenteInfoEJB = utenteInfoEJB;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.codProvinciaRichiedente = codProvinciaRichiedente;
		this.abilitazionePec = abilitazionePec;
	}

	@Override
	public List<UtenteInfo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		List<UtenteInfo> data = new ArrayList<UtenteInfo>();
		data = utenteInfoEJB.cercaUtentiPerAbilitazioneForte(nome, cognome, codiceFiscale, codProvinciaRichiedente,
				abilitazionePec, first, pageSize);

		// rowCount
		if (findAllCount == null) {
			findAllCount = utenteInfoEJB.cercaUtentiPerAbilitazioneForteCount(nome, cognome, codiceFiscale,
					codProvinciaRichiedente, abilitazionePec);
			this.setRowCount(findAllCount);
		}

		return data;
	}
}
