package com.example.bookmanagement.config;

public interface SecurityService {

    boolean isAuthenticated();

    void autoLogin(String username, String password);

}