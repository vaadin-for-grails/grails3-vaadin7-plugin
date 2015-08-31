package grails.plugins.vaadin.server

import com.vaadin.navigator.View
import com.vaadin.ui.UI
import grails.plugins.vaadin.config.VaadinConfig
import grails.util.Holders
import org.apache.log4j.Logger
import org.vaadin.grails.server.UriMappings

import javax.annotation.PostConstruct

class DefaultUriMappings extends UriMappings {

    protected abstract class AbstractMapping {

        final Map<String, Object> properties = new HashMap()
    }

    protected class FragmentMapping extends AbstractMapping {

        Class<? extends View> viewClass
    }

    protected class PathMapping extends AbstractMapping {

        Class<? extends UI> uiClass

        private final Map<String, FragmentMapping> mappingByFragment = new HashMap()
        private final Map<Class<? extends View>, String> primaryFragmentByViewClass = new IdentityHashMap()

        FragmentMapping getFragmentMapping(String fragment) {
            def mapping = mappingByFragment.get(fragment)
            if (mapping == null) {
                mapping = mappingByFragment.get(fragment)
                if (mapping == null) {
                    mapping = new FragmentMapping()
                    mappingByFragment.put(fragment, mapping)
                }
            }
            mapping
        }
    }

    private static final Logger log = Logger.getLogger(DefaultUriMappings)

    private final Map<String, PathMapping> mappingByPath = new HashMap()
    private final Map<Class<? extends UI>, String> primaryPathByUIClass = new IdentityHashMap()

    protected PathMapping getPathMapping(String path) {
        def mapping = mappingByPath.get(path)
        if (mapping == null) {
            mapping = mappingByPath.get(path)
            if (mapping == null) {
                mapping = new PathMapping()
                mappingByPath.put(path, mapping)
            }
        }
        mapping
    }

    @Override
    Class<? extends UI> getUIClass(String path) {
        getPathMapping(path).uiClass
    }

    protected void setUIClass(String path, Class<? extends UI> uiClass) {
        getPathMapping(path).uiClass = uiClass
    }

    @Override
    Class<? extends View> getViewClass(String path, String fragment) {
        def pathMapping = getPathMapping(path)
        pathMapping.getFragmentMapping(fragment).viewClass
    }

    protected void setViewClass(String path, String fragment, Class<? extends View> viewClass) {
        def pathMapping = getPathMapping(path)
        def fragmentMapping = pathMapping.getFragmentMapping(fragment)
        fragmentMapping.viewClass = viewClass
    }

    @Override
    List<String> getAllPaths() {
        def allPaths = mappingByPath.keySet()
        allPaths.asList()
    }

    @Override
    Object getPathProperty(String path, String name) {
        def pathMapping = getPathMapping(path)
        pathMapping.properties.get(name)
    }

    @Override
    Object putPathProperty(String path, String name, Object value) {
        def pathMapping = getPathMapping(path)
        pathMapping.properties.put(name, value)
    }

    @Override
    List<String> getAllFragments(String path) {
        def pathMapping = getPathMapping(path)
        def allFragments = pathMapping.mappingByFragment.keySet()
        allFragments.asList()
    }

    @Override
    Object getFragmentProperty(String path, String fragment, String name) {
        def pathMapping = getPathMapping(path)
        def fragmentMapping = pathMapping.getFragmentMapping(fragment)
        fragmentMapping.properties.get(name)
    }

    @Override
    Object putFragmentProperty(String path, String fragment, String name, Object value) {
        def pathMapping = getPathMapping(path)
        def fragmentMapping = pathMapping.getFragmentMapping(fragment)
        fragmentMapping.properties.put(name, value)
    }

    @Override
    String getPrimaryPath(Class<? extends UI> uiClass) {
        def primaryPath = primaryPathByUIClass.get(uiClass)
        if (primaryPath == null) {
            primaryPath = mappingByPath.findResult { path, mapping ->
                if (mapping.uiClass == uiClass) {
                    log.warn("No primary path set for UI [$uiClass], using [${path}]")
                    primaryPathByUIClass.put(uiClass, path)
                    return path
                }
                null
            }
        }
        primaryPath
    }

    @Override
    void setPrimaryPath(Class<? extends UI> uiClass, String primaryPath) {
        primaryPathByUIClass.put(uiClass, primaryPath)
    }

    @Override
    String getPrimaryFragment(String path, Class<? extends View> viewClass) {
        def pathMapping = getPathMapping(path)
        def primaryFragment = pathMapping.primaryFragmentByViewClass.get(viewClass)
        if (primaryFragment == null) {
            primaryFragment = pathMapping.mappingByFragment.findResult { fragment, mapping ->
                if (mapping.viewClass == viewClass) {
                    log.warn("No primary fragment set for View [$viewClass] and path [$path], using [${fragment}]")
                    pathMapping.primaryFragmentByViewClass.put(viewClass, fragment)
                    return fragment
                }
                null
            }
        }
        primaryFragment
    }

    @Override
    void setPrimaryFragment(String path, Class<? extends View> viewClass, String primaryFragment) {
        def pathMapping = getPathMapping(path)
        pathMapping.primaryFragmentByViewClass.put(viewClass, primaryFragment)
    }

    private boolean initialized

    @PostConstruct
    protected void init() {
        if (initialized) {
            throw new IllegalStateException("Already initialized")
        }
        log.debug("Loading URI mappings")
        def classLoader = Holders.grailsApplication.classLoader
        def config = VaadinConfig.getCurrent()
        Map mappings = config.getProperty('vaadin.mappings', Map, [:])
        mappings.each { String path, Map pathConfig ->

            Class uiClass
            def uiClassOrClassName = pathConfig.get('ui')
            if (uiClassOrClassName instanceof String) {
                uiClass = classLoader.loadClass(uiClassOrClassName)
            }

//            if (uiClass == null) {
//                throw new RuntimeException("No class found for ui [${uiClassOrClassName}]")
//            }

            putPathProperty(path, DEFAULT_FRAGMENT_PATH_PROPERTY, pathConfig.get(DEFAULT_FRAGMENT_PATH_PROPERTY) ?: "index")
            putPathProperty(path, THEME_PATH_PROPERTY, pathConfig.get(THEME_PATH_PROPERTY))
            putPathProperty(path, WIDGETSET_PATH_PROPERTY, pathConfig.get(WIDGETSET_PATH_PROPERTY))
            putPathProperty(path, PRESERVED_ON_REFRESH_PATH_PROPERTY, pathConfig.get(PRESERVED_ON_REFRESH_PATH_PROPERTY))
            putPathProperty(path, PAGE_TITLE_PATH_PROPERTY, pathConfig.get(PAGE_TITLE_PATH_PROPERTY))
            putPathProperty(path, PUSH_MODE_PATH_PROPERTY, pathConfig.get(PUSH_MODE_PATH_PROPERTY))
            putPathProperty(path, PUSH_TRANSPORT_PATH_PROPERTY, pathConfig.get(PUSH_TRANSPORT_PATH_PROPERTY))

            log.debug("Register UI class [$uiClass] for path [$path]")
            setUIClass(path, uiClass)

            if (pathConfig.get(PRIMARY_PROPERTY, false)) {
                setPrimaryPath(uiClass, path)
            }

            def fragments = pathConfig.get('fragments')
            fragments.each { String fragment, Map fragmentConfig ->

                Class viewClass
                def viewClassOrClassName = fragmentConfig.get('view')
                if (viewClassOrClassName instanceof String) {
                    viewClass = classLoader.loadClass(viewClassOrClassName)
                } else {
                    viewClass = (Class) viewClassOrClassName
                }

                if (viewClass == null) {
                    throw new RuntimeException("No class found for view [${viewClassOrClassName}]")
                }

                log.debug("Register View class [${viewClass.name}] for path [${path}] and fragment [${fragment}]")
                setViewClass(path, fragment, viewClass)

                if (fragmentConfig.get(PRIMARY_PROPERTY, false)) {
                    setPrimaryFragment(path, viewClass, fragment)
                }
            }
        }
        initialized = true
    }
}
