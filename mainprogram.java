import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

// Class to represent a student
class Student implements Serializable {
    int id;
    String name;
    float gpa;

    public Student(int id, String name, float gpa) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
    }
}

public class StudentManagementSystemGUI extends JFrame {
    private JTextField idField, nameField, gpaField, searchField;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private static final String FILE_NAME = "students.dat";

    public StudentManagementSystemGUI() {
        setTitle("Student Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel for adding new students
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add New Student"));

        inputPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Student GPA:"));
        gpaField = new JTextField();
        inputPanel.add(gpaField);

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(new AddStudentAction());
        inputPanel.add(addButton);

        // Panel for searching and displaying students
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Student"));

        searchField = new JTextField();
        searchPanel.add(new JLabel("Search by ID:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchStudentAction());
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Table to display student records
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "GPA"}, 0);
        studentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(studentTable);

        // Button to delete a student
        JButton deleteButton = new JButton("Delete Selected Student");
        deleteButton.addActionListener(new DeleteStudentAction());

        // Add all components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);
        add(deleteButton, BorderLayout.PAGE_END);

        loadStudents();
    }

    // Method to add a new student
    private class AddStudentAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                float gpa = Float.parseFloat(gpaField.getText());

                Student student = new Student(id, name, gpa);
                saveStudent(student);
                addStudentToTable(student);

                // Clear input fields
                idField.setText("");
                nameField.setText("");
                gpaField.setText("");

                JOptionPane.showMessageDialog(StudentManagementSystemGUI.this, "Student added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(StudentManagementSystemGUI.this, "Invalid input. Please enter valid data.");
            }
        }
    }

    // Method to search for a student by ID
    private class SearchStudentAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchId = searchField.getText();
            boolean found = false;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equals(searchId)) {
                    studentTable.setRowSelectionInterval(i, i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(StudentManagementSystemGUI.this, "Student with ID " + searchId + " not found.");
            }
        }
    }

    // Method to delete a selected student
    private class DeleteStudentAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                deleteStudent(id);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(StudentManagementSystemGUI.this, "Student deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(StudentManagementSystemGUI.this, "Please select a student to delete.");
            }
        }
    }

    // Load students from the file into the table
    private void loadStudents() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            while (true) {
                Student student = (Student) inputStream.readObject();
                addStudentToTable(student);
            }
        } catch (EOFException e) {
            // End of file reached
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading student data: " + e.getMessage());
        }
    }

    // Add a student to the table
    private void addStudentToTable(Student student) {
        tableModel.addRow(new Object[]{student.id, student.name, student.gpa});
    }

    // Save a student to the file
    private void saveStudent(Student student) {
        ArrayList<Student> students = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            while (true) {
                students.add((Student) inputStream.readObject());
            }
        } catch (EOFException | FileNotFoundException e) {
            // End of file or no file yet, proceed with saving
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while reading existing students: " + e.getMessage());
        }

        students.add(student);

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            for (Student s : students) {
                outputStream.writeObject(s);
            }
        } catch (IOException e) {
            System.out.println("Error while saving student: " + e.getMessage());
        }
    }

    // Delete a student by ID
    private void deleteStudent(int id) {
        ArrayList<Student> students = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            while (true) {
                Student student = (Student) inputStream.readObject();
                if (student.id != id) {
                    students.add(student);
                }
            }
        } catch (EOFException | FileNotFoundException e) {
            // End of file or no file yet
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while reading existing students: " + e.getMessage());
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            for (Student s : students) {
                outputStream.writeObject(s);
            }
        } catch (IOException e) {
            System.out.println("Error while saving student: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentManagementSystemGUI gui = new StudentManagementSystemGUI();
            gui.setVisible(true);
        });
    }
}
