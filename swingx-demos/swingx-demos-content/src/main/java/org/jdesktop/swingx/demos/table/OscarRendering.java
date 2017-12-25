/*
 * Created on 08.04.2008
 *
 */
package org.jdesktop.swingx.demos.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.IconHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingxset.util.DemoUtils;

public class OscarRendering {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(OscarRendering.class
            .getName());
    
    //<snip> JXTable column properties
    // Note: the custom column factory is a feature enhanced factory
    // which allows column configuration based on column identifier
    public static void configureColumnFactory(CustomColumnFactory factory, 
            Class<?> resourceBase) {
        // set location to load resources from
        factory.setBaseClass(resourceBase);
        // mark the isWinner column as hidden
        factory.addHiddenNames("winnerColumn");

        // register a custom comparator
        Comparator<OscarCandidate> comparator = new Comparator<OscarCandidate>() {

            public int compare(OscarCandidate o1, OscarCandidate o2) {
                String movie1 = o1.getMovieTitle();
                String movie2 = o2.getMovieTitle();
                if (movie1 == null) return -1;
                if (movie2 == null) return 1;
                return movie1.compareTo(movie2);
            }
            
        };
        factory.addComparator("movieTitleColumn", comparator);

        // add hints for column sizing
        OscarCandidate prototype = new OscarCandidate("Special Effects and");
        prototype.getPersons().add("some unusually name or");
        prototype.setYear(20000);
        prototype.setMovieTitle("And here we go again ... should ");
        factory.addPrototypeValue("yearColumn", prototype.getYear());
        factory.addPrototypeValue("categoryColumn", prototype.getCategory());
        factory.addPrototypeValue("movieTitleColumn", prototype);
        factory.addPrototypeValue("nomineesColumn", prototype.getPersons());
        
        // register component providers per column identifier
        factory.addComponentProvider("yearColumn", new LabelProvider(JLabel.CENTER));
        factory.addComponentProvider("nomineesColumn", new LabelProvider(new ListStringValue()));
        factory.addComponentProvider("movieTitleColumn", 
                new HyperlinkProvider(new OscarCandidateLinkAction(), OscarCandidate.class));
        
        // Visual Decorators
        // .... and more 
//        </snip>
        
        // <snip> Highlighter and Renderer
        // ToolTip for movie column
        StringValue toolTip = new StringValue() {
            
            public String getString(Object value) {
                if (value instanceof OscarCandidate) {
                    return getURIText((OscarCandidate) value);
                }
                return "";
            } 
            private String getURIText(OscarCandidate target) {
                URI uri = target.getIMDBMovieURI();
                if (uri == null) {
                    return "http://www.imdb.com/" + "\"" + target.getMovieTitle() + "\"";
                }
                return target.getIMDBMovieURI().toString();
            }
            
        };
        
        ToolTipHighlighter movieToolTip = new ToolTipHighlighter();
        movieToolTip.addStringValue(toolTip, "movieTitleColumn");
        factory.addHighlighter("movieTitleColumn", movieToolTip);
        
        // ToolTips for nominees column
        Icon winnerIcon = DemoUtils.getResourceIcon(resourceBase, "winnerIcon");
        Icon nomineeIcon = DemoUtils.getResourceIcon(resourceBase, "nomineeIcon");
        
        // Icon and tool tip decorator for winners
        IconHighlighter winner = new IconHighlighter(winnerIcon);
        ToolTipHighlighter winnerToolTip = new ToolTipHighlighter();
        winnerToolTip.addStringValue(new ListStringValue(true, "Winner!", "Winners: "), "nomineesColumn");
        // Icon and tool tip decorators for nominees
        IconHighlighter nominee = new IconHighlighter(nomineeIcon);
        ToolTipHighlighter nomineeToolTip = new ToolTipHighlighter();
        nomineeToolTip.addStringValue(new ListStringValue(true, "Nominee", "Nominees: "), "nomineesColumn");
        // the predicate to decide which to use
        HighlightPredicate winnerPredicate = new HighlightPredicate() {
            
            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                int modelColumn = adapter.getColumnIndex("winnerColumn");
                return ((Boolean) adapter.getValue(modelColumn)).booleanValue();
            }
            
        };
        // compound per-predicate and add as column highlighter to the factory
        factory.addHighlighter("nomineesColumn", new CompoundHighlighter(
                new CompoundHighlighter(winnerPredicate, winner, winnerToolTip), 
                new CompoundHighlighter(new NotHighlightPredicate(winnerPredicate), 
                        nominee, nomineeToolTip)));
//        </snip>

    }

    //<snip> Highlighter and Renderer
    // a custom link action to drive the hyperlink on the movie column
    /**
     * HyperlinkAction to open the info page related to the OscarCandiate.
     * <p>
     * 
     * The URI is created lazily before browing:
     * <ul>
     * <li>initially, the OscarCandidate only has properties movieTitle and year
     * (of nomination)
     * <li>first time around, this action's performed the uri of the info page
     * is searched online (done in IMBDLink) and set as property to the
     * OscarCandidate
     * <li>if successful, the uri of the info page is set as target to the
     * wrapped HyperlinkAction and its performed is messaged to browse its
     * target
     * </ul>
     */
    public static class OscarCandidateLinkAction extends
            AbstractHyperlinkAction<OscarCandidate> {

        HyperlinkAction browse = HyperlinkAction.createHyperlinkAction(null,
                java.awt.Desktop.Action.BROWSE);

        @Override
        protected void installTarget() {
            setName(target == null ? null : target.getMovieTitle());
            setVisited(target != null ? target.getIMDBMovieURI() != null
                    : false);
        }

        public void actionPerformed(ActionEvent e) {
            if (target == null)
                return;
            try {
                URI imdbURI = target.getIMDBMovieURI();
                if (imdbURI == null) {
                    imdbURI = lookupURI(imdbURI);
                }
                if (imdbURI != null) {
                    // success: browse uri
                    browse.setTarget(imdbURI);
                    browse.actionPerformed(null);
                } else {
                    showLookupFailure();
                }

            } catch (Exception ex) {
                showConnectionError(ex);
            }
        }
//        </snip>

        /**
         * Looks up the URI of the info page.
         */
        private URI lookupURI(URI imdbURI) throws IOException,
                URISyntaxException {
            // lookup uri if not yet set
            String imdbString = IMDBLink.getMovieURIString(target
                    .getMovieTitle(), target.getYear());
            if (imdbString != null) {
                imdbURI = new URI(imdbString);
                target.setIMDBMovieURI(imdbURI);
            }
            return imdbURI;
        }
        
        private void showLookupFailure() {
            JOptionPane.showMessageDialog(
                    null,
                    // PENDING: localized message
                    // PENDING: source panel/window
                    "Unable to locate IMDB URL for" + "\n"
                    + target.getMovieTitle(), "IMDB Link",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        private void showConnectionError(Exception ex) {
            // PENDING JW: use JXErrorDialog!
            ex.printStackTrace();
        }
        
    }
    
//----------------- not special to OscarRendering, but still missing in SwingX :-)
    

    /**
     * 
     */
    public static class ToolTipHighlighter extends AbstractHighlighter {
        
        private List<StringValue> stringValues;
        private List<Object> sourceColumns;
        private String delimiter; 
        
        
        /**
         * Adds a StringValue to use on the given sourceColumn.
         * 
         * @param sv the StringValue to use.
         * @param sourceColumn the column identifier of the column to use. 
         */
        public void addStringValue(StringValue sv, Object sourceColumn) {
           if (stringValues == null) {
               stringValues = new ArrayList<StringValue>();
               sourceColumns = new ArrayList<Object>();
           }
           stringValues.add(sv);
           sourceColumns.add(sourceColumn);
        }

        /**
         * Sets the delimiter to use between StringValues.
         * 
         * @param delimiter the delimiter to use between StringValues, if there are more than one.
         */
        public void setDelimiter(String delimiter) {
            this.delimiter = delimiter;
        }
        
        @Override
        protected Component doHighlight(Component component,
                ComponentAdapter adapter) {
            String toolTip = getToolTipText(component, adapter);
            // PENDING: treetableCellRenderer doesn't reset tooltip!
            if (toolTip != null) {
                ((JComponent) component).setToolTipText(toolTip);
            }
            return component;
        }
        
        private String getToolTipText(Component component,
                ComponentAdapter adapter) {
            if ((stringValues == null) || stringValues.isEmpty()) return null;
            String text = "";
            for (int i = 0; i < stringValues.size(); i++) {
                int modelIndex = adapter.getColumnIndex(sourceColumns.get(i));
                if (modelIndex >= 0) {
                   text += stringValues.get(i).getString(adapter.getValue(modelIndex));
                   if ((i != stringValues.size() - 1) && !isEmpty(text)){
                       text += delimiter;
                   }
                }
            }
            return isEmpty(text) ? null : text;
        }

        private boolean isEmpty(String text) {
            return text.length() == 0;
        }

        /**
         * Overridden to check for JComponent type.
         */
        @Override
        protected boolean canHighlight(Component component,
                ComponentAdapter adapter) {
            return component instanceof JComponent;
        }
        
    }

    public static class ListStringValue implements StringValue {

        boolean isToolTip;
        String singleToolTipPrefix;
        String multipleToolTipPrefix;
        
        public ListStringValue() {
            this(false, null, null);
        }
        
        public ListStringValue(boolean asToolTip, String singleItem, String multipleItems) {
            this.isToolTip = asToolTip;
            this.singleToolTipPrefix = singleItem;
            this.multipleToolTipPrefix = multipleItems;
        }

        @SuppressWarnings("unchecked")
        public String getString(Object value) {
            if (value instanceof List) {
                List<String> persons = (List<String>) value;
                if (isToolTip) {
                    return getStringAsToolTip(persons);
                }
                return getStringAsContent(persons);
            }
            return StringValues.TO_STRING.getString(value);
        }

        private String getStringAsToolTip(List<String> persons) {
            if (persons.size() > 1) {
                StringBuffer winners = new StringBuffer("");
                if (multipleToolTipPrefix != null) {
                    winners.append(multipleToolTipPrefix);
                }
                for (String person : persons) {
                    winners.append(person);
                    winners.append(", ");
                }
                winners = winners.delete(winners.lastIndexOf(","), winners.length());
                return winners.toString();
            }
            return StringValues.TO_STRING.getString(singleToolTipPrefix);
        }

        private String getStringAsContent(List<String> persons) {
            if (persons.isEmpty()) {
                return "unknown";
            }
            if (persons.size() > 1) {
                return persons.get(0) + " + more ...";
            }
            return persons.get(0);
        }

    }
    

}
