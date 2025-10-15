import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class AdminPortal extends JFrame {
    private DefaultTableModel model;
    private JTable table;

    public AdminPortal(String adminName) {
        setTitle("Admin Portal - " + adminName);
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ----- Top Panel: username + logout -----
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel(adminName);
        JButton logoutBtn = new JButton("\uD83D\uDEAA Logout"); // door icon
        logoutBtn.setForeground(Color.RED);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginPage();
        });
        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ----- Table panel: show existing users -----
        model = new DefaultTableModel(new String[]{"Username","Password","Role"},0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // all cells editable
            }
        };
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Load users
        loadUsers();

        // Listen for edits and save automatically
        model.addTableModelListener(e -> {
            if(e.getType() == TableModelEvent.UPDATE) saveUsersToFile();
        });

        // ----- Bottom panel: add new user -----
        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField unameField = new JTextField(10);
        JTextField passField = new JTextField(10);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Teacher","Student"});
        JButton addBtn = new JButton("Add User");

        addBtn.addActionListener(e -> {
            String uname = unameField.getText().trim();
            String pass = passField.getText().trim();
            String role = (String) roleBox.getSelectedItem();
            if(uname.isEmpty() || pass.isEmpty()){
                JOptionPane.showMessageDialog(this,"Please fill all fields!");
                return;
            }
            model.addRow(new Object[]{uname,pass,role}); // will auto-save via listener
            unameField.setText(""); passField.setText("");
        });

        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(unameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passField);
        inputPanel.add(new JLabel("Role:"));
        inputPanel.add(roleBox);
        inputPanel.add(addBtn);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadUsers(){
        model.setRowCount(0);
        File file = new File("C:/Users/lenovo/Downloads/ProjectSubmission/ProjectSubmission/src/users.txt");
        if(!file.exists()) return;
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                String[] parts = line.split(",",3);
                if(parts.length==3) model.addRow(parts);
            }
        }catch(IOException e){ e.printStackTrace(); }
    }

    private void saveUsersToFile(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("src/users.txt"))){
            for(int i=0;i<model.getRowCount();i++){
                String uname = model.getValueAt(i,0).toString();
                String pass = model.getValueAt(i,1).toString();
                String role = model.getValueAt(i,2).toString();
                bw.write(uname+","+pass+","+role);
                bw.newLine();
            }
        }catch(IOException e){ e.printStackTrace(); }
    }
}
