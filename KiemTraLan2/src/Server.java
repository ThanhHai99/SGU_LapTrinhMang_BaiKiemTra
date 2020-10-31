import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.*; //add json library

public class Server {

    private static int buffsize = 512;
    private static int port = 1234;
    private static String pathName = "./src/txt.txt";
//    private static ArrayList<String> countryStorage = new ArrayList<String>();
//    private static ArrayList<String> stateStorage = new ArrayList<String>();
    private static String keyAPI = "55ddade3-75da-433f-bc27-9f426b200274";

    private static String Cau1() {
        try {
            URL url = new URL("https://api.airvisual.com/v2/countries?key=" + keyAPI);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode == 200) {
                Scanner sc = new Scanner(url.openStream());
                String inline = "";
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
//                JSONObject obj = new JSONObject(inline);
//                JSONArray arr = obj.getJSONArray("data");
//                for (int i = 0; i < arr.length(); i++) {
//                    String country = arr.getJSONObject(i).getString("country");
//                    countryStorage.add(country);
//                }
                sc.close();
                return inline;
            } else {
                return "Không tìm thấy.";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Có lỗi";
    }

    private static String Cau2(String country) {
        try {
            URL url = new URL("https://api.airvisual.com/v2/states?country=" + URLEncoder.encode(country, "UTF-8") + "&key=" + keyAPI);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode == 200) {
                Scanner sc = new Scanner(url.openStream());
                String inline = "";
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
                sc.close();

                JSONObject obj = new JSONObject(inline);
                String error = obj.getString("status");
                if (error.equals("fail")) {
                    return "Lỗi, bạn hãy nhập lại!";
                } else {
                    return inline;
                }
            } else {
                return "Không tìm thấy.";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Có lỗi!";
    }

    private static String Cau3(String state, String country) {
        try {
            URL url = new URL("https://api.airvisual.com/v2/cities?state="+ URLEncoder.encode(state, "UTF-8") +"&country=" + URLEncoder.encode(country, "UTF-8") + "&key=" + keyAPI);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode == 200) {
                Scanner sc = new Scanner(url.openStream());
                String inline = "";
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
                sc.close();

                JSONObject obj = new JSONObject(inline);
                String error = obj.getString("status");
                if (error.equals("fail")) {
                    return "Lỗi, bạn hãy nhập lại!";
                } else {
                    return inline;
                }
            } else {
                return "Không tìm thấy.";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Có lỗi";
    }

    private static String Cau4(String city, String state, String country) {
        try {
            URL url = new URL("https://api.airvisual.com/v2/city?city=" + URLEncoder.encode(city, "UTF-8") + "&state="+ URLEncoder.encode(state, "UTF-8") +"&country=" + URLEncoder.encode(country, "UTF-8") + "&key=" + keyAPI);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode == 200) {
                Scanner sc = new Scanner(url.openStream());
                String inline = "";
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
                sc.close();

                JSONObject obj = new JSONObject(inline);
                String error = obj.getString("status");
                if (error.equals("fail")) {
                    return "Lỗi, bạn hãy nhập lại!";
                } else {
                    return inline;
                }
            } else {
                return "Không tìm thấy.";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Có lỗi";
    }



    public static void main(String[] args) {
        DatagramSocket socket;
        DatagramPacket dpreceive, dpsend;
        try {
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

                String[] req = dataReceived.split(":");

                if (dataReceived.equals("")) {
                    dataSend = "Bạn chưa nhập gì cả.";
                } else if (dataReceived.equals("Hello")) {
                    dataSend = Cau1();
                } else if (req.length == 1) {
//                    if(countryStorage.isEmpty())
//                        Cau1(); //save data to coutry storage
//                    if (countryStorage.contains(req[0]))
                        dataSend = Cau2(req[0]);
//                    else
//                        dataSend = "Không tồn tại quốc gia này.";

                } else if (req.length == 2) {
//                    if(countryStorage.isEmpty())
//                        Cau1(); //save data to coutry storage
//                    if (!countryStorage.contains(req[0]))
//                        dataSend = "Không tồn tại quốc gia này.";
//                    if (!countryStorage.contains(req[0]))
//                        dataSend = "Không tồn tại quốc gia này.";
//                    if (countryStorage.contains(dataReceived))
                        dataSend = Cau3(req[1], req[0]);
//                    else
//                        dataSend = "Không tồn tại quốc gia này.";
                } else if (req.length == 3) {
                    dataSend = Cau4(req[2], req[1], req[0]);
                } else {
                    dataSend = "Lỗi cú pháp";
                }

                //send result to client
                dpsend = new DatagramPacket(dataSend.getBytes(), dataSend.getBytes().length, dpreceive.getAddress(), dpreceive.getPort());
                System.out.println("Server sent: " + dataSend);
                socket.send(dpsend);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
