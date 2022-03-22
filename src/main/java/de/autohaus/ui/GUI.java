package de.autohaus.ui;

import de.autohaus.logic.*;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.autohaus.model.Connect.connect;

public class GUI {
    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;
    private JTable tableCars;
    private JTable tableAusstattung;
    private JTable tableMotor;
    private JComboBox comboBoxTyp;
    private JComboBox comboBoxHerseller;
    private JTextArea kommentarTextArea;
    private JButton ATeinfügenButton;
    private JButton ATaktialisierenButton;
    private JButton ATlöschenButton;
    private JSpinner spinnerJahr;
    private JTextField ATtextFieldATID;
    private JTextField AStextFieldASID;
    private JSpinner spinnerZoll;
    private JComboBox comboBoxFelgenMaterial;
    private JCheckBox checkBoxSitzheizung;
    private JCheckBox checkBoxLenkradheizung;
    private JCheckBox checkBoxSchiebedach;
    private JTextField textFieldFarbe;
    private JComboBox comboBoxFarbMaterial;
    private JComboBox comboBoxInnenraumMaterial;
    private JComboBox comboBoxSitzMaterial;
    private JButton ASeinfügenButton;
    private JButton ASaktualisierenButton;
    private JButton ASlöschenButton;
    private JTextField MTtextFieldMTID;
    private JTextField textFieldVerbrauch;
    private JComboBox comboBoxGetriebe;
    private JComboBox comboBoxKraftstoff;
    private JSpinner spinnerHubraum;
    private JSpinner spinnerPS;
    private JButton MAINlöschenButton2;
    private JButton MAINaktualisierenButton;
    private JTable tableMain;
    private JButton MTeinfügenButton;
    private JButton MTaktualisierenButton;
    private JButton MTlöschenButton;
    private JLabel ASErrorCode;
    private JLabel ASError;
    private JTextField textFieldPreis;
    private JTextField MAINtextFieldATID;
    private JTextField MAINtextFieldASID;
    private JTextField MAINtextFieldMTID;
    private JTextField ATtextFieldASID;
    private JTextField ATtextFieldMTID;
    private JButton ATbrowse;
    private JTextField ATbrowseLink;

    /* ----------------------------------- createTable --------------------------*/

    private void createTableCars() {
        tableCars.setModel(new DtmTableCars().getDtm());
    }

    private void createTableAusstattung(){
        tableAusstattung.setModel(new DtmTableAusstattung().getDtm());
    }

    private void createTableMotor() {
            tableMotor.setModel(new DtmTableMotor().getDtm());
    }

    private void createTableMain() {
            tableMain.setModel(new DtmTableMain().getDtm());
    }

    /* ----------------------------------- reloadTable --------------------------*/

    private void reloadTables() {
        createTableMotor();
        createTableCars();
        createTableAusstattung();
        createTableMain();
    }

    /* ----------------------------------- eventListener --------------------------*/

    private void eventListenerAuto() {
        tableCars.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                setAuto(tableCars.getSelectedRow(), tableCars.getModel());
            }
        });

        tableCars.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                setAuto(tableCars.getSelectedRow(), tableCars.getModel());
            }
        });

        ATeinfügenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement pstm = new InsertAuto().getPstm();

                    pstm.setString(1, comboBoxTyp.getSelectedItem().toString());
                    pstm.setString(2, spinnerJahr.getValue().toString());
                    pstm.setString(3, comboBoxHerseller.getSelectedItem().toString());

                    if(kommentarTextArea.getText().isBlank()){
                        pstm.setString(4, null);
                    } else {
                        pstm.setString(4, kommentarTextArea.getText());
                    }

                    pstm.setString(5, ATtextFieldASID.getText());
                    pstm.setString(6, ATtextFieldMTID.getText());
                    pstm.setString(7, textFieldPreis.getText().replace(",","."));

                    if (ATbrowseLink.getText().isBlank()) {
                        pstm.setString(8, null);
                    } else {
                        InputStream in = new FileInputStream(ATbrowseLink.getText());
                        pstm.setBlob(8, in);
                    }

                    pstm.executeUpdate();

                    reloadTables();
                    setAutoZero();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        ATlöschenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    new DeleteAuto(ATtextFieldATID);

                    reloadTables();
                    setAutoZero();
            }
        });

        ATaktialisierenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement pstm = new UpdateAuto().getPstm();

                    pstm.setString(1, comboBoxTyp.getSelectedItem().toString());
                    pstm.setString(2, spinnerJahr.getValue().toString());
                    pstm.setString(3, comboBoxHerseller.getSelectedItem().toString());

                    if(kommentarTextArea.getText().isBlank()){
                        pstm.setString(4, null);
                    } else {
                        pstm.setString(4, kommentarTextArea.getText());
                    }

                    pstm.setString(5, ATtextFieldASID.getText());
                    pstm.setString(6, ATtextFieldMTID.getText());
                    pstm.setString(7, textFieldPreis.getText().replace(",","."));
                    pstm.setString(9, ATtextFieldATID.getText());

                    if (ATbrowseLink.getText().isBlank()) {
                        String sql = "SELECT bild FROM auto WHERE ATID = ?;";
                        PreparedStatement pstmSelect = connect().prepareStatement(sql);
                        pstm.setString(1, ATtextFieldATID.getText());
                        ResultSet rs = pstm.executeQuery();

                        rs.next();
                        pstm.setBlob(8, rs.getBlob("bild"));
                    } else {
                        InputStream in = new FileInputStream(ATbrowseLink.getText());
                        pstm.setBlob(8, in);
                    }

                    pstm.executeUpdate();
                    ATbrowseLink.setText(null);
                    reloadTables();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        ATbrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ATbrowseLink.setText(new Browse().getLinkFile());
            }
        });
    }

    private void eventListenerAusstattung(){
        tableAusstattung.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                setAusstattung(tableAusstattung.getSelectedRow(), tableAusstattung.getModel());
            }
        });

        tableAusstattung.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                setAusstattung(tableAusstattung.getSelectedRow(), tableAusstattung.getModel());
            }
        });

        ASeinfügenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement pstmAusstattung = new InsertAusstattung().getPstm();

                    pstmAusstattung.setString(1,spinnerZoll.getValue().toString());
                    pstmAusstattung.setObject(2, comboBoxFelgenMaterial.getSelectedItem());
                    pstmAusstattung.setBoolean(3,checkBoxSitzheizung.isSelected());
                    pstmAusstattung.setBoolean(4,checkBoxLenkradheizung.isSelected());
                    pstmAusstattung.setBoolean(5,checkBoxSchiebedach.isSelected());

                    if(textFieldFarbe.getText().isBlank()) {
                        pstmAusstattung.setString(6, null);
                    } else {
                        pstmAusstattung.setString(6, textFieldFarbe.getText());
                    }

                    pstmAusstattung.setObject(7,comboBoxFarbMaterial.getSelectedItem());
                    pstmAusstattung.setObject(8,comboBoxInnenraumMaterial.getSelectedItem());
                    pstmAusstattung.setObject(9, comboBoxSitzMaterial.getSelectedItem());

                    pstmAusstattung.executeUpdate();

                    ATtextFieldASID.setText(getID(pstmAusstattung));

                    reloadTables();
                    setAusstattungZero();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        ASaktualisierenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement pstm = new UpdateAusstattung().getPstm();

                    pstm.setString(1, spinnerZoll.getValue().toString());
                    pstm.setString(2, comboBoxFelgenMaterial.getSelectedItem().toString());
                    pstm.setBoolean(3, checkBoxSitzheizung.isSelected());
                    pstm.setBoolean(4, checkBoxLenkradheizung.isSelected());
                    pstm.setBoolean(5, checkBoxSchiebedach.isSelected());
                    pstm.setString(6, textFieldFarbe.getText());
                    pstm.setString(7, comboBoxFarbMaterial.getSelectedItem().toString());
                    pstm.setString(8, comboBoxInnenraumMaterial.getSelectedItem().toString());
                    pstm.setString(9, comboBoxSitzMaterial.getSelectedItem().toString());
                    pstm.setString(10, AStextFieldASID.getText());

                    pstm.executeUpdate();

                    reloadTables();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        ASlöschenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               new DeleteAusstattung(AStextFieldASID);
               reloadTables();
            }
        });
    }

    private void eventListenerMotor(){
        tableMotor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                setMotor(tableMotor.getSelectedRow(), tableMotor.getModel());
            }
        });

        tableMotor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                setMotor(tableMotor.getSelectedRow(), tableMotor.getModel());
            }
        });

        MTeinfügenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement pstm = new InsertMotor().getPstm();

                    pstm.setString(1, textFieldVerbrauch.getText().replace(",","."));
                    pstm.setString(2, comboBoxGetriebe.getSelectedItem().toString());
                    pstm.setString(3, comboBoxKraftstoff.getSelectedItem().toString());
                    pstm.setString(4, spinnerHubraum.getValue().toString());
                    pstm.setString(5, spinnerPS.getValue().toString());

                    pstm.executeUpdate();

                    ATtextFieldMTID.setText(getID(pstm));

                    setMotorZero();

                    reloadTables();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }
        });

        MTaktualisierenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement pstm = new UpdateMotor().getPstm();

                    pstm.setString(1, textFieldVerbrauch.getText().replace(",","."));
                    pstm.setString(2, comboBoxGetriebe.getSelectedItem().toString());
                    pstm.setString(3, comboBoxKraftstoff.getSelectedItem().toString());
                    pstm.setString(4, spinnerHubraum.getValue().toString());
                    pstm.setString(5, spinnerPS.getValue().toString());
                    pstm.setString(6, MTtextFieldMTID.getText());

                    pstm.executeUpdate();

                    reloadTables();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        MTlöschenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    new DeleteMotor(MTtextFieldMTID);
                    setMotorZero();
                    reloadTables();
            }
        });
    }

    private void eventListenerMain(){
        tableMain.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                setMain(tableMain.getSelectedRow(), tableMain.getModel());
            }
        });

        MAINaktualisierenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadTables();
            }
        });

        MAINlöschenButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String sqlDelete = "DELETE auto, motor, ausstattung FROM auto RIGHT JOIN motor ON auto.MTID = motor.MTID RIGHT JOIN ausstattung ON auto.ASID = ausstattung.ASID WHERE auto.ATID = ?";
                    PreparedStatement psm = connect().prepareStatement(sqlDelete);
                    psm.setString(1, MAINtextFieldATID.getText());

                    psm.executeUpdate();

                    reloadTables();
                    setAutoZero();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    } //TODO in Logic verschieben

    /* ----------------------------------- setMethoden --------------------------*/

    private void setAuto(int i, TableModel tbm){

        ATtextFieldATID.setText(tbm.getValueAt(i,0).toString());

        switch(tbm.getValueAt(i,1).toString()){
            case "Combi":
                comboBoxTyp.setSelectedIndex(1);
                break;
            case "Coupe":
                comboBoxTyp.setSelectedIndex(2);
                break;
            case "Sportwagen":
                comboBoxTyp.setSelectedIndex(3);
                break;
            case "Limo":
                comboBoxTyp.setSelectedIndex(4);
                break;
            case "SUV":
                comboBoxTyp.setSelectedIndex(5);
                break;
            case "Cabrio":
                comboBoxTyp.setSelectedIndex(6);
                break;
            default:
                comboBoxTyp.setSelectedIndex(0);
                break;
        }

        spinnerJahr.setValue(Integer.parseInt(tbm.getValueAt(i,2).toString()));

        switch(tbm.getValueAt(i,3).toString()){
            case "BMW":
                comboBoxHerseller.setSelectedIndex(1);
                break;
            case "Mercedes":
                comboBoxHerseller.setSelectedIndex(2);
                break;
            case "VW":
                comboBoxHerseller.setSelectedIndex(3);
                break;
            case "Audi":
                comboBoxHerseller.setSelectedIndex(4);
                break;
            case "Opel":
                comboBoxHerseller.setSelectedIndex(5);
                break;
            case "Nissan":
                comboBoxHerseller.setSelectedIndex(6);
                break;
            case "Porsche":
                comboBoxHerseller.setSelectedIndex(7);
                break;
            case "Lamborghini":
                comboBoxHerseller.setSelectedIndex(8);
                break;
            case "Smart":
                comboBoxHerseller.setSelectedIndex(9);
                break;
            case "Ferrari":
                comboBoxHerseller.setSelectedIndex(10);
                break;
            case "Toyota":
                comboBoxHerseller.setSelectedIndex(11);
                break;
            case "Tesla":
                comboBoxHerseller.setSelectedIndex(12);
                break;
        }

        if(tbm.getValueAt(i,4) == null) {
            kommentarTextArea.setText("NULL");
        } else {
            kommentarTextArea.setText(tbm.getValueAt(i,4).toString());
        }

        ATtextFieldASID.setText(tbm.getValueAt(i, 5).toString());

        ATtextFieldMTID.setText(tbm.getValueAt(i, 6).toString());

        textFieldPreis.setText(tbm.getValueAt(i,7).toString());
    }

    private void setAutoZero(){
        ATtextFieldATID.setText("0");

        comboBoxTyp.setSelectedIndex(0);

        spinnerJahr.setValue(0);

        comboBoxHerseller.setSelectedIndex(0);

        kommentarTextArea.setText("");

        ATtextFieldASID.setText("0");

        ATtextFieldMTID.setText("0");

        textFieldPreis.setText("0,0");

        ATbrowseLink.setText(null);
    }

    private void setAusstattung(int i, TableModel tbm){

        AStextFieldASID.setText(tbm.getValueAt(i, 0).toString());

        spinnerZoll.setValue(Integer.parseInt(tbm.getValueAt(i,1).toString()));

        switch(tbm.getValueAt(i,2).toString()){
            case "Aluminium":
                comboBoxFelgenMaterial.setSelectedIndex(1);
                break;
            case "Stahl":
                comboBoxFelgenMaterial.setSelectedIndex(2);
                break;
            case "Carbon":
                comboBoxFelgenMaterial.setSelectedIndex(3);
                break;
            case "Magnesium":
                comboBoxFelgenMaterial.setSelectedIndex(4);
                break;
            case "Silizium":
                comboBoxFelgenMaterial.setSelectedIndex(5);
                break;
            case "Mangan":
                comboBoxFelgenMaterial.setSelectedIndex(6);
                break;
            default:
                comboBoxFelgenMaterial.setSelectedIndex(0);
                break;
        }

        if (tbm.getValueAt(i,3).toString().equals("Ja")){
            checkBoxSitzheizung.setSelected(true);
        } else {
            checkBoxSitzheizung.setSelected(false);
        }

        if (tbm.getValueAt(i,4).toString().equals("Ja")){
            checkBoxLenkradheizung.setSelected(true);
        } else {
            checkBoxLenkradheizung.setSelected(false);
        }

        if (tbm.getValueAt(i,5).toString().equals("Ja")){
            checkBoxSchiebedach.setSelected(true);
        } else {
            checkBoxSchiebedach.setSelected(false);
        }

        textFieldFarbe.setText(tbm.getValueAt(i,6).toString());

        switch(tbm.getValueAt(i,7).toString()) {
            case "Matt":
                comboBoxFarbMaterial.setSelectedIndex(1);
                break;
            case "Glanz":
                comboBoxFarbMaterial.setSelectedIndex(1);
                break;
            case "Perleffekt":
                comboBoxFarbMaterial.setSelectedIndex(1);
                break;
            default:
                comboBoxFarbMaterial.setSelectedIndex(0);
                break;
        }

        switch(tbm.getValueAt(i, 8).toString()) {
            case "Carbon":
                comboBoxInnenraumMaterial.setSelectedIndex(1);
                break;
            case "Alkantara":
                comboBoxInnenraumMaterial.setSelectedIndex(2);
                break;
            case "Holz":
                comboBoxInnenraumMaterial.setSelectedIndex(3);
                break;
            case "Plastik":
                comboBoxInnenraumMaterial.setSelectedIndex(4);
                break;
            default:
                comboBoxInnenraumMaterial.setSelectedIndex(0);
                break;
        }

        switch (tbm.getValueAt(i,9).toString()) {
            case "Stoff":
                comboBoxSitzMaterial.setSelectedIndex(1);
                break;
            case "Leder":
                comboBoxSitzMaterial.setSelectedIndex(2);
                break;
            case "Alkantara":
                comboBoxSitzMaterial.setSelectedIndex(3);
                break;
            default:
                comboBoxSitzMaterial.setSelectedIndex(0);
                break;
        }
    }

    private void setAusstattungZero() {
        AStextFieldASID.setText("0");

        spinnerZoll.setValue(0);

        comboBoxFelgenMaterial.setSelectedIndex(0);

        checkBoxSitzheizung.setSelected(false);

        checkBoxLenkradheizung.setSelected(false);

        checkBoxSchiebedach.setSelected(false);

        textFieldFarbe.setText(null);

        comboBoxFarbMaterial.setSelectedIndex(0);

        comboBoxInnenraumMaterial.setSelectedIndex(0);

        comboBoxSitzMaterial.setSelectedIndex(0);
    }

    private void setMotor (int i, TableModel tbm) {
        MTtextFieldMTID.setText(tbm.getValueAt(i,0).toString());

        textFieldVerbrauch.setText(tbm.getValueAt(i,1).toString());

        switch(tbm.getValueAt(i,2).toString()){
            case "Manuell":
                comboBoxGetriebe.setSelectedIndex(1);
                break;
            case "Automatik":
                comboBoxGetriebe.setSelectedIndex(2);
                break;
            case "Halb-Automatik":
                comboBoxGetriebe.setSelectedIndex(3);
                break;
            default:
                comboBoxGetriebe.setSelectedIndex(0);
                break;
        }

        switch (tbm.getValueAt(i,3).toString()) {
            case "Diesel":
                comboBoxKraftstoff.setSelectedIndex(1);
                break;
            case "Benzin":
                comboBoxKraftstoff.setSelectedIndex(2);
                break;
            case "Strom":
                comboBoxKraftstoff.setSelectedIndex(3);
                break;
            case "Gas":
                comboBoxKraftstoff.setSelectedIndex(4);
                break;
            default:
                comboBoxKraftstoff.setSelectedIndex(0);
                break;
        }

        spinnerHubraum.setValue(Integer.parseInt(tbm.getValueAt(i,4).toString()));

        spinnerPS.setValue(Integer.parseInt(tbm.getValueAt(i,5).toString()));
    }

    private void setMotorZero() {
        MTtextFieldMTID.setText(null);

        textFieldVerbrauch.setText("0,0");

        comboBoxGetriebe.setSelectedIndex(0);

        comboBoxKraftstoff.setSelectedIndex(0);

        spinnerHubraum.setValue(0);

        spinnerPS.setValue(0);
    }

    private void setMain(int i, TableModel tbm){
        MAINtextFieldATID.setText(tbm.getValueAt(i, 0).toString());

        try {
            String sql = "SELECT ASID, MTID FROM auto WHERE ATID = ?";
            PreparedStatement pstm = connect().prepareStatement(sql);
            pstm.setString(1, MAINtextFieldATID.getText());

            ResultSet rs = pstm.executeQuery();

            rs.next();
            MAINtextFieldASID.setText(rs.getString(1));
            MAINtextFieldMTID.setText(rs.getString(2));

            pstm.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void setMainZero() {
        MAINtextFieldATID.setText(null);
        MAINtextFieldASID.setText(null);
        MAINtextFieldMTID.setText(null);
    }
    /* ----------------------------------- Sonstiges --------------------------*/

    private static String getID(PreparedStatement ps){
        int id = 0;
        try {
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Integer.toString(id);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public GUI() {
        spinnerJahr.setEditor(new JSpinner.NumberEditor(spinnerJahr,"#"));
        spinnerHubraum.setEditor(new JSpinner.NumberEditor(spinnerHubraum,"#"));
        spinnerPS.setEditor(new JSpinner.NumberEditor(spinnerPS,"#"));

        ASError.setVisible(false);

        createTableCars();
        createTableAusstattung();
        createTableMotor();
        createTableMain();
        eventListenerAuto();
        eventListenerAusstattung();
        eventListenerMotor();
        eventListenerMain();
    }
}