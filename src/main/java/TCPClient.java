import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class TCPClient {

    String ip;
    int port;


    public TCPClient (String ip, int port) {
        this.ip = ip;
        this.port = port;
    }


    //send 1, 64 and 1024 bytes using this, and return time
    public long sendAndMeasureRTT(byte [] message) throws IOException {
        byte [] response = new byte[message.length];

        Socket socket = new Socket(ip, port);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        long start = System.nanoTime();
        output.write(message);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        for (int i =0; i<message.length; i++) {
            response[i] = input.readByte();
        }
        long totalTime = System.nanoTime() - start;

        socket.close();
        output.close();
        input.close();

        return totalTime;

    }

    public long send1MB(int numMessages, int messageSize) throws IOException {
        byte [] responses = new byte [numMessages];
        byte [] message = new byte[messageSize];
        Arrays.fill(message, (byte)1);

        Socket socket = new Socket(ip, port);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        DataInputStream input = new DataInputStream(socket.getInputStream());

        long start = System.nanoTime();


        try {
            for (int messages = 0; messages <numMessages; messages++) {
                output.write(message);
                responses[messages] = input.readByte();
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Socket timed out");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long totalTime = System.nanoTime() - start;

        socket.close();
        output.close();
        input.close();


        //Sleep for a split second so that the server has a chance to restart
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            e.printStackTrace();
        }

        return totalTime;

    }




}
