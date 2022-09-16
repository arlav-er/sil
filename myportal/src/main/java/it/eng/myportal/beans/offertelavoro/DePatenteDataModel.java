package it.eng.myportal.beans.offertelavoro;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import it.eng.myportal.entity.decodifiche.DePatente;

public class DePatenteDataModel extends ListDataModel<DePatente> implements SelectableDataModel<DePatente> {  

    public DePatenteDataModel() {
    }

    public DePatenteDataModel(List<DePatente> data) {
        super(data);
    }
    
    @Override
    public DePatente getRowData(String rowKey) {       
        List<DePatente> patenti = (List<DePatente>) getWrappedData();
        
        for(DePatente patente : patenti) {
            if(patente.getCodPatente().equals(rowKey))
                return patente;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(DePatente titolo) {
        return titolo.getCodPatente();
    }
}