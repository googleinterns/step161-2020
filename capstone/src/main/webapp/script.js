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

var latmap = 0;
var lngmap = 0;
var address = '';
let addresses = [];
var coordinates = [];
var places = [];
var homeLat = 0;
var homeLng = 0;

function getData() {
    //fetches address from the form and displays nearby polling location
    let form = document.getElementById("my-form");
    address = document.getElementById("my-form").elements["address"];
    fetch('https://civicinfo.googleapis.com/civicinfo/v2/voterinfo?address=' + encodeURIComponent(address.value) +'&electionId=2000&officialOnly=true&returnAllAvailableData=true&key=AIzaSyClf-1yO8u6fBpnDyI9u_WTQZX4gYkbkWs').then(response => response.json()).then((quote) => {
        let address = document.getElementById("random");
        address.innerHTML = "";
        if (quote.pollingLocations === undefined) {
            let comment  = document.createElement("p");
            comment.innerText = "No polling locations in the same zipcode as your address";
            document.getElementById("random").appendChild(comment);
        } else {
            getHomeCoord(address.value);
            addAddresses(quote);
            buildCoordinates(addresses);
            console.log(coordinates);
            initMap();
            makePlaces(coordinates);
            console.log(places);
            initMap();
        }
     });

}

function addAddresses(pollingInfo) {
    let size = pollingInfo.pollingLocations.length;
    if (size > 10) {
        size = 10;
    }
    for(let i=0; i < size; i++) {
        let br = document.createElement("br");
        let br1 = document.createElement("br");
        let comment  = document.createElement("p");
        let line1  = document.createElement("p");
        let line2  = document.createElement("p");
        comment.innerText = pollingInfo.pollingLocations[i].address.locationName;
        line1.innerText = pollingInfo.pollingLocations[i].address.line1;
        line2.innerText = pollingInfo.pollingLocations[i].address.city +", " + pollingInfo.pollingLocations[i].address.state + " " + pollingInfo.pollingLocations[0].address.zip;
        document.getElementById("random").appendChild(comment);
        document.getElementById("random").appendChild(line1);
        document.getElementById("random").appendChild(line2);
        document.getElementById("random").appendChild(br);
        document.getElementById("random").appendChild(br1);
        addresses.push(pollingInfo.pollingLocations[i].address.line1 + " " + pollingInfo.pollingLocations[i].address.city +", " + pollingInfo.pollingLocations[i].address.state + " " + pollingInfo.pollingLocations[0].address.zip);
    }    
}

//notes: build our lat long dictionary of all address
//fetches the lat and long of the individuals gps so we can feed to the maps API
function getHomeCoord(add){ 
    getCoord('https://maps.googleapis.com/maps/api/geocode/json?address=' + encodeURIComponent(add) + '&key=AIzaSyAZwerlkm0gx8mVP0zpfQqeJZM3zGUUPiM');
}

//notes: build our lat long dictionary of all address
//fetches the lat and long of the individuals gps so we can feed to the maps API
function getCoord(url){ 
    fetch(url).then(response => response.json()).then((geo) => {
        if (geo.results.length == 0) {
            console.log("Empty result");
            return;
        }
        latmap = geo.results[0].geometry.location.lat;
        lngmap = geo.results[0].geometry.location.lng;
        coordinates.push({lat: latmap, lng: lngmap});
    });
}

function buildCoordinates(adds) {
    for(let i=0; i < adds.length; i++) {
        getCoord('https://maps.googleapis.com/maps/api/geocode/json?address=' + encodeURIComponent(adds[i]) + '&key=AIzaSyAZwerlkm0gx8mVP0zpfQqeJZM3zGUUPiM');
    }
}

function initMap() {
    var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 3,
          center: {lat: homeLat, lng: homeLng}
    });

    // Create an array of alphabetical characters used to label the markers.
    var labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

    // Add some markers to the map.
    // Note: The code uses the JavaScript Array.prototype.map() method to
    // create an array of markers based on a given "ciirdinate" array.
    // The map() method here has nothing to do with the Google Maps API.
    var markers = coordinates.map(function(location, i) {
        return new google.maps.Marker({
            position: location,
            label: labels[i % labels.length]
        });
    });

    // Add a marker clusterer to manage the markers.
    var markerCluster = new MarkerClusterer(map, markers,
        {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'});
}

function makePlaces(coords) {
    for(let i=0; i < coords.length; i++) {
        console.log("entering make places " + i );
        var place = {lat: coords[0], lng: coords[1]};
        places.push(place);
    }
}
