package demo

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.VerticalLayout

class IndexView extends CustomComponent implements View {

    IndexView() {
        def root = new VerticalLayout()

        // Add your code here

        root.setMargin(true)
        compositionRoot = root
    }

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) { }
}
