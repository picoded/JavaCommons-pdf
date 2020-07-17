package picoded.pdf.stamper.element;

import java.util.Map;

//import com.lowagie.text.Element;
//import com.lowagie.text.Rectangle;

import com.lowagie.text.pdf.PdfContentByte;

import picoded.core.struct.*;

public class StampedCheckBoxArray extends StampedElement{
	private int defaultWidth = 10;
	private int defaultHeight = 10;
	private String defaultShapeType = "rectangle";
	private java.awt.Color defaultFillColour = java.awt.Color.black;
	private java.awt.Color defaultStrokeColour = java.awt.Color.black;
	private GenericConvertMap<String, Object> templateDefinition = null;
	private GenericConvertMap<String, Object> options = null;
	
	public StampedCheckBoxArray(String inKey, int inPage, Map<String, Object> inTemplateDefinition, Map<String, Object> inOptions){
		super("checkboxarray", inKey, inPage, 0, 0);
		templateDefinition = ProxyGenericConvertMap.ensure(inTemplateDefinition);
		defaultWidth = templateDefinition.getInt("width", 10);
		defaultHeight = templateDefinition.getInt("height", 10);
		defaultShapeType = templateDefinition.getString("shapetype", "rectangle");
		
		String fill = templateDefinition.getString("colour", "");
		if(!fill.isEmpty()){
			String[] fillRGB = fill.split(",");
			defaultFillColour = new java.awt.Color(Integer.parseInt(fillRGB[0]), Integer.parseInt(fillRGB[1]), Integer.parseInt(fillRGB[2]));
		}
		
		String stroke = templateDefinition.getString("strokecolour", "");
		if(!stroke.isEmpty()){
			String[] strokeRGB = stroke.split(",");
			defaultStrokeColour = new java.awt.Color(Integer.parseInt(strokeRGB[0]), Integer.parseInt(strokeRGB[1]), Integer.parseInt(strokeRGB[2]));
		}else{
			defaultStrokeColour = defaultFillColour;
		}
		
		options = ProxyGenericConvertMap.ensure(inOptions);
	}

	public Map<String, Object> getOptions() {
		return options;
	}
	
	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData){
		GenericConvertMap<String, Object> templateGCM = ProxyGenericConvertMap.ensure(templateData);
		
		String[] valArr = templateGCM.getStringArray(key(), null);
		if(valArr == null || valArr.length == 0){
			return;
		}
		
		for(String val : valArr){
			Map<String, Object> valueConfig = options.getStringMap(val, null);
			if(valueConfig == null){
				return;
			}
			
			GenericConvertMap<String, Object> valueGCM = ProxyGenericConvertMap.ensure(valueConfig);
			xPos = valueGCM.getInt("x");
			yPos = valueGCM.getInt("y");
			int width = valueGCM.getInt("width", defaultWidth);
			int height = valueGCM.getInt("height", defaultHeight);
			String shapeType = valueGCM.getString("shapetype", defaultShapeType);
			
			String colour = valueGCM.getString("colour", "");
			java.awt.Color fillColour = defaultFillColour;
			if(!colour.isEmpty()){
				String[] colourRGB = colour.split(",");
				fillColour = new java.awt.Color(Integer.parseInt(colourRGB[0]), Integer.parseInt(colourRGB[1]), Integer.parseInt(colourRGB[2]));
			}
			
			String stroke = valueGCM.getString("strokecolour", "");
			java.awt.Color strokeColour = colour.isEmpty() ? defaultStrokeColour : fillColour;
			if(!stroke.isEmpty()){
				String[] strokeRGB = stroke.split(",");
				strokeColour = new java.awt.Color(Integer.parseInt(strokeRGB[0]), Integer.parseInt(strokeRGB[1]), Integer.parseInt(strokeRGB[2]));
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
}