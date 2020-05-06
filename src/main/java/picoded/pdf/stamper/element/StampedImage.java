package picoded.pdf.stamper.element;

import java.util.Map;

//import com.lowagie.text.Element;
import com.lowagie.text.Image;

import com.lowagie.text.pdf.PdfContentByte;

public class StampedImage extends StampedElement{
	String imageSrc = "";
	public StampedImage(String inKey, int inPage, float inXPos, float inYPos, String inImageSrc){
		super("text", inKey, inPage, inXPos, inYPos);
		
		imageSrc = inImageSrc;
	}
	
	boolean setCustomWidth = false;
	float width = 0;
	public void setWidth(float inWidth){
		setCustomWidth = true;
		width = inWidth;
	}
	
	boolean setCustomHeight = false;
	float height = 0;
	public void setHeight(float inHeight){
		setCustomHeight = true;
		height = inHeight;
	}
	
	private Image getImage() throws Exception {
		if(imageSrc.isEmpty()){
			throw new RuntimeException("No image source specified for StampedImage with key: " + key());
		}
		
		Image img = Image.getInstance(imageSrc);
		
		img.setAbsolutePosition(xPos(), yPos());
		if(setCustomWidth){
			img.scaleAbsoluteWidth(width);
		}
		
		if(setCustomHeight){
			img.scaleAbsoluteHeight(height);
		}
		
		return img;
	}
	
	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData){
		try{
			canvas.addImage(getImage());
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
}