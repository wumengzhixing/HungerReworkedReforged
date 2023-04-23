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
package net.mcbbs.uid1525632.hungerreworkedreforged.integration.diet;

import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.common.group.DietGroups;

public class PresentDiet extends ProxyDiet
{	
	@Override
	public LazyOptional<ProxyIDietTracker> get(Player player)
	{
		return DietCapability.get(player).lazyMap((diet) -> new PresentIDietTracker(diet));
	}
	
	@Override
	public Set<ProxyIDietGroup> getGroups()
	{
		return DietGroups.get().stream().map((group) -> new PresentIDietGroup(group)).collect(Collectors.toSet());
	}
}
