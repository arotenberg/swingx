/*
 * Created on 08.05.2008
 *
 */
package org.jdesktop.swingx.util;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Icon;

import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.hyperlink.LinkModelAction;

/**
 * @deprecated
 * No longer used ... move variant into SwingX, using HyperlinkAction as default visitor.
 */
@Deprecated
public class XDemoUtilities {

    // PENDING JW: formulate with HyperlinkAction
    public static class LinkBrowser implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof LinkModel) {
                final LinkModel link = (LinkModel) e.getSource();
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(link.getURL().toURI());
                    link.setVisited(true);
                } catch (Exception e1) {
                    // TODO: error handling
                }
            }

        }

    }
    
//-------------------- move to SwingX    
    
    // PENDING JW: formulate with HyperlinkAction
    public static class BrowseLinkAction extends LinkModelAction<XLinkModel> {

        public BrowseLinkAction(XLinkModel model) {
            super(model, new LinkBrowser());
        }

        @Override
        protected void updateFromTarget() {
            super.updateFromTarget();
            if (getTarget() != null) {
                putValue(SMALL_ICON, getTarget().getIcon());
            }
        }

        
        
    }
    /**
     * Added property icon.
     */
    public static class XLinkModel extends LinkModel {
        
        private Icon icon;
        private String realText;

        public XLinkModel() {
            this(null, null, null);
        }

        public XLinkModel(String text, URL url) {
            this(text, null, url);
        }

        public XLinkModel(Icon icon, URL url) {
           this(null, icon, url); 
        }
        
        public XLinkModel(String text, Icon icon, URL url) {
            super(text, null, url);
            setIcon(icon);
        }
        
        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon icon) {
            Object old = getIcon();
            this.icon = icon;
            firePropertyChange("icon", old, getIcon());
        }

        @Override
        public String getText() {
            // super returns url.toString if text == null.
            if ((getIcon() !=  null) && (realText == null)) {
                return realText;
            }
            return super.getText();
        }

        @Override
        public void setText(String text) {
            // want to keep track of "real" null text as super tries to be clever 
            realText = text;
            super.setText(text);
        }
        
        
    }
}
