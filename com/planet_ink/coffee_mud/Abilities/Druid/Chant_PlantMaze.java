package com.planet_ink.coffee_mud.Abilities.Druid;
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
public class Chant_PlantMaze extends Chant
{
	@Override public String ID() { return "Chant_PlantMaze"; }
	private final static String localizedName = CMLib.lang().L("Plant Maze");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Plant Maze)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}
	Room oldRoom=null;
	Item thePlants=null;

	@Override
	public boolean tick(Tickable ticking,int tickID)
	{
		if((thePlants==null)||(thePlants.owner()==null)||(!(thePlants.owner() instanceof Room)))
		{
			unInvoke();
			return false;
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(((msg.sourceMinor()==CMMsg.TYP_QUIT)
			||(msg.sourceMinor()==CMMsg.TYP_SHUTDOWN)
			||((msg.targetMinor()==CMMsg.TYP_EXPIRE)&&(msg.target()==oldRoom))
			||(msg.sourceMinor()==CMMsg.TYP_ROOMRESET))
		&&(msg.source().location()!=null)
		&&(msg.source().location().getGridParent()==affected))
		{
			if(oldRoom!=null)
				oldRoom.bringMobHere(msg.source(),false);
			if(msg.source()==invoker)
				unInvoke();
		}
		return super.okMessage(host,msg);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(!(affected instanceof Room))
			return;
		final Room room=(Room)affected;
		if((canBeUninvoked())&&(room instanceof GridLocale)&&(oldRoom!=null))
			((GridLocale)room).clearGrid(oldRoom);
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			room.setRawExit(d,null);
		super.unInvoke();
		room.destroy();
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Item myPlant=Druid_MyPlants.myPlant(mob.location(),mob,0);
			if(myPlant==null)
				return Ability.QUALITY_INDIFFERENT;
			if(mob.location().roomID().length()==0)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		thePlants=Druid_MyPlants.myPlant(mob.location(),mob,0);
		if(thePlants==null)
		{
			mob.tell(L("There doesn't appear to be any plants here you can control!"));
			return false;
		}

		if(mob.location().roomID().length()==0)
		{
			mob.tell(L("You cannot invoke the plant maze here."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{

			final CMMsg msg = CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto), auto?"":L("^S<S-NAME> chant(s) amazingly!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("Something is happening..."));

				final Room newRoom=CMClass.getLocale("WoodsMaze");
				((GridLocale)newRoom).setXGridSize(10+super.getX1Level(invoker())+super.getXLEVELLevel(invoker()));
				((GridLocale)newRoom).setYGridSize(10+super.getX1Level(invoker())+super.getXLEVELLevel(invoker()));
				String s=CMParms.parse(thePlants.name()).lastElement().toLowerCase();
				if(!s.endsWith("s"))
					s=s+"s";
				final String nos=s.substring(0,s.length()-1).toLowerCase();
				newRoom.setDisplayText(L("@x1 Maze",CMStrings.capitalizeAndLower(nos)));
				newRoom.addNonUninvokableEffect(CMClass.getAbility("Prop_NoTeleportOut"));
				final StringBuffer desc=new StringBuffer("");
				desc.append("This quaint glade is surrounded by tall "+s+".  A gentle breeze tosses leaves up into the air.");
				desc.append("<P>");
				desc.append("This forest of "+s+" is dark and thick here.  Ominous looking "+s+" seem to block every path, and the air is perfectly still.");
				desc.append("<P>");
				desc.append("A light growth of tall "+s+" surrounds you on all sides.  There are no apparant paths, but you can still see the sky between the growths.");
				desc.append("<P>");
				desc.append("A light growth of tall "+s+" surrounds you on all sides.  You can hear the sound of a running brook, but can't tell which direction its coming from.");
				desc.append("<P>");
				desc.append("The "+s+" around you are tall, dark and old, their leaves seeming to reach towards you.  In the distance, a wolfs howl can be heard.");
				desc.append("<P>");
				desc.append("The path seems to end at the base of a copse of tall "+s+".");
				desc.append("<P>");
				desc.append("You are standing in the middle of a light forest of "+s+".  How you got here, you can't really say.");
				desc.append("<P>");
				desc.append("You are standing in the middle of a thick dark forest of "+s+".  You wish you knew how you got here.");
				desc.append("<P>");
				desc.append("The "+s+" here seem to tower endlessly into the sky.  Their leaves are blocking out all but the smallest glimpses of the sky.");
				desc.append("<P>");
				desc.append("A forest of "+s+" seems to have grown up tall all around you.  The strange magical nature of the "+s+" makes you think you've entered a druidic grove.");
				newRoom.setArea(mob.location().getArea());
				oldRoom=mob.location();
				newRoom.setDescription(desc.toString());
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					final Room R=mob.location().rawDoors()[d];
					final Exit E=mob.location().getRawExit(d);
					if((R!=null)&&(R.roomID().length()>0))
					{
						newRoom.rawDoors()[d]=R;
						newRoom.setRawExit(d,E);
					}
				}
				newRoom.getArea().fillInAreaRoom(newRoom);
				beneficialAffect(mob,newRoom,asLevel,0);
				final Vector everyone=new Vector();
				for(int m=0;m<oldRoom.numInhabitants();m++)
				{
					final MOB follower=oldRoom.fetchInhabitant(m);
					everyone.addElement(follower);
				}

				for(int m=0;m<everyone.size();m++)
				{
					final MOB follower=(MOB)everyone.elementAt(m);
					if(follower==null)
						continue;
					final Room newerRoom=((GridLocale)newRoom).getRandomGridChild();
					final CMMsg enterMsg=CMClass.getMsg(follower,newerRoom,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,L("<S-NAME> appears out of @x1.",thePlants.name()));
					final CMMsg leaveMsg=CMClass.getMsg(follower,oldRoom,this,verbalCastCode(mob,oldRoom,auto),L("<S-NAME> disappear(s) into @x1.",thePlants.name()));
					if(oldRoom.okMessage(follower,leaveMsg)&&newerRoom.okMessage(follower,enterMsg))
					{
						if(follower.isInCombat())
							follower.makePeace();
						oldRoom.send(follower,leaveMsg);
						newerRoom.bringMobHere(follower,false);
						newerRoom.send(follower,enterMsg);
						follower.tell(L("\n\r\n\r"));
						CMLib.commands().postLook(follower,true);
					}
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) amazingly, but the magic fades."));

		// return whether it worked
		return success;
	}
}
