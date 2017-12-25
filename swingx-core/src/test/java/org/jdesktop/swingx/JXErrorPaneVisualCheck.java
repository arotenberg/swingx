/*
 * $Id: JXErrorPaneVisualCheck.java 4310 2013-08-27 10:38:06Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;

import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

/**
 * A unit test for the JXErrorPane
 *
 * @author rah003
 */
public class JXErrorPaneVisualCheck extends InteractiveTestCase {

    public static void main(String[] args) throws Exception {
      JXErrorPaneVisualCheck test = new JXErrorPaneVisualCheck();
      try {
          test.runInteractive("Texts");
//          test.runInteractiveTests("interactiveShowFrameWithException");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }
    
    /**
     * Issue http://java.net/jira/browse/SWINGX-1541
     * 
     * Showing no texts at all (no message, no title, no buttontexts)
     * 
     */
    public void interactiveErrorDialogTexts() {
        Action action = new AbstractAction("show") {

            @Override
            public void actionPerformed(ActionEvent e) {
                JXErrorPane.showDialog( new IllegalArgumentException( "Error! ") );
            }
            
        };
        showInFrame(new JButton(action), "error dialog");
    }
    
    /**
     * Custom details
     */
    public void interactiveMinimalError() {
        Exception e = new NullPointerException("something ...");
        
        StringBuffer html = new StringBuffer("<html>");
        html.append("<h2>" + "Error" + "</h2>");
        html.append("<HR size='1' noshade>");
        html.append("<div></div>");
        html.append("<b>Message:</b>");
        html.append("<pre>");
        html.append("    " + e.toString());
        html.append("</pre>");
        html.append("<b>Level:</b>");
        html.append("<pre>");
        html.append("    " + ErrorLevel.SEVERE);
        html.append("</pre>");
        html.append("</html>");

        ErrorInfo errorInfo = new ErrorInfo("Error", e.getMessage(), 
                html.toString(), null, e, ErrorLevel.SEVERE, null);
        JXErrorPane.showDialog(null, 
                errorInfo);


    }
    
    /**
     * Converts the incoming string to an escaped output string. This method
     * is far from perfect, only escaping &lt;, &gt; and &amp; characters
     */
    private static String escapeXml(String input) {
        String s = input == null ? "" : input.replace("&", "&amp;");
        s = s.replace("<", "&lt;");
        return s = s.replace(">", "&gt;");
    }

    /**
     * Issue #45-swinglabs: JXErrorPane paints message text over action buttons 
     *
     */
    public void interactiveLongMessageText() {
        ErrorInfo errorInfo = new ErrorInfo("Server Error",
                "The request cannot be carried out\n1\n2\n3\n4\n5\n6\n7" +
                "\n8\n9\n0\n1\n2\n3\n4\nThis text should be shown in scroll pane.", "Server Error",
                null, new Exception(), Level.SEVERE, null);
        JXErrorPane.showDialog(null,errorInfo );
    }
    
    /**
     * Issue #802-swingx: Default size is too small. 
     */
    public void interactiveTooSmall() {
        JXErrorPane.showDialog(null, new ErrorInfo("Title", "This is a test!", null,
                null, new Exception("This is a test!"), null, null));
    }
    
    /**
     * Issue #468-swingx: JXErrorPane can't cope with null errorInfo.
     *
     */
    public void interactiveNPEWithDefaultErrorInfo() {
        JXErrorPane errorPane = new JXErrorPane();
        JXErrorPane.showDialog(null, errorPane);
    }
    
    /**
     * Issue ???: showFrame not working
     *
     */
    public void interactiveShowFrameWithException() {
        JXErrorPane.showFrame(new Exception());
    }
    
    /**
     * Issue #468-swingx: JXErrorPane can't cope with null errorInfo.
     *
     */
    public void interactiveSetNullErrorInfo() {
        JXErrorPane errorPane = new JXErrorPane();
        try {
            errorPane.setErrorInfo(null);
            fail("Failed to fail while setting null ErrorInfo");
        } catch (NullPointerException e) {
            assertEquals("Unexpected error message", "ErrorInfo can\'t be null. Provide valid ErrorInfo object.", e.getMessage());
            // ignore - expected.
        }
    }
    
    /**
     * Issue #467-swingx: calling updateUI throws error.
     *
     */
    public void interactiveUpdateUI() {
        final JXErrorPane errorPane = new JXErrorPane();
        errorPane.updateUI();
    }

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
