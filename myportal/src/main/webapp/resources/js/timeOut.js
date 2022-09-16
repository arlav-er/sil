	SessionTimeOut = {
		//tid: null
		
	}
	
	/**
	 * Inizializza il timeOut
	 */
	SessionTimeOut.startTimeOut = function() {
		/**
		 * tempo del session timeOut
		 */
		/*var timeOut = 60 * 60 * 1000; // 60 Minuti
		clearTimeout(this.tid)
		this.tid = setTimeout(SessionTimeOut.logout, timeOut)*/
	}
	/**
	 * Inizializza la modale
	 */
	SessionTimeOut.logout = function() {
		/*var modal = $("[id$=modal_session]");
		$(modal).dialog({
		    close: function(event, ui) {
		    	SessionTimeOut.submit();
		    },
			modal : true,
		    resizable: false,
		    draggable: false,
			title : 'Auto Logout'
		});
		*/
	}
	/**
	 * Esegue la action, collegata al pulsante della form,
	 * per richiamare la funzione di logout del Bean. 
	 */
	SessionTimeOut.submit = function() {
	//	$(jq('form_session:logout_session')).click();
	}
	