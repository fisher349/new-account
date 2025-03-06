package com.fisher.newaccountpoc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fisher.newaccountpoc.entity.Teller;
import com.fisher.newaccountpoc.mapper.TellerMapper;
import com.fisher.newaccountpoc.service.TellerService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TellerServiceImpl extends ServiceImpl<TellerMapper, Teller> implements TellerService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Teller findById(String id) {
        QueryWrapper<Teller> queryWrapper = Wrappers.query();
        queryWrapper.eq("employee_id", id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean createTeller(Teller teller) {
        teller.setPasswordHash(passwordEncoder.encode(teller.getPasswordHash()));
        return this.save(teller);
    }


}
