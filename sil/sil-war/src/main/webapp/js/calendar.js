//var weekend = [5,6];
var weekend = [6];
var weekendColor = "#e0e0e0";
var fontface = "Arial";
var fontsize = 2;

var gNow = new Date();
var vSelectedDay = gNow.getDate();
var vSelectedMonth = gNow.getMonth();
var vSelectedYear = gNow.getFullYear();

var ggWinCal;
isNav = (navigator.appName.indexOf("Netscape") != -1) ? true : false;
isIE = (navigator.appName.indexOf("Microsoft") != -1) ? true : false;

Calendar.Months = ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
"Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"];

// Non-Leap year Month days..
Calendar.DOMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
// Leap year Month days..
Calendar.lDOMonth = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

function Calendar(p_item, p_WinCal, p_month, p_year, p_callBackDateFunction) {
	if ((p_month == null) && (p_year == null))	return;

	if (p_WinCal == null)
		this.gWinCal = ggWinCal;
	else
		this.gWinCal = p_WinCal;
	
	if (p_month == null) {
		this.gMonthName = null;
		this.gMonth = null;
		this.gYearly = true;
	} else {
		this.gMonthName = Calendar.get_month(p_month);
		this.gMonth = new Number(p_month);
		this.gYearly = false;
	}

	this.gYear = p_year;
	this.gBGColor = "white";
	this.gFGColor = "black";
	this.gTextColor = "black";
	this.gHeaderColor = "black";
	this.gReturnItem = p_item;
	if (p_callBackDateFunction != "undefined")
		this.gCallBackDateFunction = p_callBackDateFunction;
}

Calendar.get_month = Calendar_get_month;
Calendar.get_daysofmonth = Calendar_get_daysofmonth;
Calendar.calc_month_year = Calendar_calc_month_year;

function Calendar_get_month(monthNo) {
	return Calendar.Months[monthNo];
}

function Calendar_get_daysofmonth(monthNo, p_year) {
	/* 
	Check for leap year ..
	1.Years evenly divisible by four are normally leap years, except for... 
	2.Years also evenly divisible by 100 are not leap years, except for... 
	3.Years also evenly divisible by 400 are leap years. 
	*/
	if ((p_year % 4) == 0) {
		if ((p_year % 100) == 0 && (p_year % 400) != 0)
			return Calendar.DOMonth[monthNo];
	
		return Calendar.lDOMonth[monthNo];
	} else
		return Calendar.DOMonth[monthNo];
}

function Calendar_calc_month_year(p_Month, p_Year, incr) {
	/* 
	Will return an 1-D array with 1st element being the calculated month 
	and second being the calculated year 
	after applying the month increment/decrement as specified by 'incr' parameter.
	'incr' will normally have 1/-1 to navigate thru the months.
	*/
	var ret_arr = new Array();
	
	if (incr == -1) {
		// B A C K W A R D
		if (p_Month == 0) {
			ret_arr[0] = 11;
			ret_arr[1] = parseInt(p_Year,10) - 1;
		}
		else {
			ret_arr[0] = parseInt(p_Month,10) - 1;
			ret_arr[1] = parseInt(p_Year,10);
		}
	} else if (incr == 1) {
		// F O R W A R D
		if (p_Month == 11) {
			ret_arr[0] = 0;
			ret_arr[1] = parseInt(p_Year,10) + 1;
		}
		else {
			ret_arr[0] = parseInt(p_Month,10) + 1;
			ret_arr[1] = parseInt(p_Year,10);
		}
	}
	
	return ret_arr;
}

function Calendar_calc_month_year(p_Month, p_Year, incr) {
	/* 
	Will return an 1-D array with 1st element being the calculated month 
	and second being the calculated year 
	after applying the month increment/decrement as specified by 'incr' parameter.
	'incr' will normally have 1/-1 to navigate thru the months.
	*/
	var ret_arr = new Array();
	
	if (incr == -1) {
		// B A C K W A R D
		if (p_Month == 0) {
			ret_arr[0] = 11;
			ret_arr[1] = parseInt(p_Year,10) - 1;
		}
		else {
			ret_arr[0] = parseInt(p_Month,10) - 1;
			ret_arr[1] = parseInt(p_Year,10);
		}
	} else if (incr == 1) {
		// F O R W A R D
		if (p_Month == 11) {
			ret_arr[0] = 0;
			ret_arr[1] = parseInt(p_Year,10) + 1;
		}
		else {
			ret_arr[0] = parseInt(p_Month,10) + 1;
			ret_arr[1] = parseInt(p_Year,10);
		}
	
	} else if (incr == 12) {
		// Y E A R   F O R W A R D
			ret_arr[0] = parseInt(p_Month,10);
			ret_arr[1] = parseInt(p_Year,10) + 1;
	} else if (incr == -12) {
		// Y E A R   B A C K W A R D
			ret_arr[0] = parseInt(p_Month,10);
			ret_arr[1] = parseInt(p_Year,10) - 1;
	}
	
	
	return ret_arr;
}

// This is for compatibility with Navigator 3, we have to create and discard one object before the prototype object exists.
new Calendar();

Calendar.prototype.getMonthlyCalendarCode = function() {
	var vCode = "";
	var vHeader_Code = "";
	var vData_Code = "";
	
	// Begin Table Drawing code here..
	//vCode = vCode + "<TABLE BORDER=2 BGCOLOR=\"" + this.gBGColor + "\">";
	vCode = vCode + "<table width='223px' class='cal' cellspacing='0' align='center'>";
	
	vHeader_Code = this.cal_header();
	vData_Code = this.cal_data();
	vCode = vCode + vHeader_Code + vData_Code;
	
	vCode = vCode + "</table>";
	
	return vCode;
}

Calendar.prototype.show = function() {
	var vCode = "";
	
	this.gWinCal.document.open();

	// Setup the page...
	this.wwrite("<html>");
	this.wwrite("<head><title>Calendario</title>");	
 	/*this.wwrite("<style type='text/css'>");
    this.wwrite("a:link { text-decoration : none; }");
    this.wwrite("a:visited { text-decoration : none; }");
    this.wwrite("a:active  { text-decoration : none; }");
    this.wwrite("select { font-size : 10px; font-family : arial, helvetica, sans-serif; }");
    this.wwrite("</style>");
    */
    this.wwrite("<link rel='stylesheet' href='../../css/stili.css' type='text/css'>");
	this.wwrite("</head>");

	/*this.wwrite("<body " + 
		"link=\"" + this.gLinkColor + "\" " + 
		"vlink=\"" + this.gLinkColor + "\" " +
		"alink=\"" + this.gLinkColor + "\" " +
		"text=\"" + this.gTextColor + "\">");*/
	this.wwrite("<body>");
	this.wwriteA("<br>");
	this.wwriteA("<table cellspacing='0' margin='0' cellpadding='0' border='0px' noshade align='center'>");
	this.wwriteA("<tr>");
	this.wwriteA("<td align='left' valign='top' width='13' height='19' class='cal_header'>" +
				"<img src='../../img/angoli/bia1.gif' width='6' height='6'>" +
				"</td>");
	
	// Show navigation buttons
	var prevMMYYYY = Calendar.calc_month_year(this.gMonth, this.gYear, -1);
	var prevMM = prevMMYYYY[0];
	var prevYYYY = prevMMYYYY[1];

	var nextMMYYYY = Calendar.calc_month_year(this.gMonth, this.gYear, 1);
	var nextMM = nextMMYYYY[0];
	var nextYYYY = nextMMYYYY[1];
	
	var y_prevMMYYYY = Calendar.calc_month_year(this.gMonth, this.gYear, -12);
	var y_prevMM = y_prevMMYYYY[0];
	var y_prevYYYY = y_prevMMYYYY[1];

	var y_nextMMYYYY = Calendar.calc_month_year(this.gMonth, this.gYear, 12);
	var y_nextMM = y_nextMMYYYY[0];
	var y_nextYYYY = y_nextMMYYYY[1];
	
	this.wwriteA("<td class='cal_header' align='center' valign='middle' cellpadding='2px'>");
	// Precedente
	this.wwriteA("<A HREF=\"" +
		"javascript:window.opener.Build(" + 
		"'" + this.gReturnItem + "', '" + prevMM + "', '" + prevYYYY + "', '" + this.gCallBackDateFunction + "'" +
		");" +
		"\">");
	this.wwriteA("<img src='../../img/previous.gif' border='0' alt='&lt;&lt;'/></a>&nbsp;&nbsp;");
	// MESE 
	//this.wwriteA(this.gMonthName + " " + this.gYear);
	this.wwriteA(this.gMonthName);
	this.wwriteA("&nbsp;&nbsp;");
	// SUCCESSIVO
	this.wwriteA("<A HREF=\"" +
		"javascript:window.opener.Build(" + 
		"'" + this.gReturnItem + "', '" + nextMM + "', '" + nextYYYY + "', '" + this.gCallBackDateFunction + "'" +
		");" +
		"\">");
	this.wwriteA("<img src='../../img/next.gif' border='0' alt='&gt;&gt;' /></a>");
	
	// Anno Precedente
	this.wwriteA("&nbsp;&nbsp;<A HREF=\"" +
		"javascript:window.opener.Build(" + 
		"'" + this.gReturnItem + "', '" + y_prevMM + "', '" + y_prevYYYY + "', '" + this.gCallBackDateFunction + "'" +
		");" +
		"\">");
	this.wwriteA("<img src='../../img/previous.gif' border='0' alt='&lt;&lt;'/></a>&nbsp;&nbsp;");
	// ANNO
	this.wwriteA(this.gYear);
	this.wwriteA("&nbsp;&nbsp;");
	// Anno Successivo
	this.wwriteA("<A HREF=\"" +
		"javascript:window.opener.Build(" + 
		"'" + this.gReturnItem + "', '" + y_nextMM + "', '" + y_nextYYYY + "', '" + this.gCallBackDateFunction + "'" +
		");" +
		"\">");
	this.wwriteA("<img src='../../img/next.gif' border='0' alt='&gt;&gt;' /></a></td>");
	
	
	this.wwriteA("<td class='cal_header' align='right' valign='top' width='13' height='19'>" +
				"<img src='../../img/angoli/bia2.gif' width='6' height='6'>" +
				"</td></tr>");
	this.wwriteA("<tr class='cal'>");
	this.wwriteA("<td class='cal' width='13'>&nbsp;</td>");
	this.wwriteA("<td class='cal' align='center'>");

	// Get the complete calendar code for the month..
	vCode = this.getMonthlyCalendarCode();
	this.wwriteA(vCode);
	
	this.wwriteA("</td><td class='cal' width='13'>&nbsp;</td>");
	this.wwriteA("</tr>");
	this.wwriteA("<tr class='cal'>");
	this.wwriteA("<td class='cal' align='left' valign='bottom' width='13' height='19'>" +
				"<img src='../../img/angoli/bia4.gif' width='6' height='6'></td>");
	this.wwriteA("<td class='cal' height='19' align='center' valign='middle'>");
	this.wwriteA("<b>Clicca sul giorno desiderato</b>");
	this.wwriteA("</td>");
	this.wwriteA("<td class='cal' align='right' valign='bottom' width='13' height='19'>" +
				"<img src='../../img/angoli/bia3.gif' width='6' height='6'>" +
				"</td>")
	this.wwriteA("</tr>");
	this.wwriteA("</table>");
	this.wwriteA("</body></html>");

	//this.wwrite("<br><font color='#000000'><b>Clicca sul giorno desiderato</b></font>");
	//this.wwrite("</font></center></body></html>");
	this.gWinCal.document.close();
}

Calendar.prototype.wwrite = function(wtext) {
	this.gWinCal.document.writeln(wtext);
}

Calendar.prototype.wwriteA = function(wtext) {
	this.gWinCal.document.write(wtext);
}

Calendar.prototype.cal_header = function() {
	var vCode = "";
	
	/*
	vCode = vCode + "<TR>";
	vCode = vCode + "<TD WIDTH='14%'><FONT SIZE='2' FACE='" + fontface + "' COLOR='" + this.gHeaderColor + "'><B>Lun</B></FONT></TD>";
	vCode = vCode + "<TD WIDTH='14%'><FONT SIZE='2' FACE='" + fontface + "' COLOR='" + this.gHeaderColor + "'><B>Mar</B></FONT></TD>";
	vCode = vCode + "<TD WIDTH='14%'><FONT SIZE='2' FACE='" + fontface + "' COLOR='" + this.gHeaderColor + "'><B>Mer</B></FONT></TD>";
	vCode = vCode + "<TD WIDTH='14%'><FONT SIZE='2' FACE='" + fontface + "' COLOR='" + this.gHeaderColor + "'><B>Gio</B></FONT></TD>";
	vCode = vCode + "<TD WIDTH='14%'><FONT SIZE='2' FACE='" + fontface + "' COLOR='" + this.gHeaderColor + "'><B>Ven</B></FONT></TD>";
	vCode = vCode + "<TD WIDTH='14%'><FONT SIZE='2' FACE='" + fontface + "' COLOR='" + this.gHeaderColor + "'><B>Sab</B></FONT></TD>";
	vCode = vCode + "<TD WIDTH='16%'><FONT SIZE='2' FACE='" + fontface + "' COLOR='" + this.gHeaderColor + "'><B>Dom</B></FONT></TD>";
	vCode = vCode + "</TR>";
	*/
	vCode = vCode + "<tr>";
	vCode = vCode + "<td class='cal_settimana' align='center' width='25' height='25'>LUN</td>";
	vCode = vCode + "<td class='cal_settimana' align='center' width='25' height='25'>MAR</td>";
	vCode = vCode + "<td class='cal_settimana' align='center' width='25' height='25'>MER</td>";
	vCode = vCode + "<td class='cal_settimana' align='center' width='25' height='25'>GIO</td>";
	vCode = vCode + "<td class='cal_settimana' align='center' width='25' height='25'>VEN</td>";
	vCode = vCode + "<td class='cal_settimana' align='center' width='25' height='25'>SAB</td>";
	vCode = vCode + "<td class='cal_settimana' align='center' width='25' height='25'>DOM</td>";
	vCode = vCode + "</tr>";
	
	return vCode;
}

Calendar.prototype.cal_data = function() {
	var vDate = new Date();
	vDate.setDate(1);
	vDate.setMonth(this.gMonth);
	vDate.setFullYear(this.gYear);

	var vFirstDay=vDate.getDay() - 1;
	if (vFirstDay < 0) 
		vFirstDay = 6;
	var vDay=1;
	var vLastDay=Calendar.get_daysofmonth(this.gMonth, this.gYear);
	var vOnLastDay=0;
	var vCode = "";

	/*
	Get day for the 1st of the requested month/year..
	Place as many blank cells before the 1st day of the month as necessary. 
	*/

	vCode = vCode + "<TR>";
	for (i=0; i<vFirstDay; i++) {
		//vCode = vCode + "<TD WIDTH='14%'" + this.write_weekend_string(i) + "><FONT SIZE='2' FACE='" + fontface + "'> </FONT></TD>";
		vCode = vCode + "<td class='cal_vuoto' width='25' height='25'>&nbsp;</td>";
	}

	// Write rest of the 1st week
	for (j=vFirstDay; j<7; j++) {
		/*vCode = vCode + "<td width='25' height='25' class='" + this.write_weekend_string(j, vDay) + "'>" +
			    "<A HREF='#' class='cal' " + 
				"onClick=\"opener.document.forms[0]." + this.gReturnItem + ".value='" + 
				this.format_data(vDay) + 
				"'; try {opener.fieldChanged();} catch(e){};"; */
		vCode = vCode + "<td width='25' height='25' class='" + this.write_weekend_string(j, vDay) + "' " +
				"onClick=\"opener.document.forms[0]." + this.gReturnItem + ".value='" + 
				this.format_data(vDay) + 
				"'; try {opener.fieldChanged();} catch(e){};"; 

		if (this.gCallBackDateFunction != "")
			vCode = vCode + "opener." + this.gCallBackDateFunction + ";";				
			
		vCode = vCode + "window.close();\">" + 
				this.format_day(vDay) + 
				"</td>";
		vDay=vDay + 1;
	}
	vCode = vCode + "</TR>";

	// Write the rest of the weeks
	for (k=2; k<7; k++) {
		vCode = vCode + "<TR>";

		for (j=0; j<7; j++) {
			/*vCode = vCode + "<td width='25' height='25' class='" + this.write_weekend_string(j, vDay) + "'>" +
			    "<A HREF='#' class='cal' " + 
				"onClick=\"opener.document.forms[0]." + this.gReturnItem + ".value='" + 
				this.format_data(vDay) + 
				"'; try {opener.fieldChanged();} catch(e){};";*/
			vCode = vCode + "<td width='25' height='25' class='" + this.write_weekend_string(j, vDay) + "' " +
				"onClick=\"opener.document.forms[0]." + this.gReturnItem + ".value='" + 
				this.format_data(vDay) + 
				"'; try {opener.fieldChanged();} catch(e){};";
				
			if (this.gCallBackDateFunction != "")
				vCode = vCode + "opener." + this.gCallBackDateFunction + ";";				
				
			vCode = vCode + "window.close();\">" + 
					this.format_day(vDay) + 
					"</td>";
			vDay=vDay + 1;

			if (vDay > vLastDay) {
				vOnLastDay = 1;
				break;
			}
		}

		if (j == 6)
			vCode = vCode + "</TR>";
		if (vOnLastDay == 1)
			break;
	}
	
	// Fill up the rest of last week with proper blanks, so that we get proper square blocks
	for (m=1; m<(7-j); m++) {
		if (this.gYearly)
			vCode = vCode + "<td width='25' height='25' class='cal_vuoto'>&nbsp;</td>";
		else
			vCode = vCode + "<td width='25' height='25' class='cal_vuoto'>" +m + "</td>";
	}
	if(j<6) { vCode = vCode + "</tr>"; } // Se la riga viene completata dal ciclo sopra manca il </tr>
	return vCode;
}

Calendar.prototype.format_day = function(vday) {
	if (vday == vSelectedDay && this.gMonth == vSelectedMonth && this.gYear == vSelectedYear)
		//return ("<FONT COLOR=\"RED\"><B>" + vday + "</B></FONT>");
		return("<b>" + vday + "</b>");
	else
		return (vday);
}

Calendar.prototype.write_weekend_string = function(j, vday) {
	var i;
	if (vday == vSelectedDay && this.gMonth == vSelectedMonth && this.gYear == vSelectedYear)
	{
		return("cal_sel");
	} else {
		// Return special formatting for the weekend day.
		for (i=0; i<weekend.length; i++) {
			if (j == weekend[i])
			//return (" BGCOLOR=\"" + weekendColor + "\"");
			return("cal_vuoto_sel");
		}
		return("cal_giorno");
	}
	return "";
}

Calendar.prototype.format_data = function(p_day) {
	var vData;
	var vMonth = 1 + this.gMonth;
	vMonth = (vMonth.toString().length < 2) ? "0" + vMonth : vMonth;
	var vMon = Calendar.get_month(this.gMonth).substr(0,3).toUpperCase();
	var vFMon = Calendar.get_month(this.gMonth).toUpperCase();
	var vY4 = new String(this.gYear);
	var vY2 = new String(this.gYear.substr(2,2));
	var vDD = (p_day.toString().length < 2) ? "0" + p_day : p_day;

	vData = vDD + "\/" + vMonth + "\/" + vY4;

	return vData;
}

function Build(p_item, p_month, p_year, p_callBackDateFunction) {
	var p_WinCal = ggWinCal;

	gCal = new Calendar(p_item, p_WinCal, p_month, p_year, p_callBackDateFunction);

	// Customize your Calendar here..
	gCal.gBGColor="white";
	gCal.gLinkColor="black";
	gCal.gTextColor="#e10000";
	gCal.gHeaderColor="black";
	gCal.gCallBackDateFunction=p_callBackDateFunction;

	gCal.show();
}

function show_calendar(name_field, callBackDateFunction, scrX, scrY) {
	/* 
		p_item	: Return Item.
		p_month : 0-11 for Jan-Dec; 12 for All Months.
		p_year	: 4-digit year
	*/

	p_item = name_field;	
	
	var initial_field=null;
	
	if (!validateDate(name_field))
		return;

	// Controllo se il campo e' disabilitato o in sola lettura
	var campo = eval("document.forms[0]." + name_field)
	if (campo.readonly==true || campo.disabled==true){
		//	alert ("il campo e' disabilitato o in sola lettura!!")
		return;
	}
	 
	initial_field = campo.value;
		
	var select_day = initial_field != null ? initial_field.substring(0,2) : -1;
	var select_month = initial_field != null ? (initial_field.substring(3,5)) - 1 : -1;
	var select_year = initial_field != null ? initial_field.substring(6) : -1;
	
	if (select_month != null && select_month >= 0 && select_month < 12) {
		p_month = select_month;
		vSelectedMonth = select_month;
	} else {
		p_month = new String(gNow.getMonth());
		vSelectedMonth = gNow.getMonth();
	}
	if (select_year != null && select_year >= 1900 && select_year < 2100) {
		p_year = select_year;
		vSelectedYear = select_year;
	} else {
		p_year = new String(gNow.getFullYear().toString());
		vSelectedYear = gNow.getFullYear();
	}
	if (select_day != null && select_day > 0 && select_day <= 31) {
		vSelectedDay = select_day;
	} else {
		vSelectedDay = gNow.getDate();			
	}
	
	var larghezza = 260;
    scrX = scrX - larghezza + 10;
    scrY = scrY + 17;
    vWinCal = window.open("", "Calendario", 
		"width="+larghezza+",height=250,status=no,resizable=no,top="+scrY+",left="+scrX);
	vWinCal.opener = self;
	ggWinCal = vWinCal;
	vWinCal.focus();
 
	Build(p_item, p_month, p_year, callBackDateFunction);
}


function textCounter(field, maxlimit) {
	if (field.value.length > maxlimit) // if too long...trim it!
		field.value = field.value.substring(0, maxlimit);		
}

function initDates() {
	var now = new Date();
	var vDay = gNow.getDate();
	var vMonth = gNow.getMonth() + 1;
	var vYear = gNow.getFullYear();
	var vDD = (vDay.toString().length < 2) ? "0" + vDay : vDay;	
	var vMM = (vMonth.toString().length < 2) ? "0" + vMonth : vMonth;	
	var vY4 = new String(vYear);
	var vData = vDD + "\/" + vMM + "\/" + vY4;
	window.document.insert.to_date.value = vData;
	window.document.insert.from_date.value = vData;
}
