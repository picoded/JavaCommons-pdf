package picoded.pdf.stamper.element;

import java.util.Map;

import com.lowagie.text.pdf.PdfContentByte;

import picoded.core.struct.*;

public class StampedRadioBox extends StampedElement{
	private int defaultWidth = 10;
	private int defaultHeight = 10;
	private String defaultShapeType = "rectangle";
	private java.awt.Color defaultFillColour = java.awt.Color.black;
	private java.awt.Color defaultStrokeColour = java.awt.Color.black;
	private GenericConvertMap<String, Object> templateDefinition = null;
	private GenericConvertMap<String, Object> options = null;
	
	public StampedRadioBox(String inKey, int inPage, Map<String, Object> inTemplateDefinition, Map<String, Object> inOptions){
		super("radio", inKey, inPage, 0, 0);
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
		Object rawVal = templateData.get(key());
		String val = String.valueOf(rawVal);
		if(val == null || val.isEmpty() || val.equalsIgnoreCase("false")){
			return;
		}
		
		Map<String, Object> valueConfig = null;
		for(String key : options.keySet()){
			if(key.equalsIgnoreCase(val)){
				valueConfig = options.getStringMap(key);
				break;
			}
		}
		
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