package entities;

public class City {

	private double lat;
	private double lon;
	private int cityNum;
	private int predecessorNum;
	private int succesorNum;
	
	
	public City(double x, double y, int cityNum) {
		this.lat = x;
		this.lon = y;
		this.cityNum = cityNum;
	}
	public City(int cityNum) {
		this.cityNum = cityNum;
	}
	public City() {
		
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double x) {
		this.lat = x;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double y) {
		this.lon = y;
	}
	public int getCityNum() {
		return cityNum;
	}
	public void setCityNum(int cityNum) {
		this.cityNum = cityNum;
	}
	
	public String toString(){
        return "City"+ this.cityNum + ":" +this.lat + ","+ this.lon;
    }
	public int getSuccesorNum() {
		return succesorNum;
	}
	public void setSuccesorNum(int succesorNum) {
		this.succesorNum = succesorNum;
	}
	public int getPredecessorNum() {
		return predecessorNum;
	}
	public void setPredecessorNum(int predecessorNum) {
		this.predecessorNum = predecessorNum;
	}
	
	public City clone() {
		return new City(this.lat, this.lon,this.cityNum);
		
	}
}
