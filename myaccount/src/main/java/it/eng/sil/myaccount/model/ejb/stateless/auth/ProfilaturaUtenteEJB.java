package it.eng.sil.myaccount.model.ejb.stateless.auth;

import it.eng.sil.myaccount.model.enums.TipoAbilitazioneCas;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Stateless
public class ProfilaturaUtenteEJB {

    private static final int JSON_MAX_SIZE = 1000000;

	protected static Log log = LogFactory.getLog(ProfilaturaUtenteEJB.class);
    
    @EJB
    ConstantsSingleton constantsSingleton;

    /**
     * Lista di tutte le funzioni abilitate all'utente collegato
     * HashMap<cod_funzione, Map<cod_oggetto, TipoAbilitazione>
     *
     */
    private HashMap<String, Map<String, TipoAbilitazioneCas>> abilitazioni = new HashMap<String, Map<String, TipoAbilitazioneCas>>();

    public HashMap<String, Map<String, TipoAbilitazioneCas>> getDatiProfilatura(Integer idPfPrincipal) {
        log.info("Recupero dati profilatura per l'utente " + idPfPrincipal);
        try {
            JSONObject jsonDatiUtente = getToken(idPfPrincipal);

            JSONArray listaOggetti = jsonDatiUtente.getJSONArray("tokenProfilatura");
            setDatiProfilatura(listaOggetti);

        } catch (IOException e) {
            log.error(e);
        } catch (JSONException e) {
            log.error(e);
        }

        return abilitazioni;

    }

    private void setDatiProfilatura(JSONArray listaOggetti) throws JSONException {
        for (int i = 0; i < listaOggetti.length(); i++) {
            JSONObject oggetto = (JSONObject) listaOggetti.get(i);

            String codFunzione = oggetto.getString("codFunzione");
            String codOggetto = oggetto.getString("codOggetto");
            String codAttributo = oggetto.getString("codAttributo");

            if (abilitazioni.containsKey(codFunzione)) {
                Map<String, TipoAbilitazioneCas> mapOggettiAbilitazioni = abilitazioni.get(codFunzione);
                mapOggettiAbilitazioni.put(codOggetto, TipoAbilitazioneCas.FLG_INSERIMENTO);
            } else {
                Map<String, TipoAbilitazioneCas> mapTempAbilitazioni = new HashMap<String, TipoAbilitazioneCas>();
                mapTempAbilitazioni.put(codOggetto, TipoAbilitazioneCas.FLG_INSERIMENTO);
                abilitazioni.put(codFunzione, mapTempAbilitazioni);
            }
        }
    }

    private JSONObject getToken(Integer idPfPrincipal) throws IOException, JSONException {
        HttpClient httpClient = new HttpClient();

        GetMethod urlAuth = new GetMethod(constantsSingleton.getProfileURL() + "?id="
                + idPfPrincipal.toString());
        httpClient.executeMethod(urlAuth);
        String jsonString = urlAuth.getResponseBodyAsString(JSON_MAX_SIZE);
        urlAuth.releaseConnection();

        JSONObject json = new JSONObject(jsonString);

        return json;
    }

    public HashMap<String, Map<String, TipoAbilitazioneCas>> getAbilitazioni() {
        return abilitazioni;
    }

    public void setAbilitazioni(HashMap<String, Map<String, TipoAbilitazioneCas>> abilitazioni) {
        this.abilitazioni = abilitazioni;
    }

}
