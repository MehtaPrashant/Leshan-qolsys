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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.leshan.server.LwM2mServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Service HTTP REST API calls.
 */
public class SampleServlet extends HttpServlet {

    private static final String FORMAT_PARAM = "format";

    private static final Logger LOG = LoggerFactory.getLogger(SampleServlet.class);

    private static final long TIMEOUT = 5000; // ms

    private static final long serialVersionUID = 1L;

    private final LwM2mServer server = null;

    private final Gson gson = null;

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
    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();

        ArrayList<String> list = new ArrayList<String>();
        list.add("customerId:" + req.getParameter("customerId"));
        list.add("command:" + req.getParameter("command"));
        // get parameters customerId and command
        // verify whether customerId exists or not
        // if exists, then take the command and hit the padmabushan's provided url
        // get status based on that display message with parameters
        // if (req.getPathInfo() == null) {
            try {
                String json = gson.toJson(list);
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_OK);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        // }



    }


}
