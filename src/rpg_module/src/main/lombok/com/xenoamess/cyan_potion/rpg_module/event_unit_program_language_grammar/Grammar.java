/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.rpg_module.event_unit_program_language_grammar;

/**
 * <p>Grammar class.</p>
 *
 * @author XenoAmess
 * @version 0.162.2-SNAPSHOT
 */
public class Grammar {
    private Grammar() {
        //shall never
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
    @SuppressWarnings("unused")
    public static final int G_PASS = 0;

    //Part : Pass end


    //Part : Message begin

    /**
     * Constant <code>G_SHOW_TEXT=101</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_TEXT = 101;

    /**
     * ShowText_PatternSetting
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_TEXT_0 = 101;

    /**
     * ShowText_ContentSetting
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_TEXT_1 = 401;


    /**
     * ShowChoices_BasicSetting
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_CHOICES_0 = 102;

    /**
     * ShowChoices_IfChooseOneChoiceThen
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_CHOICES_1 = 402;

    /**
     * ShowChoices_CancelChoicesThen
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_CHOICES_2 = 403;

    /**
     * ShowChoices_EndShowChoices
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_CHOICES_3 = 404;


    /**
     * Constant <code>G_INPUT_NUMBER=103</code>
     */
    @SuppressWarnings("unused")
    public static final int G_INPUT_NUMBER = 103;
    /**
     * Constant <code>G_INPUT_NUMBER_0=103</code>
     */
    @SuppressWarnings("unused")
    public static final int G_INPUT_NUMBER_0 = 103;


    /**
     * Constant <code>G_SELECT_ITEM=104</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SELECT_ITEM = 104;
    /**
     * Constant <code>G_SELECT_ITEM_0=104</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SELECT_ITEM_0 = 104;


    /**
     * Constant <code>G_SHOW_SCROLLING_TEXT_0=105</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_SCROLLING_TEXT_0 = 105;
    /**
     * Constant <code>G_SHOW_SCROLLING_TEXT_1=405</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_SCROLLING_TEXT_1 = 405;

    //Part : Message end


    //Part : Game Progress begin

    /**
     * Constant <code>G_CONTROL_SWITCHES=121</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTROL_SWITCHES = 121;
    /**
     * Constant <code>G_CONTROL_SWITCHES_0=121</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTROL_SWITCHES_0 = 121;


    /**
     * Constant <code>G_CONTROL_VARIABLES=122</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTROL_VARIABLES = 122;
    /**
     * Constant <code>G_CONTROL_VARIABLES_0=122</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTROL_VARIABLES_0 = 122;


    /**
     * Constant <code>G_CONTROL_SELF_SWITCH=123</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTROL_SELF_SWITCH = 123;
    /**
     * Constant <code>G_CONTROL_SELF_SWITCH_0=123</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTROL_SELF_SWITCH_0 = 123;


    /**
     * Constant <code>G_CONTROL_TIMER=124</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTROL_TIMER = 124;
    /**
     * Constant <code>G_CONTROL_TIMER_0=124</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTROL_TIMER_0 = 124;

    //Part : Game Progress end


    //Part : Flow Control begin

    /**
     * if
     */
    @SuppressWarnings("unused")
    public static final int G_CONDITIONAL_BRANCH_0 = 111;

    /**
     * else (conditional)
     */
    @SuppressWarnings("unused")
    public static final int G_CONDITIONAL_BRANCH_1 = 411;

    /**
     * endif
     */
    @SuppressWarnings("unused")
    public static final int G_CONDITIONAL_BRANCH_2 = 412;


    /**
     * loop,or say, while(1)
     */
    @SuppressWarnings("unused")
    public static final int G_LOOP_0 = 112;

    /**
     * end loop
     */
    @SuppressWarnings("unused")
    public static final int G_LOOP_1 = 413;


    /**
     * break loop,or say,break;
     */
    @SuppressWarnings("unused")
    public static final int G_BREAK_LOOP = 113;
    /**
     * Constant <code>G_BREAK_LOOP_0=113</code>
     */
    @SuppressWarnings("unused")
    public static final int G_BREAK_LOOP_0 = 113;

    /**
     * (abnormal) continue loop,or say,continue;
     */
    @SuppressWarnings("unused")
    public static final int G_CONTINUE_LOOP = 114;
    /**
     * Constant <code>G_CONTINUE_LOOP_0=114</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CONTINUE_LOOP_0 = 114;


    /**
     * Constant <code>G_EXIT_EVENT_PROCESSING=115</code>
     */
    @SuppressWarnings("unused")
    public static final int G_EXIT_EVENT_PROCESSING = 115;
    /**
     * Constant <code>G_EXIT_EVENT_PROCESSING_0=115</code>
     */
    @SuppressWarnings("unused")
    public static final int G_EXIT_EVENT_PROCESSING_0 = 115;


    /**
     * Constant <code>G_COMMON_EVENT=117</code>
     */
    @SuppressWarnings("unused")
    public static final int G_COMMON_EVENT = 117;
    /**
     * Constant <code>G_COMMON_EVENT_0=117</code>
     */
    @SuppressWarnings("unused")
    public static final int G_COMMON_EVENT_0 = 117;


    /**
     * Constant <code>G_LABEL=118</code>
     */
    @SuppressWarnings("unused")
    public static final int G_LABEL = 118;
    /**
     * Constant <code>G_LABEL_0=118</code>
     */
    @SuppressWarnings("unused")
    public static final int G_LABEL_0 = 118;


    /**
     * Constant <code>G_JUMP_TO_LABEL=119</code>
     */
    @SuppressWarnings("unused")
    public static final int G_JUMP_TO_LABEL = 119;
    /**
     * Constant <code>G_JUMP_TO_LABEL_0=119</code>
     */
    @SuppressWarnings("unused")
    public static final int G_JUMP_TO_LABEL_0 = 119;


    /**
     * Constant <code>G_COMMENT=108</code>
     */
    @SuppressWarnings("unused")
    public static final int G_COMMENT = 108;
    /**
     * Constant <code>G_COMMENT_0=108</code>
     */
    @SuppressWarnings("unused")
    public static final int G_COMMENT_0 = 108;

    //Part : Flow Control end


    //Part : Party begin

    /**
     * positive change
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_GOLD_0 = 108;

    /**
     * negative change
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_GOLD_1 = 125;

    /**
     * Constant <code>G_CHANGE_ITEMS=126</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ITEMS = 126;
    /**
     * Constant <code>G_CHANGE_ITEMS_0=126</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ITEMS_0 = 126;


    /**
     * Constant <code>G_CHANGE_WEAPONS=127</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_WEAPONS = 127;
    /**
     * Constant <code>G_CHANGE_WEAPONS_0=127</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_WEAPONS_0 = 127;


    /**
     * Constant <code>G_CHANGE_ARMORS=128</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ARMORS = 128;
    /**
     * Constant <code>G_CHANGE_ARMORS_0=128</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ARMORS_0 = 128;


    /**
     * Constant <code>G_CHANGE_PARTY_MEMBER=129</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PARTY_MEMBER = 129;
    /**
     * Constant <code>G_CHANGE_PARTY_MEMBER_0=129</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PARTY_MEMBER_0 = 129;

    //Part : Party end


    //Part : Actor begin

    /**
     * Constant <code>G_CHANGE_HP=311</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_HP = 311;
    /**
     * Constant <code>G_CHANGE_HP_0=311</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_HP_0 = 311;


    /**
     * Constant <code>G_CHANGE_MP=312</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_MP = 312;
    /**
     * Constant <code>G_CHANGE_MP_0=312</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_MP_0 = 312;


    /**
     * Constant <code>G_CHANGE_TP=326</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_TP = 326;
    /**
     * Constant <code>G_CHANGE_TP_0=326</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_TP_0 = 326;


    /**
     * Constant <code>G_CHANGE_STATE=313</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_STATE = 313;
    /**
     * Constant <code>G_CHANGE_STATE_0=313</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_STATE_0 = 313;


    /**
     * Constant <code>G_RECOVER_ALL=314</code>
     */
    @SuppressWarnings("unused")
    public static final int G_RECOVER_ALL = 314;
    /**
     * Constant <code>G_RECOVER_ALL_0=314</code>
     */
    @SuppressWarnings("unused")
    public static final int G_RECOVER_ALL_0 = 314;


    /**
     * Constant <code>G_CHANGE_EXP=315</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_EXP = 315;
    /**
     * Constant <code>G_CHANGE_EXP_0=315</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_EXP_0 = 315;


    /**
     * Constant <code>G_CHANGE_LEVEL=316</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_LEVEL = 316;
    /**
     * Constant <code>G_CHANGE_LEVEL_0=316</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_LEVEL_0 = 316;


    /**
     * Constant <code>G_CHANGE_PARAMETER=317</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PARAMETER = 317;
    /**
     * Constant <code>G_CHANGE_PARAMETER_0=317</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PARAMETER_0 = 317;


    /**
     * Constant <code>G_CHANGE_SKILL=318</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_SKILL = 318;
    /**
     * Constant <code>G_CHANGE_SKILL_0=318</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_SKILL_0 = 318;


    /**
     * Constant <code>G_CHANGE_EQUIPMENT=319</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_EQUIPMENT = 319;
    /**
     * Constant <code>G_CHANGE_EQUIPMENT_0=319</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_EQUIPMENT_0 = 319;


    /**
     * Constant <code>G_CHANGE_NAME=320</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_NAME = 320;
    /**
     * Constant <code>G_CHANGE_NAME_0=320</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_NAME_0 = 320;


    /**
     * Constant <code>G_CHANGE_CLASS=321</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_CLASS = 321;
    /**
     * Constant <code>G_CHANGE_CLASS_0=321</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_CLASS_0 = 321;


    /**
     * Constant <code>G_CHANGE_NICKNAME=324</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_NICKNAME = 324;
    /**
     * Constant <code>G_CHANGE_NICKNAME_0=324</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_NICKNAME_0 = 324;


    /**
     * Constant <code>G_CHANGE_PROFILE=325</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PROFILE = 325;
    /**
     * Constant <code>G_CHANGE_PROFILE_0=325</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PROFILE_0 = 325;

    //Part : Actor end


    //Part : Movement begin

    /**
     * Constant <code>G_TRANSFER_PLAYER=201</code>
     */
    @SuppressWarnings("unused")
    public static final int G_TRANSFER_PLAYER = 201;
    /**
     * Constant <code>G_TRANSFER_PLAYER_0=201</code>
     */
    @SuppressWarnings("unused")
    public static final int G_TRANSFER_PLAYER_0 = 201;


    /**
     * Constant <code>G_SET_VEHICLE_LOCATION=202</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SET_VEHICLE_LOCATION = 202;
    /**
     * Constant <code>G_SET_VEHICLE_LOCATION_0=202</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SET_VEHICLE_LOCATION_0 = 202;


    /**
     * Constant <code>G_SET_EVENT_LOCATION=203</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SET_EVENT_LOCATION = 203;
    /**
     * Constant <code>G_SET_EVENT_LOCATION_0=203</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SET_EVENT_LOCATION_0 = 203;


    /**
     * Constant <code>G_SCROLL_MAP=204</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SCROLL_MAP = 204;
    /**
     * Constant <code>G_SCROLL_MAP_0=204</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SCROLL_MAP_0 = 204;

    /**
     * SetMovementRoute_WholeRoute
     */
    @SuppressWarnings("unused")
    public static final int G_SET_MOVEMENT_ROUTE_0 = 205;

    /**
     * SetMovementRoute_Contents
     */
    @SuppressWarnings("unused")
    public static final int G_SET_MOVEMENT_ROUTE_1 = 505;


    /**
     * SetMovementRoute_WholeRoute
     */
    @SuppressWarnings("unused")
    public static final int G_GET_ON_OFF_VEHICLE = 206;

    /**
     * SetMovementRoute_Contents
     */
    @SuppressWarnings("unused")
    public static final int G_GET_ON_OFF_VEHICLE_0 = 206;

    //Part : Movement end


    //Part : Character begin

    /**
     * Constant <code>G_CHANGE_TRANSPARENCY=211</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_TRANSPARENCY = 211;
    /**
     * Constant <code>G_CHANGE_TRANSPARENCY_0=211</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_TRANSPARENCY_0 = 211;

    /**
     * Constant <code>G_CHANGE_PLAYER_FOLLOWERS=216</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PLAYER_FOLLOWERS = 216;
    /**
     * Constant <code>G_CHANGE_PLAYER_FOLLOWERS_0=216</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PLAYER_FOLLOWERS_0 = 216;

    /**
     * Constant <code>G_GATHER_FOLLOWERS=217</code>
     */
    @SuppressWarnings("unused")
    public static final int G_GATHER_FOLLOWERS = 217;
    /**
     * Constant <code>G_GATHER_FOLLOWERS_0=217</code>
     */
    @SuppressWarnings("unused")
    public static final int G_GATHER_FOLLOWERS_0 = 217;

    /**
     * Constant <code>G_SHOW_ANIMATION=212</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_ANIMATION = 212;
    /**
     * Constant <code>G_SHOW_ANIMATION_0=212</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_ANIMATION_0 = 212;

    /**
     * Constant <code>G_SHOW_BALLOON_ICON=213</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_BALLOON_ICON = 213;
    /**
     * Constant <code>G_SHOW_BALLOON_ICON_0=213</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_BALLOON_ICON_0 = 213;

    /**
     * Constant <code>G_ERASE_EVENT=214</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ERASE_EVENT = 214;
    /**
     * Constant <code>G_ERASE_EVENT_0=214</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ERASE_EVENT_0 = 214;

    //Part : Character end

    //Part : Picture begin

    /**
     * Constant <code>G_SHOW_PICTURE=231</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_PICTURE = 231;
    /**
     * Constant <code>G_SHOW_PICTURE_0=231</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_PICTURE_0 = 231;

    /**
     * Constant <code>G_MOVE_PICTURE=232</code>
     */
    @SuppressWarnings("unused")
    public static final int G_MOVE_PICTURE = 232;
    /**
     * Constant <code>G_MOVE_PICTURE_0=232</code>
     */
    @SuppressWarnings("unused")
    public static final int G_MOVE_PICTURE_0 = 232;

    /**
     * Constant <code>G_ROTATE_PICTURE=233</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ROTATE_PICTURE = 233;
    /**
     * Constant <code>G_ROTATE_PICTURE_0=233</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ROTATE_PICTURE_0 = 233;

    /**
     * Constant <code>G_TINT_PICTURE=234</code>
     */
    @SuppressWarnings("unused")
    public static final int G_TINT_PICTURE = 234;
    /**
     * Constant <code>G_TINT_PICTURE_0=234</code>
     */
    @SuppressWarnings("unused")
    public static final int G_TINT_PICTURE_0 = 234;

    /**
     * Constant <code>G_ERASE_PICTURE=235</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ERASE_PICTURE = 235;
    /**
     * Constant <code>G_ERASE_PICTURE_0=235</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ERASE_PICTURE_0 = 235;

    //Part : Picture end


    //Part : Timing begin

    /**
     * Constant <code>G_WAIT=230</code>
     */
    @SuppressWarnings("unused")
    public static final int G_WAIT = 230;
    /**
     * Constant <code>G_WAIT_0=230</code>
     */
    @SuppressWarnings("unused")
    public static final int G_WAIT_0 = 230;

    //Part : Timing end

    //Part : Screen begin

    /**
     * Constant <code>G_FADEOUT_SCREEN=221</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FADEOUT_SCREEN = 221;
    /**
     * Constant <code>G_FADEOUT_SCREEN_0=221</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FADEOUT_SCREEN_0 = 221;

    /**
     * Constant <code>G_FADEIN_SCREEN=222</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FADEIN_SCREEN = 222;
    /**
     * Constant <code>G_FADEIN_SCREEN_0=222</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FADEIN_SCREEN_0 = 222;

    /**
     * Constant <code>G_TINT_SCREEN=223</code>
     */
    @SuppressWarnings("unused")
    public static final int G_TINT_SCREEN = 223;
    /**
     * Constant <code>G_TINT_SCREEN_0=223</code>
     */
    @SuppressWarnings("unused")
    public static final int G_TINT_SCREEN_0 = 223;

    /**
     * Constant <code>G_FLASH_SCREEN=224</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FLASH_SCREEN = 224;
    /**
     * Constant <code>G_FLASH_SCREEN_0=224</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FLASH_SCREEN_0 = 224;

    /**
     * Constant <code>G_SHAKE_SCREEN=225</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHAKE_SCREEN = 225;
    /**
     * Constant <code>G_SHAKE_SCREEN_0=225</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHAKE_SCREEN_0 = 225;

    /**
     * Constant <code>G_SET_WEATHER_EFFECT=236</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SET_WEATHER_EFFECT = 236;
    /**
     * Constant <code>G_SET_WEATHER_EFFECT_0=236</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SET_WEATHER_EFFECT_0 = 236;

    //Part : Screen end


    //Part : Audio & Video begin

    /**
     * Constant <code>G_PLAY_BGM=241</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAY_BGM = 241;
    /**
     * Constant <code>G_PLAY_BGM_0=241</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAY_BGM_0 = 241;

    /**
     * Constant <code>G_FADEOUT_BGM=242</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FADEOUT_BGM = 242;
    /**
     * Constant <code>G_FADEOUT_BGM_0=242</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FADEOUT_BGM_0 = 242;

    /**
     * Constant <code>G_SAVE_BGM=243</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SAVE_BGM = 243;
    /**
     * Constant <code>G_SAVE_BGM_0=243</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SAVE_BGM_0 = 243;

    /**
     * Constant <code>G_REPLAY_BGM=244</code>
     */
    @SuppressWarnings("unused")
    public static final int G_REPLAY_BGM = 244;
    /**
     * Constant <code>G_REPLAY_BGM_0=244</code>
     */
    @SuppressWarnings("unused")
    public static final int G_REPLAY_BGM_0 = 244;

    /**
     * Constant <code>G_PLAYBGS=245</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAYBGS = 245;
    /**
     * Constant <code>G_PLAYBGS_0=245</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAYBGS_0 = 245;

    /**
     * Constant <code>G_FADEOUT_BGS=246</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FADEOUT_BGS = 246;
    /**
     * Constant <code>G_FADEOUT_BGS_0=246</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FADEOUT_BGS_0 = 246;

    /**
     * Constant <code>G_PLAYME=249</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAYME = 249;
    /**
     * Constant <code>G_PLAYME_0=249</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAYME_0 = 249;

    /**
     * Constant <code>G_PLAYSE=250</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAYSE = 250;
    /**
     * Constant <code>G_PLAYSE_0=250</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAYSE_0 = 250;

    /**
     * Constant <code>G_STOPSE=251</code>
     */
    @SuppressWarnings("unused")
    public static final int G_STOPSE = 251;
    /**
     * Constant <code>G_STOPSE_0=251</code>
     */
    @SuppressWarnings("unused")
    public static final int G_STOPSE_0 = 251;

    /**
     * Constant <code>G_PLAY_MOVIE=261</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAY_MOVIE = 261;
    /**
     * Constant <code>G_PLAY_MOVIE_0=261</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLAY_MOVIE_0 = 261;

    //Part : Audio & Video end


    //Part : AbstractScene Control begin

    /**
     * Constant <code>G_BATTLE_PROCESSING_0=301</code>
     */
    @SuppressWarnings("unused")
    public static final int G_BATTLE_PROCESSING_0 = 301;

    /**
     * If Win
     */
    @SuppressWarnings("unused")
    public static final int G_BATTLE_PROCESSING_1 = 601;

    /**
     * If Escape
     */
    @SuppressWarnings("unused")
    public static final int G_BATTLE_PROCESSING_2 = 602;

    /**
     * If Lose
     */
    @SuppressWarnings("unused")
    public static final int G_BATTLE_PROCESSING_3 = 603;

    /**
     * End If
     */
    @SuppressWarnings("unused")
    public static final int G_BATTLE_PROCESSING_4 = 604;

    /**
     * Constant <code>G_SHOP_PROCESSING=302</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOP_PROCESSING = 302;
    /**
     * Constant <code>G_SHOP_PROCESSING_0=302</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOP_PROCESSING_0 = 302;

    /**
     * Constant <code>G_NAME_INPUT_PROCESSING=303</code>
     */
    @SuppressWarnings("unused")
    public static final int G_NAME_INPUT_PROCESSING = 303;
    /**
     * Constant <code>G_NAME_INPUT_PROCESSING_0=303</code>
     */
    @SuppressWarnings("unused")
    public static final int G_NAME_INPUT_PROCESSING_0 = 303;

    /**
     * Constant <code>G_OPEN_MENU_SCREEN=351</code>
     */
    @SuppressWarnings("unused")
    public static final int G_OPEN_MENU_SCREEN = 351;
    /**
     * Constant <code>G_OPEN_MENU_SCREEN_0=351</code>
     */
    @SuppressWarnings("unused")
    public static final int G_OPEN_MENU_SCREEN_0 = 351;

    /**
     * Constant <code>G_OPEN_SAVE_SCREEN=352</code>
     */
    @SuppressWarnings("unused")
    public static final int G_OPEN_SAVE_SCREEN = 352;
    /**
     * Constant <code>G_OPEN_SAVE_SCREEN_0=352</code>
     */
    @SuppressWarnings("unused")
    public static final int G_OPEN_SAVE_SCREEN_0 = 352;

    /**
     * Constant <code>G_GAME_OVER=353</code>
     */
    @SuppressWarnings("unused")
    public static final int G_GAME_OVER = 353;
    /**
     * Constant <code>G_GAME_OVER_0=353</code>
     */
    @SuppressWarnings("unused")
    public static final int G_GAME_OVER_0 = 353;

    /**
     * Constant <code>G_RETURN_TO_TITLE_SCREEN=354</code>
     */
    @SuppressWarnings("unused")
    public static final int G_RETURN_TO_TITLE_SCREEN = 354;
    /**
     * Constant <code>G_RETURN_TO_TITLE_SCREEN_0=354</code>
     */
    @SuppressWarnings("unused")
    public static final int G_RETURN_TO_TITLE_SCREEN_0 = 354;

    //Part : AbstractScene Control end


    //Part : System Settings begin

    /**
     * Constant <code>G_CHANGE_BATTLE_BGM=132</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_BATTLE_BGM = 132;
    /**
     * Constant <code>G_CHANGE_BATTLE_BGM_0=132</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_BATTLE_BGM_0 = 132;

    /**
     * Constant <code>G_CHANGE_VICTORY_ME=133</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_VICTORY_ME = 133;
    /**
     * Constant <code>G_CHANGE_VICTORY_ME_0=133</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_VICTORY_ME_0 = 133;

    /**
     * Constant <code>G_CHANGE_DEFEAT_ME=139</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_DEFEAT_ME = 139;
    /**
     * Constant <code>G_CHANGE_DEFEAT_ME_0=139</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_DEFEAT_ME_0 = 139;

    /**
     * Constant <code>G_CHANGE_VEHICLE_BGM=140</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_VEHICLE_BGM = 140;
    /**
     * Constant <code>G_CHANGE_VEHICLE_BGM_0=140</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_VEHICLE_BGM_0 = 140;

    /**
     * Constant <code>G_CHANGE_SAVE_ACCESS=134</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_SAVE_ACCESS = 134;
    /**
     * Constant <code>G_CHANGE_SAVE_ACCESS_0=134</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_SAVE_ACCESS_0 = 134;

    /**
     * Constant <code>G_CHANGE_MENU_ACCESS=135</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_MENU_ACCESS = 135;
    /**
     * Constant <code>G_CHANGE_MENU_ACCESS_0=135</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_MENU_ACCESS_0 = 135;

    /**
     * Constant <code>G_CHANGE_ENCOUNTER=136</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENCOUNTER = 136;
    /**
     * Constant <code>G_CHANGE_ENCOUNTER_0=136</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENCOUNTER_0 = 136;

    /**
     * Constant <code>G_CHANGE_FORMATION_ACCESS=137</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_FORMATION_ACCESS = 137;
    /**
     * Constant <code>G_CHANGE_FORMATION_ACCESS_0=137</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_FORMATION_ACCESS_0 = 137;

    /**
     * Constant <code>G_CHANGE_WINDOW_COLOR=138</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_WINDOW_COLOR = 138;
    /**
     * Constant <code>G_CHANGE_WINDOW_COLOR_0=138</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_WINDOW_COLOR_0 = 138;

    /**
     * Constant <code>G_CHANGE_ACTOR_IMAGES=322</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ACTOR_IMAGES = 322;
    /**
     * Constant <code>G_CHANGE_ACTOR_IMAGES_0=322</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ACTOR_IMAGES_0 = 322;

    /**
     * Constant <code>G_CHANGE_VEHILE_IMAGES=323</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_VEHILE_IMAGES = 323;
    /**
     * Constant <code>G_CHANGE_VEHILE_IMAGES_0=323</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_VEHILE_IMAGES_0 = 323;


    //Part : System Settings end


    //Part : Map begin

    /**
     * Constant <code>G_CHANGE_MAP_NAME_DISPLAY=281</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_MAP_NAME_DISPLAY = 281;
    /**
     * Constant <code>G_CHANGE_MAP_NAME_DISPLAY_0=281</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_MAP_NAME_DISPLAY_0 = 281;

    /**
     * Constant <code>G_CHANGE_TITLE_SET=282</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_TITLE_SET = 282;
    /**
     * Constant <code>G_CHANGE_TITLE_SET_0=282</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_TITLE_SET_0 = 282;

    /**
     * Constant <code>G_CHANGE_BATTLE_BACK=283</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_BATTLE_BACK = 283;
    /**
     * Constant <code>G_CHANGE_BATTLE_BACK_0=283</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_BATTLE_BACK_0 = 283;

    /**
     * Constant <code>G_CHANGE_PARALLAX=284</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PARALLAX = 284;
    /**
     * Constant <code>G_CHANGE_PARALLAX_0=284</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_PARALLAX_0 = 284;

    /**
     * Constant <code>G_GET_LOCATION_INFO=285</code>
     */
    @SuppressWarnings("unused")
    public static final int G_GET_LOCATION_INFO = 285;
    /**
     * Constant <code>G_GET_LOCATION_INFO_0=285</code>
     */
    @SuppressWarnings("unused")
    public static final int G_GET_LOCATION_INFO_0 = 285;

    //Part : Map end


    //Part : Battle begin

    /**
     * Constant <code>G_CHANGE_ENEMY_HP=331</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENEMY_HP = 331;
    /**
     * Constant <code>G_CHANGE_ENEMY_HP_0=331</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENEMY_HP_0 = 331;

    /**
     * Constant <code>G_CHANGE_ENEMY_MP=332</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENEMY_MP = 332;
    /**
     * Constant <code>G_CHANGE_ENEMY_MP_0=332</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENEMY_MP_0 = 332;

    /**
     * Constant <code>G_CHANGE_ENEMY_TP=342</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENEMY_TP = 342;
    /**
     * Constant <code>G_CHANGE_ENEMY_TP_0=342</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENEMY_TP_0 = 342;

    /**
     * Constant <code>G_CHANGE_ENEMY_STATE=333</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENEMY_STATE = 333;
    /**
     * Constant <code>G_CHANGE_ENEMY_STATE_0=333</code>
     */
    @SuppressWarnings("unused")
    public static final int G_CHANGE_ENEMY_STATE_0 = 333;

    /**
     * Constant <code>G_ENEMY_RECOVER_ALL=334</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ENEMY_RECOVER_ALL = 334;
    /**
     * Constant <code>G_ENEMY_RECOVER_ALL_0=334</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ENEMY_RECOVER_ALL_0 = 334;

    /**
     * Constant <code>G_ENEMY_APPEAR=335</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ENEMY_APPEAR = 335;
    /**
     * Constant <code>G_ENEMY_APPEAR_0=335</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ENEMY_APPEAR_0 = 335;

    /**
     * Constant <code>G_ENEMY_TRANSFORM=336</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ENEMY_TRANSFORM = 336;
    /**
     * Constant <code>G_ENEMY_TRANSFORM_0=336</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ENEMY_TRANSFORM_0 = 336;

    /**
     * Constant <code>G_SHOW_BATTLE_ANIMATION=337</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_BATTLE_ANIMATION = 337;
    /**
     * Constant <code>G_SHOW_BATTLE_ANIMATION_0=337</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SHOW_BATTLE_ANIMATION_0 = 337;

    /**
     * Constant <code>G_FORCE_ACTION=339</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FORCE_ACTION = 339;
    /**
     * Constant <code>G_FORCE_ACTION_0=339</code>
     */
    @SuppressWarnings("unused")
    public static final int G_FORCE_ACTION_0 = 339;

    /**
     * Constant <code>G_ABORT_BATTLE=340</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ABORT_BATTLE = 340;
    /**
     * Constant <code>G_ABORT_BATTLE_0=340</code>
     */
    @SuppressWarnings("unused")
    public static final int G_ABORT_BATTLE_0 = 340;

    //Part : Battle end


    //Part : Advanced begin

    /**
     * Constant <code>G_SCRIPT=355</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SCRIPT = 355;
    /**
     * Constant <code>G_SCRIPT_0=355</code>
     */
    @SuppressWarnings("unused")
    public static final int G_SCRIPT_0 = 355;

    /**
     * Constant <code>G_PLUGIN_COMMAND=356</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLUGIN_COMMAND = 356;
    /**
     * Constant <code>G_PLUGIN_COMMAND_0=356</code>
     */
    @SuppressWarnings("unused")
    public static final int G_PLUGIN_COMMAND_0 = 356;

    //Part : Advanced end


}
