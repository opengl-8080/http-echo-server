# http-echo-server
- Simple HTTP Server implemented by Spring Boot.
- This server always returns "echo" in any requests.

## Requirement
- Java 11+

## Start Server
```sh
$ java -jar http-echo-server.jar
```

## How to use
```sh
# this server returns "echo" with 200 for any path and method.
$ curl http://localhost:8080/any/path -i -s
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 4
Date: Sat, 15 May 2021 12:55:58 GMT

echo

# you can specify response body with the "echo" query parameter.
$ curl http://localhost:8080?echo=hello -s
hello

# you can specify status code with the "status" query parameter.
$ curl http://localhost:8080?status=500 -i -s
HTTP/1.1 500
Content-Type: text/plain;charset=UTF-8
Content-Length: 4
Date: Sat, 15 May 2021 12:58:22 GMT
Connection: close

echo

# you can adjust response time with the "time" query parameter (by milliseconds).
$ curl http://localhost:8080?time=5000
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100     4    0     4    0     0      0      0 --:--:--  0:00:05 --:--:--     1echo
```
