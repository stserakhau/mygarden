package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.service.bypage.IFertilizerInventoryService;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.springframework.web.context.WebApplicationContext;

public class FertilizerStaticContentPreparer implements ViewPreparer {
    @Override
    public void execute(Request request, AttributeContext attributeContext) {
        WebApplicationContext applicationContext = (WebApplicationContext) ((ServletRequest) request).getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        IFertilizerInventoryService speciesInventoryService = applicationContext.getBean(IFertilizerInventoryService.class);

        Long fertilizerId = new Long(request.getParam().get("fertilizerId"));
        Item fertilizer = speciesInventoryService.loadFertilizerById(fertilizerId);

        ((ServletRequest) request).getRequest().setAttribute("fertilizer", fertilizer);
    }
}
