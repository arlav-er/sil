package it.eng.sil.util;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.SourceBeanUtils;

/**
 * Funzioni generali usate in un processo "passo passo" (vedi modulo StepByStepModule).
 * 
 * @author Luigi Antenucci
 */
public abstract class StepByStepUtils implements StepByStepConst {

	/**
	 * Rende una stringa HTML formattata contenente la barra di progresso basata sulla percentuale passata (tra 0 e
	 * 100). La percentuale indica lo stato di avanzamento dell'esecuzione "passo passo".
	 */
	public static String getProgressBar(SourceBean moduleResponse) {

		double perc = SourceBeanUtils.getAttrDouble(moduleResponse, PARAM_PROGPERC, 0);
		return StepByStepUtils.getProgressBar(perc);
	}

	public static String getProgressBar(double percent) {

		int DIM_BARRA = 50;
		String FILL = "&raquo;";
		String EMPTY = ".";

		int numFatti = (int) ((percent * DIM_BARRA) / 100);
		if (numFatti < 0)
			numFatti = 0;
		if (numFatti > DIM_BARRA)
			numFatti = DIM_BARRA;

		StringBuffer barra = new StringBuffer();

		barra.append("<span class=\"ProgressBarLimit\">[</span>");

		barra.append("<span class=\"ProgressBarFill\">");
		for (int f = 0; f < numFatti; f++) {
			barra.append(FILL);
		}
		barra.append("</span>");
		barra.append("<span class=\"ProgressBarEmpty\">");
		for (int e = numFatti; e < DIM_BARRA; e++) {
			barra.append(EMPTY);
		}
		barra.append("</span>");

		barra.append("<span class=\"ProgressBarLimit\">]</span>");

		barra.append("<span class=\"ProgressBarPercent\">&nbsp;");

		if (percent < 10)
			barra.append("&nbsp;");
		if (percent < 100)
			barra.append("&nbsp;");
		barra.append(Integer.toString((int) percent));

		barra.append("% </span>");

		return barra.toString();
	}

}
