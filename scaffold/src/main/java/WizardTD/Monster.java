package WizardTD;

import processing.core.PVector;
import java.util.*;

public class Monster {
    String type;
    int hp;
    float speed;
    float armour;
    int manaGainedOnKill;
    int quantity;
    float x;
    float y;
    private List<PVector> route = new ArrayList<>();

    public List<PVector> getRoute() {
        return route;
    }

    public void setRoute(List<PVector> route) {
        this.route = route;
    }

    Monster(String type, int hp, float speed, float armour, int manaGainedOnKill, int quantity,  float x, float y) {
        this.type = type;
        this.hp = hp;
        this.speed = speed;
        this.armour = armour;
        this.manaGainedOnKill = manaGainedOnKill;
        this.quantity = quantity;
        this.x = x; // X coordinate of the monster's position
        this.y = y; // Y coordinate of the monster's position
    }
}