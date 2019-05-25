

var myLat, myLon;
var busLat, busLon;

var conn = new WebSocket("ws://localhost:8080/Processador/websocketendpoint");

conn.onopen = function(){
    var p = document.getElementById('mensagem');
    p.innerHTML += '<br>Conectado ao servidor.';
}

conn.onclose = function(){
    var p = document.getElementById('mensagem');
    document.getElementById('buses').disabled = true;
    document.getElementById('botao').disabled = true;
    p.innerHTML += '<br>Erro na conexão. Recarregue a página.';
}

conn.onmessage = function(res){
    var msg = res.data;
    var posArray = msg.split(',');
    var id = posArray[0]; //usuario n precisa
    busLat = posArray[1];
    busLon = posArray[2];

    var dist = distance(myLat, myLon, busLat, busLon, 'K')
    
    var p = document.getElementById('mensagem');
    p.innerHTML += "<br>"+ dist + " km" 

    //var dist = haversine(myLat, myLon, busLat, busLon);
    if(dist >= 1){
        isClose();
    }

    else if(dist <=0){
        p.innerHTML = "CHEGOU";
        throw new FatalError("fim");
    }

}

var x = document.getElementById("loc");
function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } 
    else {
    x.innerHTML += "<br>Geolocation is not supported by this browser.";
    }
}

function showPosition(position) {
    myLat = position.coords.latitude;
    myLon = position.coords.longitude;
    x.innerHTML = "Latitude: " + myLat +
    "<br>Longitude: " + myLon;
}

var linhas = ["12226","12164","12053","12057","12107","12115","12118","12206","12231","12408","12409","12032","12254","12048","12124","12219","12427","12113","12108","12135","12203","12047","12126","12220","12028","12151","12234","12212","12044","12404","12428","12024","12218","12144","12050","12201","12051","12216","12103","12106","12101","12251","12109","12222","12040","12116","12227","12125","12043","12102","12437","12258","12027","12114","12130","12217","12105","12414","12037","12134","12426","12202","12232","12209","12432","12036","12049","12436","12208","12423","12110","12056","12022","12140","12415","12139","12413","12419","12255","12059","12030","12149","12128","12442","12229","12154","12210","12058","12132","12035","12045","12421","12211","12029","12420","12407","12215","12434","12418","12025","12039","12165","12235","12253","12161","12402","12438","12256","12445","12401","12439","12406","12425","12411","12440","12221","12042","12163","12403","12207","12230","12069","12111","12405","3336","12416","12433","12444","3333","12152","12443","12133","12143","12233","12429","12259","12204","12422","12112","3335","12021","12260","12162","12138","12224","12026","12213","12023","12142","12127","12055"];

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
}

function waitForBus(){

    var select = document.getElementById('buses');
    var bus = select.options[select.selectedIndex].text;
    var button = document.getElementById('botao');

    var p = document.getElementById('mensagem');
    p.innerHTML += "<br>Aguardando " + bus;
    conn.send('getLogFromBusId-' + bus);

    select.disabled = true;
    button.disabled = true;

}

function isClose(){
    var p = document.getElementById('mensagem');
    p.innerHTML = "<br>CHEGANDO! " + dist + " km";
    p.setAttribute('class', 'chegando');
}

function distance(lat1, lon1, lat2, lon2, unit) {
    var radlat1 = Math.PI * lat1/180
    var radlat2 = Math.PI * lat2/180
    var radlon1 = Math.PI * lon1/180
    var radlon2 = Math.PI * lon2/180
    var theta = lon1-lon2
    var radtheta = Math.PI * theta/180
    var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
    dist = Math.acos(dist)
    dist = dist * 180/Math.PI
    dist = dist * 60 * 1.1515
    if (unit=="K") { dist = dist * 1.609344 }
    if (unit=="N") { dist = dist * 0.8684 }
    return dist
}