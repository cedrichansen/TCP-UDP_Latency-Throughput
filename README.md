This project measures several network related things. The assignment prompt was the following.

1. Measure round trip time by sending different sized messages (1, 64, and 1024 bytes) using both TCP and UDP
2. Measure TCP Throughput by sending different sized messages (1K, 16K, 64K, 256K, and 1M bytes)
3. Measuring relationship between number of messages, and message size for sending 1Mb of data by sending 1024 1024Byte messages, vs 2048 512Byte messages, vs 4096 X 256Byte messages

The program can be run through any standard IDE, or can be compiled using
```
mvn clean

mvn package

java -cp target/csc445hw01-1.0-SNAPSHOT.jar Main
```

2 Instances of the program need to be running. 
One instance serving as the server side, the other as the client side.


Possible errors can include port already being taken, in which case the port variable in the main class can be changed.

