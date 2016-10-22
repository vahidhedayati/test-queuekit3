package testing

import grails.transaction.Transactional
import org.grails.plugin.queuekit.ReportsQueue
import org.grails.plugin.queuekit.priority.Priority
import org.springframework.web.servlet.support.RequestContextUtils

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
class TestExportController {

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
            String reportName = 'exportPluginExample'

            log.debug "Sending task as default priority to queueReportService instead of exportService.export"
            //def queue = queueReportService.buildReport(reportName,userId , locale, params,Priority.HIGH,ReportsQueue.PRIORITYBLOCKING)
            def queue = queueReportService.buildReport(reportName,userId , locale, params)

            flash.message = g.message(code: 'queuekit.reportQueued.label', args: [reportName, queue?.id])

            /**
             * How you would normally export using Export plugin
             * Changed to above to go through queuekit plugin and queue request instead
             *
             * Take a look at ExportPluginExampleReportingService to see how you can do the same
             *
             */
            // response.contentType = grailsApplication.config.grails.mime.types[format]
            // response.setHeader("Content-disposition", "attachment; filename=books.${params.extension}")
            // exportService.export(format, response.outputStream,TestExport.list(params), [:], [:])
        }

        respond TestExport.list(params), model:[testExportCount: TestExport.count()]
    }

    def show(TestExport testExport) {
        respond testExport
    }

    def create() {
        respond new TestExport(params)
    }

    @Transactional
    def save(TestExport testExport) {
        if (testExport == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (testExport.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond testExport.errors, view:'create'
            return
        }

        testExport.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'testExport.label', default: 'TestExport'), testExport.id])
                redirect testExport
            }
            '*' { respond testExport, [status: CREATED] }
        }
    }

    def edit(TestExport testExport) {
        respond testExport
    }

    @Transactional
    def update(TestExport testExport) {
        if (testExport == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (testExport.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond testExport.errors, view:'edit'
            return
        }

        testExport.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'testExport.label', default: 'TestExport'), testExport.id])
                redirect testExport
            }
            '*'{ respond testExport, [status: OK] }
        }
    }

    @Transactional
    def delete(TestExport testExport) {

        if (testExport == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        testExport.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'testExport.label', default: 'TestExport'), testExport.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'testExport.label', default: 'TestExport'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
