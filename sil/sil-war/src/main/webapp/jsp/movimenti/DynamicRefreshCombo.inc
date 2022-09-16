

<script type="text/javascript">
<%/*
    Funzione che apre la finestra di refresh delle combo, accetta tre argomenti: 
    - ComboModuleName: deve essere il nome del modulo che viene utilizzato per prelevare i dati da 
    inserire nella combo.
    - comboName: nome del tag SELECT che rappresenta la combo da aggiornare nella pagina  
    del chiamante.
    - argomenti: l'insieme degli argomenti eventualamente necessari per eseguire la selezione
    corretta delle opzioni nei moduli eseguiti. Tali argomenti verranno accodati così come sono
    alla richiesta inviata al server (!!occorre specificare all'inizio il carattere &!!)
    Ovviamente per nuovi moduli occorrerà modificare la struttura della page 
    FantasmaRefreshComboPage per eseguirli quando richiesti.
*/%>
    var opened;
    function refreshCombo(comboModuleName, comboName, argomenti){
        var f = "AdapterHTTP?PAGE=FantasmaRefreshComboPage&COMBOMODULENAME=" + comboModuleName + "&COMBONAME=" + comboName + argomenti;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1,height=1,top=0,left=0";
        opened = window.open(f, t, feat);
    }

<%/*
  Ripulisce la combo indicata della pagina (occorre indicare il nome completo della
  combo a partire dall'oggetto document)
*/%>
    function clearCombo(comboname) {
      combo = eval('window.document.' + comboname);
      for (var i = 0; i < combo.options.length; i++) {
        combo.options[i] = null;
      }
      return combo.options.length;
    }

<%/*
  Aggiunge un elemento alla combo indicata della pagina (occorre indicare il nome 
  completo della combo a partire dall'oggetto document) nella posizione indicata e con
  la descrizione e valore indicati (e in più può essere selezionato di default e al momento
  della creazione)
*/%>
    function addOption(comboname, position, description, value, defaultSelected, selected) {
      combo = eval('window.document.' + comboname);
      combo.options[position] = new Option(description, value, defaultSelected, selected);
    }   
 </script>