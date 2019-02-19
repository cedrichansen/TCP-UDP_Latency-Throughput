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

    static TCPClient client;

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


                System.out.println("Successfully echoed all responses");



            } else if (selection == 2) {
                System.out.println("Server ip address");
                String ip = kb.nextLine();
                client = new TCPClient(ip, 2689);

                //send 1 byte
                sendMessage("RTT for 1 byte:", 1);

                //send 64 byte
                sendMessage("RTT for 64 bytes:", 64);

                //send 1024 byte
                sendMessage("RTT for 1024 bytes:", 1024);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void sendMessage(String outputMessage, int numBytes) throws IOException {

        byte [] message = new byte[numBytes];
        Arrays.fill(message, (byte)1);
        long RTT = client.sendAndMeasureRTT(message);
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

}

