package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.user.UserWorkView;
import com.i4biz.mygarden.service.IUserWorkService;
import com.i4biz.mygarden.service.bypage.ISpeciesInventoryService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.springframework.web.context.WebApplicationContext;

public class SpeciesStaticContentPreparer extends AbstractPreparer {
    @Override
    public void doExecute(Request request, AttributeContext attributeContext) {
        WebApplicationContext applicationContext = (WebApplicationContext) ((ServletRequest) request).getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        Long userId = getUserId(request);

        String userWorkIdS = request.getParam().get("userWorkId");
        String userPatternIdS = request.getParam().get("userPatternId");
        if(StringUtils.isEmpty(userWorkIdS)) {
            userWorkIdS = userPatternIdS;
        }

        String speciesIdS = request.getParam().get("speciesId");
        Long speciesId = StringUtils.isNotEmpty(speciesIdS) ? new Long(speciesIdS) : null;

        if (StringUtils.isNotEmpty(userWorkIdS)) {
            IUserWorkService userWorkService = applicationContext.getBean(IUserWorkService.class);
            Long userWorkId = new Long(userWorkIdS);
            UserWorkView userWork = userWorkService.findUserWorkViewById(userWorkId, userId);
            speciesId = userWork.getSpeciesId();
            ((ServletRequest) request).getRequest().setAttribute("pattern", userWork);
        }

        if(speciesId!=null) {
            ISpeciesInventoryService speciesInventoryService = applicationContext.getBean(ISpeciesInventoryService.class);
            Item species = speciesInventoryService.loadMyOrSystemSpeciesById(speciesId, userId);
            ((ServletRequest) request).getRequest().setAttribute("species", species);
        }
    }
}
