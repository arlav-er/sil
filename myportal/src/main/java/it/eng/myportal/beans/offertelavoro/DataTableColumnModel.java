package it.eng.myportal.beans.offertelavoro;

import java.io.Serializable;

public class DataTableColumnModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3832413797737399991L;
	
	private String header;
    private String property;

    public DataTableColumnModel(String header, String property) {
        this.header = header;
        this.property = property;
    }

    public String getHeader() {
        return header;
    }

    public String getProperty() {
        return property;
    }
}
