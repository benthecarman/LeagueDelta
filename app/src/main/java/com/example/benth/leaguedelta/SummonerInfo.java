package com.example.benth.leaguedelta;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import net.rithms.riot.api.endpoints.champion_mastery.dto.ChampionMastery;
import net.rithms.riot.api.endpoints.league.dto.LeaguePosition;
import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.List;

class SummonerInfo {
    LeaguePosition rank;
    List<ChampionMastery> mastery;
    Summoner summoner;
    Bitmap icon;
    List<Match> matches;

    SummonerInfo(LeaguePosition r, @Nullable List<ChampionMastery> m, Summoner s, Bitmap i, List<Match> ma) {
        rank = r;
        mastery = m;
        summoner = s;
        icon = i;
        matches = ma;
    }

}
