// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.sps.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Driver;
import java.util.Arrays;
import java.util.*;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register-driver")
public class RegisterDriver extends HttpServlet { 
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        List<Driver> drivers = new ArrayList<>();
        Query query = new Query("Driver").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {  
            String first = (String)entity.getProperty("first");
            String last = (String)entity.getProperty("last");
            String day = (String)entity.getProperty("day");
            String times = (String)entity.getProperty("times");
            long seats = (long)entity.getProperty("seats");
            long timestamp = (long)entity.getProperty("timestamp");
            Driver driver = new Driver(first,last,day,times,seats,timestamp);
            drivers.add(driver);
        }
        
        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(drivers));
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = getParameter(request, "title", "");
        String first = getParameter(request, "first", "");
        String last = getParameter(request, "last", "");
        String day = getParameter(request, "day", "");
        String times = getParameter(request, "times", "");
        long seats = Long.valueOf(getParameter(request, "seats", ""));
        long timestamp = System.currentTimeMillis();
        
        Entity driverEntity = new Entity("Driver");
        driverEntity.setProperty("title", title);
        driverEntity.setProperty("first", first);
        driverEntity.setProperty("day", day);
        driverEntity.setProperty("times", times);
        driverEntity.setProperty("seats", seats);
        driverEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(driverEntity);
        response.sendRedirect("/index.html");
    }
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}