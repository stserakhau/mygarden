package com.i4biz.mygarden.security;


import com.i4biz.mygarden.domain.user.User;

import java.io.Serializable;

public class ApplicationThreadContext {
    private static final ThreadLocal<ThreadLocalStorage> tl = new ThreadLocal<ThreadLocalStorage>();

    public ApplicationThreadContext() {
        tl.set(new ThreadLocalStorage());
    }


    private static ThreadLocalStorage getThreadLocalStorage() {
        ThreadLocalStorage tls = tl.get();
        if (tls == null) {
            tls = new ThreadLocalStorage();
            tl.set(tls);
        }
        return tls;
    }

    public static User getAuthorizedUser() {
        return getThreadLocalStorage().authorizedUser;
    }
    public static long getAuthorizedUserId() {
        return getThreadLocalStorage().authorizedUser.getId();
    }

    public static void setAuthorizedUser(User user) {
        getThreadLocalStorage().authorizedUser = user;
    }

    private static class ThreadLocalStorage implements Serializable {
        private User authorizedUser;
    }
}
