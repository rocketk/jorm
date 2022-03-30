package com.github.rocketk.jorm;

/**
 * @author pengyu
 * @date 2021/12/17
 */
public interface RawQuery<T> {
    RawQuery<T> sql(String sql);
    RawQuery<T> args(Object ...args);
    RawQuery<T> first();
    RawQuery<T> find();

}
