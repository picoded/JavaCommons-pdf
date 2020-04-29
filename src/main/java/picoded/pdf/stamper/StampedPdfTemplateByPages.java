package picoded.pdf.stamper;

import java.io.File;
import java.util.List;
import java.util.Map;

import picoded.core.conv.GenericConvert;
import picoded.core.file.FileUtil;
import picoded.core.struct.GenericConvertMap;
import picoded.core.struct.ProxyGenericConvertMap;

// this is a shortcut for implementation for application that uses 
// elements.json > {$n}.json  templates organization pattern
public class StampedPdfTemplateByPages extends StampedPdfTemplate {
	
		StampedPdfTemplate templateByPages = null;
		String templatePathTo = null;
		  
		public StampedPdfTemplateByPages (File templatePageConfig)  {
			 templateByPages = getPdfTemplate(templatePageConfig);
		}
		
		public StampedPdfTemplate getPdfTemplate  (File templatePageConfig) {
			
			//get elements.json path
			String nJsonPath = templatePageConfig.getParent();
			templatePathTo = nJsonPath;
			
			// Read the elements json file
			String templateJsonString = FileUtil.readFileToString_withFallback(templatePageConfig, "UTF-8", "");
			GenericConvertMap<String, Object> templateGCM = ProxyGenericConvertMap.ensure(GenericConvert.toStringMap(templateJsonString));
			String mode = templateGCM.getString("mode", "pages");
			
			StampedPdfTemplate stampedPdfTemplate = new StampedPdfTemplate();
			if(mode.equalsIgnoreCase("pages")){
				Map<String, Object> templatePagesDefinition = templateGCM.getStringMap("template", null);
				for(String key : templatePagesDefinition.keySet()){
					String val = (String)templatePagesDefinition.get(key);
					val = nJsonPath +"/"+ val;
					templatePagesDefinition.put(key, val);
				}
				stampedPdfTemplate.setFromPagesMode(templatePagesDefinition);
			}else if(mode.equalsIgnoreCase("rawinput")){
				List<Object> templateRawInputDefinition = templateGCM.getObjectList("template", null);
				stampedPdfTemplate.setFromRawInputMode(templateRawInputDefinition);
			}else{
				throw new RuntimeException("Unknown mode: " + mode);
			}
			return stampedPdfTemplate;
		}
		public StampedPdfTemplate getTemplateByPages () { return templateByPages ;}

}
