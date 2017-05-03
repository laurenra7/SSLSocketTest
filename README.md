# SSLSocketTest
Simple test of SSL socket for specified server.

### Build

Build with [Maven](https://maven.apache.org/).

```
mvn clean install
```

Produces an executable .jar file

```
/target/SSLSocketTest.jar
```


### Run

```
java -jar SSLSocketTest.jar
```


### Options

```
usage: java -jar SSLSocketTest.jar url [-h] [-v]

Open an SSL Socket and do an HTTP GET

 -h,--help      Show this help
 -v,--verbose   show processing messages

Examples:

  java -jar SSLSocketTest.jar someserver.com

  java -jar SSLSocketTest.jar www.somewhere.com

  java -jar SSLSocketTest.jar 176.182.12.13
```
