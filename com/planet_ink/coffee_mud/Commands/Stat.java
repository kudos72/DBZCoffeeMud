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
public class Stat  extends Skills
{
	public Stat(){}

	private final String[] access=I(new String[]{"STAT"});
	@Override public String[] getAccessWords(){return access;}

	public static final int ABLETYPE_EQUIPMENT=-2;
	public static final int ABLETYPE_INVENTORY=-3;
	public static final int ABLETYPE_QUESTWINS=-4;
	public static final int ABLETYPE_TATTOOS=-5;
	public static final int ABLETYPE_COMBAT=-6;
	public static final int ABLETYPE_SCRIPTS=-7;
	public static final int ABLETYPE_TITLES=-8;
	public static final int ABLETYPE_ROOMSEXPLORED=-9;
	public static final int ABLETYPE_AREASEXPLORED=-10;
	public static final int ABLETYPE_WORLDEXPLORED=-11;
	public static final int ABLETYPE_FACTIONS=-12;
	public static final int ABLETYPE_CHARSTATS=-13;

	public static final String[][] ABLETYPE_DESCS={
		{"EQUIPMENT","EQ","EQUIP"},
		{"INVENTORY","INVEN","INV"},
		{"QUESTWINS","QUESTS","QUEST","QUESTWIN"},
		{"TATTOOS","TATTOO","TATT"},
		{"COMBAT"},
		{"SCRIPTS"},
		{"TITLES","TITLE"},
		{"ROOMSEXPLORED"},
		{"AREASEXPLORED"},
		{"WORLDEXPLORED"},
		{"FACTIONS","FACTION"},
		{"CHARSTATISTICS","CSTAT","CHARSTATS"},
	};

	public MOB getTarget(MOB mob, String targetName, boolean quiet)
	{
		MOB target=null;
		if(targetName.length()>0)
		{
			target=mob.location().fetchInhabitant(targetName);
			if(target==null)
			{
				final Environmental t=mob.location().fetchFromRoomFavorItems(null,targetName);
				if((t!=null)&&(!(t instanceof MOB)))
				{
					if(!quiet)
						mob.tell(mob,t,null,L("You can't do that to <T-NAMESELF>."));
					return null;
				}
			}
		}
		return target;
	}

	public String doSenses(int senseMask)
	{
		StringBuilder str=new StringBuilder("");
		for(int i=0;i<PhyStats.CAN_SEE_DESCS.length;i++)
			if(CMath.isSet(senseMask, i))
				str.append(PhyStats.CAN_SEE_DESCS[i].replace(' ','_')).append(" ");
		if(str.length()==0)
			str.append("NONE");
		return str.toString().trim();
	}
	
	public String doDisposition(int dispositionMask)
	{
		StringBuilder str=new StringBuilder("");
		for(int i=0;i<PhyStats.IS_DESCS.length;i++)
			if(CMath.isSet(dispositionMask, i))
				str.append(PhyStats.IS_DESCS[i].replace(' ','_')).append(" ");
		if(str.length()==0)
			str.append("NONE");
		return str.toString().trim();
	}
	
	public boolean showTableStats(MOB mob, int days, int scale, String rest)
	{
		final Calendar ENDQ=Calendar.getInstance();
		ENDQ.add(Calendar.DATE,-days);
		ENDQ.set(Calendar.HOUR_OF_DAY,23);
		ENDQ.set(Calendar.MINUTE,59);
		ENDQ.set(Calendar.SECOND,59);
		ENDQ.set(Calendar.MILLISECOND,999);
		CMLib.coffeeTables().update();
		final List<CoffeeTableRow> V=CMLib.database().DBReadStats(ENDQ.getTimeInMillis()-1);
		if(V.size()==0){ mob.tell(L("No Stats?!")); return false;}
		final StringBuffer table=new StringBuffer("");
		boolean skillUse=false;
		boolean questStats=false;
		if(rest.toUpperCase().trim().startsWith("SKILLUSE"))
		{
			skillUse=true;
			final int x=rest.indexOf(' ');
			if(x>0)
				rest=rest.substring(x+1).trim();
			else rest="";
		}
		if(rest.toUpperCase().trim().startsWith("QUEST"))
		{
			questStats=true;
			final int x=rest.indexOf(' ');
			if(x>0)
				rest=rest.substring(x+1).trim();
			else rest="";
		}
		table.append("^xStatistics since "+CMLib.time().date2String(ENDQ.getTimeInMillis())+":^.^N\n\r\n\r");
		if(skillUse)
			table.append(CMStrings.padRight(L("Skill"),25)+CMStrings.padRight(L("Uses"),10)+CMStrings.padRight(L("Skill"),25)+CMStrings.padRight(L("Uses"),10)+"\n\r");
		else
		if(questStats)
			table.append(CMStrings.padRight(L("Quest"),30)
	   					+CMStrings.padRight(L("STRT"),5)
						+CMStrings.padRight(L("TSRT"),5)
						+CMStrings.padRight(L("FLST"),5)
						+CMStrings.padRight(L("ACPT"),5)
						+CMStrings.padRight(L("WINS"),5)
						+CMStrings.padRight(L("FAIL"),5)
						+CMStrings.padRight(L("DROP"),5)
						+CMStrings.padRight(L("TSTP"),5)
						+CMStrings.padRight(L("STOP"),5)
						+"\n\r");
		else
			table.append(CMStrings.padRight(L("Date"),25)
						 +CMStrings.padRight(L("CONs"),5)
						 +CMStrings.padRight(L("HIGH"),5)
						 +CMStrings.padRight(L("ONLN"),5)
						 +CMStrings.padRight(L("AVGM"),5)
						 +CMStrings.padRight(L("NEWB"),5)
						 +CMStrings.padRight(L("DTHs"),5)
						 +CMStrings.padRight(L("PKDs"),5)
						 +CMStrings.padRight(L("CLAS"),5)
						 +CMStrings.padRight(L("PURG"),5)
						 +CMStrings.padRight(L("MARR"),5)+"\n\r");
		table.append(CMStrings.repeat('-',75)+"\n\r");
		final Calendar C=Calendar.getInstance();
		C.set(Calendar.HOUR_OF_DAY,23);
		C.set(Calendar.MINUTE,59);
		C.set(Calendar.SECOND,59);
		C.set(Calendar.MILLISECOND,999);
		long curTime=C.getTimeInMillis();
		String code="*";
		if(rest.length()>0)
			code=""+rest.toUpperCase().charAt(0);
		long lastCur=System.currentTimeMillis();
		if(skillUse)
		{
			final CharClass CharC=CMClass.getCharClass(rest);
			final Vector allSkills=new Vector();
			for(final Enumeration<Ability> e=CMClass.abilities();e.hasMoreElements();)
				allSkills.add(e.nextElement());
			final long[][] totals=new long[allSkills.size()][CoffeeTableRow.STAT_TOTAL];
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-(scale));
				curTime=C2.getTimeInMillis();
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final Vector set=new Vector();
				for(int v=V.size()-1;v>=0;v--)
				{
					final CoffeeTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.add(T);
						V.remove(v);
					}
				}
				for(int s=0;s<set.size();s++)
				{
					final CoffeeTableRow T=(CoffeeTableRow)set.get(s);
					for(int x=0;x<allSkills.size();x++)
						T.totalUp("A"+((Ability)allSkills.get(x)).ID().toUpperCase(),totals[x]);
				}
				if(scale==0)
					break;
			}
			boolean cr=false;
			for(int x=0;x<allSkills.size();x++)
			{
				Ability A=(Ability)allSkills.get(x);
				if((CharC==null)||(CMLib.ableMapper().getQualifyingLevel(CharC.ID(),true,A.ID())<0))
					continue;
				if(totals[x][CoffeeTableRow.STAT_SKILLUSE]>0)
				{
					table.append(CMStrings.padRight(""+A.ID(),25)
							+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_SKILLUSE],10));
					if(cr)
						table.append("\n\r");
					cr=!cr;
				}
				x++;
				if(x<allSkills.size())
				{
					A=(Ability)allSkills.get(x);
					if(totals[x][CoffeeTableRow.STAT_SKILLUSE]>0)
					{

						table.append(CMStrings.padRight(""+A.ID(),25)
								+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_SKILLUSE],10));
						if(cr)
							table.append("\n\r");
						cr=!cr;
					}
				}
			}
			if(cr)
				table.append("\n\r");
		}
		else
		if(questStats)
		{
			final long[][] totals=new long[CMLib.quests().numQuests()][CoffeeTableRow.STAT_TOTAL];
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-(scale));
				curTime=C2.getTimeInMillis();
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final Vector set=new Vector();
				for(int v=V.size()-1;v>=0;v--)
				{
					final CoffeeTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.add(T);
						V.remove(v);
					}
				}
				if(set.size()==0){ set.addAll(V); V.clear();}
				for(int s=0;s<set.size();s++)
				{
					final CoffeeTableRow T=(CoffeeTableRow)set.get(s);
					for(int x=0;x<CMLib.quests().numQuests();x++)
						T.totalUp("U"+T.tagFix(CMLib.quests().fetchQuest(x).name()),totals[x]);
				}
				if(scale==0)
					break;
			}
			for(int x=0;x<CMLib.quests().numQuests();x++)
			{
				final Quest Q=CMLib.quests().fetchQuest(x);
				table.append(
						 CMStrings.padRight(Q.name(),30)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTSTARTATTEMPT],5)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTTIMESTART],5)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTFAILEDSTART],5)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTACCEPTED],5)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTSUCCESS],5)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTFAILED],5)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTDROPPED],5)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTTIMESTOP],5)
						+CMStrings.centerPreserve(""+totals[x][CoffeeTableRow.STAT_QUESTSTOP],5));
				table.append("\n\r");
			}
			table.append("\n\r");
		}
		else
		while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
		{
			lastCur=curTime;
			final Calendar C2=Calendar.getInstance();
			C2.setTimeInMillis(curTime);
			C2.add(Calendar.DATE,-(scale));
			curTime=C2.getTimeInMillis();
			C2.set(Calendar.HOUR_OF_DAY,23);
			C2.set(Calendar.MINUTE,59);
			C2.set(Calendar.SECOND,59);
			C2.set(Calendar.MILLISECOND,999);
			curTime=C2.getTimeInMillis();
			final Vector set=new Vector();
			for(int v=V.size()-1;v>=0;v--)
			{
				final CoffeeTableRow T=V.get(v);
				if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
				{
					set.add(T);
					V.remove(v);
				}
			}
			final long[] totals=new long[CoffeeTableRow.STAT_TOTAL];
			long highestOnline=0;
			long numberOnlineTotal=0;
			long numberOnlineCounter=0;
			for(int s=0;s<set.size();s++)
			{
				final CoffeeTableRow T=(CoffeeTableRow)set.get(s);
				T.totalUp(code,totals);
				if(T.highestOnline()>highestOnline)
					highestOnline=T.highestOnline();
				numberOnlineTotal+=T.numberOnlineTotal();
				numberOnlineCounter+=T.numberOnlineCounter();
			}
			totals[CoffeeTableRow.STAT_TICKSONLINE]=(totals[CoffeeTableRow.STAT_TICKSONLINE]*CMProps.getTickMillis())/scale/(1000*60);
			double avgOnline=(numberOnlineCounter>0)?CMath.div(numberOnlineTotal,numberOnlineCounter):0.0;
			avgOnline=CMath.div(Math.round(avgOnline*10.0),10.0);
			table.append(CMStrings.padRight(CMLib.time().date2DateString(curTime+1)+" - "+CMLib.time().date2DateString(lastCur-1),25)
						 +CMStrings.centerPreserve(""+totals[CoffeeTableRow.STAT_LOGINS],5)
						 +CMStrings.centerPreserve(""+highestOnline,5)
						 +CMStrings.centerPreserve(""+avgOnline,5)
						 +CMStrings.centerPreserve(""+totals[CoffeeTableRow.STAT_TICKSONLINE],5)
						 +CMStrings.centerPreserve(""+totals[CoffeeTableRow.STAT_NEWPLAYERS],5)
						 +CMStrings.centerPreserve(""+totals[CoffeeTableRow.STAT_DEATHS],5)
						 +CMStrings.centerPreserve(""+totals[CoffeeTableRow.STAT_PKDEATHS],5)
						 +CMStrings.centerPreserve(""+totals[CoffeeTableRow.STAT_CLASSCHANGE],5)
						 +CMStrings.centerPreserve(""+totals[CoffeeTableRow.STAT_PURGES],5)
						 +CMStrings.centerPreserve(""+totals[CoffeeTableRow.STAT_MARRIAGES],5)+"\n\r");
			if(scale==0)
				break;
		}
		mob.tell(table.toString());
		return false;
	}

	public int averageDamage(MOB M)
	{
		long total=0;
		for(int i=0;i<1000;i++)
			total+=CMLib.combat().adjustedDamage(M,null,null,0,true);
		return (int)(total / 1000);
	}

	protected void addCharStatsChars(CharStats cstats, int headerWidth, int numberWidth, int[] col, StringBuilder str)
	{
		for(int i=0;i<cstats.getStatCodes().length;i++)
		{
			str.append("^g"+CMStrings.padRight(cstats.getStatCodes()[i]+"^w", headerWidth));
			str.append(" ");
			str.append(CMStrings.padRight(""+cstats.getStat(cstats.getStatCodes()[i]), numberWidth));
			col[0]++;
			if(col[0]==4)
				str.append("\n\r");
		}
	}

	protected void addCharStatsState(CharState cstats, int headerWidth, int numberWidth, int[] col, StringBuilder str)
	{
		for(int i=0;i<cstats.getStatCodes().length;i++)
		{
			str.append("^y"+CMStrings.padRight(cstats.getStatCodes()[i]+"^w", headerWidth));
			str.append(" ");
			str.append(CMStrings.padRight(""+cstats.getStat(cstats.getStatCodes()[i]), numberWidth));
			col[0]++;
			if(col[0]==4)
				str.append("\n\r");
		}
	}

	protected MOB getMOBTarget(MOB mob, String MOBname)
	{
		MOB target=getTarget(mob,MOBname,true);
		if((target==null)||(!target.isMonster()))
			target=mob.location().fetchInhabitant(MOBname);
		if((target==null)||(!target.isMonster()))
		{
			try
			{
				final List<MOB> inhabs=CMLib.map().findInhabitants(CMLib.map().rooms(), mob,MOBname,100);
				for(final MOB mob2 : inhabs)
				{
					final Room R=mob2.location();
					if(CMSecurity.isAllowed(mob,R,CMSecurity.SecFlag.STAT))
					{
						target=mob2;
						break;
					}
				}
			}catch(final NoSuchElementException nse){}
		}
		if(target==null)
			target=CMLib.players().getLoadPlayer(MOBname);
		return target;
	}
	
	protected void addCharStatsPhys(PhyStats pstats, int headerWidth, int numberWidth, int[] col, StringBuilder str)
	{
		for(int i=0;i<pstats.getStatCodes().length;i++)
		{
			str.append("^c"+CMStrings.padRight(pstats.getStatCodes()[i]+"^w", headerWidth));
			str.append(" ");
			str.append(CMStrings.padRight(""+pstats.getStat(pstats.getStatCodes()[i]), numberWidth));
			col[0]++;
			if(col[0]==4)
				str.append("\n\r");
		}
		for(int i=0;i<PhyStats.CAN_SEE_CODES.length;i++)
		{
			str.append("^c"+CMStrings.padRight(PhyStats.CAN_SEE_CODES[i]+"^w", headerWidth));
			str.append(" ");
			str.append(CMStrings.padRight(""+CMath.isSet(pstats.sensesMask(), i), numberWidth));
			col[0]++;
			if(col[0]==4)
				str.append("\n\r");
		}
		for(int i=0;i<PhyStats.IS_CODES.length;i++)
		{
			str.append("^c"+CMStrings.padRight(PhyStats.IS_CODES[i]+"^w", headerWidth));
			str.append(" ");
			str.append(CMStrings.padRight(""+CMath.isSet(pstats.disposition(), i), numberWidth));
			col[0]++;
			if(col[0]==4)
				str.append("\n\r");
		}
	}
	
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.remove(0);
		if(((commands.size()>0)&&(commands.get(0) instanceof String)&&((String)commands.get(0)).equals("?"))
		||((commands.size()==0)&&(!(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.STAT)))))
		{
			final StringBuilder msg = new StringBuilder("STAT allows the following options: \n\r");
			for(String stat : mob.curState().getStatCodes())
				msg.append(stat).append(", ");
			for(String stat : mob.curState().getStatCodes())
				msg.append("MAX"+stat).append(", ");
			for(String stat : mob.charStats().getStatCodes())
				msg.append(stat).append(", ");
			for(String stat : mob.phyStats().getStatCodes())
				msg.append(stat).append(", ");
			if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.STAT))
			{
				msg.append(L("[MOB/PLAYER NAME], [NUMBER] [DAYS/WEEKS/MONTHS], "));
				for (final String[] element : ABLETYPE_DESCS)
					msg.append(element[0]+", ");
				msg.append(CMParms.toStringList(Ability.ACODE_DESCS));
			}
			mob.tell(msg.toString());
			return false;
		}
		StringBuilder str=new StringBuilder("");
		if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.STAT))
		{
			if(commands.size()==0)
				commands.add("TODAY");
			final String s1=(commands.size()>0)?((String)commands.get(0)).toUpperCase():"";
			final String s2=(commands.size()>1)?((String)commands.get(1)).toUpperCase():"";
			if(s1.equalsIgnoreCase("TODAY"))
				return showTableStats(mob,1,1,CMParms.combine(commands,1));
			else
			if(commands.size()>1)
			{
				final String rest=(commands.size()>2)?CMParms.combine(commands,2):"";
				if(s2.equals("DAY")&&(CMath.isNumber(s1)))
					return showTableStats(mob,(CMath.s_int(s1)),1,rest);
				else
				if(s2.equals("DAYS")&&(CMath.isNumber(s1)))
					return showTableStats(mob,(CMath.s_int(s1)),1,rest);
				else
				if(s2.equals("WEEK")&&(CMath.isNumber(s1)))
					return showTableStats(mob,(CMath.s_int(s1)*7),7,rest);
				else
				if(s2.equals("WEEKS")&&(CMath.isNumber(s1)))
					return showTableStats(mob,(CMath.s_int(s1)*7),7,rest);
				else
				if(s2.equals("MONTH")&&(CMath.isNumber(s1)))
					return showTableStats(mob,(CMath.s_int(s1)*30),30,rest);
				else
				if(s2.equals("MONTHS")&&(CMath.isNumber(s1)))
					return showTableStats(mob,(CMath.s_int(s1)*30),30,rest);
				else
				if(s2.equals("YEAR")&&(CMath.isNumber(s1)))
					return showTableStats(mob,(CMath.s_int(s1)*365),365,rest);
				else
				if(s2.equals("YEARS")&&(CMath.isNumber(s1)))
					return showTableStats(mob,(CMath.s_int(s1)*365),365,rest);
			}
	
			int ableTypes=-1;
			if(commands.size()>1)
			{
				final String s=((String)commands.get(0)).toUpperCase();
				for(int i=0;i<ABLETYPE_DESCS.length;i++)
					for(int is=0;is<ABLETYPE_DESCS[i].length;is++)
						if(s.equals(ABLETYPE_DESCS[i][is]))
						{
							ableTypes=-2 -i;
							commands.remove(0);
							break;
						}
				if(ableTypes==-1)
				for(int a=0;a<Ability.ACODE_DESCS.length;a++)
				{
					if((Ability.ACODE_DESCS[a]+"S").equals(s)||(Ability.ACODE_DESCS[a]).equals(s))
					{
						ableTypes=a;
						commands.remove(0);
						break;
					}
				}
			}
			final String MOBname=CMParms.combine(commands,0);
			final MOB target=getMOBTarget(mob,MOBname);
			if((target!=null)
			&&(((target.isMonster())&&(CMSecurity.isAllowed(mob, target.location(), CMSecurity.SecFlag.CMDMOBS)))
				||((target.isPlayer())&&(CMSecurity.isAllowed(mob, target.location(), CMSecurity.SecFlag.CMDPLAYERS)))))
			{
				if(ableTypes>=0)
				{
					final Vector V=new Vector();
					final int mask=Ability.ALL_ACODES;
					V.add(Integer.valueOf(ableTypes));
					str=getAbilities(mob,target,V,mask,false,-1);
				}
				else
				if(ableTypes==ABLETYPE_EQUIPMENT)
					str=CMLib.commands().getEquipment(mob,target);
				else
				if(ableTypes==ABLETYPE_INVENTORY)
					str=CMLib.commands().getInventory(mob,target);
				else
				if(ableTypes==ABLETYPE_QUESTWINS)
				{
					str.append(L("Quests won:"));
					final StringBuffer won=new StringBuffer("");
					for(int q=0;q<CMLib.quests().numQuests();q++)
					{
						final Quest Q=CMLib.quests().fetchQuest(q);
						if(Q.wasWinner(target.Name()))
							won.append(" "+Q.name()+",");
					}
					if(won.length()==0)
						won.append(" None!");
					won.deleteCharAt(won.length()-1);
					str.append(won);
					str.append("\n\r");
				}
				else
				if(ableTypes==ABLETYPE_TITLES)
				{
					str.append(L("Titles:"));
					final StringBuffer ttl=new StringBuffer("");
					if(target.playerStats()!=null)
						for(int t=0;t<target.playerStats().getTitles().size();t++)
						{
							final String title = target.playerStats().getTitles().get(t);
							ttl.append(" "+title+",");
						}
					if(ttl.length()==0)
						ttl.append(" None!");
					ttl.deleteCharAt(ttl.length()-1);
					str.append(ttl);
					str.append("\n\r");
				}
				else
				if(ableTypes==ABLETYPE_SCRIPTS)
				{
					str.append(L("Scripts covered:\n\r"));
					int q=1;
					for(final Enumeration<ScriptingEngine> e=target.scripts();e.hasMoreElements();q++)
					{
						final ScriptingEngine SE=e.nextElement();
						str.append(L("Script #@x1\n\r",""+q));
						str.append(L("Quest: @x1\n\r",SE.defaultQuestName()));
						str.append(L("Savable: @x1\n\r",""+SE.isSavable()));
						str.append(L("Scope: @x1\n\r",SE.getVarScope()));
						str.append(L("Vars: @x1\n\r",SE.getLocalVarXML()));
						str.append(L("Script: @x1\n\r",SE.getScript()));
						str.append("\n\r");
					}
					str.append("\n\r");
				}
				else
				if(ableTypes==ABLETYPE_TATTOOS)
				{
					str.append(L("Tattoos:"));
					for(final Enumeration<MOB.Tattoo> e=target.tattoos();e.hasMoreElements();)
						str.append(" "+e.nextElement().tattooName+",");
					str.deleteCharAt(str.length()-1);
					str.append("\n\r");
				}
				else
				if(ableTypes==ABLETYPE_FACTIONS)
				{
					str.append(L("Factions:\n\r"));
					for(final Enumeration<String> f=target.fetchFactions();f.hasMoreElements();)
					{
						final Faction F=CMLib.factions().getFaction(f.nextElement());
						if(F!=null)
							str.append("^W[^H"+F.name()+"^N("+F.factionID()+"): "+target.fetchFaction(F.factionID())+"^W]^N, ");
					}
					str.append("\n\r");
				}
				else
				if(ableTypes==ABLETYPE_CHARSTATS)
				{
					str.append(L("^XCurrent Character Statistics:^.^N\n\r"));
					int[] col={0};
					int headerWidth=ListingLibrary.ColFixer.fixColWidth(12, mob);
					int numberWidth=ListingLibrary.ColFixer.fixColWidth(6, mob);
					addCharStatsChars(target.charStats(), headerWidth, numberWidth, col, str);
					addCharStatsPhys(target.phyStats(), headerWidth, numberWidth, col, str);
					addCharStatsState(target.curState(), headerWidth, numberWidth, col, str);
					str.append("\n\r\n\r");
					str.append(L("^XBase Character Statistics:^.^N\n\r"));
					col[0]=0;
					addCharStatsChars(target.baseCharStats(), headerWidth, numberWidth, col, str);
					addCharStatsPhys(target.basePhyStats(), headerWidth, numberWidth, col, str);
					addCharStatsState(target.baseState(), headerWidth, numberWidth, col, str);
					str.append("\n\r\n\r");
					str.append(L("^XMax Character State:^.^N\n\r"));
					col[0]=0;
					addCharStatsState(target.maxState(), headerWidth, numberWidth, col, str);
					str.append("\n\r");
				}
				else
				if(ableTypes==ABLETYPE_WORLDEXPLORED)
				{
					if(target.playerStats()!=null)
						str.append(L("@x1 has explored @x2% of the world.\n\r",target.name(),""+target.playerStats().percentVisited(target,null)));
					else
						str.append(L("Exploration data is not kept on mobs.\n\r"));
				}
				else
				if(ableTypes==ABLETYPE_AREASEXPLORED)
				{
					if(target.playerStats()!=null)
					{
						for(final Enumeration e=CMLib.map().areas();e.hasMoreElements();)
						{
							final Area A=(Area)e.nextElement();
							final int pct=target.playerStats().percentVisited(target, A);
							if(pct>0)
								str.append("^H"+A.name()+"^N: "+pct+"%, ");
						}
						str=new StringBuilder(str.toString().substring(0,str.toString().length()-2)+"\n\r");
					}
					else
						str.append(L("Exploration data is not kept on mobs.\n\r"));
				}
				else
				if(ableTypes==ABLETYPE_ROOMSEXPLORED)
				{
					if(target.playerStats()!=null)
					{
						for(final Enumeration e=CMLib.map().rooms();e.hasMoreElements();)
						{
							final Room R=(Room)e.nextElement();
							if((R.roomID().length()>0)&&(target.playerStats().hasVisited(R)))
								str.append("^H"+R.roomID()+"^N, ");
						}
						str=new StringBuilder(str.toString().substring(0,str.toString().length()-2)+"\n\r");
					}
					else
						str.append(L("Exploration data is not kept on mobs.\n\r"));
				}
				else
				if(ableTypes==ABLETYPE_COMBAT)
				{
					str.append(L("Combat summary:\n\r\n\r"));
					final MOB M=CMClass.getMOB("StdMOB");
					M.setBaseCharStats((CharStats)target.baseCharStats().copyOf());
					M.setBasePhyStats((PhyStats)target.basePhyStats().copyOf());
					M.setBaseState((CharState)target.baseState().copyOf());
					recoverMOB(target);
					recoverMOB(M);
					int base=CMLib.combat().adjustedAttackBonus(M,null);
					str.append("^c"+CMStrings.padRight(L("Base Attack"),40)+": ^W"+base+"\n\r");
					for(int i=0;i<target.numItems();i++)
					{
						final Item I=target.getItem(i);
						if((I!=null)&&(!I.amWearingAt(Wearable.IN_INVENTORY))){ recoverMOB(M); base=CMLib.combat().adjustedAttackBonus(M,null); testMOB(target,M,I); final int diff=CMLib.combat().adjustedAttackBonus(M,null)-base; reportOnDiffMOB(I,diff,str);}
					}
					recoverMOB(M);
					for(final Enumeration<Ability> a=target.effects();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if(A!=null){ recoverMOB(M); base=CMLib.combat().adjustedAttackBonus(M,null); testMOB(target,M,A); final int diff=CMLib.combat().adjustedAttackBonus(M,null)-base; reportOnDiffMOB(A,diff,str);}
					}
					recoverMOB(target);
					recoverMOB(M);
					str.append("^W-------------------------\n\r");
					str.append("^C"+CMStrings.padRight(L("Total"),40)+": ^W"+CMLib.combat().adjustedAttackBonus(target,null)+"\n\r");
					str.append("\n\r");
					base=CMLib.combat().adjustedArmor(M);
					str.append("^C"+CMStrings.padRight(L("Base Armor"),40)+": ^W"+base+"\n\r");
					for(int i=0;i<target.numItems();i++)
					{
						final Item I=target.getItem(i);
						if(I!=null){ recoverMOB(M); base=CMLib.combat().adjustedArmor(M); testMOB(target,M,I); final int diff=CMLib.combat().adjustedArmor(M)-base; reportOnDiffMOB(I,diff,str);}
					}
					recoverMOB(M);
					for(final Enumeration<Ability> a=target.effects();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if(A!=null){ recoverMOB(M); base=CMLib.combat().adjustedArmor(M); testMOB(target,M,A); final int diff=CMLib.combat().adjustedArmor(M)-base; reportOnDiffMOB(A,diff,str);}
					}
					recoverMOB(target);
					recoverMOB(M);
					str.append("^W-------------------------\n\r");
					str.append("^C"+CMStrings.padRight(L("Total"),40)+": ^W"+CMLib.combat().adjustedArmor(target)+"\n\r");
					str.append("\n\r");
					base=CMLib.combat().adjustedDamage(M,null,null,0,false);
					str.append("^C"+CMStrings.padRight(L("Base Damage"),40)+": ^W"+base+"\n\r");
					for(int i=0;i<target.numItems();i++)
					{
						final Item I=target.getItem(i);
						if(I!=null){ recoverMOB(M); base=averageDamage(M); testMOB(target,M,I); final int diff=averageDamage(M)-base; reportOnDiffMOB(I,diff,str);}
					}
					recoverMOB(M);
					for(final Enumeration<Ability> a=target.effects();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if(A!=null){ recoverMOB(M); base=averageDamage(M); testMOB(target,M,A); final int diff=averageDamage(M)-base; reportOnDiffMOB(A,diff,str);}
					}
					recoverMOB(target);
					recoverMOB(M);
					str.append("^W-------------------------\n\r");
					str.append("^C"+CMStrings.padRight(L("Total"),40)+": ^W"+CMLib.combat().adjustedDamage(target,null,null,0,false)+"\n\r");
					str.append("\n\r");
					base=(int)Math.round(M.phyStats().speed()*100);
					str.append("^C"+CMStrings.padRight(L("Base Attacks%"),40)+": ^W"+base+"\n\r");
					for(int i=0;i<target.numItems();i++)
					{
						final Item I=target.getItem(i);
						if(I!=null){ recoverMOB(M); base=(int)Math.round(M.phyStats().speed()*100); testMOB(target,M,I); final int diff=(int)Math.round(M.phyStats().speed()*100)-base; reportOnDiffMOB(I,diff,str);}
					}
					recoverMOB(M);
					for(final Enumeration<Ability> a=target.effects();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if(A!=null){ recoverMOB(M); base=(int)Math.round(M.phyStats().speed()*100); testMOB(target,M,A); final int diff=(int)Math.round(M.phyStats().speed()*100)-base; reportOnDiffMOB(A,diff,str);}
					}
					recoverMOB(target);
					recoverMOB(M);
					str.append("^W-------------------------\n\r");
					str.append("^C"+CMStrings.padRight(L("Total"),40)+": ^W"+(int)Math.round(target.phyStats().speed()*100)+"\n\r");
					str.append("\n\r");
					base=M.maxState().getHitPoints();
					str.append("^C"+CMStrings.padRight(L("Base Hit Points"),40)+": ^W"+base+"\n\r");
					for(int i=0;i<target.numItems();i++)
					{
						final Item I=target.getItem(i);
						if(I!=null){ recoverMOB(M); base=M.maxState().getHitPoints(); testMOB(target,M,I); final int diff=M.maxState().getHitPoints()-base; reportOnDiffMOB(I,diff,str);}
					}
					recoverMOB(M);
					for(int i=0;i<target.numAllEffects();i++)
					{
						final Ability A=target.fetchEffect(i);
						if(A!=null){ recoverMOB(M); base=M.maxState().getHitPoints(); testMOB(target,M,A); final int diff=M.maxState().getHitPoints()-base; reportOnDiffMOB(A,diff,str);}
					}
					recoverMOB(M);
					str.append("^W-------------------------\n\r");
					str.append("^C"+CMStrings.padRight(L("Total"),40)+": ^W"+target.maxState().getHitPoints()+"\n\r");
					recoverMOB(target);
				}
				else
				{
					if((target.playerStats()!=null)&&(CMProps.getIntVar(CMProps.Int.COMMONACCOUNTSYSTEM)>1))
						str.append(L("\n\r^xMember of Account:^.^N ^w@x1^?",(target.playerStats().getAccount()!=null)?target.playerStats().getAccount().getAccountName():L("None"))).append("\n\r");
					str.append(CMLib.commands().getScore(target));
				}
			}
		}
		if((commands.size()>0)&&(str.length()==0))
		{
			final String MOBname=commands.get(commands.size()-1).toString();
			MOB M=mob;
			if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.STAT))
			{
				MOB target=this.getMOBTarget(mob, MOBname);
				if((target!=null)
				&&(((target.isMonster())&&(CMSecurity.isAllowed(mob, target.location(), CMSecurity.SecFlag.CMDMOBS)))
					||((target.isPlayer())&&(CMSecurity.isAllowed(mob, target.location(), CMSecurity.SecFlag.CMDPLAYERS)))))
				{
					M=target;
					commands.remove(commands.size()-1);
				}
				else
				{
					mob.tell(L("You can't stat mob/player '@x1'!",MOBname));
					return false;
				}
			}
			
			for(int i=0;i<commands.size()-1;i++)
				if(commands.get(i).toString().toUpperCase().equals("MAX"))
				{
					commands.remove(i);
					commands.set(i,"MAX"+commands.get(i).toString());
				}
			for(int i=0;i<commands.size()-1;i++)
				commands.set(i,CMStrings.replaceAll(commands.get(i).toString()," ",""));
			for(int i=0;i<commands.size();i++)
			{
				String thisStat=commands.get(i).toString().toUpperCase().trim();
				boolean found=false;
				for(String stat : M.curState().getStatCodes())
					if(stat.equals(thisStat))
					{
						str.append(M.curState().getStat(stat)).append(" ");
						found=true;
						break;
					}
				if((!found)&&(thisStat.startsWith("MAX")))
				for(String stat : M.maxState().getStatCodes())
					if(stat.equals(thisStat.substring(3)))
					{
						str.append(M.maxState().getStat(stat)).append(" ");
						found=true;
						break;
					}
				if(!found)
					for(String stat : M.charStats().getStatCodes())
						if(stat.equals(thisStat))
						{
							if(stat.startsWith("MAX"))
								str.append(CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)+CMath.s_int(M.charStats().getStat(stat)));
							else
								str.append(M.charStats().getStat(stat)).append(" ");
							found=true;
							break;
						}
				if(!found)
					for(String stat : M.phyStats().getStatCodes())
						if(stat.equals(thisStat))
						{
							if(stat.equals("SENSES"))
								str.append(doSenses(CMath.s_int(M.phyStats().getStat(stat))));
							else
							if(stat.equals("DISPOSITION"))
								str.append(doDisposition(CMath.s_int(M.phyStats().getStat(stat))));
							else
								str.append(M.phyStats().getStat(stat)).append(" ");
							found=true;
							break;
						}
				if(!found)
					for(String stat : M.curState().getStatCodes())
						if(stat.startsWith(thisStat))
						{
							str.append(M.curState().getStat(stat)).append(" ");
							found=true;
							break;
						}
				if((!found)&&(thisStat.startsWith("MAX")))
					for(String stat : M.maxState().getStatCodes())
						if(stat.startsWith(thisStat.substring(3)))
						{
							str.append(M.maxState().getStat(stat)).append(" ");
							found=true;
							break;
						}
				if(!found)
					for(String stat : M.charStats().getStatCodes())
						if(stat.startsWith(thisStat))
						{
							if(stat.startsWith("MAX"))
								str.append(CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)+CMath.s_int(M.charStats().getStat(stat))).append(" ");
							else
								str.append(M.charStats().getStat(stat)).append(" ");
							found=true;
							break;
						}
				if(!found)
					for(String stat : M.phyStats().getStatCodes())
						if(stat.startsWith(thisStat))
						{
							if(stat.equals("SENSES"))
								str.append(doSenses(CMath.s_int(M.phyStats().getStat(stat))));
							else
							if(stat.equals("DISPOSITION"))
								str.append(doDisposition(CMath.s_int(M.phyStats().getStat(stat))));
							else
								str.append(M.phyStats().getStat(stat)).append(" ");
							found=true;
							break;
						}
				if(!found)
					str.append(" *UKNOWN:"+thisStat+"* ");
			}
		}
		if(!mob.isMonster())
			mob.session().wraplessPrintln(str.toString());
		return false;
	}

	public void recoverMOB(MOB M)
	{
		M.recoverCharStats();
		M.recoverPhyStats();
		M.recoverMaxState();
		M.resetToMaxState();
	}
	public void testMOB(MOB target,MOB M, Environmental test)
	{
		test.affectCharStats(target,M.charStats());
		test.affectPhyStats(target,M.phyStats());
		test.affectCharState(target,M.maxState());
	}
	public void reportOnDiffMOB(Environmental test, int diff, StringBuilder str)
	{
		if(diff>0)
			str.append("^C"+CMStrings.padRight(test.Name(),40)+": ^W+"+diff+"\n\r");
		else
		if(diff<0)
			str.append("^C"+CMStrings.padRight(test.Name(),40)+": ^W"+diff+"\n\r");
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return true;}


}
