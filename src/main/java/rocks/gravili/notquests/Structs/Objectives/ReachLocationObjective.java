/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2021 Alessio Gravili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rocks.gravili.notquests.Structs.Objectives;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import rocks.gravili.notquests.NotQuests;
import rocks.gravili.notquests.Structs.Quest;

public class ReachLocationObjective extends Objective {
    private final Location min, max;
    private final NotQuests main;
    private final String locationName;

    public ReachLocationObjective(NotQuests main, final Quest quest, final int objectiveID, final Location minLocation, final Location maxLocation, final String locationName) {
        super(main, quest, objectiveID, 1);
        this.main = main;
        this.min = minLocation;
        this.max = maxLocation;
        this.locationName = locationName;
    }

    public ReachLocationObjective(NotQuests main, Quest quest, int objectiveNumber, int progressNeeded) {
        super(main, quest, objectiveNumber, progressNeeded);
        final String questName = quest.getQuestName();
        this.main = main;

        min = main.getDataManager().getQuestsData().getLocation("quests." + questName + ".objectives." + objectiveNumber + ".specifics.minLocation");
        max = main.getDataManager().getQuestsData().getLocation("quests." + questName + ".objectives." + objectiveNumber + ".specifics.maxLocation");
        locationName = main.getDataManager().getQuestsData().getString("quests." + questName + ".objectives." + objectiveNumber + ".specifics.locationName");

    }

    @Override
    public String getObjectiveTaskDescription(final String eventualColor, final Player player) {
        return main.getLanguageManager().getString("chat.objectives.taskDescription.reachLocation.base", player)
                .replaceAll("%EVENTUALCOLOR%", eventualColor)
                .replaceAll("%LOCATIONNAME%", getLocationName());
    }

    @Override
    public void save() {
        main.getDataManager().getQuestsData().set("quests." + getQuest().getQuestName() + ".objectives." + getObjectiveID() + ".specifics.minLocation", getMinLocation());
        main.getDataManager().getQuestsData().set("quests." + getQuest().getQuestName() + ".objectives." + getObjectiveID() + ".specifics.maxLocation", getMaxLocation());
        main.getDataManager().getQuestsData().set("quests." + getQuest().getQuestName() + ".objectives." + getObjectiveID() + ".specifics.locationName", getLocationName());
    }

    public final Location getMinLocation() {
        return min;
    }

    public final Location getMaxLocation() {
        return max;
    }

    public final String getLocationName() {
        return locationName;
    }

}