package net.Indyuce.mmoitems.stat.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.mmogroup.mmolib.MMOLib;
import net.mmogroup.mmolib.api.item.ItemTag;
import net.mmogroup.mmolib.api.util.AltChar;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StringListStat extends ItemStat {
    public StringListStat(String id, ItemStack item, String name, String[] lore, String[] types, Material... materials) {
        super(id, item, name, lore, types, materials);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StringListData whenInitialized(Object object) {
        Validate.isTrue(object instanceof List<?>, "Must specify a string list");
        return new StringListData((List<String>) object);
    }

    @Override
    public void whenApplied(ItemStackBuilder item, StatData data) {
        JsonArray array = new JsonArray();
        ((StringListData) data).getList().forEach(line -> array.add(line));
        item.addItemTag(new ItemTag(getPath(), array.toString()));
    }

    @Override
    public void whenClicked(EditionInventory inv, InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.PICKUP_ALL)
            new StatEdition(inv, this).enable("Write in the chat the lore line you want to add.");

        if (event.getAction() == InventoryAction.PICKUP_HALF && inv.getEditedSection().contains(getPath())) {
            List<String> list = inv.getEditedSection().getStringList(getPath());
            if (list.isEmpty())
                return;

            String last = list.get(list.size() - 1);
            list.remove(last);
            inv.getEditedSection().set(getPath(), list.isEmpty() ? null : list);
            inv.registerTemplateEdition();
            inv.getPlayer()
                    .sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + MMOLib.plugin.parseColors(last) + ChatColor.GRAY + "'.");
        }
    }

    @Override
    public void whenInput(EditionInventory inv, String message, Object... info) {
        List<String> list = inv.getEditedSection().contains(getPath()) ? inv.getEditedSection().getStringList(getPath()) : new ArrayList<>();
        list.add(message);
        inv.getEditedSection().set(getPath(), list);
        inv.registerTemplateEdition();
        inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + getName() + " Stat successfully added.");
    }

    @Override
    public void whenLoaded(ReadMMOItem mmoitem) {
        if (mmoitem.getNBT().hasTag(getNBTPath()))
            mmoitem.setData(this, new StringListData(
                    new JsonParser().parse(mmoitem.getNBT().getString(getNBTPath())).getAsJsonArray()));
    }

    @Override
    public void whenDisplayed(List<String> lore, Optional<RandomStatData> optional) {
        if (optional.isPresent()) {
            lore.add(ChatColor.GRAY + "Current Value:");
            StringListData data = (StringListData) optional.get();
            data.getList().forEach(element -> lore.add(ChatColor.GRAY + MMOLib.plugin.parseColors(element)));

        } else
            lore.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");

        lore.add("");
        lore.add(ChatColor.YELLOW + AltChar.listDash + " Click to add a permission.");
        lore.add(ChatColor.YELLOW + AltChar.listDash + " Right click to remove the last permission.");
    }
}