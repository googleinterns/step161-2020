
// Completes basic setup of page after loading.
async function setupPage() {
  let loginInfo = await fetch('/login?format=json').then(r => r.json());
  console.log('Login info: ' + loginInfo);
  console.log(loginInfo);
  let status = id('login-status');
  let link = id('login-link');
  if (loginInfo.loggedIn) {
    id('not-logged').classList.add('hidden');
    status.innerHTML = 'Logged in as: ' + loginInfo.email;
    link.innerHTML = '<a href="' + loginInfo.logoutLink + '">Log Out</a>';
  } else {
    id('sign-up').classList.add('hidden');
  }
}

//used for testing purposes only
function getRider(){
  fetch('/rider').then(response => {
      return response.json();
    }).then((riders) => {
      console.log(riders);
  });
}

//change the rider info
function changeRider() {

}

function id(name){
    return document.getElementById(name);
}