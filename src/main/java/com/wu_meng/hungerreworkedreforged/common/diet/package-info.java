/**
 * Diet 模组兼容层喵~
 * <br/>
 * 该包提供与 Diet 模组的兼容层实现，使用代理模式实现可选的 Diet 模组集成喵~
 * <br/>
 * 主要功能：
 * <br/>
 *   1. 提供抽象的 {@link com.wu_meng.hungerreworkedreforged.common.diet.ProxyDiet} 接口，封装 Diet 模组的核心功能喵~
 * <br/>
 *   2. 实现 {@link com.wu_meng.hungerreworkedreforged.common.diet.PresentDiet}，当 Diet 模组存在时提供完整功能喵~
 * <br/>
 *   3. 提供 {@link com.wu_meng.hungerreworkedreforged.common.diet.ProxyIDietTracker} 和 {@link com.wu_meng.hungerreworkedreforged.common.diet.ProxyIDietGroup} 接口，作为 Diet API 的代理喵~
 * <br/>
 *   4. 如果 Diet 模组不存在，使用空实现，不影响核心功能喵~
 * <br/>
 * <br/>
 * 设计模式：代理模式（Proxy Pattern）喵~
 * <br/>
 * <br/>
 * 注意事项：
 * <br/>
 *   - 所有 Diet 相关的类型必须通过代理接口访问，避免直接引用 Diet API 导致类加载错误喵~
 * <br/>
 *   - 该包中的所有实现类都应该是线程安全的，因为它们会在服务端和客户端线程中被访问喵~
 * <br/>
 * <br/>
 * @see com.wu_meng.hungerreworkedreforged.common.diet.ProxyDiet
 * @see com.wu_meng.hungerreworkedreforged.common.diet.PresentDiet
 */
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
package com.wu_meng.hungerreworkedreforged.common.diet;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;