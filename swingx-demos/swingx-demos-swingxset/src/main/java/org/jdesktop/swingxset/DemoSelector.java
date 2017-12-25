/*
 * Created on 10.04.2008
 *
 */
package org.jdesktop.swingxset;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;

import com.sun.swingset3.Demo;

/**
 * Quick shot playing with TaskPane/Container instead of rolling its own.
 * 
 */
public class DemoSelector extends AbstractBean {

    private static final List<Demo> EMPTY_DEMOS = Collections.emptyList();
    private Demo selectedDemo;
    private Map<String, JXTaskPane> categoryMap;
    private List<Demo> demoSet;
    private JComponent selectorComponent;
    
    public DemoSelector() {
        this(EMPTY_DEMOS);
    }
    
    public DemoSelector(List<Demo> demos) {
        demoSet = demos;
    }

    public void setDefaultSelectedDemo() {
        if (demoSet.size() > 0) {
            setSelectedDemo(demoSet.get(0));
        }
    }
    
    public boolean isDefaultSelectedDemo(Demo demo) {
        return demoSet.size() == 0 ? false : demoSet.get(0).equals(demo);
    }
    
    public JComponent getSelectorComponent() {
        if (selectorComponent != null) return selectorComponent;
        return getSelectorComponent(demoSet);
    }
    
    private JComponent getSelectorComponent(List<Demo> demoSet) {
        JXTaskPaneContainer container = new JXTaskPaneContainer() {
            // Issue #1189-swingx: reasonable implementation of block increment
            @Override
            public int getScrollableBlockIncrement(Rectangle visibleRect,
                    int orientation, int direction) {
                return SwingConstants.VERTICAL == orientation ? visibleRect.height :
                    visibleRect.width;
            }
            
        };
        container.setLayout(new VerticalLayout(0));
        container.setBorder(BorderFactory.createEmptyBorder());
        for (Demo demo : demoSet) {
            String category = demo.getCategory();
            JXTaskPane taskPane = getTaskPane(category);
            if (taskPane == null) {
                taskPane = createTaskPane(demo);
                addTaskPane(taskPane, category);
                container.add(taskPane);
            }
            addDemo(taskPane, demo);
        }
        JScrollPane pane = new JScrollPane(container);
        return pane;
    }
    
    private void addDemo(JXTaskPane taskPane, Demo demo) {
        taskPane.add(new DemoAction(demo));
    }

    private void addTaskPane(JXTaskPane taskPane, String category) {
        if (categoryMap ==  null) {
            categoryMap = new HashMap<String, JXTaskPane>();
        }
        categoryMap.put(category, taskPane);
    }

    private JXTaskPane createTaskPane(Demo demo) {
        JXTaskPane taskPane = new JXTaskPane();
        taskPane.setTitle(demo.getCategory());
        taskPane.setName(demo.getCategory());
        return taskPane;
    }

    private JXTaskPane getTaskPane(String category) {
        if (categoryMap == null) return null;
        return categoryMap.get(category);
    }

    public void setSelectedDemo(Demo demo) {
        Object old = getSelectedDemo();
        this.selectedDemo = demo;
        firePropertyChange("selectedDemo", old, getSelectedDemo());
    }
    
    public Demo getSelectedDemo() {
        return selectedDemo;
    }

    public class DemoAction extends AbstractHyperlinkAction<Demo> {
        
        public DemoAction(Demo demo) {
            super(demo);
        }

        @Override
        protected void installTarget() {
            if (getTarget() == null) return;
            setSmallIcon(getTarget().getIcon());
            setName(getTarget().getName());
            setShortDescription(getTarget().getShortDescription());
        }

        public void actionPerformed(ActionEvent e) {
            setSelectedDemo(getTarget());
            setVisited(true);
        }
        
    }

}
