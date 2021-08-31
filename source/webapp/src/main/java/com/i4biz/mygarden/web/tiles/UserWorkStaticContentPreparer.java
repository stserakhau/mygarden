package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.domain.user.UserWorkView;
import com.i4biz.mygarden.service.IUserWorkService;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.springframework.web.context.WebApplicationContext;

public class UserWorkStaticContentPreparer extends AbstractPreparer {
    @Override
    public void doExecute(Request request, AttributeContext attributeContext) {
        WebApplicationContext applicationContext = (WebApplicationContext) ((ServletRequest) request).getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        IUserWorkService userWorkService = applicationContext.getBean(IUserWorkService.class);

        Long userWorkId = new Long(request.getParam().get("userWorkId"));
        UserWorkView userWorkView = userWorkService.findUserWorkViewById(userWorkId, getUserId(request));

        ((ServletRequest) request).getRequest().setAttribute("userWork", userWorkView);
    }
}
