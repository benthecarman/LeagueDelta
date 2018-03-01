package com.example.benth.leaguedelta;


import net.rithms.riot.api.endpoints.spectator.dto.CurrentGameParticipant;
import net.rithms.riot.api.endpoints.spectator.dto.Mastery;
import net.rithms.riot.api.endpoints.spectator.dto.Rune;
import net.rithms.riot.constant.Platform;

import java.io.Serializable;
import java.util.List;

class ChampionMatchup implements Serializable
{
    int enemyChampId, champId, spell1, spell2,queueID;
    long enemySummonerId, accountSummonerId;
    List<Rune> runes;
    List<Mastery> masteries;
    Platform platform;


    ChampionMatchup(long id, int myChampId, CurrentGameParticipant currentGameParticipant, Platform p, int qid)
    {
        champId = myChampId;
        runes = currentGameParticipant.getRunes();
        masteries = currentGameParticipant.getMasteries();
        enemyChampId = currentGameParticipant.getChampionId();
        spell1 = currentGameParticipant.getSpell1Id();
        spell2 = currentGameParticipant.getSpell2Id();
        enemySummonerId = currentGameParticipant.getSummonerId();
        platform = p;
        queueID = qid;
        accountSummonerId = id;
    }


}
