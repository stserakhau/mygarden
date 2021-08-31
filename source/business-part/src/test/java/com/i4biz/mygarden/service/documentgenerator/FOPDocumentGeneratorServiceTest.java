package com.i4biz.mygarden.service.documentgenerator;


import com.i4biz.mygarden.service.documentgenerator.fop.FOPDocumentGeneratorService;

import java.io.FileOutputStream;
import java.util.HashMap;

public class FOPDocumentGeneratorServiceTest {

    public static void main(String[] args) throws Exception {

        TemplateSource templateSource = new TemplateClassPathSource("/pdf-templates");
        TemplateSource templateConfigSource = new TemplateClassPathSource("/pdf-config");

        IDocumentGeneratorService docGen = new FOPDocumentGeneratorService(templateConfigSource, templateSource);

        HashMap<String, Object> params = new HashMap<>();

//        String xml = "<empty/>";
//        byte[] document = docGen.createDocument("EDI_850_00200.xsl", xml.getBytes(), params);
        String xml = "<stats>\n" +
                "  <stat>\n" +
                "    <statBy>EDI.810</statBy>\n" +
                "    <duration>240</duration>\n" +
                "    <successTotalSize>640784</successTotalSize>\n" +
                "    <error>0</error>\n" +
                "    <inProgress>0</inProgress>\n" +
                "    <success>67</success>\n" +
                "  </stat>\n" +
                "  <stat>\n" +
                "    <statBy>EDI.850</statBy>\n" +
                "    <duration>329</duration>\n" +
                "    <successTotalSize>640784</successTotalSize>\n" +
                "    <error>0</error>\n" +
                "    <inProgress>0</inProgress>\n" +
                "    <success>1</success>\n" +
                "  </stat>\n" +
                "  <stat>\n" +
                "    <statBy>EDI.856</statBy>\n" +
                "    <duration>307</duration>\n" +
                "    <error>0</error>\n" +
                "    <inProgress>0</inProgress>\n" +
                "    <success>1</success>\n" +
                "  </stat>\n" +
                "  <stat>\n" +
                "    <statBy>EDI.180</statBy>\n" +
                "    <duration>5</duration>\n" +
                "    <successTotalSize>640784</successTotalSize>\n" +
                "    <error>0</error>\n" +
                "    <inProgress>0</inProgress>\n" +
                "    <success>1</success>\n" +
                "  </stat>\n" +
                "</stats>";
        params.put("showSuccess", false);
        params.put("showError", true);
        params.put("showInProgress", true);

        byte[] document = docGen.createDocument("default_empty_template.xsl", xml.getBytes(), params);

        FileOutputStream fos = new FileOutputStream("/srv/test.pdf");
        fos.write(document);
        fos.close();
    }
}
