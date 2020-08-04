
function getDriver() {
    return fetch('/register-driver').then(response => response.json());
}

function getRider() {
    return fetch('/driver').then(response => response.json());;
}


function findDateInDriver(date,drivers) {
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
        let comment = document.createElement("p");
        comment.innerText = first + " is avaiable during the " + times + " of this day. He has " + seats + " seats available in his car.";
        document.getElementById("driver-container").appendChild(comment);
    }
}

async function getMatch() {

    let rider = await getRider();
    console.log("Rider " + JSON.stringify(rider));

    let date = document.getElementById("day").value;
    let drivers = await getDriver();
    console.log("Driver " + JSON.stringify(drivers));
    console.log(date);

    let available = findDateInDriver(date,drivers);
    showAvailableDrivers(available);
    console.log("Available " + JSON.stringify(available));

}