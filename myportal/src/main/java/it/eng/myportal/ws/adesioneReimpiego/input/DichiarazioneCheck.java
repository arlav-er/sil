//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.06.05 alle 11:35:53 AM CEST 
//


package it.eng.myportal.ws.adesioneReimpiego.input;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DichiarazioneCheck.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DichiarazioneCheck">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CIGS"/>
 *     &lt;enumeration value="NASPI"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DichiarazioneCheck")
@XmlEnum
public enum DichiarazioneCheck {

    CIGS,
    NASPI;

    public String value() {
        return name();
    }

    public static DichiarazioneCheck fromValue(String v) {
        return valueOf(v);
    }

}
