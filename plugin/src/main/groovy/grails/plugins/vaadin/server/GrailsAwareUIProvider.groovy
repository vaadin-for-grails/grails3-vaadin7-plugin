package grails.plugins.vaadin.server

import com.vaadin.annotations.PreserveOnRefresh
import com.vaadin.navigator.Navigator
import com.vaadin.server.UIClassSelectionEvent
import com.vaadin.server.UICreateEvent
import com.vaadin.server.UIProvider
import com.vaadin.shared.communication.PushMode
import com.vaadin.shared.ui.ui.Transport
import com.vaadin.ui.UI
import grails.plugins.vaadin.config.VaadinConfig
import grails.plugins.vaadin.navigator.GrailsAwareViewProvider
import org.apache.log4j.Logger
import org.springframework.web.util.UrlPathHelper
import org.vaadin.grails.server.UriMappings
import org.vaadin.grails.util.ApplicationContextUtils

class GrailsAwareUIProvider extends UIProvider {

    private static final Logger log = Logger.getLogger(GrailsAwareUIProvider)

    private final UrlPathHelper pathHelper = new UrlPathHelper()

    @Override
    Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        def path = pathHelper.getPathWithinApplication(event.request)
        def uriMappings = UriMappings.getCurrent()
        def uiClass = uriMappings.getUIClass(path)
        if (uiClass == null) {
            log.warn("No UI class found for path [$path]")
            return null
        }
        uiClass
    }

    protected Navigator createNavigator(UI ui) {
        def navigator = new Navigator(ui, ui)
        navigator.addProvider(new GrailsAwareViewProvider())
        navigator
    }

    @Override
    UI createInstance(UICreateEvent event) {
//        UIIdHolder.setCurrent(event.uiId)

        def uiClass = event.getUIClass()
        def ui = ApplicationContextUtils.getBeanOrInstance(uiClass)

        def path = pathHelper.getPathWithinApplication(event.request)
        def uriMappings = UriMappings.getCurrent()
        def fragments = uriMappings.getAllFragments(path)
        if (fragments?.size() > 0) {
            ui.navigator = createNavigator(ui)
        }

        ui
    }

    @Override
    String getTheme(UICreateEvent event) {
        def path = pathHelper.getPathWithinApplication(event.request)
        def uriMappings = UriMappings.getCurrent()
        def config = VaadinConfig.getCurrent()
        def value = uriMappings.getPathProperty(path, UriMappings.THEME_PATH_PROPERTY) ?:
                super.getTheme(event) ?: config.theme
        value
    }

    @Override
    String getWidgetset(UICreateEvent event) {
        def path = pathHelper.getPathWithinApplication(event.request)
        def uriMappings = UriMappings.getCurrent()
        def config = VaadinConfig.getCurrent()
        def value = uriMappings.getPathProperty(path, UriMappings.WIDGETSET_PATH_PROPERTY) ?:
                super.getWidgetset(event) ?: config.getProperty(UriMappings.WIDGETSET_PATH_PROPERTY)
        value
    }

    @Override
    boolean isPreservedOnRefresh(UICreateEvent event) {
        def path = pathHelper.getPathWithinApplication(event.request)
        def uriMappings = UriMappings.getCurrent()
        def config = VaadinConfig.getCurrent()
        def value = uriMappings.getPathProperty(path, UriMappings.PRESERVED_ON_REFRESH_PATH_PROPERTY)

        if (value == null) {
            def annotation = getAnnotationFor(event.getUIClass(), PreserveOnRefresh.class);
            value = annotation != null ? true : null;
            if (value == null) {
                value = config.getProperty(UriMappings.PRESERVED_ON_REFRESH_PATH_PROPERTY)
            }
        }
        value ?: false
    }

    @Override
    String getPageTitle(UICreateEvent event) {
        def path = pathHelper.getPathWithinApplication(event.request)
        def uriMappings = UriMappings.getCurrent()
        def config = VaadinConfig.getCurrent()
        def value = uriMappings.getPathProperty(path, UriMappings.PAGE_TITLE_PATH_PROPERTY) ?:
                super.getPageTitle(event) ?: config.getProperty(UriMappings.PAGE_TITLE_PATH_PROPERTY)
        value
    }

    @Override
    PushMode getPushMode(UICreateEvent event) {
        def path = pathHelper.getPathWithinApplication(event.request)
        def uriMappings = UriMappings.getCurrent()
        def config = VaadinConfig.getCurrent()
        def value = uriMappings.getPathProperty(path, UriMappings.PUSH_MODE_PATH_PROPERTY) ?:
                super.getPushMode(event) ?: config.getProperty(UriMappings.PUSH_MODE_PATH_PROPERTY)
        value
    }

    @Override
    Transport getPushTransport(UICreateEvent event) {
        def path = pathHelper.getPathWithinApplication(event.request)
        def uriMappings = UriMappings.getCurrent()
        def config = VaadinConfig.getCurrent()
        def value = uriMappings.getPathProperty(path, UriMappings.PUSH_TRANSPORT_PATH_PROPERTY) ?:
                super.getPushTransport(event) ?: config.getProperty(UriMappings.PUSH_TRANSPORT_PATH_PROPERTY)
        value
    }
}
