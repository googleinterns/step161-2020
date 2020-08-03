//used for testing purposes only
function getRider(){
  fetch('/rider').then(response => {
      return response.json();
    }).then((riders) => {
      console.log(riders);
  });
}

function id(name){
    return document.getElementById(name);
}