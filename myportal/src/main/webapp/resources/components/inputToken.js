if (!myportal)
	var myportal = {};

if (!myportal.inputToken) {

	function whoiAm(event) {
		
	}
	myportal.inputToken = {
			init: function(options) {
				var ccid = options.ccid;
				var disabled = options.disabled;
				var editingMode = options.editingMode;
				var readonly = options.readonly;
				var hintText = options.hintText;
				var noResultsText = options.noResultsText;
				var searchingText = options.searchingText;
				var maxToken = options.tokenLimit;
				var tokenInputExtra = options.tokenInputExtra;
				var myDiv = document.getElementById(ccid);
				myDiv.effect = document.getElementById(ccid + ":effect");
				myDiv.inputText = document.getElementById(ccid + ':inputText');
				myDiv.message = document.getElementById(ccid + ':message');

					inputObj = $(jq(myDiv.inputText.id));
					optList=$( jq(ccid + ":inputSelect option" ));
			
					initKeys = inputObj.val().split(',');	//chiavi iniziali

					var mapValues=[];

					allValues=$.map(optList,function(opt,i) {
						key=$( opt ).val();
						etichetta=$( opt ).text();
						mapValues[key]=etichetta;
						return {
							id: key,
							label: etichetta,
							selectable: (etichetta.indexOf("(codifica scaduta)") == -1)
						};
					});
					initValues={};
					

					initValues=$.map(initKeys,function(key,i) {
					if(!key) return;
					return {
						id: key,
						label: mapValues[key],
						};
					});
					source=allValues;
					delText='x';
					tema='';
					isDisabled=('true'==disabled);
					isReadonly=('true'==readonly);
					isEditing=('true'==editingMode);
					//tema='MyPortal';
					if(isDisabled || isReadonly || !isEditing){
						delText='';
						maxToken=initValues.length;
						//non si può fare qui, ancora quell'elemento non è stato creato
						//$(jq("token-input-"+ccid+":inputText")).attr('disabled', true).attr('readonly', true);
						//tema='MyPortalDisabled';
					}
					var tokenInputDefaults={
							minChars: 2,
							queryParam:'term',
							propertyToSearch:'label',
							prePopulate:initValues,
				            hintText:  hintText,
				            noResultsText: noResultsText,
				            searchingText: searchingText,
				            deleteText:delText,
				            tokenLimit: maxToken,
				         //   onAdd: posizionaBtn(ccid),
				         //   onDelete: posizionaBtn(ccid),
				         //   onReady: posizionaBtn(ccid,{ offset:"20 0"}),
				            theme:tema
						};
					tokenInputSettings = $.extend({}, tokenInputDefaults, tokenInputExtra);
					inputObj.tokenInput(source,tokenInputSettings);
					inputTokenObj=$(jq("token-input-"+ccid+":inputText"));

					if(isDisabled || isReadonly){
						inputTokenObj.attr('disabled', true).attr('readonly', true);
						$(jq(myDiv.id)).addClass('ui-state-disabled');
					}

					if(!isEditing) {
						toChange = $(jq(myDiv.id) + " " + ".token-input-list");
						toChange.addClass('viewMode');
//						var toChange = $(jq(myDiv.id) + " " + ".token-input-token");
//						toChange.addClass('viewMode');
//						toChange = $(jq(myDiv.id) + " " + ".token-input-input-token");
//						toChange.addClass('viewMode');
						inputTokenObj.attr('disabled', true).attr('readonly', true);
					}
					
					//inputObj.openFullList();

					$('[id^="'+ccid+'"] .token-input-token')
					.keypress(function(el) {
						console.log('pressed' + el);
					}).click(function(el) {
						console.log('clicked' + el);
					});
					//$(jq(ccid)).keypress(whoiAm);

					document.getElementById(ccid + ":group").childNodes[0].style.display = "inline-block";
					if(document.getElementById(ccid + ":openFullList") != null) {
						document.getElementById(ccid + ":openFullList").style.verticalAlign = "top";
						document.getElementById(ccid + ":openFullList").style.top = "0px";
					}
			}
	};



    function posizionaBtn(ccid,options) {
     return function(){
        	inputTokenBtn=$(jq(ccid+":openFullList"));
			//alert('inputTokenBtn;'+JSON.stringify(inputTokenBtn));
			tokenList=$(jq(ccid+":group")).find('ul');
			//alert('tokenList;'+JSON.stringify(tokenList));
			//alert('inputTokenBtn.length;'+JSON.stringify(inputTokenBtn.length));
			defaults={
			    my:        "left top",
			    at:        "right top",
			    of:        tokenList,
			    offset:		"3 0",
			    collision: "none none"
			};
			if (inputTokenBtn.length > 0){
				settings = $.extend({}, defaults, options);
					inputTokenBtn.position(settings);
				}
        };
    }
};