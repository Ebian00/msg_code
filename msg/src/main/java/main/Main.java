package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import entities.City;
import entities.Tour;
import strategies.EAXKMultipleStrategy;
import strategies.EAXSingle2Strategy;
import utilities.Utilities;





public class Main {
	public static void main(String[] args) throws FileNotFoundException {
//		String fileName = "msg_standorte.xlsx";
//		ClassLoader classLoader = ClassLoader.getClassLoader();
		 InputStream ExcelFileToRead = new FileInputStream(Main.class.getClassLoader().getResource("msg_standorte.xlsx").getFile());
		 XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(ExcelFileToRead);
		} catch (IOException e) {
			e.printStackTrace();
		}			
		

		
		List<City> coordinates = new ArrayList<City>();
		Tour result = null;
		XSSFSheet exelFile = wb.getSheetAt(0);
		
		int dimension = exelFile.getLastRowNum();
		ArrayList<String> cityNames = new ArrayList<String>();
		for(int i = 1; i<=dimension;++i) {
			XSSFRow row = exelFile.getRow(i);
			cityNames.add(row.getCell(1).getStringCellValue());
			double  x = row.getCell(6).getNumericCellValue();
			double y = row.getCell(7).getNumericCellValue() ;
			City c = new City(x,y, i);
			coordinates.add(c);
		}
		// this environment variable sets the number of individuals in a population
		int population = 20;

		int runs = 10;
		//converting the cities to tours 
		Utilities util = new Utilities(coordinates);

		// this environment variable sets the number of offspring solutions
//		Date time = new java.util.Date(System.currentTimeMillis());
//		System.out.println(new SimpleDateFormat("HH:mm:ss").format(time));
 		for(int x = 0; x<runs;++x) {
// 			long start = System.currentTimeMillis();
			List<Tour> firstgeneration = util.createFirstGeneration(population, dimension);

 			List<Tour> firstgenerationTemp = new ArrayList<Tour>(firstgeneration.size());
 			int size = firstgeneration.size();
 			
 			double bestTour = Double.MAX_VALUE;
 			int lokalOptima = 0;
 			int g = 0;
		while ( lokalOptima<3 ) {
			firstgenerationTemp.clear();
			for (int i = 0; i< size;++i) {
				
				Tour temp = new Tour(firstgeneration.get(i).getTspTour());
				for(int j =0; j<5;++j) {
					
					int [] rem = ThreadLocalRandom.current().ints(0, size).distinct().limit(5).toArray();
					EAXSingle2Strategy eax = new EAXSingle2Strategy(firstgeneration.get(i), firstgeneration.get(rem[j]));
					Tour child = eax.startEAXSingleStrategy2();
					if(temp.getDistance()>child.getDistance()) {
						temp = child;
					}
					
				}
				firstgenerationTemp.add(temp);
			}
			firstgeneration.clear();
			firstgeneration.addAll(firstgenerationTemp);
			boolean check = true;
			for (int j = 0; j < firstgeneration.size()&&check; ++j) {
				if (bestTour > firstgeneration.get(j).getDistance()) {
					bestTour = firstgeneration.get(j).getDistance();
					lokalOptima = 0;
					check = false;
				}
			}

			lokalOptima += 1;
			g+=1;

		}
    	result = firstgeneration.get(0);
 		}		
 		System.out.println("best tour  is "+ result.getDistance()/1000 +" Km" );
 		result.getTspTour().forEach(city -> System.out.print(cityNames.get(city.getCityNum()-1) + " --> "));

	}

}
