package com.mobileaders.app;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.mobileaders.dto.DataBaseValidation;
import com.mobileaders.model.Sources;

/**
 * Hello world!
 *
 */
public class App extends TimerTask {	
	static Logger log = Logger.getLogger(DataBaseValidation.class.getName());

	
	@Override
	public void run() {
		log.info("Start job ---------------->");
		for (Sources XMLURL : DataBaseValidation.listOfSources()) {
			int x = DataBaseValidation.checkDataInserted(XMLURL.getSourceUrl());
			if (x == 0) {
				DataBaseValidation.saveNewsPaper(XMLURL);
				DataBaseValidation.saveArtical(XMLURL.getSourceUrl());

			}

		}
		 
	}
}
