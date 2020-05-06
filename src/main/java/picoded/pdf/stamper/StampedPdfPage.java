package picoded.pdf.stamper;

import java.util.*;

import com.lowagie.text.pdf.*;

import picoded.pdf.stamper.element.*;

public class StampedPdfPage{
	PdfContentByte canvas = null;
	List<IStampedElement> elements= null;
	Map<String, Object> templateData = null;
	public StampedPdfPage(PdfContentByte inCanvas, List<IStampedElement> inElements, Map<String, Object> inTemplateData){
		canvas = inCanvas;
		elements = inElements;
		templateData = inTemplateData;
	}
	
	public void stampPage() throws Exception{	
		for(IStampedElement element : elements){
			canvas.setColorFill(java.awt.Color.black);
			canvas.setColorStroke(java.awt.Color.black);
			element.stampOnCanvas(canvas, templateData);
		}
	}
}