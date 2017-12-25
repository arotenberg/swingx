package org.jdesktop.swingx.combobox;

import javax.swing.JComboBox;

import org.jdesktop.swingx.JXFrame;

public class EnumComboBoxModelVisualCheck extends EnumComboBoxModelUnitTest {
    public static void main(String[] args) throws Exception {
        EnumComboBoxModelUnitTest test = new EnumComboBoxModelUnitTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #303-swingx: EnumComboBoxModel getSelectedItem throws
     * ClassCastException.  Fixed.
     * <p>
     * A visual example using default {@code toString} implementation.
     */
    public void interactiveSelectedItem() {
        EnumComboBoxModel<MyEnum1> enumModel = new EnumComboBoxModel<MyEnum1>(
                MyEnum1.class);
        JComboBox box = new JComboBox(enumModel);
        box.setEditable(true);
        JXFrame frame = wrapInFrame(box, "enum combo");
        frame.setVisible(true);
    }

    /**
     * Issue #303-swingx: EnumComboBoxModel getSelectedItem throws
     * ClassCastException.  Fixed.
     * <p>
     * A visual example using a custom {@code toString} implementation.
     */
    public void interactiveSelectedItemWithCustomToString() {
        EnumComboBoxModel<MyEnum2> enumModel = new EnumComboBoxModel<MyEnum2>(
                MyEnum2.class);
        JComboBox box = new JComboBox(enumModel);
        box.setEditable(true);
        JXFrame frame = wrapInFrame(box, "enum with custom toString combo");
        frame.setVisible(true);
    }
}
