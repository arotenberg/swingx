/*
 * Created on 03.12.2008
 *
 */
package org.jdesktop.swingx.util.font;

import java.awt.Font;

import javax.swing.UIDefaults;


public class UIFontUtils {
    private UIFontUtils() {};

    public static void initFontDefaults(UIDefaults table, FontSet fontSet) {
        Font controlFont = null;
        Font menuFont = null;
        Font messageFont = null;
        Font toolTipFont = null;
        Font titleFont = null;
        Font windowFont = null;
        if (fontSet != null) {
            controlFont = fontSet.getControlFont();
            menuFont = fontSet.getMenuFont();
            messageFont = fontSet.getMessageFont();
            toolTipFont = fontSet.getSmallFont();
            titleFont = fontSet.getTitleFont();
            windowFont = fontSet.getWindowTitleFont();
        }
        Object[] defaults = {

        "Button.font", controlFont,

        "CheckBox.font", controlFont,

        "ColorChooser.font", controlFont,

        "ComboBox.font", controlFont,

        "EditorPane.font", controlFont,

        "FormattedTextField.font", controlFont,

        "Label.font", controlFont,

        "List.font", controlFont,

        "Panel.font", controlFont,

        "PasswordField.font", controlFont,

        "ProgressBar.font", controlFont,

        "RadioButton.font", controlFont,

        "ScrollPane.font", controlFont,

        "Spinner.font", controlFont,

        "TabbedPane.font", controlFont,

        "Table.font", controlFont,

        "TableHeader.font", controlFont,

        "TextArea.font", controlFont,

        "TextField.font", controlFont,

        "TextPane.font", controlFont,

        "ToolBar.font", controlFont,

        "ToggleButton.font", controlFont,

        "Tree.font", controlFont,

        "Viewport.font", controlFont,

        "InternalFrame.titleFont", windowFont,

        "DesktopIcon.titleFont", windowFont,

        "OptionPane.font", messageFont,

        "OptionPane.messageFont", messageFont,

        "OptionPane.buttonFont", messageFont,

        "TitledBorder.font", titleFont,

        "ToolTip.font", toolTipFont,

        "CheckBoxMenuItem.font", menuFont,

        "CheckBoxMenuItem.acceleratorFont", menuFont,

        "Menu.font", menuFont,

        "Menu.acceleratorFont", menuFont,

        "MenuBar.font", menuFont,

        "MenuItem.font", menuFont,

        "MenuItem.acceleratorFont", menuFont,

        "PopupMenu.font", menuFont,

        "RadioButtonMenuItem.font", menuFont,

        "RadioButtonMenuItem.acceleratorFont", menuFont,
        // ?
        };
        table.putDefaults(defaults);
    }


}
