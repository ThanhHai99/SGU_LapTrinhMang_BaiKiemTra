import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.*; //add json library

public class Server {

    private static int buffsize = 512;
    private static int port = 1234;
    private static String keyAPI = "55ddade3-75da-433f-bc27-9f426b200274";

    private static HttpURLConnection conn = null;

    private static boolean validateString(String input) {
        Pattern pattern = null;

        if (input.split(";").length == 1)
            pattern = Pattern.compile("[^\\d\\;]+");
        if (input.split(";").length == 2)
            pattern = Pattern.compile("[^\\d\\;]+;[^\\d\\;]+");
        if (input.split(";").length == 3)
            pattern = Pattern.compile("[^\\d\\;]+;[^\\d\\;]+;[^\\d\\;]+");
        if (pattern == null)
            return false;
        return  pattern.matcher(input).matches();
    }

    private static String getError() {
        Scanner sc = new Scanner(conn.getErrorStream());
        String inline = "";
        while (sc.hasNext())
            inline += sc.nextLine();
        sc.close();
        JSONObject obj = new JSONObject(inline);
        return obj.getJSONObject("data").getString("message");
    }

    private static int statusCode(URL url) throws IOException {
        conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        return conn.getResponseCode();
    }

    private static String getData(URL url, String getObjectName, String title) throws IOException {
        Scanner sc = new Scanner(url.openStream());
        String inline = "";
        while (sc.hasNext())
            inline += sc.nextLine();
        sc.close();

        JSONObject obj = new JSONObject(inline);
        JSONArray arr = obj.getJSONArray("data");
        ArrayList<String> tmp = new ArrayList<String>();
        for (int i = 0; i < arr.length(); i++) {
            String element = arr.getJSONObject(i).getString(getObjectName);
            tmp.add(element);
        }
        String result = title;
        for (int i=0;i<tmp.size();i++) {
            result += tmp.get(i);
            if (i != tmp.size() - 1)
                result += ", ";
        }
        return result;
    }

    private static String Cau1() throws IOException {
        URL url = new URL("https://api.airvisual.com/v2/countries?key=" + keyAPI);
        if (statusCode(url) == 200)
            return getData(url, "country", "List country: ");
        return getError();
    }

    private static String Cau2(String country) throws IOException{
        URL url = new URL("https://api.airvisual.com/v2/states?country=" + URLEncoder.encode(country, "UTF-8") + "&key=" + keyAPI);
        if (statusCode(url) == 200)
            return getData(url, "state", "List state: ");
        return getError();
    }

    private static String Cau3(String state, String country) throws IOException {
        URL url = new URL("https://api.airvisual.com/v2/cities?state="+ URLEncoder.encode(state, "UTF-8") +"&country=" + URLEncoder.encode(country, "UTF-8") + "&key=" + keyAPI);
        if (statusCode(url) == 200)
            return getData(url, "city", "List city: ");
        return getError();
    }

    private static String Cau4(String city, String state, String country) throws IOException {
        URL url = new URL("https://api.airvisual.com/v2/city?city=" + URLEncoder.encode(city, "UTF-8") + "&state="+ URLEncoder.encode(state, "UTF-8") +"&country=" + URLEncoder.encode(country, "UTF-8") + "&key=" + keyAPI);
        if (statusCode(url) == 200) {
            Scanner sc = new Scanner(url.openStream());
            String inline = "";
            while (sc.hasNext())
                inline += sc.nextLine();
            sc.close();

            JSONObject obj = new JSONObject(inline);
            String cityName =  obj.getJSONObject("data").getString("city");
            int aqiusIndex =  obj.getJSONObject("data").getJSONObject("current").getJSONObject("pollution").getInt("aqius");
            String result = cityName + "'s aqius index is: " + aqiusIndex;
            return result;
        } else {
            return getError();
        }
    }

    public static void main(String[] args) throws IOException {
        DatagramSocket socket;
        DatagramPacket dpreceive, dpsend;

        socket = new DatagramSocket(port);
        dpreceive = new DatagramPacket(new byte[buffsize], buffsize);
        System.out.println("Server is ready!");
        while(true) {
            //receive data from client
            socket.receive(dpreceive);
            String dataReceived = new String(dpreceive.getData(), 0 , dpreceive.getLength());
            System.out.println("Server received from: " + dpreceive.getAddress().getHostAddress() + ":" + socket.getLocalPort() + "; data: " + dataReceived );

            //catch stop server
            if(dataReceived.equals("bye")) {
                System.out.println("Server closed!");
                socket.close();
                break;
            }

            String dataSend = "";
            String[] req = dataReceived.split(";");

            if (!validateString(dataReceived)) {
                dataSend = "Syntax error.";
            } else if (dataReceived.equals("")) {
                dataSend = "You have not enter anything.";
            } else if (dataReceived.equalsIgnoreCase("Hello")) {
                dataSend = Cau1();
            } else if (req.length == 1) {
                dataSend = Cau2(req[0]);
            } else if (req.length == 2) {
                dataSend = Cau3(req[1], req[0]);
            } else if (req.length == 3) {
                dataSend = Cau4(req[2], req[1], req[0]);
            }

            //send result to client
            dpsend = new DatagramPacket(dataSend.getBytes(), dataSend.getBytes().length, dpreceive.getAddress(), dpreceive.getPort());
            System.out.println("Server sent: " + dataSend);
            socket.send(dpsend);
        }
    }

}
