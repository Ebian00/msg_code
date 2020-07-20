package utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import entities.City;
import entities.Tour;

public class Utilities {
	public static  double[][] disMatrix;


	public Utilities(List<City> coordinates) {
		disMatrix = calculateDisMatrix(coordinates);
	}

	public static List<City> createPreAndSuc(List<City> tour) {
		tour.get(0).setPredecessorNum(tour.get(tour.size() - 1).getCityNum());
		tour.get(0).setSuccesorNum(tour.get(1).getCityNum());
		for (int i = 1; i < tour.size() - 1; ++i) {
			tour.get(i).setSuccesorNum(tour.get(i + 1).getCityNum());
			tour.get(i).setPredecessorNum(tour.get(i - 1).getCityNum());
		}
		tour.get(tour.size() - 1).setSuccesorNum(tour.get(0).getCityNum());
		tour.get(tour.size() - 1).setPredecessorNum(tour.get(tour.size() - 2).getCityNum());
		return tour;
	}

	
	public static Map<Integer, List<Integer>> convertTourToMapping(Tour tour){
		Map<Integer, List<Integer>> map = new HashMap< Integer,List<Integer>>();
		for (City city :tour.getTspTour()) {
			ArrayList<Integer> list = new ArrayList<Integer>(2);
			list.add(city.getPredecessorNum());
			list.add(city.getSuccesorNum());
			map.put(city.getCityNum(), list);
						}
		return map;
	}
	
	public static boolean compareTours(Tour tourA, Tour tourB) {
		Map<Integer, List<Integer>> mapA = convertTourToMapping(tourA);
		Map<Integer, List<Integer>> mapB = convertTourToMapping(tourB);
		for (int i = 1; i < mapA.size(); ++i) {
			List<Integer> a = mapA.get(i);
			List<Integer> b = mapB.get(i);
			try {
				if (!a.contains(b.get(1)) || !a.contains(b.get(0))) {
					return false;
				}
			} catch (Exception e) {
				System.out.println("wait");
			}

		}
		return true;

	}
	public static Tour convertListToTour(List<Integer> tourList) {
		tourList.remove(tourList.size()-1);
		List<City> temp = new ArrayList<City>();
		for (Integer cityNum : tourList) {
			City c = new City(cityNum);
			temp.add(c);
		}
		temp = createPreAndSuc(temp);
		
		return new Tour(temp);
	}
	public List<Tour> createFirstGeneration(int populationNum, int dimension) {
		List<Tour> tours = new ArrayList<Tour>(populationNum);
		int jump = (dimension/populationNum);
		for (int i = 1; i<dimension; i+=jump) {
			List<Integer> notVisited = IntStream.rangeClosed(1, dimension).boxed().collect(Collectors.toList());
			Integer succesor = i;
			List<Integer> visited = new ArrayList<Integer>(dimension);

			for (int j = 0; j < dimension; ++j) {
				visited.add(succesor);
				notVisited.remove(succesor);
				double min = Integer.MAX_VALUE;
				Integer next = 0;
				for (Integer city : notVisited) {
					try {

						if (min > disMatrix[succesor - 1][city - 1]) {
							min = disMatrix[succesor - 1][city - 1];
							next = city;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				succesor = next;
			}
			visited.add(visited.get(0));
			visited = this.twoOpt(visited);
			visited.remove(dimension);
			List<City> tspTour = new ArrayList<City>(dimension);
			for (Integer cityNum : visited) {
				City c = new City(cityNum);
				tspTour.add(c);
			}
			tspTour = createPreAndSuc(tspTour);
			Tour tour = new Tour(tspTour);
			
			tours.add(tour);
		}
		return tours;
	}
	public List<Integer> twoOpt(List<Integer> tour) {
		tour.remove(tour.size() - 1);

		int size = tour.size();
		int halfSize = size / 2;
		for (int i = 0; i < size; ++i) {
			for (int j = i + 2; j < size - 2; ++j) {
				if ((disMatrix[tour.get(i) - 1][tour.get(i + 1) - 1] + disMatrix[tour.get(j) - 1][tour.get(j + 1) - 1])
						- (disMatrix[tour.get(i) - 1][tour.get(j) - 1]
								+ disMatrix[tour.get(i + 1) - 1][tour.get(j + 1) - 1]) > 0) {
					if (Math.abs(i - j) > halfSize) {
						int tempi = i + size;
						int tempj = j + size + 1;
						int dif = i + (size - j);
						while (dif > 1) {
							int moduloi = tempi % size;
							int moduloj = tempj % size;
							Integer v1 = tour.get(moduloi);
							Integer v2 = tour.get(moduloj);
							tour.set(moduloi, v2);
							tour.set(moduloj, v1);
							++tempj;
							--tempi;
							dif -= 2;
						}

					} else {
						int tempi = i + 1;
						int tempj = j;
						while (tempi < tempj) {
							Integer v1 = tour.get(tempi);
							Integer v2 = tour.get(tempj);
							tour.set(tempj, v1);
							tour.set(tempi, v2);
							--tempj;
							++tempi;
						}

					}
					i = 0;
					j = 0;

				}

			}

		}
		tour.add(tour.get(0));
		return tour;

	}

	public static List<Integer> connectSubtours(List<List<Integer>> offSpring) {
		while (offSpring.size() > 1) {
			double dis = Double.MAX_VALUE;
			Integer city1 = 0;
			Integer city2 = 0;
			offSpring.sort(Comparator.comparingInt(List<Integer>::size));
			List<Integer> subtour1 = offSpring.get(0);
			int distanceOfSub1 = calculateTourInteger(subtour1);
			subtour1.remove(subtour1.size() - 1);
			List<Integer> subtour2 = offSpring.get(1);
			List<Integer> temp = new ArrayList<Integer>();
			int distanceOfSub2 = calculateTourInteger(subtour2);
			subtour2.remove(subtour2.size() - 1);
			if (subtour1.size() == 2 && subtour2.size() == 2) {

				for (Integer i : subtour1) {
					for (Integer j : subtour2) {
						double tempdis = disMatrix[i - 1][j - 1];
						if (tempdis < dis) {
							dis = tempdis;
							city1 = i;
							city2 = j;
						}
					}
				}
				int index1 = subtour1.indexOf(city1);
				int index2 = subtour2.indexOf(city2);

				temp.add(city1);
				temp.add(city2);
				temp.add(subtour2.get((index2 + 1) % 2));
				temp.add(subtour1.get((index1 + 1) % 2));
				temp.add(temp.get(0));

			}

			else {

				for (Integer i : subtour1) {
					for (Integer j : subtour2) {
						double tempdis = disMatrix[i - 1][j - 1];
						if (tempdis < dis) {
							dis = tempdis;
							city1 = i;
							city2 = j;
						}
					}
				}
				int subtour1Size = subtour1.size();
				int subtour2Size = subtour2.size();
				int index1 = subtour1.indexOf(city1);
				index1 = index1 == 0 ? index1 + subtour1Size : index1;
				int index2 = subtour2.indexOf(city2);
				index2 = index2 == 0 ? index2 + subtour2Size : index2;

					int tempDis = Integer.MAX_VALUE;
					List<Integer> temp2;
					List<Integer> tenNear = findTenNearest(index1, index2, subtour2);
					int indexOf1 = city1 - 1;
					for (Integer ind : tenNear) {
						temp2 = new ArrayList<Integer>();
						index1 += subtour1Size;
						index2 += subtour2Size;
						int indexOf2 = (ind - 1);
						index2 = subtour2.indexOf(ind) + subtour2Size;

						int minusOneTourOne = subtour1.get((index1 - 1) % subtour1Size) - 1;
						int plusOneTourOne = subtour1.get((index1 + 1) % subtour1Size) - 1;
						int minusOneTourTwo = subtour2.get((index2 - 1) % subtour2Size) - 1;
						int plusOneTourTwo = subtour2.get((index2 + 1) % subtour2Size) - 1;
						double mainConnection = disMatrix[indexOf1][indexOf2];
						double sumdist1 = (distanceOfSub1 - disMatrix[minusOneTourOne][indexOf1])
								+ (distanceOfSub2 - disMatrix[minusOneTourTwo][indexOf2])
								+ disMatrix[minusOneTourOne][minusOneTourTwo] + mainConnection;
						double sumdist2 = (distanceOfSub1 - disMatrix[minusOneTourOne][indexOf1])
								+ (distanceOfSub2 - disMatrix[plusOneTourTwo][indexOf2])
								+ disMatrix[minusOneTourOne][plusOneTourTwo] + mainConnection;
						double sumdist3 = (distanceOfSub1 - disMatrix[plusOneTourOne][indexOf1])
								+ (distanceOfSub2 - disMatrix[minusOneTourTwo][indexOf2])
								+ disMatrix[plusOneTourOne][minusOneTourTwo] + mainConnection;
						double sumdist4 = (distanceOfSub1 - disMatrix[plusOneTourOne][indexOf1])
								+ (distanceOfSub2 - disMatrix[plusOneTourTwo][indexOf2])
								+ disMatrix[plusOneTourOne][plusOneTourTwo] + mainConnection;
						double minusToSubTour2 = sumdist1 > sumdist2 ? sumdist2 : sumdist1;
						double plusToSubTour2 = sumdist3 > sumdist4 ? sumdist4 : sumdist3;

						index1 = subtour1.indexOf(city1);
						index2 = subtour2.indexOf(ind);
						if (minusToSubTour2 < plusToSubTour2) {

							if (sumdist1 < sumdist2) {
								for (int i = 0; i < index1; ++i) {
									temp2.add(subtour1.get(i));
								}
								for (int i = index2 - 1; i >= 0; --i) {
									temp2.add(subtour2.get(i));
								}
								for (int i = subtour2Size - 1; i >= index2; --i) {
									temp2.add(subtour2.get(i));
								}
								for (int i = index1; i < subtour1Size; ++i) {
									temp2.add(subtour1.get(i));
								}
								temp2.add(temp2.get(0));

							} else {
								for (int i = 0; i < index1; ++i) {
									temp2.add(subtour1.get(i));
								}
								for (int i = index2 + 1; i < subtour2Size; ++i) {
									temp2.add(subtour2.get(i));
								}
								for (int i = 0; i <= index2; ++i) {
									temp2.add(subtour2.get(i));
								}
								for (int i = index1; i < subtour1Size; ++i) {
									temp2.add(subtour1.get(i));
								}
								temp2.add(temp2.get(0));
							}

						} else {
							if (sumdist3 < sumdist4) {

								for (int i = subtour1Size - 1; i > index1; --i) {
									temp2.add(subtour1.get(i));
								}
								for (int i = index2 - 1; i >= 0; --i) {
									temp2.add(subtour2.get(i));
								}
								for (int i = subtour2Size - 1; i >= index2; --i) {
									temp2.add(subtour2.get(i));
								}
								for (int i = index1; i >= 0; --i) {
									temp2.add(subtour1.get(i));
								}
								temp2.add(temp2.get(0));
							} else {
								for (int i = subtour1Size - 1; i > index1; --i) {
									temp2.add(subtour1.get(i));
								}
								for (int i = index2 + 1; i < subtour2Size; ++i) {
									temp2.add(subtour2.get(i));
								}
								for (int i = 0; i <= index2; ++i) {
									temp2.add(subtour2.get(i));
								}
								for (int i = index1; i >= 0; --i) {
									temp2.add(subtour1.get(i));
								}
								temp2.add(temp2.get(0));
							}
						}
						int dd = calculateTourInteger(temp2);

						if (tempDis > dd) {

							temp = new ArrayList<Integer>(temp2);
							tempDis = dd;
						}

					}
			}
			offSpring.remove(0);
			offSpring.remove(0);
			offSpring.add(temp);

		}
		return offSpring.get(0);
	}

	public static List<Integer> findTenNearest(int index1, int index2, List<Integer> subtour2) {
		List<Integer> temp = new ArrayList<Integer>(subtour2);
		List<Integer> tenNearest = new ArrayList<Integer>(10);
		for (int i = 0; i < 10 && !temp.isEmpty(); ++i) {
			Integer city2 = temp.get(0);
			double dis = disMatrix[index1 - 1][temp.get(0) - 1];
			for (Integer j : temp) {
				double tempdis = disMatrix[index1 - 1][j - 1];
				if (tempdis < dis) {
					dis = tempdis;
					city2 = j;
				}

			}
			tenNearest.add(city2);
			int index = temp.indexOf(city2);
			index += temp.size();
			Integer minus = temp.get((index - 1) % temp.size());
			Integer plus = temp.get((index + 1) % temp.size());
			temp.remove(city2);
			temp.remove(minus);
			temp.remove(plus);
		}
		for (int i = 0; i < 10 && !temp.isEmpty(); ++i) {
			Integer city2 = temp.get(0);
			double dis = disMatrix[index2 - 1][subtour2.get(0) - 1];
			for (Integer j : temp) {
				double tempdis = disMatrix[index2 - 1][j - 1];
				if (tempdis < dis) {
					dis = tempdis;
					city2 = j;
				}

			}
			tenNearest.add(city2);
			int index = temp.indexOf(city2);
			index += temp.size();
			Integer minus = temp.get((index - 1) % temp.size());
			Integer plus = temp.get((index + 1) % temp.size());
			temp.remove(city2);
			temp.remove(minus);
			temp.remove(plus);
		}
		return tenNearest;

	}

	public static int calculateTourInteger(List<Integer> tour) {
		int distance = 0;
		for (int i = 0; i < tour.size() - 1; ++i) {
			distance += disMatrix[tour.get(i) - 1][tour.get(i + 1) - 1];

		}
		return distance;
	}
	
	public static double disBetwennTwoCities(City city1, City city2) {
		 double earthRadius = 6371000; //meters
		    double dLat = Math.toRadians(city2.getLat()-city1.getLat());
		    double dLng = Math.toRadians(city2.getLon()-city1.getLon());
		    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		               Math.cos(Math.toRadians(city1.getLat())) * Math.cos(Math.toRadians(city2.getLat())) *
		               Math.sin(dLng/2) * Math.sin(dLng/2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    float dist = (float) (earthRadius * c);

		    return dist;
		}

	

	public double[][] calculateDisMatrix(List<City> cities) {
		double[][] distances = new double[cities.size()][cities.size()];
		int i = 0;
		for (City city1 : cities) {
			int j = 0;
			for (City city2 : cities) {
				distances[i][j] += disBetwennTwoCities(city1, city2);
				++j;
			}
			++i;
		}
		return distances;
	}

	public static int calculateTourCity(List<City> tour) {
		int distance = 0;
		for (int i = 0; i<tour.size()-1; ++i) {
		distance += disMatrix[tour.get(i).getCityNum()-1][tour.get(i+1).getCityNum()-1];	
		}
		distance += disMatrix[tour.get(0).getCityNum()-1][tour.get(tour.size()-1).getCityNum()-1];
		return distance;
	}
	
	public static List<Tour> compareTours(List<Tour> list) {
		List<Tour> compared = new ArrayList<Tour>();
		compared.add(list.get(0));
		double dis = list.get(0).getDistance();
		for(int i =1; i<list.size();++i) {
			if(dis!=list.get(i).getDistance()) {
				compared.add(list.get(i));
				dis = list.get(i).getDistance();
			}
		}
		return compared;
		
	}
	public List<Integer> convertTourToList(List<City> list){
		List<Integer> ret = new ArrayList<Integer>();
		for(City c:list) {
			ret.add(c.getCityNum());
		}
		return ret;
	}
	

}
