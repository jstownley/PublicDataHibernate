package com.teamtreehouse.PublicData;

import com.teamtreehouse.PublicData.consoleviewer.CountryViewer;
import com.teamtreehouse.PublicData.controller.CountryController;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class Application {

    public static void main(String[] args) {

        CountryController controller = new CountryController();
        CountryViewer viewer = new CountryViewer(controller);

        viewer.run();

    }

}