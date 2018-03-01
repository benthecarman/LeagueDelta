package com.example.benth.leaguedelta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

class Utilities {

    static String getKeyStoneCD(int id) {
        switch (id) {
            case 8326://Unsealed Spell Book
                return "120s";
            case 8351://Glacial Augment
                return "7/6/5/4 s";
            case 8359://Kleptomancy
                return "2 s";
            case 8008://Lethal Tempo
                return "6 s";
            case 8005://Press the Attack
                return "6 s";
            case 8112://Electrocute
                return "50 - 25 s";
            case 8124://Predator
                return "150 - 100 s";
            case 8128:
                return "20 / 300 s";
            case 8214://Summon Aery
                return "2s";
            case 8229://Comet
                return "20 - 8 s";
            case 8230://Phase Dive
                return "15 s";
            case 8439://Aftershock
                return "20 s";
            case 8465://Guardian
                return "70 - 40 s";
            default://Others
                return "";
        }
    }

    static List<String> getAbilityCooldowns(int champId, int cdr, boolean hasUltHat, boolean hasCosmicInsight, boolean hasCelerity, Context context) {
        ArrayList<Byte> bytes = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(context.getString(R.string.CHAMPFILENAME));
            while (fis.available() > 0)
                bytes.add((byte) fis.read());
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = new byte[bytes.size()];
        for (int z = 0; z < bytes.size(); ++z)
            b[z] = bytes.get(z);
        Scanner scanner = new Scanner(new String(b, StandardCharsets.UTF_8));

        String q, w, e, r;
        q = w = e = r = "";
        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            Scanner scan = new Scanner(str);
            if (champId != Integer.parseInt(scan.next()))
                continue;

            scan.next();
            q = scan.next();
            w = scan.next();
            e = scan.next();
            r = scan.next();

            scan.close();
            break;
        }

        q = adjustCD(q, hasUltHat, false, hasCosmicInsight, hasCelerity, cdr);
        w = adjustCD(w, hasUltHat, false, hasCosmicInsight, hasCelerity, cdr);
        e = adjustCD(e, hasUltHat, false, hasCosmicInsight, hasCelerity, cdr);
        r = adjustCD(r, hasUltHat, true, hasCosmicInsight, hasCelerity, cdr);

        switch (champId) {
            case 110: //Varus
            case 67: //Vayne
            case 254: //Vi
                w = context.getString(R.string.passive_ability);
                break;
            case 4: //TwistedFate
                e = context.getString(R.string.passive_ability);
                break;
            case 77: //Udyr
                r = adjustCD(r, hasUltHat, false, hasCosmicInsight, hasCelerity, cdr);
                break;
            case 421: //Rek'Sai
                r = adjustCD("100 80 60", hasUltHat, true, hasCosmicInsight, hasCelerity, cdr);
                break;
            case 91: //Talon
                q = adjustCD("8 7.5 7 6.5 6", hasUltHat, false, hasCosmicInsight, hasCelerity, cdr);
                e = "160/135/110/85/60 s";
                break;
            case 30: //Karthus
                q = "1 s";
                break;
            case 157: //Yasuo
                q = "4 - 1.3 s";
                e = "10 9 8 7 6 s";
                r = adjustCD("80 55 30", hasUltHat, true, hasCosmicInsight, hasCelerity, cdr);
                break;
            default:
                break;
        }


        List<String> cds = new LinkedList<>();
        cds.add(q);
        cds.add(w);
        cds.add(e);
        cds.add(r);
        scanner.close();
        return cds;
    }

    public static String adjustCD(String cds, boolean hasUltHat, boolean isUlt, boolean hasCosmicInsight, boolean hasCelerity, int added) {
        cds = cds.replaceAll("/", " ");
        Scanner scanner = new Scanner(cds);
        List<Double> base = new LinkedList<>();

        while (scanner.hasNextDouble())
            base.add(scanner.nextDouble());

        StringBuilder adjusted = new StringBuilder();

        int lvl = 1;
        if (isUlt && base.size() == 3)
            lvl = 6;
        for (Double d : base) {
            double multi = added / 100.0;

            if (lvl >= 10 && hasCelerity)
                multi += .1;
            if (hasCosmicInsight)
                multi += .05;
            if (isUlt && lvl >= 11 && hasUltHat)
                multi = multi * 1.15;

            Log.d("Multi", multi + "");


            if (multi > .45 && hasCosmicInsight)
                multi = .45;
            else if (multi > .4 && !hasCosmicInsight)
                multi = .4;

            d = round(d * (1 - multi), 0);

            if (d > 1)
                adjusted.append("").append((int) d.doubleValue()).append("/");
            else
                adjusted.append("").append(d).append("/");

            if (isUlt) lvl += 5;
            else lvl += 2;
        }

        if (adjusted.toString().endsWith("/"))
            adjusted = new StringBuilder(adjusted.substring(0, adjusted.length() - 1));
        adjusted.append(" s");

        scanner.close();
        return adjusted.toString();
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        if (value < 1 && places == 0)
            places = 1;

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    static List<Integer> getSummonerCooldown(int id1, int id2, boolean hasComicInsight, boolean hasSpellBook, Context context) {
        ArrayList<Byte> bytes = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(context.getString(R.string.SSFILENAME));
            while (fis.available() > 0)
                bytes.add((byte) fis.read());
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = new byte[bytes.size()];
        for (int z = 0; z < bytes.size(); ++z)
            b[z] = bytes.get(z);
        Scanner scanner = new Scanner(new String(b, StandardCharsets.UTF_8));

        double cd2;
        double cd1 = cd2 = -1;

        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            Scanner scan = new Scanner(str);
            int id = Integer.parseInt(scan.next());
            if (id == id1)
                cd1 = Double.parseDouble(scan.next());
            else if (id == id2)
                cd2 = Double.parseDouble(scan.next());
            scan.close();
        }

        if (id1 == 12)
            cd1 = 300;
        else if (id2 == 12)
            cd2 = 300;

        double multi = 1;

        if (hasComicInsight)
            multi -= 0.05;
        if (hasSpellBook)
            multi -= .15;

        cd1 *= multi;
        cd2 *= multi;

        List<Integer> list = new LinkedList<>();
        list.add((int) cd1);
        list.add((int) cd2);

        scanner.close();
        return list;
    }

    /*static int[] getProfileIconDimens(long id, Context context)
    {
        int[] dimens = new int[0];
        ArrayList<Byte> bytes = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(context.getString(R.string.ICONFILENAME));
            while (fis.available()>0)
                bytes.add((byte) fis.read());
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = new byte[bytes.size()];
        for(int z = 0;z<bytes.size();++z)
            b[z] = bytes.get(z);
        Scanner scanner = new Scanner(new String(b, StandardCharsets.UTF_8));

        while(scanner.hasNextLine())
        {
            String str = scanner.nextLine();
            Scanner scan = new Scanner(str);
            int cur = Integer.parseInt(scan.next());
            if(id == cur)
                dimens = new int[]{scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt()};

            scan.close();
        }

        if(dimens.length == 0)
            return null;

        return dimens;
    }*/

    static Bitmap getChampIcon(String name) {

        name = name.replaceAll(" ", "").replace("'", "").replace(".", "");

        String url = "http://ddragon.leagueoflegends.com/cdn/" + Constants.LOL_VERSION + "/img/champion/" + name + ".png";

        try {
            return new IconLookUp().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            return null;
        }
    }

    static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (FileNotFoundException e) {
            int z = src.lastIndexOf('/');
            String str = src.substring(0, z + 1);
            String end = src.substring(z + 1).toLowerCase();
            end = end.substring(0, 1).toUpperCase() + end.substring(1);
            String newString = str + end;

            return newString.equals(src) ? null : getBitmapFromURL(newString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static boolean isKeystone(long id) {
        switch ((int) id) {
            case 8230:
            case 8229:
            case 8359:
            case 8112:
            case 8021:
            case 8465:
            case 8008:
            case 8214:
            case 8124:
            case 8128:
            case 8326:
            case 8005:
            case 8439:
            case 8437:
                return true;
            default:
                return false;
        }
    }

    //TODO Make api call
    static String summonerSpellIdToName(int id, boolean wantFile) {
        if (wantFile)
            return summonerSpellIdToName(id, false).toLowerCase().replaceAll(" ", "").replaceAll("!", "");
        switch (id) {
            case 12:
                return "Teleport";
            case 3:
                return "Exhaust";
            case 21:
                return "Barrier";
            case 13:
                return "Clarity";
            case 11:
                return "Smite";
            case 4:
                return "Flash";
            case 32:
                return "Mark";
            case 14:
                return "Ignite";
            case 30:
                return "To The King!";
            case 6:
                return "Ghost";
            case 7:
                return "Heal";
            case 31:
                return "Poro Throw";
            case 1:
                return "Cleanse";
            default:
                return "Invalid";
        }
    }

    private static String champToFile(String name) {
        return name == null || name.equals("") ? "" : name.toLowerCase().replaceAll(" ", "").replaceAll("\\.", "").replaceAll("'", "");
    }

    static String champIdToChampName(int id, boolean wantFile, Context context) {

        ArrayList<Byte> bytes = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(context.getString(R.string.CHAMPFILENAME));
            while (fis.available() > 0)
                bytes.add((byte) fis.read());
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = new byte[bytes.size()];
        for (int z = 0; z < bytes.size(); ++z)
            b[z] = bytes.get(z);
        Scanner scanner = new Scanner(new String(b, StandardCharsets.UTF_8));

        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            Scanner scan = new Scanner(str);
            if (id != Integer.parseInt(scan.next()))
                continue;

            String name = scan.next();

            scan.close();
            scanner.close();

            return wantFile ? champToFile(name) : name.replaceAll("([A-Z])", " $1").replaceAll("\' ", "'").trim();
        }

        return "";
    }

    private static class IconLookUp extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromURL(strings[0]);
        }
    }
}
