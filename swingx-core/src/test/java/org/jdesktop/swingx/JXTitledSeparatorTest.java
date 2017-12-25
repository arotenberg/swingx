/*
 * Created on 16.03.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class JXTitledSeparatorTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(JXTitledSeparatorTest.class.getName());
    
    public static void main(String[] args) {
        setSystemLF(true);
        JXTitledSeparatorTest test = new JXTitledSeparatorTest();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Highligh.*");
        } catch (Exception ex) {

        }
    }

    /**
     * Issue #305-swingx: JXTitledSeparator prefSize should depend on title length.
     * 
     * (to solve: never-ever call setPrefSize() - override getPrefSize() instead)
     *
     */
    @Test
    public void testPrefSize() {
        JXTitledSeparator separator = new JXTitledSeparator();
        Dimension dim = separator.getPreferredSize();
        separator.setTitle("some title definitely longer than empty");
        assertTrue(dim.width < separator.getPreferredSize().width);
    }
    
    /**
     * Issue #391-swingx: JXTitledSeparator does not respect getForeground/getFont
     */
    @Test
    public void testForeground() {
        Color testColor = new Color(10, 213, 123);
        UIManager.put("TitledBorder.titleColor", testColor);
        JXTitledSeparator separator = new JXTitledSeparator();
        assertSame(testColor, separator.getForeground());
    }
    
    /**
     * Issue #??-swingx: JXTitledSeparator - no visual clue if disabled
     */
    public void interactiveDisabled() {
        final JXTitledSeparator separator = new JXTitledSeparator("visual clue for disabled?... ");
        JXFrame frame = showInFrame(separator, "en/disabled");
        Action action = new AbstractAction("toggleEnabled") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                separator.setEnabled(!separator.isEnabled());
                
            }
        };
        addAction(frame, action);
        show(frame, 400, 200);
    }
    /**
     * Issue #304-swingx: JXTitledSeparator should have same orientation
     * dependent behaviour as TitledBorder.
     * 
     * Looking at bidi-compliance: LEFT/RIGHT should be absolute,
     * as opposed to LEADING/TRAILING which is orientation dependent.
     *  
     * weird (unrelated to the titledSeparator) the dynamic update
     * of orientation isn't taken - only after resize. Problem of
     * panel, frame, ?? An equivalent check for jxtable which resides
     * in a scrollpane is okay. Revalidate doesn't help for border.
     *
     */
    public void interactiveRToL() {
        JComponent box = Box.createVerticalBox();
        JXTitledSeparator defaultAlign = new JXTitledSeparator();
        // default is LEADING
       defaultAlign.setTitle("default");
        // default is LEADING
       Border lineBorder = BorderFactory.createLineBorder(Color.MAGENTA);
        Border defaultBorder = new TitledBorder(lineBorder, "default");
        defaultAlign.setBorder(defaultBorder);
        box.add(defaultAlign);

 
        JXTitledSeparator leading = new JXTitledSeparator();
        leading.setTitle("leading");
        leading.setHorizontalAlignment(SwingConstants.LEADING);
        Border leadingBorder = new TitledBorder(lineBorder, "leading", TitledBorder.LEADING, TitledBorder.TOP);
        leading.setBorder(leadingBorder);
        box.add(leading);

        JXTitledSeparator trailing = new JXTitledSeparator();
        trailing.setTitle("trailing");
        trailing.setHorizontalAlignment(SwingConstants.TRAILING);
        Border trailingBorder = new TitledBorder(lineBorder, "trailing", TitledBorder.TRAILING, TitledBorder.TOP);
        trailing.setBorder(trailingBorder);
        box.add(trailing);
        
        JXTitledSeparator left = new JXTitledSeparator();
        left.setTitle("left");
        left.setHorizontalAlignment(SwingConstants.LEFT);
        Border leftBorder = new TitledBorder(lineBorder, "left", TitledBorder.LEFT, TitledBorder.TOP);
        left.setBorder(leftBorder);
        box.add(left);
        
        final JXTitledSeparator right = new JXTitledSeparator();
        right.setTitle("right");
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        Border rightBorder = new TitledBorder(lineBorder, "right", TitledBorder.RIGHT, TitledBorder.TOP);
        right.setBorder(rightBorder);
        box.add(right);
        
        final JXFrame frame = wrapInFrame(box, "Bidi-compliance");
        Action action = new AbstractAction("toggleEnabled") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                right.setEnabled(!right.isEnabled());
                
            }
        };
        addAction(frame, action);
        addComponentOrientationToggle(frame);
        // titledSeparator freaks with prefSize, need to set fixed
        show(frame, 200, 400);
    }
    
    /**
     * Compares JXTitledSeparator props with corresponding TitledBorder props.
     * 
     */
    public void interactiveCompareAppearance() {
        JComponent box = Box.createVerticalBox();
        JXTitledSeparator defaultAlign = new JXTitledSeparator();
        // default is LEADING
       defaultAlign.setTitle("default");
        // default is LEADING
       Border lineBorder = BorderFactory.createLineBorder(Color.MAGENTA);
        Border defaultBorder = new TitledBorder(lineBorder, "default");
        defaultAlign.setBorder(defaultBorder);
        box.add(defaultAlign);

        Font font = new JLabel().getFont();
        Font bigFont = font.deriveFont(Font.ITALIC, font.getSize() * 2.f);
        JXTitledSeparator leading = new JXTitledSeparator();
        leading.setTitle("big font");
        leading.setFont(bigFont);
        TitledBorder leadingBorder = new TitledBorder(lineBorder, "bigFont");
        leadingBorder.setTitleFont(bigFont);
        leading.setBorder(leadingBorder);
        box.add(leading);

        JXTitledSeparator trailing = new JXTitledSeparator();
        trailing.setTitle("colored");
        trailing.setForeground(Color.BLUE);
        TitledBorder trailingBorder = new TitledBorder(lineBorder, "colored");
        trailingBorder.setTitleColor(Color.BLUE);
        trailing.setBorder(trailingBorder);
        box.add(trailing);
        
//        JXTitledSeparator left = new JXTitledSeparator();
//        left.setTitle("left");
//        left.setHorizontalAlignment(SwingConstants.LEFT);
//        Border leftBorder = new TitledBorder(lineBorder, "left", TitledBorder.LEFT, TitledBorder.TOP);
//        left.setBorder(leftBorder);
//        box.add(left);
        
        
        final JXFrame frame = wrapInFrame(box, "Compare properties");
        // titledSeparator freaks with prefSize, need to set fixed
        frame.setSize(200, 400);
        frame.setVisible(true);
        
    }
    
}
