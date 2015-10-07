package grails.plugins.vaadin

import grails.plugins.Plugin
import grails.plugins.vaadin.config.VaadinConfig
import grails.plugins.vaadin.navigator.DefaultUriMappings
import grails.plugins.vaadin.server.GrailsAwareVaadinServletRegistrationBean
import grails.plugins.vaadin.server.OpenSessionInViewFilterRegistrationBean
import grails.plugins.vaadin.spring.UIScope
import grails.plugins.vaadin.spring.VaadinSessionScope
import grails.plugins.vaadin.spring.beans.UIClassFactory
import org.apache.log4j.Logger

class Vaadin7GrailsPlugin extends Plugin {

    private static final Logger log = Logger.getLogger(Vaadin7GrailsPlugin)

    def version = "3.0.1"
    def grailsVersion = "3.0.1 > *"

    def title = "Vaadin 7 Plugin"
    def author = "Stephan Grundner"
    def authorEmail = "stephan.grundner@gmail.com"
    def description = '''\
Plugin for integrating Vaadin 7 into Grails 3.x.
'''

    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "https://github.com/vaadin-for-grails/grails3-vaadin7-plugin"

    def license = "APACHE"
    def organization = [ name: "Vaadin for Grails", url: "https://github.com/vaadin-for-grails" ]
    def developers = [ [ name: "Stephan Grundner", email: "stephan.grundner@gmail.com" ]]

    def scm = [ url: "https://github.com/vaadin-for-grails/grails3-vaadin7-plugin.git" ]

    Closure doWithSpring() { {->
            def config = VaadinConfig.getCurrent()
            if (config.autoComponentScan) {
                def mappedClasses = config.lookupMappedClasses(grailsApplication)
                def packageNamesToBeScanned = mappedClasses.collect { it.package }.groupBy { it.name }.keySet()
                log.debug("Automatic component scanning for packages $packageNamesToBeScanned")
                xmlns context: "http://www.springframework.org/schema/context"
                context.'component-scan'('base-package': packageNamesToBeScanned.join(','))
            }

            vaadinSessionScope(VaadinSessionScope)
            uiScope(UIScope)

            vaadinServlet(GrailsAwareVaadinServletRegistrationBean)
            if (pluginManager.allPlugins.find { it.name.startsWith('hibernate') }) {
                openSessionInViewFilter(OpenSessionInViewFilterRegistrationBean)
            }
            uiClass(UIClassFactory)
            uriMappings(DefaultUriMappings)
        }
    }
}
