import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class CourseEnrollmentSystem {

    private static final String STUDENTS_FILE = "students.txt";
    private static final String COURSES_FILE = "courses.txt";
    private static final String ENROLLMENTS_FILE = "enrollments.txt";
    private static final String PROFESSORS_FILE = "professors.txt";
    private static final String TEACHING_FILE = "teaching.txt";

    private JFrame frame;
    private JTextArea outputArea;
    private JTextField searchField;

    public static void main(String[] args) {
        initializeData();
        SwingUtilities.invokeLater(CourseEnrollmentSystem::new);
    }

    public CourseEnrollmentSystem() {
        setSystemLookAndFeel();

        frame = new JFrame("Course Enrollment System");
        frame.setSize(750, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(247, 247, 251));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(247, 247, 251));

        searchField = new JTextField();
        searchField.setBorder(new EmptyBorder(8, 8, 8, 8));
        searchField.setToolTipText("Search by course name...");
        topPanel.add(searchField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        buttonPanel.setBackground(new Color(247, 247, 251));

        JButton enrollButton = createButton("Enroll Student");
        JButton showButton = createButton("Show Enrollments");
        JButton studentsButton = createButton("Show Students");
        JButton coursesButton = createButton("Show Courses");
        JButton professorsButton = createButton("Show Professors");
        JButton teachingButton = createButton("Show Teaching");

        buttonPanel.add(enrollButton);
        buttonPanel.add(showButton);
        buttonPanel.add(studentsButton);
        buttonPanel.add(coursesButton);
        buttonPanel.add(professorsButton);
        buttonPanel.add(teachingButton);

        topPanel.add(buttonPanel, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        outputArea.setBackground(Color.WHITE);
        outputArea.setForeground(Color.BLACK);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(outputArea);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);

        enrollButton.addActionListener(e -> enrollStudentFlow());
        showButton.addActionListener(e -> showEnrollments());
        studentsButton.addActionListener(e -> showStudents());
        coursesButton.addActionListener(e -> showCourses());
        professorsButton.addActionListener(e -> showProfessors());
        teachingButton.addActionListener(e -> showTeaching());

        frame.setVisible(true);
    }

    // ===================== UI STYLE =====================
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(168, 218, 220)); // pastel blue
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        return button;
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    // ===================== APPLICATION FLOW =====================
    private void enrollStudentFlow() {
        String studentIdStr = JOptionPane.showInputDialog("Enter Student ID:");
        String courseIdStr = JOptionPane.showInputDialog("Enter Course ID:");

        if (studentIdStr == null || courseIdStr == null)
            return;

        try {
            enrollStudent(Integer.parseInt(studentIdStr), Integer.parseInt(courseIdStr));
        } catch (Exception e) {
            outputArea.setText("Invalid input");
        }
    }

    // ===================== BUSINESS LOGIC =====================
    private void enrollStudent(int studentId, int courseId) {

        Map<Integer, String[]> students = loadStudents();
        Map<Integer, String[]> courses = loadCourses();
        List<String> enrollments = loadEnrollments();

        if (!students.containsKey(studentId)) {
            outputArea.setText("Student not found");
            return;
        }

        if (!courses.containsKey(courseId)) {
            outputArea.setText("Course not found");
            return;
        }

        int count = 0;
        for (String e : enrollments) {
            if (Integer.parseInt(e.split(",")[1]) == courseId)
                count++;
        }

        if (count >= 5) {
            outputArea.setText("No seats available");
            return;
        }

        for (String e : enrollments) {
            String[] p = e.split(",");
            if (Integer.parseInt(p[0]) == studentId &&
                    Integer.parseInt(p[1]) == courseId) {
                outputArea.setText("Already enrolled");
                return;
            }
        }

        double price = (countCoursesByStudent(studentId, enrollments) >= 3) ? 85 : 100;

        saveEnrollment(studentId, courseId);

        String[] student = students.get(studentId);
        String[] course = courses.get(courseId);

        outputArea.setText(
                "Enrollment successful\n\n" +
                        student[0] + " " + student[1] +
                        " -> " + course[0] + " (" + course[1] + ")\n" +
                        "Price: $" + price);
    }

    private int countCoursesByStudent(int studentId, List<String> enrollments) {
        int count = 0;
        for (String e : enrollments) {
            if (Integer.parseInt(e.split(",")[0]) == studentId)
                count++;
        }
        return count;
    }

    // ===================== FILE HANDLING =====================
    private Map<Integer, String[]> loadStudents() {
        return loadFile(STUDENTS_FILE, 6);
    }

    private Map<Integer, String[]> loadCourses() {
        return loadFile(COURSES_FILE, 3);
    }

    private Map<Integer, String[]> loadProfessors() {
        return loadFile(PROFESSORS_FILE, 5);
    }

    private Map<Integer, String[]> loadFile(String file, int size) {
        Map<Integer, String[]> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                map.put(Integer.parseInt(p[0]), Arrays.copyOfRange(p, 1, size));
            }
        } catch (Exception ignored) {
        }
        return map;
    }

    private List<String> loadEnrollments() {
        return loadList(ENROLLMENTS_FILE);
    }

    private List<String> loadTeaching() {
        return loadList(TEACHING_FILE);
    }

    private List<String> loadList(String file) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null)
                list.add(line);
        } catch (Exception ignored) {
        }
        return list;
    }

    private void saveEnrollment(int studentId, int courseId) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ENROLLMENTS_FILE, true))) {
            bw.write(studentId + "," + courseId);
            bw.newLine();
        } catch (Exception ignored) {
        }
    }

    // ===================== UI OUTPUT =====================
    private void showEnrollments() {
        Map<Integer, String[]> students = loadStudents();
        Map<Integer, String[]> courses = loadCourses();
        List<String> enrollments = loadEnrollments();

        String filter = searchField.getText().toLowerCase();
        outputArea.setText("ENROLLMENTS:\n\n");

        for (String e : enrollments) {
            String[] p = e.split(",");
            String[] student = students.get(Integer.parseInt(p[0]));
            String[] course = courses.get(Integer.parseInt(p[1]));

            if (student == null || course == null)
                continue;
            if (!course[0].toLowerCase().contains(filter))
                continue;

            outputArea.append(student[0] + " " + student[1] +
                    " -> " + course[0] + " (" + course[1] + ")\n");
        }
    }

    private void showStudents() {
        loadStudents().forEach((k, v) -> outputArea.append(k + " - " + v[0] + " " + v[1] + "\n"));
    }

    private void showCourses() {
        loadCourses().forEach((k, v) -> outputArea.append(k + " - " + v[0] + "\n"));
    }

    private void showProfessors() {
        loadProfessors().forEach((k, v) -> outputArea.append(k + " - " + v[0] + " " + v[1] + "\n"));
    }

    private void showTeaching() {
        Map<Integer, String[]> professors = loadProfessors();
        Map<Integer, String[]> courses = loadCourses();

        outputArea.setText("PROFESSOR - COURSES:\n\n");

        for (String t : loadTeaching()) {
            String[] p = t.split(",");
            String[] prof = professors.get(Integer.parseInt(p[0]));
            String[] course = courses.get(Integer.parseInt(p[1]));

            if (prof == null || course == null)
                continue;

            outputArea.append(prof[0] + " " + prof[1] +
                    " -> " + course[0] + "\n");
        }
    }

    // ===================== INITIAL DATA =====================
    private static void initializeData() {
        createIfNotExists(STUDENTS_FILE, List.of("1,Ana,Perez,20,3,60"));
        createIfNotExists(COURSES_FILE, List.of("101,Programming I,A2"));
        createIfNotExists(PROFESSORS_FILE, List.of("1,Juan,Martinez,PhD,Full-Time"));
        createIfNotExists(TEACHING_FILE, List.of("1,101"));
        createIfNotExists(ENROLLMENTS_FILE, new ArrayList<>());
    }

    private static void createIfNotExists(String file, List<String> data) {
        File f = new File(file);
        if (!f.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                for (String d : data) {
                    bw.write(d);
                    bw.newLine();
                }
            } catch (Exception ignored) {
            }
        }
    }
}