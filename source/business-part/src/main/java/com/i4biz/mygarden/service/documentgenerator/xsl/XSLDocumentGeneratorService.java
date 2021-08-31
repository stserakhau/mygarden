package com.i4biz.mygarden.service.documentgenerator.xsl;

import com.i4biz.mygarden.service.documentgenerator.IDocumentGeneratorService;
import com.i4biz.mygarden.service.documentgenerator.TemplateSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.processor.TransformerFactoryImpl;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import static com.i4biz.mygarden.service.documentgenerator.TemplateConstants.XSL_EXT;

public class XSLDocumentGeneratorService implements IDocumentGeneratorService {
    private static final Log LOG = LogFactory.getLog(XSLDocumentGeneratorService.class);

    private final TemplateSource templateSource;

    public XSLDocumentGeneratorService(TemplateSource templateSource) {
        this.templateSource = templateSource;
    }


    @Override
    public byte[] createDocument(String templateName, byte[] xmlContentData, Map<String, Object> params, Object... args) throws DocumentGeneratorServiceException {
        try {
            Template templ = getTemplate(templateName, args);

            Transformer transformer = templ.getTransformer();

            if (xmlContentData == null) {
                xmlContentData = "<empty/>".getBytes();
            }

            String xml = new String(xmlContentData, "UTF-8");

            StreamSource src = new StreamSource(new ByteArrayInputStream(xml.getBytes()));

            for (Map.Entry<String, Object> e : params.entrySet()) {
                if (e.getValue() != null) {
                    transformer.setParameter(
                            e.getKey(),
                            e.getValue()
                    );
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StreamResult res = new StreamResult(out);

            transformer.transform(src, res);
            return out.toByteArray();
        } catch (Exception e) {
            throw new DocumentGeneratorServiceException(templateName + ":" + e.getMessage(), e);
        }
    }

    private Template getTemplate(String templateName, Object... args) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance(TransformerFactoryImpl.class.getName(), null);
        tFactory.setURIResolver(new URIResolverImpl(templateSource));

        InputStream xsltStream = templateSource.getSource(templateName + "/default.xsl", args);
        Transformer transformer = tFactory.newTransformer(new StreamSource(xsltStream));
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");

        transformer.setErrorListener(new ErrorListenerImpl());

        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");

        return new Template(transformer);
    }

    private class URIResolverImpl implements URIResolver {
        private final TemplateSource templateSource;

        URIResolverImpl(TemplateSource templateSource) {
            super();
            this.templateSource = templateSource;
        }

        @Override
        public Source resolve(String href, String base) throws TransformerException {
            try {
                String hrefToLoad = href;
                if (!StringUtils.isEmpty(href) && href.endsWith(XSL_EXT)) {
                    hrefToLoad = href.substring(0, href.length() - XSL_EXT.length());
                }
                InputStream stream = templateSource.getSource(hrefToLoad);
                return new StreamSource(stream);
            } catch (TemplateSource.SourceNotFoundException e) {
                LOG.error("Resource not found. href=" + href + "; base=" + base);
                throw new TransformerException("Resource not found. href=" + href + "; base=" + base, e);
            }
        }
    }

    private class ErrorListenerImpl implements ErrorListener {
        @Override
        public void warning(TransformerException exception) throws TransformerException {
            LOG.warn(exception.getMessage());
            exception.printStackTrace();
        }

        @Override
        public void error(TransformerException exception) throws TransformerException {
            LOG.error(exception.getMessage(), exception);
            exception.printStackTrace();
        }

        @Override
        public void fatalError(TransformerException exception) throws TransformerException {
            LOG.error(exception.getMessage(), exception);
            exception.printStackTrace();
        }
    }

    private class Template {
        private final Transformer transformer;

        private Template(Transformer transformer) {
            this.transformer = transformer;
        }

        public Transformer getTransformer() {
            return transformer;
        }
    }
}
