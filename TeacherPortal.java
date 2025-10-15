import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class TeacherPortal extends JFrame {
    private JTextField titleField;
    private JTextArea descArea;
    private DefaultTableModel projectModel, submissionModel;
    private JTable projectTable, submissionTable;
    private static final String PROJECTS_FILE = "C:/Users/lenovo/Downloads/ProjectSubmission/ProjectSubmission/src/projects.txt";
    private static final String SUBMISSION_LOG = "submissions_log.txt";

    public TeacherPortal(String teacherName){
        setTitle("Teacher Portal - " + teacherName);
        setSize(800,500);
        setLayout(new BorderLayout(10,10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top panel - username + logout
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel(teacherName);
        JButton logoutBtn = new JButton("\uD83D\uDEAA Logout"); // door icon
        logoutBtn.setForeground(Color.RED);
        logoutBtn.addActionListener(e -> {dispose(); new LoginPage();});
        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Project form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx=1;

        titleField = new JTextField(15);
        descArea = new JTextArea(3,15);
        JButton postBtn = new JButton("Post Project");
        postBtn.addActionListener(e -> postProject(teacherName));

        gbc.gridx=0; gbc.gridy=0;
        formPanel.add(new JLabel("Project Title:"), gbc);
        gbc.gridx=1;
        formPanel.add(titleField, gbc);

        gbc.gridx=0; gbc.gridy=1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx=1;
        formPanel.add(new JScrollPane(descArea), gbc);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2;
        formPanel.add(postBtn, gbc);

        add(formPanel, BorderLayout.WEST);

        // Project Table
        projectModel = new DefaultTableModel(new String[]{"Title","Description"},0);
        projectTable = new JTable(projectModel);
        loadProjects(teacherName);
        add(new JScrollPane(projectTable), BorderLayout.CENTER);

        // Submission Table
        submissionModel = new DefaultTableModel(new String[]{"Student","Project","Timestamp","Filename"},0);
        submissionTable = new JTable(submissionModel);
        loadSubmissions(teacherName);
        add(new JScrollPane(submissionTable), BorderLayout.EAST);

        setVisible(true);
    }

    private void postProject(String teacherName){
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();
        if(title.isEmpty() || desc.isEmpty()) return;

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PROJECTS_FILE,true))){
            bw.write(teacherName+","+title+","+desc);
            bw.newLine();
        }catch(IOException e){e.printStackTrace();}
        projectModel.addRow(new Object[]{title,desc});
        titleField.setText(""); descArea.setText("");
    }

    private void loadProjects(String teacherName){
        projectModel.setRowCount(0);
        File file = new File(PROJECTS_FILE);
        if(!file.exists()) return;
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                String[] parts = line.split(",",3);
                if(parts[0].equals(teacherName)){
                    projectModel.addRow(new Object[]{parts[1],parts[2]});
                }
            }
        }catch(IOException e){e.printStackTrace();}
    }

    private void loadSubmissions(String teacherName){
        submissionModel.setRowCount(0);
        File file = new File(SUBMISSION_LOG);
        if(!file.exists()) return;
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine())!=null){
                String[] parts = line.split(",",5);
                if(parts[1].equals(teacherName)){
                    submissionModel.addRow(new Object[]{parts[0],parts[2],parts[3],parts[4]});
                }
            }
        }catch(IOException e){e.printStackTrace();}
    }
}
