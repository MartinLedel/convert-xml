package com.personal.project.converter.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;
import java.io.*;

public class ToXML {

	BufferedReader in;
	StreamResult out;
	TransformerHandler th;
	AttributesImpl atts;
	Boolean isPActive;
	Boolean isFActive;

	public static void main(String args[]) {
        try {                    	
        	new ToXML().doit(
        			args.length < 1 ? "file" : args[0],
					args.length < 2 ? "converted" : args[1]
        					);       
        } catch(Exception e) {
            e.printStackTrace();
        }
	}

	public void doit(String input, String output) {
		try {
			in = new BufferedReader(new FileReader("data/" + input + ".txt"));
			out = new StreamResult("data/" + output + ".xml");
			initXML();
			String str;
			isPActive = false;
			isFActive = false;
			while ((str = in.readLine()) != null) {
				processLine(str);
			}
			th.endElement("", "", "person");
			in.close();
			closeXML();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initXML() throws ParserConfigurationException,
			TransformerConfigurationException, SAXException {
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();

		th = tf.newTransformerHandler();
		Transformer serializer = th.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		serializer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		th.setResult(out);
		th.startDocument();
		atts = new AttributesImpl();
		th.startElement("", "", "people", atts);
	}

	public void processLine(String s) throws SAXException {
		String[] elements = s.split("\\|");
		String startingLetter = elements[0];
		
		switch(startingLetter) {
		  case "P":
			  processP(elements);
			  break;
		  case "T":
			  processT(elements);
			  break;
		  case "A":
			  processA(elements);
			  break;
		  case "F":
			  processF(elements);
			  break;
		  default:
			  System.out.println("Missing starting letter used for file formatting");
			  break;
		}
	}
	
	public void processP(String[] elements) throws SAXException {
		if (isPActive && isFActive) {			
			th.endElement("", "", "family");
			th.endElement("", "", "person");
			isFActive = false;
		} else if (isPActive) {
			th.endElement("", "", "person");
		} else if (isFActive) {
			th.endElement("", "", "family");
			isFActive = false;
		}
		atts.clear();
		th.startElement("", "", "person", atts);
		th.startElement("", "", "firtname", atts);
		th.characters(elements[1].toCharArray(), 0, elements[1].length());
		th.endElement("", "", "firtname");
		th.startElement("", "", "lastname", atts);
		th.characters(elements[2].toCharArray(), 0, elements[2].length());
		th.endElement("", "", "lastname");
		isPActive = true;
	}

	public void processT(String[] elements) throws SAXException {
		atts.clear();
		th.startElement("", "", "phone", atts);
		th.startElement("", "", "mobile", atts);
		th.characters(elements[1].toCharArray(), 0, elements[1].length());
		th.endElement("", "", "mobile");
		th.startElement("", "", "telephone", atts);
		th.characters(elements[2].toCharArray(), 0, elements[2].length());
		th.endElement("", "", "telephone");
		th.endElement("", "", "phone");
	}
	
	public void processA(String[] elements) throws SAXException {
		int swedishAddress = elements.length;

		atts.clear();
		th.startElement("", "", "address", atts);
		th.startElement("", "", "street", atts);
		th.characters(elements[1].toCharArray(), 0, elements[1].length());
		th.endElement("", "", "street");
		th.startElement("", "", "city", atts);
		th.characters(elements[2].toCharArray(), 0, elements[2].length());
		th.endElement("", "", "city");
		if (swedishAddress > 3) {
			th.startElement("", "", "zip", atts);
			th.characters(elements[3].toCharArray(), 0, elements[3].length());
			th.endElement("", "", "zip");
		}
		th.endElement("", "", "address");
	}
	
	public void processF(String[] elements) throws SAXException {
		if (isFActive) {
			th.endElement("", "", "family");
		}
		atts.clear();
		th.startElement("", "", "family", atts);
		th.startElement("", "", "name", atts);
		th.characters(elements[1].toCharArray(), 0, elements[1].length());
		th.endElement("", "", "name");
		th.startElement("", "", "born", atts);
		th.characters(elements[2].toCharArray(), 0, elements[2].length());
		th.endElement("", "", "born");
		isFActive = true;
	}

	public void closeXML() throws SAXException {
		th.endElement("", "", "people");
		th.endDocument();
	}
}
