package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Driver;
import com.google.sps.data.Rider;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString; 

// servlet responsible for updating seats once a rider adds themselves to a car
@WebServlet("/update-seats")
public class UpdateSeatsServlet extends HttpServlet { 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Long driverId = Long.parseLong(request.getParameter("driverId"));
    Query query = new Query("Driver");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Driver> drivers = new ArrayList<>();
    Long newSeats = 0L;
    for (Entity entity : results.asIterable()) {  
      String first = (String)entity.getProperty("first");
      String last = (String)entity.getProperty("last");
      String day = (String)entity.getProperty("day");
      String times = (String)entity.getProperty("times");
      Long seats = (Long)entity.getProperty("seats");
      long timestamp = (long)entity.getProperty("timestamp");
      Long id = (Long)entity.getProperty("id");
      Driver driver = new Driver(first,last,day,times,seats,timestamp,id);
      if (id.equals(driverId) == true) {
        if (seats > 0) {
            newSeats = seats - 1;
            entity.setProperty("seats", newSeats);
            datastore.put(entity);
            break;
        }
      }
    }
    Gson gson = new Gson(); 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(newSeats));
  }
}