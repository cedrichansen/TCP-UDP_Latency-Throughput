import java.io.IOException;
import java.net.*;

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





}
