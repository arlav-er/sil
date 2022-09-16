package it.eng.myportal.beans.vacancies.pf;

import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaLingua;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;
import it.eng.myportal.utils.FormTypeAction;
import org.primefaces.context.RequestContext;
import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;


public class VaLingueSection extends VacancyBaseFormSection implements IVacancySection {

    @EJB
    VaLinguaHome vaLinguaHome;

    @EJB
    DeGradoLinHome deGradoLinHome;

    @EJB
    DeModalitaLinguaHome deModalitaLinguaHome;

    private Integer vacancyId;
    protected Boolean madrelingua;
    private VaLingua vaLinguaAttiva;
    private List<DeLingua> lingueSelezionate;
    private List<VaLingua> listaLingue;
    private VaDatiVacancy vaDatiVacancy;

    protected FormTypeAction formTypeAction = FormTypeAction.I;

    public VaLingueSection(VacancyFormPfBean vaBean, VaLinguaHome vaLingua, DeGradoLinHome linHome,
                           DeModalitaLinguaHome deModalitaLinguaHome) {
        super(vaBean);
        this.vaLinguaHome = vaLingua;
        this.deGradoLinHome = linHome;
        this.deModalitaLinguaHome = deModalitaLinguaHome;
        listaLingue = new ArrayList<>();

    }


    @Override
    public void initSection() {
        vacancyId = getVacancyFormPfBean().getVaDatiVacancy().getIdVaDatiVacancy();
        vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();
        reloadArticlesList();

        vaLinguaAttiva = new VaLingua();
        vaLinguaAttiva.setVaDatiVacancy(vaDatiVacancy);
        refactorLingueCopy(vaLinguaAttiva, true);

    }

    private void reloadArticlesList() {
        listaLingue = vaLinguaHome.findByVacancyIdOrdered(vacancyId);

    }

    public void editLingue(VaLingua vaLinguaAttiva) {
        this.vaLinguaAttiva = vaLinguaAttiva;
        refactorLingueCopy(vaLinguaAttiva, false);
        formTypeAction = FormTypeAction.E;

    }

    public void addNewLingua() {
        vaLinguaAttiva = new VaLingua();
        vaLinguaAttiva.setVaDatiVacancy(vaDatiVacancy);
        refactorLingueCopy(vaLinguaAttiva, true);
        formTypeAction = FormTypeAction.I;
    }

    public void cancelEditLingue() {
        addNewLingua();
        reloadArticlesList();
    }

    public void removeLingua(VaLingua vaLinguaAttiva) {
        this.vaLinguaAttiva = vaLinguaAttiva;
        formTypeAction = FormTypeAction.R;
        sync();

    }

    public void handleChangeMadreLingua() {
        if (vaLinguaAttiva.getFlagMadrelingua()) {
            vaLinguaAttiva.setDeGradoSilLetto(null);
            vaLinguaAttiva.setDeGradoSilParlato(null);
            vaLinguaAttiva.setDeGradoSilScritto(null);
        }

    }

    public void refactorLingueCopy(VaLingua vaLinguaAttiva, boolean check) {
        lingueSelezionate = new ArrayList<DeLingua>();
        for (VaLingua vaLingua : listaLingue) {
            if (check) { // check = true provengo da inserimento
                lingueSelezionate.add(vaLingua.getDeLingua());
            } else {// check = false provengo da modifica
                if (!vaLingua.getDeLingua().getDenominazione()
                        .equals(vaLinguaAttiva.getDeLingua().getDenominazione())) {
                    lingueSelezionate.add(vaLingua.getDeLingua());
                }
            }
        }

    }


    public List<VaLingua> getListaLingue() {
        return listaLingue;
    }

    public void setListaLingue(List<VaLingua> listaLingue) {
        this.listaLingue = listaLingue;
    }

    public VaLingua getVaLinguaAttiva() {
        return vaLinguaAttiva;
    }

    public void setVaLinguaAttiva(VaLingua vaLinguaAttiva) {
        this.vaLinguaAttiva = vaLinguaAttiva;
    }

    @Override
    public void sync() {
        Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();
        VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();
        if (formTypeAction.equals(FormTypeAction.I)) {
            if (lingueSelezionate.contains(vaLinguaAttiva.getDeLingua())) {
                log.error("Errore Lingua Duplicata:" + vaLinguaAttiva.getDeLingua().getDenominazione());
                addAlertWarnMessage("Attenzione ",
                        "Errore: esiste già una Lingua con questo nome associata al CV. Scegliere una lingua differente.");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            } else {
                vaLinguaHome.persist(vaLinguaAttiva, idPfPrinc);
                log.info("Inserimento nuova Lingua:" + vaLinguaAttiva.getDeLingua().getDenominazione());
            }
        } else {
            if (formTypeAction.equals(FormTypeAction.E)) {
                if (lingueSelezionate.contains(vaLinguaAttiva.getDeLingua())) {
                    log.error("Errore Lingua Duplicata:" + vaLinguaAttiva.getDeLingua().getDenominazione());
                    addAlertWarnMessage("Attenzione ",
                            "Errore: esiste già una Lingua con questo nome associata al CV. Scegliere una lingua differente.");
                    RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    return;
                } else {
                    vaLinguaHome.merge(vaLinguaAttiva, idPfPrinc);
                    log.info("Update Lingua:" + vaLinguaAttiva.getDeLingua().getDenominazione());
                }
            } else {
                vaLinguaHome.remove(vaLinguaAttiva);
            }
        }

        setListaLingue(vaLinguaHome.findByVacancyIdOrdered(vacancyId));
        // aggiornare VaDatiVacancy
     	getVacancyFormPfBean().setVaDatiVacancy(homeEJB.merge(vaDatiVacancy, idPfPrinc));
    }

}
