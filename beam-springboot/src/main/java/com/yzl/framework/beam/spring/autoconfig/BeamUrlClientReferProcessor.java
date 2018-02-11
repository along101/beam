package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.annotation.BeamUrlClient;
import com.yzl.framework.beam.spring.utils.ConvertUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class BeamUrlClientReferProcessor implements BeanPostProcessor {
    @Autowired
    private BeamUrlClientManager beamUrlClientManager;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                BeamUrlClient reference = field.getAnnotation(BeamUrlClient.class);
                if (reference != null) {
                    if (field.get(bean) != null) {
                        throw new BeanInitializationException("Field " + field.getName() + " in class " + bean.getClass().getName() + " is not null.");
                    }
                    Object value = refer(field.getType(), reference);
                    if (value != null) {
                        field.set(bean, value);
                    } else {
                        throw new BeanInitializationException("Can not find Object " + bean.getClass().getName());
                    }
                }
            } catch (Exception e) {
                throw new BeanInitializationException("Failed to init remote service reference at filed " + field.getName()
                        + " in class " + bean.getClass().getName(), e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private Object refer(Class<?> referenceClass, BeamUrlClient reference) {
        Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(reference);
        annotationAttributes.put("clazz", referenceClass.getName());

        Map<String, String> parameters = ConvertUtils.convertMap(annotationAttributes);
        return beamUrlClientManager.getOrCreateClientProxy(referenceClass.getName(), reference.urlKey(), parameters);
    }
}
