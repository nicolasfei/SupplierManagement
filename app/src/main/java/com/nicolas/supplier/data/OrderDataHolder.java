package com.nicolas.supplier.data;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于在多个Activity中传递大数据
 * <p>
 * activity中采用Intent传递数据时，底层采用缓存的方式，因此缓存必定存在一个最大值，例如1M
 * 如果超过1M，则会出现crash；
 * 为了解决这个大数据在activity之间传输的问题，
 * 自定义了一个静态类DataHolder，并且设置setData和getData方法，而且考虑到极端的情况，
 * 有可能传递的对象的内存是极其大的，所以为了不造成内存泄漏，
 * 我们将要传递的对象构造成一个弱引用保存到该静态类
 */
public class OrderDataHolder {
    private Map<String, WeakReference> dataList = new HashMap<>();

    private static volatile OrderDataHolder holder;

    public static OrderDataHolder getInstance(){
        if (holder==null){
            synchronized (OrderDataHolder.class){
                if (holder==null){
                    holder = new OrderDataHolder();
                }
            }
        }
        return holder;
    }

  private OrderDataHolder() {

    }

    public void setData(String key, Object o) {
        WeakReference value = new WeakReference<>(o);
        this.dataList.put(key, value);
    }

    public Object getData(String key) {
        WeakReference reference = dataList.get(key);
        if (reference!=null){
            Object o = reference.get();
            return o;
        }
        return null;
    }
}
