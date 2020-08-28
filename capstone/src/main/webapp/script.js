// Copyright 2019 Google LLC
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

var map ;

function getLoginInfo() {
  return fetch(civicinfo).then(response => response.json());
}

// Completes basic setup of page after loading.
// - Populates the user status bar if it's present.
async function setupPage() {
  // If this page has a "login-status" element, populate it with login
  // information.
  let status = document.getElementById('login-status');
  if (status) {
    let loginInfo = await fetch('/login?format=json').then(r => r.json());
    console.log('Login info: ' + loginInfo);
    let link = document.getElementById('login-link');
    if (loginInfo.loggedIn) {
      status.innerHTML = 'Logged in as: ' + loginInfo.email;
      link.innerHTML = '<a href="' + loginInfo.logoutLink + '">Log Out</a>';
    } else {
      status.innerHTML = 'Not logged in';
      link.innerHTML = '<a href="' + loginInfo.loginLink + '">Log In</a>';
    }
  }

  // Load the maps API, if desired.
  if (document.getElementsByClassName('maps-api').length > 0) {
    loadMapsApi();
  }
}

var cached_api_key = null;

// Fetches the API key from the server, caching it for subsequent calls.
async function getApiKey() {
  if (cached_api_key) {
    return cached_api_key;
  }
  let info = await fetch('/get-api-key').then(r => r.json());
  console.log('fetched maps API key: ' + info.key);
  cached_api_key = info.key;
  return info.key;
}

// Fetches and returns the API key.
async function loadMapsApi() {
  let key = await getApiKey();
  let head = document.getElementsByTagName('head')[0];
  if (!head) {
    console.log('error loading maps api: no head elements');
    return;
  }
  let script = document.createElement('script');
  script.src = [
    'https://maps.googleapis.com/maps/api/js',
    '?key=' + key,
    '&callback=mapsApiDone',
  ].join('');
  head.appendChild(script);
}

function mapsApiDone() {
  console.log('maps api loaded!');
  // We loaded the API, so enable the lookup button.
  let button = document.getElementById("lookup-button");
  if (!button) {
    console.log('no lookup button found to enable');
    return;
  }
  console.log('enabling lookup button');
  button.disabled = false;
}

async function lookupPollingLocations() {
  let form = document.getElementById("address-form");
  let address = form.elements["address"].value;
  let pollingInfo = await lookupPollingPlace(address);
  console.log('Looked up polling place: ' + JSON.stringify(pollingInfo));
  addAddressesToDom(pollingInfo);

  var coordPromises = [];
  for (let i = 0; i < pollingInfo.pollingLocations.length; i ++) {
    coordPromises.push(getCoord(getAddressFromPollingLocation(pollingInfo.pollingLocations[i])));
  }

  var home = await getCoord(address);
  var coordinates = [];
  for (let i = 0; i < coordPromises.length; i++ ) {
    let result = await coordPromises[i];
    coordinates.push(result.results[0].geometry.location)
  }
  console.log(coordinates);

  var homeLoc = home.results[0].geometry.location;
  console.log(homeLoc);
  initMap(homeLoc);
  map.setCenter({"lat": homeLoc.lat, "lng": homeLoc.lng});
  makeMarkers(coordinates);
  document.getElementById("map").style.display = 'block';
}

//finds polling locations
async function lookupPollingPlace(address) {
  let key = await getApiKey();
  console.log("Looking up address: " + address);
  let civicinfo = [
    'https://civicinfo.googleapis.com/civicinfo/v2/voterinfo?address=',
    encodeURIComponent(address),
    '&electionId=2000',
    '&officialOnly=true',
    '&returnAllAvailableData=true',
    '&key=' + key,
  ].join('');
  return fetch(civicinfo).then(response => response.json());
}

// Appends locations of polling locations to the DOM.
function addAddressesToDom(pollingInfo){
  let placeholder = id("addresses");
  if (!placeholder) {
    return;  // shouldn't happen
  }
  // Clear existing contents.
  while (placeholder.firstChild) {
    placeholder.removeChild(placeholder.firstChild);
  }
  if (!pollingInfo.pollingLocations) {
    placeholder.innerText = "Couldn't find any polling places. Please try again";
    return;
  }
  for (let x = 0; x < pollingInfo.pollingLocations.length; x++) {
    let location = pollingInfo.pollingLocations[x].address;
    let br = document.createElement("br");
    let br1 = document.createElement("br");
    let comment  = document.createElement("p");
    let line1  = document.createElement("p");
    let line2  = document.createElement("p");
    console.log(location);
    comment.innerText = location.locationName;
    line1.innerText = location.line1;
    line2.innerText = location.city + ", " + location.state + " " + location.zip;
    placeholder.appendChild(comment);
    placeholder.appendChild(line1);
    placeholder.appendChild(line2);
    placeholder.appendChild(br);
    placeholder.appendChild(br1);
  }
}

//returns address in a string
function getAddressFromPollingLocation(pollingLocation) {
  return [
    pollingLocation.address.line1,
    pollingLocation.address.line2,
    pollingLocation.address.city,
    pollingLocation.address.state,
    pollingLocation.address.zip
  ].join(' ');
}

//gets latitude and longitude of an address using Geolocation API
async function getCoord(address){
  let key = await getApiKey();
  let url = [
    'https://maps.googleapis.com/maps/api/geocode/json?address=',
    encodeURIComponent(address),
    '&key=' + key,
  ].join('');
  return fetch(url).then(response => response.json());
}

//initialize map
function initMap(center) {
    document.getElementById("map").style.display = 'none';
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 15,
        center:center
    });
}

//makes a list of markers and maps from a list of lattitudes
function makeMarkers(coord){
  let size = coord.length;
  if (coord.length == 0) {
    console.log("Empty coordinates, not able to make Markers");
    return;
  }
  if (size > 10) {
    size = 10;
  }
  for(let i=0; i < size; i++) {
    var marker = new google.maps.Marker({
      position: {lat: coord[i].lat, lng: coord[i].lng},
      map: map,
      title: (String)(i)
    });
    marker.setMap(map);
  }
}

//fetch Riders
function getQuery() {
    return fetch('/driver-dashboard').then(response => response.json());
}

function deleteDr() {
    return fetch('/delete-driver').then(response => response.json());
}

async function deleteDriver() {
    let step1 = deleteDr();
    var a = document.createElement('a');  
    var link = document.createTextNode("return home");
    a.appendChild(link);
    a.title = "return home";
    a.href = "/index.html"; 
    document.getElementById("return-home").appendChild(a);                              
}

//displays all of car's riders
function showRiders(riders) {
    let intro = document.createElement("p");
    intro.innerText = "Riders available in your car: "
    document.getElementById("driver-container").appendChild(intro);
    for (let i = 0; i < riders.length; i++){
        console.log(riders[i]);
        let name = riders[i].rider;
        let comment = document.createElement("p");
        let fav = document.createElement("I");
        comment.innerText = "•" + name;
        document.getElementById("driver-container").appendChild(comment);
    }
}

//TODO
function showDirections(riders) {

}

async function getRiders() {
    let riders = await getQuery();
    console.log(riders);
    showRiders(riders);
}

function riderDashboardStep1() {
    return fetch('/rider-dashboard').then(response => response.json());
}

function showDrivers(drivers) {
    let intro = document.createElement("p");
    intro.innerText = "Your driver information"
    document.getElementById("driver-container").appendChild(intro);
    for (let i = 0; i < drivers.length; i++){
        console.log(drivers[i]);
        let name = drivers[i].first;
        let license = drivers[i].license;
        let comment = document.createElement("p");
        comment.innerText = name + "'s license is " + license;
        document.getElementById("driver-container").appendChild(comment);
    }
}

async function riderDashboard(){
  let drivers = await riderDashboardStep1();
  if (drivers.length == 0) {
    let comment = document.createElement("p");
    comment.innerText = "We were unable to find any drivers available during your time";
    document.getElementById("driver-container").appendChild(comment);
  } else {
    showDrivers(drivers);
  }
  console.log(drivers);
    
}

function deleteRd() {
    return fetch('/delete-rider').then(response => response.json());
}

async function deleteRider() {
    let step1 = deleteRd();
    var a = document.createElement('a');  
    var link = document.createTextNode("return home");
    a.appendChild(link);
    a.title = "return home";
    a.href = "/index.html"; 
    document.getElementById("return-home").appendChild(a);                              
}

function getAdd(){
    return fetch('/get-addresses').then(response => response.json());
}

//shows address of the driver and the corresponding riders
async function getDirections() {
  let addresses = await getAdd();
  console.log(addresses);
  let start = '4370+Chase+Pl.+Las+Cruces+NM';
  let end = '1755+El+Paseo+Rd+Las+Cruces+NM';
  console.log(points);
  initDir(start);
  calcRoute(start, end, addresses);
}

var directionsRender;
var directionsService;

function initDir(center) {
  directionsService = new google.maps.DirectionsService();
  directionsRenderer = new google.maps.DirectionsRenderer();
  var mapOptions = {
    zoom:10,
    center: center
  }
  var map = new google.maps.Map(id('map'), mapOptions);
  directionsRenderer.setMap(map);
}

function calcRoute(start, end, points){
  let stops = [];
  for(let x = 0; x < points.length; x++) {
    stops.push({location: points[x], stopover:true});
  }
  var request = {
    origin: start,
    destination: end,
    waypoints: stops,
    optimizeWaypoints: true,
    travelMode: 'DRIVING'
  };
  directionsService.route(request, function(result, status) {
    if (status == 'OK') {
      directionsRenderer.setDirections(result);
    }
  });
}

//helper method
function id(thing) {
    return document.getElementById(thing);
}
