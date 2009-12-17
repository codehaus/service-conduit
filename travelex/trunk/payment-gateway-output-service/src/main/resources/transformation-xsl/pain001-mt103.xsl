<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				version="2.0"
				xmlns:xs="http://www.w3.org/2001/XMLSchema" 
				xpath-default-namespace="urn:iso:std:iso:20022:tech:xsd:pain.001.001.03">

    <xsl:output method="xml" omit-xml-declaration="yes" indent="no" />

    <xsl:param name="root.element.name" />
    <xsl:param name="payment.value.date" />
    <xsl:param name="remitting.bank.account.number" />
    <xsl:param name="remitting.bank.name" />
    
   
    <xsl:template match="*">
:20:<xsl:value-of select="PmtId/InstrId"/>
:32A:<xsl:value-of select="$payment.value.date"/>GBP<xsl:value-of select="Amt/InstdAmt"/>
:50K://<xsl:value-of select="$remitting.bank.account.number"/>
/<xsl:value-of select="$remitting.bank.name"/>
:57D://<xsl:value-of select="CdtrAgt/FinInstnId/BIC"/>
<xsl:value-of select="CdtrAgt/BrnchId/Nm"/>
:59:/<xsl:value-of select="CdtrAcct/Id/IBAN"/>
<xsl:value-of select="Cdtr/Nm"/>
-
    </xsl:template>

</xsl:stylesheet>