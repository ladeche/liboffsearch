package org.ladeche.liboffsearch;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import org.ladeche.liboffsearch.beans.ExecUnit;
import org.ladeche.liboffsearch.beans.FileExtension;
import org.ladeche.liboffsearch.beans.FileFound;
import org.ladeche.liboffsearch.beans.LangLabels;
import org.ladeche.liboffsearch.utils.LOSProperties;
//import org.ladeche.liboffsearch.utils.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.event.MouseEvent;

public class LOSGui extends JFrame implements ActionListener,
        MouseListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5712388162680875508L;

	// Logs
    static final Logger logger = LoggerFactory.getLogger(LOSGui.class);

    // Properties
    private LOSProperties configProperties;
    private LOSProperties langProperties;
    
    // Langugages
    private HashMap<String, LangLabels> langMap = new HashMap<String, LangLabels>();
    private HashMap<String, JButton> btnLangMap = new HashMap<String, JButton>();
    private SequentialGroup langHSequentialGroup; 
    private ParallelGroup langVParallelGroup; 

    
    // Functional attributes
    private ArrayList<String> fileExtList = new ArrayList<String>();
    private HashMap<String, FileExtension> fileExtMap = new HashMap<String, FileExtension>();
    private HashMap<String, ExecUnit> execUnitMap = new HashMap<String, ExecUnit>();

    // Graphical Components ///////////////
    private JPanel contentPane;
    private GroupLayout glContentPane;

    // File Extension CheckBoxes Group 
    private HashMap<String, JCheckBox> jCheckBoxMap = new HashMap<String, JCheckBox>();
    private SequentialGroup fexHSequentialGroup; 
    private ParallelGroup fexVParallelGroup; 

    // Search Zone Group
    private JTextField txfDirectory, txfKeyWord;
    private JButton btnDirectory, btnSearch;
    private JFileChooser fc;
    private SequentialGroup schHSequentialGroup; 
    private SequentialGroup schVSequentialGroup; 

    // Result Table
    private String[] columnName = { "N°","Fichiers Trouvés" };
    private JTable tblResult;
    private DefaultTableModel tblmdlResult;

    private JScrollPane scrlpnResult;

    // Options
    // private int fileNameOption = Options.NOPATH;
    //private int fileNameOption = Options.RELATIVEPATH;
    // private int fileNameOption = Options.FULLPATH;

    // Files List
    ArrayList<FileFound> filesFound =new ArrayList<FileFound>();
    ArrayList<FileFound> filesFoundCurrentExt =new ArrayList<FileFound>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        logger.debug("*** Starting Session ***");

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LOSGui frame = new LOSGui();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public LOSGui() {

        // initialize (load properties, ...)
        configProperties = new LOSProperties("config.properties");
        langProperties = new LOSProperties("lang.properties");

    	initializeLanguages();
        initializeExecUnits();
        initializeFileExtList();

         // Frame 
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setSize(new
         Dimension (720,480)); contentPane = new JPanel();
         contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
         setContentPane(contentPane);
         
         // Components 
         initializeComponents();
         
         contentPane.setLayout(glContentPane);
         
    }

    
    /**
     * From lang.properties, initializes languages
     * 
     */
    private void initializeLanguages() {

    	logger.debug("Initializing languages ...");
        // Parse properties file to gather execution units

        // get languages managed
        String[] langArray = langProperties.getProperty(
                "overall.lang.list").split(",");
        
        // initialize language Map
        for (String langCode : langArray) {
        	logger.debug(">"+langCode);
            langMap.put(langCode, new LangLabels(langCode));
        }
        
		for (Map.Entry<String, LangLabels> langLabels : langMap.entrySet()) {
			langLabels.getValue().setBtnDirectory(langProperties.getProperty("lang."+langLabels.getKey()+".btnDirectory"));
			langLabels.getValue().setBtnSearch(langProperties.getProperty("lang."+langLabels.getKey()+".btnSearch"));
			langLabels.getValue().setTblHeader(langProperties.getProperty("lang."+langLabels.getKey()+".tblHeader"));
        	logger.debug(">"+langLabels.getValue().toString());
		}
    }
    
    /**
     * From config.properties, initializes "ExecUnits" (executable scripts) 
     * Parses searchtool.* and opentool.* entries. 
     *
     */
    private void initializeExecUnits() {

    	logger.debug("Initializing Execution Units ...");
        // Parse properties file to gather execution units

        ExecUnit execUnit;

        for (String key : configProperties.stringPropertyNames()) {
            if (key.startsWith("exec")) {
                execUnit = new ExecUnit(key, configProperties.getProperty(key));
                execUnitMap.put(key, execUnit);
            	logger.debug(">"+execUnit.toString());
            }
        }
    }

    /**
     * From config.properties, initializes searchable File Extensions (file types) 
     * Parses overall.fileext.list entry and then *.*.opentool and *.*.searchtool entries.
     *
     */
    private void initializeFileExtList() {

    	logger.debug("Initializing File Extensions ...");

        // get file extensions managed
        String[] fileExtArray = configProperties.getProperty(
                "overall.fileext.list").split(",");

        fileExtList = new ArrayList<String>(Arrays.asList(fileExtArray));

        FileExtension fileExtension;

		for (String fileExt : fileExtList) {
			fileExtension = new FileExtension(fileExt);
			fileExtension.setFileSearchTool(execUnitMap.get("exec.searchtool."
					+ configProperties.getProperty("fileext." + fileExt
							+ ".searchtool")));
			fileExtension.setFileOpenTool(execUnitMap.get("exec.opentool."
					+ configProperties.getProperty("fileext." + fileExt
							+ ".opentool")));
			fileExtension.setDescription(configProperties
					.getProperty("fileext." + fileExt + ".description"));
			fileExtension.setActive(Boolean.valueOf(configProperties
					.getProperty("fileext." + fileExt + ".active")));
			fileExtMap.put(fileExt, fileExtension);
        	logger.debug(">"+fileExtension.toString());
		}

    }

    
    /**
     * Depending on initializeFileExtList results, builds a dynamic check box list.
     *
     * @see         initializeFileExtList
     */
    private void initializeCheckBoxes() {

    	logger.debug("Initializing File Extension Checkboxes ...");

        JCheckBox jCheckBox;
        fexHSequentialGroup = glContentPane.createSequentialGroup();
        fexVParallelGroup = glContentPane.createParallelGroup(Alignment.BASELINE);
        for ( String fileExt : fileExtList){
            jCheckBox = new JCheckBox(fileExt);
            jCheckBox.setSelected(fileExtMap.get(fileExt).getActive());
            jCheckBox.setToolTipText(fileExtMap.get(fileExt).getDescription());
            jCheckBox.addItemListener(this);
            jCheckBoxMap.put(fileExt, jCheckBox);
            fexHSequentialGroup.addComponent(jCheckBoxMap.get(fileExt));
            fexHSequentialGroup.addGap(28);
            fexVParallelGroup.addComponent(jCheckBoxMap.get(fileExt));
        	logger.debug(">"+fileExt+" checkbox");
        }
    }

    /**
     * From config.properties, initializes languages with overall.lang.default entry
     * and lang.* entries.
     *
     */
    
    private void initializeFlagButtons() {

    	logger.debug("Initializing Language Buttons ...");
    	// Flags / Language buttons
    	JButton jButton;
        langHSequentialGroup = glContentPane.createSequentialGroup();
        langVParallelGroup = glContentPane.createParallelGroup(Alignment.BASELINE);
        Image img;
		for (Map.Entry<String, LangLabels> langLabels : langMap.entrySet()) {
			// Try to set flag image on button
			try {
				img=ImageIO.read(getClass().getResource(langProperties.getProperty("lang."+langLabels.getKey()+".flag")));
				jButton = new JButton();
				jButton.setIcon(new ImageIcon(img));
				jButton.addActionListener(this);
				btnLangMap.put(langLabels.getKey(), jButton); 
				langHSequentialGroup.addComponent(jButton);
				langVParallelGroup.addComponent(jButton);
	        	logger.debug(">"+langLabels.getKey()+" button");
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        }
		}
    }

    /**
     * Initializes static Search Zone.
     *
     */
    private void initializeSearchZone() {

    	logger.debug("Initializing Search Zone ...");
        // GroupLayouts
        schHSequentialGroup = glContentPane.createSequentialGroup();
        schVSequentialGroup = glContentPane.createSequentialGroup();

        // Buttons
        btnDirectory = new JButton("Racine");
        btnDirectory.addActionListener(this);
        btnSearch = new JButton("Chercher");
        btnSearch.addActionListener(this);
        //resetLabels();

        // Text Fields
        txfDirectory = new JTextField();
        txfDirectory.setBackground(Color.WHITE);
        txfDirectory.setColumns(10);
        txfKeyWord = new JTextField();
        txfKeyWord.setColumns(10);
        txfKeyWord.setBackground(Color.WHITE);
        
        // file Chooser associated with btnDirectory
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        schHSequentialGroup.addGroup(glContentPane.createParallelGroup(Alignment.LEADING)
                                    .addComponent(btnSearch,GroupLayout.PREFERRED_SIZE,120,GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnDirectory,GroupLayout.PREFERRED_SIZE,120,GroupLayout.PREFERRED_SIZE)
                            )
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(glContentPane.createParallelGroup(Alignment.LEADING,false)
                                    .addComponent(txfDirectory,GroupLayout.PREFERRED_SIZE,490,GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txfKeyWord)
                            )
                            .addContainerGap();

        schVSequentialGroup.addGroup(glContentPane.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(txfDirectory,GroupLayout.PREFERRED_SIZE,22,GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnDirectory,GroupLayout.PREFERRED_SIZE,22,GroupLayout.PREFERRED_SIZE)
                                    )
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(glContentPane.createParallelGroup(Alignment.BASELINE)
                            		.addComponent(txfKeyWord,GroupLayout.PREFERRED_SIZE,22,GroupLayout.PREFERRED_SIZE)
                            		.addComponent(btnSearch,GroupLayout.PREFERRED_SIZE,22,GroupLayout.PREFERRED_SIZE)
                    		);
    }

    /**
     * Initializes Result Scroll Pane.
     *
     */
    private void initializeResultScrollPane() {

    	logger.debug("Initializing Result Scrollpane ...");

        // Result scrollpane and associated Jtable
        scrlpnResult = new JScrollPane();
        tblmdlResult = new DefaultTableModel(columnName, 1);
        tblResult = new JTable(tblmdlResult);
        tblResult.getColumnModel().getColumn(0).setMaxWidth(40);
        //tblResult.getColumnModel().getColumn(1).setPreferredWidth(20);
        tblResult.addMouseListener(this);

        scrlpnResult.setViewportView(tblResult);
    }

    /**
     * Initializes Global Layout.
     * 
     * @see initializeResultScrollPane, initializeFlagButtons, initializeCheckBoxes, initializeSearchZone
     *
     */
    private void initializeLayout() {
    	logger.debug("Initializing Layout ...");
       // LAYOUT
        glContentPane.setHorizontalGroup(glContentPane.createParallelGroup(Alignment.LEADING)
        					.addGroup(langHSequentialGroup)
                            .addGroup(fexHSequentialGroup)
                            .addGroup(schHSequentialGroup)
                            .addGroup(glContentPane.createSequentialGroup()
                                        .addComponent(scrlpnResult,GroupLayout.DEFAULT_SIZE,625,Short.MAX_VALUE)
                                        .addGap(15)
                            )
                    );
        glContentPane
                .setVerticalGroup(glContentPane.createSequentialGroup()
                			    .addGroup(langVParallelGroup)
                                .addContainerGap()
                                .addGroup(fexVParallelGroup)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(schVSequentialGroup)
                                .addGap(18)
                                .addComponent(scrlpnResult,GroupLayout.DEFAULT_SIZE, 249,Short.MAX_VALUE)
                                .addGap(28)

                        );
    }

    /**
     * Initialization sequence of graphical components.
     * 
     * @see initializeResultScrollPane, initializeFlagButtons, initializeCheckBoxes, initializeSearchZone, initializeLayout
     *
     */
    private void initializeComponents() {

        glContentPane = new GroupLayout(contentPane);
        initializeCheckBoxes();
        initializeSearchZone();
        initializeFlagButtons();
        initializeResultScrollPane();
        initializeLayout();
		// Set Default Language
		resetLabels(langProperties.getProperty("overall.lang.default"));
    }
    
    /**
     *  
     * Adjust language of application. 
     *
     * @param  language chosen
     */
    private void resetLabels(String language) {
    	logger.debug("Resetting language labels to "+language+" ...");

        btnSearch.setText(langMap.get(language).getBtnSearch());
        btnDirectory.setText(langMap.get(language).getBtnDirectory());
        tblResult.getColumnModel().getColumn(1).setHeaderValue(langMap.get(language).getTblHeader());
        tblResult.getTableHeader().repaint();

    }

    /**
     * Manage action performed on buttons.
     *
     */
    public void actionPerformed(ActionEvent e) {
        // Directory Selection
    	logger.debug("ActionPerformed "+e.toString());

        if (e.getSource() == btnDirectory) {
        	logger.debug("Opening File Dialog ...");

            // Open FileDialog
            int returnVal = fc.showOpenDialog(LOSGui.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // get Selected Directory and put it in txtDirectory
                File file = fc.getSelectedFile();
            	logger.debug("Opening: " + file.getName() + ".");
                txfDirectory.setText(file.getPath());
            } else {
            	logger.debug("Cancelled by user");
            }
        }

        // Launch Search if KeyWord is not null
        else if (e.getSource() == btnSearch
                && txfKeyWord.getText().length() > 0) {
        	logger.debug("Launching Search ...");
        	
        	tblmdlResult.setRowCount(0);
    		Object[] file = new Object[2];
    		Integer i=0;

    		try {
            	filesFound.clear();
        		for (Map.Entry<String, FileExtension> fext : fileExtMap.entrySet()){
        			if (fext.getValue().getActive()) {
        	        	logger.debug("Search "+fext.getKey()+" files");
                    	filesFoundCurrentExt=fext.getValue().search(txfDirectory.getText(), txfKeyWord.getText());
        	        	logger.debug(">"+filesFoundCurrentExt.size()+ " files found");
        				filesFound.addAll(filesFoundCurrentExt);
        				for (FileFound ff : filesFoundCurrentExt) {
        					ff.setId(i);
        					file[0]=ff.getId();
        					file[1]=ff.getFileRelativePath();
        					//logger.debug(">>"+ff.toString());
        					tblmdlResult.addRow(file);
        					i++;
        				}        			
    				}
        		}
	        	logger.debug("Overall "+filesFound.size()+ " files found");
                tblResult.setModel(tblmdlResult);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
         // Manage language buttons
        } else {
    		for (Map.Entry<String, JButton> btn : btnLangMap.entrySet()){
    			if (e.getSource() == btn.getValue()) {
    	        	logger.debug("Changing language ...");
    				resetLabels(btn.getKey());
    			}
    		}            
        }
        
    }

    public void mouseClicked(MouseEvent e) {
        JTable targetTable = (JTable) e.getSource();
        try {
        	logger.debug("Opening "+targetTable.getSelectedRow());
        	filesFound.get(targetTable.getSelectedRow()).open();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void itemStateChanged(ItemEvent e) {
       
    	// Managing check/uncheck filetype Boxes
		for (Map.Entry<String, JCheckBox> cbx : jCheckBoxMap.entrySet()){
			if (e.getSource() == cbx.getValue()) {
	        	logger.debug("Change state of checkbox "+cbx.getKey());
				fileExtMap.get(cbx.getKey()).setActive(cbx.getValue().isSelected());

			}
		}

    }
}
