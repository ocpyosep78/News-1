package au.edu.unimelb.news.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import au.edu.unimelb.helper.DeleteFolder;
import au.edu.unimelb.helper.StringHelper;
import au.edu.unimelb.news.dao.ArticleTopic;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.Article;
import au.edu.unimelb.news.dao.Topic;
import au.edu.unimelb.news.model.Topics;
import au.edu.unimelb.security.LogHelper;
import au.edu.unimelb.security.dao.Person;
import au.edu.unimelb.security.model.User;

@SuppressWarnings("deprecation")
public class ArticleImport {

	private List<String> messages = new ArrayList<String>();
	private File workingFolder = null;

	public List<String> process(InputStream in, User user) {
		try {
			workingFolder = File.createTempFile("articletmp", Long.toString(System.nanoTime()));

			if(!workingFolder.delete()) {
				messages.add("Problem creating temporary file: "+workingFolder.getAbsolutePath());
				LogHelper.log("System","Import",user.getPersonId(),"Problem creating temporary file: "+workingFolder.getAbsolutePath(),user.getIP());
				return messages;
			}

			if(!workingFolder.mkdir()) {
				messages.add("Temporary working folder could not be created: "+workingFolder.getAbsolutePath());
				LogHelper.log("System","Import",user.getPersonId(),"Temporary working folder could not be created: "+workingFolder.getAbsolutePath(),user.getIP());
				return messages;
			}
		} catch (IOException e) {
			messages.add("Problem creating temporary file: "+workingFolder.getAbsolutePath()+" "+e.getMessage());
			LogHelper.log("System","Import",user.getPersonId(),"Problem creating temporary file: "+workingFolder.getAbsolutePath()+" "+e.getMessage(),user.getIP());
			return messages;
		}

		LogHelper.log("System","Import",user.getPersonId(),"Created import folder: "+workingFolder,user.getIP());

		processZipfile(in, user);		
		processDataFiles(user);

		try {
			(new DeleteFolder(workingFolder)).execute();
		} catch(IOException e) {
			LogHelper.log("System","Import",user.getPersonId(),"Problem removing temporary working folder: "+e.getMessage(),user.getIP());
		}

		return messages;
	}

	private void processZipfile(InputStream inputStream, User user) {
		LogHelper.log("System","Import",user.getPersonId(),"Beginning unzip of uploaded file",user.getIP());
		int count=0;

		try {
			ZipInputStream zin = new ZipInputStream(inputStream);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				File outFile = new File(workingFolder + System.getProperty("file.separator") + ze.getName());
				LogHelper.log("System","Import",user.getPersonId(),"Extracting file from zip file: "+outFile,user.getIP());
				messages.add("Extracting file from zip file: "+outFile);
				FileOutputStream fout = new FileOutputStream(outFile);
				int bytesRead;
				byte[] tempBuffer = new byte[1024*16];
				while ( (bytesRead = zin.read(tempBuffer)) != -1 ){
					fout.write(tempBuffer, 0, bytesRead);
				}

				//for (int c = zin.read(); c != -1; c = zin.read()) {
				//	fout.write(c);
				//}
				zin.closeEntry();
				fout.close();
				count=count+1;
			}
			zin.close();
		} catch(IOException e) {
			LogHelper.log("System","Import",user.getPersonId(),"Problem extracting zip file contents."+e.getMessage(),user.getIP());
			messages.add("Problem extracting zip file contents."+e.getMessage());
		}

		if(count==0) {
			LogHelper.log("System","Import",user.getPersonId(),"Zip file is either invald or it has no files inside.",user.getIP());
			messages.add("Zip file is either invald or it has no files inside.");
			return;
		}
		
		LogHelper.log("System","Import",user.getPersonId(),"Finished unzip of uploaded file",user.getIP());
	}

	private void processDataFiles(User user) {
		LogHelper.log("System","Import",user.getPersonId(),"Analysing the index file",user.getIP());

		try {

			String[] children = workingFolder.list();
			if (children != null)
				for (int i=0; i<children.length; i++) {
					if(children[i].toLowerCase().endsWith(".xml")) {
						messages.add("Processing data file: "+children[i]);
						LogHelper.log("System","Import",user.getPersonId(),"Processing data file: "+children[i],user.getIP());
						FileInputStream in = new FileInputStream(workingFolder + System.getProperty("file.separator") + children[i]);
						processFile(user,in);
						in.close();
					}
				}
		} catch(Exception e) {
			messages.add("A problem occurred while reading import file contents.");
			LogHelper.log("System","Import",user.getPersonId(),"A problem occurred while reading import file contents.",user.getIP());
			return;
		}

	}
	
	private void processFile(User user, FileInputStream in) {
		LogHelper.log("System","Import",user.getPersonId(),"Analysing the index file",user.getIP());

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(in);

			doc.getDocumentElement().normalize();

			NodeList committeeNodes = doc.getElementsByTagName("articles");
			if(committeeNodes.getLength()==0) {
				messages.add("Top level of XML document should be <articles>");
				LogHelper.log("System","Import",user.getPersonId(),"Top level of XML document should be <articles>",user.getIP());
				return;
			}

			loadArticles(doc.getElementsByTagName("article"),user);
			//List<Section> sections = loadSections(doc.getElementsByTagName("sections"),user);

			/*
			if(attributes == null) {
				messages.add("Problem while reading <attributes>");
				LogHelper.log("System","Import",user.getPersonId(),"Problem while reading <attributes>",user.getIP());
				return;
			}
			if(sections == null) {
				messages.add("Problem while reading <sections>");
				LogHelper.log("System","Import",user.getPersonId(),"Problem while reading <sections>",user.getIP());
				return;
			}

			article.setLastUpdatePersonId(user.getPersonId());
			article = DAOFactory.getArticleFactory().insert(article);
			for(DocumentAttribute attribute : attributes) {
				attribute.setDocumentId(document.getId());
				DAOFactory.getDocumentAttributeFactory().insert(attribute);
			}
			for(Section section : sections) {
				section.setDocumentId(document.getId());
				DAOFactory.getSectionFactory().insert(section);
			}*/

		} catch (SAXParseException err) {
			String error="Import document parsing problem: line=" + err.getLineNumber() + " uri=" + err.getSystemId()+" message="+err.getMessage();
			messages.add(error);
			LogHelper.log("System","Import",user.getPersonId(),error,user.getIP());
			return;
		}catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
			LogHelper.log("System","Import",user.getPersonId(),"Index parsing problem: line=" + ((x == null) ? e : x).getMessage(),user.getIP());
			messages.add("Index parsing problem: line=" + ((x == null) ? e : x).getMessage());
			return;
		}catch (IOException t) {
			messages.add("Database problem occured while processing the data file: "+t.getMessage());
			LogHelper.log("System","Import",user.getPersonId(),"Database problem occured while processing the data file: "+t.getMessage(),user.getIP());
			return;
		}catch (Throwable t) {
			messages.add("Problem occured while processing the data file: "+t.getMessage());
			LogHelper.log("System","Import",user.getPersonId(),"Unexpected problem occured while processing the data file: "+t.getMessage(),user.getIP());
			t.printStackTrace();
			return;
		}

		LogHelper.log("System","Import",user.getPersonId(),"Finished processing the import file.",user.getIP());
	}

	private String getElementTagString(Element e,String tag) {
		NodeList firstNameList = e.getElementsByTagName(tag);
		Element firstNameElement = (Element)firstNameList.item(0);
		if(firstNameElement == null) return "";
		NodeList textFNList = firstNameElement.getChildNodes();
		Node contents=textFNList.item(0);
		if(contents==null) return "";
		String value=contents.getNodeValue();
		if(value==null) { return ""; }
		return value.trim();
	}

	private String getElementTagData(Element e,String tag) throws ParserConfigurationException {
		NodeList firstNameList = e.getElementsByTagName(tag);
		Element firstNameElement = (Element)firstNameList.item(0);
		if(firstNameElement == null) return "";
		String value = elementToString(firstNameElement);
		if(value==null) { return ""; }
		return value.trim();
	}

	/*
	private NodeList getChildNodeNamed(Node parent, String string) {
		NodeList nodes = parent.getChildNodes();
		for(int s=0; s<nodes.getLength() ; s++) {
			if(nodes.item(s).getNodeName().equalsIgnoreCase(string))
				return nodes.item(s).getChildNodes();
		}
		return null;
	}
	*/

	private NodeList getChildNodeNamed(NodeList parent, String string) {
		for(int s=0; s<parent.getLength() ; s++) {
			if(parent.item(s).getNodeName().equalsIgnoreCase(string))
				return parent.item(s).getChildNodes();
		}
		return null;
	}

	public String elementToString(org.w3c.dom.Node node) throws ParserConfigurationException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		Node copy =doc.importNode(node, true);
		doc.appendChild(copy);

		OutputFormat format  = new OutputFormat( doc );   //Serialize DOM
		StringWriter stringOut = new StringWriter();        //Writer will be a String
		XMLSerializer serial = new XMLSerializer( stringOut, format );
		try {
			serial.asDOMSerializer();
			serial.serialize( doc.getDocumentElement() );
		} catch (IOException e) {
			return e.getLocalizedMessage();
		}
		return stringOut.toString() ;
	}

	/*
	private List<Section> loadSections(NodeList sections, User user) {
		List<Section> list = new ArrayList<Section>();
		NodeList nodes = getChildNodeNamed(sections,"sections");

		int goodSections = 0;
		int badSections = 0;
		// Now process the meeting information
		for(int s=0; s<nodes.getLength() ; s++){
			Node node=nodes.item(s);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				String number = getElementTagString((Element)node,"number");
				String heading = getElementTagString((Element)node,"heading");
                String details = "";
				try {
					details = getElementTagData((Element)node,"details");
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					messages.add("Problem occured while reading the <section> <details> information.");
					LogHelper.log("Sysetm","Import",user.getPersonId(),"Problem occured while reading the <section> <details> information.",user.getIP());
					badSections++;
					continue;
				}

				if(heading.length()==0) {
					messages.add("<heading> tag in <section> element had no contents");
					LogHelper.log("Sysetm","Import",user.getPersonId(),"<heading> tag in <section> element had no contents",user.getIP());
					badSections++;
					continue;
				}
				if(heading.length()==0) {
					messages.add("<number> tag in <section> element had no contents");
					LogHelper.log("Sysetm","Import",user.getPersonId(),"<number> tag in <section> element had no contents",user.getIP());
					badSections++;
					continue;
				}
				Section item=new Section();
				item.setNumber(number);
				item.setName(heading);
				item.setDetails(details);
				item.setLastUpdatePersonId(user.getPersonId());
				list.add(item);
				goodSections++;
			}
		}

		messages.add("Document contains "+(goodSections+badSections) + " section(s).");
		LogHelper.log("System","Import",user.getPersonId(),"Document contains "+(goodSections+badSections) + " section(s).",user.getIP());

		if(badSections>0) {
			messages.add(badSections + " section(s) were ignored due to having invalid data.");
			LogHelper.log("System","Import",user.getPersonId(),badSections + " section(s) were ignored due to having invalid data.",user.getIP());
		}

		return list;
	}
	*/

	private void loadArticles(NodeList articles,User user) throws IOException {

		for(int s=0; s<articles.getLength() ; s++){
			Node node=articles.item(s);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				String id = getElementTagString((Element)node,"id");
				String title = getElementTagString((Element)node,"title");
				String byline = getElementTagString((Element)node,"byline");
				String topics = getElementTagString((Element)node,"topic");
				String introduction = getElementTagString((Element)node,"introduction");
				String date = getElementTagString((Element)node,"date");
				String dateCreated = getElementTagString((Element)node,"dateCreated");
				String contact = getElementTagString((Element)node,"contact");
				String document = getElementTagString((Element)node,"document");
				String username = getElementTagString((Element)node,"user");

				document = StringHelper.maxLength(document+"\r\n"+contact, 17000);

				Article article=new Article();
				article.setId(Long.parseLong(id));
				article.setName(title);
				article.setByline(byline);
				article.setIntroduction(introduction);
				article.setDetails(document);

				List<Person> people;
				people = au.edu.unimelb.security.dao.DAOFactory.getPersonFactory().getByUsernameDeleted(username, false, 0, 1);
				if(people.size()==0) {
					messages.add("Article referring to an unknown person with username: "+username);
					LogHelper.log("Sysetm","Import",user.getPersonId(),"Article referring to an unknown person. Name="+title+" Username="+username,user.getIP());
					continue;
				}
				article.setLastUpdatePersonId(people.get(0).getId());
				
				article = DAOFactory.getArticleFactory().insert(article);

				for(String item : topics.split(" *, *")) {
					if(item.length()==0) continue;
					Topic topic = Topics.get(item);
					if(topic == null) {
						messages.add("Article referring to an unknown topic: "+item);
						continue;
					}
					ArticleTopic at = new ArticleTopic();
					at.setArticleId(article.getId());
					at.setTopicId(topic.getId());
					DAOFactory.getArticleTopicFactory().insert(at);
				}
			}
		}

	}
}
