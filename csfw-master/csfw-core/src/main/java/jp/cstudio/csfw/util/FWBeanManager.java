package jp.cstudio.csfw.util;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FWBeanManager {

    private static BeanManager beanManager;

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> beanType, Annotation... bindings) {

        BeanManager manager = getBeanManager(false);
        if (manager == null) {
            return null;
        }
        Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(beanType, bindings));
        if (bean == null) {
            manager = getBeanManager(true);
            bean = (Bean<T>) manager.resolve(manager.getBeans(beanType, bindings));
            if (bean == null) {
                return null;
            }
        }
        return (T) manager.getReference(bean, beanType, manager.createCreationalContext(bean));
    }

    private static BeanManager getBeanManager(boolean isRefresh) {

        if (beanManager == null || isRefresh) {
            try {
                beanManager = InitialContext.doLookup("java:comp/env/BeanManager");
            } catch (NamingException e) {
                // TODO 例外処理
                e.printStackTrace();
            }
        }

        return beanManager;
    }

}
