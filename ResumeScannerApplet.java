import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
<applet code="ResumeScannerApplet.class" width="620" height="520">
</applet>
*/

public class ResumeScannerApplet extends Applet implements ActionListener {

    private TextField keywordField;
    private TextArea resumeArea;
    private Button scanBtn, loadBtn, clearBtn;

    private String matchedText = "None";
    private String missingText = "None";
    private String statusText = "Waiting for scan...";
    private double matchScore = 0.0;
    private boolean scanned = false;

    @Override
    public void init() {
        setLayout(null); // Absolute positioning so AWT components and paint() don't overlap
        setBackground(new Color(245, 247, 250));

        // 1. Header Label
        Label title = new Label("ATS RESUME SCANNER (JAVA APPLET)", Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBounds(20, 15, 580, 25);
        add(title);

        // 2. Custom Job Keywords Input
        Label kwLabel = new Label("Job Keywords (comma-separated):");
        kwLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        kwLabel.setBounds(20, 50, 215, 25);
        add(kwLabel);

        keywordField = new TextField("Java, OOP, SQL, HTML, CSS, Flask, Git");
        keywordField.setBounds(235, 50, 365, 25);
        add(keywordField);

        // 3. Resume Text Area + Load File Button
        Label resLabel = new Label("Paste Resume Text Below (or load from resume.txt):");
        resLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        resLabel.setBounds(20, 85, 350, 25);
        add(resLabel);

        loadBtn = new Button("Load resume.txt");
        loadBtn.setBounds(460, 83, 140, 26);
        loadBtn.addActionListener(this);
        add(loadBtn);

        resumeArea = new TextArea(
            "BTech Student skilled in Java, OOP, HTML, CSS, and Git.\n" +
            "Built a web project using Flask and SQL database.",
            6, 60, TextArea.SCROLLBARS_VERTICAL_ONLY
        );
        resumeArea.setBounds(20, 115, 580, 145);
        add(resumeArea);

        // 4. Action Buttons
        scanBtn = new Button("Scan Resume");
        scanBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        scanBtn.setBackground(new Color(37, 99, 235));
        scanBtn.setForeground(Color.WHITE);
        scanBtn.setBounds(20, 275, 280, 34);
        scanBtn.addActionListener(this);
        add(scanBtn);

        clearBtn = new Button("Clear");
        clearBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        clearBtn.setBounds(320, 275, 280, 34);
        clearBtn.addActionListener(this);
        add(clearBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadBtn) {
            loadResumeFromFile("resume.txt");
        } else if (e.getSource() == clearBtn) {
            resumeArea.setText("");
            scanned = false;
            statusText = "Cleared. Ready for new scan.";
            repaint();
        } else if (e.getSource() == scanBtn) {
            scanResume();
        }
    }

    // Reads resume.txt from the project directory using JDK 8 compatible BufferedReader
    private void loadResumeFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            resumeArea.setText(content.toString());
            statusText = "Loaded '" + fileName + "' successfully! Click 'Scan Resume'.";
        } catch (IOException ex) {
            statusText = "Error: '" + fileName + "' not found in current folder.";
        }
        repaint();
    }

    // Core ATS keyword matching logic
    private void scanResume() {
        String resumeText = resumeArea.getText().toLowerCase();
        String[] rawKeywords = keywordField.getText().split(",");

        ArrayList<String> matched = new ArrayList<>();
        ArrayList<String> missing = new ArrayList<>();
        int totalValid = 0;

        for (String kw : rawKeywords) {
            String skill = kw.trim().toLowerCase();
            if (skill.isEmpty()) {
                continue;
            }
            totalValid++;

            if (resumeText.contains(skill)) {
                matched.add(skill.toUpperCase());
            } else {
                missing.add(skill.toUpperCase());
            }
        }

        if (totalValid > 0) {
            matchScore = ((double) matched.size() / totalValid) * 100.0;
        } else {
            matchScore = 0.0;
        }

        matchedText = matched.isEmpty() ? "None" : String.join(", ", matched);
        missingText = missing.isEmpty() ? "None" : String.join(", ", missing);

        if (matchScore >= 70) {
            statusText = "SHORTLISTED FOR INTERVIEW";
        } else if (matchScore >= 40) {
            statusText = "HOLD (Moderate Skill Match)";
        } else {
            statusText = "REJECTED (Low Skill Match)";
        }

        scanned = true;
        repaint(); // Triggers paint(Graphics g) to redraw the ATS report & score bar
    }

    @Override
    public void paint(Graphics g) {
        // Draw Report Card Box
        g.setColor(Color.WHITE);
        g.fillRect(20, 325, 580, 175);
        g.setColor(Color.GRAY);
        g.drawRect(20, 325, 580, 175);

        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(Color.BLACK);
        g.drawString("ATS SCAN REPORT", 35, 350);

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(22, 101, 52));
        g.drawString("Matched Skills : " + matchedText, 35, 375);

        g.setColor(new Color(153, 27, 27));
        g.drawString("Missing Skills : " + missingText, 35, 398);

        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(Color.BLACK);
        String scoreStr = String.format("ATS Score: %.1f%%   |   Status: %s", matchScore, statusText);
        g.drawString(scoreStr, 35, 425);

        // Draw Visual Progress Bar Background
        g.setColor(new Color(229, 231, 235));
        g.fillRect(35, 445, 550, 28);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(35, 445, 550, 28);

        if (scanned) {
            // Choose bar color based on score
            if (matchScore >= 70) {
                g.setColor(new Color(34, 197, 94)); // Green
            } else if (matchScore >= 40) {
                g.setColor(new Color(245, 158, 11)); // Orange
            } else {
                g.setColor(new Color(239, 68, 68)); // Red
            }

            int barWidth = (int) ((matchScore / 100.0) * 550);
            g.fillRect(35, 445, barWidth, 28);
        }
    }

    // Fallback runner so you can also run it using 'java ResumeScannerApplet' if appletviewer is missing
 // Updated main method with screen centering logic
    public static void main(String[] args) {
        Frame frame = new Frame("Java Applet - ATS Resume Scanner");
        ResumeScannerApplet applet = new ResumeScannerApplet();
        applet.init();

        frame.add(applet);
        frame.setSize(630, 520); // Compact width & height
        
        // ** YE LINE WINDOW KO SCREEN KE BILKUL BEECH MEIN LE AAYEGI **
        frame.setLocationRelativeTo(null); 
        
        frame.setResizable(false); // Optional: prevents stretching
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }