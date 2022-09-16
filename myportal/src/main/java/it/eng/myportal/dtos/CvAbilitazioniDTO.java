package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.List;

public class CvAbilitazioniDTO extends AbstractUpdatableDTO implements ICurriculumSection {
	private static final long serialVersionUID = 1L;

	private Integer idCvDatiPersonali;
	private List<IDecode> listaPatenti;
	private List<IDecode> listaPatentini;
	private List<IDecode> listaAlbi;

	/**
	 * questa informazione viene persistita su CvDatiPersonali
	 */
	private String noteAbilitazioni;

	public CvAbilitazioniDTO() {
		super();
		listaPatenti = new ArrayList<IDecode>();
		listaPatentini = new ArrayList<IDecode>();
		listaAlbi = new ArrayList<IDecode>();
	}

	@Override
	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	@Override
	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	public List<IDecode> getListaPatenti() {
		return listaPatenti;
	}

	public void setListaPatenti(List<IDecode> listaPatenti) {
		this.listaPatenti = listaPatenti;
	}

	public List<IDecode> getListaPatentini() {
		return listaPatentini;
	}

	public void setListaPatentini(List<IDecode> listaPatentini) {
		this.listaPatentini = listaPatentini;
	}

	public List<IDecode> getListaAlbi() {
		return listaAlbi;
	}

	public void setListaAlbi(List<IDecode> listaAlbi) {
		this.listaAlbi = listaAlbi;
	}

	public String getNoteAbilitazioni() {
		return noteAbilitazioni;
	}

	public void setNoteAbilitazioni(String noteAbilitazioni) {
		this.noteAbilitazioni = noteAbilitazioni;
	}

}
