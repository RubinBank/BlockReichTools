package me.criztovyl.blockreichtools.tools;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import me.criztovyl.clickless.ClicklessSign;

public class SignDBSafe implements DBSafe<ClicklessSign> {

    @Override
    public ArrayList<ClicklessSign> loadFromDatabase(Connection con)
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveToDatabase(ClicklessSign save, Connection con)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }

}
