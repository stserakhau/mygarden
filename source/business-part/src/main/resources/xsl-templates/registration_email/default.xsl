<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:u="com.i4biz.mygarden.domain.user.User"
                xmlns:up="com.i4biz.mygarden.domain.user.UserProfile">

    <xsl:output version="1.0" method="text" encoding="UTF-8" indent="no"/>

    <xsl:param name="user"/>
    <xsl:param name="userProfile"/>
    <xsl:param name="siteEmail"/>
    <xsl:param name="siteUrl"/>
    <xsl:variable name="registrationLink" select="concat($siteUrl,'/api/users/confirmRegistration?registrationCode=',u:getRegistrationCode($user))"/>

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
<body style="margin:0; padding:0; background-color:#F2F2F2;">]]><xsl:value-of select="up:getFirstName($userProfile)"/><![CDATA[,</b><br/>
<br/>
Благодарим Вас за регистрацию на сайте eGardening.ru. Пожалуйста, <a href="]]><xsl:value-of select="$registrationLink"/><![CDATA[">]]>нажмите ссылку<![CDATA[</a> для проверки Вашего email адреса.<br/>
<br/>
Мы понимаем, что наш проект непривычен для завсегдатаев садоводческих форумов и сайтов, поэтому подготовили для Вас
<a href="https://egardening.ru/index.jsp">видео презентацию<a> и <a href="https://egardening.ru/help_species_inventory.tiles">текстовый помощник</a> с возможностями и
ограничениями, реализованными на данный момент.<br/>
<br/>
eGardening.ru – молодой проект, все еще находящийся в разработке. Вы можете узнать об анонсе новых возможностей
приложения <a href="https://egardening.ru/index.jsp#news">здесь</a>, либо принять участие в обсуждении в наших
группах в социальных сетях.<br/>
<br/>
Если у Вас возникли вопросы или претензии касательно Вашей регистрации, пожалуйста, напишите нам на
<a href="mailto:info@egardening.ru">info@egardening.ru</a>.<br/>
<br/>
С уважением,<br/>
команда eGardening.ru
</body>
</html>]]>
    </xsl:template>
</xsl:stylesheet>