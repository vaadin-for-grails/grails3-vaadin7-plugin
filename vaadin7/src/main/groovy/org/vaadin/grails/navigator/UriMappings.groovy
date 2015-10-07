package org.vaadin.grails.navigator

import com.vaadin.navigator.View
import com.vaadin.ui.UI

/**
 *
 * @author Stephan Grundner
 * @since 3.0
 */
interface UriMappings {

    static final THEME_PROPERTY = "theme"
    static final WIDGETSET_PROPERTY = "widgetset"
    static final PRESERVED_ON_REFRESH_PROPERTY = "preservedOnRefresh"
    static final PAGE_TITLE_PROPERTY = "pageTitle"
    static final PUSH_MODE_PROPERTY = "pushMode"
    static final PUSH_TRANSPORT_PROPERTY = "pushTransport"

    Class<? extends UI> lookupUIClass(String path)
    void putUIClass(String pathPattern, Class<? extends UI> uiClass)

    Class<? extends View> lookupViewClass(String path, String fragment)
    void putViewClass(String pathPattern, String fragmentPattern, Class<? extends View> viewClass)

    Object lookupProperty(String path, String key)
    void putProperty(String pathPattern, String key, Object value)

    Object lookupProperty(String path, String fragment, String key)
    void putProperty(String pathPattern, String fragmentPattern, String key, Object value)

    boolean containsFragmentMapping(String path, String fragment)
    boolean containsAnyFragmentMapping(String path)
    boolean containsPathMapping(String path)

    Set<String> getAllPathPatterns()
    Set<String> getAllFragmentPatterns(String pathPattern)

    boolean isPattern(String pathOrFragment)

    String lookupPattern(String path)
    String lookupPattern(String path, String fragment)

    Map<String, String> lookupParams(String path, String fragment)
}