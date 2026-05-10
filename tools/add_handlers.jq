# Maps screenId → handler name, applied to each screen in .screens[]
# Usage: jq -f tools/add_handlers.jq resources-src/config.json > tmp && mv tmp resources-src/config.json

def handler_for_screen:
  # screenId-based mapping
  {
    "9":"AccountHandler","10":"AccountHandler","11":"AccountHandler","63":"AccountHandler",
    "55":"AccountHandler","44":"AccountHandler","114":"AccountHandler","127":"AccountHandler",
    "128":"AccountHandler","64":"AccountHandler","14":"AccountHandler","70":"AccountHandler",
    "15":"AccountHandler","42":"AccountHandler","43":"AccountHandler","159":"AccountHandler",
    "172":"AccountHandler","169":"AccountHandler",
    "1":"SettingsHandler","8":"SettingsHandler","20":"SettingsHandler","21":"SettingsHandler",
    "52":"SettingsHandler","53":"SettingsHandler","51":"SettingsHandler","54":"SettingsHandler",
    "56":"SettingsHandler","91":"SettingsHandler","62":"SettingsHandler","132":"SettingsHandler",
    "140":"SettingsHandler","58":"SettingsHandler","152":"SettingsHandler","57":"SettingsHandler",
    "111":"SettingsHandler","175":"SettingsHandler",
    "108":"ContactHandler","60":"ContactHandler","47":"ContactHandler","48":"ContactHandler",
    "31":"ContactHandler","32":"ContactHandler","35":"ContactHandler","33":"ContactHandler",
    "30":"ContactHandler","36":"ContactHandler","39":"ContactHandler","37":"ContactHandler",
    "49":"ContactHandler","50":"ContactHandler","41":"ContactHandler","40":"ContactHandler",
    "65":"ContactHandler","109":"ContactHandler","110":"ContactHandler","113":"ContactHandler",
    "112":"ContactHandler","67":"ContactHandler","71":"ContactHandler","155":"ContactHandler",
    "142":"ContactHandler","34":"ContactHandler","143":"ContactHandler","176":"ContactHandler",
    "16":"ChatHandler","17":"ChatHandler","18":"ChatHandler","19":"ChatHandler","22":"ChatHandler",
    "115":"ChatHandler","23":"ChatHandler","24":"ChatHandler","25":"ChatHandler","26":"ChatHandler",
    "27":"ChatHandler","28":"ChatHandler","116":"ChatHandler","162":"ChatHandler","38":"ChatHandler",
    "166":"ChatHandler",
    "3":"MessageHandler","4":"MessageHandler","66":"MessageHandler",
    "46":"MessageHandler","45":"MessageHandler","5":"MessageHandler","68":"MessageHandler",
    "29":"MessageHandler","161":"MessageHandler","13":"MessageHandler","69":"MessageHandler",
    "61":"MessageHandler","73":"MessageHandler","72":"MessageHandler","167":"MessageHandler",
    "6":"MapHandler","7":"MapHandler","59":"MapHandler","105":"MapHandler","106":"MapHandler",
    "100":"MapHandler","101":"MapHandler","117":"MapHandler","102":"MapHandler","158":"MapHandler",
    "174":"MapHandler","103":"MapHandler","154":"MapHandler","104":"MapHandler",
    "74":"ProfileHandler","75":"ProfileHandler","160":"ProfileHandler","78":"ProfileHandler",
    "79":"ProfileHandler","80":"ProfileHandler","76":"ProfileHandler","77":"ProfileHandler",
    "177":"ProfileHandler",
    "81":"DialogHandler","82":"DialogHandler","83":"DialogHandler","84":"DialogHandler",
    "85":"DialogHandler","86":"DialogHandler","87":"DialogHandler","88":"DialogHandler",
    "89":"DialogHandler","90":"DialogHandler","92":"DialogHandler","93":"DialogHandler",
    "94":"DialogHandler","95":"DialogHandler","96":"DialogHandler","97":"DialogHandler",
    "98":"DialogHandler","99":"DialogHandler","107":"DialogHandler","119":"DialogHandler",
    "120":"DialogHandler","121":"DialogHandler","130":"DialogHandler",
    "118":"MiscHandler","122":"MiscHandler","123":"MiscHandler","124":"MiscHandler",
    "125":"MiscHandler","126":"MiscHandler","129":"MiscHandler","131":"MiscHandler",
    "133":"MiscHandler","134":"MiscHandler","135":"MiscHandler","136":"MiscHandler",
    "153":"MiscHandler","144":"MiscHandler","145":"MiscHandler","146":"MiscHandler",
    "147":"MiscHandler","148":"MiscHandler","149":"MiscHandler","137":"MiscHandler",
    "150":"MiscHandler","151":"MiscHandler","156":"MiscHandler","157":"MiscHandler",
    "170":"MiscHandler","178":"MiscHandler","164":"MiscHandler","168":"MiscHandler","138":"MiscHandler"
  } as $by_id |
  # name-based fallback for screenId=0
  {
    "CONTACT_ACTIONS_MENU":"ContactHandler","CONTACT_MENU":"ContactHandler",
    "ERROR_ALERT":"DialogHandler","CONFIRM_DIALOG":"DialogHandler",
    "SETTINGS_MAIN":"MiscHandler"
  } as $by_name |
  ($by_id[.screenId | tostring] // $by_name[.name] // null);

.screens |= [.[] | . + (
  handler_for_screen as $h |
  if $h then {"handler": $h} else {} end
)]
