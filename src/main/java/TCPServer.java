import java.io.*;
import java.net.*;

public class TCPServer {

    ServerSocket server;
    int port;


    public TCPServer(int port) throws IOException {
        this.port = port;
    }

    public void startServer(int numBytes) throws IOException {

        server = new ServerSocket(this.port);
        byte[] message = new byte[numBytes];

        try {
            Socket connectionSocket = server.accept();
            DataInputStream input = new DataInputStream(connectionSocket.getInputStream());

            for (int i = 0; i < numBytes; i++) {
                message[i] = input.readByte();
            }

            DataOutputStream output = new DataOutputStream(connectionSocket.getOutputStream());
            output.write(message);

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


    //message size measured in bytes
    public void echo1MBServer(int numMessages, int messageSize) throws IOException {

        server = new ServerSocket(this.port);
        byte[] message = new byte[messageSize];

        byte response = (byte) 1;

        try {
            Socket connectionSocket = server.accept();
            DataInputStream input = new DataInputStream(connectionSocket.getInputStream());

            DataOutputStream output = new DataOutputStream(connectionSocket.getOutputStream());

            for (int messages = 0; messages < numMessages; messages++) {

                for (int i = 0; i < messageSize; i++) {
                    message[i] = input.readByte();
                }
                //once done reading the current message, acknowledge by sending back one byte;
                output.write(response);

            }

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
