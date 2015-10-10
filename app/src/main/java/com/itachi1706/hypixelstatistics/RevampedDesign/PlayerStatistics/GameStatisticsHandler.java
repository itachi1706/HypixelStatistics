package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoHeader;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.ArcadeStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.ArenaStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.BlitzSGStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.CopsAndCrimsStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.MegaWallsStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.OldSpleefStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.PaintballStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.QuakecraftStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.TNTGamesStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.UHCChampionsStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.VampireZStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.WallsStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics.WarlordsStatistics;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.util.GameType;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics
 */
public class GameStatisticsHandler {

    /**
     * Parse statistics (Split based on GameType)
     * @param reply PlayerReply object
     */
    public static ArrayList<PlayerInfoHeader> parseStats(PlayerReply reply, String localPlayerName){
        ArrayList<PlayerInfoHeader> descArray = new ArrayList<>();
        JsonObject mainStats = reply.getPlayer().getAsJsonObject("stats");
        for (Map.Entry<String, JsonElement> entry : mainStats.entrySet()){
            //Based on stat go parse it
            JsonObject statistic = entry.getValue().getAsJsonObject();
            GameType parseVariousGamemode = GameType.fromDatabase(entry.getKey());
            if (parseVariousGamemode == null) {
                switch (entry.getKey().toLowerCase()) {
                    case "spleef":
                        descArray.add(new PlayerInfoHeader("Legacy Spleef Statistics", OldSpleefStatistics.parseSpleef(statistic)))
                        break;
                    case "holiday": //descArray.remove(descArray.size() - 1);
                        break;
                    default:
                        descArray.add(new PlayerInfoHeader(entry.getKey(), errorList()));
                        break;
                }
            } else {
                switch (parseVariousGamemode) {
                    case ARENA:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", ArenaStatistics.parseArena(statistic)));
                        break;
                    case ARCADE:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", ArcadeStatistics.parseArcade(statistic)));
                        break;
                    case SURVIVAL_GAMES:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", BlitzSGStatistics.parseHG(statistic)));
                        break;
                    case MCGO:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", CopsAndCrimsStatistics.parseMcGo(statistic)));
                        break;
                    case PAINTBALL:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", PaintballStatistics.parsePaintball(statistic)));
                        break;
                    case QUAKECRAFT:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", QuakecraftStatistics.parseQuake(statistic)));
                        break;
                    case TNTGAMES:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", TNTGamesStatistics.parseTntGames(statistic)));
                        break;
                    case VAMPIREZ:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", VampireZStatistics.parseVampZ(statistic)));
                        break;
                    case WALLS:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", WallsStatistics.parseWalls(statistic)));
                        break;
                    case WALLS3:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", MegaWallsStatistics.parseWalls3(statistic)));
                        break;
                    case UHC:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", UHCChampionsStatistics.parseUHC(statistic)));
                        break;
                    case BATTLEGROUND:
                        descArray.add(new PlayerInfoHeader(parseVariousGamemode.getName() + " Statistics", WarlordsStatistics.parseWarlords(statistic, localPlayerName)));
                        break;
                    default:
                        descArray.add(new PlayerInfoHeader(entry.getKey(), errorList()));
                        Crashlytics.logException(new NoSuchElementException("Required to add " + entry.getKey() + " to statistics"));
                        break;
                }
            }
        }
        return descArray;
    }

    private static ArrayList<PlayerInfoStatistics> errorList(){
        ArrayList<PlayerInfoStatistics> error = new ArrayList<>();
        error.add(new PlayerInfoStatistics("ERROR - IMFORM DEV", MinecraftColorCodes.parseColors("§cPlease contact the dev to add this into the statistics§r")));
        return error;
    }
}
