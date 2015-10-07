package grails.plugins.vaadin.spring.beans

import com.vaadin.ui.UI
import org.springframework.beans.factory.FactoryBean
import org.vaadin.grails.ui.DefaultUI

class UIClassFactory implements FactoryBean<UI> {

    @Override
    final UI getObject() throws Exception {
        DefaultUI.newInstance()
    }

    @Override
    Class<? extends UI> getObjectType() {
        DefaultUI
    }

    @Override
    final boolean isSingleton() {
        false
    }
}
