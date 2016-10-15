package test.queuekit3

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration {
    Closure doWithSpring() {
        { ->
            queuekitUserService(test.MyUserService)
        }
    }
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}