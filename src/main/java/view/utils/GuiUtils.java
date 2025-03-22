package view.utils;

import javax.swing.*;
import java.awt.*;

public class GuiUtils {
    public static JButton createModuleButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 100));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setToolTipText(tooltip);
        return button;
    }
} 