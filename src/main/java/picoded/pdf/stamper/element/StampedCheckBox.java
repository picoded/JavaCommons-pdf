package picoded.pdf.stamper.element;

import java.util.Map;

//import com.lowagie.text.Element;
//import com.lowagie.text.Rectangle;

import com.lowagie.text.pdf.PdfContentByte;

import picoded.core.struct.*; 
import picoded.core.conv.*; 

public class StampedCheckBox extends StampedElement{
	private int width = 10;
	private int height = 10;
	private String shapeType = "rectangle";
	private java.awt.Color fillColour = java.awt.Color.BLACK;
	private java.awt.Color strokeColour = java.awt.Color.BLACK;
	
	public StampedCheckBox(String inKey, int inPage, float inXPos, float inYPos, Map<String, Object> inTemplateDefinition){
		super("checkbox", inKey, inPage, inXPos, inYPos);
		
		GenericConvertMap<String, Object> templateDefinition = ProxyGenericConvertMap.ensure(inTemplateDefinition);
		
		width = templateDefinition.getInt("width", 10);
		height = templateDefinition.getInt("height", 10);
		shapeType = templateDefinition.getString("shapetype", "rectangle");
		
		String fill = templateDefinition.getString("colour", "");
		if(!fill.isEmpty()){
			String[] fillRGB = fill.split(",");
			fillColour = new java.awt.Color(Integer.parseInt(fillRGB[0]), Integer.parseInt(fillRGB[1]), Integer.parseInt(fillRGB[2]));
		}
		
		String stroke = templateDefinition.getString("strokecolour", "");
		if(!stroke.isEmpty()){
			String[] strokeRGB = stroke.split(",");
			strokeColour = new java.awt.Color(Integer.parseInt(strokeRGB[0]), Integer.parseInt(strokeRGB[1]), Integer.parseInt(strokeRGB[2]));
		}else{
			strokeColour = fillColour;
		}
	}
	
	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData){
		if(!templateData.containsKey(key())){
			return;
		}
		
		boolean val = GenericConvert.toBoolean(templateData.get(key()));
		if(!val){
			return;
		}
		
		canvas.setColorStroke(strokeColour);
		canvas.setColorFill(fillColour);
		
		if(shapeType.equalsIgnoreCase("rectangle")){
			canvas.rectangle(xPos, yPos, width, height);
		}else if(shapeType.equalsIgnoreCase("circle")){
			canvas.circle(xPos, yPos, width);
		}else{
			throw new RuntimeException("Invalid shape type found: " + shapeType);
		}
		
		canvas.fillStroke();
	}
}