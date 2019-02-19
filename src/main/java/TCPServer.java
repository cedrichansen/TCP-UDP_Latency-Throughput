import java.io.*;
import java.net.*;

public class TCPServer {

    ServerSocket server;
    byte [] response;


    public TCPServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    public void startServer(int numBytes) throws IOException {
        byte [] message = new byte[numBytes];

        try {
            System.out.println("Waiting for client on port " + server.getLocalPort());
            Socket connectionSocket = server.accept();
            System.out.println("Accepted a new connection");
            DataInputStream input = new DataInputStream(connectionSocket.getInputStream());

            for (int i = 0; i < numBytes; i++) {
                message[i] = input.readByte();
            }

            DataOutputStream output = new DataOutputStream(connectionSocket.getOutputStream());
            output.write(message);
            System.out.println("Message echoed");

            server.close();
            connectionSocket.close();
            input.close();
            output.close();

        } catch (SocketTimeoutException s) {
        System.out.println("Socket timed out!");
        } catch (IOException e) {
        e.printStackTrace();
        }

    }
}
