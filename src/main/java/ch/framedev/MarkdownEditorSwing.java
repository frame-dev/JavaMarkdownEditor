package ch.framedev;

import ch.framedev.simplejavautils.SimpleJavaUtils;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MarkdownEditorSwing {

    private final Parser parser = Parser.builder()
            .extensions(List.of(TablesExtension.create(), AutolinkExtension.create(), StrikethroughExtension.create()))
            .build();

    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .extensions(List.of(TablesExtension.create(), AutolinkExtension.create(), StrikethroughExtension.create()))
            .build();

    private JTextArea editor;
    private JEditorPane preview;
    private JFrame frame;
    private boolean darkMode = false;
    int fontSize = Main.config.getInt("fontSize", 14);
    int previewFontSize = Main.config.getInt("previewFontSize", 14);

    private List<File> recentFiles = new ArrayList<>();
    private JMenu recentMenu;

    public void createAndShowGUI() {
        loadRecentFiles();
        frame = new JFrame("Markdown Editor");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1000, 600);

        editor = new JTextArea();
        editor.setFont(new Font("Arial", Font.PLAIN, fontSize));
        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);

        preview = new JEditorPane("text/html", "");
        preview.setEditable(false);
        preview.setContentType("text/html");
        preview.setFont(new Font("Arial", Font.PLAIN, previewFontSize));

        preview.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    openLink(e.getURL().toString());
                } catch (Exception ex) {
                    showError("Failed to open link: " + ex.getMessage());
                }
            }
        });

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

        editor.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    loadFile(droppedFiles.get(0));
                } catch (Exception ex) {
                    showError("Failed to load dropped file: " + ex.getMessage());
                }
            }
        });

        JMenuBar menuBar = new JMenuBar();
        createFileJMenu(menuBar);
        createViewJMenu(menuBar);

// Create the Recent Files menu
        recentMenu = new JMenu("Recent Files");

// Add recent files list dynamically
        updateRecentMenu();

// Add a separator and a 'Clear' option
        recentMenu.addSeparator();

// Add to the menu bar
        menuBar.add(recentMenu);
        frame.setJMenuBar(menuBar);

        frame.add(splitPane);
        frame.setVisible(true);

        editor.setText("# Welcome\nThis is a *Markdown* editor.");
        updatePreview();

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Do you want to save changes before exiting?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );

                if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                    return;
                }

                if (result == JOptionPane.YES_OPTION) {
                    saveFile(); // or save logic
                }

                saveRecentFiles(); // optional
                System.exit(0);
            }
        });
    }

    private void openLink(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", url});
            } else if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            }
        } catch (IOException e) {
            showError("Failed to open link: " + e.getMessage());
        }
    }

    private void createViewJMenu(JMenuBar menuBar) {
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem darkModeToggle = new JCheckBoxMenuItem("Dark Mode");
        darkModeToggle.addActionListener(e -> {
            darkMode = darkModeToggle.isSelected();
            updatePreview();
        });
        viewMenu.add(darkModeToggle);
        menuBar.add(viewMenu);
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

    private void updateRecentMenu() {
        recentMenu.removeAll();
        for (File file : recentFiles) {
            JMenuItem item = new JMenuItem(file.getAbsolutePath());
            item.addActionListener(e -> loadFile(file));
            recentMenu.add(item);
        }
        if (recentFiles.isEmpty()) {
            recentMenu.add(new JMenuItem("(No recent files)"));
        }
        JMenuItem clearRecentMenu = new JMenuItem("Clear Recent Files");
        clearRecentMenu.addActionListener(e -> {
            recentFiles.clear();
            updateRecentMenu();
        });
        recentMenu.add(clearRecentMenu);
    }

    private void addToRecentFiles(File file) {
        recentFiles.remove(file);
        recentFiles.add(0, file);
        if (recentFiles.size() > 20) {
            recentFiles.remove(recentFiles.size() - 1);
        }
        updateRecentMenu();
    }

    private void updatePreview() {
        Node document = parser.parse(editor.getText());

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
        preview.setCaretPosition(0);
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
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                Node doc = parser.parse(editor.getText());
                String rawHtml = renderer.render(doc);
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

            loadFile(selected);
        }
    }

    private void loadFile(File file) {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            editor.setText(content);
            addToRecentFiles(file);
        } catch (IOException e) {
            showError("Could not read file: " + e.getMessage());
        }
    }

    private void saveFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter mdFilter = new FileNameExtensionFilter("Markdown files (*.md)", "md");
        chooser.setFileFilter(mdFilter);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setSelectedFile(new File("untitled.md"));

        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".md")) {
                file = new File(file.getAbsolutePath() + ".md");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(editor.getText());
                addToRecentFiles(file);
            } catch (IOException e) {
                showError("Could not save file: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void saveRecentFiles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("recent_files.txt"))) {
            for (File file : recentFiles) {
                writer.write(file.getAbsolutePath());
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Failed to save recent files: " + e.getMessage());
        }
    }

    private void loadRecentFiles() {
        File recentFile = new File(new SimpleJavaUtils().getFilePath(Main.class) + "recent_files.txt");
        if (!recentFile.exists()) {
            recentFiles = new ArrayList<>();
            return; // No recent files to load
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(recentFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                recentFiles.add(new File(line));
            }
        } catch (IOException e) {
            showError("Failed to load recent files: " + e.getMessage());
        }
    }
}