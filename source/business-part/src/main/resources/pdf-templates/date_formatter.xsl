<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xslt/java"
                xmlns:dt="java.util.Date"
                xmlns:sf="java.text.SimpleDateFormat"
                exclude-result-prefixes="java"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:str="xalan://org.apache.commons.lang3.StringUtils">

    <xsl:param name="dateFormat"/>
    <xsl:param name="timeFormat"/>
    <xsl:param name="dateTimeDelimiter" select="' '"/>

    <xsl:variable name="dateTimeFormat">
        <xsl:value-of select="$dateFormat"/><xsl:value-of select="$dateTimeDelimiter"/><xsl:value-of
            select="$timeFormat"/>
    </xsl:variable>

    <xsl:variable name="sourceFormat">yyyy-MM-dd HH:mm:ss.S z</xsl:variable>
    <xsl:variable name="sourceFormatter" select="sf:new($sourceFormat)"/>

    <xsl:variable name="currentDate" select="java:format(sf:new($dateTimeFormat), dt:new())"/>

    <xsl:variable name="dateTimeFormatter" select="sf:new($dateTimeFormat)"/>
    <xsl:variable name="dateFormatter" select="sf:new($dateFormat)"/>

    <xsl:template name="formatDateTimeWithLineSeparation">
        <xsl:param name="date"/>
        <xsl:if test="$date">
            <xsl:variable name="parsedDate" select="java:parse($sourceFormatter, $date)"/>
            <xsl:variable name="result" select="java:format($dateTimeFormatter, $parsedDate)"/>
            <xsl:call-template name="dateValueReplacer">
                <xsl:with-param name="value" select="$result"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template name="formatDateTime">
        <xsl:param name="date"/>
        <xsl:if test="$date">
            <xsl:variable name="parsedDate" select="java:parse($sourceFormatter, $date)"/>
            <xsl:value-of select="java:format($dateTimeFormatter, $parsedDate)"/>
        </xsl:if>
    </xsl:template>

    <xsl:template name="formatDate">
        <xsl:param name="date"/>
        <xsl:if test="$date">
            <xsl:variable name="parsedDate" select="java:parse($sourceFormatter, $date)"/>
            <xsl:variable name="result" select="java:format($dateFormatter, $parsedDate)"/>
            <xsl:call-template name="dateValueReplacer">
                <xsl:with-param name="value" select="$result"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template name="dateValueReplacer">
        <xsl:param name="value"/>
        <xsl:value-of select="str:replacePattern($value, '( )', '$1&#x200B;')"/>
    </xsl:template>
</xsl:stylesheet>