package org.vaadin.grails.navigator

import com.vaadin.server.Page
import com.vaadin.server.VaadinService
import com.vaadin.ui.UI
import org.apache.commons.lang.StringUtils
import org.springframework.web.util.UrlPathHelper
import org.vaadin.grails.util.ApplicationContextUtils

import javax.servlet.http.HttpServletRequest

/**
 * Navigating between UIs and Views as defined in {@link UriMappings}.
 *
 * @see {@link UriMappings}
 * @author Stephan Grundner
 * @since 2.0
 */
class Navigation {

    /**
     * Get the current path.
     *
     * @return The current path
     */
    static String getCurrentPath() {
        def pathHelper = new UrlPathHelper()
        def currentRequest = VaadinService.currentRequest
        def path = pathHelper.getPathWithinApplication((HttpServletRequest) currentRequest)
        StringUtils.removeEnd(path, '/UIDL/')
    }

    /**
     * Get the current mapped fragment.
     *
     * @return The current mapped fragment
     */
    static String getCurrentFragment() {
        def uri = Page.current.location
        def fragmentAndParams = uri.fragment
        StringUtils.removeStart(fragmentAndParams, '!')
    }

    /**
     * Get the current parameters as a map.
     *
     * @return The current parameters as a map
     */
    static Map getCurrentParams() {
        uriMappings.lookupParams(currentPath, currentFragment)
    }

    /**
     * Navigate to a different UI or View.
     *
     * @see {@link com.vaadin.navigator.Navigator#navigateTo(java.lang.String)}
     *
     * @param path The path mapped to a {@link com.vaadin.ui.UI}
     * @param fragment The fragment mapped to a {@link com.vaadin.navigator.View}
     */
    static void navigateTo(String path, String fragment) {
        def currentPath = getCurrentPath()

        if (path == null) {
            path = currentPath
        }

        if (path?.equals(currentPath)) {
            UI.current.navigator.navigateTo(fragment)
        } else {
            Page.current.setLocation("$path/$fragment")
        }
    }

    /**
     * Navigate to a different UI or View using the named parameters.
     *
     * @see {@link #navigateTo(java.lang.String, java.lang.String)}
     */
    static void navigateTo(Map args) {
        def path = args.get('path') as String
        def fragment = args.get('fragment') as String
        navigateTo(path, fragment)
    }

    /**
     * Get the {@link UriMappings} instance.
     *
     * @return the {@link UriMappings} instance
     */
    static UriMappings getUriMappings() {
        ApplicationContextUtils.applicationContext.getBean(UriMappings)
    }
}
