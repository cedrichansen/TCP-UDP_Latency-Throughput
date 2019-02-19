import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main (String [] args) {
        Scanner kb = new Scanner(System.in);
        System.out.println("Type 1 for server, 2 for client");
        int selection = Integer.parseInt(kb.nextLine());

        try {

            if(selection == 1) {

                printExternalIP();

                TCPServer server = new TCPServer(2689);

                //Receive 1 byte
                server.startServer(1);

                //receive 64 bytes
                server.startServer(64);

                //receive 1024 bytes
                server.startServer(1024);




            } else if (selection == 2) {
                System.out.println("Server ip address");
                String ip = kb.nextLine();
                TCPClient client = new TCPClient(ip, 2689);

                //send 1 byte
                byte [] message_1Byte = new byte[1];
                Arrays.fill(message_1Byte, (byte)1);
                long RTT_1Byte = client.sendAndMeasureRTT(message_1Byte);
                System.out.println("RTT for 1 byte: " + convertNanoToMs(RTT_1Byte) +"ms");

                //send 64 byte
                byte [] message_64Byte = new byte[64];
                Arrays.fill(message_64Byte, (byte)1);
                long RTT_64Byte = client.sendAndMeasureRTT(message_64Byte);
                System.out.println("RTT for 64 bytes: " + convertNanoToMs(RTT_64Byte)+"ms");

                //send 1024 byte
                byte [] message_1024Byte = new byte[1024];
                Arrays.fill(message_1024Byte, (byte)1);
                long RTT_1024Byte = client.sendAndMeasureRTT(message_1024Byte);
                System.out.println("RTT for 1024 bytes: " + convertNanoToMs(RTT_1024Byte)+"ms");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


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
        System.out.println("Public IP Address: " + systemipaddress +"\n");
    }


    public static long convertNanoToMs(long time) {
        return TimeUnit.MICROSECONDS.convert(time, TimeUnit.NANOSECONDS);
    }

}

