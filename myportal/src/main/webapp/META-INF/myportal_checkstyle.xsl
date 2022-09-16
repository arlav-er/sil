<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <html>
<style type="text/css">
tr.d0 td {
	background-color: #C0C0C0; color: black;
}
tr.d1 td {
	background-color: #E0E0E0; color: black;
}
tr.tr_info td {
	background-color: limegreen; color: black;
}
tr.tr_warning td {
	background-color: khaki; color: black;	
}
tr.tr_error td {
	background-color: orange; color: black;	
}
  </style>  
  <body bgcolor='#f9f9f9'>
  <h2>Checkstyle Report ver. <xsl:value-of select="checkstyle/@version"/></h2>
  <table border="1">
    <tr bgcolor='#909090'>
      <th>Item</th>
      <th>Class</th>
      <th>Line</th>
      <th>Column</th>
      <th>Severity</th>
      <th>Message</th>
      <!--<th>Source</th>-->
    </tr>
    <xsl:for-each select="checkstyle/file/error">
      <xsl:param name="dir" />
      <xsl:variable name="class" select="substring(../@name,2+string-length($dir))"/>
      <xsl:sort data-type="number" order="ascending"
            select="(number(@severity='error') * 1)
                  + (number(@severity='warning') * 2)
                  + (number(@severity='info') * 3)" />
      <xsl:sort order="ascending" select="@severity"/>
      <!--<tr class="{concat('d',position() mod 2)}">-->
      <tr class="{concat('tr_',@severity)}">
        <td><xsl:value-of select="position()"/></td>
        <!--<td><xsl:value-of select="substring(../@name,2+string-length($dir))"/></td>-->
        <td><xsl:value-of select="translate($class, '\/', '.')"/></td>
        <td><xsl:value-of select="@line"/></td>
        <td><xsl:value-of select="@column"/></td>
        <td><xsl:value-of select="@severity"/></td>
        <td><xsl:value-of select="@message"/></td>
        <!--<td><xsl:value-of select="@source"/></td>-->
      </tr>
    </xsl:for-each>
  </table>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>