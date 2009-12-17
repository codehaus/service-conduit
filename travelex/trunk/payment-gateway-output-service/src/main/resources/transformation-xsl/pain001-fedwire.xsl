<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				version="2.0"
				xmlns:xs="http://www.w3.org/2001/XMLSchema" 
				xpath-default-namespace="urn:iso:std:iso:20022:tech:xsd:pain.001.001.03">

    <xsl:output method="xml" omit-xml-declaration="yes" indent="no" />

    <xsl:param name="root.element.name" />
    <xsl:param name="remitting.bank.id" />
    <xsl:param name="remitting.bank.name" />
    <xsl:param name="remitting.bank.account.number" />
    <xsl:param name="remitting.bank.accountHolder.name" />
    <xsl:param name="remitting.bank.accountHolder.line1" />
    <xsl:param name="remitting.bank.accountHolder.city" />
    <xsl:param name="remitting.bank.accountHolder.state" />
    <xsl:param name="remitting.bank.accountHolder.zip" />
   
    <xsl:template match="*">
{2000} <xsl:value-of select="Amt/InstdAmt"/>
{3100} <xsl:value-of select="$remitting.bank.id"/> <xsl:value-of select="$remitting.bank.name"/>
{3400} <xsl:value-of select="CdtrAgt/FinInstnId/BIC"/> <xsl:value-of select="CdtrAgt/BrnchId/Nm"/>
{4200} 9108739
       SECURITIES AND EXCHANGE COMMISSION
{4320} CIK350001
{5000} <xsl:value-of select="$remitting.bank.account.number"/> <xsl:value-of select="$remitting.bank.accountHolder.name"/>
       <xsl:value-of select="$remitting.bank.accountHolder.line1"/> <xsl:value-of select="$remitting.bank.accountHolder.city"/> <xsl:value-of select="$remitting.bank.accountHolder.state"/> <xsl:value-of select="$remitting.bank.accountHolder.zip"/>
{6000} CIK1057656
    </xsl:template>

</xsl:stylesheet>