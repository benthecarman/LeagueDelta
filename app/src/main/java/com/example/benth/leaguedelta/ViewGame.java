package com.example.benth.leaguedelta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import net.rithms.riot.api.endpoints.spectator.dto.CurrentGameParticipant;

import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class ViewGame extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game);

        Intent i = getIntent();
        Search search = (Search)i.getSerializableExtra("data");

        List<CurrentGameParticipant> all = search.currentGameInfo.getParticipants();
        List<CurrentGameParticipant> enemies = new LinkedList<>();

        int team = search.currentGameInfo.getParticipantByParticipantName(search.summoner.getName()).getTeamId();

        int champId = search.currentGameInfo.getParticipantByParticipantName(search.summoner.getName()).getChampionId();

        enemies.addAll(all.stream().filter(c -> c.getTeamId() != team).collect(toList()));

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener((View v) ->
                onBackPressed());

        ImageButton im;
        String champ;
        int imageId;
        long id = search.summoner.getAccountId();
        Context c;

        im = (ImageButton) findViewById(R.id.opponent0);
        champ = Utilities.champIdToChampName(enemies.get(0).getChampionId(),true);
        c = im.getContext();
        imageId = c.getResources().getIdentifier(champ, "drawable", c.getPackageName());
        im.setImageDrawable(getDrawable(imageId));
        im.setOnClickListener((View v) ->
        {
            Intent in = new Intent(this, Matchup.class);
            in.putExtra("x", new ChampionMatchup(id,champId, enemies.get(0),search.platform,search.currentGameInfo.getGameQueueConfigId()));
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        });

        im = (ImageButton) findViewById(R.id.opponent1);
        champ = Utilities.champIdToChampName(enemies.get(1).getChampionId(),true);
        c = im.getContext();
        imageId = c.getResources().getIdentifier(champ, "drawable", c.getPackageName());
        im.setImageDrawable(getDrawable(imageId));
        im.setOnClickListener((View v) ->
        {
            Intent in = new Intent(this, Matchup.class);
            in.putExtra("x", new ChampionMatchup(id,champId, enemies.get(1),search.platform,search.currentGameInfo.getGameQueueConfigId()));
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        });

        im = (ImageButton) findViewById(R.id.opponent2);
        champ = Utilities.champIdToChampName(enemies.get(2).getChampionId(),true);
        c = im.getContext();
        imageId = c.getResources().getIdentifier(champ, "drawable", c.getPackageName());
        im.setImageDrawable(getDrawable(imageId));
        im.setOnClickListener((View v) ->
        {
            Intent in = new Intent(this, Matchup.class);
            in.putExtra("x", new ChampionMatchup(id,champId, enemies.get(2),search.platform,search.currentGameInfo.getGameQueueConfigId()));
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        });

        im = (ImageButton) findViewById(R.id.opponent3);
        champ = Utilities.champIdToChampName(enemies.get(3).getChampionId(),true);
        c = im.getContext();
        imageId = c.getResources().getIdentifier(champ, "drawable", c.getPackageName());
        im.setImageDrawable(getDrawable(imageId));
        im.setOnClickListener((View v) ->
        {
            Intent in = new Intent(this, Matchup.class);
            in.putExtra("x", new ChampionMatchup(id,champId, enemies.get(3),search.platform, search.currentGameInfo.getGameQueueConfigId()));
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        });


        im = (ImageButton) findViewById(R.id.opponent4);
        champ = Utilities.champIdToChampName(enemies.get(4).getChampionId(),true);
        c = im.getContext();
        imageId = c.getResources().getIdentifier(champ, "drawable", c.getPackageName());
        im.setImageDrawable(getDrawable(imageId));
        im.setOnClickListener((View v) ->
        {
            Intent in = new Intent(this, Matchup.class);
            in.putExtra("x", new ChampionMatchup(id,champId, enemies.get(4),search.platform,search.currentGameInfo.getGameQueueConfigId()));
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        });
    }
}
