/**
 * Home
 * 
 * @author: hatemalimam
 * @date: 10/05/2017
 */
var CandidatureList = MyPortal.createNS("MyPortal.CandidatureList");

(function(CandidatureList, $, undefined) {

	CandidatureList.sendEmailOnComplete = function(args) {
		if (args) {
			if(!args.errore){
                if (!args.validationFailed) {
                    $('#sendEmail-modal').modal('hide');
                    swal({
                        title : "Messaggio/i inviati correttamente!",
                        type : "success",
                        confirmButtonColor : "#c82b2e"
                    }, function() {
            			refresh();
            		});
                }
			}else{
                $('#sendEmail-modal').modal('hide');
				// for now nothing

			}

		}
	}



}(window.MyPortal.CandidatureList = window.CandidatureList || {}, jQuery));
/**
 * END Home
 */

