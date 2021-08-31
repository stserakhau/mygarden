<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xslt/java"
                xmlns:uw="com.i4biz.mygarden.domain.report.UserWeatherNotificationReport"
                exclude-result-prefixes="java">

    <xsl:output version="1.0" method="text" encoding="UTF-8" indent="no"/>

    <xsl:decimal-format name="form" decimal-separator="." grouping-separator="&#160;"/>

    <xsl:param name="today"/>
    <xsl:param name="weatherFailDay"/>
    <xsl:param name="reportItem"/>
    <xsl:param name="siteUrl"/>


    <xsl:template match="/"><![CDATA[<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">

  <title></title>

  <style type="text/css">
  </style>
</head>
<body style="margin:0; padding:0; background-color:#F2F2F2;"><b>Здравствуйте ]]><xsl:value-of select="uw:getFirstName($reportItem)"/><![CDATA[</b>,<br/>
    Вы просили сообщить, если о прогнозу температура опустится ниже ]]><xsl:value-of select="uw:getTemperatureLowLevelNotification($reportItem)"/><![CDATA[ ℃ за ]]><xsl:value-of select="uw:getTemperatureLowLevelNotificationDaysBefore($reportItem)"/><![CDATA[ дней. Критерий выполнен по прогнозу на  ]]><xsl:value-of select="$weatherFailDay"/><![CDATA[.
<br/><br/>
Перейдите по ссылке <a href="http://egardening.ru/my_work_plan.tiles">http://egardening.ru/my_work_plan.tiles</a> для более полной информации.<br/>
<br/>
С уважением,<br/>
команда eGardening.ru
<![CDATA[</body></html>]]></xsl:template>
</xsl:stylesheet>