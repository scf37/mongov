# MongoV
MongoDB viewer and editor

##What is this

Web application that connects to existing MongoDB database and then you can:
- View databases, collections, documents
- Sort and filter by any field of the document
- Modify documents and create new documents

##How to use
####Option 1: Just checkout and run
JDK6 is required.
#####Linux
```bash
git clone https://github.com/scf37/mongov/
cd ./mongov
./gradlew installApp
cd ./build/install/mongov
./bin/mongov
```
#####Windows
```cmd
git clone https://github.com/scf37/mongov/
cd .\mongov
.\gradlew.bat installApp
cd .\build\install\mongov\
.\bin\mongov.bat
```
####Option 2: Docker image
```bash
docker run --rm -e mongov.bindAddr=127.0.0.1 --net=host scf37/mongov
```

##Configuration
NOTE - MongoV can connect to any mongo instance and therefore must be protected by firewall, binded to localhost and/or hidden behind authenticating proxy like nginx.

To set additional configuration options, they must be exported to environment variables - that is, via ```export``` bash builtin or ```-e``` Docker parameter.

| Name               | Default value  | Description  |
| ---------------    | -------------  | ------------ |
| mongov.port        | 8888           | Port to listen for HTTP connections
| mongov.bindAddr    | 0.0.0.0        | Interface to bind listening port to. Often used value is 127.0.0.1 (bind to localhost)
| mongov.contextPath | /              | Context path of Mongov web application. If you need mongov at ```http://myhost.com:8888/mongov/```, then context path must be set to ```/mongov/```
