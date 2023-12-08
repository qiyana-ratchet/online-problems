package org.example;

import java.util.Objects;

// Define a Position class to represent (x, y) coordinates
public class Position {
    int x, y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Override equals method to compare positions based on x and y
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Position other = (Position) obj;
        return x == other.x && y == other.y;
    }

    // Override hashCode to generate a hash code based on x and y
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}