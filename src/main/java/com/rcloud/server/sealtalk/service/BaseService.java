package com.rcloud.server.sealtalk.service;

import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public interface BaseService<T, PrimaryKey> {

    /**
     * 根据主键查询实体
     *
     * @param id 主键ID
     * @return
     */
    T getByPrimaryKey(PrimaryKey id);

    /**
     * 根据实体对象查询一条数据
     *
     * @param t 实体对象
     * @return
     */
    T getOne(T t);

    /**
     * 根据实体对象查询多条数据
     *
     * @param t 实体对象
     * @return
     */
    List<T> get(T t);

    /**
     * 根据example查询一条数据
     *
     * @param example
     * @return
     */
    T getOneByExample(Example example);

    /**
     * 根据example查询多条数据
     *
     * @param example
     * @return
     */
    List<T> getByExample(Example example);

    /**
     * 根据实体插入数据，生成的insert sql不包括属性为null对应的字段
     *
     * @param t 实体对象
     * @return 影响记录条数
     */
    int saveSelective(T t);

    /**
     * 根据实体插入数据,生成的insert sql包括所有属性字段
     *
     * @param t 实体对象
     * @return 影响记录条数
     */
    int save(T t);

    /**
     * 根据主键选择更新，生成的update sql不包括属性为null对应的字段
     *
     * @param t 实体对象
     * @return 影响记录条数
     */
    int updateByPrimaryKeySelective(T t);

    /**
     * 根据主键全部字段更新，生成的update sql包括所有属性对应的字段
     *
     * @param t 实体对象
     * @return 影响记录数
     */
    int updateByPrimaryKey(T t);

    /**
     * 根据example选择更新，生成的update sql不包括属性为null对应的字段
     *
     * @param t
     * @param example
     * @return
     */
    int updateByExampleSelective(T t, Example example);

    /**
     * 据example全部字段更新，生成的update sql包括所有属性对应的字段
     *
     * @param t
     * @param example
     * @return
     */
    int updateByExample(T t, Example example);


    /**
     * 根据主键删除数据
     *
     * @param id 主键ID
     * @return 影响记录条数
     */
    int deleteByPrimaryKey(PrimaryKey id);

    /**
     * 根据实体对象删除数据
     *
     * @param t 实体对象
     * @return 影响记录数
     */
    int delete(T t);

    /**
     * 根据example删除数据
     *
     * @param example
     * @return
     */
    int deleteByExample(Example example);
}
