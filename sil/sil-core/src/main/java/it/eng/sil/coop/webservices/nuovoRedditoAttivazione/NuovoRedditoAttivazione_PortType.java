/**
 * NuovoRedditoAttivazione_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.nuovoRedditoAttivazione;

public interface NuovoRedditoAttivazione_PortType extends java.rmi.Remote {
	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType validazioneDomanda(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ValidazioneDomandaNraType richiesta_RichiestaRispostaSincrona_validazioneDomanda)
			throws java.rmi.RemoteException;

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType comunicazioneVariazioneResidenza(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioneVariazioneResidenzaFuoriTrentoType richiesta_RichiestaRispostaSincrona_comunicazioneVariazioneResidenza)
			throws java.rmi.RemoteException;

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType validazioneComunicazioniSuccessive(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioniSuccessiveNraType richiesta_RichiestaRispostaSincrona_validazioneComunicazioniSuccessive)
			throws java.rmi.RemoteException;
}
