import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentPortal extends JFrame {

    private JComboBox<String> projectDropdown;
    private JTextArea projectDetails;
    private DefaultTableModel submissionModel;
    private JTable submissionTable;

    private static final String PROJECTS_FILE = "C:/Users/lenovo/Downloads/ProjectSubmission/ProjectSubmission/src/projects.txt";
    private static final String SUBMISSIONS_FOLDER = "submissions";
    private static final String SUBMISSION_LOG = "submissions_log.txt";

    public StudentPortal(String studentName) {
        setTitle("Student Portal - " + studentName);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // --- Top panel: username + logout ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        JLabel userLabel = new JLabel("Logged in as: " + studentName);
        JButton logoutBtn = new JButton("\uD83D\uDEAA Logout");
        logoutBtn.setForeground(Color.RED);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginPage();
        });
        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- Left panel: project selection ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Projects"));
        leftPanel.setPreferredSize(new Dimension(250,0));

        projectDropdown = new JComboBox<>();
        loadProjects();
        projectDropdown.setMaximumSize(new Dimension(220,25));
        projectDropdown.addActionListener(e -> showProjectDetails());
        leftPanel.add(new JLabel("Select Project:"));
        leftPanel.add(Box.createRigidArea(new Dimension(0,5)));
        leftPanel.add(projectDropdown);

        add(leftPanel, BorderLayout.WEST);

        // --- Center panel: project details + upload ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(5,5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Project Details"));

        projectDetails = new JTextArea();
        projectDetails.setEditable(false);
        projectDetails.setLineWrap(true);
        projectDetails.setWrapStyleWord(true);
        JScrollPane detailScroll = new JScrollPane(projectDetails);
        detailScroll.setPreferredSize(new Dimension(350,200));
        centerPanel.add(detailScroll, BorderLayout.CENTER);

        JButton uploadBtn = new JButton("Upload Project");
        uploadBtn.addActionListener(e -> uploadProject(studentName));
        JPanel uploadPanel = new JPanel();
        uploadPanel.add(uploadBtn);
        centerPanel.add(uploadPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // --- Right panel: submission table ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("My Submissions"));
        rightPanel.setPreferredSize(new Dimension(400,0));

        submissionModel = new DefaultTableModel(new String[]{"Project","Teacher","Timestamp","Filename"},0);
        submissionTable = new JTable(submissionModel);
        JScrollPane submissionScroll = new JScrollPane(submissionTable);
        rightPanel.add(submissionScroll, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        loadSubmissions(studentName); // show past submissions
        setVisible(true);
    }

    private void loadProjects() {
        projectDropdown.removeAllItems();
        File file = new File(PROJECTS_FILE);
        if(!file.exists()) return;

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                projectDropdown.addItem(line);
            }
        } catch(IOException e) { e.printStackTrace(); }
    }

    private void showProjectDetails() {
        String selected = (String) projectDropdown.getSelectedItem();
        if(selected == null) return;

        String[] parts = selected.split(",",3);
        if(parts.length >= 3) {
            projectDetails.setText(
                    "Teacher: " + parts[0] + "\n" +
                            "Title: " + parts[1] + "\n" +
                            "Description: " + parts[2]
            );
        }
    }

    private void uploadProject(String studentName) {
        String selected = (String) projectDropdown.getSelectedItem();
        if(selected == null) {
            JOptionPane.showMessageDialog(this,"No project selected!");
            return;
        }

        String[] parts = selected.split(",",3);
        String teacherName = parts[0];
        String projectTitle = parts[1];

        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);
        if(option != JFileChooser.APPROVE_OPTION) return;

        File src = chooser.getSelectedFile();
        if(!src.exists()) return;

        new File(SUBMISSIONS_FOLDER).mkdirs();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        Path dest = Paths.get(SUBMISSIONS_FOLDER,teacherName+"_"+studentName+"_"+timeStamp+"_"+src.getName());

        try {
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            // log submission
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(SUBMISSION_LOG,true))) {
                bw.write(studentName+","+teacherName+","+projectTitle+","+timeStamp+","+src.getName());
                bw.newLine();
            }

            JOptionPane.showMessageDialog(this,"Uploaded Successfully!");
            loadSubmissions(studentName);  // refresh submission table
        } catch(IOException e){ e.printStackTrace();}
    }

    private void loadSubmissions(String studentName) {
        submissionModel.setRowCount(0);
        File file = new File(SUBMISSION_LOG);
        if(!file.exists()) return;

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(",",5);
                if(parts[0].equals(studentName)) {
                    submissionModel.addRow(new Object[]{parts[2],parts[1],parts[3],parts[4]});
                }
            }
        } catch(IOException e){ e.printStackTrace();}
    }
}
