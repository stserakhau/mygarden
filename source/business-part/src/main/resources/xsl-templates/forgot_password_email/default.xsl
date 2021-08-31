<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:u="com.i4biz.mygarden.domain.user.User"
                xmlns:up="com.i4biz.mygarden.domain.user.UserProfile">

    <xsl:output version="1.0" method="text" encoding="UTF-8" indent="no"/>

    <xsl:decimal-format name="form" decimal-separator="." grouping-separator="&#160;"/>

    <xsl:param name="user"/>
    <xsl:param name="userProfile"/>
    <xsl:param name="siteUrl"/>
    <xsl:variable name="registrationLink" select="concat($siteUrl,'/api/users/confirmRegistration?registrationCode=',u:getRegistrationCode($user))"/>

    <xsl:template match="/"><![CDATA[<b>Здравствуйте ]]><xsl:value-of select="up:getFirstName($userProfile)"/><![CDATA[</b><br/>
<br/>
Ваш пароль деактивирован. Пожалуйста, перейдите по ссылке для установки нового пароля.<br/>
<br/>
<a href="]]><xsl:value-of select="$registrationLink"/><![CDATA[">]]><xsl:value-of select="$registrationLink"/><![CDATA[</a><br/>
<br/>
С уважением,<br/>
команда eGardening.ru]]></xsl:template>
</xsl:stylesheet>