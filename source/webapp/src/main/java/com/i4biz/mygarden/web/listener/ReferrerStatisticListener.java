package com.i4biz.mygarden.web.listener;

import com.i4biz.mygarden.service.IStatisticService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;

public class ReferrerStatisticListener implements ServletRequestListener {
    static Collection<String> excludeFromStat = new ArrayList<String>() {{
        add("egardening.ru");
    }};


    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();

        String target = req.getRequestURI();
//        if (!target.endsWith(".tiles")) {
//            return;
//        }
        String referer = req.getHeader("referer");
        if (referer == null) {
            return;
        }

        for (String domain : excludeFromStat) {
            int pos = referer.indexOf(domain);
            if (pos != -1) {
                return;
            }
        }

        HttpSession session = req.getSession();
        ApplicationContext ctx = WebApplicationContextUtils.
                getWebApplicationContext(session.getServletContext());

        IStatisticService statisticService = ctx.getBean(IStatisticService.class);
        statisticService.collectReferer(referer, target);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
    }
}
