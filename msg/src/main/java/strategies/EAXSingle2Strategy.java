package strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import entities.City;
import entities.Tour;
import utilities.Utilities;

public class EAXSingle2Strategy {
	private Tour tourA;
	private Tour tourB;
	private Map<Integer, List<Integer>> mapTourA;
	private Map<Integer, List<Integer>> mapTourB;
	private Map<Integer, List<Integer>> tempTourA; 
	private Map<Integer, List<Integer>> tempTourB;
	int size;
	Random random = new Random();
	
	public EAXSingle2Strategy(Tour tourA, Tour tourB) {
		this.tourA = tourA;
		this.tourB = tourB;
		this.size = tourA.getTspTour().size() + 1;
		convertTour();

	}
	public Tour startEAXSingleStrategy2(int numOfChildren) {
		this.size = tourA.getTspTour().size() + 1;
		if(tourA.getDistance()==tourB.getDistance()&&Utilities.compareTours(tourA, tourB)) return tourA;
		boolean check = true;
		List<Integer> list = new ArrayList<Integer>();
		List<Integer> tempTour = new ArrayList<Integer>();
		double distance =tourA.getDistance();
		for(int i = 0; i<numOfChildren; ++i){
			if(check) {
				deleteSameEdges();
		 check = false;
			}
		list = createABCycleSingleStrategy2();

		
				tempTour = createIntermediateSolution(createESetsSingleStrategy(list, this.tourA));
				int tempDis = Utilities.calculateTourInteger(tempTour);
				if(distance>tempDis ) {
					tourA = Utilities.convertListToTour(tempTour);
					distance = tourA.getDistance();
					check = true;
					convertTour();
				}
			}
		
		
		return  tourA ;
	}
	public Tour startEAXSingleStrategy2() {
		this.size = tourA.getTspTour().size() + 1;
		if(tourA.getDistance()==tourB.getDistance()&&Utilities.compareTours(tourA, tourB)) return tourA;
		boolean check = true;
		List<Integer> list = new ArrayList<Integer>();
		List<Integer> tempTour = new ArrayList<Integer>();
		double distance =tourA.getDistance();
		while(true){
			if(check) {
				deleteSameEdges();
		 check = false;
			}
		list = createABCycleSingleStrategy2();
		if(list.isEmpty()) {
			return tourA;
		}
				tempTour = createIntermediateSolution(createESetsSingleStrategy(list, this.tourA));
				int tempDis = Utilities.calculateTourInteger(tempTour);
				if(distance>tempDis ) {
					tourA = Utilities.convertListToTour(tempTour);
					distance = tourA.getDistance();
					check = true;
					convertTour();
				}
			}
		
		
	}
	private List<Integer> createIntermediateSolution(Map<Integer, Integer> tourA) {
		List<List<Integer>> liste = new ArrayList<List<Integer>>();

		List<Integer> remaining = IntStream.rangeClosed(1, tourA.size()).boxed().collect(Collectors.toList());
		while (!remaining.isEmpty()) {
			Integer start = remaining.get(0);
			remaining.remove(start);
			Integer nextCity = tourA.get(start);
			List<Integer> subtour = new ArrayList<Integer>();
			subtour.add(start);
			while (!start.equals(nextCity)) {
				subtour.add(nextCity);
				remaining.remove(nextCity);
				nextCity = tourA.get(nextCity);

			}
			subtour.add(start);
			liste.add(subtour);

		}
		
		return liste.size() == 1 ? liste.get(0) : Utilities.connectSubtours(liste);

	}
	public List<Integer> createABCycleSingleStrategy2() {
		tempTourA = mapTourA;
		tempTourB = mapTourB;
					List<Integer> abcycleTemp = new ArrayList<Integer>();
					while(abcycleTemp.isEmpty()&&!tempTourA.isEmpty()) {
						Integer number = ThreadLocalRandom.current().nextInt(1, size);
						if(tempTourA.containsKey(number)) {
						abcycleTemp = createABCycle(number, new ArrayList<Integer>());
						}
					}
							
		return abcycleTemp;
	}
	private ArrayList<Integer> createABCycle(Integer start, ArrayList<Integer> abcycle) {
		List<Integer> cities = new ArrayList<Integer>(2);
		Integer city = null;
		try {
			cities = this.tempTourA.get(start);
		} catch (Exception ex) {
			System.out.println(ex.getCause());
			tempTourA.remove(start);
			return new ArrayList<Integer>(0);
		}

		abcycle.add(start);
		if (cities.size() == 0) {
			tempTourA.remove(start);
			return new ArrayList<Integer>(0);
		}
		if (cities.size() > 1) {
			city = random.nextBoolean() ? cities.get(1) : cities.get(0);
			createABCyclesRekTourB(start, start, city, abcycle);

		} else {
			city = cities.get(0);
			createABCyclesRekTourB(start, start, city, abcycle);

		}

		return abcycle;
	}
	private void createABCyclesRekTourA(Integer start, Integer from, Integer to, ArrayList<Integer> abcycle) {
		if (start.equals(to) && abcycle.size() % 2 == 0) {
			abcycle.add(to);
			tempTourB.get(from).remove(to);
			tempTourB.get(to).remove(from);
			if (tempTourB.get(to).isEmpty()) {
				tempTourB.remove(to);
			}
			if (tempTourB.get(from).isEmpty()) {
				tempTourB.remove(from);
			}
			return;
		}
		List<Integer> cities = new ArrayList<Integer>(2);
		Integer city = null;
		try {
			cities = this.tempTourA.get(to);
		} catch (Exception ex) {
			return;
		}
		try {
			
		
		if (cities.size() > 1) {
			city = random.nextBoolean() ? cities.get(1) : cities.get(0);
			abcycle.add(to);
			tempTourB.get(from).remove(to);
			tempTourB.get(to).remove(from);
			if (tempTourB.get(to).isEmpty()) {
				tempTourB.remove(to);
			}
			if (tempTourB.get(from).isEmpty()) {
				tempTourB.remove(from);
			}
			from = to;
			createABCyclesRekTourB(start, from, city, abcycle);
		} else {
			city = cities.get(0);
			abcycle.add(to);
			tempTourB.get(from).remove(to);
			tempTourB.get(to).remove(from);
			if (tempTourB.get(to).isEmpty()) {
				tempTourB.remove(to);
			}
			if (tempTourB.get(from).isEmpty()) {
				tempTourB.remove(from);
			}
			from = to;
			createABCyclesRekTourB(start, from, city, abcycle);
		}
		}
		catch(Exception e) {
			System.out.println("Stop");
		}

	}

	private void createABCyclesRekTourB(Integer start, Integer from, Integer to, ArrayList<Integer> abcycle) {
		List<Integer> cities = new ArrayList<Integer>(2);
		Integer city = null;
		try {
			cities = this.tempTourB.get(to);
		} catch (Exception ex) {
			tempTourB.remove(to);
			return;
		}
		try {
			
		
		if (cities.size() > 1) {
			city = random.nextBoolean() ? cities.get(1) : cities.get(0);
			abcycle.add(to);
			tempTourA.get(from).remove(to);
			tempTourA.get(to).remove(from);
			if (tempTourA.get(to).isEmpty()) {
				tempTourA.remove(to);
			}
			if (tempTourA.get(from).isEmpty()) {
				tempTourA.remove(from);
			}
			from = to;
			createABCyclesRekTourA(start, from, city, abcycle);
		}
		else {
			city = cities.get(0);
			abcycle.add(to);
			tempTourA.get(from).remove(to);
			tempTourA.get(to).remove(from);
			if (tempTourA.get(to).isEmpty()) {
				tempTourA.remove(to);
			}
			if (tempTourA.get(from).isEmpty()) {
				tempTourA.remove(from);
			}
			from = to;
			createABCyclesRekTourA(start, from, city, abcycle);

		}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Stop");
		}
	

	}
	private Map<Integer, Integer> createESetsSingleStrategy(List<Integer> li, Tour tourA) {
		Map<Integer, Integer> tour = deleteExistingEdgesFromParentASingleStrategy(convertTourToMap(tourA), li);
		
		
			
			int[] abcycle = li.stream().mapToInt(i -> i).toArray();
			for (int i = 1; i < abcycle.length; i += 2) {
				if (!tour.containsKey(abcycle[i])) {

					tour.put(abcycle[i], abcycle[i + 1]);
				} else if (!tour.containsKey(abcycle[i + 1])) {
					tour.put(abcycle[i + 1], abcycle[i]);
				} else {

					if (!tour.containsKey(tour.get(abcycle[i]))) {
						tour.put(tour.get(abcycle[i]), abcycle[i]);
						tour.put(abcycle[i], abcycle[i + 1]);
					} else if (!tour.containsKey(tour.get(abcycle[i + 1]))) {
						tour.put(tour.get(abcycle[i + 1]), abcycle[i + 1]);
						tour.put(abcycle[i + 1], abcycle[i]);
					} else {
						Integer from = abcycle[i];
						Integer to = tour.get(from);
						Integer temp = tour.get(to);
						tour.remove(from);
						tour.put(to, from);
						tour.put(abcycle[i], abcycle[i + 1]);

						boolean check = true;

						while (check) {
							if (!tour.containsKey(temp)) {
								tour.put(temp, to);
								check = false;
							} else {
								from = to;
								to = temp;
								temp = tour.get(to);
								tour.put(to, from);

							}
						}
					}
				}

			}
			
		

//			if(tour.size()!=76) {
//				System.out.println("wait");
//			}
		return tour;
	}
	
	private Map<Integer, Integer> convertTourToMap(Tour tour) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(tour.getTspTour().size());
		for (City city : tour.getTspTour()) {
			map.put(city.getCityNum(), city.getSuccesorNum());
		}
		return map;

	}
	private Map<Integer, Integer> deleteExistingEdgesFromParentASingleStrategy(Map<Integer, Integer> tourA,
			List<Integer> li) {



			int[] abcycle = li.stream().mapToInt(i -> i).toArray();
			for (int i = 1; i < abcycle.length; i += 2) {
				boolean suc = tourA.remove(abcycle[i], abcycle[i - 1]);
				if (!suc) {
					tourA.remove(abcycle[i - 1], abcycle[i]);
				}

			}

		

		return tourA;

	}
	
	private void convertTour() {
		this.mapTourA = Utilities.convertTourToMapping(tourA);
		this.mapTourB = Utilities.convertTourToMapping(tourB);
	}
	public void deleteSameEdges( ) {
		 int size = mapTourA.size();

		for(Integer i = 1; i <= size; ++i) {
				try {
			 List<Integer> a  = new ArrayList<Integer>(mapTourA.get(i));
			 List<Integer> b  = new ArrayList<Integer>(mapTourB.get(i));
		
			if (!a.isEmpty()&&!b.isEmpty()) {
				
			if(a.size()>b.size()) {
				if(a.get(0).equals(b.get(0))) {
					mapTourA.get(a.get(0)).remove(i);
					mapTourA.get(i).remove(a.get(0));
					mapTourB.get(b.get(0)).remove(i);
					mapTourB.remove(i);	
				}
				if(a.get(1).equals(b.get(0))) {
					mapTourA.get(a.get(1)).remove(i);
					mapTourA.get(i).remove(a.get(1));
					mapTourB.get(b.get(0)).remove(i);
					mapTourB.remove(i);	
				}
			}
			
			else if(a.size()<b.size()) {
				if(a.get(0).equals(b.get(0))) {
					mapTourB.get(b.get(0)).remove(i);
					mapTourB.get(i).remove(b.get(0));
					mapTourA.get(a.get(0)).remove(i);
					mapTourA.remove(i);	
				}
				if(a.get(0).equals(b.get(1))) {
					mapTourB.get(b.get(1)).remove(i);
					mapTourB.get(i).remove(b.get(1));
					mapTourA.get(a.get(0)).remove(i);
					mapTourA.remove(i);	
				}
			
			}
			else if (a.size()==2){
				if(a.get(0).equals(b.get(0))) {
					mapTourA.get(a.get(0)).remove(i);
					mapTourA.get(i).remove(a.get(0));
					mapTourB.get(b.get(0)).remove(i);
					mapTourB.get(i).remove(b.get(0));
				}
				if(a.get(0).equals(b.get(1))) {
					mapTourA.get(a.get(0)).remove(i);
					mapTourA.get(i).remove(a.get(0));
					mapTourB.get(b.get(1)).remove(i);
					mapTourB.get(i).remove(b.get(1));
				}
				if(a.get(1).equals(b.get(0))) {
					mapTourB.get(b.get(0)).remove(i);
					mapTourB.get(i).remove(b.get(0));
					mapTourA.get(a.get(1)).remove(i);
					mapTourA.get(i).remove(a.get(1));	
				}
				if(a.get(1).equals(b.get(1))) {
					mapTourB.get(b.get(1)).remove(i);
					mapTourB.get(i).remove(b.get(1));
					mapTourA.get(a.get(1)).remove(i);
					mapTourA.get(i).remove(a.get(1));	
				}
				
			}
			else {
				if(a.get(0).equals(b.get(0))) {
					mapTourA.get(a.get(0)).remove(i);
					mapTourA.get(i).remove(a.get(0));
					mapTourB.get(b.get(0)).remove(i);
					mapTourB.get(i).remove(b.get(0));
				}
			}
		}
			if (mapTourA.get(i).isEmpty()) {
				mapTourA.remove(i);
			}
			if (mapTourB.get(i).isEmpty()) {
				mapTourB.remove(i);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("wait");
		}
		}
		
		
	}
}
