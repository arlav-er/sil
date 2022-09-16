package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class DeOrarioSilDataModel extends ListDataModel<DeOrarioSil> implements SelectableDataModel<DeOrarioSil> {
	public DeOrarioSilDataModel() {
	}

	public DeOrarioSilDataModel(List<DeOrarioSil> data) {
		super(data);
	}

	@Override
	public DeOrarioSil getRowData(String rowKey) {
		List<DeOrarioSil> orari = (List<DeOrarioSil>) getWrappedData();

		for (DeOrarioSil orario : orari) {
			if (orario.getCodOrarioSil().equals(rowKey))
				return orario;
		}

		return null;
	}

	@Override
	public Object getRowKey(DeOrarioSil orario) {
		return orario.getCodOrarioSil();
	}
}
