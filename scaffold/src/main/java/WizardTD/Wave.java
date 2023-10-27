// Wave.java
package WizardTD;

import processing.data.JSONArray;
import processing.data.JSONObject;

import static processing.core.PApplet.println;

public class Wave {
    int duration;
    float preWavePause;
    Monster[] monsters;

    Wave(int duration, float preWavePause, JSONArray monstersArray) {
        this.duration = duration;
        this.preWavePause = preWavePause;
        this.monsters = new Monster[monstersArray.size()];

        for (int i = 0; i < monstersArray.size(); i++) {
            println("monstersArray: ", monstersArray);
            JSONObject monsterObject = monstersArray.getJSONObject(i);
            monsters[i] = new Monster(
                    monsterObject.getString("type"),
                    monsterObject.getInt("hp"),
                    monsterObject.getFloat("speed"),
                    monsterObject.getFloat("armour"),
                    monsterObject.getInt("mana_gained_on_kill"),
                    monsterObject.getInt("quantity"),
                    0,
                    0
            );
        }

        for (int i=0;i<this.monsters.length;i++){
            println("i, monster", i, monsters[i]);
        }
    }
}
