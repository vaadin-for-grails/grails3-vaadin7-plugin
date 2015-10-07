package grails.plugins.vaadin.config

import grails.core.GrailsApplication
import groovy.transform.InheritConstructors
import org.apache.log4j.Logger
import org.grails.config.PropertySourcesConfig
import org.grails.core.io.ResourceLocator
import org.grails.gsp.io.GroovyPageStaticResourceLocator
import org.springframework.beans.factory.config.YamlMapFactoryBean

/**
 *
 * @author Stephan Grundner
 * @since 1.0
 */
@InheritConstructors
class VaadinConfig extends PropertySourcesConfig {

    private static final log = Logger.getLogger(VaadinConfig)

    private static VaadinConfig config

    static VaadinConfig getCurrent() {
        if (config == null) {
            config = load()
        }
        config
    }

    private static VaadinConfig load() {
        Map config = null
        log.debug("Loading Vaadin config")
        try {
            ResourceLocator resourceLocator = new GroovyPageStaticResourceLocator()
            def resource = resourceLocator.findResourceForURI('classpath:vaadin.yml')
            if (resource?.exists()) {
                def yamlMapFactory = new YamlMapFactoryBean()
                yamlMapFactory.setResources(resource)
                config = yamlMapFactory.object
            }

        } catch (e) {
            log.error("Loading Vaading config failed", e)
        }
        new VaadinConfig(config ?: Collections.EMPTY_MAP)
    }

    private VaadinConfig(Map config) {
        super(config)
    }

    Set<Class<?>> lookupMappedClasses(GrailsApplication grailsApplication) {
        def config = this
        def mappedClasses = new HashSet<Class<?>>()
        def classLoader = grailsApplication.classLoader
        config.mappings.each { mapping ->
            if (mapping.ui) {
                mappedClasses.add classLoader.loadClass(mapping.ui as String)
            }
            if (mapping.view) {
                mappedClasses.add classLoader.loadClass(mapping.view as String)
            }
        }
        mappedClasses
    }
}
