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
@SuppressWarnings({"unchecked","rawtypes"})
public class Take extends StdCommand
{
	public Take(){}

	private final String[] access=I(new String[]{"TAKE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.ORDER)
		||CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDMOBS)
		||CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDROOMS))
		{
			if(commands.size()<3)
			{
				mob.tell(L("Take what from whom?"));
				return false;
			}
			commands.remove(0);
			if(commands.size()<2)
			{
				mob.tell(L("From whom should I take the @x1",(String)commands.get(0)));
				return false;
			}

			final MOB victim=mob.location().fetchInhabitant((String)commands.lastElement());
			if((victim==null)||(!CMLib.flags().canBeSeenBy(victim,mob)))
			{
				mob.tell(L("I don't see anyone called @x1 here.",(String)commands.lastElement()));
				return false;
			}
			if((!victim.isMonster())&&(!CMSecurity.isAllowedEverywhere(mob,CMSecurity.SecFlag.ORDER)))
			{
				mob.tell(L("@x1 is a player!",victim.Name()));
				return false;
			}
			commands.remove(commands.size()-1);
			if((commands.size()>0)&&(((String)commands.lastElement()).equalsIgnoreCase("from")))
				commands.remove(commands.size()-1);

			final int maxToGive=CMLib.english().calculateMaxToGive(mob,commands,true,victim,false);
			if(maxToGive<0)
				return false;

			String thingToGive=CMParms.combine(commands,0);
			int addendum=1;
			String addendumStr="";
			final Vector V=new Vector();
			boolean allFlag=((String)commands.get(0)).equalsIgnoreCase("all");
			if(thingToGive.toUpperCase().startsWith("ALL.")){ allFlag=true; thingToGive="ALL "+thingToGive.substring(4);}
			if(thingToGive.toUpperCase().endsWith(".ALL")){ allFlag=true; thingToGive="ALL "+thingToGive.substring(0,thingToGive.length()-4);}

			if((thingToGive.equalsIgnoreCase("qp"))
			||(thingToGive.toUpperCase().endsWith(" QP"))
			||(thingToGive.toUpperCase().endsWith(".QP")))
			{
				int numToTake=1;
				if(allFlag)
					numToTake=victim.getQuestPoint();
				if(numToTake>maxToGive)
					numToTake=maxToGive;
				if((victim.getQuestPoint()<=0)||(victim.getQuestPoint()<numToTake))
				{
					if(victim.getQuestPoint()<=0)
						mob.tell(L("@x1 has no quest points!",victim.name()));
					else
						mob.tell(L("@x1 has only @x2 quest points!",victim.name(),""+victim.getQuestPoint()));
					return false;
				}
				mob.tell(L("You silently take @x1 quest points from @x2.",""+numToTake,victim.name()));
				victim.setQuestPoint(victim.getQuestPoint()-numToTake);
				return false;
			}

			boolean doBugFix = true;
			while(doBugFix || ((allFlag)&&(addendum<=maxToGive)))
			{
				doBugFix=false;
				Environmental giveThis=CMLib.english().bestPossibleGold(victim,null,thingToGive);

				if(giveThis!=null)
				{
					if(((Coins)giveThis).getNumberOfCoins()<CMLib.english().numPossibleGold(victim,thingToGive))
						return false;
					allFlag=false;
				}
				else
					giveThis=victim.fetchItem(null,Wearable.FILTER_UNWORNONLY,thingToGive+addendumStr);
				if((giveThis==null)
				&&(V.size()==0)
				&&(addendumStr.length()==0)
				&&(!allFlag))
					giveThis=victim.findItem(thingToGive);
				if(giveThis==null)
					break;
				if(giveThis instanceof Item)
				{
					((Item)giveThis).unWear();
					((Item)giveThis).setContainer(null);
					V.add(giveThis);
				}
				addendumStr="."+(++addendum);
			}

			if(V.size()==0)
				mob.tell(L("@x1 does not seem to be carrying that.",victim.name()));
			else
			for(int i=0;i<V.size();i++)
			{
				final Item giveThis=(Item)V.get(i);
				final CMMsg newMsg=CMClass.getMsg(victim,mob,giveThis,CMMsg.MASK_ALWAYS|CMMsg.MSG_GIVE,L("<T-NAME> take(s) <O-NAME> from <S-NAMESELF>."));
				if(victim.location().okMessage(victim,newMsg))
					victim.location().send(victim,newMsg);
				if(!mob.isMine(giveThis))
					mob.moveItemTo(giveThis);
				if(giveThis instanceof Coins)
					((Coins)giveThis).putCoinsBack();
				if(giveThis instanceof RawMaterial)
					((RawMaterial)giveThis).rebundle();
			}
		}
		else
		{
			if(((String)commands.lastElement()).equalsIgnoreCase("off"))
			{
				commands.remove(commands.size()-1);
				final Command C=CMClass.getCommand("Remove");
				if(C!=null)
					C.execute(mob,commands,metaFlags);
			}
			else
			if((commands.size()>1)&&(((String)commands.get(1)).equalsIgnoreCase("off")))
			{
				commands.remove(1);
				final Command C=CMClass.getCommand("Remove");
				if(C!=null)
					C.execute(mob,commands,metaFlags);
			}
			else
			{
				final Command C=CMClass.getCommand("Get");
				if(C!=null)
					C.execute(mob,commands,metaFlags);
			}
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
