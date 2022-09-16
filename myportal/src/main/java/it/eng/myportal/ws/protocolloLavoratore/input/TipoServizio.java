//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.09.14 alle 02:47:10 PM CEST 
//


package it.eng.myportal.ws.protocolloLavoratore.input;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per TipoServizio.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoServizio">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="C2S"/>
 *     &lt;enumeration value="STO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TipoServizio")
@XmlEnum
public enum TipoServizio {

    @XmlEnumValue("C2S")
    C_2_S("C2S"),
    STO("STO");
    private final String value;

    TipoServizio(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TipoServizio fromValue(String v) {
        for (TipoServizio c: TipoServizio.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
