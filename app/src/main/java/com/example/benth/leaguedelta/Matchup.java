package com.example.benth.leaguedelta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.champion_mastery.dto.ChampionMastery;
import net.rithms.riot.api.endpoints.league.constant.LeagueQueue;
import net.rithms.riot.api.endpoints.league.dto.LeaguePosition;
import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.ParticipantStats;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Matchup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchup);

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Night Mode", false))
            findViewById(R.id.base1).setBackground(getDrawable(R.drawable.bgd));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * A Matchup fragment containing a simple view.
     */
    public static class MatchupFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public MatchupFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MatchupFragment newInstance(int sectionNumber, int cdr) {
            MatchupFragment fragment = new MatchupFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt("cdr", cdr);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_matchup, container, false);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onViewCreated(View rootView, Bundle savedInstanceState) {
            ChampionMatchup matchup = (ChampionMatchup) getActivity().getIntent().getSerializableExtra("x");
            int cdr = getArguments().getInt("cdr");

            if(PreferenceManager.getDefaultSharedPreferences(rootView.getContext()).getBoolean("Night Mode", false))
                rootView.findViewById(R.id.base3).setBackground(rootView.getContext().getDrawable(R.drawable.bgd));

            String champ = Utilities.champIdToChampName(matchup.enemyChampId, getContext());

            ImageView opponent = rootView.findViewById(R.id.opponent);
            Context c = rootView.getContext();
            Bitmap bitmap = Utilities.getChampIcon(champ);
            if (bitmap != null)
                opponent.setImageBitmap(bitmap);
            else {
                int imageId = c.getResources().getIdentifier("invalid", "drawable", c.getPackageName());
                opponent.setImageDrawable(getActivity().getDrawable(imageId));
            }

            TextView champ_name = rootView.findViewById(R.id.champ_name);
            champ_name.setText(champ);
            champ_name.setAllCaps(false);

            int keyStoneID = 0;
            boolean hasUltHat =  matchup.perks.getPerkIds().contains(8243L);
            boolean hasSpellBook =  matchup.perks.getPerkIds().contains(8326L);
            boolean hasCosmicInsight =  matchup.perks.getPerkIds().contains(8347L);
            boolean hasCelerity =  matchup.perks.getPerkIds().contains(8234L);

            for (long l : matchup.perks.getPerkIds())
                if (Utilities.isKeystone(l))
                    keyStoneID = (int) l;


            List<String> abilityCooldowns = Utilities.getAbilityCooldowns(matchup.enemyChampId, cdr, hasUltHat, hasCosmicInsight, hasCelerity, c);

            TextView currentCDR = rootView.findViewById(R.id.current_cdr);
            currentCDR.setText(getString(R.string.current_cdr, cdr));

            ImageView qIcon = rootView.findViewById(R.id.q_icon);
            Bitmap bq = Utilities.getChampAbilityIcon(matchup.enemyChampId,0,getContext());
            if(bq != null)
                qIcon.setImageBitmap(bq);
            else {
                int qImageId = c.getResources().getIdentifier("invalid", "drawable", c.getPackageName());
                qIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), qImageId));
            }
            TextView qLabel = rootView.findViewById(R.id.q_label);
            qLabel.setText(abilityCooldowns.get(0));

            ImageView wIcon = rootView.findViewById(R.id.w_icon);
            Bitmap bw = Utilities.getChampAbilityIcon(matchup.enemyChampId,1,getContext());
            if(bw != null)
                wIcon.setImageBitmap(bw);
            else {
                int wImageId = c.getResources().getIdentifier("invalid", "drawable", c.getPackageName());
                wIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), wImageId));
            }
            TextView wLabel = rootView.findViewById(R.id.w_label);
            wLabel.setText(abilityCooldowns.get(1));

            ImageView eIcon = rootView.findViewById(R.id.e_icon);
            Bitmap be = Utilities.getChampAbilityIcon(matchup.enemyChampId,2,getContext());
            if(be != null)
                eIcon.setImageBitmap(be);
            else {
                int eImageId = c.getResources().getIdentifier("invalid", "drawable", c.getPackageName());
                eIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), eImageId));
            }
            TextView eLabel = rootView.findViewById(R.id.e_label);
            eLabel.setText(abilityCooldowns.get(2));

            ImageView rIcon = rootView.findViewById(R.id.r_icon);
            Bitmap br = Utilities.getChampAbilityIcon(matchup.enemyChampId,3,getContext());
            if(br != null)
                rIcon.setImageBitmap(br);
            else {
                int rImageId = c.getResources().getIdentifier("invalid", "drawable", c.getPackageName());
                rIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), rImageId));
            }
            TextView rLabel = rootView.findViewById(R.id.r_label);
            rLabel.setText(abilityCooldowns.get(3));

            List<Integer> cds = Utilities.getSummonerCooldown(matchup.spell1, matchup.spell2, hasCosmicInsight, hasSpellBook, c);

            TextView ss1_icon = rootView.findViewById(R.id.ss1_cd);
            int ss1ImageId = c.getResources().getIdentifier(Utilities.summonerSpellIdToName(matchup.spell1, true), "drawable", c.getPackageName());
            Drawable d1 = ContextCompat.getDrawable(this.getContext(), ss1ImageId);
            d1.setBounds(0, 0, 180, 180);
            ss1_icon.setCompoundDrawables(null, d1, null, null);
            ss1_icon.setText("" + cds.get(0) + " s");

            TextView ss2_icon = rootView.findViewById(R.id.ss2_cd);
            int ss2ImageId = c.getResources().getIdentifier(Utilities.summonerSpellIdToName(matchup.spell2, true), "drawable", c.getPackageName());
            Drawable d2 = ContextCompat.getDrawable(this.getContext(), ss2ImageId);
            d2.setBounds(0, 0, 180, 180);
            ss2_icon.setCompoundDrawables(null, d2, null, null);
            ss2_icon.setText("" + cds.get(1) + " s");

            if(keyStoneID != 0) {
                TextView keystone_icon = rootView.findViewById(R.id.keystone_icon);
                int keystoneImageId = c.getResources().getIdentifier("r" + keyStoneID, "drawable", c.getPackageName());
                Drawable d3 = ContextCompat.getDrawable(this.getContext(), keystoneImageId);
                d3.setBounds(0, 0, 180, 180);
                keystone_icon.setCompoundDrawables(null, d3, null, null);
                keystone_icon.setText(Utilities.getKeyStoneCD(keyStoneID));
            }


            Button plus10 = rootView.findViewById(R.id.cdr10);
            plus10.setOnClickListener((View v) ->
            {
                if (cdr < 40) {
                    Bundle bundle = getArguments();
                    bundle.remove("cdr");
                    bundle.putInt("cdr", cdr + 10);

                    onViewCreated(rootView, bundle);
                }
            });

            Button plus20 = rootView.findViewById(R.id.cdr20);
            plus20.setOnClickListener((View v) ->
            {
                if (cdr < 40) {
                    Bundle bundle = getArguments();
                    bundle.remove("cdr");
                    bundle.putInt("cdr", cdr + 20);

                    onViewCreated(rootView, bundle);
                }
            });

            Button reset = rootView.findViewById(R.id.reset);
            reset.setOnClickListener((View v) ->
            {
                Bundle bundle = getArguments();
                bundle.remove("cdr");
                bundle.putInt("cdr", 0);

                onViewCreated(rootView, bundle);
            });

            ImageButton back = rootView.findViewById(R.id.back_button);
            back.setOnClickListener((View v) ->
                    getActivity().onBackPressed());
        }

    }


    public static class R_And_MFragment extends Fragment { //Runes Fragment
        private static final String ARG_SECTION_NUMBER = "section_number";

        public R_And_MFragment() { //TODO

        }

         public static R_And_MFragment newInstance(int sectionNumber) {
            R_And_MFragment fragment = new R_And_MFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
         }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_runes, container, false);
            ChampionMatchup matchup = (ChampionMatchup) getActivity().getIntent().getSerializableExtra("x");
            return rootView;
        }
    }

    public static class SummonerFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public ChampionMatchup matchup;
        SummonerInfo summonerInfo;
        View root;

        public SummonerFragment() {
        }

        public static SummonerFragment newInstance(int sectionNumber) {
            SummonerFragment fragment = new SummonerFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            matchup = (ChampionMatchup) getActivity().getIntent().getSerializableExtra("x");
            SummonerLookup s = new SummonerLookup();
            root = inflater.inflate(R.layout.fragment_summoner, container, false);
            s.execute();
            return root;
        }

        @Override
        public void onViewCreated(View rootView, Bundle savedInstanceState) {
            if(PreferenceManager.getDefaultSharedPreferences(rootView.getContext()).getBoolean("Night Mode", false))
                rootView.findViewById(R.id.base4).setBackground(rootView.getContext().getDrawable(R.drawable.bgd));
        }

        private void postMethod() {
            if (summonerInfo == null)
                return;

            TextView summonerName = root.findViewById(R.id.summoner);
            TextView masteryScore = root.findViewById(R.id.mastery_points);
            TextView soloqueueDetails = root.findViewById(R.id.soloqueue_details);

            ImageView profile = root.findViewById(R.id.summoner_icon);
            ImageView masteryRank = root.findViewById(R.id.mastery_rank);
            ImageView soloqueueRank = root.findViewById(R.id.soloqueue_rank);

            ImageView mastery1 = root.findViewById(R.id.mastery1);
            ImageView mastery2 = root.findViewById(R.id.mastery2);
            ImageView mastery3 = root.findViewById(R.id.mastery3);

            TextView masteryPoints1 = root.findViewById(R.id.mastery_points_champ_1);
            TextView masteryPoints2 = root.findViewById(R.id.mastery_points_champ_2);
            TextView masteryPoints3 = root.findViewById(R.id.mastery_points_champ_3);

            summonerName.setText(summonerInfo.summoner.getName());
            profile.setImageBitmap(summonerInfo.icon);

            ChampionMastery current = summonerInfo.mastery.get(0);

            for (ChampionMastery m : summonerInfo.mastery) {
                if (m.getChampionId() == matchup.enemyChampId)
                    current = m;
            }

            if (summonerInfo.mastery != null) {
                masteryRank.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier("mastery" + current.getChampionLevel(), "drawable", root.getContext().getPackageName())));
                masteryScore.setText(getString(R.string.mastery_score, Utilities.champIdToChampName(matchup.enemyChampId, getContext()), current.getChampionPoints()));
            } else {
                masteryRank.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier(Utilities.champIdToChampName(matchup.enemyChampId, getContext()), "drawable", root.getContext().getPackageName())));
                masteryScore.setText(getString(R.string.no_score));
            }

            if (summonerInfo.rank != null) {
                String x = summonerInfo.rank.getTier();
                switch (x.toUpperCase()) {
                    case "CHALLENGER":
                        soloqueueRank.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier(x.toLowerCase(), "drawable", root.getContext().getPackageName())));
                        soloqueueDetails.setText(getString(R.string.challenger));
                        break;
                    case "MASTER":
                        soloqueueRank.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier(x.toLowerCase(), "drawable", root.getContext().getPackageName())));
                        soloqueueDetails.setText(getString(R.string.master));
                        break;
                    default:
                        x = x.toLowerCase();
                        x = Character.toUpperCase(x.charAt(0)) + x.substring(1) + " " + summonerInfo.rank.getRank();
                        soloqueueDetails.setText(x);
                        x = x.toLowerCase().replace(" ", "_");
                        soloqueueRank.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier(x, "drawable", root.getContext().getPackageName())));
                        break;
                }

                soloqueueDetails.append("\n" + summonerInfo.rank.getLeaguePoints() + " LP  " + summonerInfo.rank.getWins() + "W" + "/" + summonerInfo.rank.getLosses() + "L");
                double winLoss = ((double) summonerInfo.rank.getWins() / (summonerInfo.rank.getWins() + summonerInfo.rank.getLosses())) * 100;
                soloqueueDetails.append("\n" + (int) winLoss + "% Win Ratio");
            } else {
                soloqueueDetails.setText(getString(R.string.unranked));
                soloqueueRank.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier("provisional", "drawable", root.getContext().getPackageName())));
            }

            doInfo();

            Bitmap bitmap;

            bitmap = Utilities.getChampIcon(Utilities.champIdToChampName(summonerInfo.mastery.get(0).getChampionId(), getContext()));
            if(bitmap != null)
                mastery1.setImageBitmap(bitmap);
            else
                mastery1.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier("invalid", "drawable", root.getContext().getPackageName())));

            bitmap = Utilities.getChampIcon(Utilities.champIdToChampName(summonerInfo.mastery.get(1).getChampionId(), getContext()));
            if(bitmap != null)
                mastery2.setImageBitmap(bitmap);
            else
                mastery2.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier("invalid", "drawable", root.getContext().getPackageName())));

            bitmap = Utilities.getChampIcon(Utilities.champIdToChampName(summonerInfo.mastery.get(2).getChampionId(), getContext()));
            if(bitmap != null)
                mastery3.setImageBitmap(bitmap);
            else
                mastery3.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier("invalid", "drawable", root.getContext().getPackageName())));

            masteryPoints1.setText(getString(R.string.points, summonerInfo.mastery.get(0).getChampionPoints()));
            masteryPoints2.setText(getString(R.string.points, summonerInfo.mastery.get(1).getChampionPoints()));
            masteryPoints3.setText(getString(R.string.points, summonerInfo.mastery.get(2).getChampionPoints()));

            ImageView masteryRank1 = root.findViewById(R.id.mastery1_level);
            ImageView masteryRank2 = root.findViewById(R.id.mastery2_level);
            ImageView masteryRank3 = root.findViewById(R.id.mastery3_level);

            masteryRank1.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier("mastery" + summonerInfo.mastery.get(0).getChampionLevel(), "drawable", root.getContext().getPackageName())));
            masteryRank2.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier("mastery" + summonerInfo.mastery.get(1).getChampionLevel(), "drawable", root.getContext().getPackageName())));
            masteryRank3.setImageDrawable(ContextCompat.getDrawable(this.getContext(), root.getContext().getResources().getIdentifier("mastery" + summonerInfo.mastery.get(2).getChampionLevel(), "drawable", root.getContext().getPackageName())));

        }

        private void doInfo() {
            ImageView info1 = root.findViewById(R.id.info1);
            ImageView info2 = root.findViewById(R.id.info2);
            ImageView info3 = root.findViewById(R.id.info3);

            if (summonerInfo.matches != null && summonerInfo.matches.size() > 0) {
                List<Double> times = new LinkedList<>();
                List<Boolean> wins = new LinkedList<>();
                for (Match m : summonerInfo.matches) {
                    times.add((System.currentTimeMillis() - m.getGameCreation()) * 0.000000277778);
                    ParticipantStats stat = m.getParticipantByAccountId(summonerInfo.summoner.getAccountId()).getStats();
                    wins.add(stat.isWin());
                }

                if (times.get(0) > 4 || times.size() < 3) {
                    info1.setImageAlpha(0);
                    info3.setImageAlpha(0);
                    return;
                }

                for (int i = 0; i < times.size(); i++)
                    if (times.get(0) > 4)
                        wins.remove(i);

                boolean mode = wins.get(0);
                boolean streak = true;
                for (int i = 1; i < wins.size(); i++)
                    if (mode != wins.get(i)) {
                        streak = false;
                        break;
                    }

                if (mode && streak) {
                    info2.setImageAlpha(0);
                    info3.setImageAlpha(0);
                    return;
                } else if (!mode && streak) {
                    info1.setImageAlpha(0);
                    info2.setImageAlpha(0);
                    return;
                }
            }
            info1.setImageAlpha(0);
            info2.setImageAlpha(0);
            info3.setImageAlpha(0);
        }

        @SuppressLint("StaticFieldLeak")
        private class SummonerLookup extends AsyncTask<String, Void, SummonerInfo> {
            @Override
            protected SummonerInfo doInBackground(String... params) {
                ApiConfig config = new ApiConfig().setKey(Constants.API_KEY).setRateLimitHandler(null);

                RiotApi api = new RiotApi(config);

                LeaguePosition rank = null;
                List<ChampionMastery> mastery;
                Summoner summoner;
                Bitmap icon;
                List<Match> matchList = new LinkedList<>();

                try {
                    mastery = api.getChampionMasteriesBySummoner(matchup.platform, matchup.enemySummonerId);
                } catch (RiotApiException ignored) {
                    mastery = null;
                }
                try {
                    /*
                    List<MatchReference> matches = api.getRecentMatchListByAccountId(matchup.platform,api.getSummoner(matchup.platform,matchup.enemySummonerId).getAccountId()).getMatches();
                    for(MatchReference m: matches)
                        matchList.add(api.getMatch(matchup.platform, m.getGameId()));
                    */
                } catch (Exception e) {
                    matchList = null;
                    e.printStackTrace();
                }

                try {
                    Set<LeaguePosition> positionSet = api.getLeaguePositionsBySummonerId(matchup.platform, matchup.enemySummonerId);
                    summoner = api.getSummoner(matchup.platform, matchup.enemySummonerId);
                    for (LeaguePosition l : positionSet)
                        if (l.getQueueType().equals(LeagueQueue.RANKED_SOLO_5x5.name()))
                            rank = l;

                    icon = Utilities.getBitmapFromURL("http://ddragon.leagueoflegends.com/cdn/" + Constants.LOL_VERSION + "/img/profileicon/" + summoner.getProfileIconId() + ".png");
                } catch (RiotApiException e) {
                    e.printStackTrace();
                    return null;
                }

                return new SummonerInfo(rank, mastery, summoner, icon, matchList);
            }

            @Override
            protected void onPostExecute(SummonerInfo s) {
                super.onPostExecute(s);
                summonerInfo = s;

                postMethod();
            }
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return Matchup.MatchupFragment.newInstance(position + 1, 0);
            if (position == 1)
                return Matchup.R_And_MFragment.newInstance(position + 1);
            return Matchup.SummonerFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Matchup";
                case 1:
                    return "Runes";
                case 2:
                    return "Summoner Info";
            }
            return null;
        }
    }
}