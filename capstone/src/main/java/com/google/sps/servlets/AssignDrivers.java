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

public class AssignDrivers {
  public void updateAssignments() {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    String userEmail = (String)user.getEmail();
    //finds all riders that currently don't have a driver but are still in the system
    Filter findEmpty = new FilterPredicate("email", FilterOperator.EQUAL, "");
    Query emptyRiderQuery = new Query("Riders").setFilter(findEmpty);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery emptyRiders = datastore.prepare(emptyRiderQuery);
    Integer counter = 0;
    for (Entity riderEntity : emptyRiders.asIterable()) {
      String riderName = (String)riderEntity.getProperty("rider");
      String riderDay = (String)riderEntity.getProperty("day");
      String riderEmail = (String)riderEntity.getProperty("email");
      String riderAddress = (String)riderEntity.getProperty("address");
      Rider rider = new Rider(riderName, riderDay, riderEmail,riderAddress);

      //query drivers available on the same day as each of the empty riders
      Filter sameDay = new FilterPredicate("day", FilterOperator.EQUAL, riderDay);
      Query sameDayQuery = new Query("Driver").setFilter(sameDay);
      DatastoreService datastore1 = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery sameDate = datastore1.prepare(sameDayQuery);
      for (Entity driverEntity : sameDate.asIterable()) {
          String first = (String)driverEntity.getProperty("first");
          String day = (String)driverEntity.getProperty("day");
          String times = (String)driverEntity.getProperty("times");
          Long seats = (long)driverEntity.getProperty("seats");
          String email = (String)driverEntity.getProperty("email");
          String license = (String)driverEntity.getProperty("license");
          String pollingAddress = (String)driverEntity.getProperty("pollingAddress");
          riderEntity.setProperty("email", email);
          datastore.put(riderEntity);
      }
    }
  }
}
