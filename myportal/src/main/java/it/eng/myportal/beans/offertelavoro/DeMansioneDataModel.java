package it.eng.myportal.beans.offertelavoro;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import it.eng.myportal.entity.decodifiche.DeMansione;

public class DeMansioneDataModel extends ListDataModel<DeMansione> implements SelectableDataModel<DeMansione> {  

    public DeMansioneDataModel() {
    }

    public DeMansioneDataModel(List<DeMansione> data) {
        super(data);
    }
    
    @Override
    public DeMansione getRowData(String rowKey) {       
        List<DeMansione> mansioni = (List<DeMansione>) getWrappedData();
        
        for(DeMansione mansione : mansioni) {
            if(mansione.getCodMansione().equals(rowKey))
                return mansione;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(DeMansione mansione) {
        return mansione.getCodMansione();
    }
}