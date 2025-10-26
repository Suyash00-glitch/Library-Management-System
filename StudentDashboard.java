import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class StudentDashboard {

    StudentDashboard(String loggedInUSN) {
        JFrame jfrm = new JFrame("Student Dashboard");
        jfrm.setSize(700, 600);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setLocationRelativeTo(null);

        // Main tabbed pane with theme
         JTabbedPane jtp = new JTabbedPane();
    jtp.setFont(new Font("Segoe UI", Font.BOLD, 16));
    jtp.setBackground(new Color(245, 245, 255)); // light bluish
    jtp.setForeground(new Color(40, 40, 80));    // dark tab titles

        // Add tabs
        jtp.addTab("Search Books", new SearchBooks());
        jtp.addTab("Profile", new Profile(loggedInUSN));

        jfrm.add(jtp);
        jfrm.setVisible(true);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new StudentDashboard("NNM24IS188"));
    }
}

// --------------------- SEARCH BOOKS PANEL (ADMIN DASHBOARD STYLE) ------------------------
class SearchBooks extends JPanel {
    SearchBooks() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        // Form panel with frame/border
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 250, 255));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 255), 2, true),
                "Search Books",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 80, 160)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lbId = new JLabel("Book ID:");
        lbId.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField jt1 = new JTextField(15);
        jt1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        jt1.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lbAuthor = new JLabel("Book Author:");
        lbAuthor.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField jt2 = new JTextField(15);
        jt2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        jt2.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lbMsg = new JLabel("");
        lbMsg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbMsg.setForeground(new Color(0, 120, 0));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lbId, gbc);
        gbc.gridx = 1;
        formPanel.add(jt1, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lbAuthor, gbc);
        gbc.gridx = 1;
        formPanel.add(jt2, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(lbMsg, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(245, 250, 255));
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchBtn.setBackground(new Color(100, 150, 255));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btnPanel.add(searchBtn);
        add(btnPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(ae -> {
            try {
                String id = jt1.getText();
                String author = jt2.getText();
                boolean success = showbooks(id, author);
                lbMsg.setText(success ? "Fetched successfully" : "No data found");
                lbMsg.setForeground(success ? new Color(0, 120, 0) : Color.RED);
            } catch (NumberFormatException e) {
                lbMsg.setText("Enter a valid numeric Book ID");
                lbMsg.setForeground(Color.RED);
            }
        });
    }

    // keep your existing showbooks logic unchanged
    boolean showbooks(String id, String author) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://mysql-2a105d13-suyashpatkar66-9638.i.aivencloud.com:22420/librarysys?useSSL=true&verifyServerCertificate=false",
                "avnadmin",
                "AVNS_aTyMe6O1dg4BxCmQGaY"
            );

            String checkqty = "SELECT * FROM books WHERE bookid = ?";
            PreparedStatement psmt1 = conn.prepareStatement(checkqty);
            psmt1.setString(1, id);
            ResultSet rs = psmt1.executeQuery();

            if (rs.next()) {
                String title = rs.getString("bookname");
                int qty = rs.getInt("bookquantity");
                String shelf=rs.getString("bookshelf");

                JOptionPane.showMessageDialog(
                        this,
                        "Book Found:\n\nID: " + id +
                                "\nAuthor: " + author +
                                "\nTitle: " + title +
                                "\nQuantity: " + qty+
                                "\nShelf No:"+shelf,
                        "Book Details",
                        JOptionPane.INFORMATION_MESSAGE
                );
                conn.close();
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "No book found with that ID.", "Not Found", JOptionPane.WARNING_MESSAGE);
                conn.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }
}

// --------------------- PROFILE PANEL -----------------------
class Profile extends JPanel {
    JLabel nameLabel, usnLabel, branchLabel, secLabel, yearLabel, emailLabel, contactLabel, imageLabel;
    JTable borrowedTable;
    DefaultTableModel model;

    public Profile(String loggedInUSN) {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        titlePanel.setPreferredSize(new Dimension(700, 60));
        JLabel title = new JLabel("Student Profile", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        // Top panel: Image + Info
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);

        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(160, 160));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
        imagePanel.add(imageLabel);

        JPanel infoPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        Font infoFont = new Font("Segoe UI", Font.BOLD, 16);
        nameLabel = new JLabel(); usnLabel = new JLabel(); branchLabel = new JLabel();
        secLabel = new JLabel(); yearLabel = new JLabel(); emailLabel = new JLabel(); contactLabel = new JLabel();

        JLabel[] labels = {nameLabel, usnLabel, branchLabel, secLabel, yearLabel, emailLabel, contactLabel};
        for (JLabel label : labels) { 
            label.setFont(infoFont); 
        }

        for (JLabel label : labels) infoPanel.add(label);

        topPanel.add(imagePanel, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Borrowed books table
        String[] columns = {"Book ID", "Book Name", "Author", "Issued Date", "Return Date", "Status"};
        model = new DefaultTableModel(columns, 0);
        borrowedTable = new JTable(model);
        borrowedTable.setRowHeight(25);
        borrowedTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        borrowedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        borrowedTable.setFillsViewportHeight(true);

        JScrollPane tableScroll = new JScrollPane(borrowedTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Borrowed Books"));
        add(tableScroll, BorderLayout.CENTER);

        loadStudentDetails(loggedInUSN);
        loadBorrowedBooks(loggedInUSN);
    }

    void loadStudentDetails(String usn) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://mysql-2a105d13-suyashpatkar66-9638.i.aivencloud.com:22420/librarysys?useSSL=true&verifyServerCertificate=false",
                "avnadmin",
                "AVNS_aTyMe6O1dg4BxCmQGaY"
            );

            String query = "SELECT * FROM studentinfo WHERE usn = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, usn);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameLabel.setText("Name: " + rs.getString("name"));
                usnLabel.setText("USN: " + rs.getString("usn"));
                branchLabel.setText("Branch: " + rs.getString("branch"));
                secLabel.setText("Section: " + rs.getString("sec"));
                yearLabel.setText("Year: " + rs.getString("year"));
                emailLabel.setText("Email: " + rs.getString("email"));
                contactLabel.setText("Contact: " + rs.getString("contact"));

                String imageUrl = rs.getString("imageurl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        ImageIcon icon = new ImageIcon(new java.net.URL(imageUrl));
                        Image img = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(img));
                        imageLabel.setText("");
                    } catch (Exception ex) {
                        imageLabel.setText("No Photo");
                    }
                } else imageLabel.setText("No Photo");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadBorrowedBooks(String usn) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://mysql-2a105d13-suyashpatkar66-9638.i.aivencloud.com:22420/librarysys?useSSL=true&verifyServerCertificate=false",
                "avnadmin",
                "AVNS_aTyMe6O1dg4BxCmQGaY"
            );

            String query = "SELECT ib.bookid, b.bookname, b.bookauthor, ib.issuedate, ib.returndate, " +
                           "CASE WHEN ib.returndate IS NULL THEN 'Issued' ELSE 'Returned' END AS status " +
                           "FROM issuedbooks ib JOIN booksnew b ON ib.bookid=b.bookid " +
                           "WHERE ib.studentusn=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, usn);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("bookid"),
                    rs.getString("bookname"),
                    rs.getString("bookauthor"),
                    rs.getDate("issuedate"),
                    rs.getDate("returndate"),
                    rs.getString("status")
                };
                model.addRow(row);
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
