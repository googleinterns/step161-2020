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
    if (user == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not logged in");
      return;
    }
    Query queryD = new Query("Driver");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery resultsD = datastore.prepare(queryD);
    for (Entity entity : resultsD.asIterable()) {
      String first = (String)entity.getProperty("first");
      String day = (String)entity.getProperty("day");
      String times = (String)entity.getProperty("times");
      long seats = (long)entity.getProperty("seats");
      Long id = (Long)entity.getProperty("id");
      if (id == null) {
        id = 0L;
      }
      Driver driver = new Driver(first,day,times,seats,id);
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
    String day = getParameter(request, "day", "");
    String times = getParameter(request, "times", "");
    long seats = Long.valueOf(getParameter(request, "seats", ""));
    long id = (long)(Math.random() * (Long.MAX_VALUE + 1)* -1);
    int seatNum = (int)seats;
    UserService userService = UserServiceFactory.getUserService();

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
    driverEntity.setProperty("id", id);

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
