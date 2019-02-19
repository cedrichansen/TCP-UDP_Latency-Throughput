import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main (String [] args) {
        Scanner kb = new Scanner(System.in);
        System.out.println("Type 1 for server, 2 for client");
        int selection = Integer.parseInt(kb.nextLine());

        try {

            if(selection == 1) {
                TCPServer server = new TCPServer(2689);
                server.startServer(1);
            } else if (selection == 2) {
                System.out.println("Server ip address");
                String ip = kb.nextLine();
                TCPClient client = new TCPClient(ip, 2689);

                byte [] message_1Byte = new byte[] {(byte)0x1a};
                long RTT_1Byte = client.sendAndMeasureRTT(message_1Byte);
                System.out.println(RTT_1Byte);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
