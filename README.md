# httpserver

using tomcat as the http server up file uploading

example:

docker run -p 9090:8080 -p 8443:8443 kk1983/httpserver 

expose 8080 port for http and 8443 port for https

browser http://host_IP:9090/, if you see the welcome page, it's working for you.

the upload testing html is http://httpserver_IP/helloworld/helloworld/upload.html
