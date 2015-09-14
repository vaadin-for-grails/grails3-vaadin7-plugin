description("Creates a new Vaadin build script") {
    usage "grails create-vaadin-buildscript"
}

render  template: "vaadin.gradle.template",
        destination: file("vaadin.gradle"),
        overwrite: true

