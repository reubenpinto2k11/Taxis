package com.pranavtalking.devserver;


/*
 * Because an EntityManagerFactory instance takes time to initialize, this wrapper class is used with a static instance for all calls
 * 
 * 
 * 
 * */
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMF {
    private static final EntityManagerFactory emfInstance =
        Persistence.createEntityManagerFactory("transactions-optional");

    private EMF() {}

    public static EntityManagerFactory get() {
        return emfInstance;
    }
}