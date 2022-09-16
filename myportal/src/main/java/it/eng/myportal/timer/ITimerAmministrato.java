package it.eng.myportal.timer;
/**
 * Interfaccia comune ai timer inseriti sulla pagina di amministrazione
 * http://localhost:20000/MyPortal/faces/secure/amministrazione/timerOverride.xhtml
 * 
 * la disabilitazione applicativa fa riferimento al flag_abilitato in Ts_Timer
 * 
 * @author Ale
 *
 */
public interface ITimerAmministrato {

	public void programmaticTimeout();

	//ULTIME ESECUZIONI
	public String getLastProgrammaticTimeout();
	public String getLastAutomaticTimeout();
	
	//ABILITA/DIABILITA APPLICATIVO
	public boolean isLogicEnabled();
	public boolean setLogicEnabled(boolean operativo);
	
	public void setTimer(long duration);
	

}
