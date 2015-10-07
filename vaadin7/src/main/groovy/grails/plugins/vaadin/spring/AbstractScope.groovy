package grails.plugins.vaadin.spring

import org.springframework.beans.BeansException
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.Scope

import java.util.concurrent.ConcurrentHashMap

/**
 * Vaadin aware spring scope.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
abstract class AbstractScope implements Scope, BeanFactoryPostProcessor {

    static class BeanMap implements Map<String, Object> {

        private final Map<String, Object> delegate
        protected final Map<String, Runnable> destructionCallbackMap

        BeanMap(Map<String, Object> delegate) {
            this.delegate = delegate
            destructionCallbackMap = new HashMap<>()
        }

        BeanMap() {
            this(new ConcurrentHashMap<String, Object>())
        }

        final void registerDestructionCallback(String name, Runnable callback) {
            destructionCallbackMap.put(name, callback)
        }

        @Override
        int size() {
            delegate.size()
        }

        @Override
        boolean isEmpty() {
            delegate.isEmpty()
        }

        @Override
        boolean containsKey(Object key) {
            delegate.containsKey(key)
        }

        @Override
        boolean containsValue(Object value) {
            delegate.containsValue(value)
        }

        protected Object create(ObjectFactory<?> objectFactory) {
            objectFactory.getObject()
        }

        Object get(Object key, ObjectFactory<?> objectFactory) {
            def bean = get(key)
            if (bean == null) {
                bean = create(objectFactory)
                put(key, bean)
            }
            bean
        }

        @Override
        Object get(Object key) {
            delegate.get(key)
        }

        @Override
        Object put(String key, Object value) {
            delegate.put(key, value)
        }

        @Override
        Object remove(Object key) {
            delegate.remove(key)
            destructionCallbackMap.remove(key)
        }

        @Override
        void putAll(Map<? extends String, ?> m) {
            throw new UnsupportedOperationException()
        }

        @Override
        void clear() {
            destructionCallbackMap.values().each { runnable ->
                runnable.run()
            }
            destructionCallbackMap.clear()
            delegate.clear()
        }

        @Override
        Set<String> keySet() {
            delegate.keySet()
        }

        @Override
        Collection<Object> values() {
            delegate.values()
        }

        @Override
        Set<Map.Entry<String, Object>> entrySet() {
            delegate.entrySet()
        }
    }

    private final String scopeName

    AbstractScope(String scopeName) {
        this.scopeName = scopeName
    }

    abstract BeanMap getBeanMap()

    @Override
    Object get(String name, ObjectFactory<?> objectFactory) {
        beanMap.get(name, objectFactory)
    }

    @Override
    Object remove(String name) {
        beanMap.remove(name)
    }

    @Override
    void registerDestructionCallback(String name, Runnable callback) {
        beanMap.registerDestructionCallback(name, callback)
    }

    @Override
    Object resolveContextualObject(String key) { null }

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope(scopeName, this)
    }
}
