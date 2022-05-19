package scripts;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class kuir {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException, ClassNotFoundException {
		
		String command = args[0];
		String path = args[1];
		
		if(command.equals("-c")) {
			makeCollection collection = new makeCollection(path);
			collection.makeXml();
		}
		else if(command.equals("-k")) {
			makeKeyword keyword = new makeKeyword(path);
			keyword.convertXml();
		}
		else if(command.equals("-i")) {
			indexer index = new indexer(path);
			index.makePost();
		}
		else if(command.equals("-s")) {
			searcher searcher = new searcher(path);
			String query = args[3];
<<<<<<< HEAD
			searcher.calcsim(query);
=======
			searcher.search(query);
>>>>>>> feature
		}
	}

}
