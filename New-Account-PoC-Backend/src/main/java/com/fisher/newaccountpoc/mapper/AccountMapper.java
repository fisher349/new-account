package com.fisher.newaccountpoc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fisher.newaccountpoc.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
