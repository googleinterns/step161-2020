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
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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


//servlet responsible for showing rider dashboard
@WebServlet("/rider-dashboard")
public class RiderDashboardServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Long requestedId = Long.parseLong(request.getParameter("driverId"));
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not logged in");
      return;
    }
    String userEmail = (String)user.getEmail();
    Filter propertyFilter = new FilterPredicate("rider", FilterOperator.EQUAL, userEmail);
    ArrayList<Driver> drivers = new ArrayList<>();


    Query query = new Query("Riders").setFilter(propertyFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String name = (String)entity.getProperty("rider");
      String day = (String)entity.getProperty("day");
      String email = (String)entity.getProperty("email");
      String address = (String)entity.getProperty("address");
      Filter driverFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
      Query driver_query = new Query("Driver").setFilter(driverFilter);
      PreparedQuery driver_results = datastore.prepare(driver_query);
      for (Entity entityd : driver_results.asIterable()) {
        String first= (String) entityd.getProperty("first");
        String driver_day = (String) entityd.getProperty("day");
        String times = (String) entityd.getProperty("times");
        Long seats = (Long)entityd.getProperty("seats");
        String driver_email = (String)entityd.getProperty("email");
        String license = (String)entityd.getProperty("license"); 
        String pollingAddress = (String)entityd.getProperty("pollingAddress");
        String address1 = (String)entityd.getProperty("address");
        drivers.add(new Driver(first,driver_day,times,seats,driver_email,license,pollingAddress,address1)) ;
      }
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(drivers));
  }
}
