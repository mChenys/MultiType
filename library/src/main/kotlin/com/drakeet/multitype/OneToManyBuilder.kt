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

import androidx.annotation.CheckResult

/**
 * @author Drakeet Xu
 * 支持一对多，即同一个数据data，对应不同delegate的情况的处理
 */
internal class OneToManyBuilder<T>(
    private val adapter: MultiTypeAdapter,
    private val clazz: Class<T> // 数据类
) : OneToManyFlow<T>, OneToManyEndpoint<T> {

    private var delegates: Array<ItemViewDelegate<T, *>>? = null // delegate集合，外部调用to方法的时候赋值

    @SafeVarargs
    @CheckResult(suggest = "#withLinker(Linker)")
    override fun to(vararg delegates: ItemViewDelegate<T, *>) = apply {
        @Suppress("UNCHECKED_CAST")
        // 只是类型转化，同时返回自身OneToManyEndpoint实现链式调用
        this.delegates = delegates as Array<ItemViewDelegate<T, *>>
    }

    @SafeVarargs
    @CheckResult(suggest = "#withLinker(Linker)")
    override fun to(vararg binders: ItemViewBinder<T, *>) = apply {
        @Suppress("UNCHECKED_CAST")
        // 只是类型转化，同时返回自身OneToManyEndpoint实现链式调用
        this.delegates = binders as Array<ItemViewDelegate<T, *>>
    }

    /**
     * 绑定Linker，实现OneToManyEndpoint接口的方法
     */
    override fun withLinker(linker: Linker<T>) {
        doRegister(linker)
    }

    /**
     * 实现OneToManyEndpoint接口的方法
     */
    override fun withJavaClassLinker(javaClassLinker: JavaClassLinker<T>) {
        // 通过ClassLinkerBridge转化Linker
        withLinker(ClassLinkerBridge.toLinker(javaClassLinker, delegates!!))
    }

    private fun doRegister(linker: Linker<T>) {
        for (delegate in delegates!!) {
            // 开始注册Type，使用自定义Linker
            adapter.register(Type(clazz, delegate, linker))
        }
    }
}
