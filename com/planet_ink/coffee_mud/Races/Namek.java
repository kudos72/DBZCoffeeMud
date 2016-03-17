package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Libraries.interfaces.DiceLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;

/*
   Copyright 2001-2014 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
@SuppressWarnings({"unchecked","rawtypes"})
public class Namek extends StdRace
{
	@Override public String ID(){	return "Namek"; }
	@Override public String name(){ return "Namek"; }
	@Override public int shortestMale(){return 60;}
	@Override public int shortestFemale(){return 48;}
	@Override public int heightVariance(){return 30;}
	@Override public int lightestWeight(){return 250;}
	@Override public int weightVariance(){return 300;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Namek";}
	private final String[]culturalAbilityNames={"Namekian"};
	private final int[]culturalAbilityProficiencies={100,50};
	@Override public String[] culturalAbilityNames(){return culturalAbilityNames;}
	@Override public int[] culturalAbilityProficiencies(){return culturalAbilityProficiencies;}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={2 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,1,5,40,125,188,250,270,290};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected, affectableStats);
		//affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.);
	}
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		DiceLibrary die = CMLib.dice();
		super.affectCharStats(affectedMOB, affectableStats);
		int currentStat;
		affectableStats.setStat(CharStats.STAT_GENDER, 'M');
		currentStat = die.roll(4,6,0);
		affectableStats.setStat(CharStats.STAT_INTELLIGENCE,  currentStat);
		affectableStats.setStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ,  currentStat);
		currentStat = die.roll(5,6,5);
		affectableStats.setStat(CharStats.STAT_SPIRIT,currentStat);
		affectableStats.setStat(CharStats.STAT_MAX_SPIRIT_ADJ, currentStat);
		currentStat = die.roll(5,6,5);
		affectableStats.setStat(CharStats.STAT_DEXTERITY, currentStat);
		affectableStats.setStat(CharStats.STAT_MAX_DEXTERITY_ADJ, currentStat);
		currentStat = die.roll(6,6,5);
		affectableStats.setStat(CharStats.STAT_ENDURANCE,currentStat);
		affectableStats.setStat(CharStats.STAT_MAX_ENDURANCE_ADJ,currentStat);
		currentStat = die.roll(5,6,0);
		affectableStats.setStat(CharStats.STAT_QUICKNESS,die.roll(5,6,0));
		affectableStats.setStat(CharStats.STAT_MAX_QUICKNESS_ADJ,currentStat);
		currentStat = die.roll(5,6,5);
		affectableStats.setStat(CharStats.STAT_STRENGTH, die.roll(5, 6, 5));
		affectableStats.setStat(CharStats.STAT_MAX_STRENGTH_ADJ,currentStat);
	}
	@Override
	public List<Item> outfit(MOB myChar)
	{
		if(outfitChoices==null)
		{
			outfitChoices=new Vector();
			// Have to, since it requires use of special constructor
			final Armor s1=CMClass.getArmor("GenShirt");
			s1.setName(L("a grey work tunic"));
			s1.setDisplayText(L("a grey work tunic has been left here."));
			s1.setDescription(L("There are lots of little loops and folks for hanging tools about it."));
			s1.text();
			outfitChoices.add(s1);

			final Armor s2=CMClass.getArmor("GenShoes");
			s2.setName(L("a pair of hefty work boots"));
			s2.setDisplayText(L("some hefty work boots have been left here."));
			s2.setDescription(L("Thick and well worn boots with very tough souls."));
			s2.text();
			outfitChoices.add(s2);

			final Armor p1=CMClass.getArmor("GenPants");
			p1.setName(L("some hefty work pants"));
			p1.setDisplayText(L("some hefty work pants have been left here."));
			p1.setDescription(L("There are lots of little loops and folks for hanging tools about it."));
			p1.text();
			outfitChoices.add(p1);

			final Armor s3=CMClass.getArmor("GenBelt");
			outfitChoices.add(s3);
		}
		return outfitChoices;
	}
	@Override
	public Weapon myNaturalWeapon()
	{ return funHumanoidWeapon();	}

	@Override
	public String healthText(MOB viewer, MOB mob)
	{
		final double pct=(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return L("^r@x1^r is nearly dead!^N",mob.name(viewer));
		else
		if(pct<.20)
			return L("^r@x1^r is covered in blood.^N",mob.name(viewer));
		else
		if(pct<.30)
			return L("^r@x1^r is bleeding from cuts and gashes.^N",mob.name(viewer));
		else
		if(pct<.40)
			return L("^y@x1^y has numerous wounds.^N",mob.name(viewer));
		else
		if(pct<.50)
			return L("^y@x1^y has some wounds.^N",mob.name(viewer));
		else
		if(pct<.60)
			return L("^p@x1^p has a few cuts.^N",mob.name(viewer));
		else
		if(pct<.70)
			return L("^p@x1^p is cut.^N",mob.name(viewer));
		else
		if(pct<.80)
			return L("^g@x1^g has some bruises.^N",mob.name(viewer));
		else
		if(pct<.90)
			return L("^g@x1^g is very winded.^N",mob.name(viewer));
		else
		if(pct<.99)
			return L("^g@x1^g is slightly winded.^N",mob.name(viewer));
		else
			return L("^c@x1^c is in perfect health.^N",mob.name(viewer));
	}
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}
