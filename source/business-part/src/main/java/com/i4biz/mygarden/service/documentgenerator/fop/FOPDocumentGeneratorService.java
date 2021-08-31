package com.i4biz.mygarden.service.documentgenerator.fop;

import com.i4biz.mygarden.service.documentgenerator.IDocumentGeneratorService;
import com.i4biz.mygarden.service.documentgenerator.TemplateSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.*;
import org.apache.xalan.processor.TransformerFactoryImpl;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import static com.i4biz.mygarden.service.documentgenerator.TemplateConstants.XSL_EXT;

/**
 * Class implements generating PDF document with Apache FOP
 */
public class FOPDocumentGeneratorService implements IDocumentGeneratorService {
    private static final Log LOG = LogFactory.getLog(FOPDocumentGeneratorService.class);

    private final TemplateSource templateConfigSource;
    private final TemplateSource templateSource;

    public FOPDocumentGeneratorService(TemplateSource templateConfigSource, TemplateSource templateSource) {
        this.templateConfigSource = templateConfigSource;
        this.templateSource = templateSource;
    }

    /**
     * Method generates PDF document.
     *
     * @param templateName   template name for identification template content, that will be requested from TemplateSource
     * @param xmlContentData xml content
     * @param params         parameters sends to xsl context params, can be extracted via &lt;xsl:param name="SenderEid"/&gt; + &lt;xsl:value-of select="$SenderEid"/&gt;
     * @return PDF content
     * @throws DocumentGeneratorServiceException
     */
    @Override
    public byte[] createDocument(String templateName, byte[] xmlContentData, Map<String, Object> params, Object... args) throws DocumentGeneratorServiceException {
        try {
            Template templ = getTemplate(templateName, args);

            FopFactory fopFactory = templ.getFopFactory();
            Transformer transformer = templ.getTransformer();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //Setup FOP
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            //Make sure the XSL transformation's result is piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            //Start the transformation and rendering process

            if (xmlContentData == null) {
                xmlContentData = "<empty/>".getBytes();
            }

            String xml = new String(xmlContentData, "UTF-8");

            ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
            Source src = new StreamSource(bais);

            for (Map.Entry<String, Object> e : params.entrySet()) {
                if (e.getValue() != null) {
                    transformer.setParameter(
                            e.getKey(),
                            e.getValue()
                    );
                }
            }
            transformer.transform(src, res);
            return out.toByteArray();
        } catch (Exception e) {
            LOG.error("Can't build PDF.", e);
            throw new DocumentGeneratorServiceException(e.getMessage(), e);
        }
    }

    private Template getTemplate(String templateName, Object... args) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance(TransformerFactoryImpl.class.getName(), null);
        tFactory.setURIResolver(new URIResolverImpl(templateSource));

        InputStream xsltStream = templateSource.getSource(templateName + "/default.xsl", args);
        Transformer transformer = tFactory.newTransformer(new StreamSource(xsltStream));
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");

        transformer.setErrorListener(new ErrorListenerImpl());
        URIResolver documentUriResolver = new URIResolverImpl(templateConfigSource);

        transformer.setURIResolver(documentUriResolver);
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");


        InputStream fopConfig = templateConfigSource.getSource("fop-config.xml");

        FopConfParser parser = new FopConfParser(fopConfig, templateConfigSource.getUri());
        FopFactoryBuilder builder = parser.getFopFactoryBuilder();
        FopFactory fopFactory = builder.build();

        return new Template(fopFactory, transformer, documentUriResolver);//todo should be cached. economy on tFactory.newTransformer
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
        }

        @Override
        public void error(TransformerException exception) throws TransformerException {
            LOG.error(exception.getMessage(), exception);
        }

        @Override
        public void fatalError(TransformerException exception) throws TransformerException {
            LOG.error(exception.getMessage(), exception);
        }
    }

    private class Template {
        private final FopFactory fopFactory;
        private final Transformer transformer;
        private final URIResolver documentUriResolver;

        private Template(FopFactory fopFactory, Transformer transformer, URIResolver documentUriResolver) {
            this.fopFactory = fopFactory;
            this.transformer = transformer;
            this.documentUriResolver = documentUriResolver;
        }

        public FopFactory getFopFactory() {
            return fopFactory;
        }

        public Transformer getTransformer() {
            return transformer;
        }

        public URIResolver getDocumentUriResolver() {
            return documentUriResolver;
        }
    }
}

