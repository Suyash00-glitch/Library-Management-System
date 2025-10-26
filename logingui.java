import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import java.sql.*;

class SwingDemo {

    SwingDemo() {
        JFrame jfrm = new JFrame("Login Page");
        jfrm.setSize(400, 320);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setLocationRelativeTo(null);

        // Modern look & feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 250, 255)); // same light blue theme

        // Form panel with titled border like AddBook
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 250, 255));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 255), 2, true),
            "Login",
            0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 80, 160)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels and fields
        JLabel usnLabel = new JLabel("Enter USN:");
        usnLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField usnField = new JTextField(15);
        usnField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usnField.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel passLabel = new JLabel("Enter Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passField.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        String[] roles = {"Student", "Admin"};
        JComboBox<String> roleSelect = new JComboBox<>(roles);
        roleSelect.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        roleSelect.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel msgLabel = new JLabel(" ");
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Layout fields
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(usnLabel, gbc);
        gbc.gridx = 1; formPanel.add(usnField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(passLabel, gbc);
        gbc.gridx = 1; formPanel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(roleLabel, gbc);
        gbc.gridx = 1; formPanel.add(roleSelect, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginBtn.setBackground(new Color(100, 150, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        formPanel.add(loginBtn, gbc);

        gbc.gridy = 4;
        formPanel.add(msgLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        jfrm.add(mainPanel);
        jfrm.getContentPane().setBackground(new Color(230, 240, 255));
        jfrm.setVisible(true);

        // Action listener
        loginBtn.addActionListener(ae -> {
            String usn = usnField.getText();
            String pass = new String(passField.getPassword());
            String role = (String) roleSelect.getSelectedItem();

            boolean success = false;
            if (role.equals("Admin")) {
                success = checkAdminLogin(usn, pass);
                if (success) {
                    msgLabel.setText("Login Successful! (Admin)");
                    new AdminDashboard();
                    jfrm.dispose();
                }
            } else {
                success = checkStudentLogin(usn, pass);
                if (success) {
                    msgLabel.setText("Login Successful! (Student)");
                    new StudentDashboard(usn);
                    jfrm.dispose();
                }
            }

            if (!success) {
                msgLabel.setText("Invalid USN/Password for selected role");
            }
        });
    }

    boolean checkStudentLogin(String usn, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://mysql-2a105d13-suyashpatkar66-9638.i.aivencloud.com:22420/librarysys?useSSL=true&verifyServerCertificate=false",
                "avnadmin", "AVNS_aTyMe6O1dg4BxCmQGaY"
            );
            String query = "SELECT * FROM students WHERE studentusn=? AND password=?";
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, usn);
            psmt.setString(2, pass);
            ResultSet rs = psmt.executeQuery();
            boolean valid = rs.next();
            conn.close();
            return valid;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    boolean checkAdminLogin(String username, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://mysql-2a105d13-suyashpatkar66-9638.i.aivencloud.com:22420/librarysys?useSSL=true&verifyServerCertificate=false",
                "avnadmin", "AVNS_aTyMe6O1dg4BxCmQGaY"
            );
            String query = "SELECT * FROM admins WHERE adminid=? AND password=?";
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, username);
            psmt.setString(2, pass);
            ResultSet rs = psmt.executeQuery();
            boolean valid = rs.next();
            conn.close();
            return valid;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}

public class logingui {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingDemo());
    }
}
