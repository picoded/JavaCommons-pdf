package picoded.pdf.stamper.element;

import java.util.Map;

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.FontFactory;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.ColumnText;

import picoded.core.struct.*;

public class StampedTextArea extends StampedElement{
	String mode = "boundingbox";
	public String fontAlias = "times-roman";
	public float textSize = 10;
	public int maxCharsPerLine = 100;
	public int maxLines = 10;
	public int lineHeight = 10;
	public float boxWidth = 100;
	public float boxHeight = 100;
	private boolean debug = false;
	
	// TODO:: Specify max chars?
	
	public StampedTextArea(String inKey, int inPage, float inXPos, float inYPos, Map<String, Object> inTemplateDefinition){
		super("text", inKey, inPage, inXPos, inYPos);
		
		GenericConvertMap<String, Object> templateDefinition = ProxyGenericConvertMap.ensure(inTemplateDefinition);
		
		fontAlias = templateDefinition.getString("fontalias", "");
		textSize = templateDefinition.getFloat("textsize", 10f);
		maxCharsPerLine = templateDefinition.getInt("maxcharsperline", 100);
		maxLines = templateDefinition.getInt("maxlines", 10);
		lineHeight = templateDefinition.getInt("lineheight", 10);
		boxWidth = templateDefinition.getFloat("boxwidth", 10);
		boxHeight = templateDefinition.getFloat("boxheight", 10);
		debug = templateDefinition.getBoolean("debug", false);
	}
	
	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> inTemplateData){
		if(!inTemplateData.containsKey(key())){
			return;
		}
		
		String val = "";
		try{
			val = String.valueOf(inTemplateData.get(key()));
		}catch(Exception ex){
			System.out.println("Exception converting val for key: " + key() + " with raw val: " + inTemplateData.get(key()));
			throw new RuntimeException(ex);
		}
		
		
		if(mode.equalsIgnoreCase("boundingbox")){
			stampByBoundingBox(canvas, val);
		}else if(mode.equalsIgnoreCase("manual")){
			stampByManualDefinition(canvas, val);
		}else{
			throw new RuntimeException("No behaviour defined for mode: " + mode);
		}
	}
	
	private void stampByBoundingBox(PdfContentByte canvas, String val){
		try{
			ColumnText ct = new ColumnText(canvas);
			Phrase iTextPhrase = new Phrase(val, FontFactory.getFont(fontAlias, "UTF-8", true, textSize));
			float topRightX = xPos() + boxWidth;
			float topRightY = yPos() + boxHeight;
			ct.setSimpleColumn(iTextPhrase, xPos(), yPos(), topRightX, topRightY, lineHeight, Element.ALIGN_LEFT);
			ct.go();
			
			if(debug){
				canvas.rectangle(xPos(), yPos(), boxWidth, boxHeight);
				canvas.stroke();
			}
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	private void stampByManualDefinition(PdfContentByte canvas, String val){
		throw new RuntimeException("Not yet implemented");
	}
	
	public float textSize(){ return textSize; }
}