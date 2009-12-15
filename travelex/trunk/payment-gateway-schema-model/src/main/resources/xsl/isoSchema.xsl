<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				version="2.0"
				xmlns:xs="http://www.w3.org/2001/XMLSchema">
				
	<!-- Works on ISO20022 defined schemas. May work on others - to be tested -->

    <xsl:output method="xml" omit-xml-declaration="yes" indent="no" />

    <xsl:param name="root.element.name" />    

    <xsl:template match="//xs:schema/xs:element[@name=$root.element.name]">
    	<node xsl:exclude-result-prefixes="xs" label="{./@name}">    
			<xsl:variable name="type" select="./@type"/>
			<xsl:apply-templates mode="copy-no-ns" select="//xs:complexType[@name=$type]" />
		</node>			    		
    </xsl:template>

    <xsl:template mode="copy-no-ns" match="xs:*">    		  
    		<xsl:for-each select=".//xs:element">
    			<xsl:variable name="type" select="./@type"/>   
    			
    			<node xsl:exclude-result-prefixes="xs" label="{./@name}">    			     			     		
	    			<xsl:variable name="schema.base.type" select="document('document1')/mappings/mapping[./schemaType=$type]/baseType"/>
	    			<xsl:if test="$schema.base.type">
		    			<xsl:variable name="java.type" select="document('document2')/mappings/mapping[./xmlType=$schema.base.type]/javaType"/>
		    			<xsl:if test="$java.type">
		   					<xsl:attribute name="isLeaf">true</xsl:attribute>		    			
		   					<xsl:attribute name="javaType">
		   						<xsl:value-of select="$java.type"/>
		   					</xsl:attribute>
		    			</xsl:if>		    			
	    			</xsl:if>	    			   
    				<xsl:apply-templates mode="copy-no-ns" select="//xs:complexType[@name=$type]" />
    			</node>
    			 
    		</xsl:for-each> 	 		
    </xsl:template>    

</xsl:stylesheet>