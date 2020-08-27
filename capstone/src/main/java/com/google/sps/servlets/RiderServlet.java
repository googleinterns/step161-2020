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
import com.google.sps.data.Rider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//servlet responsible for creating riders
@WebServlet("/rider")
public class RiderServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<Rider> riders = new ArrayList<>();
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not logged in");
      return;
    }
    DuplicateCheck d = new DuplicateCheck();
    boolean duplicate = d.isRiderDuplicate();
    if (duplicate == true) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are already registerd as a rider");
      return;  
    }
    Query query = new Query("Riders");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String name = (String) entity.getProperty("rider");
      String day = (String) entity.getProperty("day");
      String email = (String)entity.getProperty("email");
      String address = (String)entity.getProperty("address");
      Rider rider = new Rider(name, day, email, address);
      riders.add(rider);
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(riders));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not logged in");
      return;
    }
    DuplicateCheck b = new DuplicateCheck();
    boolean dup = b.isRiderDuplicate();
    if (dup == true) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You already exist in the system");
      return;  
    }
    String userEmail = user.getEmail();
    String text = userEmail;
    String day = getParameter(request, "day-input", "");
    String email = "";
    String address = getParameter(request, "address", "");
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Entity commentEntity = new Entity("Riders");
    commentEntity.setProperty("rider", text);
    commentEntity.setProperty("day", day);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("address", address);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.setContentType("text/html;");
    response.getWriter().println(text);
    response.sendRedirect("/riderDashboard.html");
    AssignDrivers a = new AssignDrivers();
    a.updateAssignments();
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
