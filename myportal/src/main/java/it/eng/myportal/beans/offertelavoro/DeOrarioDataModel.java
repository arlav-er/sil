package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.entity.decodifiche.DeOrario;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class DeOrarioDataModel extends ListDataModel<DeOrario> implements SelectableDataModel<DeOrario> {

	public DeOrarioDataModel() {
	}

	public DeOrarioDataModel(List<DeOrario> data) {
		super(data);
	}

	@Override
	public DeOrario getRowData(String rowKey) {
		List<DeOrario> orari = (List<DeOrario>) getWrappedData();

		for (DeOrario orario : orari) {
			if (orario.getCodOrario().equals(rowKey))
				return orario;
		}

		return null;
	}

	@Override
	public Object getRowKey(DeOrario orario) {
		return orario.getCodOrario();
	}
}