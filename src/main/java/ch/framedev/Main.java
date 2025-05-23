package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 23.05.2025 22:11
 */

import ch.framedev.simplejavautils.SimpleJavaUtils;
import ch.framedev.yamlutils.FileConfiguration;

import javax.swing.*;
import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static FileConfiguration config;

    public static void main(String[] args) {
        createConfig();
        UIManager.put("FileChooser.useSystemExtensionHiding", Boolean.FALSE);
        UIManager.put("FileChooser.useShellFolder", Boolean.FALSE); // force Swing dialog
        if(System.getProperty("os.name").toLowerCase().contains("mac") || System.getProperty("os.name").toLowerCase().contains("darwin")) {
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            System.setProperty("apple.awt.application.name", "MarkdownEditor");
            System.setProperty("apple.awt.application.appearance", "system");
            System.setProperty("apple.awt.showGrowBox", "false");
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            System.setProperty("apple.awt.systemMenu", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        System.setProperty("swing.crossPlatformLAF", "true");
        System.setProperty("jdk.swing.usePlatformFileDialog", "false");
        SwingUtilities.invokeLater(() -> new MarkdownEditorSwing().createAndShowGUI());
    }

    public static void createConfig() {
        SimpleJavaUtils utils = new SimpleJavaUtils();
        config = new FileConfiguration(utils.getFromResourceFile("config.yml", Main.class),
                new File(utils.getFilePath(Main.class), "config.yml"));
    }
}