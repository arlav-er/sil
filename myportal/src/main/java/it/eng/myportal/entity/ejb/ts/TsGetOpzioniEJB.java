package it.eng.myportal.entity.ejb.ts;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.ts.TsOpzioneEnum;
import it.eng.myportal.entity.ts.TsOpzioni;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
public class TsGetOpzioniEJB {

	private static final Log log = LogFactory.getLog(TsGetOpzioniEJB.class);

	@EJB
	TsOpzioniEJB tsOpzioniEJB;

	public Long getNumGGVacScadenzaTimer() {

		try {
			return tsOpzioniEJB.retrieveLongOption(TsOpzioneEnum.TIMER_NUMGG_VAC_SCADENZA);
		} catch (OpzioneNonPresenteException e) {
			// non esiste? Uso il default
			setNumGGVacScadenzaTimer(ConstantsSingleton.DEFAULT_NUMGG_VAC_SCADENZA);
			return ConstantsSingleton.DEFAULT_NUMGG_VAC_SCADENZA;
		}
	}

	public void setNumGGVacScadenzaTimer(Long val) {
		tsOpzioniEJB.saveLongOption(val, TsOpzioneEnum.TIMER_NUMGG_VAC_SCADENZA);
	}

	public Long getNumGGVacInLavTimer() {

		try {
			return tsOpzioniEJB.retrieveLongOption(TsOpzioneEnum.TIMER_NUMGG_VAC_IN_LAV);
		} catch (OpzioneNonPresenteException e) {
			setNumGGVacInLavTimer(ConstantsSingleton.DEFAULT_NUMGG_VAC_IN_LAV);
			return ConstantsSingleton.DEFAULT_NUMGG_VAC_IN_LAV;
		}
	}

	public void setNumGGVacInLavTimer(Long val) {
		tsOpzioniEJB.saveLongOption(val, TsOpzioneEnum.TIMER_NUMGG_VAC_IN_LAV);
	}

	public List<TsOpzioni> getAllOptions() {
		return tsOpzioniEJB.retrieveAllOptions();
	}

	// EX ST_CONFIGURAZIONE
	public Date getDtAttivazioneYg() {

		try {
			return tsOpzioniEJB.retrieveDateTimeOption(TsOpzioneEnum.DT_AVVIO_YG);
		} catch (OpzioneNonPresenteException e) {
			return new Date();
		}
	}

	public void setDtAttivazioneYg(Date val) {
		tsOpzioniEJB.saveDateTimeOption(TsOpzioneEnum.DT_AVVIO_YG, val);
	}

	public Date getDtAggiornamentoMonit() {

		try {
			return tsOpzioniEJB.retrieveDateTimeOption(TsOpzioneEnum.DT_AGGIORNAMENTO_MONIT);
		} catch (OpzioneNonPresenteException e) {
			return new Date();
		}
	}

	public void setDtAggiornamentoMonit(Date val) {
		tsOpzioniEJB.saveDateTimeOption(TsOpzioneEnum.DT_AGGIORNAMENTO_MONIT, val);
	}

	public Date getDtBatchYg() {

		try {
			return tsOpzioniEJB.retrieveDateTimeOption(TsOpzioneEnum.DT_BATCH_YG);
		} catch (OpzioneNonPresenteException e) {
			return new Date();
		}
	}

	public void setDtBatchYg(Date val) {
		tsOpzioniEJB.saveDateTimeOption(TsOpzioneEnum.DT_BATCH_YG, val);
	}
	
	public Boolean getFlgRegUtenteAbilitatoYg() {
		try {
			return tsOpzioniEJB.retrieveBooleanOption(TsOpzioneEnum.FLG_REG_UTENTE_ABILITATO_YG);
		} catch (OpzioneNonPresenteException e) {
			return false;
		}
	}

	public void setFlgRegUtenteAbilitatoYg(Boolean flgRegUtenteAbilitatoYg) {
		tsOpzioniEJB.saveBooleanOption(TsOpzioneEnum.FLG_REG_UTENTE_ABILITATO_YG, flgRegUtenteAbilitatoYg);
	}
	
	
	public Date getDtBatchConfMassivoYg() {
		try {
			return tsOpzioniEJB.retrieveDateTimeOption(TsOpzioneEnum.DT_BATCH_CONF_MASSIVO_YG);
		} catch (OpzioneNonPresenteException e) {
			return new Date();
		}
	}

	public void setDtBatchConfMassivoYg(Date dtBatchConfMassivoYg) {
		tsOpzioniEJB.saveDateTimeOption(TsOpzioneEnum.DT_BATCH_CONF_MASSIVO_YG, dtBatchConfMassivoYg);
	}
	
	public Date getDtAvvioClic() {
		try {
			return tsOpzioniEJB.retrieveDateTimeOption(TsOpzioneEnum.DT_AVVIO_CLIC);
		} catch (OpzioneNonPresenteException e) {
			return new Date();
		}
	}

	public void setDtAvvioClic(Date dtAvvioClic) {
		tsOpzioniEJB.saveDateTimeOption(TsOpzioneEnum.DT_AVVIO_CLIC, dtAvvioClic);
	}
	
	public Date getDtFineConfermaDid() {
		try {
			return tsOpzioniEJB.retrieveDateTimeOption(TsOpzioneEnum.DT_FINE_CONFERMA_DID);
		} catch (OpzioneNonPresenteException e) {
			return new Date();
		}
	}

	public void setDtFineConfermaDid(Date dtFineConfermaDid) {
		tsOpzioniEJB.saveDateTimeOption(TsOpzioneEnum.DT_FINE_CONFERMA_DID, dtFineConfermaDid);
	}
	
	public Date getdtFineDisabilDidApp() {
		try {
			return tsOpzioniEJB.retrieveDateTimeOption(TsOpzioneEnum.DT_FINE_DISABIL_DID_APP);
		} catch (OpzioneNonPresenteException e) {
			return new Date();
		}
	}

	public void setDtFineDisabilDidApp(Date dtFineDisabilDidApp) {
		tsOpzioniEJB.saveDateTimeOption(TsOpzioneEnum.DT_FINE_DISABIL_DID_APP, dtFineDisabilDidApp);
	}
	
	public Long getNumGGCVScadenzaTimer() {

		try {
			return tsOpzioniEJB.retrieveLongOption(TsOpzioneEnum.TIMER_NUMGG_CV_SCADENZA);
		} catch (OpzioneNonPresenteException e) {
			// non esiste? Uso il default
			setNumGGVacScadenzaTimer(ConstantsSingleton.DEFAULT_NUMGG_CV_IN_SCADENZA);
			return ConstantsSingleton.DEFAULT_NUMGG_CV_IN_SCADENZA;
		}
	}

	public void setNumGGCVScadenzaTimer(Long val) {
		tsOpzioniEJB.saveLongOption(val, TsOpzioneEnum.TIMER_NUMGG_CV_SCADENZA);
	}

}
