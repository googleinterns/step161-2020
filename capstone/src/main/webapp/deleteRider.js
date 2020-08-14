function deleteRider(riderName) {
    return fetch('/delete-rider?riderName='+ riderName).then(response => response.json());
}

async function delRiders() {
    let form = document.getElementById("delete-form");
    let riderName = form.elements["riderName"].value;
    // let rider = riderName.toString();
    console.log("Rider name is " + riderName);
    deleteRider(riderName);
    console.log("completed deleteRider");
}