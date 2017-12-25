/*
 * $Id: TableColumnExtIssues.java 3017 2008-08-01 13:31:34Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.table.TableColumn;

import junit.framework.TestCase;

import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.SerializableSupport;

/**
 * Test to exposed known issues of <code>TableColumnExt</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 *  
 * @author Jeanette Winzenburg
 */
public class TableColumnExtIssues extends TestCase {

    /**
     * Issue #815-swingx: Listeners must not be cloned.
     * Test that a listener registered with the clone is not registered 
     * with the original.
     */
    public void testListenersOriginalNotRegistered() {
        TableColumnCloneable column = new TableColumnCloneable();
        column.setPreferredWidth(column.getMinWidth());
        int old = column.getPropertyChangeListeners().length;
        assertEquals("sanity, no listener", 0, old);
        TableColumnCloneable clone = (TableColumnCloneable) column.clone();
        PropertyChangeReport report = new PropertyChangeReport();
        clone.addPropertyChangeListener(report);
        assertEquals(old, column.getPropertyChangeListeners().length);
    }
    

    
    /**
     * Issue #815-swingx: Listeners must not be cloned.
     * test that listeners registered with the original are not notified
     * when changing the clone.
     */
    public void testListenerNotificationCloneChanged() {
        TableColumnCloneable column = new TableColumnCloneable();
        column.setPreferredWidth(column.getMinWidth());
        PropertyChangeReport report = new PropertyChangeReport();
        column.addPropertyChangeListener(report);
        TableColumnCloneable clone = (TableColumnCloneable) column.clone();
        clone.setPreferredWidth(column.getPreferredWidth() + 10);
        assertEquals(0, report.getEventCount());
    }
    
    /**
     * Issue #815-swingx: Listeners must not be cloned.
     * Test that the listeners are still registered to the old.
     */
    public void testListenersOriginalRegistered() {
        TableColumnCloneable column = new TableColumnCloneable();
        PropertyChangeReport report = new PropertyChangeReport();
        column.addPropertyChangeListener(report);
        int old = column.getPropertyChangeListeners().length;
        column.clone();
        assertEquals(old, column.getPropertyChangeListeners().length);
     }
    
    /**
     * Try a TableColumn sub with a better-behaved clone - removes the cloned listeners.
     * 
     * Doesn't help: the listeners are removed from the original as well. The problem
     * is in core - it doesn't provide a well-behaved clone implementation (in fact, it
     * doesn't provide any implementation). With the private listener registering there
     * is nothing subclasses can do. Except not implement cloneable (which is the best
     * to do anyway, see Bloch)
     */
    public static class TableColumnCloneable extends TableColumn implements Cloneable {

        @Override
        public Object clone()  {
            try {
                TableColumn column = (TableColumn) super.clone();
                PropertyChangeListener[] listeners = getPropertyChangeListeners();
                for (PropertyChangeListener listener : listeners) {
                    column.removePropertyChangeListener(listener);
                }
                return column;
            } catch (CloneNotSupportedException e) { // don't expect
            }
            return null;
        }
        
        
    }

    /**
     * Issue #??-swingx: tableColumnExt does not fire propertyChange on resizable.
     * 
     * Happens, if property is changed indirectly by changing min/max value
     * to be the same.
     *
     */
    public void testResizableBoundProperty() {
        TableColumnExt columnExt = new TableColumnExt();
        // sanity: assert expected defaults of resizable, minWidth
        assertTrue(columnExt.getResizable());
        assertTrue(columnExt.getMinWidth() > 0);
        PropertyChangeReport report = new PropertyChangeReport();
        columnExt.addPropertyChangeListener(report);
        columnExt.setMaxWidth(columnExt.getMinWidth());
        if (!columnExt.getResizable()) {
            assertEquals("fixing column widths must fire resizable ", 
                    1, report.getEventCount("resizable"));
        } else {
           fail("resizable must respect fixed column width"); 
        }
        
    }
    
    /**
     * Sanity test Serializable: Listeners? Report not serializable?
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * 
     */
    public void testSerializable() throws IOException, ClassNotFoundException {
        TableColumnExt columnExt = new TableColumnExt();
        PropertyChangeReport report = new PropertyChangeReport();
        columnExt.addPropertyChangeListener(report);
        TableColumnExt serialized = SerializableSupport.serialize(columnExt);
        PropertyChangeListener[] listeners = serialized
                .getPropertyChangeListeners();
        assertTrue(listeners.length > 0);
    }


    /**
     * Issue #??-swingx: must handle non-serializable client properties
     * gracefully.
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * 
     * 
     */
    public void testNonSerializableClientProperties() throws IOException, ClassNotFoundException {
        TableColumnExt columnExt = new TableColumnExt();
        Object value = new Object();
        columnExt.putClientProperty("date", value );
        SerializableSupport.serialize(columnExt);
     }


    /**
     * Dummy stand-in test method, does nothing. 
     * without, the test would fail if there are no open issues.
     *
     */
    public void testDummy() {
    }
}
