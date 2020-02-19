// 
// Decompiled by Procyon v0.5.36
// 

package modello;

public class Modulo
{
    private String id;
    private String nome;
    
    public Modulo(final String id, final String nome) {
        this.id = id;
        this.nome = nome;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getNome() {
        return this.nome;
    }
    
    public void setNome(final String nome) {
        this.nome = nome;
    }
    
    @Override
    public String toString() {
        return this.nome;
    }
}
