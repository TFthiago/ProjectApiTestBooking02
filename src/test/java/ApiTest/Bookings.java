package ApiTest;

public class Bookings {
    public String firstname;
    public String lastname;
    public int totalprice;
    public boolean depositpaid;

    public static class Bookingdates{

        Bookingdates(String checkin, String checkout){
            this.checkin = checkin;
            this.checkout = checkout;
        }

        public final String checkin;
        public final String checkout;
    }
    public Bookingdates bookingdates;
    public String additionalneeds;
}
