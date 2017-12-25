package org.jdesktop.swingx.demos.search.edited;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXFindBar;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.IconHighlighter;
import org.jdesktop.swingx.demos.search.MatchingTextHighlighter;
import org.jdesktop.swingx.demos.search.XMatchingTextHighlighter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.search.AbstractSearchable;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.util.DecoratorFactory;

/**
 * A FindBar searching on a JXTable to test some special highlighter cases:
 * <ul>
 * <li>Cell alignments left, right, center</li>
 * <li>JLabel with icon</li>
 * <li>Right to left component orientation</li>
 * <li>Highlight matches in viewed text AND in the ellipsis</li>
 * <li>Stable moving of the Highlighter while dragging the Column-width</li>
 * <li>One row with different rowHeight</li>
 * </ul>
 * 
 * @author Thorsten Klimpel
 */
public class JXTableFindBarMatchHighlighterDemo {
	static JFrame frame;
	static JXTable table;
	private static JXFindBar findBar;

	private static String[] data = {
			"Some Strings to show the searching, matching and highlighting capabilities of the JXFindBar and the MatchingTextHighlighter",
			"Who cut thi", "Nearly every sea is blue",
			"You're right, I will stop" };

	private static String[] columns = { "align left", "align right", "center",
			"align left + icon", "align right + icon", "center + icon",
			"Length", "Upper-case" };

	/**
	 * It will all be done here.
	 * 
	 * @param args will be ignored
	 */
	public static void main(String[] args) {
		table = new JXTable(new SampleTableModel());
		table.setRowHeight(2, 46);

		setLookAndFeel();
		setColumnAlignments();
		addIconsToSomeColumns();
		final JXCollapsiblePane collapsible = connectCollapsibleFindBarWithTable();
		installTheMatchingTextHighlighter();
		JCheckBox rightToLeftSwitch = createComponentOrientationSwitch();
		buildGui(collapsible, rightToLeftSwitch);
		callTheRobots();
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
	}

	private static void setColumnAlignments() {
		table.getColumnExt(0).setCellRenderer(
				new DefaultTableRenderer(null, SwingConstants.LEADING));
		table.getColumnExt(1).setCellRenderer(
				new DefaultTableRenderer(null, SwingConstants.TRAILING));
		table.getColumnExt(2).setCellRenderer(
				new DefaultTableRenderer(null, SwingConstants.CENTER));
		table.getColumnExt(3).setCellRenderer(
				new DefaultTableRenderer(null, SwingConstants.LEADING));
		table.getColumnExt(4).setCellRenderer(
				new DefaultTableRenderer(null, SwingConstants.TRAILING));
		table.getColumnExt(5).setCellRenderer(
				new DefaultTableRenderer(null, SwingConstants.CENTER));
	}

	private static void addIconsToSomeColumns() {
		Icon icon = new Icon() {
			@Override
			public int getIconHeight() {
				return 8;
			}

			@Override
			public int getIconWidth() {
				return 18;
			}

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.setColor(Color.GREEN);
				g.fillRect(x, y, getIconWidth(), getIconHeight());
			}
		};

		IconHighlighter iconHighlighter = new IconHighlighter(
				new HighlightPredicate.ColumnHighlightPredicate(3, 4, 5), icon);
		table.addHighlighter(iconHighlighter);
	}

	private static JXCollapsiblePane connectCollapsibleFindBarWithTable() {
		final JXCollapsiblePane collapsible = new JXCollapsiblePane();
		findBar = SearchFactory.getInstance().createFindBar();
		table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER,
				Boolean.TRUE);
		findBar.setSearchable(table.getSearchable());
		collapsible.add(findBar);
		collapsible.setCollapsed(false);

		Action openFindBar = new AbstractActionExt() {
			@Override
			public void actionPerformed(ActionEvent e) {
				collapsible.setCollapsed(false);
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
						.focusNextComponent(findBar);
			}
		};

		Action closeFindBar = new AbstractActionExt() {
			@Override
			public void actionPerformed(ActionEvent e) {
				collapsible.setCollapsed(true);
				table.requestFocusInWindow();
			}

		};

		table.getActionMap().put("find", openFindBar);
		findBar.getActionMap().put("close", closeFindBar);

		return collapsible;
	}

	private static void installTheMatchingTextHighlighter() {
		MatchingTextHighlighter matchingTextMarker = new XMatchingTextHighlighter();
		matchingTextMarker.setPainter(DecoratorFactory.createPlainPainter());
		((AbstractSearchable) table.getSearchable())
				.setMatchHighlighter(matchingTextMarker);
	}

	private static JCheckBox createComponentOrientationSwitch() {
		JCheckBox rightToLeftSwitch = new JCheckBox();
		rightToLeftSwitch.setAction(new AbstractAction("RightToLeft") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getComponentOrientation().isLeftToRight())
					frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				else
					frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

				frame.repaint();
			}
		});
		return rightToLeftSwitch;
	}

	private static void buildGui(final JXCollapsiblePane collapsible,
			JCheckBox rightToLeftSwitch) {
		frame = new JFrame();
		frame.getContentPane().add(collapsible, BorderLayout.NORTH);

		frame.getContentPane().add(new JScrollPane(table));
		frame.add(rightToLeftSwitch, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setMinimumSize(new Dimension(400, 170));
		frame.setSize(new Dimension(800, 240));
		frame.setVisible(true);
	}

	private static void callTheRobots() {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_A);
			robot.keyPress(KeyEvent.VK_R);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private static class SampleTableModel extends AbstractTableModel {
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String theData = data[rowIndex];
			Object result = null;
			switch (columnIndex) {
			case 6:
				result = theData.length();
				break;

			case 7:
				result = theData.toUpperCase();
				break;

			default:
				result = theData;
			}

			return result;
		}

	}// private static class SampleTableModel extends AbstractTableModel

}