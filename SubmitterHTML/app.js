var util = require('util'),
    connect = require('connect'),
    colors = require('colors'),
    httpProxy = require('http-proxy');

var PROXY_PORT = 8000;
var SERVER_PORT = 3000;

var regex = /\/coursera/;

//
// Now we set up our proxy.
//
httpProxy.createServer(function(req, res, proxy) {
  // For coursera specific path
  var path, match;

  match = regex.exec(req.url);
  if(match) {
    path = req.url.slice(match[0].length);
    req.url = path;
    req.headers.host = undefined; // Clear old header
    proxy.proxyRequest(req, res, { 
      host: 'class.coursera.org',
      port: 80 });
  } else {
    // For all else default to serving it as normal
    proxy.proxyRequest(req, res, {
      host: 'localhost',
      port: SERVER_PORT });
  }
}).listen(PROXY_PORT);

var app = connect().
  use(connect.logger('dev')).
  use(connect.static('app')).
  use(function(req, res){
    res.end('hello world\n');
  }).listen(SERVER_PORT);

// And finally, some colored startup output.
util.puts('http proxy server'.blue + ' started '.green.bold + 'on port '.blue + PROXY_PORT.toString().yellow);

