package opendata.data;


import com.google.gson.annotations.SerializedName;


public class CulturalEvents
{
    private String name;
    private String type;
    private Feature[] features;

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public Feature[] getFeatures()
    {
        return features;
    }

    public static class Feature
    {
        private String type;
        private Geometry geometry;
        private Properties properties;

        public String getType()
        {
            return type;
        }

        public Geometry getGeometry()
        {
            return geometry;
        }

        public Properties getProperties()
        {
            return properties;
        }

        public static class Geometry
        {
            private String type;
//            private double[][] coordinates;

            public String getType()
            {
                return type;
            }

            /*
            public double[][] getCoordinates()
            {
                return coordinates;
            }
            */
        }

        public static class Properties
        {
            @SerializedName("email") private String email;
            @SerializedName("phone") private String phone;
            @SerializedName("Name") private String name;
            @SerializedName("Address") private String address;
            @SerializedName("Descriptn") private String description;
            @SerializedName("id") private String id;
            @SerializedName("category") private int category;
            @SerializedName("company") private String company;
            @SerializedName("city") private String city;
            @SerializedName("prov") private String province;
            @SerializedName("pcode") private String postalCode;
            @SerializedName("website") private String website;
            @SerializedName("X") private String x;
            @SerializedName("Y") private String y;

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getCategory() {
                return category;
            }

            public void setCategory(int category) {
                this.category = category;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getPostalCode() {
                return postalCode;
            }

            public void setPostalCode(String postalCode) {
                this.postalCode = postalCode;
            }

            public String getWebsite() {
                return website;
            }

            public void setWebsite(String website) {
                this.website = website;
            }

            public String getX()
            {
                return x;
            }

            public void setX(String x)
            {
                this.x = x;
            }

            public String getY()
            {
                return y;
            }

            public void setY(String y)
            {
                this.y = y;
            }
        }
    }
}
