packbirthDate it.marcosoft.ticketwave.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

import com.google.firebase.database.Exclude;


public class User implements Parcelable {
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String email;

    private String idToken;

    public User(){

    }

    public User(String firstName, String email, String idToken) {
        this.firstName = firstName;
        this.email = email;
        this.idToken = idToken;
        this.lastName=null;
        this.birthDate=null;

    }

    public User(String firstName, String email, String lastName, Date birthDate, String idToken) {
        this.firstName = firstName;
        this.lastName=lastName;
        this.birthDate=birthDate;
        this.email = email;
        this.idToken = idToken;
    }

    public String getfirstName() {
        return firstName;
    }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getlastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getbirthDate() {
        return birthDate;
    }

    public void setbirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Exclude
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + (lastName != null ? lastName : "N/A") + '\'' +
                ", email='" + email + '\'' +
                ", idToken='" + idToken + '\'' +
                ", birthDate=" + (birthDate != null ? birthDate.toString() : "N/A") +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.email);
        dest.writeString(this.idToken);
    }

    public void readFromParcel(Parcel source) {
        this.firstName = source.readString();
        this.email = source.readString();
        this.idToken = source.readString();


    }

    protected User(Parcel in) {
        this.firstName = in.readString();
        this.email = in.readString();
        this.idToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
