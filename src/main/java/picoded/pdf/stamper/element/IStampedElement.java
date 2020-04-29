package picoded.pdf.stamper.element;

import java.util.Map;

import com.lowagie.text.pdf.PdfContentByte;

public interface IStampedElement{
	public String type();
	public String key();
	public int page();
	public float xPos();
	public float yPos();
	
	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData);
}