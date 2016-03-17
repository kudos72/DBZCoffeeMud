package com.planet_ink.coffee_mud.Commands;
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

@SuppressWarnings("rawtypes")
public class AutoGuard extends StdCommand
{
	public AutoGuard(){}

	private final String[] access=I(new String[]{"AUTOGUARD","GUARD"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((!mob.isAttribute(MOB.Attrib.AUTOGUARD))
		   ||((commands.size()>0)&&(((String)commands.get(0)).toUpperCase().startsWith("G"))))
		{
			mob.setAttribute(MOB.Attrib.AUTOGUARD,true);
			mob.tell(L("You are now on guard. You will no longer follow group leaders."));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,null,L("I am now on guard."),false,false);
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOGUARD,false);
			mob.tell(L("You are no longer on guard.  You will now follow group leaders."));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,null,L("I will now follow my group leader."),false,false);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

