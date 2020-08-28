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

@WebServlet("/debug-data")
public class DebugServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //example car #1
    makeDriver("Driver Ex1","2020-10-16","morning",4L,
    "driverEx1@example.com",
    "ABCD123","100 2nd Street, Austin, TX 78701","2206 Haskell St, Austin, TX 78702");
    makeRider("Rider A.1", "2020-10-16", "", 
    "2001 E Martin Luther King Jr Blvd, Austin, TX 78702");
    makeRider("Rider B.1", "2020-10-16", "", "1800 Congress Ave, Austin, TX 78701");
    makeRider("Rider C.1", "2020-10-16", "", "419 Congress Ave, Austin, TX 78701");
    
    //example car #2
    makeDriver("Driver Ex2","2020-11-16","morning",3L,"driverEx2@example.com",
    "EFGH123","100 2nd Street, Austin, TX 78701","2206 Haskell St, Austin, TX 78702");
    makeRider("Rider A.2", "2020-11-16", "", "801 S Lamar Blvd, Austin, TX 78704");
    makeRider("Rider B.2", "2020-11-16", "", "1201 S Lamar Blvd, Austin, TX 78704");
    makeRider("Rider C.2", "2020-11-16", "",
     "1700 S Lamar Blvd Suite 301, Austin,TX 78704");
   
    System.out.println("");
    System.out.println("");
    System.out.println("");
    System.out.println("=============Added example car #1  and example car #2=============");
    System.out.println("");
    System.out.println("");
    System.out.println("");
    AssignDrivers a = new AssignDrivers();
    a.updateAssignments(); 
  }
  public static void makeDriver(String first, String day, String times, Long seats, 
  String email, String license, String pollingAddress, String address){
    Entity driverEntity = new Entity("Driver");
    driverEntity.setProperty("first", first);
    driverEntity.setProperty("day", day);
    driverEntity.setProperty("times", times);
    driverEntity.setProperty("seats", seats);
    driverEntity.setProperty("email", email);
    driverEntity.setProperty("license", license);
    driverEntity.setProperty("pollingAddress", pollingAddress);
    driverEntity.setProperty("address", address);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(driverEntity);
  }
  public static void makeRider(String text,String day,String email,String address){
    Entity commentEntity = new Entity("Riders");
    commentEntity.setProperty("rider", text);
    commentEntity.setProperty("day", day);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("address", address);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }
}
