package picoded.pdf.stamper.element;

import java.util.Map;
import java.awt.Color;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.ColumnText;

import picoded.core.struct.*;

public class StampedCurrency extends StampedElement {

	public String fontAlias = "times-roman";
	public float textSize = 10;
	private float rot = 0;

	private Color negativeValueColour = Color.RED;
	private String currencySymbol = "S$";
	private Integer precision = 0;

	public StampedCurrency(String inKey, int inPage, float inXPos, float inYPos, Map<String, Object> inTemplateDefinition, float inTextSize, String inFontAlias){
		super("currency", inKey, inPage, inXPos, inYPos);

		GenericConvertMap<String, Object> templateDefinition = ProxyGenericConvertMap.ensure(inTemplateDefinition);

		fontAlias = inFontAlias;
		textSize = inTextSize;
		rot = templateDefinition.getFloat("rot", 0);

		String negativeColourOverride = templateDefinition.getString("colour", "");
		if(!negativeColourOverride.isEmpty()){
			String[] negColourOverrideRGB = negativeColourOverride.split(",");
			negativeValueColour = new java.awt.Color(Integer.parseInt(negColourOverrideRGB[0]), Integer.parseInt(negColourOverrideRGB[1]), Integer.parseInt(negColourOverrideRGB[2]));
		}
		currencySymbol = templateDefinition.getString("currencySymbol", "S$");
		precision = templateDefinition.getInt("precision", 0);
	}

	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData){
		if(!templateData.containsKey(key())){
			return;
		}

		String stampedStr;
		Font font;

		// checking for empty string
		// if this check is not included, it will stamp '0' for empty string values
		String strVal = picoded.core.conv.GenericConvert.toString(templateData.get(key()));
		if(strVal != null && strVal.isEmpty()){
			stampedStr = "";
			font = FontFactory.getFont(fontAlias, "UTF-8", true, textSize, java.awt.Font.PLAIN, Color.BLACK);
		}else{
			int val = picoded.core.conv.GenericConvert.toInt(templateData.get(key()));

			font = val < 0 ?
				FontFactory.getFont(fontAlias, "UTF-8", true, textSize, java.awt.Font.PLAIN, negativeValueColour)
				: FontFactory.getFont(fontAlias, "UTF-8", true, textSize, java.awt.Font.PLAIN, Color.BLACK);

			// format currency
			NumberFormat formattedCurrency = NumberFormat.getCurrencyInstance();
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setCurrencySymbol(currencySymbol);
			dfs.setGroupingSeparator(',');
			formattedCurrency.setMaximumFractionDigits(precision);
			((DecimalFormat) formattedCurrency).setDecimalFormatSymbols(dfs);
			stampedStr = formattedCurrency.format(val);
		}

		Phrase iTextPhrase = new Phrase(stampedStr, font);
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, iTextPhrase, xPos(), yPos(), rot());
	}

	public float rot(){ return rot; }
	public float textSize(){ return textSize; }
}
