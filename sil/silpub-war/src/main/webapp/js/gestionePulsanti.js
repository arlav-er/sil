var prefix_var = "array_";
var sufix_var = "H";
var sufix_anchor = "A";
var sufix_id = "I";

var condizione_in = "IN";
var condizione_not_in = "NOT_IN";
var condizione_all_equal = "ALL_EQUAL";

function initParametri (p_var,s_var,s_anchor,s_id)
{
	prefix_var = p_var;
	sufix_var = s_var;
	sufix_anchor = s_anchor;
	sufix_id = s_id;
}

//torna 'true','false' o messaggio di warning
function isPulsanteAttivabile(azione,numero,condizioni,prefix_rb,separatore,nomeForm) 
{
/*condizioni=
	array[][]
		condizioni[i][0] = field
		condizioni[i][1] = tipo_condizione (in, not in, all_equal...)
		condizioni[i][2] = elenco valori (,xx,xx,xx,)
		condizioni[i][3] = eventuale messaggio warning
*/
  	var n_check = 0;
  	var ctr = 0;
  	var n_posizione = 0;
	var old_valore = '';
	var messaggio_warning = 'true';
	var obj_form = getForm(nomeForm);
  	while (ctr < obj_form.elements.length) 
  	{
    	var curr_obj = obj_form.elements[ctr];
    	if (isInListaCheckbox(prefix_rb,curr_obj)) 
    	{
//alert ('inizio controllo check numero '+n_posizione+' per azione '+azione );
  	   		n_posizione++;
      		if( curr_obj.checked ) 
      		{
//alert ('CHECKATO!');
        		n_check++;
        		if (condizioni!=null && condizioni.length > 0) 
        		{
//alert ('inizio controllo condizioni');
  		   			for (var i = 0; i < condizioni.length; i++) 
  		   			{
//alert ('inizio controllo condizioni su variabili ' +  condizioni[i][0]);
  		     			var supporto = eval(prefix_var+condizioni[i][0]);
  		     			var tipo_cond = condizioni[i][1];
  		     			var valori = condizioni[i][2];
   	     				var valore = supporto[n_posizione-1];
   	     				if (condizioni[i][3] != null)
   	     				{
  			     			if (tipo_cond != null && tipo_cond == condizione_in)
  		    	 			{ 
  		     					if (valori.indexOf(separatore+valore+separatore) >= 0) 
	     							messaggio_warning =  condizioni[i][3];
  		     				}
	  		     			else if (tipo_cond != null && tipo_cond == condizione_not_in)
  			     			{ 
  			     				if (valori.indexOf(separatore+valore+separatore) < 0) 
	     							messaggio_warning =  condizioni[i][3];
  		     				}
  		     				else if (tipo_cond != null && tipo_cond == condizione_all_equal)
  		     				{ 
  		     					if (old_valore == '') 
  		     	  					old_valore =  valore;
  		     					if (valore != old_valore) 
	     							messaggio_warning =  condizioni[i][3];
  		     				}
   	     				}
   	     				else
   	     				{
  		     				if (tipo_cond != null && tipo_cond == condizione_in)
  		     				{ 
//alert('a tipo_cond IN     ' + valore+ '    ' +valori);
  		     					if (valori.indexOf(separatore+valore+separatore) < 0) 
	     							return 'false';
  		     				}
  		     				else if (tipo_cond != null && tipo_cond == condizione_not_in)
  		     				{ 
//alert('a tipo_cond NOT IN     ' + valore+ '   ' +valori);
  		     					if (valori.indexOf(separatore+valore+separatore) >= 0) 
	     							return 'false';
  		     				}
  		     				else if (tipo_cond != null && tipo_cond == condizione_all_equal)
  		     				{ 
  		     					if (old_valore == '') 
  		     	  					old_valore =  valore;
//alert('a tipo_cond ALL EQUAL     ' + valore+ ' to   ' +old_valore);
  		     					if (valore != old_valore) 
	     							return 'false';
	     					}
  		     			}
		   			}
		 		}
	   		}
	 	}
	 	ctr++
  	}
//alert('test condizione su numero check  su '+azione+'        ' +n_check+'   ammessi: ' + numero);
	var n_check_ammessi = 0;
	var oltre = '';
	if (numero!=null )
	{
		if (numero=='n')
			numero = '1+';
		n_check_ammessi = parseInt(numero,10);
		if (numero.length>0)
			oltre = numero.substring(numero.length-1,numero.length);
	}
	if (oltre=='-' && n_check>n_check_ammessi ) return ('false'); 
	else if (oltre=='+' && n_check<n_check_ammessi ) return ('false'); 
	else if (oltre!='+' && oltre!='-' && n_check!=n_check_ammessi ) return ('false'); 

  	else	return messaggio_warning; 
}

//ritorna true se l'oggetto e' uno dei checkbox
function isInListaCheckbox(prefix_rb,curr_obj)
{
	if (curr_obj==null) return false;
	if (curr_obj.type == 'checkbox' && (curr_obj.name!=null && curr_obj.name.indexOf(prefix_rb)==0))
		return true;
	return false;
}

//ritorna la lista degli ID dei checbok selezionati (id1,id2,id3,...,...)
function getListaCheckSelezionati(prefix_rb, nomeForm)
{
	var listaCheck = '';
	var ctr = 0;
	var obj_form = getForm(nomeForm);
	while (ctr < obj_form.elements.length)
	{
		var curr_obj = obj_form.elements[ctr];
		if (isInListaCheckbox(prefix_rb,curr_obj))
		{
			if (curr_obj.checked)
			{
				listaCheck = listaCheck + getId(prefix_rb,curr_obj)+',';
			}
		}
		ctr ++;
	}
	if (listaCheck != '')
		listaCheck = listaCheck.substring(0,listaCheck.length-1);
	return listaCheck;
}

//ritorna l'id del checkbox
function getId(prefix_rb,curr_obj)
{
	return curr_obj.name.substring(prefix_rb.length,curr_obj.name.length);
}

//ritorna il progressivo dell'oggetto all'interno della lista dei checkbox (a partire da 0)
function getPosizioneCheckbox(prefix_rb,id_check,nomeForm)
{
	var ctr = 0;
	var n_posizione = 0;
	var obj_form = getForm(nomeForm);
  	while (ctr < obj_form.elements.length) 
  	{
    	var curr_obj = obj_form.elements[ctr];
    	if (isInListaCheckbox(prefix_rb,curr_obj) ) 
    	{
    		var id = getId(prefix_rb,curr_obj);
    		if (id == id_check)
    			return n_posizione;
  	   		n_posizione++; 		
	 	}
	 	ctr++
  	}
  	return null;
}

//ritorna il valore del campo 'nome_var' relativo al checkbox con id 'id_check'
function getValore(prefix_rb,nome_var,id_check,nomeForm)
{
    var supporto = eval(prefix_var+nome_var);
    var pos = getPosizioneCheckbox(prefix_rb,id_check,nomeForm);
    if (pos ==  null) return "";
	if (supporto==null || pos >= supporto.length)
		return "";
	return supporto[pos];	
}

function cambiaStatoAttivo(parent)
{
	var class_name = parent.className;
	if (isDisab(class_name))
		parent.className =  class_name.substring(0,class_name.length-5);
}

function cambiaStatoDisattivo(parent,nome)
{
	var class_name = parent.className;
	if (!isDisab(class_name) && !isOver(class_name))
		parent.className =  class_name+"DISAB";
}

function isDisab(classname)
{
	if (classname==null) return false;
	if (classname.substring(classname.length-5)=='DISAB')
		return true;
	return false;
}

function isOver(classname)
{
	if (classname==null) return false;
	if (classname.substring(classname.length-4)=='OVER')
		return true;
	return false;
}

function goIn(obj_td)
{
	if (obj_td == null) return;
	var class_name = obj_td.className;
	if (!isOver(class_name) && !isDisab(class_name))
		obj_td.className = class_name+'OVER';
}

function goOut(obj_td)
{
	if (obj_td==null) return;
	var class_name = obj_td.className;
	if (isOver(class_name))
		obj_td.className = class_name.substring(0,class_name.length-4);
}

function selectAll(obj,prefix,browser,nomeForm)	
{
	var ctr = 0;
	var obj_form = getForm(nomeForm);
	while (ctr < obj_form.elements.length)
	{
		var curr_obj = obj_form.elements[ctr];
		if (isInListaCheckbox(prefix,curr_obj))
		{
			curr_obj.checked = obj.checked;
			if (browser!=null && browser!='NS' && browser!='OP')
			{
				var nomeId = curr_obj.name;
				nomeId = nomeId.substring(prefix.length,nomeId.length);
				selezionaRiga('TR_'+nomeId,curr_obj.checked)
			}
		}
		ctr ++;
	}
	reloadPulsanti();
}

function selezionaRiga(nomeId,checked)
{
	var obj = document.getElementById(nomeId);
	if (obj!=null)
	{
		var classe = obj.className;
		if (checked && (classe!=null && classe.indexOf('XSEL')<0))
			obj.className = classe+'XSEL';
		else if (!checked && (classe!=null && classe.indexOf('XSEL')>=0))
			obj.className = classe.substring(0,classe.indexOf('XSEL'));
	}
}

function getForm(nome)
{
	if (nome==null || nome.length<=0)
		return this.document.forms[0];
	else	
		return eval("this.document."+nome);
}
