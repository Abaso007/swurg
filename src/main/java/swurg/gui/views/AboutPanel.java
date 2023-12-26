package swurg.gui.views;

import static burp.MyBurpExtension.EXTENSION;
import static burp.MyBurpExtension.VERSION;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.batik.swing.JSVGCanvas;

import burp.api.montoya.MontoyaApi;
import swurg.gui.components.StatusPanel;
import swurg.utilities.HtmlResourceLoader;

public class AboutPanel extends JPanel {

    private final int INSET = 16;

    private final Frame suiteFrame;

    private JPanel northPanel;

    public AboutPanel(MontoyaApi montoyaApi) {
        this.suiteFrame = montoyaApi.userInterface().swingUtils().suiteFrame();

        initComponents();

        addComponentListeners();
    }

    private void addComponentListeners() {
        this.addComponentListener(new ComponentAdapter() {
            private void setNorthPanelPreferredHeight() {
                int newHeight = (int) (suiteFrame.getHeight() * 0.2);

                northPanel.setPreferredSize(
                        new Dimension(northPanel.getPreferredSize().width, newHeight));
            }

            @Override
            public void componentResized(ComponentEvent e) {
                setNorthPanelPreferredHeight();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                setNorthPanelPreferredHeight();
            }
        });
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        this.northPanel = createNorthPanel();
        this.northPanel.setBorder(new EmptyBorder(INSET, INSET, 0, 16));

        JPanel centerPanel = createCenterPanel();
        centerPanel.setBorder(new EmptyBorder(0, INSET, 0, INSET));

        this.add(northPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(new StatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createNorthPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JSVGCanvas svgCanvas = new JSVGCanvas();

        try {
            URI svgFileURI = getClass().getResource("/images/logo.svg").toURI();
            svgCanvas.setURI(svgFileURI.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        svgCanvas.setOpaque(false);
        svgCanvas.setBackground(new Color(0, 0, 0, 0));

        panel.add(svgCanvas, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(createEditorPane("about.html"));
        scrollPane.setBorder(null);

        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(INSET));
        panel.add(createButtonPanel());

        return panel;
    }

    private JEditorPane createEditorPane(String resourcePath) {
        String htmlContent = HtmlResourceLoader.loadHtmlContent(resourcePath);
        String formattedHtmlContent = MessageFormat.format(htmlContent, VERSION, EXTENSION);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(formattedHtmlContent);
        editorPane.setEditable(false);

        return editorPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        Map<String, String> buttonMap = Map.of(
                "<html>Get in touch with <b>Aegis Cyber</b></html>", "https://www.aegiscyber.co.uk",
                "<html>Connect with me on <b>LinkedIn</b></html>", "https://www.linkedin.com/in/alexandre-teyar",
                "<html>Follow me on <b>GitHub</b></html>", "https://github.com/aress31",
                "<html>Submit a <b>pull request</b> or report a <b>bug</b></html>", "https://github.com/aress31/swurg");

        buttonMap.forEach((key, value) -> {
            JButton button = new JButton(key);
            button.putClientProperty("html.disable", null);
            button.setAlignmentX(CENTER_ALIGNMENT);

            button.addActionListener(e -> {
                try {
                    Desktop.getDesktop().browse(new URI(value));
                } catch (IOException | URISyntaxException ex) {
                    // Do nothing
                }
            });

            if (key.equals(buttonMap.keySet().iterator().next())) {
                button.setBackground(UIManager.getColor("Burp.burpOrange"));
                button.setFont(new Font(button.getFont().getName(), Font.BOLD, button.getFont().getSize()));
                button.setForeground(UIManager.getColor("Burp.primaryButtonForeground"));
            }

            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(4));
        });

        return buttonPanel;
    }
}
