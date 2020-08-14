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

async function getData() {
    let form = document.getElementById("my-form");
    let address = form.elements["address"].value;
    let pollingInfo = await lookupPollingPlace(address);

  if (!pollingInfo.pollingLocations) {
    console.log("couldn't find anything");
  }


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
    initMap(homeLoc);
    map.setCenter({"lat": homeLoc.lat, "lng": homeLoc.lng});
    makeMarkers(coordinates);
    document.getElementById("map").style.display = 'block';
}


//finds polling locations
function lookupPollingPlace(address) {
  console.log("Looking up address: " + address);
  let civicinfo = [
    'https://civicinfo.googleapis.com/civicinfo/v2/voterinfo?address=',
    encodeURIComponent(address),
    '&electionId=2000',
    '&officialOnly=true',
    '&returnAllAvailableData=true',
    '&key=AIzaSyClf-1yO8u6fBpnDyI9u_WTQZX4gYkbkWs'
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
    let url = [ 'https://maps.googleapis.com/maps/api/geocode/json?address=',
    encodeURIComponent(address),
    '&key=AIzaSyAZwerlkm0gx8mVP0zpfQqeJZM3zGUUPiM',].join('');

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

//helper method
function id(thing) {
    return document.getElementById(thing);
}
