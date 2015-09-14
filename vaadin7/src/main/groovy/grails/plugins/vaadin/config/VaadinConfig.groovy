package grails.plugins.vaadin.config

import org.vaadin.grails.ui.DefaultUI
import groovy.transform.InheritConstructors
import org.apache.log4j.Logger
import org.grails.config.PropertySourcesConfig
import org.grails.core.io.ResourceLocator
import org.grails.gsp.io.GroovyPageStaticResourceLocator
import org.springframework.beans.factory.config.YamlMapFactoryBean

/**
 *
 */
@InheritConstructors
class VaadinConfig extends PropertySourcesConfig {

    private static final log = Logger.getLogger(VaadinConfig)

    private static VaadinConfig config

    private VaadinConfig(Map config) {
        super(config)
    }

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

            config = [
                vaadin: [
                    theme: 'valo',
                    mappings: ['/vaadin': [ui: DefaultUI]]
                ]
            ]
        }

        new VaadinConfig(config ?: Collections.EMPTY_MAP)
    }

}
