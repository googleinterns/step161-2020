// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Gives back an API key pulled from the environment. This is to avoid having
// our API key stored in github.
@WebServlet("/get-api-key")
public class ApiKeyServlet extends HttpServlet {

  private static final String API_KEY_ENV_VAR = "API_KEY";

  private static class ApiKey {
    private final String name;
    private final String key;

    public ApiKey(String name, String key) {
      this.name = name;
      this.key = key;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter writer = response.getWriter();
    String apiKey = System.getenv().get(API_KEY_ENV_VAR);
    if (apiKey == null) {
      response.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing API_KEY Configuration.");
      return;
    }
    ApiKey result = new ApiKey("default", apiKey);
    writer.println((new Gson()).toJson(result));
  }
}
