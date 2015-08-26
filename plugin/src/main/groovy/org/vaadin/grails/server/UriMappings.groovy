package org.vaadin.grails.server

import com.vaadin.navigator.View
import com.vaadin.ui.UI
import grails.util.Holders

abstract class UriMappings {

    public static final PRIMARY_PROPERTY = "primary"

    public static final DEFAULT_FRAGMENT_PATH_PROPERTY = "defaultFragment"
    public static final THEME_PATH_PROPERTY = "theme"
    public static final WIDGETSET_PATH_PROPERTY = "widgetset"
    public static final PRESERVED_ON_REFRESH_PATH_PROPERTY = "preservedOnRefresh"
    public static final PAGE_TITLE_PATH_PROPERTY = "pageTitle"
    public static final PUSH_MODE_PATH_PROPERTY = "pushMode"
    public static final PUSH_TRANSPORT_PATH_PROPERTY = "pushTransport"

    static UriMappings getCurrent() {
        Holders.applicationContext.getBean(UriMappings)
    }

    abstract Class<? extends UI> getUIClass(String path)
//    void setUIClass(String path, Class<? extends UI> uiClass)

    abstract Class<? extends View> getViewClass(String path, String fragment)
//    void setViewClass(String path, String fragment, Class<? extends View> viewClass)

    abstract List<String> getAllPaths()

    abstract Object getPathProperty(String path, String name)
    abstract Object putPathProperty(String path, String name, Object value)

    abstract List<String> getAllFragments(String path)

    abstract Object getFragmentProperty(String path, String fragment, String name)
    abstract Object putFragmentProperty(String path, String fragment, String name, Object value)

    abstract String getPrimaryPath(Class<? extends UI> uiClass)
    abstract void setPrimaryPath(Class<? extends UI> uiClass, String primaryPath)

    abstract String getPrimaryFragment(String path, Class<? extends View> viewClass)
    abstract void setPrimaryFragment(String path, Class<? extends View> viewClass, String primaryFragment)
}