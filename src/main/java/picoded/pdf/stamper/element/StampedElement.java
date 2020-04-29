package picoded.pdf.stamper.element;

import java.util.Map;

import com.lowagie.text.pdf.PdfContentByte;

public abstract class StampedElement implements IStampedElement{
	public String type = "";
	public String key = "";
	public int page = 0;
	public float xPos = 0f;
	public float yPos = 0f;
	
	public StampedElement(String inType, String inKey, int inPage, float inXPos, float inYPos){
		this(inType, inKey, inPage);
		xPos = inXPos;
		yPos = inYPos;
	}
	
	public StampedElement(String inType, String inKey, int inPage){
		type = inType;
		key = inKey;
		page = inPage;
	}
	
	public abstract void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData);
	
	public String type(){ return type; }
	public String key(){ return key; }
	public int page(){ return page; }
	public float xPos(){ return xPos; }
	public float yPos(){ return yPos; }
}