/*
 * Copyright (c) 2019 simplity.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.simplity.fm;

import java.io.InputStream;
import java.util.Properties;

import org.simplity.fm.rdb.RdbDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * all parameters used by this app that are loaded from config file
 * TODO: read all these from a properties file. Will read-up best practices as
 * of today and build that functionality
 * 
 * @author simplity.org
 *
 */
public class Config {
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	private static final String RES_NAME = "fm/config.properties";
	private static final String NAME1 = "generatedPackage";
	private static final String VAL1 = "example.project.gen";
	private static final String NAME2 = "generatedSourceRoot";
	private static final String VAL2 = "c:/fm/";
	private static final String NAME3 = "xlsRootFolder";
	private static final String VAL3 = "c:/fm/xls/";
	private static final String NAME4 = "customCodePackage";
	private static final String VAL4 = "example.project.custom";
	private static final String DATA_SOURCE = "dataSourceJndiName";
	private static final String CON_STRING = "dbConnectoinString";
	private static final String DRIVER_NAME = "DbDriverClassName";

	private static final Config instance = load();

	/**
	 * 
	 * @return config instance
	 */
	public static Config getConfig() {
		return instance;
	}

	private static Config load() {
		logger.info("Locating resource named {} for configuraiton parameters", RES_NAME);
		Config config = new Config();
		Properties p = new Properties();
		ClassLoader loader = Config.class.getClassLoader();
		try (InputStream stream = loader.getResourceAsStream(RES_NAME)) {
			if (stream == null) {
				logger.error("Unable to locate resource {}. Config will work with hard coded values!!!", RES_NAME);
				return config;
			}
			p.load(stream);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception while loading properties from {}. \nError: {}\nConfig will work with hard coded values!!!",
					RES_NAME, e.getMessage());
		}
		config.generatedPackageName = getProperty(p, NAME1, VAL1, false);
		config.generatedSourceRoot = getProperty(p, NAME2, VAL2, true);
		config.xlsRootFolder = getProperty(p, NAME3, VAL3, true);
		config.customCodePackage = getProperty(p, NAME4, VAL4, false);
		
		setupDb(p);
		return config;
	}

	private static void setupDb(Properties p) {
		String ds = p.getProperty(DATA_SOURCE);
		if (ds != null && ds.isEmpty() == false) {
			try {
				RdbDriver.SetDataSource(ds);
				logger.info("JDBC driver successfully set");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("JDBC could not be set up with the data source value specified");
			}
			return;
		}

		logger.info("{} is not set for JDBC connection. We will try {}", DATA_SOURCE, CON_STRING);
		String con = p.getProperty(CON_STRING);
		if (con == null || con.isEmpty()) {
			logger.warn("RDBMS is not set up for this project.");
			return;
		}

		String driver = p.getProperty(DRIVER_NAME);
		if (driver == null || driver.isEmpty()) {
			logger.error("{} specified for db conenction, but {} is not set. JDBC driver cannot be initialized");
			return;
		}

		try {
			RdbDriver.SetConnectionString(con, driver);
			logger.info("JDBC driver successfully set");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("JDBC could not be set up with the connection string and driver calss name");
		}

	}

	private static String getProperty(Properties p, String name, String def, boolean isFolder) {
		String val = p.getProperty(name);
		if (val == null) {
			if (def == null) {
				logger.error("no value for property {} defined.");
				return null;
			}
			logger.error("no value for property {} defined. A Defualt of {} assumed");
			return def;
		}
		if (isFolder) {
			char c = val.charAt(val.length() - 1);
			if (c != '/') {
				val += '/';
			}
		}
		logger.info("{} set to {}", name, val);
		return val;
	}

	private Config() {
		//
	}

	private String generatedPackageName;
	private String generatedSourceRoot;
	private String xlsRootFolder;
	private String customCodePackage;

	/**
	 * @return package name with trailing that all generated classes belong to.
	 *         We may create sub-packages in them
	 * 
	 */
	public String getGeneratedPackageName() {
		return this.generatedPackageName;
	}

	/**
	 * @return '/'-ended root source folder where sources are generated. folders
	 *         are
	 *         appended to this by the generator based on package name
	 */
	public String getGeneratedSourceRoot() {
		return this.generatedSourceRoot;
	}

	/**
	 * @return '/' ended root folder where dataTypes.xlsx is found. forms are
	 *         stored under a sub-folder named form under this folder
	 */
	public String getXlsRootFolder() {
		return this.xlsRootFolder;
	}

	/**
	 * @return package name ending with a '.' where user-defined classes are
	 *         placed
	 */
	public String getCustomCodePackage() {
		return this.customCodePackage;
	}

	/**
	 * 
	 * @return name of the java class that has all the generated data types.
	 *         This class extends <code>DataTypes</code>
	 */
	public String getDataTypesClassName() {
		return "DefinedDataTypes";
	}
}
