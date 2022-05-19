package scripts;

import java.io.File;
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

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class makeCollection {
	
	private String data_path;
	private String output_file = "./collection.xml";
	
	public static File[] makeFileList(String path) {
		File dir = new File(path);
		return dir.listFiles();
	}
	
	public makeCollection(String path) throws ParserConfigurationException, IOException, TransformerException {
		this.data_path = path;
	}
	
	public void makeXml() throws ParserConfigurationException, IOException, TransformerException {
		File[] file = makeFileList(data_path);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document document = docBuilder.newDocument();

		Element docs = document.createElement("docs");
		document.appendChild(docs);

		for (int i = 0; i < file.length; i++) {
			org.jsoup.nodes.Document html = Jsoup.parse(file[i], "UTF-8");
			String titleData = html.title();
			String bodyData = html.body().text();

			Element doc = document.createElement("doc");
			docs.appendChild(doc);

			doc.setAttribute("id", String.valueOf(i));

			Element title = document.createElement("title");
			title.appendChild(document.createTextNode(titleData));
			doc.appendChild(title);

			Element body = document.createElement("body");
			body.appendChild(document.createTextNode(bodyData));
			doc.appendChild(body);
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new FileOutputStream(new File(output_file)));

		transformer.transform(source, result);
		System.out.println("2주차 실행완료");
	}
	

}
