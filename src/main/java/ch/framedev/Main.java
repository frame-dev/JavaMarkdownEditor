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
        System.setProperty("apple.laf.useScreenMenuBar", "true"); // optional, mac style
        System.setProperty("swing.crossPlatformLAF", "true");
        System.setProperty("jdk.swing.usePlatformFileDialog", "false");
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        SwingUtilities.invokeLater(() -> MarkdownEditorSwing.main(args));
    }

    public static void createConfig() {
        SimpleJavaUtils utils = new SimpleJavaUtils();
        config = new FileConfiguration(utils.getFromResourceFile("config.yml", Main.class),
                new File(utils.getFilePath(Main.class), "config.yml"));
    }
}