package it.eng.myportal.helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeEvasioneRich;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;

/**
 *
 * @author hatemalimam
 */

public class RedazioneSearchParams implements Serializable {

	private static final long serialVersionUID = 7_886_517_961_070_144_947L;

	private Integer numRichiesta;
	private Integer numAnno;

	private String titoloAnnuncio;
	private String operatoreCPI;
	private String aziendaRichiedente;

	private DeEvasioneRich evasioneRich;
	private DeProvenienza deProvenienzas;
	private CodStatoVacancyEnum statoVacancy;
	private DeComune comuneSedeLavorativa;
	private DeComune comuneSedeAziendale;
	private DeProvincia provinciaLuogoLavoro;
	private DeProvincia provinciaSedeAzienda;
	private Date dataPubblicazioneDa;
	private Date dataPubblicazioneA;
 
	private Integer startFrom;
	private Integer chunkSize;
	private boolean ascending;
	private String validaScaduta;
	private boolean flgVacancyNotRer;


	private VacancyRedazioneSortEnum sortOrder;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getNumRichiesta() {
		return numRichiesta;
	}

	public void setNumRichiesta(Integer numRichiesta) {
		this.numRichiesta = numRichiesta;
	}

	public Integer getNumAnno() {
		return numAnno;
	}

	public void setNumAnno(Integer numAnno) {
		this.numAnno = numAnno;
	}

	public String getTitoloAnnuncio() {
		return titoloAnnuncio;
	}

	public void setTitoloAnnuncio(String titoloAnnuncio) {
		this.titoloAnnuncio = titoloAnnuncio;
	}

	public String getOperatoreCPI() {
		return operatoreCPI;
	}

	public void setOperatoreCPI(String operatoreCPI) {
		this.operatoreCPI = operatoreCPI;
	}

	public String getAziendaRichiedente() {
		return aziendaRichiedente;
	}

	public void setAziendaRichiedente(String aziendaRichiedente) {
		this.aziendaRichiedente = aziendaRichiedente;
	}

	public DeEvasioneRich getEvasioneRich() {
		return evasioneRich;
	}

	public void setEvasioneRich(DeEvasioneRich evasioneRich) {
		this.evasioneRich = evasioneRich;
	}

	public DeProvenienza getDeProvenienzas() {
		return deProvenienzas;
	}

	public void setDeProvenienzas(DeProvenienza deProvenienzas) {
		this.deProvenienzas = deProvenienzas;
	}

	public CodStatoVacancyEnum getStatoVacancy() {
		return statoVacancy;
	}

	public void setStatoVacancy(CodStatoVacancyEnum statoVacancy) {
		this.statoVacancy = statoVacancy;
	}

	public Integer getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(Integer startFrom) {
		this.startFrom = startFrom;
	}

	public Integer getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public CodStatoVacancyEnum[] getCodStatoVacancyEnumValues() {
        return CodStatoVacancyEnum.values();
    }

	public List<SelectItem> selectItemList(){
		List<SelectItem> listStato = new ArrayList<>();
		for (CodStatoVacancyEnum current : CodStatoVacancyEnum.values()) {
			if(!current.getDescrizione().equals(CodStatoVacancyEnum.NAS.getDescrizione()) && (!current.getDescrizione().equals(CodStatoVacancyEnum.LAV.getDescrizione()))){
				listStato.add(new SelectItem(current, current.getDescrizione()));
			}
		}
		return listStato;
	}


	public DeComune getComuneSedeLavorativa() {
		return comuneSedeLavorativa;
	}

	public void setComuneSedeLavorativa(DeComune comuneSedeLavorativa) {
		this.comuneSedeLavorativa = comuneSedeLavorativa;
	}

	public DeComune getComuneSedeAziendale() {
		return comuneSedeAziendale;
	}

	public void setComuneSedeAziendale(DeComune comuneSedeAziendale) {
		this.comuneSedeAziendale = comuneSedeAziendale;
	}

	public VacancyRedazioneSortEnum getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(VacancyRedazioneSortEnum sortOrder) {
		this.sortOrder = sortOrder;
	}

	public DeProvincia getProvinciaLuogoLavoro() {
		return provinciaLuogoLavoro;
	}

	public void setProvinciaLuogoLavoro(DeProvincia provinciaLuogoLavoro) {
		this.provinciaLuogoLavoro = provinciaLuogoLavoro;
	}

	public DeProvincia getProvinciaSedeAzienda() {
		return provinciaSedeAzienda;
	}

	public void setProvinciaSedeAzienda(DeProvincia provinciaSedeAzienda) {
		this.provinciaSedeAzienda = provinciaSedeAzienda;
	}

	public Date getDataPubblicazioneDa() {
		return dataPubblicazioneDa;
	}

	public void setDataPubblicazioneDa(Date dataPubblicazioneDa) {
		this.dataPubblicazioneDa = dataPubblicazioneDa;
	}

	public Date getDataPubblicazioneA() {
		return dataPubblicazioneA;
	}

	public void setDataPubblicazioneA(Date dataPubblicazioneA) {
		this.dataPubblicazioneA = dataPubblicazioneA;
	}

	public String getValidaScaduta() {
		return validaScaduta;
	}

	public void setValidaScaduta(String validaScaduta) {
		this.validaScaduta = validaScaduta;
	}

	public boolean isFlgVacancyNotRer() {
		return flgVacancyNotRer;
	}

	public void setFlgVacancyNotRer(boolean flgVacancyNotRer) {
		this.flgVacancyNotRer = flgVacancyNotRer;
	}
	
}
