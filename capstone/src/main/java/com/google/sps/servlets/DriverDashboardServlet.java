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

// servlet responsible for querying riders of a certain id
@WebServlet("/driver-dashboard")
public class DriverDashboardServlet extends HttpServlet { 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Long requestedId = Long.parseLong(request.getParameter("driverId"));
    Filter propertyFilter = new FilterPredicate("driverId", FilterOperator.EQUAL, requestedId);
    Query query = new Query("Riders").setFilter(propertyFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    ArrayList<Rider> riders = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String name = (String)entity.getProperty("rider");
      String day = (String)entity.getProperty("day");
      Long driverId = (Long)entity.getProperty("driverId");
      Rider rider = new Rider(name, day, driverId);
      riders.add(rider);
    }
    Gson gson = new Gson(); 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(riders));
  }
}