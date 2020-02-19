package generatore;

import java.util.Iterator;
import java.util.HashMap;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import modello.Stile;
import com.google.gson.JsonObject;

public class XSLGenerator
{
    public String datiRiassuntivi;
    private JsonObject template;
    private Stile style;
    
    private static String getShortNameFromName(final String name) {
        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }
    
    public XSLGenerator(final JsonObject templateParsed, final ArrayList<String> moduleToExport, final String fase, final Stile style, final Stile logo) {
        System.out.println(" --- --- XSL GENERATOR --- --- ");
        this.style = style;
        final JsonArray allTemplates = templateParsed.get("template").getAsJsonArray();
        for (int temp = 0; temp < allTemplates.size(); ++temp) {
            final String nome = allTemplates.get(temp).getAsJsonObject().get("name").getAsString();
            System.out.println("template: " + nome);
        }
        this.template = allTemplates.get(0).getAsJsonObject();
        final String nome2 = this.template.get("name").getAsString();
        final String desc = this.template.get("description").getAsString();
        this.initDatiRiassuntivi(nome2, desc, fase, logo);
        final JsonArray modules = this.template.get("modules").getAsJsonArray();
        for (int i = 0; i < modules.size(); ++i) {
            final JsonObject module = modules.get(i).getAsJsonObject();
            if (moduleToExport.size() == 0 || moduleToExport.contains(module.get("name").getAsString())) {
                final JsonObject labels = module.get("labels").getAsJsonObject();
                String label = "";
                if (labels.has("it")) {
                    label = labels.get("it").getAsString();
                }
                else if (labels.has("en")) {
                    label = labels.get("en").getAsString();
                }
                this.initModule(label);
                final JsonArray sections = module.get("sections").getAsJsonArray();
                for (int k = 0; k < sections.size(); ++k) {
                    final JsonArray fieldsets = sections.get(k).getAsJsonObject().get("children").getAsJsonArray();
                    for (int j = 0; j < fieldsets.size(); ++j) {
                        final JsonObject fieldsetOrModule = fieldsets.get(j).getAsJsonObject();
                        if (fieldsetOrModule.get("type").getAsString().equals("module")) {
                            this.elaborateSubModule(fieldsetOrModule, "", 1);
                        }
                        else if (fieldsetOrModule.get("type").getAsString().equals("fieldset")) {
                            this.elaborateFieldset(fieldsetOrModule, "", 0, false);
                        }
                    }
                }
                this.chiusuraModule();
            }
        }
        this.chiusuraDatiRiassuntivi();
    }
    
    public String getDatiRiassuntivi() {
        return this.datiRiassuntivi;
    }
    
    public String getNomeBando() {
        return this.template.get("name").getAsString();
    }
    
    private void elaborateSubModule(final JsonObject subModule, String path, final int level) {
   	 final JsonObject labels = subModule.get("labels").getAsJsonObject();
	   	 final String submoduleName = getShortNameFromName(subModule.get("name").getAsString()) ;
	   	 path= path.length()!=0 ? "concat("+path+",'"+submoduleName+"')" : "concat('','"+submoduleName+"')";	   	 
	   	 String label = "";
	   	 if (labels.has("it")) {
	   			 label = labels.get("it").getAsString();
	   	 }
	   	 else if (labels.has("en")) {
	   			 label = labels.get("en").getAsString();
	   	 }
	   	 final boolean isHidden = false;
	   	 final JsonArray fieldsets = subModule.get("sections").getAsJsonArray().get(0).getAsJsonObject().get("children").getAsJsonArray();
	   	 boolean inizializzato = false;
	   	 for (int i = 0; i < fieldsets.size() && !inizializzato; ++i) {
	   			 final JsonObject fieldsetOrModule = fieldsets.get(i).getAsJsonObject();
	   			 if (fieldsetOrModule.get("type").getAsString().equals("fieldset")) {
	   					 for (int j = 0; j < fieldsetOrModule.get("items").getAsJsonArray().size() && !inizializzato; ++j) {
	   							 final JsonObject item = fieldsetOrModule.get("items").getAsJsonArray().get(j).getAsJsonObject();
	   							 if(!itemHasDn(item,fieldsetOrModule.get("items").getAsJsonArray())) {
	   								this.initSubModule(label, path, getShortNameFromName(item.get("name").getAsString()), isHidden, level);
	   								inizializzato = true;
	   							 }
	   					 }
	   			 }
	   	 }
	   	 if (!inizializzato) {
	   			 System.err.println("Sei un coglione!");
	   	 }
	   	 else {
	   			 boolean first = true;
	   			String nextPath = path.length()!=0 ? "concat("+path+",'_',$index" + level+",'.')": "concat('')";
	   			
	   			 for (int k = 0; k < fieldsets.size(); ++k) {
	   					 final JsonObject fieldsetOrModule2 = fieldsets.get(k).getAsJsonObject();
	   					 if (fieldsetOrModule2.get("type").getAsString().equals("module")) {
	   							 String lastName = fieldsetOrModule2.get("name").getAsString();
	   							 lastName = lastName.substring(lastName.lastIndexOf(46) + 1, lastName.length());
	   							 this.elaborateSubModule(fieldsetOrModule2, nextPath, level + 1);
	   					 }
	   					 else if (fieldsetOrModule2.get("type").getAsString().equals("fieldset")) {
	   							 this.elaborateFieldset(fieldsetOrModule2, nextPath, level, first);
	   					 }
	   					 first = false;
	   			 }
	   			 this.chiusuraSubModule(isHidden);
	   	 }
    }
    
    private void elaborateFieldset(final JsonObject fieldset, String path, final int level, final boolean first) {
        boolean inizializzato = true;
        if (level > 0) {
            inizializzato = false;
            for (int j = 0; j < fieldset.get("items").getAsJsonArray().size(); ++j) {
                if (inizializzato) {
                    break;
                }
                final JsonObject item = fieldset.get("items").getAsJsonArray().get(j).getAsJsonObject();
                if (!item.get("hidden").getAsBoolean()) {
                    final String firstItemConcat = "concat("+path+",'"+getShortNameFromName(item.get("name").getAsString()+"')");
                    
                    final JsonObject labels = fieldset.get("labels").getAsJsonObject();
                    String label = "";
                    if (labels.has("it")) {
                        label = labels.get("it").getAsString();
                    }
                    else if (labels.has("en")) {
                        label = labels.get("en").getAsString();
                    }
                    this.initFieldset(label, fieldset.has("hidden") && fieldset.get("hidden").getAsBoolean(), level, first, firstItemConcat);
                    inizializzato = true;
                }
            }
        }
        else {
            final JsonObject labels2 = fieldset.get("labels").getAsJsonObject();
            String label2 = "";
            if (labels2.has("it")) {
                label2 = labels2.get("it").getAsString();
            }
            else if (labels2.has("en")) {
                label2 = labels2.get("en").getAsString();
            }
            this.initFieldset(label2, fieldset.has("hidden") && fieldset.get("hidden").getAsBoolean(), level, false, null);
        }
        if (!inizializzato) {
            System.err.println("Sei un coglione!");
        }
        else {
            final JsonArray items = fieldset.get("items").getAsJsonArray();
            for (int k = 0; k < items.size(); ++k) {
                JsonObject item2 = items.get(k).getAsJsonObject();                
                HashMap<String, String> options = getOptionsFromItem(item2);
                
                final JsonObject labels3 = item2.get("labels").getAsJsonObject(); //labels of item (or reference)
                
                //if this item is a reference, replace this item with the referenced item
                if(item2.has("widget") ? item2.get("widget").getAsString().equals("REF") : false) {
                	JsonObject refItem = findReferenceItem(options.get("ref"));
                	if(refItem!=null) {
                		item2 = refItem;
                    	options = getOptionsFromItem(item2);
                	}
                }
                
                if(!itemHasDn(item2,items)) {
	                String labelItem = "";
	                if (labels3.has("it")) {
	                    labelItem = labels3.get("it").getAsString();
	                }
	                else if (labels3.has("en")) {
	                    labelItem = labels3.get("en").getAsString();
	                }
	                labelItem = labelItem.replaceAll("&", "&#38;").replaceAll("--", "").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	                this.initItem(item2.has("name") ? getShortNameFromName(item2.get("name").getAsString()) : "", labelItem, item2.has("hidden") && item2.get("hidden").getAsBoolean(), item2.has("widget") ? item2.get("widget").getAsString() : "INPUT", options, path, level);
                }
            }
            this.chiusuraFieldset(fieldset.has("hidden") && fieldset.get("hidden").getAsBoolean());
        }
    }
    
    //search for the referenced item of a reference in all items of the template (except submodules)
    private JsonObject findReferenceItem(String nameRef) {
    	JsonArray modules = this.template.get("modules").getAsJsonArray();
    	for(int a=0;a<modules.size();a++) {
    		JsonObject module = modules.get(a).getAsJsonObject();
    		JsonArray sections = module.get("sections").getAsJsonArray();
    		if(sections != null) {
    			for(int b=0;b<sections.size();b++) {
    				JsonArray fieldsets = sections.get(b).getAsJsonObject().get("children").getAsJsonArray();
    				if(fieldsets != null) {
    					for(int c=0;c<fieldsets.size();c++) {
    						JsonObject fieldset = fieldsets.get(c).getAsJsonObject();
    						if (fieldset.get("type").getAsString().equals("fieldset")) {
    							JsonArray items = fieldset.get("items").getAsJsonArray();
    							if(items != null) {
    								for(int d=0;d<items.size();d++) {
    									JsonObject item = items.get(d).getAsJsonObject();
    									if(item.has("name") ? getShortNameFromName(item.get("name").getAsString()).equals(nameRef) : false)
    										return item;
    								}
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	return null;
    }
    
    private HashMap<String, String> getOptionsFromItem(JsonObject item2) {
    	HashMap<String, String> options = null;
        if (item2.has("options")) {
            final JsonArray optionsArr = item2.get("options").getAsJsonArray();
            options = new HashMap<String, String>();
            for (int op = 0; op < optionsArr.size(); ++op) {
                options.put(optionsArr.get(op).getAsJsonObject().get("value").getAsString(), optionsArr.get(op).getAsJsonObject().get("label").getAsString());
            }
        }
        return options;
    }
    
    private boolean itemHasDn(final JsonObject item2,final JsonArray items) {
    	for (int k = 0; k < items.size(); ++k)
    		if(items.get(k).getAsJsonObject().get("name").getAsString().toLowerCase().equals(item2.get("name").getAsString().toLowerCase()+"dn"))
    			return true;     
    	return false;
    }
    
    private void initDatiRiassuntivi(final String nomeBando, final String descrizione, final String fase, final Stile logo) {
        this.datiRiassuntivi = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" version=\"2.0\" xmlns:date=\"http://exslt.org/dates-and-times\" xmlns=\"http://www.w3.org/2000/svg\"><xsl:output method=\"xml\" indent=\"yes\" />" + this.style.getHeadStyle() + "" + "<xsl:template match=\"/\">" + "<fo:root xsl:use-attribute-sets=\"root\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">" + "" + "<fo:layout-master-set> " + "<fo:simple-page-master master-name=\"A4\" page-width=\"210mm\" page-height=\"297mm\" margin-top=\"1cm\" margin-bottom=\"1cm\" margin-left=\"2cm\">" + "<fo:region-body region-name=\"xsl-region-body\" margin-bottom=\"10mm\" margin-top=\"10mm\" margin-right=\"2cm\" />" + "<fo:region-before region-name=\"xsl-region-before\" extent=\"10mm\"/>" + "<fo:region-after region-name=\"xsl-region-after\" extent=\"10mm\"/>" + "<fo:region-end region-name=\"right-sidebar\" reference-orientation=\"90\" extent=\"10mm\" />" + "</fo:simple-page-master>" + "</fo:layout-master-set>" + "" + "<fo:page-sequence master-reference=\"A4\" border-style=\"solid\">" + "<fo:static-content flow-name=\"xsl-region-after\">" + "<fo:block text-align=\"center\"><fo:page-number/></fo:block>" + "</fo:static-content>" + "<fo:static-content flow-name=\"right-sidebar\">" + "<fo:block padding-before=\"4pt\" text-align=\"center\" font-size=\"10pt\">" + "Dati Riassuntivi - Conformi all'originale informatico - Generati in Data <xsl:value-of select=\"date:format-date(date:date-time(), 'dd/MM/yyyy HH:mm:ss')\" />" + "</fo:block>" + "</fo:static-content>" + "<fo:flow flow-name=\"xsl-region-body\">" + "<fo:block  text-align=\"center\" padding=\"2mm\" space-after=\"1cm\">" + "<fo:inline>" + "<fo:external-graphic " + " content-width=\"scale-to-fit\"" + " content-height=\"100%\"" + " width=\"100%\"" + " scaling=\"uniform\"" + " src=\"url('" + logo.getHeadStyle() + "')\" /></fo:inline>" + "</fo:block>" + "" + "<!--  DATI GENERALI  -->" + "" + "<fo:table table-layout=\"fixed\" width=\"170mm\" space-after=\"8mm\" xsl:use-attribute-sets=\"table\"  break-after=\"page\">" + this.style.getTableStyle() + "<fo:table-body>" + "" + "<fo:table-row>" + "<fo:table-cell xsl:use-attribute-sets=\"module-header\">" + "<fo:block>" + "Dati Generali" + "</fo:block>" + "</fo:table-cell>" + "</fo:table-row>" + "" + "" + "<fo:table-row>" + "<fo:table-cell xsl:use-attribute-sets=\"main-column\">" + "<fo:block>" + "ID Domanda" + "</fo:block>" + "</fo:table-cell>" + "<fo:table-cell xsl:use-attribute-sets=\"secondary-column\"> " + "<fo:block>" + "<xsl:value-of select=\"/_/idPratica\" />" + "</fo:block>" + "</fo:table-cell>" + "</fo:table-row>" + "" + "" + "<fo:table-row>" + "<fo:table-cell xsl:use-attribute-sets=\"main-column\">" + "<fo:block>" + "Nome Bando" + "</fo:block>" + "</fo:table-cell>" + "<fo:table-cell xsl:use-attribute-sets=\"secondary-column\"> " + "<fo:block>" + "" + nomeBando + "" + "</fo:block>" + "</fo:table-cell>" + "</fo:table-row>" + "" + "" + "<fo:table-row>" + "<fo:table-cell xsl:use-attribute-sets=\"main-column\">" + "<fo:block>" + "Descrizione Bando" + "</fo:block>" + "</fo:table-cell>" + "<fo:table-cell xsl:use-attribute-sets=\"secondary-column\"> " + "<fo:block>" + "" + descrizione + "" + "</fo:block>" + "</fo:table-cell>" + "</fo:table-row>" + "" + "" + "<fo:table-row>" + "<fo:table-cell xsl:use-attribute-sets=\"main-column\">" + "<fo:block>" + "Fase" + "</fo:block>" + "</fo:table-cell>" + "<fo:table-cell xsl:use-attribute-sets=\"secondary-column\"> " + "<fo:block>" + "" + fase + "" + "</fo:block>" + "</fo:table-cell>" + "</fo:table-row>" + "" + "</fo:table-body>" + "</fo:table>" + "" + "";
    }
    
    private void chiusuraDatiRiassuntivi() {
        this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "</fo:flow></fo:page-sequence></fo:root></xsl:template></xsl:stylesheet>";
    }
    
    private void initModule(final String nomeModulo) {
        System.out.println("modulo analizzato: " + nomeModulo);
        this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<fo:table table-layout=\"fixed\" width=\"170mm\" space-after=\"8mm\">" + this.style.getTableStyle() + "<fo:table-body>" + "" + "<fo:table-row>" + "<fo:table-cell xsl:use-attribute-sets=\"module-header\">" + "<fo:block>" + "" + nomeModulo + "" + "</fo:block>" + "</fo:table-cell>" + "</fo:table-row>" + "" + "";
    }
    
    private void chiusuraModule() {
        this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "</fo:table-body></fo:table>";
    }
    
    private void initSubModule(final String labelSottomodulo, final String path, final String itemCiclo, final boolean hidden, final int level) {
	    this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<xsl:if test=\"/_/*[name() = concat(" + path + ",'_0." + itemCiclo + "')]\">";
	    this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<fo:table-row><fo:table-cell xsl:use-attribute-sets=\"module-header\"><fo:block>" + labelSottomodulo + "" + "</fo:block>" + "</fo:table-cell>" + "</fo:table-row>" + "</xsl:if>" + "" + "" + "<!-- ######################################  SOTTOMODULO " + labelSottomodulo + "  ###################################### -->";
	    String startString = "concat("+path+",'_')";
	    this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<xsl:for-each select=\"(/_/*[(starts-with(name(), " + startString + ")) and (contains(name(),'." + itemCiclo + "'))])\" >" + "<xsl:sort data-type=\"number\" order=\"ascending\" select=\"number(substring-after(substring-before(name(), '." + itemCiclo + "'), " + startString + "))\"/>" + "<xsl:variable name=\"index" + level + "\" select=\"position()-1\" data-type=\"number\" />" + "" + "";
	}

    private void chiusuraSubModule(final boolean hidden) {
        this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "</xsl:for-each>";
    }
    
    private String getConcatItemSubModule(final ArrayList<String> subModulesPathName, final int level, final String itemName) {
        String concatString = "";
        if (level == 0) {
            concatString = itemName;
        }
        else {
            String concat = "concat('";
            for (int i = 0; i < level; ++i) {
                concat = String.valueOf(concat) + subModulesPathName.get(i) + "', $index" + (i + 1) + ", '.";
            }
            concatString = "*[name()=" + concat + itemName + "')]";
        }
        return concatString;
    }
    
    private void initFieldset(final String nomeFieldset, final boolean hidden, final int level, final boolean first, final String firstItemFieldset) {
        this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<!-- ######################################  " + nomeFieldset + "  ###################################### -->" + "<fo:table-row>";
        if (level > 0 && first) {
            String concatNumber = "";
            for (int i = 1; i < level; ++i) {
                concatNumber = String.valueOf(concatNumber) + "<xsl:value-of select=\"$index" + i + " + 1\" />.";
            }
            concatNumber = String.valueOf(concatNumber) + "<xsl:value-of select=\"$index" + level + " + 1\" />";
            this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<fo:table-cell xsl:use-attribute-sets=\"sub-module-header" + level + "\">" + "<fo:block>" + "<fo:inline xsl:use-attribute-sets=\"big-number\">" + "" + concatNumber + "" + "</fo:inline>" + "<fo:leader vertical-align=\"middle\"/>" + "" + nomeFieldset + "<fo:leader vertical-align=\"middle\"/> " + "</fo:block>" + "</fo:table-cell>";
        }
        else {
            if (level == 0) {
                this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<fo:table-cell xsl:use-attribute-sets=\"fieldset-header\">";
            }
            else {
                this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<fo:table-cell xsl:use-attribute-sets=\"sub-module-header" + level + "\" text-align-last=\"center\">";
            }
            this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<fo:block>" + nomeFieldset + "" + "</fo:block>" + "</fo:table-cell>";
        }
        this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "</fo:table-row>";
    }
    
    private void chiusuraFieldset(final boolean hidden) {
    }
    
    private void initItem(final String itemName, final String itemLabel, final boolean hidden, final String widget, final HashMap<String, String> options, final String path, final int level) {
		if (!(widget.equals("REF") ? options.get("ref") : itemName).equals("idPratica")) {
			if(widget.equals("LABEL")) {
				this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<fo:table-row><fo:table-cell xsl:use-attribute-sets=\"label-column\"><fo:block>" + itemLabel + "" + "</fo:block>" + "</fo:table-cell>";
				this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "";
				this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "</fo:table-row>";

			}
			//final String itemNameConcat = path.length() != 0 ?  "/_/*[name() = concat("+path+",'"+ (widget.equals("REF") ? options.get("ref") : itemName)+"')]" : "/_/*[name() = concat('','"+(widget.equals("REF") ? options.get("ref") : itemName)+"')]" ;
			final String itemNameConcat = path.length() != 0 ?  "/_/*[name() = concat("+path+",'"+ (widget.equals("REF") ? options.get("ref") : itemName)+"')]" : "/_/"+(widget.equals("REF") ? options.get("ref") : itemName);
			this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<xsl:if test=\""+itemNameConcat+"\"><fo:table-row><fo:table-cell xsl:use-attribute-sets=\"main-column\"><fo:block>" + itemLabel + "" + "</fo:block>" + "</fo:table-cell>" + "<fo:table-cell xsl:use-attribute-sets=\"secondary-column\"> " + "<fo:block>";
				
			switch(widget) {
									
				case "CURRENCY":
					this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<xsl:value-of select=\"" + itemNameConcat + "\" /> €";
				break;
				
				case "INTEGER":
					this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<xsl:value-of select=\"format-number(translate(" + itemNameConcat + ", ',.', '.'), '#')\" />";
				break;
				
				case "FILE":
					this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "Caricato";
				break;
				
				case "LINK":
					this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "Scaricato";
				break;
				
				case "REF":
				default:
					if (options != null)
						this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<xsl:value-of select=\"" +itemNameConcat+ "/@decoded \" />";
					else
						this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "<xsl:value-of select=\"" + itemNameConcat + "\" />";
				break;
			}
			this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "</fo:block></fo:table-cell></fo:table-row></xsl:if>";
			this.datiRiassuntivi = String.valueOf(this.datiRiassuntivi) + "";
		}
    }
}
