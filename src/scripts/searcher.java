package scripts;

import java.io.*;
import java.util.HashMap;
import javax.xml.parsers.*;
import org.snu.ids.kkma.index.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

class SimByIdx {
	int idx;
	double sim;
	double tfA;
	double tfB;
}

public class searcher {
	KeywordExtractor ke;
	KeywordList kl;
	HashMap<String, String> hashMap;
	SimByIdx[] simByIdx;
	
	private String input_file;

	public searcher(String path) {
		this.input_file = path;
	}
	
	
	@SuppressWarnings("unchecked")
	public void search(String query)
			throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {	
		ke = new KeywordExtractor();
		kl = ke.extractKeyword(query, true);

		FileInputStream fileInputStream = new FileInputStream(input_file);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

		Object object = objectInputStream.readObject();
		objectInputStream.close();

		hashMap = (HashMap<String, String>) object;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.parse("index.xml");

		document.getDocumentElement().normalize();
		NodeList nodeList = document.getElementsByTagName("doc");
		int n = nodeList.getLength();
		simByIdx = new SimByIdx[n];
		for (int i = 0; i < simByIdx.length; i++) {
			simByIdx[i] = new SimByIdx();
			simByIdx[i].idx = i;
		}
		
		InnerProduct();
		

		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				SimByIdx temp;
				if (simByIdx[i].sim > simByIdx[j].sim) {
					temp = simByIdx[i];
					simByIdx[i] = simByIdx[j];
					simByIdx[j] = temp;
				}
			}
		}
		
		boolean isZero = true;
		boolean isEqual = true;
		int searched = 0;
		for (int i = 0; i < simByIdx.length; i++) {
			if (simByIdx[i].sim != 0) {
				isZero = false;
				searched++;
				break;
			}
			
			if (i != 0)
				if (simByIdx[i - 1].idx != simByIdx[i].idx)
					isEqual = false;
		}
		
		
		if ((isZero) || (isEqual && searched > 3))
			System.out.println("검색된 문구가 없습니다.");
		else if (searched < 3) {
			int c = 0;
			for (int i = 0; i < n; i++) {
				if (simByIdx[i].sim > 0) {
					System.out.println("Document title: " + ((Element) nodeList.item(simByIdx[i].idx)).getElementsByTagName("title").item(0).getTextContent() + ", Similarity: " + String.format("%.2f", simByIdx[i].sim));
					c++;
				}
				if (c == 3)	break;
			}
		} else
			for (int i = 0; i < n; i++) 
				System.out.println("Document title: " + ((Element) nodeList.item(simByIdx[i].idx)).getElementsByTagName("title").item(0).getTextContent() + ", Similarity: " + String.format("%.2f", simByIdx[i].sim));
	}
	
	public void InnerProduct()
			throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {		

		for (int i = 0; i < kl.size(); i++) {
			Keyword kwrd = kl.get(i);
			if (hashMap.containsKey(kwrd.getString())) {
				String str[] = hashMap.get(kwrd.getString()).split(" ");
				double index[] = new double[str.length];
				for (int j = 0; j < str.length; j++)
					index[j] = Double.parseDouble(str[j]);
				for (int j = 0; j < str.length; j += 2) {
					simByIdx[(int) index[j]].sim += kwrd.getCnt() * index[j + 1];
					simByIdx[(int) index[j]].tfA += Math.pow(kwrd.getCnt(), 2);
					simByIdx[(int) index[j]].tfB += Math.pow(index[j + 1], 2);
				}
			}
		}
		
		for (int i = 0; i < kl.size(); i++) {
			simByIdx[i].tfA = Math.sqrt(simByIdx[i].tfA);
			simByIdx[i].tfB = Math.sqrt(simByIdx[i].tfB);
			simByIdx[i].sim /= simByIdx[i].tfA * simByIdx[i].tfB;
		}
		
	}

}