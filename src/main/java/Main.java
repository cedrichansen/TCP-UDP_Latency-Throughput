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
    -Measure round-trip latency as a function of message size, by sending and receiving (echoing) messages of size 1, 64,
     and 1024 bytes, using both TCP and UDP. Measure RTTs.

    -Measure throughput by sending TCP messages of size 1K, 16K, 64K, 256K, and 1M bytes in each direction.
    Measure transfer rates.

    -Measure the interaction between message size and number of messages using TCP and UDP by sending 1MByte of data
    (with a 1-byte acknowledgment in the reverse direction) using different numbers of messages: 1024 1024Byte messages,
    vs 2048 512Byte messages, vs 4096 X 256Byte messages.

     */


    final static int port = 2689;

    static TCPClient tcpClient;
    static UDPClient udpClient;

    public static void main (String [] args) {
        Scanner kb = new Scanner(System.in);
        System.out.println("Type 1 for server, 2 for client");
        int selection = Integer.parseInt(kb.nextLine());

        try {

            if(selection == 1) {

                printExternalIP();

                TCPServer tcpServer = new TCPServer(port);

                //Receive 1 byte
                tcpServer.startServer(1);

                //receive 64 bytes
                tcpServer.startServer(64);

                //receive 1024 bytes
                tcpServer.startServer(1024);


                System.out.println("Successfully echoed all TCP responses");

                UDPServer udpServer = new UDPServer(port);

                //Receive 1 byte
                udpServer.startServer(1);

                //Receive 64 bytes
                udpServer.startServer(64);

                //Receive 1024 bytes
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



            } else if (selection == 2) {
                System.out.print("Server ip address: ");
                String ip = kb.nextLine();
                System.out.println("");
                tcpClient = new TCPClient(ip, port);
                udpClient = new UDPClient(ip, port);


                System.out.println("Measuring Round Trip Time");

                System.out.println("---TCP RTT's---");
                //send 1 byte
                sendTCPMessage("RTT for 1 byte:", 1);

                //send 64 byte
                sendTCPMessage("RTT for 64 bytes:", 64);

                //send 1024 byte
                sendTCPMessage("RTT for 1024 bytes:", 1024);


                System.out.println("---UDP RTT's---");
                sendUDPMessage("RTT for 1 byte:", 1);
                sendUDPMessage("RTT for 64 byte:", 64);
                sendUDPMessage("RTT for 1024 byte:", 1024);


                System.out.println("------------------------");

                System.out.println("Measuring throughput (TCP only)");

                measureTCPThroughput(1024);
                measureTCPThroughput(16384);
                measureTCPThroughput(65536);
                measureTCPThroughput(262144);
                measureTCPThroughput(1048576);

                System.out.println("------------------------");

                System.out.println("Measuring interaction between msg size and number of messages for 1MB");

                System.out.println("---TCP---");

                measureInteractionTCP(1024,1024);
                measureInteractionTCP(2048, 512);
                measureInteractionTCP(4096,256);

                System.out.println("---UDP---");
                measureInteractionUDP(1024, 1024);
                measureInteractionUDP(2048,512);
                measureInteractionUDP(4096, 256);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void measureInteractionTCP(int numMessages, int messageSize) throws IOException {

        System.out.println("Time to send " + numMessages + ", " + messageSize + " byte packets: " + convertNanoToMilli(tcpClient.send1MB(numMessages, messageSize)) + " Milliseconds");

    }

    public static void measureInteractionUDP(int numMessages, int messageSize) throws IOException {
        System.out.println("Time to send " + numMessages + ", " + messageSize + " byte packets: " + convertNanoToMilli(udpClient.send1MB(numMessages, messageSize)) + " Milliseconds");
    }


    public static void sendTCPMessage(String outputMessage, int numBytes) throws IOException {

        byte [] message = new byte[numBytes];
        Arrays.fill(message, (byte)1);
        long RTT = tcpClient.sendAndMeasureRTT(message);
        System.out.println(outputMessage +  " " + convertNanoToMs(RTT) + " microseconds");

    }

    public static void measureTCPThroughput(int numBytes) throws IOException {
        byte [] message = new byte[numBytes];
        Arrays.fill(message, (byte)1);
        long RTT = tcpClient.sendAndMeasureRTT(message);

        //throughout here in bits/nanosecond
        float throughput = (numBytes*8)/((float)(RTT/2));

        //convert to megabits/sec
        throughput = throughput*1000;
        
        System.out.println("Throughput for "+ numBytes + " : " + throughput + " Mbps");
            

    }

    public static void sendUDPMessage(String outputMessage, int numBytes) throws IOException {
        byte [] message = new byte[numBytes];
        Arrays.fill(message, (byte)1);
        long RTT =udpClient.sendAndMeasureRTT(message);
        System.out.println(outputMessage +  " " + convertNanoToMs(RTT) + " microseconds");

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

