var x = document.getElementById("loc");
function getLocation() {
    if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(showPosition);
    } else {
    x.innerHTML = "Geolocation is not supported by this browser.";
    }
}

function showPosition(position) {
    x.innerHTML = "Latitude: " + position.coords.latitude +
    "<br>Longitude: " + position.coords.longitude;
}

var linhas = ["Linha1", "Linha2"];

function initList(){

    var select = document.getElementById('buses');
    for(var i=0; i<linhas.length; i++){
        var option = document.createElement("OPTION");
        option.innerHTML = linhas[i];
        select.appendChild(option);
    }

}

function iniciar(){
    getLocation();
    initList();
    loadMapScenario();
}

function waitForBus(){

    var select = document.getElementById('buses');
    var bus = select.options[select.selectedIndex].text;

    var p = document.getElementById('mensagem');
    p.innerHTML = "Aguardando " + bus;
}

function isClose(){
    var p = document.getElementById('mensagem');
    p.innerHTML = "CHEGANDO";
    p.setAttribute('class', 'chegando');
}