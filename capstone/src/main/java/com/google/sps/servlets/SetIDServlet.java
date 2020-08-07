package com.google.sps.servlets;

import com.google.sps.data.Rider;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//servlet responsible for changing the DriverId of a specifc rider
@WebServlet("/setRiderId")
public class SetIDServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long driverId_temp = Long.parseLong(request.getParameter("driverId"));
        String riderName = request.getParameter("riderName");
        Query query = new Query("Riders");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<Rider> riders = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            String name = (String) entity.getProperty("rider");
            String day = (String) entity.getProperty("day");
            Long driverId = (Long)entity.getProperty("driverId");
            Rider rider = new Rider(name, day, driverId);
            if (name.equals(riderName) == true) {  //finds rider and changes id 
                entity.setProperty("driverId", driverId_temp);
                datastore.put(entity);
                break;
            }
        }
        Gson gson = new Gson(); 
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson("{'success': true}"));
    }
}