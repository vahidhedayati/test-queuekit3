package testing

import org.springframework.web.servlet.support.RequestContextUtils

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class TestAttributesController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    //def exportService
    def myUserService
    def queueReportService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        String format=params.f  ?: params.extensions ?: 'html'
        if(format && format != "html"){
            def locale = RequestContextUtils.getLocale(request)
            def userId = myUserService.currentuser
            String reportName = 'exportPluginAdvanced'
            /**
             * ! -- IMPORTANT
             * In order to let this dynamic exportPluginAdvancedReportingService pickup the correct domainClass
             * We must send an additional params as part of reports calls and bind in the actual domainClass we would be listing
             * just like shown here
             *
             */
            params.domainClass=TestAttributes.class

            log.debug "Sending task as default priority to queueReportService instead of exportService.export"
            //def queue = queueReportService.buildReport(reportName,userId , locale, params,Priority.HIGH,ReportsQueue.PRIORITYBLOCKING)
            def queue = queueReportService.buildReport(reportName,userId , locale, params)

            flash.message = g.message(code: 'queuekit.reportQueued.label', args: [reportName, queue?.id])

            /**
             * How you would normally export using Export plugin
             * Changed to above to go through queuekit plugin and queue request instead
             *
             * Take a look at ExportPluginAdvancedReportingService to see how you can do the same
             *
             */
            // response.contentType = grailsApplication.config.grails.mime.types[format]
            // response.setHeader("Content-disposition", "attachment; filename=books.${params.extension}")
            // exportService.export(format, response.outputStream,TestExport.list(params), [:], [:])
        }
        respond TestAttributes.list(params), model:[testAttributesCount: TestAttributes.count()]
    }

    def show(TestAttributes testAttributes) {
        respond testAttributes
    }

    def create() {
        respond new TestAttributes(params)
    }

    @Transactional
    def save(TestAttributes testAttributes) {
        if (testAttributes == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (testAttributes.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond testAttributes.errors, view:'create'
            return
        }

        testAttributes.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'testAttributes.label', default: 'TestAttributes'), testAttributes.id])
                redirect testAttributes
            }
            '*' { respond testAttributes, [status: CREATED] }
        }
    }

    def edit(TestAttributes testAttributes) {
        respond testAttributes
    }

    @Transactional
    def update(TestAttributes testAttributes) {
        if (testAttributes == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (testAttributes.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond testAttributes.errors, view:'edit'
            return
        }

        testAttributes.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'testAttributes.label', default: 'TestAttributes'), testAttributes.id])
                redirect testAttributes
            }
            '*'{ respond testAttributes, [status: OK] }
        }
    }

    @Transactional
    def delete(TestAttributes testAttributes) {

        if (testAttributes == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        testAttributes.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'testAttributes.label', default: 'TestAttributes'), testAttributes.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'testAttributes.label', default: 'TestAttributes'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
