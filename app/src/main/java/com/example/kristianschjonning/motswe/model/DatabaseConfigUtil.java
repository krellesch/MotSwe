package com.example.kristianschjonning.motswe.model;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Matti
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil
{

    public static void generateConfigFile() throws SQLException, IOException
    {
        writeConfigFile("ormlite_config.txt");
    }
}