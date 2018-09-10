package org.opencb.biodata.models.commons;

public class Analyst {

    private String name;
    private String email;
    private String company;

    public Analyst() {
    }

    public Analyst(String name, String email, String company) {
        this.name = name;
        this.email = email;
        this.company = company;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Analyst{");
        sb.append("author='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", company='").append(company).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public Analyst setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Analyst setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getCompany() {
        return company;
    }

    public Analyst setCompany(String company) {
        this.company = company;
        return this;
    }
}
