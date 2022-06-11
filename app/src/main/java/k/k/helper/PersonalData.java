package k.k.helper;

public class PersonalData {
    public String name;
    public String itemClass;
    public String address;
    public Double lat;
    public Double longitude;

    public String getName() {
        return name;
    }

    public String getItemClass() {
        return itemClass;
    }

    public String getAddress() {
        return address;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}