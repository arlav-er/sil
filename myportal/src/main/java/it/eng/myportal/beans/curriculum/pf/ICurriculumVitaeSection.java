package it.eng.myportal.beans.curriculum.pf;

public interface ICurriculumVitaeSection {

	//init della sezione ed eventuali lookup
	public void initSection();
	
	//salvataggio della singola sezione
	public void sync();
	
	//getter/setter del mbean del CV
	public CurriculumVitaePfBean getCurriculumVitaePfBean();
	public void setCurriculumVitaePfBean(CurriculumVitaePfBean curriculumVitaePfBean);
	
}
