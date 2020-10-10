import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public  Client(String address, int port) {
        try {
            Socket socket = new Socket(address, port);
            System.out.println("Connect to server successful");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            String line = "";

            do {
                //input data
                System.out.print("Enter keyword to translate: ");
                line = stdIn.readLine();
                if (line.equalsIgnoreCase("bye")) {
                    break;
                }

                //Client send data to server
                out.write(line);
                out.newLine();
                out.flush();
                
                //Receiver data from server
                String dt = in.readLine();
                if (dt != null) {
                    System.out.println("Client - Ket qua: " + dt);
                }
            } while (!line.equalsIgnoreCase("bye"));

            //CLOSE
            System.out.print("See you later ❤❤");
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Client client1 = new Client("localhost", 5000);
    }
}
