package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Seller;

public interface SellerDao {
    //根据route_seller的id查询
    Seller findById(int id);
}
