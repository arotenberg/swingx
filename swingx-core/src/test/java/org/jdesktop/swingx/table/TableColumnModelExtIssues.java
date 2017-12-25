/*
 * Created on 24.08.2006
 *
 */
package org.jdesktop.swingx.table;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.Action;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test to exposed known issues of <code>TableColumnModelExt</code>
 * implementations.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class TableColumnModelExtIssues extends TableColumnModelTest {

    public static void main(String args[]) {
        setSystemLF(false);
       TableColumnModelExtIssues test = new TableColumnModelExtIssues();
       try {
         test.runInteractiveTests();
       //    test.runInteractiveTests("interactive.*Column.*");
//           test.runInteractiveTests("interactive.*TableHeader.*");
       //    test.runInteractiveTests("interactive.*SorterP.*");
       //    test.runInteractiveTests("interactive.*Column.*");
       } catch (Exception e) {
           System.err.println("exception when executing interactive tests:");
           e.printStackTrace();
       }
   }

   
    /**
     * Issue #624-swingx: support use-case to store/restore column sequence.
     */
    public void interactiveRestoreColumnOrder() {
        final JXTable source = new JXTable(20, 5);
        source.setColumnControlVisible(true);
        final JXTable target = new JXTable(source.getModel());
        target.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(source, target, "source --> target: copy column sequence");
        Action resetOrder = new AbstractActionExt("reset to initial") {

            public void actionPerformed(ActionEvent e) {
                source.tableChanged(null);
                target.tableChanged(null);
                
            }
            
        };
        Action copyOrder = new AbstractActionExt("copy source order to target") {

            public void actionPerformed(ActionEvent e) {
                // source ...
                // hidden columns, don't care about sequence
                Collection<TableColumn> hiddenSourceColumns = source.getColumns(true);
                hiddenSourceColumns.removeAll(source.getColumns());
                // temporary set visible so we can get hold of their "virtual" 
                // view index
                for (TableColumn tableColumn : hiddenSourceColumns) {
                    ((TableColumnExt) tableColumn).setVisible(true);
                }
                
                // target
                Collection<TableColumn> hiddenTargetColumns = target.getColumns(true);
                hiddenTargetColumns.removeAll(target.getColumns());
                for (TableColumn tableColumn : hiddenTargetColumns) {
                    ((TableColumnExt) tableColumn).setVisible(true);
                }
                // visible sequence if all were visible
                List<TableColumn> sourceColumns = source.getColumns();
                for (int i = 0; i < sourceColumns.size(); i++) {
                    int sourceID = sourceColumns.get(i).getModelIndex();
                    List<TableColumn> targetColumns = target.getColumns();
                    for (int j = 0; j < targetColumns.size(); j++) {
                        if (targetColumns.get(j).getModelIndex() == sourceID) {
                            target.getColumnModel().moveColumn(j, i);
                            break;
                        }
                    }
                }
                // cleanup: hide originally hidden columns
                for (TableColumn tableColumn : hiddenSourceColumns) {
                    ((TableColumnExt) tableColumn).setVisible(false);
                }
                for (TableColumn tableColumn : hiddenTargetColumns) {
                    ((TableColumnExt) tableColumn).setVisible(false);
                }
            }
            
        };
        addAction(frame, resetOrder);
        addAction(frame, copyOrder);
        frame.pack();
        frame.setVisible(true);
    }
    /** 
     * Do nothing, just to make the runner happy if there are no 
     * issues.
     *
     */
    public void testDummy() {
        
    }
}
