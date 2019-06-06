#!/usr/bin/env node 

var amqp = require('amqplib/callback_api');
var fs = require('fs');

let data_logs = fs.readFileSync('./dados/dadosOnibus.json');

let data_parsed  = JSON.parse(data_logs);


amqp.connect('amqp://localhost', function(error0, connection) {

    if (error0) {
        throw error0;
    }

    connection.createChannel(function(error1, channel) {
        
        if (error1) {
            throw error1;
        }

        var queue = 'logs';

        channel.assertQueue(queue, {

            durable : false 
        });
        
    
        data_parsed.forEach(el => {
            
            setTimeout(function(){
                let line = JSON.stringify(el);
                channel.sendToQueue(queue, Buffer.from(line));
                console.log(line);
            }, 500);
            
                
        });

        
    



    });

    setTimeout(function() {
        connection.close();
        process.exit(0);
    }, 3000);

});

