# Vaadin 7 plugin for Grails 3.x
***Build Vaadin Applications on top of Grails in record time!***

The Vaadin 7 plugin integrates the [Vaadin Framework](https://vaadin.com/home) into Grails in an easy way.

## Quickstart
Add the repo to your `build.gradle` file.

```gradle
maven { url "https://dl.bintray.com/vaadin-for-grails/plugins" }
```

Then, add the plugin dependency to your build script.

```gradle
compile 'org.grails.plugins:vaadin7:3.+'
```

Finally execute the grails command `grails vaadin-quickstart com.yourcompany` and run your app.
