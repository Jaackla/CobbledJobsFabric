package io.github.adainish.cobbledjobsfabric.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.item.ItemStack;


import java.lang.reflect.Modifier;

public class Adapters
{
    public static Gson PRETTY_MAIN_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();
}
