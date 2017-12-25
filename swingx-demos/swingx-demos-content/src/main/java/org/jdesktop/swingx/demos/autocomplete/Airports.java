/*
 * $Id: Airports.java 4097 2011-11-30 19:22:13Z kschaefe $
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

import java.util.Arrays;
import java.util.List;

/**
 * Class containing the known {@code Airports} in the demo universe.
 * 
 * @author Karl George Schaefer
 */
public final class Airports {
    public static final Airport SCHOENEFELD = new Airport("Berlin - Sch\u00F6nefeld", "EDDB", "SXF");
    
    public static final Airport WICKEDE = new Airport("Dortmund - Wickede", "EDLW", "DTM");
    
    public static final Airport RHEIN_MAIN = new Airport("Frankfurt - Rhein Main", "EDDF", "FRA");
    
    public static final Airport LANGENHAGEN = new Airport("Hannover - Langenhagen", "EDDV", "HAJ");
    
    public static final Airport FUHLSBEETTEL = new Airport("Hamburg - Fuhlsb\u00FCttel", "EDDH", "HAM");
    
    public static final Airport KONRAD_ADENAUER = new Airport("K\u00F6ln/Bonn - Konrad Adenauer", "EDDK", "CGN");
    
    public static final Airport HALLE = new Airport("Leipzig/Halle", "EDDP", "LEJ");
    
    public static final Airport FRANZ_JOSEF_STRAUSS = new Airport("M\u00FCnchen - Franz Josef Strauss", "EDDM", "MUC");
    
    public static final Airport NUERNBERG = new Airport("N\u00F6rnberg", "EDDN", "NUE");
    
    public static final Airport ECHTERDINGEN = new Airport("Stuttgart - Echterdingen", "EDDS", "STR");
    
    public static final List<Airport> ALL_AIRPORTS = Arrays.asList(
        SCHOENEFELD,
        WICKEDE,
        RHEIN_MAIN,
        LANGENHAGEN,
        FUHLSBEETTEL,
        KONRAD_ADENAUER,
        HALLE,
        FRANZ_JOSEF_STRAUSS,
        NUERNBERG,
        ECHTERDINGEN
    );

    private Airports() {
        //does nothing
    }
}
