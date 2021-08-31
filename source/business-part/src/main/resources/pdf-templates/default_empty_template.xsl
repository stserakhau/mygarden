<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
        >

    <xsl:output version="1.0" method="xml" encoding="UTF-8" indent="no"/>

    <xsl:decimal-format name="form" decimal-separator="." grouping-separator="&#160;"/>

    <xsl:param name="DocType"/>
    <xsl:param name="DocSubType"/>

    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="first-page"
                                       page-height="297mm" page-width="210mm">
                    <fo:region-body margin-left="0.5cm" margin-right="0.5cm" margin-top="0.5cm" margin-bottom="0.5cm"/>
                    <fo:region-before extent="0.2cm" region-name="first-page-header"/>
                    <fo:region-after extent="0.2cm"/>
                </fo:simple-page-master>

                <fo:simple-page-master master-name="other-page"
                                       page-height="210mm" page-width="297mm">
                    <fo:region-body margin-left="0.5cm" margin-right="0.5cm" margin-top="0.5cm" margin-bottom="0.5cm"/>
                    <fo:region-before extent="0.2cm" region-name="other-page-header"/>
                    <fo:region-after extent="0.2cm"/>
                </fo:simple-page-master>

                <fo:page-sequence-master master-name="pages">
                    <fo:repeatable-page-master-alternatives>
                        <fo:conditional-page-master-reference page-position="first" master-reference="first-page"/>
                        <fo:conditional-page-master-reference master-reference="other-page"/>
                    </fo:repeatable-page-master-alternatives>
                </fo:page-sequence-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="pages" format="1">

                <fo:static-content flow-name="other-page-header">
                    <fo:block-container height="0.2cm" background-color="#d1113b">
                        <fo:block/>
                    </fo:block-container>
                </fo:static-content>

                <fo:static-content flow-name="xsl-region-after">
                    <fo:block-container height="0.2cm" background-color="#d1113b">
                        <fo:block/>
                    </fo:block-container>
                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <fo:block-container font-size="8px">
                        <fo:table margin-top="1cm" table-layout="fixed">
                            <fo:table-column column-width="50%"/>
                            <fo:table-column column-width="50%"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block font-weight="bold">Your Company Name</fo:block>
                                        <fo:block>{Your Company Slogan}</fo:block>

                                        <fo:block margin-top="0.5cm">{Street Address}</fo:block>
                                        <fo:block>{City, ST ZIP Code}</fo:block>
                                        <fo:block>Phone {509.555.0190} Fax {509.555.0191}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block font-weight="bold" color="#e0e0e0" font-size="1cm">
                                            <xsl:value-of select="concat($DocType, ' ', $DocSubType)"/>
                                        </fo:block>
                                        <fo:block margin-top="0.5cm">Invoice #{100}</fo:block>
                                        <fo:block>DATE: {OCTOBER 9, 2011}</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <fo:table margin-top="1cm" table-layout="fixed">
                            <fo:table-column column-width="50%" text-align="left"/>
                            <fo:table-column column-width="50%" text-align="left"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">TO</fo:block>
                                        <fo:block>{Name}</fo:block>
                                        <fo:block>{Company Name}</fo:block>
                                        <fo:block>{Street address}</fo:block>
                                        <fo:block>{City, ST ZIP Code}</fo:block>
                                        <fo:block>{Phone}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">SHIP TO</fo:block>
                                        <fo:block>{Name}</fo:block>
                                        <fo:block>{Company Name}</fo:block>
                                        <fo:block>{Street address}</fo:block>
                                        <fo:block>{City, ST ZIP Code}</fo:block>
                                        <fo:block>{Phone}</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                    </fo:block-container>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>