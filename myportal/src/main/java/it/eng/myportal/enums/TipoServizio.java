package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Classe di enumerazione per determinare il tipo di servizio che si vuole invocare.
 * 
 * @author Rodi A.
 * 
 */
public enum TipoServizio {
DID("did"), STATO_OCCPUAZIONALE("statoocc"), ELENCO_MOVIMENTI("listamov"), PERCORSO_LAVORATORE("perclav"), SANA_REDDITI(
			"sanaredd"), CONFERMA_PERIODICA("confper"), SERVIZI_LAVORATORE("servlav"), CLICLAVORO_INVIAVACANCY("cl_vac"), CLICLAVORO_INVIACANDIDATURA(
			"cl_cand"), CLICLAVORO_INVIAMESSAGGIO("cl_msg"), SIL_RICHIESTAPERSONALE("sil_rp"), SIL_CLICLAVORO("sil_cl"), SARE(
			"sare"), GET_CITTATINO("getcitt"), PUT_CITTATINO("putcitt"), NUOVO_SARE("sare_new"), YG_CHECKUTENTE(
			"yg_ckut"), YG_ADESIONE("yg_ades"), YG_CHECKSAP("yg_cksap"), YG_INVIO_SAP("yg_sap"), YG_RICHIESTA_SAP(
			"yg_gtsap"), YG_DATA_ADESIONE("yg_dtade"), APPUNTAMENTO("appunt"), YG_GET_STATO_ADESIONE_MIN("yg_get_s"), YG_SET_STATO_ADESIONE_MIN(
			"yg_set_s"), MYSTAGE("mystage"), RINNOVO_PATTO("rinpatto"), MYSAP_ROOT("mysap"), MYSAP("mysap_cv"), CHECK_GARANZIA_OVER(
			"gg_over"),ATTIVA_VOUCHER("att_vch"), GO_DATA_ADESIONE("go_dtade"), ENTI_ACCREDITATI("entiaccr"), CONFERIMENTO_DID("confdid"),
			ADESIONE_REIMPIEGO("reimpieg"),SIL_PATTO_NON_FIRMATO("ptonline"),SIL_PATTO_FIRMATO_CLI("ptonsil"), SINTESI_PROTO_PUGLIA("sinprot");

	private static List<SelectItem> tipiServizio;
	private String codTipoServizio;

	TipoServizio(String label) {
		this.codTipoServizio = label;
	}

	public String getCodTipoServizio() {
		return codTipoServizio;
	}

	static {
		tipiServizio = new ArrayList<SelectItem>();
		for (TipoServizio tipo : TipoServizio.values()) {
			tipiServizio.add(new SelectItem(tipo, tipo.getCodTipoServizio()));
		}
	}

	/**
	 * Restituisce l'enumerazione convertita in lista di selectItem.<br/>
	 * <b>Attenzione! Per usare l'enumerazione come valore nel BackingBean è
	 * necessario che l'EnumConverter sia associato alla classe di enumerazione
	 * da convertire. Vedi faces-config.xml
	 * 
	 * @return l'enumerazione convertita in lista di selectItem.
	 */
	public static List<SelectItem> asSelectItems() {
		return tipiServizio;
	}
}
