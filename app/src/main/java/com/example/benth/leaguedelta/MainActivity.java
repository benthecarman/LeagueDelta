package com.example.benth.leaguedelta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.spectator.dto.CurrentGameInfo;
import net.rithms.riot.api.endpoints.spectator.dto.FeaturedGameInfo;
import net.rithms.riot.api.endpoints.static_data.constant.ChampionListTags;
import net.rithms.riot.api.endpoints.static_data.constant.Locale;
import net.rithms.riot.api.endpoints.static_data.constant.SpellListTags;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.static_data.dto.ChampionList;
import net.rithms.riot.api.endpoints.static_data.dto.ChampionSpell;
import net.rithms.riot.api.endpoints.static_data.dto.SummonerSpell;
import net.rithms.riot.api.endpoints.static_data.dto.SummonerSpellList;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    public EditText editText;
    public TextView text;
    public Search search;
    public Spinner spinner;
    private int pos;
    private String currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.summoner_name);
        spinner = findViewById(R.id.region);
        text = findViewById(R.id.data);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultName = sharedPref.getString("summoner_name", "");
        String defaultRegion = sharedPref.getString("preferred_region", "NA");

        if(sharedPref.getBoolean("Night Mode", false))
            findViewById(R.id.base).setBackground(getDrawable(R.drawable.bgd));

        if (!defaultName.equals(""))
            editText.setText(defaultName);

        editText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.action_search || id == EditorInfo.IME_NULL) {
                search();
                return true;
            }
            return false;
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.regions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        switch (defaultRegion) {
            case "NA":
                spinner.setSelection(0);
                break;
            case "EUW":
                spinner.setSelection(1);
                break;
            case "EUNE":
                spinner.setSelection(2);
                break;
            case "OCE":
                spinner.setSelection(3);
                break;
            case "LAN":
                spinner.setSelection(4);
                break;
        }

        /*
        FileOutputStream fos;
        try {
            fos = openFileOutput(getString(R.string.VERSIONFILENAME), Context.MODE_PRIVATE);
            fos.write("update".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        ArrayList<Byte> bytes = new ArrayList<>();
        FileInputStream fis;
        try {
            fis = openFileInput(getString(R.string.VERSIONFILENAME));
            while (fis.available() > 0)
                bytes.add((byte) fis.read());
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] b = new byte[bytes.size()];
        for (int z = 0; z < bytes.size(); ++z)
            b[z] = bytes.get(z);

        Constants.LOL_VERSION = currentVersion = new String(b, StandardCharsets.UTF_8);

        text.setText(currentVersion);

        //new SummonerLookup().execute("d");

        findViewById(R.id.search_button).setOnClickListener((View v) -> search());

        findViewById(R.id.settings).setOnClickListener((View v) -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void search() {
        SummonerLookup s = new SummonerLookup();
        s.execute(editText.getText().toString());

        if (search == null) {
            text.setText(getString(R.string.dne));
        } else if (search.currentGameInfo == null) {
            text.setText(getString(R.string.notInGame));
        } else {
            text.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("summoner_name", ""));
            Intent intent = new Intent(this, ViewGame.class);
            intent.putExtra("data", search);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        pos = parent.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, About.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private class SummonerLookup extends AsyncTask<String, Void, Search> {
        @Override
        protected Search doInBackground(String... params) {
            ApiConfig config = new ApiConfig().setKey(Constants.API_KEY).setRateLimitHandler(null);
            RiotApi api = new RiotApi(config);
            //RiotApiAsync apiAsync = api.getAsyncApi();

            Platform platform;

            switch (pos) {
                case 0:
                    platform = Platform.NA;
                    break;
                case 1:
                    platform = Platform.EUW;
                    break;
                case 2:
                    platform = Platform.EUNE;
                    break;
                case 3:
                    platform = Platform.OCE;
                    break;
                case 4:
                    platform = Platform.LAN;
                    break;
                default:
                    platform = Platform.NA;
            }

            Constants.platform = platform;

            Summoner sum;
            CurrentGameInfo cgi;

            try {
                //String name = api.getSummonerByName(platform, params[0].replaceAll("\\s+", "").toLowerCase());
                //FIXME for real use

                //I am for testing <
                List<FeaturedGameInfo> gameInfoList = api.getFeaturedGames(platform).getGameList();
                int i = -1;
                for (FeaturedGameInfo f : gameInfoList) {
                    if (f.getMapId() == 11) {
                        i++;
                        break;
                    }
                    i++;
                }
                if (i == -1) {
                    for (FeaturedGameInfo f : gameInfoList) {
                        if (f.getParticipants().size() == 10) {
                            i++;
                            break;
                        }
                        i++;
                    }
                }//I am for testing >

                sum = api.getSummonerByName(platform, gameInfoList.get(i).getParticipants().get(0).getSummonerName());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            try {
                cgi = api.getActiveGameBySummoner(platform, sum.getId());
            } catch (RiotApiException e) {
                e.printStackTrace();
                cgi = null;
            }

            try {
                String newVersion = api.getDataVersions(platform).get(0);
                if (!currentVersion.equalsIgnoreCase(newVersion)) {
                    FileOutputStream fos;
                    Constants.LOL_VERSION = currentVersion = newVersion;
                    fos = openFileOutput(getString(R.string.VERSIONFILENAME), Context.MODE_PRIVATE);
                    fos.write(newVersion.getBytes());
                    fos.close();

                    fos = openFileOutput(getString(R.string.SSFILENAME), Context.MODE_PRIVATE);
                    SummonerSpellList ssl = api.getDataSummonerSpellList(platform, Locale.EN_US, newVersion, true, SpellListTags.COOLDOWN, SpellListTags.IMAGE);
                    Map<String, SummonerSpell> ssMap = ssl.getData();
                    StringBuilder summoners = new StringBuilder();
                    for (SummonerSpell ss : ssMap.values())
                        summoners.append("").append(ss.getId()).append(" ").append(ss.getCooldown().get(0)).append(" ").append(ss.getImage().getFull()).append("\n");
                    /*Format: (SummonerSpellID) (SummonerSpellCD) (SummonerSpellIcon)\n */
                    fos.write(summoners.toString().getBytes());

                    fos.close();

                    fos = openFileOutput(getString(R.string.CHAMPFILENAME), Context.MODE_PRIVATE);
                    ChampionList championList = api.getDataChampionList(platform, Locale.EN_US, newVersion, true, ChampionListTags.SPELLS, ChampionListTags.PASSIVE);
                    Map<String, Champion> championMap = championList.getData();
                    StringBuilder champs = new StringBuilder();
                    for (Champion champion : championMap.values()) {
                        champs.append("").append(champion.getId()).append(" ").append(champion.getName().replace(" ",""));
                        for (ChampionSpell spell : champion.getSpells())
                            champs.append(" ").append(spell.getCooldownBurn());
                        champs.append("\n");
                    }/*Format:(champID) (name) (qCD) (wCD) (eCD) (rCD)\n */
                    fos.write(champs.toString().getBytes());
                    fos.close();

                    fos = openFileOutput(getString(R.string.ABILITYFILENAME), Context.MODE_PRIVATE);
                    championList = api.getDataChampionList(platform, Locale.EN_US, newVersion, true, ChampionListTags.SPELLS);
                    championMap = championList.getData();
                    StringBuilder spells = new StringBuilder();
                    for (Champion champion : championMap.values()) {
                        spells.append(champion.getId());
                        for (ChampionSpell spell : champion.getSpells())
                            spells.append(" ").append(spell.getImage().getFull());
                        spells.append("\n");
                    }/*Format:(champID) (qIcon) (wIcon) (eIcon) (rIcon)\n */
                    fos.write(spells.toString().getBytes());
                    fos.close();
                }
            } catch (RiotApiException | IOException e) {
                e.printStackTrace();
            }

            return new Search(sum, cgi, platform);
        }

        @Override
        protected void onPostExecute(Search s) {
            super.onPostExecute(s);
            /*
            ArrayList<Byte> bytes = new ArrayList<>();
            FileInputStream fis;
            try {
                fis = openFileInput(getString(R.string.SSFILENAME));
                while (fis.available()>0)
                    bytes.add((byte) fis.read());
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] b = new byte[bytes.size()];
            for(int z = 0;z<bytes.size();++z)
                b[z] = bytes.get(z);

            text.setText(new String(b, StandardCharsets.UTF_8));
            */
            if (s != null)
                search = new Search(s);
            else
                text.setText(getString(R.string.error));
        }
    }
}
