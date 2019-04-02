![][license img]

### smf-java
port of SMF in Java language.
Examples are stored in example.demo package (those are not generated). If you want to run server and client, first build them
```bash
./gradlew buildClientExample
./gradlew buildServerExample
```
and run
```bash
java -jar ./build/libs/smf-java-server-1.0-SNAPSHOT.jar
java -jar ./build/libs/smf-java-client-1.0-SNAPSHOT.jar
```

### API
If are familiar with internals of SMF, you can use smf.client.core.SmfClient and smf.server.core.SmfServer directly, if not, just look at examples.

### Contribution
Before raising PR be sure tu run
```
python3 fmt.py
```


## References

* [Main Repository](https://github.com/smfrpc/smf) - smf source code
* [Official Documentation](https://smfrpc.github.io/smf/) - smf documentation

## Powered by
<img src="http://normanmaurer.me/presentations/2014-netflix-netty/images/netty_logo.png" height="75" width="150">

[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg
