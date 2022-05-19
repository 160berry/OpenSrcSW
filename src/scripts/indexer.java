package scripts;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class indexer {

	private static String input_file;
	private static String output_file = "./index.post";

	@SuppressWarnings("static-access")
	public indexer(String file) {
		this.input_file = file;
	}

	public static void makePost() throws SAXException, IOException, ParserConfigurationException, TransformerException, ClassNotFoundException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document document = docBuilder.parse(input_file);

		document.getDocumentElement().normalize();
		NodeList nodeList = document.getElementsByTagName("doc");
		
		int n = nodeList.getLength();
		
		FileOutputStream fileOutputStream = new FileOutputStream(output_file);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		
		List<LinkedHashMap<String, Integer>> tfList = new ArrayList<LinkedHashMap<String, Integer>>();

		for (int i = 0; i < n; i++) {
			String bodyText = ((Element) nodeList.item(i)).getElementsByTagName("body").item(0).getTextContent();
			String string[] = bodyText.split("#");
			LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<String, Integer>();

			for (String s : string) {
				String temp[] = s.split(":");
				hashMap.put(temp[0], Integer.parseInt(temp[1]));
			}

			tfList.add(hashMap);
		}
		
		List<LinkedHashMap<String, Integer>> dfList = new ArrayList<LinkedHashMap<String, Integer>>();
		
		for (int i = 0; i < n; i++) {
			LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<String, Integer>();
			Iterator<String> it = tfList.get(i).keySet().iterator();
			
			while (it.hasNext()) {
				int frequency = 0;
				String key = it.next();
				for (int j = 0; j < tfList.size(); j++)
					if (tfList.get(j).containsKey(key))
						frequency++;
				hashMap.put(key, frequency);
			}
			
			dfList.add(hashMap);
		}
		
		HashMap<String, String> IndexMap = new HashMap<String, String>();
		
		for (int i = 0; i < n; i++) {
			Iterator<String> it = dfList.get(i).keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = "";
				for (int j = 0; j < n; j++)
					if (tfList.get(j).containsKey(key))
						value += j + " " + String.format("%.2f", tfList.get(j).get(key) * Math.log(n / dfList.get(j).get(key))) + " ";
				IndexMap.put(key, value);
			}
		}
		
		objectOutputStream.writeObject(IndexMap);
		objectOutputStream.close();
		
		FileInputStream fileInputStream = new FileInputStream(output_file);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> hashMap = (HashMap<String, String>)object;
		Iterator<String> it = hashMap.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			String value = (String)hashMap.get(key);
			System.out.println(key + " → " + value);
		}
		
	}

}
