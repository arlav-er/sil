var engaged=false;
var obj1,obj2,style,eX,eY,offsetX,offsetY;
var currentOffsetX,currentOffsetY;
var OGG;
var engagedZindex=0;
var differL,differT;


//DOM detected build up:
if(document.getElementById){
  obj1="document.getElementById('"
  obj2="')"
  style=".style"
  eX=(navigator.appName.indexOf("Internet Explorer")==-1)?
  "e.clientX":"event.clientX";
  eY=(navigator.appName.indexOf("Internet Explorer")==-1)?
  "e.clientY":"event.clientY";
  //
  offsetX=(navigator.appName.indexOf("Internet Explorer")==-1)?
  "pageXOffset":"document.body.scrollLeft"
  offsetY=(navigator.appName.indexOf("Internet Explorer")==-1)?
  "pageYOffset":"document.body.scrollTop"
}
else if(document.all){
  obj1="document.all['"
  obj2="']"
  style=".style"
  eX="event.clientX"
  eY="event.clientY"
  //
  offsetX="document.body.scrollLeft"
  offsetY="document.body.scrollTop"
  }
    else if(document.layers){
    obj1="document.layers['"
    obj2="']"
    style=""
    eX="e.pageX"
    eY="e.pageY" 
    //
    offsetX="pageXOffset"
    offsetY="pageYOffset"
    document.captureEvents(Event.MOUSEMOVE)
  }
//DOM build up now over

function engager(e,namer){/*1st argument must be set as e */
  //requires specific DOM buildup
  engaged=(!engaged)?namer:false
  if(engaged){
  OGG=eval(obj1+engaged+obj2+style);
  currentOffsetX=(document.layers)?0:eval(offsetX)
  currentOffsetY=(document.layers)?0:eval(offsetY)
  engagedZindex=OGG.zIndex;
  OGG.zIndex=101;/*you can custom this value: is the higher z-index*/
  var eXin=eval(eX)
  var eYin=eval(eY)
  differL=(eXin+currentOffsetX)-parseFloat(OGG.left)
  differT=(eYin+currentOffsetY)-parseFloat(OGG.top)
  document.onmousemove=dragLayerByCorner
  return;
}
OGG.zIndex=engagedZindex
document.onmousemove=null;/*you could assign here another function*/
/*keep this comment to use freely
http://www.unitedscripters.com */}


function dragLayerByCorner(e){/*argument must be set as e */
  //requires specific DOM buildup
  if(!engaged){return true}
  var eXin=eval(eX)
  var eYin=eval(eY)
  OGG.top=(eYin+currentOffsetY)-differT
  OGG.left=(eXin+currentOffsetX)-differL;
}



  function apriNuovoDivLayer(nuovo, nomeDiv, url)
  {
    if(nuovo) {
      // sono in modalità "Nuovo" per cui attivo solo il layer
      var collDiv = document.getElementsByName(nomeDiv);
      var objDiv = collDiv.item(0);
      objDiv.style.display = "";
    } else {
      // sono in modalità "Dettaglio" per cui devo ricaricare la pagina in modalità "Nuovo"
      // L'url deve avere la forma della stringa w qui commentata
      /*var w = "AdapterHTTP?PAGE=MobilitaGeoPage";
      w += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      w += "&CDNFUNZIONE=<%=_funzione%>";
      w += "&APRIDIV=1";*/
      document.location = url;
    }
  }
  
  function ChiudiDivLayer(nomeDiv)
  {
    ok=true;
    if ((flagChanged!=null) && flagChanged) {
       if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
           ok=false;
       } else { 
           // Vogliamo chiudere il layer. 
           // Pongo il flag = false per evitare che mi venga riproposto 
           // un "confirm" quando poi navigo con le linguette nella pagina principale
           flagChanged = false;       }
       
    }
    
    if (ok) {
       var collDiv = document.getElementsByName(nomeDiv);
       var objDiv = collDiv.item(0);
       objDiv.style.display = "none";
    }
    
  }//ChiudiDivLayer(_)
