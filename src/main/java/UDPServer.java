import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {

    int port;
    DatagramSocket socket;



    public UDPServer(int port) {
        this.port = port;
    }


    public void startServer(int numBytes) throws IOException {

        byte [] message = new byte[numBytes];

        DatagramPacket packet = new DatagramPacket(message, message.length);

        socket = new DatagramSocket(port);
        socket.receive(packet);

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(message, message.length, address, port);
        socket.send(packet);

        socket.close();
    }


}
