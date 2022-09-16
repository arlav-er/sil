var collezione = new Array;
var pos_titolo = 10;
var pos_layer=20;
var n_layer = 0;
var n_righe_titolo = 0;
var n_celle_per_riga = 6;
var h_riga = 26;
var pre_titolo = "tit_";
var col_null = 0;
var dim_tab = 0;


function init (p_col,p_tab)
{
	col_null = p_col;
	dim_tab = p_tab;
}

function classLayer(nome,testata)
{
	var nome = ""+nome;
	var titolo = pre_titolo+testata;
	var testata = "";
	var n_righe = 0;
}

function nascondiAllLayer()
{
	for (var i=0; i<this.n_layer ; i++)
	{
		nascondiLayer(this.collezione[i].nome);
		nascondiLayer(this.collezione[i].titolo);
	}
}

function addLayer(nome,testata)
{
	this.collezione[n_layer] = new classLayer(nome,testata);
	this.collezione[n_layer].nome = ""+nome;
	this.collezione[n_layer].titolo = pre_titolo+nome;
	this.collezione[n_layer].testata = testata;
	this.collezione[n_layer].n_righe = 0;
	
	n_layer++;
	if (n_layer%n_celle_per_riga ==1)
		n_righe_titolo++;
}

function addRiga(n_lay)
{
	this.collezione[n_lay].n_righe ++;
}

function getDefinizioneLayer(n_lay, posY, p_visibile)
{
	var visibile = "visible";
	if (!p_visibile)
		visibile = "hidden";
		
	posY=posY+pos_layer;
	return "#"+this.collezione[n_lay].nome+" {POSITION: absolute; Z-INDEX: 20; VISIBILITY: "+visibile+";  LEFT:"+col_null+"px; TOP:"+posY+"px;}";
	
}
	
function getDefinizioneTitolo(n_lay, posY, p_visibile)
{
	var code = "";
	var visibile = "visible";
	if (!p_visibile)
		visibile = "hidden";
		
	code = "#"+this.collezione[n_lay].titolo+" {POSITION: absolute; Z-INDEX: 30; VISIBILITY: "+visibile+";  LEFT:"+col_null+"px; TOP:"+posY+"px;}";
	return code;
}

function getRowTitolo(pos_first_lay, pos_lay_visible)
{
	var code_html = "";
	var first_row = "0";
	var ret = new Array;
	var curr_lay = pos_first_lay;
	code_html = code_html+"<tr height='"+this.h_riga+"'>";
	for (var ctr = 0; ctr < this.n_celle_per_riga; ctr++)
	{
		if (curr_lay >= this.n_layer)
			code_html = code_html +"	<td height='"+this.h_riga+"' class='TITLAYNOSEL'>&nbsp;</td>";
		else
		{
			var lay = this.collezione[curr_lay];
			if (pos_lay_visible!=null && pos_lay_visible==curr_lay)
			{
				code_html = code_html +"	<td height='"+this.h_riga+"' class='TITLAYSEL'>"+lay.testata+"</td>";
				first_row = "1";
			}
			else
				code_html = code_html +"	<td height='"+this.h_riga+"' class='TITLAYNOSEL'><a class='TITLAYNOSEL' HREF=\"javascript:vedi('"+lay.titolo+"','"+lay.nome+"',"+curr_lay+")\">"+lay.testata+"</a></td>";
			curr_lay++;
		}	
	}
	code_html = code_html+"</tr>";
	ret[0]= first_row;
	ret[1] = code_html;
	return ret;
}

//scrive le definizioni dei Layer nella sezione text/css
function getDefinizioneLayers()
{
	var code_html = "<STYLE TYPE='text/css'>";
	var visibile = true;
	for (var ctr=0; ctr<this.n_layer;ctr++)
	{
		var lay = this.collezione[ctr];
		if (lay!=null)
		{
			code_html = code_html + getDefinizioneTitolo(ctr, pos_titolo, visibile);
			code_html = code_html + getDefinizioneLayer(ctr, pos_titolo + n_righe_titolo*this.h_riga, visibile);
			visibile = false;
		}
	}
	code_html = code_html+"#pulsanti {POSITION: absolute; Z-INDEX: 50; VISIBILITY: visible; LEFT:"+col_null+"px; TOP:"+pos_titolo + (n_righe_titolo+this.collezione[0].n_righe)*this.h_riga+"px;}";
	code_html = code_html+ "</STYLE>";
	document.write(code_html);
}

//scrive le sazioni dei titoli
function getTitolo(pos_lay_visibile)
{
	var code_html = "";
	code_html = code_html +"<div id='"+this.collezione[pos_lay_visibile].titolo+"'>";
	code_html = code_html +"<table cellpadding='1' cellspacing='1' border='0' width='"+(dim_tab - col_null)+"'>";
	var rows = "";
	for (var ctr=0; ctr<this.n_layer;ctr= ctr + this.n_celle_per_riga)
	{
		var ret = getRowTitolo(ctr,pos_lay_visibile);
		if (ret[0]=="0")
			rows = ret[1]+rows;
		else
			rows = rows+ret[1];
	}
	code_html = code_html+rows;
	code_html = code_html+"</table>";
	code_html = code_html+"</div>";
		
	return (code_html);
}

//
function getDivTitoli()
{
	var code_html = "";
	for (var ctr=0; ctr<this.n_layer;ctr++)
	{
		var lay = this.collezione[ctr];
		if (lay!=null)
		{
			code_html = code_html + getTitolo(ctr);
		}
	}
	document.write(code_html);
}

function newLine(pos_lay)
{
	addRiga(pos_lay);
	document.write ("<tr height='"+h_riga+"'>");
}

function endLine(pos_lay)
{
	document.write ("</tr>");
}



function vediLayer(lay)
{
	if (document.all)
		document.all[lay].style.visibility="visible";
	else if (document.layers)
		document.layers[lay].visibility="visible";
	else if (document.getElementById)
	{
	 	var obj = document.getElementById(lay);
	 	obj.style.visibility="visible";
	}
}
function nascondiLayer(lay)
{
	if (document.all)
	{
		document.all[lay].style.visibility= "hidden";
	//	document.all[lay] = "rect(75px 75px 75px 75px)";
	}
	else if (document.layers)
	{
//		document.layers[lay].clip.bottom = document.layers[lay].clip.top;
	//	document.layers[lay].clip.left = document.layers[lay].clip.right;
		document.layers[lay].visibility="hidden";
	}
	else if (document.getElementById)
	{
	 	var obj = document.getElementById(lay);
	 	obj.style.visibility="hidden";
	}
}

function spostaLayer(lay,pos_lay_visible)
{
	if (pos_lay_visible!=null && this.collezione[pos_lay_visible]!=null)
	{
		pos_y = (n_righe_titolo+this.collezione[pos_lay_visible].n_righe)*h_riga+pos_titolo;
		pos_y=pos_y+pos_layer;
		if (document.all)
			document.all[lay].style.top=pos_y+'px';
		else if (document.layers)
			document.layers[lay].top=pos_y;
		else if (document.getElementById)
		{
		 	var obj = document.getElementById(lay);
		 	obj.style.top=pos_y+'px';
		}
	}
}

function vedi(lay1,lay2,pos)
{  
	nascondiAllLayer();  
	vediLayer(lay1);
	vediLayer(lay2);
	spostaLayer('pulsanti',pos);
}

function creaVariabiliHidden(form_obj, nome_form_principale)
{
	var n_ele = form_obj.elements.length;
	if (form_obj.name != nome_form_principale)
	{
		for (i_ele=0; i_ele<n_ele; i_ele++)
		{
			var nome = form_obj.elements[i_ele].name;
			document.write("<input type='hidden' name='"+nome+"' value=''>");
		}
	}
}

function caricaVariabiliHidden(nome_form_dest,form_obj)
{
	var n_ele = form_obj.elements.length;
	if (form_obj.name != 'lay_form')
	{
		for (i_ele=0; i_ele<n_ele; i_ele++)
		{
			var nome = form_obj.elements[i_ele].name;
			var valore = form_obj.elements[i_ele].value;
			var obj_ele = eval (nome_form_dest+"."+nome);
			obj_ele.value = valore;
		}
	}
}

function loadHidden(nome_form_principale)
{
	if  (document.layers)
	{
		var n_lay = this.document.layers.length;
		if (n_lay >= 1 )
		{
			for (i_lay=0; i_lay<n_lay; i_lay++)
			{
				var doc = this.document.layers[i_lay].document;
				if (doc.forms!=null && doc.forms.length > 0)
				{
					creaVariabiliHidden(doc.forms[0],nome_form_principale);
				}
			}
		}
		
		return;
	}
	var n_frame = this.document.forms.length;
	if (n_frame > 1 )
	{
		for (i_frame=0; i_frame<n_frame; i_frame++)
		{
			creaVariabiliHidden(this.document.forms[i_frame],nome_form_principale);
		}
	}
	
}

function loadValue(nome_form_principale)
{
	if  (document.layers)
	{
		var n_lay = this.document.layers.length;
		if (n_lay >= 1 )
		{
			for (i_lay=0; i_lay<n_lay; i_lay++)
			{
				var doc = this.document.layers[i_lay].document;
				if (doc.forms!=null && doc.forms.length > 0)
				{
					caricaVariabiliHidden("this.document."+nome_form_principale,doc.forms[0]);
				}
			}
		}
		
		return;
	}
	var n_frame = this.document.forms.length;
	if (n_frame > 1 )
	{
		for (i_frame=0; i_frame<n_frame; i_frame++)
		{
			caricaVariabiliHidden("this.document."+nome_form_principale,this.document.forms[i_frame]);
		}
	}
}
