package it.eng.myportal.ws.statoadesione.notifica;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.0
 * 2014-10-17T14:27:07.095+02:00
 * Generated source version: 2.7.0
 * 
 */
@WebService(targetNamespace = "http://servizi.lavoro.gov.it/servizicoap", name = "servizicoapWS")
@XmlSeeAlso({it.eng.myportal.ws.statoadesione.notifica.types.ObjectFactory.class})
public interface ServizicoapWS {

    @RequestWrapper(localName = "notificaCambioStatoAdesioneYG", targetNamespace = "http://servizi.lavoro.gov.it/servizicoap/types", className = "it.eng.myportal.ws.statoadesione.notifica.types.RichiestaNotificaCambioStatoAdesioneYGType")
    @WebMethod
    @ResponseWrapper(localName = "risposta_notificaCambioStatoAdesioneYG", targetNamespace = "http://servizi.lavoro.gov.it/servizicoap/types", className = "it.eng.myportal.ws.statoadesione.notifica.types.RispostaNotificaCambioStatoAdesioneYGType")
    public void notificaCambioStatoAdesioneYG(
        @WebParam(name = "DatiStatoAdesione", targetNamespace = "http://servizi.lavoro.gov.it/servizicoap/types")
        java.lang.String datiStatoAdesione,
        @WebParam(mode = WebParam.Mode.OUT, name = "Esito", targetNamespace = "http://servizi.lavoro.gov.it/servizicoap/types")
        javax.xml.ws.Holder<java.lang.String> esito,
        @WebParam(mode = WebParam.Mode.OUT, name = "MessaggioErrore", targetNamespace = "http://servizi.lavoro.gov.it/servizicoap/types")
        javax.xml.ws.Holder<java.lang.String> messaggioErrore
    );
}