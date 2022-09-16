package it.eng.sil.myaccount.helpers;

import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeAutorizzazioneSare;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyAbilitaUtentiSareModel extends LazyDataModel<AziendaInfo> {

	private static final long serialVersionUID = 8323401798618277773L;

	private AziendaInfoEJB aziendaInfoEJB;
	private String user;
	private DeAutorizzazioneSare autorizzazioneSare;
	private String codPorvincia;
	private boolean rettificaFlag;

	private Integer findAllCount;

	public LazyAbilitaUtentiSareModel(AziendaInfoEJB aziendaInfoEJB, String user,
			DeAutorizzazioneSare autorizzazioneSare, String codPorvincia, boolean rettificaFlag) {
		this.aziendaInfoEJB = aziendaInfoEJB;
		this.user = user;
		this.autorizzazioneSare = autorizzazioneSare;
		this.codPorvincia = codPorvincia;
		this.rettificaFlag = rettificaFlag;
	}

	@Override
	public List<AziendaInfo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		List<AziendaInfo> data = new ArrayList<AziendaInfo>();
		data = aziendaInfoEJB.cercaUtentiSAREAbilita(user, autorizzazioneSare, codPorvincia, rettificaFlag, first,
				pageSize);

		for (AziendaInfo azienda : data) {
			if (azienda.getDeAutorizzazioneSare() != null) {
				azienda.setModificaAutorizzazioneSARE(azienda.getDeAutorizzazioneSare().getCodAutorizzazioneSare());
			}

			if (azienda.getDeTipoUtenteSare() != null) {
				azienda.setModificaCodTipoUtenteSare(azienda.getDeTipoUtenteSare().getCodTipoUtenteSare());
			}
		}

		// rowCount
		if (findAllCount == null) {
			findAllCount = aziendaInfoEJB.cercaUtentiSAREAbilitaCount(user, autorizzazioneSare, codPorvincia,
					rettificaFlag);
			this.setRowCount(findAllCount);
		}

		return data;
	}
}
