package com.example.benth.leaguedelta;

import net.rithms.riot.api.endpoints.spectator.dto.CurrentGameParticipant;
import net.rithms.riot.api.endpoints.spectator.dto.Perks;
import net.rithms.riot.constant.Platform;

import java.io.Serializable;

class ChampionMatchup implements Serializable {
    int enemyChampId, champId, spell1, spell2, queueID;
    long enemySummonerId, accountSummonerId;
    Platform platform;
    Perks perks;


    ChampionMatchup(long id, int myChampId, CurrentGameParticipant currentGameParticipant, Platform p, int qid) {
        champId = myChampId;
        perks = currentGameParticipant.getPerks();
        enemyChampId = currentGameParticipant.getChampionId();
        spell1 = currentGameParticipant.getSpell1Id();
        spell2 = currentGameParticipant.getSpell2Id();
        enemySummonerId = currentGameParticipant.getSummonerId();
        platform = p;
        queueID = qid;
        accountSummonerId = id;
    }


}
