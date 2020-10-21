package picoded.pdf.stamper;

import java.io.*;
import java.util.*;

import picoded.core.conv.*;
import picoded.core.struct.*;
import picoded.core.file.*;
import picoded.pdf.stamper.element.*;

public class StampedPdfTemplate{
	List<IStampedElement> elements = null;
	int numberOfPages = 0;
	StampedElementFactory elementFactory = null;
	GenericConvertMap<String, Object> templateConfig = new GenericConvertHashMap<String,Object>();

	public StampedPdfTemplate(File inTemplateFile){
		elements = new ArrayList<IStampedElement>();
		elementFactory = new StampedElementFactory();
		readTemplateFile(inTemplateFile);
	}
	
	public StampedPdfTemplate(){
		elements = new ArrayList<IStampedElement>();
		elementFactory = new StampedElementFactory();
	}

	public StampedPdfTemplate(GenericConvertMap<String, Object> inConfig){
		elements = new ArrayList<IStampedElement>();
		elementFactory = new StampedElementFactory();
		templateConfig = inConfig;
	}
	
	public int numberOfPages(){ return numberOfPages; }
	
	public List<IStampedElement> getElementsForPage(int pageNum){
		if(pageToElementsMap.containsKey(pageNum)){
			return pageToElementsMap.get(pageNum);
		}
		
		return new ArrayList<IStampedElement>();
	}
	
	public void setFromRawInputMode(List<Object> inTemplateDefinition){
		for(Object rawTemplateObject : inTemplateDefinition){
			Map<String, Object> templateMap = GenericConvert.toStringMap(rawTemplateObject);
			IStampedElement element = elementFactory.createElement(templateMap,templateConfig);
			numberOfPages = Math.max(numberOfPages, element.page());
			elements.add(element);
		}
		
		generatePageToElementsMap();
	}
	
	public void setFromPagesMode(Map<String, Object> inPagesDefinition){
		
		for(String page : inPagesDefinition.keySet()){
			
			String location = (String)inPagesDefinition.get(page);
		
			String pageTemplateStringDefinition = readTemplatePageFile(location);
			
			List<Object> pageTemplateDefinition = GenericConvert.toGenericConvertList(pageTemplateStringDefinition);
			
			for(Object rawTemplateObject : pageTemplateDefinition){
				
				Map<String, Object> templateMap = GenericConvert.toStringMap(rawTemplateObject);
				IStampedElement element = elementFactory.createElement(templateMap, Integer.parseInt(page),templateConfig);
				numberOfPages = Math.max(numberOfPages, element.page());
				elements.add(element);
			}
		}
		
		generatePageToElementsMap();
	}
	
	private void readTemplateFile(File inTemplateFile){
		
		String templateString = FileUtil.readFileToString_withFallback(inTemplateFile, "UTF-8", "");
		if(templateString == null || templateString.isEmpty()){
			return;
		}
		
		Map<String, Object> jsonTemplateAsMap = ConvertJSON.toMap(templateString);
		GenericConvertMap<String, Object> jsonTemplateAsMapGCM = ProxyGenericConvertMap.ensure(jsonTemplateAsMap);
		String mode = jsonTemplateAsMapGCM.getString("mode", "rawinput");
		if(mode.equalsIgnoreCase("rawinput")){
			List<Object> templateDefinition = jsonTemplateAsMapGCM.getObjectList("template", null);
			setFromRawInputMode(templateDefinition);
		}else if(mode.equalsIgnoreCase("pages")){
			Map<String, Object> templatePagesMap = jsonTemplateAsMapGCM.getStringMap("template", null);
			setFromPagesMode(templatePagesMap);
		}else{
			throw new RuntimeException("Behaviour for mode: " + mode + " undefined.");
		}
	}
	
	private String readTemplatePageFile(String location){
		File templatePageFile = new File(location);
		if(!templatePageFile.exists()){
			throw new RuntimeException("Template page file does not exist");
		}
		
		return FileUtil.readFileToString_withFallback(templatePageFile, "UTF-8", "");
	}
	
	Map<Integer, List<IStampedElement>> pageToElementsMap = null;
	private void generatePageToElementsMap(){
		pageToElementsMap = new HashMap<Integer, List<IStampedElement>>();
		
		for(IStampedElement elm : elements){
			List<IStampedElement> elementList = new ArrayList<IStampedElement>();
			if(pageToElementsMap.containsKey(elm.page())){
				elementList = pageToElementsMap.get(elm.page());
			}
			
			elementList.add(elm);
			pageToElementsMap.put(elm.page(), elementList);
		}
	}
}