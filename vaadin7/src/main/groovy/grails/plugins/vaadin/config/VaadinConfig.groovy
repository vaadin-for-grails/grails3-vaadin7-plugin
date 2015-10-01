package grails.plugins.vaadin.config

import com.vaadin.navigator.View
import com.vaadin.ui.UI
import grails.util.Holders
import groovy.transform.InheritConstructors
import org.apache.log4j.Logger
import org.grails.config.PropertySourcesConfig
import org.grails.core.io.ResourceLocator
import org.grails.gsp.io.GroovyPageStaticResourceLocator
import org.springframework.beans.factory.config.YamlMapFactoryBean
import org.vaadin.grails.ui.DefaultUI
import org.vaadin.grails.util.ApplicationContextUtils

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

    static Class<? extends UI> getUIClass(Map pathMapping) {
        def classLoader = Holders.grailsApplication.classLoader
        def ui = pathMapping.ui
        if (ui) {
            Class<? extends UI> uiClass
            if (!(ui instanceof Class)) {
                uiClass = (Class<? extends UI>) classLoader.loadClass((String) ui)
            } else {
                uiClass = ui
            }
            return uiClass
        }
        null
    }

    static Class<? extends View> getViewClass(Map fragmentMapping) {
        def classLoader = Holders.grailsApplication.classLoader
        def view = fragmentMapping.view
        if (view) {
            Class<? extends View> viewClass
            if (!(view instanceof Class)) {
                viewClass = (Class<? extends View>) classLoader.loadClass((String) view)
            } else {
                viewClass = view
            }
            return viewClass
        }
        null
    }

    Set<Class<?>> getMappedClasses() {
        def mappedClasses = new HashSet<Class<?>>()
        def config = this
        config.vaadin.mappings.each { String path, Map pathMapping ->
            def uiClass = getUIClass(pathMapping)
            if (uiClass) {
                mappedClasses.add(uiClass)
            }
            pathMapping.fragments.each { String fragment, Map fragmentMapping ->
                def viewClass = getViewClass(fragmentMapping)
                if (viewClass) {
                    mappedClasses.add(viewClass)
                }
            }
        }
        mappedClasses
    }
}
