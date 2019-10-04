package bergmann.masterarbeit.generationtarget.utils;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * UIUtils
 */
public class UIUtils {
    public static File databaseSelection() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Database file", "db");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Select Database");
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            return selectedFile;
        }
        return null;
    }

    public static int isRealTimeSelection() {
        String TEXT = "Use realtime simulation mode?\nIn this mode the database is watched for changes in real time.\n\nWARNING: Evaluation for some functions, e.g. LTL's G may be affected.";
        String TITLE = "Realtime selection";
        Object[] options = { "Yes", "No", "Cancel" };
        int input = JOptionPane.showOptionDialog(null, TEXT, TITLE, JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[1]);
        return input;
    }

    public static String selectTable(List<String> options) {
        String[] optionsArray = options.toArray(new String[options.size()]);
        String x = (String) JOptionPane.showInputDialog(null, "Select database table", "Table selection",
                JOptionPane.QUESTION_MESSAGE, null, optionsArray, optionsArray[0]);
        return x;
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}