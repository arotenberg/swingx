/*
 * Created on 06.12.2008
 *
 */
package org.jdesktop.swingx.demos.highlighterext;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.demos.search.Contributor;
import org.jdesktop.swingx.demos.search.Contributors;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.treetable.TreeTableModelAdapter;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.swingxset.util.DemoUtils;
import org.jdesktop.swingxset.util.RelativePainterHighlighter;
import org.jdesktop.swingxset.util.RelativePainterHighlighter.NumberRelativizer;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

import com.sun.swingset3.DemoProperties;

@DemoProperties(
        value = "Highlighter (extended)",
        category = "Functionality",
        description = "Demonstrates value based highlighting.",
        sourceFiles = {
                "org/jdesktop/swingx/demos/highlighterext/HighlighterExtDemo.java",
                "org/jdesktop/swingxset/util/RelativePainterHighlighter.java"
                }
)

public class HighlighterExtDemo extends JPanel {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(HighlighterExtDemo.class
            .getName());

    private Contributors contributors;
    private JXTreeTable treeTable;
    private JXTree tree;
    private JXList list;
    private JXTable table;
    private JCheckBox extendedMarkerBox;
    private JButton raceButton;
    private String[] keys = {"name", "date", "merits", "email"};
    private Map<String, StringValue> stringValues;
    private int meritColumn = 2;
    private HighlighterControl highlighterControl;
    private JButton fadeInButton;

    
    public HighlighterExtDemo() {
        super(new BorderLayout());
        initComponents();
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        bind();
        // simple setup of per-column renderers, so can do only after binding
        installRenderers();
    }

    //----------------------- bind    
    /**
     * 
     */
    private void bind() {
        // set the models
        contributors = new Contributors();
        table.setModel(contributors.getTableModel());
        list.setModel(contributors.getListModel());
        tree.setModel(new DefaultTreeModel(contributors.getRootNode()));
        treeTable.setTreeTableModel(new TreeTableModelAdapter(
                tree.getModel(), contributors.getContributorNodeModel()));
        // init highlighter control
        highlighterControl = new HighlighterControl(); 
        raceButton.setAction(getAction("race"));
        fadeInButton.setAction(getAction("fadeIn"));
    }
    
    @Action 
    public void race() {
        highlighterControl.race();
    }
    
    @Action 
    public void fadeIn() {
        highlighterControl.fadeIn();
    }
    
    private javax.swing.Action getAction(String string) {
        return Application.getInstance().getContext().getActionMap(this).get(string);
    }

    // <snip> Relativizer
    // implement custom Relativizer class
    public static class MeritRelativizer extends NumberRelativizer {

        public MeritRelativizer(int column, boolean spreadColumns, 
                Number max, Number current) {
            super(column, spreadColumns, max, current);
        }

        // custom mapping of content to a Number
        @Override
        protected Number getNumber(ComponentAdapter adapter) {
            if (!(adapter.getValue(getValueColumn()) instanceof Contributor)) {
                return null;
            }
            return ((Contributor) adapter.getValue(getValueColumn())).getMerits();
        }
        // </snip>
        
    }
    
    public class HighlighterControl extends AbstractBean {
        
        private RelativePainterHighlighter tableValueBasedHighlighter;
        private boolean spreadColumns;
        private RelativePainterHighlighter valueBasedHighlighter;
        
        private Timeline raceTimeline;
        private Timeline fadeInTimeline;
        private MattePainter matte;
        private Color base = PaintUtils.setSaturation(Color.MAGENTA, .7f);
        
        public HighlighterControl() {
            matte = new MattePainter(PaintUtils.setAlpha(base, 125));
            tableValueBasedHighlighter = new RelativePainterHighlighter(matte);
            table.addHighlighter(tableValueBasedHighlighter);
            treeTable.addHighlighter(tableValueBasedHighlighter);

            valueBasedHighlighter = new RelativePainterHighlighter(matte);
            list.addHighlighter(valueBasedHighlighter);
            tree.addHighlighter(valueBasedHighlighter);
            
            setSpreadColumns(false);
            
            BindingGroup group = new BindingGroup();
            group.addBinding(Bindings.createAutoBinding(READ, 
                    extendedMarkerBox, BeanProperty.create("selected"),
                    this, BeanProperty.create("spreadColumns")));
            group.bind();
        }

        // PENDING JW: how-to find the resource of this action for injection?
        @Action
        public void race() {
            if (raceTimeline == null) {
                raceTimeline = new Timeline(this);
                raceTimeline.addPropertyToInterpolate("currentMerit", 0, 100);
            }
            raceTimeline.replay();
        }
        
        @Action
        public void fadeIn() {
            if (fadeInTimeline == null) {
                fadeInTimeline = new Timeline(this);
                fadeInTimeline.addPropertyToInterpolate("background", 
                        PaintUtils.setAlpha(base, 0), PaintUtils.setAlpha(base, 125));
                fadeInTimeline.setDuration(2000);
                fadeInTimeline.setEase(new Spline(0.7f));
            }
            fadeInTimeline.replay();
        }
        
        public void setBackground(Color color) {
            matte.setFillPaint(color);
        }
        
        public void setCurrentMerit(int merit) {
            MeritRelativizer relativizer = createMeritRelativizer(merit);
            tableValueBasedHighlighter.setRelativizer(relativizer);
            valueBasedHighlighter.setRelativizer(relativizer);
        }


        /**
         * Creates and returns a relativizer with the given intermediate value.
         * 
         */
        private MeritRelativizer createMeritRelativizer(int intermediate) {
            return new MeritRelativizer(meritColumn, isSpreadColumns(), 100, intermediate);
        }
        
        /**
         * 
         */
        private void updateTableHighlighter() {
            tableValueBasedHighlighter.setRelativizer(createMeritRelativizer(100));
            valueBasedHighlighter.setRelativizer(
                    tableValueBasedHighlighter.getRelativizer());
            if (isSpreadColumns()) {
                tableValueBasedHighlighter.setHighlightPredicate(HighlightPredicate.ALWAYS);
            } else {
                tableValueBasedHighlighter.setHighlightPredicate(
                        new HighlightPredicate.ColumnHighlightPredicate(meritColumn));
            }    
        }

        public boolean isSpreadColumns() {
            return spreadColumns;
        }
        
        public void setSpreadColumns(boolean extendedMarker) {
            boolean old = isSpreadColumns();
            this.spreadColumns = extendedMarker;
            updateTableHighlighter();
            firePropertyChange("spreadColumns", old, isSpreadColumns());
        }

    }

//---------------------- renderers
    

    /**
     * Install renderers which use the prepared string representations.
     * Note: this method is called after the binding (aka: attach models)
     * because it installs per-column renderers which in this setup can be done only 
     * after the columns are created. 
     */
    private void installRenderers() {
        initStringRepresentation();
        StringValue sv = stringValues.get("name");
        table.setDefaultRenderer(Contributor.class, new DefaultTableRenderer(sv));
        list.setCellRenderer(new DefaultListRenderer(sv));
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        treeTable.setTreeCellRenderer(new DefaultTreeRenderer(sv));
        
        for (int i = 1; i < keys.length; i++) {
            installColumnRenderers(i, new DefaultTableRenderer(stringValues.get(keys[i])));
        }
        // <snip> Unrelated, just for fun: Hyperlink 
        // Use a hyperlinkRenderer for the email column
        HyperlinkProvider provider = new HyperlinkProvider(new ContributorMailAction(
                stringValues.get("email")));
        installColumnRenderers(keys.length - 1, new DefaultTableRenderer(provider));
        table.getColumnExt(keys.length - 1).setToolTipText(
                "Note: the mail-to action will do nothing in security restricted environments");
        // </snip>
        table.packAll();
    }

    private void installColumnRenderers(int column, TableCellRenderer renderer) {
        if (column >= table.getColumnCount()) return;
        table.getColumn(column).setCellRenderer(renderer);
        treeTable.getColumn(column).setCellRenderer(renderer);
    }

    /**
     * Prepare different String representations.
     */
    private void initStringRepresentation() {
        stringValues = new HashMap<String, StringValue>();
        StringValue nameValue = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Contributor) {
                    Contributor c = (Contributor) value;
                    return c.getLastName() + ", " + c.getFirstName();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        stringValues.put("name", nameValue);

        // show the joined date
        StringValue dateValue = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Contributor) {
                    return StringValues.DATE_TO_STRING.getString(
                            ((Contributor) value).getJoinedDate());
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        stringValues.put("date", dateValue);
        
        // show the merits
        StringValue meritValue = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Contributor) {
                    return StringValues.NUMBER_TO_STRING.getString(
                            ((Contributor) value).getMerits());
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        stringValues.put("merits", meritValue);
        // <snip> Unrelated, just for fun: Hyperlink 
        // string representation of contributor's email
        StringValue emailValue = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Contributor) {
                    URI mail = ((Contributor) value).getEmail();
                    // strip mailto:
                    String path = mail.toString();
                    return path.replace("mailto:", "");
                }
                return StringValues.EMPTY.getString(value);
            }
            
        };
        // </snip>
        stringValues.put("email", emailValue);

    }

    // <snip> Unrelated, just for fun: Hyperlink 
    // custom hyperlink action which delegates to Desktop 
    public static class ContributorMailAction extends AbstractHyperlinkAction<Contributor> {
        HyperlinkAction browse = HyperlinkAction.createHyperlinkAction(null,
                java.awt.Desktop.Action.MAIL);

        StringValue sv;
        
        public ContributorMailAction(StringValue sv) {
            this.sv = sv;
        }
        
        @Override
        protected void installTarget() {
            if (sv == null) return;
            // configure the name based on the StringValue
            setName(sv.getString(getTarget()));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (target == null) return;
            browse.setTarget(target.getEmail());
            browse.actionPerformed(null);
        }
        // </snip>
    }
    
//------------------ init ui
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        table = new JXTable();
        list = new JXList(true);
        tree = new JXTree();
        treeTable = new JXTreeTable();
        
        table.setColumnControlVisible(true);
        treeTable.setColumnControlVisible(true);

        JTabbedPane tab = new JTabbedPane();
        addTab(tab, table, "tableTabTitle", true);
        addTab(tab, list, "listTabTitle", true);
//        addTab(tab, tree, "HighlighterExtDemo.tree", true);
//        addTab(tab, treeTable, "HighlighterExtDemo.treeTable", true);
        add(tab);
        
        extendedMarkerBox = new JCheckBox();
        extendedMarkerBox.setName("extendedMarkerBox");
        raceButton = new JButton();
        raceButton.setName("playButton");
        fadeInButton = new JButton();
        fadeInButton.setName("fadeInButton");
        
        JPanel control = new JPanel();
        control.add(extendedMarkerBox);
        control.add(raceButton);
        control.add(fadeInButton);
        add(control, BorderLayout.SOUTH);
    }

    private void addTab(JTabbedPane tab, JComponent comp, String string, boolean createScroll) {
        String name = DemoUtils.getResourceString(getClass(), string);
        tab.addTab(name, createScroll ? new JScrollPane(comp) : comp);
    }
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame(HighlighterExtDemo.class.getAnnotation(DemoProperties.class).value());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new HighlighterExtDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}
