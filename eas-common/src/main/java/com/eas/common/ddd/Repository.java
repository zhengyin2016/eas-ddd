package com.eas.common.ddd;

/**
 * 资源库标记接口
 * <p>
 * 所有资源库接口应继承此接口，遵循DDD仓储模式。
 * 资源库负责聚合根的持久化和查询，是领域层与基础设施层之间的抽象。
 * </p>
 *
 * @param <T>  聚合根类型
 * @param <ID> 聚合根ID类型
 */
public interface Repository<T extends AggregateRoot<?>, ID> {

    /**
     * 保存聚合根
     * <p>
     * 新增和更新都使用此方法。
     * 如果聚合根有未发布的领域事件，保存后应发布事件。
     * </p>
     *
     * @param aggregate 聚合根
     * @return 保存后的聚合根
     */
    T save(T aggregate);

    /**
     * 根据ID查找聚合根
     *
     * @param id 聚合根ID
     * @return 聚合根，不存在返回null
     */
    T findById(ID id);

    /**
     * 根据ID删除聚合根
     *
     * @param id 聚合根ID
     */
    void deleteById(ID id);

    /**
     * 检查聚合根是否存在
     *
     * @param id 聚合根ID
     * @return true如果存在
     */
    default boolean existsById(ID id) {
        return findById(id) != null;
    }
}
