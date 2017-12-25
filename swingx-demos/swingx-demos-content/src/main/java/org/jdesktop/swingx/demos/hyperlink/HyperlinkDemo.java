/*
 * $Id: HyperlinkDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.demos.hyperlink;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.WrappingProvider;
import org.jdesktop.swingxset.util.DemoUtils;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXHyperlink}.
 *
 * @author Karl George Schaefer
 * @author Richard Bair (original JXHyperlinkDemoPanel)
 * @author Jeanette Winzenburg (original JXHyperlinkDemoPanel)
 */
@DemoProperties(
    value = "JXHyperlink Demo",
    category = "Controls",
    description = "Demonstrates JXHyperlink, a button providing a hyperlink feel.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/hyperlink/HyperlinkDemo.java",
        "org/jdesktop/swingx/demos/hyperlink/resources/HyperlinkDemo.properties"
    }
)
@SuppressWarnings("serial")
public class HyperlinkDemo extends JPanel {
    private JXHyperlink plainBrowse;
    private JXHyperlink customBrowse;
    private JXHyperlink plainMail;
    private JXHyperlink customLink;
    private JXList linkList;
    private JXTable linkTable;
    private JXTree linkTree;
    
    
    public HyperlinkDemo() {
        super(new BorderLayout());
        
        createHyperlinkDemo();
        try {
            bind();
        } catch (URISyntaxException e) {
            // will not happen ... we did type correctly, didn't we ;-)
        }
        // inject after bind - overwriting some properies of hyperlinks
        DemoUtils.injectResources(this);
//        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
    }
    
    private void bind() throws URISyntaxException {
        // <snip> Hyperlink with desktop action
        // default browser action, text defaults to url
        plainBrowse.setURI(new URI("https://swingx.dev.java.net"));
        // default mailer action, text defaults to uri
        plainMail.setURI(new URI("mailto:nobody@dev.java.net"));
        // default browser action, custom inject properties from resources
        customBrowse.setURI(new URI("http://wiki.java.net/bin/view/Javadesktop/SwingLabsSwingX"));
        // </snip>
        DemoUtils.setSnippet("Hyperlink with desktop action", plainBrowse, plainMail, customBrowse);
        
        // <snip> Hyperlink with custom action
        customLink.setAction(createLinkAction());
        // </snip>
        DemoUtils.setSnippet("Hyperlink with custom action", customLink);
        
        // <snip> List with hyperlink renderer
        // model containing URIs
        linkList.setModel(new DefaultComboBoxModel(new Object[] {
                new URI("https://swingx.dev.java.net"),
                new URI("http://wiki.java.net/bin/view/Javadesktop/SwingLabsSwingX"),
                new URI("http://forums.java.net/jive/forum.jspa?forumID=73")
        }));
        // enable rollover
        linkList.setRolloverEnabled(true);
        // set a renderer delegating to a HyperlinkProvider configured
        // with raw HyperlinkAction
        linkList.setCellRenderer(new DefaultListRenderer(new HyperlinkProvider(
                new HyperlinkAction())));
        //</snip>
        DemoUtils.setSnippet("List with hyperlink renderer", linkList);
        
        // <snip> Tree with hyperlink renderer
        // model containing URIs wrapped in treeNodes
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
                new URI("http://java.net"));
        for (int i = 0; i < linkList.getElementCount(); i++) {
            root.add(new DefaultMutableTreeNode(linkList.getElementAt(i)));
        }
        linkTree.setModel(new DefaultTreeModel(root));
        linkTree.expandAll();
        // enable rollover
        linkTree.setRolloverEnabled(true);
        // set a renderer delegating to wrapper around a HyperlinkProvider
        // configured with a raw HyperlinkAction 
        linkTree.setCellRenderer(new DefaultTreeRenderer(new WrappingProvider(
                new HyperlinkProvider(new HyperlinkAction()))));
        // </snip>
        DemoUtils.setSnippet("Tree with hyperlink renderer", linkTree);
        
    }

    private Action createLinkAction() {
        // <snip> Hyperlink with custom action
        // custom implementation of AbstractHyperlinkAction
        // updates visited property and related state
        AbstractHyperlinkAction<?> action = new AbstractHyperlinkAction<Object>() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null, 
                        getResourceString("customAction.message"),
                        getResourceString("customAction.title"), 
                        JOptionPane.YES_NO_OPTION);
                setVisited(option == JOptionPane.YES_OPTION);
            }

            @Override
            public void setVisited(boolean visited) {
                super.setVisited(visited);
                setSmallIcon(isVisited() ? getResourceIcon("customAction.verifiedIcon") 
                        : getResourceIcon("customAction.unverifiedIcon")); 
                setName(isVisited() ? getResourceString("customAction.verifiedText")
                        : getResourceString("customAction.unverifiedText"));
            }
            // </snip>
        };
        action.setVisited(false);
        return action;
    }

    
    
//-------------------------- init ui
    
    protected Icon getResourceIcon(String string) {
        return DemoUtils.getResourceIcon(getClass(), string);
    }

    protected String getResourceString(String string) {
        return DemoUtils.getResourceString(getClass(), string);
    }

    private void createHyperlinkDemo() {
        JXTitledSeparator simple = new JXTitledSeparator();
        simple.setName("simpleSeparator");
        
        plainBrowse = new JXHyperlink();
        plainBrowse.setName("plainBrowse");
        
        plainMail = new JXHyperlink();
        plainMail.setName("plainMail");
        
        customBrowse = new JXHyperlink();
        customBrowse.setName("customBrowse");
        
        JXTitledSeparator custom = new JXTitledSeparator();
        custom.setName("customSeparator");
        customLink = new JXHyperlink();
        customLink.setName("customLink");
        
        JComponent standaloneLinks = new JXPanel(new VerticalLayout(20));
        standaloneLinks.add(simple);
        standaloneLinks.add(plainBrowse);
        standaloneLinks.add(plainMail);
        standaloneLinks.add(customBrowse);
        standaloneLinks.add(custom);
        standaloneLinks.add(customLink);
        standaloneLinks.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JComponent renderedLinks = new JXPanel(new BorderLayout());
        linkList = new JXList();
        
        linkTable = new JXTable();
        linkTable.setVisibleRowCount(10);

        linkTree = new JXTree();
        
        JXPanel top = new JXPanel(new GridLayout(1, 2, 20, 10));
        top.add(new JScrollPane(linkList));
        top.add(new JScrollPane(linkTree));
        renderedLinks.add(top);
//        renderedLinks.add(new JScrollPane(linkTable), BorderLayout.SOUTH);
        renderedLinks.setBorder(standaloneLinks.getBorder());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setName("hyperlinkTabs");
        addTab(tabbedPane, standaloneLinks, "standaloneTab", false);
        addTab(tabbedPane, renderedLinks, "renderedTab", false);
        
        add(tabbedPane);
    }
    
    private void addTab(JTabbedPane tab, JComponent comp, String string, boolean createScroll) {
        String name = getResourceString(string);
        tab.addTab(name, createScroll ? new JScrollPane(comp) : comp);
    }
    

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(HyperlinkDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new HyperlinkDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}
