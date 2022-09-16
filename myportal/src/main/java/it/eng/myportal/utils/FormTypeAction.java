package it.eng.myportal.utils;

public enum FormTypeAction {
	
		E("EDIT"), I("INSERT"), R("REMOVE");

		private String descrizione;

		public String getDescrizione() {
			return descrizione;
		}

		public void setDescrizione(String descrizione) {
			this.descrizione = descrizione;
		}

		private FormTypeAction(String descrizione) {
			this.descrizione = descrizione;
		}


}
