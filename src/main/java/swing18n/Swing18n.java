package swing18n;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class Swing18n {
    ResourceBundle actualResources = ResourceBundle.getBundle(Swing18n.class.getName());
    String internationalisingXX = actualResources.getString("InternationalisingXX");
    String inEnglishXX = actualResources.getString("InEnglishXX");
    String copyToClipboardText = actualResources.getString("CopyToClipboard");
    String translationsForXX = actualResources.getString("TranslationsForXX");
    String theTranslationsHaveBeenCopiedToTheClipboardPleaseEmailToXX = actualResources.getString("TheTranslationsHaveBeenCopiedToTheClipboardPleaseEmailToXX");
    JFrame frame;

    public Swing18n(String appName, final Locale locale, final String email, Class<?>... classes) {
        frame = new JFrame(String.format(internationalisingXX, appName));
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (locale.equals(Locale.ENGLISH))
            throw new IllegalArgumentException("Not valid for English");

        final Map<String, JTextField> edits = new HashMap<String, JTextField>();
        for (Class<?> clazz: classes) {
            final Properties english = new Properties();
            final Properties foreign = new Properties();

            try {
                english.load(clazz.getResource(clazz.getSimpleName() + ".properties").openStream());
                final String foreignName = clazz.getSimpleName() + '_' + locale.getLanguage() + ".properties";
                foreign.load(clazz.getResource(foreignName).openStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Object key: english.keySet())
                if (!foreign.containsKey(key)) {
                    final JPanel line = new JPanel();
                    line.add(new JLabel(key + " in " + locale.getDisplayLanguage() + ": "));
                    final JTextField field = new JTextField(20);
                    line.add(field);
                    edits.put(clazz + " " + key, field);
                    line.add(new JLabel('(' + String.format(inEnglishXX, english.getProperty(key.toString())) + ')'));
                    panel.add(line);
                }
        }
        frame.add(new JScrollPane(panel));
        final JButton copyToClipboard = new JButton(copyToClipboardText);
        copyToClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                final StringBuilder builder = new StringBuilder();
                builder.append(String.format(translationsForXX, locale.getDisplayLanguage())).append(" (").append(locale.getLanguage()).append(")\n");
                for (String classNameAndKey: edits.keySet()) {
                    final String value = edits.get(classNameAndKey).getText();
                    if (!value.isEmpty())
                        builder.append(classNameAndKey).append(' ').append(value).append('\n');
                }
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(builder.toString()), null);
                JOptionPane.showMessageDialog(frame, String.format(theTranslationsHaveBeenCopiedToTheClipboardPleaseEmailToXX, email));
            }
        });
        frame.add(copyToClipboard, BorderLayout.SOUTH);
        frame.pack();
    }

    public static void main(String[] args) {
        final Swing18n i18n = new Swing18n("Swing18n", new Locale("es", "ES", "es"), "ricky.clarkson@gmail.com", Swing18n.class);
        i18n.frame.setVisible(true);
        i18n.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
