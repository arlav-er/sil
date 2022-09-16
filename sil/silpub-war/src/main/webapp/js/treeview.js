xImgs=new Array(10);
flagGif2=false;
UniqueID="Pagina_utente";
DocRoot="";
ImgRoot="../../img/";
ImgWidth=14;
ImgHeight=18;
EntryHeight=ImgHeight;
InitialKey="d+0";
LinkCurrPage=true;
TreeRootHint="";
NormalPageHint="";
LinkedPageHint="";
OpenBookHint="chiudi";
ClosedBookHint="apri";
OpenBookStatus="Chiudi";
ClosedBookStatus="Apri";
lngStringaMassima=18;
Skip=3;
newLine="&#;";

function initTree (text,key,link,opts)
{
	initTreeView();
	treePfx="";
	idx(text,key,link,(is(opts,"cntd.")?"/":(is(opts,"img")?xImg(opts):(is(opts,"link")?"R":"r") ) ),"",opts,text);
}

function initTreeView()
{
	if(self.TVinitd)
	{
		return
	};
	if(""+window.innerWidth != "0")
	{
		tmpTopName=top.name;
		cutPos=UniqueID.length;
		if(tmpTopName.length > cutPos)
		{
			tmpTopName=tmpTopName.substring(0,cutPos);
		}
	}
	isOpera=(myIndexOf(navigator.userAgent,"Opera") > -1);
	if((navigator.appName=="Netscape")&& (navigator.appVersion.charAt(0)=="2"))
		CurrPageFG="#339933\"><B><CurrPage=\"YES";
	isDHTML=(document.all || document.layers);
	if((navigator.appName=="Netscape")&& (navigator.appVersion.charAt(0)=="4")&& isMac()) 
		isDHTML=false;
	if(document.layers && document.preamble) 
		TVtop=document.preamble.clip.bottom;
	else if(document.all && document.all.preamble) 
		TVtop=document.all.preamble.offsetHeight;
	else 
		TVtop=0;
	TVcount=0;
	TVentries=new Array();
	TVkeys=new Array();
	TVinitd=true;
	showKey=printBuffer="";
	wrt("");
	currPosY=TVtop;	
	splitPrm();
}

function splitPrm()
{
	input="";
	if(top.key) 
		input=""+ top.key;	
	if((input=="") || (myIndexOf(input,"<object") > -1)) 
		input=InitialKey;
	pos=myIndexOf(input,"+");
	if(pos < 0) 
		viewKey="";
	else 
	{
		viewKey=input.substring(pos+1);
		input=input.substring(0,pos);
	}
	if(input=="") 
		input=".+.";
	prm=input;
	dontVKey=false;
}

function DHTMLTreeView(currKey,text_status)
{
	if(!isDHTML) 
		return false;
	TVkeys[currKey]=newVis=(!TVkeys[currKey]);
	currPosY=TVtop;	
	TVelemTop=TVelemBtm=0;
	for (var j=1;j < viewKey.length;j++)	
		if(!dontVKey)
		{
			var viewSub=viewKey.substring(0,j);
			for (var i=1;i <= TVcount;i++) 
				if(!TVkeys[viewSub]) 
					TVentries[i][0] &= (TVentries[i][2] != viewSub);
			TVkeys[viewSub]=true;
		}
	if(TVkeys[currKey] != newVis) 
		dontVKey=true;
	TVkeys[currKey]=newVis;
	for (var i=1;i <= TVcount;i++)
	{
		var tmpKey=TVentries[i][2];
		var isVisible=true;
		for (var j=1;j < tmpKey.length;j++) 
			isVisible &= TVkeys[tmpKey.substring(0,j)];
		if(isVisible)
		{
			TVentries[i][0] &= ((tmpKey != currKey) && (tmpKey != viewKey));
			if(document.layers)
			{
				thisTop=document.layers["TV"+i].pageY;
				if(thisTop != currPosY) 
					document.layers["TV"+i].top=currPosY;
			}
			else 
			{
				thisTop=document.all["TV"+i].offsetTop;
				if(thisTop != currPosY) 
					document.all["TV"+i].style.top=currPosY;
			}
			if(tmpKey==showKey)
			{
				TVelemTop=TVelemBtm=currPosY;
				if (isMac())
				{
					if(document.all)document.all["TV"+i].style.top=currPosY;
					reloadImg(currKey);
				}
			}
			if((tmpKey.substring(0,showKey.length)==showKey) && (currPosY > TVelemBtm))
				TVelemBtm=currPosY;	
			currPosY += EntryHeight;
			if(!TVentries[i][0])
			{
				treePfx=TVentries[i][4];
				prm=(TVkeys[tmpKey]?tmpKey:tmpKey.substring(0,tmpKey.length-1));
				var retVal=wrtIdx(text_status, TVentries[i][1],tmpKey,TVentries[i][3],TVentries[i][5],TVentries[i][6],i);
				if(document.all)
				{
					if((!isMac()) || (tmpKey!=showKey)) 
						document.all["TV"+i].innerHTML=retVal;	
				}
				else 
					with (document.layers["TV"+i].document) 
					{
						clear();	
						write(retVal);
						close();
					}
				TVentries[i][0]=(tmpKey != viewKey);
			}
		}
		if(document.layers) 
			document.layers["TV"+i].visibility=(isVisible?"show":"hide");
		else 
			document.all["TV"+i].style.display=(isVisible?"block":"none");
	}
	if(TVelemTop > 0)
	{
		TVelemBtm += EntryHeight;	
		if(document.layers)	
		{
			var ScreenTop=window.pageYOffset;
			var ScreenBtm=ScreenTop+window.innerHeight;
		} 
		else	
		{
			var ScreenTop=this.document.body.scrollTop;
			var ScreenBtm=ScreenTop+this.document.body.clientHeight;
		}
		if((TVelemBtm > ScreenBtm) || (TVelemTop < ScreenTop))
		{
			var scrollTo=ScreenTop+TVelemBtm - ScreenBtm;
			if(TVelemTop < scrollTo) 
				scrollTo=TVelemTop;
			window.scrollTo(0,scrollTo);
		}
	}
	return true;
}

function reloadImg(name,img)
{
	var id = retIndexImg(name);
	if (id <= 0) 
		return;
	var Xsrc = document.all.tags("IMG")(id).src;
	var idx_pre = Xsrc.indexOf("ix_");
	if (idx_pre < 0) 
		return;
	var idx_suf = Xsrc.indexOf(".gif");
	if (idx_suf < 0) 
		return;
	var src_final = Xsrc.substring(0,idx_pre);
	var src_img = Xsrc.substring(idx_pre,idx_suf);
	if (src_img.charAt(src_img.length-1) == 'p')
	{
		src_img = src_img.substring(0,src_img.length-1)+"m";
		document.all.tags("IMG")(id).alt = OpenBookHint;
	}
	else if (src_img.charAt(src_img.length-1) == 'm')	
	{
		src_img = src_img.substring(0,src_img.length-1)+"p";
		document.all.tags("IMG")(id).alt = ClosedBookHint;
	}
	src_final = src_final + src_img + ".gif";
	document.all.tags("IMG")(id).src = src=src_final;
}

function retIndexImg(name)
{
	for (ctr=1; ctr < document.all.tags("IMG").length;ctr++)
	{
		if (document.all.tags("IMG")(ctr).name == name)
		{
			return ctr;
		}
	}
	return -1;
}

function img (image,hint, name)
{
	return "<IMG name=\""+name+"\" SRC=\""+ImgRoot +"ix_"+ image +".gif\" ALT=\""+ hint +"\" BORDER=\"0\""+" WIDTH=\""+ ImgWidth +"\" HEIGHT=\""+ ImgHeight +"\">";
}

function tree (code,nameImg)
{
	var ret="";
	if(myIndexOf(code,"null") > -1) 
		return "";
	for (var i=0;i < code.length;i++)
	{
		var c=code.charAt(i);
		if(c>='0' && c<='9') 
			ret+=img(xImgs[c],"","");	
		if(!self.CompactTree)
		{
			if(c=='l') 
				ret+=img("list","",nameImg);
			if(c=='L') 
				ret+=img("end","",nameImg);
		}
		if(flagGif2==true)
		{
			if(c=='.') 
				ret+=img("space","","");
			if(c=='/') 
				ret+=img("line","","");
			if(c=='r') 	
				ret+=img("open",TreeRootHint,nameImg);
			if(c=='R') 
				ret+=img("link",TreeRootHint,nameImg);
			if(c=='#') 
				ret+=img("leaf",NormalPageHint,"");
			if(c=='x') 
				ret+=img("link",LinkedPageHint,nameImg);
			if(c=='b') 
				ret+=img("book",ClosedBookHint,nameImg);
			if(c=='+') 
				ret+=img("listp",ClosedBookHint,nameImg);
			if(c=='*') 
				ret+=img("endp",ClosedBookHint,nameImg);
			if(c=='o') 
				ret+=img("open",OpenBookHint,nameImg);
			if(c=='-') 
				ret+=img("listm",OpenBookHint,nameImg);
			if(c=='_') 
				ret+=img("endm",OpenBookHint,nameImg);
		}
		else
		{
			if(c=='.') 
				ret+=img("space","","");
			if(c=='/') 	
				ret+=img("line","","");
			if(c=='R') 
				ret+=img("link",TreeRootHint,nameImg);
			if(c=='x') 
				ret+=img("link",LinkedPageHint,nameImg);
			if(c=='+') 
				ret+=img("listp",ClosedBookHint,nameImg);
			if(c=='*') 
				ret+=img("endp",ClosedBookHint,nameImg);
			if(c=='-') 
				ret+=img("listm",OpenBookHint,nameImg);
			if(c=='_') 
				ret+=img("endm",OpenBookHint,nameImg);
		}
	}	
	return ret;
}

function tree_line (code,nameImg)
{
	var ret="";
	if(myIndexOf(code,"null") > -1) 
		return "";
	for (var i=0;i < code.length;i++)
	{
		var c=code.charAt(i);
		if(c>='0' && c<='9') 
			ret+=img(xImgs[c],"",nameImg);
		if(!self.CompactTree)
		{
			if(c=='l') 
				ret+=img("line","","");
			if(c=='L') 
				ret+=img("space","","");
		}
		if(flagGif2==true)
		{
			if(c=='.') 
				ret+=img("space","","");
			if(c=='/') 
				ret+=img("line","","");
			if(c=='r') 
				ret+=img("space",TreeRootHint,"");
			if(c=='b') 
				ret+=img("space",ClosedBookHint,"");
			if(c=='_') 
				ret+=img("endm",OpenBookHint,nameImg);
		}
		else
		{
			if(c=='.') 
				ret+=img("space","","");
			if(c=='/') 
				ret+=img("line","","");
			if(c=='R') 
				ret+=img("link",TreeRootHint,nameImg);
			if(c=='x') 
				ret+=img("link",LinkedPageHint,nameImg);
			if(c=='+') 
				ret+=img("listp",ClosedBookHint,nameImg);
			if(c=='*') 
				ret+=img("endp",ClosedBookHint,nameImg);
			if(c=='-') 
				ret+=img("listm",OpenBookHint,nameImg);
			if(c=='_') 
				ret+=img("endm",OpenBookHint,nameImg);
		}
	}
	return ret;
}

function unquote (text)
{
	var pos=myIndexOf(text,"\"");
	while (pos > -1)
	{
		text=text.substring(0,pos) +"``"+ text.substring(pos+1);
		pos=myIndexOf(text,"\"");
	}
	var pos=myIndexOf(text,"'");
	while (pos > -1)
	{
		text=text.substring(0,pos) +"`"+text.substring(pos+1);
		pos=myIndexOf(text,"'");
	}
	var pos=myIndexOf(text,"<");
	var pos2=myIndexOf(text,">");
	while ((pos > -1) && (pos2 > -1) && (pos < pos2))
	{
		text=text.substring(0,pos)+text.substring(pos2+1);
		pos=myIndexOf(text,"<");
		pos2=myIndexOf(text,">");
	}
	return text;
}

function lnk (xHref,onOver,misc,xText)
{
	return "<A class='TREE' H"+"REF=\""+ xHref+" ONMOUSEOVER=\"window.status='"+ onOver +"';return true\" "+"ONMOUSEOUT=\"window.status='';return true\""+ misc +">"+ xText +"<\/A>";
}

function lnk2 (xHref,onOver,misc,xText)
{
	return  xHref +"\"  "+ misc ;
}

function wrtEntry (tree,key,link,text,TVnr,text_status)
{
	var split=myIndexOf(text,"|");
	if(split < 0)
	{
		var statusText=unquote(text_status);
		var tipText="";
	} 
	else
	{
		var statusText=unquote(text_status.substring(split+1));
		var tipText=" TITLE=\""+ statusText +"\"";	
		text=text.substring(0,split);
	}
	var pos=myIndexOf(text," ");
	while (pos > -1)
	{
		text=text.substring(0,pos) +"&#160;"+text.substring(pos+1);
		pos=myIndexOf(text," ");
	}
	var isCurr=(viewKey==key);
	if(link) 
		link=(link.charAt(0)=="|"?link.substring(1):DocRoot+link);
	if(link && !(isCurr && (isOpera || !LinkCurrPage))) 
		text=lnk(link,statusText,(isCurr?" STYLE=\"color:000000;\"":"")+tipText,(isCurr?""+ text+"":text));
	tableBeg="<TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\"><TR >";
	return tableBeg +"<TD class='TREE' >&#160;<\/TD><TD class='TREE' NOWRAP><NOBR>"+ tree+"<\/NOBR><\/TD><TD>&#160;<\/TD><TD NOWRAP>"+ tableBeg+ (!isCurr?"<TD class='TREE' NOWRAP><NOBR>": "<TD class='TREE'  NOWRAP><NOBR>") +"&#160;"+ text+"&#160;<\/NOBR><\/TD><\/TR><\/TABLE><\/TD><\/TR><\/TABLE>";
}

function index (text_status,newKey,currKey)
{
	var newHash="";
	if(!currKey) 
		showKey="";
	else 
		showKey=currKey;
	if(!currKey || !isDHTML)
	{
		var pos=myIndexOf(newKey,"+");
		if(pos < 0) 
			newHash=newKey +"+"+ viewKey;
		else 
		{
			if(pos > 0) 
				newHash=newKey;
			else 
			{
				var KeyAdd=newKey.substring(1);
				showKey=KeyAdd;
				if(myIndexOf(":"+prm+":",":"+KeyAdd+":") > -1) 
					newHash=prm+newKey;
				else 
					newHash=((prm==".+.")?"":prm +":")+KeyAdd+newKey;
			}
		}
		top.key=newHash;
		splitPrm();
		currKey="";TVkeys[viewKey]=true;
	}
	if(!DHTMLTreeView(currKey,text_status))
	{
		if(isOpera) 
			location.reload();
		else if(document.images) 
			location.replace(location.href);
		else 
			location.href=location.href;
	}
}

function makePrm (currPrm,add,sub)
{
	if(myIndexOf(currPrm," ") > -1) 	
		currPrm=".+.";
	if(add != "") 
		var newPrm=((currPrm==".+.")?"":currPrm +":")+add;
	if(sub != "")
	{
		var newPrm=":"+currPrm+":";
		var cutPos=myIndexOf(newPrm,":"+sub);
		while(cutPos > -1)
		{
			newPrm=newPrm.substring(0,cutPos) +newPrm.substring(myIndexOf(newPrm,":",cutPos+1));
			cutPos=myIndexOf(newPrm,":"+sub);
		}
		if(newPrm==":") 
			newPrm=":*:";
		newPrm=newPrm.substring(1,newPrm.length-1);
	}
	if(myIndexOf(newPrm," ") > -1) 
		newPrm=currPrm;return newPrm;
}

function rld (text_status,currKey,newPrm,treecode,hint)
{
	return lnk("#\" ONCLICK=\"index('"+text_status+"','"+ newPrm +"+"+ viewKey +"','"+ currKey +"');return false\" TARGET=\"_self\"  ",hint,"",treecode);
}

function rld2 (text_status,currKey,newPrm,treecode,hint)
{
	return lnk2("#\" ONCLICK=\"index('"+text_status+"','"+ newPrm +"+"+ viewKey +"','"+ currKey +"');return false\" TARGET=\"_self",hint,"",treecode);
}

function wrtIdx (text_status,text,key,link,prefix,code,TVnr)
{
	var idxRet="";
	var pos=myIndexOf(key," ");
	if(pos > -1) 
		key=key.substring(0,pos);
	var subKey=(key.length > 1?key.substring(0,key.length-1):"");
	currIsVisible=(myIndexOf(":"+prm+":",":"+subKey) > -1);
	if(currIsVisible || isDHTML)
	{
		var codePos=myIndexOf(code,"|");
		if(codePos > -1)
		{
			var prefixPos=myIndexOf(prefix,"|");
			if(myIndexOf(":"+prm+":",":"+key) < 0)
				idxRet=tree(treePfx+(prefixPos < 0?prefix :prefix.substring(prefixPos+1)),key)+rld(text_status,key,makePrm(prm,key,""),tree(code.substring(codePos+1),key),ClosedBookStatus);
			else 
				idxRet=tree(treePfx+(prefixPos < 0?prefix :prefix.substring(0,prefixPos)),key)+rld(text_status,key,makePrm(prm,"",key),tree(code.substring(0,codePos),key),OpenBookStatus);
		} 
		else 
			idxRet=tree(treePfx+prefix+code,key);
		return wrtEntry(idxRet,key,link,text,TVnr,text_status);
	} 
	else 
		return "";
}

function wrtIdx_line (text_status,text,key,link,prefix,code,TVnr)
{
	var idxRet="";
	var pos=myIndexOf(key," ");	
	if(pos > -1) 
		key=key.substring(0,pos);
	var subKey=(key.length > 1?key.substring(0,key.length-1):"");
	currIsVisible=(myIndexOf(":"+prm+":",":"+subKey) > -1);
	if(currIsVisible || isDHTML)
	{
		var codePos=myIndexOf(code,"|");
		if(codePos > -1)
		{
			var prefixPos=myIndexOf(prefix,"|");
			if(myIndexOf(":"+prm+":",":"+key) < 0)
				idxRet=tree_line(treePfx+(prefixPos < 0?prefix:prefix.substring(prefixPos+1)),key)+rld(text_status,key,makePrm(prm,key,""),tree_line(code.substring(codePos+1),key),ClosedBookStatus);
			else 
				idxRet=tree_line(treePfx+(prefixPos < 0?prefix:prefix.substring(0,prefixPos)),key)+rld(text_status,key,makePrm(prm,"",key),tree_line(code.substring(0,codePos),key),OpenBookStatus);
		} 
		else	
		{
			idxRet=tree_line (treePfx+prefix+code,key);
		};
		return wrtEntry(idxRet,key,link,text,TVnr,text_status);
	} 
	else return "";
}

function idx (text,key,link,prefix,code,opts,text_status)
{
	if(!key) 
		key="*";
	if(!text) 
		text="";
	if(link) 
		link += "\" TARGET=\""+ xTarget(opts)+"\"";
	else 
	{	
		var pos=myIndexOf(key," ");
		if(pos > -1) 
			key=key.substring(0,pos);
		var subKey=(key.length > 1?key.substring(0,key.length-1):"");
		currIsVisible=(myIndexOf(":"+prm+":",":"+subKey) > -1);
		if(currIsVisible || isDHTML)
		{
			var codePos=myIndexOf(code,"|");
			if(codePos > -1)
			{
				var prefixPos=myIndexOf(prefix,"|");
				if(myIndexOf(":"+prm+":",":"+key) < 0){
					link=rld2(text_status,key,makePrm(prm,key,""),tree(code.substring(codePos+1),key),ClosedBookStatus);
				}else{ 
					link=rld2(text_status,key,makePrm(prm,"",key),tree(code.substring(0,codePos),key),OpenBookStatus);
				}	
			}
		}
	};
	TVcount++;
	var retVal=wrtIdx(text_status,text,key,link,prefix,code,opts,TVcount);
	if(document.layers) 
		retVal="<LAYER ID=\"TV"+ TVcount+"\" TOP=\""+ currPosY +"\" LEFT=\"0\" VISIBILITY=\""+ (currIsVisible?"show":"hide") +"\">"+ retVal +"<\/LAYER>";
	if(document.all) 
		retVal="<DIV ID=\"TV"+ TVcount +"\""+" STYLE=\"position:absolute;top:"+ currPosY +"px;left:0px;display:"+ (currIsVisible?"block":"none") +";\">"+ retVal +"<\/DIV>";
	wrt(retVal);
	if(currIsVisible) 	
		currPosY += EntryHeight;
	if(isDHTML)
	{
		TVkeys[key]=false;
		TVentries[TVcount]=new Array((viewKey != key),text,key,link,treePfx,prefix,code);
		TVkeys[key.substring(0,key.length-1)]=currIsVisible;
	}
}

function idx_line (text,key,link,prefix,code,opts,text_status)
{
	if(!key) 
		key="*";
	if(!text) 
		text="";
	if(link) 
		link += "\" TARGET=\""+ xTarget(opts)+"\"";
	else 
	{
		var pos=myIndexOf(key," ");
		if(pos > -1) 
			key=key.substring(0,pos);
		var subKey=(key.length > 1?key.substring(0,key.length-1):"");
		currIsVisible=(myIndexOf(":"+prm+":",":"+subKey) > -1);
		if(currIsVisible || isDHTML)
		{
			var codePos=myIndexOf(code,"|");
			if(codePos > -1)
			{
				var prefixPos=myIndexOf(prefix,"|");
				if(myIndexOf(":"+prm+":",":"+key) < 0) 
					link=rld2(text_status,key,makePrm(prm,key,""),tree(code.substring(codePos+1),key),ClosedBookStatus);
				else 
					link=rld2(text_status,key,makePrm(prm,"",key),tree(code.substring(0,codePos),key),OpenBookStatus);
			}
		}
	};
	TVcount++;
	var retVal=wrtIdx_line(text_status,text,key,link,prefix,code,opts,TVcount);
	if(document.layers) 
		retVal="<LAYER ID=\"TV"+ TVcount+"\" TOP=\""+ currPosY +"\" LEFT=\"0\" VISIBILITY=\""+ (currIsVisible?"show":"hide") +"\">"+ retVal +"<\/LAYER>";
	if(document.all) 
		retVal="<DIV ID=\"TV"+ TVcount +"\""+" STYLE=\"position:absolute;top:"+ currPosY +"px;left:0px;display:"+ (currIsVisible?"block":"none") +";\">"+ retVal +"<\/DIV>";
	wrt(retVal);	
	if(currIsVisible) 
		currPosY += EntryHeight;
	if(isDHTML) 
	{
		TVkeys[key]=false;
		TVentries[TVcount]=new Array((viewKey != key),text,key,link,treePfx,prefix,code);
		TVkeys[key.substring(0,key.length-1)]=currIsVisible;
	}
}

function myIndexOf(text,srch,start)
{
	if(!start) 
		start=0;	
	var pos=(""+ text).indexOf(srch,start);	
	return (""+ pos != ""?pos:-1);
}

function is (opts,keyword)
{	
	return (myIndexOf(""+ opts,keyword) > -1);
}

function xTarget (opts)
{
	if(opts && is(opts,"target"))
	{
		opts += ",";
		startPos=myIndexOf(opts,"target=")+7;
		return opts.substring(startPos,myIndexOf(opts,",",startPos));
	} 
	else 	
		return "body";
}

function xImg (opts)
{
	return (opts?opts.substring(myIndexOf(opts,"img")+3,myIndexOf(opts,"img")+4):"");
}

function wrt (text)
{
	printBuffer += text +"";
}

function fl()
{
	document.write(printBuffer);
	printBuffer="";
}

function  dividiStringa (xText,codice)
{
	var str="";
	str=dividiStringa2(xText,codice,newLine);
	return (str.split(newLine));
}

function  dividiStringa2 (xText,codice,newS)
{
	var lngMax=lngStringaMassima - (codice.length - 1)*Skip;
	var strA=xText.split(" ");	
	var str="";	
	var lng=0;
	if(strA.length==1)
	{
		str=strA[0];
	}
	if(strA.length > 1)
	{
		str=strA[0];
		lng=strA[0].length;
		for (var i=1;i < strA.length;i++)
		{
			if((lng+1+strA[i].length) <= lngMax)
			{
				str=str+" "+strA[i];
				lng=lng+1+strA[i].length;
			} 
			else 
			{
				str=str+newS+strA[i];
				lng=strA[i].length;
			}
		}
	}
	return (str);
}

function sub_Book (text,key,link,opts)
{
	var textA=dividiStringa(text,key);
	if(textA.length >= 1)
	{
		if(is(opts,"cntd.")) 
			idx(text,key,link,"/|.","|",opts,text);	
		else 
			idx(textA[0],key,link,"",(is(opts,"img")?(is(opts,"last")? "_"+xImg(opts)+"|*"+xImg(opts):"-"+xImg(opts)+"|+"+xImg(opts)): (is(opts,"last")?"_o|*b":"-o|+b") ),opts , text);
		treePfx += (is(opts,"last")?".":"/");
	}
}

function lastBook (text,key,link,opts)
{
	sub_Book(text,key,link,"last,"+ opts);
}

function end_Book()
{
	treePfx=treePfx.substring(0,treePfx.length-1);
}

function sub_Page (text,key,link,opts)
{	
	var textA=dividiStringa(text,key);
	if(textA.length >= 1)	
	{
		idx(textA[0],key,link,"",(is(opts,"cntd.")?(is(opts,"last")? "..":"/."):(is(opts,"last")?"L":"l")+(is(opts,"img")? xImg(opts):(is(opts,"link")?"x":"#") ) ),opts, text);
		for (var i=1 ;i< textA.length;i++)
		{
			idx_line(textA[i],key,link,"",(is(opts,"cntd.")?(is(opts,"last")? "..":"/."):(is(opts,"last")?"L":"l")+(is(opts,"img")? xImg(opts):(is(opts,"link")?"x":"#") ) ),opts, text);
		}
	}
}

function lastPage (text,key,link,opts)
{	
	sub_Page(text,key,link,"last,"+ opts);
}

function end_Tree()
{
	idx();
	if(document.layers) 
		wrt("<DIV ID=\"bottom\"" +" STYLE=\"position:absolute;top:"+ (TVtop +EntryHeight * (TVcount-1)) +"px;\"><FONT SIZE=\"1\">&#160;<\/FONT><\/DIV>");	
	treePfx="";
}

function isMac () 
{
	if (myIndexOf(navigator.userAgent, "Mac") == -1) 
		return (myIndexOf(navigator.userAgent, "MAC") >= 0);
	else 
		return true; 
}