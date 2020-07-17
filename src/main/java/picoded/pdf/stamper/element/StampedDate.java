package picoded.pdf.stamper.element;

import java.util.Map;

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.FontFactory;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.ColumnText;

import picoded.core.conv.GenericConvert;
import picoded.core.struct.*;
import picoded.pdf.stamper.CustomDateConv;
import picoded.pdf.stamper.CustomDateConv.ISODateFormat;


public class StampedDate extends StampedElement{
	public String fontAlias = "times-roman";
	public float textSize = 10;
	public int maxChars = -1;
	private float rot = 0;
	
	private String format = "ddmmyyyy";
	private String separator = "";
	private boolean includeTime = false;
	private String middleText = "";
	private boolean formatMonthAsMMM = false;
	
	public StampedDate(String inKey, int inPage, float inXPos, float inYPos, Map<String, Object> inTemplateDefinition){
		super("date", inKey, inPage, inXPos, inYPos);
		
		GenericConvertMap<String, Object> templateDefinition = ProxyGenericConvertMap.ensure(inTemplateDefinition);
		fontAlias = templateDefinition.getString("fontalias", "times-roman");
		textSize = templateDefinition.getFloat("textsize", 10);
		maxChars = templateDefinition.getInt("maxchars", -1);
		rot = templateDefinition.getFloat("rot", 0);
		
		format = templateDefinition.getString("format", "ddmmyyyy");
		separator = templateDefinition.getString("separator", "");
		includeTime = templateDefinition.getBoolean("includeTime", false);
		middleText = templateDefinition.getString("middleText", "");
		formatMonthAsMMM = templateDefinition.getBoolean("formatMonthAsMMM", false);
	}
	
	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData){
		if(!templateData.containsKey(key())){
			return;
		}
		
		String dateFormatted;
		
		if(templateData.get(key()).toString().equalsIgnoreCase("")){
			dateFormatted = "";
		}else{
			Long dateLong = GenericConvert.toLong(templateData.get(key()));
			ISODateFormat dateFormat = CustomDateConv.toISODateFormat(format());
			dateFormatted = CustomDateConv.toISOFormat(dateLong, dateFormat, separator(), includeTime, middleText, formatMonthAsMMM);
		}		
		Phrase iTextPhrase = new Phrase(dateFormatted, FontFactory.getFont(fontAlias, "UTF-8", true, textSize));
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, iTextPhrase, xPos(), yPos(), rot());
	}
	
	public String fontAlias(){ return fontAlias; }
	public float rot(){ return rot; }
	public float textSize(){ return textSize; }
	public int maxChars(){ return maxChars; }
	public String format(){ return format; }
	public String separator(){ return separator; }
}