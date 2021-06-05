/*
 * Copyright (c) 2016-present. Drakeet Xu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drakeet.multitype

/**
 * A TypePool implemented by an ArrayList.
 * 布局类型集的封装
 *
 * @author Drakeet Xu
 */
open class MutableTypes constructor(
    open val initialCapacity: Int = 0,
    open val types: MutableList<Type<*>> = ArrayList(initialCapacity) // 存储所有的布局类型
) : Types {

    /**
     * 返回布局类型的总数
     */
    override val size: Int get() = types.size

    /**
     * 注册布局类型[Type]
     */
    override fun <T> register(type: Type<T>) {
        types.add(type)
    }

    /**
     * 反注册布局类型
     * @param clazz 数据bean的class
     * @return 操作是否成功
     */
    override fun unregister(clazz: Class<*>): Boolean {
        // 删除所有与参数clazz相关的布局类型
        return types.removeAll {
            // 这里it是Type对象，it.class是Type的属性Class<out T>对象，因此比较的是数据bean的class，如果相同则删除此Type
            it.clazz == clazz
        }
    }

    /**
     * 根据索引获取布局类型Type
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> getType(index: Int): Type<T> = types[index] as Type<T>

    /**
     * 返回布局类型在第一次出现在布局集合[types]的位置
     * @param clazz:数据bean的Class对象，Class对象是唯一的，相同类型的数据bean，它们的Class对象是同一个
     */
    override fun firstIndexOf(clazz: Class<*>): Int {
        // 从布局集合types中搜索第一次出现对应Type的索引，可以这么说Type出现的索引完全取决于数据bean的Class对象，这也是此框架的特色，按数据bean来类型来实现多布局
        val index = types.indexOfFirst {
            it.clazz == clazz  // 这里对比的是Type内的数据bean的class属性！！注意：这里比较的是Class对象，由于Class对象是唯一的，因此同类型的数据bean的Class对比是true
        }
        if (index != -1) {
            return index
        }
        return types.indexOfFirst { it.clazz.isAssignableFrom(clazz) }
    }
}
