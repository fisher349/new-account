package com.fisher.newaccountpoc.service;

import com.fisher.newaccountpoc.entity.Teller;

public interface TellerService {
    Teller findById(String id);
    boolean validatePassword(String rawPassword, String encodedPassword);

    boolean createTeller(Teller teller);
}
