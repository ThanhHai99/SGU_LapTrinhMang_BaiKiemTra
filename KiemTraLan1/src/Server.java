import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
    public static final String PathName = "./src/dictionary.txt";

    public static String translate(String input) {
        try {
            File myObj = new File(PathName);
            Scanner myReader = new Scanner(myObj);

            HashMap<String, String> map1 = new HashMap<String, String>();
            HashMap<String, String> map2 = new HashMap<String, String>();

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] tmp = data.split(";");
                map1.put(tmp[0], tmp[1]);
                map2.put(tmp[1], tmp[0]);
            }
            myReader.close();

            for (Map.Entry<String, String> entry : map1.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(input)) {
                    return entry.getValue();
                }
            }
            for (Map.Entry<String, String> entry : map2.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(input)) {
                    return entry.getValue();
                }
            }
        } catch (FileNotFoundException e) {
            return "Lỗi hệ thống.";
            //e.printStackTrace();
        }
        return "Không tìm thấy từ trong từ điển 😢😢";
    }

    public static String addWords(String filePath, String input) {
        String t = input.substring(4);
        if (t.equals(";")) {
            return "Cú pháp thêm từ mới như sau: ADD;từ tiếng Anh;nghĩa tiếng Việt";
        }

        //dem dau cham phay sau khi cat chuoi
        int demDauChamPhay = 0;
        for (int i = 0; i < t.length(); i++) {
            char tmp = t.charAt(i);
            if(tmp == ';') {
                demDauChamPhay++;
            }
        }
        if (demDauChamPhay > 1 || demDauChamPhay == 0) {
            return "Cú pháp thêm từ mới như sau: ADD;từ tiếng Anh;nghĩa tiếng Việt";
        }


        if (t.endsWith(";")) {
            return "Cú pháp thêm từ mới như sau: ADD;từ tiếng Anh;nghĩa tiếng Việt";
        }

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            File file = new File(filePath);

            if(!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(t);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
                return "Thêm từ mới thành công.";
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return "Thêm từ mới thất bại.";
    }

    public static String removeWords(String filePath, String input) {
        String t = input.substring(4);
        if (t.equals(";")) {
            return "Cú pháp xóa từ mới như sau: DEL;từ tiếng Anh cần xóa";
        }

        //dem dau cham phay sau khi cat chuoi
        int demDauChamPhay = 0;
        for (int i = 0; i < t.length(); i++) {
            char tmp = t.charAt(i);
            if(tmp == ';') {
                demDauChamPhay++;
            }
        }
        if (demDauChamPhay > 0) {
            return "Cú pháp xóa từ mới như sau: DEL;từ tiếng Anh cần xóa";
        }

        boolean flag = false;

        try {
            File inFile = new File(filePath);
            if (!inFile.isFile()) {
                return "Parameter is not an existing file";
            }
            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(filePath));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.

            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(";");
                if (tmp[0].trim().equalsIgnoreCase(t)) {
                    flag = true;
                    continue;
                } else {
                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                return "Could not delete file";
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        if (flag == false) {
            return "Không tìm thấy từ cần xóa.";
        }

        return "Xóa thành công.";
    }

    public Server(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client...");
            Socket socket = server.accept();
            System.out.println("Client accepted");
            
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String line = "";
            String dataSend = "";

            do {
                //Receiver data from client
                line = in.readLine();

                if (line.startsWith("ADD;")) {
                    dataSend = addWords(PathName, line);
                }
                else if (line.startsWith("DEL;")) {
                    dataSend = removeWords(PathName, line);
                } else if (line.equalsIgnoreCase("bye")) {
                    break;
                } else {
                    dataSend = translate(line);
                }

                //Server send data to client
                out.write(dataSend);
                out.newLine();
                out.flush();
            } while (!line.equalsIgnoreCase("bye"));

            //CLOSE
            in.close();
            out.close();
            socket.close();
            server.close();
            System.out.println("Server closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
       Server server = new Server(5000);
    }
}
