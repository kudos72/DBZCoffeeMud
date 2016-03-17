package com.planet_ink.coffee_mud.Races;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;

import java.util.List;
import java.util.Vector;

/*
   Copyright 2004-2014 Bo Zimmerman

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
public class Lich extends Skeleton
{
	@Override public String ID(){	return "Lich"; }
	@Override public String name(){ return "Lich"; }

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_ENDURANCE,affectableStats.getStat(CharStats.STAT_ENDURANCE)-4);
		affectableStats.setStat(CharStats.STAT_QUICKNESS,affectableStats.getStat(CharStats.STAT_QUICKNESS)+6);
	}
	@Override
	public List<RawMaterial> myResources()
	{
		return resources;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(ticking instanceof MOB))
			return super.tick(ticking,tickID);
		final MOB myChar=(MOB)ticking;
		if((tickID==Tickable.TICKID_MOB)&&(CMLib.dice().rollPercentage()<10))
		{
			final Ability A=CMClass.getAbility("Spell_Fear");
			if(A!=null)
			{
				A.setMiscText("WEAK");
				A.invoke(myChar,null,true,0);
			}
		}
		return super.tick(myChar,tickID);
	}
}
