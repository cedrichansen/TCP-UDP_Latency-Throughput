import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {



    /*
    Assignment instructions

    -Measure round-trip latency as a function of message size, by sending and receiving (echoing) messages of size 1, 64,
     and 1024 bytes, using both TCP and UDP. Measure RTTs.

    -Measure throughput by sending TCP messages of size 1K, 16K, 64K, 256K, and 1M bytes in each direction.
    Measure transfer rates.

    -Measure the interaction between message size and number of messages using TCP and UDP by sending 1MByte of data
    (with a 1-byte acknowledgment in the reverse direction) using different numbers of messages: 1024 1024Byte messages,
    vs 2048 512Byte messages, vs 4096 X 256Byte messages.

     */


    final static int port = 2689;
    final static int trials = 10;


    static TCPClient tcpClient;
    static UDPClient udpClient;


    //these arrays are to calculate average values for the data to get a better estimate of true results
    static int [] tcpRTT1Byte = new int [trials];
    static int [] tcpRTT64Byte = new int [trials];
    static int [] tcpRTT1024Byte = new int [trials];

    static int [] udpRTT1Byte = new int [trials];
    static int [] udpRTT64Byte = new int [trials];
    static int [] udpRTT1024Byte = new int [trials];

    static float [] tcpThroughput1KByte = new float [trials];
    static float [] tcpThroughput16KByte = new float [trials];
    static float [] tcpThroughput64KByte = new float [trials];
    static float [] tcpThroughput256KByte = new float [trials];
    static float [] tcpThroughput1MByte = new float [trials];

    static int [] tcpInteraction1024Messages = new int [trials];
    static int [] tcpInteraction2048Messages = new int [trials];
    static int [] tcpInteraction4096Messages = new int [trials];

    static int [] udpInteraction1024Messages = new int [trials];
    static int [] udpInteraction2048Messages = new int [trials];
    static int [] udpInteraction4096Messages = new int [trials];

    static int currentTrial = 0;


    public static void main (String [] args) {
        Scanner kb = new Scanner(System.in);
        System.out.println("Type 1 for server, 2 for client");
        int selection = Integer.parseInt(kb.nextLine());

        try {

            if(selection == 1) {

                printExternalIP();

                for (; currentTrial < trials; currentTrial++) {

                    System.out.println("Current trial: " + (currentTrial+1));

                    TCPServer tcpServer = new TCPServer(port);

                    tcpServer.startServer(1);
                    tcpServer.startServer(64);
                    tcpServer.startServer(1024);

                    System.out.println("Successfully echoed all TCP responses");

                    UDPServer udpServer = new UDPServer(port);
                    udpServer.startServer(1);
                    udpServer.startServer(64);
                    udpServer.startServer(1024);

                    System.out.println("Successfully echoed all UDP responses");

                    tcpServer.startServer(1024);
                    tcpServer.startServer(16384);
                    tcpServer.startServer(65536);
                    tcpServer.startServer(262144);
                    tcpServer.startServer(1048576);

                    System.out.println("Succesfully calculated throughput for TCP messages ");

                    tcpServer.echo1MBServer(1024,1024);
                    tcpServer.echo1MBServer(2048, 512);
                    tcpServer.echo1MBServer(4096,256);

                    System.out.println("Successfully calculated time for messages/numMessages TCP");

                    udpServer.echo1MBServer(1024,1024);
                    udpServer.echo1MBServer(2048, 512);
                    udpServer.echo1MBServer(4096,256);

                    System.out.println("Successfully calculated time for messages/numMessages UDP");
                }

                System.out.println("Succesfully completed all trials!");


            } else if (selection == 2) {
                System.out.print("Server ip address: ");
                String ip = kb.nextLine();
                System.out.println("");
                tcpClient = new TCPClient(ip, port);
                udpClient = new UDPClient(ip, port);

                for (; currentTrial<trials; currentTrial++) {

                    System.out.println("Current trial: " + (currentTrial+1));


                    System.out.println("Measuring Round Trip Time");

                    System.out.println("---TCP RTT's---");
                    tcpRTT1Byte[currentTrial] = sendTCPMessage("RTT for 1 byte:", 1);
                    tcpRTT64Byte[currentTrial] = sendTCPMessage("RTT for 64 bytes:", 64);
                    tcpRTT1024Byte[currentTrial] = sendTCPMessage("RTT for 1024 bytes:", 1024);

                    System.out.println("---UDP RTT's---");
                    udpRTT1Byte[currentTrial] = sendUDPMessage("RTT for 1 byte:", 1);
                    udpRTT64Byte[currentTrial] = sendUDPMessage("RTT for 64 byte:", 64);
                    udpRTT1024Byte[currentTrial] = sendUDPMessage("RTT for 1024 byte:", 1024);

                    System.out.println("------------------------");
                    System.out.println("Measuring throughput (TCP only)");

                    tcpThroughput1KByte[currentTrial] = measureTCPThroughput(1024);
                    tcpThroughput16KByte[currentTrial] = measureTCPThroughput(16384);
                    tcpThroughput64KByte[currentTrial] =  measureTCPThroughput(65536);
                    tcpThroughput256KByte[currentTrial] = measureTCPThroughput(262144);
                    tcpThroughput1MByte[currentTrial] = measureTCPThroughput(1048576);

                    System.out.println("------------------------");

                    System.out.println("Measuring interaction between msg size and number of messages for 1MB");

                    System.out.println("---TCP---");

                    tcpInteraction1024Messages[currentTrial] = measureInteractionTCP(1024, 1024);
                    tcpInteraction2048Messages[currentTrial] = measureInteractionTCP(2048, 512);
                    tcpInteraction4096Messages[currentTrial] = measureInteractionTCP(4096, 256);

                    System.out.println("---UDP---");
                    udpInteraction1024Messages[currentTrial] = measureInteractionUDP(1024, 1024);
                    udpInteraction2048Messages[currentTrial] = measureInteractionUDP(2048, 512);
                    udpInteraction4096Messages[currentTrial] = measureInteractionUDP(4096, 256);

                }

                System.out.println("\n\n Finished running trials... Results are below\n\n");

                printResults();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void printResults() {

        System.out.println("Average TCP RTT 1 byte: " + getAverage(tcpRTT1Byte) + " microseconds");
        System.out.println("Average TCP RTT 64 byte: " + getAverage(tcpRTT64Byte) + " microseconds");
        System.out.println("Average TCP RTT 1024 byte: " + getAverage(tcpRTT1024Byte) + " microseconds");

        System.out.println("Average UDP RTT 1 byte: " + getAverage(udpRTT1Byte) + " microseconds");
        System.out.println("Average UDP RTT 64 byte: " + getAverage(udpRTT64Byte) + " microseconds");
        System.out.println("Average UDP RTT 1024 byte: " + getAverage(udpRTT1024Byte) + " microseconds");

        System.out.println("Average throughput for 1k bytes: " + getAverageFloat(tcpThroughput1KByte) + " Mbps");
        System.out.println("Average throughput for 16k bytes: " + getAverageFloat(tcpThroughput16KByte) + " Mbps");
        System.out.println("Average throughput for 64k bytes: " + getAverageFloat(tcpThroughput64KByte) + " Mbps");
        System.out.println("Average throughput for 256k bytes: " + getAverageFloat(tcpThroughput256KByte) + " Mbps");
        System.out.println("Average throughput for 1M bytes: " + getAverageFloat(tcpThroughput1MByte) + " Mbps");

        System.out.println("Average time to send 1024, 1024 byte TCP messages: " + getAverage(tcpInteraction1024Messages) + " Milliseconds");
        System.out.println("Average time to send 2048, 512 byte TCP messages: " + getAverage(tcpInteraction2048Messages) + " Milliseconds");
        System.out.println("Average time to send 4096, 256 byte TCP messages: " + getAverage(tcpInteraction4096Messages) + " Milliseconds");

        System.out.println("Average time to send 1024, 1024 byte UDP messages: " + getAverage(udpInteraction1024Messages) + " Milliseconds");
        System.out.println("Average time to send 2048, 512  byte UDP messages: " + getAverage(udpInteraction2048Messages) + " Milliseconds");
        System.out.println("Average time to send 4096, 256  byte UDP messages: " + getAverage(udpInteraction4096Messages) + " Milliseconds");


    }

    private static int getAverage(int [] data) {
        int count =0;
        for (int i: data) {
            count += i;
        }
        return count/data.length;
    }

    private static float getAverageFloat(float [] data) {
        float count =0;
        for (float i: data) {
            count += i;
        }
        return count/data.length;
    }


    public static int measureInteractionTCP(int numMessages, int messageSize) throws IOException {
        int time = (int)convertNanoToMilli(tcpClient.send1MB(numMessages, messageSize));
        System.out.println("Time to send " + numMessages + ", " + messageSize + " byte packets: " + time + " Milliseconds");
        return time;

    }

    public static int measureInteractionUDP(int numMessages, int messageSize) throws IOException {
        int time = (int)convertNanoToMilli(udpClient.send1MB(numMessages, messageSize));
        System.out.println("Time to send " + numMessages + ", " + messageSize + " byte packets: " + time + " Milliseconds");
        return time;
    }


    public static int sendTCPMessage(String outputMessage, int numBytes) throws IOException {

        byte [] message = new byte[numBytes];
        Arrays.fill(message, (byte)1);
        long RTT = tcpClient.sendAndMeasureRTT(message);
        System.out.println(outputMessage +  " " + convertNanoToMs(RTT) + " microseconds");
        return (int)convertNanoToMs(RTT);

    }

    public static float measureTCPThroughput(int numBytes) throws IOException {
        byte [] message = new byte[numBytes];
        Arrays.fill(message, (byte)1);
        long RTT = tcpClient.sendAndMeasureRTT(message);

        //throughout here in bits/nanosecond
        int numBits = numBytes*8;
        double time = (double)RTT/2;
        double throughput = numBits/time;

        //convert to megabits/sec
        float throughputMBPS = (float)throughput*1000;
        
        System.out.println("Throughput for " + numBytes + " : " + throughputMBPS + " Mbps");
        return throughputMBPS;
            

    }

    public static int sendUDPMessage(String outputMessage, int numBytes) throws IOException {
        byte [] message = new byte[numBytes];
        Arrays.fill(message, (byte)1);
        long RTT =udpClient.sendAndMeasureRTT(message);
        System.out.println(outputMessage +  " " + convertNanoToMs(RTT) + " microseconds");
        return (int)convertNanoToMs(RTT);

    }


    public static void printExternalIP() throws UnknownHostException {

        // Find public IP address
        String systemipaddress;
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

            // reads system IPAddress
            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e)
        {
            systemipaddress = "Cannot Execute Properly";
        }
        System.out.println("Public IP Address: " + systemipaddress);
    }


    public static long convertNanoToMs(long time) {
        return TimeUnit.MICROSECONDS.convert(time, TimeUnit.NANOSECONDS);
    }
    public static long convertNanoToMilli(long time) {
        return TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);
    }

}

