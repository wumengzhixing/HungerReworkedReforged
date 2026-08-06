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

/**
 * Diet 食物组代理接口喵~
 * <br/>
 * 该接口作为 {@link net.appleseed.appleseed.api.type.IDietGroup} 的代理，用于在不直接依赖 Diet 模组的情况下访问食物组信息喵~
 * <br/>
 * 通过使用代理模式，当 Diet 模组不存在时，可以提供空实现或默认行为，避免类加载错误喵~
 * <br/>
 * <br/>
 * 实现类：
 * <br/>
 *   - {@link PresentIDietGroup}：当 Diet 模组存在时，委托给真实的 {@link net.appleseed.appleseed.api.type.IDietGroup} 实现喵~
 * <br/>
 * <br/>
 * @see PresentIDietGroup
 * @see net.appleseed.appleseed.api.type.IDietGroup
 * @author liudongyu
 */
public interface ProxyIDietGroup {
	/**
	 * 获取食物组的名称喵~
	 * <br/>
	 * 该名称通常对应 Diet 模组中定义的食物组标识符，例如 "proteins"、"fruits"、"vegetables" 等喵~
	 * <br/>
	 * <br/>
	 * @return 食物组名称，不会为 {@code null} 喵~
	 */
	String getName();
}
