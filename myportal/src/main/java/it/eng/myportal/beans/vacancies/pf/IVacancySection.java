package it.eng.myportal.beans.vacancies.pf;

public interface IVacancySection {

	//init della sezione ed eventuali lookup
	public void initSection();
	
	//salvataggio della singola sezione
	public void sync();
	
	//getter/setter del mbean del CV
	public VacancyFormPfBean getVacancyFormPfBean();
	public void setVacancyFormPfBeann(VacancyFormPfBean curriculumVitaePfBean);
	
}
