package test


import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.util.CellRangeAddress
import grails.web.databinding.DataBindingUtils
import org.grails.plugin.queuekit.ReportsQueue
import org.grails.plugin.queuekit.examples.Report3Bean
import org.grails.plugin.queuekit.reports.QueuekitBaseReportsService

class XlsExample3ReportingService extends QueuekitBaseReportsService {
	def tsvService

	String getReportExension() {
		return 'xls'
	}

	def runReport(ReportsQueue queue, Map params) {
		// This is the service bound to index2 action of Report2Controller
		// The action is bound to report2Bean which is now bound back to params received
		// through the running job
		Report3Bean bean = new Report3Bean()

		DataBindingUtils.bindObjectToInstance(bean, params)

		//tsvService would generate an instanceList for runReport
		def queryResults = tsvService.runReport3(bean)
		
		//This is within the main ReportsService class and calls back to actionInternal
		runReport(queue,queryResults,bean)
	}

	def actionInternal(out,bean, queryResults,Locale locale) {
		actionReport3Report(out,bean,queryResults)
	}

	//It goes through a variety of in/out of ReportService finally it has real results
	// here and the real bean which it displays results for
	private void actionReport3Report(out,Report3Bean bean,queryResults) {
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
	//	out << g.message(code:'report2Example.label', default: 'Testing report 2')
//		out << 'Report Date: '+g.formatDate(date:new Date(), format:'dd-MMM-yyyy HH:mm:ss')
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
