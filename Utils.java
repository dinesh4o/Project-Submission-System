import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static void addUser(String username, String password, String role) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
            bw.write(username + "," + password + "," + role);
            bw.newLine();
        }
    }

    public static List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        File file = new File("users.txt");
        if (!file.exists()) return users;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null){
                String[] parts = line.split(",", 3);
                if(parts.length == 3) users.add(parts);
            }
        } catch(IOException e){ e.printStackTrace();}
        return users;
    }
}
