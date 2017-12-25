/*
 * Created on 03.12.2008
 *
 */
package org.jdesktop.swingx.util.font;

import java.awt.Font;

import javax.swing.plaf.FontUIResource;


public class WrapperFontSet implements FontSet {
    private int extra;

    private FontSet delegate;

    public WrapperFontSet(FontSet delegate, int extra) {
        super();
        this.delegate = delegate;
        this.extra = extra;
    }

    private Font getWrappedFont(Font systemFont) {
        return new FontUIResource(systemFont.getFontName(), systemFont
                .getStyle(), systemFont.getSize() + extra);
    }

    public Font getControlFont() {
        return getWrappedFont(delegate.getControlFont());
    }

    public Font getMenuFont() {
        return getWrappedFont(delegate.getMenuFont());
    }

    public Font getMessageFont() {
        return getWrappedFont(delegate.getMessageFont());
    }

    public Font getSmallFont() {
        return getWrappedFont(delegate.getSmallFont());
    }

    public Font getTitleFont() {
        return getWrappedFont(delegate.getTitleFont());
    }

    public Font getWindowTitleFont() {
        // FontUIResource f = this.getWrappedFont(this.delegate
        // .getWindowTitleFont());
        // return new FontUIResource(f.deriveFont(Font.BOLD, f.getSize() +
        // 1));
        return getWrappedFont(delegate.getWindowTitleFont());
    }
}
