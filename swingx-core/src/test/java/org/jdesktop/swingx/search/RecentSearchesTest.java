package org.jdesktop.swingx.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.prefs.Preferences;

import org.jdesktop.swingx.JXSearchField;
import org.junit.Before;
import org.junit.Test;

public class RecentSearchesTest {
	private RecentSearches searchHistory;

	@Before
	public void setUp() {
		searchHistory = new RecentSearches("search.test");
		searchHistory.removeAll();
	}

	@Test
	public void testNullName() throws Exception {
	 	RecentSearches rs = new RecentSearches(null);
	 	rs.put("test");
	 	rs.removeAll();
	}

	@Test
	public void testCreateRemoveAdd() throws Exception {
		RecentSearches sh = new RecentSearches(Preferences.userRoot(), "search.test");
		sh.removeAll();

		assertSame(0, sh.getLength());
		sh.put("search1");
		assertSame(1, sh.getLength());
		sh.put("search2");

		sh = new RecentSearches("search.test");
		assertSame(2, sh.getLength());
	}

	@Test
	public void testRecentsOrder() throws Exception {
		searchHistory.setMaxRecents(2);
		searchHistory.put("search1");
		searchHistory.put("search2");
		assertEquals("search2", searchHistory.getRecentSearches()[0]);
		assertEquals("search1", searchHistory.getRecentSearches()[1]);

		searchHistory.put("search3");
		assertSame(2, searchHistory.getLength());
		assertEquals("search3", searchHistory.getRecentSearches()[0]);
		assertEquals("search2", searchHistory.getRecentSearches()[1]);

		searchHistory.put("search2");
		assertEquals("search2", searchHistory.getRecentSearches()[0]);
	}

	@Test
	public void testDoubleAdd() throws Exception {
		searchHistory.put("search1");
		searchHistory.put("search1");
		assertSame(1, searchHistory.getLength());
	}

	@Test
	public void testEmptyAdd() throws Exception {
		searchHistory.put(null);
		searchHistory.put("");
		searchHistory.put(" ");
		assertSame(0, searchHistory.getLength());
	}

	@Test
	public void testMaxRecents() throws Exception {
		assertSame(5, searchHistory.getMaxRecents());
		searchHistory.setMaxRecents(1);
		assertSame(1, searchHistory.getMaxRecents());
		searchHistory.put("search1");
		searchHistory.put("search2");
		assertSame(1, searchHistory.getLength());
	}

	@Test
	public void testPersistency() throws Exception {
		searchHistory.put("test");
		assertSame(1, new RecentSearches("search.test").getLength());
	}

	@Test
	public void testUnInstall() throws Exception {
		JXSearchField searchField = new JXSearchField("Search");
		searchHistory.install(searchField);

		assertSame(1, searchField.getActionListeners().length);
		assertSame(1, searchHistory.getChangeListeners().length);

		searchHistory.uninstall(searchField);

		assertSame(0, searchField.getActionListeners().length);
		assertSame(0, searchHistory.getChangeListeners().length);
		assertNull(searchField.getFindPopupMenu());
	}
}
