package org.jdesktop.swingx.action;

//Click twice on "Refresh" button (it will refresh the tool bar twice)
//Click on one of the tool bar button (eg "ChoiceA")
//It will throw the exception

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import org.jdesktop.swingx.action.ActionManager;
import org.jdesktop.swingx.action.BoundAction;

@SuppressWarnings("nls")
public class BoundActionIssues {

    public static void main(String[] args) {
        new BoundActionIssues().start();
    }

    ActionManager am = new ActionManager();
    ActionContainerFactory acf = new ActionContainerFactory(am);
    JToolBar bar = null;

    BoundActionIssues() {
        initActionManager();

        bar = acf.createToolBar(Arrays.asList(new String[] { "ba", "bb" }));
    }

    private void initActionManager() {
        BoundAction ba = new BoundAction("ChoiceA");
        BoundAction bb = new BoundAction("ChoiceB");
        ba.registerCallback(this, "test");
        bb.registerCallback(this, "test");
        ba.setStateAction();
        ba.setSelected(true);
        bb.setStateAction();
        String group = "agroup";

        ba.setGroup(group);
        bb.setGroup(group);

        am.addAction("ba", ba);
        am.addAction("bb", bb);
    }

    private void start() {
        JFrame f = createFrame();
        f.getContentPane().add(bar, "North");
        JButton brefresh = new JButton("Refresh");
        brefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshToolBar();
            }
        });
        f.getContentPane().add(brefresh, "South");
        f.setVisible(true);
    }

    private void refreshToolBar() {
        bar.removeAll();
        bar.add(acf.createButton(am.getAction("ba")));
        bar.add(acf.createButton(am.getAction("bb")));
    }

    private static JFrame createFrame() {
        JFrame f = new JFrame();
        f.setSize(400, 400);
        f.setLocationRelativeTo(null);
        return f;
    }
}
