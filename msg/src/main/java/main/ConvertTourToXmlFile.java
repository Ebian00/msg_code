package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import entities.Tour;

public class ConvertTourToXmlFile {
	//this class is for savind and reading the individuals on the drive
	public static void marshalingTour(Tour tour , String fileName) throws JAXBException
	{
	    JAXBContext jaxbContext = JAXBContext.newInstance(Tour.class);
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    //the enviroment variable decide where to save the files 
    	String directory = (System.getProperty("Directory"))!=null?System.getProperty("Directory"):System.getenv("Directory"); 

	    //jaxbMarshaller.marshal(tour, new File(directory +fileName+".xml"));
	}
	
	public static List<Tour> unMarshalingTour() throws JAXBException
	{
		String directory = (System.getProperty("Directory"))!=null?System.getProperty("Directory"):System.getenv("Directory"); 

	    JAXBContext jaxbContext = JAXBContext.newInstance(Tour.class);
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	     
	    List<String> fileNames = new ArrayList<String>();
	    try {
	      DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory));
	      for (Path path : directoryStream) {
	        fileNames.add(path.toString());
	      }
	    } catch (IOException ex) {
	    }
	    List<Tour> tours = new ArrayList<Tour>(fileNames.size());
	    for(String path : fileNames) {
	    	Tour tour = (Tour) jaxbUnmarshaller.unmarshal( new File(path) );
	    	tours.add(tour);
	    }	    
		return tours;
	     
	    
	}
}
