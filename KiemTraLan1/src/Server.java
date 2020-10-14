/*
* Run by IntelliJ
* */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Server {
    private static final String PathName = "./src/dictionary.txt";

    private boolean validateString(String input) {
        Pattern pattern = null;

        if (input.substring(0,4).equalsIgnoreCase("ADD;")) {
            pattern = Pattern.compile("^[^\\s\\d\\;]+;[^\\d\\;]+");
        } else if (input.substring(0,4).equalsIgnoreCase("DEL;")) {
            pattern = Pattern.compile("^[^\\s\\d\\;]+");
        }

        if (pattern == null)
            return false;

        return  pattern.matcher(input.substring(4)).matches();
    }

    private String translate(String input) {
        try {
            File myObj = new File(PathName);
            Scanner scanner = new Scanner(myObj);

            HashMap<String, String> map1 = new HashMap<String, String>();
            HashMap<String, String> map2 = new HashMap<String, String>();

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] tmp = data.split(";");
                map1.put(tmp[0], tmp[1]);
                map2.put(tmp[1], tmp[0]);
            }
            scanner.close();

            for (Map.Entry<String, String> entry : map1.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(input))
                    return entry.getValue();
            }
            for (Map.Entry<String, String> entry : map2.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(input))
                    return entry.getValue();
            }
            
        } catch (FileNotFoundException e) {
            return "Lỗi hệ thống.";
            //e.printStackTrace();
        }
        return "Không tìm thấy từ trong từ điển 😢😢";
    }

    private boolean checkExistsWord(String input) {
        try {
            File myObj = new File(PathName);
            Scanner scanner = new Scanner(myObj);

            HashMap<String, String> map = new HashMap<String, String>();

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] tmp = data.split(";");
                map.put(tmp[0], tmp[1]);
            }
            scanner.close();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(input))
                    return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return  false;
    }

    private boolean addLineToFile(String filePath, String input) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            File file = new File(filePath);

            if(!file.exists()) file.createNewFile();

            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.newLine();
            bw.write(input.substring(4));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    private String addWord(String filePath, String input) {
        //Kiểm tra cú pháp
        if (validateString(input) == false)
            return "Cú pháp thêm từ mới như sau: ADD;từ tiếng Anh;nghĩa tiếng Việt";

        //Kiểm tra từ tồn tại hay không
        if (checkExistsWord(input.substring(4).split(";")[0]))
            return "Từ này đã tồn tại";

        // Xứ lý file========================================================
        if (addLineToFile(filePath, input))
            return "Thêm từ mới thành công";

        return "Thêm từ mới thất bại.";
    }

    private String removeLineByWord(String filePath, String input) {
        try {
            File inFile = new File(filePath);
            if (!inFile.isFile())  return "Lỗi file";

            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(filePath));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = "";
            boolean f = false; // run once
            boolean status = false;

            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(";");
                if (tmp[0].trim().equalsIgnoreCase(input.substring(4)) == false) {
                    if (!f) {
                        pw.print(line.trim());
                        pw.flush();
                        f=true;
                        continue;
                    }
                    pw.printf("\n%s", line.trim());
                    pw.flush();
                }

                if (tmp[0].trim().equalsIgnoreCase(input.substring(4))) {
                    status=true;
                }
            }

            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete())
                return "Lỗi file";

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Lỗi file");

            if (status == true)
                return "Xóa thành công.";
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return "Không tìm thấy từ cần xóa.";
    }

    private String removeWord(String filePath, String input) {
        if (validateString(input) == false)
            return "Cú pháp xóa từ vựng như sau: DEL;từ tiếng Anh cần xóa";

        return removeLineByWord(filePath, input);
    }

    private Server(int port) {
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

                if (line.equalsIgnoreCase("bye")) {
                    break;
                } else if (line.startsWith("ADD;")) {
                    dataSend = addWord(PathName, line);
                } else if (line.startsWith("DEL;")) {
                    dataSend = removeWord(PathName, line);
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
            System.err.println("Server closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
       Server server = new Server(5000);
    }
}
