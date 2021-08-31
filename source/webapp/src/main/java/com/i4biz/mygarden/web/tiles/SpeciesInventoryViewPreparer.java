package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.service.bypage.ISpeciesInventoryService;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

public class SpeciesInventoryViewPreparer extends AbstractPreparer {
    @Override
    public void doExecute(Request request, AttributeContext attributeContext) {
        WebApplicationContext applicationContext = (WebApplicationContext) ((ServletRequest) request).getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        ISpeciesInventoryService speciesInventoryService = applicationContext.getBean(ISpeciesInventoryService.class);

        Collection<Map.Entry<Catalog, List<Item>>> res = speciesInventoryService.loadSpeciesInventory(getUserId(request));

        List<Map.Entry<Map.Entry<Long, String>, List<Item>>> res1 = new ArrayList<>(res.size());

        for (Map.Entry<Catalog, List<Item>> e : res) {
            Catalog sc = e.getKey();
            if (sc.getOwnerId() == null) {
                sc.setId(null);
            }
            res1.add(new AbstractMap.SimpleEntry<>(
                    new AbstractMap.SimpleEntry<>(e.getKey().getId(), e.getKey().getName()),
                    e.getValue()
            ));
        }

        Collections.sort(res1, new Comparator<Map.Entry<Map.Entry<Long, String>, List<Item>>>() {
            @Override
            public int compare(Map.Entry<Map.Entry<Long, String>, List<Item>> o1, Map.Entry<Map.Entry<Long, String>, List<Item>> o2) {
                return o2.getValue().size() - o1.getValue().size();
            }
        });

        ((ServletRequest) request).getRequest().setAttribute("speciesInventory", res1);
    }
}
