package com.example.benth.leaguedelta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
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

class Utilities
{
    static double getRuneScalingCDR(int id)
    {
        switch (id)
        {
            case 5052:
                return 0.0005;
            case 5112:
                return 0.0016;
            case 5174:
                return 0.0007;
            case 5234:
                return 0.0022;
            case 5296:
                return 0.0009;
            case 5356:
                return 0.0028;
            default:
                return 0;
        }
    }

    static double getRuneFlatCDR(int id)
    {
        switch (id)
        {
            case 5021:
                return 0.0011;
            case 5051:
                return 0.0047;
            case 5081:
                return 0.002;
            case 5111:
                return 0.014;
            case 5143:
                return 0.0016;
            case 5173:
                return 0.0067;
            case 5203:
                return 0.0029;
            case 5233:
                return 0.0195;
            case 5265:
                return 0.002;
            case 5295:
                return 0.0083;
            case 5325:
                return 0.0036;
            case 5355:
                return 0.025;
            case 8003:
                return 0.0075;
            case 8012:
                return 0.0075;
            default:
                return 0;
        }
    }

    static String getKeyStoneCooldown(int id)
    {
        switch (id)
        {
            case 6362:
                return "25-15 s";
            case 6361:
                return "10 s";
            case 6262:
                return "45-30 s";
            case 6261:
                return "4 s";
            default:
                return "No CD";
        }
    }

    static List<String> getAbilityCooldowns(int champId, double flatCDR, double scalingCDR, boolean hasMast, Context context)
    {
        ArrayList<Byte> bytes = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(context.getString(R.string.CHAMPFILENAME));
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

        String q,w,e,r;
        q=w=e=r="";
        while (scanner.hasNextLine())
        {
            String str = scanner.nextLine();
            Scanner scan = new Scanner(str);
            if(champId != Integer.parseInt(scan.next()))
                continue;

            q = scan.next();
            w = scan.next();
            e = scan.next();
            r = scan.next();

            scan.close();
            break;
        }

        q = adjustCD(q,flatCDR,scalingCDR,false, hasMast);
        w = adjustCD(w,flatCDR,scalingCDR,false, hasMast);
        e = adjustCD(e,flatCDR,scalingCDR,false, hasMast);
        r = adjustCD(r,flatCDR,scalingCDR,true, hasMast);

        switch (champId)
        {
            case 110:
            case 67:
            case 254:
                w = context.getString(R.string.passive_ability);
                break;
            case 4:
                e = context.getString(R.string.passive_ability);
                break;
            case 77:
                r = adjustCD(r,flatCDR,scalingCDR,false, hasMast);
                break;
            case 421:
                r = adjustCD("100 80 60",flatCDR,scalingCDR,true, hasMast);
                break;
            case 91:
                q = adjustCD("8 7.5 7 6.5 6", flatCDR, scalingCDR, false, hasMast);
                e = "160/135/110/85/60 s";
                break;
            case 30:
                q ="1 s";
                break;
            case 157:
                q = "4 - 1.3 s";
                e = "10 9 8 7 6 s";
                r = adjustCD("80 55 30", flatCDR, scalingCDR, true, hasMast);
                break;
            default: break;
        }


        List<String> cds = new LinkedList<>();
        cds.add(q);
        cds.add(w);
        cds.add(e);
        cds.add(r);
        scanner.close();
        return cds;
    }

    private static String adjustCD(String cds, double flatCDR, double scalingCDR, boolean isUlt, boolean hasMast)
    {
        cds = cds.replaceAll("/", " ");
        Scanner scanner = new Scanner(cds);
        List<Double> base = new LinkedList<>();

        while (scanner.hasNextDouble())
            base.add(scanner.nextDouble());

        String adjusted = "";

        int lvl = 1;
        if(isUlt && base.size()== 3)
            lvl = 6;
        for (Double d: base)
        {
            double multiplier = 1 - (flatCDR +(scalingCDR*lvl));

            if(hasMast && multiplier < .55)
                multiplier = .55;
            else if(!hasMast && multiplier < .60)
                multiplier = .6;

            d = round(d * multiplier,0);

            if(d > 1)
                adjusted+=""+(int)d.doubleValue()+"/";
            else
                adjusted+=""+d+"/";

            if(isUlt) lvl += 5;
            else lvl += 2;
        }

        if (adjusted.endsWith("/"))
            adjusted = adjusted.substring(0, adjusted.length() - 1);
        adjusted += " s";

        scanner.close();
        return adjusted;
    }

    private static double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();

        if(value < 1 && places == 0)
            places = 1;

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    static List<Integer> getSummonerCooldown(int id1, int id2, boolean hasCDR, Context context)
    {
        ArrayList<Byte> bytes = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(context.getString(R.string.SSFILENAME));
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

        double cd2;
        double cd1 = cd2 = -1;

        while(scanner.hasNextLine())
        {
            String str = scanner.nextLine();
            Scanner scan = new Scanner(str);
            int id = Integer.parseInt(scan.next());
            if(id == id1)
                cd1 = Double.parseDouble(scan.next());
            else if (id == id2)
                cd2 = Double.parseDouble(scan.next());
            scan.close();
        }

        if(id1 == 12)
            cd1 = 300;
        else if(id2 == 12)
            cd2 = 300;

        if(hasCDR)
        {
            cd1*=.85;
            cd2*=.85;
        }

        List<Integer> list = new LinkedList<>();
        list.add((int)cd1);
        list.add((int)cd2);

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

    static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static boolean isKeystone(int id)
    {
        switch (id)
        {
            case 6161:
            case 6162:
            case 6164:
            case 6261:
            case 6262:
            case 6263:
            case 6361:
            case 6362:
            case 6363:
                return true;
            default: return false;
        }
    }

    static String summonerSpellIdToName(int id, boolean wantFile)
    {
        if(wantFile)
            return summonerSpellIdToName(id,false).toLowerCase().replaceAll(" ","").replaceAll("!","");
        switch (id)
        {
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
            default: return "Invalid";
        }
    }


    static String champIdToChampName(int id, boolean wantFile)
    {
        if (wantFile)
            return  champIdToChampName(id, false).toLowerCase().replaceAll(" ", "").replaceAll("\\.", "").replaceAll("'", "");
        switch (id)
        {
            case 1:
                return "Annie";
            case 2:
                return "Olaf";
            case 3:
                return "Galio";
            case 4:
                return "Twisted Fate";
            case 5:
                return "Xin Zhao";
            case 6:
                return "Urgot";
            case 7:
                return "LeBlanc";
            case 8:
                return "Vladimir";
            case 9:
                return "Fiddlesticks";
            case 10:
                return "Kayle";
            case 11:
                return "Master Yi";
            case 12:
                return "Alistar";
            case 13:
                return "Ryze";
            case 14:
                return "Sion";
            case 15:
                return "Sivir";
            case 16:
                return "Soraka";
            case 17:
                return "Teemo";
            case 18:
                return "Tristana";
            case 19:
                return "Warwick";
            case 20:
                return "Nunu";
            case 21:
                return "Miss Fortune";
            case 22:
                return "Ashe";
            case 23:
                return "Tryndamere";
            case 24:
                return "Jax";
            case 25:
                return "Morgana";
            case 26:
                return "Zilean";
            case 27:
                return "Singed";
            case 28:
                return "Evelynn";
            case 29:
                return "Twitch";
            case 30:
                return "Karthus";
            case 31:
                return "Cho'Gath";
            case 32:
                return "Amumu";
            case 33:
                return "Rammus";
            case 34:
                return "Anivia";
            case 35:
                return "Shaco";
            case 36:
                return "Dr. Mundo";
            case 37:
                return "Sona";
            case 38:
                return "Kassadin";
            case 39:
                return "Irelia";
            case 40:
                return "Janna";
            case 41:
                return "Gangplank";
            case 42:
                return "Corki";
            case 43:
                return "Karma";
            case 44:
                return "Taric";
            case 45:
                return "Veigar";
            case 48:
                return "Trundle";
            case 50:
                return "Swain";
            case 51:
                return "Caitlyn";
            case 53:
                return "Blitzcrank";
            case 54:
                return "Malphite";
            case 55:
                return "Katarina";
            case 56:
                return "Nocturne";
            case 57:
                return "Maokai";
            case 58:
                return "Renekton";
            case 59:
                return "Jarvan IV";
            case 60:
                return "Elise";
            case 61:
                return "Orianna";
            case 62:
                return "Wukong";
            case 63:
                return "Brand";
            case 64:
                return "Lee Sin";
            case 67:
                return "Vayne";
            case 68:
                return "Rumble";
            case 69:
                return "Cassiopeia";
            case 72:
                return "Skarner";
            case 74:
                return "Heimerdinger";
            case 75:
                return "Nasus";
            case 76:
                return "Nidalee";
            case 77:
                return "Udyr";
            case 78:
                return "Poppy";
            case 79:
                return "Gragas";
            case 80:
                return "Pantheon";
            case 81:
                return "Ezreal";
            case 82:
                return "Mordekaiser";
            case 83:
                return "Yorick";
            case 84:
                return "Akali";
            case 85:
                return "Kennen";
            case 86:
                return "Garen";
            case 89:
                return "Leona";
            case 90:
                return "Malzahar";
            case 91:
                return "Talon";
            case 92:
                return "Riven";
            case 96:
                return "Kog'Maw";
            case 98:
                return "Shen";
            case 99:
                return "Lux";
            case 101:
                return "Xerath";
            case 102:
                return "Shyvana";
            case 103:
                return "Ahri";
            case 104:
                return "Graves";
            case 105:
                return "Fizz";
            case 106:
                return "Volibear";
            case 107:
                return "Rengar";
            case 110:
                return "Varus";
            case 111:
                return "Nautilus";
            case 112:
                return "Viktor";
            case 113:
                return "Sejuani";
            case 114:
                return "Fiora";
            case 115:
                return "Ziggs";
            case 117:
                return "Lulu";
            case 119:
                return "Draven";
            case 120:
                return "Hecarim";
            case 121:
                return "Kha'Zix";
            case 122:
                return "Darius";
            case 126:
                return "Jayce";
            case 127:
                return "Lissandra";
            case 131:
                return "Diana";
            case 133:
                return "Quinn";
            case 134:
                return "Syndra";
            case 136:
                return "Aurelion Sol";
            case 141:
                return "Kayn";
            case 143:
                return "Zyra";
            case 150:
                return "Gnar";
            case 154:
                return "Zac";
            case 157:
                return "Yasuo";
            case 161:
                return "Vel'Koz";
            case 163:
                return "Taliyah";
            case 164:
                return "Camille";
            case 201:
                return "Braum";
            case 202:
                return "Jhin";
            case 203:
                return "Kindred";
            case 222:
                return "Jinx";
            case 223:
                return "Tahm Kench";
            case 236:
                return "Lucian";
            case 238:
                return "Zed";
            case 240:
                return "Kled";
            case 245:
                return "Ekko";
            case 254:
                return "Vi";
            case 266:
                return "Aatrox";
            case 267:
                return "Nami";
            case 268:
                return "Azir";
            case 412:
                return "Thresh";
            case 420:
                return "Illaoi";
            case 421:
                return "Rek'Sai";
            case 427:
                return "Ivern";
            case 429:
                return "Kalista";
            case 432:
                return "Bard";
            case 497:
                return "Rakan";
            case 498:
                return "Xayah";
            case 516:
                return "Ornn";
            default:
                return "Invalid";
        }
    }
}
