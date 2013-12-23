package com.neteast.cloud.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

public class XmlUtils {
	
	private static String filename = "src/exam.xml";
	
	public static Document getDocument(){
		Document document = DocumentHelper.createDocument();
	    return document;  //创建文档   
	}
	
	public static void write2Xml(Document document) throws IOException{
		
		 Writer fileWriter=new FileWriter(filename);    
         XMLWriter xmlWriter=new XMLWriter(fileWriter);    
         xmlWriter.write(document);   //写入文件中 
         xmlWriter.close();
	}
}
