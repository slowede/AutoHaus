package de.autohaus.logic;

import de.autohaus.data.TableCars;

import javax.swing.table.DefaultTableModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DtmTableCars {
    private DefaultTableModel dtm;


    public DtmTableCars() {
        try {
            ResultSet rs = new TableCars().getRs();

            DefaultTableModel dtm = new DefaultTableModel(
                    null,
                    new String[]{"ATID", "Typ", "Baujahr", "Hersteller", "Kommentar", "ASID", "MTID", "Preis"}
            );

            while (rs.next()) {
                String[] data = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)};
                dtm.addRow(data);
            }

            this.dtm = dtm;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public DefaultTableModel getDtm() {
        return dtm;
    }
}