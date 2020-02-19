// 
// Decompiled by Procyon v0.5.36
// 

package modello;

import java.util.Scanner;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.ImageIcon;

public class Stile
{
    private String nome;
    private String headStyle;
    private String tableStyle;
    private String subModuleStyle;
    private ImageIcon styleIcon;
    
    public Stile(final String nome, final String headStyle, final String tableStyle, final String subModuleStyle) {
        this.nome = nome;
        this.headStyle = headStyle;
        this.tableStyle = tableStyle;
        this.subModuleStyle = subModuleStyle;
    }
    
    public Stile(final String nome, final String headStyle, final String tableStyle, final String subModuleStyle, final ImageIcon styleIcon) {
        this.nome = nome;
        this.headStyle = headStyle;
        this.tableStyle = tableStyle;
        this.subModuleStyle = subModuleStyle;
        this.styleIcon = styleIcon;
    }
    
    public String getNome() {
        return this.nome;
    }
    
    public void setNome(final String nome) {
        this.nome = nome;
    }
    
    public ImageIcon getStyleIcon() {
        return this.styleIcon;
    }
    
    public void setStyleIcon(final ImageIcon styleIcon) {
        this.styleIcon = styleIcon;
    }
    
    public String getHeadStyle() {
        return this.headStyle;
    }
    
    public void setHeadStyle(final String headStyle) {
        this.headStyle = headStyle;
    }
    
    public String getTableStyle() {
        return this.tableStyle;
    }
    
    public void setTableStyle(final String tableStyle) {
        this.tableStyle = tableStyle;
    }
    
    public String getSubModuleStyle() {
        return this.subModuleStyle;
    }
    
    public void setSubModuleStyle(final String subModuleStyle) {
        this.subModuleStyle = subModuleStyle;
    }
    
    public static Stile readFromFile(final InputStream file, final String styleName) {
        final Scanner sc = new Scanner(file);
        final String name = styleName;
        boolean readingHead = false;
        boolean readingTable = false;
        boolean readingSub = false;
        String headAttribute = "";
        String table = "";
        String subModule = "";
        String temp = "";
        while (sc.hasNextLine()) {
            if (readingHead) {
                temp = sc.nextLine();
                if (temp.trim().equalsIgnoreCase("<!-- FINE ATTRIBUTI -->")) {
                    readingHead = false;
                }
                else {
                    headAttribute = String.valueOf(headAttribute) + temp + "";
                }
            }
            else if (readingTable) {
                temp = sc.nextLine();
                if (temp.trim().equalsIgnoreCase("<!-- FINE TABELLA -->")) {
                    readingTable = false;
                }
                else {
                    table = String.valueOf(table) + temp + "";
                }
            }
            else if (readingSub) {
                temp = sc.nextLine();
                if (temp.trim().equalsIgnoreCase("<!-- FINE SOTTOMODULI -->")) {
                    readingSub = false;
                }
                else {
                    subModule = String.valueOf(subModule) + temp + "";
                }
            }
            else {
                temp = sc.nextLine().trim();
                if (temp.equalsIgnoreCase("<!-- ATTRIBUTI -->")) {
                    readingHead = true;
                }
                else if (temp.equalsIgnoreCase("<!-- TABELLA -->")) {
                    readingTable = true;
                }
                else {
                    if (!temp.equalsIgnoreCase("<!-- SOTTOMODULI -->")) {
                        continue;
                    }
                    readingSub = true;
                }
            }
        }
        sc.close();
        return new Stile(name, headAttribute, table, subModule);
    }
    
    public static Stile readLogos(final String styleName) {
        final String name = styleName;
        String headAttribute = "";
        String table = "";
        String subModule = "";
        final String temp = "";
        headAttribute = readFileAsString(styleName+".b64");
        return new Stile(name, headAttribute, table, subModule);
    }
    
    private static String readFileAsString(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
