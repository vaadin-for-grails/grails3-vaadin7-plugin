package grails.plugins.vaadin.navigator

import com.vaadin.navigator.View
import com.vaadin.ui.UI
import grails.plugins.vaadin.config.VaadinConfig
import grails.util.Holders
import org.apache.log4j.Logger
import org.springframework.util.AntPathMatcher
import org.vaadin.grails.navigator.UriMappings

import javax.annotation.PostConstruct

/**
 * Default implementation for {@link UriMappings}.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
class DefaultUriMappings implements UriMappings {

    static abstract class AbstractMapping {

        final Properties properties = new Properties()

        String pattern

        void mergeWith(AbstractMapping mapping) {
            mapping.properties.each { key, value ->
                if (!properties.containsKey(key)) {
                    properties.put(key, value)
                }
            }
        }
    }

    static class FragmentMapping extends AbstractMapping {

        Class<? extends View> viewClass

        @Override
        void mergeWith(AbstractMapping mapping) {
            super.mergeWith(mapping)
            if (mapping instanceof FragmentMapping) {
                if (viewClass == null) {
                    viewClass = mapping.viewClass
                }
            }
        }

        @Override
        String toString() {
            "{pattern: $pattern, " +
                    "view: $viewClass, " +
                    "properties: ${properties}"
        }
    }

    static class PathMapping extends AbstractMapping {

        Class<? extends UI> uiClass
        final Map<String, FragmentMapping> fragmentMappingByPattern = new HashMap<>()

        @Override
        void mergeWith(AbstractMapping mapping) {
            super.mergeWith(mapping)
            if (mapping instanceof PathMapping) {
                if (uiClass == null) {
                    uiClass = mapping.uiClass
                }
                fragmentMappingByPattern.putAll(mapping.fragmentMappingByPattern)
            }
        }

        @Override
        String toString() {
            "{pattern: $pattern, " +
                    "ui: $uiClass, " +
                    "properties: ${properties}, " +
                    "fragments: ${fragmentMappingByPattern.keySet()}"
        }
    }

    private static final Logger log = Logger.getLogger(DefaultUriMappings)

    static String ensurePathOnly(String value) {
        value = value.trim()

        if (!value.startsWith('/')) {
            throw new IllegalArgumentException("path must start with '/'")
        }

        def i = value.indexOf('#!')
        if (i != -1) {
            return value.substring(0, i)
        }
        value
    }

    static String ensureFragmentOnly(String value) {
        def i = value.indexOf('#!')
        if (i == -1) {
            return value
        }
        value.substring(i + 2, value.length())
    }

    static boolean hasFragment(String uriOrPattern) {
        uriOrPattern.indexOf('#!') > -1
    }

    final Map<String, PathMapping> pathMappingByPattern = new HashMap<>()

    final def matcher = new AntPathMatcher()

    private PathMapping findOrCreatePathMapping(String pathPattern) {
        pathPattern = ensurePathOnly(pathPattern)
        def pathMapping = pathMappingByPattern.get(pathPattern)
        if (pathMapping == null) {
            pathMapping = new PathMapping()
            pathMapping.pattern = pathPattern
            pathMappingByPattern.put(pathPattern, pathMapping)
        }
        pathMapping
    }

    private FragmentMapping findOrCreateFragmentMapping(String pathPattern, String fragmentPattern) {
        fragmentPattern = ensureFragmentOnly(fragmentPattern)
        def pathMapping = findOrCreatePathMapping(pathPattern)
        def fragmentMapping = pathMapping.fragmentMappingByPattern.get(fragmentPattern)
        if (fragmentMapping == null) {
            fragmentMapping = new FragmentMapping()
            fragmentMapping.pattern = fragmentPattern
            pathMapping.fragmentMappingByPattern.put(fragmentPattern, fragmentMapping)
        }
        fragmentMapping
    }

    private List<PathMapping> findAllMatchingPathMappings(String path) {
        path = ensurePathOnly(path)
        def mappings = pathMappingByPattern.values().findAll { mapping ->
            matcher.match(mapping.pattern, path)
        }

        def patternComparator = matcher.getPatternComparator(path)
        return mappings.toSorted(new Comparator<PathMapping>() {
            @Override
            int compare(PathMapping mapping1, PathMapping mapping2) {
                patternComparator.compare(mapping1?.pattern, mapping2?.pattern)
            }
        })
    }

    private PathMapping lookupPathMapping(String path) {
//        assert !matcher.isPattern(path), "path must not be a pattern!"
        def allMatchingPathMappings = findAllMatchingPathMappings(path)
        if (allMatchingPathMappings.isEmpty()) {
            return null
        }
        def merged = new PathMapping()
        def i = allMatchingPathMappings.iterator()
        if (i.hasNext()) {
            def first = i.next()
            merged.mergeWith(first)
            merged.pattern = first.pattern

            while (i.hasNext()) {
                merged.mergeWith(i.next())
            }
        }
        merged
    }

    private List<FragmentMapping> findAllMatchingFragmentMappings(String path, String fragment) {
        def pathMapping = lookupPathMapping(path)
        if (pathMapping == null) {
            return Collections.EMPTY_LIST
        }
        def mappings = pathMapping.fragmentMappingByPattern.values().findAll { mapping ->
            matcher.match(mapping.pattern, fragment)
        }

        def patternComparator = matcher.getPatternComparator(fragment)
        return mappings.toSorted(new Comparator<FragmentMapping>() {
            @Override
            int compare(FragmentMapping mapping1, FragmentMapping mapping2) {
                patternComparator.compare(mapping1?.pattern, mapping2?.pattern)
            }
        })
    }

    private FragmentMapping lookupFragmentMapping(String path, String fragment) {
//        assert !matcher.isPattern(fragment), "fragment must not be a pattern!"
        def allMatchingFragmentMappings = findAllMatchingFragmentMappings(path, fragment)
        if (allMatchingFragmentMappings.isEmpty()) {
            return null
        }
        def merged = new FragmentMapping()
        def i = allMatchingFragmentMappings.iterator()
        if (i.hasNext()) {
            def first = i.next()
            merged.mergeWith(first)
            merged.pattern = first.pattern

            while (i.hasNext()) {
                merged.mergeWith(i.next())
            }
        }

        merged
    }

    @Override
    Class<? extends UI> lookupUIClass(String path) {
        lookupPathMapping(path)?.uiClass
    }

    @Override
    void putUIClass(String pathPattern, Class<? extends UI> uiClass) {
        pathPattern = ensurePathOnly(pathPattern)
        findOrCreatePathMapping(pathPattern).uiClass = uiClass
    }

    @Override
    Class<? extends View> lookupViewClass(String path, String fragment) {
        lookupFragmentMapping(path, fragment)?.viewClass
    }

    @Override
    void putViewClass(String pathPattern, String fragmentPattern, Class<? extends View> viewClass) {
        pathPattern = ensurePathOnly(pathPattern)
        fragmentPattern = ensureFragmentOnly(fragmentPattern)
        findOrCreateFragmentMapping(pathPattern, fragmentPattern).viewClass = viewClass
    }

    @Override
    Object lookupProperty(String path, String key) {
        path = ensurePathOnly(path)
        lookupPathMapping(path)?.properties?.get(key)
    }

    @Override
    void putProperty(String pathPattern, String key, Object value) {
        pathPattern = ensurePathOnly(pathPattern)
        findOrCreatePathMapping(pathPattern).properties.put(key, value)
        log.debug("Applied property [$key: $value] to pattern [$pathPattern]")
    }

    @Override
    Object lookupProperty(String path, String fragment, String key) {
        path = ensurePathOnly(path)
        fragment = ensureFragmentOnly(fragment)
        lookupFragmentMapping(path, fragment)?.properties?.get(key)
    }

    private static boolean isPathRelatedProperty(String key) {
        switch (key) {
            case THEME_PROPERTY:
            case WIDGETSET_PROPERTY:
            case PRESERVED_ON_REFRESH_PROPERTY:
            case PAGE_TITLE_PROPERTY:
            case PUSH_MODE_PROPERTY:
            case PUSH_TRANSPORT_PROPERTY:
                return true
        }
        false
    }

    @Override
    void putProperty(String pathPattern, String fragmentPattern, String key, Object value) {
        if (isPathRelatedProperty(key)) {
            log.warn("Applying property [$key] to fragment mapping [$fragmentPattern] has no effect")
        }
        pathPattern = ensurePathOnly(pathPattern)
        fragmentPattern = ensureFragmentOnly(fragmentPattern)
        findOrCreateFragmentMapping(pathPattern, fragmentPattern).properties.put(key, value)
        log.debug("Applied property [$key: $value] to pattern [$pathPattern#!$fragmentPattern]")
    }

    @Override
    boolean containsFragmentMapping(String path, String fragment) {
        lookupFragmentMapping(path, fragment) != null
    }

    @Override
    boolean containsAnyFragmentMapping(String path) {
        def mapping = lookupPathMapping(path)
        if (mapping) {
            return !mapping.fragmentMappingByPattern.isEmpty()
        }
        false
    }

    @Override
    boolean containsPathMapping(String path) {
        lookupPathMapping(path) != null
    }

    @Override
    Set<String> getAllPathPatterns() {
        pathMappingByPattern.keySet()
    }

    @Override
    Set<String> getAllFragmentPatterns(String pathPattern) {
        def pathMapping = pathMappingByPattern.get(pathPattern)
        if (pathMapping) {
            return pathMapping.fragmentMappingByPattern.keySet()
        }
        Collections.EMPTY_SET
    }

    @Override
    boolean isPattern(String pathOrFragment) {
        matcher.isPattern(pathOrFragment)
    }

    @Override
    String lookupPattern(String path) {
        lookupPathMapping(path)?.pattern
    }

    @Override
    String lookupPattern(String path, String fragment) {
        lookupFragmentMapping(path, fragment)?.pattern
    }

    @Override
    Map<String, String> lookupParams(String path, String fragment) {
        def mapping = lookupFragmentMapping(path, fragment)
        if (mapping) {
            return matcher.extractUriTemplateVariables(mapping.pattern, fragment)
        }
        null
    }

    private static Class<?> asClass(Object classOrClassName) {
        if (classOrClassName == null) {
            return null
        }
        if (classOrClassName instanceof Class) {
            return classOrClassName
        }
        Holders.grailsApplication
                .classLoader
                .loadClass(classOrClassName.toString())
    }

    @PostConstruct
    void init() {
        def config = VaadinConfig.getCurrent()
        config.mappings.each { Map<String, Object> configEntry ->
            configEntry = new HashMap<>(configEntry)
            String uriPattern = configEntry.remove('uri')

            def pathPattern = ensurePathOnly(uriPattern)
            def uiClass = asClass(configEntry.remove('ui')) as Class<? extends UI>
            if (uiClass) {
                putUIClass(pathPattern, uiClass)
            }

            if (!hasFragment(uriPattern)) {
                configEntry.each { key, value ->
                    putProperty(pathPattern, key, value)
                }
                return
            }

            def fragmentPattern = ensureFragmentOnly(uriPattern)
            def viewClass = asClass(configEntry.remove('view')) as Class<? extends View>
            if (viewClass) {
                putViewClass(pathPattern, fragmentPattern, viewClass)
            }

            configEntry.each { key, value ->
                putProperty(pathPattern, fragmentPattern, key, value)
            }
        }
    }
}
