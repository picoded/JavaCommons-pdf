package picoded.pdf.stamper.element;

import java.util.Map;
import java.io.*;
/*import java.lang.Class;

import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.xml.bind.DatatypeConverter;
import javax.imageio.ImageIO;

import org.w3c.dom.Document;*/
import org.w3c.dom.svg.SVGDocument;

//import org.apache.batik.bridge.*;
import org.apache.batik.dom.svg.*;
//import org.apache.batik.gvt.*;
import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.*;
//import org.apache.batik.util.*;

//import com.lowagie.text.Element;
import com.lowagie.text.Image;
//import com.lowagie.text.ImgTemplate;
//import com.lowagie.text.pdf.PdfGraphics2D;
//import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfContentByte;

import picoded.core.struct.*;
import picoded.core.conv.GenericConvert;
//import picoded.core.image.PaddedResizedImage;

public class StampedSignature extends StampedElement{
	float height = 0;
	float width = 0;
	float svgheight = 0;
	float svgwidth = 0;
	boolean debug = false;
	// this flag will cause the signature to get stamped twice - this helps to alleviate the wiry and thin signature look after resizing
	// will be true by default
	boolean thickenSignature = true;
	
	public StampedSignature(String inKey, int inPage, float inXPos, float inYPos, Map<String, Object> inTemplateDefinition){
		super("signature", inKey, inPage, inXPos, inYPos);
		
		GenericConvertMap<String, Object> templateDefinition = ProxyGenericConvertMap.ensure(inTemplateDefinition);
		height = templateDefinition.getFloat("height", 0);
		width = templateDefinition.getFloat("width", 0);
		svgheight = templateDefinition.getFloat("svgheight", 300);
		svgwidth = templateDefinition.getFloat("svgwidth", 1000);
		debug = templateDefinition.getBoolean("debug", false);
		thickenSignature = templateDefinition.getBoolean("thickenSignature", true);
	}
	
	private byte[] SVGDocumentToPNG(SVGDocument inSVGDoc) {
		byte[] res = null;
		try {
			TranscoderInput input_svg_image = new TranscoderInput(inSVGDoc);
			
			//Step-2: Define OutputStream to PNG Image and attach to TranscoderOutput
			ByteArrayOutputStream png_ostream = new ByteArrayOutputStream();
			TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
			
			// Step-3: Create PNGTranscoder and define hints if required
			PNGTranscoder my_converter = new PNGTranscoder();
			my_converter.addTranscodingHint(PNGTranscoder.KEY_WIDTH, svgwidth);
			my_converter.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, svgheight);
			
			// Step-4: Convert and Write output
			my_converter.transcode(input_svg_image, output_png_image);
			
			// Step 5- close / flush Output Stream
			png_ostream.flush();
			png_ostream.close();
			
			res = png_ostream.toByteArray();
		} catch (Exception e) {
			System.out.println("SignatureLog -> Exception while transcoding svg to png");
			throw new RuntimeException(e);
		}
		
		return res;
	}
	
	public void stampOnCanvas(PdfContentByte canvas, Map<String, Object> templateData){
		if(!templateData.containsKey(key())){
			return;
		}
		
		String val = GenericConvert.toString(templateData.get(key()));
		if(val.isEmpty()){
			return;
		}
		
		try{
			SVGDocument svgDoc = getSVGDocument(val);
			byte[] pngBytes = SVGDocumentToPNG(svgDoc);
			Image img = Image.getInstance(pngBytes);
			img.scaleToFit(width, height);
			img.setAbsolutePosition(xPos(), yPos());
			canvas.addImage(img);
			
			if(thickenSignature){
				// thicken signature by just stamping it again
				canvas.addImage(img);
			}
			
			if(debug){
				canvas.rectangle(xPos(), yPos(), img.getWidth(), img.getHeight());
				canvas.stroke();
			}
		}catch(Exception ex){
			System.out.println("Error with key: " + key() + " and val: " + val);
			throw new RuntimeException(ex);
		}
	}
	
	private SVGDocument getSVGDocument(String svgURI) throws Exception {
		/*try {*/
			SAXSVGDocumentFactory svgDomFactory = new SAXSVGDocumentFactory(null);	
			SVGDocument svgDoc = svgDomFactory.createSVGDocument(svgURI);

			float originalSVGWidth = Float.parseFloat(svgDoc.getDocumentElement().getAttribute("width").replaceAll("[^0-9.,]",""));
			float originalSVGHeight = Float.parseFloat(svgDoc.getDocumentElement().getAttribute("height").replaceAll("[^0-9.,]",""));
			
			svgDoc.getDocumentElement().setAttribute("viewBox", "0 0 "+originalSVGWidth+" "+originalSVGHeight);
			svgDoc.getDocumentElement().setAttribute("width", "" + width);
			svgDoc.getDocumentElement().setAttribute("height", "" + height);
			
			return svgDoc;
		/*} catch (Exception ex) {
			System.out.println(ex);
		}
		return new SAXSVGDocumentFactory(null).createSVGDocument(null);*/
	}
	
	// private byte[] padOrResize(byte[] imageBytes) throws Exception {
	// 	if(!resize){
	// 		return imageBytes;
	// 	}
		
	// 	BufferedImage buf = ImageIO.read(new ByteArrayInputStream(imageBytes));
	// 	PaddedResizedImage prImage = new PaddedResizedImage(buf, null);
	// 	BufferedImage newImage = prImage.resize((int)width, (int)height);
	// 	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// 	ImageIO.write(newImage, "png", baos);
	// 	return baos.toByteArray();
	// }
	
	/*
	This function was modified from https://stackoverflow.com/questions/408042/vector-graphics-in-itext-pdf - Nick Russlers answer
	The original idea was the convert the svgURI to a svgdocument and stamp the svg directly to pdf
	However I realised that I couldn't change the svg position no matter what i did in the .addTemplate function
	So my solution was to convert svgURI to svgDocument, resize that SVG, then convert to PNG
	I leave this function here as a reference and warning to future attempts to visit this again
	*/
	// private void stampSVGImage(String svgURI, PdfContentByte canvas) throws Exception {
	// 	SVGDocument svgDoc = getSVGDocument(svgURI);
	// 	PdfTemplate svgTempl = canvas.createTemplate(templateWidth, templateHeight);
	// 	Graphics2D g2d = canvas.createGraphicsShapes(svgTempl.getWidth(), svgTempl.getHeight());
	// 	GraphicsNode gfxNode = (new GVTBuilder()).build(new BridgeContext(new UserAgentAdapter()), svgDoc);
	// 	gfxNode.paint(g2d);
	// 	g2d.dispose();
	// 	canvas.addTemplate(svgTempl, xPos(), yPos());
	// }
}