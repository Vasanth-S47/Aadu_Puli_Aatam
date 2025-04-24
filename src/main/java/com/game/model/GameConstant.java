package com.game.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;



public class GameConstant {

    public Map<Integer, List<Integer>> VALID_MOVES;
    public Map<Integer, Map<Integer, Integer>> VALID_JUMPS;


    private static GameConstant instance;


    public GameConstant() {
        VALID_MOVES = Collections.emptyMap();
        VALID_JUMPS = Collections.emptyMap();
    }


    public static synchronized GameConstant getInstance() {
        if (instance == null) {
            instance = new GameConstant();
        }
        return instance;
    }


    public static void setInstance(GameConstant newInstance) {
        instance = newInstance;
    }
}



