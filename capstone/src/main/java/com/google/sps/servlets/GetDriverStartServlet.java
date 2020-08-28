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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

//servlet responsible for gathering addresses asociated with a driver
@WebServlet("/get-start")
public class GetDriverStartServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not logged in");
      return;
    }
    String userEmail = (String)user.getEmail();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    //gets the address of the driver
    ArrayList<String> driver_address = new ArrayList<>();
    Filter driverFilter = new FilterPredicate("email", FilterOperator.EQUAL, userEmail);
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
      String address = (String)entityd.getProperty("address");
      driver_address.add(address);
    }
    
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(driver_address));
  }
}
