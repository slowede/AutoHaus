package de.autohaus.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.autohaus.model.Connect.connect;

public class TableAusstattung {
    private ResultSet rs;

    public TableAusstattung() {
        try {
            String execute = "SELECT * FROM ausstattung";
            PreparedStatement stm = connect().prepareStatement(execute);
            this.rs = stm.executeQuery();
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public ResultSet getRsTableAusstattung() {
        return rs;
    }
}
