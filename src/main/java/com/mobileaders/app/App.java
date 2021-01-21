package com.mobileaders.app;

 
import com.mobileaders.model.Sources;
import com.mobileaders.utils.DataBaseValidation;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	for (Sources XMLURL : DataBaseValidation.listOfSources()) {
			int x = DataBaseValidation.checkDataInserted(XMLURL.getSourceUrl());
			if (x == 0) {
				DataBaseValidation.saveNewsPaper(XMLURL);
				DataBaseValidation.saveArtical(XMLURL.getSourceUrl());
				

			}
			

		}
    }
}
