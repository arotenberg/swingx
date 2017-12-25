/*
 * Created on 16.03.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.test.AncientSwingTeam;

public class JXStatusBarVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(JXStatusBarVisualCheck.class.getName());
    
    public static void main(String[] args) {
        JXStatusBarVisualCheck test = new JXStatusBarVisualCheck();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Decorate.*");
        } catch (Exception ex) {

        }
    }

    /**
     * Issue #1192-swingx: Cursor not adjusted to text component.
     */
    public void interactiveTextComponentCursor() {
        JTextField field = new JTextField("here we go, move mouse over me - text-cursor as expected");
        JXFrame frame = wrapInFrame(field, "text cursor");
        JTextField status = new JTextField("I'm in statusbar - move mouse over me, no text cursor");
        addStatusComponent(frame, status);
        show(frame);
    }
    /**
     * Issue #936-swingx: JXRootPane can't cope with default decoration.
     */
    public void interactiveDefaultDecorated() {
        JXTable table = new JXTable(new AncientSwingTeam());
        final JXFrame frame = wrapWithScrollingInFrame(table, "toggle laf decoration");
        Action toggleDecoration = new AbstractAction("toggleDecoration") {

            public void actionPerformed(ActionEvent e) {
                boolean wasDecoratedByLAF = frame.isUndecorated();
                frame.setVisible(false);
                frame.dispose();
                frame.setUndecorated(!wasDecoratedByLAF);
                frame.getRootPane().setWindowDecorationStyle(wasDecoratedByLAF ?
                         JRootPane.NONE : JRootPane.FRAME );
                frame.setVisible(true);
            }
            
        };
        addAction(frame, toggleDecoration);
        addStatusMessage(frame, "we must be showing");
        show(frame);
    }
    /**
     * Use-case: mimic win2k explorer status bar.
     * 
     * has a - leading message area which resizes on frame resize - two trailing
     * fixed size text areas with different fixed sizes.
     * 
     * goal: - add the message text - add the trailing labels - auto-space (in
     * win2k that would be "gaps" between the labels, in other LFs it would be),
     * that is no need to manually insert anything nor fiddle with "insets". The
     * LF should come up with a reasonable default. - simple constraint for
     * trailing, that is no need for any hack with weights
     * 
     */
    public void interactiveWin2kExplorerStatus() {
        JComponent panel = new JXPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(createIconLabel("resources/images/explorer_win2k.png"));
        panel.add(createIconLabel("resources/images/explorer_win2k_narrow.png"));
        final JXFrame frame = wrapInFrame(panel, "Compare with screenshots");
        addMessage(frame, "18 Objekt(e) (Freier Speicherplatz 2,08 GB)");
        JXStatusBar statusBar = getStatusBar(frame);
        statusBar.add(new JLabel("286 Byte")); // , JLabel.TRAILING);
        statusBar.add(new JLabel("[ ] Arbeitsplatz")); //, JLabel.TRAILING);
        frame.setSize(1000, 200);
        frame.setVisible(true);
    }

    /**
     * Use-case: mimic win2k eclipse status bar.
     * 
     * has a 
     * - trailing fixed size area for progress info
     * - before that: three fixed-size text areas (which are near to 
     * same size, but aren't :-) Hmm.... maybe they aren't even fixed-size,
     * the separators in the wide vs narrow screenshot aren't exactly aligned. 
     * 
     * This might be a compound statusbar (one being the context infos, the
     * other for progress info) - so could be considered a edge case,
     * not necessarily included. 
     * 
     * goals (as far as the text fields go) are similar to the explorer
     * plus:
     * - support equals size cells
     * - keep layout if one of the text labels is removed/hidden 
     *  
     *  
     */
    public void interactiveWin2kEclipseStatus() {
        JComponent panel = new JXPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(createIconLabel("resources/images/eclipse_win2k.png"));
        panel.add(createIconLabel("resources/images/eclipse_win2k_narrow.png"));
        panel.add(createIconLabel("resources/images/eclipse_win2k_empty.png"));
        final JXFrame frame = wrapInFrame(panel, "Compare with screenshots");
        // JW: this is a hack .. add an empty label to force the following
        // components to trailing
        addMessage(frame, "");
        JXStatusBar statusBar = getStatusBar(frame);
        statusBar.add(new JLabel("Writable")); // , JLabel.TRAILING);
        final JLabel insertLabel = new JLabel("Smart Insert");
        statusBar.add(insertLabel); //, JLabel.TRAILING);
        statusBar.add(new JLabel("2440 : 59")); //, JLabel.TRAILING);
        // mimic a second statusbar?
        statusBar.add(new JLabel("Building workspace ..."));
        Action action = new AbstractActionExt("toggle insertLabel") {

            public void actionPerformed(ActionEvent e) {
                insertLabel.setVisible(!insertLabel.isVisible());
                
            }
            
        };
        addAction(frame, action);
        frame.setSize(1000, 200);
        frame.setVisible(true);
    }

    private JLabel createIconLabel(String resource) {
        Icon wideIcon = new ImageIcon(getClass().getResource(resource));
        JLabel wide = new JLabel(wideIcon);
        wide.setAlignmentX(JLabel.TRAILING);
        return wide;
    }
    
    /**
     * Issue ??-swingx: JXStatusBar must be bidi-compliant.
     * 
     * On toggle CO, status bar should revers order of components.
     */
    public void interactiveRToL() {
        final JComponent panel = new JXPanel(); 
        panel.add(new JLabel("leading"));
        panel.add(new JLabel("trailing"));
        panel.setBorder(new TitledBorder("FlowLayout"));
        final JXFrame frame = wrapWithScrollingInFrame(panel, "Bidi-compliance of StatusBar");
        addMessage(frame, "leading");
        JXStatusBar statusBar = getStatusBar(frame);
        statusBar.add(new JLabel("trailing"));
        addComponentOrientationToggle(frame);
        frame.setSize(200, 400);
        frame.setVisible(true);
    }
    
    /**
     * do-nothing method - suppress warning if there are no other
     * test fixtures to run.
     *
     */
    public void testDummy() {
        
    }
}
