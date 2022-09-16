<%  // *** getCommonObjects.inc ***

    // Recupero ServiceRequest e sessionContainer (secondo "modello 2") [GG] 
    RequestContainer requestContainer = getRequestContainer(request);
    SourceBean serviceRequest = requestContainer.getServiceRequest();
    SessionContainer sessionContainer = requestContainer.getSessionContainer();  

    // Recupero ServiceResponse
    ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);
    SourceBean serviceResponse = responseContainer.getServiceResponse();

    // Recupero utente autenticato e nome della classe corrente
    User user = (User) sessionContainer.getAttribute(User.USERID);
    String thisClassName = this.getClass().getName();
%>