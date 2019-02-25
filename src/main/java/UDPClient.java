import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPClient {


    String ip;
    int port;

    DatagramSocket socket;
    InetAddress address;

    public UDPClient(String ip, int port ) throws UnknownHostException {
        this.ip = ip;
        this.port = port;
        address = InetAddress.getByName(ip);
    }


    public long sendAndMeasureRTT(byte [] message) throws IOException {
        socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(message, message.length, address, port);

        long start = System.nanoTime();
        socket.send(packet);
        packet = new DatagramPacket(message, message.length);
        socket.receive(packet);
        long totalTime = System.nanoTime() - start;

        socket.close();

        return totalTime;
    }


    public long send1MB(int numMessages, int messageSize) throws IOException {
        byte [] message = new byte[messageSize];
        Arrays.fill(message, (byte)1);

        byte [] response = new byte [1];

        socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(message, message.length, address, port);


        long start = System.nanoTime();


        for (int messages = 0; messages<numMessages; messages++) {
            socket.send(packet);
            DatagramPacket resp = new DatagramPacket(response, response.length);
            socket.receive(resp);
        }

        long totalTime = System.nanoTime() - start;

        socket.close();

        return totalTime;

    }





}
