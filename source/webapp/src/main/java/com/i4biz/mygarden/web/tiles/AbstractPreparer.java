package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.domain.user.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractPreparer implements ViewPreparer {
    private final Log LOG = LogFactory.getLog(getClass());

    @Override
    public void execute(Request request, AttributeContext attributeContext) {
        try {
            doExecute(request, attributeContext);
        } catch(Exception e) {
            e.printStackTrace();
            LOG.warn(e.getMessage(), e);
            HttpServletRequest req = ((ServletRequest) request).getRequest();
            HttpServletResponse res = ((ServletRequest) request).getResponse();
            try {
                res.sendRedirect("/");
            } catch (Exception e1) {
                e1.printStackTrace();
                LOG.warn(e1.getMessage(), e1);
            }
        }
    }

    protected Long getUserId(Request request) {
        User user = (User) ((ServletRequest) request).getRequest().getSession().getAttribute(User.class.getName());
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    protected abstract void doExecute(Request tilesContext, AttributeContext attributeContext);
}
