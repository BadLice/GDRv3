// 
// Decompiled by Procyon v0.5.36
// 

package generatore;

import com.google.gson.JsonArray;
import java.util.Comparator;
import modello.Modulo;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.util.Scanner;
import java.io.FileInputStream;
import com.google.gson.JsonObject;
import java.io.File;

public class JsonTemplateParser
{
    public static JsonObject parseJson(final File inputJson) {
        try {
            final Scanner sc = new Scanner(new FileInputStream(inputJson), "UTF-8");
            final StringBuilder template = new StringBuilder();
            template.append("{\n \"template\" : ");
            while (sc.hasNextLine()) {
                template.append(sc.nextLine());
            }
            sc.close();
            template.append("\n}");
            final String templateStr = template.toString();
            final JsonParser parser = new JsonParser();
            final JsonObject jsonObject = parser.parse(templateStr).getAsJsonObject();
            return jsonObject;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ArrayList<Modulo> getAllModulesName(final JsonObject templateParsed) {
        final ArrayList<Modulo> values = new ArrayList<Modulo>();
        System.out.println(" --- --- CREA LISTA MODULI --- --- ");
        final JsonArray allTemplates = templateParsed.get("template").getAsJsonArray();
        for (int temp = 0; temp < allTemplates.size(); ++temp) {
            final String nome = allTemplates.get(temp).getAsJsonObject().get("name").getAsString();
            System.out.println("template: " + nome);
        }
        final JsonObject template = allTemplates.get(0).getAsJsonObject();
        final JsonArray modules = template.get("modules").getAsJsonArray();
        for (int i = 0; i < modules.size(); ++i) {
            final JsonObject module = modules.get(i).getAsJsonObject();
            final String nomeModulo = module.get("labels").getAsJsonObject().get("it").getAsString();
            final String idModulo = module.get("name").getAsString();
            values.add(new Modulo(idModulo, nomeModulo));
        }
        values.sort(new Comparator<Modulo>() {
            @Override
            public int compare(final Modulo m1, final Modulo m2) {
                return m1.getNome().compareTo(m2.getNome());
            }
        });
        return values;
    }
    
    public static String getTemplateName(final JsonObject templateParsed) {
        final JsonArray allTemplates = templateParsed.get("template").getAsJsonArray();
        final JsonObject template = allTemplates.get(0).getAsJsonObject();
        return template.get("name").getAsString();
    }
}
