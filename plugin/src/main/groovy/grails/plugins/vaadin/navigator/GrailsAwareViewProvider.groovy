package grails.plugins.vaadin.navigator

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewProvider
import com.vaadin.server.VaadinSession
import org.apache.log4j.Logger
import org.vaadin.grails.navigator.Navigation
import org.vaadin.grails.server.UriMappings
import org.vaadin.grails.server.util.UriMappingUtils
import org.vaadin.grails.util.ApplicationContextUtils

class GrailsAwareViewProvider implements ViewProvider {

    private static final Logger log = Logger.getLogger(GrailsAwareViewProvider)

    @Override
    String getViewName(String fragmentAndParameters) {
        def path = Navigation.currentPath
        def viewName = UriMappingUtils.lookupFragment(path, fragmentAndParameters)

        viewName
    }

    @Override
    View getView(String fragment) {
        def path = Navigation.currentPath
        def uriMappings = UriMappings.getCurrent()

        if (fragment == "") {
            fragment = uriMappings.getPathProperty(path,
                    UriMappings.DEFAULT_FRAGMENT_PATH_PROPERTY)
        }

        def viewClass = uriMappings.getViewClass(path, fragment)
        if (viewClass) {
            log.debug("View class [${viewClass?.name}] found for path [${path}] and fragment [${fragment}]")
            def view = ApplicationContextUtils.getBeanOrInstance(viewClass)
            VaadinSession.current.setAttribute(View, view)
            return view
        }

        log.debug("No View class found for path [${path}] and fragment [${fragment}]")

        null
    }
}
