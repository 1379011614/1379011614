package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao dao = new CategoryDaoImpl();

    @Override
    public List<Category> findAll() {
        //使用Redis进行优化
        //获取jedis客户端
        Jedis jedis = JedisUtil.getJedis();
        //使用sortedset排序查询
//        Set<String> categorys = jedis.zrange("category", 0, -1);
        //使用sortedset排序查询cid和cname
        Set<Tuple> categorys = jedis.zrangeWithScores("category", 0, -1);
        List<Category> cs = null;
        //判断查询的集合是否为空
        if (categorys == null || categorys.size() == 0) {
            //为空,则从数据库中查询,并将数据存入Redis
            //查询数据库
            System.out.println("从数据库中查询....");
            cs = dao.findAll();
            //将数据存入Redis
            for (int i = 0; i < cs.size(); i++) {
                jedis.zadd("category", cs.get(i).getCid(), cs.get(i).getCname());
            }
        } else {
            System.out.println("从Redis中查询.....");
            //不为空,将set的数据存入list,保证数据结构的一致性
            cs = new ArrayList<Category>();
            for (Tuple tuple : categorys) {
                Category category = new Category();
                category.setCname(tuple.getElement());
                category.setCid((int) tuple.getScore());
                cs.add(category);
            }
        }
        return cs;
    }
}
