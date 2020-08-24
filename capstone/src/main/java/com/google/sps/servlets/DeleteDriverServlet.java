package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Driver;
import com.google.sps.data.Rider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//servlet responsible for deleting a driver
@WebServlet("/delete-driver")
public class DeleteDriverServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    String userEmail = (String)user.getEmail();
    //deletes driver based on the Users email
    Filter propertyFilter = new FilterPredicate("email", FilterOperator.EQUAL, userEmail);
    Query query = new Query("Driver").setFilter(propertyFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    ArrayList<Driver> drivers = new ArrayList<>();
    if (user == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not logged in");
      return;
    }
    for (Entity entity : results.asIterable()) {
      String first= (String) entity.getProperty("first");
      String day = (String) entity.getProperty("day");
      String times = (String) entity.getProperty("times");
      Long seats = (Long)entity.getProperty("seats");
      String email = (String)entity.getProperty("email");
      Driver driver = new Driver(first,day,times,seats,email,null);
      Key driverKey = entity.getKey();
      datastore.delete(driverKey);
    }

    //deletes all riders asociated with that driver
    Filter riderFilter = new FilterPredicate("email", FilterOperator.EQUAL, userEmail);
    Query query1 = new Query("Riders").setFilter(riderFilter);
    DatastoreService datastore1 = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results1 = datastore1.prepare(query1);
    ArrayList<Rider> riders = new ArrayList<>();
    for (Entity entity : results1.asIterable()) {
      String name = (String) entity.getProperty("rider");
      String day = (String) entity.getProperty("day");
      String email = (String) entity.getProperty("email");
      Rider rider = new Rider(name, day, email, null);
      entity.setProperty("email", "");
      datastore1.put(entity);
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson("{}"));
    //not sure how to send a redirect after Iprint the json?
    response.sendRedirect("/index.html");
  }
}
