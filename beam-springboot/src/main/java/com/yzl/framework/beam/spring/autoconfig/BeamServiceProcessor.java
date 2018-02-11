package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.annotation.BeamInterface;
import com.yzl.framework.beam.annotation.BeamService;
import com.yzl.framework.beam.exception.BeamServiceException;
import com.yzl.framework.beam.protocol.beam.BeamProtocol;
import com.yzl.framework.beam.registry.Registry;
import com.yzl.framework.beam.rpc.*;
import com.yzl.framework.beam.spring.utils.AopHelper;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@Configuration
public class BeamServiceProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //bean字段上有@BeamService注解，设置@BeamService注解的bean
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                BeamService beamService = field.getAnnotation(BeamService.class);
                if (beamService != null) {
                    Object value = getBeamServiceBean(field.getType());
                    if (value != null) {
                        field.set(bean, value);
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
    public Object postProcessAfterInitialization(Object springBean, String beanName) throws BeansException {
        Object bean = springBean;
        if (AopUtils.isAopProxy(bean)) {
            try {
                bean = AopHelper.getTarget(bean);
            } catch (Exception e) {
                throw new BeamServiceException(String.format("Can not find proxy target for %s .", bean.getClass().getName()));
            }
        } else {
            bean = springBean;
        }
        Annotation annotation = AnnotationUtils.findAnnotation(bean.getClass(), BeamService.class);
        if (annotation == null) {
            return springBean;
        }
        List<Class<?>> interfaceClasses = findAllInterfaces(bean);
        if (interfaceClasses.size() == 0) {
            throw new BeamServiceException(String.format("Can not find %s serviceImpl's interface.", bean.getClass().getName()));
        }
        for (Class<?> interfaceClass : interfaceClasses) {
            registryBeamService(interfaceClass, springBean);
        }
        return springBean;
    }


    private List<Class<?>> findAllInterfaces(Object bean) {
        List<Class<?>> beamInterfaces = new ArrayList<>();
        Set<Class<?>> interfaceClasses = ClassUtils.getAllInterfacesAsSet(bean);
        for (Class<?> interfaceClass : interfaceClasses) {
            Annotation annotation = AnnotationUtils.findAnnotation(interfaceClass, BeamInterface.class);
            if (annotation != null) {
                beamInterfaces.add(interfaceClass);
            }
        }
        return beamInterfaces;
    }

    private void registryBeamService(Class interfaceClass, Object bean) {
        //TODO 找到interfaceClass的参数
        URL serviceUrl = URL.builder().parameters(new HashMap<>()).build();
        //TODO 只支持一种协议
        Protocol protocol = applicationContext.getBean(Protocol.class);
        if (protocol == null) {
            throw new BeamServiceException(String.format("Can not find bean of class %s ", BeamProtocol.class.getName()));
        }
        Provider<?> provider = new DefaultProvider<>(interfaceClass, bean, serviceUrl.getParameters());
        Exporter<?> exporter = protocol.export(provider, serviceUrl);
        Registry registry = applicationContext.getBean(Registry.class);
        if (registry == null) {
            throw new BeamServiceException(String.format("Can not find bean of class %s ", Registry.class.getName()));
        }
        registry.register(exporter.getServiceUrl());
    }

    private Object getBeamServiceBean(Class<?> beamServiceClass) {
        Map<String, ?> beans = applicationContext.getBeansOfType(beamServiceClass);
        for (Object bean : beans.values()) {
            BeamService anno = AnnotationUtils.findAnnotation(bean.getClass(), BeamService.class);
            if (anno != null) {
                return bean;
            }
        }
        return null;
    }
}
