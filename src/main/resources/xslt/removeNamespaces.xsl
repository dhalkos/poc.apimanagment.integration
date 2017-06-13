<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output omit-xml-declaration="yes" indent="yes" encoding="UTF-16"/>
	
   <xsl:template match="*">
     <xsl:element name="{local-name(.)}">
       <xsl:apply-templates select="@* | node()"/>
     </xsl:element>
   </xsl:template>
   <xsl:template match="@*">
  </xsl:template>
</xsl:stylesheet>