// 
// Decompiled by Procyon v0.5.36
// 

package componenti;

import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import javax.swing.Icon;
import java.awt.Component;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import modello.Stile;
import javax.swing.JPanel;

public class CellaStile extends JPanel
{
    private static final long serialVersionUID = 2190862834731282127L;
    private Stile style;
    
    public CellaStile(final Stile style) {
        String nome = style.getNome();
        this.style = style;
        this.setLayout(new BorderLayout(0, 0));
        if (nome == "RL") {
            nome = "Logo Regione";
        }
        if (nome == "RLFSEUE") {
            nome = "Loghi UE, Regione e FSE";
        }
        final JLabel styleName = new JLabel(nome);
        styleName.setBackground(new Color(255, 255, 153));
        this.add(styleName, "North");
        final JLabel styleImage = new JLabel();
        styleImage.setBackground(new Color(255, 255, 153));
        styleImage.setIcon(style.getStyleIcon());
        this.add(styleImage, "Center");
    }
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(300, 300);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(50, 50);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 300);
    }
    
    public Stile getStyle() {
        return this.style;
    }
    
    public void setStyle(final Stile style) {
        this.style = style;
    }
    
    public void selectCell(final boolean select) {
        if (select) {
            this.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }
        else {
            this.setBorder(null);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CellaStile && ((CellaStile)obj).getStyle().getNome().equals(this.getStyle().getNome());
    }
}
