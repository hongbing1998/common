package org.example.common.core.util;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author: 李红兵
 * @date: 2021/3/25 19:53
 * @description: 集合工具类
 */
public class CollectionUtil extends CollectionUtils {
    /**
     * 给集合中的对象根据该对象的某个指定属性创建索引，使用属性值即可获取到该对象本身
     * 键重复时默认以新值替换旧值
     *
     * @param collection 要创建索引的集合
     * @param indexBy    作为索引key的方法引用
     * @param <K>        索引键类型
     * @param <V>        集合中的值类型
     * @return 以对象的指定属性作为key，对象本身为value的HashMap
     */
    public static <K, V> Map<K, V> index(Collection<V> collection, Function<? super V, ? extends K> indexBy) {
        return index(collection, indexBy, (a, b) -> b);
    }

    /**
     * 给集合中的对象根据该对象的某个指定属性创建索引，使用属性值即可获取到该对象本身
     * 键重复时使用指定的合并函数合并
     *
     * @param collection    要创建索引的集合
     * @param indexBy       获取索引键的函数
     * @param mergeFunction 键重复时的合并函数
     * @param <K>           索引键类型
     * @param <V>           集合中的值类型
     * @return 以对象的指定属性作为key，对象本身为value的HashMap
     */
    public static <K, V> Map<K, V> index(Collection<V> collection,
                                         Function<? super V, ? extends K> indexBy,
                                         BinaryOperator<V> mergeFunction) {
        return index(collection, indexBy, mergeFunction, HashMap::new);
    }

    /**
     * 给集合中的对象根据该对象的某个指定属性创建索引，使用属性值即可获取到该对象本身
     * 键重复时使用指定的合并函数合并
     *
     * @param collection    要创建索引的集合
     * @param indexBy       获取索引键的函数
     * @param mergeFunction 键重复时的合并函数
     * @param mapSupplier   Map提供器
     * @param <K>           索引键类型
     * @param <V>           集合中的值类型
     * @return 以对象的指定属性作为key，对象本身为value的指定Map
     */
    public static <K, V, M extends Map<K, V>> Map<K, V> index(Collection<V> collection,
                                                              Function<? super V, ? extends K> indexBy,
                                                              BinaryOperator<V> mergeFunction,
                                                              Supplier<M> mapSupplier) {
        return collection == null ? null : collection.stream().collect(Collectors.toMap(indexBy, v -> v, mergeFunction, mapSupplier));
    }

    /**
     * 将指定集合按照指定映射方式映射成一个List
     * 默认采用ArrayList实现
     * 对于映射后的每一项数据，默认使用Collection类的add方法进行合并
     * 对于多个分片处理结果采用Collection类的addAll方法进行合并得到最终结果
     *
     * @param collection 要映射的集合
     * @param mapper     映射函数
     * @param <O>        原始集合元素类型
     * @param <R>        结果集合元素类型
     * @return 按照映射函数映射后的List
     */
    public static <O, R> List<R> mapToList(Collection<O> collection,
                                           Function<? super O, ? extends R> mapper) {
        return map(collection, mapper, ArrayList::new, Collection::add, Collection::addAll);
    }

    /**
     * 将指定集合按照映射结果为集合的指定映射方式平展映射成一个一维List
     * 默认采用ArrayList实现
     * 对于映射后的每一项数据（是一个集合），默认使用Collection类的addAll方法进行合并
     * 对于多个分片处理结果采用Collection类的addAll方法进行合并得到最终结果
     *
     * @param collection 要映射的集合
     * @param mapper     映射结果为集合的映射函数
     * @param <O>        原始集合元素类型
     * @param <R>        结果集合元素类型
     * @return 按照映射函数映射后的List
     */
    public static <O, R> List<R> flatMapToList(Collection<O> collection,
                                               Function<? super O, ? extends Collection<R>> mapper) {
        return map(collection, mapper, ArrayList::new, Collection::addAll, Collection::addAll);
    }


    /**
     * 将指定集合按照指定映射方式映射成一个Set
     * 默认采用HashSet实现
     * 对于映射后的每一项数据，默认使用Collection类的add方法进行合并
     * 对于多个分片处理结果采用Collection类的addAll方法进行合并得到最终结果
     *
     * @param collection 要映射的集合
     * @param mapper     映射函数
     * @param <O>        原始集合元素类型
     * @param <R>        结果集合元素类型
     * @return 按照映射函数映射后的Set
     */
    public static <O, R> Set<R> mapToSet(Collection<O> collection,
                                         Function<? super O, ? extends R> mapper) {
        return map(collection, mapper, HashSet::new, Collection::add, Collection::addAll);
    }

    /**
     * 将指定集合按照映射结果为集合的指定映射方式平展映射成一个一维Set
     * 默认采用HashSet实现
     * 对于映射后的每一项数据（是一个集合），默认使用Collection类的addAll方法进行合并
     * 对于多个分片处理结果采用Collection类的addAll方法进行合并得到最终结果
     *
     * @param collection 要映射的集合
     * @param mapper     映射结果为集合的映射函数
     * @param <O>        原始集合元素类型
     * @param <R>        结果集合元素类型
     * @return 按照映射函数映射后的Set
     */
    public static <O, R> Set<R> flatMapToSet(Collection<O> collection,
                                             Function<? super O, ? extends Collection<R>> mapper) {
        return map(collection, mapper, HashSet::new, Collection::addAll, Collection::addAll);
    }

    /**
     * 将指定集合按照指定映射方式映射成另一个集合
     *
     * @param collection 要映射的集合
     * @param mapper     映射函数
     * @param supplier   映射结果集合提供器
     * @param <O>        原始集合元素类型
     * @param <T>        原始集合元素转换后的类型
     * @param <R>        最终结果集合类型
     * @return 按照映射函数映射后的集合
     */
    public static <O, T, R> R map(Collection<O> collection,
                                  Function<? super O, ? extends T> mapper,
                                  Supplier<R> supplier,
                                  BiConsumer<R, T> accumulator,
                                  BiConsumer<R, R> combiner) {
        return collection == null ? null : collection.stream().map(mapper).collect(supplier, accumulator, combiner);
    }

    /**
     * 从指定集合中根据索引函数和索引值取得一个值
     *
     * @param collection 要取值的集合
     * @param indexBy    索引函数
     * @param value      索引键值
     * @param <O>        集合元素类型
     * @param <R>        索引类型
     * @return 通过索引value在集合中取到的一个值
     */
    public static <O, R> O oneOf(Collection<O> collection,
                                 Function<? super O, ? extends R> indexBy,
                                 R value) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(value);

        return index(collection, indexBy).get(value);
    }

    /**
     * 从指定集合中根据索引函数和索引值数组取得集合的一个子集
     *
     * @param collection 要取值的集合
     * @param indexBy    索引函数
     * @param values     索引键值集合
     * @param <O>        集合元素类型
     * @param <R>        索引类型
     * @return 通过索引value数组在集合中取到的一个子集
     */
    public static <O, R> Collection<O> subOf(Collection<O> collection,
                                             Function<? super O, ? extends R> indexBy,
                                             R[] values) {
        return subOf(collection, indexBy, Arrays.asList(values));
    }

    /**
     * 从指定集合中根据索引函数和索引值集合取得集合的一个子集
     *
     * @param collection 要取值的集合
     * @param indexBy    索引函数
     * @param values     索引键值集合
     * @param <O>        集合元素类型
     * @param <R>        索引类型
     * @return 通过索引value集合在集合中取到的一个子集
     */
    public static <O, R> Collection<O> subOf(Collection<O> collection,
                                             Function<? super O, ? extends R> indexBy,
                                             Collection<R> values) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(values);

        Set<R> valueSet = new HashSet<>(values);
        Collection<O> subCollection = new ArrayList<>(collection);
        subCollection.removeIf(i -> i == null || !valueSet.contains(indexBy.apply(i)));

        return subCollection;
    }

    /**
     * 更高效地检查指定的集合是否包含指定的元素
     *
     * @param collection 要判断的集合
     * @param element    要判断的一个对象
     * @return 指定集合是否包含指定元素
     * @see CollectionUtils#containsInstance
     */
    public static boolean contains(Collection<?> collection, Object element) {
        return Objects.nonNull(collection) && new HashSet<>(collection).contains(element);
    }

    /**
     * 判断两个数组是否存在相同的值
     *
     * @param array1 数组1
     * @param array2 数组2
     * @param <T>    数组元素类型
     * @return 两个数组是否存在相同的值，即存在交集
     */
    public static <T> boolean containsSame(T[] array1, T[] array2) {
        return containsSame(Arrays.asList(array1), Arrays.asList(array2));
    }

    /**
     * 判断两个集合是否存在相同的值
     *
     * @param collection1 集合1
     * @param collection2 集合2
     * @param <T>         集合元素类型
     * @return 两个集合是否存在相同的值，即存在交集
     */
    public static <T> boolean containsSame(Collection<T> collection1, Collection<T> collection2) {
        if (CollectionUtils.isEmpty(collection1) || CollectionUtils.isEmpty(collection2)) {
            return false;
        }

        Set<T> set = new HashSet<>(collection1);
        for (T item : collection2) {
            if (set.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将多元对象转换为集合，支持以下类型：
     * Collection
     * Map
     * Array
     * Iterator
     * Enumeration
     * 注意：Iterator、Enumeration是一次性的容器，遍历后被清空
     *
     * @param object 多元对象
     * @return 集合
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection convertToCollection(Object object) {
        Objects.requireNonNull(object);
        if (object instanceof Collection) {
            return ((Collection) object);
        }
        if (object instanceof Map) {
            return new ArrayList(((Map) object).values());
        }
        if (object.getClass().isArray()) {
            Collection collection = new ArrayList<>();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                collection.add(Array.get(object, i));
            }
            return collection;
        }
        if (object instanceof Iterator) {
            Collection collection = new ArrayList<>();
            for (Iterator iterator = (Iterator) object; iterator.hasNext(); ) {
                collection.add(iterator.next());
            }
            return collection;
        }
        if (object instanceof Enumeration) {
            Collection collection = new ArrayList<>();
            for (Enumeration enumeration = (Enumeration) object; enumeration.hasMoreElements(); ) {
                collection.add(enumeration.nextElement());
            }
            return collection;
        }
        throw new RuntimeException(String.format("不能将%s类型转换为集合类型", object.getClass()));
    }
}
