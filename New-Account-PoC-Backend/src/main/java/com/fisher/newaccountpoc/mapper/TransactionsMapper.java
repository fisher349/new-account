package com.fisher.newaccountpoc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fisher.newaccountpoc.entity.Transactions;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionsMapper extends BaseMapper<Transactions> {
}
