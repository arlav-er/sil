package it.eng.myportal.beans.offertelavoro;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import it.eng.myportal.entity.decodifiche.DeLingua;

public class DeLinguaDataModel extends ListDataModel<DeLingua> implements SelectableDataModel<DeLingua> {  

    public DeLinguaDataModel() {
    }

    public DeLinguaDataModel(List<DeLingua> data) {
        super(data);
    }
    
    @Override
    public DeLingua getRowData(String rowKey) {       
        List<DeLingua> lingue = (List<DeLingua>) getWrappedData();
        
        for(DeLingua lingua : lingue) {
            if(lingua.getCodLingua().equals(rowKey))
                return lingua;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(DeLingua lingua) {
        return lingua.getCodLingua();
    }
}