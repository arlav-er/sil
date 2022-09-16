package it.eng.myportal.entity.enums;

/**
 * 
 * @author OMenghini
 *
 */
public enum TipoNotificaEnum {
CV_SCAD, 
RIC_SALV, /* Notifica puntuale relativa alle offerte lavorative */
PUNT_CPI, /* Notifica puntuale inviata dall'operatore CPI */
RISP_ASS, /* Notifica puntuale inviata dall'operatore CPI in risposta ad una richiesta di assistenza */
BROADCAST, /* Notifica broacast inviata dall'operatore CPI */
OTP_PATTO, /* Notifica puntuale contente il codice per accettazione del patto (OTP) */
INFO_PATTO /* Notifica puntuale per informativa ricezione patto */
}
