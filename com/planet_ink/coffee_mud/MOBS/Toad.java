package com.planet_ink.coffee_mud.MOBS;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/*
   Copyright 2000-2014 Bo Zimmerman

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
public class Toad extends StdMOB
{
	@Override public String ID(){return "Toad";}
	public Toad()
	{
		super();
		username="a toad";
		setDescription("A stubby little dry amphibian with short legs.");
		setDisplayText("A toad is squatting here.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);

		basePhyStats().setDamage(6);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		baseCharStats().setMyRace(CMClass.getRace("Frog"));
		baseCharStats().getMyRace().startRacing(this,false);
		baseState.setHitPoints(CMLib.dice().rollHP(basePhyStats.level(), 20));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
