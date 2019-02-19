import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
        //output.write(message.length);
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

}
