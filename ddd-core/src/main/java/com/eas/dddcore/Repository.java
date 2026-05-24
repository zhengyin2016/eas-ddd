package com.eas.dddcore;

/**
 * 资源库标记接口
 * 所有资源库接口均继承此接口，以表达 DDD 中资源库的语义。
 *
 * @param <T>  聚合根类型
 * @param <ID> 标识类型
 */
public interface Repository<T extends AggregateRoot<ID>, ID> {
}
