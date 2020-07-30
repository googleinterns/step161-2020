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
let temp = "";
var map ; 

function getData() {
    //fetches address from the form and displays nearby polling locations
    let form = document.getElementById("my-form");
    address = document.getElementById("my-form").elements["address"];
    console.log("Address value before fetch:" + address.value);
    temp = address.value;
    fetch('https://civicinfo.googleapis.com/civicinfo/v2/voterinfo?address=' + encodeURIComponent(address.value) +'&electionId=2000&officialOnly=true&returnAllAvailableData=true&key=AIzaSyClf-1yO8u6fBpnDyI9u_WTQZX4gYkbkWs').then(response => response.json()).then((quote) => {
        let address = document.getElementById("random");
        address.innerHTML = "";
        if (quote.pollingLocations === undefined) {
            let comment  = document.createElement("p");
            comment.innerText = "No polling locations in the same zipcode as your address";
            document.getElementById("random").appendChild(comment);
        } else {
            getHomeCoord(temp, quote);
        }
    });
}


function getHomeCoord(add,quote){ 
    fetch('https://maps.googleapis.com/maps/api/geocode/json?address=' + encodeURIComponent(add) + '&key=AIzaSyAZwerlkm0gx8mVP0zpfQqeJZM3zGUUPiM').then(response => response.json()).then((geo) => {
        if (geo.results.length == 0) {
            console.log("Empty result in getHomeCoord");
            return;
        }
        homeLat = geo.results[0].geometry.location.lat;
        homeLng = geo.results[0].geometry.location.lng;
        addAddresses(quote);
        console.log("after addAdd, homeLat = " + homeLat + " homeLng = " + homeLng);
        buildCoordinates(addresses);
        console.log(coordinates);
        map.setCenter({"lat": homeLat, "lng": homeLng});
        makeMarkers(coordinates);
        document.getElementById("map").style.display = 'block';
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


function getCoord(url){ 
    fetch(url).then(response => response.json()).then((geo) => {
        if (geo.results.length == 0) {
            console.log("Empty result");
            return;
        }
        latmap = geo.results[0].geometry.location.lat;
        lngmap = geo.results[0].geometry.location.lng;
        coordinates.push({lat: latmap, lng: lngmap});
        console.log("Inside of getCoord" + coordinates);
    });
}

function buildCoordinates(adds) {
    for(let i=0; i < adds.length; i++) {
        console.log("Build coordinates" + i);
        getCoord('https://maps.googleapis.com/maps/api/geocode/json?address=' + encodeURIComponent(adds[i]) + '&key=AIzaSyAZwerlkm0gx8mVP0zpfQqeJZM3zGUUPiM');
    }
}

function initMap() {
    document.getElementById("map").style.display = 'none';
    console.log("initMap, homeLat = " + homeLat + " homeLng = " + homeLng);
    var myLatLng = {lat: homeLat, lng: homeLng};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 4,
        center: myLatLng
    });

}

//TODO: make Java servlet that will process addresses and return map of lat and longs for each location using GEOCODE API
function makeMarkers(coord){
    if (coord.length == 0) {
            console.log("Empty coordinates, not able to make Markers");
            return;
    }
    for(let i=0; i < coord.length; i++) {
        var marker = new google.maps.Marker({
            position: {lat: coord[i].lat, lng: coord[i].lng},
            map: map,
            title: (String)(i)
            
        }); 
        marker.setMap(map);
    }
}
