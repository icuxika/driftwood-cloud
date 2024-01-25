package com.icuxika.framework.object.base.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Pageable;

public class PageableUtil {

    /**
     * 将 Pageable 中的排序参数应用到 QueryWrapper 中
     */
    public static <T> LambdaQueryWrapper<T> sort(Pageable pageable) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        pageable.getSort().stream().forEach(order -> {
            if (order.isAscending()) {
                wrapper.orderByAsc(order.getProperty());
            } else {
                wrapper.orderByDesc(order.getProperty());
            }
        });
        return wrapper.lambda();
    }

    /**
     * 根据 Pageable 参数构造一个能被 MyBatis-Plus 使用的分页对象并应用 Pageable 中的排序参数，对于分页查询使用了此函数后，不需要同时使用上面的 sort 函数
     */
    public static <T> Page<T> page(Pageable pageable) {
        Page<T> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        page.addOrder(pageable.getSort().stream().map(order -> order.isAscending() ? OrderItem.asc(order.getProperty()) : OrderItem.desc(order.getProperty())).toList());
        return page;
    }
}
