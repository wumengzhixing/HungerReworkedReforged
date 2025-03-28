package net.mcbbs.uid1525632.hungerreworkedreforged.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.mcbbs.uid1525632.hungerreworkedreforged.HungerReworked.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeRegistration
{
	public static final Attribute EXTRA_STOMACH = new RangedAttribute("generic.hunger_reworked_reforged.extra_stomach", 0.0D, -1.0D, 300.0D);

	@SubscribeEvent
	public static void init(RegistryEvent.Register<Attribute> event)
	{
		EXTRA_STOMACH.setSyncable(true);
		EXTRA_STOMACH.setRegistryName(new ResourceLocation(MOD_ID, "extra_stomach"));
		event.getRegistry().register(EXTRA_STOMACH);
	}

	@SubscribeEvent
	public static void modifyDefaultAttributes(EntityAttributeModificationEvent event)
	{
		event.add(EntityType.PLAYER, AttributeRegistration.EXTRA_STOMACH, 0.0D);
	}
}
