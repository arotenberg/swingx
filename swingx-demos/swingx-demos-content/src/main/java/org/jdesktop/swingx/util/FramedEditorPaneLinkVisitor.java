/*
 * $Id: FramedEditorPaneLinkVisitor.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.util;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.hyperlink.EditorPaneLinkVisitor;
import org.jdesktop.swingx.hyperlink.LinkModel;

/**
 * A LinkVisitor opening itself in a JFrame.
 * 
 * @author  Jeanette Winzenburg
 */
public class FramedEditorPaneLinkVisitor extends EditorPaneLinkVisitor {
    JFrame frame;
    
    
    public void visit(LinkModel model){
        showFrame(model);
        super.visit(model);
    }

    private void showFrame(LinkModel model) {
        if (frame == null) {
            frame = createFrame();
        }
        frame.setVisible(true);
        frame.toFront();
        frame.setTitle(String.valueOf(model.getURL()));
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame();
        frame.add(new JScrollPane(getOutputComponent()));
        frame.setSize(800, 600);
        return frame;
    }

}
