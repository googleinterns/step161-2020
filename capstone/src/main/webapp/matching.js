//fetch Drivers
function getDriver() {
    return fetch('/register-driver').then(response => response.json());
}

//fetch Riders
function getRider() {
    return fetch('/rider').then(response => response.json());
}

//returns an array of drivers that aravailable during a specific date
function findDateInDriver(date, drivers) {
    let availableDates = [];
    for(let i = 0; i < drivers.length; i++){
        if (drivers[i].day == date){
            availableDates.push(drivers[i]);
        }
    }
    return availableDates;
}

//finds Rider based on date user inputs
function findRiderDate(name, riders) {
    for(let i = 0; i < riders.length; i++){
        if (riders[i].rider == name){
            console.log(name);
            return riders[i].day;
        }
    }
    return 0;
}

//displays list of available to the DOM
function showAvailableDrivers(available) {
    for (let i = 0; i < available.length; i++){
        let first = available[i].first;
        let seats = available[i].seats;
        let times = available[i].times;
        let id = available[i].id;
        let comment = document.createElement("p");
        comment.innerText = first + " is avaiable during the " + times + " of this day. They has " + seats + " seats available in his car. Join this user's car using this id "  + id;
        document.getElementById("driver-container").appendChild(comment);
    }
}

//calls the SetIDServlet to change riderName's ID from 0 to driverID
function changeRiderId(driverId, riderName) {
    return fetch('/setRiderId?driverId=' + driverId +"&riderName=" + riderName).then(response => response.json());
}

function updateSeats(driverId) {
    return fetch('/update-seats?driverId=' + driverId).then(response => response.json());
}

//called when user clicks button
async function setId(){
    // console.log("entered set Id");
    let newDriverId = document.getElementById("driverid").value
    let curRider = document.getElementById("first").value;
    changeRiderId(newDriverId, curRider)
    // console.log("Succesfully set " + curRider +"'s Id to " + newDriverId );
    let results = await updateSeats(newDriverId);
    console.log(results);
}


//function that controls displaying available drivers using rider's name
async function getMatch() {
    let riders = await getRider();
    // console.log(riders);
    let curRider = document.getElementById("first").value;
    let curRiderDate = findRiderDate(curRider, riders);
    if (curRiderDate == 0) {
        return;
    }
    let drivers = await getDriver();
    let available = findDateInDriver(curRiderDate,drivers.drivers)
    showAvailableDrivers(available);  
    let newSeats = updateSeats(6702914302545664000);
    // console.log(newSeats);
}

