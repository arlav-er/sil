<script language="JavaScript">

  function checkAndFormatDate(dateObj) {
    var dateStr = dateObj.value;
    var day;
    var month;
    var year;
    if(dateStr.length == 10) { 
      var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
      var matchArray = dateStr.match(datePat); // is the format ok?

      if (matchArray == null) {
        alert("La data \'"+dateStr+"\' è scritta in modo errato!\nInserire nel formato \'gg/mm/aaaa\'");
        return false;  
      }
      month = matchArray[3]; // p@rse date into variables
      day = matchArray[1];
      year = matchArray[5];
    
      if (month < 1 || month > 12) { // check month range
        alert("Inserire mese corretto!");
        return false;
      }
    
      if (day < 1 || day > 31) {
        alert("Inserire giorno corretto!");
        return false;
      }

      if ((month==4 || month==6 || month==9 || month==11) && day==31) {
        alert("Data non corretta!");
        return false;
      }

      if (month == 2) { // check for february 29th
        var isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
        if (day > 29 || (day==29 && !isleap)) {
          alert("Data non corretta!");
          return false;
        }
      }
      dateObj.value = day+"/"+month+"/"+year;
      return true; // date is valid
    }
    else {
      alert("La data \'"+dateStr+"\' è scritta in modo errato!\nInserire nel formato \'gg/mm/aaaa\'");
      return false;
    }
  } 

</script>
