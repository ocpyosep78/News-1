
Installing
==========

Installation involves checking out the source code, adding your custom settings, then deploying the application.

Checking out the source code
----------------------------

Assuming you have git installed, simply type the following

    git clone git://github.com/dongyage/News.git

Adding custom settings
----------------------

You must create a `build.properties` file and add your own settings, this can be done by copying the template properties file:

    cp build.properties.templ build.properties

Read the properties file to see what settings you will need to fill out. This includes the database details, and the username/password
for deploying to tomcat.

Deploying the application
-------------------------

This assumes you have at least Java 1.6 installed, and a recent version of the ant utility. From the command line simply type

    ant deploy-production

What this does is create a war called `Policy.war` with the custom database settings and then uploads it to the designated tomcat server. If you
prefer to only create a war file then upload it manually type the following:

    ant war-production


Clearing out import data
========================

The following SQL can be used to clear out all traces of articles and
newsletters. Useful for clearing out before a clean data load.

    delete from article;
    delete from article_topic;
    delete from search_index;
    delete from newsletter;
    delete from newsletter_article;


