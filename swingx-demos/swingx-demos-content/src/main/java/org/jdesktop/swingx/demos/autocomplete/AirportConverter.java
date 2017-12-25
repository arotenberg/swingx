/*
 * $Id: AirportConverter.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.demos.autocomplete;

import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

/**
 * A autocomplete converter for airports.
 * 
 * @author Karl George Schaefer
 * @author  Thomas Bierhance (original AutoCompleteDemoPanel.AirportConverter)
 */
public class AirportConverter extends ObjectToStringConverter {

    public String[] getPossibleStringsForItem(Object item) {
        if (item == null)
            return null;
        if (!(item instanceof Airport))
            return new String[0];
        Airport airport = (Airport) item;
        return new String[] {
                airport.toString(), airport.icaoCode, airport.iataCode
        };
    }

    public String getPreferredStringForItem(Object item) {
        String[] possible = getPossibleStringsForItem(item);
        String preferred = null;
        if (possible != null && possible.length > 0) {
            preferred = possible[0];
        }
        return preferred;
    }
}
