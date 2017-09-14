# jgroups-simple-chat-demo

jgroups-simple-chat-demo is a jgroups sample code build on top of spring boot. 

### build
```bash
mvn clean package
```

### start the demo app on local machine
```bash
mvn spring-boot:run -Djava.net.preferIPv4Stack=true -Dserver.port=8081

mvn spring-boot:run -Djava.net.preferIPv4Stack=true -Dserver.port=8082

mvn spring-boot:run -Djava.net.preferIPv4Stack=true -Dserver.port=8083
   
```

### deploy the demo app using [hi-cicd](https://github.com/hi-cli/hi-cicd) on openshift
```bash
hi init
hi cicd deploy hi_debug new build jcluster=simple-chat
```
