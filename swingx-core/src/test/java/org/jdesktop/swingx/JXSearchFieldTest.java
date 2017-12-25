package org.jdesktop.swingx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.JXSearchField.ClearAction;
import org.jdesktop.swingx.JXSearchField.FindAction;
import org.jdesktop.swingx.JXSearchField.LayoutStyle;
import org.jdesktop.swingx.JXSearchField.SearchMode;
import org.jdesktop.swingx.plaf.SearchFieldUI;
import org.jdesktop.swingx.plaf.UIManagerExt;
import org.jdesktop.swingx.search.NativeSearchFieldSupport;
import org.jdesktop.swingx.search.RecentSearches;
import org.jdesktop.swingx.search.RecentSearches.RecentSearchesPopup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JXSearchFieldTest {
    boolean focused;

    boolean eventReceived;

    JXSearchField searchField;

    @Before
    public void setUp() throws Exception {
        searchField = new JXSearchField() {
            @Override
            public boolean isFocusOwner() {
                return focused;
            }
        };
        searchField.setInstantSearchDelay(0);
        // make tests work under leopard
        searchField.setUseNativeSearchFieldIfPossible(false);
    }

    @Test
    public void testSearchPopupPropertyChange() throws Exception {
        final JPopupMenu popupMenu = new JPopupMenu();
        searchField.addPropertyChangeListener("findPopupMenu",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        assertNull(evt.getOldValue());
                        assertSame(evt.getNewValue(), popupMenu);
                        eventReceived = true;
                    }
                });

        NativeSearchFieldSupport.setFindPopupMenu(searchField, popupMenu);
        assertTrue(eventReceived);
    }

    @Test
    public void testCancelAction() throws Exception {
        assertSame(ClearAction.class, searchField.getCancelAction().getClass());
        ActionListener a = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eventReceived = true;
            }
        };
        searchField.setCancelAction(a);
        assertSame(a, searchField.getCancelAction());
        eventReceived = false;
        searchField.getCancelButton().doClick(0);
        assertTrue(eventReceived);

        NativeSearchFieldSupport.setCancelAction(searchField, null);
        assertSame(ClearAction.class, searchField.getCancelAction().getClass());
    }

    @Test
    public void testFindAction() throws Exception {
        assertSame(FindAction.class, searchField.getFindAction().getClass());
        ActionListener a = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eventReceived = true;
            }
        };
        searchField.setFindAction(a);
        assertSame(a, searchField.getFindAction());
        eventReceived = false;
        searchField.getFindButton().doClick(0);
        assertTrue(eventReceived);

        NativeSearchFieldSupport.setFindAction(searchField, null);
        assertSame(FindAction.class, searchField.getFindAction().getClass());
    }

    @Test
    public void testCancelActionPropertyChange() throws Exception {
        final ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        };
        searchField.addPropertyChangeListener("cancelAction",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        assertSame(evt.getOldValue().getClass(),
                                ClearAction.class);
                        assertSame(evt.getNewValue(), action);
                        eventReceived = true;
                    }
                });

        NativeSearchFieldSupport.setCancelAction(searchField, action);
        assertTrue(eventReceived);
        assertSame(searchField.getCancelAction(), action);
    }

    @Test
    public void testFindActionPropertyChange() throws Exception {
        final ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        };
        searchField.addPropertyChangeListener("findAction",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        assertSame(evt.getOldValue().getClass(),
                                FindAction.class);
                        assertSame(evt.getNewValue(), action);
                        eventReceived = true;
                    }
                });

        NativeSearchFieldSupport.setFindAction(searchField, action);
        assertTrue(eventReceived);
        assertSame(searchField.getFindAction(), action);
    }

    @Test
    public void testDefaultPrompt() throws Exception {
        JXSearchField sf = new JXSearchField();
        assertSame(UIManagerExt.getString("SearchField.prompt"), sf.getPrompt());

        sf = new JXSearchField("prompt");
        assertEquals("prompt", sf.getPrompt());
    }

    @Test
    public void testDefaultSearchMode() throws Exception {
        JXSearchField sf = new JXSearchField();
        assertTrue(sf.isInstantSearchMode());
        assertSame(SearchMode.INSTANT, sf.getSearchMode());
    }

    @Test
    public void testFireAction() throws Exception {
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.err.println(e);
                eventReceived = true;
            }
        });
        eventReceived = false;
        searchField.setInstantSearchDelay(0);
        searchField.setSearchMode(SearchMode.INSTANT);
        searchField.setText("search");
        Assert.assertTrue(eventReceived);

        eventReceived = false;
        searchField.setSearchMode(SearchMode.REGULAR);
        searchField.setText("search2");
        assertFalse(eventReceived);
    }

    @Test
    public void testInstantSearchDelay() throws Exception {
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.err.println(e);
                eventReceived = true;
            }
        });
        eventReceived = false;
        searchField.setSearchMode(SearchMode.INSTANT);
        searchField.setText("search");
        Assert.assertTrue(eventReceived);

        eventReceived = false;
        searchField.setInstantSearchDelay(100);
        assertSame(100, searchField.getInstantSearchDelay());

        searchField.setText("search2");
        assertFalse(eventReceived);
    }

    @Test
    public void testButtonVisibility() throws Exception {
        assertTrue(searchField.getFindButton().isVisible());
        assertFalse(searchField.getCancelButton().isVisible());

        assertTrue(searchField.isMacLayoutStyle());
        assertTrue(searchField.isInstantSearchMode());
        searchField.setText("text");
        assertTrue(searchField.getFindButton().isVisible());
        assertTrue(searchField.getCancelButton().isVisible());

        searchField.setLayoutStyle(LayoutStyle.VISTA);
        assertFalse(searchField.getFindButton().isVisible());
        assertTrue(searchField.getCancelButton().isVisible());

        searchField.setSearchMode(SearchMode.REGULAR);
        assertTrue(searchField.getFindButton().isVisible());
        assertFalse(searchField.getCancelButton().isVisible());

        searchField.setFindPopupMenu(new JPopupMenu());
        searchField.setUseSeperatePopupButton(false);
        assertFalse(searchField.getFindButton().isVisible());
        assertTrue(searchField.getPopupButton().isVisible());

        searchField.setUseSeperatePopupButton(true);
        assertTrue(searchField.getFindButton().isVisible());
        assertTrue(searchField.getPopupButton().isVisible());
    }

    @Test
    public void testSearchMode() throws Exception {
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.err.println(e);
                eventReceived = true;
            }
        });
        assertTrue(searchField.isInstantSearchMode());
        assertSame(UIManager.getIcon("SearchField.icon"), searchField
                .getFindButton().getIcon());
        assertNull(searchField.getFindButton().getRolloverIcon());
        assertNull(searchField.getFindButton().getPressedIcon());
        eventReceived = false;
        searchField.getFindButton().doClick();
        assertFalse(eventReceived);
        searchField.setText("test");
        assertTrue(eventReceived);

        eventReceived = false;
        searchField.setSearchMode(SearchMode.REGULAR);
        assertTrue(searchField.isRegularSearchMode());
        assertSame(UIManager.getIcon("SearchField.rolloverIcon"), searchField
                .getFindButton().getRolloverIcon());
        assertSame(UIManager.getIcon("SearchField.pressedIcon"), searchField
                .getFindButton().getPressedIcon());

        focused = true;
        searchField.getFindButton().doClick();
        assertTrue(eventReceived);
    }

    @Test
    public void testEditableEnabled() throws Exception {
        searchField.setEditable(false);
        searchField.setEnabled(true);
        // assertFalse(searchField.getCancelAction().isEnabled());
        assertFalse(searchField.getCancelButton().isEnabled());
        // assertTrue(searchField.getSearchAction().isEnabled());
        assertTrue(searchField.getFindButton().isEnabled());
        assertTrue(searchField.getPopupButton().isEnabled());

        searchField.setEditable(true);
        searchField.setEnabled(false);
        // assertFalse(searchField.getCancelAction().isEnabled());
        assertFalse(searchField.getCancelButton().isEnabled());
        // assertFalse(searchField.getSearchAction().isEnabled());
        assertFalse(searchField.getFindButton().isEnabled());
        assertFalse(searchField.getPopupButton().isEnabled());

        searchField.setEditable(false);
        searchField.setEnabled(false);
        // assertFalse(searchField.getCancelAction().isEnabled());
        assertFalse(searchField.getCancelButton().isEnabled());
        // assertFalse(searchField.getSearchAction().isEnabled());
        assertFalse(searchField.getFindButton().isEnabled());
        assertFalse(searchField.getPopupButton().isEnabled());

        searchField.setEditable(true);
        searchField.setEnabled(true);
        // assertTrue(searchField.getCancelAction().isEnabled());
        assertTrue(searchField.getCancelButton().isEnabled());
        // assertTrue(searchField.getSearchAction().isEnabled());
        assertTrue(searchField.getFindButton().isEnabled());
        assertTrue(searchField.getPopupButton().isEnabled());
    }

    @Test
    public void testSetLayoutStyle() throws Exception {
        assertSame(LayoutStyle.MAC, searchField.getLayoutStyle());
        assertTrue(searchField.isMacLayoutStyle());
        assertFalse(searchField.getCancelButton().isVisible());
        assertTrue(searchField.getFindButton().isVisible());

        searchField.setText("test");
        assertTrue(searchField.getCancelButton().isVisible());
        assertTrue(searchField.getFindButton().isVisible());

        searchField.setLayoutStyle(LayoutStyle.VISTA);
        assertTrue(searchField.isVistaLayoutStyle());
        assertTrue(searchField.getCancelButton().isVisible());
        assertFalse(searchField.getFindButton().isVisible());
    }

    @Test
    public void testSearchButtonIcons() throws Exception {
        UIManager.put("SearchField.icon", new TestIconUI("SearchField.icon"));
        UIManager.put("SearchField.rolloverIcon", new TestIconUI(
                "SearchField.rolloverIcon"));
        UIManager.put("SearchField.pressedIcon", new TestIconUI(
                "SearchField.pressedIcon"));

        UIManager.put("SearchField.clearIcon", new TestIconUI(
                "SearchField.clearIcon"));
        UIManager.put("SearchField.clearRolloverIcon", new TestIconUI(
                "SearchField.clearRolloverIcon"));
        UIManager.put("SearchField.clearPressedIcon", new TestIconUI(
                "SearchField.clearPressedIcon"));

        searchField.updateUI();

        assertSame(UIManager.getIcon("SearchField.icon"), searchField
                .getFindButton().getIcon());
        assertFalse(searchField.isRegularSearchMode());
        assertNull(searchField.getFindButton().getRolloverIcon());
        assertNull(searchField.getFindButton().getPressedIcon());

        searchField.setSearchMode(SearchMode.REGULAR);
        assertSame(UIManager.getIcon("SearchField.icon"), searchField
                .getFindButton().getIcon());
        assertSame(UIManager.getIcon("SearchField.rolloverIcon"), searchField
                .getFindButton().getRolloverIcon());
        assertSame(UIManager.getIcon("SearchField.pressedIcon"), searchField
                .getFindButton().getPressedIcon());

        TestIcon diff = new TestIcon("different");
        searchField.getFindButton().setRolloverIcon(diff);
        searchField.updateUI();
        assertSame(diff, searchField.getFindButton().getRolloverIcon());
        searchField.getFindButton().setPressedIcon(diff);
        searchField.updateUI();
        assertSame(diff, searchField.getFindButton().getPressedIcon());
        searchField.getFindButton().setIcon(diff);
        searchField.updateUI();
        assertSame(diff, searchField.getFindButton().getIcon());
    }

    @Test
    public void testPopupIcons() throws Exception {
        UIManager.put("SearchField.popupIcon", new TestIconUI(
                "SearchField.popupIcon"));
        UIManager.put("SearchField.popupRolloverIcon", new TestIconUI(
                "SearchField.popupRolloverIcon"));
        UIManager.put("SearchField.popupPressedIcon", new TestIconUI(
                "SearchField.popupPressedIcon"));
        searchField.updateUI();

        assertNotSame(UIManager.getIcon("SearchField.popupIcon"), searchField
                .getFindButton().getIcon());
        assertNotSame(UIManager.getIcon("SearchField.popupRolloverIcon"),
                searchField.getFindButton().getRolloverIcon());
        assertNotSame(UIManager.getIcon("SearchField.popupPressedIcon"),
                searchField.getFindButton().getPressedIcon());

        searchField.setUseSeperatePopupButton(true);

        assertSame(UIManager.getIcon("SearchField.icon"), searchField
                .getFindButton().getIcon());

        searchField.setSearchMode(SearchMode.REGULAR);
        assertSame(UIManager.getIcon("SearchField.rolloverIcon"), searchField
                .getFindButton().getRolloverIcon());
        assertSame(UIManager.getIcon("SearchField.pressedIcon"), searchField
                .getFindButton().getPressedIcon());

        assertSame(UIManager.getIcon("SearchField.popupIcon"), searchField
                .getPopupButton().getIcon());
        assertSame(UIManager.getIcon("SearchField.popupRolloverIcon"),
                searchField.getPopupButton().getRolloverIcon());
        assertSame(UIManager.getIcon("SearchField.popupPressedIcon"),
                searchField.getPopupButton().getPressedIcon());
    }

    @Test
    public void testPopupButton() throws Exception {
        searchField.setFindPopupMenu(new JPopupMenu());
        assertFalse(searchField.getFindButton().isVisible());
        assertTrue(searchField.getPopupButton().isVisible());

        assertFalse(searchField.isUseSeperatePopupButton());
        searchField.addPropertyChangeListener("useSeperatePopupButton",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        eventReceived = true;
                    }
                });
        searchField.setUseSeperatePopupButton(true);
        assertTrue(eventReceived);
        assertTrue(searchField.getFindButton().isVisible());
        assertTrue(searchField.getPopupButton().isVisible());

        searchField.setFindPopupMenu(null);
        assertFalse(searchField.getPopupButton().isVisible());
        assertTrue(searchField.getFindButton().isVisible());
    }

    @Test
    public void testSearchOnClick() throws Exception {
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eventReceived = true;
            }
        });
        assertFalse(searchField.isRegularSearchMode());
        searchField.getFindAction().actionPerformed(null);
        assertFalse(eventReceived);

        searchField.setSearchMode(SearchMode.REGULAR);
        focused = false;
        searchField.getFindAction().actionPerformed(null);
        assertFalse(eventReceived);
        focused = true;
        searchField.getFindAction().actionPerformed(null);
        assertTrue(eventReceived);

        eventReceived = false;
        searchField.setUseSeperatePopupButton(true);
        searchField.getFindAction().actionPerformed(null);
        assertTrue(eventReceived);
    }

    @Test
    public void testSeperatePopupDefault() throws Exception {
        UIManager.put("SearchField.useSeperatePopupButton", Boolean.FALSE);
        searchField.updateUI();
        assertFalse(searchField.isUseSeperatePopupButton());

        UIManager.put("SearchField.useSeperatePopupButton", Boolean.TRUE);
        searchField.updateUI();
        assertTrue(searchField.isUseSeperatePopupButton());

        UIManager.put("SearchField.useSeperatePopupButton", Boolean.TRUE);
        searchField.setUseSeperatePopupButton(false);
        searchField.updateUI();
        assertFalse(searchField.isUseSeperatePopupButton());
    }

    @Test
    public void testLayoutDefault() throws Exception {
        UIManager.put("SearchField.layoutStyle", LayoutStyle.VISTA);
        searchField.updateUI();
        assertFalse(searchField.isMacLayoutStyle());

        UIManager.put("SearchField.layoutStyle", LayoutStyle.MAC);
        searchField.updateUI();
        assertFalse(searchField.isVistaLayoutStyle());

        UIManager.put("SearchField.layoutStyle", LayoutStyle.MAC);
        searchField.setLayoutStyle(LayoutStyle.VISTA);
        searchField.updateUI();
        assertTrue(searchField.isVistaLayoutStyle());
    }

    @Test
    public void testPromptFontStyleDefault() throws Exception {
        UIManager.put("SearchField.promptFontStyle", Font.ITALIC);
        searchField.updateUI();
        assertSame(Font.ITALIC, searchField.getPromptFontStyle());

        UIManager.put("SearchField.promptFontStyle", null);
        searchField.updateUI();
        assertNull(searchField.getPromptFontStyle());

        UIManager.put("SearchField.promptFontStyle", Font.BOLD);
        searchField.setPromptFontStyle(null);
        searchField.updateUI();
        assertNull(searchField.getPromptFontStyle());
    }

    @Test
    public void testButtonMarginDefault() throws Exception {
        UIManager.put("SearchField.buttonMargin", new InsetsUIResource(5, 5, 5,
                5));
        searchField.updateUI();
        assertSame(UIManager.get("SearchField.buttonMargin"), searchField
                .getOuterMargin());

        UIManager.put("SearchField.buttonMargin", new InsetsUIResource(5, 5, 5,
                5));
        searchField.setOuterMargin(new Insets(0, 0, 0, 0));
        searchField.updateUI();
        assertNotSame(UIManager.get("SearchField.buttonMargin"), searchField
                .getOuterMargin());
    }

    @Test
    public void testBorder() throws Exception {
        Border newBorder = BorderFactory.createEmptyBorder();
        searchField.setBorder(newBorder);
        assertNotSame("Border should have been wrapped.", newBorder,
                searchField.getBorder());
    }

    @Test
    public void testUseNativeSearchFieldIfPossible() throws Exception {
        SearchFieldUI ui = (SearchFieldUI) searchField.getUI();

        searchField.setUseNativeSearchFieldIfPossible(true);
        assertTrue(searchField.isUseNativeSearchFieldIfPossible());
        assertTrue(NativeSearchFieldSupport.isSearchField(searchField));
        assertNotSame(ui, searchField.getUI());

        searchField.setUseNativeSearchFieldIfPossible(false);
        assertFalse(searchField.isUseNativeSearchFieldIfPossible());
        assertFalse(NativeSearchFieldSupport.isSearchField(searchField));
    }

    @Test
    public void testManageRecentSearches() throws Exception {
        assertNull(searchField.getRecentSearches());
        assertFalse(searchField.isManagingRecentSearches());

        searchField.addPropertyChangeListener("recentSearchesSaveKey",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        eventReceived = true;
                    }
                });

        searchField.setRecentSearchesSaveKey("test");
        assertTrue(eventReceived);
        assertTrue(searchField.isManagingRecentSearches());
        assertSame("test", searchField.getRecentSearchesSaveKey());
        assertSame(RecentSearchesPopup.class, searchField.getFindPopupMenu()
                .getClass());

        eventReceived = false;
        searchField.setRecentSearchesSaveKey(null);
        assertTrue(eventReceived);
        assertFalse(searchField.isManagingRecentSearches());
        assertNull(searchField.getRecentSearchesSaveKey());
        assertNull(searchField.getFindPopupMenu());
        assertNull(searchField.getRecentSearches());
    }

    @Test
    public void testChangeRecentSearchesName() throws Exception {
        searchField.setRecentSearchesSaveKey("test");
        RecentSearches rs = searchField.getRecentSearches();
        rs.removeAll();
        rs.put("test1");

        searchField.setRecentSearchesSaveKey("test2");
        searchField.setRecentSearchesSaveKey("test");

        rs = searchField.getRecentSearches();
        assertSame(1, rs.getLength());
        assertEquals("test1", rs.getRecentSearches()[0]);
    }

    @Test
    public void testManageRecentSearchesAndSetPopup() throws Exception {
        searchField.setRecentSearchesSaveKey("test");
        JPopupMenu pm = new JPopupMenu();
        searchField.setFindPopupMenu(pm);

        assertNotSame(pm, searchField.getFindPopupMenu());
    }

    class TestIcon implements Icon {
        private String name;

        public TestIcon(String name) {
            this.name = name;
        }

        public int getIconHeight() {
            return 0;
        }

        public int getIconWidth() {
            return 0;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestIcon)) {
                return false;
            }
            return name.equals(((TestIcon) obj).name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    class TestIconUI extends TestIcon implements UIResource {
        public TestIconUI(String name) {
            super(name);
        }
    }
}
