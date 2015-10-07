package grails.plugins.vaadin.navigator

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewProvider
import org.apache.log4j.Logger
import org.vaadin.grails.navigator.Navigation
import org.vaadin.grails.util.ApplicationContextUtils

/**
 * Grails specific implementation for {@link ViewProvider}.
 *
 * @author Stephan Grundner
 * @since 1.0
 */
class GrailsAwareViewProvider implements ViewProvider {

    private static final Logger log = Logger.getLogger(GrailsAwareViewProvider)

    @Override
    String getViewName(String fragmentAndParams) {
        def path = Navigation.currentPath
        def uriMappings = Navigation.uriMappings
        if (uriMappings.containsFragmentMapping(path, fragmentAndParams)) {
            return fragmentAndParams
        }

        log.debug("No fragment found for uri [$path#!$fragmentAndParams]")

        null
    }

    @Override
    View getView(String fragment) {
        def path = Navigation.currentPath
        def uriMappings = Navigation.uriMappings
        def viewClass = uriMappings.lookupViewClass(path, fragment)
        if (viewClass) {
            log.debug("Creating view for class [${viewClass?.name}] matching path [${path}] and fragment [${fragment}]")
            return ApplicationContextUtils.getBeanOrInstance(viewClass)
        }

        log.debug("No View class found for path [${path}] and fragment [${fragment}]")

        null
    }
}
