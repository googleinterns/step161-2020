package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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
import java.util.*;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/register-driver")
public class RegisterDriver extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Driver> drivers = new ArrayList<>();
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    String userEmail = user.getEmail();
    if (user == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not logged in");
      return;
    }
    DuplicateCheck d = new DuplicateCheck();
    boolean duplicate = d.isDriverDuplicate();
    if (duplicate) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are already registerd as a driver");
      return;  
    }
    Query queryD = new Query("Driver");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery resultsD = datastore.prepare(queryD);
    for (Entity entity : resultsD.asIterable()) {
      String first = (String)entity.getProperty("first");
      String day = (String)entity.getProperty("day");
      String times = (String)entity.getProperty("times");
      Long seats = (long)entity.getProperty("seats");
      String email = (String)entity.getProperty("email");
      String license = (String)entity.getProperty("license");
      String pollingAddress = (String)entity.getProperty("pollingAddress");
      if (email == null) {
        email = userEmail; 
      }
      Driver driver = new Driver(first,day,times,seats,email,license,pollingAddress);
      drivers.add(driver);
    }
    String gson = new Gson().toJson(drivers);
    response.setContentType("application/json;");
    String both = "{'drivers':" + gson + "}";
    JSONObject json = new JSONObject(both);
    response.getWriter().println(json.toString());
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    String userEmail = user.getEmail();
    DuplicateCheck b = new DuplicateCheck();
    boolean dup = b.isDriverDuplicate();
    if (dup) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are already registerd as a driver");
      return;  
    }
    String first = getParameter(request, "first", "");
    String license = getParameter(request, "license", "");
    String day = getParameter(request, "day", "");
    String times = getParameter(request, "times", "");
    String pollingAddress = getParameter(request, "pollingAddress", "");
    long seats = Long.valueOf(getParameter(request, "seats", ""));
    String email = userEmail;
    int seatNum = (int)seats;
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    List<Rider> riders = new ArrayList<Rider>();

    Entity driverEntity = new Entity("Driver");
    driverEntity.setProperty("first", first);
    driverEntity.setProperty("day", day);
    driverEntity.setProperty("times", times);
    driverEntity.setProperty("seats", seats);
    driverEntity.setProperty("email", email);
    driverEntity.setProperty("license", license);
    driverEntity.setProperty("pollingAddress", pollingAddress);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(driverEntity);
    response.sendRedirect("/driverDashboard.html");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
