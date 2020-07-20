package entities;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import utilities.Utilities;


public class Tour {
	@XmlElement(name = "city")
	private List<City> tspTour;
	private double distance = 0;

	public Tour(List<City> tour) {
		this.tspTour = tour;
		if (!(this.tspTour.isEmpty() && distance == 0)) {
			calculateTour();
		}
	}

	public Tour() {

	}

	public List<City> getTspTour() {
		return tspTour;
	}

	public double getDistance() {
		return distance;
	}

	private void calculateTour() {
		distance = Utilities.calculateTourCity(tspTour);

	}

	@Override
	public boolean equals(Object obj) {
		boolean check = true;
		if (!(obj instanceof Tour)) {
			return false;
		}

		Tour tour2 = (Tour) obj;
		if (tour2.getTspTour().size() != this.tspTour.size())
			return false;

		else {
			for (int i = 0; i < tour2.getTspTour().size(); ++i) {
				if (tour2.getTspTour().get(i).getCityNum() != this.getTspTour().get(i).getCityNum()) {
					check = false;
				}
			}
		}
		return check;
	}

}
