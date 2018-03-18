

public class Point {
	public String firstname;
	public String surname;
	public String longitude;
	public String latitude;
	public String address;
	public String telephone;



	public String toString() {
		return longitude + "," + latitude + "," + firstname + " " + surname + ", " + telephone + ", " + address;
	}


	public Point(String firstname, String surname, String longitude, String latitude, String address, String telephone) {
		this.firstname = firstname;
		this.surname = surname;
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
		this.telephone = telephone;
	}

}
