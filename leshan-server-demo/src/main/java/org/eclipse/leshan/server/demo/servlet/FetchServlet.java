/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.server.demo.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.eclipse.leshan.server.model.ClientDao;
import org.leshan.server.configuration.DataBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Service HTTP REST API calls.
 */
public class FetchServlet extends HttpServlet {


    private static final Logger LOG = LoggerFactory.getLogger(FetchServlet.class);
    private static final long serialVersionUID = 1L;
    private final String mongoDBAdd = DataBaseConfiguration.getInstance().getPropertyString("MONGODB_ADD");
    private int mongoDBPort = DataBaseConfiguration.getInstance().getPropertyInt("MONGODB_PORT");
    private String mongoDBName = DataBaseConfiguration.getInstance().getPropertyString("MONGODB_DataBaseName");

    /*
     * public FetchServlet(LwM2mServer server, int securePort) { this.server = server;
     * 
     * GsonBuilder gsonBuilder = new GsonBuilder(); gsonBuilder.registerTypeHierarchyAdapter(Client.class, new
     * ClientSerializer(securePort)); gsonBuilder.registerTypeHierarchyAdapter(LwM2mResponse.class, new
     * ResponseSerializer()); gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
     * gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeDeserializer());
     * gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"); this.gson = gsonBuilder.create(); }
     */

    /**
     * {@inheritDoc}
     */

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        MongoClient client = new MongoClient(mongoDBAdd, mongoDBPort);
        // MongoClient client = new MongoClient("54.161.178.113", 27017);
        MongoDatabase database = client.getDatabase(mongoDBName);
        MongoCollection<Document> collection = database.getCollection("events");
        Gson gson = new Gson();

        ArrayList<ClientDao> clientDaoList = new ArrayList<ClientDao>();
        if (req.getPathInfo() == null) {
            try {
                MongoCursor<String> mongoCursor = database.getCollection("events").distinct("client_ep", String.class)
                        .iterator();
                while (mongoCursor.hasNext()) {
                    String clientEp = mongoCursor.next();
                    ClientDao clientDao = new ClientDao();
                    clientDao.setClientEP(clientEp);
                    clientDao.setTimestamp(null);
                    clientDaoList.add(clientDao);
                }

                String json = gson.toJson(clientDaoList);
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_OK);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.close();
            }
        }

        if (req.getPathInfo() != null) {
            String[] path = StringUtils.split(req.getPathInfo(), '/');
            if (path.length == 1) {
                try {

                    BasicDBObject query1 = new BasicDBObject();
                    BasicDBObject sort = new BasicDBObject();
                    sort.put("timestamp", -1);

                    query1.put("client_ep", path[0].toString());
                    Iterable<Document> cur = collection.find(query1).sort(sort);
                    Iterator<Document> itr = cur.iterator();
                    while (itr.hasNext()) {

                        Document document = itr.next();
                        ClientDao clientDao = new ClientDao();
                        clientDao.setClientEP(document.getString("client_ep"));
                        clientDao.setEvent(document.getString("event"));
                        clientDao.setTimestamp(document.getString("timestamp"));
                        clientDaoList.add(clientDao);
                    }

                    String json = gson.toJson(clientDaoList);
                    resp.getWriter().write(json.toString());
                    resp.setStatus(HttpServletResponse.SC_OK);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    client.close();
                }

            }

        }

    }

    /**
     * {@inheritDoc}
     */

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        @SuppressWarnings("deprecation")
        Mongo mongo = new Mongo(mongoDBAdd, mongoDBPort);
        @SuppressWarnings("deprecation")
        DB db = mongo.getDB(mongoDBName);

        DBCollection coll = db.getCollection("events");



        if (req.getPathInfo() != null) {
            String[] path = StringUtils.split(req.getPathInfo(), '/');
            if (path.length == 1) {
                BasicDBObject document = new BasicDBObject();
                document.put("client_ep", path[0]);
                coll.remove(document);
                resp.getWriter().write("deleted succesfully" + path[0]);
                resp.setStatus(HttpServletResponse.SC_OK);

            }

            else if (path.length == 2) {
                BasicDBObject document = new BasicDBObject();
                document.put("event", path[1].toString());
                coll.remove(document);
                resp.getWriter().write("deleted succesfully" + path[1]);
                resp.setStatus(HttpServletResponse.SC_OK);

            } else {
                resp.getWriter().write("invalid request" + req.getPathInfo());
                resp.setStatus(HttpServletResponse.SC_OK);
            }

        } else {
            resp.getWriter().write("invalid request" + req.getPathInfo());
            resp.setStatus(HttpServletResponse.SC_OK);
        }

    }
}
