/*
 * Created on 06.12.2008
 *
 */
package org.jdesktop.swingx.demos.search;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class Contributor implements Comparable<Contributor> {
    private String firstName;
    private String lastName;
    @SuppressWarnings("unused")
    private String userID;
    private URI devnetMail;
    private int merits;
    private Date since;
    
    public Contributor(String rawData) {
        setData(rawData);
        merits = createRandomMerits();
        since = createRandomJoinedDate();
    }

    public int getMerits() {
        return merits;
    }
    
    public Date getJoinedDate() {
        return since;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public Object getID() {
        return userID;
    }
    
    public URI getEmail() {
        return devnetMail;
    }
    
    /**
     * @param rawData
     */
    private void setData(String rawData) {
        if (rawData == null) {
            lastName = " <unknown> ";
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(rawData);
        try {
           firstName = tokenizer.nextToken();
           lastName = tokenizer.nextToken();
           userID = tokenizer.nextToken();
           devnetMail = new URI("mailto:" + userID + "@dev.java.net");
        } catch (Exception ex) {
            // don't care ...
        }
        
    }

    /**
     * @return
     */
    private Date createRandomJoinedDate() {
        Calendar sinceBase = Calendar.getInstance();
        sinceBase.add(Calendar.YEAR, -5);
        long max = new Date().getTime() - sinceBase.getTimeInMillis();
        Date entry = new Date(sinceBase.getTimeInMillis() + new Double(Math.random() * max).longValue());
        return entry;
    }

    /**
     * @return
     */
    private int createRandomMerits() {
        return new Double(Math.random() * 100).intValue();
    }


    @Override
    public int compareTo(Contributor o) {
        if (!(o instanceof Contributor)) return -1;
        return lastName.compareTo(((Contributor) o).lastName);
    }
}