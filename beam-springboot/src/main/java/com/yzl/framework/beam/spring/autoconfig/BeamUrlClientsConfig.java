package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.annotation.BeamInterface;
import com.yzl.framework.beam.annotation.BeamUrlClient;
import com.yzl.framework.beam.direct.UrlRepository;
import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.rpc.Protocol;
import com.yzl.framework.beam.spring.annotation.BeamUrlClientConfigs;
import com.yzl.framework.beam.spring.annotation.BeamUrlClientScan;
import com.yzl.framework.beam.spring.utils.ConvertUtils;
import com.yzl.framework.beam.spring.utils.ScanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Configuration
public class BeamUrlClientsConfig implements ImportAware, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, ApplicationContextAware {
    private ResourceLoader resourceLoader;
    private ClassLoader classLoader;
    private AnnotationMetadata importMetadata;
    private Environment environment;
    private ApplicationContext applicationContext;

    private final static String DEFAULT_PREFIX = "beam.url.";
    private final static String PREFIX_KEY = "beam.url.prefix";

    @Bean
    @ConditionalOnProperty(name = "beam.urlRepository", havingValue = "springEnv", matchIfMissing = true)
    public UrlRepository createUrlRepository() {
        return new SpringEnvUrlRepository(environment);
    }

    @Bean
    public BeamUrlClientManager createUrlBeamClients() {
        BeamUrlClientManager beamUrlClientManager = new BeamUrlClientManager(getProtocol(), getUrlRepository());
        beamUrlClientManager.setUrlKeyPrefix(environment.getProperty(PREFIX_KEY, DEFAULT_PREFIX));
        List<AnnotationAttributes> enableUrlBeamClientAttrs = getEnableUrlBeamClientValues();
        for (AnnotationAttributes annotationAttributes : enableUrlBeamClientAttrs) {
            Class<?> clazz = (Class<?>) annotationAttributes.get("clazz");
            String interfaceName = clazz.getName();
            String urlKey = (String) annotationAttributes.get("urlKey");
            Map<String, String> parameters = ConvertUtils.convertMap(annotationAttributes);
            beamUrlClientManager.getOrCreateClientProxy(interfaceName, urlKey, parameters);
        }

        Set<BeanDefinition> candidateComponents = scanBeamInterfaces();
        for (BeanDefinition candidateComponent : candidateComponents) {
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
            AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
            String interfaceName = annotationMetadata.getClassName();
            BeamUrlClient annotation = AnnotationUtils.synthesizeAnnotation(BeamUrlClient.class);
            Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation);
            annotationAttributes.put("clazz", interfaceName);
            String urlKey = (String) annotationAttributes.get("urlKey");
            Map<String, String> parameters = ConvertUtils.convertMap(annotationAttributes);

            beamUrlClientManager.getOrCreateClientProxy(interfaceName, urlKey, parameters);
        }
        return beamUrlClientManager;
    }

    protected List<AnnotationAttributes> getEnableUrlBeamClientValues() {
        Map<String, Object> attributes = importMetadata.getAnnotationAttributes(BeamUrlClientConfigs.class.getCanonicalName());
        if (attributes == null) {
            return Collections.emptyList();
        }
        AnnotationAttributes[] annotationAttributesArr = (AnnotationAttributes[]) attributes.get("value");
        if (annotationAttributesArr != null) {
            return Arrays.asList(annotationAttributesArr);
        }
        return Collections.emptyList();
    }

    protected Set<BeanDefinition> scanBeamInterfaces() {
        Set<String> basePackages = getBasePackages();
        return ScanUtils.findBeanDefinitionWithAnnotation(basePackages, BeamInterface.class, classLoader, resourceLoader);
    }

    protected Set<String> getBasePackages() {
        Map<String, Object> attributes = importMetadata.getAnnotationAttributes(BeamUrlClientScan.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();

        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        basePackages.add(ClassUtils.getPackageName(importMetadata.getClassName()));
        return basePackages;
    }

    private Protocol getProtocol() {
        //TODO 只支持一个BeamProtocol协议
        Map<String, Protocol> protocolMap = applicationContext.getBeansOfType(Protocol.class);
        if (protocolMap.size() > 1 || protocolMap.size() == 0) {
            throw new BeamFrameworkException("Must has only one BeamProtocol bean, but has " + protocolMap.size());
        }
        return new ArrayList<>(protocolMap.values()).get(0);
    }

    private UrlRepository getUrlRepository() {
        Map<String, UrlRepository> repositoryMap = applicationContext.getBeansOfType(UrlRepository.class);
        if (repositoryMap.size() > 1 || repositoryMap.size() == 0) {
            throw new BeamFrameworkException("Must has only one UrlRepository bean, but has " + repositoryMap.size());
        }
        return new ArrayList<>(repositoryMap.values()).get(0);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.importMetadata = importMetadata;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
