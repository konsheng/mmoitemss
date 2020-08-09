package net.Indyuce.mmoitems.comp.mythicmobs.stat;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.mmogroup.mmolib.version.VersionMaterial;

public class FactionDamage extends DoubleStat {

    public FactionDamage(String faction) {
        super("FACTION_DAMAGE_" + faction.toUpperCase(), VersionMaterial.RED_DYE.toItem(), faction + " Faction Damage", new String[] { "Deals additional damage to mobs", "from the " + faction + " faction in %." }, new String[] { "!block", "all" });
    }
}

