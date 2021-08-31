<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xslt/java"
                xmlns:sf="java.text.SimpleDateFormat"
                xmlns:po="com.i4biz.autobiz.domain.po.PurchaseOrderView"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <xsl:output version="1.0" method="xml" encoding="UTF-8" indent="no"/>

    <xsl:decimal-format name="form" decimal-separator="." grouping-separator="&#160;"/>

    <xsl:param name="purchaseOrder"/>

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
                    <fo:block-container font-size="8px" font-family="arial">
                        <fo:table margin-top="1cm" table-layout="fixed">
                            <fo:table-column column-width="30%"/>
                            <fo:table-column column-width="30%"/>
                            <fo:table-column column-width="30%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block margin-top="0.5cm">ООО "Немецкий Дом Балашиха"</fo:block>
                                        <fo:block margin-top="0.5cm">Официальный диллер Volkswagen</fo:block>
                                        <fo:block>Принял: <xsl:value-of select="po:getMasterName($purchaseOrder)"/></fo:block>
                                        <fo:block>Сервис: +7 (495) 1500405</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="center">
                                        <fo:block>
                                            <fo:external-graphic src="../pdf-templates/purchase_order/vw-logo.jpg"
                                                                 content-height="scale-to-fit"
                                                                 height="3cm"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block margin-top="0.5cm">
                                            Заказ-наряд № <xsl:value-of select="po:getId($purchaseOrder)"/> от <xsl:value-of select="java:format(sf:new('dd MMM yyyy hh:mm'), po:getCreatedAt($purchaseOrder))"/>
                                        </fo:block>
                                        <fo:block><xsl:value-of select="po:getCustomerName($purchaseOrder)"/></fo:block>
                                        <fo:block><xsl:value-of select="po:getCustomerCarModel($purchaseOrder)"/></fo:block>
                                        <fo:block>Гос.номер: <xsl:value-of select="po:getCustomerCarRegistrationNumber($purchaseOrder)"/></fo:block>
                                        <fo:block>VIN: #{vin}</fo:block>
                                        <fo:block>Пробег: #{odometr}</fo:block>
                                        <fo:block>телефон клиента <xsl:value-of select="po:getCustomerPhone($purchaseOrder)"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding-left="0.5cm">
                                        <fo:block>
                                            <!-- http://barcode4j.sourceforge.net/2.1/symbol-ean-128.html -->
                                            <fo:instream-foreign-object>
                                                <barcode:barcode
                                                        xmlns:barcode="http://barcode4j.krysalis.org/ns"
                                                        message="{po:getId($purchaseOrder)}" orientation="90">
                                                    <barcode:code128>
                                                        <barcode:height>8mm</barcode:height>
                                                    </barcode:code128>
                                                </barcode:barcode>
                                            </fo:instream-foreign-object>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>



                        <fo:table margin-top="1cm" table-layout="fixed">
                            <fo:table-column column-width="2cm"/>
                            <fo:table-column column-width="1cm"/>
                            <fo:table-column column-width="2cm"/>
                            <fo:table-column column-width="12cm"/>
                            <fo:table-header>
                                <fo:table-row font-weight="bold">
                                    <fo:table-cell text-align="left">
                                        <fo:block>Выполнено</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block>№</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block>Время</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block>Вид ремонтных работ со слов клиента</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left"><fo:block></fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block>1</fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block></fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block>ТО-2</fo:block></fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left"><fo:block></fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block>2</fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block></fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block>инспекционный сервис</fo:block></fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left"><fo:block></fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block>3</fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block></fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block>регламентное обслуживание</fo:block></fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left"><fo:block></fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block>4</fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block></fo:block></fo:table-cell>
                                    <fo:table-cell text-align="left"><fo:block>микрофон на стороне водителя не закреплен</fo:block></fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>


                        <fo:block-container margin-top="1cm">
                            <fo:block font-weight="bold">Замененные детали передать клиенту: <fo:inline>[_]Да</fo:inline><fo:inline padding-left="0.5cm">[_]Нет</fo:inline></fo:block>
                            <fo:block>Ожидание в салоне: <fo:inline>[_]Да</fo:inline><fo:inline padding-left="0.5cm">[_]Нет</fo:inline></fo:block>
                            <fo:block>Обсуждение предстоящих работ у автомобиля ________________</fo:block>
                        </fo:block-container>

                        <fo:block-container margin-top="1cm">
                            <fo:block>Заказ выполняется согласно действующим условиям выполнения работ по автомобилям прицепам, агрегатам и их деталям и в соответствии установленным расценкам</fo:block>
                            <fo:block>
                                Гарантия на выполненные работы-15 дней, на оригинальные з\части-2 года (В случаях гарантийного ремонта, гарантия на замененную запасную часть заканчивается вместе с гарантией на автомобиль). Для выполнения работ, отвечающих требованиям производителя  Фольксваген АГ,
                                исполнитель оставляет за собой право , на использование расходных материалов. Выдача автомобиля из ремонта осуществляется не позднее 1(одного) дня с момента устного уведомления заказчика о готовности автомобиля. Дальнейшая стоянка автомобиля оплачивается из расчета
                                200(двести) рублей за каждый день стоянки. В соответствии со ст.ст. 359,360,712 Гражданского кодекса РФ исполнитель имеет право на удержание автотранспортного средства до полной оплаты Заказчиком работ и издержек на хранение. Предварительно согласованный срок
                                проведения работ - 45 дней.Настоящим заказчик выражает свое безусловное согласие на обработку (на осуществление любых действий, операций) любым способом информации, относящейся к персональным данным Заказчика, которая предоставлена или может быть предоставлена
                                Заказчиком Исполнителю при заключении и исполнении настоящего договора (далее – персональные данные), в том числе, на передачу указанных персональных данных и осуществление аналогичных действий импортером автомобиля в РФ, а также любым иным компаниям, с которыми
                                импортер автомобиля по собственному усмотрению заключил/заключит соответствующие договоры  для следующих целей:
                            </fo:block>
                            <fo:block>
                                1. Предоставление заказчику информации о товарах и услугах, которые потенциально могут представлять интерес;
                            </fo:block>
                            <fo:block>
                                2. Сбора и обработки статистической информации и проведения маркетинговых, социологических и других исследований;
                            </fo:block>
                            <fo:block>
                                3. Доставка заказанных/согласованных товаров и предоставления услуг.
                            </fo:block>
                            <fo:block>
                                Согласие на обработку персональных данных в соответствии с указанными выше условиями предоставляется Заказчиком на десять лет. Заказчик уведомлен и согласен с тем, что указанное согласие может быть отозвано путем направления в письменной форме уведомления
                                Исполнителю или Импортеру автомобиля заказанным почтовым отправлением с описью вложения, либо под роспись уполномоченному представителю. В случае обнаружения в процессе ремонта дефектов, устраняемых в рамках гарантийного ремонта, даю свое согласие на их устранение
                                по гарантии.
                            </fo:block>
                        </fo:block-container>

                        <fo:block-container margin-top="1cm">
                            <fo:block>Предварительная стоимость услуг и деталей по заказ-наряду на момент обращения составляет около: 22 200 рублей</fo:block>
                        </fo:block-container>
                        <fo:block-container margin-top="1cm">
                            <fo:block>Предварительное время окончания работ :
                                <xsl:if test="po:getProbableTimeIssue($purchaseOrder)!=''">
                                    <xsl:value-of select="java:format(sf:new('dd MMM yyyy hh:mm'), po:getProbableTimeIssue($purchaseOrder))"/>
                                </xsl:if>
                            </fo:block>
                        </fo:block-container>

                    </fo:block-container>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>