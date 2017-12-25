package org.jdesktop.swingx.demos.blendcomposite;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.painter.AbstractPainter;

public class CompositePainter extends AbstractPainter<Object> {
    private Composite composite;
    private BufferedImage src;
    private BufferedImage dst;
    
    @Override
    protected void doPaint(Graphics2D g, Object object, int width, int height) {
        Composite original = g.getComposite();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(src, 0, 0, null);
        g.setComposite(composite);
        g.drawImage(dst, 0, 0, null);
        g.setComposite(original);
    }

}
