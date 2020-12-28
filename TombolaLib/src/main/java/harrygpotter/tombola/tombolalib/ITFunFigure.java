/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package harrygpotter.tombola.tombolalib;

/**
 *
 * @author Harry G. Potter (harry[.]g[.]potter[@]gmail[.]com)
 */
public interface ITFunFigure {

    public enum TFigureCategory {
        GAME, EVENT, COMMUNITY
    }

    String getID();

    String getMessage();

    TFigureCategory getCategory();

    // int getLevel();
    Object[] getValues();

    int evaluate();
}
