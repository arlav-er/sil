package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class DeContrattoSilDataModel extends ListDataModel<DeContrattoSil> implements
		SelectableDataModel<DeContrattoSil> {
	public DeContrattoSilDataModel() {
	}

	public DeContrattoSilDataModel(List<DeContrattoSil> data) {
		super(data);
	}

	@Override
	public DeContrattoSil getRowData(String rowKey) {
		List<DeContrattoSil> contratti = (List<DeContrattoSil>) getWrappedData();

		for (DeContrattoSil contratto : contratti) {
			if (contratto.getCodContrattoSil().equals(rowKey))
				return contratto;
		}

		return null;
	}

	@Override
	public Object getRowKey(DeContrattoSil contratto) {
		return contratto.getCodContrattoSil();
	}
}
