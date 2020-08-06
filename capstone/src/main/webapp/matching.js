
function getDriver() {
    return fetch('/register-driver').then(response => response.json());
}

function getRider() {
    return fetch('/rider').then(response => response.json());
}

function findDateInDriver(date, drivers) {
    let availableDates = [];
    for(let i = 0; i < drivers.length; i++){
        if (drivers[i].day == date){
            availableDates.push(drivers[i]);
        }
    }
    return availableDates;
}


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

async function getMatch() {
    let rider = await getRider();
    console.log(rider);
    let driver = await getDriver();
    //Driver " + JSON.stringify(JSON.parse(driver))
    console.log(driver);
    // let date = document.getElementById("day").value;
    // let drivers = await getDriver();
    // console.log("Driver " + JSON.stringify(drivers));
    // console.log(date);
    
    // let available = findDateInDriver(date,drivers);
    // showAvailableDrivers(available);
    // console.log("Available " + JSON.stringify(available));

}

function addRiderToCar() {
    console.log("Calls right function")
}