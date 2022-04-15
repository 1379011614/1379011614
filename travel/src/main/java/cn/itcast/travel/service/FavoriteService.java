package cn.itcast.travel.service;

public interface FavoriteService {
    //判断是否收藏
    boolean isFavorite(String rid , int uid );

    //添加收藏信息
    void add(String rid, int uid);
}

