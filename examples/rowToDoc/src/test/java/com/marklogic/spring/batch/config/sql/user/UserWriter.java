package com.marklogic.spring.batch.config.sql.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.item.AbstractDocumentWriter;
import com.marklogic.uri.XmlStringUriGenerator;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import java.util.*;

/**
 * Writer class that is specific to the "ReadFromHsqlTest" test. It shows an example of how to deal with a SQL statement
 * that does a left join on users and comments. We'll get a row per comment, which means we need a way to merge all
 * those rows for a single user together into a single user document. This class shows a technique for doing that,
 * assuming that the rows are ordered by the user ID.
 */
public class UserWriter extends AbstractDocumentWriter implements ItemWriter<User>, ItemStream {

    private XMLDocumentManager mgr;
    private XmlMapper xmlMapper;
    private XmlStringUriGenerator uriGenerator;

    // Keeps track of users between calls to the write method
    private Map<Integer, User> userMap = new HashMap<>();

    public UserWriter(DatabaseClient client) {
        this.mgr = client.newXMLDocumentManager();
        this.xmlMapper = new XmlMapper();
        xmlMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        uriGenerator = new XmlStringUriGenerator();
    }

    /**
     * For each incoming User, we check to see if it's in the map already. If it is, we add a comment to it if one
     * exists. Otherwise, we add it to the map.
     * 
     * Then, for every user already in the map that we didn't modify, we write those to MarkLogic.
     */
    @Override
    public void write(List<? extends User> items) throws Exception {
        logger.debug("Checking for users to write, size of list: " + items.size());
        Set<Integer> userIdsToIgnore = new HashSet<>();
        for (User user : items) {
            int id = user.getId();
            userIdsToIgnore.add(id);
            if (userMap.containsKey(id) && !user.getComments().isEmpty()) {
                userMap.get(id).getComments().addAll(user.getComments());
            } else {
                userMap.put(id, user);
            }
        }

        writeUsers(userIdsToIgnore);
    }

    private void writeUsers(Set<Integer> userIdsToIgnore) {
        DocumentWriteSet set = mgr.newWriteSet();
        Set<Integer> keys = userMap.keySet();
        Set<Integer> keysToRemove = new HashSet<>();
        for (int id : keys) {
            if (userIdsToIgnore == null || !userIdsToIgnore.contains(id)) {
                User user = userMap.get(id);
                try {
                    String xml = xmlMapper.writeValueAsString(user);
                    String uri = uriGenerator.generateUri(xml, id + "");
                    logger.debug("Adding user to the set of documents to write: " + id);
                    set.add(uri, buildMetadata(), new StringHandle(xml));
                    keysToRemove.add(id);
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                logger.debug("Not writing user because it was either just added to the map or modified: " + id);
            }
        }

        if (!set.isEmpty()) {
            logger.debug("Writing set of documents");
            mgr.write(set);
            logger.debug("Finished writing set of documents");
        }

        keys.removeAll(keysToRemove);
    }

    /**
     * This close method from ItemStream gives us a way to write all the remaining user records in our map after all the
     * users have been read from the SQL database.
     */
    @Override
    public void close() throws ItemStreamException {
        logger.info("Closing UserWriter, and writing remaining user records");
        writeUsers(null);
    }

}
