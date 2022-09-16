package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class DePatenteSilDataModel extends ListDataModel<DePatenteSil> implements SelectableDataModel<DePatenteSil> {
	public DePatenteSilDataModel() {
	}

	public DePatenteSilDataModel(List<DePatenteSil> data) {
		super(data);
	}

	@Override
	public DePatenteSil getRowData(String rowKey) {
		List<DePatenteSil> patenti = (List<DePatenteSil>) getWrappedData();

		for (DePatenteSil patente : patenti) {
			if (patente.getCodPatenteSil().equals(rowKey))
				return patente;
		}

		return null;
	}

	@Override
	public Object getRowKey(DePatenteSil titolo) {
		return titolo.getCodPatenteSil();
	}
}
