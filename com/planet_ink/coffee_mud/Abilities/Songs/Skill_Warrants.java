package com.planet_ink.coffee_mud.Abilities.Songs;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.ListingLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;


import java.util.*;

/*
   Copyright 2003-2014 Bo Zimmerman

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
public class Skill_Warrants extends BardSkill
{
	@Override public String ID() { return "Skill_Warrants"; }
	private final static String localizedName = CMLib.lang().L("Warrants");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"WARRANTS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_LEGAL; }
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		LegalBehavior B=null;
		if(mob.location()!=null)
			B=CMLib.law().getLegalBehavior(mob.location());

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,(-25+mob.charStats().getStat(CharStats.STAT_QUICKNESS)+(2*getXLEVELLevel(mob))),auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_DELICATE_SMALL_HANDS_ACT|(auto?CMMsg.MASK_ALWAYS:0),null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				List<LegalWarrant> V=new Vector();
				if(B!=null)
					V=B.getWarrantsOf(CMLib.law().getLegalObject(mob.location()),(MOB)null);
				if(V.size()==0)
				{
					mob.tell(L("No one is wanted for anything here."));
					return false;
				}
				final StringBuffer buf=new StringBuffer("");
				final int colWidth=ListingLibrary.ColFixer.fixColWidth(14,mob.session());
				buf.append(L("@x1 @x2 @x3 Crime\n\r",CMStrings.padRight(L("Name"),colWidth),CMStrings.padRight(L("Victim"),colWidth),CMStrings.padRight(L("Witness"),colWidth)));
				for(int v=0;v<V.size();v++)
				{
					final LegalWarrant W=V.get(v);
					buf.append(CMStrings.padRight(W.criminal().Name(),colWidth)+" ");
					buf.append(CMStrings.padRight(W.victim()!=null?W.victim().Name():L("N/A"),colWidth)+" ");
					buf.append(CMStrings.padRight(W.witness()!=null?W.witness().Name():L("N/A"),colWidth)+" ");
					buf.append(CMLib.coffeeFilter().fullOutFilter(mob.session(),mob,W.criminal(),W.victim(),null,W.crime(),false)+"\n\r");
				}
				if(!mob.isMonster())
					mob.session().rawPrintln(buf.toString());
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> attempt(s) to gather warrant information, but fail(s)."));

		return success;
	}

}
