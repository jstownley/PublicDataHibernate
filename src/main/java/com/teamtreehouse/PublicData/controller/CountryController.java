package com.teamtreehouse.PublicData.controller;

import com.teamtreehouse.PublicData.model.Country;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;

public class CountryController {

    // Hold a reusable reference to a Session Factory (since we need only one)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public CountryController() {}

    public void addCounty(Country country) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Do stuff
        session.beginTransaction();
        session.persist(country);
        session.getTransaction().commit();

        // Close session
        session.close();
    }

    public Country findCountryByName(String name) {
        // Find all the countries, then filter by name
        List<Country> countries = findAllCountries();
        return countries.stream()
            .filter(c->c.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    public Country findCountryByCode(String code) {
        // Find all the countries, then filter by name
        List<Country> countries = findAllCountries();
        return countries.stream()
            .filter(c->c.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public List<Country> findAllCountries() {
        // Open session
        Session session = sessionFactory.openSession();

        // Get the countries
        Criteria criteria = session.createCriteria(Country.class);
        List<Country> countries = criteria.list();

        // Close session
        session.close();

        return countries;
    }

    public List<String> findAllCountryCodes() {
        List<Country> countries = findAllCountries();
        List<String> codes = new ArrayList<>();
        for (Country country : countries) {
            codes.add(country.getCode());
        }
        return codes;
    }

    public void updateCountry(Country country) {
        // Open session
        Session session = sessionFactory.openSession();

        // Do stuff
        session.beginTransaction();
        session.update(country);
        session.getTransaction().commit();

        // Close session
        session.close();
    }

    public void deleteCountryByCode(String code) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Do stuff
        session.beginTransaction();
        Country country = findCountryByCode(code);
        session.delete(country);
        session.getTransaction().commit();

        // Close session
        session.close();
    }
}
