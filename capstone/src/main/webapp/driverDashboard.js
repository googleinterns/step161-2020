//fetch Riders
function getQuery(driverId) {
    console.log("Entering getQuery");
    return fetch('/driver-dashboard?driverId=' + driverId).then(response => response.json());
}

//displays all of car's riders
function showRiders(riders) {
    for (let i = 0; i < riders.length; i++){
        console.log(riders[i]);
        let name = riders[i].rider;
        let comment = document.createElement("p");
        comment.innerText = name;
        document.getElementById("driver-container").appendChild(comment);
    }
}

async function getRiders() {
    let driverId = document.getElementById("driverId").value;
    let riders = await getQuery(driverId);
    console.log(riders);
    showRiders(riders);
}

