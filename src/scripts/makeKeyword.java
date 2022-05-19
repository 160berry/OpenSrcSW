package scripts;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class makeKeyword {

	private String input_file;
	private String output_file = "./index.xml";
	
	public makeKeyword(String file) {
		this.input_file = file;
	}

	public void convertXml() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document document = docBuilder.parse(input_file);

		document.getDocumentElement().normalize();
		NodeList nodeList = document.getElementsByTagName("doc");

		DocumentBuilderFactory docFactory2 = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder2 = docFactory2.newDocumentBuilder();

		Document document2 = docBuilder2.newDocument();

		Element docs = document2.createElement("docs");
		document2.appendChild(docs);

		for (int i = 0; i < nodeList.getLength(); i++) {
			String testString = "";

			KeywordExtractor ke = new KeywordExtractor();
			KeywordList kl = ke.extractKeyword(((Element) nodeList.item(i)).getElementsByTagName("body").item(0).getTextContent(), true);
			for (int j = 0; j < kl.size(); j++) {
				Keyword kwrd = kl.get(j);
				testString += kwrd.getString() + ":" + kwrd.getCnt() + "#";
			}

			Element doc = document2.createElement("doc");
			docs.appendChild(doc);

			doc.setAttribute("id", String.valueOf(i));

			Element title = document2.createElement("title");
			title.appendChild(document2.createTextNode(((Element) nodeList.item(i)).getElementsByTagName("title").item(0).getTextContent()));
			doc.appendChild(title);

			Element body = document2.createElement("body");
			body.appendChild(document2.createTextNode(testString));
			doc.appendChild(body);
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		DOMSource source = new DOMSource(document2);
		StreamResult result = new StreamResult(new FileOutputStream(output_file));

		transformer.transform(source, result);
		System.out.println("3주차 실행완료");
	}


}