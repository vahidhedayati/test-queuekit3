package testing

import grails.plugin.queuekit.examples.Report3Bean

import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.CellRangeAddress
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.grails.plugin.queuekit.examples.Report3Bean
import org.springframework.web.servlet.support.RequestContextUtils

class ApachePoiXlsController {
	
	def queueReportService

	//used to show how it would be typically	
	def tsvService
	
	private String VIEW='/testReport/index'
	
	def index(Report3Bean bean) {
		render view:VIEW, model:[bean:bean]
	}

	def index2(Report3Bean bean) {
		def locale = RequestContextUtils.getLocale(request)
		def userId = 1L

		// You will need to create a new service called:
		// ---> XlsExample3ReportingService
		// This needs to follow the example XlsExample3ReportingService.groovy provided
		// If you called this xlsExample55 then create XlsExample55ReportingService and follow
		// example
		String reportName = 'xlsExample3'
		def queue = queueReportService.buildReport(reportName,userId , locale, bean.loadValues())
		
		flash.message = g.message(code: 'queuekit.reportQueued.label', args: [reportName, queue?.id])
		render view:VIEW, model:[bean:bean]
	}
	
	
	/*
	 -------------------------------------------------------------------------------------------------------
	 	In the service you will simply copy the method that generates XLS for you as it would currently for
	 	your controller view.
	 	that is done by actionReport3Report(out,bean,queryResults) in your new generated service			
	  	
	 -------------------------------------------------------------------------------------------------------
	 */
	
	/**
	 * 
	 * @return
	 */
	def index2Before(Report3Bean bean) {
		if (bean.hasErrors()) {
			render view:VIEW,model:[bean:bean]
			return
		}
		def queryResults = tsvService.runReport3(bean)
		if (!queryResults) {
			flash.message = message(code: 'queuekit.noreport.label', default: 'No results found')
			render view:VIEW,model:[bean:bean]
			return
		}
		// This generateXLS would be what it's content would replace actionReport3Report(out,bean,queryResults)
		// accept you have to also add out to the new call you create and remove out from your below method. 
		// out will then be provided by the plugin
		//generateXls(bean, queryResults)
	}
	
	private def generateXls(Report3Bean bean, queryResults) {
		String filename = 'Report3Example.xls'
		HSSFWorkbook wb = new HSSFWorkbook()
		HSSFSheet sheet = wb.createSheet()
		wb.setMissingCellPolicy(HSSFRow.RETURN_NULL_AND_BLANK)
		def heading=headerList(bean)
		int counter=0
		int firstRowCounter
		HSSFCellStyle standard = wb.createCellStyle()
		standard.setVerticalAlignment(CellStyle.VERTICAL_TOP)
		HSSFCellStyle headingStyle = wb.createCellStyle()
		headingStyle.cloneStyleFrom(standard)
		Font headerFont = wb.createFont()
		headingStyle.setAlignment(CellStyle.ALIGN_CENTER)
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD)
		headingStyle.setFont(headerFont)
		HSSFCellStyle  number = wb.createCellStyle()
		number.cloneStyleFrom(standard)
		number.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"))
		heading.eachWithIndex{element, i ->
			HSSFRow row = sheet.createRow(i)
			row.createCell(0).setCellValue("${element}")
			counter=i
		}
		counter=counter+2
		firstRowCounter=counter+1
		HSSFRow row=sheet.createRow(counter)
		def firstRow=headingRowList
		firstRow?.eachWithIndex { cell, i ->
			Cell cell1 = row.createCell(i)
			cell1.setCellValue("${cell}")
			cell1.setCellStyle(headingStyle)
		}
		queryResults.each{ instance ->
			counter++
			row=sheet.createRow(counter)
			def rowItem = excelRow(instance)
			rowItem?.eachWithIndex {qr,i ->
				Cell cell = row.createCell(i)
				cell.setCellValue(qr)
				cell.setCellStyle(standard)
			}
		}

		try {
			
			// When copying your method over to your new Service
			// as already mentioed out is already provided by plugin 
			// the below 4 lines should not be provided in the new service call
			// everything else is identical
			response.setContentType("application/ms-excel")
			response.setHeader("Expires:", "0") // eliminates browser caching
			response.setHeader("Content-Disposition", "attachment; filename=$filename")
			OutputStream out = response.outputStream
			// End of no longer required - when converted to plugin service method 
			
			wb.write(out)
			out.close()
		} catch (Exception e) {
		}
	
	}
	/**
	 * used by EXCEL report to print top heading
	 * @param bean
	 * @return a list of headings
	 */
	private List headerList(Report3Bean bean) {
		def out=[]
		out << g.message(code:'report2Example.label', default: 'Testing report 2')
		out << 'Report Date: '+g.formatDate(date:new Date(), format:'dd-MMM-yyyy HH:mm:ss')
		out << 'reportName: '+(bean.report)
		out << 'Sample Text: '+(bean.sample)
		out << 'Country selected: '+(bean.countrySelected)
		return out
	}

	/**
	 * used by EXCEL report top  row i.e. field headings
	 * @return as list
	 */
	private List getHeadingRowList() {
		def out =[]
		out << 'id'
		out << 'text'
		return out
	}

	/**
	 * iterate through each instanceList returned from queryResults.
	 * @param instance
	 * @return
	 */
	private List excelRow(instance) {
		def out = []
		out << instance.id
		out << instance.text
		return out
	}

}
