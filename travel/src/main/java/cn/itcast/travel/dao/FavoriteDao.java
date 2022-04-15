package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Favorite;

public interface FavoriteDao {
    //根据rid和uid查询收藏信息
    Favorite findByRidAndUid(int rid , int uid );
    //根据rid查询收藏数量
    int findCountByRid(int sid);

    void add(int rid, int uid);
}
