package it.eng.sil.module;

import java.io.Serializable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

/**
 * Interfaccia che definisce i metodi di un generico processo che cammina "passo passo", ossia eseguendo un'operazione a
 * ogni chiamata. Per vederene un uso, guardare la classe "EsportaMigrazioni".
 * 
 * @author Luigi Antenucci
 */
public interface StepByStepMarchable extends Serializable {

	/**
	 * Inizializzazione dell'Esecutore. In genere è utile precaricare i dati per i passi da eseguire. In input riceve la
	 * "getConfig()" del modulo, in caso possa servire.
	 */
	public void initMarchable(SourceBean moduleConfig) throws EMFUserError;

	/**
	 * Rende TRUE se c'è almeno un passo successivo da eseguire col metodo "doNextStepMarchable()". Serve per
	 * controllare se si è terminata l'elaborazione.
	 */
	public boolean hasNextStepMarchable();

	/**
	 * Elabora tutti i dati relativi al passo successivo. può essere invocato solo dopo una "initMarchable()". In caso
	 * di errore, genera un eccezione (già come messaggio per l'utente).
	 */
	public void doNextStepMarchable() throws EMFUserError;

	/**
	 * Rende la percentuale di elaborazione fatta.
	 */
	public double getProgPercMarchable();

	/**
	 * Metodo chiamato durante l'esecuzione della fase di INIZIO. può essere utile completare la configurazione o fare
	 * ulteriori controlli.
	 */
	public void callbackOnInizioMarchable() throws EMFUserError;

	/**
	 * Metodo chiamato al termine dell'esecuzione dell'ULTIMA SUCC. può essere utile per salvare il nuovo stato
	 * raggiunto. NB: insieme a "callbackOnInizioMarch" possono gestire operazioni fatte in un contesto transazionale.
	 */
	public void callbackOnLastSuccMarchable() throws EMFUserError;

	/**
	 * Metodo chiamato in caso di errore durante una SUCC. può essere utile per annullare dei "lock" iniziali o
	 * riportare l'errore. per altre vie. Viene passata l'eccezione che si è avuta. può essere utile o no.
	 */
	public void callbackOnErrorMarchable(Exception e);

}
