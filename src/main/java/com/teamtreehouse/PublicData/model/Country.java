package com.teamtreehouse.PublicData.model;

import javax.persistence.*;

@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private float internetUsers;

    @Column
    private float adultLiteracyRate;

    // Default constructor for JPA
    public Country() {}

    public Country(CountryBuilder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.internetUsers = builder.internetUsers;
        this.adultLiteracyRate = builder.adultLiteracyRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(float internetUsers) {
        this.internetUsers = internetUsers;
    }

    public float getAdultLiteracyRate() {
        return adultLiteracyRate;
    }

    public void setAdultLiteracyRate(float adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
    }

    public static class CountryBuilder {
        private String code;
        private String name;
        private float internetUsers;
        private float adultLiteracyRate;

        public CountryBuilder(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public CountryBuilder withInternetUsers(float internetUsers) {
            this.internetUsers = internetUsers;
            return this;
        }

        public CountryBuilder withAdultLiteracyRate(float adultLiteracyRate) {
            this.adultLiteracyRate = adultLiteracyRate;
            return this;
        }

        public Country build() {
            return new Country(this);
        }
    }
}
