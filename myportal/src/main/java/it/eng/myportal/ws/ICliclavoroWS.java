package it.eng.myportal.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.7.0
 * 2012-11-08T11:47:15.657+01:00
 * Generated source version: 2.7.0
 * 
 */
@WebService(targetNamespace = "http://servizi.lavoro.gov.it/cliclavoro", name = "cliclavoroWS")
@XmlSeeAlso({it.gov.lavoro.servizi.cliclavoro.types.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ICliclavoroWS {

    @WebResult(name = "risposta_invioCandidatura", targetNamespace = "http://servizi.lavoro.gov.it/cliclavoro/types", partName = "parameters")
    @WebMethod(operationName = "InvioCandidatura")
    public it.gov.lavoro.servizi.cliclavoro.types.RispostaInvioCandidaturaType invioCandidatura(
        @WebParam(partName = "parameters", name = "invioCandidatura", targetNamespace = "http://servizi.lavoro.gov.it/cliclavoro/types")
        it.gov.lavoro.servizi.cliclavoro.types.RichiestaInvioCandidaturaType parameters
    );

    @WebResult(name = "risposta_invioMessaggio", targetNamespace = "http://servizi.lavoro.gov.it/cliclavoro/types", partName = "parameters")
    @WebMethod(operationName = "InvioMessaggio")
    public it.gov.lavoro.servizi.cliclavoro.types.RispostaInvioMessaggioType invioMessaggio(
        @WebParam(partName = "parameters", name = "invioMessaggio", targetNamespace = "http://servizi.lavoro.gov.it/cliclavoro/types")
        it.gov.lavoro.servizi.cliclavoro.types.RichiestaInvioMessaggioType parameters
    );

    @WebResult(name = "risposta_invioVacancy", targetNamespace = "http://servizi.lavoro.gov.it/cliclavoro/types", partName = "parameters")
    @WebMethod(operationName = "InvioVacancy")
    public it.gov.lavoro.servizi.cliclavoro.types.RispostaInvioVacancyType invioVacancy(
        @WebParam(partName = "parameters", name = "invioVacancy", targetNamespace = "http://servizi.lavoro.gov.it/cliclavoro/types")
        it.gov.lavoro.servizi.cliclavoro.types.RichiestaInvioVacancyType parameters
    );
}
