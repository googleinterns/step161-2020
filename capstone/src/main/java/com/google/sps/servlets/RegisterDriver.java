package com.google.sps.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map;
import java.util.Arrays;
import com.google.sps.data.Driver;
import com.google.sps.data.Rider;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString; 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register-driver")
public class RegisterDriver extends HttpServlet { 
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Driver> drivers = new ArrayList<>();
        Query queryD = new Query("Driver").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery resultsD = datastore.prepare(queryD);

        for (Entity entity : resultsD.asIterable()) {  
            String first = (String)entity.getProperty("first");
            String last = (String)entity.getProperty("last");
            String day = (String)entity.getProperty("day");
            String times = (String)entity.getProperty("times");
            long seats = (long)entity.getProperty("seats");
            long timestamp = (long)entity.getProperty("timestamp");
            Long id = (Long)entity.getProperty("id");
            if (id == null) {
               id = 0L; 
            }
            Driver driver = new Driver(first,last,day,times,seats,timestamp,id);
            drivers.add(driver);
        }
        
        String gson = new Gson().toJson(drivers); 
        response.setContentType("application/json;");
        String both = "{'drivers':" + gson + "}"; 
        JSONObject json = new JSONObject(both);
        response.getWriter().println(json.toString());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String first = getParameter(request, "first", "");
        String last = getParameter(request, "last", "");
        String day = getParameter(request, "day", "");
        String times = getParameter(request, "times", "");
        long seats = Long.valueOf(getParameter(request, "seats", ""));
        long timestamp = System.currentTimeMillis();
        long id = (long)((Math.random() * (Long.MAX_VALUE - 0 + 1) + 0) * -1);
        int seatNum = (int)seats;

        List<Rider> riders = new ArrayList<Rider>();

        Entity driverEntity = new Entity("Driver");
        driverEntity.setProperty("last", last);
        driverEntity.setProperty("first", first);
        driverEntity.setProperty("day", day);
        driverEntity.setProperty("times", times);
        driverEntity.setProperty("seats", seats);
        driverEntity.setProperty("timestamp", timestamp);
        driverEntity.setProperty("id", id);

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