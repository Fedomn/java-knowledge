## package workflow

1.package spi impl library 

```bash
cd com/fedomn/spi
javac ./Spi.java ./spiimpl/SpiImpl.java

cd java
jar -cvf spiimpl.jar com/fedomn/spi/spiimpl/*.class com/fedomn/spi/spi/Spi.class


mv spiimpl.jar com/fedomn/spi
cd com/fedomn/spi
mkdir -p META-INF/services
cd META-INF/services
touch com.fedomn.spi.Spi
echo 'com.fedomn.spi.spiimpl.SpiImpl' > com.fedomn.spi.Spi

# append spi info to META-INFO
jar -ufv spiimpl.jar META-INF/services/com.fedomn.spi.Spi


mv spiimpl.jar ~/Desktop/
```

2.package test jar that need to invoke spi impl

```bash
cd com/fedomn/spi
javac ./Spi.java ./spitest/SpiTest.java

cd java
jar -cvf spitest.jar com/fedomn/spi/spitest/*.class com/fedomn/spi/Spi.class

mv spitest.jar ~/Desktop/
```

3.run test jar and invoke spi impl dynamically

```bash
java -cp spiimpl.jar:spitest.jar com.fedomn.spi.spitest.SpiTest
```


