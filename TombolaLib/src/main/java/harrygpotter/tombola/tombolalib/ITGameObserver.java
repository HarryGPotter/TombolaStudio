/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package harrygpotter.tombola.tombolalib;

/**
 * TODO(2.0) I'm working here
 *
 * @author Harry G. Potter (harry[.]g[.]potter[@]gmail[.]com)
 */
public interface ITGameObserver {

    void startGameObservation(TGame game);

    void beforeStart();

    void afterEnd();

    void postExtractionEvaluation();

    void postAssignmentEvaluation();

    void stopGameObservation(TGame game);
}
