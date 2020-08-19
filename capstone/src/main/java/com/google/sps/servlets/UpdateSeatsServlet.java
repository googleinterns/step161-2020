package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
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
    Filter propertyFilter =
        new FilterPredicate("email", FilterOperator.EQUAL, driverId);
    Query q = new Query("Driver").setFilter(propertyFilter);
    List<Driver> drivers = new ArrayList<>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(q);
    Long newSeats = 0L;
    Driver res_driver = new Driver("","","",0L,"");
    //always loops once
    for (Entity entity : results.asIterable()) {  
      String first = (String)entity.getProperty("first");
      String day = (String)entity.getProperty("day");
      String times = (String)entity.getProperty("times");
      Long seats = (Long)entity.getProperty("seats");
      String email = (String)entity.getProperty("email");
      System.out.println(first + " has " + seats + " seats available");
      Driver driver = new Driver(first,day,times,seats,email);
      if (seats > 0) {
        res_driver = driver;
        newSeats = seats - 1;
        entity.setProperty("seats", newSeats);
        datastore.put(entity);
        break;
      }
    }
    if (res_driver == null) {
      response.sendError(404,"No queries availble");
      return;
    }
    Gson gson = new Gson(); 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(res_driver));
  }
}