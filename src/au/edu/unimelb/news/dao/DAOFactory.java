/*
 * This file is automatically generated by the UptecsDaoGenerator. You 
 * should not edit this file directly as changes here may be overwritten 
 * by the auto code generator. 
 */
package au.edu.unimelb.news.dao;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;
public class DAOFactory {

    private static TopicFactory topicFactory;
    private static PublicationFactory publicationFactory;
    private static NewsletterViewCountFactory newsletterViewCountFactory;
    private static AttachmentFactory attachmentFactory;
    private static ArticleFactory articleFactory;
    private static ArticleViewCountFactory articleViewCountFactory;
    private static ArticleTopicFactory articleTopicFactory;
    private static SearchIndexFactory searchIndexFactory;
    private static NewsletterFactory newsletterFactory;

    public static DataSource dataSource=null;

    public static void setup() throws IOException {
        try {
        	 Context ctx = new InitialContext();
            if(ctx==null)
                throw new IOException("Could not retrieve 'Initial Context'.");
            dataSource=(DataSource)ctx.lookup("java:comp/env/jdbc/news");
        } catch(NamingException e) {
            throw new IOException("Problem with Database Configuration. "+e.toString());
        }
		if(dataSource==null)
			throw new IOException("Problem setting up / reading database DataSource");

        topicFactory=new TopicFactory(dataSource);
        publicationFactory=new PublicationFactory(dataSource);
        newsletterViewCountFactory=new NewsletterViewCountFactory(dataSource);
        attachmentFactory=new AttachmentFactory(dataSource);
        articleFactory=new ArticleFactory(dataSource);
        articleViewCountFactory=new ArticleViewCountFactory(dataSource);
        articleTopicFactory=new ArticleTopicFactory(dataSource);
        searchIndexFactory=new SearchIndexFactory(dataSource);
        newsletterFactory=new NewsletterFactory(dataSource);

        topicFactory.setup();
        publicationFactory.setup();
        newsletterViewCountFactory.setup();
        attachmentFactory.setup();
        articleFactory.setup();
        articleViewCountFactory.setup();
        articleTopicFactory.setup();
        searchIndexFactory.setup();
        newsletterFactory.setup();

        topicFactory.postSetup();
        publicationFactory.postSetup();
        newsletterViewCountFactory.postSetup();
        attachmentFactory.postSetup();
        articleFactory.postSetup();
        articleViewCountFactory.postSetup();
        articleTopicFactory.postSetup();
        searchIndexFactory.postSetup();
        newsletterFactory.postSetup();

		try {
			executeCommand("create trigger si_article_ins after insert on article for each row begin insert into search_index (article_id, field_name, field_value) values (NEW.id, 'article_name', NEW.name); insert into search_index (article_id, field_name, field_value) values (NEW.id, 'article_details', NEW.details); end;");
		} catch(IOException e) { e.printStackTrace(); }

		try {
			executeCommand("create trigger si_article_upd after update on article for each row begin update search_index set field_value=NEW.name where article_id=NEW.id and field_name='article_name'; update search_index set field_value=NEW.details where article_id=NEW.id and field_name='article_details'; end;");
		} catch(IOException e) { e.printStackTrace(); }

		try {
			executeCommand("alter table search_index type=MyISAM;");
		} catch(IOException e) { e.printStackTrace(); }

		try {
			executeCommand("create fulltext index si_search on search_index (field_value);");
		} catch(IOException e) { e.printStackTrace(); }

    }

    public static TopicFactory getTopicFactory() throws IOException {
        if(topicFactory==null) {
            setup();
        }
        return topicFactory;
    }

    public static PublicationFactory getPublicationFactory() throws IOException {
        if(publicationFactory==null) {
            setup();
        }
        return publicationFactory;
    }

    public static NewsletterViewCountFactory getNewsletterViewCountFactory() throws IOException {
        if(newsletterViewCountFactory==null) {
            setup();
        }
        return newsletterViewCountFactory;
    }

    public static AttachmentFactory getAttachmentFactory() throws IOException {
        if(attachmentFactory==null) {
            setup();
        }
        return attachmentFactory;
    }

    public static ArticleFactory getArticleFactory() throws IOException {
        if(articleFactory==null) {
            setup();
        }
        return articleFactory;
    }

    public static ArticleViewCountFactory getArticleViewCountFactory() throws IOException {
        if(articleViewCountFactory==null) {
            setup();
        }
        return articleViewCountFactory;
    }

    public static ArticleTopicFactory getArticleTopicFactory() throws IOException {
        if(articleTopicFactory==null) {
            setup();
        }
        return articleTopicFactory;
    }

    public static SearchIndexFactory getSearchIndexFactory() throws IOException {
        if(searchIndexFactory==null) {
            setup();
        }
        return searchIndexFactory;
    }

    public static NewsletterFactory getNewsletterFactory() throws IOException {
        if(newsletterFactory==null) {
            setup();
        }
        return newsletterFactory;
    }


    public static void executeCommand(String sql) throws IOException {

        Connection c=null;
        Statement s=null;
        try {
            c=dataSource.getConnection();
            s=c.createStatement();
            s.execute(sql);
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

    }

    public static long queryNumber(String sql) throws IOException {

        long total=0;
        Connection c=null;
        Statement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.createStatement();
            results=s.executeQuery(sql);
            if(results.next()) {
                total=results.getLong(1);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

        return total;
    }

	public static List<ArticleInfo> queryArticleMostPopular() throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a join article_view_count avc on (a.id=avc.article_id) order by avc.views desc"
              );
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleMostPopular(int index, int limit) throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a join article_view_count avc on (a.id=avc.article_id) order by avc.views desc " +
                "limit "+index+","+limit
              );
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleByBrowse(String name) throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a where name like ? order by a.name,a.number"
              );
            s.setString(1,name);
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleByBrowse(String name, int index, int limit) throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a where name like ? order by a.name,a.number " +
                "limit "+index+","+limit
              );
            s.setString(1,name);
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<NewsletterInfo> queryNewsletterByPublication(Long publicationId) throws IOException {
		List<NewsletterInfo> list = new ArrayList<NewsletterInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select n.id,n.name,n.status,n.number,n.published from newsletter n where publication_id = ? order by n.name,n.last_update"
              );
            s.setLong(1,publicationId);
            results=s.executeQuery();
            while(results.next()) {
                NewsletterInfo item=new NewsletterInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<NewsletterInfo> queryNewsletterByPublication(Long publicationId, int index, int limit) throws IOException {
		List<NewsletterInfo> list = new ArrayList<NewsletterInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select n.id,n.name,n.status,n.number,n.published from newsletter n where publication_id = ? order by n.name,n.last_update " +
                "limit "+index+","+limit
              );
            s.setLong(1,publicationId);
            results=s.executeQuery();
            while(results.next()) {
                NewsletterInfo item=new NewsletterInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleRecentlyUpdated() throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a order by a.last_update desc"
              );
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleRecentlyUpdated(int index, int limit) throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a order by a.last_update desc " +
                "limit "+index+","+limit
              );
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleByTopic(Long topicId) throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a join article_topic at on (a.id=at.article_id and at.topic_id=?) order by a.name,a.number"
              );
            s.setLong(1,topicId);
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleByTopic(Long topicId, int index, int limit) throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a join article_topic at on (a.id=at.article_id and at.topic_id=?) order by a.name,a.number " +
                "limit "+index+","+limit
              );
            s.setLong(1,topicId);
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<SearchResult> queryArticleSimpleSearch(String field, String keywords) throws IOException {
		List<SearchResult> list = new ArrayList<SearchResult>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select d.id,d.name,d.status,d.number,count(d.id),d.published as headingcount from search_index si join article d on (si.article_id=d.id) where field_name=? and field_value like ? group by article_id order by headingcount desc"
              );
            s.setString(1,field);
            s.setString(2,keywords);
            results=s.executeQuery();
            while(results.next()) {
                SearchResult item=new SearchResult();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setRank(results.getLong(5));
                item.setPublished(results.getBoolean(6));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<SearchResult> queryArticleSimpleSearch(String field, String keywords, int index, int limit) throws IOException {
		List<SearchResult> list = new ArrayList<SearchResult>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select d.id,d.name,d.status,d.number,count(d.id),d.published as headingcount from search_index si join article d on (si.article_id=d.id) where field_name=? and field_value like ? group by article_id order by headingcount desc " +
                "limit "+index+","+limit
              );
            s.setString(1,field);
            s.setString(2,keywords);
            results=s.executeQuery();
            while(results.next()) {
                SearchResult item=new SearchResult();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setRank(results.getLong(5));
                item.setPublished(results.getBoolean(6));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleRecentlyPublished() throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a order by a.last_update desc"
              );
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

	public static List<ArticleInfo> queryArticleRecentlyPublished(int index, int limit) throws IOException {
		List<ArticleInfo> list = new ArrayList<ArticleInfo>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select a.id,a.name,a.status,a.number,a.published from article a order by a.last_update desc " +
                "limit "+index+","+limit
              );
            results=s.executeQuery();
            while(results.next()) {
                ArticleInfo item=new ArticleInfo();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
                item.setStatus(results.getString(3));
                item.setNumber(results.getString(4));
                item.setPublished(results.getBoolean(5));
                list.add(item);
            }
            results.close();
            results=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(results!=null) { try { results.close(); } catch(Exception f){} }
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

		return list;
	}

}
