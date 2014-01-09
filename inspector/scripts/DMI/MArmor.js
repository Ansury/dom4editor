//namespace scope
(function( DMI, $, undefined ){
		
var MArmor = DMI.MArmor = DMI.MArmor || {};

var Format = DMI.Format;
var Utils = DMI.Utils;
var modctx = DMI.modctx;
var modconstants = DMI.modconstants;

//////////////////////////////////////////////////////////////////////////
// PREPARE DATA
//////////////////////////////////////////////////////////////////////////

MArmor.initArmor = function(o) {
	o.type = 5;
	o.used_by = [];
}

MArmor.prepareData_PreMod = function() {
	for (var oi=0, o; o= modctx.armordata[oi]; oi++) {
		o.used_by = [];
	}
}

MArmor.prepareData_PostMod = function() {
	for (var oi=0, o; o= modctx.armordata[oi]; oi++) {
		o.id = parseInt(o.id);

		for (var oi2=0, o2; o2 = modctx.protections_by_armor[oi2]; oi2++) {
			var o2id = parseInt(o2.armor_number);
			if (o2id == o.id) {
				if (parseInt(o2.zone_number) == 1 || 
					parseInt(o2.zone_number) == 2 || 
					parseInt(o2.zone_number) == 5 || 
					parseInt(o2.zone_number) == 6) {
					o.prot = o2.protection;
					break;
				}
			}
		}
		
		o.renderOverlay = MArmor.renderOverlay;
		o.matchProperty = MArmor.matchProperty;

		//serachable string
		o.searchable = o.name.toLowerCase();
		
		o.type = {4:'shield', 5:'armor', 6:'helm', 8:'misc'}[o.type];
		
		if (o.type=="shield") {
			if (o.prot) {
				o.protshield = o.prot;
				delete o.prot;
			}
			o.parry = parseInt(o.def) + parseInt(o.enc);
			o.def = Utils.negative(o.enc);
		}
		else if (o.type=="helm" && o.prot) {
			o.prothead = o.prot;
			delete o.prot;
		}
		else if (o.type=="armor" && o.prot) {
			o.protbody = o.prot;
			delete o.prot;
		}
	}
}
		
//////////////////////////////////////////////////////////////////////////
// DEFINE GRID
//////////////////////////////////////////////////////////////////////////

MArmor.CGrid = Utils.Class( DMI.CGrid, function() {
	//grid columns
	var columns = [
		{ id: "name",     width: 145, name: "Armor Name", field: "name", sortable: true },
		{ id: "type",     width: 60, name: "Type", field: "type", sortable: true }
	];
	this.superClass.call(this, 'armor', modctx.armordata, columns); //superconstructor
	
	$(this.domsel+' .grid-container').css('width', 530);//set table width

	
	//in closure scope
	var that = this;


	//reads search boxes
	this.getSearchArgs = function() {
		var args = Utils.merge(this.getPropertyMatchArgs(), {
			str: $(that.domselp+" input.search-box").val().toLowerCase()
		});
		return args;
	}
	//apply search
	this.searchFilter =  function(o, args) {
		//type in id to ignore filters
		if (args.str && args.str == String(o.id)) return true;
		
		//search string
		if (args.str && o.searchable.indexOf(args.str) == -1)
			return false;

		//key =~ val
		if (args.key) {
			var r = o.matchProperty(o, args.key, args.comp, args.val);
			if (args.not  ?  r  :  !r)
				return false;
		}
		return true;
	}

	//final init
	this.init();
});
MArmor.matchProperty = DMI.matchProperty;

		
//////////////////////////////////////////////////////////////////////////
// OVERLAY RENDERING
//////////////////////////////////////////////////////////////////////////

var aliases = {};
var formats = {};
var displayorder = DMI.Utils.cutDisplayOrder(aliases, formats,
[
	'prot',		'basic protection',
	'protbody',	'protection, body',
	'prothead',	'protection, head',
	'protshield',	'protection, shield',
	'def',		'defence',		Format.Signed,
	'parry',	'parry',
	'enc',		'encumbrance'
]);

var flagorder = DMI.Utils.cutDisplayOrder(aliases, formats,
[
//	dbase key	displayed key		function/dict to format value
]);
var hiddenkeys = DMI.Utils.cutDisplayOrder(aliases, formats,
[
	'id', 		'armor id',	function(v,o){ return v + ' ('+o.name+')'; },
	'rcost',	'resource cost'
]);
var ignorekeys = {
	used_by:1,
	name:1,
	type:1,
	searchable:1,renderOverlay:1, matchProperty:1
};
	

MArmor.renderOverlay = function(o, baseAtt) {
	//template
	var h=''
	h+='<div class="armor overlay-contents"> ';
	
	var slot = { shield:'1 hand', armor:'body', helm:'head', misc:'misc' }[o.type];
	
	//header
	h+='	<div class="overlay-header" title="armor id: '+o.id+'"> ';
	h+='		<p style="float:right; height:0px;">'+slot+'</p>';
	h+='		<h2>'+o.name+'</h2> ';
	h+='	</div>';
	
	//mid
	h+='	<div class="overlay-main">';
	h+=' 		<input class="overlay-pin" type="image" src="images/PinPageTrns.png" title="unpin" />';
	
	h+='		<table class="overlay-table armor-table"> ';
	h+= 			Utils.renderDetailsRows(o, hiddenkeys, aliases, formats, 'hidden-row');
	h+= 			Utils.renderDetailsRows(o, displayorder, aliases, formats);
	h+= 			Utils.renderDetailsFlags(o, flagorder, aliases, formats);
	h+= 			Utils.renderStrangeDetailsRows(o, ignorekeys, aliases, 'strange');
	
	if (o.modded) {
		h+='	<tr class="modded hidden-row"><td colspan="2">Modded<span class="internal-inline"> [modded]</span>:<br />';
		h+=		o.modded.replace('ERROR:', '<span style="color:red;font-weight:bold;">ERROR:</span>');
		h+='	</td></tr>';
	}
	h+='		</table> ';		
	h+='	</div>';
	
	//footer
	if (o.used_by.length) {
		h+='<div class="overlay-footer">';
		if (o.used_by.length > 8) {
			//hide uberlong list
			h+='	<p class="firstline">';
			h+='		Used by: '+o.used_by.length+' things ';
			
			//button to reveal
			var codereveal = "$(this).parent('p').hide().parent('div').find('.full-list').show()"
			h+='<input class="inline-button" style="padding:none" type="button" value="show" onclick="'+codereveal+'"/>';
			h+='	</p>';
		
			//the actual list
			h+='	<p class="firstline full-list" style="display:none">';
			h+='		Used by: '+ o.used_by.join(', ');
			h+='	</p>';
		} else {
			h+='	<p class="firstline">';
			h+='		Used by: '+ o.used_by.join(', ');
			h+='	</p">';
		}
		h+='</div> ';
	}
	
	h+='</div> ';
	return h;	
}

//namespace args
}( window.DMI = window.DMI || {}, jQuery ));
