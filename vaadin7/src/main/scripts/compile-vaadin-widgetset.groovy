def widgetset = params.first()

ant.java(classname: "com.google.gwt.dev.Compiler",
        maxmemory: "512m",
        failonerror: true,
        fork: true,
        classpathref: "grails.compile.classpath"
) {
    ant.classpath {
        pathelement location: "${basedir}/src/java"
        pathelement location: "${basedir}/target/classes"
    }
    arg(value: "-logLevel")
    arg(value: "INFO")
    arg(value: "-localWorkers")
    arg(value: "3")
    arg(value: "-war")
    arg(value: "web-app/VAADIN/widgetsets")
    arg(value: widgetset)
    jvmarg(value: "-Xss1024k")
    jvmarg(value: "-Djava.awt.headless=true")
}