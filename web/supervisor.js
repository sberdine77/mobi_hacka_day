var map;
function loadMapScenario() {
    map = new Microsoft.Maps.Map(document.getElementById('map'), {});
}

var linhas = ["Linha1", "Linha2"]; 
/*{id: id, mostrar: false/true}*/

function initFilter(){

    loadMapScenario();

    for(var i=0; i<linhas.length; i++){
        var li = document.createElement("LI");
        var input = document.createElement("INPUT");
        input.setAttribute('onclick', "toggleOne(" + linhas[i] + ")");
        input.setAttribute('type', 'checkbox');
        input.setAttribute('id', linhas[i]);
        input.checked = true;
        li.appendChild(input);
        var span = document.createElement("SPAN");
        span.innerHTML = linhas[i];
        li.appendChild(span);
        document.getElementById('lista').appendChild(li);
    }
}

function toggleAll(b){
    for(var i=0; i<linhas.length; i++){
        var input = document.getElementById(linhas[i]);
        input.checked = b;
        toggleOne(linhas[i]);
    }
}

function toggleOne(id){
    //toggle do mapa
    //addPushpin({latitude: -8, longitude: -34, altitude: 0, altitudeReference: -1});

    var dbg = map.entities;
    debugger;
}

function addPushpin(pos){

    /*var pos = {latitude: -8, longitude: -34, altitude: 0, altitudeReference: -1};*/

    var pushpin = new Microsoft.Maps.Pushpin(pos, null);
    map.entities.push(pushpin);
    map.entities.pop(pushpin);
}