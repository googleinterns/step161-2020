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

public class DuplicateCheck {
  public boolean isRiderDuplicate() {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    String userEmail = (String)user.getEmail();
    Filter exists = new FilterPredicate("rider", FilterOperator.EQUAL,userEmail);
    Query alreadyExists = new Query("Riders").setFilter(exists);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(alreadyExists);
    for (Entity riderEntity : results.asIterable()) {
      return true;
    }
    return false;
  }
  public boolean isDriverDuplicate() {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    String userEmail = (String)user.getEmail();
    Filter exists = new FilterPredicate("email", FilterOperator.EQUAL,userEmail);
    Query alreadyExists = new Query("Driver").setFilter(exists);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(alreadyExists);
    for (Entity driverEntity : results.asIterable()) {
      return true;
    }
    return false;
  }
}
