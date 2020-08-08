package com.rcloud.server.sealtalk.service;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public abstract class AbstractBaseService<T, PrimaryKey> implements BaseService<T, PrimaryKey> {

    protected abstract Mapper<T> getMapper();

    public T getByPrimaryKey(PrimaryKey id) {
        return getMapper().selectByPrimaryKey(id);

    }

    @Override
    public T getOne(T t) {
        return getMapper().selectOne(t);
    }

    @Override
    public List<T> get(T t) {
        return getMapper().select(t);
    }

    @Override
    public T getOneByExample(Example example) {
        return getMapper().selectOneByExample(example);
    }

    @Override
    public List<T> getByExample(Example example) {
        return getMapper().selectByExample(example);
    }

    @Override
    public int saveSelective(T t) {
        return getMapper().insertSelective(t);

    }

    @Override
    public int save(T t) {
        return getMapper().insert(t);
    }

    @Override
    public int updateByPrimaryKeySelective(T t) {
        return getMapper().updateByPrimaryKeySelective(t);
    }

    @Override
    public int updateByPrimaryKey(T t) {
        return getMapper().updateByPrimaryKey(t);
    }

    @Override
    public int updateByExampleSelective(T t, Example example) {
        return getMapper().updateByExampleSelective(t, example);
    }

    @Override
    public int updateByExample(T t, Example example) {
        return getMapper().updateByExample(t, example);
    }

    @Override
    public int deleteByPrimaryKey(PrimaryKey id) {
        return getMapper().deleteByPrimaryKey(id);
    }

    @Override
    public int delete(T t) {
        return getMapper().delete(t);
    }

    @Override
    public int deleteByExample(Example example) {
        return getMapper().deleteByExample(example);
    }

}
