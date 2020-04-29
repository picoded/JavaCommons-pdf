package picodedTest.pdf.stamper;

//import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.junit.Test;

import picoded.core.conv.GenericConvert;
import picoded.core.file.*;
import picoded.pdf.stamper.*;

public class _TestRunner {
	private File getFile(String fileName){
		return new File("./test-files/test-specific/pdfstamper/unit_test_1/" + fileName);
	}
	@Test
	public void test2() throws Exception {
		
		File inputPdf = getFile("test.pdf");
		
		//getting the template config
		StampedPdfTemplateByPages newTemplate = new StampedPdfTemplateByPages(getFile("elements.json"));
		StampedPdfTemplate template = newTemplate.getTemplateByPages();
		
		//getting the case specific data
		String dataJsonString = FileUtil.readFileToString_withFallback(getFile("inputformdata.json"),"UTF-8","");
		Map<String, Object> templateData = GenericConvert.toStringMap(dataJsonString);
		
		//output file spec
		File outputFile = new File("./test-files/test-specific/pdfstamper/unit_test_1/result" , "test-1.pdf");
		
		//perform stamping
		try{
			StampedPdfBuilder builder = new StampedPdfBuilder(inputPdf, new FileOutputStream(outputFile), template, templateData);
			builder.stampPdf();
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
		
	}
	
}
