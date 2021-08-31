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

public class SystemSpeciesStaticContentPreparer extends AbstractPreparer {
    @Override
    public void doExecute(Request request, AttributeContext attributeContext) {
        WebApplicationContext applicationContext = (WebApplicationContext) ((ServletRequest) request).getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        ISpeciesInventoryService speciesInventoryService = applicationContext.getBean(ISpeciesInventoryService.class);

        Long specieId = new Long(request.getParam().get("speciesId"));
        Item species = speciesInventoryService.loadMyOrSystemSpeciesById(specieId, null);
        ((ServletRequest) request).getRequest().setAttribute("species", species);

        String userWorkIdS = request.getParam().get("userWorkId");
        String userPatternIdS = request.getParam().get("userPatternId");
        if (StringUtils.isNotEmpty(userWorkIdS) || StringUtils.isNotEmpty(userPatternIdS)) {
            IUserWorkService userWorkService = applicationContext.getBean(IUserWorkService.class);
            Long userWorkId = new Long(userWorkIdS!=null ? userWorkIdS : userPatternIdS);
            UserWorkView userWork = userWorkService.findUserWorkViewById(userWorkId, null);
            ((ServletRequest) request).getRequest().setAttribute("pattern", userWork);
        }
    }
}
