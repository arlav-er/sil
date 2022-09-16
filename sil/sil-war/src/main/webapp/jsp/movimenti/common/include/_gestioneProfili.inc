<script language="javascript">

function gestioneProfilo(obj)
{
  <% 
     boolean canView = false;
     boolean canImpatti = false;
     boolean canEditLav = false;
     boolean canEditAz = false;
     String _pageDaValutare = "";
     if (!cdnLavoratore.equals("") && !prgAzienda.equals("") && !prgUnita.equals("")){
         _pageDaValutare = "";
  %>
        if(obj.value == "AVV"){
          <%
            if(inserisci){
              _pageDaValutare = "MovDettaglioAvviamentoInserisciPage"; 
            } else {
                    if(valida){
                      _pageDaValutare = "MovValidaAvviamentoPage";
                    }     
              }%>	
        } 
        else {
            if(obj.value == "CES"){
              <% 
                if(inserisci){
                  _pageDaValutare = "MovDettaglioCessazioneInserisciPage"; 
                } else {
                      if(valida){
                        _pageDaValutare = "MovValidaCessazionePage";
                      }     
                }%>
            }
            else {
                if((obj.value == "TRA") || (obj.value == "PRO")){
                  <%
                    if(inserisci){
                      _pageDaValutare = "MovDettaglioTrasfProInserisciPage"; 
                    } else {
                          if(valida){
                            _pageDaValutare = "MovValidaTrasfProPage";
                          }     
                      }
                  %>
                }
            }
        }
  <%
        if(consulta && _pageDaValutare.equals("")){ _pageDaValutare = "MovDettaglioGeneraleConsultaPage"; }
        ProfileDataFilter filter = new ProfileDataFilter(user, _pageDaValutare);
  
        filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
        canEditLav = filter.canEditLavoratore();

        filter.setCdnLavoratore(null);
        filter.setPrgAzienda(new BigDecimal(prgAzienda));
        filter.setPrgUnita(new BigDecimal(prgUnita));
        canEditAz = filter.canEditUnitaAzienda();
  
        canView = (canEditLav || canEditAz);
    }//if (!cdnLavoratore.equals("") && !prgAzienda.equals("") && !prgUnita.equals(""))
  %>
}//end function

</script>
