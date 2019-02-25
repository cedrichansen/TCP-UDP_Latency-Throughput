import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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


    public void echo1MBServer(int numMessages, int messageSize) throws IOException {

        byte [] response = {(byte)1};

        byte [] messages = new byte [messageSize];
        DatagramPacket packet = new DatagramPacket(messages, messages.length);
        socket = new DatagramSocket(port);


        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        DatagramPacket resp = new DatagramPacket(response, response.length, address, port);


        for (int message = 0; message<numMessages; message++) {

            socket.receive(packet);
            socket.send(resp);

        }

        socket.close();

    }


}
