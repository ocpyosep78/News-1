
Clearing out import data
------------------------

The following SQL can be used to clear out all traces of articles and
newsletters. Useful for clearing out before a clean data load.

    delete from article;
    delete from article_topic;
    delete from search_index;
    delete from newsletter;
    delete from newsletter_article;


