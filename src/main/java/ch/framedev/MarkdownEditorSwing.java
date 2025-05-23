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

    private boolean darkMode = false;

    int fontSize = Main.config.getInt("fontSize", 14);
    int previewFontSize = Main.config.getInt("previewFontSize", 14);

    private static final String RECENT_FILES_PATH = new SimpleJavaUtils().getFilePath(Main.class) + "recent_files.txt";
    private List<File> recentFiles = new ArrayList<>();
    private JMenu recentMenu;

    public void createAndShowGUI() {
        loadRecentFiles();
        JFrame frame = new JFrame("Markdown Editor");
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

        // Add keyboard shortcuts
        KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
        KeyStroke ctrlO = KeyStroke.getKeyStroke("control O");
        KeyStroke ctrlE = KeyStroke.getKeyStroke("control E");
        KeyStroke ctrlD = KeyStroke.getKeyStroke("control D");

        editor.getInputMap().put(ctrlS, "save");
        editor.getInputMap().put(ctrlO, "open");
        editor.getInputMap().put(ctrlE, "export");
        editor.getInputMap().put(ctrlD, "darkMode");

        editor.getActionMap().put("save", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        editor.getActionMap().put("open", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        editor.getActionMap().put("export", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                exportAsHtml();
            }
        });
        editor.getActionMap().put("darkMode", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                darkMode = !darkMode;
                updateTheme();
                updatePreview();
            }
        });

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
                    @SuppressWarnings("unchecked")
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

// Add a recent files list dynamically
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
        
        // Dark Mode Toggle
        JCheckBoxMenuItem darkModeToggle = new JCheckBoxMenuItem("Dark Mode");
        darkModeToggle.addActionListener(e -> {
            darkMode = darkModeToggle.isSelected();
            updateTheme();
            updatePreview();
        });
        viewMenu.add(darkModeToggle);
        
        // Font Size Controls
        JMenu fontSizeMenu = new JMenu("Font Size");
        String[] sizes = {"12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38", "40"};
        for (String size : sizes) {
            JMenuItem sizeItem = new JMenuItem(size + "px");
            sizeItem.addActionListener(e -> {
                fontSize = Integer.parseInt(size);
                previewFontSize = fontSize;
                editor.setFont(new Font("Arial", Font.PLAIN, fontSize));
                updatePreview();
            });
            fontSizeMenu.add(sizeItem);
        }
        viewMenu.add(fontSizeMenu);
        
        menuBar.add(viewMenu);
    }

    private void updateTheme() {
        if (darkMode) {
            editor.setBackground(new Color(30, 30, 30));
            editor.setForeground(new Color(221, 221, 221));
            editor.setCaretColor(new Color(221, 221, 221));
        } else {
            editor.setBackground(Color.WHITE);
            editor.setForeground(Color.BLACK);
            editor.setCaretColor(Color.BLACK);
        }
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("HTML files", "html"));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }
            try (FileWriter writer = new FileWriter(file)) {
                String css = darkMode ?
                    "body { background: #1e1e1e; color: #ddd; font-family: Arial; font-size: " + previewFontSize + "px; }" +
                    "pre { font-family: monospace; background:#2d2d2d; color:#ccc; padding:6px; border:1px solid #555; }" +
                    "table { border-collapse:collapse; font-family: Arial; border:1px solid #555; color:#ddd; background:#2d2d2d; }" +
                    "th, td { border: 1px solid #555; padding: 6px; }" +
                    "a { color: #4a9eff; }" +
                    "code { background: #2d2d2d; padding: 2px 4px; border-radius: 3px; }" :
                    "body { font-family: Arial; font-size: " + previewFontSize + "px; }" +
                    "pre { font-family: monospace; background:#f4f4f4; padding:6px; border:1px solid #ccc; }" +
                    "table { border-collapse:collapse; font-family: Arial; }" +
                    "th, td { border: 1px solid #ccc; padding: 6px; }" +
                    "a { color: #0066cc; }" +
                    "code { background: #f4f4f4; padding: 2px 4px; border-radius: 3px; }";

                String html = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>" + file.getName() + "</title>\n" +
                    "    <style>\n" + css + "\n    </style>\n" +
                    "</head>\n" +
                    "<body>\n" + renderer.render(parser.parse(editor.getText())) + "\n</body>\n</html>";

                writer.write(html);
                JOptionPane.showMessageDialog(null, "HTML file exported successfully!");
            } catch (IOException ex) {
                showError("Failed to export HTML: " + ex.getMessage());
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
        File recentFile = new File(RECENT_FILES_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(recentFile, false))) {
            for (File file : recentFiles) {
                writer.write(file.getAbsolutePath());
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Failed to save recent files: " + e.getMessage());
        }
    }

    private void loadRecentFiles() {
        File recentFile = new File(RECENT_FILES_PATH);
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