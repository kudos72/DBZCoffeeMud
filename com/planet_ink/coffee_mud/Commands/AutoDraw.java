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
public class AutoDraw extends StdCommand
{
	public AutoDraw(){}

	private final String[] access=I(new String[]{"AUTODRAW"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isAttribute(MOB.Attrib.AUTODRAW))
		{
			mob.setAttribute(MOB.Attrib.AUTODRAW,true);
			mob.tell(L("Auto weapon drawing has been turned on.  You will now draw a weapon when one is handy, and sheath one a few seconds after combat."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTODRAW,false);
			mob.tell(L("Auto weapon drawing has been turned off.  You will no longer draw or sheath your weapon automatically."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

