package it.eng.sil.myaccount.controller.mbeans.session;

import it.eng.sil.myaccount.model.ejb.stateless.auth.ProfilaturaUtenteEJB;
import it.eng.sil.myaccount.model.enums.TipoAbilitazioneCas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ManagedBean(name = "profilatura")
@SessionScoped
public class ProfilaturaBean {

    protected AccountInfoBean accountInfo;

    /*
     * fornisce la lista di tutta la profilatura per l'utente collegato 
     */
    @EJB
    ProfilaturaUtenteEJB profilaturaUtenteEJB;

    /**
     * Lista di tutte le funzioni abilitate all'utente collegato
     * HashMap<cod_funzione, Map<cod_oggetto, TipoAbilitazione>
     *
     */
    private HashMap<String, Map<String, TipoAbilitazioneCas>> abilitazioniUtente = new HashMap<String, Map<String, TipoAbilitazioneCas>>();

    private Set<String> funzioniUtente = new HashSet<String>();

    public ProfilaturaBean() {
    }

    @PostConstruct
    public void postConstruct() {
        //POPOLARE le abilitazioni dal CAS 
        ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
        if (ex.getSessionMap().containsKey("accountInfoBean")) {
            accountInfo = (AccountInfoBean) ex.getSessionMap().get("accountInfoBean");
        }
        initProfilatura(accountInfo.getIdPfPrincipal());
    }

    public void initProfilatura(Integer idPfPrincipal) {
        InitialContext ic;
        if (profilaturaUtenteEJB == null) {
            try {
                ic = new InitialContext();
                profilaturaUtenteEJB = (ProfilaturaUtenteEJB) ic.lookup("java:module/ProfilaturaUtenteEJB");
            } catch (NamingException e) {
            	throw new RuntimeException("Errore nella lookup", e);
            }
        }

        abilitazioniUtente = profilaturaUtenteEJB.getDatiProfilatura(idPfPrincipal);

        setFunzioniUtente(abilitazioniUtente.keySet());
    }

    public HashMap<String, Map<String, TipoAbilitazioneCas>> getAbilitazioniUtente() {
        return abilitazioniUtente;
    }

    public void setAbilitazioniUtente(HashMap<String, Map<String, TipoAbilitazioneCas>> abilitazioniUtente) {
        this.abilitazioniUtente = abilitazioniUtente;
    }

    public Set<String> getFunzioniUtente() {
        return funzioniUtente;
    }

    public void setFunzioniUtente(Set<String> funzioniUtente) {
        this.funzioniUtente = funzioniUtente;
    }

    public Map<String, TipoAbilitazioneCas> listaOggettiAbilitati(String codFunzione) {
        HashMap<String, Map<String, TipoAbilitazioneCas>> abilitazioni = getAbilitazioniUtente();
        return abilitazioni.get(codFunzione);
    }

    public TipoAbilitazioneCas isOggettoAbilitato(String codFunzione, String codOggetto) {
        TipoAbilitazioneCas abilitazione = null;
        Map<String, TipoAbilitazioneCas> listaOggettiAbilitati = listaOggettiAbilitati(codFunzione);
        if (listaOggettiAbilitati != null && !listaOggettiAbilitati.isEmpty()) {
            if (listaOggettiAbilitati.containsKey(codOggetto)) {
                abilitazione = listaOggettiAbilitati.get(codOggetto);
            }
        }

        return abilitazione;
    }

    /**
     *
     *
     * @param codFunzione
     * @param codOggetto
     * @return true se l'oggetto è abilitato per il percorso
     */
    public boolean isAbilitato(String codFunzione, String codOggetto) {
        boolean checkAbilitato = false;
        Map<String, TipoAbilitazioneCas> listaOggettiAbilitati = listaOggettiAbilitati(codFunzione);
        if (listaOggettiAbilitati != null && !listaOggettiAbilitati.isEmpty()) {
            checkAbilitato = listaOggettiAbilitati.containsKey(codOggetto);
        }

        return checkAbilitato;
    }

    /**
     * verifica se per l'attività passata l'utente è abilitato a visualizzare la
     * sezione o i pulsanti
     *
     * @param servizio
     * @return true se può visualizzare
     */
    public boolean checkAbilitazioneInserimento(String codFunzione, String codOggetto) {
        TipoAbilitazioneCas abilitazione = isOggettoAbilitato(codFunzione, codOggetto);
        if (abilitazione != null && TipoAbilitazioneCas.FLG_INSERIMENTO.equals(abilitazione)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAbilitazioneVisibile(String codFunzione, String codOggetto) {
        TipoAbilitazioneCas abilitazione = isOggettoAbilitato(codFunzione, codOggetto);
        if (abilitazione != null && TipoAbilitazioneCas.FLG_VISIBILE.equals(abilitazione)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAbilitazioneModifica(String codFunzione, String codOggetto) {
        TipoAbilitazioneCas abilitazione = isOggettoAbilitato(codFunzione, codOggetto);
        if (abilitazione != null && TipoAbilitazioneCas.FLG_MODIFICA.equals(abilitazione)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAbilitazioneCancellazione(String codFunzione, String codOggetto) {
        TipoAbilitazioneCas abilitazione = isOggettoAbilitato(codFunzione, codOggetto);
        if (abilitazione != null && TipoAbilitazioneCas.FLG_CANCELLAZIONE.equals(abilitazione)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAbilitazioneInoltra(String codFunzione, String codOggetto) {
        TipoAbilitazioneCas abilitazione = isOggettoAbilitato(codFunzione, codOggetto);
        if (abilitazione != null && TipoAbilitazioneCas.FLG_INOLTRA.equals(abilitazione)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAbilitazioneRispondi(String codFunzione, String codOggetto) {
        TipoAbilitazioneCas abilitazione = isOggettoAbilitato(codFunzione, codOggetto);
        if (abilitazione != null && TipoAbilitazioneCas.FLG_RISPONDI.equals(abilitazione)) {
            return true;
        } else {
            return false;
        }
    }
}
