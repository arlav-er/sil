
window.onerror = null;
window.defaultStatus = '';

var N = navigator.appName;
var V = parseInt(navigator.appVersion);
var loaded=0;
var n3 = (N == "Netscape" && V < 4);
var i3 = (N == "Microsoft Internet Explorer" && V < 4);
var n4 = (N == "Netscape" && V >= 4);
var i4 = (N == "Microsoft Internet Explorer" && V >= 4);

if (n3 || i3)
{
	window.location = "/best_view.htm";
}


var ActiveLayer="";
var ActiveMenu="";

var ActiveSubLayer="";
var ActiveSubMenu="";

var X=0;
var Y=0;

var ActivePopup = "";


var loading = true;


function SaveMenu ()
{
	this.oButton=null;
	this.oItems=null;
}

LastOpen = new Array ();
for (x=0;x < 5;x++)
	LastOpen[x] = new SaveMenu ();


if (n4 || i4)
{
	if (navigator.appName == "Netscape") 
	{
		layerStyleRef="layer.";
		layerRef="document.layers";
		styleSwitch="";
	}
	else
	{
		layerStyleRef="";
		layerRef="document.all";
		styleSwitch=".style";
	}
}


if (n4)
{
	document.captureEvents(Event.MOUSEMOVE);
	document.onMouseMove = get_pos;
	
	// window.captureEvents(Event.RESIZE);
	// window.onResize = resize;
}
if (i4)
{
	document.onclick = onClick;
}

function onClick ()
{
	if (ActivePopup.indexOf("_nah_") == -1) //non chiude automaticamente i popup che contengono _nah_ nel nome ( NoAutomaticHide! )
	{
		closePopup ();
	}
}

function getIndex(el) 
{
	ind = null;
	for (i=0; i<document.layers.length; i++) 
	{
		whichEl = document.layers[i];
		alert ("loop " + whichEl.id);
		if (whichEl.id == el) {
			ind = i;
			break;
		}
	}
	return ind;
}


function arrange()
{
	alert ("dentro 0");

	firstInd = getIndex('T0');
	alert ("dentro " + firstInd);
	
	nextY = document.layers[firstInd].pageY;
	// + document.layers[firstInd].document.height;
	alert ("dentro 2");
	
	
	for (i=firstInd+1; i<document.layers.length; i++)
	{
		alert ("cesare");
		whichEl = document.layers[i];
		if (whichEl.visibility != "hide") 
		{
			alert ("dentro if");
			whichEl.pageY = nextY;
			nextY += whichEl.document.height;
		}
	}
}



function setClass(obj,style)
{
	obj.className = style;
}


function setClassHover(obj)
{
	obj.className = obj.className+"Hover";
}

function setClassNormal(obj)
{
	s = new String (obj.className);
	obj.className = s.replace("Hover","");
}

function setAddress (address)
{
	if (window.parent != null)
		window.parent.location=address;
	else
		window.location=address;
}

function SetMenuLevel (obj,level)
{
	if (i4)
		obj.MenuLevel = level;
}



function getKey (str)
{
	start = str.indexOf ("key=");
	s = new String ("");
	
	if (start != -1)
		s = str.substr (start);
	
	return s;
}


function MustOpen (oItem,address)
{
	var ret = false;
	var level = 0;
	
	if (oItem == null)
		return ret;

	var child = oItem.firstChild;
	
	while (child != null)
	{
		if (child.href != null)
		{
			s = new String (child.href);
			if (s.indexOf ("javascript") == -1 &&
			    s.indexOf (".gif") == -1 &&
			    s.indexOf (".jpg"))
			{
				if (s.indexOf ("key=") == -1) // full address
				{
					if (address.indexOf (child.href) != -1 )
					{
						oItem.className="TreeActive";
						child.className="TreeActive";
						ret=true;
					}
				}
				else // where is a key !
				{
					if (getKey (address) == getKey (child.href))
					{
						oItem.className="TreeActive";
						child.className="TreeActive";
						ret=true;
					}
				}
			}
		}
				
		if (MustOpen (child,address))
			ret = true;
	
		child=child.nextSibling;
	}
	
	return ret;
}


function SetStartup (MaxMenu,image_open)
{
	var x;
	var nameItem;
	var nameButton;
	var oItem;
	var oButton;

	if (n4)
		return;

	address = new String (document.location);

	for (x=0;x < MaxMenu;x++)
	{
		nameItem="T"+x;
		nameButton="B"+x;
		nameMenu="MT"+x;
		
		oItem   = document.all[nameItem];
		oButton = document.all[nameButton];
		oMenu   = document.all[nameMenu];
		
		ok = MustOpen (oItem,address);
		
		if (ok)
		{
			if (oMenu.MenuLevel <= 1)
				oMenu.className = "TreeMenuActive";
			else
				oMenu.className = "TreeActive";
			
			oItem.style.display = "block";
			oButton.src = image_open+oMenu.MenuLevel+".gif";
			LastOpen[oMenu.MenuLevel].oButton=oButton;
			LastOpen[oMenu.MenuLevel].oItems=oItem;
			
		}
	}
	
}

function ToggleDisplay(level,oButton,oItems,image_close,image_open)
{
	var SingleMenu = true;

	if (n4)
	{

		window.alert ("elemento "+ oItems);

		whichEl = eval("document." + oItems);
		// whichIm = eval("document.images['"+oButton+"']");
		window.alert (whichEl);
		
		
		if (whichEl.visibility == "hide")
		{
			whichEl.visibility = "show";
			window.alert ("mostra");
		}
		else 
		{
			whichEl.visibility = "hide";
			window.alert ("nascondi");
		}
		
	}

	if (i4)
	{
		MenuItem = document.all["M"+oItems.id];
	
		if ((oItems.style.display == "") || (oItems.style.display == "none"))	
		{
		
			// MenuItem.className = "TreeMenuActive";
			oItems.style.display = "block";
			oButton.src = image_open;
	
	
			// chiude i menù dello stesso livello
			if (LastOpen[level].oItems != null)
				ToggleDisplay (level,LastOpen[level].oButton,LastOpen[level].oItems,image_close,image_open);
	
		
			if (SingleMenu == true)
			{		
				LastOpen[level].oButton=oButton;
				LastOpen[level].oItems=oItems;
			}
			
		}	
		else 
		{
			oItems.style.display = "none";
			oButton.src = image_close;
	
			LastOpen[level].oButton=null;
			LastOpen[level].oItems=null;
		}
	}
}



function doShow (obj)
{
NS4 = (document.layers) ? 1 : 0;
IE4 = (document.all) ? 1 : 0;

doTransIE(obj,5,0.3,1);

/*

		if (IE4) setTimeout('doTrans("'+obj+'",'+12+','+2+',0)',10);
		if(NS4){doTrans("davanti",12,2,0);on=false;}
		front_view=false;

		if (IE4) setTimeout('doTrans("davanti",'+12+','+2+',1)',10);
		if(NS4){doTrans("davanti",12,2,1);on=true;}
		front_view=true;
*/

}

// ---------------------------
function resize (evnt)
{
	if (!loading)
		window.history.go(0);
		
	loading = false;
		
}
// ---------------------------
function get_pos(evnt)
{
	X = evnt.pageX;
	Y = evnt.pageY;

	// try to find submenu
	if (ActiveLayer != "")
	{
		var parent = document.layers[ActiveLayer];
		var menu = parent.above;
		while (menu)
		{
			if (menu.parentLayer == parent)
			{
				left   = menu.pageX;
				top    = menu.pageY;
				var right  = left + menu.clip.right;
				var bottom = top  + menu.clip.bottom;

				if (X  >= left && X <= right &&
					Y  >= top  && Y <= bottom )
				{
			
					name = menu.name;
					if (name.substr (0,1) == "M")
					{
						name = name.substr (1);
						showSubLayer(name);
						return;
					}
			
				}
			}
			
			menu = menu.siblingAbove;
		}
		
		if (ActiveSubLayer != "")
			hideThisSubLayer ();

	}

	var x = 0;
	var max = document.layers.length;

	for (x=0; x < max;x++)
	{
		l = document.layers[x];
		name = l.name;
		if (name.substr (0,1) == "M")
		{
			var left   = l.pageX;
			var top    = l.pageY;
			var right  = left + l.clip.right;
			var bottom = top  + l.clip.bottom;

			if (X  >= left && X <= right &&
				Y  >= top  && Y <= bottom )
			{
				name = name.substr (1);
				showLayer(name);
				return;
			}
		}
	}
	
	if (ActiveLayer != "")
		hideThisLayer ();
	
}


// ----------------------------------
function showLayer (layer)
{
	if (i4)
		showLayerEx (layer,0,5)
	if (n4)
	{
		var name = 'M'+layer;
		var xOff = document.layers[name].xOff;
		var yOff = document.layers[name].yOff;
		
		showLayerEx (layer,xOff,yOff);
		
	}	
		
}

// ----------------------------------
function openPopup (layer)
{
	var xOff = 0;
	var yOff = 0;


	if (n4 || i4) 
	{

		if (ActivePopup != "")
		{	
			eval(layerRef+'["'+ActivePopup+'"]'+styleSwitch+'.visibility="hidden"');
		}

		ActivePopup = 'L'+ layer;

	
		if (i4)
		{

			var y = ((window.screen.availHeight-100) - document.all[ActivePopup].clientHeight) / 2;
	
			if (y < 20)
				y = 20;
				
			y += body.scrollTop;
	
			
			document.all[ActivePopup].style.left=
				(window.screen.width - document.all[ActivePopup].clientWidth) / 2;

			document.all[ActivePopup].style.top=y;
			
			document.all[ActivePopup].style.visibility='visible';
		
		
		}
		if (n4)
		{

			var y = ((window.innerHeight) - document.layers[ActivePopup].clip.width) / 2;

			y += window.pageYOffset;
			var x = window.innerHeight/2;
			
			if ((y + document.layers[ActivePopup].clip.height)> document.height)
				y = document.height - (document.layers[ActivePopup].clip.height + 50);

		
			document.layers[ActivePopup].pageX = x;
			document.layers[ActivePopup].pageY = y;
			document.layers[ActivePopup].visibility = true;
		
		}
	}
}

// ----------------------------------
function openPopupFrame (layer,FrameSize)
{
	var xOff = 0;
	var yOff = 0;	
	if (n4 || i4) 
	{

		if (ActivePopup != "")
		{	
			eval(layerRef+'["'+ActivePopup+'"]'+styleSwitch+'.visibility="hidden"');
		}

		ActivePopup = 'L'+ layer;
		ActiveMenu  = 'M'+ layer;
		
		if (i4)
		{

			var x= window[ActiveMenu].offsetLeft + xOff;
			var y= window[ActiveMenu].offsetTop+window[ActiveMenu].offsetHeight+yOff;	
			var parent;

			for (parent=window[ActiveMenu].offsetParent;
				 parent != null;parent=parent.offsetParent)
			{
			   x = x + parent.offsetLeft;
			   y = y + parent.offsetTop;
			}

			// document.all[ActivePopup].style.left=x;
			// document.all[ActivePopup].style.top=y+5;
			
			document.all[ActivePopup].style.left=
				(window.screen.width - document.all[ActivePopup].clientWidth) / 2 + FrameSize;

			document.all[ActivePopup].style.top=body.scrollTop + 
				((window.screen.availHeight-100) - document.all[ActivePopup].clientHeight) / 2;

			
			document.all[ActivePopup].style.visibility='visible';
		
		
		}
		if (n4)
		{
			x = document.layers[ActiveMenu].pageX + xOff;
			y = document.layers[ActiveMenu].pageY + document.layers[ActiveMenu].clip.height+yOff;
			
			document.layers[ActivePopup].left = x;
			document.layers[ActivePopup].top = y;
			document.layers[ActivePopup].visibility = true;
		}
	}
}

// ----------------------------------

function closePopup ()
{
	if (ActivePopup != "")
	{	
		
		eval(layerRef+'["'+ActivePopup+'"]'+styleSwitch+'.visibility="hidden"');
	}
	
	ActivePopup = "";
	
}

// ----------------------------------
function showLayerEx (layer,xOff,yOff)
{

	if (n4 || i4) 
	{

		if (ActiveLayer != "")
		{	
			eval(layerRef+'["'+ActiveLayer+'"]'+styleSwitch+'.visibility="hidden"');
		}

		ActiveLayer = 'L'+ layer;
		ActiveMenu  = 'M'+ layer;
		
		if (i4)
		{
		
			var x= window[ActiveMenu].offsetLeft + xOff;
			var y= window[ActiveMenu].offsetTop+window[ActiveMenu].offsetHeight+yOff;	
			var parent;

			for (parent=window[ActiveMenu].offsetParent;
				 parent != null;parent=parent.offsetParent)
			{
				
			   x = x + parent.offsetLeft;
			   y = y + parent.offsetTop;
			}

			// document.all[ActiveLayer].style.left=x;
			document.all[ActiveLayer].style.left=x-22;
			document.all[ActiveLayer].style.top=y;
			// doTransIE(ActiveLayer,5,0.3,1);
			document.all[ActiveLayer].style.visibility='visible';
		
		}
		if (n4)
		{
			x = document.layers[ActiveMenu].pageX + xOff;
			y = document.layers[ActiveMenu].pageY + document.layers[ActiveMenu].clip.height+yOff;
			
			document.layers[ActiveLayer].left = x;
			document.layers[ActiveLayer].top = y;
			document.layers[ActiveLayer].visibility = true;
		}
	}
}

// ---------------------------
function showSubLayer(layer)
{
	if (n4 || i4) 
	{
		if (ActiveSubLayer != "")
		{	
			eval(layerRef+'["'+ActiveSubLayer+'"]'+styleSwitch+'.visibility="hidden"');
		}
	
		ActiveSubLayer = 'L'+ layer;
		ActiveSubMenu  = 'M'+ layer;
		
		if (i4)
		{
			document.all[ActiveSubMenu].style.color='red';
		
			var x= window[ActiveSubMenu].offsetLeft+window[ActiveSubMenu].offsetWidth;
			var y= window[ActiveSubMenu].offsetTop-1;	
			var parent;
			
			for (parent=window[ActiveSubMenu].offsetParent;
				 parent != null;parent=parent.offsetParent)
				 {
					x = x + parent.offsetLeft;
					y = y + parent.offsetTop;
				 }

			document.all[ActiveSubLayer].style.left=x;
			document.all[ActiveSubLayer].style.top=y;
			doTransIE(ActiveSubLayer,6,0.3,1);
//			document.all[ActiveSubLayer].style.visibility='visible';
		
		}
		if (n4)
		{
			var parent = document.layers[ActiveLayer];
			var menu = parent.above;
			
			while (menu)
			{
				if (menu.name == ActiveSubMenu)
					break;
				menu = menu.siblingAbove;
			}

			if (menu)
			{
				x = parent.left + parent.clip.right - 15;
				y = menu.pageY + 2;
			
				document.layers[ActiveSubLayer].left = x;
				document.layers[ActiveSubLayer].top = y;
				document.layers[ActiveSubLayer].visibility = true;
			}
		}
	}
}


// ---------------------------
function hideThisLayer()
{
	hideThisLayerEx(0,5);
}

// ---------------------------
function hideThisLayerEx(xOff,yOff)
{
	if (n4 || i4) 
	{

		if (ActiveSubLayer == "") // can hide only in no submenu open !!
		{
			var element;
			var parent = null;
				
			if (i4)
			{
				X = window.event.x+xOff;
				Y = window.event.y+yOff;
				
				element = document.elementFromPoint(X,Y);
				
				
				if (element)
				{
					for (parent=element.parentElement;
						 parent!= null;parent=parent.parentElement)
						if (parent.id == ActiveLayer)
							break;
				}
			}
				
			if (n4)
			{
				Y = Y+yOff;
				x = x+xOff;
					
				var left   = document.layers[ActiveLayer].left;
				var top    = document.layers[ActiveLayer].top;
				var right  = document.layers[ActiveLayer].left + 
							 document.layers[ActiveLayer].clip.right;
				var bottom = document.layers[ActiveLayer].top + 
							 document.layers[ActiveLayer].clip.bottom;

				if (X  >= left && X <= right &&
					Y  >= top  && Y <= bottom )
					parent = true;
			}
				
			if (!parent)
			{
				if (i4)
					// doTransIE(ActiveLayer,4,0.3,0);
					eval(layerRef+'["'+ActiveLayer+'"]'+styleSwitch+'.visibility="hidden"');
				else
					eval(layerRef+'["'+ActiveLayer+'"]'+styleSwitch+'.visibility="hidden"');
				ActiveLayer="";
			}
		}
	}
}

// ---------------------------
function hideThisSubLayer()
{
	if (n4 || i4)
	{	
		var element;
		var parent = null;
		var x,y;
		
		if (i4)
		{
			x = window.event.x;
			y = window.event.y;
			element = document.elementFromPoint(x,y);
			
			if (element)
			{
				for (parent=element.parentElement;
					 parent!= null;parent=parent.parentElement)
					if (parent.id == ActiveSubLayer)
						break;
			}
		}
		
		if (n4)
		{
					
			var left   = document.layers[ActiveSubLayer].left;
			var top    = document.layers[ActiveSubLayer].top;
			var right  = document.layers[ActiveSubLayer].left + 
						 document.layers[ActiveSubLayer].clip.right;
			var bottom = document.layers[ActiveSubLayer].top + 
						 document.layers[ActiveSubLayer].clip.bottom;
	
			if (X  >= left && X <= right &&
				Y  >= top  && Y <= bottom )
				parent = true;
		}
		
		
		if (!parent)
		{
			if (i4)
				doTransIE(ActiveSubLayer,7,0.3,0);
			else
				eval(layerRef+'["'+ActiveSubLayer+'"]'+styleSwitch+'.visibility="hidden"');
				
			ActiveSubLayer="";
			hideThisLayer ();
		}
	}
}


// ---------------------------
function showThisLayer()
{
	if (n4 || i4 && ActiveLayer != "")
		eval(layerRef+'["'+ActiveLayer+'"]'+styleSwitch+'.visibility="visible"');
}

// ---------------------------
function showThisSubLayer()
{
	if (n4 || i4 && ActiveSubLayer != "")
		eval(layerRef+'["'+ActiveSubLayer+'"]'+styleSwitch+'.visibility="visible"');
}


// ---------------------------
function openNewWindow(address,name)
{
	window.open (address,name,"dependent=yes,toolbar=no,status=no,scrollbars=yes,resizable=yes,alwaysLowered=yes");

}

