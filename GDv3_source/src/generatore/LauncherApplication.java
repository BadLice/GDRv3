// 
// Decompiled by Procyon v0.5.36
// 

package generatore;

import javax.swing.AbstractListModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import javax.swing.SwingWorker;

import com.google.gson.JsonParseException;
import java.io.FileNotFoundException;
import java.awt.AWTEvent;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import componenti.CellaStile;
import java.util.ArrayList;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.GridLayout;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.EventQueue;
import javax.swing.UIManager;
import com.jtattoo.plaf.smart.SmartLookAndFeel;
import modello.Stile;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JLabel;
import javax.swing.JTextField;
import modello.Modulo;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class LauncherApplication
{
    private JFrame frmGeneratoreDatiRiassuntivi;
    private JList<Modulo> moduliList;
    private JTextField txtFase;
    private JTextField txtNomeFile;
    private JLabel templateLabel;
    private boolean fileSelezionato;
    private File selectedFile;
    private JsonObject parsed;
    private Stile selectedStyle;
    private Stile selectedLogo;
    
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //SmartLookAndFeel.setTheme("Gold-Large-Font");
                    UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                final LauncherApplication window = new LauncherApplication();
                window.frmGeneratoreDatiRiassuntivi.setVisible(true);
            }
        });
    }
    
    public LauncherApplication() {
        this.fileSelezionato = false;
        this.initialize();
    }
    
    private void initialize() {
        (this.frmGeneratoreDatiRiassuntivi = new JFrame()).setTitle("Generatore Dati Riassuntivi SiAge v3");
        this.frmGeneratoreDatiRiassuntivi.setResizable(false);
        //this.frmGeneratoreDatiRiassuntivi.getContentPane().setBackground(SystemColor.window);
        //this.frmGeneratoreDatiRiassuntivi.setBackground(SystemColor.windowBorder);
        this.frmGeneratoreDatiRiassuntivi.setBounds(100, 100, 600, 350);
        this.frmGeneratoreDatiRiassuntivi.setDefaultCloseOperation(3);
        this.frmGeneratoreDatiRiassuntivi.getContentPane().setLayout(new CardLayout(0, 0));
        final JPanel globalPanelFirst = new JPanel();
        this.frmGeneratoreDatiRiassuntivi.getContentPane().add(globalPanelFirst, "panel 1");
        globalPanelFirst.setLayout(null);
        final JPanel modulPanel = new JPanel();
        modulPanel.setBounds(0, 32, 594, 192);
        globalPanelFirst.add(modulPanel);
        //modulPanel.setBackground(new Color(255, 255, 153));
        modulPanel.setLayout(null);
        final JLabel lblIdSottomoduli = new JLabel("Seleziona Moduli", 0);
        lblIdSottomoduli.setToolTipText("Utilizza Ctrl per selezionare pi\u00f9 moduli");
        lblIdSottomoduli.setBounds(0, 74, 111, 41);
        modulPanel.add(lblIdSottomoduli);
        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(110, 0, 484, 192);
        modulPanel.add(scrollPane);
        (this.moduliList = new JList<Modulo>()).setToolTipText("Utilizza Ctrl per selezionare pi\u00f9 moduli");
        this.moduliList.setSelectionMode(2);
        this.moduliList.setLayoutOrientation(0);
        this.moduliList.setVisibleRowCount(-1);
        scrollPane.setViewportView(this.moduliList);
        final JPanel namePanel = new JPanel();
        namePanel.setBounds(0, 0, 594, 32);
        globalPanelFirst.add(namePanel);
        namePanel.setBorder(null);
        //namePanel.setBackground(new Color(255, 255, 153));
        (this.templateLabel = new JLabel("Template")).setForeground(Color.WHITE);
        this.templateLabel.setFont(new Font("Rockwell Condensed", 0, 18));
        namePanel.add(this.templateLabel);
        final JPanel bottomPanel = new JPanel();
        bottomPanel.setBounds(0, 222, 594, 99);
        globalPanelFirst.add(bottomPanel);
        //bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setLayout(new GridLayout(0, 1, 0, 0));
        final JPanel panel_5 = new JPanel();
        bottomPanel.add(panel_5);
        //panel_5.setBackground(new Color(255, 255, 153));
        final JLabel lblSs = new JLabel("Nome Fase");
        panel_5.add(lblSs);
        panel_5.add(this.txtFase = new JTextField());
        this.txtFase.setColumns(25);
        final JPanel panel_6 = new JPanel();
        bottomPanel.add(panel_6);
        //panel_6.setBackground(new Color(255, 255, 153));
        panel_6.add(this.txtNomeFile = new JTextField());
        //this.txtNomeFile.setBackground(Color.WHITE);
        this.txtNomeFile.setEditable(false);
        this.txtNomeFile.setText("FileName.json");
        this.txtNomeFile.setColumns(25);
        final JButton btnSelezionaFile = new JButton("Seleziona File");
        panel_6.add(btnSelezionaFile);
        final JPanel panel_7 = new JPanel();
        bottomPanel.add(panel_7);
        //panel_7.setBackground(new Color(255, 255, 153));
        final JButton btnGenera = new JButton("Avanti");
        panel_7.add(btnGenera);
        final JPanel globalPanelSecond = new JPanel();
        this.frmGeneratoreDatiRiassuntivi.getContentPane().add(globalPanelSecond, "panel 2");
        globalPanelSecond.setLayout(new BorderLayout(0, 0));
        final JPanel titleStyle = new JPanel();
        //titleStyle.setBackground(new Color(255, 255, 153));
        globalPanelSecond.add(titleStyle, "North");
        final JLabel lblSelezionaStile = new JLabel("Seleziona Stile");
        lblSelezionaStile.setFont(new Font("Rockwell Condensed", 0, 18));
        titleStyle.add(lblSelezionaStile);
        final JPanel styleGrid = new JPanel();
        //styleGrid.setBackground(new Color(255, 255, 153));
        globalPanelSecond.add(styleGrid, "Center");
        styleGrid.setLayout(new GridLayout(2, 0, 0, 0));
        final JPanel panel = new JPanel();
        //panel.setBackground(new Color(255, 255, 153));
        globalPanelSecond.add(panel, "South");
        final JButton btnGenera2 = new JButton("Genera");
        panel.add(btnGenera2);
        try {
        	final String dir = "settings/";
            final String[] styleList = { "Classico", "Quadratoni" };
            final String[] loghi = { "RL", "RLFSEUE" };
            final ArrayList<CellaStile> allStylesCell = new ArrayList<CellaStile>();
            final ArrayList<CellaStile> allLoghiCell = new ArrayList<CellaStile>();
            String[] array;
            for ( int i = 0; i < styleList.length; i++) {
                final String fileName = styleList[i];
                final Stile st = Stile.readFromFile(new FileInputStream(new File(dir+fileName + ".xml")), fileName);
                st.setStyleIcon(new ImageIcon(ImageIO.read(new FileInputStream(new File(dir+st.getNome()) + ".JPG"))));
                final CellaStile cell = new CellaStile(st);
                //cell.setBackground(new Color(255, 255, 153));
                allStylesCell.add(cell);
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(final MouseEvent me) {
                        cell.selectCell(true);
                        for (final CellaStile other : allStylesCell) {
                            if (!other.equals(cell)) {
                                other.selectCell(false);
                            }
                        }
                        LauncherApplication.access$1(LauncherApplication.this, cell.getStyle());
                    }
                });
                if(styleList[i].equals("Quadratoni")) {
                	cell.selectCell(true);
                	LauncherApplication.access$1(LauncherApplication.this, cell.getStyle());
                }
                styleGrid.add(cell);
            }
            String[] array2;
            for (int length2 = (array2 = loghi).length, j = 0; j < length2; ++j) {
                final String fileName = array2[j];
                final Stile st = Stile.readLogos(dir+fileName);
                st.setStyleIcon(new ImageIcon(ImageIO.read(new FileInputStream(new File(st.getNome()) + ".JPG"))));
                final CellaStile cell = new CellaStile(st);
                //cell.setBackground(new Color(255, 255, 153));
                allLoghiCell.add(cell);
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(final MouseEvent me) {
                        cell.selectCell(true);
                        for (final CellaStile other : allLoghiCell) {
                            if (!other.equals(cell)) {
                                other.selectCell(false);
                            }
                        }
                        LauncherApplication.access$2(LauncherApplication.this, cell.getStyle());
                    }
                });
                if(array2[j].equals("RL")) {
                	cell.selectCell(true);
                	LauncherApplication.access$2(LauncherApplication.this, cell.getStyle());
                }
                styleGrid.add(cell);
            }
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        btnGenera2.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {

        	    final JDialog loading = new JDialog(frmGeneratoreDatiRiassuntivi);
        	    JPanel p1 = new JPanel(new BorderLayout());
        	    JLabel genLabel = new JLabel("Generazione in corso...");
        	    p1.add(genLabel, BorderLayout.CENTER);
        	    p1.setPreferredSize( new Dimension(255, 95) );
        	    loading.setUndecorated(true);
        	    loading.getContentPane().add(p1);
        	    loading.pack();
        	    loading.setLocationRelativeTo(frmGeneratoreDatiRiassuntivi);
        	    loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        	    loading.setModal(true);

        	    SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
        	        @Override
        	        protected String doInBackground() throws InterruptedException  {
        	        	 if (LauncherApplication.this.fileSelezionato && LauncherApplication.this.selectedStyle != null) {
        	                    try {
        	                        final int[] idx = LauncherApplication.this.moduliList.getSelectedIndices();
        	                        final ListModel<Modulo> model = LauncherApplication.this.moduliList.getModel();
        	                        final ArrayList<String> moduleArr = new ArrayList<String>();
        	                        int[] array;
        	                        for (int length = (array = idx).length, j = 0; j < length; ++j) {
        	                            final int i = array[j];
        	                            moduleArr.add(model.getElementAt(i).getId());
        	                        }
        	                        final String fase = LauncherApplication.this.txtFase.getText();
        	                        final XSLGenerator generator = new XSLGenerator(LauncherApplication.this.parsed, moduleArr, fase, LauncherApplication.this.selectedStyle, LauncherApplication.this.selectedLogo);
        	                        final String datiRiass = generator.getDatiRiassuntivi();
        	                        System.out.println("Output Generato");
        	                    	String directory = String.valueOf(LauncherApplication.this.selectedFile.getParent()) + "/PDF_" + fase + "_" + generator.getNomeBando();
        	                    	new File(directory).mkdir();
        	                        BufferedWriter writer  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory+"/pdf_riass_" + fase + "_" + generator.getNomeBando() + ".xsl"), StandardCharsets.UTF_8));
        	                        writer.write(datiRiass);
        	                        writer.flush();
        	                        writer.close();
        	                        System.out.println("Salvataggio File");
        	                        System.out.println("Creazione .bat...");
        	                        final BufferedWriter outCmdFile  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory+"/crea_pdf_riass_" + fase + "_" + generator.getNomeBando() + ".bat"), StandardCharsets.UTF_8));
        	                        outCmdFile.write("fop -xsl \"pdf_riass_" + fase + "_" + generator.getNomeBando() + ".xsl"+"\" -xml \"dati_def.xml\" -pdf output.pdf && output.pdf");
        	                        outCmdFile.flush();
        	                        outCmdFile.close();
        	                        System.out.println(".bat Creato");
        	                    }
        	                    catch (Exception e) {
        	                        e.printStackTrace();
        	                        JOptionPane.showMessageDialog(frmGeneratoreDatiRiassuntivi,e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
        	                        LauncherApplication.this.frmGeneratoreDatiRiassuntivi.dispatchEvent(new WindowEvent(LauncherApplication.this.frmGeneratoreDatiRiassuntivi, 201));
        	                    }
        	  
        	                }
						return ""; 
        	        }
        	        @Override
        	        protected void done() {
        	            loading.dispose();
                        JOptionPane.showMessageDialog(frmGeneratoreDatiRiassuntivi, "File generato corretamente!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        LauncherApplication.this.frmGeneratoreDatiRiassuntivi.dispatchEvent(new WindowEvent(LauncherApplication.this.frmGeneratoreDatiRiassuntivi, 201));

        	        }
        	    };
        	    worker.execute();
        	    loading.setVisible(true);
        	    try {
        	        worker.get();
        	    } catch (Exception e1) {
        	        e1.printStackTrace();
        	    }
        	}
        });
        btnGenera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                if (LauncherApplication.this.fileSelezionato) {
                    final CardLayout cardLayout = (CardLayout)LauncherApplication.this.frmGeneratoreDatiRiassuntivi.getContentPane().getLayout();
                    cardLayout.show(LauncherApplication.this.frmGeneratoreDatiRiassuntivi.getContentPane(), "panel 2");
                    btnGenera2.requestFocus();
                }
            }
        });
        btnSelezionaFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(String.valueOf(System.getProperty("user.home")) + "/Desktop"));
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(final File file) {
                        final boolean isJSON = file.getName().toLowerCase().endsWith(".json");
                        final boolean isFolder = file.isDirectory();
                        return isJSON || isFolder;
                    }
                    
                    @Override
                    public String getDescription() {
                        return "JSON Files";
                    }
                });
                final int result = fileChooser.showOpenDialog(LauncherApplication.this.frmGeneratoreDatiRiassuntivi);
                if (result == 0) {
                    LauncherApplication.access$10(LauncherApplication.this, fileChooser.getSelectedFile());
                    if (LauncherApplication.this.selectedFile.getName().toLowerCase().endsWith(".json")) {
                        LauncherApplication.this.txtNomeFile.setText(LauncherApplication.this.selectedFile.getAbsolutePath());
                        LauncherApplication.access$12(LauncherApplication.this, true);
                        System.out.println("File Selezionato");
                        LauncherApplication.access$13(LauncherApplication.this, JsonTemplateParser.parseJson(LauncherApplication.this.selectedFile));
                        System.out.println("File Parsato");
                        final ArrayList<Modulo> allModules = JsonTemplateParser.getAllModulesName(LauncherApplication.this.parsed);
                        LauncherApplication.this.templateLabel.setText("<html>Template: <font color=\"green\">" + JsonTemplateParser.getTemplateName(LauncherApplication.this.parsed) + "</font></html>");
                        LauncherApplication.this.moduliList.setModel(new AbstractListModel<Modulo>() {
                            private static final long serialVersionUID = 7743184032598078229L;
                            
                            @Override
                            public int getSize() {
                                return allModules.size();
                            }
                            
                            @Override
                            public Modulo getElementAt(final int index) {
                                return allModules.get(index);
                            }
                        });
                        btnGenera.requestFocus();
                    }
                    else {
                        LauncherApplication.this.txtNomeFile.setText("File non JSON");
                        LauncherApplication.access$12(LauncherApplication.this, false);
                        System.err.println("File Non Json");
                    }
                }
            }
        });
    }
    
    static /* synthetic */ void access$1(final LauncherApplication launcherApplication, final Stile selectedStyle) {
        launcherApplication.selectedStyle = selectedStyle;
    }
    
    static /* synthetic */ void access$2(final LauncherApplication launcherApplication, final Stile selectedLogo) {
        launcherApplication.selectedLogo = selectedLogo;
    }
    
    static /* synthetic */ void access$10(final LauncherApplication launcherApplication, final File selectedFile) {
        launcherApplication.selectedFile = selectedFile;
    }
    
    static /* synthetic */ void access$12(final LauncherApplication launcherApplication, final boolean fileSelezionato) {
        launcherApplication.fileSelezionato = fileSelezionato;
    }
    
    static /* synthetic */ void access$13(final LauncherApplication launcherApplication, final JsonObject parsed) {
        launcherApplication.parsed = parsed;
    }
}
