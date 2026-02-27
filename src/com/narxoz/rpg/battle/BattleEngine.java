package com.narxoz.rpg.battle;

import com.narxoz.rpg.battle.Combatant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BattleEngine {

    private static BattleEngine instance;
    private Random random = new Random(1L);

    private BattleEngine() {
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public void reset() {
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        List<Combatant> groupA = new ArrayList<>(teamA);
        List<Combatant> groupB = new ArrayList<>(teamB);

        EncounterResult result = new EncounterResult();
        int round = 0;

        result.addLog("Battle started: " + groupA.size() + " heroes vs " + groupB.size() + " enemies");

        while (!groupA.isEmpty() && !groupB.isEmpty()) {
            round++;
            result.addLog("Round " + round);

            // Heroes attack first
            performAttacks(groupA, groupB, result, "Heroes");

            // Remove dead enemies
            groupB.removeIf(c -> !c.isAlive());

            if (groupB.isEmpty()) {
                result.setWinner("Heroes");
                break;
            }

            // Enemies attack
            performAttacks(groupB, groupA, result, "Enemies");

            // Remove dead heroes
            groupA.removeIf(c -> !c.isAlive());

            if (groupA.isEmpty()) {
                result.setWinner("Enemies");
                break;
            }
        }

        result.setRounds(round);

        String summary = groupA.isEmpty()
                ? "Enemies won. Survivors: " + groupB.size() + " enemies"
                : "Heroes won. Survivors: " + groupA.size() + " heroes";

        result.addLog("Battle ended: " + summary);

        return result;
    }

    private void performAttacks(List<Combatant> attackers, List<Combatant> defenders,
                                EncounterResult result, String teamName) {
        List<Combatant> activeAttackers = new ArrayList<>(attackers);

        for (Combatant attacker : activeAttackers) {
            if (!attacker.isAlive()) continue;

            List<Combatant> aliveTargets = new ArrayList<>();
            for (Combatant target : defenders) {
                if (target.isAlive()) {
                    aliveTargets.add(target);
                }
            }

            if (aliveTargets.isEmpty()) {
                result.addLog(teamName + ": no living opponents left");
                break;
            }

            Combatant target = aliveTargets.get(0);

            int damage = attacker.getAttackPower();
            target.takeDamage(damage);

            String log = attacker.getName() + " attacks " + target.getName() +
                    " for " + damage + " damage";
            if (!target.isAlive()) {
                log += " [killed]";
            }
            result.addLog(log);
        }
    }
}
