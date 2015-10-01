import org.apache.commons.lang.StringUtils

/**
 * http://grails.github.io/grails-doc/3.0.x/api/org/grails/cli/profile/commands/script/GroovyScriptCommand.html
 */

description("Creates useful Vaadin defaults") {
    usage "grails vaadin-quickstart"
    argument name: 'packageName', description: "The name of the package", required: false
    flag name: 'force', description: "Whether to overwrite existing files"
}

def packageName = args[0]

def overwrite = flag('force') ? true : false

render  template: "themes/grails/styles.scss.template",
        destination: file("src/main/webapp/VAADIN/themes/grails/styles.scss"),
        overwrite: overwrite

render  template: "vaadin.yml.template",
        destination: file("grails-app/conf/vaadin.yml"),
        model: [packageName: packageName],
        overwrite: overwrite

def viewGroovyFile = "IndexView.groovy"

if (StringUtils.isNotEmpty(packageName)) {
    def packagePath = StringUtils.replace(packageName, ".", "/")
    viewGroovyFile = packagePath + "/" + viewGroovyFile
}

render  template: "View.groovy.template",
        destination: file("src/main/groovy/$viewGroovyFile"),
        model: [packageName: packageName],
        overwrite: overwrite

render  template: "vaadin.gradle.template",
        destination: file("vaadin.gradle"),
        overwrite: true

def buildScriptFile = file("build.gradle") as File
def applyFromFound = buildScriptFile.text.find(/apply\sfrom[:]\s[']vaadin\.gradle[']/)
if (!applyFromFound) {
    def writer = buildScriptFile.newWriter(true)
    try {
        writer.append("\napply from: 'vaadin.gradle'\n")
    } finally {
        writer.close()
    }
}