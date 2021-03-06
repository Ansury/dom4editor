/* This file is part of dom4Editor.
 *
 * dom4Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dom4Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dom4Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.larz.dom4.editor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.larz.dom4.Activator;
import org.larz.dom4.db.Database;
import org.larz.dom4.db.MonsterDB;
import org.larz.dom4.dm.dm.DmFactory;
import org.larz.dom4.dm.dm.Monster;
import org.larz.dom4.dm.dm.MonsterInst1;
import org.larz.dom4.dm.dm.MonsterInst2;
import org.larz.dom4.dm.dm.MonsterInst3;
import org.larz.dom4.dm.dm.MonsterInst4;
import org.larz.dom4.dm.dm.MonsterInst5;
import org.larz.dom4.dm.dm.MonsterInst6;
import org.larz.dom4.dm.dm.MonsterMods;
import org.larz.dom4.dm.dm.SelectMonsterById;
import org.larz.dom4.dm.dm.SelectMonsterByName;
import org.larz.dom4.dm.ui.editor.DmXtextEditor;
import org.larz.dom4.dm.ui.help.HelpTextHelper;
import org.larz.dom4.image.ImageConverter;
import org.larz.dom4.image.ImageLoader;

@SuppressWarnings("incomplete-switch")
public class MonsterDetailsPage extends AbstractDetailsPage {
	private Text name;
	private Button nameCheck;
	private Text fixedName;
	private Button fixedNameCheck;
	private Text descr;
	private Button descCheck;
	private Text spr1;
	private Button spr1Check;
	private Button spr1Browse;
	private Text spr2;
	private Button spr2Check;
	private Button spr2Browse;
	private Label sprite1Label;
	private Label sprite2Label;
	private Text specialLook;
	private Button specialLookCheck;

	enum Inst {
		NAME (Messages.getString("MonsterDetailsSection.mod.name"), ""),
		FIXEDNAME (Messages.getString("MonsterDetailsSection.mod.fixedname"), "0"),
		SPR1 (Messages.getString("MonsterDetailsSection.mod.spr1"), ""),
		SPR2 (Messages.getString("MonsterDetailsSection.mod.spr2"), ""),
		SPECIALLOOK (Messages.getString("MonsterDetailsSection.mod.speciallook"), "1"),
		DESCR (Messages.getString("MonsterDetailsSection.mod.descr"), ""),
		
		HP (Messages.getString("MonsterDetailsSection.mod.hp"), "10"),
		SIZE (Messages.getString("MonsterDetailsSection.mod.size"), "2"),
		RESSIZE (Messages.getString("MonsterDetailsSection.mod.ressize"), "1"),
		PROT (Messages.getString("MonsterDetailsSection.mod.prot"), "0"),
		MR (Messages.getString("MonsterDetailsSection.mod.mr"), "10"),
		MOR (Messages.getString("MonsterDetailsSection.mod.mor"), "10"),
		STR (Messages.getString("MonsterDetailsSection.mod.str"), "10"),
		ATT (Messages.getString("MonsterDetailsSection.mod.att"), "10"),
		DEF (Messages.getString("MonsterDetailsSection.mod.def"), "10"),
		PREC (Messages.getString("MonsterDetailsSection.mod.prec"), "10"),
		ENC (Messages.getString("MonsterDetailsSection.mod.enc"), "3"),
		MAPMOVE (Messages.getString("MonsterDetailsSection.mod.mapmove"), "1"),
		AP (Messages.getString("MonsterDetailsSection.mod.ap"), "12"),
		EYES (Messages.getString("MonsterDetailsSection.mod.eyes"), "2"),
		VOIDSANITY (Messages.getString("MonsterDetailsSection.mod.voidsanity"), "10"),

		CLEAR (Messages.getString("MonsterDetailsSection.mod.clear")),
		CLEARWEAPONS (Messages.getString("MonsterDetailsSection.mod.clearweapons")),
		CLEARARMOR (Messages.getString("MonsterDetailsSection.mod.cleararmor")),
		CLEARMAGIC (Messages.getString("MonsterDetailsSection.mod.clearmagic")),
		CLEARSPEC (Messages.getString("MonsterDetailsSection.mod.clearspec")),
		COPYSTATS (Messages.getString("MonsterDetailsSection.mod.copystats"), "0"),
		COPYSPR (Messages.getString("MonsterDetailsSection.mod.copyspr"), "0"),
		
		SLOWREC (Messages.getString("MonsterDetailsSection.mod.slowrec"), "0"),
		NOSLOWREC (Messages.getString("MonsterDetailsSection.mod.noslowrec"), "0"),
		RECLIMIT (Messages.getString("MonsterDetailsSection.mod.reclimit"), "0"),
		REQLAB (Messages.getString("MonsterDetailsSection.mod.reqlab"), "0"),
		REQTEMPLE (Messages.getString("MonsterDetailsSection.mod.reqtemple"), "0"),
		CHAOSREC (Messages.getString("MonsterDetailsSection.mod.chaosrec"), "0"),
		AISINGLEREC (Messages.getString("MonsterDetailsSection.mod.aisinglerec"), "0"),
		AINOREC (Messages.getString("MonsterDetailsSection.mod.ainorec"), "0"),

		GCOST (Messages.getString("MonsterDetailsSection.mod.gcost"), "10"),
		RCOST (Messages.getString("MonsterDetailsSection.mod.rcost"), "1"),
		
		WEAPON1 (Messages.getString("MonsterDetailsSection.mod.weapon"), "1"),
		WEAPON2 (Messages.getString("MonsterDetailsSection.mod.weapon"), "2"),
		WEAPON3 (Messages.getString("MonsterDetailsSection.mod.weapon"), "3"),
		WEAPON4 (Messages.getString("MonsterDetailsSection.mod.weapon"), "4"),
		ARMOR1 (Messages.getString("MonsterDetailsSection.mod.armor"), ""),
		ARMOR2 (Messages.getString("MonsterDetailsSection.mod.armor"), ""),
		ARMOR3 (Messages.getString("MonsterDetailsSection.mod.armor"), ""),
		
		PATHCOST (Messages.getString("MonsterDetailsSection.mod.pathcost"), "10"),
		STARTDOM (Messages.getString("MonsterDetailsSection.mod.startdom"), "1"),
		HOMEREALM (Messages.getString("MonsterDetailsSection.mod.homerealm"), "0"),

		FEMALE (Messages.getString("MonsterDetailsSection.mod.female")),
		MOUNTED (Messages.getString("MonsterDetailsSection.mod.mounted")),
		HOLY (Messages.getString("MonsterDetailsSection.mod.holy")),
		ANIMAL (Messages.getString("MonsterDetailsSection.mod.animal")),
		UNDEAD (Messages.getString("MonsterDetailsSection.mod.undead")),
		DEMON (Messages.getString("MonsterDetailsSection.mod.demon")),
		MAGICBEING (Messages.getString("MonsterDetailsSection.mod.magicbeing")),
		STONEBEING (Messages.getString("MonsterDetailsSection.mod.stonebeing")),
		INANIMATE (Messages.getString("MonsterDetailsSection.mod.inanimate")),
		COLDBLOOD (Messages.getString("MonsterDetailsSection.mod.coldblood")),
		IMMORTAL (Messages.getString("MonsterDetailsSection.mod.immortal")),
		LESSERHORROR (Messages.getString("MonsterDetailsSection.mod.lesserhorror"), "0"),
		GREATERHORROR (Messages.getString("MonsterDetailsSection.mod.greaterhorror"), "0"),
		DOOMHORROR (Messages.getString("MonsterDetailsSection.mod.doomhorror"), "0"),
		BLIND (Messages.getString("MonsterDetailsSection.mod.blind")),
		UNIQUE (Messages.getString("MonsterDetailsSection.mod.unique")),
		BUG (Messages.getString("MonsterDetailsSection.mod.bug"), "0"),
		UWBUG (Messages.getString("MonsterDetailsSection.mod.uwbug"), "0"),
		AUTOCOMPETE (Messages.getString("MonsterDetailsSection.mod.autocompete"), "0"),

		IMMOBILE (Messages.getString("MonsterDetailsSection.mod.immobile")),
		AQUATIC (Messages.getString("MonsterDetailsSection.mod.aquatic")),
		AMPHIBIAN (Messages.getString("MonsterDetailsSection.mod.amphibian")),
		POORAMPHIBIAN (Messages.getString("MonsterDetailsSection.mod.pooramphibian")),
		FLOAT (Messages.getString("MonsterDetailsSection.mod.float")),
		FLYING (Messages.getString("MonsterDetailsSection.mod.flying")),
		STORMIMMUNE (Messages.getString("MonsterDetailsSection.mod.stormimmune")),
		TELEPORT (Messages.getString("MonsterDetailsSection.mod.teleport")),
		UNTELEPORTABLE (Messages.getString("MonsterDetailsSection.mod.unteleportable")),
		NORIVERPASS (Messages.getString("MonsterDetailsSection.mod.noriverpass")),
		FORESTSURVIVAL (Messages.getString("MonsterDetailsSection.mod.forestsurvival")),
		MOUNTAINSURVIVAL (Messages.getString("MonsterDetailsSection.mod.mountainsurvival")),
		SWAMPSURVIVAL (Messages.getString("MonsterDetailsSection.mod.swampsurvival")),
		WASTESURVIVAL (Messages.getString("MonsterDetailsSection.mod.wastesurvival")),
		SAILING (Messages.getString("MonsterDetailsSection.mod.sailing"), "999", "2"),
		GIFTOFWATER (Messages.getString("MonsterDetailsSection.mod.giftofwater"), "0"),
		INDEPMOVE (Messages.getString("MonsterDetailsSection.mod.indepmove"), "0"),

		STEALTHY (Messages.getString("MonsterDetailsSection.mod.stealthy"), "0"),
		ILLUSION (Messages.getString("MonsterDetailsSection.mod.illusion")),
		SPY (Messages.getString("MonsterDetailsSection.mod.spy")),
		ASSASSIN (Messages.getString("MonsterDetailsSection.mod.assassin")),
		SEDUCE (Messages.getString("MonsterDetailsSection.mod.seduce"), "10"),
		SUCCUBUS (Messages.getString("MonsterDetailsSection.mod.succubus"), "10"),
		BECKON (Messages.getString("MonsterDetailsSection.mod.beckon"), "10"),
		PATIENCE (Messages.getString("MonsterDetailsSection.mod.patience"), "0"),
		FALSEARMY (Messages.getString("MonsterDetailsSection.mod.falsearmy"), "0"),
		FOOLSCOUTS (Messages.getString("MonsterDetailsSection.mod.foolscouts"), "0"),

		SINGLEBATTLE (Messages.getString("MonsterDetailsSection.mod.singlebattle"), "0"),
		DESERTER (Messages.getString("MonsterDetailsSection.mod.deserter"), "0"),
		HORRORDESERTER (Messages.getString("MonsterDetailsSection.mod.horrordeserter"), "0"),
		DEFECTOR (Messages.getString("MonsterDetailsSection.mod.defector"), "0"),
		
		STARTAGE (Messages.getString("MonsterDetailsSection.mod.startage"), "20"),
		MAXAGE (Messages.getString("MonsterDetailsSection.mod.maxage"), "50"),
		OLDER (Messages.getString("MonsterDetailsSection.mod.older"), "10"),
		HEAL (Messages.getString("MonsterDetailsSection.mod.heal")),
		NOHEAL (Messages.getString("MonsterDetailsSection.mod.noheal")),
		HEALER (Messages.getString("MonsterDetailsSection.mod.healer"), "10"),
		AUTOHEALER (Messages.getString("MonsterDetailsSection.mod.autohealer"), "0"),
		AUTODISHEALER (Messages.getString("MonsterDetailsSection.mod.autodishealer"), "0"),
		AUTODISGRINDER (Messages.getString("MonsterDetailsSection.mod.autodisgrinder"), "0"),
		STARTAFF (Messages.getString("MonsterDetailsSection.mod.startaff"), "10"),
		WOUNDFEND (Messages.getString("MonsterDetailsSection.mod.woundfend"), "0"),
		UWDAMAGE (Messages.getString("MonsterDetailsSection.mod.uwdamage"), "10"),
		HOMESICK (Messages.getString("MonsterDetailsSection.mod.homesick"), "10"),
		HPOVERFLOW (Messages.getString("MonsterDetailsSection.mod.hpoverflow"), "0"),

		PIERCERES (Messages.getString("MonsterDetailsSection.mod.pierceres"), "0"),
		SLASHRES (Messages.getString("MonsterDetailsSection.mod.slashres"), "0"),
		BLUNTRES (Messages.getString("MonsterDetailsSection.mod.bluntres"), "0"),
		ETHEREAL (Messages.getString("MonsterDetailsSection.mod.ethereal")),
		COLDRES (Messages.getString("MonsterDetailsSection.mod.coldres"), "100"),
		FIRERES (Messages.getString("MonsterDetailsSection.mod.fireres"), "100"),
		POISONRES (Messages.getString("MonsterDetailsSection.mod.poisonres"), "100"),
		SHOCKRES (Messages.getString("MonsterDetailsSection.mod.shockres"), "100"),
		ICEPROT (Messages.getString("MonsterDetailsSection.mod.iceprot"), "2"),
		INVULNERABLE (Messages.getString("MonsterDetailsSection.mod.invulnerable"), "2"),
		REGENERATION (Messages.getString("MonsterDetailsSection.mod.regeneration"), "10"),
		REINVIGORATION (Messages.getString("MonsterDetailsSection.mod.reinvigoration"), "10"),

		HEAT (Messages.getString("MonsterDetailsSection.mod.heat"), "3"),
		COLD (Messages.getString("MonsterDetailsSection.mod.cold"), "3"),
		POISONCLOUD (Messages.getString("MonsterDetailsSection.mod.poisoncloud"), "6"),
		DISEASECLOUD (Messages.getString("MonsterDetailsSection.mod.diseasecloud"), "6"),
		ANIMALAWE (Messages.getString("MonsterDetailsSection.mod.animalawe"), "1"),
		AWE (Messages.getString("MonsterDetailsSection.mod.awe"), "1"),
		FEAR (Messages.getString("MonsterDetailsSection.mod.fear"), "0"),
		FIRESHIELD (Messages.getString("MonsterDetailsSection.mod.fireshield"), "8"),
		BANEFIRESHIELD (Messages.getString("MonsterDetailsSection.mod.banefireshield"), "8"),
		DAMAGEREV (Messages.getString("MonsterDetailsSection.mod.damagerev"), "0"),
		BLOODVENGEANCE (Messages.getString("MonsterDetailsSection.mod.bloodvengeance"), "1"),
		SLIMER (Messages.getString("MonsterDetailsSection.mod.slimer"), "0"),
		ENTANGLE (Messages.getString("MonsterDetailsSection.mod.entangle")),
		EYELOSS (Messages.getString("MonsterDetailsSection.mod.eyeloss")),
		HORRORMARK (Messages.getString("MonsterDetailsSection.mod.horrormark")),
		POISONARMOR (Messages.getString("MonsterDetailsSection.mod.poisonarmor")),

		CHAOSPOWER (Messages.getString("MonsterDetailsSection.mod.chaospower"), "0"),
		COLDPOWER (Messages.getString("MonsterDetailsSection.mod.coldpower"), "10"),
		FIREPOWER (Messages.getString("MonsterDetailsSection.mod.firepower"), "10"),
		MAGICPOWER (Messages.getString("MonsterDetailsSection.mod.magicpower"), "0"),
		STORMPOWER (Messages.getString("MonsterDetailsSection.mod.stormpower"), "10"),
		DARKPOWER (Messages.getString("MonsterDetailsSection.mod.darkpower"), "10"),

		SPRINGPOWER (Messages.getString("MonsterDetailsSection.mod.springpower"), "10"),
		SUMMERPOWER (Messages.getString("MonsterDetailsSection.mod.summerpower"), "10"),
		FALLPOWER (Messages.getString("MonsterDetailsSection.mod.fallpower"), "10"),
		WINTERPOWER (Messages.getString("MonsterDetailsSection.mod.winterpower"), "10"),
		
		AMBIDEXTROUS (Messages.getString("MonsterDetailsSection.mod.ambidextrous"), "2"),
		BERSERK (Messages.getString("MonsterDetailsSection.mod.berserk"), "3"),
		DARKVISION (Messages.getString("MonsterDetailsSection.mod.darkvision"), "100"),
		TRAMPLE (Messages.getString("MonsterDetailsSection.mod.trample")),
		TRAMPSWALLOW (Messages.getString("MonsterDetailsSection.mod.trampswallow"), "0"),
		DIGEST (Messages.getString("MonsterDetailsSection.mod.digest"), "0"),
		INCORPORATE (Messages.getString("MonsterDetailsSection.mod.incorporate"), "0"),
		DEATHCURSE (Messages.getString("MonsterDetailsSection.mod.deathcurse"), "0"),
		DEATHDISEASE (Messages.getString("MonsterDetailsSection.mod.deathdisease"), "0"),
		DEATHPARALYZE (Messages.getString("MonsterDetailsSection.mod.deathparalyze"), "0"),
		DEATHFIRE (Messages.getString("MonsterDetailsSection.mod.deathfire"), "0"),

		CASTLEDEF (Messages.getString("MonsterDetailsSection.mod.castledef"), "10"),
		SIEGEBONUS (Messages.getString("MonsterDetailsSection.mod.siegebonus"), "10"),
		PATROLBONUS (Messages.getString("MonsterDetailsSection.mod.patrolbonus"), "10"),
		PILLAGEBONUS (Messages.getString("MonsterDetailsSection.mod.pillagebonus"), "10"),
		SUPPLYBONUS (Messages.getString("MonsterDetailsSection.mod.supplybonus"), "10"),
		RESOURCES (Messages.getString("MonsterDetailsSection.mod.resources"), "10"),
		NEEDNOTEAT (Messages.getString("MonsterDetailsSection.mod.neednoteat")),
		NOBADEVENTS (Messages.getString("MonsterDetailsSection.mod.nobadevents"), "10"),
		INCUNREST (Messages.getString("MonsterDetailsSection.mod.incunrest"), "10"),
		INCPROVDEF (Messages.getString("MonsterDetailsSection.mod.incprovdef"), "0"),
		LEPER (Messages.getString("MonsterDetailsSection.mod.leper"), "10"),
		POPKILL (Messages.getString("MonsterDetailsSection.mod.popkill"), "10"),
		INQUISITOR (Messages.getString("MonsterDetailsSection.mod.inquisitor")),
		HERETIC (Messages.getString("MonsterDetailsSection.mod.heretic"), "1"),
		ELEGIST (Messages.getString("MonsterDetailsSection.mod.elegist"), "0"),
		SPREADDOM (Messages.getString("MonsterDetailsSection.mod.spreaddom"), "10"),
		SHATTEREDSOUL (Messages.getString("MonsterDetailsSection.mod.shatteredsoul"), "10"),
		TAXCOLLECTOR (Messages.getString("MonsterDetailsSection.mod.taxcollector"), "0"),
		GOLD (Messages.getString("MonsterDetailsSection.mod.gold"), "0"),
		NOHOF (Messages.getString("MonsterDetailsSection.mod.nohof"), "0"),

		FIRSTSHAPE (Messages.getString("MonsterDetailsSection.mod.firstshape"), ""),
		SECONDSHAPE (Messages.getString("MonsterDetailsSection.mod.secondshape"), ""),
		SECONDTMPSHAPE (Messages.getString("MonsterDetailsSection.mod.secondtmpshape"), ""),
		CLEANSHAPE (Messages.getString("MonsterDetailsSection.mod.cleanshape"), "0"),
		SHAPECHANGE (Messages.getString("MonsterDetailsSection.mod.shapechange"), ""),
		LANDSHAPE (Messages.getString("MonsterDetailsSection.mod.landshape"), ""),
		WATERSHAPE (Messages.getString("MonsterDetailsSection.mod.watershape"), ""),
		FORESTSHAPE (Messages.getString("MonsterDetailsSection.mod.forestshape"), ""),
		PLAINSHAPE (Messages.getString("MonsterDetailsSection.mod.plainshape"), ""),
		GROWHP (Messages.getString("MonsterDetailsSection.mod.growhp"), "0"),
		SHRINKHP (Messages.getString("MonsterDetailsSection.mod.shrinkhp"), "0"),
		
		REANIMATOR (Messages.getString("MonsterDetailsSection.mod.reanimator"), "0"),
		DOMSUMMON (Messages.getString("MonsterDetailsSection.mod.domsummon"), ""),
		DOMSUMMON2 (Messages.getString("MonsterDetailsSection.mod.domsummon2"), ""),
		DOMSUMMON20 (Messages.getString("MonsterDetailsSection.mod.domsummon20"), ""),
		RAREDOMSUMMON (Messages.getString("MonsterDetailsSection.mod.raredomsummon"), "0"),
		MAKEMONSTERS1 (Messages.getString("MonsterDetailsSection.mod.makemonsters1"), ""),
		MAKEMONSTERS2 (Messages.getString("MonsterDetailsSection.mod.makemonsters2"), ""),
		MAKEMONSTERS3 (Messages.getString("MonsterDetailsSection.mod.makemonsters3"), ""),
		MAKEMONSTERS4 (Messages.getString("MonsterDetailsSection.mod.makemonsters4"), ""),
		MAKEMONSTERS5 (Messages.getString("MonsterDetailsSection.mod.makemonsters5"), ""),
		SUMMON1 (Messages.getString("MonsterDetailsSection.mod.summon1"), ""),
		SUMMON2 (Messages.getString("MonsterDetailsSection.mod.summon2"), ""),
		SUMMON3 (Messages.getString("MonsterDetailsSection.mod.summon3"), ""),
		SUMMON4 (Messages.getString("MonsterDetailsSection.mod.summon4"), ""),
		SUMMON5 (Messages.getString("MonsterDetailsSection.mod.summon5"), ""),
		BATTLESUM1 (Messages.getString("MonsterDetailsSection.mod.battlesum1"), "0"),
		BATTLESUM2 (Messages.getString("MonsterDetailsSection.mod.battlesum2"), "0"),
		BATTLESUM3 (Messages.getString("MonsterDetailsSection.mod.battlesum3"), "0"),
		BATTLESUM4 (Messages.getString("MonsterDetailsSection.mod.battlesum4"), "0"),
		BATTLESUM5 (Messages.getString("MonsterDetailsSection.mod.battlesum5"), "0"),
		BATSTARTSUM1 (Messages.getString("MonsterDetailsSection.mod.batstartsum1"), "0"),
		BATSTARTSUM2 (Messages.getString("MonsterDetailsSection.mod.batstartsum2"), "0"),
		BATSTARTSUM3 (Messages.getString("MonsterDetailsSection.mod.batstartsum3"), "0"),
		BATSTARTSUM4 (Messages.getString("MonsterDetailsSection.mod.batstartsum4"), "0"),
		BATSTARTSUM5 (Messages.getString("MonsterDetailsSection.mod.batstartsum5"), "0"),
		BATSTARTSUM1D6 (Messages.getString("MonsterDetailsSection.mod.batstartsum1d6"), "0"),
		BATSTARTSUM2D6 (Messages.getString("MonsterDetailsSection.mod.batstartsum2d6"), "0"),
		BATSTARTSUM3D6 (Messages.getString("MonsterDetailsSection.mod.batstartsum3d6"), "0"),
		BATSTARTSUM4D6 (Messages.getString("MonsterDetailsSection.mod.batstartsum4d6"), "0"),
		BATSTARTSUM5D6 (Messages.getString("MonsterDetailsSection.mod.batstartsum5d6"), "0"),
		MONTAG (Messages.getString("MonsterDetailsSection.mod.montag"), "0"),

		NAMETYPE (Messages.getString("MonsterDetailsSection.mod.nametype"), "100"),

		ITEMSLOTS (Messages.getString("MonsterDetailsSection.mod.itemslots"), "15494"),
		NOITEM (Messages.getString("MonsterDetailsSection.mod.noitem")),

		NOLEADER (Messages.getString("MonsterDetailsSection.mod.noleader")),
		POORLEADER (Messages.getString("MonsterDetailsSection.mod.poorleader")),
		OKLEADER (Messages.getString("MonsterDetailsSection.mod.okleader")),
		GOODLEADER (Messages.getString("MonsterDetailsSection.mod.goodleader")),
		EXPERTLEADER (Messages.getString("MonsterDetailsSection.mod.expertleader")),
		SUPERIORLEADER (Messages.getString("MonsterDetailsSection.mod.superiorleader")),

		NOMAGICLEADER (Messages.getString("MonsterDetailsSection.mod.nomagicleader")),
		POORMAGICLEADER (Messages.getString("MonsterDetailsSection.mod.poormagicleader")),
		OKMAGICLEADER (Messages.getString("MonsterDetailsSection.mod.okmagicleader")),
		GOODMAGICLEADER (Messages.getString("MonsterDetailsSection.mod.goodmagicleader")),
		EXPERTMAGICLEADER (Messages.getString("MonsterDetailsSection.mod.expertmagicleader")),
		SUPERIORMAGICLEADER (Messages.getString("MonsterDetailsSection.mod.superiormagicleader")),

		NOUNDEADLEADER (Messages.getString("MonsterDetailsSection.mod.noundeadleader")),
		POORUNDEADLEADER (Messages.getString("MonsterDetailsSection.mod.poorundeadleader")),
		OKUNDEADLEADER (Messages.getString("MonsterDetailsSection.mod.okundeadleader")),
		GOODUNDEADLEADER (Messages.getString("MonsterDetailsSection.mod.goodundeadleader")),
		EXPERTUNDEADLEADER (Messages.getString("MonsterDetailsSection.mod.expertundeadleader")),
		SUPERIORUNDEADLEADER (Messages.getString("MonsterDetailsSection.mod.superiorundeadleader")),

		INSPIRATIONAL (Messages.getString("MonsterDetailsSection.mod.inspirational"), "0"),
		BEASTMASTER (Messages.getString("MonsterDetailsSection.mod.beastmaster"), "0"),
		TASKMASTER (Messages.getString("MonsterDetailsSection.mod.taskmaster"), "0"),
		SLAVE (Messages.getString("MonsterDetailsSection.mod.slave"), "0"),
		UNDISCIPLINED (Messages.getString("MonsterDetailsSection.mod.undisciplined"), "0"),
		FORMATIONFIGHTER (Messages.getString("MonsterDetailsSection.mod.formationfighter"), "0"),
		BODYGUARD (Messages.getString("MonsterDetailsSection.mod.bodyguard"), "0"),
		STANDARD (Messages.getString("MonsterDetailsSection.mod.standard"), "3"),

		MAGICSKILL1 (Messages.getString("MonsterDetailsSection.mod.magicskill"), "0", "1"),
		CUSTOMMAGIC1 (Messages.getString("MonsterDetailsSection.mod.custommagic"), "128", "100"),
		MAGICSKILL2 (Messages.getString("MonsterDetailsSection.mod.magicskill"), "0", "1"),
		CUSTOMMAGIC2 (Messages.getString("MonsterDetailsSection.mod.custommagic"), "128", "100"),
		MAGICSKILL3 (Messages.getString("MonsterDetailsSection.mod.magicskill"), "0", "1"),
		CUSTOMMAGIC3 (Messages.getString("MonsterDetailsSection.mod.custommagic"), "128", "100"),
		MAGICSKILL4 (Messages.getString("MonsterDetailsSection.mod.magicskill"), "0", "1"),
		CUSTOMMAGIC4 (Messages.getString("MonsterDetailsSection.mod.custommagic"), "128", "100"),
		MAGICSKILL5 (Messages.getString("MonsterDetailsSection.mod.magicskill"), "0", "1"),
		CUSTOMMAGIC5 (Messages.getString("MonsterDetailsSection.mod.custommagic"), "128", "100"),
		MAGICSKILL6 (Messages.getString("MonsterDetailsSection.mod.magicskill"), "0", "1"),
		CUSTOMMAGIC6 (Messages.getString("MonsterDetailsSection.mod.custommagic"), "128", "100"),
		MAGICSKILL7 (Messages.getString("MonsterDetailsSection.mod.magicskill"), "0", "1"),
		CUSTOMMAGIC7 (Messages.getString("MonsterDetailsSection.mod.custommagic"), "128", "100"),
		MAGICSKILL8 (Messages.getString("MonsterDetailsSection.mod.magicskill"), "0", "1"),
		CUSTOMMAGIC8 (Messages.getString("MonsterDetailsSection.mod.custommagic"), "128", "100"),
		MAGICBOOST1 (Messages.getString("MonsterDetailsSection.mod.magicboost"), "0", "1"),
		MAGICBOOST2 (Messages.getString("MonsterDetailsSection.mod.magicboost"), "0", "1"),
		MAGICBOOST3 (Messages.getString("MonsterDetailsSection.mod.magicboost"), "0", "1"),
		MAGICBOOST4 (Messages.getString("MonsterDetailsSection.mod.magicboost"), "0", "1"),
		MAGICBOOST5 (Messages.getString("MonsterDetailsSection.mod.magicboost"), "0", "1"),
		MAGICBOOST6 (Messages.getString("MonsterDetailsSection.mod.magicboost"), "0", "1"),
		MAGICBOOST7 (Messages.getString("MonsterDetailsSection.mod.magicboost"), "0", "1"),
		MAGICBOOST8 (Messages.getString("MonsterDetailsSection.mod.magicboost"), "0", "1"),
		MASTERRIT (Messages.getString("MonsterDetailsSection.mod.masterrit"), "0"),
		
		RESEARCHBONUS (Messages.getString("MonsterDetailsSection.mod.researchbonus"), "4"),
		INSPIRINGRES (Messages.getString("MonsterDetailsSection.mod.inspiringres"), "0"),
		DIVINEINS (Messages.getString("MonsterDetailsSection.mod.divineins"), "0"),
		DRAINIMMUNE (Messages.getString("MonsterDetailsSection.mod.drainimmune"), ""),
		MAGICIMMUNE (Messages.getString("MonsterDetailsSection.mod.magicimmune"), "0"),

		FIRERANGE (Messages.getString("MonsterDetailsSection.mod.firerange"), "0"),
		AIRRANGE (Messages.getString("MonsterDetailsSection.mod.airrange"), "0"),
		WATERRANGE (Messages.getString("MonsterDetailsSection.mod.waterrange"), "0"),
		EARTHRANGE (Messages.getString("MonsterDetailsSection.mod.earthrange"), "0"),
		ASTRALRANGE (Messages.getString("MonsterDetailsSection.mod.astralrange"), "0"),
		DEATHRANGE (Messages.getString("MonsterDetailsSection.mod.deathrange"), "0"),
		NATURERANGE (Messages.getString("MonsterDetailsSection.mod.naturerange"), "0"),
		BLOODRANGE (Messages.getString("MonsterDetailsSection.mod.bloodrange"), "0"),
		ELEMENTRANGE (Messages.getString("MonsterDetailsSection.mod.elementrange"), "0"),
		SORCERYRANGE (Messages.getString("MonsterDetailsSection.mod.sorceryrange"), "0"),
		ALLRANGE (Messages.getString("MonsterDetailsSection.mod.allrange"), "0"),

		GEMPROD1 (Messages.getString("MonsterDetailsSection.mod.gemprod"), "0", "1"),
		GEMPROD2 (Messages.getString("MonsterDetailsSection.mod.gemprod"), "0", "1"),
		GEMPROD3 (Messages.getString("MonsterDetailsSection.mod.gemprod"), "0", "1"),
		GEMPROD4 (Messages.getString("MonsterDetailsSection.mod.gemprod"), "0", "1"),
		GEMPROD5 (Messages.getString("MonsterDetailsSection.mod.gemprod"), "0", "1"),
		GEMPROD6 (Messages.getString("MonsterDetailsSection.mod.gemprod"), "0", "1"),
		GEMPROD7 (Messages.getString("MonsterDetailsSection.mod.gemprod"), "0", "1"),
		GEMPROD8 (Messages.getString("MonsterDetailsSection.mod.gemprod"), "0", "1"),
		TMPFIREGEMS (Messages.getString("MonsterDetailsSection.mod.tmpfiregems"), "0"),
		TMPAIRGEMS (Messages.getString("MonsterDetailsSection.mod.tmpairgems"), "0"),
		TMPWATERGEMS (Messages.getString("MonsterDetailsSection.mod.tmpwatergems"), "0"),
		TMPEARTHGEMS (Messages.getString("MonsterDetailsSection.mod.tmpearthgems"), "0"),
		TMPASTRALGEMS (Messages.getString("MonsterDetailsSection.mod.tmpastralgems"), "0"),
		TMPDEATHGEMS (Messages.getString("MonsterDetailsSection.mod.tmpdeathgems"), "0"),
		TMPNATUREGEMS (Messages.getString("MonsterDetailsSection.mod.tmpnaturegems"), "0"),
		TMPBLOODSLAVES (Messages.getString("MonsterDetailsSection.mod.tmpbloodslaves"), "0"),
		DOUSE (Messages.getString("MonsterDetailsSection.mod.douse"), "1"),
		MAKEPEARLS (Messages.getString("MonsterDetailsSection.mod.makepearls"), "0"),

		BONUSSPELLS (Messages.getString("MonsterDetailsSection.mod.bonusspells"), "0"),
		ONEBATTLESPELL (Messages.getString("MonsterDetailsSection.mod.onebattlespell"), ""),
		RANDOMSPELL (Messages.getString("MonsterDetailsSection.mod.randomspell"), "0"),
		TAINTED (Messages.getString("MonsterDetailsSection.mod.tainted"), "0"),
		FORGEBONUS (Messages.getString("MonsterDetailsSection.mod.forgebonus"), "10"),
		FIXFORGEBONUS (Messages.getString("MonsterDetailsSection.mod.fixforgebonus"), "0"),
		MASTERSMITH (Messages.getString("MonsterDetailsSection.mod.mastersmith"), "0"),
		COMSLAVE (Messages.getString("MonsterDetailsSection.mod.comslave"), "0"),
		CROSSBREEDER (Messages.getString("MonsterDetailsSection.mod.crossbreeder"), "0"),
		DEATHBANISH (Messages.getString("MonsterDetailsSection.mod.deathbanish"), "0"),
		KOKYTOSRET (Messages.getString("MonsterDetailsSection.mod.kokytosret"), "0"),
		INFERNORET (Messages.getString("MonsterDetailsSection.mod.infernoret"), "0"),
		VOIDRET (Messages.getString("MonsterDetailsSection.mod.voidret"), "0"),
		ALLRET (Messages.getString("MonsterDetailsSection.mod.allret"), "0");

		private String label;
		private String defaultValue;
		private String defaultValue2;
		
		Inst(String label, String defaultValue) {
			this.label = label;
			this.defaultValue = defaultValue;
		}
		
		Inst(String label, String defaultValue, String defaultValue2) {
			this.label = label;
			this.defaultValue = defaultValue;
			this.defaultValue2 = defaultValue2;
		}
		
		Inst(String label) {
			this.label = label;
		}
	}
	
	interface InstFields {}
	
	class Inst1Fields implements InstFields {
		private Button check;
		private Text value;
		private Label defaultLabel;
	}
	
	class Inst2Fields implements InstFields {
		private Button check;
		private Text value;
		private Label defaultLabel;
	}
	
	class Inst3Fields implements InstFields {
		private Button check;
		private MappedDynamicCombo value1;
		private Text value2;
		private Label defaultLabel1;
		private Label defaultLabel2;
	}
	
	class Inst4Fields implements InstFields {
		private Button check;
		private Label defaultLabel;
	}

	class Inst5Fields implements InstFields {
		private Button check;
		private Text value;
		private Label defaultLabel;
	}
	
	class Inst6Fields implements InstFields {
		private Button check;
		private Text value;
		private Label defaultLabel;
	}
	
	class Inst7Fields implements InstFields {
		private Button check;
		private Text value1;
		private Text value2;
		private Label defaultLabel1;
		private Label defaultLabel2;
	}
	
	class Inst8Fields implements InstFields {
		private Button check;
		private MappedDynamicCombo value1;
		private MappedDynamicCombo value2;
		private MappedDynamicCombo value3;
		private MappedDynamicCombo value4;
		private MappedDynamicCombo value5;
		private Label defaultLabel;
	}
	
	private EnumMap<Inst, InstFields> instMap = new EnumMap<Inst, InstFields>(Inst.class);
	private Set<List<Inst>> dynamicFields = new HashSet<List<Inst>>();
	
	public MonsterDetailsPage(XtextEditor doc, TableViewer viewer) {
		super(doc, viewer);
		instMap.put(Inst.AP, new Inst2Fields());
		instMap.put(Inst.MAPMOVE, new Inst2Fields());
		instMap.put(Inst.HP, new Inst2Fields());
		instMap.put(Inst.PROT, new Inst2Fields());
		instMap.put(Inst.SIZE, new Inst2Fields());
		instMap.put(Inst.RESSIZE, new Inst2Fields());
		instMap.put(Inst.STR, new Inst2Fields());
		instMap.put(Inst.ENC, new Inst2Fields());
		instMap.put(Inst.ATT, new Inst2Fields());
		instMap.put(Inst.DEF, new Inst2Fields());
		instMap.put(Inst.PREC, new Inst2Fields());
		instMap.put(Inst.MR, new Inst2Fields());
		instMap.put(Inst.MOR, new Inst2Fields());
		instMap.put(Inst.GCOST, new Inst2Fields());
		instMap.put(Inst.RCOST, new Inst2Fields());
		instMap.put(Inst.PATHCOST, new Inst2Fields());
		instMap.put(Inst.STARTDOM, new Inst2Fields());
		instMap.put(Inst.EYES, new Inst2Fields());
		instMap.put(Inst.VOIDSANITY, new Inst2Fields());
		instMap.put(Inst.COPYSTATS, new Inst2Fields());
		instMap.put(Inst.COPYSPR, new Inst2Fields());
		instMap.put(Inst.SHATTEREDSOUL, new Inst2Fields());
		instMap.put(Inst.COLDRES, new Inst2Fields());
		instMap.put(Inst.FIRERES, new Inst2Fields());
		instMap.put(Inst.POISONRES, new Inst2Fields());
		instMap.put(Inst.SHOCKRES, new Inst2Fields());
		instMap.put(Inst.DARKVISION, new Inst2Fields());
		instMap.put(Inst.STEALTHY, new Inst6Fields());
		instMap.put(Inst.SEDUCE, new Inst2Fields());
		instMap.put(Inst.SUCCUBUS, new Inst2Fields());
		instMap.put(Inst.BECKON, new Inst2Fields());
		instMap.put(Inst.STARTAGE, new Inst2Fields());
		instMap.put(Inst.MAXAGE, new Inst2Fields());
		instMap.put(Inst.OLDER, new Inst2Fields());
		instMap.put(Inst.HEALER, new Inst2Fields());
		instMap.put(Inst.STARTAFF, new Inst2Fields());
		instMap.put(Inst.SUPPLYBONUS, new Inst2Fields());
		instMap.put(Inst.RESOURCES, new Inst2Fields());
		instMap.put(Inst.UWDAMAGE, new Inst2Fields());
		instMap.put(Inst.HOMESICK, new Inst2Fields());
		instMap.put(Inst.COLDPOWER, new Inst2Fields());
		instMap.put(Inst.FIREPOWER, new Inst2Fields());
		instMap.put(Inst.STORMPOWER, new Inst2Fields());
		instMap.put(Inst.DARKPOWER, new Inst2Fields());
		instMap.put(Inst.SPRINGPOWER, new Inst2Fields());
		instMap.put(Inst.SUMMERPOWER, new Inst2Fields());
		instMap.put(Inst.FALLPOWER, new Inst2Fields());
		instMap.put(Inst.WINTERPOWER, new Inst2Fields());
		instMap.put(Inst.AMBIDEXTROUS, new Inst2Fields());
		instMap.put(Inst.BANEFIRESHIELD, new Inst2Fields());
		instMap.put(Inst.BERSERK, new Inst2Fields());
		instMap.put(Inst.STANDARD, new Inst2Fields());
		instMap.put(Inst.ANIMALAWE, new Inst2Fields());
		instMap.put(Inst.AWE, new Inst2Fields());
		instMap.put(Inst.FEAR, new Inst2Fields());
		instMap.put(Inst.REGENERATION, new Inst2Fields());
		instMap.put(Inst.REINVIGORATION, new Inst2Fields());
		instMap.put(Inst.FIRESHIELD, new Inst2Fields());
		instMap.put(Inst.HEAT, new Inst6Fields());
		instMap.put(Inst.COLD, new Inst6Fields());
		instMap.put(Inst.ICEPROT, new Inst2Fields());
		instMap.put(Inst.INVULNERABLE, new Inst2Fields());
		instMap.put(Inst.POISONCLOUD, new Inst2Fields());
		instMap.put(Inst.DISEASECLOUD, new Inst2Fields());
		instMap.put(Inst.BLOODVENGEANCE, new Inst2Fields());
		instMap.put(Inst.CASTLEDEF, new Inst2Fields());
		instMap.put(Inst.SIEGEBONUS, new Inst2Fields());
		instMap.put(Inst.PATROLBONUS, new Inst2Fields());
		instMap.put(Inst.PILLAGEBONUS, new Inst2Fields());
		instMap.put(Inst.MASTERRIT, new Inst2Fields());
		instMap.put(Inst.RESEARCHBONUS, new Inst2Fields());
		instMap.put(Inst.INSPIRINGRES, new Inst2Fields());
		instMap.put(Inst.FORGEBONUS, new Inst2Fields());
		instMap.put(Inst.DOUSE, new Inst2Fields());
		instMap.put(Inst.NOBADEVENTS, new Inst2Fields());
		instMap.put(Inst.INCUNREST, new Inst2Fields());
		instMap.put(Inst.SPREADDOM, new Inst2Fields());
		instMap.put(Inst.LEPER, new Inst2Fields());
		instMap.put(Inst.POPKILL, new Inst2Fields());
		instMap.put(Inst.HERETIC, new Inst2Fields());
		instMap.put(Inst.ITEMSLOTS, new Inst8Fields());
		instMap.put(Inst.NAMETYPE, new Inst2Fields());
		instMap.put(Inst.MAGICSKILL1, new Inst3Fields());
		instMap.put(Inst.MAGICSKILL2, new Inst3Fields());
		instMap.put(Inst.MAGICSKILL3, new Inst3Fields());
		instMap.put(Inst.MAGICSKILL4, new Inst3Fields());
		instMap.put(Inst.MAGICSKILL5, new Inst3Fields());
		instMap.put(Inst.MAGICSKILL6, new Inst3Fields());
		instMap.put(Inst.MAGICSKILL7, new Inst3Fields());
		instMap.put(Inst.MAGICSKILL8, new Inst3Fields());
		instMap.put(Inst.CUSTOMMAGIC1, new Inst7Fields());
		instMap.put(Inst.CUSTOMMAGIC2, new Inst7Fields());
		instMap.put(Inst.CUSTOMMAGIC3, new Inst7Fields());
		instMap.put(Inst.CUSTOMMAGIC4, new Inst7Fields());
		instMap.put(Inst.CUSTOMMAGIC5, new Inst7Fields());
		instMap.put(Inst.CUSTOMMAGIC6, new Inst7Fields());
		instMap.put(Inst.CUSTOMMAGIC7, new Inst7Fields());
		instMap.put(Inst.CUSTOMMAGIC8, new Inst7Fields());
		instMap.put(Inst.MAGICBOOST1, new Inst3Fields());
		instMap.put(Inst.MAGICBOOST2, new Inst3Fields());
		instMap.put(Inst.MAGICBOOST3, new Inst3Fields());
		instMap.put(Inst.MAGICBOOST4, new Inst3Fields());
		instMap.put(Inst.MAGICBOOST5, new Inst3Fields());
		instMap.put(Inst.MAGICBOOST6, new Inst3Fields());
		instMap.put(Inst.MAGICBOOST7, new Inst3Fields());
		instMap.put(Inst.MAGICBOOST8, new Inst3Fields());
		instMap.put(Inst.GEMPROD1, new Inst3Fields());
		instMap.put(Inst.GEMPROD2, new Inst3Fields());
		instMap.put(Inst.GEMPROD3, new Inst3Fields());
		instMap.put(Inst.GEMPROD4, new Inst3Fields());
		instMap.put(Inst.GEMPROD5, new Inst3Fields());
		instMap.put(Inst.GEMPROD6, new Inst3Fields());
		instMap.put(Inst.GEMPROD7, new Inst3Fields());
		instMap.put(Inst.GEMPROD8, new Inst3Fields());
		instMap.put(Inst.CLEAR, new Inst4Fields());
		instMap.put(Inst.CLEARWEAPONS, new Inst4Fields());
		instMap.put(Inst.CLEARARMOR, new Inst4Fields());
		instMap.put(Inst.CLEARMAGIC, new Inst4Fields());
		instMap.put(Inst.CLEARSPEC, new Inst4Fields());
		instMap.put(Inst.FEMALE, new Inst4Fields());
		instMap.put(Inst.MOUNTED, new Inst4Fields());
		instMap.put(Inst.HOLY, new Inst4Fields());
		instMap.put(Inst.ANIMAL, new Inst4Fields());
		instMap.put(Inst.UNDEAD, new Inst4Fields());
		instMap.put(Inst.DEMON, new Inst4Fields());
		instMap.put(Inst.MAGICBEING, new Inst4Fields());
		instMap.put(Inst.STONEBEING, new Inst4Fields());
		instMap.put(Inst.INANIMATE, new Inst4Fields());
		instMap.put(Inst.COLDBLOOD, new Inst4Fields());
		instMap.put(Inst.IMMORTAL, new Inst4Fields());
		instMap.put(Inst.BLIND, new Inst4Fields());
		instMap.put(Inst.UNIQUE, new Inst4Fields());
		instMap.put(Inst.IMMOBILE, new Inst4Fields());
		instMap.put(Inst.AQUATIC, new Inst4Fields());
		instMap.put(Inst.AMPHIBIAN, new Inst4Fields());
		instMap.put(Inst.POORAMPHIBIAN, new Inst4Fields());
		instMap.put(Inst.FLYING, new Inst4Fields());
		instMap.put(Inst.STORMIMMUNE, new Inst4Fields());
		instMap.put(Inst.SAILING, new Inst7Fields());
		instMap.put(Inst.FORESTSURVIVAL, new Inst4Fields());
		instMap.put(Inst.MOUNTAINSURVIVAL, new Inst4Fields());
		instMap.put(Inst.SWAMPSURVIVAL, new Inst4Fields());
		instMap.put(Inst.WASTESURVIVAL, new Inst4Fields());
		instMap.put(Inst.ILLUSION, new Inst4Fields());
		instMap.put(Inst.SPY, new Inst4Fields());
		instMap.put(Inst.ASSASSIN, new Inst4Fields());
		instMap.put(Inst.HEAL, new Inst4Fields());
		instMap.put(Inst.NOHEAL, new Inst4Fields());
		instMap.put(Inst.NEEDNOTEAT, new Inst4Fields());
		instMap.put(Inst.ETHEREAL, new Inst4Fields());
		instMap.put(Inst.TRAMPLE, new Inst4Fields());
		instMap.put(Inst.ENTANGLE, new Inst4Fields());
		instMap.put(Inst.EYELOSS, new Inst4Fields());
		instMap.put(Inst.HORRORMARK, new Inst4Fields());
		instMap.put(Inst.POISONARMOR, new Inst4Fields());
		instMap.put(Inst.INQUISITOR, new Inst4Fields());
		instMap.put(Inst.NOITEM, new Inst4Fields());
		instMap.put(Inst.DRAINIMMUNE, new Inst4Fields());
		instMap.put(Inst.NOLEADER, new Inst4Fields());
		instMap.put(Inst.POORLEADER, new Inst4Fields());
		instMap.put(Inst.OKLEADER, new Inst4Fields());
		instMap.put(Inst.GOODLEADER, new Inst4Fields());
		instMap.put(Inst.EXPERTLEADER, new Inst4Fields());
		instMap.put(Inst.SUPERIORLEADER, new Inst4Fields());
		instMap.put(Inst.NOMAGICLEADER, new Inst4Fields());
		instMap.put(Inst.POORMAGICLEADER, new Inst4Fields());
		instMap.put(Inst.OKMAGICLEADER, new Inst4Fields());
		instMap.put(Inst.GOODMAGICLEADER, new Inst4Fields());
		instMap.put(Inst.EXPERTMAGICLEADER, new Inst4Fields());
		instMap.put(Inst.SUPERIORMAGICLEADER, new Inst4Fields());
		instMap.put(Inst.NOUNDEADLEADER, new Inst4Fields());
		instMap.put(Inst.POORUNDEADLEADER, new Inst4Fields());
		instMap.put(Inst.OKUNDEADLEADER, new Inst4Fields());
		instMap.put(Inst.GOODUNDEADLEADER, new Inst4Fields());
		instMap.put(Inst.EXPERTUNDEADLEADER, new Inst4Fields());
		instMap.put(Inst.SUPERIORUNDEADLEADER, new Inst4Fields());
		instMap.put(Inst.WEAPON1, new Inst5Fields());
		instMap.put(Inst.WEAPON2, new Inst5Fields());
		instMap.put(Inst.WEAPON3, new Inst5Fields());
		instMap.put(Inst.WEAPON4, new Inst5Fields());
		instMap.put(Inst.ARMOR1, new Inst5Fields());
		instMap.put(Inst.ARMOR2, new Inst5Fields());
		instMap.put(Inst.ARMOR3, new Inst5Fields());
		instMap.put(Inst.ONEBATTLESPELL, new Inst5Fields());
		instMap.put(Inst.FIRSTSHAPE, new Inst5Fields());
		instMap.put(Inst.SECONDSHAPE, new Inst5Fields());
		instMap.put(Inst.SECONDTMPSHAPE, new Inst5Fields());
		instMap.put(Inst.CLEANSHAPE, new Inst4Fields());
		instMap.put(Inst.SHAPECHANGE, new Inst5Fields());
		instMap.put(Inst.LANDSHAPE, new Inst5Fields());
		instMap.put(Inst.WATERSHAPE, new Inst5Fields());
		instMap.put(Inst.FORESTSHAPE, new Inst5Fields());
		instMap.put(Inst.PLAINSHAPE, new Inst5Fields());
		instMap.put(Inst.DOMSUMMON, new Inst5Fields());
		instMap.put(Inst.DOMSUMMON2, new Inst5Fields());
		instMap.put(Inst.DOMSUMMON20, new Inst5Fields());
		instMap.put(Inst.MAKEMONSTERS1, new Inst5Fields());
		instMap.put(Inst.MAKEMONSTERS2, new Inst5Fields());
		instMap.put(Inst.MAKEMONSTERS3, new Inst5Fields());
		instMap.put(Inst.MAKEMONSTERS4, new Inst5Fields());
		instMap.put(Inst.MAKEMONSTERS5, new Inst5Fields());
		instMap.put(Inst.SUMMON1, new Inst5Fields());
		instMap.put(Inst.SUMMON2, new Inst5Fields());
		instMap.put(Inst.SUMMON3, new Inst5Fields());
		instMap.put(Inst.SUMMON4, new Inst5Fields());
		instMap.put(Inst.SUMMON5, new Inst5Fields());
		instMap.put(Inst.SLOWREC, new Inst4Fields());
		instMap.put(Inst.NOSLOWREC, new Inst4Fields());
		instMap.put(Inst.RECLIMIT, new Inst2Fields());
		instMap.put(Inst.REQLAB, new Inst4Fields());
		instMap.put(Inst.REQTEMPLE, new Inst4Fields());
		instMap.put(Inst.CHAOSREC, new Inst2Fields());
		instMap.put(Inst.SINGLEBATTLE, new Inst4Fields());
		instMap.put(Inst.AISINGLEREC, new Inst4Fields());
		instMap.put(Inst.AINOREC, new Inst4Fields());
		instMap.put(Inst.HOMEREALM, new Inst2Fields());
		instMap.put(Inst.LESSERHORROR, new Inst4Fields());
		instMap.put(Inst.GREATERHORROR, new Inst4Fields());
		instMap.put(Inst.DOOMHORROR, new Inst4Fields());
		instMap.put(Inst.BUG, new Inst4Fields());
		instMap.put(Inst.UWBUG, new Inst4Fields());
		instMap.put(Inst.AUTOCOMPETE, new Inst4Fields());
		instMap.put(Inst.FLOAT, new Inst4Fields());
		instMap.put(Inst.TELEPORT, new Inst4Fields());
		instMap.put(Inst.NORIVERPASS, new Inst4Fields());
		instMap.put(Inst.UNTELEPORTABLE, new Inst4Fields());
		instMap.put(Inst.GIFTOFWATER, new Inst2Fields());
		instMap.put(Inst.INDEPMOVE, new Inst2Fields());
		instMap.put(Inst.PATIENCE, new Inst2Fields());
		instMap.put(Inst.FALSEARMY, new Inst2Fields());
		instMap.put(Inst.FOOLSCOUTS, new Inst2Fields());
		instMap.put(Inst.DESERTER, new Inst2Fields());
		instMap.put(Inst.HORRORDESERTER, new Inst2Fields());
		instMap.put(Inst.DEFECTOR, new Inst2Fields());
		instMap.put(Inst.AUTOHEALER, new Inst2Fields());
		instMap.put(Inst.AUTODISHEALER, new Inst2Fields());
		instMap.put(Inst.AUTODISGRINDER, new Inst2Fields());
		instMap.put(Inst.WOUNDFEND, new Inst2Fields());
		instMap.put(Inst.HPOVERFLOW, new Inst4Fields());
		instMap.put(Inst.PIERCERES, new Inst4Fields());
		instMap.put(Inst.SLASHRES, new Inst4Fields());
		instMap.put(Inst.BLUNTRES, new Inst4Fields());
		instMap.put(Inst.DAMAGEREV, new Inst2Fields());
		instMap.put(Inst.SLIMER, new Inst2Fields());
		instMap.put(Inst.DEATHCURSE, new Inst4Fields());
		instMap.put(Inst.DEATHDISEASE, new Inst2Fields());
		instMap.put(Inst.DEATHPARALYZE, new Inst2Fields());
		instMap.put(Inst.DEATHFIRE, new Inst2Fields());
		instMap.put(Inst.CHAOSPOWER, new Inst2Fields());
		instMap.put(Inst.MAGICPOWER, new Inst2Fields());
		instMap.put(Inst.TRAMPSWALLOW, new Inst4Fields());
		instMap.put(Inst.DIGEST, new Inst2Fields());
		instMap.put(Inst.INCORPORATE, new Inst2Fields());
		instMap.put(Inst.INCPROVDEF, new Inst2Fields());
		instMap.put(Inst.ELEGIST, new Inst2Fields());
		instMap.put(Inst.TAXCOLLECTOR, new Inst4Fields());
		instMap.put(Inst.GOLD, new Inst2Fields());
		instMap.put(Inst.NOHOF, new Inst4Fields());
		instMap.put(Inst.GROWHP, new Inst2Fields());
		instMap.put(Inst.SHRINKHP, new Inst2Fields());
		instMap.put(Inst.REANIMATOR, new Inst2Fields());
		instMap.put(Inst.RAREDOMSUMMON, new Inst5Fields());
		instMap.put(Inst.BATTLESUM1, new Inst5Fields());
		instMap.put(Inst.BATTLESUM2, new Inst5Fields());
		instMap.put(Inst.BATTLESUM3, new Inst5Fields());
		instMap.put(Inst.BATTLESUM4, new Inst5Fields());
		instMap.put(Inst.BATTLESUM5, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM1, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM2, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM3, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM4, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM5, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM1D6, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM2D6, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM3D6, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM4D6, new Inst5Fields());
		instMap.put(Inst.BATSTARTSUM5D6, new Inst5Fields());
		instMap.put(Inst.MONTAG, new Inst2Fields());
		instMap.put(Inst.INSPIRATIONAL, new Inst2Fields());
		instMap.put(Inst.BEASTMASTER, new Inst2Fields());
		instMap.put(Inst.TASKMASTER, new Inst2Fields());
		instMap.put(Inst.SLAVE, new Inst4Fields());
		instMap.put(Inst.UNDISCIPLINED, new Inst4Fields());
		instMap.put(Inst.FORMATIONFIGHTER, new Inst2Fields());
		instMap.put(Inst.BODYGUARD, new Inst2Fields());
		instMap.put(Inst.DIVINEINS, new Inst2Fields());
		instMap.put(Inst.MAGICIMMUNE, new Inst4Fields());
		instMap.put(Inst.FIRERANGE, new Inst2Fields());
		instMap.put(Inst.AIRRANGE, new Inst2Fields());
		instMap.put(Inst.WATERRANGE, new Inst2Fields());
		instMap.put(Inst.EARTHRANGE, new Inst2Fields());
		instMap.put(Inst.ASTRALRANGE, new Inst2Fields());
		instMap.put(Inst.DEATHRANGE, new Inst2Fields());
		instMap.put(Inst.NATURERANGE, new Inst2Fields());
		instMap.put(Inst.BLOODRANGE, new Inst2Fields());
		instMap.put(Inst.ELEMENTRANGE, new Inst2Fields());
		instMap.put(Inst.SORCERYRANGE, new Inst2Fields());
		instMap.put(Inst.ALLRANGE, new Inst2Fields());
		instMap.put(Inst.TMPFIREGEMS, new Inst2Fields());
		instMap.put(Inst.TMPAIRGEMS, new Inst2Fields());
		instMap.put(Inst.TMPWATERGEMS, new Inst2Fields());
		instMap.put(Inst.TMPEARTHGEMS, new Inst2Fields());
		instMap.put(Inst.TMPASTRALGEMS, new Inst2Fields());
		instMap.put(Inst.TMPDEATHGEMS, new Inst2Fields());
		instMap.put(Inst.TMPNATUREGEMS, new Inst2Fields());
		instMap.put(Inst.TMPBLOODSLAVES, new Inst2Fields());
		instMap.put(Inst.MAKEPEARLS, new Inst2Fields());
		instMap.put(Inst.BONUSSPELLS, new Inst2Fields());
		instMap.put(Inst.RANDOMSPELL, new Inst2Fields());
		instMap.put(Inst.TAINTED, new Inst2Fields());
		instMap.put(Inst.FIXFORGEBONUS, new Inst2Fields());
		instMap.put(Inst.MASTERSMITH, new Inst2Fields());
		instMap.put(Inst.COMSLAVE, new Inst4Fields());
		instMap.put(Inst.CROSSBREEDER, new Inst2Fields());
		instMap.put(Inst.DEATHBANISH, new Inst2Fields());
		instMap.put(Inst.KOKYTOSRET, new Inst2Fields());
		instMap.put(Inst.INFERNORET, new Inst2Fields());
		instMap.put(Inst.VOIDRET, new Inst2Fields());
		instMap.put(Inst.ALLRET, new Inst2Fields());

		List<Inst> magicList = new ArrayList<Inst>();
		magicList.add(Inst.MAGICSKILL1);
		magicList.add(Inst.MAGICSKILL2);
		magicList.add(Inst.MAGICSKILL3);
		magicList.add(Inst.MAGICSKILL4);
		magicList.add(Inst.MAGICSKILL5);
		magicList.add(Inst.MAGICSKILL6);
		magicList.add(Inst.MAGICSKILL7);
		magicList.add(Inst.MAGICSKILL8);
		dynamicFields.add(magicList);
		List<Inst> customList = new ArrayList<Inst>();
		customList.add(Inst.CUSTOMMAGIC1);
		customList.add(Inst.CUSTOMMAGIC2);
		customList.add(Inst.CUSTOMMAGIC3);
		customList.add(Inst.CUSTOMMAGIC4);
		customList.add(Inst.CUSTOMMAGIC5);
		customList.add(Inst.CUSTOMMAGIC6);
		customList.add(Inst.CUSTOMMAGIC7);
		customList.add(Inst.CUSTOMMAGIC8);
		dynamicFields.add(customList);
		List<Inst> boostList = new ArrayList<Inst>();
		boostList.add(Inst.MAGICBOOST1);
		boostList.add(Inst.MAGICBOOST2);
		boostList.add(Inst.MAGICBOOST3);
		boostList.add(Inst.MAGICBOOST4);
		boostList.add(Inst.MAGICBOOST5);
		boostList.add(Inst.MAGICBOOST6);
		boostList.add(Inst.MAGICBOOST7);
		boostList.add(Inst.MAGICBOOST8);
		dynamicFields.add(boostList);
		List<Inst> gemList = new ArrayList<Inst>();
		gemList.add(Inst.GEMPROD1);
		gemList.add(Inst.GEMPROD2);
		gemList.add(Inst.GEMPROD3);
		gemList.add(Inst.GEMPROD4);
		gemList.add(Inst.GEMPROD5);
		gemList.add(Inst.GEMPROD6);
		gemList.add(Inst.GEMPROD7);
		gemList.add(Inst.GEMPROD8);
		dynamicFields.add(gemList);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(final Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 2;
		parent.setLayout(layout);

		final FormToolkit toolkit = mform.getToolkit();
		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		s1.marginWidth = 10;
		s1.setText(Messages.getString("MonsterDetailsSection.name"));
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		td.grabHorizontal = true;
		s1.setLayoutData(td);
		
		final Composite client = toolkit.createComposite(parent);
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = glayout.marginHeight = 0;
		glayout.numColumns = 2;
		glayout.makeColumnsEqualWidth = true;
		client.setLayout(glayout);
		
		final Composite nameComp = toolkit.createComposite(client);
		glayout = new GridLayout(3, false);
		glayout.marginHeight = 0;
		glayout.verticalSpacing = 2;
		glayout.marginWidth = 0;
		nameComp.setLayout(glayout);
		GridData gd = new GridData(SWT.DEFAULT, SWT.FILL, false, false);
		gd.horizontalSpan = 2;
		nameComp.setLayoutData(gd);
		
		nameCheck = toolkit.createButton(nameComp, Messages.getString("MonsterDetailsSection.mod.name"), SWT.CHECK); //$NON-NLS-1$
		setToolTip(nameCheck, HelpTextHelper.getText(HelpTextHelper.MONSTER_CATEGORY, "name"));
		
		name = toolkit.createText(nameComp, null, SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		name.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setMonstername(doc, name.getText());
			}			
		});
		name.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					setMonstername(doc, name.getText());
				}
			}
			
		});
		
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 500;
		gd.horizontalSpan = 2;
		name.setLayoutData(gd);
		nameCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (nameCheck.getSelection()) {
					addInst1(Inst.NAME, doc, "");
					name.setEnabled(true);
					name.setText("");
					nameCheck.setFont(boldFont);
				} else {
					removeInst(Inst.NAME, doc);
					name.setEnabled(false);
					if (input instanceof SelectMonsterById || input instanceof SelectMonsterByName) {
						String monsterName = getSelectMonstername((Monster)input);
						name.setText(monsterName != null ? monsterName : "");
					} else {
						name.setText("");
					}
					nameCheck.setFont(normalFont);
				}
			}
		});

		fixedNameCheck = toolkit.createButton(nameComp, Messages.getString("MonsterDetailsSection.mod.fixedname"), SWT.CHECK); //$NON-NLS-1$
		setToolTip(fixedNameCheck, HelpTextHelper.getText(HelpTextHelper.MONSTER_CATEGORY, "fixedname"));

		fixedName = toolkit.createText(nameComp, null, SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		fixedName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setInst1(Inst.FIXEDNAME, doc, fixedName.getText());
			}			
		});
		fixedName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					setInst1(Inst.FIXEDNAME, doc, fixedName.getText());
				}
			}
			
		});
		fixedName.setEnabled(false);
		
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 500;
		gd.horizontalSpan = 2;
		fixedName.setLayoutData(gd);
		fixedNameCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (fixedNameCheck.getSelection()) {
					addInst1(Inst.FIXEDNAME, doc, "");
					fixedName.setEnabled(true);
					fixedName.setText("");
					fixedNameCheck.setFont(boldFont);
				} else {
					removeInst(Inst.FIXEDNAME, doc);
					fixedName.setEnabled(false);
					fixedName.setText("");
					fixedNameCheck.setFont(normalFont);
				}
			}
		});

		descCheck = toolkit.createButton(nameComp, Messages.getString("MonsterDetailsSection.mod.descr"), SWT.CHECK);
		setToolTip(descCheck, HelpTextHelper.getText(HelpTextHelper.MONSTER_CATEGORY, "descr"));

		descr = toolkit.createText(nameComp, null, SWT.MULTI | SWT.BORDER | SWT.WRAP); //$NON-NLS-1$
		descr.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setMonsterdescr(doc, descr.getText());
			}			
		});
		descr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					setMonsterdescr(doc, descr.getText());
				}
			}
			
		});
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 500;
		gd.horizontalSpan = 2;
		descr.setLayoutData(gd);
		descr.addListener(SWT.Modify, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				int currentHeight = descr.getSize().y;
				int preferredHeight = descr.computeSize(500, SWT.DEFAULT).y;
				if (currentHeight < preferredHeight || currentHeight > 1.5*preferredHeight) {
					GridData data = (GridData)descr.getLayoutData();
					data.heightHint = preferredHeight;
					client.pack();
				}
			}
		});
		descr.setEnabled(false);
		descr.setBackground(toolkit.getColors().getInactiveBackground());
		descCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (descCheck.getSelection()) {
					addInst1(Inst.DESCR, doc, "");
					descr.setEnabled(true);
					descr.setBackground(toolkit.getColors().getBackground());
					descr.setText(getSelectMonsterdescr((Monster)input));
					descCheck.setFont(boldFont);
				} else {
					removeInst(Inst.DESCR, doc);
					descr.setEnabled(false);
					descr.setBackground(toolkit.getColors().getInactiveBackground());
					descr.setText(getSelectMonsterdescr((Monster)input));
					descCheck.setFont(normalFont);
				}
			}
		});

		Composite spriteComp = toolkit.createComposite(nameComp);
		spriteComp.setLayout(new GridLayout(2, false));
		gd = new GridData();
		gd.horizontalSpan = 3;
		spriteComp.setLayoutData(gd);
		sprite1Label = new Label(spriteComp, SWT.NONE);
		GridData gridData = new GridData(SWT.DEFAULT, SWT.BOTTOM, false, false);
		sprite1Label.setLayoutData(gridData);
		sprite2Label = new Label(spriteComp, SWT.NONE);
		gridData = new GridData(SWT.DEFAULT, SWT.BOTTOM, false, false);
		sprite2Label.setLayoutData(gridData);
		
		spr1Check = toolkit.createButton(nameComp, Messages.getString("MonsterDetailsSection.mod.spr1"), SWT.CHECK);
		setToolTip(spr1Check, HelpTextHelper.getText(HelpTextHelper.MONSTER_CATEGORY, "spr1"));

		spr1 = toolkit.createText(nameComp, null, SWT.BORDER); //$NON-NLS-1$
		spr1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setInst1(Inst.SPR1, doc, spr1.getText());
				sprite1Label.setImage(getSprite(spr1.getText()));
				update();
			}			
		});
		spr1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					setInst1(Inst.SPR1, doc, spr1.getText());
					sprite1Label.setImage(getSprite(spr1.getText()));
					update();
				}
			}
			
		});
		spr1.setLayoutData(new GridData(450, SWT.DEFAULT));
		spr1Browse = toolkit.createButton(nameComp, "Browse...", SWT.PUSH);
		spr1Browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[]{"*.tga", "*.rgb", "*.sgi"});
				if (dialog.open() != null) {
					String targetpath = new File(dialog.getFilterPath() + File.separator + dialog.getFileName()).getAbsolutePath();
					String basepath = ((DmXtextEditor)doc).getPath();
					String relativepath = ResourceUtils.getRelativePath(targetpath, basepath, "/");
					spr1.setText("./"+relativepath);
					setInst1(Inst.SPR1, doc, spr1.getText());
					sprite1Label.setImage(getSprite(spr1.getText()));
					update();
				}
			}
		}); 
		spr1Browse.setEnabled(false);
		spr1Check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (spr1Check.getSelection()) {
					addInst1(Inst.SPR1, doc, "");
					spr1.setEnabled(true);
					spr1.setText("");
					spr1Check.setFont(boldFont);
					spr1Browse.setEnabled(true);
				} else {
					removeInst(Inst.SPR1, doc);
					spr1.setEnabled(false);
					spr1.setText("");
					spr1Check.setFont(normalFont);
					spr1Browse.setEnabled(false);
				}
			}
		});

		spr2Check = toolkit.createButton(nameComp, Messages.getString("MonsterDetailsSection.mod.spr2"), SWT.CHECK);
		setToolTip(spr2Check, HelpTextHelper.getText(HelpTextHelper.MONSTER_CATEGORY, "spr2"));

		spr2 = toolkit.createText(nameComp, null, SWT.BORDER); //$NON-NLS-1$
		spr2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setInst1(Inst.SPR2, doc, spr2.getText());
				sprite2Label.setImage(getSprite(spr2.getText()));
				update();
			}			
		});
		spr2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					setInst1(Inst.SPR2, doc, spr2.getText());
					sprite2Label.setImage(getSprite(spr2.getText()));
					update();
				}
			}
			
		});
		spr2.setLayoutData(new GridData(450, SWT.DEFAULT));
		spr2Browse = toolkit.createButton(nameComp, "Browse...", SWT.PUSH);
		spr2Browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[]{"*.tga", "*.rgb", "*.sgi"});
				if (dialog.open() != null) {
					String targetpath = new File(dialog.getFilterPath() + File.separator + dialog.getFileName()).getAbsolutePath();
					String basepath = ((DmXtextEditor)doc).getPath();
					String relativepath = ResourceUtils.getRelativePath(targetpath, basepath, "/");
					spr2.setText("./"+relativepath);
					setInst1(Inst.SPR2, doc, spr2.getText());
					sprite2Label.setImage(getSprite(spr2.getText()));
					update();
				}
			}
		}); 
		spr2Browse.setEnabled(false);
		spr2Check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (spr2Check.getSelection()) {
					addInst1(Inst.SPR2, doc, "");
					spr2.setEnabled(true);
					spr2.setText("");
					spr2Browse.setEnabled(true);
					spr2Check.setFont(boldFont);
				} else {
					removeInst(Inst.SPR2, doc);
					spr2.setEnabled(false);
					spr2.setText("");
					spr2Browse.setEnabled(false);
					spr2Check.setFont(normalFont);
				}
			}
		});

		specialLookCheck = toolkit.createButton(nameComp, Messages.getString("MonsterDetailsSection.mod.speciallook"), SWT.CHECK); //$NON-NLS-1$
		setToolTip(specialLookCheck, HelpTextHelper.getText(HelpTextHelper.MONSTER_CATEGORY, "speciallook"));

		specialLook = toolkit.createText(nameComp, null, SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		specialLook.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setInst2(Inst.SPECIALLOOK, doc, specialLook.getText());
			}			
		});
		specialLook.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					setInst2(Inst.SPECIALLOOK, doc, specialLook.getText());
				}
			}
			
		});
		specialLook.setEnabled(false);
		
		gd = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gd.widthHint = DEFAULT_VALUE_WIDTH;
		specialLook.setLayoutData(gd);
		specialLookCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (specialLookCheck.getSelection()) {
					addInst2(Inst.SPECIALLOOK, doc, "1");
					specialLook.setEnabled(true);
					specialLook.setText("1");
					specialLookCheck.setFont(boldFont);
				} else {
					removeInst(Inst.SPECIALLOOK, doc);
					specialLook.setEnabled(false);
					specialLook.setText("");
					specialLookCheck.setFont(normalFont);
				}
			}
		});

		Composite leftColumn = null;
		Composite rightColumn = null;
		boolean isRight = false;
		for (final Map.Entry<Inst, InstFields> fields : instMap.entrySet()) {
			final Inst key = fields.getKey();
			
			if (key.equals(Inst.HP) || 
				key.equals(Inst.CLEAR) || 
				key.equals(Inst.SLOWREC) ||
				key.equals(Inst.GCOST) ||
				key.equals(Inst.WEAPON1) ||
				key.equals(Inst.PATHCOST) ||
				key.equals(Inst.FEMALE) || 
				key.equals(Inst.IMMOBILE) || 
				key.equals(Inst.STEALTHY) || 
				key.equals(Inst.SINGLEBATTLE) || 
				key.equals(Inst.STARTAGE) || 
				key.equals(Inst.PIERCERES) || 
				key.equals(Inst.HEAT) || 
				key.equals(Inst.CHAOSPOWER) || 
				key.equals(Inst.SPRINGPOWER) || 
				key.equals(Inst.AMBIDEXTROUS) || 
				key.equals(Inst.CASTLEDEF) || 
				key.equals(Inst.FIRSTSHAPE) || 
				key.equals(Inst.REANIMATOR) || 
				key.equals(Inst.NAMETYPE) || 
				key.equals(Inst.ITEMSLOTS) || 
				key.equals(Inst.NOLEADER) ||
				key.equals(Inst.NOMAGICLEADER) ||
				key.equals(Inst.NOUNDEADLEADER) ||
				key.equals(Inst.INSPIRATIONAL) ||
				key.equals(Inst.MAGICSKILL1) ||
				key.equals(Inst.RESEARCHBONUS) ||
				key.equals(Inst.FIRERANGE) ||
				key.equals(Inst.GEMPROD1) ||
				key.equals(Inst.BONUSSPELLS)) {
				
				final Section expandable = toolkit.createSection(client, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
				switch (key) {
				case HP:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.basic"));
					break;
				case CLEAR:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.clearing"));
					break;
				case SLOWREC:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.recruit"));
					break;
				case GCOST:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.gcost"));
					break;
				case WEAPON1:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.weapon"));
					break;
				case PATHCOST:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.pretender"));
					break;
				case FEMALE:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.type"));
					break;
				case IMMOBILE:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.movement"));
					break;
				case STEALTHY:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.stealth"));
					break;
				case SINGLEBATTLE:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.desertion"));
					break;
				case STARTAGE:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.age"));
					break;
				case PIERCERES:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.damageReduction"));
					break;
				case HEAT:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.auras"));
					break;
				case CHAOSPOWER:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.elemental"));
					break;
				case SPRINGPOWER:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.seasonal"));
					break;
				case AMBIDEXTROUS:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.combat"));
					break;
				case CASTLEDEF:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.noncombat"));
					break;
				case ITEMSLOTS:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.items"));
					break;
				case FIRSTSHAPE:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.shapes"));
					break;
				case REANIMATOR:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.summons"));
					break;
				case NAMETYPE:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.names"));
					break;
				case NOLEADER:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.leadership"));
					break;
				case NOMAGICLEADER:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.magicleadership"));
					break;
				case NOUNDEADLEADER:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.undeadleadership"));
					break;
				case INSPIRATIONAL:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.morale"));
					break;
				case MAGICSKILL1:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.magicpaths"));
					break;
				case RESEARCHBONUS:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.magicresearch"));
					break;
				case FIRERANGE:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.ritualrange"));
					break;
				case GEMPROD1:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.gems"));
					break;
				case BONUSSPELLS:
					expandable.setText(Messages.getString("MonsterDetailsSection.mod.section.othermagic"));
					break;
				}
				gd = new GridData(SWT.FILL, SWT.FILL, false, false);
				gd.horizontalSpan = 2;
				expandable.setLayoutData(gd);
				expandable.addExpansionListener(new ExpansionAdapter() {
					public void expansionStateChanged(ExpansionEvent e) {
						mform.getForm().reflow(true);
					}
				});
				
				Composite header1 = toolkit.createComposite(expandable, SWT.BORDER);
				header1.setLayout(new GridLayout(2, true));
				expandable.setClient(header1);
				if (key.equals(Inst.HP)) {
					expandable.setExpanded(true);
				}

				leftColumn = toolkit.createComposite(header1);
				glayout = new GridLayout(5, false);
				glayout.marginHeight = 0;
				glayout.marginWidth = 0;
				glayout.verticalSpacing = 0;
				leftColumn.setLayout(glayout);
				leftColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				
				rightColumn = toolkit.createComposite(header1);
				glayout = new GridLayout(5, false);
				glayout.marginHeight = 0;
				glayout.marginWidth = 0;
				glayout.verticalSpacing = 0;
				rightColumn.setLayout(glayout);
				rightColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				isRight = false;
			}

			final InstFields field = fields.getValue();
			Composite checkParent;
			if (field instanceof Inst4Fields) {
				checkParent = toolkit.createComposite(isRight?rightColumn:leftColumn);
				glayout = new GridLayout(2, false);
				glayout.marginHeight = 0;
				glayout.marginWidth = 0;
				checkParent.setLayout(glayout);
				gd = new GridData(SWT.BEGINNING, SWT.DEFAULT, false, false);
				gd.horizontalSpan = 3;
				gd.heightHint=20;
				checkParent.setLayoutData(gd);
			} else {
				checkParent = isRight?rightColumn:leftColumn;
			}
			final Button check = new DynamicButton(checkParent, SWT.CHECK);
			check.setText(key.label);
			setToolTip(check, HelpTextHelper.getText(HelpTextHelper.MONSTER_CATEGORY, key.label));
			check.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (check.getSelection()) {
						check.setFont(boldFont);
						if (field instanceof Inst1Fields) {
							addInst1(key, doc, key.defaultValue);
						} else if (field instanceof Inst2Fields) {
							addInst2(key, doc, key.defaultValue);
						} else if (field instanceof Inst3Fields) {
							addInst3(key, doc, key.defaultValue, key.defaultValue2);
						} else if (field instanceof Inst4Fields) {
							addInst4(key, doc);
						} else if (field instanceof Inst5Fields) {
							addInst5(key, doc, key.defaultValue);
						} else if (field instanceof Inst6Fields) {
							addInst6(key, doc, key.defaultValue);
						} else if (field instanceof Inst7Fields) {
							addInst3(key, doc, key.defaultValue, key.defaultValue2);
						} else if (field instanceof Inst8Fields) {
							addInst2(key, doc, key.defaultValue);
						}
					} else {
						removeInst(key, doc);
						check.setFont(normalFont);
					}
				}

			});

			Text myValue1 = null;
			Text myValue2 = null;
			if (field instanceof Inst1Fields ||	field instanceof Inst2Fields ||	field instanceof Inst7Fields ||	field instanceof Inst5Fields || field instanceof Inst6Fields) {
				final Text value = new DynamicText(isRight?rightColumn:leftColumn, SWT.SINGLE | SWT.BORDER);
				myValue1 = value;
				
				if (field instanceof Inst2Fields ||	field instanceof Inst7Fields || field instanceof Inst6Fields) {
					value.addVerifyListener(new VerifyListener() {
						
						@Override
						public void verifyText(VerifyEvent e) {
							if (Character.isLetter(e.character)) {
								e.doit = false;
							}
						}
					});
				}
				check.addSelectionListener(new SelectionAdapter() {
					@SuppressWarnings("unchecked")
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (check.getSelection()) {
							value.setEnabled(true);
							value.setText(key.defaultValue);
							for (List<Inst> dynamic : dynamicFields) {
								if (dynamic.contains(key)) {
									for (final Map.Entry<Inst, InstFields> fields : instMap.entrySet()) {
										if (dynamic.contains(fields.getKey())) {
											if (Boolean.FALSE.equals(((Inst7Fields)fields.getValue()).value1.getData())) {
												((Inst7Fields)fields.getValue()).value1.setData(Boolean.TRUE);
												((Inst7Fields)fields.getValue()).value2.setData(Boolean.TRUE);
												((Inst7Fields)fields.getValue()).check.setData(Boolean.TRUE);
												((Inst7Fields)fields.getValue()).defaultLabel1.setData(Boolean.TRUE);
												((Inst7Fields)fields.getValue()).defaultLabel2.setData(Boolean.TRUE);
												break;
											}
										}
									}
									update();
									mform.fireSelectionChanged(mform.getParts()[0], viewer.getSelection());
								}
							}
						} else {
							value.setEnabled(false);
							value.setText("");
							for (List<Inst> dynamic : dynamicFields) {
								if (dynamic.contains(key)) {
									@SuppressWarnings("rawtypes")
									List<Map.Entry> entries = Arrays.asList(instMap.entrySet().toArray(new Map.Entry[instMap.entrySet().size()]));
									Collections.reverse(entries);
									for (final Map.Entry<Inst, InstFields> fields : entries) {
										if (!key.equals(fields.getKey()) && dynamic.contains(fields.getKey())) {
											if (Boolean.TRUE.equals(((Inst7Fields)fields.getValue()).value1.getData()) && !((Inst7Fields)fields.getValue()).value1.isEnabled()) {
												((Inst7Fields)fields.getValue()).value1.setData(Boolean.FALSE);
												((Inst7Fields)fields.getValue()).value2.setData(Boolean.FALSE);
												((Inst7Fields)fields.getValue()).check.setData(Boolean.FALSE);
												((Inst7Fields)fields.getValue()).defaultLabel1.setData(Boolean.FALSE);
												((Inst7Fields)fields.getValue()).defaultLabel2.setData(Boolean.FALSE);
												break;
											}
										}
									}
									update();
									mform.fireSelectionChanged(mform.getParts()[0], viewer.getSelection());
								}
							}
						}
					}

				});
				value.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						if (field instanceof Inst1Fields) {
							setInst1(key, doc, value.getText());
						} else if (field instanceof Inst2Fields) {
							setInst2(key, doc, value.getText());
						} else if (field instanceof Inst7Fields) {
							setInst3(key, doc, value.getText(), null);
						} else if (field instanceof Inst5Fields) {
							setInst5(key, doc, value.getText());
						} else if (field instanceof Inst6Fields) {
							setInst6(key, doc, value.getText());
						}
					}			
				});
				value.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.character == '\r') {
							if (field instanceof Inst1Fields) {
								setInst1(key, doc, value.getText());
							} else if (field instanceof Inst2Fields) {
								setInst2(key, doc, value.getText());
							} else if (field instanceof Inst7Fields) {
								setInst3(key, doc, value.getText(), null);
							} else if (field instanceof Inst5Fields) {
								setInst5(key, doc, value.getText());
							} else if (field instanceof Inst6Fields) {
								setInst6(key, doc, value.getText());
							}
						}
					}
				});
				value.setEnabled(false);
				if (field instanceof Inst1Fields) {
					gd = new GridData(SWT.FILL, SWT.FILL, false, false);
					gd.widthHint = 140;
					gd.horizontalSpan = 3;
				} else if (field instanceof Inst2Fields || field instanceof Inst6Fields) {
					gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
					gd.widthHint = DEFAULT_VALUE_WIDTH;
				} else if (field instanceof Inst7Fields) {
					gd = new GridData(SWT.FILL, SWT.FILL, false, false);
					gd.widthHint = DEFAULT_VALUE_WIDTH-12;
				} else if (field instanceof Inst5Fields) {
					gd = new GridData(SWT.FILL, SWT.FILL, false, false);
					if (fields.getKey() == Inst.ONEBATTLESPELL) {
						gd.widthHint = DEFAULT_VALUE_WIDTH-12;
					} else {
						gd.widthHint = DEFAULT_VALUE_WIDTH;
					}
				}
				value.setLayoutData(gd);
				
			}
			
			MappedDynamicCombo myInst3Value1 = null;
			Text myInst3Value2 = null;
			if (field instanceof Inst3Fields) {
				final MappedDynamicCombo value = new MappedDynamicCombo(isRight?rightColumn:leftColumn, SWT.READ_ONLY);
				myInst3Value1 = value;
				
				check.addSelectionListener(new SelectionAdapter() {
					@SuppressWarnings("unchecked")
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (check.getSelection()) {
							value.setEnabled(true);
							setComboItems(fields.getKey(), value);
							int selection = Integer.parseInt(key.defaultValue);
							value.select(selection);
							for (List<Inst> dynamic : dynamicFields) {
								if (dynamic.contains(key)) {
									for (final Map.Entry<Inst, InstFields> fields : instMap.entrySet()) {
										if (dynamic.contains(fields.getKey())) {
											if (Boolean.FALSE.equals(((Inst3Fields)fields.getValue()).value1.getData())) {
												((Inst3Fields)fields.getValue()).value1.setData(Boolean.TRUE);
												((Inst3Fields)fields.getValue()).value2.setData(Boolean.TRUE);
												((Inst3Fields)fields.getValue()).check.setData(Boolean.TRUE);
												((Inst3Fields)fields.getValue()).defaultLabel1.setData(Boolean.TRUE);
												((Inst3Fields)fields.getValue()).defaultLabel2.setData(Boolean.TRUE);
												break;
											}
										}
									}
									update();
									mform.fireSelectionChanged(mform.getParts()[0], viewer.getSelection());
								}
							}
						} else {
							value.setEnabled(true);
							value.removeAll();
							value.setEnabled(false);
							for (List<Inst> dynamic : dynamicFields) {
								if (dynamic.contains(key)) {
									@SuppressWarnings("rawtypes")
									List<Map.Entry> entries = Arrays.asList(instMap.entrySet().toArray(new Map.Entry[instMap.entrySet().size()]));
									Collections.reverse(entries);
									for (final Map.Entry<Inst, InstFields> fields : entries) {
										if (!key.equals(fields.getKey()) && dynamic.contains(fields.getKey())) {
											if (Boolean.TRUE.equals(((Inst3Fields)fields.getValue()).value1.getData()) && !((Inst3Fields)fields.getValue()).value1.isEnabled()) {
												((Inst3Fields)fields.getValue()).value1.setData(Boolean.FALSE);
												((Inst3Fields)fields.getValue()).value2.setData(Boolean.FALSE);
												((Inst3Fields)fields.getValue()).check.setData(Boolean.FALSE);
												((Inst3Fields)fields.getValue()).defaultLabel1.setData(Boolean.FALSE);
												((Inst3Fields)fields.getValue()).defaultLabel2.setData(Boolean.FALSE);
												break;
											}
										}
									}
									update();
									mform.fireSelectionChanged(mform.getParts()[0], viewer.getSelection());
								}
							}
						}
					}

				});
				value.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						int val = value.getSelectedValue();
						setInst3(key, doc, Integer.toString(val), null);
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
				value.setEnabled(false);
				gd = new GridData(SWT.FILL, SWT.FILL, false, false);
				gd.widthHint = DEFAULT_VALUE_WIDTH+16;
				value.setLayoutData(gd);
				
			}

			MappedDynamicCombo myInst8Value1 = null;
			MappedDynamicCombo myInst8Value2 = null;
			MappedDynamicCombo myInst8Value3 = null;
			MappedDynamicCombo myInst8Value4 = null;
			MappedDynamicCombo myInst8Value5 = null;
			if (field instanceof Inst8Fields) {
				final MappedDynamicCombo value1 = new MappedDynamicCombo(isRight?rightColumn:leftColumn, SWT.READ_ONLY);
				final MappedDynamicCombo value2 = new MappedDynamicCombo(isRight?rightColumn:leftColumn, SWT.READ_ONLY);
				new Label(isRight?rightColumn:leftColumn, SWT.NONE);
				new Label(isRight?rightColumn:leftColumn, SWT.NONE);
				final MappedDynamicCombo value3 = new MappedDynamicCombo(isRight?rightColumn:leftColumn, SWT.READ_ONLY);
				final MappedDynamicCombo value4 = new MappedDynamicCombo(isRight?rightColumn:leftColumn, SWT.READ_ONLY);
				final MappedDynamicCombo value5 = new MappedDynamicCombo(isRight?rightColumn:leftColumn, SWT.READ_ONLY);
				new Label(isRight?rightColumn:leftColumn, SWT.NONE);
				new Label(isRight?rightColumn:leftColumn, SWT.NONE);
				myInst8Value1 = value1;
				myInst8Value2 = value2;
				myInst8Value3 = value3;
				myInst8Value4 = value4;
				myInst8Value5 = value5;
				
				check.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (check.getSelection()) {
							value1.setEnabled(true);
							value1.setItems(new String[]{
									"0 hands",	"1 hand", "2 hands", "3 hands", "4 hands"},
									new int[]{0, 2, 6, 14, 30});
							value1.select(getHands(Integer.parseInt(key.defaultValue)));

							value2.setEnabled(true);
							value2.setItems(new String[]{
									"0 heads",	"1 head", "2 heads"},
									new int[]{0, 128, 384});
							value2.select(getHeads(Integer.parseInt(key.defaultValue)));

							value3.setEnabled(true);
							value3.setItems(new String[]{
									"0 bodies",	"1 body"},
									new int[]{0, 1024});
							value3.select(getBodies(Integer.parseInt(key.defaultValue)));

							value4.setEnabled(true);
							value4.setItems(new String[]{
									"0 feet", "1 foot"},
									new int[]{0, 2048});
							value4.select(getFeet(Integer.parseInt(key.defaultValue)));

							value5.setEnabled(true);
							value5.setItems(new String[]{
									"0 misc", "1 misc", "2 misc", "3 misc", "4 misc"},
									new int[]{0, 4096, 12288, 28672, 61440});
							value5.select(getMisc(Integer.parseInt(key.defaultValue)));
						} else {
							value1.setEnabled(true);
							value1.removeAll();
							value1.setEnabled(false);
							value2.setEnabled(true);
							value2.removeAll();
							value2.setEnabled(false);
							value3.setEnabled(true);
							value3.removeAll();
							value3.setEnabled(false);
							value4.setEnabled(true);
							value4.removeAll();
							value4.setEnabled(false);
							value5.setEnabled(true);
							value5.removeAll();
							value5.setEnabled(false);
						}
					}

				});
				SelectionListener selectionListener = new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int val = getItemMask(value1.getSelectedValue(), value2.getSelectedValue(), value3.getSelectedValue(), value4.getSelectedValue(), value5.getSelectedValue());
						setInst2(key, doc, Integer.toString(val));
					}
				};
				value1.addSelectionListener(selectionListener);
				value2.addSelectionListener(selectionListener);
				value3.addSelectionListener(selectionListener);
				value4.addSelectionListener(selectionListener);
				value5.addSelectionListener(selectionListener);
				value1.setEnabled(false);
				value2.setEnabled(false);
				value3.setEnabled(false);
				value4.setEnabled(false);
				value5.setEnabled(false);
				gd = new GridData(SWT.FILL, SWT.FILL, false, false);
				gd.widthHint = DEFAULT_VALUE_WIDTH+16;
				value1.setLayoutData(gd);
				
			}

			Label defaultLabel1 = null;
			
			if (field instanceof Inst1Fields || field instanceof Inst2Fields || field instanceof Inst3Fields || field instanceof Inst5Fields || field instanceof Inst6Fields || field instanceof Inst7Fields || field instanceof Inst8Fields) {
				defaultLabel1 = new DynamicLabel(isRight?rightColumn:leftColumn, SWT.NONE);
				defaultLabel1.setEnabled(false);
			}
			if (field instanceof Inst4Fields) {
				defaultLabel1 = toolkit.createLabel(checkParent, "");
				defaultLabel1.setEnabled(false);
			}
			if (field instanceof Inst2Fields || field instanceof Inst5Fields || field instanceof Inst6Fields) {
				gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
				defaultLabel1.setLayoutData(gd);
				createSpacer(toolkit, isRight?rightColumn:leftColumn, 2);
			} else if (field instanceof Inst1Fields || field instanceof Inst3Fields || field instanceof Inst7Fields) {
				gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
				defaultLabel1.setLayoutData(gd);
			} else if (field instanceof Inst4Fields) {
				gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
				defaultLabel1.setLayoutData(gd);
				createSpacer(toolkit, isRight?rightColumn:leftColumn, 2);
			} else if (field instanceof Inst8Fields) {
				gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
				gd.horizontalSpan = 3;
				defaultLabel1.setLayoutData(gd);
			}

			Label defaultLabel2 = null;
			if (field instanceof Inst3Fields) {
				final Text value = new DynamicText(isRight?rightColumn:leftColumn, SWT.SINGLE | SWT.BORDER);
				myInst3Value2 = value;
				value.addVerifyListener(new VerifyListener() {
					
					@Override
					public void verifyText(VerifyEvent e) {
						if (Character.isLetter(e.character)) {
							e.doit = false;
						}
					}
				});
				check.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (check.getSelection()) {
							value.setEnabled(true);
							value.setText(key.defaultValue2);
							update();
						} else {
							value.setEnabled(false);
							value.setText("");
							update();
						}
					}

				});
				value.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						setInst3(key, doc, null, value.getText());
					}			
				});
				value.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.character == '\r') {
							setInst3(key, doc, null, value.getText());
						}
					}
				});
				value.setEnabled(false);
				gd = new GridData(SWT.BEGINNING, SWT.DEFAULT, false, false);
				gd.widthHint = DEFAULT_VALUE_WIDTH-24;
				value.setLayoutData(gd);
				
				defaultLabel2 = new DynamicLabel(isRight?rightColumn:leftColumn, SWT.NONE);
				defaultLabel2.setEnabled(false);

				for (List<Inst> list : dynamicFields) {
					boolean firstElement = true;
					for (Inst inst : list) {
						if (key.equals(inst)) {
							if (firstElement) {
								myInst3Value1.setData(Boolean.TRUE);
								myInst3Value2.setData(Boolean.TRUE);
								check.setData(Boolean.TRUE);
								defaultLabel1.setData(Boolean.TRUE);
								defaultLabel2.setData(Boolean.TRUE);
							} else {
								myInst3Value1.setData(Boolean.FALSE);
								myInst3Value2.setData(Boolean.FALSE);
								check.setData(Boolean.FALSE);
								defaultLabel1.setData(Boolean.FALSE);
								defaultLabel2.setData(Boolean.FALSE);
							}
						}
						firstElement = false;
					}
				}
			}
			if (field instanceof Inst7Fields) {
				final Text value = new DynamicText(isRight?rightColumn:leftColumn, SWT.SINGLE | SWT.BORDER);
				myValue2 = value;
				value.addVerifyListener(new VerifyListener() {
					
					@Override
					public void verifyText(VerifyEvent e) {
						if (Character.isLetter(e.character)) {
							e.doit = false;
						}
					}
				});
				check.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (check.getSelection()) {
							value.setEnabled(true);
							value.setText(key.defaultValue2);
							update();
						} else {
							value.setEnabled(false);
							value.setText("");
							update();
						}
					}

				});
				value.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						setInst3(key, doc, null, value.getText());
					}			
				});
				value.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.character == '\r') {
							setInst3(key, doc, null, value.getText());
						}
					}
				});
				value.setEnabled(false);
				gd = new GridData(SWT.BEGINNING, SWT.DEFAULT, false, false);
				gd.widthHint = DEFAULT_VALUE_WIDTH-24;
				value.setLayoutData(gd);
				
				defaultLabel2 = new DynamicLabel(isRight?rightColumn:leftColumn, SWT.NONE);
				defaultLabel2.setEnabled(false);

				for (List<Inst> list : dynamicFields) {
					boolean firstElement = true;
					for (Inst inst : list) {
						if (key.equals(inst)) {
							if (firstElement) {
								myValue1.setData(Boolean.TRUE);
								myValue2.setData(Boolean.TRUE);
								check.setData(Boolean.TRUE);
								defaultLabel1.setData(Boolean.TRUE);
								defaultLabel2.setData(Boolean.TRUE);
							} else {
								myValue1.setData(Boolean.FALSE);
								myValue2.setData(Boolean.FALSE);
								check.setData(Boolean.FALSE);
								defaultLabel1.setData(Boolean.FALSE);
								defaultLabel2.setData(Boolean.FALSE);
							}
						}
						firstElement = false;
					}
				}
			}
			
			if (field instanceof Inst1Fields) {
				((Inst1Fields)field).check = check;
				((Inst1Fields)field).value = myValue1;
				((Inst1Fields)field).defaultLabel = defaultLabel1;
			} else if (field instanceof Inst2Fields) {
				((Inst2Fields)field).check = check;
				((Inst2Fields)field).value = myValue1;
				((Inst2Fields)field).defaultLabel = defaultLabel1;
			} else if (field instanceof Inst3Fields) {
				((Inst3Fields)field).check = check;
				((Inst3Fields)field).value1 = myInst3Value1;
				((Inst3Fields)field).defaultLabel1 = defaultLabel1;
				((Inst3Fields)field).value2 = myInst3Value2;
				((Inst3Fields)field).defaultLabel2 = defaultLabel2;
			} else if (field instanceof Inst4Fields) {
				((Inst4Fields)field).check = check;
				((Inst4Fields)field).defaultLabel = defaultLabel1;
			} else if (field instanceof Inst5Fields) {
				((Inst5Fields)field).check = check;
				((Inst5Fields)field).value = myValue1;
				((Inst5Fields)field).defaultLabel = defaultLabel1;
			} else if (field instanceof Inst6Fields) {
				((Inst6Fields)field).check = check;
				((Inst6Fields)field).value = myValue1;
				((Inst6Fields)field).defaultLabel = defaultLabel1;
			} else if (field instanceof Inst7Fields) {
				((Inst7Fields)field).check = check;
				((Inst7Fields)field).value1 = myValue1;
				((Inst7Fields)field).defaultLabel1 = defaultLabel1;
				((Inst7Fields)field).value2 = myValue2;
				((Inst7Fields)field).defaultLabel2 = defaultLabel2;
			} else if (field instanceof Inst8Fields) {
				((Inst8Fields)field).check = check;
				((Inst8Fields)field).value1 = myInst8Value1;
				((Inst8Fields)field).value2 = myInst8Value2;
				((Inst8Fields)field).value3 = myInst8Value3;
				((Inst8Fields)field).value4 = myInst8Value4;
				((Inst8Fields)field).value5 = myInst8Value5;
				((Inst8Fields)field).defaultLabel = defaultLabel1;
			}

			if (fields.getKey() == Inst.MAGICBOOST1 ||
				fields.getKey() == Inst.MAGICBOOST2 ||
				fields.getKey() == Inst.MAGICBOOST3 ||
				fields.getKey() == Inst.MAGICBOOST4 ||
				fields.getKey() == Inst.MAGICBOOST5 ||
				fields.getKey() == Inst.MAGICBOOST6 ||
				fields.getKey() == Inst.MAGICBOOST7 ||
				fields.getKey() == Inst.MAGICBOOST8) {
				isRight = !isRight;
			}
			isRight = !isRight;
		}
	}
	
	private Image getSpriteFromZip(final String sprite) {
		ImageLoader loader1 = new ImageLoader() {
			@Override
			public InputStream getStream() throws IOException {
				Path path = new Path("$nl$/lib/sprites.zip");
				URL url = FileLocator.find(Activator.getDefault().getBundle(), path, null);
				String dbPath = FileLocator.toFileURL(url).getPath();
				ZipFile zipFile = new ZipFile(new File(dbPath));
				return zipFile.getInputStream(zipFile.getEntry(sprite));
			}
		};
		Image image = null;
		try {
			if (spriteMap.get(sprite) != null) {
				image = spriteMap.get(sprite);
			} else {
				image = new Image(null, ImageConverter.convertToSWT(ImageConverter.cropImage(loader1.loadImage())));
				spriteMap.put(sprite, image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	
	private String getPathName(int id) {
		switch (id) {
		case 0:
			return "Fire";
		case 1:
			return "Air";
		case 2:
			return "Water";
		case 3:
			return "Earth";
		case 4:
			return "Astral";
		case 5:
			return "Death";
		case 6:
			return "Nature";
		case 7:
			return "Blood";
		case 8:
			return "Priest";
		case 50:
			return "Random";
		case 51:
			return "Elemental";
		case 52:
			return "Sorcery";			
		case 53:
			return "All";			
		}
		return "Unknown";
	}
	
	private void setComboItems(Inst key, MappedDynamicCombo combo) {
		if (key == Inst.MAGICSKILL1 ||
			key == Inst.MAGICSKILL2 ||
			key == Inst.MAGICSKILL3 ||
			key == Inst.MAGICSKILL4 ||
			key == Inst.MAGICSKILL5 ||
			key == Inst.MAGICSKILL6 ||
			key == Inst.MAGICSKILL7 ||
			key == Inst.MAGICSKILL8) {
			combo.setItems(new String[]{
					"Fire",	"Air", "Water", "Earth", "Astral", "Death", "Nature", "Blood", "Priest", "Random", "Elemental", "Sorcery"},
					new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 50, 51, 52});
		}
		if (key == Inst.MAGICBOOST1 ||
			key == Inst.MAGICBOOST2 ||
			key == Inst.MAGICBOOST3 ||
			key == Inst.MAGICBOOST4 ||
			key == Inst.MAGICBOOST5 ||
			key == Inst.MAGICBOOST6 ||
			key == Inst.MAGICBOOST7 ||
			key == Inst.MAGICBOOST8) {
			combo.setItems(new String[]{
					"Fire",	"Air", "Water", "Earth", "Astral", "Death",	"Nature", "Blood", "Priest", "All"},
					new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 53});
		}
		if (key == Inst.GEMPROD1 ||
			key == Inst.GEMPROD2 ||
			key == Inst.GEMPROD3 ||
			key == Inst.GEMPROD4 ||
			key == Inst.GEMPROD5 ||
			key == Inst.GEMPROD6 ||
			key == Inst.GEMPROD7 ||
			key == Inst.GEMPROD8) {
			combo.setItems(new String[]{
					"Fire",	"Air", "Water", "Earth", "Astral", "Death",	"Nature", "Blood"},
					new int[]{0, 1, 2, 3, 4, 5, 6, 7});
		}
	}
	
	public void update() {
		if (input != null) {
			String nameString = getInst1(Inst.NAME, (Monster)input);

			String sprite1 = null;
			String sprite2 = null;
			boolean fromZip1 = false;
			boolean fromZip2 = false;
			final Format format = new DecimalFormat("0000");
			if (input instanceof SelectMonsterByName || input instanceof SelectMonsterById) {
				if (nameString != null) {
					name.setText(nameString);
					name.setEnabled(true);
					nameCheck.setSelection(true);
					nameCheck.setFont(boldFont);
				} else {
					String monsterName = getSelectMonstername((Monster)input);
					name.setText(monsterName != null ? monsterName : "");
					name.setEnabled(false);
					nameCheck.setSelection(false);
					nameCheck.setFont(normalFont);
				}

				int id = getSelectMonsterid((Monster)input);
				if (getInst1(Inst.SPR1, input) != null) {
					sprite1 = getInst1(Inst.SPR1, input);
				} else if (getInst2(Inst.COPYSPR, input) != null) {
					Integer copyId = getInst2(Inst.COPYSPR, input);
					sprite1 = format.format(copyId) + "_1.tga";
					fromZip1 = true;
				} else {
					sprite1 = format.format(id) + "_1.tga";
					fromZip1 = true;
				}

				if (getInst1(Inst.SPR2, input) != null) {
					sprite2 = getInst1(Inst.SPR2, input);
				} else if (getInst2(Inst.COPYSPR, input) != null) {
					Integer copyId = getInst2(Inst.COPYSPR, input);
					sprite2 = format.format(copyId) + "_2.tga";
					fromZip2 = true;
				} else {
					sprite2 = format.format(id) + "_2.tga";
					fromZip2 = true;
				}
				
			} else {
				if (nameString != null) {
					name.setText(nameString);
					name.setEnabled(true);
					nameCheck.setSelection(true);
					nameCheck.setFont(boldFont);
				} else {
					String str = getMonstername((Monster)input);
					name.setText(str!=null?str:"");
					name.setEnabled(false);
					nameCheck.setSelection(false);
					nameCheck.setFont(normalFont);
				}
				nameCheck.setEnabled(false);
				
				if (getInst1(Inst.SPR1, input) != null) {
					sprite1 = getInst1(Inst.SPR1, input);
				} else if (getInst1(Inst.COPYSPR, input) != null) {
					String strId = getInst1(Inst.COPYSPR, input);
					sprite1 = format.format(Integer.parseInt(strId)) + "_1.tga";
					fromZip1 = true;
				}

				if (getInst1(Inst.SPR2, input) != null) {
					sprite2 = getInst1(Inst.SPR2, input);
				} else if (getInst1(Inst.COPYSPR, input) != null) {
					String strId = getInst1(Inst.COPYSPR, input);
					sprite2 = format.format(Integer.parseInt(strId)) + "_2.tga";
					fromZip2 = true;
				}
			}
			if (sprite1 != null) {
				if (fromZip1) {
					sprite1Label.setImage(getSpriteFromZip(sprite1));
				} else {
					sprite1Label.setImage(getSprite(sprite1));
				}
			} else {
				sprite1Label.setImage(null);
			}
			if (sprite2 != null) {
				if (fromZip2) {
					sprite2Label.setImage(getSpriteFromZip(sprite2));
				} else {
					sprite2Label.setImage(getSprite(sprite2));
				}
			} else {
				sprite2Label.setImage(null);
			}
			
			String fixedNameString = getInst1(Inst.FIXEDNAME, input);
			if (fixedNameString != null) {
				fixedName.setText(fixedNameString);
				fixedName.setEnabled(true);
				fixedNameCheck.setSelection(true);
				fixedNameCheck.setFont(boldFont);
			} else {
				fixedName.setText("");
				fixedName.setEnabled(false);
				fixedNameCheck.setSelection(false);
				fixedNameCheck.setFont(normalFont);
			}

			String description = getInst1(Inst.DESCR, input);
			final FormToolkit toolkit = mform.getToolkit();
			if (description != null) {
				descr.setText(description);
				descr.setEnabled(true);
				descr.setBackground(toolkit.getColors().getBackground());
				descCheck.setSelection(true);
				descCheck.setFont(boldFont);
			} else {
				descr.setText(getSelectMonsterdescr((Monster)input));
				descr.setEnabled(false);
				descr.setBackground(toolkit.getColors().getInactiveBackground());
				descCheck.setSelection(false);
				descCheck.setFont(normalFont);
			}

			String spr1Text = getInst1(Inst.SPR1, input);
			if (spr1Text != null) {
				spr1.setText(spr1Text);
				spr1.setEnabled(true);
				spr1Browse.setEnabled(true);
				spr1Check.setSelection(true);
				spr1Check.setFont(boldFont);
			} else {
				spr1.setText("");
				spr1.setEnabled(false);
				spr1Browse.setEnabled(false);
				spr1Check.setSelection(false);
				spr1Check.setFont(normalFont);
			}
			String spr2Text = getInst1(Inst.SPR2, input);
			if (spr2Text != null) {
				spr2.setText(spr2Text);
				spr2.setEnabled(true);
				spr2Browse.setEnabled(true);
				spr2Check.setSelection(true);
				spr2Check.setFont(boldFont);
			} else {
				spr2.setText("");
				spr2.setEnabled(false);
				spr2Browse.setEnabled(false);
				spr2Check.setSelection(false);
				spr2Check.setFont(normalFont);
			}
		}
		MonsterDB monsterDB = new MonsterDB();
		if (input instanceof SelectMonsterById) {
			monsterDB = Database.getMonster(((SelectMonsterById)input).getValue());
		} else if (input instanceof SelectMonsterByName) {
			monsterDB = Database.getMonster(((SelectMonsterByName)input).getValue());
		}
		Set<List<Inst>> dynamicFirstEmpty = new HashSet<List<Inst>>();
		for (Map.Entry<Inst, InstFields> fields : instMap.entrySet()) {
			String val1 = getInst1(fields.getKey(), input);
			if (val1 != null) {
				if (fields.getValue() instanceof Inst1Fields) {
					((Inst1Fields)fields.getValue()).value.setText(val1);
					((Inst1Fields)fields.getValue()).value.setEnabled(true);
					((Inst1Fields)fields.getValue()).check.setSelection(true);
					((Inst1Fields)fields.getValue()).check.setFont(boldFont);
				}
			} else {
				if (fields.getValue() instanceof Inst1Fields) {
					((Inst1Fields)fields.getValue()).value.setText("");
					((Inst1Fields)fields.getValue()).value.setEnabled(false);
					((Inst1Fields)fields.getValue()).check.setSelection(false);
					((Inst1Fields)fields.getValue()).check.setFont(normalFont);
				}
			}
			Integer val = getInst2(fields.getKey(), input);
			if (val != null) {
				if (fields.getValue() instanceof Inst2Fields) {
					((Inst2Fields)fields.getValue()).value.setText(val.toString());
					((Inst2Fields)fields.getValue()).value.setEnabled(true);
					((Inst2Fields)fields.getValue()).check.setSelection(true);
					((Inst2Fields)fields.getValue()).check.setFont(boldFont);
				}
				if (fields.getValue() instanceof Inst8Fields) {
					((Inst8Fields)fields.getValue()).value1.setEnabled(true);
					((Inst8Fields)fields.getValue()).value1.setItems(new String[]{
							"0 hands",	"1 hand", "2 hands", "3 hands", "4 hands"},
							new int[]{0, 2, 6, 14, 30});
					((Inst8Fields)fields.getValue()).value1.select(getHands(Integer.parseInt(val.toString())));

					((Inst8Fields)fields.getValue()).value2.setEnabled(true);
					((Inst8Fields)fields.getValue()).value2.setItems(new String[]{
							"0 heads",	"1 head", "2 heads"},
							new int[]{0, 128, 384});
					((Inst8Fields)fields.getValue()).value2.select(getHeads(Integer.parseInt(val.toString())));

					((Inst8Fields)fields.getValue()).value3.setEnabled(true);
					((Inst8Fields)fields.getValue()).value3.setItems(new String[]{
							"0 bodies",	"1 body"},
							new int[]{0, 1024});
					((Inst8Fields)fields.getValue()).value3.select(getBodies(Integer.parseInt(val.toString())));

					((Inst8Fields)fields.getValue()).value4.setEnabled(true);
					((Inst8Fields)fields.getValue()).value4.setItems(new String[]{
							"0 feet", "1 foot"},
							new int[]{0, 2048});
					((Inst8Fields)fields.getValue()).value4.select(getFeet(Integer.parseInt(val.toString())));

					((Inst8Fields)fields.getValue()).value5.setEnabled(true);
					((Inst8Fields)fields.getValue()).value5.setItems(new String[]{
							"0 misc", "1 misc", "2 misc", "3 misc", "4 misc"},
							new int[]{0, 4096, 12288, 28672, 61440});
					((Inst8Fields)fields.getValue()).value5.select(getMisc(Integer.parseInt(val.toString())));

					((Inst8Fields)fields.getValue()).check.setSelection(true);
					((Inst8Fields)fields.getValue()).check.setFont(boldFont);
				}
			} else {
				if (fields.getValue() instanceof Inst2Fields) {
					((Inst2Fields)fields.getValue()).value.setText("");
					((Inst2Fields)fields.getValue()).value.setEnabled(false);
					((Inst2Fields)fields.getValue()).check.setSelection(false);
					((Inst2Fields)fields.getValue()).check.setFont(normalFont);
				}
				if (fields.getValue() instanceof Inst8Fields) {
					((Inst8Fields)fields.getValue()).value1.removeAll();
					((Inst8Fields)fields.getValue()).value1.setEnabled(false);
					((Inst8Fields)fields.getValue()).value2.removeAll();
					((Inst8Fields)fields.getValue()).value2.setEnabled(false);
					((Inst8Fields)fields.getValue()).value3.removeAll();
					((Inst8Fields)fields.getValue()).value3.setEnabled(false);
					((Inst8Fields)fields.getValue()).value4.removeAll();
					((Inst8Fields)fields.getValue()).value4.setEnabled(false);
					((Inst8Fields)fields.getValue()).value5.removeAll();
					((Inst8Fields)fields.getValue()).value5.setEnabled(false);
					((Inst8Fields)fields.getValue()).check.setSelection(false);
					((Inst8Fields)fields.getValue()).check.setFont(normalFont);
				}
			}
			Integer[] vals = getInst3(fields.getKey(), input);
			if (vals != null) {
				if (fields.getValue() instanceof Inst3Fields) {
					int selection = vals[0];
					((Inst3Fields)fields.getValue()).value1.setEnabled(true);
					setComboItems(fields.getKey(), ((Inst3Fields)fields.getValue()).value1);
					((Inst3Fields)fields.getValue()).value1.select(selection);
					((Inst3Fields)fields.getValue()).value2.setText(vals[1].toString());
					((Inst3Fields)fields.getValue()).value2.setEnabled(true);
					((Inst3Fields)fields.getValue()).check.setSelection(true);
					((Inst3Fields)fields.getValue()).check.setFont(boldFont);
					for (List<Inst> dynamic : dynamicFields) {
						if (dynamic.contains(fields.getKey())) {
							if (Boolean.FALSE.equals(((Inst3Fields)fields.getValue()).value1.getData())) {
								((Inst3Fields)fields.getValue()).value1.setData(Boolean.TRUE);
								((Inst3Fields)fields.getValue()).value2.setData(Boolean.TRUE);
								((Inst3Fields)fields.getValue()).check.setData(Boolean.TRUE);
								((Inst3Fields)fields.getValue()).defaultLabel1.setData(Boolean.TRUE);
								((Inst3Fields)fields.getValue()).defaultLabel2.setData(Boolean.TRUE);
							}
						}
					}
				} else if (fields.getValue() instanceof Inst7Fields) {
					((Inst7Fields)fields.getValue()).value1.setEnabled(true);
					((Inst7Fields)fields.getValue()).value1.setText(vals[0].toString());
					((Inst7Fields)fields.getValue()).value2.setText(vals[1].toString());
					((Inst7Fields)fields.getValue()).value2.setEnabled(true);
					((Inst7Fields)fields.getValue()).check.setSelection(true);
					((Inst7Fields)fields.getValue()).check.setFont(boldFont);
					for (List<Inst> dynamic : dynamicFields) {
						if (dynamic.contains(fields.getKey())) {
							if (Boolean.FALSE.equals(((Inst7Fields)fields.getValue()).value1.getData())) {
								((Inst7Fields)fields.getValue()).value1.setData(Boolean.TRUE);
								((Inst7Fields)fields.getValue()).value2.setData(Boolean.TRUE);
								((Inst7Fields)fields.getValue()).check.setData(Boolean.TRUE);
								((Inst7Fields)fields.getValue()).defaultLabel1.setData(Boolean.TRUE);
								((Inst7Fields)fields.getValue()).defaultLabel2.setData(Boolean.TRUE);
							}
						}
					}
				}
			} else {
				if (fields.getValue() instanceof Inst3Fields) {
					((Inst3Fields)fields.getValue()).value1.setEnabled(true);
					((Inst3Fields)fields.getValue()).value1.removeAll();
					((Inst3Fields)fields.getValue()).value1.setEnabled(false);
					((Inst3Fields)fields.getValue()).value2.setText("");
					((Inst3Fields)fields.getValue()).value2.setEnabled(false);
					((Inst3Fields)fields.getValue()).check.setSelection(false);
					((Inst3Fields)fields.getValue()).check.setFont(normalFont);
					for (List<Inst> dynamic : dynamicFields) {
						if (dynamic.contains(fields.getKey())) {
							if (dynamicFirstEmpty.contains(dynamic) && !isDefaultValue(fields.getKey(), monsterDB)) {
								if (Boolean.TRUE.equals(((Inst3Fields)fields.getValue()).value1.getData())) {
									((Inst3Fields)fields.getValue()).value1.setData(Boolean.FALSE);
									((Inst3Fields)fields.getValue()).value2.setData(Boolean.FALSE);
									((Inst3Fields)fields.getValue()).check.setData(Boolean.FALSE);
									((Inst3Fields)fields.getValue()).defaultLabel1.setData(Boolean.FALSE);
									((Inst3Fields)fields.getValue()).defaultLabel2.setData(Boolean.FALSE);
								}
							} else {
								if (!isDefaultValue(fields.getKey(), monsterDB)) {
									dynamicFirstEmpty.add(dynamic);
								}
								if (Boolean.FALSE.equals(((Inst3Fields)fields.getValue()).value1.getData())) {
									((Inst3Fields)fields.getValue()).value1.setData(Boolean.TRUE);
									((Inst3Fields)fields.getValue()).value2.setData(Boolean.TRUE);
									((Inst3Fields)fields.getValue()).check.setData(Boolean.TRUE);
									((Inst3Fields)fields.getValue()).defaultLabel1.setData(Boolean.TRUE);
									((Inst3Fields)fields.getValue()).defaultLabel2.setData(Boolean.TRUE);
								}
							}
						}
					}
				} else if (fields.getValue() instanceof Inst7Fields) {
					((Inst7Fields)fields.getValue()).value1.setEnabled(true);
					((Inst7Fields)fields.getValue()).value1.setText("");
					((Inst7Fields)fields.getValue()).value1.setEnabled(false);
					((Inst7Fields)fields.getValue()).value2.setText("");
					((Inst7Fields)fields.getValue()).value2.setEnabled(false);
					((Inst7Fields)fields.getValue()).check.setSelection(false);
					((Inst7Fields)fields.getValue()).check.setFont(normalFont);
					for (List<Inst> dynamic : dynamicFields) {
						if (dynamic.contains(fields.getKey())) {
							if (dynamicFirstEmpty.contains(dynamic) && !isDefaultValue(fields.getKey(), monsterDB)) {
								if (Boolean.TRUE.equals(((Inst7Fields)fields.getValue()).value1.getData())) {
									((Inst7Fields)fields.getValue()).value1.setData(Boolean.FALSE);
									((Inst7Fields)fields.getValue()).value2.setData(Boolean.FALSE);
									((Inst7Fields)fields.getValue()).check.setData(Boolean.FALSE);
									((Inst7Fields)fields.getValue()).defaultLabel1.setData(Boolean.FALSE);
									((Inst7Fields)fields.getValue()).defaultLabel2.setData(Boolean.FALSE);
								}
							} else {
								if (!isDefaultValue(fields.getKey(), monsterDB)) {
									dynamicFirstEmpty.add(dynamic);
								}
								if (Boolean.FALSE.equals(((Inst7Fields)fields.getValue()).value1.getData())) {
									((Inst7Fields)fields.getValue()).value1.setData(Boolean.TRUE);
									((Inst7Fields)fields.getValue()).value2.setData(Boolean.TRUE);
									((Inst7Fields)fields.getValue()).check.setData(Boolean.TRUE);
									((Inst7Fields)fields.getValue()).defaultLabel1.setData(Boolean.TRUE);
									((Inst7Fields)fields.getValue()).defaultLabel2.setData(Boolean.TRUE);
								}
							}
						}
					}
				}
			}
			Boolean isVal = getInst4(fields.getKey(), input);
			if (isVal != null) {
				if (fields.getValue() instanceof Inst4Fields) {
					((Inst4Fields)fields.getValue()).check.setSelection(isVal);
					((Inst4Fields)fields.getValue()).check.setFont(isVal ? boldFont : normalFont);
				}
			}
			Object val5 = getInst5(fields.getKey(), input);
			if (val5 != null) {
				if (fields.getValue() instanceof Inst5Fields) {
					((Inst5Fields)fields.getValue()).value.setText(val5.toString());
					((Inst5Fields)fields.getValue()).value.setEnabled(true);
					((Inst5Fields)fields.getValue()).check.setSelection(true);
					((Inst5Fields)fields.getValue()).check.setFont(boldFont);
				}
			} else {
				if (fields.getValue() instanceof Inst5Fields) {
					((Inst5Fields)fields.getValue()).value.setText("");
					((Inst5Fields)fields.getValue()).value.setEnabled(false);
					((Inst5Fields)fields.getValue()).check.setSelection(false);
					((Inst5Fields)fields.getValue()).check.setFont(normalFont);
				}
			}
			Integer val6 = getInst6(fields.getKey(), input);
			if (val6 != null) {
				if (fields.getValue() instanceof Inst6Fields) {
					((Inst6Fields)fields.getValue()).value.setText(val6.equals(Integer.valueOf(0)) ? "" : val6.toString());
					((Inst6Fields)fields.getValue()).value.setEnabled(true);
					((Inst6Fields)fields.getValue()).check.setSelection(true);
					((Inst6Fields)fields.getValue()).check.setFont(boldFont);
				}
			} else {
				if (fields.getValue() instanceof Inst6Fields) {
					((Inst6Fields)fields.getValue()).value.setText("");
					((Inst6Fields)fields.getValue()).value.setEnabled(false);
					((Inst6Fields)fields.getValue()).check.setSelection(false);
					((Inst6Fields)fields.getValue()).check.setFont(normalFont);
				}
			}
			if (input instanceof SelectMonsterByName || input instanceof SelectMonsterById) {
				switch (fields.getKey()) {
				case ARMOR1:
					if (monsterDB.armor1 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.armor1));
						Inst.ARMOR1.defaultValue = monsterDB.armor1;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ARMOR1.defaultValue = "";
					}
					break;
				case ARMOR2:
					if (monsterDB.armor2 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.armor2));
						Inst.ARMOR2.defaultValue = monsterDB.armor2;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ARMOR2.defaultValue = "";
					}
					break;
				case ARMOR3:
					if (monsterDB.armor3 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.armor3));
						Inst.ARMOR3.defaultValue = monsterDB.armor3;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ARMOR3.defaultValue = "";
					}
					break;
				case SPECIALLOOK:
					if (monsterDB.speciallook != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.speciallook));
						Inst.SPECIALLOOK.defaultValue = monsterDB.speciallook.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SPECIALLOOK.defaultValue = "1";
					}
					break;
				case AP:
					if (monsterDB.ap != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.ap));
						Inst.AP.defaultValue = monsterDB.ap.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AP.defaultValue = "";
					}
					break;
				case MAPMOVE:
					if (monsterDB.mapmove != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.mapmove));
						Inst.MAPMOVE.defaultValue = monsterDB.mapmove.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAPMOVE.defaultValue = "";
					}
					break;
				case HP:
					if (monsterDB.hp != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.hp));
						Inst.HP.defaultValue = monsterDB.hp.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HP.defaultValue = "";
					}
					break;
				case PROT:
					if (monsterDB.prot != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.prot));
						Inst.PROT.defaultValue = monsterDB.prot.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.PROT.defaultValue = "";
					}
					break;
				case SIZE:
					if (monsterDB.size != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.size));
						Inst.SIZE.defaultValue = monsterDB.size.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SIZE.defaultValue = "";
					}
					break;
				case RESSIZE:
					if (monsterDB.ressize != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.ressize));
						Inst.RESSIZE.defaultValue = monsterDB.ressize.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.RESSIZE.defaultValue = "1";
					}
					break;
				case STR:
					if (monsterDB.str != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.str));
						Inst.STR.defaultValue = monsterDB.str.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STR.defaultValue = "";
					}
					break;
				case ENC:
					if (monsterDB.enc != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.enc));
						Inst.ENC.defaultValue = monsterDB.enc.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ENC.defaultValue = "";
					}
					break;
				case ATT:
					if (monsterDB.att != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.att));
						Inst.ATT.defaultValue = monsterDB.att.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ATT.defaultValue = "";
					}
					break;
				case DEF:
					if (monsterDB.def != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.def));
						Inst.DEF.defaultValue = monsterDB.def.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEF.defaultValue = "";
					}
					break;
				case PREC:
					if (monsterDB.prec != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.prec));
						Inst.PREC.defaultValue = monsterDB.prec.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.PREC.defaultValue = "";
					}
					break;
				case MR:
					if (monsterDB.mr != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.mr));
						Inst.MR.defaultValue = monsterDB.mr.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MR.defaultValue = "";
					}
					break;
				case MOR:
					if (monsterDB.mor != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.mor));
						Inst.MOR.defaultValue = monsterDB.mor.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MOR.defaultValue = "";
					}
					break;
				case GCOST:
					if (monsterDB.gcost != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.gcost));
						Inst.GCOST.defaultValue = monsterDB.gcost.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.GCOST.defaultValue = "";
					}
					break;
				case RCOST:
					if (monsterDB.rcost != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.rcost));
						Inst.RCOST.defaultValue = monsterDB.rcost.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.RCOST.defaultValue = "";
					}
					break;
				case PATHCOST:
					if (monsterDB.pathcost != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.pathcost));
						Inst.PATHCOST.defaultValue = monsterDB.pathcost.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.PATHCOST.defaultValue = "";
					}
					break;
				case STARTDOM:
					if (monsterDB.startdom != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.startdom));
						Inst.STARTDOM.defaultValue = monsterDB.startdom.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STARTDOM.defaultValue = "";
					}
					break;
				case EYES:
					if (monsterDB.eyes != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.eyes));
						Inst.EYES.defaultValue = monsterDB.eyes.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.EYES.defaultValue = "2";
					}
					break;
				case VOIDSANITY:
					if (monsterDB.voidsanity != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.voidsanity));
						Inst.VOIDSANITY.defaultValue = monsterDB.voidsanity.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.VOIDSANITY.defaultValue = "10";
					}
					break;
				case COPYSTATS:
					if (monsterDB.copystats != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.copystats));
						Inst.COPYSTATS.defaultValue = monsterDB.copystats.toString();
					}
					break;
				case COPYSPR:
					if (monsterDB.copyspr != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.copyspr));
						Inst.COPYSPR.defaultValue = monsterDB.copyspr.toString();
					}
					break;
				case SHATTEREDSOUL:
					if (monsterDB.shatteredsoul != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.shatteredsoul));
						Inst.SHATTEREDSOUL.defaultValue = monsterDB.shatteredsoul.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SHATTEREDSOUL.defaultValue = "";
					}
					break;
				case COLDRES:
					if (monsterDB.coldres != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.coldres));
						Inst.COLDRES.defaultValue = monsterDB.coldres.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.COLDRES.defaultValue = "";
					}
					break;
				case FIRERES:
					if (monsterDB.fireres != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.fireres));
						Inst.FIRERES.defaultValue = monsterDB.fireres.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FIRERES.defaultValue = "";
					}
					break;
				case POISONRES:
					if (monsterDB.poisonres != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.poisonres));
						Inst.POISONRES.defaultValue = monsterDB.poisonres.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.POISONRES.defaultValue = "";
					}
					break;
				case SHOCKRES:
					if (monsterDB.shockres != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.shockres));
						Inst.SHOCKRES.defaultValue = monsterDB.shockres.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SHOCKRES.defaultValue = "";
					}
					break;
				case DARKVISION:
					if (monsterDB.darkvision != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.darkvision));
						Inst.DARKVISION.defaultValue = monsterDB.darkvision.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DARKVISION.defaultValue = "";
					}
					break;
				case STEALTHY:
					if (monsterDB.stealthy != null) {
						((Inst6Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.stealthy));
						Inst.STEALTHY.defaultValue = monsterDB.stealthy.toString();
					} else {
						((Inst6Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STEALTHY.defaultValue = "";
					}
					break;
				case SEDUCE:
					if (monsterDB.seduce != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.seduce));
						Inst.SEDUCE.defaultValue = monsterDB.seduce.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SEDUCE.defaultValue = "";
					}
					break;
				case SUCCUBUS:
					if (monsterDB.succubus != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.succubus));
						Inst.SUCCUBUS.defaultValue = monsterDB.succubus.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUCCUBUS.defaultValue = "";
					}
					break;
				case BECKON:
					if (monsterDB.beckon != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.beckon));
						Inst.BECKON.defaultValue = monsterDB.beckon.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BECKON.defaultValue = "10";
					}
					break;
				case STARTAGE:
					if (monsterDB.startage != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.startage));
						Inst.STARTAGE.defaultValue = monsterDB.startage.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STARTAGE.defaultValue = "";
					}
					break;
				case MAXAGE:
					if (monsterDB.maxage != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.maxage));
						Inst.MAXAGE.defaultValue = monsterDB.maxage.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAXAGE.defaultValue = "";
					}
					break;
				case OLDER:
					if (monsterDB.older != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.older));
						Inst.OLDER.defaultValue = monsterDB.older.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.OLDER.defaultValue = "";
					}
					break;
				case HEALER:
					if (monsterDB.healer != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.healer));
						Inst.HEALER.defaultValue = monsterDB.healer.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HEALER.defaultValue = "";
					}
					break;
				case STARTAFF:
					if (monsterDB.startaff != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.startaff));
						Inst.STARTAFF.defaultValue = monsterDB.startaff.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STARTAFF.defaultValue = "";
					}
					break;
				case SUPPLYBONUS:
					if (monsterDB.supplybonus != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.supplybonus));
						Inst.SUPPLYBONUS.defaultValue = monsterDB.supplybonus.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUPPLYBONUS.defaultValue = "";
					}
					break;
				case RESOURCES:
					if (monsterDB.resources != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.resources));
						Inst.RESOURCES.defaultValue = monsterDB.resources.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.RESOURCES.defaultValue = "";
					}
					break;
				case UWDAMAGE:
					if (monsterDB.uwdamage != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.uwdamage));
						Inst.UWDAMAGE.defaultValue = monsterDB.uwdamage.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.UWDAMAGE.defaultValue = "";
					}
					break;
				case COLDPOWER:
					if (monsterDB.coldpower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.coldpower));
						Inst.COLDPOWER.defaultValue = monsterDB.coldpower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.COLDPOWER.defaultValue = "";
					}
					break;
				case FIREPOWER:
					if (monsterDB.firepower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.firepower));
						Inst.FIREPOWER.defaultValue = monsterDB.firepower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FIREPOWER.defaultValue = "";
					}
					break;
				case STORMPOWER:
					if (monsterDB.stormpower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.stormpower));
						Inst.STORMPOWER.defaultValue = monsterDB.stormpower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STORMPOWER.defaultValue = "";
					}
					break;
				case DARKPOWER:
					if (monsterDB.darkpower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.darkpower));
						Inst.DARKPOWER.defaultValue = monsterDB.darkpower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DARKPOWER.defaultValue = "";
					}
					break;
				case SPRINGPOWER:
					if (monsterDB.springpower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.springpower));
						Inst.SPRINGPOWER.defaultValue = monsterDB.springpower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SPRINGPOWER.defaultValue = "";
					}
					break;
				case SUMMERPOWER:
					if (monsterDB.summerpower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.summerpower));
						Inst.SUMMERPOWER.defaultValue = monsterDB.summerpower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUMMERPOWER.defaultValue = "";
					}
					break;
				case FALLPOWER:
					if (monsterDB.fallpower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.fallpower));
						Inst.FALLPOWER.defaultValue = monsterDB.fallpower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FALLPOWER.defaultValue = "";
					}
					break;
				case WINTERPOWER:
					if (monsterDB.winterpower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.winterpower));
						Inst.WINTERPOWER.defaultValue = monsterDB.winterpower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WINTERPOWER.defaultValue = "";
					}
					break;
				case AMBIDEXTROUS:
					if (monsterDB.ambidextrous != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.ambidextrous));
						Inst.AMBIDEXTROUS.defaultValue = monsterDB.ambidextrous.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AMBIDEXTROUS.defaultValue = "";
					}
					break;
				case BANEFIRESHIELD:
					if (monsterDB.banefireshield != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.banefireshield));
						Inst.BANEFIRESHIELD.defaultValue = monsterDB.banefireshield.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BANEFIRESHIELD.defaultValue = "";
					}
					break;
				case BERSERK:
					if (monsterDB.berserk != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.berserk));
						Inst.BERSERK.defaultValue = monsterDB.berserk.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BERSERK.defaultValue = "";
					}
					break;
				case STANDARD:
					if (monsterDB.standard != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.standard));
						Inst.STANDARD.defaultValue = monsterDB.standard.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STANDARD.defaultValue = "";
					}
					break;
				case ANIMALAWE:
					if (monsterDB.animalawe != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.animalawe));
						Inst.ANIMALAWE.defaultValue = monsterDB.animalawe.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ANIMALAWE.defaultValue = "";
					}
					break;
				case AWE:
					if (monsterDB.awe != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.awe));
						Inst.AWE.defaultValue = monsterDB.awe.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AWE.defaultValue = "";
					}
					break;
				case FEAR:
					if (monsterDB.fear != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.fear));
						Inst.FEAR.defaultValue = monsterDB.fear.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FEAR.defaultValue = "";
					}
					break;
				case REGENERATION:
					if (monsterDB.regeneration != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.regeneration));
						Inst.REGENERATION.defaultValue = monsterDB.regeneration.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.REGENERATION.defaultValue = "";
					}
					break;
				case REINVIGORATION:
					if (monsterDB.reinvigoration != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.reinvigoration));
						Inst.REINVIGORATION.defaultValue = monsterDB.reinvigoration.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.REINVIGORATION.defaultValue = "";
					}
					break;
				case FIRESHIELD:
					if (monsterDB.fireshield != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.fireshield));
						Inst.FIRESHIELD.defaultValue = monsterDB.fireshield.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FIRESHIELD.defaultValue = "";
					}
					break;
				case HEAT:
					if (monsterDB.heat != null) {
						((Inst6Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.heat));
						Inst.HEAT.defaultValue = monsterDB.heat.toString();
					} else {
						((Inst6Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HEAT.defaultValue = "";
					}
					break;
				case COLD:
					if (monsterDB.cold != null) {
						((Inst6Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.cold));
						Inst.COLD.defaultValue = monsterDB.cold.toString();
					} else {
						((Inst6Fields)fields.getValue()).defaultLabel.setText("");
						Inst.COLD.defaultValue = "";
					}
					break;
				case ICEPROT:
					if (monsterDB.iceprot != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.iceprot));
						Inst.ICEPROT.defaultValue = monsterDB.iceprot.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ICEPROT.defaultValue = "";
					}
					break;
				case INVULNERABLE:
					if (monsterDB.invulnerable!= null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.invulnerable));
						Inst.INVULNERABLE.defaultValue = monsterDB.invulnerable.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INVULNERABLE.defaultValue = "0";
					}
					break;
				case POISONCLOUD:
					if (monsterDB.poisoncloud != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.poisoncloud));
						Inst.POISONCLOUD.defaultValue = monsterDB.poisoncloud.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.POISONCLOUD.defaultValue = "";
					}
					break;
				case DISEASECLOUD:
					if (monsterDB.diseasecloud != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.diseasecloud));
						Inst.DISEASECLOUD.defaultValue = monsterDB.diseasecloud.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DISEASECLOUD.defaultValue = "";
					}
					break;
				case BLOODVENGEANCE:
					if (monsterDB.bloodvengeance != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.bloodvengeance));
						Inst.BLOODVENGEANCE.defaultValue = monsterDB.bloodvengeance.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BLOODVENGEANCE.defaultValue = "1";
					}
					break;
				case CASTLEDEF:
					if (monsterDB.castledef != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.castledef));
						Inst.CASTLEDEF.defaultValue = monsterDB.castledef.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.CASTLEDEF.defaultValue = "";
					}
					break;
				case SIEGEBONUS:
					if (monsterDB.siegebonus != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.siegebonus));
						Inst.SIEGEBONUS.defaultValue = monsterDB.siegebonus.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SIEGEBONUS.defaultValue = "";
					}
					break;
				case PATROLBONUS:
					if (monsterDB.patrolbonus != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.patrolbonus));
						Inst.PATROLBONUS.defaultValue = monsterDB.patrolbonus.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.PATROLBONUS.defaultValue = "";
					}
					break;
				case PILLAGEBONUS:
					if (monsterDB.pillagebonus != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.pillagebonus));
						Inst.PILLAGEBONUS.defaultValue = monsterDB.pillagebonus.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.PILLAGEBONUS.defaultValue = "";
					}
					break;
				case MASTERRIT:
					if (monsterDB.masterrit != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.masterrit));
						Inst.MASTERRIT.defaultValue = monsterDB.masterrit.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MASTERRIT.defaultValue = "0";
					}
					break;
				case RESEARCHBONUS:
					if (monsterDB.researchbonus != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.researchbonus));
						Inst.RESEARCHBONUS.defaultValue = monsterDB.researchbonus.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.RESEARCHBONUS.defaultValue = "0";
					}
					break;
				case INSPIRINGRES:
					if (monsterDB.inspiringres != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.inspiringres));
						Inst.INSPIRINGRES.defaultValue = monsterDB.inspiringres.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INSPIRINGRES.defaultValue = "0";
					}
					break;
				case FORGEBONUS:
					if (monsterDB.forgebonus != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.forgebonus));
						Inst.FORGEBONUS.defaultValue = monsterDB.forgebonus.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FORGEBONUS.defaultValue = "";
					}
					break;
				case DOUSE:
					if (monsterDB.douse != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.douse));
						Inst.DOUSE.defaultValue = monsterDB.douse.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DOUSE.defaultValue = "";
					}
					break;
				case NOBADEVENTS:
					if (monsterDB.nobadevents != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.nobadevents));
						Inst.NOBADEVENTS.defaultValue = monsterDB.nobadevents.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NOBADEVENTS.defaultValue = "";
					}
					break;
				case INCUNREST:
					if (monsterDB.incunrest != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.incunrest));
						Inst.INCUNREST.defaultValue = monsterDB.incunrest.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INCUNREST.defaultValue = "";
					}
					break;
				case SPREADDOM:
					if (monsterDB.spreaddom != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.spreaddom));
						Inst.SPREADDOM.defaultValue = monsterDB.spreaddom.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SPREADDOM.defaultValue = "10";
					}
					break;
				case LEPER:
					if (monsterDB.leper != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.leper));
						Inst.LEPER.defaultValue = monsterDB.leper.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.LEPER.defaultValue = "0";
					}
					break;
				case POPKILL:
					if (monsterDB.popkill != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.popkill));
						Inst.POPKILL.defaultValue = monsterDB.popkill.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.POPKILL.defaultValue = "";
					}
					break;
				case HERETIC:
					if (monsterDB.heretic != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.heretic));
						Inst.HERETIC.defaultValue = monsterDB.heretic.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HERETIC.defaultValue = "";
					}
					break;
				case ITEMSLOTS:
					if (monsterDB.itemslots != null) {
						((Inst8Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", getItemMaskString(monsterDB.itemslots)));
						Inst.ITEMSLOTS.defaultValue = monsterDB.itemslots.toString();
					} else {
						((Inst8Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ITEMSLOTS.defaultValue = "";
					}
					break;
				case NAMETYPE:
					if (monsterDB.nametype != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.nametype));
						Inst.NAMETYPE.defaultValue = monsterDB.nametype.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ITEMSLOTS.defaultValue = "";
					}
					break;
				case MAGICSKILL1:
					if (monsterDB.magicskillpath1 != null && monsterDB.magicskilllevel1 != null) {
						((Inst3Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", getPathName(monsterDB.magicskillpath1)));
						Inst.MAGICSKILL1.defaultValue = monsterDB.magicskillpath1.toString();
						((Inst3Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicskilllevel1));
						Inst.MAGICSKILL1.defaultValue2 = monsterDB.magicskilllevel1.toString();
					} else {
						((Inst3Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst3Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.MAGICSKILL1.defaultValue = "0";
						Inst.MAGICSKILL1.defaultValue2 = "1";
					}
					break;
				case MAGICSKILL2:
					if (monsterDB.magicskillpath2 != null && monsterDB.magicskilllevel2 != null) {
						((Inst3Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", getPathName(monsterDB.magicskillpath2)));
						Inst.MAGICSKILL2.defaultValue = monsterDB.magicskillpath2.toString();
						((Inst3Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicskilllevel2));
						Inst.MAGICSKILL2.defaultValue2 = monsterDB.magicskilllevel2.toString();
					} else {
						((Inst3Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst3Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.MAGICSKILL2.defaultValue = "0";
						Inst.MAGICSKILL2.defaultValue2 = "1";
					}
					break;
				case MAGICSKILL3:
					if (monsterDB.magicskillpath3 != null && monsterDB.magicskilllevel3 != null) {
						((Inst3Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", getPathName(monsterDB.magicskillpath3)));
						Inst.MAGICSKILL3.defaultValue = monsterDB.magicskillpath3.toString();
						((Inst3Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicskilllevel3));
						Inst.MAGICSKILL3.defaultValue2 = monsterDB.magicskilllevel3.toString();
					} else {
						((Inst3Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst3Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.MAGICSKILL3.defaultValue = "0";
						Inst.MAGICSKILL3.defaultValue2 = "1";
					}
					break;
				case MAGICSKILL4:
					if (monsterDB.magicskillpath4 != null && monsterDB.magicskilllevel4 != null) {
						((Inst3Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", getPathName(monsterDB.magicskillpath4)));
						Inst.MAGICSKILL4.defaultValue = monsterDB.magicskillpath4.toString();
						((Inst3Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicskilllevel4));
						Inst.MAGICSKILL4.defaultValue2 = monsterDB.magicskilllevel4.toString();
					} else {
						((Inst3Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst3Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.MAGICSKILL4.defaultValue = "0";
						Inst.MAGICSKILL4.defaultValue2 = "1";
					}
					break;
				case CUSTOMMAGIC1:
					if (monsterDB.custommagicpath1 != null && monsterDB.custommagicchance1 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicpath1));
						Inst.CUSTOMMAGIC1.defaultValue = monsterDB.custommagicpath1.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicchance1));
						Inst.CUSTOMMAGIC1.defaultValue2 = monsterDB.custommagicchance1.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.CUSTOMMAGIC1.defaultValue = "128";
						Inst.CUSTOMMAGIC1.defaultValue2 = "100";
					}
					break;
				case CUSTOMMAGIC2:
					if (monsterDB.custommagicpath2 != null && monsterDB.custommagicchance2 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicpath2));
						Inst.CUSTOMMAGIC2.defaultValue = monsterDB.custommagicpath2.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicchance2));
						Inst.CUSTOMMAGIC2.defaultValue2 = monsterDB.custommagicchance2.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.CUSTOMMAGIC2.defaultValue = "128";
						Inst.CUSTOMMAGIC2.defaultValue2 = "100";
					}
					break;
				case CUSTOMMAGIC3:
					if (monsterDB.custommagicpath3 != null && monsterDB.custommagicchance3 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicpath3));
						Inst.CUSTOMMAGIC3.defaultValue = monsterDB.custommagicpath3.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicchance3));
						Inst.CUSTOMMAGIC3.defaultValue2 = monsterDB.custommagicchance3.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.CUSTOMMAGIC3.defaultValue = "128";
						Inst.CUSTOMMAGIC3.defaultValue2 = "100";
					}
					break;
				case CUSTOMMAGIC4:
					if (monsterDB.custommagicpath4 != null && monsterDB.custommagicchance4 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicpath4));
						Inst.CUSTOMMAGIC4.defaultValue = monsterDB.custommagicpath4.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicchance4));
						Inst.CUSTOMMAGIC4.defaultValue2 = monsterDB.custommagicchance4.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.CUSTOMMAGIC4.defaultValue = "128";
						Inst.CUSTOMMAGIC4.defaultValue2 = "100";
					}
					break;
				case CUSTOMMAGIC5:
					if (monsterDB.custommagicpath5 != null && monsterDB.custommagicchance5 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicpath5));
						Inst.CUSTOMMAGIC5.defaultValue = monsterDB.custommagicpath5.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicchance5));
						Inst.CUSTOMMAGIC5.defaultValue2 = monsterDB.custommagicchance5.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.CUSTOMMAGIC5.defaultValue = "128";
						Inst.CUSTOMMAGIC5.defaultValue2 = "100";
					}
					break;
				case CUSTOMMAGIC6:
					if (monsterDB.custommagicpath6 != null && monsterDB.custommagicchance6 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicpath6));
						Inst.CUSTOMMAGIC6.defaultValue = monsterDB.custommagicpath6.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicchance6));
						Inst.CUSTOMMAGIC6.defaultValue2 = monsterDB.custommagicchance6.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.CUSTOMMAGIC6.defaultValue = "128";
						Inst.CUSTOMMAGIC6.defaultValue2 = "100";
					}
					break;
				case CUSTOMMAGIC7:
					if (monsterDB.custommagicpath7 != null && monsterDB.custommagicchance7 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicpath7));
						Inst.CUSTOMMAGIC7.defaultValue = monsterDB.custommagicpath7.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicchance7));
						Inst.CUSTOMMAGIC7.defaultValue2 = monsterDB.custommagicchance7.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.CUSTOMMAGIC7.defaultValue = "128";
						Inst.CUSTOMMAGIC7.defaultValue2 = "100";
					}
					break;
				case CUSTOMMAGIC8:
					if (monsterDB.custommagicpath8 != null && monsterDB.custommagicchance8 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicpath8));
						Inst.CUSTOMMAGIC8.defaultValue = monsterDB.custommagicpath8.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.custommagicchance8));
						Inst.CUSTOMMAGIC8.defaultValue2 = monsterDB.custommagicchance8.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.CUSTOMMAGIC8.defaultValue = "128";
						Inst.CUSTOMMAGIC8.defaultValue2 = "100";
					}
					break;
				case MAGICBOOST1:
					((Inst3Fields)fields.getValue()).defaultLabel1.setText(monsterDB.magicboost1 != null ? Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicboost1) : "");
					((Inst3Fields)fields.getValue()).defaultLabel2.setText(monsterDB.magicboost2 != null ? Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicboost2) : "");
					break;
				case GEMPROD1:
					((Inst3Fields)fields.getValue()).defaultLabel1.setText(monsterDB.gemprod1 != null ? Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.gemprod1) : "");
					((Inst3Fields)fields.getValue()).defaultLabel2.setText(monsterDB.gemprod2 != null ? Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.gemprod2) : "");
					break;
				case ONEBATTLESPELL:
					if (monsterDB.onebattlespell != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.onebattlespell));
						Inst.ONEBATTLESPELL.defaultValue = monsterDB.onebattlespell.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ONEBATTLESPELL.defaultValue = "";
					}
					break;
				case CLEAR:
					if (monsterDB.clear != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.clear));
						Inst.CLEAR.defaultValue = monsterDB.clear.toString();
					}
					break;
				case CLEARMAGIC:
					if (monsterDB.clearmagic != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.clearmagic));
						Inst.CLEARMAGIC.defaultValue = monsterDB.clearmagic.toString();
					}
					break;
				case CLEARSPEC:
					if (monsterDB.clearspec != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.clearspec));
						Inst.CLEARSPEC.defaultValue = monsterDB.clearspec.toString();
					}
					break;
				case FEMALE:
					if (monsterDB.female != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.female));
						Inst.FEMALE.defaultValue = monsterDB.female.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FEMALE.defaultValue = "";
					}
					break;
				case MOUNTED:
					if (monsterDB.mounted != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.mounted));
						Inst.MOUNTED.defaultValue = monsterDB.mounted.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MOUNTED.defaultValue = "";
					}
					break;
				case HOLY:
					if (monsterDB.holy != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.holy));
						Inst.HOLY.defaultValue = monsterDB.holy.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HOLY.defaultValue = "";
					}
					break;
				case ANIMAL:
					if (monsterDB.animal != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.animal));
						Inst.ANIMAL.defaultValue = monsterDB.animal.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ANIMAL.defaultValue = "";
					}
					break;
				case UNDEAD:
					if (monsterDB.undead != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.undead));
						Inst.UNDEAD.defaultValue = monsterDB.undead.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.UNDEAD.defaultValue = "";
					}
					break;
				case DEMON:
					if (monsterDB.demon != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.demon));
						Inst.DEMON.defaultValue = monsterDB.demon.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEMON.defaultValue = "";
					}
					break;
				case MAGICBEING:
					if (monsterDB.magicbeing != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicbeing));
						Inst.MAGICBEING.defaultValue = monsterDB.magicbeing.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAGICBEING.defaultValue = "";
					}
					break;
				case STONEBEING:
					if (monsterDB.stonebeing != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.stonebeing));
						Inst.STONEBEING.defaultValue = monsterDB.stonebeing.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STONEBEING.defaultValue = "";
					}
					break;
				case INANIMATE:
					if (monsterDB.inanimate != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.inanimate));
						Inst.INANIMATE.defaultValue = monsterDB.inanimate.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INANIMATE.defaultValue = "";
					}
					break;
				case COLDBLOOD:
					if (monsterDB.coldblood != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.coldblood));
						Inst.COLDBLOOD.defaultValue = monsterDB.coldblood.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.COLDBLOOD.defaultValue = "";
					}
					break;
				case IMMORTAL:
					if (monsterDB.immortal != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.immortal));
						Inst.IMMORTAL.defaultValue = monsterDB.immortal.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.IMMORTAL.defaultValue = "";
					}
					break;
				case BLIND:
					if (monsterDB.blind != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.blind));
						Inst.BLIND.defaultValue = monsterDB.blind.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BLIND.defaultValue = "";
					}
					break;
				case UNIQUE:
					if (monsterDB.unique != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.unique));
						Inst.UNIQUE.defaultValue = monsterDB.unique.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.UNIQUE.defaultValue = "";
					}
					break;
				case IMMOBILE:
					if (monsterDB.immobile != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.immobile));
						Inst.IMMOBILE.defaultValue = monsterDB.immobile.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.IMMOBILE.defaultValue = "";
					}
					break;
				case AQUATIC:
					if (monsterDB.aquatic != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.aquatic));
						Inst.AQUATIC.defaultValue = monsterDB.aquatic.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AQUATIC.defaultValue = "";
					}
					break;
				case AMPHIBIAN:
					if (monsterDB.amphibian != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.amphibian));
						Inst.AMPHIBIAN.defaultValue = monsterDB.amphibian.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AMPHIBIAN.defaultValue = "";
					}
					break;
				case POORAMPHIBIAN:
					if (monsterDB.pooramphibian != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.pooramphibian));
						Inst.POORAMPHIBIAN.defaultValue = monsterDB.pooramphibian.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.POORAMPHIBIAN.defaultValue = "";
					}
					break;
				case FLYING:
					if (monsterDB.flying != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.flying));
						Inst.FLYING.defaultValue = monsterDB.flying.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FLYING.defaultValue = "";
					}
					break;
				case STORMIMMUNE:
					if (monsterDB.stormimmune != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.stormimmune));
						Inst.STORMIMMUNE.defaultValue = monsterDB.stormimmune.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.STORMIMMUNE.defaultValue = "";
					}
					break;
				case SAILING:
					if (monsterDB.sailing1 != null) {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.sailing1));
						Inst.SAILING.defaultValue = monsterDB.sailing1.toString();
						((Inst7Fields)fields.getValue()).defaultLabel2.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.sailing2));
						Inst.SAILING.defaultValue2 = monsterDB.sailing2.toString();
					} else {
						((Inst7Fields)fields.getValue()).defaultLabel1.setText("");
						Inst.SAILING.defaultValue = "999";
						((Inst7Fields)fields.getValue()).defaultLabel2.setText("");
						Inst.SAILING.defaultValue2 = "2";
					}
					break;
				case FORESTSURVIVAL:
					if (monsterDB.forestsurvival != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.forestsurvival));
						Inst.FORESTSURVIVAL.defaultValue = monsterDB.forestsurvival.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FORESTSURVIVAL.defaultValue = "";
					}
					break;
				case MOUNTAINSURVIVAL:
					if (monsterDB.mountainsurvival != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.mountainsurvival));
						Inst.MOUNTAINSURVIVAL.defaultValue = monsterDB.mountainsurvival.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MOUNTAINSURVIVAL.defaultValue = "";
					}
					break;
				case SWAMPSURVIVAL:
					if (monsterDB.swampsurvival != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.swampsurvival));
						Inst.SWAMPSURVIVAL.defaultValue = monsterDB.swampsurvival.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SWAMPSURVIVAL.defaultValue = "";
					}
					break;
				case WASTESURVIVAL:
					if (monsterDB.wastesurvival != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.wastesurvival));
						Inst.WASTESURVIVAL.defaultValue = monsterDB.wastesurvival.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WASTESURVIVAL.defaultValue = "";
					}
					break;
				case ILLUSION:
					if (monsterDB.illusion != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.illusion));
						Inst.ILLUSION.defaultValue = monsterDB.illusion.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ILLUSION.defaultValue = "";
					}
					break;
				case SPY:
					if (monsterDB.spy != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.spy));
						Inst.SPY.defaultValue = monsterDB.spy.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SPY.defaultValue = "";
					}
					break;
				case ASSASSIN:
					if (monsterDB.assassin != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.assassin));
						Inst.ASSASSIN.defaultValue = monsterDB.assassin.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ASSASSIN.defaultValue = "";
					}
					break;
				case HEAL:
					if (monsterDB.heal != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.heal));
						Inst.HEAL.defaultValue = monsterDB.heal.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HEAL.defaultValue = "";
					}
					break;
				case NOHEAL:
					if (monsterDB.noheal != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.noheal));
						Inst.NOHEAL.defaultValue = monsterDB.noheal.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NOHEAL.defaultValue = "";
					}
					break;
				case NEEDNOTEAT:
					if (monsterDB.neednoteat != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.neednoteat));
						Inst.NEEDNOTEAT.defaultValue = monsterDB.neednoteat.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NEEDNOTEAT.defaultValue = "";
					}
					break;
				case ETHEREAL:
					if (monsterDB.ethereal != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.ethereal));
						Inst.ETHEREAL.defaultValue = monsterDB.ethereal.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ETHEREAL.defaultValue = "";
					}
					break;
				case TRAMPLE:
					if (monsterDB.trample != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.trample));
						Inst.TRAMPLE.defaultValue = monsterDB.trample.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TRAMPLE.defaultValue = "";
					}
					break;
				case ENTANGLE:
					if (monsterDB.entangle != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.entangle));
						Inst.ENTANGLE.defaultValue = monsterDB.entangle.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ENTANGLE.defaultValue = "";
					}
					break;
				case EYELOSS:
					if (monsterDB.eyeloss != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.eyeloss));
						Inst.EYELOSS.defaultValue = monsterDB.eyeloss.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.EYELOSS.defaultValue = "";
					}
					break;
				case HORRORMARK:
					if (monsterDB.horrormark != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.horrormark));
						Inst.HORRORMARK.defaultValue = monsterDB.horrormark.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HORRORMARK.defaultValue = "";
					}
					break;
				case POISONARMOR:
					if (monsterDB.poisonarmor != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.poisonarmor));
						Inst.POISONARMOR.defaultValue = monsterDB.poisonarmor.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.POISONARMOR.defaultValue = "";
					}
					break;
				case INQUISITOR:
					if (monsterDB.inquisitor != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.inquisitor));
						Inst.INQUISITOR.defaultValue = monsterDB.inquisitor.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INQUISITOR.defaultValue = "";
					}
					break;
				case NOITEM:
					if (monsterDB.noitem != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.noitem));
						Inst.NOITEM.defaultValue = monsterDB.noitem.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NOITEM.defaultValue = "";
					}
					break;
				case NOLEADER:
					if (monsterDB.noleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.noleader));
						Inst.NOLEADER.defaultValue = monsterDB.noleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NOLEADER.defaultValue = "";
					}
					break;
				case POORLEADER:
					if (monsterDB.poorleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.poorleader));
						Inst.POORLEADER.defaultValue = monsterDB.poorleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.POORLEADER.defaultValue = "";
					}
					break;
				case OKLEADER:
					if (monsterDB.okleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.okleader));
						Inst.OKLEADER.defaultValue = monsterDB.okleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.OKLEADER.defaultValue = "";
					}
					break;
				case GOODLEADER:
					if (monsterDB.goodleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.goodleader));
						Inst.GOODLEADER.defaultValue = monsterDB.goodleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.GOODLEADER.defaultValue = "";
					}
					break;
				case EXPERTLEADER:
					if (monsterDB.expertleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.expertleader));
						Inst.EXPERTLEADER.defaultValue = monsterDB.expertleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.EXPERTLEADER.defaultValue = "";
					}
					break;
				case SUPERIORLEADER:
					if (monsterDB.superiorleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.superiorleader));
						Inst.SUPERIORLEADER.defaultValue = monsterDB.superiorleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUPERIORLEADER.defaultValue = "";
					}
					break;
				case NOMAGICLEADER:
					if (monsterDB.nomagicleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.nomagicleader));
						Inst.NOMAGICLEADER.defaultValue = monsterDB.nomagicleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NOMAGICLEADER.defaultValue = "";
					}
					break;
				case POORMAGICLEADER:
					if (monsterDB.poormagicleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.poormagicleader));
						Inst.POORMAGICLEADER.defaultValue = monsterDB.poormagicleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.POORMAGICLEADER.defaultValue = "";
					}
					break;
				case OKMAGICLEADER:
					if (monsterDB.okmagicleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.okmagicleader));
						Inst.OKMAGICLEADER.defaultValue = monsterDB.okmagicleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.OKMAGICLEADER.defaultValue = "";
					}
					break;
				case GOODMAGICLEADER:
					if (monsterDB.goodmagicleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.goodmagicleader));
						Inst.GOODMAGICLEADER.defaultValue = monsterDB.goodmagicleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.GOODMAGICLEADER.defaultValue = "";
					}
					break;
				case EXPERTMAGICLEADER:
					if (monsterDB.expertmagicleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.expertmagicleader));
						Inst.EXPERTMAGICLEADER.defaultValue = monsterDB.expertmagicleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.EXPERTMAGICLEADER.defaultValue = "";
					}
					break;
				case SUPERIORMAGICLEADER:
					if (monsterDB.superiormagicleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.superiormagicleader));
						Inst.SUPERIORMAGICLEADER.defaultValue = monsterDB.superiormagicleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUPERIORMAGICLEADER.defaultValue = "";
					}
					break;
				case NOUNDEADLEADER:
					if (monsterDB.noundeadleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.noundeadleader));
						Inst.NOUNDEADLEADER.defaultValue = monsterDB.noundeadleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NOUNDEADLEADER.defaultValue = "";
					}
					break;
				case POORUNDEADLEADER:
					if (monsterDB.poorundeadleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.poorundeadleader));
						Inst.POORUNDEADLEADER.defaultValue = monsterDB.poorundeadleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.POORUNDEADLEADER.defaultValue = "";
					}
					break;
				case OKUNDEADLEADER:
					if (monsterDB.okundeadleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.okundeadleader));
						Inst.OKUNDEADLEADER.defaultValue = monsterDB.okundeadleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.OKUNDEADLEADER.defaultValue = "";
					}
					break;
				case GOODUNDEADLEADER:
					if (monsterDB.goodundeadleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.goodundeadleader));
						Inst.GOODUNDEADLEADER.defaultValue = monsterDB.goodundeadleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.GOODUNDEADLEADER.defaultValue = "";
					}
					break;
				case EXPERTUNDEADLEADER:
					if (monsterDB.expertundeadleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.expertundeadleader));
						Inst.EXPERTUNDEADLEADER.defaultValue = monsterDB.expertundeadleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.EXPERTUNDEADLEADER.defaultValue = "";
					}
					break;
				case SUPERIORUNDEADLEADER:
					if (monsterDB.superiorundeadleader != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.superiorundeadleader));
						Inst.SUPERIORUNDEADLEADER.defaultValue = monsterDB.superiorundeadleader.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUPERIORUNDEADLEADER.defaultValue = "";
					}
					break;
				case WEAPON1:
					if (monsterDB.weapon1 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.weapon1));
						Inst.WEAPON1.defaultValue = monsterDB.weapon1;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WEAPON1.defaultValue = "";
					}
					break;
				case WEAPON2:
					if (monsterDB.weapon2 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.weapon2));
						Inst.WEAPON2.defaultValue = monsterDB.weapon2;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WEAPON2.defaultValue = "";
					}
					break;
				case WEAPON3:
					if (monsterDB.weapon3 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.weapon3));
						Inst.WEAPON3.defaultValue = monsterDB.weapon3;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WEAPON3.defaultValue = "";
					}
					break;
				case WEAPON4:
					if (monsterDB.weapon4 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.weapon4));
						Inst.WEAPON4.defaultValue = monsterDB.weapon4;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WEAPON4.defaultValue = "";
					}
					break;
				case FIRSTSHAPE:
					if (monsterDB.firstshape != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.firstshape));
						Inst.FIRSTSHAPE.defaultValue = monsterDB.firstshape;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FIRSTSHAPE.defaultValue = "";
					}
					break;
				case SECONDSHAPE:
					if (monsterDB.secondshape != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.secondshape));
						Inst.SECONDSHAPE.defaultValue = monsterDB.secondshape;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SECONDSHAPE.defaultValue = "";
					}
					break;
				case SECONDTMPSHAPE:
					if (monsterDB.secondtmpshape != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.secondtmpshape));
						Inst.SECONDTMPSHAPE.defaultValue = monsterDB.secondtmpshape;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SECONDTMPSHAPE.defaultValue = "";
					}
					break;
				case CLEANSHAPE:
					if (monsterDB.cleanshape != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.cleanshape));
						Inst.CLEANSHAPE.defaultValue = monsterDB.cleanshape.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.CLEANSHAPE.defaultValue = "";
					}
					break;
				case SHAPECHANGE:
					if (monsterDB.shapechange != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.shapechange));
						Inst.SHAPECHANGE.defaultValue = monsterDB.shapechange;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SHAPECHANGE.defaultValue = "";
					}
					break;
				case LANDSHAPE:
					if (monsterDB.landshape != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.landshape));
						Inst.LANDSHAPE.defaultValue = monsterDB.landshape;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.LANDSHAPE.defaultValue = "";
					}
					break;
				case WATERSHAPE:
					if (monsterDB.watershape != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.watershape));
						Inst.WATERSHAPE.defaultValue = monsterDB.watershape;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WATERSHAPE.defaultValue = "";
					}
					break;
				case FORESTSHAPE:
					if (monsterDB.forestshape != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.forestshape));
						Inst.FORESTSHAPE.defaultValue = monsterDB.forestshape;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FORESTSHAPE.defaultValue = "";
					}
					break;
				case PLAINSHAPE:
					if (monsterDB.plainshape != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.plainshape));
						Inst.PLAINSHAPE.defaultValue = monsterDB.plainshape;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.PLAINSHAPE.defaultValue = "";
					}
					break;
				case DOMSUMMON:
					if (monsterDB.domsummon != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.domsummon));
						Inst.DOMSUMMON.defaultValue = monsterDB.domsummon;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DOMSUMMON.defaultValue = "";
					}
					break;
				case DOMSUMMON2:
					if (monsterDB.domsummon2 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.domsummon2));
						Inst.DOMSUMMON2.defaultValue = monsterDB.domsummon2;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DOMSUMMON2.defaultValue = "";
					}
					break;
				case DOMSUMMON20:
					if (monsterDB.domsummon20 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.domsummon20));
						Inst.DOMSUMMON20.defaultValue = monsterDB.domsummon20;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DOMSUMMON20.defaultValue = "";
					}
					break;
				case MAKEMONSTERS1:
					if (monsterDB.makemonsters1 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.makemonsters1));
						Inst.MAKEMONSTERS1.defaultValue = monsterDB.makemonsters1;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAKEMONSTERS1.defaultValue = "";
					}
					break;
				case MAKEMONSTERS2:
					if (monsterDB.makemonsters2 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.makemonsters2));
						Inst.MAKEMONSTERS2.defaultValue = monsterDB.makemonsters2;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAKEMONSTERS2.defaultValue = "";
					}
					break;
				case MAKEMONSTERS3:
					if (monsterDB.makemonsters3 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.makemonsters3));
						Inst.MAKEMONSTERS3.defaultValue = monsterDB.makemonsters3;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAKEMONSTERS3.defaultValue = "";
					}
					break;
				case MAKEMONSTERS4:
					if (monsterDB.makemonsters4 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.makemonsters4));
						Inst.MAKEMONSTERS4.defaultValue = monsterDB.makemonsters4;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAKEMONSTERS4.defaultValue = "";
					}
					break;
				case MAKEMONSTERS5:
					if (monsterDB.makemonsters5 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.makemonsters5));
						Inst.MAKEMONSTERS5.defaultValue = monsterDB.makemonsters5;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAKEMONSTERS5.defaultValue = "";
					}
					break;
				case SUMMON1:
					if (monsterDB.summon1 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.summon1));
						Inst.SUMMON1.defaultValue = monsterDB.summon1;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUMMON1.defaultValue = "";
					}
					break;
				case SUMMON2:
					if (monsterDB.summon2 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.summon2));
						Inst.SUMMON2.defaultValue = monsterDB.summon2;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUMMON2.defaultValue = "";
					}
					break;
				case SUMMON3:
					if (monsterDB.summon3 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.summon3));
						Inst.SUMMON3.defaultValue = monsterDB.summon3;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUMMON3.defaultValue = "";
					}
					break;
				case SUMMON4:
					if (monsterDB.summon4 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.summon4));
						Inst.SUMMON4.defaultValue = monsterDB.summon4;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUMMON4.defaultValue = "";
					}
					break;
				case SUMMON5:
					if (monsterDB.summon5 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.summon5));
						Inst.SUMMON5.defaultValue = monsterDB.summon5;
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SUMMON5.defaultValue = "";
					}
					break;
				case FIXEDNAME:
					if (monsterDB.fixedname != null) {
						((Inst1Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.fixedname));
						Inst.FIXEDNAME.defaultValue = monsterDB.fixedname.toString();
					} else {
						((Inst1Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FIXEDNAME.defaultValue = "";
					}
					break;
				case SLOWREC:
					if (monsterDB.slowrec != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.slowrec));
						Inst.SLOWREC.defaultValue = monsterDB.slowrec.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SLOWREC.defaultValue = "";
					}
					break;
				case NOSLOWREC:
					if (monsterDB.noslowrec != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.noslowrec));
						Inst.NOSLOWREC.defaultValue = monsterDB.noslowrec.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NOSLOWREC.defaultValue = "";
					}
					break;
				case RECLIMIT:
					if (monsterDB.reclimit != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.reclimit));
						Inst.RECLIMIT.defaultValue = monsterDB.reclimit.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.RECLIMIT.defaultValue = "0";
					}
					break;
				case REQLAB:
					if (monsterDB.reqlab != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.reqlab));
						Inst.REQLAB.defaultValue = monsterDB.reqlab.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.REQLAB.defaultValue = "";
					}
					break;
				case REQTEMPLE:
					if (monsterDB.reqtemple != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.reqtemple));
						Inst.REQTEMPLE.defaultValue = monsterDB.reqtemple.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.REQTEMPLE.defaultValue = "";
					}
					break;
				case CHAOSREC:
					if (monsterDB.chaosrec != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.chaosrec));
						Inst.CHAOSREC.defaultValue = monsterDB.chaosrec.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.CHAOSREC.defaultValue = "0";
					}
					break;
				case SINGLEBATTLE:
					if (monsterDB.singlebattle != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.singlebattle));
						Inst.SINGLEBATTLE.defaultValue = monsterDB.singlebattle.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SINGLEBATTLE.defaultValue = "";
					}
					break;
				case AISINGLEREC:
					if (monsterDB.aisinglerec != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.aisinglerec));
						Inst.AISINGLEREC.defaultValue = monsterDB.aisinglerec.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AISINGLEREC.defaultValue = "";
					}
					break;
				case AINOREC:
					if (monsterDB.ainorec != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.ainorec));
						Inst.AINOREC.defaultValue = monsterDB.ainorec.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AINOREC.defaultValue = "";
					}
					break;
				case HOMEREALM:
					if (monsterDB.homerealm != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.homerealm));
						Inst.HOMEREALM.defaultValue = monsterDB.homerealm.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HOMEREALM.defaultValue = "0";
					}
					break;
				case LESSERHORROR:
					if (monsterDB.lesserhorror != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.lesserhorror));
						Inst.LESSERHORROR.defaultValue = monsterDB.lesserhorror.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.LESSERHORROR.defaultValue = "";
					}
					break;
				case GREATERHORROR:
					if (monsterDB.greaterhorror != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.greaterhorror));
						Inst.GREATERHORROR.defaultValue = monsterDB.greaterhorror.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.GREATERHORROR.defaultValue = "";
					}
					break;
				case DOOMHORROR:
					if (monsterDB.doomhorror != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.doomhorror));
						Inst.DOOMHORROR.defaultValue = monsterDB.doomhorror.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DOOMHORROR.defaultValue = "";
					}
					break;
				case BUG:
					if (monsterDB.bug != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.bug));
						Inst.BUG.defaultValue = monsterDB.bug.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BUG.defaultValue = "";
					}
					break;
				case UWBUG:
					if (monsterDB.uwbug != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.uwbug));
						Inst.UWBUG.defaultValue = monsterDB.uwbug.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.UWBUG.defaultValue = "";
					}
					break;
				case AUTOCOMPETE:
					if (monsterDB.autocompete != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.autocompete));
						Inst.AUTOCOMPETE.defaultValue = monsterDB.autocompete.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AUTOCOMPETE.defaultValue = "";
					}
					break;
				case FLOAT:
					if (monsterDB.floatBoolean != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.floatBoolean));
						Inst.FLOAT.defaultValue = monsterDB.floatBoolean.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FLOAT.defaultValue = "";
					}
					break;
				case TELEPORT:
					if (monsterDB.teleport != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.teleport));
						Inst.TELEPORT.defaultValue = monsterDB.teleport.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TELEPORT.defaultValue = "";
					}
					break;
				case NORIVERPASS:
					if (monsterDB.noriverpass != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.noriverpass));
						Inst.NORIVERPASS.defaultValue = monsterDB.noriverpass.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NORIVERPASS.defaultValue = "";
					}
					break;
				case UNTELEPORTABLE:
					if (monsterDB.unteleportable != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.unteleportable));
						Inst.UNTELEPORTABLE.defaultValue = monsterDB.unteleportable.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.UNTELEPORTABLE.defaultValue = "";
					}
					break;
				case GIFTOFWATER:
					if (monsterDB.giftofwater != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.giftofwater));
						Inst.GIFTOFWATER.defaultValue = monsterDB.giftofwater.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.GIFTOFWATER.defaultValue = "0";
					}
					break;
				case INDEPMOVE:
					if (monsterDB.indepmove != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.indepmove));
						Inst.INDEPMOVE.defaultValue = monsterDB.indepmove.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INDEPMOVE.defaultValue = "0";
					}
					break;
				case PATIENCE:
					if (monsterDB.patience != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.patience));
						Inst.PATIENCE.defaultValue = monsterDB.patience.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.PATIENCE.defaultValue = "0";
					}
					break;
				case FALSEARMY:
					if (monsterDB.falsearmy != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.falsearmy));
						Inst.FALSEARMY.defaultValue = monsterDB.falsearmy.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FALSEARMY.defaultValue = "0";
					}
					break;
				case FOOLSCOUTS:
					if (monsterDB.foolscouts != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.foolscouts));
						Inst.FOOLSCOUTS.defaultValue = monsterDB.foolscouts.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FOOLSCOUTS.defaultValue = "0";
					}
					break;
				case DESERTER:
					if (monsterDB.deserter != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.deserter));
						Inst.DESERTER.defaultValue = monsterDB.deserter.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DESERTER.defaultValue = "0";
					}
					break;
				case HORRORDESERTER:
					if (monsterDB.horrordeserter != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.horrordeserter));
						Inst.HORRORDESERTER.defaultValue = monsterDB.horrordeserter.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HORRORDESERTER.defaultValue = "0";
					}
					break;
				case DEFECTOR:
					if (monsterDB.defector != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.defector));
						Inst.DEFECTOR.defaultValue = monsterDB.defector.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEFECTOR.defaultValue = "0";
					}
					break;
				case AUTOHEALER:
					if (monsterDB.autohealer != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.autohealer));
						Inst.AUTOHEALER.defaultValue = monsterDB.autohealer.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AUTOHEALER.defaultValue = "0";
					}
					break;
				case AUTODISHEALER:
					if (monsterDB.autodishealer != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.autodishealer));
						Inst.AUTODISHEALER.defaultValue = monsterDB.autodishealer.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AUTODISHEALER.defaultValue = "0";
					}
					break;
				case AUTODISGRINDER:
					if (monsterDB.autodisgrinder != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.autodisgrinder));
						Inst.AUTODISGRINDER.defaultValue = monsterDB.autodisgrinder.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AUTODISGRINDER.defaultValue = "0";
					}
					break;
				case WOUNDFEND:
					if (monsterDB.woundfend != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.woundfend));
						Inst.WOUNDFEND.defaultValue = monsterDB.woundfend.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WOUNDFEND.defaultValue = "0";
					}
					break;
				case HPOVERFLOW:
					if (monsterDB.hpoverflow != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.hpoverflow));
						Inst.HPOVERFLOW.defaultValue = monsterDB.hpoverflow.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.HPOVERFLOW.defaultValue = "";
					}
					break;
				case PIERCERES:
					if (monsterDB.pierceres != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.pierceres));
						Inst.PIERCERES.defaultValue = monsterDB.pierceres.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.PIERCERES.defaultValue = "";
					}
					break;
				case SLASHRES:
					if (monsterDB.slashres != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.slashres));
						Inst.SLASHRES.defaultValue = monsterDB.slashres.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SLASHRES.defaultValue = "";
					}
					break;
				case BLUNTRES:
					if (monsterDB.bluntres != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.bluntres));
						Inst.BLUNTRES.defaultValue = monsterDB.bluntres.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BLUNTRES.defaultValue = "";
					}
					break;
				case DAMAGEREV:
					if (monsterDB.damagerev != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.damagerev));
						Inst.DAMAGEREV.defaultValue = monsterDB.damagerev.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DAMAGEREV.defaultValue = "0";
					}
					break;
				case SLIMER:
					if (monsterDB.slimer != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.slimer));
						Inst.SLIMER.defaultValue = monsterDB.slimer.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SLIMER.defaultValue = "0";
					}
					break;
				case DEATHCURSE:
					if (monsterDB.deathcurse != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.deathcurse));
						Inst.DEATHCURSE.defaultValue = monsterDB.deathcurse.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEATHCURSE.defaultValue = "";
					}
					break;
				case DEATHDISEASE:
					if (monsterDB.deathdisease != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.deathdisease));
						Inst.DEATHDISEASE.defaultValue = monsterDB.deathdisease.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEATHDISEASE.defaultValue = "0";
					}
					break;
				case DEATHPARALYZE:
					if (monsterDB.deathparalyze != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.deathparalyze));
						Inst.DEATHPARALYZE.defaultValue = monsterDB.deathparalyze.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEATHPARALYZE.defaultValue = "0";
					}
					break;
				case DEATHFIRE:
					if (monsterDB.deathfire != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.deathfire));
						Inst.DEATHFIRE.defaultValue = monsterDB.deathfire.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEATHFIRE.defaultValue = "0";
					}
					break;
				case CHAOSPOWER:
					if (monsterDB.chaospower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.chaospower));
						Inst.CHAOSPOWER.defaultValue = monsterDB.chaospower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.CHAOSPOWER.defaultValue = "0";
					}
					break;
				case MAGICPOWER:
					if (monsterDB.magicpower != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicpower));
						Inst.MAGICPOWER.defaultValue = monsterDB.magicpower.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAGICPOWER.defaultValue = "0";
					}
					break;
				case TRAMPSWALLOW:
					if (monsterDB.trampswallow != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.trampswallow));
						Inst.TRAMPSWALLOW.defaultValue = monsterDB.trampswallow.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TRAMPSWALLOW.defaultValue = "";
					}
					break;
				case DIGEST:
					if (monsterDB.digest != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.digest));
						Inst.DIGEST.defaultValue = monsterDB.digest.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DIGEST.defaultValue = "0";
					}
					break;
				case INCORPORATE:
					if (monsterDB.incorporate != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.incorporate));
						Inst.INCORPORATE.defaultValue = monsterDB.incorporate.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INCORPORATE.defaultValue = "0";
					}
					break;
				case INCPROVDEF:
					if (monsterDB.incprovdef != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.incprovdef));
						Inst.INCPROVDEF.defaultValue = monsterDB.incprovdef.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INCPROVDEF.defaultValue = "0";
					}
					break;
				case ELEGIST:
					if (monsterDB.elegist != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.elegist));
						Inst.ELEGIST.defaultValue = monsterDB.elegist.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ELEGIST.defaultValue = "0";
					}
					break;
				case TAXCOLLECTOR:
					if (monsterDB.taxcollector != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.taxcollector));
						Inst.TAXCOLLECTOR.defaultValue = monsterDB.taxcollector.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TAXCOLLECTOR.defaultValue = "";
					}
					break;
				case GOLD:
					if (monsterDB.gold != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.gold));
						Inst.GOLD.defaultValue = monsterDB.gold.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.GOLD.defaultValue = "0";
					}
					break;
				case NOHOF:
					if (monsterDB.nohof != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.nohof));
						Inst.NOHOF.defaultValue = monsterDB.nohof.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NOHOF.defaultValue = "";
					}
					break;
				case GROWHP:
					if (monsterDB.growhp != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.growhp));
						Inst.GROWHP.defaultValue = monsterDB.growhp.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.GROWHP.defaultValue = "0";
					}
					break;
				case SHRINKHP:
					if (monsterDB.shrinkhp != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.shrinkhp));
						Inst.SHRINKHP.defaultValue = monsterDB.shrinkhp.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SHRINKHP.defaultValue = "0";
					}
					break;
				case REANIMATOR:
					if (monsterDB.reanimator != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.reanimator));
						Inst.REANIMATOR.defaultValue = monsterDB.reanimator.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.REANIMATOR.defaultValue = "0";
					}
					break;
				case RAREDOMSUMMON:
					if (monsterDB.raredomsummon != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.raredomsummon));
						Inst.RAREDOMSUMMON.defaultValue = monsterDB.raredomsummon.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.RAREDOMSUMMON.defaultValue = "";
					}
					break;
				case BATTLESUM1:
					if (monsterDB.battlesum1 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.battlesum1));
						Inst.BATTLESUM1.defaultValue = monsterDB.battlesum1.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATTLESUM1.defaultValue = "";
					}
					break;
				case BATTLESUM2:
					if (monsterDB.battlesum2 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.battlesum2));
						Inst.BATTLESUM2.defaultValue = monsterDB.battlesum2.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATTLESUM2.defaultValue = "";
					}
					break;
				case BATTLESUM3:
					if (monsterDB.battlesum3 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.battlesum3));
						Inst.BATTLESUM3.defaultValue = monsterDB.battlesum3.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATTLESUM3.defaultValue = "";
					}
					break;
				case BATTLESUM4:
					if (monsterDB.battlesum4 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.battlesum4));
						Inst.BATTLESUM4.defaultValue = monsterDB.battlesum4.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATTLESUM4.defaultValue = "";
					}
					break;
				case BATTLESUM5:
					if (monsterDB.battlesum5 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.battlesum5));
						Inst.BATTLESUM5.defaultValue = monsterDB.battlesum5.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATTLESUM5.defaultValue = "";
					}
					break;
				case BATSTARTSUM1:
					if (monsterDB.batstartsum1 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum1));
						Inst.BATSTARTSUM1.defaultValue = monsterDB.batstartsum1.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM1.defaultValue = "";
					}
					break;
				case BATSTARTSUM2:
					if (monsterDB.batstartsum2 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum2));
						Inst.BATSTARTSUM2.defaultValue = monsterDB.batstartsum2.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM2.defaultValue = "";
					}
					break;
				case BATSTARTSUM3:
					if (monsterDB.batstartsum3 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum3));
						Inst.BATSTARTSUM3.defaultValue = monsterDB.batstartsum3.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM3.defaultValue = "";
					}
					break;
				case BATSTARTSUM4:
					if (monsterDB.batstartsum4 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum4));
						Inst.BATSTARTSUM4.defaultValue = monsterDB.batstartsum4.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM4.defaultValue = "";
					}
					break;
				case BATSTARTSUM5:
					if (monsterDB.batstartsum5 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum5));
						Inst.BATSTARTSUM5.defaultValue = monsterDB.batstartsum5.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM5.defaultValue = "";
					}
					break;
				case BATSTARTSUM1D6:
					if (monsterDB.batstartsum1d6 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum1d6));
						Inst.BATSTARTSUM1D6.defaultValue = monsterDB.batstartsum1d6.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM1D6.defaultValue = "";
					}
					break;
				case BATSTARTSUM2D6:
					if (monsterDB.batstartsum2d6 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum2d6));
						Inst.BATSTARTSUM2D6.defaultValue = monsterDB.batstartsum2d6.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM2D6.defaultValue = "";
					}
					break;
				case BATSTARTSUM3D6:
					if (monsterDB.batstartsum3d6 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum3d6));
						Inst.BATSTARTSUM3D6.defaultValue = monsterDB.batstartsum3d6.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM3D6.defaultValue = "";
					}
					break;
				case BATSTARTSUM4D6:
					if (monsterDB.batstartsum4d6 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum4d6));
						Inst.BATSTARTSUM4D6.defaultValue = monsterDB.batstartsum4d6.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM4D6.defaultValue = "";
					}
					break;
				case BATSTARTSUM5D6:
					if (monsterDB.batstartsum5d6 != null) {
						((Inst5Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.batstartsum5d6));
						Inst.BATSTARTSUM5D6.defaultValue = monsterDB.batstartsum5d6.toString();
					} else {
						((Inst5Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BATSTARTSUM5D6.defaultValue = "";
					}
					break;
				case MONTAG:
					if (monsterDB.montag != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.montag));
						Inst.MONTAG.defaultValue = monsterDB.montag.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MONTAG.defaultValue = "0";
					}
					break;
				case INSPIRATIONAL:
					if (monsterDB.inspirational != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.inspirational));
						Inst.INSPIRATIONAL.defaultValue = monsterDB.inspirational.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INSPIRATIONAL.defaultValue = "0";
					}
					break;
				case BEASTMASTER:
					if (monsterDB.beastmaster != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.beastmaster));
						Inst.BEASTMASTER.defaultValue = monsterDB.beastmaster.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BEASTMASTER.defaultValue = "0";
					}
					break;
				case TASKMASTER:
					if (monsterDB.taskmaster != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.taskmaster));
						Inst.TASKMASTER.defaultValue = monsterDB.taskmaster.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TASKMASTER.defaultValue = "0";
					}
					break;
				case SLAVE:
					if (monsterDB.slave != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.slave));
						Inst.SLAVE.defaultValue = monsterDB.slave.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SLAVE.defaultValue = "";
					}
					break;
				case UNDISCIPLINED:
					if (monsterDB.undisciplined != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.undisciplined));
						Inst.UNDISCIPLINED.defaultValue = monsterDB.undisciplined.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.UNDISCIPLINED.defaultValue = "";
					}
					break;
				case FORMATIONFIGHTER:
					if (monsterDB.formationfighter != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.formationfighter));
						Inst.FORMATIONFIGHTER.defaultValue = monsterDB.formationfighter.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FORMATIONFIGHTER.defaultValue = "0";
					}
					break;
				case BODYGUARD:
					if (monsterDB.bodyguard != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.bodyguard));
						Inst.BODYGUARD.defaultValue = monsterDB.bodyguard.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BODYGUARD.defaultValue = "0";
					}
					break;
				case DIVINEINS:
					if (monsterDB.divineins != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.divineins));
						Inst.DIVINEINS.defaultValue = monsterDB.divineins.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DIVINEINS.defaultValue = "0";
					}
					break;
				case MAGICIMMUNE:
					if (monsterDB.magicimmune != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.magicimmune));
						Inst.MAGICIMMUNE.defaultValue = monsterDB.magicimmune.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAGICIMMUNE.defaultValue = "";
					}
					break;
				case FIRERANGE:
					if (monsterDB.firerange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.firerange));
						Inst.FIRERANGE.defaultValue = monsterDB.firerange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FIRERANGE.defaultValue = "0";
					}
					break;
				case AIRRANGE:
					if (monsterDB.airrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.airrange));
						Inst.AIRRANGE.defaultValue = monsterDB.airrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.AIRRANGE.defaultValue = "0";
					}
					break;
				case WATERRANGE:
					if (monsterDB.waterrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.waterrange));
						Inst.WATERRANGE.defaultValue = monsterDB.waterrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.WATERRANGE.defaultValue = "0";
					}
					break;
				case EARTHRANGE:
					if (monsterDB.earthrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.earthrange));
						Inst.EARTHRANGE.defaultValue = monsterDB.earthrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.EARTHRANGE.defaultValue = "0";
					}
					break;
				case ASTRALRANGE:
					if (monsterDB.astralrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.astralrange));
						Inst.ASTRALRANGE.defaultValue = monsterDB.astralrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ASTRALRANGE.defaultValue = "0";
					}
					break;
				case DEATHRANGE:
					if (monsterDB.deathrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.deathrange));
						Inst.DEATHRANGE.defaultValue = monsterDB.deathrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEATHRANGE.defaultValue = "0";
					}
					break;
				case NATURERANGE:
					if (monsterDB.naturerange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.naturerange));
						Inst.NATURERANGE.defaultValue = monsterDB.naturerange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.NATURERANGE.defaultValue = "0";
					}
					break;
				case BLOODRANGE:
					if (monsterDB.bloodrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.bloodrange));
						Inst.BLOODRANGE.defaultValue = monsterDB.bloodrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BLOODRANGE.defaultValue = "0";
					}
					break;
				case ELEMENTRANGE:
					if (monsterDB.elementrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.elementrange));
						Inst.ELEMENTRANGE.defaultValue = monsterDB.elementrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ELEMENTRANGE.defaultValue = "0";
					}
					break;
				case SORCERYRANGE:
					if (monsterDB.sorceryrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.sorceryrange));
						Inst.SORCERYRANGE.defaultValue = monsterDB.sorceryrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.SORCERYRANGE.defaultValue = "0";
					}
					break;
				case ALLRANGE:
					if (monsterDB.allrange != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.allrange));
						Inst.ALLRANGE.defaultValue = monsterDB.allrange.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ALLRANGE.defaultValue = "0";
					}
					break;
				case TMPFIREGEMS:
					if (monsterDB.tmpfiregems != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tmpfiregems));
						Inst.TMPFIREGEMS.defaultValue = monsterDB.tmpfiregems.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TMPFIREGEMS.defaultValue = "0";
					}
					break;
				case TMPAIRGEMS:
					if (monsterDB.tmpairgems != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tmpairgems));
						Inst.TMPAIRGEMS.defaultValue = monsterDB.tmpairgems.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TMPAIRGEMS.defaultValue = "0";
					}
					break;
				case TMPWATERGEMS:
					if (monsterDB.tmpwatergems != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tmpwatergems));
						Inst.TMPWATERGEMS.defaultValue = monsterDB.tmpwatergems.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TMPWATERGEMS.defaultValue = "0";
					}
					break;
				case TMPEARTHGEMS:
					if (monsterDB.tmpearthgems != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tmpearthgems));
						Inst.TMPEARTHGEMS.defaultValue = monsterDB.tmpearthgems.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TMPEARTHGEMS.defaultValue = "0";
					}
					break;
				case TMPASTRALGEMS:
					if (monsterDB.tmpastralgems != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tmpastralgems));
						Inst.TMPASTRALGEMS.defaultValue = monsterDB.tmpastralgems.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TMPASTRALGEMS.defaultValue = "0";
					}
					break;
				case TMPDEATHGEMS:
					if (monsterDB.tmpdeathgems != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tmpdeathgems));
						Inst.TMPDEATHGEMS.defaultValue = monsterDB.tmpdeathgems.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TMPDEATHGEMS.defaultValue = "0";
					}
					break;
				case TMPNATUREGEMS:
					if (monsterDB.tmpnaturegems != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tmpnaturegems));
						Inst.TMPNATUREGEMS.defaultValue = monsterDB.tmpnaturegems.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TMPNATUREGEMS.defaultValue = "0";
					}
					break;
				case TMPBLOODSLAVES:
					if (monsterDB.tmpbloodslaves != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tmpbloodslaves));
						Inst.TMPBLOODSLAVES.defaultValue = monsterDB.tmpbloodslaves.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TMPBLOODSLAVES.defaultValue = "0";
					}
					break;
				case MAKEPEARLS:
					if (monsterDB.makepearls != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.makepearls));
						Inst.MAKEPEARLS.defaultValue = monsterDB.makepearls.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MAKEPEARLS.defaultValue = "0";
					}
					break;
				case BONUSSPELLS:
					if (monsterDB.bonusspells != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.bonusspells));
						Inst.BONUSSPELLS.defaultValue = monsterDB.bonusspells.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.BONUSSPELLS.defaultValue = "0";
					}
					break;
				case RANDOMSPELL:
					if (monsterDB.randomspell != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.randomspell));
						Inst.RANDOMSPELL.defaultValue = monsterDB.randomspell.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.RANDOMSPELL.defaultValue = "0";
					}
					break;
				case TAINTED:
					if (monsterDB.tainted != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.tainted));
						Inst.TAINTED.defaultValue = monsterDB.tainted.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.TAINTED.defaultValue = "0";
					}
					break;
				case FIXFORGEBONUS:
					if (monsterDB.fixforgebonus != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.fixforgebonus));
						Inst.FIXFORGEBONUS.defaultValue = monsterDB.fixforgebonus.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.FIXFORGEBONUS.defaultValue = "0";
					}
					break;
				case MASTERSMITH:
					if (monsterDB.mastersmith != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.mastersmith));
						Inst.MASTERSMITH.defaultValue = monsterDB.mastersmith.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.MASTERSMITH.defaultValue = "0";
					}
					break;
				case COMSLAVE:
					if (monsterDB.comslave != null) {
						((Inst4Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.comslave));
						Inst.COMSLAVE.defaultValue = monsterDB.comslave.toString();
					} else {
						((Inst4Fields)fields.getValue()).defaultLabel.setText("");
						Inst.COMSLAVE.defaultValue = "";
					}
					break;
				case CROSSBREEDER:
					if (monsterDB.crossbreeder != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.crossbreeder));
						Inst.CROSSBREEDER.defaultValue = monsterDB.crossbreeder.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.CROSSBREEDER.defaultValue = "0";
					}
					break;
				case DEATHBANISH:
					if (monsterDB.deathbanish != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.deathbanish));
						Inst.DEATHBANISH.defaultValue = monsterDB.deathbanish.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.DEATHBANISH.defaultValue = "0";
					}
					break;
				case KOKYTOSRET:
					if (monsterDB.kokytosret != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.kokytosret));
						Inst.KOKYTOSRET.defaultValue = monsterDB.kokytosret.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.KOKYTOSRET.defaultValue = "0";
					}
					break;
				case INFERNORET:
					if (monsterDB.infernoret != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.infernoret));
						Inst.INFERNORET.defaultValue = monsterDB.infernoret.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.INFERNORET.defaultValue = "0";
					}
					break;
				case VOIDRET:
					if (monsterDB.voidret != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.voidret));
						Inst.VOIDRET.defaultValue = monsterDB.voidret.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.VOIDRET.defaultValue = "0";
					}
					break;
				case ALLRET:
					if (monsterDB.allret != null) {
						((Inst2Fields)fields.getValue()).defaultLabel.setText(Messages.format("DetailsPage.DefaultLabel.fmt", monsterDB.allret));
						Inst.ALLRET.defaultValue = monsterDB.allret.toString();
					} else {
						((Inst2Fields)fields.getValue()).defaultLabel.setText("");
						Inst.ALLRET.defaultValue = "0";
					}
					break;
				}
			}
		}
		name.getParent().getParent().layout(true, true);
	}
	
	private boolean isDefaultValue(Inst value, MonsterDB monsterDB) {
		switch (value) {
		case MAGICSKILL1:
			if (monsterDB.magicskillpath1 != null && monsterDB.magicskilllevel1 != null) {
				return true;
			}
			break;
		case MAGICSKILL2:
			if (monsterDB.magicskillpath2 != null && monsterDB.magicskilllevel2 != null) {
				return true;
			}
			break;
		case MAGICSKILL3:
			if (monsterDB.magicskillpath3 != null && monsterDB.magicskilllevel3 != null) {
				return true;
			}
			break;
		case MAGICSKILL4:
			if (monsterDB.magicskillpath4 != null && monsterDB.magicskilllevel4 != null) {
				return true;
			}
			break;
		case CUSTOMMAGIC1:
			if (monsterDB.custommagicpath1 != null && monsterDB.custommagicchance1 != null) {
				return true;
			}
			break;
		case CUSTOMMAGIC2:
			if (monsterDB.custommagicpath2 != null && monsterDB.custommagicchance2 != null) {
				return true;
			}
			break;
		case CUSTOMMAGIC3:
			if (monsterDB.custommagicpath3 != null && monsterDB.custommagicchance3 != null) {
				return true;
			}
			break;
		case CUSTOMMAGIC4:
			if (monsterDB.custommagicpath4 != null && monsterDB.custommagicchance4 != null) {
				return true;
			}
			break;
		case CUSTOMMAGIC5:
			if (monsterDB.custommagicpath5 != null && monsterDB.custommagicchance5 != null) {
				return true;
			}
			break;
		case CUSTOMMAGIC6:
			if (monsterDB.custommagicpath6 != null && monsterDB.custommagicchance6 != null) {
				return true;
			}
			break;
		case CUSTOMMAGIC7:
			if (monsterDB.custommagicpath7 != null && monsterDB.custommagicchance7 != null) {
				return true;
			}
			break;
		case CUSTOMMAGIC8:
			if (monsterDB.custommagicpath8 != null && monsterDB.custommagicchance8 != null) {
				return true;
			}
			break;
		}
		return false;
	}
	
	private String getSelectMonstername(Monster monster) {
		if (monster instanceof SelectMonsterByName) {
			return ((SelectMonsterByName)monster).getValue();
		} else {
			int id = ((SelectMonsterById)monster).getValue();
			return Database.getMonsterName(id);
		}
	}
	
	private String getSelectMonsterdescr(Monster monster) {
		if (monster instanceof SelectMonsterByName) {
			String name = ((SelectMonsterByName)monster).getValue();
			return Database.getMonsterDescr(Database.getMonster(name).id);
		} else if (monster instanceof SelectMonsterById) {
			return Database.getMonsterDescr(((SelectMonsterById)monster).getValue());
		}
		return "";
	}
	
	private int getSelectMonsterid(Monster monster) {
		if (monster instanceof SelectMonsterByName) {
			MonsterDB monsterDB = Database.getMonster(((SelectMonsterByName) monster).getValue());
			return monsterDB != null && monsterDB.id != null ? monsterDB.id.intValue() : 0;
		} else {
			return ((SelectMonsterById)monster).getValue();
		}
	}
	
	private String getMonstername(Monster item) {
		EList<MonsterMods> list = item.getMods();
		for (MonsterMods mod : list) {
			if (mod instanceof MonsterInst1) {
				if (((MonsterInst1)mod).isName()) {
					return ((MonsterInst1)mod).getValue();
				}
			}
		}
		return null;
	}
	
	private void setMonstername(final XtextEditor editor, final String newName) 
	{
		final IXtextDocument myDocument = editor.getDocument();
		myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				Monster monsterToEdit = (Monster)input;
				EList<MonsterMods> mods = monsterToEdit.getMods();
				boolean nameSet = false;
				for (MonsterMods mod : mods) {
					if (mod instanceof MonsterInst1) {
						if (((MonsterInst1)mod).isName()) {
							((MonsterInst1)mod).setValue(newName);
							nameSet = true;
						}
					}
				}
				if (!nameSet) {
					MonsterInst1 nameInst = DmFactory.eINSTANCE.createMonsterInst1();
					nameInst.setName(true);
					nameInst.setValue(newName);
					mods.add(nameInst);
				}
			}  
		});

		updateSelection();
	}

	private void setMonsterdescr(final XtextEditor editor, final String newName) 
	{
		final IXtextDocument myDocument = editor.getDocument();
		myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				Monster monsterToEdit = (Monster)input;
				EList<MonsterMods> mods = monsterToEdit.getMods();
				boolean nameSet = false;
				for (MonsterMods mod : mods) {
					if (mod instanceof MonsterInst1) {
						if (((MonsterInst1)mod).isDescr()) {
							((MonsterInst1)mod).setValue(newName);
							nameSet = true;
						}
					}
				}
				if (!nameSet) {
					MonsterInst1 nameInst = DmFactory.eINSTANCE.createMonsterInst1();
					nameInst.setDescr(true);
					nameInst.setValue(newName);
					mods.add(nameInst);
				}
			}  
		});

		updateSelection();
	}
	
	private int getItemMask(int hands, int heads, int bodies, int feet, int misc) {
		return hands | heads | bodies | feet | misc;
	}

	private int getHands(int mask) {
		return mask & 30;
	}

	private int getHeads(int mask) {
		return mask & 384;
	}

	private int getBodies(int mask) {
		return mask & 1024;
	}

	private int getFeet(int mask) {
		return mask & 2048;
	}

	private int getMisc(int mask) {
		return mask & 61440;
	}
	
	private String getItemMaskString(int mask) {
		StringBuffer string = new StringBuffer();
		if ((mask & 30) == 2) {
			string.append("1 hand");
		} else if ((mask & 30) == 6) {
			string.append("2 hands");
		} else if ((mask & 30) == 14) {
			string.append("3 hands");
		} else if ((mask & 30) == 30) {
			string.append("4 hands");
		}
		if ((mask & 384) == 128) {
			if (string.length() > 0) string.append(",");
			string.append("1 head");
		} else if ((mask & 384) == 384) {
			if (string.length() > 0) string.append(",");
			string.append("2 heads");
		}
		if ((mask & 1024) == 1024) {
			if (string.length() > 0) string.append(",");
			string.append("1 body");
		}
		if ((mask & 2048) == 2048) {
			if (string.length() > 0) string.append(",");
			string.append("1 feet");
		}
		if ((mask & 61440) == 4096) {
			if (string.length() > 0) string.append(",");
			string.append("1 misc");
		} else if ((mask & 61440) == 12288) {
			if (string.length() > 0) string.append(",");
			string.append("2 misc");
		} else if ((mask & 61440) == 28672) {
			if (string.length() > 0) string.append(",");
			string.append("3 misc");
		} else if ((mask & 61440) == 61440) {
			if (string.length() > 0) string.append(",");
			string.append("4 misc");
		}
		return string.toString();
	}

	private String getInst1(Inst inst2, Object monster) {
		EList<MonsterMods> list = ((Monster)monster).getMods();
		for (MonsterMods mod : list) {
			if (mod instanceof MonsterInst1) {
				switch (inst2) {
				case NAME:
					if (((MonsterInst1)mod).isName()){
						return ((MonsterInst1)mod).getValue();
					}
					break;
				case SPR1:
					if (((MonsterInst1)mod).isSpr1()){
						return ((MonsterInst1)mod).getValue();
					}
					break;
				case SPR2:
					if (((MonsterInst1)mod).isSpr2()){
						return ((MonsterInst1)mod).getValue();
					}
					break;
				case DESCR:
					if (((MonsterInst1)mod).isDescr()){
						return ((MonsterInst1)mod).getValue();
					}
					break;
				case FIXEDNAME:
					if (((MonsterInst1)mod).isFixedname()){
						return ((MonsterInst1)mod).getValue();
					}
					break;
				}
			}
		}
		return null;
	}
	
	private Integer getInst2(Inst inst2, Object monster) {
		EList<MonsterMods> list = ((Monster)monster).getMods();
		for (MonsterMods mod : list) {
			if (mod instanceof MonsterInst2) {
				switch (inst2) {
				case SPECIALLOOK:
					if (((MonsterInst2)mod).isSpeciallook()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case AP:
					if (((MonsterInst2)mod).isAp()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MAPMOVE:
					if (((MonsterInst2)mod).isMapmove()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case HP:
					if (((MonsterInst2)mod).isHp()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case PROT:
					if (((MonsterInst2)mod).isProt()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SIZE:
					if (((MonsterInst2)mod).isSize()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case RESSIZE:
					if (((MonsterInst2)mod).isRessize()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case STR:
					if (((MonsterInst2)mod).isStr()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ENC:
					if (((MonsterInst2)mod).isEnc()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ATT:
					if (((MonsterInst2)mod).isAtt()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DEF:
					if (((MonsterInst2)mod).isDef()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case PREC:
					if (((MonsterInst2)mod).isPrec()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MR:
					if (((MonsterInst2)mod).isMr()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MOR:
					if (((MonsterInst2)mod).isMor()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case GCOST:
					if (((MonsterInst2)mod).isGcost()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case RCOST:
					if (((MonsterInst2)mod).isRcost()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case PATHCOST:
					if (((MonsterInst2)mod).isPathcost()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case STARTDOM:
					if (((MonsterInst2)mod).isStartdom()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case EYES:
					if (((MonsterInst2)mod).isEyes()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case VOIDSANITY:
					if (((MonsterInst2)mod).isVoidsanity()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case COPYSTATS:
					if (((MonsterInst2)mod).isCopystats()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case COPYSPR:
					if (((MonsterInst2)mod).isCopyspr()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SHATTEREDSOUL:
					if (((MonsterInst2)mod).isShatteredsoul()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case COLDRES:
					if (((MonsterInst2)mod).isColdres()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FIRERES:
					if (((MonsterInst2)mod).isFireres()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case POISONRES:
					if (((MonsterInst2)mod).isPoisonres()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SHOCKRES:
					if (((MonsterInst2)mod).isShockres()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DARKVISION:
					if (((MonsterInst2)mod).isDarkvision()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SEDUCE:
					if (((MonsterInst2)mod).isSeduce()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SUCCUBUS:
					if (((MonsterInst2)mod).isSuccubus()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case BECKON:
					if (((MonsterInst2)mod).isBeckon()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case STARTAGE:
					if (((MonsterInst2)mod).isStartage()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MAXAGE:
					if (((MonsterInst2)mod).isMaxage()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case OLDER:
					if (((MonsterInst2)mod).isOlder()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case HEALER:
					if (((MonsterInst2)mod).isHealer()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case STARTAFF:
					if (((MonsterInst2)mod).isStartaff()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SUPPLYBONUS:
					if (((MonsterInst2)mod).isSupplybonus()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case RESOURCES:
					if (((MonsterInst2)mod).isResources()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case UWDAMAGE:
					if (((MonsterInst2)mod).isUwdamage()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case HOMESICK:
					if (((MonsterInst2)mod).isHomesick()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case COLDPOWER:
					if (((MonsterInst2)mod).isColdpower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FIREPOWER:
					if (((MonsterInst2)mod).isFirepower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case STORMPOWER:
					if (((MonsterInst2)mod).isStormpower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DARKPOWER:
					if (((MonsterInst2)mod).isDarkpower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SPRINGPOWER:
					if (((MonsterInst2)mod).isSpringpower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SUMMERPOWER:
					if (((MonsterInst2)mod).isSummerpower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FALLPOWER:
					if (((MonsterInst2)mod).isFallpower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case WINTERPOWER:
					if (((MonsterInst2)mod).isWinterpower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case AMBIDEXTROUS:
					if (((MonsterInst2)mod).isAmbidextrous()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case BANEFIRESHIELD:
					if (((MonsterInst2)mod).isBanefireshield()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case BERSERK:
					if (((MonsterInst2)mod).isBerserk()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case STANDARD:
					if (((MonsterInst2)mod).isStandard()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ANIMALAWE:
					if (((MonsterInst2)mod).isAnimalawe()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case AWE:
					if (((MonsterInst2)mod).isAwe()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FEAR:
					if (((MonsterInst2)mod).isFear()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case REGENERATION:
					if (((MonsterInst2)mod).isRegeneration()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case REINVIGORATION:
					if (((MonsterInst2)mod).isReinvigoration()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FIRESHIELD:
					if (((MonsterInst2)mod).isFireshield()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
//				case HEAT:
//					if (((MonsterInst6)mod).isHeat()){
//						return Integer.valueOf(((MonsterInst2)mod).getValue());
//					}
//					break;
//				case COLD:
//					if (((MonsterInst6)mod).isCold()){
//						return Integer.valueOf(((MonsterInst2)mod).getValue());
//					}
//					break;
				case ICEPROT:
					if (((MonsterInst2)mod).isIceprot()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case INVULNERABLE:
					if (((MonsterInst2)mod).isInvulnerable()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case POISONCLOUD:
					if (((MonsterInst2)mod).isPoisoncloud()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DISEASECLOUD:
					if (((MonsterInst2)mod).isDiseasecloud()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case BLOODVENGEANCE:
					if (((MonsterInst2)mod).isBloodvengeance()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case CASTLEDEF:
					if (((MonsterInst2)mod).isCastledef()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SIEGEBONUS:
					if (((MonsterInst2)mod).isSiegebonus()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case PATROLBONUS:
					if (((MonsterInst2)mod).isPatrolbonus()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case PILLAGEBONUS:
					if (((MonsterInst2)mod).isPillagebonus()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MASTERRIT:
					if (((MonsterInst2)mod).isMasterrit()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case RESEARCHBONUS:
					if (((MonsterInst2)mod).isResearchbonus()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case INSPIRINGRES:
					if (((MonsterInst2)mod).isInspiringres()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FORGEBONUS:
					if (((MonsterInst2)mod).isForgebonus()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DOUSE:
					if (((MonsterInst2)mod).isDouse()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case NOBADEVENTS:
					if (((MonsterInst2)mod).isNobadevents()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case INCUNREST:
					if (((MonsterInst2)mod).isIncunrest()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SPREADDOM:
					if (((MonsterInst2)mod).isSpreaddom()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case LEPER:
					if (((MonsterInst2)mod).isLeper()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case POPKILL:
					if (((MonsterInst2)mod).isPopkill()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case HERETIC:
					if (((MonsterInst2)mod).isHeretic()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ITEMSLOTS:
					if (((MonsterInst2)mod).isItemslots()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case NAMETYPE:
					if (((MonsterInst2)mod).isNametype()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case RECLIMIT:
					if (((MonsterInst2)mod).isReclimit()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case HOMEREALM:
					if (((MonsterInst2)mod).isHomerealm()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case GIFTOFWATER:
					if (((MonsterInst2)mod).isGiftofwater()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case INDEPMOVE:
					if (((MonsterInst2)mod).isIndepmove()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case PATIENCE:
					if (((MonsterInst2)mod).isPatience()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FALSEARMY:
					if (((MonsterInst2)mod).isFalsearmy()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FOOLSCOUTS:
					if (((MonsterInst2)mod).isFoolscouts()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DESERTER:
					if (((MonsterInst2)mod).isDeserter()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case HORRORDESERTER:
					if (((MonsterInst2)mod).isHorrordeserter()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DEFECTOR:
					if (((MonsterInst2)mod).isDefector()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case AUTOHEALER:
					if (((MonsterInst2)mod).isAutohealer()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case AUTODISHEALER:
					if (((MonsterInst2)mod).isAutodishealer()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case AUTODISGRINDER:
					if (((MonsterInst2)mod).isAutodisgrinder()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case WOUNDFEND:
					if (((MonsterInst2)mod).isWoundfend()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DAMAGEREV:
					if (((MonsterInst2)mod).isDamagerev()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SLIMER:
					if (((MonsterInst2)mod).isSlimer()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DEATHDISEASE:
					if (((MonsterInst2)mod).isDeathdisease()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DEATHPARALYZE:
					if (((MonsterInst2)mod).isDeathparalyze()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DEATHFIRE:
					if (((MonsterInst2)mod).isDeathfire()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case CHAOSPOWER:
					if (((MonsterInst2)mod).isChaospower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DIGEST:
					if (((MonsterInst2)mod).isDigest()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case INCORPORATE:
					if (((MonsterInst2)mod).isIncorporate()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case INCPROVDEF:
					if (((MonsterInst2)mod).isIncprovdef()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ELEGIST:
					if (((MonsterInst2)mod).isElegist()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case GOLD:
					if (((MonsterInst2)mod).isGold()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case GROWHP:
					if (((MonsterInst2)mod).isGrowhp()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SHRINKHP:
					if (((MonsterInst2)mod).isShrinkhp()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case REANIMATOR:
					if (((MonsterInst2)mod).isReanimator()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MONTAG:
					if (((MonsterInst2)mod).isMontag()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case INSPIRATIONAL:
					if (((MonsterInst2)mod).isInspirational()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case BEASTMASTER:
					if (((MonsterInst2)mod).isBeastmaster()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TASKMASTER:
					if (((MonsterInst2)mod).isTaskmaster()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FORMATIONFIGHTER:
					if (((MonsterInst2)mod).isFormationfighter()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case BODYGUARD:
					if (((MonsterInst2)mod).isBodyguard()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DIVINEINS:
					if (((MonsterInst2)mod).isDivineins()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FIRERANGE:
					if (((MonsterInst2)mod).isFirerange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case AIRRANGE:
					if (((MonsterInst2)mod).isAirrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case WATERRANGE:
					if (((MonsterInst2)mod).isWaterrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case EARTHRANGE:
					if (((MonsterInst2)mod).isEarthrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ASTRALRANGE:
					if (((MonsterInst2)mod).isAstralrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DEATHRANGE:
					if (((MonsterInst2)mod).isDeathrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case NATURERANGE:
					if (((MonsterInst2)mod).isNaturerange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case BLOODRANGE:
					if (((MonsterInst2)mod).isBloodrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ELEMENTRANGE:
					if (((MonsterInst2)mod).isElementrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case SORCERYRANGE:
					if (((MonsterInst2)mod).isSorceryrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ALLRANGE:
					if (((MonsterInst2)mod).isAllrange()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TMPFIREGEMS:
					if (((MonsterInst2)mod).isTmpfiregems()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TMPAIRGEMS:
					if (((MonsterInst2)mod).isTmpairgems()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TMPWATERGEMS:
					if (((MonsterInst2)mod).isTmpwatergems()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TMPEARTHGEMS:
					if (((MonsterInst2)mod).isTmpearthgems()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TMPASTRALGEMS:
					if (((MonsterInst2)mod).isTmpastralgems()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TMPDEATHGEMS:
					if (((MonsterInst2)mod).isTmpdeathgems()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TMPNATUREGEMS:
					if (((MonsterInst2)mod).isTmpnaturegems()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TMPBLOODSLAVES:
					if (((MonsterInst2)mod).isTmpbloodslaves()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MAKEPEARLS:
					if (((MonsterInst2)mod).isMakepearls()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case BONUSSPELLS:
					if (((MonsterInst2)mod).isBonusspells()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case TAINTED:
					if (((MonsterInst2)mod).isTainted()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case FIXFORGEBONUS:
					if (((MonsterInst2)mod).isFixforgebonus()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MASTERSMITH:
					if (((MonsterInst2)mod).isMastersmith()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case CROSSBREEDER:
					if (((MonsterInst2)mod).isCrossbreeder()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case DEATHBANISH:
					if (((MonsterInst2)mod).isDeathbanish()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case KOKYTOSRET:
					if (((MonsterInst2)mod).isKokytosret()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case INFERNORET:
					if (((MonsterInst2)mod).isInfernoret()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case VOIDRET:
					if (((MonsterInst2)mod).isVoidret()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case ALLRET:
					if (((MonsterInst2)mod).isAllret()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case MAGICPOWER:
					if (((MonsterInst2)mod).isMagicpower()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case RANDOMSPELL:
					if (((MonsterInst2)mod).isRandomspell()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				case CHAOSREC:
					if (((MonsterInst2)mod).isChaosrec()){
						return Integer.valueOf(((MonsterInst2)mod).getValue());
					}
					break;
				}
			}
		}
		return null;
	}
	
	private Integer[] getInst3(Inst inst3, Object monster) {
		int magicSkillCount = 0;
		int customMagicCount = 0;
		int boostCount = 0;
		int gemProdCount = 0;
		EList<MonsterMods> list = ((Monster)monster).getMods();
		for (MonsterMods mod : list) {
			if (mod instanceof MonsterInst3) {
				switch (inst3) {
				case MAGICSKILL1:
					if (((MonsterInst3)mod).isMagicskill()) {
						magicSkillCount++;
						if (magicSkillCount == 1) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICSKILL2:
					if (((MonsterInst3)mod).isMagicskill()) {
						magicSkillCount++;
						if (magicSkillCount == 2) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICSKILL3:
					if (((MonsterInst3)mod).isMagicskill()) {
						magicSkillCount++;
						if (magicSkillCount == 3) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICSKILL4:
					if (((MonsterInst3)mod).isMagicskill()) {
						magicSkillCount++;
						if (magicSkillCount == 4) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICSKILL5:
					if (((MonsterInst3)mod).isMagicskill()) {
						magicSkillCount++;
						if (magicSkillCount == 5) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICSKILL6:
					if (((MonsterInst3)mod).isMagicskill()) {
						magicSkillCount++;
						if (magicSkillCount == 6) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICSKILL7:
					if (((MonsterInst3)mod).isMagicskill()) {
						magicSkillCount++;
						if (magicSkillCount == 7) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICSKILL8:
					if (((MonsterInst3)mod).isMagicskill()) {
						magicSkillCount++;
						if (magicSkillCount == 8) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case CUSTOMMAGIC1:
					if (((MonsterInst3)mod).isCustommagic()) {
						customMagicCount++;
						if (customMagicCount == 1) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case CUSTOMMAGIC2:
					if (((MonsterInst3)mod).isCustommagic()) {
						customMagicCount++;
						if (customMagicCount == 2) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case CUSTOMMAGIC3:
					if (((MonsterInst3)mod).isCustommagic()) {
						customMagicCount++;
						if (customMagicCount == 3) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case CUSTOMMAGIC4:
					if (((MonsterInst3)mod).isCustommagic()) {
						customMagicCount++;
						if (customMagicCount == 4) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case CUSTOMMAGIC5:
					if (((MonsterInst3)mod).isCustommagic()) {
						customMagicCount++;
						if (customMagicCount == 5) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case CUSTOMMAGIC6:
					if (((MonsterInst3)mod).isCustommagic()) {
						customMagicCount++;
						if (customMagicCount == 6) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case CUSTOMMAGIC7:
					if (((MonsterInst3)mod).isCustommagic()) {
						customMagicCount++;
						if (customMagicCount == 7) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case CUSTOMMAGIC8:
					if (((MonsterInst3)mod).isCustommagic()) {
						customMagicCount++;
						if (customMagicCount == 8) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICBOOST1:
					if (((MonsterInst3)mod).isMagicboost()) {
						boostCount++;
						if (boostCount == 1) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICBOOST2:
					if (((MonsterInst3)mod).isMagicboost()) {
						boostCount++;
						if (boostCount == 2) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICBOOST3:
					if (((MonsterInst3)mod).isMagicboost()) {
						boostCount++;
						if (boostCount == 3) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICBOOST4:
					if (((MonsterInst3)mod).isMagicboost()) {
						boostCount++;
						if (boostCount == 4) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICBOOST5:
					if (((MonsterInst3)mod).isMagicboost()) {
						boostCount++;
						if (boostCount == 5) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICBOOST6:
					if (((MonsterInst3)mod).isMagicboost()) {
						boostCount++;
						if (boostCount == 6) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICBOOST7:
					if (((MonsterInst3)mod).isMagicboost()) {
						boostCount++;
						if (boostCount == 7) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case MAGICBOOST8:
					if (((MonsterInst3)mod).isMagicboost()) {
						boostCount++;
						if (boostCount == 8) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case GEMPROD1:
					if (((MonsterInst3)mod).isGemprod()) {
						gemProdCount++;
						if (gemProdCount == 1) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case GEMPROD2:
					if (((MonsterInst3)mod).isGemprod()) {
						gemProdCount++;
						if (gemProdCount == 2) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case GEMPROD3:
					if (((MonsterInst3)mod).isGemprod()) {
						gemProdCount++;
						if (gemProdCount == 3) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case GEMPROD4:
					if (((MonsterInst3)mod).isGemprod()) {
						gemProdCount++;
						if (gemProdCount == 4) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case GEMPROD5:
					if (((MonsterInst3)mod).isGemprod()) {
						gemProdCount++;
						if (gemProdCount == 5) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case GEMPROD6:
					if (((MonsterInst3)mod).isGemprod()) {
						gemProdCount++;
						if (gemProdCount == 6) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case GEMPROD7:
					if (((MonsterInst3)mod).isGemprod()) {
						gemProdCount++;
						if (gemProdCount == 7) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case GEMPROD8:
					if (((MonsterInst3)mod).isGemprod()) {
						gemProdCount++;
						if (gemProdCount == 8) {
							return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
						}
					}
					break;
				case SAILING:
					if (((MonsterInst3)mod).isSailing()){
						return new Integer[]{Integer.valueOf(((MonsterInst3)mod).getValue1()), Integer.valueOf(((MonsterInst3)mod).getValue2())};
					}
					break;
				}
			}
		}
		return null;
	}
	
	private Boolean getInst4(Inst inst4, Object monster) {
		EList<MonsterMods> list = ((Monster)monster).getMods();
		for (MonsterMods mod : list) {
			if (mod instanceof MonsterInst4) {
				switch (inst4) {
				case CLEAR:
					if (((MonsterInst4)mod).isClear()){
						return Boolean.TRUE;
					}
					break;
				case CLEARMAGIC:
					if (((MonsterInst4)mod).isClearmagic()){
						return Boolean.TRUE;
					}
					break;
				case CLEARWEAPONS:
					if (((MonsterInst4)mod).isClearweapons()){
						return Boolean.TRUE;
					}
					break;
				case CLEARARMOR:
					if (((MonsterInst4)mod).isCleararmor()){
						return Boolean.TRUE;
					}
					break;
				case CLEARSPEC:
					if (((MonsterInst4)mod).isClearspec()){
						return Boolean.TRUE;
					}
					break;
				case FEMALE:
					if (((MonsterInst4)mod).isFemale()){
						return Boolean.TRUE;
					}
					break;
				case MOUNTED:
					if (((MonsterInst4)mod).isMounted()){
						return Boolean.TRUE;
					}
					break;
				case HOLY:
					if (((MonsterInst4)mod).isHoly()){
						return Boolean.TRUE;
					}
					break;
				case ANIMAL:
					if (((MonsterInst4)mod).isAnimal()){
						return Boolean.TRUE;
					}
					break;
				case UNDEAD:
					if (((MonsterInst4)mod).isUndead()){
						return Boolean.TRUE;
					}
					break;
				case DEMON:
					if (((MonsterInst4)mod).isDemon()){
						return Boolean.TRUE;
					}
					break;
				case MAGICBEING:
					if (((MonsterInst4)mod).isMagicbeing()){
						return Boolean.TRUE;
					}
					break;
				case STONEBEING:
					if (((MonsterInst4)mod).isStonebeing()){
						return Boolean.TRUE;
					}
					break;
				case INANIMATE:
					if (((MonsterInst4)mod).isInanimate()){
						return Boolean.TRUE;
					}
					break;
				case COLDBLOOD:
					if (((MonsterInst4)mod).isColdblood()){
						return Boolean.TRUE;
					}
					break;
				case IMMORTAL:
					if (((MonsterInst4)mod).isImmortal()){
						return Boolean.TRUE;
					}
					break;
				case BLIND:
					if (((MonsterInst4)mod).isBlind()){
						return Boolean.TRUE;
					}
					break;
				case UNIQUE:
					if (((MonsterInst4)mod).isUnique()){
						return Boolean.TRUE;
					}
					break;
				case IMMOBILE:
					if (((MonsterInst4)mod).isImmobile()){
						return Boolean.TRUE;
					}
					break;
				case AQUATIC:
					if (((MonsterInst4)mod).isAquatic()){
						return Boolean.TRUE;
					}
					break;
				case AMPHIBIAN:
					if (((MonsterInst4)mod).isAmphibian()){
						return Boolean.TRUE;
					}
					break;
				case POORAMPHIBIAN:
					if (((MonsterInst4)mod).isPooramphibian()){
						return Boolean.TRUE;
					}
					break;
				case FLYING:
					if (((MonsterInst4)mod).isFlying()){
						return Boolean.TRUE;
					}
					break;
				case STORMIMMUNE:
					if (((MonsterInst4)mod).isStormimmune()){
						return Boolean.TRUE;
					}
					break;
				case FORESTSURVIVAL:
					if (((MonsterInst4)mod).isForestsurvival()){
						return Boolean.TRUE;
					}
					break;
				case MOUNTAINSURVIVAL:
					if (((MonsterInst4)mod).isMountainsurvival()){
						return Boolean.TRUE;
					}
					break;
				case SWAMPSURVIVAL:
					if (((MonsterInst4)mod).isSwampsurvival()){
						return Boolean.TRUE;
					}
					break;
				case WASTESURVIVAL:
					if (((MonsterInst4)mod).isWastesurvival()){
						return Boolean.TRUE;
					}
					break;
				case ILLUSION:
					if (((MonsterInst4)mod).isIllusion()){
						return Boolean.TRUE;
					}
					break;
				case SPY:
					if (((MonsterInst4)mod).isSpy()){
						return Boolean.TRUE;
					}
					break;
				case ASSASSIN:
					if (((MonsterInst4)mod).isAssassin()){
						return Boolean.TRUE;
					}
					break;
				case HEAL:
					if (((MonsterInst4)mod).isHeal()){
						return Boolean.TRUE;
					}
					break;
				case NOHEAL:
					if (((MonsterInst4)mod).isNoheal()){
						return Boolean.TRUE;
					}
					break;
				case NEEDNOTEAT:
					if (((MonsterInst4)mod).isNeednoteat()){
						return Boolean.TRUE;
					}
					break;
				case ETHEREAL:
					if (((MonsterInst4)mod).isEthereal()){
						return Boolean.TRUE;
					}
					break;
				case TRAMPLE:
					if (((MonsterInst4)mod).isTrample()){
						return Boolean.TRUE;
					}
					break;
				case ENTANGLE:
					if (((MonsterInst4)mod).isEntangle()){
						return Boolean.TRUE;
					}
					break;
				case EYELOSS:
					if (((MonsterInst4)mod).isEyeloss()){
						return Boolean.TRUE;
					}
					break;
				case HORRORMARK:
					if (((MonsterInst4)mod).isHorrormark()){
						return Boolean.TRUE;
					}
					break;
				case POISONARMOR:
					if (((MonsterInst4)mod).isPoisonarmor()){
						return Boolean.TRUE;
					}
					break;
				case INQUISITOR:
					if (((MonsterInst4)mod).isInquisitor()){
						return Boolean.TRUE;
					}
					break;
				case NOITEM:
					if (((MonsterInst4)mod).isNoitem()){
						return Boolean.TRUE;
					}
					break;
				case DRAINIMMUNE:
					if (((MonsterInst4)mod).isDrainimmune()){
						return Boolean.TRUE;
					}
					break;
				case NOLEADER:
					if (((MonsterInst4)mod).isNoleader()){
						return Boolean.TRUE;
					}
					break;
				case POORLEADER:
					if (((MonsterInst4)mod).isPoorleader()){
						return Boolean.TRUE;
					}
					break;
				case OKLEADER:
					if (((MonsterInst4)mod).isOkleader()){
						return Boolean.TRUE;
					}
					break;
				case GOODLEADER:
					if (((MonsterInst4)mod).isGoodleader()){
						return Boolean.TRUE;
					}
					break;
				case EXPERTLEADER:
					if (((MonsterInst4)mod).isExpertleader()){
						return Boolean.TRUE;
					}
					break;
				case SUPERIORLEADER:
					if (((MonsterInst4)mod).isSuperiorleader()){
						return Boolean.TRUE;
					}
					break;
				case NOMAGICLEADER:
					if (((MonsterInst4)mod).isNomagicleader()){
						return Boolean.TRUE;
					}
					break;
				case POORMAGICLEADER:
					if (((MonsterInst4)mod).isPoormagicleader()){
						return Boolean.TRUE;
					}
					break;
				case OKMAGICLEADER:
					if (((MonsterInst4)mod).isOkmagicleader()){
						return Boolean.TRUE;
					}
					break;
				case GOODMAGICLEADER:
					if (((MonsterInst4)mod).isGoodmagicleader()){
						return Boolean.TRUE;
					}
					break;
				case EXPERTMAGICLEADER:
					if (((MonsterInst4)mod).isExpertmagicleader()){
						return Boolean.TRUE;
					}
					break;
				case SUPERIORMAGICLEADER:
					if (((MonsterInst4)mod).isSuperiormagicleader()){
						return Boolean.TRUE;
					}
					break;
				case NOUNDEADLEADER:
					if (((MonsterInst4)mod).isNoundeadleader()){
						return Boolean.TRUE;
					}
					break;
				case POORUNDEADLEADER:
					if (((MonsterInst4)mod).isPoorundeadleader()){
						return Boolean.TRUE;
					}
					break;
				case OKUNDEADLEADER:
					if (((MonsterInst4)mod).isOkundeadleader()){
						return Boolean.TRUE;
					}
					break;
				case GOODUNDEADLEADER:
					if (((MonsterInst4)mod).isGoodundeadleader()){
						return Boolean.TRUE;
					}
					break;
				case EXPERTUNDEADLEADER:
					if (((MonsterInst4)mod).isExpertundeadleader()){
						return Boolean.TRUE;
					}
					break;
				case SUPERIORUNDEADLEADER:
					if (((MonsterInst4)mod).isSuperiorundeadleader()){
						return Boolean.TRUE;
					}
					break;
				case SLOWREC:
					if (((MonsterInst4)mod).isSlowrec()){
						return Boolean.TRUE;
					}
					break;
				case NOSLOWREC:
					if (((MonsterInst4)mod).isNoslowrec()){
						return Boolean.TRUE;
					}
					break;
				case REQLAB:
					if (((MonsterInst4)mod).isReqlab()){
						return Boolean.TRUE;
					}
					break;
				case REQTEMPLE:
					if (((MonsterInst4)mod).isReqtemple()){
						return Boolean.TRUE;
					}
					break;
				case SINGLEBATTLE:
					if (((MonsterInst4)mod).isSinglebattle()){
						return Boolean.TRUE;
					}
					break;
				case AISINGLEREC:
					if (((MonsterInst4)mod).isAisinglerec()){
						return Boolean.TRUE;
					}
					break;
				case AINOREC:
					if (((MonsterInst4)mod).isAinorec()){
						return Boolean.TRUE;
					}
					break;
				case LESSERHORROR:
					if (((MonsterInst4)mod).isLesserhorror()){
						return Boolean.TRUE;
					}
					break;
				case GREATERHORROR:
					if (((MonsterInst4)mod).isGreaterhorror()){
						return Boolean.TRUE;
					}
					break;
				case DOOMHORROR:
					if (((MonsterInst4)mod).isDoomhorror()){
						return Boolean.TRUE;
					}
					break;
				case BUG:
					if (((MonsterInst4)mod).isBug()){
						return Boolean.TRUE;
					}
					break;
				case UWBUG:
					if (((MonsterInst4)mod).isUwbug()){
						return Boolean.TRUE;
					}
					break;
				case AUTOCOMPETE:
					if (((MonsterInst4)mod).isAutocompete()){
						return Boolean.TRUE;
					}
					break;
				case FLOAT:
					if (((MonsterInst4)mod).isFloat()){
						return Boolean.TRUE;
					}
					break;
				case TELEPORT:
					if (((MonsterInst4)mod).isTeleport()){
						return Boolean.TRUE;
					}
					break;
				case NORIVERPASS:
					if (((MonsterInst4)mod).isNoriverpass()){
						return Boolean.TRUE;
					}
					break;
				case UNTELEPORTABLE:
					if (((MonsterInst4)mod).isUnteleportable()){
						return Boolean.TRUE;
					}
					break;
				case HPOVERFLOW:
					if (((MonsterInst4)mod).isHpoverflow()){
						return Boolean.TRUE;
					}
					break;
				case PIERCERES:
					if (((MonsterInst4)mod).isPierceres()){
						return Boolean.TRUE;
					}
					break;
				case SLASHRES:
					if (((MonsterInst4)mod).isSlashres()){
						return Boolean.TRUE;
					}
					break;
				case BLUNTRES:
					if (((MonsterInst4)mod).isBluntres()){
						return Boolean.TRUE;
					}
					break;
				case DEATHCURSE:
					if (((MonsterInst4)mod).isDeathcurse()){
						return Boolean.TRUE;
					}
					break;
				case TRAMPSWALLOW:
					if (((MonsterInst4)mod).isTrampswallow()){
						return Boolean.TRUE;
					}
					break;
				case TAXCOLLECTOR:
					if (((MonsterInst4)mod).isTaxcollector()){
						return Boolean.TRUE;
					}
					break;
				case NOHOF:
					if (((MonsterInst4)mod).isNohof()){
						return Boolean.TRUE;
					}
					break;
				case CLEANSHAPE:
					if (((MonsterInst4)mod).isCleanshape()){
						return Boolean.TRUE;
					}
					break;
				case SLAVE:
					if (((MonsterInst4)mod).isSlave()){
						return Boolean.TRUE;
					}
					break;
				case UNDISCIPLINED:
					if (((MonsterInst4)mod).isUndisciplined()){
						return Boolean.TRUE;
					}
					break;
				case MAGICIMMUNE:
					if (((MonsterInst4)mod).isMagicimmune()){
						return Boolean.TRUE;
					}
					break;
				case COMSLAVE:
					if (((MonsterInst4)mod).isComslave()){
						return Boolean.TRUE;
					}
					break;
				}
			}
		}
		return Boolean.FALSE;
	}
	
	private Object getInst5(Inst inst2, Object monster) {
		EList<MonsterMods> list = ((Monster)monster).getMods();
		int weaponCount = 0;
		int armorCount = 0;
		for (MonsterMods mod : list) {
			if (mod instanceof MonsterInst5) {
				switch (inst2) {
				case WEAPON1:
					if (((MonsterInst5)mod).isWeapon()){
						weaponCount ++;
						if (weaponCount == 1) {
							String strVal = ((MonsterInst5)mod).getValue1();
							Integer intVal = ((MonsterInst5)mod).getValue2();
							if (strVal != null) {
								return strVal;
							}
							return intVal;
						}
					}
					break;
				case WEAPON2:
					if (((MonsterInst5)mod).isWeapon()){
						weaponCount ++;
						if (weaponCount == 2) {
							String strVal = ((MonsterInst5)mod).getValue1();
							Integer intVal = ((MonsterInst5)mod).getValue2();
							if (strVal != null) {
								return strVal;
							}
							return intVal;
						}
					}
					break;
				case WEAPON3:
					if (((MonsterInst5)mod).isWeapon()){
						weaponCount ++;
						if (weaponCount == 3) {
							String strVal = ((MonsterInst5)mod).getValue1();
							Integer intVal = ((MonsterInst5)mod).getValue2();
							if (strVal != null) {
								return strVal;
							}
							return intVal;
						}
					}
					break;
				case WEAPON4:
					if (((MonsterInst5)mod).isWeapon()){
						weaponCount ++;
						if (weaponCount == 4) {
							String strVal = ((MonsterInst5)mod).getValue1();
							Integer intVal = ((MonsterInst5)mod).getValue2();
							if (strVal != null) {
								return strVal;
							}
							return intVal;
						}
					}
					break;
				case ARMOR1:
					if (((MonsterInst5)mod).isArmor()){
						armorCount ++;
						if (armorCount == 1) {
							String strVal = ((MonsterInst5)mod).getValue1();
							Integer intVal = ((MonsterInst5)mod).getValue2();
							if (strVal != null) {
								return strVal;
							}
							return intVal;
						}
					}
					break;
				case ARMOR2:
					if (((MonsterInst5)mod).isArmor()){
						armorCount ++;
						if (armorCount == 2) {
							String strVal = ((MonsterInst5)mod).getValue1();
							Integer intVal = ((MonsterInst5)mod).getValue2();
							if (strVal != null) {
								return strVal;
							}
							return intVal;
						}
					}
					break;
				case ARMOR3:
					if (((MonsterInst5)mod).isArmor()){
						armorCount ++;
						if (armorCount == 3) {
							String strVal = ((MonsterInst5)mod).getValue1();
							Integer intVal = ((MonsterInst5)mod).getValue2();
							if (strVal != null) {
								return strVal;
							}
							return intVal;
						}
					}
					break;
				case ONEBATTLESPELL:
					if (((MonsterInst5)mod).isOnebattlespell()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case FIRSTSHAPE:
					if (((MonsterInst5)mod).isFirstshape()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case SECONDSHAPE:
					if (((MonsterInst5)mod).isSecondshape()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case SECONDTMPSHAPE:
					if (((MonsterInst5)mod).isSecondtmpshape()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case SHAPECHANGE:
					if (((MonsterInst5)mod).isShapechange()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case LANDSHAPE:
					if (((MonsterInst5)mod).isLandshape()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case WATERSHAPE:
					if (((MonsterInst5)mod).isWatershape()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case FORESTSHAPE:
					if (((MonsterInst5)mod).isForestshape()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case PLAINSHAPE:
					if (((MonsterInst5)mod).isPlainshape()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case DOMSUMMON:
					if (((MonsterInst5)mod).isDomsummon()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case DOMSUMMON2:
					if (((MonsterInst5)mod).isDomsummon2()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case DOMSUMMON20:
					if (((MonsterInst5)mod).isDomsummon20()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case MAKEMONSTERS1:
					if (((MonsterInst5)mod).isMakemonsters1()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case MAKEMONSTERS2:
					if (((MonsterInst5)mod).isMakemonsters2()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case MAKEMONSTERS3:
					if (((MonsterInst5)mod).isMakemonsters3()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case MAKEMONSTERS4:
					if (((MonsterInst5)mod).isMakemonsters4()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case MAKEMONSTERS5:
					if (((MonsterInst5)mod).isMakemonsters5()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case SUMMON1:
					if (((MonsterInst5)mod).isSummon1()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case SUMMON2:
					if (((MonsterInst5)mod).isSummon2()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case SUMMON3:
					if (((MonsterInst5)mod).isSummon3()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case SUMMON4:
					if (((MonsterInst5)mod).isSummon4()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case SUMMON5:
					if (((MonsterInst5)mod).isSummon5()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case RAREDOMSUMMON:
					if (((MonsterInst5)mod).isRaredomsummon()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATTLESUM1:
					if (((MonsterInst5)mod).isBattlesum1()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATTLESUM2:
					if (((MonsterInst5)mod).isBattlesum2()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATTLESUM3:
					if (((MonsterInst5)mod).isBattlesum3()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATTLESUM4:
					if (((MonsterInst5)mod).isBattlesum4()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATTLESUM5:
					if (((MonsterInst5)mod).isBattlesum5()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM1:
					if (((MonsterInst5)mod).isBatstartsum1()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM2:
					if (((MonsterInst5)mod).isBatstartsum2()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM3:
					if (((MonsterInst5)mod).isBatstartsum3()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM4:
					if (((MonsterInst5)mod).isBatstartsum4()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM5:
					if (((MonsterInst5)mod).isBatstartsum5()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM1D6:
					if (((MonsterInst5)mod).isBatstartsum1d6()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM2D6:
					if (((MonsterInst5)mod).isBatstartsum2d6()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM3D6:
					if (((MonsterInst5)mod).isBatstartsum3d6()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM4D6:
					if (((MonsterInst5)mod).isBatstartsum4d6()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				case BATSTARTSUM5D6:
					if (((MonsterInst5)mod).isBatstartsum5d6()){
						String strVal = ((MonsterInst5)mod).getValue1();
						Integer intVal = ((MonsterInst5)mod).getValue2();
						if (strVal != null) {
							return strVal;
						}
						return intVal;
					}
					break;
				}
			}
		}
		return null;
	}
	
	private Integer getInst6(Inst inst2, Object monster) {
		EList<MonsterMods> list = ((Monster)monster).getMods();
		for (MonsterMods mod : list) {
			if (mod instanceof MonsterInst6) {
				switch (inst2) {
				case HEAT:
					if (((MonsterInst6)mod).isHeat()){
						return Integer.valueOf(((MonsterInst6)mod).getValue());
					}
					break;
				case COLD:
					if (((MonsterInst6)mod).isCold()){
						return Integer.valueOf(((MonsterInst6)mod).getValue());
					}
					break;
				case STEALTHY:
					if (((MonsterInst6)mod).isStealthy()){
						return Integer.valueOf(((MonsterInst6)mod).getValue());
					}
					break;
				}
			}
		}
		return null;
	}
	
	private void setInst1(final Inst inst2, final XtextEditor editor, final String newName) 
	{
		final IXtextDocument myDocument = editor.getDocument();
		myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				Monster monsterToEdit = (Monster)input;
				EList<MonsterMods> mods = monsterToEdit.getMods();				
				for (MonsterMods mod : mods) {
					if (mod instanceof MonsterInst1) {
						switch (inst2) {
						case SPR1:
							if (((MonsterInst1)mod).isSpr1()) {
								((MonsterInst1)mod).setValue(newName);
							}
							break;
						case SPR2:
							if (((MonsterInst1)mod).isSpr2()) {
								((MonsterInst1)mod).setValue(newName);
							}
							break;
						case DESCR:
							if (((MonsterInst1)mod).isDescr()) {
								((MonsterInst1)mod).setValue(newName);
							}
							break;
						case FIXEDNAME:
							if (((MonsterInst1)mod).isFixedname()) {
								((MonsterInst1)mod).setValue(newName);
							}
							break;
						}
					}
				}

			}  
		});

		updateSelection();
	}

	private void setInst2(final Inst inst2, final XtextEditor editor, final String newName) 
	{
		try {
			// If this is not an int, return
			Integer.parseInt(newName);
		} catch (NumberFormatException e) {
			return;
		}
		final IXtextDocument myDocument = editor.getDocument();
		myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				Monster monsterToEdit = (Monster)input;
				EList<MonsterMods> mods = monsterToEdit.getMods();
				for (MonsterMods mod : mods) {
					if (mod instanceof MonsterInst2) {
						switch (inst2) {
						case SPECIALLOOK:
							if (((MonsterInst2)mod).isSpeciallook()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case AP:
							if (((MonsterInst2)mod).isAp()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MAPMOVE:
							if (((MonsterInst2)mod).isMapmove()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case HP:
							if (((MonsterInst2)mod).isHp()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case PROT:
							if (((MonsterInst2)mod).isProt()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SIZE:
							if (((MonsterInst2)mod).isSize()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case RESSIZE:
							if (((MonsterInst2)mod).isRessize()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case STR:
							if (((MonsterInst2)mod).isStr()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ENC:
							if (((MonsterInst2)mod).isEnc()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ATT:
							if (((MonsterInst2)mod).isAtt()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DEF:
							if (((MonsterInst2)mod).isDef()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case PREC:
							if (((MonsterInst2)mod).isPrec()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MR:
							if (((MonsterInst2)mod).isMr()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MOR:
							if (((MonsterInst2)mod).isMor()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case GCOST:
							if (((MonsterInst2)mod).isGcost()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case RCOST:
							if (((MonsterInst2)mod).isRcost()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case PATHCOST:
							if (((MonsterInst2)mod).isPathcost()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case STARTDOM:
							if (((MonsterInst2)mod).isStartdom()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case EYES:
							if (((MonsterInst2)mod).isEyes()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case VOIDSANITY:
							if (((MonsterInst2)mod).isVoidsanity()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case COPYSTATS:
							if (((MonsterInst2)mod).isCopystats()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case COPYSPR:
							if (((MonsterInst2)mod).isCopyspr()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SHATTEREDSOUL:
							if (((MonsterInst2)mod).isShatteredsoul()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case COLDRES:
							if (((MonsterInst2)mod).isColdres()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FIRERES:
							if (((MonsterInst2)mod).isFireres()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case POISONRES:
							if (((MonsterInst2)mod).isPoisonres()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SHOCKRES:
							if (((MonsterInst2)mod).isShockres()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DARKVISION:
							if (((MonsterInst2)mod).isDarkvision()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SEDUCE:
							if (((MonsterInst2)mod).isSeduce()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SUCCUBUS:
							if (((MonsterInst2)mod).isSuccubus()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case BECKON:
							if (((MonsterInst2)mod).isBeckon()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case STARTAGE:
							if (((MonsterInst2)mod).isStartage()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MAXAGE:
							if (((MonsterInst2)mod).isMaxage()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case OLDER:
							if (((MonsterInst2)mod).isOlder()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case HEALER:
							if (((MonsterInst2)mod).isHealer()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case STARTAFF:
							if (((MonsterInst2)mod).isStartaff()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SUPPLYBONUS:
							if (((MonsterInst2)mod).isSupplybonus()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case RESOURCES:
							if (((MonsterInst2)mod).isResources()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case UWDAMAGE:
							if (((MonsterInst2)mod).isUwdamage()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case HOMESICK:
							if (((MonsterInst2)mod).isHomesick()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case COLDPOWER:
							if (((MonsterInst2)mod).isColdpower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FIREPOWER:
							if (((MonsterInst2)mod).isFirepower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case STORMPOWER:
							if (((MonsterInst2)mod).isStormpower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DARKPOWER:
							if (((MonsterInst2)mod).isDarkpower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SPRINGPOWER:
							if (((MonsterInst2)mod).isSpringpower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SUMMERPOWER:
							if (((MonsterInst2)mod).isSummerpower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FALLPOWER:
							if (((MonsterInst2)mod).isFallpower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case WINTERPOWER:
							if (((MonsterInst2)mod).isWinterpower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case AMBIDEXTROUS:
							if (((MonsterInst2)mod).isAmbidextrous()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case BANEFIRESHIELD:
							if (((MonsterInst2)mod).isBanefireshield()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case BERSERK:
							if (((MonsterInst2)mod).isBerserk()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case STANDARD:
							if (((MonsterInst2)mod).isStandard()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ANIMALAWE:
							if (((MonsterInst2)mod).isAnimalawe()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case AWE:
							if (((MonsterInst2)mod).isAwe()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FEAR:
							if (((MonsterInst2)mod).isFear()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case REGENERATION:
							if (((MonsterInst2)mod).isRegeneration()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case REINVIGORATION:
							if (((MonsterInst2)mod).isReinvigoration()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FIRESHIELD:
							if (((MonsterInst2)mod).isFireshield()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ICEPROT:
							if (((MonsterInst2)mod).isIceprot()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case INVULNERABLE:
							if (((MonsterInst2)mod).isInvulnerable()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case POISONCLOUD:
							if (((MonsterInst2)mod).isPoisoncloud()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DISEASECLOUD:
							if (((MonsterInst2)mod).isDiseasecloud()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case BLOODVENGEANCE:
							if (((MonsterInst2)mod).isBloodvengeance()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case CASTLEDEF:
							if (((MonsterInst2)mod).isCastledef()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SIEGEBONUS:
							if (((MonsterInst2)mod).isSiegebonus()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case PATROLBONUS:
							if (((MonsterInst2)mod).isPatrolbonus()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case PILLAGEBONUS:
							if (((MonsterInst2)mod).isPillagebonus()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MASTERRIT:
							if (((MonsterInst2)mod).isMasterrit()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case RESEARCHBONUS:
							if (((MonsterInst2)mod).isResearchbonus()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case INSPIRINGRES:
							if (((MonsterInst2)mod).isInspiringres()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FORGEBONUS:
							if (((MonsterInst2)mod).isForgebonus()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DOUSE:
							if (((MonsterInst2)mod).isDouse()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case NOBADEVENTS:
							if (((MonsterInst2)mod).isNobadevents()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case INCUNREST:
							if (((MonsterInst2)mod).isIncunrest()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SPREADDOM:
							if (((MonsterInst2)mod).isSpreaddom()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case LEPER:
							if (((MonsterInst2)mod).isLeper()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case POPKILL:
							if (((MonsterInst2)mod).isPopkill()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case HERETIC:
							if (((MonsterInst2)mod).isHeretic()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ITEMSLOTS:
							if (((MonsterInst2)mod).isItemslots()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case NAMETYPE:
							if (((MonsterInst2)mod).isNametype()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case RECLIMIT:
							if (((MonsterInst2)mod).isReclimit()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case HOMEREALM:
							if (((MonsterInst2)mod).isHomerealm()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case GIFTOFWATER:
							if (((MonsterInst2)mod).isGiftofwater()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case INDEPMOVE:
							if (((MonsterInst2)mod).isIndepmove()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case PATIENCE:
							if (((MonsterInst2)mod).isPatience()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FALSEARMY:
							if (((MonsterInst2)mod).isFalsearmy()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FOOLSCOUTS:
							if (((MonsterInst2)mod).isFoolscouts()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DESERTER:
							if (((MonsterInst2)mod).isDeserter()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case HORRORDESERTER:
							if (((MonsterInst2)mod).isHorrordeserter()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DEFECTOR:
							if (((MonsterInst2)mod).isDefector()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case AUTOHEALER:
							if (((MonsterInst2)mod).isAutohealer()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case AUTODISHEALER:
							if (((MonsterInst2)mod).isAutodishealer()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case AUTODISGRINDER:
							if (((MonsterInst2)mod).isAutodisgrinder()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case WOUNDFEND:
							if (((MonsterInst2)mod).isWoundfend()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DAMAGEREV:
							if (((MonsterInst2)mod).isDamagerev()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SLIMER:
							if (((MonsterInst2)mod).isSlimer()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DEATHDISEASE:
							if (((MonsterInst2)mod).isDeathdisease()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DEATHPARALYZE:
							if (((MonsterInst2)mod).isDeathparalyze()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DEATHFIRE:
							if (((MonsterInst2)mod).isDeathfire()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case CHAOSPOWER:
							if (((MonsterInst2)mod).isChaospower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DIGEST:
							if (((MonsterInst2)mod).isDigest()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case INCORPORATE:
							if (((MonsterInst2)mod).isIncorporate()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case INCPROVDEF:
							if (((MonsterInst2)mod).isIncprovdef()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ELEGIST:
							if (((MonsterInst2)mod).isElegist()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case GOLD:
							if (((MonsterInst2)mod).isGold()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case GROWHP:
							if (((MonsterInst2)mod).isGrowhp()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SHRINKHP:
							if (((MonsterInst2)mod).isShrinkhp()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case REANIMATOR:
							if (((MonsterInst2)mod).isReanimator()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MONTAG:
							if (((MonsterInst2)mod).isMontag()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case INSPIRATIONAL:
							if (((MonsterInst2)mod).isInspirational()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case BEASTMASTER:
							if (((MonsterInst2)mod).isBeastmaster()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TASKMASTER:
							if (((MonsterInst2)mod).isTaskmaster()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FORMATIONFIGHTER:
							if (((MonsterInst2)mod).isFormationfighter()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case BODYGUARD:
							if (((MonsterInst2)mod).isBodyguard()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DIVINEINS:
							if (((MonsterInst2)mod).isDivineins()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FIRERANGE:
							if (((MonsterInst2)mod).isFirerange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case AIRRANGE:
							if (((MonsterInst2)mod).isAirrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case WATERRANGE:
							if (((MonsterInst2)mod).isWaterrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case EARTHRANGE:
							if (((MonsterInst2)mod).isEarthrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ASTRALRANGE:
							if (((MonsterInst2)mod).isAstralrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DEATHRANGE:
							if (((MonsterInst2)mod).isDeathrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case NATURERANGE:
							if (((MonsterInst2)mod).isNaturerange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case BLOODRANGE:
							if (((MonsterInst2)mod).isBloodrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ELEMENTRANGE:
							if (((MonsterInst2)mod).isElementrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case SORCERYRANGE:
							if (((MonsterInst2)mod).isSorceryrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ALLRANGE:
							if (((MonsterInst2)mod).isAllrange()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TMPFIREGEMS:
							if (((MonsterInst2)mod).isTmpfiregems()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TMPAIRGEMS:
							if (((MonsterInst2)mod).isTmpairgems()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TMPWATERGEMS:
							if (((MonsterInst2)mod).isTmpwatergems()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TMPEARTHGEMS:
							if (((MonsterInst2)mod).isTmpearthgems()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TMPASTRALGEMS:
							if (((MonsterInst2)mod).isTmpastralgems()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TMPDEATHGEMS:
							if (((MonsterInst2)mod).isTmpdeathgems()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TMPNATUREGEMS:
							if (((MonsterInst2)mod).isTmpnaturegems()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TMPBLOODSLAVES:
							if (((MonsterInst2)mod).isTmpbloodslaves()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MAKEPEARLS:
							if (((MonsterInst2)mod).isMakepearls()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case BONUSSPELLS:
							if (((MonsterInst2)mod).isBonusspells()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case TAINTED:
							if (((MonsterInst2)mod).isTainted()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case FIXFORGEBONUS:
							if (((MonsterInst2)mod).isFixforgebonus()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MASTERSMITH:
							if (((MonsterInst2)mod).isMastersmith()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case CROSSBREEDER:
							if (((MonsterInst2)mod).isCrossbreeder()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case DEATHBANISH:
							if (((MonsterInst2)mod).isDeathbanish()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case KOKYTOSRET:
							if (((MonsterInst2)mod).isKokytosret()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case INFERNORET:
							if (((MonsterInst2)mod).isInfernoret()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case VOIDRET:
							if (((MonsterInst2)mod).isVoidret()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case ALLRET:
							if (((MonsterInst2)mod).isAllret()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case MAGICPOWER:
							if (((MonsterInst2)mod).isMagicpower()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						case RANDOMSPELL:
							if (((MonsterInst2)mod).isRandomspell()){
								((MonsterInst2)mod).setValue(Integer.parseInt(newName));
							}
							break;
						}
					}
				}

			}  
		});

		updateSelection();
	}

	private void setInst3(final Inst inst3, final XtextEditor editor, final String value1, final String value2) 
	{
		try {
			// If this is not an int, return
			if (value1 != null) {
				Integer.parseInt(value1);
			}
			if (value2 != null) {
				Integer.parseInt(value2);
			}
		} catch (NumberFormatException e) {
			return;
		}
		final IXtextDocument myDocument = editor.getDocument();
		myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				int magicSkillCount = 0;
				int customMagicCount = 0;
				int boostCount = 0;
				int gemProdCount = 0;
				Monster monsterToEdit = (Monster)input;
				EList<MonsterMods> mods = monsterToEdit.getMods();
				for (MonsterMods mod : mods) {
					if (mod instanceof MonsterInst3) {
						switch (inst3) {
						case MAGICSKILL1:
							if (((MonsterInst3)mod).isMagicskill()) {
								magicSkillCount++;
								if (magicSkillCount == 1) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICSKILL2:
							if (((MonsterInst3)mod).isMagicskill()) {
								magicSkillCount++;
								if (magicSkillCount == 2) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICSKILL3:
							if (((MonsterInst3)mod).isMagicskill()) {
								magicSkillCount++;
								if (magicSkillCount == 3) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICSKILL4:
							if (((MonsterInst3)mod).isMagicskill()) {
								magicSkillCount++;
								if (magicSkillCount == 4) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICSKILL5:
							if (((MonsterInst3)mod).isMagicskill()) {
								magicSkillCount++;
								if (magicSkillCount == 5) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICSKILL6:
							if (((MonsterInst3)mod).isMagicskill()) {
								magicSkillCount++;
								if (magicSkillCount == 6) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICSKILL7:
							if (((MonsterInst3)mod).isMagicskill()) {
								magicSkillCount++;
								if (magicSkillCount == 7) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICSKILL8:
							if (((MonsterInst3)mod).isMagicskill()) {
								magicSkillCount++;
								if (magicSkillCount == 8) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case CUSTOMMAGIC1:
							if (((MonsterInst3)mod).isCustommagic()) {
								customMagicCount++;
								if (customMagicCount == 1) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case CUSTOMMAGIC2:
							if (((MonsterInst3)mod).isCustommagic()) {
								customMagicCount++;
								if (customMagicCount == 2) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case CUSTOMMAGIC3:
							if (((MonsterInst3)mod).isCustommagic()) {
								customMagicCount++;
								if (customMagicCount == 3) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case CUSTOMMAGIC4:
							if (((MonsterInst3)mod).isCustommagic()) {
								customMagicCount++;
								if (customMagicCount == 4) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case CUSTOMMAGIC5:
							if (((MonsterInst3)mod).isCustommagic()) {
								customMagicCount++;
								if (customMagicCount == 5) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case CUSTOMMAGIC6:
							if (((MonsterInst3)mod).isCustommagic()) {
								customMagicCount++;
								if (customMagicCount == 6) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case CUSTOMMAGIC7:
							if (((MonsterInst3)mod).isCustommagic()) {
								customMagicCount++;
								if (customMagicCount == 7) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case CUSTOMMAGIC8:
							if (((MonsterInst3)mod).isCustommagic()) {
								customMagicCount++;
								if (customMagicCount == 8) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICBOOST1:
							if (((MonsterInst3)mod).isMagicboost()) {
								boostCount++;
								if (boostCount == 1) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICBOOST2:
							if (((MonsterInst3)mod).isMagicboost()) {
								boostCount++;
								if (boostCount == 2) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICBOOST3:
							if (((MonsterInst3)mod).isMagicboost()) {
								boostCount++;
								if (boostCount == 3) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICBOOST4:
							if (((MonsterInst3)mod).isMagicboost()) {
								boostCount++;
								if (boostCount == 4) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICBOOST5:
							if (((MonsterInst3)mod).isMagicboost()) {
								boostCount++;
								if (boostCount == 5) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICBOOST6:
							if (((MonsterInst3)mod).isMagicboost()) {
								boostCount++;
								if (boostCount == 6) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICBOOST7:
							if (((MonsterInst3)mod).isMagicboost()) {
								boostCount++;
								if (boostCount == 7) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case MAGICBOOST8:
							if (((MonsterInst3)mod).isMagicboost()) {
								boostCount++;
								if (boostCount == 8) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case GEMPROD1:
							if (((MonsterInst3)mod).isGemprod()) {
								gemProdCount++;
								if (gemProdCount == 1) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case GEMPROD2:
							if (((MonsterInst3)mod).isGemprod()) {
								gemProdCount++;
								if (gemProdCount == 2) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case GEMPROD3:
							if (((MonsterInst3)mod).isGemprod()) {
								gemProdCount++;
								if (gemProdCount == 3) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case GEMPROD4:
							if (((MonsterInst3)mod).isGemprod()) {
								gemProdCount++;
								if (gemProdCount == 4) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case GEMPROD5:
							if (((MonsterInst3)mod).isGemprod()) {
								gemProdCount++;
								if (gemProdCount == 5) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case GEMPROD6:
							if (((MonsterInst3)mod).isGemprod()) {
								gemProdCount++;
								if (gemProdCount == 6) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case GEMPROD7:
							if (((MonsterInst3)mod).isGemprod()) {
								gemProdCount++;
								if (gemProdCount == 7) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case GEMPROD8:
							if (((MonsterInst3)mod).isGemprod()) {
								gemProdCount++;
								if (gemProdCount == 8) {
									if (value1 != null) {
										((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
									}
									if (value2 != null) {
										((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
									}
								}
							}
							break;
						case SAILING:
							if (((MonsterInst3)mod).isSailing()) {
								if (value1 != null) {
									((MonsterInst3)mod).setValue1(Integer.parseInt(value1));
								}
								if (value2 != null) {
									((MonsterInst3)mod).setValue2(Integer.parseInt(value2));
								}
							}
							break;
						}
					}
				}

			}  
		});

		updateSelection();
	}
	
	private void setInst5(final Inst inst2, final XtextEditor editor, final String newName) 
	{
		final IXtextDocument myDocument = editor.getDocument();
		myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				Monster monsterToEdit = (Monster)input;
				int weaponCount = 0;
				int armorCount = 0;
				List<MonsterMods> modsToRemove = new ArrayList<MonsterMods>();
				List<MonsterMods> modsToAdd = new ArrayList<MonsterMods>();
				EList<MonsterMods> mods = monsterToEdit.getMods();
				for (MonsterMods mod : mods) {
					if (mod instanceof MonsterInst5) {
						Integer newValue = null;
						try {
							newValue = Integer.valueOf(newName);
						} catch (NumberFormatException e) {
							// is not a number
						}

						switch (inst2) {
						case WEAPON1:
							if (((MonsterInst5)mod).isWeapon()){
								weaponCount++;
								if (weaponCount == 1) {
									modsToRemove.add(mod);
									MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
									newMod.setWeapon(true);
									if (newValue != null) {
										newMod.setValue2(Integer.parseInt(newName));
									} else {
										newMod.setValue1(newName);
									}
									modsToAdd.add(newMod);
								}
							}
							break;
						case WEAPON2:
							if (((MonsterInst5)mod).isWeapon()){
								weaponCount++;
								if (weaponCount == 2) {
									modsToRemove.add(mod);
									MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
									newMod.setWeapon(true);
									if (newValue != null) {
										newMod.setValue2(Integer.parseInt(newName));
									} else {
										newMod.setValue1(newName);
									}
									modsToAdd.add(newMod);
								}
							}
							break;
						case WEAPON3:
							if (((MonsterInst5)mod).isWeapon()){
								weaponCount++;
								if (weaponCount == 3) {
									modsToRemove.add(mod);
									MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
									newMod.setWeapon(true);
									if (newValue != null) {
										newMod.setValue2(Integer.parseInt(newName));
									} else {
										newMod.setValue1(newName);
									}
									modsToAdd.add(newMod);
								}
							}
							break;
						case WEAPON4:
							if (((MonsterInst5)mod).isWeapon()){
								weaponCount++;
								if (weaponCount == 4) {
									modsToRemove.add(mod);
									MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
									newMod.setWeapon(true);
									if (newValue != null) {
										newMod.setValue2(Integer.parseInt(newName));
									} else {
										newMod.setValue1(newName);
									}
									modsToAdd.add(newMod);
								}
							}
							break;
						case ARMOR1:
							if (((MonsterInst5)mod).isArmor()){
								armorCount++;
								if (armorCount == 1) {
									modsToRemove.add(mod);
									MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
									newMod.setArmor(true);
									if (newValue != null) {
										newMod.setValue2(Integer.parseInt(newName));
									} else {
										newMod.setValue1(newName);
									}
									modsToAdd.add(newMod);
								}
							}
							break;
						case ARMOR2:
							if (((MonsterInst5)mod).isArmor()){
								armorCount++;
								if (armorCount == 2) {
									modsToRemove.add(mod);
									MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
									newMod.setArmor(true);
									if (newValue != null) {
										newMod.setValue2(Integer.parseInt(newName));
									} else {
										newMod.setValue1(newName);
									}
									modsToAdd.add(newMod);
								}
							}
							break;
						case ARMOR3:
							if (((MonsterInst5)mod).isArmor()){
								armorCount++;
								if (armorCount == 3) {
									modsToRemove.add(mod);
									MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
									newMod.setArmor(true);
									if (newValue != null) {
										newMod.setValue2(Integer.parseInt(newName));
									} else {
										newMod.setValue1(newName);
									}
									modsToAdd.add(newMod);
								}
							}
							break;
						case ONEBATTLESPELL:
							if (((MonsterInst5)mod).isOnebattlespell()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setOnebattlespell(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case FIRSTSHAPE:
							if (((MonsterInst5)mod).isFirstshape()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setFirstshape(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case SECONDSHAPE:
							if (((MonsterInst5)mod).isSecondshape()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setSecondshape(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case SECONDTMPSHAPE:
							if (((MonsterInst5)mod).isSecondtmpshape()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setSecondtmpshape(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case SHAPECHANGE:
							if (((MonsterInst5)mod).isShapechange()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setShapechange(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case LANDSHAPE:
							if (((MonsterInst5)mod).isLandshape()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setLandshape(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case WATERSHAPE:
							if (((MonsterInst5)mod).isWatershape()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setWatershape(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case FORESTSHAPE:
							if (((MonsterInst5)mod).isForestshape()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setForestshape(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case PLAINSHAPE:
							if (((MonsterInst5)mod).isPlainshape()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setPlainshape(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case DOMSUMMON:
							if (((MonsterInst5)mod).isDomsummon()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setDomsummon(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case DOMSUMMON2:
							if (((MonsterInst5)mod).isDomsummon2()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setDomsummon2(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case DOMSUMMON20:
							if (((MonsterInst5)mod).isDomsummon20()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setDomsummon20(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case MAKEMONSTERS1:
							if (((MonsterInst5)mod).isMakemonsters1()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setMakemonsters1(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case MAKEMONSTERS2:
							if (((MonsterInst5)mod).isMakemonsters2()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setMakemonsters2(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case MAKEMONSTERS3:
							if (((MonsterInst5)mod).isMakemonsters3()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setMakemonsters3(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case MAKEMONSTERS4:
							if (((MonsterInst5)mod).isMakemonsters4()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setMakemonsters4(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case MAKEMONSTERS5:
							if (((MonsterInst5)mod).isMakemonsters5()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setMakemonsters5(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case SUMMON1:
							if (((MonsterInst5)mod).isSummon1()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setSummon1(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case SUMMON5:
							if (((MonsterInst5)mod).isSummon5()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setSummon5(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case RAREDOMSUMMON:
							if (((MonsterInst5)mod).isRaredomsummon()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setRaredomsummon(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATTLESUM1:
							if (((MonsterInst5)mod).isBattlesum1()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBattlesum1(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATTLESUM2:
							if (((MonsterInst5)mod).isBattlesum2()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBattlesum2(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATTLESUM3:
							if (((MonsterInst5)mod).isBattlesum3()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBattlesum3(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATTLESUM4:
							if (((MonsterInst5)mod).isBattlesum4()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBattlesum4(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATTLESUM5:
							if (((MonsterInst5)mod).isBattlesum5()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBattlesum5(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM1:
							if (((MonsterInst5)mod).isBatstartsum1()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum1(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM2:
							if (((MonsterInst5)mod).isBatstartsum2()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum2(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM3:
							if (((MonsterInst5)mod).isBatstartsum3()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum3(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM4:
							if (((MonsterInst5)mod).isBatstartsum4()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum4(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM5:
							if (((MonsterInst5)mod).isBatstartsum5()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum5(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM1D6:
							if (((MonsterInst5)mod).isBatstartsum1d6()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum1d6(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM2D6:
							if (((MonsterInst5)mod).isBatstartsum2d6()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum2d6(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM3D6:
							if (((MonsterInst5)mod).isBatstartsum3d6()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum3d6(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM4D6:
							if (((MonsterInst5)mod).isBatstartsum4d6()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum4d6(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;
						case BATSTARTSUM5D6:
							if (((MonsterInst5)mod).isBatstartsum5d6()){
								modsToRemove.add(mod);
								MonsterInst5 newMod = DmFactory.eINSTANCE.createMonsterInst5();
								newMod.setBatstartsum5d6(true);
								if (newValue != null) {
									newMod.setValue2(Integer.parseInt(newName));
								} else {
									newMod.setValue1(newName);
								}
								modsToAdd.add(newMod);
							}
							break;						}
					}
				}
				mods.removeAll(modsToRemove);
				mods.addAll(modsToAdd);
			}  
		});

		updateSelection();
	}

	private void setInst6(final Inst inst2, final XtextEditor editor, final String newName) 
	{
		final IXtextDocument myDocument = editor.getDocument();
		myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				Monster monsterToEdit = (Monster)input;
				List<MonsterMods> modsToRemove = new ArrayList<MonsterMods>();
				List<MonsterMods> modsToAdd = new ArrayList<MonsterMods>();
				EList<MonsterMods> mods = monsterToEdit.getMods();
				for (MonsterMods mod : mods) {
					if (mod instanceof MonsterInst6) {
						Integer newValue = null;
						try {
							newValue = Integer.valueOf(newName);
						} catch (NumberFormatException e) {
							// is not a number
						}

						switch (inst2) {
						case HEAT:
							if (((MonsterInst6)mod).isHeat()){
								modsToRemove.add(mod);
								MonsterInst6 newMod = DmFactory.eINSTANCE.createMonsterInst6();
								newMod.setHeat(true);
								if (newValue != null) {
									newMod.setValue(Integer.parseInt(newName));
								}
								modsToAdd.add(newMod);
							}
							break;
						case COLD:
							if (((MonsterInst6)mod).isCold()){
								modsToRemove.add(mod);
								MonsterInst6 newMod = DmFactory.eINSTANCE.createMonsterInst6();
								newMod.setCold(true);
								if (newValue != null) {
									newMod.setValue(Integer.parseInt(newName));
								}
								modsToAdd.add(newMod);
							}
							break;
						case STEALTHY:
							if (((MonsterInst6)mod).isStealthy()){
								modsToRemove.add(mod);
								MonsterInst6 newMod = DmFactory.eINSTANCE.createMonsterInst6();
								newMod.setStealthy(true);
								if (newValue != null) {
									newMod.setValue(Integer.parseInt(newName));
								}
								modsToAdd.add(newMod);
							}
							break;
						}
					}
				}
				mods.removeAll(modsToRemove);
				mods.addAll(modsToAdd);
			}  
		});

		updateSelection();
	}

	private void addInst1(final Inst inst, final XtextEditor editor, final String newName) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@Override
			public void run() {
				final IXtextDocument myDocument = editor.getDocument();
				myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					@Override
					public void process(XtextResource resource) throws Exception {
						EList<MonsterMods> mods = ((Monster)input).getMods();
						MonsterInst1 type = DmFactory.eINSTANCE.createMonsterInst1();
						switch (inst) {
						case NAME:
							type.setName(true);
							break;
						case SPR1:
							type.setSpr1(true);
							break;
						case SPR2:
							type.setSpr2(true);
							break;
						case DESCR:
							type.setDescr(true);
							break;
						case FIXEDNAME:
							type.setFixedname(true);
							break;
						}
						type.setValue(newName);
						mods.add(type);
					}  
				});

				updateSelection();
			}
		});
	}
	
	private void addInst2(final Inst inst, final XtextEditor editor, final String newName) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@Override
			public void run() {
				final IXtextDocument myDocument = editor.getDocument();
				myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					@Override
					public void process(XtextResource resource) throws Exception {
						EList<MonsterMods> mods = ((Monster)input).getMods();
						MonsterInst2 type = DmFactory.eINSTANCE.createMonsterInst2();
						switch (inst) {
						case SPECIALLOOK:
							type.setSpeciallook(true);
							break;
						case AP:
							type.setAp(true);
							break;
						case MAPMOVE:
							type.setMapmove(true);
							break;
						case HP:
							type.setHp(true);
							break;
						case PROT:
							type.setProt(true);
							break;
						case SIZE:
							type.setSize(true);
							break;
						case RESSIZE:
							type.setRessize(true);
							break;
						case STR:
							type.setStr(true);
							break;
						case ENC:
							type.setEnc(true);
							break;
						case ATT:
							type.setAtt(true);
							break;
						case DEF:
							type.setDef(true);
							break;
						case PREC:
							type.setPrec(true);
							break;
						case MR:
							type.setMr(true);
							break;
						case MOR:
							type.setMor(true);
							break;
						case GCOST:
							type.setGcost(true);
							break;
						case RCOST:
							type.setRcost(true);
							break;
						case PATHCOST:
							type.setPathcost(true);
							break;
						case STARTDOM:
							type.setStartdom(true);
							break;
						case EYES:
							type.setEyes(true);
							break;
						case VOIDSANITY:
							type.setVoidsanity(true);
							break;
						case COPYSTATS:
							type.setCopystats(true);
							break;
						case COPYSPR:
							type.setCopyspr(true);
							break;
						case SHATTEREDSOUL:
							type.setShatteredsoul(true);
							break;
						case COLDRES:
							type.setColdres(true);
							break;
						case FIRERES:
							type.setFireres(true);
							break;
						case POISONRES:
							type.setPoisonres(true);
							break;
						case SHOCKRES:
							type.setShockres(true);
							break;
						case DARKVISION:
							type.setDarkvision(true);
							break;
						case SEDUCE:
							type.setSeduce(true);
							break;
						case SUCCUBUS:
							type.setSuccubus(true);
							break;
						case BECKON:
							type.setBeckon(true);
							break;
						case STARTAGE:
							type.setStartage(true);
							break;
						case MAXAGE:
							type.setMaxage(true);
							break;
						case OLDER:
							type.setOlder(true);
							break;
						case HEALER:
							type.setHealer(true);
							break;
						case STARTAFF:
							type.setStartaff(true);
							break;
						case SUPPLYBONUS:
							type.setSupplybonus(true);
							break;
						case RESOURCES:
							type.setResources(true);
							break;
						case UWDAMAGE:
							type.setUwdamage(true);
							break;
						case HOMESICK:
							type.setHomesick(true);
							break;
						case COLDPOWER:
							type.setColdpower(true);
							break;
						case FIREPOWER:
							type.setFirepower(true);
							break;
						case STORMPOWER:
							type.setStormpower(true);
							break;
						case DARKPOWER:
							type.setDarkpower(true);
							break;
						case SPRINGPOWER:
							type.setSpringpower(true);
							break;
						case SUMMERPOWER:
							type.setSummerpower(true);
							break;
						case FALLPOWER:
							type.setFallpower(true);
							break;
						case WINTERPOWER:
							type.setWinterpower(true);
							break;
						case AMBIDEXTROUS:
							type.setAmbidextrous(true);
							break;
						case BANEFIRESHIELD:
							type.setBanefireshield(true);
							break;
						case BERSERK:
							type.setBerserk(true);
							break;
						case STANDARD:
							type.setStandard(true);
							break;
						case ANIMALAWE:
							type.setAnimalawe(true);
							break;
						case AWE:
							type.setAwe(true);
							break;
						case FEAR:
							type.setFear(true);
							break;
						case REGENERATION:
							type.setRegeneration(true);
							break;
						case REINVIGORATION:
							type.setReinvigoration(true);
							break;
						case FIRESHIELD:
							type.setFireshield(true);
							break;
						case ICEPROT:
							type.setIceprot(true);
							break;
						case INVULNERABLE:
							type.setInvulnerable(true);
							break;
						case POISONCLOUD:
							type.setPoisoncloud(true);
							break;
						case DISEASECLOUD:
							type.setDiseasecloud(true);
							break;
						case BLOODVENGEANCE:
							type.setBloodvengeance(true);
							break;
						case CASTLEDEF:
							type.setCastledef(true);
							break;
						case SIEGEBONUS:
							type.setSiegebonus(true);
							break;
						case PATROLBONUS:
							type.setPatrolbonus(true);
							break;
						case PILLAGEBONUS:
							type.setPillagebonus(true);
							break;
						case MASTERRIT:
							type.setMasterrit(true);
							break;
						case RESEARCHBONUS:
							type.setResearchbonus(true);
							break;
						case INSPIRINGRES:
							type.setInspiringres(true);
							break;
						case FORGEBONUS:
							type.setForgebonus(true);
							break;
						case DOUSE:
							type.setDouse(true);
							break;
						case NOBADEVENTS:
							type.setNobadevents(true);
							break;
						case INCUNREST:
							type.setIncunrest(true);
							break;
						case SPREADDOM:
							type.setSpreaddom(true);
							break;
						case LEPER:
							type.setLeper(true);
							break;
						case POPKILL:
							type.setPopkill(true);
							break;
						case HERETIC:
							type.setHeretic(true);
							break;
						case ITEMSLOTS:
							type.setItemslots(true);
							break;
						case NAMETYPE:
							type.setNametype(true);
							break;				
						case RECLIMIT:
							type.setReclimit(true);
							break;				
						case GIFTOFWATER:
							type.setGiftofwater(true);
							break;				
						case INDEPMOVE:
							type.setIndepmove(true);
							break;				
						case PATIENCE:
							type.setPatience(true);
							break;				
						case FALSEARMY:
							type.setFalsearmy(true);
							break;				
						case FOOLSCOUTS:
							type.setFoolscouts(true);
							break;				
						case DESERTER:
							type.setDeserter(true);
							break;				
						case HORRORDESERTER:
							type.setHorrordeserter(true);
							break;				
						case DEFECTOR:
							type.setDefector(true);
							break;				
						case AUTOHEALER:
							type.setAutohealer(true);
							break;				
						case AUTODISHEALER:
							type.setAutodishealer(true);
							break;				
						case AUTODISGRINDER:
							type.setAutodisgrinder(true);
							break;				
						case WOUNDFEND:
							type.setWoundfend(true);
							break;				
						case DAMAGEREV:
							type.setDamagerev(true);
							break;				
						case SLIMER:
							type.setSlimer(true);
							break;				
						case DEATHDISEASE:
							type.setDeathdisease(true);
							break;				
						case DEATHPARALYZE:
							type.setDeathparalyze(true);
							break;				
						case DEATHFIRE:
							type.setDeathfire(true);
							break;				
						case CHAOSPOWER:
							type.setChaospower(true);
							break;				
						case DIGEST:
							type.setDigest(true);
							break;				
						case INCORPORATE:
							type.setIncorporate(true);
							break;				
						case INCPROVDEF:
							type.setIncprovdef(true);
							break;				
						case ELEGIST:
							type.setElegist(true);
							break;				
						case GOLD:
							type.setGold(true);
							break;				
						case GROWHP:
							type.setGrowhp(true);
							break;				
						case SHRINKHP:
							type.setShrinkhp(true);
							break;				
						case REANIMATOR:
							type.setReanimator(true);
							break;				
						case MONTAG:
							type.setMontag(true);
							break;				
						case INSPIRATIONAL:
							type.setInspirational(true);
							break;				
						case BEASTMASTER:
							type.setBeastmaster(true);
							break;				
						case TASKMASTER:
							type.setTaskmaster(true);
							break;				
						case FORMATIONFIGHTER:
							type.setFormationfighter(true);
							break;				
						case BODYGUARD:
							type.setBodyguard(true);
							break;				
						case DIVINEINS:
							type.setDivineins(true);
							break;				
						case FIRERANGE:
							type.setFirerange(true);
							break;				
						case AIRRANGE:
							type.setAirrange(true);
							break;				
						case WATERRANGE:
							type.setWaterrange(true);
							break;				
						case EARTHRANGE:
							type.setEarthrange(true);
							break;				
						case ASTRALRANGE:
							type.setAstralrange(true);
							break;				
						case DEATHRANGE:
							type.setDeathrange(true);
							break;				
						case NATURERANGE:
							type.setNaturerange(true);
							break;				
						case BLOODRANGE:
							type.setBloodrange(true);
							break;				
						case ELEMENTRANGE:
							type.setElementrange(true);
							break;				
						case SORCERYRANGE:
							type.setSorceryrange(true);
							break;				
						case ALLRANGE:
							type.setAllrange(true);
							break;				
						case TMPFIREGEMS:
							type.setTmpfiregems(true);
							break;				
						case TMPAIRGEMS:
							type.setTmpairgems(true);
							break;				
						case TMPWATERGEMS:
							type.setTmpwatergems(true);
							break;				
						case TMPEARTHGEMS:
							type.setTmpearthgems(true);
							break;				
						case TMPASTRALGEMS:
							type.setTmpastralgems(true);
							break;				
						case TMPDEATHGEMS:
							type.setTmpdeathgems(true);
							break;				
						case TMPNATUREGEMS:
							type.setTmpnaturegems(true);
							break;				
						case TMPBLOODSLAVES:
							type.setTmpbloodslaves(true);
							break;				
						case MAKEPEARLS:
							type.setMakepearls(true);
							break;				
						case BONUSSPELLS:
							type.setBonusspells(true);
							break;				
						case TAINTED:
							type.setTainted(true);
							break;				
						case FIXFORGEBONUS:
							type.setFixforgebonus(true);
							break;				
						case MASTERSMITH:
							type.setMastersmith(true);
							break;				
						case CROSSBREEDER:
							type.setCrossbreeder(true);
							break;				
						case DEATHBANISH:
							type.setDeathbanish(true);
							break;				
						case KOKYTOSRET:
							type.setKokytosret(true);
							break;				
						case INFERNORET:
							type.setInfernoret(true);
							break;				
						case VOIDRET:
							type.setVoidret(true);
							break;				
						case ALLRET:
							type.setAllret(true);
							break;				
						case MAGICPOWER:
							type.setMagicpower(true);
							break;				
						case RANDOMSPELL:
							type.setRandomspell(true);
							break;				
						case HOMEREALM:
							type.setHomerealm(true);
							break;				
						case CHAOSREC:
							type.setChaosrec(true);
							break;				
						}
						type.setValue(Integer.valueOf(newName));
						// copystats should be the first command
						if (inst == Inst.COPYSTATS) {
							mods.add(0, type);
						} else {
							mods.add(type);	
						}
					}  
				});

				updateSelection();
			}
		});
	}
	
	private void addInst3(final Inst inst, final XtextEditor editor, final String newName1, final String newName2) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@Override
			public void run() {
				final IXtextDocument myDocument = editor.getDocument();
				myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					@Override
					public void process(XtextResource resource) throws Exception {
						EList<MonsterMods> mods = ((Monster)input).getMods();
						MonsterInst3 type = DmFactory.eINSTANCE.createMonsterInst3();
						switch (inst) {
						case MAGICSKILL1:
							type.setMagicskill(true);
							break;
						case MAGICSKILL2:
							type.setMagicskill(true);
							break;
						case MAGICSKILL3:
							type.setMagicskill(true);
							break;
						case MAGICSKILL4:
							type.setMagicskill(true);
							break;
						case MAGICSKILL5:
							type.setMagicskill(true);
							break;
						case MAGICSKILL6:
							type.setMagicskill(true);
							break;
						case MAGICSKILL7:
							type.setMagicskill(true);
							break;
						case MAGICSKILL8:
							type.setMagicskill(true);
							break;
						case CUSTOMMAGIC1:
							type.setCustommagic(true);
							break;
						case CUSTOMMAGIC2:
							type.setCustommagic(true);
							break;
						case CUSTOMMAGIC3:
							type.setCustommagic(true);
							break;
						case CUSTOMMAGIC4:
							type.setCustommagic(true);
							break;
						case CUSTOMMAGIC5:
							type.setCustommagic(true);
							break;
						case CUSTOMMAGIC6:
							type.setCustommagic(true);
							break;
						case CUSTOMMAGIC7:
							type.setCustommagic(true);
							break;
						case CUSTOMMAGIC8:
							type.setCustommagic(true);
							break;
						case MAGICBOOST1:
							type.setMagicboost(true);
							break;
						case MAGICBOOST2:
							type.setMagicboost(true);
							break;
						case MAGICBOOST3:
							type.setMagicboost(true);
							break;
						case MAGICBOOST4:
							type.setMagicboost(true);
							break;
						case MAGICBOOST5:
							type.setMagicboost(true);
							break;
						case MAGICBOOST6:
							type.setMagicboost(true);
							break;
						case MAGICBOOST7:
							type.setMagicboost(true);
							break;
						case MAGICBOOST8:
							type.setMagicboost(true);
							break;
						case GEMPROD1:
							type.setGemprod(true);
							break;
						case GEMPROD2:
							type.setGemprod(true);
							break;
						case GEMPROD3:
							type.setGemprod(true);
							break;
						case GEMPROD4:
							type.setGemprod(true);
							break;
						case GEMPROD5:
							type.setGemprod(true);
							break;
						case GEMPROD6:
							type.setGemprod(true);
							break;
						case GEMPROD7:
							type.setGemprod(true);
							break;
						case GEMPROD8:
							type.setGemprod(true);
							break;
						case SAILING:
							type.setSailing(true);
							break;
						}
						type.setValue1(Integer.valueOf(newName1));
						type.setValue2(Integer.valueOf(newName2));
						mods.add(type);
					}  
				});

			}
		});
		updateSelection();
	}
	
	private void addInst4(final Inst inst, final XtextEditor editor) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@Override
			public void run() {
				final IXtextDocument myDocument = editor.getDocument();
				myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					@Override
					public void process(XtextResource resource) throws Exception {
						EList<MonsterMods> mods = ((Monster)input).getMods();
						MonsterInst4 type = DmFactory.eINSTANCE.createMonsterInst4();
						switch (inst) {
						case CLEAR:
							type.setClear(true);
							break;
						case CLEARMAGIC:
							type.setClearmagic(true);
							break;
						case CLEARWEAPONS:
							type.setClearweapons(true);
							break;
						case CLEARARMOR:
							type.setCleararmor(true);
							break;
						case CLEARSPEC:
							type.setClearspec(true);
							break;
						case FEMALE:
							type.setFemale(true);
							break;
						case MOUNTED:
							type.setMounted(true);
							break;
						case HOLY:
							type.setHoly(true);
							break;
						case ANIMAL:
							type.setAnimal(true);
							break;
						case UNDEAD:
							type.setUndead(true);
							break;
						case DEMON:
							type.setDemon(true);
							break;
						case MAGICBEING:
							type.setMagicbeing(true);
							break;
						case STONEBEING:
							type.setStonebeing(true);
							break;
						case INANIMATE:
							type.setInanimate(true);
							break;
						case COLDBLOOD:
							type.setColdblood(true);
							break;
						case IMMORTAL:
							type.setImmortal(true);
							break;
						case BLIND:
							type.setBlind(true);
							break;
						case UNIQUE:
							type.setUnique(true);
							break;
						case IMMOBILE:
							type.setImmobile(true);
							break;
						case AQUATIC:
							type.setAquatic(true);
							break;
						case AMPHIBIAN:
							type.setAmphibian(true);
							break;
						case POORAMPHIBIAN:
							type.setPooramphibian(true);
							break;
						case FLYING:
							type.setFlying(true);
							break;
						case STORMIMMUNE:
							type.setStormimmune(true);
							break;
						case FORESTSURVIVAL:
							type.setForestsurvival(true);
							break;
						case MOUNTAINSURVIVAL:
							type.setMountainsurvival(true);
							break;
						case SWAMPSURVIVAL:
							type.setSwampsurvival(true);
							break;
						case WASTESURVIVAL:
							type.setWastesurvival(true);
							break;
						case ILLUSION:
							type.setIllusion(true);
							break;
						case SPY:
							type.setSpy(true);
							break;
						case ASSASSIN:
							type.setAssassin(true);
							break;
						case HEAL:
							type.setHeal(true);
							break;
						case NOHEAL:
							type.setNoheal(true);
							break;
						case NEEDNOTEAT:
							type.setNeednoteat(true);
							break;
						case ETHEREAL:
							type.setEthereal(true);
							break;
						case TRAMPLE:
							type.setTrample(true);
							break;
						case ENTANGLE:
							type.setEntangle(true);
							break;
						case EYELOSS:
							type.setEyeloss(true);
							break;
						case HORRORMARK:
							type.setHorrormark(true);
							break;
						case POISONARMOR:
							type.setPoisonarmor(true);
							break;
						case INQUISITOR:
							type.setInquisitor(true);
							break;
						case NOITEM:
							type.setNoitem(true);
							break;
						case DRAINIMMUNE:
							type.setDrainimmune(true);
							break;
						case NOLEADER:
							type.setNoleader(true);
							break;
						case POORLEADER:
							type.setPoorleader(true);
							break;
						case OKLEADER:
							type.setOkleader(true);
							break;
						case GOODLEADER:
							type.setGoodleader(true);
							break;
						case EXPERTLEADER:
							type.setExpertleader(true);
							break;
						case SUPERIORLEADER:
							type.setSuperiorleader(true);
							break;
						case NOMAGICLEADER:
							type.setNomagicleader(true);
							break;
						case POORMAGICLEADER:
							type.setPoormagicleader(true);
							break;
						case OKMAGICLEADER:
							type.setOkmagicleader(true);
							break;
						case GOODMAGICLEADER:
							type.setGoodmagicleader(true);
							break;
						case EXPERTMAGICLEADER:
							type.setExpertmagicleader(true);
							break;
						case SUPERIORMAGICLEADER:
							type.setSuperiormagicleader(true);
							break;
						case NOUNDEADLEADER:
							type.setNoundeadleader(true);
							break;
						case POORUNDEADLEADER:
							type.setPoorundeadleader(true);
							break;
						case OKUNDEADLEADER:
							type.setOkundeadleader(true);
							break;
						case GOODUNDEADLEADER:
							type.setGoodundeadleader(true);
							break;
						case EXPERTUNDEADLEADER:
							type.setExpertundeadleader(true);
							break;
						case SUPERIORUNDEADLEADER:
							type.setSuperiorundeadleader(true);
							break;				
						case SLOWREC:
							type.setSlowrec(true);
							break;				
						case NOSLOWREC:
							type.setNoslowrec(true);
							break;				
						case REQLAB:
							type.setReqlab(true);
							break;				
						case REQTEMPLE:
							type.setReqtemple(true);
							break;				
						case SINGLEBATTLE:
							type.setSinglebattle(true);
							break;				
						case AISINGLEREC:
							type.setAisinglerec(true);
							break;				
						case AINOREC:
							type.setAinorec(true);
							break;				
						case LESSERHORROR:
							type.setLesserhorror(true);
							break;				
						case GREATERHORROR:
							type.setGreaterhorror(true);
							break;				
						case DOOMHORROR:
							type.setDoomhorror(true);
							break;				
						case BUG:
							type.setBug(true);
							break;				
						case UWBUG:
							type.setUwbug(true);
							break;				
						case AUTOCOMPETE:
							type.setAutocompete(true);
							break;				
						case FLOAT:
							type.setFloat(true);
							break;				
						case TELEPORT:
							type.setTeleport(true);
							break;				
						case NORIVERPASS:
							type.setNoriverpass(true);
							break;				
						case UNTELEPORTABLE:
							type.setUnteleportable(true);
							break;				
						case HPOVERFLOW:
							type.setHpoverflow(true);
							break;				
						case PIERCERES:
							type.setPierceres(true);
							break;				
						case SLASHRES:
							type.setSlashres(true);
							break;				
						case BLUNTRES:
							type.setBluntres(true);
							break;				
						case DEATHCURSE:
							type.setDeathcurse(true);
							break;				
						case TRAMPSWALLOW:
							type.setTrampswallow(true);
							break;				
						case TAXCOLLECTOR:
							type.setTaxcollector(true);
							break;				
						case NOHOF:
							type.setNohof(true);
							break;				
						case CLEANSHAPE:
							type.setCleanshape(true);
							break;				
						case SLAVE:
							type.setSlave(true);
							break;				
						case UNDISCIPLINED:
							type.setUndisciplined(true);
							break;				
						case MAGICIMMUNE:
							type.setMagicimmune(true);
							break;				
						case COMSLAVE:
							type.setComslave(true);
							break;				
						}
						mods.add(type);
					}  
				});

				updateSelection();
			}
		});
	}

	private void addInst5(final Inst inst, final XtextEditor editor, final String newName) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@Override
			public void run() {
				final IXtextDocument myDocument = editor.getDocument();
				myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					@Override
					public void process(XtextResource resource) throws Exception {
						EList<MonsterMods> mods = ((Monster)input).getMods();
						MonsterInst5 type = DmFactory.eINSTANCE.createMonsterInst5();
						switch (inst) {
						case WEAPON1:
							type.setWeapon(true);
							break;
						case WEAPON2:
							type.setWeapon(true);
							break;
						case WEAPON3:
							type.setWeapon(true);
							break;
						case WEAPON4:
							type.setWeapon(true);
							break;
						case ARMOR1:
							type.setArmor(true);
							break;
						case ARMOR2:
							type.setArmor(true);
							break;
						case ARMOR3:
							type.setArmor(true);
							break;
						case ONEBATTLESPELL:
							type.setOnebattlespell(true);
							break;
						case FIRSTSHAPE:
							type.setFirstshape(true);
							break;
						case SECONDSHAPE:
							type.setSecondshape(true);
							break;
						case SECONDTMPSHAPE:
							type.setSecondtmpshape(true);
							break;
						case SHAPECHANGE:
							type.setShapechange(true);
							break;
						case LANDSHAPE:
							type.setLandshape(true);
							break;
						case WATERSHAPE:
							type.setWatershape(true);
							break;
						case FORESTSHAPE:
							type.setForestshape(true);
							break;
						case PLAINSHAPE:
							type.setPlainshape(true);
							break;
						case DOMSUMMON:
							type.setDomsummon(true);
							break;
						case DOMSUMMON2:
							type.setDomsummon2(true);
							break;
						case DOMSUMMON20:
							type.setDomsummon20(true);
							break;
						case MAKEMONSTERS1:
							type.setMakemonsters1(true);
							break;
						case MAKEMONSTERS2:
							type.setMakemonsters2(true);
							break;
						case MAKEMONSTERS3:
							type.setMakemonsters3(true);
							break;
						case MAKEMONSTERS4:
							type.setMakemonsters4(true);
							break;
						case MAKEMONSTERS5:
							type.setMakemonsters5(true);
							break;
						case SUMMON1:
							type.setSummon1(true);
							break;
						case SUMMON2:
							type.setSummon2(true);
							break;
						case SUMMON3:
							type.setSummon3(true);
							break;
						case SUMMON4:
							type.setSummon4(true);
							break;
						case SUMMON5:
							type.setSummon5(true);
							break;
						case RAREDOMSUMMON:
							type.setRaredomsummon(true);
							break;
						case BATTLESUM1:
							type.setBattlesum1(true);
							break;
						case BATTLESUM2:
							type.setBattlesum2(true);
							break;
						case BATTLESUM3:
							type.setBattlesum3(true);
							break;
						case BATTLESUM4:
							type.setBattlesum4(true);
							break;
						case BATTLESUM5:
							type.setBattlesum5(true);
							break;
						case BATSTARTSUM1:
							type.setBatstartsum1(true);
							break;
						case BATSTARTSUM2:
							type.setBatstartsum2(true);
							break;
						case BATSTARTSUM3:
							type.setBatstartsum3(true);
							break;
						case BATSTARTSUM4:
							type.setBatstartsum4(true);
							break;
						case BATSTARTSUM5:
							type.setBatstartsum5(true);
							break;
						case BATSTARTSUM1D6:
							type.setBatstartsum1d6(true);
							break;
						case BATSTARTSUM2D6:
							type.setBatstartsum2d6(true);
							break;
						case BATSTARTSUM3D6:
							type.setBatstartsum3d6(true);
							break;
						case BATSTARTSUM4D6:
							type.setBatstartsum4d6(true);
							break;
						case BATSTARTSUM5D6:
							type.setBatstartsum5d6(true);
							break;
						}
						Integer newValue = null;
						try {
							newValue = Integer.valueOf(newName);
						} catch (NumberFormatException e) {
							// is not a number
						}
						if (newValue != null) {
							type.setValue2(Integer.valueOf(newName));
						} else {
							type.setValue1(newName);
						}
						mods.add(type);
					}  
				});

				updateSelection();
			}
		});
	}
	
	private void addInst6(final Inst inst, final XtextEditor editor, final String newName) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@Override
			public void run() {
				final IXtextDocument myDocument = editor.getDocument();
				myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					@Override
					public void process(XtextResource resource) throws Exception {
						EList<MonsterMods> mods = ((Monster)input).getMods();
						MonsterInst6 type = DmFactory.eINSTANCE.createMonsterInst6();
						switch (inst) {
						case HEAT:
							type.setHeat(true);
							break;
						case COLD:
							type.setCold(true);
							break;
						case STEALTHY:
							type.setStealthy(true);
							break;
						}
						type.setValue(Integer.valueOf(newName));
						mods.add(type);
					}  
				});

				updateSelection();
			}
		});
	}
	
	private void removeInst(final Inst inst2, final XtextEditor editor) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@Override
			public void run() {
				final IXtextDocument myDocument = editor.getDocument();
				myDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					@Override
					public void process(XtextResource resource) throws Exception {
						int magicSkillCount = 0;
						int customMagicCount = 0;
						int boostCount = 0;
						int gemProdCount = 0;
						MonsterMods modToRemove = null;
						int armorCount = 0;
						int weaponCount = 0;
						EList<MonsterMods> mods = ((Monster)input).getMods();
						for (MonsterMods mod : mods) {
							if (mod instanceof MonsterInst1) {
								switch (inst2) {
								case NAME:
									if (((MonsterInst1)mod).isName()){
										modToRemove = mod;
									}
									break;
								case SPR1:
									if (((MonsterInst1)mod).isSpr1()){
										modToRemove = mod;
									}
									break;
								case SPR2:
									if (((MonsterInst1)mod).isSpr2()){
										modToRemove = mod;
									}
									break;
								case DESCR:
									if (((MonsterInst1)mod).isDescr()){
										modToRemove = mod;
									}
									break;
								case FIXEDNAME:
									if (((MonsterInst1)mod).isFixedname()){
										modToRemove = mod;
									}
									break;
								}
							}
							if (mod instanceof MonsterInst2) {
								switch (inst2) {
								case SPECIALLOOK:
									if (((MonsterInst2)mod).isSpeciallook()){
										modToRemove = mod;
									}
									break;
								case AP:
									if (((MonsterInst2)mod).isAp()){
										modToRemove = mod;
									}
									break;
								case MAPMOVE:
									if (((MonsterInst2)mod).isMapmove()){
										modToRemove = mod;
									}
									break;
								case HP:
									if (((MonsterInst2)mod).isHp()){
										modToRemove = mod;
									}
									break;
								case PROT:
									if (((MonsterInst2)mod).isProt()){
										modToRemove = mod;
									}
									break;
								case SIZE:
									if (((MonsterInst2)mod).isSize()){
										modToRemove = mod;
									}
									break;
								case RESSIZE:
									if (((MonsterInst2)mod).isRessize()){
										modToRemove = mod;
									}
									break;
								case STR:
									if (((MonsterInst2)mod).isStr()){
										modToRemove = mod;
									}
									break;
								case ENC:
									if (((MonsterInst2)mod).isEnc()){
										modToRemove = mod;
									}
									break;
								case ATT:
									if (((MonsterInst2)mod).isAtt()){
										modToRemove = mod;
									}
									break;
								case DEF:
									if (((MonsterInst2)mod).isDef()){
										modToRemove = mod;
									}
									break;
								case PREC:
									if (((MonsterInst2)mod).isPrec()){
										modToRemove = mod;
									}
									break;
								case MR:
									if (((MonsterInst2)mod).isMr()){
										modToRemove = mod;
									}
									break;
								case MOR:
									if (((MonsterInst2)mod).isMor()){
										modToRemove = mod;
									}
									break;
								case GCOST:
									if (((MonsterInst2)mod).isGcost()){
										modToRemove = mod;
									}
									break;
								case RCOST:
									if (((MonsterInst2)mod).isRcost()){
										modToRemove = mod;
									}
									break;
								case PATHCOST:
									if (((MonsterInst2)mod).isPathcost()){
										modToRemove = mod;
									}
									break;
								case STARTDOM:
									if (((MonsterInst2)mod).isStartdom()){
										modToRemove = mod;
									}
									break;
								case EYES:
									if (((MonsterInst2)mod).isEyes()){
										modToRemove = mod;
									}
									break;
								case VOIDSANITY:
									if (((MonsterInst2)mod).isVoidsanity()){
										modToRemove = mod;
									}
									break;
								case COPYSTATS:
									if (((MonsterInst2)mod).isCopystats()){
										modToRemove = mod;
									}
									break;
								case COPYSPR:
									if (((MonsterInst2)mod).isCopyspr()){
										modToRemove = mod;
									}
									break;
								case SHATTEREDSOUL:
									if (((MonsterInst2)mod).isShatteredsoul()){
										modToRemove = mod;
									}
									break;
								case COLDRES:
									if (((MonsterInst2)mod).isColdres()){
										modToRemove = mod;
									}
									break;
								case FIRERES:
									if (((MonsterInst2)mod).isFireres()){
										modToRemove = mod;
									}
									break;
								case POISONRES:
									if (((MonsterInst2)mod).isPoisonres()){
										modToRemove = mod;
									}
									break;
								case SHOCKRES:
									if (((MonsterInst2)mod).isShockres()){
										modToRemove = mod;
									}
									break;
								case DARKVISION:
									if (((MonsterInst2)mod).isDarkvision()){
										modToRemove = mod;
									}
									break;
								case SEDUCE:
									if (((MonsterInst2)mod).isSeduce()){
										modToRemove = mod;
									}
									break;
								case SUCCUBUS:
									if (((MonsterInst2)mod).isSuccubus()){
										modToRemove = mod;
									}
									break;
								case BECKON:
									if (((MonsterInst2)mod).isBeckon()){
										modToRemove = mod;
									}
									break;
								case STARTAGE:
									if (((MonsterInst2)mod).isStartage()){
										modToRemove = mod;
									}
									break;
								case MAXAGE:
									if (((MonsterInst2)mod).isMaxage()){
										modToRemove = mod;
									}
									break;
								case OLDER:
									if (((MonsterInst2)mod).isOlder()){
										modToRemove = mod;
									}
									break;
								case HEALER:
									if (((MonsterInst2)mod).isHealer()){
										modToRemove = mod;
									}
									break;
								case STARTAFF:
									if (((MonsterInst2)mod).isStartaff()){
										modToRemove = mod;
									}
									break;
								case SUPPLYBONUS:
									if (((MonsterInst2)mod).isSupplybonus()){
										modToRemove = mod;
									}
									break;
								case RESOURCES:
									if (((MonsterInst2)mod).isResources()){
										modToRemove = mod;
									}
									break;
								case UWDAMAGE:
									if (((MonsterInst2)mod).isUwdamage()){
										modToRemove = mod;
									}
									break;
								case HOMESICK:
									if (((MonsterInst2)mod).isHomesick()){
										modToRemove = mod;
									}
									break;
								case COLDPOWER:
									if (((MonsterInst2)mod).isColdpower()){
										modToRemove = mod;
									}
									break;
								case FIREPOWER:
									if (((MonsterInst2)mod).isFirepower()){
										modToRemove = mod;
									}
									break;
								case STORMPOWER:
									if (((MonsterInst2)mod).isStormpower()){
										modToRemove = mod;
									}
									break;
								case DARKPOWER:
									if (((MonsterInst2)mod).isDarkpower()){
										modToRemove = mod;
									}
									break;
								case SPRINGPOWER:
									if (((MonsterInst2)mod).isSpringpower()){
										modToRemove = mod;
									}
									break;
								case SUMMERPOWER:
									if (((MonsterInst2)mod).isSummerpower()){
										modToRemove = mod;
									}
									break;
								case FALLPOWER:
									if (((MonsterInst2)mod).isFallpower()){
										modToRemove = mod;
									}
									break;
								case WINTERPOWER:
									if (((MonsterInst2)mod).isWinterpower()){
										modToRemove = mod;
									}
									break;
								case AMBIDEXTROUS:
									if (((MonsterInst2)mod).isAmbidextrous()){
										modToRemove = mod;
									}
									break;
								case BANEFIRESHIELD:
									if (((MonsterInst2)mod).isBanefireshield()){
										modToRemove = mod;
									}
									break;
								case BERSERK:
									if (((MonsterInst2)mod).isBerserk()){
										modToRemove = mod;
									}
									break;
								case STANDARD:
									if (((MonsterInst2)mod).isStandard()){
										modToRemove = mod;
									}
									break;
								case ANIMALAWE:
									if (((MonsterInst2)mod).isAnimalawe()){
										modToRemove = mod;
									}
									break;
								case AWE:
									if (((MonsterInst2)mod).isAwe()){
										modToRemove = mod;
									}
									break;
								case FEAR:
									if (((MonsterInst2)mod).isFear()){
										modToRemove = mod;
									}
									break;
								case REGENERATION:
									if (((MonsterInst2)mod).isRegeneration()){
										modToRemove = mod;
									}
									break;
								case REINVIGORATION:
									if (((MonsterInst2)mod).isReinvigoration()){
										modToRemove = mod;
									}
									break;
								case FIRESHIELD:
									if (((MonsterInst2)mod).isFireshield()){
										modToRemove = mod;
									}
									break;
								case ICEPROT:
									if (((MonsterInst2)mod).isIceprot()){
										modToRemove = mod;
									}
									break;
								case INVULNERABLE:
									if (((MonsterInst2)mod).isInvulnerable()){
										modToRemove = mod;
									}
									break;
								case POISONCLOUD:
									if (((MonsterInst2)mod).isPoisoncloud()){
										modToRemove = mod;
									}
									break;
								case DISEASECLOUD:
									if (((MonsterInst2)mod).isDiseasecloud()){
										modToRemove = mod;
									}
									break;
								case BLOODVENGEANCE:
									if (((MonsterInst2)mod).isBloodvengeance()){
										modToRemove = mod;
									}
									break;
								case CASTLEDEF:
									if (((MonsterInst2)mod).isCastledef()){
										modToRemove = mod;
									}
									break;
								case SIEGEBONUS:
									if (((MonsterInst2)mod).isSiegebonus()){
										modToRemove = mod;
									}
									break;
								case PATROLBONUS:
									if (((MonsterInst2)mod).isPatrolbonus()){
										modToRemove = mod;
									}
									break;
								case PILLAGEBONUS:
									if (((MonsterInst2)mod).isPillagebonus()){
										modToRemove = mod;
									}
									break;
								case MASTERRIT:
									if (((MonsterInst2)mod).isMasterrit()){
										modToRemove = mod;
									}
									break;
								case RESEARCHBONUS:
									if (((MonsterInst2)mod).isResearchbonus()){
										modToRemove = mod;
									}
									break;
								case INSPIRINGRES:
									if (((MonsterInst2)mod).isInspiringres()){
										modToRemove = mod;
									}
									break;
								case FORGEBONUS:
									if (((MonsterInst2)mod).isForgebonus()){
										modToRemove = mod;
									}
									break;
								case DOUSE:
									if (((MonsterInst2)mod).isDouse()){
										modToRemove = mod;
									}
									break;
								case NOBADEVENTS:
									if (((MonsterInst2)mod).isNobadevents()){
										modToRemove = mod;
									}
									break;
								case INCUNREST:
									if (((MonsterInst2)mod).isIncunrest()){
										modToRemove = mod;
									}
									break;
								case SPREADDOM:
									if (((MonsterInst2)mod).isSpreaddom()){
										modToRemove = mod;
									}
									break;
								case LEPER:
									if (((MonsterInst2)mod).isLeper()){
										modToRemove = mod;
									}
									break;
								case POPKILL:
									if (((MonsterInst2)mod).isPopkill()){
										modToRemove = mod;
									}
									break;
								case HERETIC:
									if (((MonsterInst2)mod).isHeretic()){
										modToRemove = mod;
									}
									break;
								case ITEMSLOTS:
									if (((MonsterInst2)mod).isItemslots()){
										modToRemove = mod;
									}
									break;
								case NAMETYPE:
									if (((MonsterInst2)mod).isNametype()){
										modToRemove = mod;
									}
									break;
								case RECLIMIT:
									if (((MonsterInst2)mod).isReclimit()){
										modToRemove = mod;
									}
									break;
								case HOMEREALM:
									if (((MonsterInst2)mod).isHomerealm()){
										modToRemove = mod;
									}
									break;
								case GIFTOFWATER:
									if (((MonsterInst2)mod).isGiftofwater()){
										modToRemove = mod;
									}
									break;
								case INDEPMOVE:
									if (((MonsterInst2)mod).isIndepmove()){
										modToRemove = mod;
									}
									break;
								case PATIENCE:
									if (((MonsterInst2)mod).isPatience()){
										modToRemove = mod;
									}
									break;
								case FALSEARMY:
									if (((MonsterInst2)mod).isFalsearmy()){
										modToRemove = mod;
									}
									break;
								case FOOLSCOUTS:
									if (((MonsterInst2)mod).isFoolscouts()){
										modToRemove = mod;
									}
									break;
								case DESERTER:
									if (((MonsterInst2)mod).isDeserter()){
										modToRemove = mod;
									}
									break;
								case HORRORDESERTER:
									if (((MonsterInst2)mod).isHorrordeserter()){
										modToRemove = mod;
									}
									break;
								case DEFECTOR:
									if (((MonsterInst2)mod).isDefector()){
										modToRemove = mod;
									}
									break;
								case AUTOHEALER:
									if (((MonsterInst2)mod).isAutohealer()){
										modToRemove = mod;
									}
									break;
								case AUTODISHEALER:
									if (((MonsterInst2)mod).isAutodishealer()){
										modToRemove = mod;
									}
									break;
								case AUTODISGRINDER:
									if (((MonsterInst2)mod).isAutodisgrinder()){
										modToRemove = mod;
									}
									break;
								case WOUNDFEND:
									if (((MonsterInst2)mod).isWoundfend()){
										modToRemove = mod;
									}
									break;
								case DAMAGEREV:
									if (((MonsterInst2)mod).isDamagerev()){
										modToRemove = mod;
									}
									break;
								case SLIMER:
									if (((MonsterInst2)mod).isSlimer()){
										modToRemove = mod;
									}
									break;
								case DEATHDISEASE:
									if (((MonsterInst2)mod).isDeathdisease()){
										modToRemove = mod;
									}
									break;
								case DEATHPARALYZE:
									if (((MonsterInst2)mod).isDeathparalyze()){
										modToRemove = mod;
									}
									break;
								case DEATHFIRE:
									if (((MonsterInst2)mod).isDeathfire()){
										modToRemove = mod;
									}
									break;
								case CHAOSPOWER:
									if (((MonsterInst2)mod).isChaospower()){
										modToRemove = mod;
									}
									break;
								case MAGICPOWER:
									if (((MonsterInst2)mod).isMagicpower()){
										modToRemove = mod;
									}
									break;
								case DIGEST:
									if (((MonsterInst2)mod).isDigest()){
										modToRemove = mod;
									}
									break;
								case INCORPORATE:
									if (((MonsterInst2)mod).isIncorporate()){
										modToRemove = mod;
									}
									break;
								case INCPROVDEF:
									if (((MonsterInst2)mod).isIncprovdef()){
										modToRemove = mod;
									}
									break;
								case ELEGIST:
									if (((MonsterInst2)mod).isElegist()){
										modToRemove = mod;
									}
									break;
								case GOLD:
									if (((MonsterInst2)mod).isGold()){
										modToRemove = mod;
									}
									break;
								case GROWHP:
									if (((MonsterInst2)mod).isGrowhp()){
										modToRemove = mod;
									}
									break;
								case SHRINKHP:
									if (((MonsterInst2)mod).isShrinkhp()){
										modToRemove = mod;
									}
									break;
								case REANIMATOR:
									if (((MonsterInst2)mod).isReanimator()){
										modToRemove = mod;
									}
									break;
								case MONTAG:
									if (((MonsterInst2)mod).isMontag()){
										modToRemove = mod;
									}
									break;
								case INSPIRATIONAL:
									if (((MonsterInst2)mod).isInspirational()){
										modToRemove = mod;
									}
									break;
								case BEASTMASTER:
									if (((MonsterInst2)mod).isBeastmaster()){
										modToRemove = mod;
									}
									break;
								case TASKMASTER:
									if (((MonsterInst2)mod).isTaskmaster()){
										modToRemove = mod;
									}
									break;
								case FORMATIONFIGHTER:
									if (((MonsterInst2)mod).isFormationfighter()){
										modToRemove = mod;
									}
									break;
								case BODYGUARD:
									if (((MonsterInst2)mod).isBodyguard()){
										modToRemove = mod;
									}
									break;
								case DIVINEINS:
									if (((MonsterInst2)mod).isDivineins()){
										modToRemove = mod;
									}
									break;
								case FIRERANGE:
									if (((MonsterInst2)mod).isFirerange()){
										modToRemove = mod;
									}
									break;
								case AIRRANGE:
									if (((MonsterInst2)mod).isAirrange()){
										modToRemove = mod;
									}
									break;
								case WATERRANGE:
									if (((MonsterInst2)mod).isWaterrange()){
										modToRemove = mod;
									}
									break;
								case EARTHRANGE:
									if (((MonsterInst2)mod).isEarthrange()){
										modToRemove = mod;
									}
									break;
								case ASTRALRANGE:
									if (((MonsterInst2)mod).isAstralrange()){
										modToRemove = mod;
									}
									break;
								case DEATHRANGE:
									if (((MonsterInst2)mod).isDeathrange()){
										modToRemove = mod;
									}
									break;
								case NATURERANGE:
									if (((MonsterInst2)mod).isNaturerange()){
										modToRemove = mod;
									}
									break;
								case BLOODRANGE:
									if (((MonsterInst2)mod).isBloodrange()){
										modToRemove = mod;
									}
									break;
								case ELEMENTRANGE:
									if (((MonsterInst2)mod).isElementrange()){
										modToRemove = mod;
									}
									break;
								case SORCERYRANGE:
									if (((MonsterInst2)mod).isSorceryrange()){
										modToRemove = mod;
									}
									break;
								case ALLRANGE:
									if (((MonsterInst2)mod).isAllrange()){
										modToRemove = mod;
									}
									break;
								case TMPFIREGEMS:
									if (((MonsterInst2)mod).isTmpfiregems()){
										modToRemove = mod;
									}
									break;
								case TMPAIRGEMS:
									if (((MonsterInst2)mod).isTmpairgems()){
										modToRemove = mod;
									}
									break;
								case TMPWATERGEMS:
									if (((MonsterInst2)mod).isTmpwatergems()){
										modToRemove = mod;
									}
									break;
								case TMPEARTHGEMS:
									if (((MonsterInst2)mod).isTmpearthgems()){
										modToRemove = mod;
									}
									break;
								case TMPASTRALGEMS:
									if (((MonsterInst2)mod).isTmpastralgems()){
										modToRemove = mod;
									}
									break;
								case TMPDEATHGEMS:
									if (((MonsterInst2)mod).isTmpdeathgems()){
										modToRemove = mod;
									}
									break;
								case TMPNATUREGEMS:
									if (((MonsterInst2)mod).isTmpnaturegems()){
										modToRemove = mod;
									}
									break;
								case TMPBLOODSLAVES:
									if (((MonsterInst2)mod).isTmpbloodslaves()){
										modToRemove = mod;
									}
									break;
								case MAKEPEARLS:
									if (((MonsterInst2)mod).isMakepearls()){
										modToRemove = mod;
									}
									break;
								case BONUSSPELLS:
									if (((MonsterInst2)mod).isBonusspells()){
										modToRemove = mod;
									}
									break;
								case RANDOMSPELL:
									if (((MonsterInst2)mod).isRandomspell()){
										modToRemove = mod;
									}
									break;
								case TAINTED:
									if (((MonsterInst2)mod).isTainted()){
										modToRemove = mod;
									}
									break;
								case FIXFORGEBONUS:
									if (((MonsterInst2)mod).isFixforgebonus()){
										modToRemove = mod;
									}
									break;
								case MASTERSMITH:
									if (((MonsterInst2)mod).isMastersmith()){
										modToRemove = mod;
									}
									break;
								case CROSSBREEDER:
									if (((MonsterInst2)mod).isCrossbreeder()){
										modToRemove = mod;
									}
									break;
								case DEATHBANISH:
									if (((MonsterInst2)mod).isDeathbanish()){
										modToRemove = mod;
									}
									break;
								case KOKYTOSRET:
									if (((MonsterInst2)mod).isKokytosret()){
										modToRemove = mod;
									}
									break;
								case INFERNORET:
									if (((MonsterInst2)mod).isInfernoret()){
										modToRemove = mod;
									}
									break;
								case VOIDRET:
									if (((MonsterInst2)mod).isVoidret()){
										modToRemove = mod;
									}
									break;
								case ALLRET:
									if (((MonsterInst2)mod).isAllret()){
										modToRemove = mod;
									}
									break;
								case CHAOSREC:
									if (((MonsterInst2)mod).isChaosrec()){
										modToRemove = mod;
									}
									break;								}
							}
							if (mod instanceof MonsterInst3) {
								switch (inst2) {
								case MAGICSKILL1:
									if (((MonsterInst3)mod).isMagicskill()){
										magicSkillCount++;
										if (magicSkillCount == 1) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICSKILL2:
									if (((MonsterInst3)mod).isMagicskill()){
										magicSkillCount++;
										if (magicSkillCount == 2) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICSKILL3:
									if (((MonsterInst3)mod).isMagicskill()){
										magicSkillCount++;
										if (magicSkillCount == 3) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICSKILL4:
									if (((MonsterInst3)mod).isMagicskill()){
										magicSkillCount++;
										if (magicSkillCount == 4) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICSKILL5:
									if (((MonsterInst3)mod).isMagicskill()){
										magicSkillCount++;
										if (magicSkillCount == 5) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICSKILL6:
									if (((MonsterInst3)mod).isMagicskill()){
										magicSkillCount++;
										if (magicSkillCount == 6) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICSKILL7:
									if (((MonsterInst3)mod).isMagicskill()){
										magicSkillCount++;
										if (magicSkillCount == 7) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICSKILL8:
									if (((MonsterInst3)mod).isMagicskill()){
										magicSkillCount++;
										if (magicSkillCount == 8) {
											modToRemove = mod;
										}
									}
									break;
								case CUSTOMMAGIC1:
									if (((MonsterInst3)mod).isCustommagic()){
										customMagicCount++;
										if (customMagicCount == 1) {
											modToRemove = mod;
										}
									}
									break;
								case CUSTOMMAGIC2:
									if (((MonsterInst3)mod).isCustommagic()){
										customMagicCount++;
										if (customMagicCount == 2) {
											modToRemove = mod;
										}
									}
									break;
								case CUSTOMMAGIC3:
									if (((MonsterInst3)mod).isCustommagic()){
										customMagicCount++;
										if (customMagicCount == 3) {
											modToRemove = mod;
										}
									}
									break;
								case CUSTOMMAGIC4:
									if (((MonsterInst3)mod).isCustommagic()){
										customMagicCount++;
										if (customMagicCount == 4) {
											modToRemove = mod;
										}
									}
									break;
								case CUSTOMMAGIC5:
									if (((MonsterInst3)mod).isCustommagic()){
										customMagicCount++;
										if (customMagicCount == 5) {
											modToRemove = mod;
										}
									}
									break;
								case CUSTOMMAGIC6:
									if (((MonsterInst3)mod).isCustommagic()){
										customMagicCount++;
										if (customMagicCount == 6) {
											modToRemove = mod;
										}
									}
									break;
								case CUSTOMMAGIC7:
									if (((MonsterInst3)mod).isCustommagic()){
										customMagicCount++;
										if (customMagicCount == 7) {
											modToRemove = mod;
										}
									}
									break;
								case CUSTOMMAGIC8:
									if (((MonsterInst3)mod).isCustommagic()){
										customMagicCount++;
										if (customMagicCount == 8) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICBOOST1:
									if (((MonsterInst3)mod).isMagicboost()){
										boostCount++;
										if (boostCount == 1) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICBOOST2:
									if (((MonsterInst3)mod).isMagicboost()){
										boostCount++;
										if (boostCount == 2) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICBOOST3:
									if (((MonsterInst3)mod).isMagicboost()){
										boostCount++;
										if (boostCount == 3) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICBOOST4:
									if (((MonsterInst3)mod).isMagicboost()){
										boostCount++;
										if (boostCount == 4) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICBOOST5:
									if (((MonsterInst3)mod).isMagicboost()){
										boostCount++;
										if (boostCount == 5) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICBOOST6:
									if (((MonsterInst3)mod).isMagicboost()){
										boostCount++;
										if (boostCount == 6) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICBOOST7:
									if (((MonsterInst3)mod).isMagicboost()){
										boostCount++;
										if (boostCount == 7) {
											modToRemove = mod;
										}
									}
									break;
								case MAGICBOOST8:
									if (((MonsterInst3)mod).isMagicboost()){
										boostCount++;
										if (boostCount == 8) {
											modToRemove = mod;
										}
									}
									break;
								case GEMPROD1:
									if (((MonsterInst3)mod).isGemprod()){
										gemProdCount++;
										if (gemProdCount == 1) {
											modToRemove = mod;
										}
									}
									break;
								case GEMPROD2:
									if (((MonsterInst3)mod).isGemprod()){
										gemProdCount++;
										if (gemProdCount == 2) {
											modToRemove = mod;
										}
									}
									break;
								case GEMPROD3:
									if (((MonsterInst3)mod).isGemprod()){
										gemProdCount++;
										if (gemProdCount == 3) {
											modToRemove = mod;
										}
									}
									break;
								case GEMPROD4:
									if (((MonsterInst3)mod).isGemprod()){
										gemProdCount++;
										if (gemProdCount == 4) {
											modToRemove = mod;
										}
									}
									break;
								case GEMPROD5:
									if (((MonsterInst3)mod).isGemprod()){
										gemProdCount++;
										if (gemProdCount == 5) {
											modToRemove = mod;
										}
									}
									break;
								case GEMPROD6:
									if (((MonsterInst3)mod).isGemprod()){
										gemProdCount++;
										if (gemProdCount == 6) {
											modToRemove = mod;
										}
									}
									break;
								case GEMPROD7:
									if (((MonsterInst3)mod).isGemprod()){
										gemProdCount++;
										if (gemProdCount == 7) {
											modToRemove = mod;
										}
									}
									break;
								case GEMPROD8:
									if (((MonsterInst3)mod).isGemprod()){
										gemProdCount++;
										if (gemProdCount == 8) {
											modToRemove = mod;
										}
									}
									break;
								case SAILING:
									if (((MonsterInst3)mod).isSailing()){
										modToRemove = mod;
									}
									break;
								}
							}
							if (mod instanceof MonsterInst4) {
								switch (inst2) {
								case CLEAR:
									if (((MonsterInst4)mod).isClear()){
										modToRemove = mod;
									}
									break;
								case CLEARMAGIC:
									if (((MonsterInst4)mod).isClearmagic()){
										modToRemove = mod;
									}
									break;
								case CLEARWEAPONS:
									if (((MonsterInst4)mod).isClearweapons()){
										modToRemove = mod;
									}
									break;
								case CLEARARMOR:
									if (((MonsterInst4)mod).isCleararmor()){
										modToRemove = mod;
									}
									break;
								case CLEARSPEC:
									if (((MonsterInst4)mod).isClearspec()){
										modToRemove = mod;
									}
									break;
								case FEMALE:
									if (((MonsterInst4)mod).isFemale()){
										modToRemove = mod;
									}
									break;
								case MOUNTED:
									if (((MonsterInst4)mod).isMounted()){
										modToRemove = mod;
									}
									break;
								case HOLY:
									if (((MonsterInst4)mod).isHoly()){
										modToRemove = mod;
									}
									break;
								case ANIMAL:
									if (((MonsterInst4)mod).isAnimal()){
										modToRemove = mod;
									}
									break;
								case UNDEAD:
									if (((MonsterInst4)mod).isUndead()){
										modToRemove = mod;
									}
									break;
								case DEMON:
									if (((MonsterInst4)mod).isDemon()){
										modToRemove = mod;
									}
									break;
								case MAGICBEING:
									if (((MonsterInst4)mod).isMagicbeing()){
										modToRemove = mod;
									}
									break;
								case STONEBEING:
									if (((MonsterInst4)mod).isStonebeing()){
										modToRemove = mod;
									}
									break;
								case INANIMATE:
									if (((MonsterInst4)mod).isInanimate()){
										modToRemove = mod;
									}
									break;
								case COLDBLOOD:
									if (((MonsterInst4)mod).isColdblood()){
										modToRemove = mod;
									}
									break;
								case IMMORTAL:
									if (((MonsterInst4)mod).isImmortal()){
										modToRemove = mod;
									}
									break;
								case BLIND:
									if (((MonsterInst4)mod).isBlind()){
										modToRemove = mod;
									}
									break;
								case UNIQUE:
									if (((MonsterInst4)mod).isUnique()){
										modToRemove = mod;
									}
									break;
								case IMMOBILE:
									if (((MonsterInst4)mod).isImmobile()){
										modToRemove = mod;
									}
									break;
								case AQUATIC:
									if (((MonsterInst4)mod).isAquatic()){
										modToRemove = mod;
									}
									break;
								case AMPHIBIAN:
									if (((MonsterInst4)mod).isAmphibian()){
										modToRemove = mod;
									}
									break;
								case POORAMPHIBIAN:
									if (((MonsterInst4)mod).isPooramphibian()){
										modToRemove = mod;
									}
									break;
								case FLYING:
									if (((MonsterInst4)mod).isFlying()){
										modToRemove = mod;
									}
									break;
								case STORMIMMUNE:
									if (((MonsterInst4)mod).isStormimmune()){
										modToRemove = mod;
									}
									break;
								case FORESTSURVIVAL:
									if (((MonsterInst4)mod).isForestsurvival()){
										modToRemove = mod;
									}
									break;
								case MOUNTAINSURVIVAL:
									if (((MonsterInst4)mod).isMountainsurvival()){
										modToRemove = mod;
									}
									break;
								case SWAMPSURVIVAL:
									if (((MonsterInst4)mod).isSwampsurvival()){
										modToRemove = mod;
									}
									break;
								case WASTESURVIVAL:
									if (((MonsterInst4)mod).isWastesurvival()){
										modToRemove = mod;
									}
									break;
								case ILLUSION:
									if (((MonsterInst4)mod).isIllusion()){
										modToRemove = mod;
									}
									break;
								case SPY:
									if (((MonsterInst4)mod).isSpy()){
										modToRemove = mod;
									}
									break;
								case ASSASSIN:
									if (((MonsterInst4)mod).isAssassin()){
										modToRemove = mod;
									}
									break;
								case HEAL:
									if (((MonsterInst4)mod).isHeal()){
										modToRemove = mod;
									}
									break;
								case NOHEAL:
									if (((MonsterInst4)mod).isNoheal()){
										modToRemove = mod;
									}
									break;
								case NEEDNOTEAT:
									if (((MonsterInst4)mod).isNeednoteat()){
										modToRemove = mod;
									}
									break;
								case ETHEREAL:
									if (((MonsterInst4)mod).isEthereal()){
										modToRemove = mod;
									}
									break;
								case TRAMPLE:
									if (((MonsterInst4)mod).isTrample()){
										modToRemove = mod;
									}
									break;
								case ENTANGLE:
									if (((MonsterInst4)mod).isEntangle()){
										modToRemove = mod;
									}
									break;
								case EYELOSS:
									if (((MonsterInst4)mod).isEyeloss()){
										modToRemove = mod;
									}
									break;
								case HORRORMARK:
									if (((MonsterInst4)mod).isHorrormark()){
										modToRemove = mod;
									}
									break;
								case POISONARMOR:
									if (((MonsterInst4)mod).isPoisonarmor()){
										modToRemove = mod;
									}
									break;
								case INQUISITOR:
									if (((MonsterInst4)mod).isInquisitor()){
										modToRemove = mod;
									}
									break;
								case NOITEM:
									if (((MonsterInst4)mod).isNoitem()){
										modToRemove = mod;
									}
									break;
								case DRAINIMMUNE:
									if (((MonsterInst4)mod).isDrainimmune()){
										modToRemove = mod;
									}
									break;
								case NOLEADER:
									if (((MonsterInst4)mod).isNoleader()){
										modToRemove = mod;
									}
									break;
								case POORLEADER:
									if (((MonsterInst4)mod).isPoorleader()){
										modToRemove = mod;
									}
									break;
								case OKLEADER:
									if (((MonsterInst4)mod).isOkleader()){
										modToRemove = mod;
									}
									break;
								case GOODLEADER:
									if (((MonsterInst4)mod).isGoodleader()){
										modToRemove = mod;
									}
									break;
								case EXPERTLEADER:
									if (((MonsterInst4)mod).isExpertleader()){
										modToRemove = mod;
									}
									break;
								case SUPERIORLEADER:
									if (((MonsterInst4)mod).isSuperiorleader()){
										modToRemove = mod;
									}
									break;
								case NOMAGICLEADER:
									if (((MonsterInst4)mod).isNomagicleader()){
										modToRemove = mod;
									}
									break;
								case POORMAGICLEADER:
									if (((MonsterInst4)mod).isPoormagicleader()){
										modToRemove = mod;
									}
									break;
								case OKMAGICLEADER:
									if (((MonsterInst4)mod).isOkmagicleader()){
										modToRemove = mod;
									}
									break;
								case GOODMAGICLEADER:
									if (((MonsterInst4)mod).isGoodmagicleader()){
										modToRemove = mod;
									}
									break;
								case EXPERTMAGICLEADER:
									if (((MonsterInst4)mod).isExpertmagicleader()){
										modToRemove = mod;
									}
									break;
								case SUPERIORMAGICLEADER:
									if (((MonsterInst4)mod).isSuperiormagicleader()){
										modToRemove = mod;
									}
									break;
								case NOUNDEADLEADER:
									if (((MonsterInst4)mod).isNoundeadleader()){
										modToRemove = mod;
									}
									break;
								case POORUNDEADLEADER:
									if (((MonsterInst4)mod).isPoorundeadleader()){
										modToRemove = mod;
									}
									break;
								case OKUNDEADLEADER:
									if (((MonsterInst4)mod).isOkundeadleader()){
										modToRemove = mod;
									}
									break;
								case GOODUNDEADLEADER:
									if (((MonsterInst4)mod).isGoodundeadleader()){
										modToRemove = mod;
									}
									break;
								case EXPERTUNDEADLEADER:
									if (((MonsterInst4)mod).isExpertundeadleader()){
										modToRemove = mod;
									}
									break;
								case SUPERIORUNDEADLEADER:
									if (((MonsterInst4)mod).isSuperiorundeadleader()){
										modToRemove = mod;
									}
									break;
								case SLOWREC:
									if (((MonsterInst4)mod).isSlowrec()){
										modToRemove = mod;
									}
									break;
								case NOSLOWREC:
									if (((MonsterInst4)mod).isNoslowrec()){
										modToRemove = mod;
									}
									break;
								case REQLAB:
									if (((MonsterInst4)mod).isReqlab()){
										modToRemove = mod;
									}
									break;
								case REQTEMPLE:
									if (((MonsterInst4)mod).isReqtemple()){
										modToRemove = mod;
									}
									break;
								case SINGLEBATTLE:
									if (((MonsterInst4)mod).isSinglebattle()){
										modToRemove = mod;
									}
									break;
								case AISINGLEREC:
									if (((MonsterInst4)mod).isAisinglerec()){
										modToRemove = mod;
									}
									break;
								case AINOREC:
									if (((MonsterInst4)mod).isAinorec()){
										modToRemove = mod;
									}
									break;
								case LESSERHORROR:
									if (((MonsterInst4)mod).isLesserhorror()){
										modToRemove = mod;
									}
									break;
								case GREATERHORROR:
									if (((MonsterInst4)mod).isGreaterhorror()){
										modToRemove = mod;
									}
									break;
								case DOOMHORROR:
									if (((MonsterInst4)mod).isDoomhorror()){
										modToRemove = mod;
									}
									break;
								case BUG:
									if (((MonsterInst4)mod).isBug()){
										modToRemove = mod;
									}
									break;
								case UWBUG:
									if (((MonsterInst4)mod).isUwbug()){
										modToRemove = mod;
									}
									break;
								case AUTOCOMPETE:
									if (((MonsterInst4)mod).isAutocompete()){
										modToRemove = mod;
									}
									break;
								case FLOAT:
									if (((MonsterInst4)mod).isFloat()){
										modToRemove = mod;
									}
									break;
								case TELEPORT:
									if (((MonsterInst4)mod).isTeleport()){
										modToRemove = mod;
									}
									break;
								case NORIVERPASS:
									if (((MonsterInst4)mod).isNoriverpass()){
										modToRemove = mod;
									}
									break;
								case UNTELEPORTABLE:
									if (((MonsterInst4)mod).isUnteleportable()){
										modToRemove = mod;
									}
									break;
								case HPOVERFLOW:
									if (((MonsterInst4)mod).isHpoverflow()){
										modToRemove = mod;
									}
									break;
								case PIERCERES:
									if (((MonsterInst4)mod).isPierceres()){
										modToRemove = mod;
									}
									break;
								case SLASHRES:
									if (((MonsterInst4)mod).isSlashres()){
										modToRemove = mod;
									}
									break;
								case BLUNTRES:
									if (((MonsterInst4)mod).isBluntres()){
										modToRemove = mod;
									}
									break;
								case DEATHCURSE:
									if (((MonsterInst4)mod).isDeathcurse()){
										modToRemove = mod;
									}
									break;
								case TRAMPSWALLOW:
									if (((MonsterInst4)mod).isTrampswallow()){
										modToRemove = mod;
									}
									break;
								case TAXCOLLECTOR:
									if (((MonsterInst4)mod).isTaxcollector()){
										modToRemove = mod;
									}
									break;
								case NOHOF:
									if (((MonsterInst4)mod).isNohof()){
										modToRemove = mod;
									}
									break;
								case CLEANSHAPE:
									if (((MonsterInst4)mod).isCleanshape()){
										modToRemove = mod;
									}
									break;
								case SLAVE:
									if (((MonsterInst4)mod).isSlave()){
										modToRemove = mod;
									}
									break;
								case UNDISCIPLINED:
									if (((MonsterInst4)mod).isUndisciplined()){
										modToRemove = mod;
									}
									break;
								case MAGICIMMUNE:
									if (((MonsterInst4)mod).isMagicimmune()){
										modToRemove = mod;
									}
									break;
								case COMSLAVE:
									if (((MonsterInst4)mod).isComslave()){
										modToRemove = mod;
									}
									break;
								}
							}
							if (mod instanceof MonsterInst5) {
								switch (inst2) {
								case WEAPON1:
									if (((MonsterInst5)mod).isWeapon()){
										weaponCount++;
										if (weaponCount == 1) {
											modToRemove = mod;
										}
									}
									break;
								case WEAPON2:
									if (((MonsterInst5)mod).isWeapon()){
										weaponCount++;
										if (weaponCount == 2) {
											modToRemove = mod;
										}
									}
									break;
								case WEAPON3:
									if (((MonsterInst5)mod).isWeapon()){
										weaponCount++;
										if (weaponCount == 3) {
											modToRemove = mod;
										}
									}
									break;
								case WEAPON4:
									if (((MonsterInst5)mod).isWeapon()){
										weaponCount++;
										if (weaponCount == 4) {
											modToRemove = mod;
										}
									}
									break;
								case ARMOR1:
									if (((MonsterInst5)mod).isArmor()){
										armorCount++;
										if (armorCount == 1) {
											modToRemove = mod;
										}
									}
									break;						
								case ARMOR2:
									if (((MonsterInst5)mod).isArmor()){
										armorCount++;
										if (armorCount == 2) {
											modToRemove = mod;
										}
									}
									break;						
								case ARMOR3:
									if (((MonsterInst5)mod).isArmor()){
										armorCount++;
										if (armorCount == 3) {
											modToRemove = mod;
										}
									}
									break;						
								case ONEBATTLESPELL:
									if (((MonsterInst5)mod).isOnebattlespell()){
										modToRemove = mod;
									}
									break;
								case FIRSTSHAPE:
									if (((MonsterInst5)mod).isFirstshape()){
										modToRemove = mod;
									}
									break;
								case SECONDSHAPE:
									if (((MonsterInst5)mod).isSecondshape()){
										modToRemove = mod;
									}
									break;
								case SECONDTMPSHAPE:
									if (((MonsterInst5)mod).isSecondtmpshape()){
										modToRemove = mod;
									}
									break;
								case SHAPECHANGE:
									if (((MonsterInst5)mod).isShapechange()){
										modToRemove = mod;
									}
									break;
								case LANDSHAPE:
									if (((MonsterInst5)mod).isLandshape()){
										modToRemove = mod;
									}
									break;
								case WATERSHAPE:
									if (((MonsterInst5)mod).isWatershape()){
										modToRemove = mod;
									}
									break;
								case FORESTSHAPE:
									if (((MonsterInst5)mod).isForestshape()){
										modToRemove = mod;
									}
									break;
								case PLAINSHAPE:
									if (((MonsterInst5)mod).isPlainshape()){
										modToRemove = mod;
									}
									break;
								case DOMSUMMON:
									if (((MonsterInst5)mod).isDomsummon()){
										modToRemove = mod;
									}
									break;
								case DOMSUMMON2:
									if (((MonsterInst5)mod).isDomsummon2()){
										modToRemove = mod;
									}
									break;
								case DOMSUMMON20:
									if (((MonsterInst5)mod).isDomsummon20()){
										modToRemove = mod;
									}
									break;
								case MAKEMONSTERS1:
									if (((MonsterInst5)mod).isMakemonsters1()){
										modToRemove = mod;
									}
									break;
								case MAKEMONSTERS2:
									if (((MonsterInst5)mod).isMakemonsters2()){
										modToRemove = mod;
									}
									break;
								case MAKEMONSTERS3:
									if (((MonsterInst5)mod).isMakemonsters3()){
										modToRemove = mod;
									}
									break;
								case MAKEMONSTERS4:
									if (((MonsterInst5)mod).isMakemonsters4()){
										modToRemove = mod;
									}
									break;
								case MAKEMONSTERS5:
									if (((MonsterInst5)mod).isMakemonsters5()){
										modToRemove = mod;
									}
									break;
								case SUMMON1:
									if (((MonsterInst5)mod).isSummon1()){
										modToRemove = mod;
									}
									break;
								case SUMMON2:
									if (((MonsterInst5)mod).isSummon2()){
										modToRemove = mod;
									}
									break;
								case SUMMON3:
									if (((MonsterInst5)mod).isSummon3()){
										modToRemove = mod;
									}
									break;
								case SUMMON4:
									if (((MonsterInst5)mod).isSummon4()){
										modToRemove = mod;
									}
									break;
								case SUMMON5:
									if (((MonsterInst5)mod).isSummon5()){
										modToRemove = mod;
									}
									break;
								case RAREDOMSUMMON:
									if (((MonsterInst5)mod).isRaredomsummon()){
										modToRemove = mod;
									}
									break;
								case BATTLESUM1:
									if (((MonsterInst5)mod).isBattlesum1()){
										modToRemove = mod;
									}
									break;
								case BATTLESUM2:
									if (((MonsterInst5)mod).isBattlesum2()){
										modToRemove = mod;
									}
									break;
								case BATTLESUM3:
									if (((MonsterInst5)mod).isBattlesum3()){
										modToRemove = mod;
									}
									break;
								case BATTLESUM4:
									if (((MonsterInst5)mod).isBattlesum4()){
										modToRemove = mod;
									}
									break;
								case BATTLESUM5:
									if (((MonsterInst5)mod).isBattlesum5()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM1:
									if (((MonsterInst5)mod).isBatstartsum1()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM2:
									if (((MonsterInst5)mod).isBatstartsum2()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM3:
									if (((MonsterInst5)mod).isBatstartsum3()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM4:
									if (((MonsterInst5)mod).isBatstartsum4()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM5:
									if (((MonsterInst5)mod).isBatstartsum5()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM1D6:
									if (((MonsterInst5)mod).isBatstartsum1d6()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM2D6:
									if (((MonsterInst5)mod).isBatstartsum2d6()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM3D6:
									if (((MonsterInst5)mod).isBatstartsum3d6()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM4D6:
									if (((MonsterInst5)mod).isBatstartsum4d6()){
										modToRemove = mod;
									}
									break;
								case BATSTARTSUM5D6:
									if (((MonsterInst5)mod).isBatstartsum5d6()){
										modToRemove = mod;
									}
									break;
								}
							}
							if (mod instanceof MonsterInst6) {
								switch (inst2) {
								case HEAT:
									if (((MonsterInst6)mod).isHeat()){
										modToRemove = mod;
									}
									break;
								case COLD:
									if (((MonsterInst6)mod).isCold()){
										modToRemove = mod;
									}
									break;
								case STEALTHY:
									if (((MonsterInst6)mod).isStealthy()){
										modToRemove = mod;
									}
									break;
								}
							}
							if (modToRemove != null) {
								break;
							}
						}
						if (modToRemove != null) {
							mods.remove(modToRemove);
						}
					}  
				});
			}
		});
		updateSelection();
	}
}
