package picoded.pdf.stamper;

import java.io.*;
import java.util.*;

import com.lowagie.text.Phrase;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.*;

// import picoded.pdfstamper.element.*;

public class StampedPdfBuilder{
	// File readPdf = null;
	// OutputStream os = null;
	// StampedPdfTemplate pdfTemplate = null;
	// Map<String, Object> templateData = null;
	
	// public StampedPdfBuilder(File inputPdf, OutputStream inOutputStream, StampedPdfTemplate inStampedPdfTemplate, Map<String, Object> inData){
	// 	readPdf = inputPdf;
	// 	os = inOutputStream;
	// 	pdfTemplate = inStampedPdfTemplate;
	// 	templateData = inData;
	// }
	
	// public void registerFont(File inFontTTFile, String inFontAlias){
	// 	FontFactory.register(inFontTTFile.getAbsolutePath(), inFontAlias);
	// }
	
	// public void stampPdf(){
	// 	try{
	// 		if(templateData == null){
	// 			throw new RuntimeException("templateData is null");
	// 		}
			
	// 		PdfReader pdfReader = new PdfReader(readPdf.getAbsolutePath());
			
	// 		// This function is because for some reason itext cannot open pdfs not created by us
	// 		// Returns this error : com.lowagie.text.exceptions.BadPasswordException: No message found for pdfreader.not.opened.with.owner.password
	// 		// https://stackoverflow.com/questions/36176286/itext-read-pdfs-created-with-an-unknown-random-owner-password/36176562#36176562
	// 		pdfReader = unlockPdf(pdfReader);
			
	// 		// can change the constructor to take in an output stream
	// 		PdfStamper pdfStamper = new PdfStamper(pdfReader, os);
			
	// 		int totalPages = pdfTemplate.numberOfPages();
	// 		for(int i = 0; i < totalPages; ++i){
	// 			List<IStampedElement> pageElements = pdfTemplate.getElementsForPage(i + 1);
	// 			// getOverContent parameter is page number, 1-indexed
	// 			PdfContentByte canvas = pdfStamper.getOverContent(i + 1);
				
	// 			StampedPdfPage stampedPage = new StampedPdfPage(canvas, pageElements, templateData);
	// 			stampedPage.stampPage();
	// 		}
			
	// 		pdfStamper.close();
	// 		pdfReader.close();
			
	// 	}catch(Exception ex){
	// 		throw new RuntimeException(ex);
	// 	}
	// }
	
	// private PdfReader unlockPdf(PdfReader reader) {
	// 	if (reader == null) {
	// 		return reader;
	// 	}
	// 	try {
	// 		java.lang.reflect.Field f = reader.getClass().getDeclaredField("encrypted");
	// 		f.setAccessible(true);
	// 		f.set(reader, false);
	// 	} catch (Exception e) { /* ignore */ }
	// 	return reader;
	// }
}
