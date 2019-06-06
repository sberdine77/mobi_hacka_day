var map;

var conn = new WebSocket("ws://localhost:8080/Processador/websocketendpoint");

conn.onopen = function(){
    var p = document.getElementById('mensagem');
    p.innerHTML += '<br>Conectado ao servidor.';
    conn.send('getLogFromAllBuses-1');
}

conn.onclose = function(){
    var p = document.getElementById('mensagem');
    p.innerHTML += '<br>Erro na conexão. Recarregue a página.';
}

conn.onmessage = function(res){
    var msg = res.data;
    var posArray = msg.split(',');
    var id = posArray[0];
    busLat = posArray[1];
    busLon = posArray[2];

    movePushpin(id, {latitude: busLat, longitude: busLon, altitude: 0, altitudeReference: -1})
}

function loadMapScenario() {
    map = new Microsoft.Maps.Map(document.getElementById('map'), {});
}

var linhas = ["12226","12164","12053","12057","12107","12115","12118","12206","12231","12408","12409","12032","12254","12048","12124","12219","12427","12113","12108","12135","12203","12047","12126","12220","12028","12151","12234","12212","12044","12404","12428","12024","12218","12144","12050","12201","12051","12216","12103","12106","12101","12251","12109","12222","12040","12116","12227","12125","12043","12102","12437","12258","12027","12114","12130","12217","12105","12414","12037","12134","12426","12202","12232","12209","12432","12036","12049","12436","12208","12423","12110","12056","12022","12140","12415","12139","12413","12419","12255","12059","12030","12149","12128","12442","12229","12154","12210","12058","12132","12035","12045","12421","12211","12029","12420","12407","12215","12434","12418","12025","12039","12165","12235","12253","12161","12402","12438","12256","12445","12401","12439","12406","12425","12411","12440","12221","12042","12163","12403","12207","12230","12069","12111","12405","3336","12416","12433","12444","3333","12152","12443","12133","12143","12233","12429","12259","12204","12422","12112","3335","12021","12260","12162","12138","12224","12026","12213","12023","12142","12127","12055"];
/*for(var i=0; i<linhas.length; i++){
    linhas[i] = {id: linhas[i], pos: {latitude: 0, longitude: 0, altitude: 0, altitudeReference: -1}};
}*/
    

var dataQueue = [];

/* POPULAÇÃO "ALEATÓRIA" PRA TESTAR
for(var u=0; u<2; u++){
    var idAux = "Linha" + u
    var lat = -8 + 0.05*Math.random();
    var lon = -34.9 + 0.05*Math.random();
    linhas[u] = {id: idAux, pos: {latitude: lat, longitude: lon, altitude: 0, altitudeReference: -1}};
    console.log(lat + "," + lon);
}

linhas[2] = {id: "teste", undefined}; **/

function initFilter(){

    loadMapScenario();

    //ler para linhas

    for(var i=0; i<linhas.length; i++){
        linhas[i] = {id: linhas[i], pos: {latitude: 0, longitude: 0, altitude: 0, altitudeReference: -1}};
        var li = document.createElement("LI");
        var input = document.createElement("INPUT");
        input.setAttribute('type', 'checkbox');
        input.setAttribute('id', linhas[i].id);
        input.checked = true;
        li.appendChild(input);
        var span = document.createElement("SPAN");
        span.innerHTML = linhas[i].id;
        li.appendChild(span);
        document.getElementById('lista').appendChild(li);

        input.addEventListener('change', function(){
            togglePushpin(this.id, this.checked);
        });

        addPushpin(linhas[i].pos, linhas[i].id, false);
    }
}


function markAll(b){
    for(var i=0; i<linhas.length; i++){
        var checkbox = document.getElementById(linhas[i].id);
        
        var event = document.createEvent("HTMLEvents");
        checkbox.checked = b;
        event.initEvent('change', false, true);
        checkbox.dispatchEvent(event);
        
        
    }
    if(b){
        conn.send('getLogFromAllBuses-1');
    }
    else{
        conn.send('getLogFromSomeBusIds-1');
    }
}

function addPushpin(pos, id, show){

    /*var pos = {latitude: -8, longitude: -34, altitude: 0, altitudeReference: -1};*/

    var pushpin = new Microsoft.Maps.Pushpin(pos, null);
    pushpin.busID = id;
    map.entities.push(pushpin);
    pushpin.setOptions({visible: show});
}

function togglePushpin(id, b){
    for(var i=0; i<map.entities.getLength(); i++){
        var pushpin = map.entities.get(i);
        if(pushpin instanceof Microsoft.Maps.Pushpin && pushpin.busID==id){
            pushpin.setOptions({visible: b});
            var str = getChecked();
            if(!str){
                conn.send('getLogFromSomeBusIds-1' + str)
            }
            else{
                conn.send('getLogFromSomeBusIds-1');
            }
        }
    }
}

function movePushpin(id, posLatLong){

    for(var i=0; i<map.entities.getLength(); i++){
        var pushpin = map.entities.get(i);
        if(pushpin instanceof Microsoft.Maps.Pushpin && pushpin.busID==id){
            pushpin.setLocation(posLatLong);
        }
    }
}

/**
 * ATÉ AQUI
 * COM CERTEZA
 * FUNCIONAM
 */

/*function consumeQueue(){
    while(true){
        if(dataQueue!=""){
            var 
        }
        
    }
}*/

function getChecked(){
    var items=document.getElementsByClassName('input');
    var selectedItems="";
    for(var i=0; i<items.length; i++){
        if(items[i].checked==true)
            selectedItems+=items[i].value+",";
    }
    if(selectedItems.charAt(selectedItems.length - 1)==','){
        selectedItems = selectedItems.slice(0, selectedItems.length-1);
    }
    return selectedItems;
}	