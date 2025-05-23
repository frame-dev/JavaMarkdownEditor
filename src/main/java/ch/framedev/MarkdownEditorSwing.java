package ch.framedev;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MarkdownEditorSwing {

    private final Parser parser = Parser.builder()
            .extensions(java.util.List.of(TablesExtension.create()))
            .build();

    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .extensions(java.util.List.of(TablesExtension.create()))
            .build();
    private JTextArea editor;
    private JEditorPane preview;

    int fontSize = Main.config.getInt("fontSize", 14);
    int previewFontSize = Main.config.getInt("previewFontSize", 14);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MarkdownEditorSwing().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Markdown Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        editor = new JTextArea();
        editor.setFont(new Font("Arial", Font.PLAIN, fontSize));
        preview = new JEditorPane("text/html", "");
        preview.setEditable(false);
        preview.setContentType("text/html");
        preview.setFont(new Font("Arial", Font.PLAIN, fontSize));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(editor), new JScrollPane(preview));
        splitPane.setDividerLocation(500);

        editor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updatePreview();
            }

            public void removeUpdate(DocumentEvent e) {
                updatePreview();
            }

            public void changedUpdate(DocumentEvent e) {
                updatePreview();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        createFileJMenu(menuBar);
        createViewJMenu(menuBar);
        frame.setJMenuBar(menuBar);

        frame.add(splitPane);
        frame.setVisible(true);

        editor.setText("# Welcome\nThis is a *Markdown* editor.");

        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);
        updatePreview();
    }

    private void createViewJMenu(JMenuBar menuBar) {
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem darkModeToggle = new JCheckBoxMenuItem("Dark Mode");
        viewMenu.add(darkModeToggle);
        menuBar.add(viewMenu);


        darkModeToggle.addActionListener(e -> {
            darkMode = darkModeToggle.isSelected();
            updatePreview();
        });
    }

    private void createFileJMenu(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem(new AbstractAction("Open") {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        JMenuItem saveItem = new JMenuItem(new AbstractAction("Save") {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        JMenuItem exportHtmlItem = new JMenuItem(new AbstractAction("Export as HTML") {
            public void actionPerformed(ActionEvent e) {
                exportAsHtml();
            }
        });
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exportHtmlItem);
        menuBar.add(fileMenu);
    }

    private boolean darkMode = false;

    private void updatePreview() {
        Node document = parser.parse(editor.getText());

        // Replace unsupported code class attributes and style <pre> blocks
        String bodyStyle = darkMode
                ? "background: #1e1e1e; color: #ddd; font-family: Arial; font-size: 14px;"
                : "font-family: Arial; font-size: " + previewFontSize + "px;";

        String preStyle = darkMode
                ? "font-family: monospace; background:#2d2d2d; color:#ccc; padding:6px; border:1px solid #555;"
                : "font-family: monospace; background:#f4f4f4; padding:6px; border:1px solid #ccc;";

        String tableStyle = darkMode
                ? "border-collapse:collapse; font-family: Arial; border:1px solid #555; color:#ddd; background:#2d2d2d;"
                : "border-collapse:collapse; font-family: Arial;";

        String cleanHtml = renderer.render(document)
                .replaceAll("(?i)<code class=\"language-[^\"]*\">", "<code>")
                .replaceAll("(?i)<pre><code>", "<pre style='" + preStyle + "'><code>")
                .replaceAll("(?i)<table>", "<table border='1' cellspacing='0' cellpadding='6' style='" + tableStyle + "'>")
                .replaceAll("(?i)</?thead>", "")
                .replaceAll("(?i)</?tbody>", "");

        String html = "<html><head><meta charset='UTF-8'></head><body style='" + bodyStyle + "'>" +
                      cleanHtml + "</body></html>";

        preview.setText(html);
        preview.setCaretPosition(0); // Scroll to top
    }

    private void exportAsHtml() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter htmlFilter = new FileNameExtensionFilter("HTML files", "html");
        chooser.setFileFilter(htmlFilter);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setSelectedFile(new File("export.html"));

        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            // Append .html extension if missing
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                Node doc = parser.parse(editor.getText());
                String rawHtml = renderer.render(doc);

                // Clean fenced code and table styles
                rawHtml = rawHtml.replaceAll("(?i)<code class=\"language-[^\"]*\">", "<code>");
                rawHtml = rawHtml.replaceAll("(?i)<pre><code>", "<pre style='font-family: monospace; background:#f4f4f4; padding:6px; border:1px solid #ccc;'><code>");
                rawHtml = rawHtml.replaceAll("(?i)<table>", "<table border='1' cellspacing='0' cellpadding='6' style='border-collapse:collapse; font-family: Arial;'>");

                String html = "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial; font-size: 14px'>" +
                              rawHtml + "</body></html>";

                writer.write(html);
            } catch (IOException e) {
                showError("Failed to export: " + e.getMessage());
            }
        }
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter mdFilter = new FileNameExtensionFilter("Markdown files (*.md, *.markdown)", "md", "markdown");
        chooser.setFileFilter(mdFilter);
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (!selected.getName().toLowerCase().endsWith(".md") &&
                !selected.getName().toLowerCase().endsWith(".markdown")) {
                showError("Invalid file type. Please select a .md or .markdown file.");
                return;
            }

            try {
                String content = new String(java.nio.file.Files.readAllBytes(selected.toPath()));
                editor.setText(content);
            } catch (IOException e) {
                showError("Could not read file: " + e.getMessage());
            }
        }
    }

    private void saveFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter mdFilter = new FileNameExtensionFilter("Markdown files (*.md)", "md");
        chooser.setFileFilter(mdFilter);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setSelectedFile(new File("untitled.md")); // Optional: default filename

        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            // Append .md if it's missing
            if (!file.getName().toLowerCase().endsWith(".md")) {
                file = new File(file.getAbsolutePath() + ".md");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(editor.getText());
            } catch (IOException e) {
                showError("Could not save file: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}