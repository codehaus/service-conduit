<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				version="2.0"
				xmlns:xs="http://www.w3.org/2001/XMLSchema" 
				xpath-default-namespace="urn:iso:std:iso:20022:tech:xsd:pain.001.001.03">

    <xsl:output method="xml" omit-xml-declaration="yes" indent="no" />

    <xsl:param name="root.element.name" />    
   
    <xsl:template match="*">
    	<xsl:value-of select="CstmrCdtTrfInitn/GrpHdr/MsgId"/>
    </xsl:template>

</xsl:stylesheet>