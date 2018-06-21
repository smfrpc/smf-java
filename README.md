[![](https://img.shields.io/badge/unicorn-approved-ff69b4.svg)](https://www.youtube.com/watch?v=9auOCbH5Ns4)
![][license img]

### smf-java
how to test ? for now just run
```bash
./gradlew run
```
it will execute example.demo.DemoApp, which starts 1000requests in cycles per 50requests (one req == one thread).
Of course, SMF server started on port 7000 is required.

### API
this highly depends on SMF code gen, but it should looks like :
```java
smfStorageClient.get(request)
        .thenAccept(response -> {
            //something
        });
```
see DemoApp.java

## References

* [Main Repository](https://github.com/senior7515/smf) - smf source code
* [Official Documentation](https://senior7515.github.io/smf/) - smf documentation

## Powered by
<img src="http://normanmaurer.me/presentations/2014-netflix-netty/images/netty_logo.png" height="75" width="150">

[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg
