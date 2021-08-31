package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.task.Task;
import com.i4biz.mygarden.service.ITaskService;
import com.i4biz.mygarden.service.bypage.ISpeciesInventoryService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

public class SpeciesInventorySystemViewPreparer extends AbstractPreparer {
    @Override
    public void doExecute(Request request, AttributeContext attributeContext) {
        ServletRequest sr = (ServletRequest) request;

        WebApplicationContext applicationContext = (WebApplicationContext) sr.getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);


        String monthStr = sr.getParam().get("month");
        String taskIdStr = sr.getParam().get("taskId");

        Integer monthNum = StringUtils.isNumeric(monthStr) ? new Integer(monthStr) : null;
        Long taskId = StringUtils.isNumeric(taskIdStr) ? new Long(taskIdStr) : null;;

        if(monthNum!=null) {
            ITaskService taskService = applicationContext.getBean(ITaskService.class);
            monthNum = Integer.parseInt(monthStr);
            List<Task> tasks = taskService.findAvailableTaskInPatternsForMonth(monthNum);

            ((ServletRequest) request).getRequest().setAttribute("availableTasks", tasks);
        }

        ISpeciesInventoryService speciesInventoryService = applicationContext.getBean(ISpeciesInventoryService.class);

        Collection<Map.Entry<Catalog, List<Item>>> res = speciesInventoryService.loadSystemSpeciesInventory(monthNum, taskId);

        List<Map.Entry<Map.Entry<Long, String>, List<Item>>> res1 = new ArrayList<>(res.size());

        for (Map.Entry<Catalog, List<Item>> e : res) {
            Catalog sc = e.getKey();
            res1.add(new AbstractMap.SimpleEntry<>(
                    new AbstractMap.SimpleEntry<>(sc.getId(), sc.getName()),
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
        ((ServletRequest) request).getRequest().setAttribute("months", attributeContext.getAttribute("months").getValue());
    }
}
