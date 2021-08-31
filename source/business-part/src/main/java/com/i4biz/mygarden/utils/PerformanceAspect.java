package com.i4biz.mygarden.utils;

import com.i4biz.mygarden.dao.IStatisticDAO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class PerformanceAspect {
    @Autowired
    IStatisticDAO performanceStatisticDAO;

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object o = null;
        try {
            long st = System.currentTimeMillis();
            o = proceedingJoinPoint.proceed();
            long en = System.currentTimeMillis();
            Signature s = proceedingJoinPoint.getSignature();
            String method = s.toString();

            performanceStatisticDAO.savePerformance(method, (en - st));
        } catch (Throwable throwable) {
            throw throwable;
        }

        return o;
    }
}
