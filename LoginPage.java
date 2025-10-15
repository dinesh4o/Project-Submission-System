import javax.swing.*;
import java.awt.*;
import java.io.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> checkLogin());

        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(loginBtn, gbc);

        setVisible(true);
    }

    private void checkLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please fill all fields!");
            return;
        }

        File file = new File("C:/Users/lenovo/Downloads/ProjectSubmission/ProjectSubmission/src/users.txt");
        if(!file.exists()){
            JOptionPane.showMessageDialog(this,"users.txt not found!");
            return;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                String[] parts = line.split(",",3);
                if(parts.length==3 && parts[0].equals(username) && parts[1].equals(password)){
                    dispose();
                    switch(parts[2]){
                        case "Admin" -> new AdminPortal(username);
                        case "Teacher" -> new TeacherPortal(username);
                        case "Student" -> new StudentPortal(username);
                    }
                    return;
                }
            }
        }catch(IOException e){e.printStackTrace();}

        JOptionPane.showMessageDialog(this,"Invalid credentials!");
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
