package au.edu.unimelb.news.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import au.edu.unimelb.news.dao.Newsletter;
import au.edu.unimelb.news.dao.NewsletterArticle;
import au.edu.unimelb.news.dao.Publication;
import au.edu.unimelb.news.dao.Topic;
import au.edu.unimelb.news.model.Topics;
import au.edu.unimelb.security.LogHelper;
import au.edu.unimelb.security.dao.Person;
import au.edu.unimelb.security.model.User;



@SuppressWarnings("deprecation")
public class ArticleImport {

	private File workingFolder = null;
	private int staffnewsArticle = 10000;
	private ArticleImportResponder responder;

	public ArticleImport(ArticleImportResponder responder) {
		this.responder = responder;
	}

	public void process(InputStream in, User user) {
		responder.feedback("Beginning file analysis");

		try {
			workingFolder = File.createTempFile("articletmp", Long.toString(System.nanoTime()));

			if(!workingFolder.delete()) {
				responder.feedback("Problem creating temporary file: "+workingFolder.getAbsolutePath());
				return;
			}

			if(!workingFolder.mkdir()) {
				responder.feedback("Temporary working folder could not be created: "+workingFolder.getAbsolutePath());
			}
		} catch (IOException e) {
			responder.feedback("Problem creating temporary file: "+workingFolder.getAbsolutePath()+" "+e.getMessage());
			return;
		}

		LogHelper.log("System","Import",user.getPersonId(),"Created import folder: "+workingFolder,user.getIP());

		processZipfile(in, user);
		processDataFiles(user);

		try {
			(new DeleteFolder(workingFolder)).execute();
		} catch(IOException e) {
			responder.feedback("Problem removing temporary working folder: "+e.getMessage());
		}

		responder.feedback("Completed file analysis");
		return;
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
				responder.feedback("Extracting file from zip file: "+outFile);
				FileOutputStream fout = new FileOutputStream(outFile);

				int bytesRead;
				byte[] tempBuffer = new byte[1024*16];
				while ( (bytesRead = zin.read(tempBuffer)) != -1 ){
					fout.write(tempBuffer, 0, bytesRead);
				}

				zin.closeEntry();
				fout.close();
				count=count+1;
			}
			zin.close();
		} catch(IOException e) {
			responder.feedback("Problem extracting zip file contents."+e.getMessage());
		}

		if(count==0) {
			responder.feedback("Zip file is either invald or it has no files inside.");
			return;
		}

		LogHelper.log("System","Import",user.getPersonId(),"Finished unzip of uploaded file",user.getIP());
	}

	private void processDataFiles(User user) {

		try {

			String[] children = workingFolder.list();
			if (children != null)
				for (int i=0; i<children.length; i++) {
					if(children[i].toLowerCase().endsWith(".xml")) {
						responder.feedback("Processing data file: "+children[i]);
						FileInputStream in = new FileInputStream(workingFolder + System.getProperty("file.separator") + children[i]);
						processFile(user,in);
						in.close();
					}
				}
		} catch(Exception e) {
			responder.feedback("A problem occurred while reading import file contents.");
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

			Element root = doc.getDocumentElement();

			if(root.getNodeName().equalsIgnoreCase("articles"))
				loadArticles(doc.getElementsByTagName("article"),user);
			else if(root.getNodeName().equalsIgnoreCase("newsletters"))
				loadNewsletters(doc.getElementsByTagName("newsletter"),user);
			else if(root.getNodeName().equalsIgnoreCase("staffnews"))
				loadStaffnews(doc.getElementsByTagName("staffnews"),user);
			else
				responder.feedback("XML file had unknown root element name: "+root.getNodeName());

		} catch (SAXParseException err) {
			responder.feedback("Import document parsing problem: line=" + err.getLineNumber() + " uri=" + err.getSystemId()+" message="+err.getMessage());
			return;
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
			responder.feedback("Index parsing problem: line=" + ((x == null) ? e : x).getMessage());
			return;
		} catch (IOException t) {
			responder.feedback("Database problem occured while processing the data file: "+t.getMessage());
			return;
		} catch (Throwable t) {
			responder.feedback("Problem occured while processing the data file: "+t.getMessage());
			LogHelper.log("System","Import",user.getPersonId(),"Unexpected problem occured while processing the data file: "+t.getMessage(),user.getIP());
			t.printStackTrace();
			return;
		}

		responder.feedback("Finished processing the import file.");
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

	/*
	private String getElementTagData(Element e,String tag) throws ParserConfigurationException {
		NodeList firstNameList = e.getElementsByTagName(tag);
		Element firstNameElement = (Element)firstNameList.item(0);
		if(firstNameElement == null) return "";
		String value = elementToString(firstNameElement);
		if(value==null) { return ""; }
		return value.trim();
	}
	*/

	private NodeList getChildNodeNamed(Node parent, String string) {
		NodeList nodes = parent.getChildNodes();
		for(int s=0; s<nodes.getLength() ; s++) {
			if(nodes.item(s).getNodeName().equalsIgnoreCase(string))
				return nodes.item(s).getChildNodes();
		}
		return null;
	}

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

	private void loadArticles(NodeList articles,User user) throws IOException {

		for(int s=0; s<articles.getLength() ; s++){
			Node node=articles.item(s);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				String id = getElementTagString((Element)node,"id");
				if(id.length()==0) continue;
				String name = getElementTagString((Element)node,"name");
				String publicationName = getElementTagString((Element)node,"publication");
				String byline = getElementTagString((Element)node,"byline");
				String topics = getElementTagString((Element)node,"topic");
				String introduction = getElementTagString((Element)node,"introduction");
				String publishedDate = getElementTagString((Element)node,"published_date");
				String createdDate = getElementTagString((Element)node,"created_date");
				String lastUpdated = getElementTagString((Element)node,"last_updated");
				String contact = getElementTagString((Element)node,"contact");
				String details = getElementTagString((Element)node,"details");
				String username = getElementTagString((Element)node,"user");

				details = StringHelper.maxLength(details+"\r\n"+contact, 17000);

				Publication publication = Publications.get(publicationName);
				if(publication == null) {
					responder.feedback("Article referring to an unknown publication. Name="+publicationName);
					continue;
				}

				DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

				Article article=new Article();
				article.setId(Long.parseLong(id));
				article.setPublicationId(publication.getId());
				article.setName(unescapeCrap(name));
				article.setByline(unescapeCrap(byline));

				if(introduction.startsWith("<b>") && introduction.endsWith("</b>"))
					introduction=introduction.substring(3,introduction.length()-6);
				article.setIntroduction(unescapeCrap(introduction));
				article.setDetails(unescapeCrap(details));
				article.setPublished(true);
				article.setStatus("Published");
				article.setDeleted(false);
				if(publishedDate.startsWith("0000")) publishedDate = lastUpdated;
				try {
					article.setCreatedDate(format.parse(createdDate));
				} catch (ParseException e) {
					responder.feedback("Date parsing problem for date in newsletter/articles/article element where article id = "+article.getId());
				}
				try {
					article.setPublishedDate(format.parse(publishedDate));
				} catch (ParseException e) {
					responder.feedback("Date parsing problem for date in newsletter/articles/article element where article id = "+article.getId());
				}
				try {
					article.setLastUpdate(format.parse(lastUpdated));
				} catch (ParseException e) {
					responder.feedback("Date parsing problem for date in newsletter/articles/article element where article id = "+article.getId());
				}

				List<Person> people;
				people = au.edu.unimelb.security.dao.DAOFactory.getPersonFactory().getByUsernameDeleted(username, false, 0, 1);
				if(people.size()==0) {
					responder.feedback("Article "+id+" referring to an unknown person with username: "+username);
					continue;
				}
				article.setLastUpdatePersonId(people.get(0).getId());

				article = DAOFactory.getArticleFactory().insert(article);

				for(String item : topics.split(" *, *")) {
					if(item.length()==0) continue;
					Topic topic = Topics.get(item);
					if(topic == null) {
						if(item.equals("Other"))
							continue;
						responder.feedback("Article referring to an unknown topic: "+item);
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

	private void loadNewsletters(NodeList newsletters,User user) throws IOException {

		for(int s=0; s<newsletters.getLength() ; s++){
			Node node=newsletters.item(s);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				String id = getElementTagString((Element)node,"id");
				String heading = getElementTagString((Element)node,"heading");
				String publicationName = getElementTagString((Element)node,"publication");
				String startDate = getElementTagString((Element)node,"start_date");
				String endDate = getElementTagString((Element)node,"end_date");
				String username = getElementTagString((Element)node,"user");

				Publication publication = Publications.get(publicationName);
				if(publication == null) {
					responder.feedback("Article referring to an unknown publication. Name="+publicationName);
					continue;
				}

				Newsletter newsletter=new Newsletter();
				if(id.length()>0)
					newsletter.setId(Long.parseLong(id));
				newsletter.setPublicationId(publication.getId());
				newsletter.setName(unescapeCrap(heading));
				newsletter.setPublished(true);
				newsletter.setStatus("Published");
				newsletter.setArchived(false);
				newsletter.setDeleted(false);

				DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

				if(endDate.startsWith("0000")) startDate = endDate;
				try {
					newsletter.setStartDate(format.parse(startDate));
				} catch (ParseException e) {
					responder.feedback("Date parsing problem for date in newsletter/start_date element where newsletter id = "+newsletter.getId());
				}
				try {
					newsletter.setEndDate(format.parse(endDate));
				} catch (ParseException e) {
					responder.feedback("Date parsing problem for date in newsletter/end_date element where newsletter id = "+newsletter.getId());
				}

				List<Person> people;
				people = au.edu.unimelb.security.dao.DAOFactory.getPersonFactory().getByUsernameDeleted(username, false, 0, 1);
				if(people.size()==0) {
					newsletter.setLastUpdatePersonId(user.getPersonId());
				} else {
					newsletter.setLastUpdatePersonId(people.get(0).getId());
				}

				newsletter = DAOFactory.getNewsletterFactory().insert(newsletter);

				NodeList articles = this.getChildNodeNamed(node, "articles");
				for(int t=0; t<articles.getLength() ; t++) {
					if(articles.item(t).getNodeName().equalsIgnoreCase("article")) {
						NodeList article = articles.item(t).getChildNodes();
						String section = getElementTagString((Element)article,"section");
						String articleId = getElementTagString((Element)article,"article_id");
						String sortOrder = getElementTagString((Element)article,"sort_order");
						String picture = getElementTagString((Element)article,"picture");
						NewsletterArticle item = new NewsletterArticle();
						item.setNewsletterId(newsletter.getId());
						item.setArticleId(Long.parseLong(articleId));
						item.setSortOrder(Long.parseLong(sortOrder));
						item.setSection(section);
						item.setPicture(picture);
						DAOFactory.getNewsletterArticleFactory().insert(item);
					}
				}

			}
		}

	}

	private void loadStaffnews(NodeList nodelist,User user) throws IOException {

		NodeList newsletters = this.getChildNodeNamed(nodelist, "staffnews");

		for(int s=0; s<newsletters.getLength() ; s++){
			Node node=newsletters.item(s);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				String heading = getElementTagString((Element)node,"heading");
				String publicationName = getElementTagString((Element)node,"publication");
				String date = getElementTagString((Element)node,"start_date");
				String username = getElementTagString((Element)node,"user");
				String text = getElementTagString((Element)node,"article");

				Publication publication = Publications.get(publicationName);
				if(publication == null) {
					responder.feedback("Article referring to an unknown publication. Name="+publicationName);
					continue;
				}

				Newsletter newsletter=new Newsletter();
				newsletter.setPublicationId(publication.getId());
				newsletter.setName(unescapeCrap(heading));
				newsletter.setPublished(true);
				newsletter.setStatus("Published");
				newsletter.setArchived(false);
				newsletter.setDeleted(false);

				DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

				try {
					newsletter.setStartDate(format.parse(date));
				} catch (ParseException e) {
					responder.feedback("Date parsing problem for date in newsletter/start_date element where newsletter id = "+newsletter.getId());
				}
				newsletter.setEndDate(newsletter.getStartDate());
				newsletter.setLastUpdate(newsletter.getStartDate());

				List<Person> people;
				people = au.edu.unimelb.security.dao.DAOFactory.getPersonFactory().getByUsernameDeleted(username, false, 0, 1);
				if(people.size()==0) {
					newsletter.setLastUpdatePersonId(user.getPersonId());
				} else {
					newsletter.setLastUpdatePersonId(people.get(0).getId());
				}

				newsletter = DAOFactory.getNewsletterFactory().insert(newsletter);

				List<Article> articles = StaffNewsParser.parse(text);
				int sortOrder = 0;
				for(Article article : articles) {
					sortOrder++;
					article.setName(unescapeCrap(article.getName()));
					article.setByline(unescapeCrap(article.getByline()));
					article.setDetails(unescapeCrap(article.getDetails()));
					article.setIntroduction(unescapeCrap(article.getIntroduction()));
					article.setId(staffnewsArticle++);
					article.setDeleted(false);
					article.setPublished(false);
					article.setPublicationId(publication.getId());
					article.setLastUpdate(newsletter.getStartDate());
					article.setCreatedDate(newsletter.getStartDate());
					article.setPublishedDate(newsletter.getStartDate());
					article.setLastUpdatePersonId(newsletter.getLastUpdatePersonId());
					article = DAOFactory.getArticleFactory().insert(article);

					NewsletterArticle item = new NewsletterArticle();
					item.setNewsletterId(newsletter.getId());
					item.setArticleId(article.getId());
					item.setSortOrder(sortOrder);
					item.setSection("");
					item.setPicture("");
					DAOFactory.getNewsletterArticleFactory().insert(item);
				}

			}
		}

	}

	/**
	 * Remove all of the pointlessly escaped html characters due to legacy character encoding issues.
	 * @return
	 */
	private static String unescapeCrap(String string) {

		string = string.replace("&amp;", "&");
		string = string.replace("&lsquo;", "‘");
		string = string.replace("&rsquo;", "’");
		string = string.replace("&ldquo;", "“");
		string = string.replace("&rdquo;", "”");
		string = string.replace("&mdash;", "—");
		string = string.replace("&ndash;", "—");
		string = string.replace("&mdash;", "“");
		string = string.replace("&mdash;", "”");

		return string;
	}

}
