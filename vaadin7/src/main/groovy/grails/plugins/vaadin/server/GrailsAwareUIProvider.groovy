package grails.plugins.vaadin.server

import com.vaadin.annotations.PreserveOnRefresh
import com.vaadin.navigator.Navigator
import com.vaadin.server.UIClassSelectionEvent
import com.vaadin.server.UICreateEvent
import com.vaadin.server.UIProvider
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.communication.PushMode
import com.vaadin.shared.ui.ui.Transport
import com.vaadin.ui.UI
import com.vaadin.util.CurrentInstance
import grails.plugins.vaadin.config.VaadinConfig
import grails.plugins.vaadin.navigator.GrailsAwareViewProvider
import grails.plugins.vaadin.spring.beans.UIClassFactory
import org.apache.log4j.Logger
import org.springframework.web.util.UrlPathHelper
import org.vaadin.grails.navigator.UriMappings
import org.vaadin.grails.util.ApplicationContextUtils

import javax.servlet.http.HttpServletRequest

/**
 * Grails specific implementation for {@link UIProvider}.
 *
 * @author Stephan Grundner
 * @since 1.0
 */
class GrailsAwareUIProvider extends UIProvider {

    private static final Logger log = Logger.getLogger(GrailsAwareUIProvider)

    private final UrlPathHelper pathHelper = new UrlPathHelper()

    protected String getPath(VaadinRequest request) {
        pathHelper.getPathWithinApplication((HttpServletRequest) request)
    }

    protected UriMappings getUriMappings() {
        ApplicationContextUtils.applicationContext.getBean(UriMappings)
    }

    protected Class<? extends UI> getDefaultUIClass() {
        UIClassFactory factory = ApplicationContextUtils
                .applicationContext.getBean("&uiClass")
        factory.objectType
    }

    @Override
    Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        def path = getPath(event.request)
        def uriMappings = getUriMappings()
        def uiClass = uriMappings.lookupUIClass(path)
        if (uiClass) {
            return uiClass
        } else if (uriMappings.containsPathMapping(path)) {
            log.debug("No UI class specified for path [$path], using default!")
            return defaultUIClass
        }

        log.debug("No UI class found for path [$path]")

        null
    }

    protected UI createInstance(Class<UI> uiClass) {
        ApplicationContextUtils.getBeanOrInstance(uiClass)
    }

    protected Navigator createNavigator(UI ui) {
        def navigator = new Navigator(ui, ui)
        navigator.addProvider(new GrailsAwareViewProvider())
        navigator
    }

    @Override
    UI createInstance(UICreateEvent event) {
        CurrentInstance.set(UIID, new UIID(event))
        try {
            def uiClass = event.getUIClass()
            def path = getPath(event.request)
            log.debug("Creating ui for class [${uiClass.name}] matching path [$path] with id [${event.uiId}]")
            UI ui = createInstance(uiClass)
            def navigator = ui.navigator
            if (navigator == null && uriMappings.containsAnyFragmentMapping(path)) {
                navigator = createNavigator(ui)
                ui.navigator = navigator
            }
            ui
        } finally {
            CurrentInstance.set(UIID, null)
        }
    }

    @Override
    String getTheme(UICreateEvent event) {
        def path = getPath(event.request)
        def config = VaadinConfig.getCurrent()
        def propertyName = UriMappings.THEME_PROPERTY
        def value = uriMappings.lookupProperty(path, propertyName) ?:
                super.getTheme(event) ?:
                        config.getProperty(propertyName)
        value
    }

    @Override
    String getWidgetset(UICreateEvent event) {
        def path = getPath(event.request)
        def config = VaadinConfig.getCurrent()
        def propertyName = UriMappings.WIDGETSET_PROPERTY
        def value = uriMappings.lookupProperty(path, propertyName) ?:
                super.getWidgetset(event) ?:
                        config.getProperty(propertyName)
        value
    }

    @Override
    boolean isPreservedOnRefresh(UICreateEvent event) {
        def path = getPath(event.request)
        def config = VaadinConfig.getCurrent()
        def propertyName = UriMappings.PRESERVED_ON_REFRESH_PROPERTY
        def value = uriMappings.lookupProperty(path, propertyName)
        if (value == null) {
            def annotation = getAnnotationFor(event.getUIClass(), PreserveOnRefresh)
            value = annotation != null ? true : null
            if (value == null) {
                value = config.getProperty(propertyName)
            }
        }
        value ?: false
    }

    @Override
    String getPageTitle(UICreateEvent event) {
        def path = getPath(event.request)
        def config = VaadinConfig.getCurrent()
        def propertyName = UriMappings.PAGE_TITLE_PROPERTY
        def value = uriMappings.lookupProperty(path, propertyName) ?:
                super.getPageTitle(event) ?:
                        config.getProperty(propertyName)
        value
    }

    @Override
    PushMode getPushMode(UICreateEvent event) {
        def path = getPath(event.request)
        def config = VaadinConfig.getCurrent()
        def propertyName = UriMappings.PUSH_MODE_PROPERTY
        def value = uriMappings.lookupProperty(path, propertyName) ?:
                super.getPushMode(event) ?: config.getProperty(propertyName)
        value
    }

    @Override
    Transport getPushTransport(UICreateEvent event) {
        def path = getPath(event.request)
        def config = VaadinConfig.getCurrent()
        def propertyName = UriMappings.PUSH_TRANSPORT_PROPERTY
        def value = uriMappings.lookupProperty(path, propertyName) ?:
                super.getPushTransport(event) ?:
                        config.getProperty(propertyName)
        value
    }
}
