/*
 * Created on 12.04.2006
 * 
 */
package org.jdesktop.swingx.combobox;

import org.jdesktop.swingx.InteractiveTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class EnumComboBoxModelUnitTest extends InteractiveTestCase {
    enum MyEnum1 {
        GoodStuff, BadStuff
    };

    enum MyEnum2 {
        EasyStuff, HardStuff;
        @Override
        public String toString() {
            switch (this) {
            case EasyStuff:
                return "Easy Stuff";
            case HardStuff:
                return "Hard Stuff";
            }

            return "ERROR";
        }
    };

    enum MyEnum3 {
        RightStuff, WrongStuff;
        @Override
        public String toString() {
            switch (this) {
            case RightStuff:
                return "One Value";
            case WrongStuff:
                return "One Value";
            }

            return "ERROR";
        }
    }

    /**
     * Issue #303-swingx: EnumComboBoxModel getSelectedItem throws
     * ClassCastException. Fixed.
     */
    @Test
    public void testSetSelectedItem() {
        EnumComboBoxModel<MyEnum1> model1 = new EnumComboBoxModel<MyEnum1>(
                MyEnum1.class);
        // always starts by selecting the first value
        assertEquals(model1.getSelectedItem(), MyEnum1.GoodStuff);

        // should ignore the non-mapped string
        model1.setSelectedItem("something else");
        assertEquals(model1.getSelectedItem(), MyEnum1.GoodStuff);

        model1.setSelectedItem(MyEnum1.BadStuff);
        assertEquals(model1.getSelectedItem(), MyEnum1.BadStuff);

        model1.setSelectedItem(null);
        assertNull(model1.getSelectedItem());

        model1.setSelectedItem("GoodStuff");
        assertEquals(model1.getSelectedItem(), MyEnum1.GoodStuff);

        // ensure that custom strings map correctly
        EnumComboBoxModel<MyEnum2> model2 = new EnumComboBoxModel<MyEnum2>(
                MyEnum2.class);

        // always starts by selecting the first value
        assertEquals(model2.getSelectedItem(), MyEnum2.EasyStuff);

        // should ignore the non-mapped string and set to null
        model2.setSelectedItem("something else");
        assertEquals(model2.getSelectedItem(), MyEnum2.EasyStuff);

        model2.setSelectedItem(MyEnum2.HardStuff);
        assertEquals(model2.getSelectedItem(), MyEnum2.HardStuff);

        model2.setSelectedItem(null);
        assertNull(model2.getSelectedItem());

        model2.setSelectedItem("Easy Stuff");
        assertEquals(model2.getSelectedItem(), MyEnum2.EasyStuff);
    }

    @Test
    public void testNonUniqueToString() {
        try {
            new EnumComboBoxModel<MyEnum3>(MyEnum3.class);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

}
