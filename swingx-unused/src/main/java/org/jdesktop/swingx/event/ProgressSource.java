/*
 * $Id: ProgressSource.java 2977 2008-07-07 03:17:53Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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

package org.jdesktop.swingx.event;

/**
 * Interface for ProgressListener registrations methods and indicates that the
 * implementation class is a source of ProgressEvents. 
 * ProgressListeners which are interested in ProgressEvents from this class can
 * register themselves as listeners. 
 * 
 * @see ProgressEvent
 * @see ProgressListener
 * @author Mark Davidson
 */
public interface ProgressSource  {

    /**
     * Register the ProgressListener. 
     * 
     * @param l the listener to register
     */
    void addProgressListener(ProgressListener l);

    /**
     * Unregister the ProgressListener from the ProgressSource.
     * 
     * @param l the listener to unregister
     */
    void removeProgressListener(ProgressListener l);

    /**
     * Returns an array of listeners.
     *
     * @return an non null array of ProgressListeners.
     */
    ProgressListener[] getProgressListeners();
}
