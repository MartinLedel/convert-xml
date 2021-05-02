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
		new ToXML().doit();
	}   

	public void doit() {
		try {
			in = new BufferedReader(new FileReader("data/file.txt"));
			out = new StreamResult("data/converted.xml");
			initXML();
			String str;
			isPActive = false;
			isFActive = false;
			Boolean lineProcessed = true;
			String lastLine = "";
			while ((str = in.readLine()) != null && lineProcessed) {
				lastLine = str;
				lineProcessed = processLine(str);
			}
			if (!lineProcessed) {
				throw new WrongFormatException("Missing starting letter used for XML formatting " + lastLine);
			}
			if(isFActive) {
				th.endElement("", "", "family");
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

	public boolean processLine(String s) throws SAXException {
		String[] elements = s.split("\\|");
		String startingLetter = elements[0];
		Boolean toReturn = true;
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
			  toReturn = false;
			  break;
		}
		return toReturn;
	}
	
	public void processP(String[] elements) throws SAXException {
		if (isPActive && isFActive) {			
			th.endElement("", "", "family");
			th.endElement("", "", "person");
			isFActive = false;
		} else if (isPActive) {
			th.endElement("", "", "person");
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
