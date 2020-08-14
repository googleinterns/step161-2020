package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//servlet responsible for deleting a driver
@WebServlet("/delete-driver")
public class DeleteDriverServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Long driverId_temp = Long.parseLong(request.getParameter("driverId"));
    Filter propertyFilter = new FilterPredicate("id", FilterOperator.EQUAL, driverId_temp);
    Query query = new Query("Driver").setFilter(propertyFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    ArrayList<Driver> drivers = new ArrayList<>();
    for (Entity entity : results.asIterable()
      String first= (String) entity.getProperty("first");
      String day = (String) entity.getProperty("day");
      String times = (String) entity.getProperty("times");
      Long seats = (Long)entity.getProperty("seats");
      Long id = (Long)entity.getProperty("id");
      Driver driver = new Driver(first,day,times,seats,id);
      Key driverKey = entity.getKey();
      datastore.delete(driverKey);
    }
    Gson gson = new Gson(); 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson("{}"));
  }
}