package com.example.kristianschjonning.motswe.model;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by herpderp on 30/05/2015.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil
{

    public static void generateConfigFile() throws SQLException, IOException
    {
        writeConfigFile("ormlite_config.txt");
    }
}