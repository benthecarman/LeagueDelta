package com.example.benth.leaguedelta;

import android.support.annotation.Nullable;

import net.rithms.riot.api.endpoints.spectator.dto.CurrentGameInfo;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.io.Serializable;

@SuppressWarnings("serial")
class Search implements Serializable
{
    Summoner summoner;
    CurrentGameInfo currentGameInfo;
    Platform platform;

    Search(Search s)
    {
        this.summoner = s.summoner;
        this.currentGameInfo = s.currentGameInfo;
        this.platform = s.platform;
    }

    Search(Summoner sum, @Nullable CurrentGameInfo cgi, Platform p)
    {
        this.summoner = sum;
        this.currentGameInfo = cgi;
        this.platform = p;
    }

    @Override
    public String toString()
    {
        return summoner.getName()+ " "+ currentGameInfo.getGameMode()+" "+platform.toString();
    }
}
