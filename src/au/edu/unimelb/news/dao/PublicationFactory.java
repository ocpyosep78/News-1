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

public class PublicationFactory {

    private DataSource dataSource=null;

    public PublicationFactory(DataSource dataSource) {
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
				"create table if not exists publication (" +
				"id bigint auto_increment primary key,"+
                "name varchar(250)"+
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
     * Retrieve an object from the <i>Publication</i> data source. 
     */
    public Publication get(long id) throws IOException {
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        Publication item=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select id,name "+
                "from publication " +
                "where id=?");
            s.setLong(1,id);
            results=s.executeQuery();
            if(results.next()) {
                item=new Publication();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
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
     * Retrieve a list of objects from the <i>Publication</i>
     * data source. 
     */
    public List<Publication> getAll(long index, long limit) throws IOException {
        List<Publication> list=new ArrayList<Publication>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select id, name "+
                "from publication " +
                "order by name " +
                "limit "+index+","+limit
                );
            results=s.executeQuery();
            while(results.next()) {
                Publication item=new Publication();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
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
     * Permanently remove an object from the <i>Publication</i>
     * data source. 
     */
    public void delete(long id) throws IOException {
        Connection c=null;
        PreparedStatement s=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "delete from publication "+
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
     * Permanently remove all objects from the <i>Publication</i>
     * data source. 
     */
    public void deleteAll() throws IOException {
        Connection c=null;
        PreparedStatement s=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement("delete from publication"); 
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
     * Add a new <i>Publication</i> object to the data source. 
     */
    public Publication insert(Publication item) throws IOException {
        Connection c=null;
        PreparedStatement s=null;

        try {
            c=dataSource.getConnection();

            s=c.prepareStatement(
                "insert into publication ("+
                    "name) "+
                "values(?)");
            s.setString(1,item.getName());
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
            System.err.println("Problem duing inserting into table publication. "+
                "name="+item.getName()+", "+ 
        "");
            throw new IOException(e.toString());
        }

        return item;
    }

    /**
     * Update an object of type <i>Publication</i> the 
     * data source. 
     */
    public void update(Publication item) throws IOException {
        Connection c=null;
        PreparedStatement s=null;

        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "update publication set name=? "+
                "where id=?");
            s.setString(1,item.getName());
            s.setLong(2,item.getId());
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
     * Count the number of objects from the <i>Publication</i>
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
                "from publication "
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
     * Retrieve a set from the Publication data source
     * matching on name. 
     *
     * @param name Value to match on Name.
     * @param index Search results should start from this item.
     * @param limit Search results should return at most this many items.
     */
    public List<Publication> getByName(String name,  long index, long limit) throws IOException {
        List<Publication> list=new ArrayList<Publication>();
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        Publication item=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select id, name "+
                "from publication " +
                "where name=? " +
                "order by name " +
                "limit "+index+","+limit
                );
            s.setString(1,name);
            results=s.executeQuery();
            while(results.next()) {
                item=new Publication();
                item.setId(results.getLong(1));
                item.setName(results.getString(2));
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
     * Count number of items in the <i>Publication</i> data source
     * matching on Name. 
     *
     * @param name Value to match on Name.
     */
    public long countByName(String name) throws IOException {
        long total=0;
        Connection c=null;
        PreparedStatement s=null;
        ResultSet results=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "select count(*)"+
                "from publication " +
                "where name=? " +
                "");
            s.setString(1,name);
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
     * Delete of item(s) in the Publication data source
     * matching on Name. 
     *
     * @param Name Value to match on Name.
     */
    public long deleteByName(String name) throws IOException {
        long total=0;
        Connection c=null;
        PreparedStatement s=null;
        try {
            c=dataSource.getConnection();
            s=c.prepareStatement(
                "delete from publication " +
                "where name=? " +
                "");
            s.setString(1,name);
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
