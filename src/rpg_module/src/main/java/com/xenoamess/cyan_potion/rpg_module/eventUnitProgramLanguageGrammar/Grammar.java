package com.xenoamess.cyan_potion.rpg_module.eventUnitProgramLanguageGrammar;

/**
 * @author XenoAmess
 */
public class Grammar {
    private Grammar() {
    }

    //Coding is so hard that I do hard coding
    //;)

    //408 (which is a green color "undefined") might have some special usage
    // and we just didn't find it yet.
    //605 (which is a blue color ":") might have some special usage and we
    // just didn't find it yet.
    //655 (which is a gray color "undefined") might have some special usage
    // and we just didn't find it yet.


    //Part : Pass begin

    /**
     * do nothing,just leave a empty line there(just like pass in python.)
     */
    public static final int G_PASS = 0;

    //Part : Pass end


    //Part : Message begin

    public static final int G_SHOW_TEXT = 101;

    /**
     * ShowText_PatternSetting
     */
    public static final int G_SHOW_TEXT_0 = 101;

    /**
     * ShowText_ContentSetting
     */
    public static final int G_SHOW_TEXT_1 = 401;


    /**
     * ShowChoices_BasicSetting
     */
    public static final int G_SHOW_CHOICES_0 = 102;

    /**
     * ShowChoices_IfChooseOneChoiceThen
     */
    public static final int G_SHOW_CHOICES_1 = 402;

    /**
     * ShowChoices_CancelChoicesThen
     */
    public static final int G_SHOW_CHOICES_2 = 403;

    /**
     * ShowChoices_EndShowChoices
     */
    public static final int G_SHOW_CHOICES_3 = 404;


    public static final int G_INPUT_NUMBER = 103;
    public static final int G_INPUT_NUMBER_0 = 103;


    public static final int G_SELECT_ITEM = 104;
    public static final int G_SELECT_ITEM_0 = 104;


    public static final int G_SHOW_SCROLLING_TEXT_0 = 105;
    public static final int G_SHOW_SCROLLING_TEXT_1 = 405;

    //Part : Message end


    //Part : Game Progress begin

    public static final int G_CONTROL_SWITCHES = 121;
    public static final int G_CONTROL_SWITCHES_0 = 121;


    public static final int G_CONTROL_VARIABLES = 122;
    public static final int G_CONTROL_VARIABLES_0 = 122;


    public static final int G_CONTROL_SELF_SWITCH = 123;
    public static final int G_CONTROL_SELF_SWITCH_0 = 123;


    public static final int G_CONTROL_TIMER = 124;
    public static final int G_CONTROL_TIMER_0 = 124;

    //Part : Game Progress end


    //Part : Flow Control begin

    /**
     * if
     */
    public static final int G_CONDITIONAL_BRANCH_0 = 111;

    /**
     * else (conditional)
     */
    public static final int G_CONDITIONAL_BRANCH_1 = 411;

    /**
     * endif
     */
    public static final int G_CONDITIONAL_BRANCH_2 = 412;


    /**
     * loop,or say, while(1)
     */
    public static final int G_LOOP_0 = 112;

    /**
     * end loop
     */
    public static final int G_LOOP_1 = 413;


    /**
     * break loop,or say,break;
     */
    public static final int G_BREAK_LOOP = 113;
    public static final int G_BREAK_LOOP_0 = 113;

    /**
     * (abnormal) continue loop,or say,continue;
     */
    public static final int G_CONTINUE_LOOP = 114;
    public static final int G_CONTINUE_LOOP_0 = 114;


    public static final int G_EXIT_EVENT_PROCESSING = 115;
    public static final int G_EXIT_EVENT_PROCESSING_0 = 115;


    public static final int G_COMMON_EVENT = 117;
    public static final int G_COMMON_EVENT_0 = 117;


    public static final int G_LABEL = 118;
    public static final int G_LABEL_0 = 118;


    public static final int G_JUMP_TO_LABEL = 119;
    public static final int G_JUMP_TO_LABEL_0 = 119;


    public static final int G_COMMENT = 108;
    public static final int G_COMMENT_0 = 108;

    //Part : Flow Control end


    //Part : Party begin

    /**
     * positive change
     */
    public static final int G_CHANGE_GOLD_0 = 108;

    /**
     * negative change
     */
    public static final int G_CHANGE_GOLD_1 = 125;

    public static final int G_CHANGE_ITEMS = 126;
    public static final int G_CHANGE_ITEMS_0 = 126;


    public static final int G_CHANGE_WEAPONS = 127;
    public static final int G_CHANGE_WEAPONS_0 = 127;


    public static final int G_CHANGE_ARMORS = 128;
    public static final int G_CHANGE_ARMORS_0 = 128;


    public static final int G_CHANGE_PARTY_MEMBER = 129;
    public static final int G_CHANGE_PARTY_MEMBER_0 = 129;

    //Part : Party end


    //Part : Actor begin

    public static final int G_CHANGE_HP = 311;
    public static final int G_CHANGE_HP_0 = 311;


    public static final int G_CHANGE_MP = 312;
    public static final int G_CHANGE_MP_0 = 312;


    public static final int G_CHANGE_TP = 326;
    public static final int G_CHANGE_TP_0 = 326;


    public static final int G_CHANGE_STATE = 313;
    public static final int G_CHANGE_STATE_0 = 313;


    public static final int G_RECOVER_ALL = 314;
    public static final int G_RECOVER_ALL_0 = 314;


    public static final int G_CHANGE_EXP = 315;
    public static final int G_CHANGE_EXP_0 = 315;


    public static final int G_CHANGE_LEVEL = 316;
    public static final int G_CHANGE_LEVEL_0 = 316;


    public static final int G_CHANGE_PARAMETER = 317;
    public static final int G_CHANGE_PARAMETER_0 = 317;


    public static final int G_CHANGE_SKILL = 318;
    public static final int G_CHANGE_SKILL_0 = 318;


    public static final int G_CHANGE_EQUIPMENT = 319;
    public static final int G_CHANGE_EQUIPMENT_0 = 319;


    public static final int G_CHANGE_NAME = 320;
    public static final int G_CHANGE_NAME_0 = 320;


    public static final int G_CHANGE_CLASS = 321;
    public static final int G_CHANGE_CLASS_0 = 321;


    public static final int G_CHANGE_NICKNAME = 324;
    public static final int G_CHANGE_NICKNAME_0 = 324;


    public static final int G_CHANGE_PROFILE = 325;
    public static final int G_CHANGE_PROFILE_0 = 325;

    //Part : Actor end


    //Part : Movement begin

    public static final int G_TRANSFER_PLAYER = 201;
    public static final int G_TRANSFER_PLAYER_0 = 201;


    public static final int G_SET_VEHICLE_LOCATION = 202;
    public static final int G_SET_VEHICLE_LOCATION_0 = 202;


    public static final int G_SET_EVENT_LOCATION = 203;
    public static final int G_SET_EVENT_LOCATION_0 = 203;


    public static final int G_SCROLL_MAP = 204;
    public static final int G_SCROLL_MAP_0 = 204;

    /**
     * SetMovementRoute_WholeRoute
     */
    public static final int G_SET_MOVEMENT_ROUTE_0 = 205;

    /**
     * SetMovementRoute_Contents
     */
    public static final int G_SET_MOVEMENT_ROUTE_1 = 505;


    /**
     * SetMovementRoute_WholeRoute
     */
    public static final int G_GET_ON_OFF_VEHICLE = 206;

    /**
     * SetMovementRoute_Contents
     */
    public static final int G_GET_ON_OFF_VEHICLE_0 = 206;

    //Part : Movement end


    //Part : Character begin

    public static final int G_CHANGE_TRANSPARENCY = 211;
    public static final int G_CHANGE_TRANSPARENCY_0 = 211;

    public static final int G_CHANGE_PLAYER_FOLLOWERS = 216;
    public static final int G_CHANGE_PLAYER_FOLLOWERS_0 = 216;

    public static final int G_GATHER_FOLLOWERS = 217;
    public static final int G_GATHER_FOLLOWERS_0 = 217;

    public static final int G_SHOW_ANIMATION = 212;
    public static final int G_SHOW_ANIMATION_0 = 212;

    public static final int G_SHOW_BALLOON_ICON = 213;
    public static final int G_SHOW_BALLOON_ICON_0 = 213;

    public static final int G_ERASE_EVENT = 214;
    public static final int G_ERASE_EVENT_0 = 214;

    //Part : Character end

    //Part : Picture begin

    public static final int G_SHOW_PICTURE = 231;
    public static final int G_SHOW_PICTURE_0 = 231;

    public static final int G_MOVE_PICTURE = 232;
    public static final int G_MOVE_PICTURE_0 = 232;

    public static final int G_ROTATE_PICTURE = 233;
    public static final int G_ROTATE_PICTURE_0 = 233;

    public static final int G_TINT_PICTURE = 234;
    public static final int G_TINT_PICTURE_0 = 234;

    public static final int G_ERASE_PICTURE = 235;
    public static final int G_ERASE_PICTURE_0 = 235;

    //Part : Picture end


    //Part : Timing begin

    public static final int G_WAIT = 230;
    public static final int G_WAIT_0 = 230;

    //Part : Timing end

    //Part : Screen begin

    public static final int G_FADEOUT_SCREEN = 221;
    public static final int G_FADEOUT_SCREEN_0 = 221;

    public static final int G_FADEIN_SCREEN = 222;
    public static final int G_FADEIN_SCREEN_0 = 222;

    public static final int G_TINT_SCREEN = 223;
    public static final int G_TINT_SCREEN_0 = 223;

    public static final int G_FLASH_SCREEN = 224;
    public static final int G_FLASH_SCREEN_0 = 224;

    public static final int G_SHAKE_SCREEN = 225;
    public static final int G_SHAKE_SCREEN_0 = 225;

    public static final int G_SET_WEATHER_EFFECT = 236;
    public static final int G_SET_WEATHER_EFFECT_0 = 236;

    //Part : Screen end


    //Part : Audio & Video begin

    public static final int G_PLAY_BGM = 241;
    public static final int G_PLAY_BGM_0 = 241;

    public static final int G_FADEOUT_BGM = 242;
    public static final int G_FADEOUT_BGM_0 = 242;

    public static final int G_SAVE_BGM = 243;
    public static final int G_SAVE_BGM_0 = 243;

    public static final int G_REPLAY_BGM = 244;
    public static final int G_REPLAY_BGM_0 = 244;

    public static final int G_PLAYBGS = 245;
    public static final int G_PLAYBGS_0 = 245;

    public static final int G_FADEOUT_BGS = 246;
    public static final int G_FADEOUT_BGS_0 = 246;

    public static final int G_PLAYME = 249;
    public static final int G_PLAYME_0 = 249;

    public static final int G_PLAYSE = 250;
    public static final int G_PLAYSE_0 = 250;

    public static final int G_STOPSE = 251;
    public static final int G_STOPSE_0 = 251;

    public static final int G_PLAY_MOVIE = 261;
    public static final int G_PLAY_MOVIE_0 = 261;

    //Part : Audio & Video end


    //Part : AbstractScene Control begin

    public static final int G_BATTLE_PROCESSING_0 = 301;

    /**
     * If Win
     */
    public static final int G_BATTLE_PROCESSING_1 = 601;

    /**
     * If Escape
     */
    public static final int G_BATTLE_PROCESSING_2 = 602;

    /**
     * If Lose
     */
    public static final int G_BATTLE_PROCESSING_3 = 603;

    /**
     * End If
     */
    public static final int G_BATTLE_PROCESSING_4 = 604;

    public static final int G_SHOP_PROCESSING = 302;
    public static final int G_SHOP_PROCESSING_0 = 302;

    private static final int G_NAME_INPUT_PROCESSING = 303;
    public static final int G_NAME_INPUT_PROCESSING_0 = 303;

    public static final int G_OPEN_MENU_SCREEN = 351;
    public static final int G_OPEN_MENU_SCREEN_0 = 351;

    public static final int G_OPEN_SAVE_SCREEN = 352;
    public static final int G_OPEN_SAVE_SCREEN_0 = 352;

    public static final int G_GAME_OVER = 353;
    public static final int G_GAME_OVER_0 = 353;

    public static final int G_RETURN_TO_TITLE_SCREEN = 354;
    public static final int G_RETURN_TO_TITLE_SCREEN_0 = 354;

    //Part : AbstractScene Control end


    //Part : System Settings begin

    public static final int G_CHANGE_BATTLE_BGM = 132;
    public static final int G_CHANGE_BATTLE_BGM_0 = 132;

    public static final int G_CHANGE_VICTORY_ME = 133;
    public static final int G_CHANGE_VICTORY_ME_0 = 133;

    public static final int G_CHANGE_DEFEAT_ME = 139;
    public static final int G_CHANGE_DEFEAT_ME_0 = 139;

    public static final int G_CHANGE_VEHICLE_BGM = 140;
    public static final int G_CHANGE_VEHICLE_BGM_0 = 140;

    public static final int G_CHANGE_SAVE_ACCESS = 134;
    public static final int G_CHANGE_SAVE_ACCESS_0 = 134;

    public static final int G_CHANGE_MENU_ACCESS = 135;
    public static final int G_CHANGE_MENU_ACCESS_0 = 135;

    public static final int G_CHANGE_ENCOUNTER = 136;
    public static final int G_CHANGE_ENCOUNTER_0 = 136;

    public static final int G_CHANGE_FORMATION_ACCESS = 137;
    public static final int G_CHANGE_FORMATION_ACCESS_0 = 137;

    public static final int G_CHANGE_WINDOW_COLOR = 138;
    public static final int G_CHANGE_WINDOW_COLOR_0 = 138;

    public static final int G_CHANGE_ACTOR_IMAGES = 322;
    public static final int G_CHANGE_ACTOR_IMAGES_0 = 322;

    public static final int G_CHANGE_VEHILE_IMAGES = 323;
    public static final int G_CHANGE_VEHILE_IMAGES_0 = 323;


    //Part : System Settings end


    //Part : Map begin

    public static final int G_CHANGE_MAP_NAME_DISPLAY = 281;
    public static final int G_CHANGE_MAP_NAME_DISPLAY_0 = 281;

    public static final int G_CHANGE_TITLE_SET = 282;
    public static final int G_CHANGE_TITLE_SET_0 = 282;

    public static final int G_CHANGE_BATTLE_BACK = 283;
    public static final int G_CHANGE_BATTLE_BACK_0 = 283;

    public static final int G_CHANGE_PARALLAX = 284;
    public static final int G_CHANGE_PARALLAX_0 = 284;

    public static final int G_GET_LOCATION_INFO = 285;
    public static final int G_GET_LOCATION_INFO_0 = 285;

    //Part : Map end


    //Part : Battle begin

    public static final int G_CHANGE_ENEMY_HP = 331;
    public static final int G_CHANGE_ENEMY_HP_0 = 331;

    public static final int G_CHANGE_ENEMY_MP = 332;
    public static final int G_CHANGE_ENEMY_MP_0 = 332;

    public static final int G_CHANGE_ENEMY_TP = 342;
    public static final int G_CHANGE_ENEMY_TP_0 = 342;

    public static final int G_CHANGE_ENEMY_STATE = 333;
    public static final int G_CHANGE_ENEMY_STATE_0 = 333;

    public static final int G_ENEMY_RECOVER_ALL = 334;
    public static final int G_ENEMY_RECOVER_ALL_0 = 334;

    public static final int G_ENEMY_APPEAR = 335;
    public static final int G_ENEMY_APPEAR_0 = 335;

    public static final int G_ENEMY_TRANSFORM = 336;
    public static final int G_ENEMY_TRANSFORM_0 = 336;

    public static final int G_SHOW_BATTLE_ANIMATION = 337;
    public static final int G_SHOW_BATTLE_ANIMATION_0 = 337;

    public static final int G_FORCE_ACTION = 339;
    public static final int G_FORCE_ACTION_0 = 339;

    public static final int G_ABORT_BATTLE = 340;
    public static final int G_ABORT_BATTLE_0 = 340;

    //Part : Battle end


    //Part : Advanced begin

    public static final int G_SCRIPT = 355;
    public static final int G_SCRIPT_0 = 355;

    public static final int G_PLUGIN_COMMAND = 356;
    public static final int G_PLUGIN_COMMAND_0 = 356;

    //Part : Advanced end


}
