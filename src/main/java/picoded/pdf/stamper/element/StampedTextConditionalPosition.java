package picoded.pdf.stamper.element;

import java.util.Map;

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.FontFactory;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.ColumnText;

import picoded.core.struct.*;

public class StampedTextConditionalPosition extends StampedElement{
	private String fontAlias = "times-roman";
	private float textSize = 12;
	private int maxChars = -1;
	private float rot = 0;
	private GenericConvertMap<String, Object> options = null;
	
	public StampedTextConditionalPosition(String inKey, int inPage, Map<String, Object> inOptions){
		super("stampedtextconditionalposition", inKey, inPage, 0, 0);
		options = ProxyGenericConvertMap.ensure(inOptions);
		
		fontAlias = options.getString("fontalias", "times-roman");
		textSize = options.getFloat("textsize", 10f);
		maxChars = options.getInt("maxchars", -1);
		rot = options.getFloat("rot", 0);
	}
	
	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData){
		Object rawVal = templateData.get(key());
		// System.out.println("Retrieve val: " + rawVal + " for key: " + key());
		String val = String.valueOf(rawVal);
		if(val == null || val.isEmpty()){
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
			// throw new RuntimeException("No options configuration for KV pair: "+key()+","+val+" on page: " +page());
		}
		
		if(maxChars > -1 && val.length() > maxChars){
			val = val.substring(0, maxChars);
		}
		
		GenericConvertMap<String, Object> valueGCM = ProxyGenericConvertMap.ensure(valueConfig);
		xPos = valueGCM.getInt("x");
		yPos = valueGCM.getInt("y");
		
		Phrase iTextPhrase = new Phrase(val, FontFactory.getFont(fontAlias, "UTF-8", true, textSize));
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, iTextPhrase, xPos, yPos, rot);
	}
}