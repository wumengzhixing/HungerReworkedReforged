/*
MIT License

        Copyright (c) 2022 Adneths

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
 */
package com.wu_meng.hungerreworkedreforged.common.diet;

import net.appleseed.appleseed.api.type.IDietGroup;

/**
 * Diet 食物组代理的具体实现类喵~
 * <br/>
 * 当 Diet 模组存在时，该类作为 {@link IDietGroup} 的包装器，将所有方法调用委托给真实的 Diet API 实现喵~
 * <br/>
 * 该类使用装饰器模式（Wrapper Pattern），通过持有一个 {@link IDietGroup} 实例并转发方法调用来实现代理功能喵~
 * <br/>
 * <br/>
 *
 * @param group 被包装的真实 Diet 食物组对象喵~
 * @author liudongyu
 * @see ProxyIDietGroup
 * @see IDietGroup
 * @see PresentDiet
 */
public record PresentIDietGroup(IDietGroup group) implements ProxyIDietGroup {
	/**
	 * {@inheritDoc}
	 * <br/>
	 * 该方法直接委托给底层的 {@link IDietGroup#getName()} 喵~
	 */
	@Override
	public String getName() {
		return this.group.getName();
	}
}
