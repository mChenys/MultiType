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
 * @author Drakeet Xu
 */
internal class ClassLinkerBridge<T> private constructor(
    private val javaClassLinker: JavaClassLinker<T>,
    private val delegates: Array<ItemViewDelegate<T, *>>
) : Linker<T> {

    override fun index(position: Int, item: T): Int {
        // 获取使用者返回的delegate的Class对象
        val indexedClass = javaClassLinker.index(position, item)
        // 从delegate集合中查找此delegate首次出现的位置
        val index = delegates.indexOfFirst {
            it.javaClass == indexedClass  // 之所以对比Class对象，是因为Class对象是全局唯一的
        }
        if (index != -1) return index // 存在则返回，这个index关乎列表item的类型
        throw IndexOutOfBoundsException(
            "The delegates'(${delegates.contentToString()}) you registered do not contain this ${indexedClass.name}."
        )
    }

    companion object {
        fun <T> toLinker(
            javaClassLinker: JavaClassLinker<T>,
            delegates: Array<ItemViewDelegate<T, *>>
        ): Linker<T> {
            // 返回ClassLinkerBridge对象，它是实现了Linker接口的
            return ClassLinkerBridge(javaClassLinker, delegates)
        }
    }
}
