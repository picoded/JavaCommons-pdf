package picoded.pdf.stamper.element;

import java.util.*;

//import picoded.core.conv.*;
import picoded.core.struct.*;

public class StampedElementFactory{
	public IStampedElement createElement(Map<String, Object> inTemplateData){
		return createElement(inTemplateData, -1);
	}

	// TODO:: Do some refactoring or how the params get passed down to the element
	public IStampedElement createElement(Map<String, Object> inTemplateData, int inPage){
		GenericConvertMap<String, Object> template = ProxyGenericConvertMap.ensure(inTemplateData);

		String type = template.getString("type", "");
		String key = template.getString("key", "");
		int page = template.getInt("page", inPage);
		float xPos = template.getFloat("x", 0f);
		float yPos = template.getFloat("y", 0f);
		float rot = template.getFloat("rot", 0f);
		String capitalize = template.getString("capitalize", "");

		if(type.equalsIgnoreCase("text")){
			String fontAlias = template.getString("fontalias", "");
			float textSize = template.getFloat("textsize", 10f);
			int maxChars = template.getInt("maxchars", -1);
			return new StampedText(key, page, xPos, yPos, rot, textSize, maxChars, fontAlias, capitalize);
		}else if (type.equalsIgnoreCase("image")){
			String imageSrc = template.getString("src", "");
			StampedImage si = new StampedImage(key, page, xPos, yPos, imageSrc);
			if(template.containsKey("width")){
				si.setWidth(template.getFloat("width", 0));
			}
			if(template.containsKey("height")){
				si.setHeight(template.getFloat("height", 0));
			}

			return si;
		}else if(type.equalsIgnoreCase("checkbox")){
			return new StampedCheckBox(key, page, xPos, yPos, template);
		}else if(type.equalsIgnoreCase("radio")){
			Map<String, Object> options = template.getStringMap("options", new HashMap<String, Object>());
			return new StampedRadioBox(key, page, template, options);
		}else if(type.equalsIgnoreCase("checkboxarray")){
			Map<String, Object> options = template.getStringMap("options", new HashMap<String, Object>());
			return new StampedCheckBoxArray(key, page, template, options);
		}else if(type.equalsIgnoreCase("textarea")){
			return new StampedTextArea(key, page, xPos, yPos, template);
		}else if(type.equalsIgnoreCase("signature")){
			return new StampedSignature(key, page, xPos, yPos, template);
		}else if(type.equalsIgnoreCase("date")){
			return new StampedDate(key, page, xPos, yPos, template);
		}else if(type.equalsIgnoreCase("stampedtextconditionalposition")){
			Map<String, Object> options = template.getStringMap("options", new HashMap<String, Object>());
			return new StampedTextConditionalPosition(key, page, options);
		}

		throw new RuntimeException("No builder found for element type: " +type);
	}
}