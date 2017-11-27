# ErrorZ

The ErrorZ project implements the error correcting code I described in my paper published in the Russian journal
_Autometria_ in 1996 [(original paper in Russian)](https://www.iae.nsk.su/images/stories/5_Autometria/5_Archives/1996/5/104-107.pdf).
The journal was translated and published in the USA by _Allerton Press, Inc._ as _Optoelectronics, Instrumentation and Data Processing_.

The main idea was to work in a finite field of a large size, say _GF(2^64)_, but use locators from its proper subfield, f.e. _GF(2^8)_.
That limits the maximal length of the code but dramatically increases its decoding ability as we now can easily increase the
number of equations for the locator polynomial coefficients by simply raising each equation to the power of the size 
of the subfield, _2^8_. For example, the (256, 248) code can be quite reliably decoded when the number of errors is 7 as opposed
to 4 for the analogous Reed-Solomon code. The probability of rejected or wrong decoding at the same time remains negligible,
which along with the simplicity of the decoding procedure makes the code interesting from a practical perspective.

Performance improvements that would compromise the simplicity of the implementation have not been attempted.
I hope to write up a document [(current version)](docs/ECCfromScratch.pdf) providing a gentle introduction to Reed-Solomon codes, my algorithm, and some extensions.

## How to build, test, and run

The project uses Java 8 and Maven (3.3.9), though it doesn't really have any dependencies.
To run tests:
```
    mvn test
```
To create a jar file:
```shell
    man package
```
To run tests from jar:
```shell
    java -jar target/ErrorZ-1.0.0.jar
```

## How to build, test, and run without Maven

To build
```shell
    mkdir -p target/classes
    javac -sourcepath src/main/java -d target/classes src/main/java/org/mazurov/errorz/*.java
```
To test
```shell
    java -cp target/classes org.mazurov.errorz.Main
```

## License

ErrorZ is licensed under the Apache License, Version 2.0.

For additional information, see the [LICENSE](LICENSE) file.

