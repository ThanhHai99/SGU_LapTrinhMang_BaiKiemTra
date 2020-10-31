import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {

    private static int destPort = 1234;
    private static String hostname = "localhost";

    public static void main(String[] args) {
        DatagramSocket socket;
        DatagramPacket dpsend, dpreceive;
        InetAddress add; Scanner stdIn;
        System.out.println("Client is ready!");

        try {
            add = InetAddress.getByName(hostname);
            socket = new DatagramSocket();
            stdIn = new Scanner(System.in);

            while(true) {
                //input data
                System.out.print("Client input: ");
                String dataSend = stdIn.nextLine();
                byte[] data = dataSend.getBytes();

                //send
                dpsend = new DatagramPacket(data, data.length, add, destPort);
                System.out.println("Client sent to: " + add.getHostAddress() + " by port " + socket.getLocalPort() + "; data: " + dataSend);
                socket.send(dpsend);

                //catch stop server
                if(dataSend.equals("bye")) {
                    System.out.println("Client socket closed!");
                    stdIn.close();
                    socket.close();
                    break;
                }

                // Get response from server
                dpreceive = new DatagramPacket(new byte[512], 512);
                socket.receive(dpreceive);
                String dataReceived = new String(dpreceive.getData(), 0, dpreceive.getLength());
                System.out.println("Client get: " + dataReceived);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
