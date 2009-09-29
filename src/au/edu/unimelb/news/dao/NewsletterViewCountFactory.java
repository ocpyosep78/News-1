/* Generated by DaoGen version 0.1
 *
 * DO NOT EDIT THIS FILE. This file was automatically
 * generated, any changes made to this fill will be
 * lost if the file is re-generated.
 */
package au.edu.unimelb.news.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Date;

public class NewsletterViewCountFactory {

    private DataSource dataSource=null;

    public NewsletterViewCountFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    /**
     * Setup is called during factory initialization to 
     * allow any initialization to be done before this 
     * factory object is used.
     */
    public void setup() throws IOException {

        //Create the database table if it does not exist.
        Connection c=null;
        PreparedStatement s=null;
		try {
			c=dataSource.getConnection();
			s=c.prepareStatement(
				"create table if not exists newsletter_view_count (" +
				"id bigint auto_increment primary key,"+
                "newsletter_id bigint,"+
                "views bigint"+
				")DEFAULT CHARSET=utf8 ENGINE=innodb");
            s.execute();
            s.close();
            s=null;
        } catch(SQLException e) {
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(!e.toString().contains("ORA-00955")) {
                if(c!=null) { try { c.close(); } catch(Exception f){} }
                throw new IOException(e.toString());
            }
        }

        if(c!=null) { try { c.close(); } catch(Exception f){} }
    }

    /**
     * Post-setup is called when the DAO layer has completed 
     * initalisation of all DAO objects. 
     */
    public void postSetup() throws IOException {

    }

    /**
     * Retrieve an object from the <i>Newsletter View Count</i> data source. 
     */
    public NewsletterViewCount get(long id) throws IOException {
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        NewsletterViewCount item=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select id,newsletter_id,views "+
                "from newsletter_view_count " +
                "where id=?");
            s.setLong(1,id);
            results=s.executeQuery();
            if(results.next()) {
                item=new NewsletterViewCount();
                item.setId(results.getLong(1));
                item.setNewsletterId(results.getLong(2));
                item.setViews(results.getLong(3));
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

        return item;
    }

    /**
     * Retrieve a list of objects from the <i>Newsletter View Count</i>
     * data source. 
     */
    public List<NewsletterViewCount> getAll(long index, long limit) throws IOException {
        List<NewsletterViewCount> list=new ArrayList<NewsletterViewCount>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select id, newsletter_id, views "+
                "from newsletter_view_count " +
                "order by id " +
                "limit "+index+","+limit
                );
            results=s.executeQuery();
            while(results.next()) {
                NewsletterViewCount item=new NewsletterViewCount();
                item.setId(results.getLong(1));
                item.setNewsletterId(results.getLong(2));
                item.setViews(results.getLong(3));
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

    /**
     * Permanently remove an object from the <i>Newsletter View Count</i>
     * data source. 
     */
    public void delete(long id) throws IOException {
        Connection c=null;
        PreparedStatement s=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "delete from newsletter_view_count "+
                "where id=?");
            s.setLong(1,id);
            s.execute();
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

    /**
     * Permanently remove all objects from the <i>Newsletter View Count</i>
     * data source. 
     */
    public void deleteAll() throws IOException {
        Connection c=null;
        PreparedStatement s=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement("delete from newsletter_view_count"); 
            s.execute();
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

    /**
     * Add a new <i>Newsletter View Count</i> object to the data source. 
     */
    public NewsletterViewCount insert(NewsletterViewCount item) throws IOException {
        Connection c=null;
        PreparedStatement s=null;

        try {
            c=dataSource.getConnection();

			s=c.prepareStatement(
				"insert into newsletter_view_count ("+
					((item.getId()>0)?"id, ":"")+
                    "newsletter_id, "+
                    "views) "+
                "values("+(item.getId()>0?"?,":"")+"?,?)");
			if(item.getId()>0) {
			s.setLong(1,item.getId());
            s.setLong(2,item.getNewsletterId());
            s.setLong(3,item.getViews());
			} else {
            s.setLong(1,item.getNewsletterId());
            s.setLong(2,item.getViews());
			}
            s.execute();
            // Discover the unique id allocated to the new record
            ResultSet r = s.getGeneratedKeys();
            if (r.next()) {
            	item.setId(r.getInt(1));
            }
            r.close();
            r=null;
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            System.err.println("Problem duing inserting into table newsletter_view_count. "+
                "newsletter_id="+item.getNewsletterId()+", "+ 
                "views="+item.getViews()+", "+ 
        "");
            throw new IOException(e.toString());
        }

        return item;
    }

    /**
     * Update an object of type <i>Newsletter View Count</i> the 
     * data source. 
     */
    public void update(NewsletterViewCount item) throws IOException {
        Connection c=null;
        PreparedStatement s=null;

        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "update newsletter_view_count set newsletter_id=?, views=? "+
                "where id=?");
            s.setLong(1,item.getNewsletterId());
            s.setLong(2,item.getViews());
            s.setLong(3,item.getId());
            s.execute();
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

    /**
     * Count the number of objects from the <i>Newsletter View Count</i>
     * data source. 
     */
    public long countAll() throws IOException {
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        long count=0;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select count(1) "+
                "from newsletter_view_count "
                );
            results=s.executeQuery();
            if(results.next())
                count=results.getLong(1);
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

        return count;
    }

    /**
     * Retrieve a set from the Newsletter View Count data source
     * matching on newsletterId. 
     *
     * @param newsletterId Value to match on Newsletter Id.
     * @param index Search results should start from this item.
     * @param limit Search results should return at most this many items.
     */
    public List<NewsletterViewCount> getByNewsletterId(Long newsletterId,  long index, long limit) throws IOException {
        List<NewsletterViewCount> list=new ArrayList<NewsletterViewCount>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        NewsletterViewCount item=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select id, newsletter_id, views "+
                "from newsletter_view_count " +
                "where newsletter_id=? " +
                "order by id " +
                "limit "+index+","+limit
                );
            s.setLong(1,newsletterId);
            results=s.executeQuery();
            while(results.next()) {
                item=new NewsletterViewCount();
                item.setId(results.getLong(1));
                item.setNewsletterId(results.getLong(2));
                item.setViews(results.getLong(3));
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

    /**
     * Count number of items in the <i>Newsletter View Count</i> data source
     * matching on Newsletter Id. 
     *
     * @param newsletterId Value to match on Newsletter Id.
     */
    public long countByNewsletterId(Long newsletterId) throws IOException {
        long total=0;
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select count(*)"+
                "from newsletter_view_count " +
                "where newsletter_id=? " +
                "");
            s.setLong(1,newsletterId);
            results=s.executeQuery();
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

    /**
     * Delete of item(s) in the Newsletter View Count data source
     * matching on Newsletter Id. 
     *
     * @param NewsletterId Value to match on Newsletter Id.
     */
    public long deleteByNewsletterId(Long newsletterId) throws IOException {
        long total=0;
        Connection c=null;
        PreparedStatement s=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "delete from newsletter_view_count " +
                "where newsletter_id=? " +
                "");
            s.setLong(1,newsletterId);
            s.executeUpdate();
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

        return total;
    }

    /**
     * Retrieve a set from the Newsletter View Count data source
     * matching on views. 
     *
     * @param views Value to match on Views.
     * @param index Search results should start from this item.
     * @param limit Search results should return at most this many items.
     */
    public List<NewsletterViewCount> getByViews(Long views,  long index, long limit) throws IOException {
        List<NewsletterViewCount> list=new ArrayList<NewsletterViewCount>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        NewsletterViewCount item=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select id, newsletter_id, views "+
                "from newsletter_view_count " +
                "where views=? " +
                "order by id " +
                "limit "+index+","+limit
                );
            s.setLong(1,views);
            results=s.executeQuery();
            while(results.next()) {
                item=new NewsletterViewCount();
                item.setId(results.getLong(1));
                item.setNewsletterId(results.getLong(2));
                item.setViews(results.getLong(3));
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

    /**
     * Count number of items in the <i>Newsletter View Count</i> data source
     * matching on Views. 
     *
     * @param views Value to match on Views.
     */
    public long countByViews(Long views) throws IOException {
        long total=0;
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select count(*)"+
                "from newsletter_view_count " +
                "where views=? " +
                "");
            s.setLong(1,views);
            results=s.executeQuery();
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

    /**
     * Delete of item(s) in the Newsletter View Count data source
     * matching on Views. 
     *
     * @param Views Value to match on Views.
     */
    public long deleteByViews(Long views) throws IOException {
        long total=0;
        Connection c=null;
        PreparedStatement s=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "delete from newsletter_view_count " +
                "where views=? " +
                "");
            s.setLong(1,views);
            s.executeUpdate();
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

        return total;
    }

    /**
     * Retrieve a set from the Newsletter View Count data source
     * matching on newsletterId views. 
     *
     * @param newsletterId Value to match on Newsletter Id.
     * @param views Value to match on Views.
     * @param index Search results should start from this item.
     * @param limit Search results should return at most this many items.
     */
    public List<NewsletterViewCount> getByNewsletterIdViews(Long newsletterId, Long views,  long index, long limit) throws IOException {
        List<NewsletterViewCount> list=new ArrayList<NewsletterViewCount>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        NewsletterViewCount item=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select id, newsletter_id, views "+
                "from newsletter_view_count " +
                "where newsletter_id=? and views=? " +
                "order by id " +
                "limit "+index+","+limit
                );
            s.setLong(1,newsletterId);
            s.setLong(2,views);
            results=s.executeQuery();
            while(results.next()) {
                item=new NewsletterViewCount();
                item.setId(results.getLong(1));
                item.setNewsletterId(results.getLong(2));
                item.setViews(results.getLong(3));
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

    /**
     * Count number of items in the <i>Newsletter View Count</i> data source
     * matching on Newsletter Id Views. 
     *
     * @param newsletterId Value to match on Newsletter Id.
     * @param views Value to match on Views.
     */
    public long countByNewsletterIdViews(Long newsletterId, Long views) throws IOException {
        long total=0;
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select count(*)"+
                "from newsletter_view_count " +
                "where newsletter_id=? and views=? " +
                "");
            s.setLong(1,newsletterId);
            s.setLong(2,views);
            results=s.executeQuery();
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

    /**
     * Delete of item(s) in the Newsletter View Count data source
     * matching on Newsletter Id Views. 
     *
     * @param NewsletterId Value to match on Newsletter Id.
     * @param Views Value to match on Views.
     */
    public long deleteByNewsletterIdViews(Long newsletterId, Long views) throws IOException {
        long total=0;
        Connection c=null;
        PreparedStatement s=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "delete from newsletter_view_count " +
                "where newsletter_id=? and views=? " +
                "");
            s.setLong(1,newsletterId);
            s.setLong(2,views);
            s.executeUpdate();
            s.close();
            s=null;
            c.close();
            c=null;
        } catch(SQLException e) {
            if(s!=null) { try { s.close(); } catch(Exception f){} }
            if(c!=null) { try { c.close(); } catch(Exception f){} }
            throw new IOException(e.toString());
        }

        return total;
    }


}
